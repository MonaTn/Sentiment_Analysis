package lexicon;

/**
 * Non POS tagged lexicon
 * Binary lexicon
 * Last modification 25 February 2013
 */

import java.io.Serializable;

public class MSOL extends AbstractLexicon implements Lexicon, Serializable {

	private static final long serialVersionUID = 5870622848074123802L;
	private String lexiconFile = "Lexicons/MOSL.txt";

	public MSOL() {
		regEx = "\\s+";
		posedLexicon = false;
		buildLexicon(lexiconFile);
	}

	/** tokens[0] = word **/
	protected String buildToken(String[] tokens) {
		String word = tokens[0].replaceAll("[\\_\\,]", " ");
		return word.toLowerCase();
	}

	/** tokens[1] = polarity **/
	protected double extractScore(String[] tokens) {
		return (tokens[1].equals("positive")) ? 1 : -1;
	}

}