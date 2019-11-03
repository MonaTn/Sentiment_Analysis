package xExtraClass;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import lexicon.Lexicon;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

public class GI2 extends Lexicon implements Serializable {

	private static final long serialVersionUID = -8727298228525036877L;
	private String lexiconFile = "Lexicons/inquirerbasic.xls";
	private int[] positiveTagIndex = { 2, 4, 112, 178 };
	private int[] negativeTagIndex = { 3, 6, 113, 177 };
	private int[] verbTagIndex = { 116, 117, 118 };
	private int[] adjectiveTagIndex = { 119, 120 };
	private int tagsIndex = 185;

	public GI2() {
		posedLexicon = true;
		buildLexicon(lexiconFile);
	}

	@Override
	protected void buildLexicon(String lexiconFile) {
		wordAndScore = new HashMap<String, Double>();
		try {
			InputStream inputFileStream = new BufferedInputStream(
					new FileInputStream(lexiconFile));
			POIFSFileSystem fileSystem = new POIFSFileSystem(inputFileStream);
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.rowIterator();
			while (rowIterator.hasNext()) {
				addWordsAndScoreToMap(rowIterator.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(Row row) {
		String word = extractword(row);
		String tag = extractTag(row);
		double initialScore = extractScore(row);
		if (!tag.isEmpty()) {
		String token = word + "#" + tag;
		double score = computeScore(token, initialScore);
		wordAndScore.put(token, score);
		}
	}

	private String extractword(Row row) {
		String[] tokens = row.getCell(0).toString().split("#");
		return tokens[0].toLowerCase();
	}

	private boolean isPositive(Row row) {
		for (int index : positiveTagIndex) {
			if (!row.getCell(index).getStringCellValue().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean isNegative(Row row) {
		for (int index : negativeTagIndex) {
			if (!row.getCell(index).getStringCellValue().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private double extractScore(Row row) {
		if (isPositive(row)) {
			return (double) 1;
		} else if (isNegative(row)) {
			return (double) -1;
		} else {
			return (double) 0;
		}
	}

	private String extractTag(Row row) {
		if (isAdjective(row)) {
			return "a";
		} else if (isVerb(row)) {
			return "v";
		} else if (isAdverb(row)) {
			return "r";
		} else if (isNoun(row)) {
			return "n";
		} else {
			return "";
		}
	}

	private boolean isVerb(Row row) {
		if (row.getCell(tagsIndex).getStringCellValue().contains("SUPV")) {
			return true;
		} else {
			for (int index : verbTagIndex) {
				if (!row.getCell(index).getStringCellValue().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAdjective(Row row) {
		for (int index : adjectiveTagIndex) {
			if (!row.getCell(index).getStringCellValue().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean isAdverb(Row row) {
		for (int index : adjectiveTagIndex) {
			if (!row.getCell(index).getStringCellValue().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean isNoun(Row row) {
		if (row.getCell(tagsIndex).getStringCellValue().contains("Noun")) {
			return true;
		}
		return false;
	}

	protected String buildToken(String[] tokens) {
		return null;
	}

	protected double extractScore(String[] tokens) {
		return (Double) null;
	}
}
