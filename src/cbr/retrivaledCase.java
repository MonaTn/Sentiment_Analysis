package cbr;

import java.util.Comparator;

public class retrivaledCase implements Comparable<retrivaledCase> {
	private CaseSolution caseSolution;
	private double measure;

	public retrivaledCase() {
	}

	public retrivaledCase(CaseSolution caseSolution, double measure) {
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

	@Override
	public String toString() {
		return "Solution [caseSolution=" + caseSolution.toString()
				+ ", measure=" + measure + "]";
	}

	@Override
	public int compareTo(retrivaledCase solution) {
		return Comparators.Distance.compare(this, solution);
	}

	public static class Comparators {

		public static Comparator<retrivaledCase> Distance = new Comparator<retrivaledCase>() {
			@Override
			public int compare(retrivaledCase a, retrivaledCase b) {
				return a.getMeasure() > b.getMeasure() ? 1
						: a.getMeasure() == b.getMeasure() ? 0 : -1;
			}
		};
	}
}
