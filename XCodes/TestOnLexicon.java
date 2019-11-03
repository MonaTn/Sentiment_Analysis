package xExtraClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.NRC;
import lexicon.SentiWordNet;
import lexicon.SubjectivityClues;

public class TestOnLexicon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
	}

	public static void buildLexiconList() {
		List<Lexicon> lexiconsList = new ArrayList<Lexicon>();
		lexiconsList.add(new SentiWordNet());
		lexiconsList.add(new MSOL());
		lexiconsList.add(new GeneralInquirer());
		lexiconsList.add(new SubjectivityClues());
		lexiconsList.add(new NRC());
		Lexicon l = new SubjectivityClues();
		for (Lexicon ll : lexiconsList) {
			if (ll.getClass().getName().equals(l.getClass().getName())) {
				System.out.println(lexiconsList.indexOf(ll));
			}
		}

	}

	public static void array() {
		int[] a = new int[5];
		Arrays.fill(a, 0);
		System.out.println(a[0]++);
		System.out.println(++a[1]);

	}

	public static void test() {
		List<LexiconCount> llc = new ArrayList<LexiconCount>();
		LexiconCount lc = new LexiconCount(new MSOL(), 2);
		llc.add(lc);
		lc = new LexiconCount(new SentiWordNet(), 1);
		llc.add(lc);
		lc = new LexiconCount(new GeneralInquirer(), 4);
		llc.add(lc);
		lc = new LexiconCount(new NRC(), 3);
		llc.add(lc);
		System.out.println(Arrays.toString(llc.toArray()));
		Collections.sort(llc, LexiconCount.Comparators.Count);
		System.out.println(Arrays.toString(llc.toArray()));
		lc = new LexiconCount(new GeneralInquirer(), 1);
		if (llc.contains(lc.getClass().getName())) {
			System.out.println("YES");
		}else {
			System.out.println("Nooo");

		}
		for (LexiconCount l : llc) {
			if (l.getLexicon().getClass().getName().equals(lc.getLexicon().getClass().getName())) {
				l.increaseCount();
			}
		}
		System.out.println(Arrays.toString(llc.toArray()));
	}

}

class LexiconCount implements Comparable<LexiconCount> {
	Lexicon lexicon;
	int count;

	public LexiconCount(Lexicon lexicon, int count) {
		this.lexicon = lexicon;
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

	@Override
	public String toString() {
		return "LexiconCount [lexicon=" + lexicon + ", count=" + count + "]";
	}

	@Override
	public int compareTo(LexiconCount a) {
		return Comparators.Count.compare(this, a);
	}

	public static class Comparators {

		public static Comparator<LexiconCount> Count = new Comparator<LexiconCount>() {

			@Override
			public int compare(LexiconCount a, LexiconCount b) {
				return a.getCount() > b.getCount() ? -1 : a.getCount() == b
						.getCount() ? 0 : 1;
			}

		};
	}
}