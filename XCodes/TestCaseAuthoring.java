package testing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lexicon.Lexicon;

import shared.Case;
import shared.CaseBase;
import shared.CaseSolution;

public class TestCaseAuthoring {

	public static CaseSolution buildDocumentCaseSolution(CaseBase retrievedCaseBase) {
		Map<Lexicon, Integer> lexiconsMap = new HashMap<Lexicon, Integer>();
		for (Case retrievedCases : retrievedCaseBase.getCases()) {
			for (Lexicon caseSolutionLexicon : retrievedCases.getCaseSolution()
					.getLexicons()) {
				int count = (lexiconsMap.containsKey(caseSolutionLexicon)) ? lexiconsMap
						.get(caseSolutionLexicon) + 1 : 1;
				lexiconsMap.put(caseSolutionLexicon, count);
			}
		}
		return mostFrequentLexicons(lexiconsMap);
	}

	public static CaseSolution mostFrequentLexicons(Map<Lexicon, Integer> lexiconsMap) {
		DscendingComparator valuComparator = new DscendingComparator(lexiconsMap);
		TreeMap<Lexicon, Integer> sortedMap = new TreeMap<Lexicon, Integer>(
				valuComparator);
		sortedMap.putAll(lexiconsMap);
		CaseSolution caseSolution = new CaseSolution();
		int maxFrequency = sortedMap.firstEntry().getValue();
		for (Map.Entry<Lexicon, Integer> entry : sortedMap.entrySet()) {
			if (entry.getValue() == maxFrequency) {
				caseSolution.add(entry.getKey());
			}
		}
		return caseSolution;
	}
}

class DscendingComparator implements Comparator<Lexicon> {

	Map<Lexicon, Integer> map;

	public DscendingComparator(Map<Lexicon, Integer> map) {
		this.map = map;
	}

	public int compare(Lexicon a, Lexicon b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}
}
