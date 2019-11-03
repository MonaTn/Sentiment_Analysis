package cbr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import classifier.LexiconBasedClassifier;
import lexicon.Lexicon;
import nlp.TextProcessor;
import nlp.text_processor.Document;
import utilities.DirectoryUtil;
import utilities.Serialization;
import utilities.Utility;

public class CaseListBuilder {

	private String trainingSetRootPath;
	private String caseBasePath;
	private LexiconBasedClassifier classifier;

	public CaseListBuilder() {
	}

	public void setClassifier(LexiconBasedClassifier classifier) {
		this.classifier = classifier;
	}

	public void setPaths(String caseBasePath, String trainingSetRootPath) {
		this.caseBasePath = caseBasePath;
		this.trainingSetRootPath = trainingSetRootPath;
	}

	public List<Case> build(String domain, List<Lexicon> lexiconsList,
 CaseAuthoring caseAuthore,
			TextProcessor processor)
			throws IOException, Throwable {
		long startTime = System.currentTimeMillis();
		List<Case> caseBase = getCasesList(domain, lexiconsList,
 caseAuthore, processor);
		long endTime = System.currentTimeMillis();
		System.out.println("\n Case-list size for " + domain + " = " + caseBase.size());
		System.out.printf("\n >>>>>>> The time of runnig is: %f",
				runningTimeInSeconds(startTime, endTime));
		return caseBase;
	}

	private List<Case> getCasesList(String domain, List<Lexicon> lexicons,
 CaseAuthoring caseAuthore,
			TextProcessor processor)
			throws Throwable {
		List<Document> trainingDocumentsList = createDocumentList(domain);
		List<Case> cases = new ArrayList<Case>();
		for (Document document : trainingDocumentsList) {
			Case oneCase = caseAuthore.buildCase(document, lexicons,
					processor, classifier);
			if (oneCase != null) {
				cases.add(oneCase);
			}
		}
		return cases;
	}

	private List<Document> createDocumentList(String domain) throws Throwable {
		List<Document> trainingDocumentsList = new ArrayList<Document>();
		trainingDocumentsList.addAll(getNegativeDocuments(domain));
		trainingDocumentsList.addAll(getPositiveDocuments(domain));
		return trainingDocumentsList;
	}

	private List<Document> getNegativeDocuments(String domain) throws Throwable {
		String path = trainingSetRootPath + domain + "/neg/";
		return DirectoryUtil.getListOfDocument(path, false);
	}

	private List<Document> getPositiveDocuments(String domain) throws Throwable {
		String path = trainingSetRootPath + domain + "/pos/";
		return DirectoryUtil.getListOfDocument(path, true);
	}

	public void save(CaseBase caseBase, String csvCaseBaseFileName,
			String serializedCaeBaseFileName) throws IOException {

		Utility.writeCaseBaseToFile(caseBasePath, csvCaseBaseFileName, caseBase);

		String serializedAbsoluteFileName = caseBasePath
				+ serializedCaeBaseFileName;
		Serialization.serialize(caseBase, serializedAbsoluteFileName);
	}

	private float runningTimeInSeconds(long startTime, long endTime) {
		long diffTime = endTime - startTime;
		if (diffTime < 0)
			return 0.0f;
		return diffTime / 1000f;
	}

}
