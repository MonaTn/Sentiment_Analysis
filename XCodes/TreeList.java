package Negation;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class TreeList {
	private List<Tree> treeList;
	
	public TreeList() {
		treeList = new ArrayList<Tree>();
	}
	
	public Tree getFirst() {
		return treeList.get(0);
	}
	
	public boolean isAuxiliary() {
		Tree node = getFirst();
		TregexPattern tregexPattern = TregexPattern.compile("have|has|had|'ve|'s|do|does|did");
		TregexMatcher matcher = tregexPattern.matcher(node);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isBe() {
//		TregexPattern tregexPattern = TregexPattern.compile("have|has|had|'ve|'s|do|does|did");
//		TregexMatcher matcher = tregexPattern.matcher(node);
//		if (matcher.find()) {
//			
//		}
		return false;
	}

}
