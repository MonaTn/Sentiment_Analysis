package nlp.text_processor;

/**
 * Last modification 25 February 2013
 */

public class Word {
	private String word;
	private String tag;
	private int count;

	public Word() {
	}

	public Word(String word, String tag, int count) {
		this.word = word;
		this.tag = tag;
		this.count = count;
	}

	public Word(String word, String tag) {
		this.word = word;
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = getPOSName(tag);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public boolean isUnique() {
		return (getCount() == 1);
	}

	public boolean isStopWord() {
		return StopWords.isStopWord(word);
	}

	public int getNumberSyllables() {
		return new EnglishSyllableCounter().countSyllables(word);
	}

	public boolean equal(Word word2) {
		return (word2.getWord().equals(word) && word2.getTag().equals(tag));
	}

	private String getPOSName(String tag) {
		String tagType = (tag.matches("[\\$,\\.:#]") || tag.endsWith("RB-")
				|| tag.equals("\''") || tag.matches("\\``") || tag.equals("\"")) ? "PUNCT"
				: tag;
		return POSType.valueOf(tagType).getName();
	}

	@Override
	public String toString() {
		return word + ", "+ tag + ", "+ Integer.toString(count);
	}
}
