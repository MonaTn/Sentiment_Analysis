package xTestUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CopyOfExtractReviews {

	private final String positiveReviewsFileName = "PositiveHotelReviews.txt";
	private final String negativeReviewsFileName = "NegativeHotelReviews.txt";

	public void exteractReviewsFromAmazonDataset(String path, BufferedReader sourceBufferedReader) throws IOException {
		String line = "";
		while ((line = sourceBufferedReader.readLine()) != null) {
			createReview(path, sourceBufferedReader, line);
		}
	}

	
	private void createReview(String path, BufferedReader sourceBufferedReader,
			String line) throws IOException {
		File oneReviewFile = createReviewFile(path, reviewFileName);
		BufferedWriter bufferedWrite = new BufferedWriter(new FileWriter(
				oneReviewFile));
		writeReviewTextInFile(sourceBufferedReader, bufferedWrite, line);
	}

	private void writeReviewTextInFile(BufferedReader bufferedReader,
			BufferedWriter bufferedWrite, String line) throws IOException {
		
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
				negativeReviewsCount = putInReviewsInFile(negativeBufferedWriter,
						negativeReviewsCount, reviewsItems);
			}
			if (Integer.parseInt(reviewsItems[2]) > 3
					&& positiveReviewsCount < maximumNumberOfReviews) {
				positiveReviewsCount = putInReviewsInFile(positiveBufferedWriter,
						positiveReviewsCount, reviewsItems);
			}
		}
		positiveBufferedWriter.close();
		negativeBufferedWriter.close();
	}

	private int putInReviewsInFile(BufferedWriter negativeBufferedWriter,
			int negativeReviewsCount, String[] reviewsItems) throws IOException {
		negativeBufferedWriter.write(reviewsItems[1]);
		negativeBufferedWriter.newLine();
		negativeReviewsCount++;
		return negativeReviewsCount;
	}

	private  void preprocessingText(BufferedReader bufferedReader)
			throws IOException {
		String line = "";
		String positivePath = "../../TrainingSet/Hotel/Train/1200_Positive/";
		String negativePath = "../../TrainingSet/Hotel/Train/1200_Negative/";
		while ((line = bufferedReader.readLine()) != null) {
			String[] reviewItems = prepareDocumetItem(line);
			if (Integer.parseInt(reviewItems[2]) > 3) {
				www(positivePath, reviewItems);
			}
			if (Integer.parseInt(reviewItems[2]) < 3) {
				www(negativePath, reviewItems);
			}

		}
	}



	
}
