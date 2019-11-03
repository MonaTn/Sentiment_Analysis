package xTestUtil;

/**
 * Last modification 1 April 2013
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import shared.DirectoryUtil;
import shared.FileUtility;

public class ExtractAnnotation {
	private static String sourcePath = "../../TrainingSet/SFU";
	private static String destinationPath ="../../TrainingSet/new";

	public static void main(String[] args) throws IOException {
		File[] listOfFileNames = DirectoryUtil.getListOfFiles(sourcePath);
		System.out.println(listOfFileNames.length);
		for (File file : listOfFileNames) {
			BufferedReader sourceBufferedReader = new BufferedReader(
					new FileReader(file.getAbsolutePath()));
			createReviewFiles(file.getName(), sourceBufferedReader);
		}
		
	}

	private static void createReviewFiles(String name,
			BufferedReader bufferedReader) throws IOException {
		String line = "";
		System.out.println(name);
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
			if (line.contains("-<cue type=\"negation\" ")) {
				creatFile(name, bufferedReader);
			}
		}
	}

	private static void creatFile(String name, BufferedReader bufferedReader)
			throws IOException {
		File newFile = FileUtility.createFile(destinationPath, name);
		BufferedWriter bufferedWriter = (new BufferedWriter(new FileWriter(newFile)));
		writeInFile(bufferedReader, bufferedWriter);
		bufferedWriter.close();
	}

	private static void writeInFile(BufferedReader bufferedReader,
			BufferedWriter bufferedWriter) throws IOException {
		String line;
		line = bufferedReader.toString();//.readLine();
		while (!line.equals("</xcope>")) {
			bufferedWriter.write(line);
			line = bufferedReader.readLine();
		}
	}

}
