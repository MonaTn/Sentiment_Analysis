package xExtraClass;

import TextProcess.AbstractTextPreprocessor;
import TextProcess.PorterStemmerTextPreprocessor;
import TextProcess.SnowBallStemmerTextPreprocessor;
import TextProcess.Stemmer;
import xExtraClass.AbstractNegationTextPreprocessor;
import xExtraClass.PorterTextPreprocessorByNegation;
import xExtraClass.SnowBallTextPreprocessorByNegation;

public class TextProcessorFactory {

	public static AbstractTextPreprocessor createRegularProcessor(
			Stemmer stemmerName) {
		AbstractTextPreprocessor processor = null;
		switch (stemmerName) {
		case PORTER:
			processor = new PorterStemmerTextPreprocessor();
		case SNOW_BALL:
			processor = new SnowBallStemmerTextPreprocessor();
		}
		return processor;
	}

	public static AbstractNegationTextPreprocessor createNegationProcessor(
			Stemmer stemmerName) {
		AbstractNegationTextPreprocessor processor = null;
		switch (stemmerName) {
		case PORTER:
			processor = new PorterTextPreprocessorByNegation();
		case SNOW_BALL:
			processor = new SnowBallTextPreprocessorByNegation();
		}
		return processor;
	}
}
