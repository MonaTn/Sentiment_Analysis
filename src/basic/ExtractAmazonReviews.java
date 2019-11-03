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

public class ExtractAmazonReviews {
	private static String sourceFile = "../../DataBase/Amazon Reviews/unprocessed/4-music/negative.review";
	private static String destinationPath = "../../Results/Music";
	private static int lineNumber = 0;
	private static final String domain = "";

	public static void main(String[] args) throws IOException {
		BufferedReader sourceBufferedReader = new BufferedReader(
				new FileReader(sourceFile));
		createReviewFiles(destinationPath, sourceBufferedReader);
	}

	private static void createReviewFiles(String path,
			BufferedReader bufferedReader) throws IOException {
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			lineNumber++;
			if (line.equals("<review_text>")) {
				creatReview(bufferedReader);
			}
		}
	}

	private static void creatReview(BufferedReader bufferedReader)
			throws IOException {
		lineNumber++;
		String fileName = domain + Integer.toString(lineNumber) + ".text";
		File newFile = FileUtility.createFile(destinationPath, fileName);
		BufferedWriter bufferedWriter = (new BufferedWriter(new FileWriter(
				newFile)));
		writeInReviewFile(bufferedReader, bufferedWriter);
		bufferedWriter.close();
	}

	private static void writeInReviewFile(BufferedReader bufferedReader,
			BufferedWriter bufferedWriter) throws IOException {
		String line;
		line = bufferedReader.readLine();
		while (!line.equals("</review_text>")) {
			bufferedWriter.write(line);
			line = bufferedReader.readLine();
			lineNumber++;
		}
	}

}
