package xTestUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExteractReviews {

	private final String positiveReviewsFileName = "PositiveHotelReviews.txt";
	private final String negativeReviewsFileName = "NegativeHotelReviews.txt";

	public File exteractReviewsFromRawText(String path, String reviewFileName,
			BufferedReader bufferedReader) throws IOException {
		File reviewsFile = createReviewFile(path, reviewFileName);
		BufferedWriter bufferedWrite = new BufferedWriter(new FileWriter(
				reviewsFile));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			if (line.equals("<review_text>")) {
				while (!line.equals("</review_text>")) {
					bufferedWrite.write(bufferedReader.readLine());
					line = bufferedReader.readLine();
				}
				bufferedWrite.newLine();
			}
		}
		bufferedWrite.close();
		return reviewsFile;
	}

	public void exteractReviewsFromProcessedText(String path,
			BufferedReader bufferedReader) throws IOException {
		File positivesFile = createReviewFile(path, positiveReviewsFileName);
		File negativesFile = createReviewFile(path, negativeReviewsFileName);
		BufferedWriter positiveBufferedWriter = new BufferedWriter(
				new FileWriter(positivesFile));
		BufferedWriter negativeBufferedWriter = new BufferedWriter(
				new FileWriter(negativesFile));
		int maximumNumberOfReviews = 1437;
		int positiveReviewsCount = 0;
		int negativeReviewsCount = 0;
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			line = line.replaceAll("\\_(PROS).+\t", "\t");
			String[] reviewsItems = line.split("\t");
			if (Integer.parseInt(reviewsItems[2]) < 3
					&& negativeReviewsCount < maximumNumberOfReviews) {
				negativeReviewsCount = writeInReviewFile(negativeBufferedWriter,
						negativeReviewsCount, reviewsItems);
			}
			if (Integer.parseInt(reviewsItems[2]) > 3
					&& positiveReviewsCount < maximumNumberOfReviews) {
				positiveReviewsCount = writeInReviewFile(positiveBufferedWriter,
						positiveReviewsCount, reviewsItems);
			}
		}
		System.out.println(positiveReviewsCount);
		System.out.println(negativeReviewsCount);
		positiveBufferedWriter.close();
		negativeBufferedWriter.close();
	}

	private int writeInReviewFile(BufferedWriter negativeBufferedWriter,
			int negativeReviewsCount, String[] reviewsItems) throws IOException {
		negativeBufferedWriter.write(reviewsItems[1]);
		negativeBufferedWriter.newLine();
		negativeReviewsCount++;
		return negativeReviewsCount;
	}

	private File createReviewFile(String path, String reviewFileName)
			throws IOException {
		FileUtility fileUtil = new FileUtility();
		File reviewsFile = fileUtil.createFile(path, reviewFileName);
		return reviewsFile;
	}
}
