package xTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Test.Solution;

import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.NRC;
import lexicon.SentiWordNet;
import lexicon.SubjectivityClues;

import shared.CaseSolution;

public class testOnCaseSolutionList {

	public static void main(String[] args) {
		exampleTwo();
	}

	private static void exampleTwo() {
		CaseSolution cs = new CaseSolution();
		List<Solution> ls = new ArrayList<Solution>();

		cs.add(new GeneralInquirer());
		cs.add(new NRC());
		Solution ss = new Solution(cs, 1.02);
		ls.add(ss);

		List<Lexicon> ll = new ArrayList<Lexicon>();
		ll.add(new SentiWordNet());
		ll.add(new MSOL());
		System.out.println(ll.size());

		cs = new CaseSolution();
		cs.setCaseSolution(ll);
		ss = new Solution(cs, 2.332);
		ls.add(ss);
		ll.clear();
		System.out.println(ll.size());

		ll.add(new SubjectivityClues());
		cs = new CaseSolution();
		cs.setCaseSolution(ll);
		ss = new Solution(cs, 0.5);
		ls.add(ss);
		ll.clear();

		ll.add(new GeneralInquirer());
		ll.add(new MSOL());
		cs = new CaseSolution();
		cs.setCaseSolution(ll);
		ss = new Solution(cs, 5.5);
		ls.add(ss);
		ll.clear();

		System.out.println("Unsorted List : " + Arrays.toString(ls.toArray()));
		Collections.sort(ls,Solution.Comparators.Measure);
		System.out.println("Sorted List : " + Arrays.toString(ls.toArray()));
	}

	public static void exampleOne() {
		CaseSolution cs = new CaseSolution();
		List<Sol> ls = new ArrayList<Sol>();
		cs.add(new GeneralInquirer());
		cs.add(new NRC());
		Sol ss = new Sol(cs, 1.02);
		ls.add(ss);

		List<Lexicon> ll = new ArrayList<Lexicon>();
		ll.add(new SentiWordNet());
		ll.add(new MSOL());
		cs = new CaseSolution();
		cs.setCaseSolution(ll);
		ss = new Sol(cs, 2.332);
		ls.add(ss);
		ll.clear();

		ll.add(new SubjectivityClues());
		cs = new CaseSolution();
		cs.setCaseSolution(ll);
		ss = new Sol(cs, 0.5);
		ls.add(ss);
		ll.clear();

		ll.add(new GeneralInquirer());
		ll.add(new MSOL());
		cs = new CaseSolution();
		cs.setCaseSolution(ll);
		ss = new Sol(cs, 5.5);
		ls.add(ss);
		ll.clear();

		System.out.println("Unsorted List : " + Arrays.toString(ls.toArray()));
		Collections.sort(ls, new AscendingComparator3());
		System.out.println("Sorted List : " + Arrays.toString(ls.toArray()));
	}

}

class Sol {
	CaseSolution caseSolution;
	double score;

	public Sol(CaseSolution caseSolution, double score) {
		this.caseSolution = caseSolution;
		this.score = score;
	}

	public CaseSolution getCaseSolution() {
		return caseSolution;
	}

	public void setCaseSolution(CaseSolution caseSolution) {
		this.caseSolution = caseSolution;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Solution [caseSolution=" + caseSolution.toString() + ", score="
				+ score + "]";
	}
}

class AscendingComparator3 implements Comparator<Sol> {
	public int compare(Sol a, Sol b) {
		return a.getScore() >= b.getScore() ? 1
				: a.getScore() == b.getScore() ? 0 : -1;
	}
}
