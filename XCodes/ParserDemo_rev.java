package xExtraClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class ParserDemo_rev {

	/**
	 * The main method demonstrates the easiest way to load a parser. Simply
	 * call loadModel and specify the path, which can either be a file or any
	 * resource in the classpath. For example, this demonstrates loading from
	 * the models jar file, which you need to include in the classpath for
	 * ParserDemo to work.
	 */
	public static void main(String[] args) {
		LexicalizedParser lp = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		String fileName = "../../TrainingSet/Test/test_rather.txt";
		if (fileName != "") {
			demoDP(lp, fileName);
		} else {
			demoAPI(lp);
		}
	}

	/**
	 * demoDP demonstrates turning a file into tokens and then parse trees. Note
	 * that the trees are printed by calling pennPrint on the Tree object. It is
	 * also possible to pass a PrintWriter to pennPrint if you want to capture
	 * the output.
	 */
	public static void demoDP(LexicalizedParser lp, String filename) {
		// This option shows loading and sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);
			List<Tree> testTree = new ArrayList<Tree>();

			// for (Tree subtree : parse) {
			// if (subtree.label().value().equals("VP")) {
			// testTree.add(subtree);
			// }
			// }
			//
//			String pattern = "VP < ((/VB.?/ < am|is|are|was|were) $+ (RB < /n['o]t/)) [< NP | < VP]";// "\\(VP \\([am|is|was|were|are]\\) .*\\)";//
//			String pat = "@NP < /NN.?/";																			// "\\(VP \\(.* [am|is|are|was|were]\\) \\(RB n[o’]t\\) \\(*\\)\\)";
//			TregexPattern tgrepPattern = TregexPattern.compile(pattern);
//			TregexMatcher m = tgrepPattern.matcher(parse);
//			while (m.find()) {
//				Tree subtree = m.getMatch();
//				testTree.add(subtree);
//				System.out.println(subtree.toString());
//			}

			TregexPatternCompiler tpc = new TregexPatternCompiler();
			tpc.addMacro("@VB", "/^VB/");
			tpc.addMacro("@Be", "am|is|are|was|were");
			tpc.addMacro("@Aux", "have|has|had|do|does|did");
			tpc.addMacro("@NEGTerm", "(RB < /n['o]t/)");
			tpc.addMacro("@PRN", "NP|ADJP|PP|ADVP|SBAR|S");
			tpc.addMacro("@Modal", "can|could|may|might|will|would|must|shall|should|ought");
			tpc.addMacro("@Be-Not", "(@VB < @Be $+ @NEGTerm)");
//			TregexPattern test = tpc.compile("NP = cnp < DT !$ PP");
//			TregexPattern test = tpc.compile("VP < ((@VB < @Be) $+ @NEGTerm) <@PRN");
//			TregexPattern test = tpc.compile("VP < ((@VB < @Be) $+ @NEGTerm) < @PRN");
//			TregexPattern test = tpc.compile("VP < @VB =vb < @NEGTerm < @PRN =xc ");
//			TregexPattern test = tpc.compile("VP < ((MD < @Modal) $+ @NEGTerm) < @PRN");
//			TregexPattern test = tpc.compile("VP < (MD $+ @NEGTerm) < VP "); //GOOD
//			TregexPattern test = tpc.compile("VP < MD=md < VP: =md $+ @NEGTerm ");
//			TregexPattern test = tpc.compile("VP < @VB=vb : =vb < @Be : =vb $+ @NEGTerm");
//			TregexPattern test = tpc.compile("VP < @VB=vb < @PRN :(=vb < @Be $+ @NEGTerm)");
//			TregexPattern test = tpc.compile("@VB  < @Be $+ @NEGTerm");

			TregexPattern test = tpc.compile("VP < ((@VB < @Aux) $+ @NEGTerm) < VP");
			

			TregexMatcher mth = test.matcher(parse);
			if (mth.find()) {
				Tree subtree = mth.getMatch();
				testTree.add(subtree);
				System.out.println("Ter Compiler: "+subtree.toString());
			}
//			for (Tree subTree : testTree) {
//				String prdPattern = "NP";
//				 TregexPattern prd = TregexPattern.compile(prdPattern);
//				 TregexMatcher macher = prd.matcher(subTree);
//				 if (macher.find()) {
//				 Tree prdTree = m.getMatch();
//				 System.out.println("PRD pattern: "+prdTree.toString());
//				 }
//			}
			System.out.println("--- Pattern: " + testTree.toString());
			 System.out.println("***" + parse.toString());
//			parse.pennPrint();
//			System.out.println(parse.pennString());
			System.out.println();

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			@SuppressWarnings("rawtypes")
			Collection tdl = gs.typedDependenciesCCprocessed();
			 System.out.println("td1: "+tdl);
			 System.out.println();
		}
	}

	/**
	 * demoAPI demonstrates other ways of calling the parser with already
	 * tokenized text, or in some cases, raw text that needs to be tokenized as
	 * a single sentence. Output is handled with a TreePrint object. Note that
	 * the options used when creating the TreePrint can determine what results
	 * to print out. Once again, one can capture the output by passing a
	 * PrintWriter to TreePrint.printTree.
	 */
	public static void demoAPI(LexicalizedParser lp) {
		// This option shows parsing a list of correctly tokenized words
		String[] sent = { "This", "is", "an", "easy", "sentence", "." };
		List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
		Tree parse = lp.apply(rawWords);
		parse.pennPrint();
		System.out.println();

		// This option shows loading and using an explicit tokenizer
		String sent2 = "This is another sentence.";
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords2 = tokenizerFactory.getTokenizer(
				new StringReader(sent2)).tokenize();
		parse = lp.apply(rawWords2);

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);
		System.out.println();

		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		tp.printTree(parse);
	}

	private ParserDemo_rev() {
	} // static methods only

}
