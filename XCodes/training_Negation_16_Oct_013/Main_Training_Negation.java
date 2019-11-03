package training_Negation;

/**
 * Last modification 25 February 2013
 */

import java.io.IOException;

import shared.Constant;

public class Main_Training_Negation {

	private static String resultPath = Constant.resultPath;
	private static String rootTrainingSetPath = Constant.rootTrainingSetPath;
	private static String serializedCaeBaseFileName = "CaseBase_Movies_neg.ser";
	private static String csvCaseBaseFileName = "CaseBase_Movies_neg.csv";
	
	public static void main(String[] args) throws IOException, Exception {
		CaseBaseBuilder_Negation trainer = new CaseBaseBuilder_Negation();
		setParameters(trainer);
		trainer.train();
	}

	private static void setParameters(CaseBaseBuilder_Negation trainer) {
		trainer.setResultPath(resultPath);
		trainer.setBaseTrainingSetPath(rootTrainingSetPath);
		trainer.setCsvCaseBaseFileName(csvCaseBaseFileName);
		trainer.setSerializedCaeBaseFileName(serializedCaeBaseFileName);
	}

}
