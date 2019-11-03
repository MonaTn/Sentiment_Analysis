package basic;

/**
 * Last modification 1 April 2013
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import utilities.FileUtility;

public class ExtractHotelReviews {
	private static final String hotelSourceFile = "../../TrainingSet/Hotel/Main Folders/Baccianella,Esuli,Sebastiani (2009) -DATA- TripAdvisor_corpus/AllTripAdvisor_15763.txt";
	private final static String positiveDestinationPath = "../../Results/2013/11.02.2013/Hotel/pos_Hl/pos_H_";
	private final static String negativeDestinationPath = "../../Results/2013/11.02.2013/Hotel/neg_Hl/neg_H_";
	private static int counter;

	public static void main(String[] args) throws Exception {
		counter = 0;
		BufferedReader sourceBufferedReader = new BufferedReader(
				new FileReader(hotelSourceFile));
		createReviewFiles(sourceBufferedReader);
	}

	private static void createReviewFiles(BufferedReader bufferedReader)
			throws Exception, IOException {
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			reviewProcess(line);
		}
	}

	private static void reviewProcess(String line) throws IOException {
		String[] reviewItems = separateDocumentItems(line);
		String reviewFileName = Integer.toString(++counter) + "_"
				+ reviewItems[0] + ".txt";
		String reviewText = reviewItems[1];
		int reviewRankingNumber = Integer.parseInt(reviewItems[2]);
		creatReviewFile(reviewFileName, reviewText, reviewRankingNumber);
	}

	private static String[] separateDocumentItems(String line) {
		line = line.replaceAll("\\_(PROS).+\t", "\t");
		return line.split("\t");
	}

	private static void creatReviewFile(String fileName, String text, int rate)
			throws IOException {
		if (rate > 3) {
			writeToFile(positiveDestinationPath, fileName, text);
		} else if (rate < 3) {
			writeToFile(negativeDestinationPath, fileName, text);
		}
	}

	private static void writeToFile(String path, String fileName, String text)
			throws IOException {
		File newFile = FileUtility.createFile(path, fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
		bw.write(text);
		bw.close();
	}

}
