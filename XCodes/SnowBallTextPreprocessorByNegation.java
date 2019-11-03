package xExtraClass;

import shared.EnglishStemmer;
import shared.SnowBallStemmer;

public class SnowBallTextPreprocessorByNegation extends
		AbstractNegationTextPreprocessor {

	private final SnowBallStemmer stemmer;

	public SnowBallTextPreprocessorByNegation() {
		stemmer = new EnglishStemmer();
	}

	@Override
	protected String stem(String word) {
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}

}
