package testing;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import lexicon.Lexicon;

import shared.CaseBase;
import shared.DirectoryUtil;
import shared.Document;
import shared.DocumentUtiles;
import shared.Serialization;


import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class TestPhase {
	
	private static MaxentTagger taggerModel;
	private static final String caseBaseSerializedFile = "";
	private static final String minmaxFile = "";
	private static final String testSetPath = "C:/Master_Project/Programming/Java_Project/TestSet";
	private final static String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	private static final int k = 3;
	private static List<CaseBase> caseBaseList;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		taggerModel = new MaxentTagger(englishModel);
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(testSetPath, false);
		
		List<double[]> minmaxValues = (List<double[]>) Serialization.deSerialize(minmaxFile);
		double[] minValues = Arrays.copyOf(minmaxValues.get(0), minmaxValues.get(0).length);
		double[] maxValues = Arrays.copyOf(minmaxValues.get(1), minmaxValues.get(1).length);
		
		caseBaseList = (List<CaseBase>) Serialization.deSerialize(caseBaseSerializedFile);
		for (Document document : testDocumentList) {
			DocumentUtiles.extractTokensAndSentences(document, taggerModel);
			DocumentUtiles.computeCaseDescription(document);
			document.setCaseDescription(Normalize.minMaxNormalization(document.getCaseDescription(), minValues, maxValues));
			List<CaseBase> retrievedCaseBaseList = retrieved_k_nearestCaseBase(document, caseBaseList,k);
			//TODO buildcaseSolution();
			//FIXME predictPolarity(document);
		}
	}
	private static List<CaseBase> retrieved_k_nearestCaseBase (Document document, List<CaseBase> caseBaseList , int k ) {
		List<CaseBase> retrievedCaseBaseList = new ArrayList<CaseBase>();
		EuclideanDistance.cumputeEclideanDistanceBetweenADocumentAndAllCaseBases(caseBaseList, document);
		for (int i = 0 ; i<k ; i++) {
			retrievedCaseBaseList.add(caseBaseList.get(i));
		}
		return retrievedCaseBaseList;
	}
	
	
}
