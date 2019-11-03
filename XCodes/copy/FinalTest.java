package X.Negation.copy;

import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class FinalTest {
	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static final String fileName = "../../TrainingSet/Test/SFU/Phone/yes1.txt";

	public static void main(String[] args) {
		NegationPreprocessor negationProcessor = new NegationPreprocessor();
		patternsList = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
		TextPreprocessorWithNegationFinder1 nProcessor = new TextPreprocessorWithNegationFinder1();
		for (List<HasWord> sentence : new DocumentPreprocessor(fileName)) {
			nProcessor.testProcessor(sentence);
		}
	}

}
