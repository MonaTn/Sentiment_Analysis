package xTestUtil;

import java.util.ArrayList;
import java.util.List;

import Negation.NegationPreprocessor;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class test_TextPreprocessorWithNegation {
	private static MaxentTagger taggerModel;
//	static LexicalizedParser parser = LexicalizedParser
//			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static List<Tree> listOfRestOfTree ;
	private final static String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	static 		NegationPreprocessor negationProcessor = new NegationPreprocessor();

	private static void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<TaggedWord> getTaggedSentence(List<HasWord> sentence, LexicalizedParser parser) {
		patternsList = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
		initializeTagger();
		
		listOfRestOfTree = new ArrayList<Tree>();

		if (negationProcessor.containNegationCues(sentence)) {
			System.out.println("Negation happend ! ==> " + sentence);
			return getTaggedWordsOfNegatedSentence(sentence, parser);
		} else {
			System.out.println("-- No Nagation ==> "+sentence);
			System.out.println(taggerModel.tagSentence(sentence).toString());
			return taggerModel.tagSentence(sentence);
		}
	}

	private static List<TaggedWord> getTaggedWordsOfNegatedSentence(
			List<HasWord> sentence, LexicalizedParser parser) {
		List<TaggedWord> tagedWords = new ArrayList<TaggedWord>();
		Tree pennTree = parser.apply(sentence);
		List<Tree> negatedTrees = extractNegatedSubTree(pennTree);
		if (negatedTrees.isEmpty()) {
			System.out.println ("-- Negation tree is null!");
			return taggerModel.tagSentence(sentence);
		} else {
		List<Tree> realNegatedTree = getRealNegatedTrees(negatedTrees);
		addNegationSymbolToWords(realNegatedTree);
		tagedWords.addAll(getTaggedwords(realNegatedTree));
		
		tagedWords.addAll(getTaggedwords(listOfRestOfTree));
		
		return tagedWords;
		}
	}

	private static List<TaggedWord> getTaggedwords(List<Tree> trees) {
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		for (Tree subTree : trees) {
			taggedWords.addAll(subTree.taggedYield());
		}
		return taggedWords;
	}

	private static void addNegationSymbolToWords(List<Tree> trees) {
		for (Tree subTree : trees) {
			List<Tree> leaves = subTree.getLeaves();
			for (Tree leave : leaves) {
				leave.setValue("~" + leave.value());
			}
			System.out.println(leaves.toString());
		}
	}

//	private static List<Tree> getRealNegatedTrees(List<Tree> negatedTrees) {
//		for (Tree subTree : negatedTrees) {
//			System.out.println("**" + subTree.toString());
//			if (NegationElements.isPseudoNegation(subTree)) {
//				listOfRestOfTree.add(subTree);
//				negatedTrees.remove(subTree);
//				System.out.println(negatedTrees.toString());
//			}
//		}
//		return negatedTrees;
//	}

	private static List<Tree> getRealNegatedTrees(List<Tree> tree) {
		for (int i = 0; i < tree.size(); i++) {
			System.out.println("**" + tree.get(i).toString());
			if (negationProcessor.isPseudoNegation(tree.get(i))) {
				listOfRestOfTree.add(tree.get(i));
				tree.remove(i);
				System.out.println(tree.toString());
			}
		}
		return tree;
	}
	
	private static List<Tree> extractNegatedSubTree(Tree tree) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		for (String pattern : patternsList) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree subTree = matcher.getMatch();
				negatedTrees.add(subTree);
			}
			deleteSubTree(tree, tregexPattern);
		}
		listOfRestOfTree.add(tree);
		return negatedTrees;
	}

	public static Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

}

/** Not **/ 
//patternsList.add("@AD=head $,, @RB-NOT > VP");
//patternsList.add("RB=notRB . RB=rb : (=notRB < not) : (=rb < only|solely)"); // not only ... but also ...
//patternsList.add("VP=head , NP=np : (=np << @Anybody)"); // Anybody in subject
//patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Anybody)"); // Anybody in object
//patternsList.add("VP=head , @Neither !< @Nor"); // Neither structure
//patternsList.add("VP=head $, NP=np : (=np << @Neither ?<< @Nor)");// Neither & nor structure 
//patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Neither ?<< @Nor)");// Neither & nor structure 
//patternsList.add("VP=head < @VB [<< @Neither | << @Nor]"); // Using neither & nor structure with to be
