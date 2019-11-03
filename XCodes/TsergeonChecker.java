package xExtraClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class TsergeonChecker {

	public static void main(String[] args) throws IOException {
//		Tree mainTree = Tree.valueOf("(ROOT (S (NP (NNP Maria_Eugenia_Ochoa_Garcia)) (VP (VBD was) (VP (VBN arrested) (PP (IN in) (NP (NNP May))))) (. .)))");
		Tree mainTree = Tree
				.valueOf("(ROOT (S (NP (NNP Conclusion)) (VP (VBP Do) (RB n't) (VP (VP (VB buy) (NP (DT the) (NN CD))) (, ,) (CC but) (ADVP (RB definitely)) (VP (VB buy) (NP (NP (DT the) (NN song)) (, ,) (`` ``) (NP (NNP Hey) (NNP Ma)) ('' ''))))) (. .)))");
		TregexPattern pattern = TregexPattern.compile("__=head > CC=cc : (=cc < but) : (=cc $. __=everyNode)");
		BufferedReader bf = new BufferedReader (new FileReader("TsurgeonScript.txt"));
		List<TsurgeonPattern> listOfTsurgeonPatterns = new ArrayList<TsurgeonPattern>();
		while (bf.ready()){
			listOfTsurgeonPatterns.add(Tsurgeon.getTsurgeonOperationsFromReader(bf));
		}
		TsurgeonPattern surgeon = Tsurgeon.collectOperations(listOfTsurgeonPatterns);
		Tsurgeon.processPattern(pattern, surgeon, mainTree).pennPrint();
		System.out.println(Arrays.toString(listOfTsurgeonPatterns.toArray()));
	}

	public static Tree removePP(Tree tree) {
		String pattern = "PP=pp >__";
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		List<TsurgeonPattern> listOfSurgeos = new ArrayList<TsurgeonPattern>();
		listOfSurgeos.add(Tsurgeon.parseOperation("coindex pp"));
		listOfSurgeos.add(Tsurgeon.parseOperation("prune pp"));
		TsurgeonPattern surgeon = Tsurgeon.collectOperations(listOfSurgeos);
		Tsurgeon.processPattern(tregexPattern, surgeon, tree);
		return tree;
	}
}
