package cbr;

/**
 * Last modification 25 February 2013
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lexicon.Lexicon;

public class CaseSolution implements Serializable {
	private static final long serialVersionUID = -9152910410292712912L;

	private List<Lexicon> caseSolution;

	public CaseSolution() {
		caseSolution = new ArrayList<Lexicon>();
	}

	public List<Lexicon> getLexicons() {
		return caseSolution;
	}

	public void setCaseSolution(List<Lexicon> lexicons) {
		this.caseSolution.addAll(lexicons);
	}

	public void add(Lexicon lexicon) {
		caseSolution.add(lexicon);
	}

	public void add(CaseSolution caseSolution) {
		for (Lexicon lexicon : caseSolution.getLexicons()) {
			this.caseSolution.add(lexicon);
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(caseSolution.toArray());
	}
}
