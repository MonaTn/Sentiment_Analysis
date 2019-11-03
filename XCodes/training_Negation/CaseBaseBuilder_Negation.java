package training_Negation;

/**
 * Last modification 16 October 2013
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
import shared.Utility;
import xTestUtil.NegationTextPreprocessor_SnowBall;


public class CaseBaseBuilder_Negation {

	private String resultPath;
	private String rootTrainingSetPath;
	private String serializedCaeBaseFileName;
	private String csvCaseBaseFileName;
	private CaseBase caseBase;
	private List<Lexicon> lexiconsList;

	public void train() throws IOException, Exception {
		caseBase = new CaseBase();
		lexiconsList = new ArrayList<Lexicon>();
		long startTime = System.currentTimeMillis();
		buildLexiconList();
		buildCaseBase();
		long endTime = System.currentTimeMillis();
		System.out.println("\n Case Base size = " + caseBase.getCases().size());
		System.out.printf("\n >>>>>>> By Negation : The time of runnig is: %f",
				runningTimeInSeconds(startTime, endTime));
		storeCaseBaseInFile();
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
//		 buildCasesOnBook();
//		 buildCasesOnElectronics();
//		 buildCasesOnHotel();
//		 buildCasesOnMovie();
//		 buildCasesOnMusic();

//		buildCasesOnSmallTest();
	}

	private void buildCasesOnSmallTest() throws IOException, Exception {
		caseBase.add(makeCaseBase("/Test/", false).getCases());
	}

	private void buildCasesOnAppareal() throws IOException, Exception {
		System.out.println("\n Apparel - Negative : ");
		caseBase.add(makeCaseBase("/Apparel/neg_Apl/", false).getCases());
		System.out.println("\n Apparel - Positive : ");
		caseBase.add(makeCaseBase("/Apparel/pos_Apl/", true).getCases());
	}

	private void buildCasesOnBook() throws IOException, Exception {
		System.out.println("\n Book - Negative : ");
		caseBase.add(makeCaseBase("/Book/neg_Bk/", false).getCases());
		System.out.println("\n Book - Positive : ");
		caseBase.add(makeCaseBase("/Book/pos_Bk/", true).getCases());
	}

	private void buildCasesOnElectronics() throws IOException, Exception {
		System.out.println("\n Electronics - Negative : ");
		caseBase.add(makeCaseBase("/Electronics/neg_El/", false).getCases());
		System.out.println("\n Electronics - Positive : ");
		caseBase.add(makeCaseBase("/Electronics/pos_El/", true).getCases());
	}

	private void buildCasesOnHotel() throws IOException, Exception {
		System.out.println("\n Hotel - Negative : ");
		caseBase.add(makeCaseBase("/Hotel/neg_Hl/", false).getCases());
		System.out.println("\n Hotl - Positive : ");
        caseBase.add(makeCaseBase("/Hotel/pos_Hl/", true).getCases());
	}

	private void buildCasesOnMovie() throws IOException, Exception {
		System.out.println("\n movie - Negative : ");
		caseBase.add(makeCaseBase("/Movie/neg_Mov/", false).getCases());
		System.out.println("\n movie - Positive : ");
		caseBase.add(makeCaseBase("/Movie/pos_Mov/", true).getCases());
	}

	private void buildCasesOnMusic() throws IOException, Exception {
		System.out.println("\n Music - Negative : ");
		caseBase.add(makeCaseBase("/Music/neg_Mc/", false).getCases());
		System.out.println("\n Music - Positive : ");
		caseBase.add(makeCaseBase("/Music/pos_Mc/", true).getCases());
	}

	private CaseBase makeCaseBase(String subTrainingSetPath,
			boolean truePolarity) throws IOException, Exception {
		CaseAuthoring_Negation caseBaseAuthor = new CaseAuthoring_Negation();
		String path = rootTrainingSetPath + subTrainingSetPath;
		List<Document> trainingDocumentsList = DirectoryUtil.getListOfDocument(
				path, truePolarity);
		return caseBaseAuthor.buildCaseBase(trainingDocumentsList,
				lexiconsList); // send processor to avoid rebuilding
											// it ? OK
	}

	private void storeCaseBaseInFile() throws IOException {
		Utility.writeToFile(resultPath, csvCaseBaseFileName, caseBase);
		String serializedAbsoluteFileName = resultPath
				+ serializedCaeBaseFileName;
		Serialization.serialize(caseBase, serializedAbsoluteFileName);
		// caseBase.serialized(resultPath, serializedCaeBaseFileName);
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

	public void setRootOfTrainingPath(String baseTrainingSetPath) {
		this.rootTrainingSetPath = baseTrainingSetPath;
	}

	public void setSerializedCaeBaseFileName(String serializedCaeBaseFileName) {
		this.serializedCaeBaseFileName = serializedCaeBaseFileName;
	}

	public void setCsvCaseBaseFileName(String csvCaseBaseFileName) {
		this.csvCaseBaseFileName = csvCaseBaseFileName;
	}

}
