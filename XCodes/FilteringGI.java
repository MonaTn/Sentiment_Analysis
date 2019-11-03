package shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilteringGI {
	public FilteringGI() throws IOException {
		BufferedReader bufferedReader = openOriginalGIFile();
		BufferedWriter bufferedWriter = createNewGILexicon();
		String line = "";
		bufferedReader.readLine();
		while ((line = bufferedReader.readLine()) != null) {
			writeSentimentalEntriesInNewGI(bufferedWriter, line);
		}
		bufferedWriter.close();
	}

	private void writeSentimentalEntriesInNewGI(BufferedWriter bufferedWriter,
			String line) throws IOException {
		String[] lexiconCategories = line.split("\t");
		if (hasSentimentalValue(lexiconCategories)) {
			bufferedWriter.write(line);
			bufferedWriter.newLine();
		}
	}

	private BufferedWriter createNewGILexicon() throws IOException {
		String newLexiconFileName = "Lexicons/newGeneralInquirer.txt";
		File newLexiconFile = new File(newLexiconFileName);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				newLexiconFile));
		if (!newLexiconFile.exists()) {
			newLexiconFile.createNewFile();
		}
		return bufferedWriter;
	}

	private BufferedReader openOriginalGIFile() throws FileNotFoundException {
		String lexiconFile = "Lexicons/inquirerbasicttabsclean";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				lexiconFile));
		return bufferedReader;
	}

	private boolean hasSentimentalValue(String[] categories) {
		return (isContainVerb(categories) || isContainAdjective(categories) || isContainAdverb(categories));
	}

	private boolean isContainVerb(String[] categories) {
		return (categories[116].equals("IAV ")
				|| categories[117].endsWith("DAV") || categories[118]
					.equals("SV"));
	}

	private boolean isContainAdjective(String[] categories) {
		return (categories[119].equals("IPadj") || categories[120]
				.endsWith("IndAdj"));
	}

	private boolean isContainAdverb(String[] categories) {
		return (categories[184].equals("DEG"));
	}

}
