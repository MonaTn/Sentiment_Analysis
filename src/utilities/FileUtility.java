package utilities;

/**
 * Last modification 25 February 2013
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileUtility {

	public static File createFile(String path, String fileName)
			throws IOException {
		File file = new File(path + fileName);
		if (!file.exists()) {
			file.createNewFile();
			file.setWritable(true);
			System.out.println(" Create file : " + fileName);
			return file;
		} else {
			System.out.println("\n >>> " + file.getName() + " is already existed!!");
			return null;
		}
	}

	public static String buildFileName(String firstPart, String secondPart) {
		return firstPart.substring(0, firstPart.length() - 4) + secondPart;
	}

	public static Scanner openFileByScanner(String fileName) throws IOException {
		return new Scanner(new File(fileName));
	}

	public static String[] tokenize(String str, String regex) {
		return str.split(regex);
	}

	public static void writeALineInFile(String line, BufferedWriter bufferWriter)
			throws IOException {
		bufferWriter.write(line);
		bufferWriter.newLine();
	}

}
