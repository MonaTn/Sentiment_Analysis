package Negation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class ExteraMethods {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void printOutTree(List<Tree> trees, String treesName) {
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

	public List<Tree> xxx(Tree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(extractSBARsSubtrees(tree));
		trees.addAll(extractParentheticalSubtrees(tree));
		return trees;
	}

	public List<Tree> extractSBARsSubtrees(Tree tree) {
		String pattern = "SBAR=sbar > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public List<Tree> extractParentheticalSubtrees(Tree tree) {
		String pattern = "PRN=prn > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public List<Tree> findSubTreeBasedOnAPattern(Tree tree, String pattern) {
		List<Tree> subTrees = new ArrayList<Tree>();
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		while (matcher.findNextMatchingNode()) {
			subTrees.add(matcher.getMatch());
		}
		printOutTree(subTrees, "* SUB Trees ");
		return subTrees;
	}

	public Tree applyPatternsOnTree(Tree tree, String pattern,
			String tsurgeonPattern) {
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TsurgeonPattern surgeon = Tsurgeon.parseOperation(tsurgeonPattern);
		Tsurgeon.processPattern(tregexPattern, surgeon, tree);
		return tree;
	}

	public Tree pruneSBAR(Tree tree) {
		String pattern = "SBAR=sbar > __";
		String tsurgeonPattern = "prune sbar";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public Tree prunePP(Tree tree) {
		String pattern = "PP=pp >__";
		String tsurgeonPattern = "prune pp";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public Tree pruneParenthetical(Tree tree) {
		String pattern = "PRN=prn >__";
		String tsurgeonPattern = "prune prn";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public Tree prune(Tree tree) {
		pruneSBAR(tree);
		pruneParenthetical(tree);
		prunePP(tree);
		return tree;
	}

	public void pruneTree(Tree tree) {
		System.out.println("@@ Main Tree : " + tree.toString());
		System.out.println("Size of Tree : " + tree.numChildren());
		List<Tree> extra = new ArrayList<Tree>();
		for (int i = 0; i < tree.numChildren(); i++) {
			System.out.println("get " + i + " : " + tree.getChild(i).value());
			if (tree.getChild(i).value().equals("SBAR")
					|| tree.getChild(i).value().equals("S")) {
				extra.add(tree.getChild(i));
				System.out.println("Extra at " + i + " is "
						+ Arrays.toString(extra.toArray()));
				tree.removeChild(i);
			}
		}
		System.out.println("Tree: " + tree.toString());
		System.out.println("Extra: " + Arrays.toString(extra.toArray()));
	}

	public List<Tree> extractNegatedSubTree(Tree tree , List<String> patterns, TregexPatternCompiler macros) {
		// System.out.println ("Tree to process : " + tree.toString());
		List<Tree> negatedTrees = new ArrayList<Tree>();
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree subTree = matcher.getMatch();
				negatedTrees.add(subTree);
				System.out.println("**Negated Pattern : " + pattern);
				System.out.println("** Negated Tree : " + subTree.toString());
			}
			deleteSubTree(tree, tregexPattern);
		}
		// listOfRestOfTrees.add(tree);
		// System.out.println("-- Rest of the tree : " + tree.toString());
		return negatedTrees;
	}

	public boolean isContainSBAR(Tree tree) {
		String pattern = "SBAR=sbar > __";
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		return matcher.find();
	}

	List<Tree> treesToFindNegation;

	public List<Tree> extractNegatedSubTree1(Tree mainTree,
			List<String> patternsList, TregexPatternCompiler macros) {
		System.out.println("@@ Main Tree : " + mainTree.toString());
		List<Tree> negatedTrees = new ArrayList<Tree>();
		treesToFindNegation = new ArrayList<Tree>();
		treesToFindNegation.add(mainTree);
		System.out.println(treesToFindNegation.size());
		while (treesToFindNegation.iterator().hasNext()) {
			System.out.println("===== Size :" + treesToFindNegation.size());
			Tree tree = treesToFindNegation.iterator().next();// .get(treesToFindNegation.size()-1);
			for (String pattern : patternsList) {
				TregexPattern tregexPattern = macros.compile(pattern);
				TregexMatcher matcher = tregexPattern.matcher(tree);
				while (matcher.findNextMatchingNode()) {
					Tree subTree = matcher.getMatch();
					subTree = verifyContainSBAR1(subTree);
					negatedTrees.add(subTree);
					System.out.println("**Negated Pattern : " + pattern);
					System.out.println("** Negated Tree : "
							+ subTree.toString());
				}
				deleteSubTree(tree, tregexPattern);

			}
		}
		// listOfRestOfTrees.add(mainTree);
		// System.out.println("-- Rest of the tree : " + tree.toString());
		return negatedTrees;
	}

	public Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	public Tree verifyContainSBAR1(Tree tree) {
		for (int i = 0; i < tree.numChildren(); i++) {
			if (tree.getChild(i).value().equals("SBAR")) {
				treesToFindNegation.add(tree.getChild(i));
				tree.removeChild(i);
			}
		}
		return tree;
	}
}
