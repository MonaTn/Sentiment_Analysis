package lexicon;

/** 
 * POS tagged Lexicon
 * Binary lexicon
 * modification 1 April 2013
 * lasr modificatio 9 December 2013 : add tag anypos to lexicon
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubjectivityClues_Ver01 extends Lexicon implements Serializable {

	private static final long serialVersionUID = -8335803672374171957L;
	private final String lexiconName = "Lexicons/subjclueslen1-HLTEMNLP05.tff";

	public SubjectivityClues_Ver01() {
		regEx = "\\s+";
		posedLexicon = true;
		buildLexicon(lexiconName);
	}

	@Override
	protected void addWordsAndScoreToMap(String line) {
		String[] tokens = line.split(regEx);
		List<String> wordsTags = buildWordTag(tokens);
		if (wordsTags != null) {
			for (String wordTag : wordsTags) {
				double initialScore = extractScore(tokens);
				double score = computeScore(wordTag, initialScore);
				wordAndScore.put(wordTag, score);
			}
		}
	}

	private List<String> buildWordTag(String[] tokens) {
		List<String> WordsTags = new ArrayList<String>();
		String word = extractWord(tokens);
		String tag = extractTag(tokens);
		if (tag != null && tag != "avrn") {
			WordsTags.add((word + "#" + tag).toLowerCase());
		} else if (tag.equals("avrn")) {
			char[] tags = tag.toCharArray();
			for (char oneTag : tags) {
				WordsTags.add((word + "#" + oneTag).toLowerCase());
			}
		} else {
			return null;
		}
		return WordsTags;
	}

	protected String buildToken(String[] tokens) {
		return null ;
	}

	private String extractWord(String[] tokens) {
		String[] word = tokens[2].split("=");
		return word[1];
	}

	private String extractTag(String[] tokens) {
		String[] tag = tokens[3].split("=");
		if (isAdjective(tag[1])) {
			return "a";
		} else if (isVerb(tag[1])) {
			return "v";
		} else if (isAdverb(tag[1])) {
			return "r";
		} else if (isNoun(tag[1])) {
			return "n";
		} else if (isAnyPOSTag(tag[1])) {
			return "avrn";
		} else {
			return null;
		}
	}

	protected double extractScore(String[] tokens) {
		String[] polarity = tokens[5].split("=");
		if (polarity[1].equals("positive")) {
			return 1;
		} else if (polarity[1].equals("negative")) {
			return -1;
		} else {
			return 0;
		}
	}

	private boolean isVerb(String tag) {
		return (tag.equals("verb"));
	}

	private boolean isAdjective(String tag) {
		return (tag.equals("adj"));
	}

	private boolean isAdverb(String tag) {
		return (tag.equals("adverb"));
	}

	private boolean isNoun(String tag) {
		return (tag.equals("noun"));
	}

	private boolean isAnyPOSTag(String tag) {
		return (tag.equals("anypos"));
	}
}
