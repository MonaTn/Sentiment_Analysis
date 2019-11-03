package training_Negation;

/**
 * Last modification 16 October 2013
 */

import java.io.IOException;

public class Main_Training_Negation {

	private static String resultPath = "../../Results/2013/Dec/9/";
	private static String rootTrainingSetPath = "../../DataSet/";
	private static String serializedCaeBaseFileName = "Test_Neg_app.ser";
	private static String csvCaseBaseFileName = "Test_Neg_app.csv";

	public static void main(String[] args) throws IOException, Exception {
		CaseBaseBuilder_Negation trainer = new CaseBaseBuilder_Negation();
		trainer.setResultPath(resultPath);
		trainer.setRootOfTrainingPath(rootTrainingSetPath);
		trainer.setCsvCaseBaseFileName(csvCaseBaseFileName);
		trainer.setSerializedCaeBaseFileName(serializedCaeBaseFileName);
		trainer.train();
	}

}
