package lexicon;

/**
 * POS tagged Lexicon
 * Binary lexicon
 * 1 April 2013
 * Last modification 12 Jan 2014
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeneralInquirer_Ver01 extends Lexicon implements Serializable {

	private static final long serialVersionUID = -8727298228525036877L;
	private String lexiconFile = "Lexicons/GeneralInquirer";
	private List<String> tags;

	public GeneralInquirer_Ver01() {
		posedLexicon = true;
		buildLexicon(lexiconFile);
	}

	@Override
	protected void addWordsAndScoreToMap(String line) {
		String[] tokens = line.split(regEx);
		String word = buildToken(tokens);
		checkPOSTag(tokens);
		double initialScore = extractScore(tokens);
		if (tags.size() > 0) {
			for (String tag : tags) {
				String token = word + "#" + tag;
				double score = computeScore(token, initialScore);
				wordAndScore.put(token, score);
			}
		}
	}

	protected String buildToken(String[] tokens) {
		String[] word = tokens[0].split("#");
		return word[0].toLowerCase();
	}

	protected double extractScore(String[] tokens) {
		if (tokens[2].equals("Positiv") || tokens[178].equals("PosAff")) {
			return 1;
		} else if (tokens[3].equals("Negativ") || tokens[177].equals("NegAff")) {
			return -1;
		} else
			return 0;
	}

	private void checkPOSTag(String[] tokens) {
		tags = new ArrayList<String>();
		String tagString = "";
		if (tokens.length == 186 && tokens[185].length() > 2) {
			String[] properties = tokens[185].split("\\s");
			tagString = (properties[1].matches("\\d+\\%")) ? properties[2]
					: properties[1];
		}
		returnTags(tokens, tagString);
	}

	private void returnTags(String[] tokens, String tagString) {
		if (isAdjective(tokens, tagString)) {
			tags.add("a");
		}
		if (isVerb(tokens, tagString)) {
			tags.add("v");
		}
		if (isAdverb(tokens, tagString)) {
			tags.add("r");
		}
		if (isNoun(tokens, tagString)) {
			tags.add("n");
		}
	}

	private boolean isVerb(String[] tokens, String tagString) {
		return (tokens[116].equals("IAV") || tokens[117].equals("DAV")
				|| tokens[118].equals("SV") || tagString.contains("verb"));
	}

	private boolean isAdjective(String[] tokens, String tagString) {
		return (tokens[119].equals("IPadj") || tokens[120].equals("IndAdj")
				|| tagString.contains("adj") || tagString.contains("adjective"));
	}

	private boolean isAdverb(String[] tokens, String tagString) {
		return (tokens[184].contains("DEG") || tagString.contains("adv") || tagString
				.contains("adverb"));
	}

	private boolean isNoun(String[] tokens, String tagString) {
		return (tagString.contains("noun"));
	}

}
