package Test;

import java.util.Comparator;

import shared.CaseSolution;

class Solution implements Comparable<Solution> {
	private CaseSolution caseSolution;
	private double measure;

	public Solution() {
	}

	public Solution(CaseSolution caseSolution, double measure) {
		this.caseSolution = caseSolution;
		this.measure = measure;
	}

	public CaseSolution getCaseSolution() {
		return caseSolution;
	}

	public void setCaseSolution(CaseSolution caseSolution) {
		this.caseSolution = caseSolution;
	}

	public double getMeasure() {
		return measure;
	}

	public void setMeasure(double measure) {
		this.measure = measure;
	}

	@Override
	public String toString() {
		return "Solution [caseSolution=" + caseSolution.toString()
				+ ", measure=" + measure + "]";
	}

	@Override
	public int compareTo(Solution solution) {
		return Comparators.Distance.compare(this, solution);
	}

	public static class Comparators {

		public static Comparator<Solution> Distance = new Comparator<Solution>() {
			@Override
			public int compare(Solution a, Solution b) {
				return a.getMeasure() > b.getMeasure() ? 1
						: a.getMeasure() == b.getMeasure() ? 0 : -1;
			}
		};
	}
}
