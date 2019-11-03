package xExtraClass;

import shared.PorterStemmer;

public class PorterTextPreprocessorByNegation extends
		AbstractNegationTextPreprocessor {

	private final PorterStemmer stemmer;

	public PorterTextPreprocessorByNegation() {
		stemmer = new PorterStemmer();
	}

	@Override
	protected String stem(String word) {
		return stemmer.stem(word);
	}

}
