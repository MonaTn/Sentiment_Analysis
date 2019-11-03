package xExtraClass;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Negation.NegationAnalysis;
import Negation.NegationCues;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class test22 {
	static List<Tree> trees;
	static List<Tree> mainTrees;
	static List<String> patterns;
	static TregexPatternCompiler macros;

	public static void main(String[] args) throws IOException {
		// String fileName = "../../TrainingSet/Test/SFU/Movies/no12.txt";
		// String grammar =
		// "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		// String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		// LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
		// DocumentPreprocessor dp = documentPreprocessor(fileName);
		// for (List<HasWord> sentence : dp) {
		// Tree tree = lp.parse(sentence);
		// System.out.println(tree.toString());
		// }
		testTagger();
	}

	public static DocumentPreprocessor documentPreprocessor(String fileName)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				new FileReader(fileName));
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	public static List<TaggedWord> correctWords(List<TaggedWord> taggedWords) {
		for (TaggedWord taggedWord : taggedWords) {
			String word = taggedWord.word().toLowerCase();
			String tag = taggedWord.tag();
			if (word.equals("~ca") && tag.equals("MD")) {
				taggedWord.setWord("~can");
			}
		}
		return taggedWords;
	}

	public static Tree butprocess(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		int butPosition = -1;
		for (Tree leaf : leaves) {
			if (leaf.value().toLowerCase().equals("~but")) {
				butPosition = leaves.indexOf(leaf);
				System.out.println("index of but = " + butPosition);
				if (!isNegationCueAfterBut(butPosition, leaves)) {
					return correctWrongNegatedButClause(tree, butPosition);
				} else {
					return tree;
				}
			}
		}
		return tree;
	}

	public static boolean isNegationCueAfterBut(int butPosition,
			List<Tree> leaves) {
		NegationAnalysis negationProcessor = new NegationAnalysis();
		for (int i = butPosition; i < leaves.size(); i++) {
			if (NegationCues.isCues(leaves.get(i).value().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static Tree correctWrongNegatedButClause(Tree tree, int index) {
		int i = index;
		List<Tree> leaves = tree.getLeaves();
		while (i < leaves.size() && !leaves.get(i).value().equals(".")) {
			leaves.get(i).setValue(leaves.get(i).value().replace("~", ""));
			i++;
		}
		return tree;
	}

	public static void checkBut(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		if (leaves.contains("but")) {
			int i = leaves.indexOf("but");
			System.out.println("YES " + i);
		}
	}

	public static Tree correctWrongNegatedButClause(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		for (int i = 0; i < leaves.size(); i++) {
			if (isBut(leaves.get(i).value())) {
				while (i < leaves.size() && !leaves.get(i).value().equals(".")) {
					leaves.get(i).value().replaceFirst("~", "");
					i++;
				}
			}
		}
		return tree;
	}

	public static boolean isBut(String word) {
		return word.equals("~BUT") | word.equals("~But") | word.equals("~but");
	}

	public static Tree pruneButClause(Tree tree) {
		TregexPattern tregexPattern = TregexPattern
				.compile("__=head > CC=cc : (=cc < but) : (=cc $. __=everyNode)");
		List<TsurgeonPattern> surgeons = new ArrayList<TsurgeonPattern>();
		surgeons.add(Tsurgeon.parseOperation("prune everyNode"));
		Tsurgeon.processPattern(tregexPattern,
				Tsurgeon.collectOperations(surgeons), tree);
		tregexPattern = TregexPattern
				.compile("__=head > CC=cc : (=cc < but) : (=cc ?$. __=everyNode)");
		surgeons.add(Tsurgeon.parseOperation("prune cc"));
		surgeons.remove(0);
		Tsurgeon.processPattern(tregexPattern,
				Tsurgeon.collectOperations(surgeons), tree);
		return tree;
	}

	public static void mainMethod(Tree tree) {
		mainTrees = new ArrayList<Tree>();
		mainTrees.add(tree);
		Iterator<Tree> iterator = mainTrees.iterator();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		while (iterator.hasNext()) {
			negatedTrees.addAll(exteractNegatedSubTrees(iterator.next(),
					patterns, macros));
		}
	}

	public static List<Tree> exteractNegatedSubTrees(Tree tree,
			List<String> patterns, TregexPatternCompiler macros) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree subTree = matcher.getMatch();
				mainTrees.addAll(extractSBARsSubtrees(subTree));
				mainTrees.addAll(extractParentheticalSubtrees(subTree));
				negatedTrees.add(prune(subTree));
				System.out.println(subTree.toString());
			}
		}
		// printOutTree(mainTrees, "* MAin Trees");
		return negatedTrees;
	}

	public static void printOutTree(List<Tree> trees, String treesName) {
		System.out
				.println(treesName
						+ "_____________________________________________________________");
		for (Tree tree : trees) {
			System.out.println(tree.toString());
		}
		System.out
				.println(treesName
						+ "_____________________________________________________________");
	}

	public static List<Tree> xxx(Tree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(extractSBARsSubtrees(tree));
		trees.addAll(extractParentheticalSubtrees(tree));
		return trees;
	}

	public static List<Tree> extractSBARsSubtrees(Tree tree) {
		String pattern = "SBAR=sbar > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public static List<Tree> extractParentheticalSubtrees(Tree tree) {
		String pattern = "PRN=prn > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public static List<Tree> findSubTreeBasedOnAPattern(Tree tree,
			String pattern) {
		List<Tree> subTrees = new ArrayList<Tree>();
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		while (matcher.findNextMatchingNode()) {
			subTrees.add(matcher.getMatch());
		}
		// printOutTree(subTrees, "* SUB Trees ");
		return subTrees;
	}

	public static Tree applyPatternsOnTree(Tree tree, String pattern,
			String tsurgeonPattern) {
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TsurgeonPattern surgeon = Tsurgeon.parseOperation(tsurgeonPattern);
		Tsurgeon.processPattern(tregexPattern, surgeon, tree);
		return tree;
	}

	public static Tree pruneSBAR(Tree tree) {
		String pattern = "SBAR=sbar > __";
		String tsurgeonPattern = "prune sbar";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public static Tree prunePP(Tree tree) {
		String pattern = "PP=pp >__";
		String tsurgeonPattern = "prune pp";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public static Tree pruneParenthetical(Tree tree) {
		String pattern = "PRN=prn >__";
		String tsurgeonPattern = "prune prn";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public static Tree prune(Tree tree) {
		pruneSBAR(tree);
		pruneParenthetical(tree);
		prunePP(tree);
		return tree;
	}

	public static Tree treeMackerFromAString() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
		Tree tree = lp.parse("He doesn’t go to school.");
		System.out.println(tree.toString());
		tree = lp
				.parse("While I haven’t stayed in other Disney resorts, this hotel is described by the cast members as the frequent guests’ favorite. ");
		System.out.println(tree.toString());
		tree = lp.parse("It is my mother’s umbrella.");
		System.out.println(tree.toString());

		return tree;
	}

	public static List<Tree> removeTree(Tree tree) {
		trees = new ArrayList<Tree>();
		trees.addAll(extractSBARsSubtrees(tree));
		trees.add(tree);
		System.out.println("size of trees : " + trees.size());
		for (Tree tr : trees) {
			pruneSBAR(tr);
		}
		for (Tree subTree : trees) {
			System.out.println(subTree.toString());
		}
		return trees;
	}

	public static void testTagger() {

		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger(
				"TaggerModels/english-bidirectional-distsim.tagger");

		// The sample string
		String sample = "He doesn’t go to school, but go to game";

		// The tagged string
		String tagged = tagger.tagString(sample);

		// Output the result
		System.out.println(tagged);
		// The sample string
		sample = "It is my mother’s umbrella.";

		// The tagged string
		tagged = tagger.tagString(sample);

		// Output the result
		System.out.println(tagged);

		sample = "While I haven’t stayed in other Disney resorts, this hotel is described by the cast members as the frequent guests’ favorite.";

		// The tagged string
		tagged = tagger.tagString(sample);

		// Output the result
		System.out.println(tagged);
	}

	public static void testValueLabel(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		for (Tree leaf : leaves) {
			System.out
					.println("value = " + leaf.value() + " , label = "
							+ leaf.label() + " , label.value = "
							+ leaf.label().value()
							+ " , label.label factory = "
							+ leaf.label().labelFactory());
		}
	}

}
