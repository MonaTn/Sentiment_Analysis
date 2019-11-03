package xTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Negation.NegationPreprocessor;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

public class TreeTester {

	public static void main(String[] args) {
		Tree tree = Tree
				.valueOf("(ROOT (S (NP (NP (NN laCK)) (PP (IN of) (NP (NN hope) (, ,) (NN ~love) (CC ~and) (NN ~mony)))) (VP (VBD ~was) (VP (VBN ~kiled) (NP (PRP ~her)))) (. !)))");
		treeMackerFromAString();
	}

	private Tree exceptionWordProcess(Tree tree) {
		tree = butProcess(tree);
		tree = commaProcess(tree);
		return tree;
	}

	private Tree butProcess(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		int index = getIndexOfExceptionWord(leaves, "~but");
		if (isValidIndex(leaves.size(), index)
				&& !isNegationCueAfterExceptionWords(index, tree)) {
			return editWrongNegatedWords(tree, index);
		} else {
			return tree;
		}
	}
	
	private Tree commaProcess(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		int index = getIndexOfExceptionWord(leaves, ",");
		if (isValidIndex(leaves.size(), index)
				&& !isNegationCueAfterExceptionWords(index, tree) && !isSameTag(index, tree)) {
			return editWrongNegatedWords(tree, index);
		} else {
			return tree;
		}
	}

	private Tree exceptionWordProcess1(Tree tree, String word) {
		List<Tree> leaves = tree.getLeaves();
		int index = getIndexOfExceptionWord(leaves, word);
		if (index <= 0 || index >= leaves.size()
				|| isNegationCueAfterExceptionWords(index, tree)) {
			return tree;
		} else {
			return editWrongNegatedWords(tree, index);
		}
	}

	private int getIndexOfExceptionWord(List<Tree> trees, String word) {
		for (Tree tree : trees) {
			String leafValue = tree.value().toLowerCase();
			if (leafValue.equals(word)) {
				return trees.indexOf(tree);
			}
		}
		return -1;
	}

	private boolean needToEdit(int index, Tree tree) {
		int size = tree.getLeaves().size();
		System.out.println(size);
		if (isValidIndex(size, index)
				|| !isNegationCueAfterExceptionWords(index, tree)
				|| !isSameTag(index, tree)) {
			return true;
		}
		return false;
	}

	private boolean isValidIndex(int size, int index) {
		return index > 0 || index < size - 2;
	}

	private boolean isNegationCueAfterExceptionWords(int index, Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		NegationPreprocessor negationProcessor = new NegationPreprocessor();
		for (int i = index; i < leaves.size(); i++) {
			String word = leaves.get(i).value().replaceFirst("~", "");
			if (negationProcessor.isNegationCues(word)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSameTag(int index, Tree tree) {
		List<Label> preterminalsNodes = tree.preTerminalYield();
		if (isValidIndex(preterminalsNodes.size(), index)) {
			return preterminalsNodes.get(index - 1).equals(
					preterminalsNodes.get(index + 1));
		}
		return false;
	}

	private Tree editWrongNegatedWords(Tree tree, int index) {
		int i = index;
		List<Tree> leaves = tree.getLeaves();
		while (i < leaves.size() && !leaves.get(i).value().equals(".")) {
			leaves.get(i).setValue(leaves.get(i).value().replace("~", ""));
			i++;
		}
		return tree;
	}

	private boolean equalTags(String word, List<Tree> trees, int index) {
		if (word.equals(",")) {
			ArrayList<TaggedWord> previousLeaf = trees.get(index - 1)
					.taggedYield();
			ArrayList<TaggedWord> nextLeaf = trees.get(index - 1).taggedYield();
			if (previousLeaf.get(0).tag().equals(nextLeaf.get(0).tag())) {
				return true;
			}
		}
		return false;
	}

	public static void testLeaf(List<Tree> leaves) {
		Tree leaf = leaves.get(3);
		List<Tree> p = leaf.preOrderNodeList();
		// Tree pp = tree.getChild(0).parent();
	}

	public static void cc(Tree tree, int index) {
		List<Label> preterminals = tree.preTerminalYield();
		List<Tree> leaves = tree.getLeaves();
		for (int i = 0; i < leaves.size(); i++) {
			System.out.println(preterminals.get(i).toString());
			System.out.println(leaves.get(i).value());
		}

		if (preterminals.get(index - 1).equals(preterminals.get(index + 1))) {
			for (int i = index; i < leaves.size(); i++) {
				leaves.get(i).setValue(
						leaves.get(i).value().replaceFirst("~", ""));
			}
		}
		System.out.println(tree.toString());
	}

	public static Tree treeMackerFromAString() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
		Tree tree = lp.parse("He doesn’t go to school.");
		System.out.println(tree.toString());
		tree = lp.parse("He has no self confidence, self esteem and love.");
		System.out.println(tree.toString());
		tree = lp.parse("not charming but nasty");
		System.out.println(tree.toString());

		return tree;
	}
}
