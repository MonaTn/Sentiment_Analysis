package utilities;

/**
 * Last modification 29 March 2013
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import basic.Constant;
import cbr.Case;
import cbr.CaseBase;
import lexicon.Lexicon;
import nlp.text_processor.Document;
import nlp.text_processor.Word;
import nlp.text_processor.WordList;

public class Utility {

	private final static int length = Constant.featureSize;

	public static double[] findMin(double[] array1, double[] array2) {
		double[] min = new double[length];
		for (int i = 0; i < length; i++) {
			min[i] = Math.min(array1[i], array2[i]);
		}
		return min;
	}

	public static double[] findMax(double[] array1, double[] array2) {
		double[] max = new double[length];
		for (int i = 0; i < length; i++) {
			max[i] = Math.max(array1[i], array2[i]);
		}
		return max;
	}

	public static void writeWordsListToFile(String path, Document document)
			throws IOException {
		String fileName = document.getName().substring(0,
				document.getName().length() - 4)
				+ ".csv";
		File resultFileOfDocuments = new File(path + fileName);
		if (!resultFileOfDocuments.exists()) {
			resultFileOfDocuments.createNewFile();
		}
		BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(
				resultFileOfDocuments));
		for (Word word : document.getWordsList().getWords()) {
			bufferWrite.write(word.getWord() + "," + word.getTag() + ","
					+ word.getCount());
			bufferWrite.newLine();
		}
		bufferWrite.close();
		System.out.println("Done!  :)");
	}

	public String caseToString(Case oneCase) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(oneCase.getName());
		stringBuilder.append(", ");
		stringBuilder.append(Arrays.toString(oneCase.getCaseDescription()
				.getFeatures()));
		stringBuilder.append(", ");
		for (Lexicon lexicon : oneCase.getCaseSolution().getLexicons()) {
			stringBuilder.append(lexicon + ", ");
		}
		return stringBuilder.toString();
	}

	public static void writeListInFile(List<String> strList, String strPath,
			String strFileName) {
		String strAbsolutFileName = strPath + strFileName;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					strAbsolutFileName));
			for (String string : strList) {
				bw.write(string);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void writeMapToFile(String strPath, String strFileName,
			Map<String, Double> map) throws IOException { // must delete
		String strAbsolutFileName = strPath + strFileName;
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				strAbsolutFileName));
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			bw.write(entry.getKey() + ", " + entry.getValue());
			bw.newLine();
		}
		bw.close();
	}

	public static void writeListOfVectorsToFile(List<Vector<Object>> vectors, String path,
			String fileName) throws IOException {
		File resultFile = FileUtility.createFile(path, fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				resultFile));
		for (Vector<Object> vector : vectors) {
			FileUtility.writeALineInFile(vector.toString(), bufferedWriter);
		}
		bufferedWriter.close();
	}

	public static void writeListOfVectorsToFile(List<Vector<Object>> vectors,
			BufferedWriter bufferedWriter) throws Exception {
		for (Vector<Object> vector : vectors) {
			FileUtility.writeALineInFile(vector.toString(), bufferedWriter);
		}
	}

	public static void writeWordListToFile(WordList wordList, String fileName)
			throws IOException {
		String path = "../../Results/2015/";
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				FileUtility.createFile(path, fileName)));
		for (Word word : wordList.getWords()) {
			bw.write(word.getWord() + "," + word.getTag() + ", "
					+ word.getCount());
			bw.newLine();
		}
		bw.close();
	}

	public static void writeCaseBaseToFile(String path, String fileName,
			CaseBase caseBase) throws IOException {
		File resultFileOfDocuments = FileUtility.createFile(path, fileName);
		BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(
				resultFileOfDocuments));
		bufferedwriter.write(title);
		// FileUtility.writeALineInFile(title, bufferedwriter);
		// for (Case oneCase : caseBase.getCases()) {
		// FileUtility.writeALineInFile(oneCase.toString(), bufferedwriter);
		// }
		bufferedwriter.close();
	}

	private static String title = "Name" + " , " + "WORDS" + " , " + "TOKENS"
			+ " , " + "SENTENCES" + " , " + "AVERAGE_SENTENCE_SIZE" + " , "
			+ "SPACE_RATIO" + " , " + "STOPWORDS_RATIO" + " , "
			+ "AVERAGE_SYLLABLES" + " , " + "MONOSYLLABLE_RATIO" + " , "
			+ "WORD_TO_TOKEN_RATIO" + " , " + "UNIQUE_WORDS_RATIO" + " , "
			+ "VERBS Counts" + " , " + "ADJECTIVE Counts" + " , "
			+ "ADVERB Counts" + " , " + "NOUNS Counts" + " , "
			+ "PUNCTUATION Counts" + " , " + "CONJUNCTION Counts" + " , "
			+ "INTERJECTION Counts" + " , " + "Case Solution";

}
