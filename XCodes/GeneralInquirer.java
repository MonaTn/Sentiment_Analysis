package lexicon;

/** 
 * POS tagged Lexicon
 * Binary lexicon
 * Last modification 21 March 2013
 */


import java.io.Serializable;

public class GeneralInquirer extends Lexicon implements Serializable {

	private static final long serialVersionUID = -8727298228525036877L;
	private String lexiconFile = "Lexicons/GeneralInquirer";

	
	public GeneralInquirer() {
		setRegEx("\t");
		buildLexicon(lexiconFile);
	}

	 boolean hasPolarity(String[] tokens) {
		return (isVerb(tokens) || isAdjective(tokens) || isAdverb(tokens));
	}

	private boolean isVerb(String[] tokens) {// must complete
		return (tokens[116].equals("IAV") || tokens[117].equals("DAV") || tokens[118]
				.equals("SV"));
	}

	private boolean isAdjective(String[] tokens) {// must complete
		return (tokens[119].equals("IPadj") || tokens[120].equals("IndAdj"));
	}

	private boolean isAdverb(String[] tokens) {// must complete
		return (tokens[184].equals("DEG"));
	}

	String extractWord(String[] tokens) {
		String[] word = tokens[0].split("#");
		return word[0];
	}

	String returnTag(String[] tokens) { 
		if (isAdjective(tokens)) {
			return "a";
		} else if (isVerb(tokens)) {
			return "v";
		} else if (isAdverb(tokens)) {
			return "r";
		} else
			return "";
	}

	int extractScore(String[] tokens) {
		if (tokens[2].equals("Positiv")) {
			return 1;
		} else if (tokens[3].equals("Negativ")) {
			return -1;
		} else
			return 0;
	}

}
