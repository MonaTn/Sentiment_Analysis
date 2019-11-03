package methods_tester;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nlp.text_processor.Document;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;

public class TestDisPOSTagFile {

	public static boolean isIncorrectPOSTag(Document document,
			LexicalizedParser parser) throws IOException {
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			Tree treeOfSentence = parser.parse(sentence);
			String strTree = treeOfSentence.toString();
			if (strTree.contains("(X ")) {
				System.out.println(document.getName() + " : "
						+ sentence.toString());
			}
		}
		return false;
	}

	private static DocumentPreprocessor documentPreprocessing(Document document)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String fileName = document.getAbsoluteFileName();
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				new FileReader(fileName));
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}
}
