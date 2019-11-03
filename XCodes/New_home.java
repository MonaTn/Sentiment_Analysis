package Negation;

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
	static final String fileName = "../../TrainingSet/Test/NeitherNor.txt";

	public static void main(String[] args) {
		patternsList = NegationElements.getPatternsList();
		macros = NegationElements.getMacros();
		WordList global = getAllWords(fileName);
		global.toPrint();
	}

	private static WordList getAllWords(String filename) {
		List<Tree> negationTree = new ArrayList<Tree>();
		WordList globalWordList = new WordList();
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) { // **
			System.out.println("Sentence: " + sentence.toString());
			Tree pennTree = parser.apply(sentence);
			List<Tree> negatedSubTrees = extractNegatedSubTree(pennTree);
			if (negatedSubTrees != null) {
				System.out.println(negatedSubTrees.size());
				for (Tree tree : negatedSubTrees) {
					if (isPseudoNegation(tree)) {
						continue;
					}
					System.out.println("Negated Tree: " + tree.toString());
					negationTree.addAll(tree);
					WordList wordList = pruneTree(tree);
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
		Tree restOfTree = null;
		for (String pattern : patternsList) {
			TregexPattern negationRegeX = macros.compile(pattern);
			TregexMatcher matcher = negationRegeX.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree subTree = matcher.getMatch();
				negatedTree.add(subTree);
				System.out.println("Pattern: " + pattern);
			}
			restOfTree = deleteSubTree(tree, negationRegeX);
		}
		System.out.println(restOfTree.toString());
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
			List<TaggedWord> words = trees.get(j).taggedYield();
			System.out.println(Arrays.toString(words.toArray()));
			for (TaggedWord taggedWord : words) {
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
		TregexPattern auxVerbPattern = macros.compile("@VB $. (@NOT $.. VP)");
		TregexMatcher matcher = auxVerbPattern.matcher(tree);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
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
	
	public static TsurgeonPattern getPatterns(String scriptName) throws FileNotFoundException {
		List<TsurgeonPattern> patterns = new ArrayList<TsurgeonPattern>();
		try {
			BufferedReader bufferReader = new BufferedReader (new FileReader (scriptName));
			while (bufferReader.ready()) {
				patterns.add(Tsurgeon.getTsurgeonOperationsFromReader(bufferReader));
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
	 
	public static Tree coindexationTree (Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("coindex head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}
	
}
