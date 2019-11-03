package utilities;

/**
 * Last modification 25 February 2013
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nlp.text_processor.Document;

public class DirectoryUtil {

	 public static File[] getListOfFiles(String path) {
		 return new File(path).listFiles();
	}

	public static String createSubDirectory(String motherPath, String childPath) {
		File new_Path = new File(motherPath + "/" + childPath);
		return (new_Path.mkdir()) ? new_Path.toString() : "";
	}

	public static void printNameOfFilesInDirectory(File[] listOfFiles) {
		for (File file : listOfFiles)
			if (file.isFile()) {
				System.out.println(file.getName());
			}
	}

	public static List<Document> getListOfDocument (String path, boolean polarity) throws Exception {
		List<Document> documentList = new ArrayList <Document>();
		File[] listOfFiles = getListOfFiles(path);
		createDocumentList(path, polarity, documentList, listOfFiles);
		return documentList;
	}

	private static void createDocumentList(String path, boolean polarity,
                                           List<Document> documentList, File[] listOfFiles) throws Exception
			 {
		for (File file : listOfFiles) {
			if (!file.isFile()) {
				continue;
			}
			Document doc = new Document(file.getName(), path, polarity);
			documentList.add(doc);
		}
	}
}
