package training;

/**
 * Last modification 25 February 2013
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.NRC;
import lexicon.SentiWordNet;
import lexicon.SubjectivityClues;

import shared.CaseBase;
import shared.DirectoryUtil;
import shared.Document;
import shared.Serialization;

public class CaseBaseBuilder {

	public String resultPath;
	public String baseTrainingSetPath;
	public String serializedCaeBaseFileName;
	public String csvCaseBaseFileName;
	private CaseBase caseBase = new CaseBase();
	private ArrayList<Lexicon> lexiconsList = new ArrayList<Lexicon>();

	public void train() throws IOException, Exception {
		long startTime = System.currentTimeMillis();
		buildLexiconList();
		buildCaseBase();
		storeCaseBaseInFile();
		long endTime = System.currentTimeMillis();
		System.out.printf("\n >>>The time of runnig is: %f",
				runningTimeInSeconds(startTime, endTime));
	}

	private void buildLexiconList() {
		lexiconsList.add(new SentiWordNet());
		lexiconsList.add(new MSOL());
		lexiconsList.add(new GeneralInquirer());
		lexiconsList.add(new SubjectivityClues());
		lexiconsList.add(new NRC());
	}

	private void buildCaseBase() throws IOException, Exception {
		buildCasesOnAppareal();
		buildCasesOnBook();
		buildCasesOnElectronics();
		buildCasesOnHotel();
		buildCasesOnMovie();
		buildCasesOnMusic();
		// caseBase.add(makeCaseBase(subTrainingSetPath, true).getCases());
		System.out.println("\n Case Base size = " + caseBase.getCases().size());
	}

	private void buildCasesOnMusic() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Music/neg_Mc/", false).getCases());
		caseBase.add(makeCaseBase("/Music/pos_Mc/", true).getCases());
	}

	private void buildCasesOnMovie() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Movie/neg_Mov/", false).getCases());
		caseBase.add(makeCaseBase("/Movie/pos_Mov/", true).getCases());
	}

	private void buildCasesOnHotel() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Hotel/neg_Hl/", false).getCases());
		caseBase.add(makeCaseBase("/Hotel/pos_Hl/", true).getCases());
	}

	private void buildCasesOnElectronics() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Electronics/neg_El/", false).getCases());
		caseBase.add(makeCaseBase("/Electronics/pos_El/", true).getCases());
	}

	private void buildCasesOnBook() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Book/neg_Bk/", false).getCases());
		caseBase.add(makeCaseBase("/Book/pos_Bk/", true).getCases());
	}

	private void buildCasesOnAppareal() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Apparel/neg_Apl/", false).getCases());
		caseBase.add(makeCaseBase("/Apparel/pos_Apl/", true).getCases());
	}

	private CaseBase makeCaseBase(String subTrainingSetPath,
			boolean truePolarity) throws IOException, Exception {
		CaseAuthoring caseConstructor = new CaseAuthoring();
		String path = baseTrainingSetPath + subTrainingSetPath;
		List<Document> trainingDocumentsList = DirectoryUtil.getListOfDocument(
				path, truePolarity);
		return caseConstructor.buildCaseBase(trainingDocumentsList,
				lexiconsList);
	}

	private void storeCaseBaseInFile() throws IOException {
		caseBase.writeToFile(resultPath, csvCaseBaseFileName);
		String serializedFileName = resultPath + serializedCaeBaseFileName;
		Serialization.serialize(caseBase, serializedFileName);
	}

	private float runningTimeInSeconds(long startTime, long endTime) {
		long diffTime = endTime - startTime;
		if (diffTime < 0)
			return 0.0f;
		return diffTime / 1000f;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public void setBaseTrainingSetPath(String baseTrainingSetPath) {
		this.baseTrainingSetPath = baseTrainingSetPath;
	}

	public void setSerializedCaeBaseFileName(String serializedCaeBaseFileName) {
		this.serializedCaeBaseFileName = serializedCaeBaseFileName;
	}

	public void setCsvCaseBaseFileName(String csvCaseBaseFileName) {
		this.csvCaseBaseFileName = csvCaseBaseFileName;
	}

}
