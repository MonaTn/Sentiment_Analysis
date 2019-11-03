package TextProcess;


public class TextProcessorFactory {

	public static AbstractTextPreprocessor create(Stemmer stemmerName) {
		AbstractTextPreprocessor processor = null;
		switch (stemmerName) {
		case PORTER:
			processor = new PorterStemmerTextPreprocessor();
		case SNOW_BALL:
			processor = new SnowBallStemmerTextPreprocessor();
		}
		return processor;
	}

}
