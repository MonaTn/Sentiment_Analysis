package negation;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class TregexTree {

	private Tree tree;

	public TregexTree(Tree tree) {
		this.tree = tree;
	}

	public List<Tree> getSBARs() {
		String pattern = "SBAR=sbar > __ !>> SBAR";
		return getSubTrees(pattern);
	}

	public List<Tree> getParentheses() {
		String pattern = "PRN=prn > __ !>> PRN";
		return getSubTrees(pattern);
	}

	public List<Tree> getPPs() {
		String pattern = "PP=pp >__ : (__=label >, =pp) : (=label !< /^(?i:of)/ !< /^(?i:for)/ !< /^(?i:with)/)";
		return getSubTrees(pattern);
	}

	public List<Tree> getSubTrees(String pattern) {
		List<Tree> subTrees = new ArrayList<Tree>();
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		while (matcher.find()) {
			subTrees.add(matcher.getMatch());
		}
		return subTrees;
	}

	private Tree applyPatterns(String pattern, String tsurgeonPattern) {
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TsurgeonPattern surgeon = Tsurgeon.parseOperation(tsurgeonPattern);
		Tsurgeon.processPattern(tregexPattern, surgeon, tree);
		return tree;
	}

	public Tree pruneSBAR() {
		String pattern = "SBAR=sbar > __";
		String tsurgeonPattern = "prune sbar";
		return applyPatterns(pattern, tsurgeonPattern);
	}

	public Tree prunePP() {
		String pattern = "PP=pp >__ : (__=label >, =pp) : (=label !< /^(?i:of)/ !< /^(?i:for)/ !< /^(?i:with)/)";
		String tsurgeonPattern = "prune pp";
		return applyPatterns(pattern, tsurgeonPattern);
	}

	public Tree pruneParentheses() {
		String pattern = "PRN=prn >__";
		String tsurgeonPattern = "prune prn";
		return applyPatterns(pattern, tsurgeonPattern);
	}

	public Tree prune() {
		pruneSBAR();
		pruneParentheses();
		prunePP();
		return tree;
	}

	public Tree getTrees() {
		return tree;
	}

	public void toPrint() {
		System.out.println(tree.toString());
	}

	@Override
	public String toString() {
		return tree.toString();
	}
}
