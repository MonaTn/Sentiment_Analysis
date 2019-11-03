package test;

import java.io.IOException;
import java.util.List;

import lexicon.Lexicon;
import nlp.text_processor.Document;
import utilities.DirectoryUtil;
import utilities.Serialization;
import cbr.CaseBase;
import cbr.CaseNormalizer;
import classifier.CBRClassifier;

public class Tester {

	private static String rootPath = "../../DataSet";
	private static String subPath = "/Apparel/neg_apl/";
	private static String pathOfCaseBase = "../../Results/2014/Train/Jan/Regular-2014.01.23/";
	private static String serializedCaeBaseFile = "All-BUT-Appareal(20140123).ser";
	List<Lexicon> lexicons;

	public static void main(String[] args) throws Throwable {
		Tester tester = new Tester();
		CaseBase caseBase = tester.extractCaseBase();
		List<Document> allDocuments = tester.getUnseenDocuments();
		tester.test(caseBase, allDocuments);
	}

	public void test(CaseBase caseBase, List<Document> allDocuments)
			throws Throwable {
		CBRClassifier classifier = new CBRClassifier();
		classifier.classify(caseBase, allDocuments);
		System.out.println("DON!");
	}

	private CaseBase extractCaseBase() throws IOException {
		String absoluteSerializedFile = pathOfCaseBase + serializedCaeBaseFile;
		CaseBase caseBase = (CaseBase) Serialization
				.deSerialize(absoluteSerializedFile);
		double[] featuresMinValues = caseBase.getMinValues();
		double[] featuresMaxValues = caseBase.getMaxValues();
		return CaseNormalizer.caseBaseNormalize(caseBase, featuresMinValues,
				featuresMaxValues);
	}

	private List<Document> getUnseenDocuments() throws Exception {
		String path = rootPath + subPath;
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
				false);
		return testDocumentList;
	}

}
