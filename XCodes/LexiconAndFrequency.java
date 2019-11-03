package xTestUtil;

import lexicon.Lexicon;

public class LexiconAndFrequency {

	private Class<? extends Lexicon> lexicon;
	public Class<? extends Lexicon> getLexicon() {
		return lexicon;
	}

	private int frequency;

	public LexiconAndFrequency() {
	};

	public LexiconAndFrequency(Class<? extends Lexicon> lexicon, int frequency) {
		this.lexicon = lexicon;
		this.frequency = frequency;
	}

	public void setLexicon(Class<? extends Lexicon> lexicon) {
		 this.lexicon = lexicon;
	 }
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	
}
