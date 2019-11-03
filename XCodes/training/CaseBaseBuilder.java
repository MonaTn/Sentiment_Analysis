package training;

/**
 * Last modification 4 March 2013
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import TextProcess.Stemmer;

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
import shared.TextProcessor;
import shared.TextProcessorFactory;
import shared.Utility;

public class CaseBaseBuilder {

	public String resultPath;
	public String rootTrainingSetPath;
	public String serializedCaeBaseFileName;
	public String csvCaseBaseFileName;
	private TextProcessor processor; // Dec 9
	private CaseBase caseBase = new CaseBase();
	private ArrayList<Lexicon> lexiconsList = new ArrayList<Lexicon>();

	public void train() throws IOException, Exception {
		long startTime = System.currentTimeMillis();
		processor.setNegationFlag(false);
		processor = TextProcessorFactory.create(Stemmer.SNOW_BALL); // Dec 10
		buildLexiconList();
		buildCaseBase();
		storeCaseBaseInFile();
		long endTime = System.currentTimeMillis();
		System.out.printf("\n >>>The time of runnig is: %f",
				runningTimeInSeconds(startTime, endTime));
		System.out.println("\n Case Base size = " + caseBase.getCases().size());
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
		// buildCasesOnBook();
		// buildCasesOnElectronics();
		// buildCasesOnHotel();
		// buildCasesOnMovie();
		// buildCasesOnMusic();
		// buildCasesOnSmallTest();
	}

	private void buildCasesOnSmallTest() throws IOException, Exception {
		System.out.println("Test2 - Negative : ");
		caseBase.add(makeCaseBase("/Test/", false).getCases());
		// caseBase.add(makeCaseBase("/Test/2", false).getCases());

	}

	private void buildCasesOnAppareal() throws IOException, Exception {
		System.out.println("Apparel - Negative : ");
		caseBase.add(makeCaseBase("/Apparel/neg_Apl/", false).getCases());
		System.out.println("Apparel - Positive : ");
		caseBase.add(makeCaseBase("/Apparel/pos_Apl/", true).getCases());
	}

	private void buildCasesOnBook() throws IOException, Exception {
		System.out.println("Book - Negative : ");
		caseBase.add(makeCaseBase("/Book/neg_Bk/", false).getCases());
		System.out.println("Book - Positive : ");
		caseBase.add(makeCaseBase("/Book/pos_Bk/", true).getCases());
	}

	private void buildCasesOnElectronics() throws IOException, Exception {
		System.out.println("Electronics - Negative : ");
		caseBase.add(makeCaseBase("/Electronics/neg_El/", false).getCases());
		System.out.println("Electronics - Positive : ");
		caseBase.add(makeCaseBase("/Electronics/pos_El/", true).getCases());
	}

	private void buildCasesOnHotel() throws IOException, Exception {
		System.out.println("Hotel - Negative : ");
		caseBase.add(makeCaseBase("/Hotel/neg_Hl/", false).getCases());
		System.out.println("Hotel - Positive : ");
		caseBase.add(makeCaseBase("/Hotel/pos_Hl/", true).getCases());
	}

	private void buildCasesOnMovie() throws IOException, Exception {
		System.out.println("Movie - Negative : ");
		caseBase.add(makeCaseBase("/Movie/neg_Mov/", false).getCases());
		System.out.println("Movie - Positive : ");
		caseBase.add(makeCaseBase("/Movie/pos_Mov/", true).getCases());
	}

	private void buildCasesOnMusic() throws IOException, Exception {
		System.out.println("Music - Negative : ");
		caseBase.add(makeCaseBase("/Music/neg_Mc/", false).getCases());
		System.out.println("Music - Positive : ");
		caseBase.add(makeCaseBase("/Music/pos_Mc/", true).getCases());
	}

	private CaseBase makeCaseBase(String subPath, boolean truePolarity)
			throws IOException, Exception {
		CaseAuthoring caseConstructor = new CaseAuthoring();
		String path = rootTrainingSetPath + subPath;
		List<Document> trainingDocumentsList = DirectoryUtil.getListOfDocument(
				path, truePolarity);
		return caseConstructor.buildCaseBase(trainingDocumentsList,
				lexiconsList, processor); // send processor to avoid reloading
											// it // Dec 9
	}

	private void storeCaseBaseInFile() throws IOException {
		Utility.writeToFile(resultPath, csvCaseBaseFileName, caseBase);
		String serializedFileName = resultPath + serializedCaeBaseFileName;
		Serialization.serialize(caseBase, serializedFileName);
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

	public void setBaseTrainingSetPath(String baseTrainingSetPath) {
		this.rootTrainingSetPath = baseTrainingSetPath;
	}

	public void setSerializedCaeBaseFileName(String serializedCaeBaseFileName) {
		this.serializedCaeBaseFileName = serializedCaeBaseFileName;
	}

	public void setCsvCaseBaseFileName(String csvCaseBaseFileName) {
		this.csvCaseBaseFileName = csvCaseBaseFileName;
	}

}
