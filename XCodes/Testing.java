package testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shared.Case;
import shared.CaseBase;
import shared.CaseSolution;
import shared.DirectoryUtil;
import shared.Document;
import shared.Serialization;
import shared.LexiconBasedClassifier;

public class Testing {
	
	private  String serializedCaeBaseFile;
	private  String baseTestSetPath ;
	private  String subTestSetPath ;
	private  String resultPath ;
	private  String resultFile ;
	
	
	private LexiconBasedClassifier classifier;
	
	public void test() throws Exception {
		List<PredictedDocument> predictedDocumentsList = new ArrayList<PredictedDocument>();
		List<Document> testDocumentList = prepareDocumentList();
		for(Document document : testDocumentList) {
			PredictedDocument predictedDocument = predictDocumentPolarity(document, classifier);
			predictedDocumentsList.add(predictedDocument);
		}
		// return predicted documents 
	}
	
	private PredictedDocument predictDocumentPolarity(Document document, LexiconBasedClassifier classifier) {
		CaseSolution caseSolution = extractLexicons();
		double score = classifier.calculateDocumentTotalScoreFromLexicons(document, caseSolution);
		boolean polarity = classifier.predictPolarity(score);
		document.setPolarity(polarity);
		return new PredictedDocument(document, score, caseSolution);
	}
	
	private CaseSolution extractLexicons() {
		return null;
	}

	private  List<Document> prepareDocumentList() throws Exception {
		String path = baseTestSetPath + subTestSetPath;
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
				true);
		return testDocumentList;
	}
	private void normalizedCaseBase() throws IOException {
		CaseBase caseBase = prepareCaseBase();
		CaseBase normalizedCaseBase = normalizedCaseBase(caseBase);
	}
	
	private CaseBase prepareCaseBase() throws IOException {
		String serializedFilename = resultPath + serializedCaeBaseFile;
		CaseBase caseBase = (CaseBase) Serialization
				.deSerialize(serializedFilename);
//		minValues = caseBase.extractMinValues();
//		maxValues = caseBase.extractMaxValues();
		return caseBase;
	}

	private CaseBase normalizedCaseBase(CaseBase caseBase) {
		CaseBase normalizedCaseBase = new CaseBase();
		for (Case oneCase : caseBase.getCases()) {
			Case normalizedCase = normalizedCase(oneCase);
			normalizedCaseBase.add(normalizedCase);
		}
		return normalizedCaseBase;
	}
	
	private Case normalizedCase(Case oneCase) {
		// TODO Auto-generated method stub
		return null;
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
		this.resultFile = resultOfTestFiles;
	}
	

}
