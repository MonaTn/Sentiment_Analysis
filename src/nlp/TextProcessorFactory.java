package nlp;

public class TextProcessorFactory {
	private String stemmer;

	public void setStemmer(String stemmer) {
		this.stemmer = stemmer;
	}

	public AbstractTextPreprocessor create() {
		switch (stemmer) {
		case "Porter_Stemmer":
			return new PorterStemmerTextPreprocessor();
		case "SnowBall_Stemmer":
			return new SnowBallStemmerTextPreprocessor();
		default:
			return null;
		}
	}

}
