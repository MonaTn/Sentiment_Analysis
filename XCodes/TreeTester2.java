package xExtraClass;

import java.util.ArrayList;
import java.util.List;

import Negation.NegationAnalysis;
import Negation.NegationCues;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

public class TreeTester2 {

	public static void main(String[] args) {
		Tree tree = Tree
				.valueOf("(ROOT (S (NP (NNP Buddy)) (VP (VBZ is) (RB not) (NP (NP (DT the) (JJ typical) (NN elf)) (, ~,)  (NP (PRP$ ~his) (JJ ~large) (NN ~size) (S (VP (VBG ~being) (NP (DT ~the) (JJS ~biggest) (NN ~factor))))))) (. ~.)))");
		Tree tree1 = exceptionWordProcess1(tree);
		System.out.println(tree1.toString());
		tree = Tree
				.valueOf("(ROOT (S (NP (PRP He)) (VP (VBZ has) (NP (DT no) (NN ~self-confidence) (, ~,) (NN ~self-esteem) (CC ~and) (NN ~love))) (. ~.)))");
		tree1 = exceptionWordProcess1(tree);
		System.out.println(tree1.toString());
		tree = Tree
				.valueOf("(ROOT (S (S (NP (PRP$ My) (JJ old) (NN car)) (VP (VBZ ~does) (RB ~not) (VP (VB ~work) (ADVP (RB ~well))))) (CC ~but) (S (NP (PRP ~it)) (VP (VBZ ~is) (ADJP (RB ~too) (JJ ~old)))) (. ~!)))");
		tree1 = exceptionWordProcess1(tree);
		System.out.println(tree1.toString());
	}

	public static String[] exceptionWords = { "~but", "~," };

	public static Tree exceptionWordProcess1(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		int sizeLeaves = leaves.size();
		for (String word : exceptionWords) {
			int index = getIndexOfExceptionWord(leaves, word);
			if (needToEdit(index, sizeLeaves, leaves)) {
				tree = editWrongNegatedWords(tree, index);
			}
		}
		return tree;
	}

	public static int getIndexOfExceptionWord(List<Tree> trees, String word) {
		for (Tree tree : trees) {
			String leafValue = tree.value().toLowerCase();
			if (leafValue.equals(word)) {
				return trees.indexOf(tree);
			}
		}
		return 0;
	}

	public static boolean needToEdit(int index, int size, List<Tree> trees) {
		if (isValidIndex(size, index)
				&& !isNegationCueAfterExceptionWords(index, trees)) {
			return true;
		}
		return false;
	}

	public static boolean isValidIndex(int size, int index) {
		return index != 0 && index < size - 2;
	}

	public static boolean isNegationCueAfterExceptionWords(int index,
			List<Tree> leaves) {
		System.out.println(index);
		NegationAnalysis negationProcessor = new NegationAnalysis();
		for (int i = index; i < leaves.size(); i++) {
			String word = leaves.get(i).value().replaceFirst("~", "");
			if (NegationCues.isCues(word)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasSameTag(int index, Tree tree) {
		List<Label> preterminalsNodes = tree.preTerminalYield();
		if (isValidIndex(preterminalsNodes.size(), index)) {
			return preterminalsNodes.get(index - 1).equals(
					preterminalsNodes.get(index + 1));
		}
		return false;
	}

	public static Tree editWrongNegatedWords(Tree tree, int index) {
		List<Tree> leaves = tree.getLeaves();
		if (leaves.get(index).value().equals("~,") && hasSameTag(index, tree)) {
			return tree;
		} else {
			leaves = removeNegationSymbol(index, leaves);
			return tree;
		}

	}

	public static List<Tree> removeNegationSymbol(int index, List<Tree> trees) {
		int i = index;
		while (i < trees.size()) {
			trees.get(i).setValue(trees.get(i).value().replace("~", ""));
			i++;
		}
		return trees;
	}

	public boolean equalTags(String word, List<Tree> trees, int index) {
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
