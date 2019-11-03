package lexicon;

/**
 * Non POS tagged lexicon
 * Binary lexicon
 * Last modification 21 March 2013
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import shared.Utility;

public class NRC_Different extends Lexicon implements Serializable {

	private static final long serialVersionUID = 6808724589886806796L;
	private String lexiconFile = "Lexicons/NRC-emotion-lexicon-wordlevel-v0.92.txt";
	private List<String> strPosRecordList;
	private List<String> strNegRecordList;

	public NRC_Different() {
		posedLexicon = false;
		strPosRecordList = new ArrayList<String>();
		strNegRecordList = new ArrayList<String>();
		buildLexicon(lexiconFile);
		for (String string : strNegRecordList) {
			if (strPosRecordList.contains(string)) {
				System.out.println(string);
			}
		}
		Utility.writeListInFile(strPosRecordList, "../../Results/2014/Jan/12/",
				"NRC_ListPOS2.csv");
		Utility.writeListInFile(strNegRecordList, "../../Results/2014/Jan/12/",
				"NRC_ListNeg2.csv");

	}

	protected String buildToken(String[] tokens) {
		return tokens[0].toLowerCase();
	}

	protected double extractScore(String[] tokens) {
		if (isPositive(tokens)) {
			strPosRecordList.add(tokens[0]);
			return (double) 1;
		} else if (isNegative(tokens)) {
			strNegRecordList.add(tokens[0]);
			return (double) -1;
		} else {
			return (double) 0;
		}
	}

	private boolean isPositive(String[] tokens) {
		return (tokens[1].equals("positive") && tokens[2].equals("1"));
	}

	private boolean isNegative(String[] tokens) {
		return (tokens[1].equals("negative") && tokens[2].equals("1"));
	}

}
