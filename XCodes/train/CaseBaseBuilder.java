package train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nlp.TextProcessor;
import nlp.TextProcessorFactory;
import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.NRC;
import lexicon.SentiWordNet;
import lexicon.SubjectivityClues;
import text_processor.Document;
import utilities.DirectoryUtil;
import utilities.Serialization;
import utilities.Utility;
import cbr.CaseBase;

public class CaseBaseBuilder {

	private String resultPath;
	private String rootTrainingSetPath;
	private String serializedCaeBaseFileName;
	private String csvCaseBaseFileName;
	private CaseBase caseBase;
	private List<Lexicon> lexiconsList;
	private TextProcessor processor; // ***

	public void train() throws IOException, Exception {
		initialize();
		long startTime = System.currentTimeMillis();
		buildLexiconList();
		buildCaseBase();
		long endTime = System.currentTimeMillis();
		SaveAndDisplayResults(startTime, endTime);
	}

	private void SaveAndDisplayResults(long startTime, long endTime)
			throws IOException {
		System.out.println("\n Case Base size = " + caseBase.getCases().size());
		System.out.printf("\n >>>>>>> By Negation : The time of runnig is: %f",
				runningTimeInSeconds(startTime, endTime));
		storeCaseBaseInFile();
	}

	private void initialize() {
		caseBase = new CaseBase();
		lexiconsList = new ArrayList<Lexicon>();
		TextProcessorFactory factory = new TextProcessorFactory();
		processor = factory.create("SnowBall_Stemmer");
		processor.enableNegationAnalysis(false);
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
		// buildCasesOnMusic();
		// buildCasesOnSmallTest();
	}

	private void buildCasesOnSmallTest() throws IOException, Exception {
		System.out.println("\n Test2 - Negative : ");
		caseBase.add(buildCaseBase("/Small_Test/neg/", false).getCases());
		caseBase.add(buildCaseBase("/Small_Test/pos", true).getCases());
	}

	private void buildCasesOnAppareal() throws IOException, Exception {
		System.out.println("\n Apparel - Negative : ");
		caseBase.add(buildCaseBase("/Apparel/neg_Apl/", false).getCases());
		System.out.println("\n Apparel - Positive : ");
		caseBase.add(buildCaseBase("/Apparel/pos_Apl/", true).getCases());
	}

	private void buildCasesOnBook() throws IOException, Exception {
		System.out.println("\n Book - Negative : ");
		caseBase.add(buildCaseBase("/Book/neg_Bk/", false).getCases());
		System.out.println("\n Book - Positive : ");
		caseBase.add(buildCaseBase("/Book/pos_Bk/", true).getCases());
	}

	private void buildCasesOnElectronics() throws IOException, Exception {
		System.out.println("\n Electronics - Negative : ");
		caseBase.add(buildCaseBase("/Electronics/neg_El/", false).getCases());
		System.out.println("\n Electronics - Positive : ");
		caseBase.add(buildCaseBase("/Electronics/pos_El/", true).getCases());
	}

	private void buildCasesOnHotel() throws IOException, Exception {
		System.out.println("\n Hotel - Negative : ");
		caseBase.add(buildCaseBase("/Hotel/neg_Hl/", false).getCases());
		System.out.println("\n Hotl - Positive : ");
		caseBase.add(buildCaseBase("/Hotel/pos_Hl/", true).getCases());
	}

	private void buildCasesOnMovie() throws IOException, Exception {
		System.out.println("\n movie - Negative : ");
		caseBase.add(buildCaseBase("/Movie/neg_Mov/", false).getCases());
		System.out.println("\n movie - Positive : ");
		caseBase.add(buildCaseBase("/Movie/pos_Mov/", true).getCases());
	}

	private void buildCasesOnMusic() throws IOException, Exception {
		System.out.println("\n Music - Negative : ");
		caseBase.add(buildCaseBase("/Music/neg_Mc/", false).getCases());
		System.out.println("\n Music - Positive : ");
		caseBase.add(buildCaseBase("/Music/pos_Mc/", true).getCases());
	}

	private CaseBase buildCaseBase(String subPath, boolean truePolarity)
			throws IOException, Exception {
		CaseBaseAuthore caseBaseAuthore = new CaseBaseAuthore();
		String path = rootTrainingSetPath + subPath;
		List<Document> trainingDocumentsList = DirectoryUtil.getListOfDocument(
				path, truePolarity);
		return caseBaseAuthore.buildCaseBase(trainingDocumentsList,
				lexiconsList, processor);
	}

	private void storeCaseBaseInFile() throws IOException {
		Utility.writeCaseBaseToFile(resultPath, csvCaseBaseFileName, caseBase);
		String serializedAbsoluteFileName = resultPath
				+ serializedCaeBaseFileName;
		Serialization.serialize(caseBase, serializedAbsoluteFileName);
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
