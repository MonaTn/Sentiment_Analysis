package Negation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class home {

	static PatternsList patternsList = new PatternsList();
	static TregexPatternCompiler macros;

	public static void main(String[] args) {
		buildPatternList(); // ?

		macros = new TregexPatternCompiler();
		defineMarcos(macros); // ?

		String fileName = "../../TrainingSet/Test/test.txt";

		LexicalizedParser parser = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

		List<Tree> negationTree = null; // !!!!!!!

		if (fileName != "") {
			negationTree = buildNegationTree(parser, fileName);
		} else {
			System.out.println("Please select a file!");
		}
		System.out
		.println("___________________________________________________________________________");
		pruneTree(negationTree);
	}

	private static void buildPatternList() {
		patternsList.add("VP < @VB=vb < @PRN : (=vb < @Be $+ @NOT)");
		patternsList.add("VP < @VB=vb < VP : (=vb < @Be $+ @NOT)");
		patternsList.add("VP < @VB=vb < VP : (=vb < @Aux $+ @NOT)");
		patternsList.add("VP < MD=md < VP : (=md $+ @NOT)");
	}

	private static void defineMarcos(TregexPatternCompiler macros) {
		macros.addMacro("@VB", "/^VB/");
		macros.addMacro("@Be", "am|is|are|was|were");
		macros.addMacro("@Aux", "have|has|had|do|does|did");
		macros.addMacro("@NOT", "(RB < /n['o]t/)");
		macros.addMacro("@PRN", "NP|ADJP|PP|ADVP|SBAR|S");
		macros.addMacro("@Be-Not", "(@VB < @Be $+ @NOT)");
	}

	private static Tree extractNegatedSubTree(Tree parse,
			TregexPatternCompiler macros) {
		for (String pattern : patternsList.getPatterns()) {
			TregexPattern negationRegEX = macros.compile(pattern);
			TregexMatcher matcher = negationRegEX.matcher(parse);
			if (matcher.find()) {
				Tree subTree = matcher.getMatch();
				// System.out.println("Pattern:" + pattern);
				System.out.println("Negated sub tree: " + subTree.toString());
				return subTree;
			}
		}
		return null;
	}

	private static List<Tree> buildNegationTree(LexicalizedParser parser,
			String filename) {
		List<Tree> negationTree = new ArrayList<Tree>();
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) { // **
			System.out.println(sentence.toString());
			Tree pennTree = parser.apply(sentence);
			Tree negatedSubTree = extractNegatedSubTree(pennTree, macros);
			if (negatedSubTree != null) {
				negationTree.add(negatedSubTree);
			}
		}
		return negationTree;
	}
//	Tree[] children = tree.children();
	// Tree test = tree.getNodeNumber(7);

	public static void pruneTree(List<Tree> treeList) {
		for (Tree subTree : treeList) {
			List<Tree> listOfNodeAfterNegatedTerm = new ArrayList<Tree>();
			listOfNodeAfterNegatedTerm = subTree.getChild(2).getChildrenAsList();
			System.out.println ("Children as List : "+Arrays.toString(listOfNodeAfterNegatedTerm.toArray()));
			int sizeOfNegatedNodes = listOfNodeAfterNegatedTerm.size();
			for (Tree negatedNode : listOfNodeAfterNegatedTerm) {
				String valueLable = negatedNode.label().value();
				System.out.println("Negated new: " + valueLable);
				if (valueLable.equals("S") | valueLable.equals("PP")) {
				negatedNode.remove(valueLable);
				System.out.println(valueLable +" is deleted!!");
				}
				System.out.println(negatedNode.toString());
			}
//			for (int i = 0; i < sizeOfNegatedNodes ; i++) {
//				String valueLable = listOfNodeAfterNegatedTerm.get(i).label().value();
//				System.out.println("***************" + valueLable);
//				if (valueLable.equals("S") | valueLable.equals("PP")) {
//					System.out.println("Before remove "+ valueLable+" : " + listOfNodeAfterNegatedTerm.toString());
//					listOfNodeAfterNegatedTerm.remove(i);
//				} else {
//					List<TaggedWord> tag = listOfNodeAfterNegatedTerm.get(i).taggedYield();
//					for (TaggedWord ss : tag) {
//						System.out.println(ss);
//					}
//				}
//			}
			System.out.println("Final  " + listOfNodeAfterNegatedTerm.toString());
			System.out
			.println("___________________________________________________________________________");
		}
	}
}
