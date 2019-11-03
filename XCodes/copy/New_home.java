package X.Negation.copy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Word;
import shared.WordList;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class New_home {

	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static LexicalizedParser parser = LexicalizedParser
			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	static final String fileName = "../../TrainingSet/Test/unisen.txt";
	static NegationPreprocessor negationProcessor;
	public static void main(String[] args) {
		negationProcessor = new NegationPreprocessor();

		patternsList = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
		WordList global = getAllWords(fileName);
		global.toPrint();
	}

	private static WordList getAllWords(String filename) {
		List<Tree> negationTree = new ArrayList<Tree>();
		WordList globalWordList = new WordList();
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) { // **
			System.out.println("Sentence: " + sentence.toString());
			if (negationProcessor.containNegationCues(sentence)) {
				System.out
						.println("Negation found!******************************************");
			}
			Tree pennTree = parser.apply(sentence);
			List<Tree> negatedTrees = extractNegatedSubTree(pennTree);
			if (negatedTrees != null) {
				System.out.println("number of negated trees : "
						+ negatedTrees.size());
				for (Tree nSubTree : negatedTrees) {
					if (isPseudoNegation(nSubTree)) {
						continue;
					}
					System.out.println("Negated Tree: " + nSubTree.toString());
					negationTree.addAll(nSubTree);
					WordList wordList = pruneTree(nSubTree);
					globalWordList.add(wordList);
				}
			}
			System.out
					.println("___________________________________________________________________________");
		}
		return globalWordList;
	}

	private static List<Tree> extractNegatedSubTree(Tree tree) {
		List<Tree> negatedTree = new ArrayList<Tree>();
		System.out.println("Main Tree: " + tree.toString());
		// tree.pennPrint();
		for (String pattern : patternsList) {
			TregexPattern negationRegeX = macros.compile(pattern);
			TregexMatcher matcher = negationRegeX.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree subTree = matcher.getMatch();
				negatedTree.add(subTree);
				System.out.println("Pattern: " + pattern);
			}
			deleteSubTree(tree, negationRegeX);
		}
		System.out.println("rest of tree" + tree.toString());
		return negatedTree;
	}

	public static WordList pruneTree(Tree tree) {
		WordList wordslist = new WordList();
		int i;
		if (containAuxVerb(tree)) {
			System.out
					.println("||||||||||||||||| Aux find ! ||||||||||||||||||||||");
			i = 2;
		} else {
			i = 0;
		}
		
		List<Tree> trees = tree.getChildrenAsList();
		for (int j = i; j < trees.size(); j++) {
			List<TaggedWord> taggedWords = trees.get(j).taggedYield();
			System.out.println(Arrays.toString(taggedWords.toArray()));
			for (TaggedWord taggedWord : taggedWords) {
				if (taggedWord.tag().contains("VB")
						|| taggedWord.tag().contains("JJ")
						|| taggedWord.tag().contains("MD")) {
					Word word = new Word();
					word.setWord("~" + taggedWord.word());
					word.setTag(taggedWord.tag());
					wordslist.add(word);
					System.out.println("------" + taggedWord.word() + " , "
							+ taggedWord.tag());
				}
			}
		}
		return wordslist;
	}

	private static boolean containAuxVerb(Tree tree) {
		// @VB $. (@NOT $.. VP)
		// @VB $. @NOT=not : (=not $.. VP)
		// VP=h [< @VB |< MD=md] < @RB-NOT < VP : (=md < @WILL)
		List<String> patterns = negationProcessor.getAuxiliaryPattern();
		for (String pattern : patterns) {
			TregexPattern auxVerbPattern = macros.compile(pattern);
			TregexMatcher matcher = auxVerbPattern.matcher(tree);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

	private static boolean isPseudoNegation(Tree tree) {
		// TregexPattern auxVerbPattern = macros
		// .compile("RB=rb . @AD=ad : (=rb < not) : (=ad < (RB < only))");
		TregexPattern notOnlyPattern = TregexPattern
				.compile("RB=notRB . RB=rb : (=notRB < not) : (=rb < only|solely)");
		TregexMatcher matcher = notOnlyPattern.matcher(tree);
		if (matcher.find()) {
			System.out.println("************* Pseudo Negation ! ***********");
			return true;
		}
		return false;
	}

	public static TsurgeonPattern getPatterns(String scriptName)
			throws FileNotFoundException {
		List<TsurgeonPattern> patterns = new ArrayList<TsurgeonPattern>();
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(
					scriptName));
			while (bufferReader.ready()) {
				patterns.add(Tsurgeon
						.getTsurgeonOperationsFromReader(bufferReader));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Tsurgeon.collectOperations(patterns);
	}

	public static Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	public static Tree coindexationTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("coindex head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}
	public void pruneTrees(Tree tree) {
		System.out.println("@@ Main Tree : " + tree.toString());
		List<Tree> extra = new ArrayList<Tree>();
		List<Tree> allChildren = tree.getChildrenAsList();
		System.out.println("Children as list: " + Arrays.toString(allChildren.toArray()));
		System.out.println("size : " + allChildren.size());
		for (int i = 0; i < allChildren.size(); i++) {
			System.out.println ("get " + i + " : " + allChildren.get(i).value());
			if (allChildren.get(i).value().equals("VP")
					|| allChildren.get(i).value().equals("NP")) {
				extra.add(allChildren.get(i));
				System.out.println("Extra at " + i + " is " + Arrays.toString(extra.toArray()));
				allChildren.remove(i);
			}
		}
		System.out.println("Tree: " + tree.toString());
		System.out.println("Extra: " + Arrays.toString(extra.toArray()));
	}
}
