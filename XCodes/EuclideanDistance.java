package testing;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import shared.CaseBase;
import shared.Document;


public class EuclideanDistance {
	
	private static double computeEuclideanDistanceBetweenDocumentAndACaseBase(Document document, CaseBase caseBase) {
		double[] documentFeatures = document.getCaseDescription().getFeatures();
		double[] caseBaseFeatures = caseBase.getCaseDescription().getFeatures();
		double sum = 0;
		for (int i = 0; i<documentFeatures.length; i++) {
			sum += Math.pow(documentFeatures[i]- caseBaseFeatures[i] , 2);
		}
		return Math.sqrt(sum);
	}

	public static void cumputeEclideanDistanceBetweenADocumentAndAllCaseBases (List<CaseBase> caseBaseList , Document document) {
		for (CaseBase caseBase : caseBaseList) {
			double euclideanDistance = computeEuclideanDistanceBetweenDocumentAndACaseBase(document, caseBase);
			caseBase.setEuclideanDistance(euclideanDistance);
		}
		Collections.sort(caseBaseList, new Comparator <CaseBase>() {
				@Override
				public int compare (CaseBase a , CaseBase b) {
					return (a.getEucliDeandistance() > b.getEucliDeandistance()) ? 1 : -1;
				}
		});
	}
}
