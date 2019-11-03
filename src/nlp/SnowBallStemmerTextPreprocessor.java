package nlp;

import nlp.stemmer.EnglishStemmer;
import nlp.stemmer.SnowBallStemmer;

public class SnowBallStemmerTextPreprocessor extends AbstractTextPreprocessor {

	private final SnowBallStemmer stemmer;

	public SnowBallStemmerTextPreprocessor() {
		stemmer = new EnglishStemmer();
	}

	protected String stem(String word) {
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}

}
