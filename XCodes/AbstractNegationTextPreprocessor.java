package xExtraClass;

import java.util.ArrayList;
import java.util.List;

import Negation.NegationAnalysis;
import Negation.NegationCues;
import Negation.TregexTree;
import TextProcess.AbstractTextPreprocessor;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public abstract class AbstractNegationTextPreprocessor extends
		AbstractTextPreprocessor {
	private List<Tree> unnegatedTrees;
	private final List<String> patterns;
	private List<Tree> treesToProcess;
	private List<Tree> subExtractedTrees;
	private final TregexPatternCompiler macros;
	private LexicalizedParser parser;
	private final NegationAnalysis negationAnalyzer;

	public AbstractNegationTextPreprocessor() {
		initializeTagger();
		initializeLexicalizedParser();
		negationAnalyzer = new NegationAnalysis();
		patterns = negationAnalyzer.getPatternsList();
		macros = negationAnalyzer.getMacros();
	}

	private void initializeLexicalizedParser() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		parser = LexicalizedParser.loadModel(grammar, options);
	}

	/* Negation detection part */
	@Override
	protected List<TaggedWord> getTaggedSentence(List<HasWord> sentence) {
		List<TaggedWord> taggedWord = taggerModel.tagSentence(sentence);
		if (negationAnalyzer.containNegationCues(sentence)) {
			// System.out.println("___________________");
			// System.out.println("Negation happend ! ==> " + sentence);
			List<HasWord> negSentence = getTaggedWordsOfNegatedSentence(sentence);
			int i = 0;
			for (HasWord word : negSentence) {
				taggedWord.get(i).setWord(word.toString());
				i++;
			}
		}
		return taggedWord;
	}

	private List<HasWord> getTaggedWordsOfNegatedSentence(List<HasWord> sentence) {
		unnegatedTrees = new ArrayList<Tree>();
		treesToProcess = new ArrayList<Tree>();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		Tree treeOfSentence = parser.parse(sentence);
		// System.out
		// .println("@@ Tree of sentence : " + treeOfSentence.toString());
		treesToProcess.add(treeOfSentence);
		while (treesToProcess.size() != 0) {
			for (Tree tree : treesToProcess) {
				// System.out.println("Tree to process : " + tree.toString());
				negatedTrees.addAll(exteractNegatedTrees(tree));
			}
			treesToProcess.clear();
			treesToProcess.addAll(subExtractedTrees);
			subExtractedTrees.clear();
		}
		if (negatedTrees.isEmpty()) {
			// System.out.println("-- Negated tree is null!");
			return sentence;
			// return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTrees = removePseudoNegation(negatedTrees);
			addNegationSymbolToWords(realNegatedTrees);
			// taggedWords.addAll(getTaggedwords(realNegatedTrees));
			// taggedWords.addAll(getTaggedwords(unnegatedTrees));
			return sentence;
		}
	}

	private List<Tree> exteractNegatedTrees(Tree tree) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		subExtractedTrees = new ArrayList<Tree>(); // ?
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				TregexTree matchedTree = new TregexTree(matcher.getMatch());
				// System.out.println("**Negated Pattern : " + pattern);
				// System.out.println("** Negated Tree : "
				// + matchedTree.toString());
				subExtractedTrees
						.addAll(exteractUnrelevantSubtrees(matchedTree));
				negatedTrees.add(matchedTree.getTrees());
				// System.out.println("%%% Negated Tree After prunning: "
				// + matchedTree.toString());
			}
			deleteFormerMachedTrees(tree, tregexPattern);
		}
		// System.out.println(tree.toString());
		unnegatedTrees.add(tree);
		// System.out.println("Size of negated tree list: " +
		// negatedTrees.size());
		return negatedTrees;
	}

	private List<Tree> exteractUnrelevantSubtrees(TregexTree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(tree.getSBARs());
		tree.pruneSBAR();
		trees.addAll(tree.getParentheses());
		tree.pruneParentheses();
		trees.addAll(tree.getPPs());
		tree.prunePP();
		return trees;
	}

	private List<Tree> removePseudoNegation(List<Tree> trees) {
		for (int i = 0; i < trees.size(); i++) {
			if (negationAnalyzer.isPseudoNegation(trees.get(i))) {
				// System.out.println("--- pseudo negated tree : " +
				// tree.get(i));
				unnegatedTrees.add(trees.get(i));
				trees.remove(i);
			}
		}
		return trees;
	}

	private void addNegationSymbolToWords(List<Tree> trees) {
		for (Tree tree : trees) {
			List<Tree> leaves = tree.getLeaves();
			for (Tree leaf : leaves) {
				leaf.setValue("~" + leaf.value());
			}
			tree = exceptionWordsProcess(tree);
			// System.out.println("** Negated words : " + leaves.toString());
		}
	}

	// ***************
	private final String[] exceptionWords = { "~but", "~," };

	private Tree exceptionWordsProcess(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		int leavesSize = leaves.size();
		for (String word : exceptionWords) {
			int index = getIndexOfExceptionWord(leaves, word);
			if (needToEdit(index, leavesSize, leaves)) {
				tree = editWrongNegatedWords(tree, index);
			}
		}
		return tree;
	}

	private int getIndexOfExceptionWord(List<Tree> trees, String word) {
		for (Tree tree : trees) {
			String leafValue = tree.value().toLowerCase();
			if (leafValue.equals(word)) {
				return trees.indexOf(tree);
			}
		}
		return 0;
	}

	private boolean needToEdit(int index, int size, List<Tree> trees) {
		if (isValidIndex(size, index)
				&& !isNegationCueAfterExceptionWords(index, trees)) {
			return true;
		}
		return false;
	}

	private boolean isValidIndex(int size, int index) {
		return index != 0 && index < size - 2;
	}

	private boolean isNegationCueAfterExceptionWords(int index,
			List<Tree> leaves) {
		for (int i = index; i < leaves.size(); i++) {
			String word = leaves.get(i).value().replaceFirst("~", "");
			if (NegationCues.isCues(word)) {
				return true;
			}
		}
		return false;
	}

	private Tree editWrongNegatedWords(Tree tree, int index) {// ???
		List<Tree> leaves = tree.getLeaves();
		if (leaves.get(index).value().equals("~,") && hasSameTag(index, tree)) {
			return tree;

		} else {
			leaves = removeNegationSymbol(index, leaves);
			return tree;
		}
	}

	private boolean hasSameTag(int index, Tree tree) {// check valid index ?
		List<Label> preterminalsNodes = tree.preTerminalYield();
		if (isValidIndex(preterminalsNodes.size(), index)) {
			return preterminalsNodes.get(index - 1).equals(
					preterminalsNodes.get(index + 1));
		}
		return false;
	}

	private List<Tree> removeNegationSymbol(int index, List<Tree> trees) {
		int i = index;
		while (i < trees.size()) {
			trees.get(i).setValue(trees.get(i).value().replace("~", ""));
			i++;
		}
		return trees;
	}

	@Override
	protected String editingMisspellingWords(String word, String tag) { // dic
																		// //Dec
																		// 9
		if (word.equalsIgnoreCase("~ca") && tag.equals("Modal")) {
			return "~can";
		} else if (word.equals("~wo") && tag.equals("Modal")) {
			return "~will";
		} else if (word.equals("~'s") && tag.equals("Verb_Z")) {
			return "~is";
		} else if (word.equals("~'m") && tag.equals("Verb_P")) {
			return "~am";
		} else if (word.equals("~doe") && tag.equals("Verb_Z")) {
			return "~does";
		}
		return word;
	}

	// private List<TaggedWord> getTaggedwords(List<Tree> trees) {
	// List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
	// for (Tree tree : trees) {
	// taggedWords.addAll(tree.taggedYield());
	// }
	// editingMisspellingWords(taggedWords);
	// return taggedWords;
	// }

	// private List<TaggedWord> editingMisspellingWords(
	// List<TaggedWord> taggedWords) { // dic
	// for (TaggedWord taggedWord : taggedWords) {
	// String word = taggedWord.word().toLowerCase();
	// String tag = taggedWord.tag();
	// if (word.equals("~ca") && tag.equals("MD")) {
	// taggedWord.setWord("~can");
	// } else if (word.equals("~wo") && tag.equals("MD")) {
	// taggedWord.setWord("~will");
	// }
	// }
	// return taggedWords;
	// }

	private Tree deleteFormerMachedTrees(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head"); // prune
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	/* ************ */

}