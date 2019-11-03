package shared;

/**
 * Last modification 28 February 2013
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseBase1 implements Serializable {

	private static final long serialVersionUID = -2514867103449049009L;
	private List<Case> caseList;
    private final int length = Constant.featureSize;
    private double[] minValues = new double[length];
	private double[] maxValues;

	public CaseBase1() {
		caseList = new ArrayList<Case>();
	}


	public List<Case> getCases() {
		return caseList;
	}

	public void setCaseBase(List<Case> cases) {
		this.caseList = cases;
	}

	public void add(Case oneCase) {
		caseList.add(oneCase);
	}

	public void add(List<Case> cases) {
		this.caseList.addAll(cases);
	}

	public double[] getMinValues() {
		return minValues;
	}

	public void setMinValues(double[] minValues) {
		this.minValues = Arrays.copyOf(minValues, length);
	}

	public void setMinValues() {
		this.minValues = extractMinimumValues();
	}

	public double[] getMaxValues() {
		return maxValues;
	}

	public void setMaxValues(double[] maxValues) {
		this.maxValues = Arrays.copyOf(maxValues, length);
	}

	public void setMaxValues() {
		this.maxValues = extractMaximumValues();
	}

	public double[] extractMinimumValues() {
		double[] firstCaseFeatures = caseList.get(0).getFeatures();
		setMinValues(firstCaseFeatures);
		for (Case oneCase : caseList) {
			double[] caseFeatures = oneCase.getFeatures();
			minValues = Utility.findMinimumValues(minValues, caseFeatures);
		}
		return minValues;
	}

	public double[] extractMaximumValues() {
		double[] firstCaseFeatures = caseList.get(0).getFeatures();
		System.out.println("Features oneCase  " + Arrays.toString(firstCaseFeatures));
 		setMaxValues(firstCaseFeatures);
		for (Case oneCase : caseList) {
			double[] caseFeatures = oneCase.getFeatures();
			maxValues = Utility.findMaximumValues(maxValues, caseFeatures);
		}
		return maxValues;
	}


	public void writeToFile(String path, String fileName) throws IOException {
		File resultFileOfDocuments = FileUtility.createFile(path, fileName);
		BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(
				resultFileOfDocuments));
		FileUtility.writeToFile(title, bufferedwriter);
		for (Case oneCase : getCases()) {
			FileUtility.writeToFile(oneCase.toString(), bufferedwriter);
		}
		bufferedwriter.close();
	}

	private String title = "Name" + " , " + "WORDS" + " , " + "TOKENS" + " , "
			+ "SENTENCES" + " , " + "AVERAGE_SENTENCE_SIZE" + " , "
			+ "SPACE_RATIO" + " , " + "STOPWORDS_RATIO" + " , "
			+ "AVERAGE_SYLLABLES" + " , " + "MONOSYLLABLE_RATIO" + " , "
			+ "WORD_TO_TOKEN_RATIO" + " , " + "UNIQUE_WORDS_RATIO" + " , "
			+ "VERBS Counts" + " , " + "ADJECTIVE Counts" + " , "
			+ "ADVERB Counts" + " , " + "NOUNS Counts" + " , "
			+ "PUNCTUATION Counts" + " , " + "CONJUNCTION Counts" + " , "
			+ "INTERJECTION Counts" + "," + "Case Solution";
}
