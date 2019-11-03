package Negation;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class FinalTest {
	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static final String fileName = "../../TrainingSet/Test/SFU/Computer/no5.txt";

	public static void main(String[] args) throws IOException {
		NegationPreprocessor negationProcessor = new NegationPreprocessor();
		patternsList = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
		TextPreprocessorWithNegationFinder2 nProcessor = new TextPreprocessorWithNegationFinder2();
		DocumentPreprocessor processor = documentPreprocessor(fileName);
		for (List<HasWord> sentence : processor) {
			nProcessor.testProcessor(sentence);
		}
	}

	private static DocumentPreprocessor documentPreprocessor(String fileName)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				new FileReader(fileName));
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

}
