package nlp;

import nlp.stemmer.PorterStemmer;

public class PorterStemmerTextPreprocessor extends AbstractTextPreprocessor {

	private final PorterStemmer stemmer;

	public PorterStemmerTextPreprocessor() {
		stemmer = new PorterStemmer();
	}

	protected String stem(String word) {
		return stemmer.stem(word);
	}

}
