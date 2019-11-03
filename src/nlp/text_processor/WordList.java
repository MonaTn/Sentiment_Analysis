package nlp.text_processor;

/**
 * Last modification 25 February 2013
 */

import java.util.ArrayList;
import java.util.List;

public class WordList {
	private List<Word> wordList;

	public WordList() {
		wordList = new ArrayList<Word>();
	}

	public void add(Word word) {
		wordList.add(word);
	}

	public void add(WordList list) {
		for (Word w : list.getWords()) {
			wordList.add(w);
		}
	}

	public List<Word> getWords() {
		return wordList;
	}

	public void toPrintOut() {
		for (Word word : getWords()) {
			System.out.println("<< " + word.getWord() + " , " + word.getTag()
					+ " , " + word.getCount() + " >>");
		}
	}

	public boolean containsWord(Word word) {
		for (Word oneWord : wordList) {
			if (oneWord.equal(word)) {
				return true;
			}
		}
		return false;
}
	public void updateCount(Word word) {
		for (Word oneWord : wordList) {
			if (oneWord.equal(word)) {
				oneWord.setCount(word.getCount()+1);
			}
		}
	}
}
