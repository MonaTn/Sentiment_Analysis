package testing;

import java.util.HashMap;
import java.util.Map;

import shared.Case;
import shared.CaseBase;
import shared.Document;

public class RetrivalCases {

	public CaseBase retrievedKNearestCases(Document document,
			CaseBase caseBase, int k) {
		SimilarityMeasure measure = new SimilarityMeasure();
		Map<Case, Double> casesAlongDistanceValues = new HashMap<Case, Double>();
		for (Case oneCase : caseBase.getCases()) {
			double distanceValue = measure.euclideanDistance(document, oneCase);
			casesAlongDistanceValues.put(oneCase, distanceValue);
		}
		return findKNearestCasesToDocument(k, measure,
				casesAlongDistanceValues);
	}

	private CaseBase findKNearestCasesToDocument(int k,
			SimilarityMeasure distanceFinder,
			Map<Case, Double> caseBaseWithEuclideanDistanceMap) {
		CaseBase retrievedCaseBase = new CaseBase();
		Map<Case, Double> sortedMap = distanceFinder
				.sortAscending(caseBaseWithEuclideanDistanceMap);
		for (Case oneCase : sortedMap.keySet()) {
			if (k > 0) {
				retrievedCaseBase.add(oneCase);
				k--;
			}
		}
		for (Map.Entry<Case, Double> entry : sortedMap.entrySet()) {
			System.out.println(entry.toString());
		}
		
		for (Case oneCase : retrievedCaseBase.getCases()) {
			System.out.println(oneCase.toString());
		}
		return retrievedCaseBase;
	}

}
