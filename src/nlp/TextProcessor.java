package nlp;

import java.io.IOException;

import nlp.text_processor.Document;
import nlp.text_processor.WordList;

public interface TextProcessor {

	public WordList extractWordList(Document document) throws IOException;

	public void enableNegationAnalysis(boolean flag);

}