package methods_tester;

import java.io.IOException;

import nlp.TextProcessor;
import nlp.TextProcessorFactory;
import nlp.text_processor.Document;
import nlp.text_processor.WordList;

public class TestWordList {

	public static void main(String[] args) throws Throwable, IOException {
		String strPath = "../../Results/2014/ErrorAnalysis/";
		String strFileName = "neg920.text";
		extractWordList(strPath, strFileName);
	}

	protected static WordList extractWordList(String strPath, String strFileName)
			throws ClassNotFoundException, IOException {
		Document document = new Document(strFileName, strPath, false);
		TextProcessor processor;
		TextProcessorFactory factory = new TextProcessorFactory();
		factory.setStemmer("SnowBall_Stemmer");
		processor = factory.create();
		processor.enableNegationAnalysis(true);
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		// Utility.writeWordsListToFile(strPath, document);
		return wordList;
	}

}
