package lexicon;

import java.util.Comparator;

public class LexiconFrequency implements Comparable<LexiconFrequency> {
	private Lexicon lexicon;
	private int count;

	public LexiconFrequency(Lexicon lexicon2, int count) {
		this.lexicon = lexicon2;
		this.count = count;
	}

	public Lexicon getLexicon() {
		return lexicon;
	}

	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void increaseCount() {
		this.count++;
	}

	public boolean equals(Lexicon lexicon) {
		return this.lexicon.getClass().getName()
				.equals(lexicon.getClass().getName());
	}

	@Override
	public String toString() {
		return "LexiconCount [lexicon=" + lexicon + ", count=" + count + "]";
	}

	@Override
	public int compareTo(LexiconFrequency a) {
		return Comparators.Count.compare(this, a);
	}

	public static class Comparators {

		public static Comparator<LexiconFrequency> Count = new Comparator<LexiconFrequency>() {

			@Override
			public int compare(LexiconFrequency a, LexiconFrequency b) {
				return a.getCount() > b.getCount() ? -1 : a.getCount() == b
						.getCount() ? 0 : 1;
			}

		};
	}

}
