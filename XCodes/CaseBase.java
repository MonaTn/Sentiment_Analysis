package shared;

/**
 * Last modification 25 February 2013
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaseBase implements Serializable {

	private static final long serialVersionUID = -2514867103449049009L;

	private List<Case> caseBase;

	public CaseBase() {
		caseBase = new ArrayList<Case>();
	}

	public List<Case> getCases() {
		return caseBase;
	}

	public void setCaseBase(List<Case> cases) {
		this.caseBase = cases;
	}

	public void add(Case oneCase) {
		caseBase.add(oneCase);
	}

	public void add(List<Case> cases) {
		this.caseBase.addAll(cases);
	}

	
	public void writeToFile(String path, String fileNmae) throws IOException {
		File resultFileOfDocuments = new File(path + fileNmae);
		if (!resultFileOfDocuments.exists()) {
			resultFileOfDocuments.createNewFile();
		}
		BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(
				resultFileOfDocuments));
		bufferWrite.write(titre);
		bufferWrite.newLine();
		for (Case oneCase : getCases()) {
			bufferWrite.write(oneCase.toString());
			bufferWrite.newLine();
		}
		bufferWrite.close();
	}

	private String titre = "Name" + " , " + "WORDS" + " , " + "TOKENS" + " , "
			+ "SENTENCES" + " , " + "AVERAGE_SENTENCE_SIZE" + " , "
			+ "SPACE_RATIO" + " , " + "STOPWORDS_RATIO" + " , "
			+ "AVERAGE_SYLLABLES" + " , " + "MONOSYLLABLE_RATIO" + " , "
			+ "WORD_TO_TOKEN_RATIO" + " , " + "UNIQUE_WORDS_RATIO" + " , "
			+ "VERBS Counts" + " , " + "ADJECTIVE Counts" + " , "
			+ "ADVERB Counts" + " , " + "NOUNS Counts" + " , "
			+ "PUNCTUATION Counts" + " , " + "CONJUNCTION Counts" + " , "
			+ "INTERJECTION Counts" + "," + "Case Solution";
}
