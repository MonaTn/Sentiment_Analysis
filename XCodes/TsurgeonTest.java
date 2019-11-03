package xExtraClass;


import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class TsurgeonTest {

	static LexicalizedParser parser = LexicalizedParser
			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	public static void main(String[] args) {
		 Tree t = Tree.valueOf("(ROOT (S (NP (NP (NNP Bank)) (PP (IN of) (NP (NNP America)))) (VP (VBD called)) (. .)))");
		 TregexPattern pat = TregexPattern.compile("NP <1 (NP << Bank) <2 PP=remove");
		 TsurgeonPattern surgery = Tsurgeon.parseOperation("delete remove");
		 Tsurgeon.processPattern(pat, surgery, t).pennPrint();
		 System.out.println(t.toString());
		
//		 TregexPattern matchPattern = TregexPattern.compile("SQ=sq < (/^WH/ $++ VP)");
//		 List<TsurgeonPattern> ps = new ArrayList<TsurgeonPattern>();
//
//		 TsurgeonPattern p = Tsurgeon.parseOperation("relabel sq S");
//
//		 ps.add(p);
//		 Treebank lTrees ;
//		 List<Tree> result = Tsurgeon.processPatternOnTrees(matchPattern,Tsurgeon.collectOperations(ps),lTrees);
		 
	}

}
