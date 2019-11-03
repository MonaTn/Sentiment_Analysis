package Negation;

import java.io.IOException;
import java.util.List;

import shared.Document;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class FinalTest {
	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static final String fileName = "pyes19.txt";
	static final String path = "../../TrainingSet/Negation Train/Test SFU";

	public static void main(String[] args) throws IOException, Exception {
		NegationPreprocessor negationProcessor = new NegationPreprocessor();
		patternsList = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
		Document document = new Document(fileName, path, false);
		NegationPreprocessor_MinMinStrategy nProcessor = new NegationPreprocessor_MinMinStrategy();
		nProcessor.extractWordsListOfDocument(document);
	}
}
