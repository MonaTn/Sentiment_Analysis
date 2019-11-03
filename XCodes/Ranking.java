package testing;

import java.util.ArrayList;
import java.util.List;

import shared.CaseBase;

import lexicon.Lexicon;

import xTestUtil.LexiconAndFrequency;


public class Ranking {
	
	public static List<LexiconAndFrequency> ranking(
			List<CaseBase> retrievedCaseBase) {
		List<LexiconAndFrequency> lexiconAndFrequency = new ArrayList<LexiconAndFrequency>();
		for (CaseBase cb : retrievedCaseBase) {
			for (Class<? extends Lexicon> lexicon : cb.getCaseSolution()) {
				buildListOfRetrievedLexicon(lexicon, lexiconAndFrequency);
			}

		}
		return lexiconAndFrequency;
	}

	private static void buildListOfRetrievedLexicon(
			Class<? extends Lexicon> lexicon,
			List<LexiconAndFrequency> lexiconAndFrequency) {
		for (LexiconAndFrequency lf : lexiconAndFrequency) {
			if (lf.getLexicon().equals(lexicon)) {
				lf.setFrequency(lf.getFrequency() + 1);
				return;
			}
		}
		lexiconAndFrequency.add(new LexiconAndFrequency(lexicon, 1));
	}

}
