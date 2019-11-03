package xTestUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileUtility_8Jan2013 {

	public File createFile(String path, String fileName) throws IOException {
		File file = new File(path + fileName);
		if (!file.exists()) {
			file.createNewFile();
			file.setWritable(true);
		}
		return file;
	}

	public  String buildFileName(String firstPart, String secondPart) {
		return firstPart.substring(0,
				firstPart.length() - 4) + secondPart;
	}

	public  Scanner openFile(String fileName) throws IOException {
		return  new Scanner(new File(fileName));
	}

	public  String[] tokenize(String str, String regex) {
		return str.split(regex);
	}
	
	public void writeToFile(String line, BufferedWriter bufferWrite)
			throws IOException {
		bufferWrite.write(line);
		bufferWrite.newLine();
	}

}
