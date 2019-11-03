package methods_tester;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.SubjectivityClues;
import nlp.text_processor.Document;
import nlp.text_processor.WordList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import utilities.Utility;
import classifier.LexiconBasedClassifier;

public class TestLexicons {
	private static final int[] positiveTagIndex = { 2, 4, 112, 178 };

	public static void main(String[] args) throws IOException, Throwable {
		Lexicon lexicon = new GeneralInquirer();
		String strFileName = "neg920.text";
		String strPath = "../../Results/2014/ErrorAnalysis/";
		Document document = new Document(strFileName, strPath, false);
		WordList wordList = TestWordList.extractWordList(strPath, strFileName);
		document.setWordsList(wordList);
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();

		double score = classifier.calculateDocumentScore(document, lexicon);
		System.out.println(score);

	}

	protected static void WriteLexiconMapInFile(String strFileName,
			String strPath) throws IOException {
		Lexicon lexicon = new SubjectivityClues();
		Map<String, Double> wordAndScore = lexicon.getMap();
		Utility.writeMapToFile(strPath, strFileName, wordAndScore);
		System.out.println("Done! :) ");
	}

	protected static void readXLSFile(String strFileName, String strPath)
			throws FileNotFoundException,
			IOException {
		// String strFileName = "inquirerbasic.xls";
		// String strPath = "Lexicons/";
		InputStream inputFileStream = new BufferedInputStream(
				new FileInputStream(strPath + strFileName));
		POIFSFileSystem fileSystem = new POIFSFileSystem(inputFileStream);
		HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
		HSSFSheet sheet = workBook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();
		int pos = 0;
		while (rowIterator.hasNext()) {
			HSSFRow row = (HSSFRow) rowIterator.next();
			if (isPositive(row)) {
				pos++;
			}

		}
		System.out.println(pos);
	}

	protected static Object getType(Row row) {
		return row.getCell(2).getStringCellValue();
	}

	protected static boolean isPositive(Row row) {
		for (int index : positiveTagIndex) {
			if (!row.getCell(index).getStringCellValue().isEmpty()) {
				System.out.println(row.getCell(0).toString() + " , "
						+ row.getCell(index).toString());
				return true;
			}
		}
		return false;
	}

	protected static void printRowContent(HSSFRow row) {
		System.out.println(row.getCell(2).toString() + " , "
				+ row.getCell(3).toString() + " , " + row.getCell(4).toString()
				+ " , " + row.getCell(6).toString() + " , "
				+ row.getCell(112).toString() + " , "
				+ row.getCell(113).toString() + " , "
				+ row.getCell(116).toString() + " , "
				+ row.getCell(117).toString() + " , "
				+ row.getCell(118).toString() + " , "
				+ row.getCell(119).toString() + " , "
				+ row.getCell(120).toString() + " , "
				+ row.getCell(177).toString() + " , "
				+ row.getCell(178).toString() + " , "
				+ row.getCell(184).toString());
	}

}
