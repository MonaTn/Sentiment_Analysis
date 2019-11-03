package testing;

/**
 * Last modification 4 March 2013
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import lexicon.Lexicon;

import shared.CaseSolution;
import shared.Document;

public class PredictedDocument_2013March {
	private Document document;
	private double totalScore;
	private CaseSolution caseSolution;

	public PredictedDocument_2013March() {
	}

	public PredictedDocument_2013March(Document document, double totalScore,
			CaseSolution caseSolution) {
		super();
		this.document = document;
		this.totalScore = totalScore;
		this.caseSolution = caseSolution;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}

	public CaseSolution getCaseSolution() {
		return caseSolution;
	}

	public void setCaseSolution(CaseSolution caseSolution) {
		for (Lexicon lexicon : caseSolution.getLexicons()) {
			this.caseSolution.add(lexicon);
		}
	}

	public static void writeToFile(
			List<PredictedDocument_2013March> predictedDocumentList, String path,
			String fileName) throws IOException {
		File resultFileOfDocuments = new File(path); // !!????
		if (!resultFileOfDocuments.exists()) {
			resultFileOfDocuments.createNewFile();
		}
		BufferedWriter bufferWrite = new BufferedWriter(
				new FileWriter(fileName));
		bufferWrite.write("Name" + " , " + "Total Score" + " , "
				+ "Predicted Polarity" + " , " + "Case Solution");
		bufferWrite.newLine();
		for (PredictedDocument_2013March predictedDocument : predictedDocumentList) {
			bufferWrite.write(predictedDocument.getDocument().getName() + " , "
					+ predictedDocument.getTotalScore() + " , "
					+ predictedDocument.getDocument().getPolarity() + " , "
					+ predictedDocument.getCaseSolution().getLexicons());
			bufferWrite.newLine();
		}
		bufferWrite.close();
	}

}
