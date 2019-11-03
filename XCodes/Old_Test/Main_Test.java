package Old_Test;

import shared.*;
import xTestUtil.TextPreprocessor_SnowBall;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Main_Test {

	private static String rootPath = "../../TestSet";
	private static String subPath = "/Small_Test/Test/";
	private static String resultPath = "../../Results/2013/March/Small/";
	private static String resultFileName = "TestDocument2.csv";
	private static String serializedCaeBaseFile = "CaseBase_test_neg.ser"; // ?

	private static TextPreprocessor_SnowBall processor = new TextPreprocessor_SnowBall();
	private static double[] minValues;
	private static double[] maxValues;
	private static int k = 3;

	public static void main(String[] args) throws Exception {
		List<Vector<Object>> resultsOfTestingPhase = new ArrayList<Vector<Object>>();
		CaseBase normalizedCaseBase = caseBasePreparation();
		List<Document> testDocumentsList = prepareDocumentList();
		Testing tester = new Testing();
		for (Document document : testDocumentsList) {
			documentPreparation(document);
			Vector<Object> result = tester.classify(document,
					normalizedCaseBase, k);
			resultsOfTestingPhase.add(result);
		}
		writeToFile(resultsOfTestingPhase);
	}

	private static CaseBase caseBasePreparation() throws IOException {
		CaseBase caseBase = extractCaseBase();
		return Normalizer.normalizeCaseBase(caseBase, minValues, maxValues);
	}

	private static CaseBase extractCaseBase() throws IOException {
		String serializedFile = resultPath + serializedCaeBaseFile;
		CaseBase caseBase = (CaseBase) Serialization
				.deSerialize(serializedFile);
		minValues = caseBase.getMinValues();
		maxValues = caseBase.getMaxValues();
		return caseBase;
	}

	private static List<Document> prepareDocumentList() throws Exception {
		String path = rootPath + subPath;
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
				false);
		return testDocumentList;
	}

	private static void documentPreparation(Document document)
			throws IOException {
		WordList wordList =processor.extractWordList(document);
		document.setWordsList(wordList);
		CaseDescription caseDescription = new CaseDescription(document);
		CaseDescription normalizedCaseDescription = Normalizer
				.normalizeCaseDescription(caseDescription, minValues, maxValues);
		document.setCaseDescription(normalizedCaseDescription);
	}

	private static void writeToFile(List<Vector<Object>> vectors)
			throws IOException {
		File resultFile = FileUtility.createFile(resultPath, resultFileName);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				resultFile));
		for (Vector<Object> vector : vectors) {
			FileUtility.writeALineInFile(vector.toString(), bufferedWriter);
		}
		bufferedWriter.close();
	}
}
