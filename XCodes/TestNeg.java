package Negation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class TestNeg {

	static LexicalizedParser parser = LexicalizedParser
			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	private static MaxentTagger taggerModel;
	private final static String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	static String filename = "../../TrainingSet/Test/test3.txt";
	static List<String> patternsList;
	static TregexPatternCompiler macros;

	private static void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		initializeTagger();
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		TregexPatternCompiler macros = NegationElements.getMacros();

		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree pennTree = parser.apply(sentence);
			List<TaggedWord> words1 = pennTree.taggedYield();
			System.out.println(Arrays.toString(words1.toArray()));
			List<TaggedWord> words = taggerModel.tagSentence(sentence);
			System.out.println(Arrays.toString(words.toArray()));
			System.out.println();
		}
	}

	private static List<Tree> extractNegatedSubTree(Tree tree) {
		List<Tree> negatedTree = new ArrayList<Tree>();
		patternsList = NegationElements.getPatternsList();
		macros = NegationElements.getMacros();
		for (String pattern : patternsList) {
			TregexPattern negationRegeX = macros.compile(pattern);
			TregexMatcher matcher = negationRegeX.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree subTree = matcher.getMatch();
				negatedTree.add(subTree);
			}
			deleteSubTree(tree, negationRegeX);
		}
		System.out.println("rest of tree" + tree.toString());
		return negatedTree;
	}

	public static Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}
}
