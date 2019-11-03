package testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Case;
import shared.CaseAuthoring;
import shared.CaseBase;
import shared.CaseSolution;
import shared.DirectoryUtil;
import shared.Document;
import shared.Serialization;
import shared.TextPreprocessor;
import shared.LexiconBasedClassifier;

public class Evaluation {

	public String serializedCaeBaseFile;
	public String baseTestSetPath;
	public String subTestSetPath;
	public String resultPath;
	public String resulFileName ;
	public int k ;
	private double[] minValues;
	private double[] maxValues;
	private final int lowerRange = 0;
	private final int upperRange = 10;

	public Evaluation() throws Exception, IOException {
		CaseBase caseBase = PrepareCaseBase();
		extractMinMaxValues (caseBase);
		System.out.println(Arrays.toString(minValues));
		System.out.println(Arrays.toString(maxValues));
		CaseBase normalizedCaseBase = normalization(caseBase);
		List<Document> testDocumentsList = PrepareTestDocuments();
		TextPreprocessor processor = new TextPreprocessor();
		CaseAuthoring caseConstructor = new CaseAuthoring();
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		RetrivalCases retriever = new RetrivalCases();

		List<PredictedDocument> predictedDocuments = new ArrayList<PredictedDocument>();

		for (Document testDocument : testDocumentsList) {
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
				resulFileName);
	}

	private void extractMinMaxValues(CaseBase caseBase) {//
		minValues = caseBase.extractMinValues();
		maxValues = caseBase.extractMaxValues();
	}

	private CaseBase normalization(CaseBase caseBase) {//
		CaseBase normalizedCaseBase = new CaseBase();
		Normalize normalizer = new Normalize();
		for (Case oneCase : caseBase.getCases()) {
			Case normalizedCase = normalizer.normalized(oneCase, minValues, maxValues, lowerRange, upperRange);
			normalizedCaseBase.add(normalizedCase);
		}
		return normalizedCaseBase;
	}

	private CaseBase PrepareCaseBase() throws IOException {//
		String fileName = resultPath+ serializedCaeBaseFile; 
		return (CaseBase) Serialization.deSerialize(fileName);
	}

	private List<Document> PrepareTestDocuments() throws Exception {//
		String path = baseTestSetPath + subTestSetPath;
		List<Document> testDocumentsList = DirectoryUtil.getListOfDocument(path,
				true);
		return testDocumentsList;
	}

	public void setSerializedCaeBaseFile(String serializedCaeBaseFile) {
		this.serializedCaeBaseFile = serializedCaeBaseFile;
	}

	public void setBaseTestSetPath(String baseTestSetPath) {
		this.baseTestSetPath = baseTestSetPath;
	}

	public void setSubTestSetPath(String subTestSetPath) {
		this.subTestSetPath = subTestSetPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public void setResultOfTestFiles(String resultOfTestFiles) {
		this.resulFileName = resultOfTestFiles;
	}

	public void setK(int k) {
		this.k = k;
	}

}
