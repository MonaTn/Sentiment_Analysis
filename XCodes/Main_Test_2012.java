package testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.CaseAuthoring;
import shared.CaseBase;
import shared.CaseSolution;
import shared.DirectoryUtil;
import shared.Document;
import shared.LexiconBasedClassifier;
import shared.Serialization;
import shared.TextPreprocessor;

public class Main_Test_2012 {

	private static String serializedCaeBaseFile = "CaseBase_All-Apparel.ser"; // ?
	private static String baseTestSetPath = "../../TrainingSet";
	private static String subTestSetPath = "/Apparel/pos_Apl/";
	private static String resultPath = "../../Results/2013/feb/25.02.013/";
	private static String resultOfTestFiles = "PredictedTestDocument_Apl_pos_2502013.csv";
	private static int k = 3;

	public static void main(String[] args) throws Exception {
		String path = baseTestSetPath + subTestSetPath;
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
				true);
		CaseBase caseBase = (CaseBase) Serialization.deSerialize(resultPath
				+ serializedCaeBaseFile);
		Normalize normalizer = new Normalize();

		List<double[]> minMaxArrays = normalizer.exteractMinMaxValues(caseBase);
		double[] minValues = Arrays.copyOf(minMaxArrays.get(0),
				minMaxArrays.get(0).length);
		double[] maxValues = Arrays.copyOf(minMaxArrays.get(1),
				minMaxArrays.get(1).length);
		System.out.println(Arrays.toString(minValues));
		System.out.println(Arrays.toString(maxValues));
		double[] test = caseBase.extractMinValues();
		System.out.println(Arrays.toString(test));
		double[] test2 = caseBase.extractMaxValues();
		System.out.println(Arrays.toString(test2));

		CaseBase normalizedCaseBase = normalizer.normalizeCaseBase(caseBase,
				minValues, maxValues);
		TextPreprocessor processor = new TextPreprocessor();
		CaseAuthoring caseConstructor = new CaseAuthoring();
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		RetrivalCases retriever = new RetrivalCases();

		List<PredictedDocument> predictedDocuments = new ArrayList<PredictedDocument>();

		for (Document testDocument : testDocumentList) {
			processor.extractWordsAndSentences(testDocument);
			caseConstructor.computeCaseDescription(testDocument);
			testDocument.setCaseDescription(normalizer.minMaxNormalization(
					testDocument.getCaseDescription().getFeatures(), minValues,
					maxValues));
			CaseBase retrievedCaseBase = retriever.retrievedKNearestCases(
					testDocument, normalizedCaseBase, k);
			CaseSolution caseSolution = TestCaseAuthoring
					.buildDocumentCaseSolution(retrievedCaseBase);
			double totalScore = classifier.calculateDocumentTotalScoreFromLexicons(
					testDocument, caseSolution);
			boolean polarity = classifier.predictPolarity(totalScore);
			testDocument.setPolarity(polarity);
			predictedDocuments.add(new PredictedDocument(testDocument,
					totalScore, caseSolution));
		}
		System.out.println(predictedDocuments.size());
		PredictedDocument.writeToFile(predictedDocuments, resultPath,
				resultOfTestFiles);
	}

}
