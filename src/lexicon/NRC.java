package lexicon;

/**
 * Non POS tagged lexicon
 * Binary lexicon
 * Last modification 21 March 2013
 */

import java.io.Serializable;

public class NRC extends AbstractLexicon implements Lexicon, Serializable {

	private static final long serialVersionUID = 6808724589886806796L;
	private String lexiconFile = "Lexicons/NRC-emotion-lexicon-wordlevel-v0.92.txt";

	public NRC() {
		posedLexicon = false;
		buildLexicon(lexiconFile);
	}

	protected String buildToken(String[] tokens) {
		return tokens[0].toLowerCase();
	}

	protected double extractScore(String[] tokens) {
		if (isPositive(tokens)) {
			return 1;
		} else if (isNegative(tokens)) {
			return -1;
		} else {
			return 0;
		}
	}

	private boolean isPositive(String[] tokens) {
		return (tokens[1].equals("positive") && tokens[2].equals("1"));
	}

	private boolean isNegative(String[] tokens) {
		return (tokens[1].equals("negative") && tokens[2].equals("1"));
	}

}
