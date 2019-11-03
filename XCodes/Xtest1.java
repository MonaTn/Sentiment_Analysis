package xTestUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lexicon.*;

import shared.CaseBase;
import shared.Serialization;
import testing.SimilarityMeasure;
import shared.*;

public class Xtest1 {
	private static double[] minValues = { 2700, 1500, 3400 };
	private static double[] maxValues = { 6700, 5400, 10500 };

	public static void main(String[] args) throws IOException {
		testNormalizeMinMaxCaseBase() ;
//		Lexicon lexicon = new GeneralInquirer();
//		System.out.println(lexicon.getScore("good", "a"));
//		lexicon.writeToFile("GI5.csv");
	}
	@SuppressWarnings("unused")
	private static double[] arrayFill() {
		double[] a = new double[5];
		System.out.println(Arrays.toString(a));
		Arrays.fill(a, 1000);
		return a;
	}
	   public static List<double[]> findMinMaxValues (double[] array1, double[] array2) {
	        double[] minArray = new double[3];
	        double[] maxArray = new double[3];
	        List<double[]> minMax = new ArrayList<double[]>();
	        for (int i = 0; i < 3; i++) {
	            minArray[i] = Math.min(array1[i], array2[i]);
	            maxArray[i] = Math.max(array1[i], array2[i]);
	        }
	        minMax.add(minArray);
	        minMax.add(maxArray);
	        return minMax;
	    }
	@SuppressWarnings("unused")
	private static void CheckAStringInAArray() {
		String[] str = {"abc", "bad","good"};
		if (Arrays.asList(str).contains("bad")) {
			System.out.println("yes");
		}
	}

	@SuppressWarnings("unused")
	private static void ArrayListChckCountain() {
		List<String> str = new ArrayList<String>();
		str.add("adjective");
		str.add("adverb");
		str.add("verb");
		str.add("example");
//		str.add("adj");
		if (str.contains("adj")) {
			System.out.println("find");
		}
	}

	@SuppressWarnings("unused")
	private static void GI_extractTag() throws FileNotFoundException,
			IOException {
		BufferedReader br = new BufferedReader(new FileReader("Lexicons/GeneralInquirer"));
		String line = ""; 
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (tokens.length == 186) {
				String[] def = tokens[185].split("\\s");
				for (String token : def) {
					if (token.contains(":")) {
						String[] tags = token.split("[-:]");
						for (String tag : tags) {
							System.out.print(tag+" , ");
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void trimTest() {
		String str = "   \nT his is   a    book  !   \n\n";
		System.out.println("**" + str + "**");
		System.out.println("**" + str.trim() + "**");
	}

	@SuppressWarnings("unused")
	private static void testSimilarityDistance() {
		double[] a1 = { 18.020477815699657, 18.28, 26.666666666666664,
				0.030120813647426754, 3.6459809206743476, 3.05927619069765,
				21.213349742995767, 10.927934534265036, 0.6253412869021855,
				4.6906906906906904, 16.086956521739133, 17.6, 10.0,
				29.25619834710744, 15.128205128205128, 17.241379310344826, 0.0 };
		double[] a2 = { 5.836177474402731, 5.306666666666667,
				7.666666666666667, 0.07675438596491227, 0.55032526619005,
				6.489953742879618, 7.600393916183407, 3.5767322764601044,
				1.7515853696758232, 1.0137115839243456, 6.608695652173912, 2.6,
				3.8333333333333335, 5.206611570247935, 4.52991452991453,
				4.482758620689655, 0.0 };
		double[] a = { 1, 2, 3, 4 };
		double[] b = { 5, 6, 7, 8 };

		double distance = SimilarityMeasure.euclideanDistance(a, b);
		System.out.println(distance);
	}

	private static void testNormalizeMinMaxCaseBase() throws IOException {
		CaseBase cb = (CaseBase) Serialization
				.deSerialize("../../Results/2013/March/Small/"
						+ "CaseBase_test_neg.ser");
		double[] min = cb.getMinValues();
		double[] max = cb.getMaxValues();
		System.out.println(Arrays.toString(min));
		System.out.println(Arrays.toString(max));

//		CaseBase ncb = Normalizer.normalizeCaseBase(cb, Nm);
//		for (Case one : ncb.getCases()) {
//			System.out.println(one.toString());
//		}
	}

	@SuppressWarnings("unused")
	private static void minMaxtest() {
		double[] a1 = { 1.1, 2.3, 5.6, 7, 89 };
		double[] a2 = { 0, -2.3, 7.8, 32, 99 };
		List<double[]> minmax = new ArrayList<double[]>();
		double[] min = new double[5];
		System.out.println(Arrays.toString(min));
		for (double[] a : minmax) {
			System.out.println(Arrays.toString(a));
		}
	}

	@SuppressWarnings("unused")
	private static void vectorTest() throws IOException {
		List<Vector<Object>> vlist = new ArrayList<Vector<Object>>();
		Vector<Object> vector = new Vector<Object>();
		vector.addElement(new String("a"));
		vector.addElement(new Double(2.6));
		vector.addElement(new Boolean(true));

		vlist.add(vector);
		vector = new Vector<Object>();

		vector.addElement("file 2");
		vector.addElement(false);
		vector.addElement(5);
		vlist.add(vector);
		for (Vector<Object> vv : vlist) {
			System.out.println(vv.toString());
		}
		writeAVectorTofile(vlist);
	}

	private static void writeAVectorTofile(List<Vector<Object>> vectors)
			throws IOException {
		// String resultPath = "../../Results/2013/feb/";
		// String resultFileName = "Pred.csv";
		// FileUtility fileUtil = new FileUtility();
		// File resultFile = fileUtil.createFile(resultPath, resultFileName);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				"k.csv"));
		for (Vector<Object> vector : vectors) {
			bufferedWriter.write(vector.toString());
			bufferedWriter.newLine();
			// fileUtil.writeToFile(vector.toString(), bufferedWriter);
		}
		bufferedWriter.close();
	}

	@SuppressWarnings("unused")
	private static void minmax() {
		int[] array = { 5300, 2450, 7560 };
		for (int i = 0; i < 3; i++) {
			double digit = Normalizer.minMaxNormalization(array[i], minValues[i],
					maxValues[i]);
			System.out.println(digit);
		}
	}

	// private int calculateScore(String token, int score,
	// Map<String, Integer> wordAndScore) {
	// if (wordAndScore.containsKey(token)) {
	// int value = wordAndScore.get(token);
	// return assignScore(score, value);
	// }
	// return score;
	// }
	//
	// private int assignScore(int score, int value) {
	// if (value == 0 || value == score) {
	// return score;
	// } else {
	// return value;
	// }
	// }

	public static void BuildSecondVersion() throws IOException {
		Pattern pattern = Pattern.compile("\\w+\tpositive\t\\d");
		String nrc = "";
		BufferedReader br = new BufferedReader(new FileReader(nrc));
		String line = "";
		while ((line = br.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				System.out.println(line);
			}
			// read10LinesBlock(br);
		}
	}

	@SuppressWarnings("unused")
	private static void caseBuilder() throws IOException {
		CaseBase ret = (CaseBase) Serialization
				.deSerialize("CaseBase_09112012_2.ser");
		// CaseSolution caseSolution = TestCaseAuthoring
		// .buildDocumentCaseSolution(ret);
		// System.out.println(caseSolution.toString());
	}

	@SuppressWarnings("unused")
	private static void lexiconSample() throws IOException {
		Lexicon newClues = new SubjectivityClues();
		LexicInterface oldClues = new Clues11();
		Utility.writeToFile("Clues2.csv", newClues.getMap());
		
		Lexicon newGI = new GeneralInquirer();
		LexicInterface oldgi = new GI();
		Utility.writeToFile("GI2.csv", newGI.getMap());
		
		LexicInterface oldSWN = new SWN();
		Lexicon newSWN = new SentiWordNet();
		Utility.writeToFile("SWN2.csv", newSWN.getMap());
		
		Lexicon newMsol = new MSOL();
		LexicInterface oldMsol = new MSOL_Old();
		Utility.writeToFile("MSOL2.csv", newMsol.getMap());
		
		Lexicon newNRC = new NRC();
		LexicInterface oldNRC = new NRC_Old();
		Utility.writeToFile("NRC2.csv", newNRC.getMap());

		System.out.println("************* New SentiWordNet ");
		System.out.println("constructive#a, "
				+ newSWN.getScore("constructive", "a"));
		System.out.println("boast#v, " + newSWN.getScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ newSWN.getScore("autocratic", "a"));

		System.out.println("*************Old SentiWordNet");
		System.out.println("constructive#a, "
				+ oldSWN.extractScore("constructive", "a"));
		System.out.println("boast#v, " + oldSWN.extractScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ oldSWN.extractScore("autocratic", "a"));
		System.out.println();

		System.out.println("*************New Subjectivity Clues");
		System.out.println("constructive#a, "
				+ newClues.getScore("constructive", "a"));
		System.out.println("boast#v, " + newClues.getScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ newClues.getScore("autocratic", "a"));

		System.out.println("*************Old Subjectivity Clues");
		System.out.println("constructive#a, "
				+ oldClues.extractScore("constructive", "a"));
		System.out.println("boast#v, " + oldClues.extractScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ oldClues.extractScore("autocratic", "a"));
		System.out.println();

		System.out.println("*************New GI");
		System.out.println("constructive#a, "
				+ newGI.getScore("constructive", "a"));
		System.out.println("boast#v, " + newGI.getScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ newGI.getScore("autocratic", "a"));
		System.out.println("*************OLD GI");
		System.out.println("constructive#a, "
				+ oldgi.extractScore("constructive", "a"));
		System.out.println("boast#v, " + oldgi.extractScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ oldgi.extractScore("autocratic", "a"));
		System.out.println();

		System.out.println("************* New MSOL");
		System.out.println("constructive#a, "
				+ newMsol.getScore("constructive", "a"));
		System.out.println("boast#v, " + newMsol.getScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ newMsol.getScore("autocratic", "a"));
		System.out.println("************* Old MSOL ");
		System.out.println("constructive#a, "
				+ oldMsol.extractScore("constructive", "a"));
		System.out.println("boast#v, " + oldMsol.extractScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ oldMsol.extractScore("autocratic", "a"));

		System.out.println();
		System.out.println("************* New NRC ********");
		System.out.println("constructive#a, "
				+ newNRC.getScore("constructive", "a"));
		System.out.println("boast#v, " + newNRC.getScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ newNRC.getScore("autocratic", "a"));
		System.out.println("************* Old NRC ");
		System.out.println("constructive#a, "
				+ oldNRC.extractScore("constructive", "a"));
		System.out.println("boast#v, " + oldNRC.extractScore("boast", "v"));
		System.out.println("autocratic#a, "
				+ oldNRC.extractScore("autocratic", "a"));

	}

	public static void exOfNormalize() {
		@SuppressWarnings("unused")
		double[] min = { 164.0, 289.0, 11.0, 19.853658536585368,
				0.21388216303470542, 0.3783231083844581, 1.3192834562697577,
				0.6292682926829268, 0.3973509933774834, 0.25184275184275184,
				41.0, 14.0, 22.0, 65.0, 36.0, 8.0, 0.0 };
		@SuppressWarnings("unused")
		double[] max = { 477.0, 986.0, 45.0, 29.0, 0.26873239436619717,
				0.524609843937575, 1.5841463414634147, 0.7755532139093783,
				0.5674740484429066, 0.4357638888888889, 133.0, 111.0, 68.0,
				238.0, 167.0, 34.0, 1.0 };
		@SuppressWarnings("unused")
		double[] f = { 431.0, 852.0, 56.0, 15.214285714285714,
				0.2326165875453784, 0.4025821596244131, 1.4870892018779343,
				0.6784037558685446, 0.505868544600939, 0.392018779342723,
				110.0, 95.0, 37.0, 215.0, 119.0, 20.0, 0.0 };
		// CaseDescription cd = new CaseDescription();
		// @SuppressWarnings("unused")
		// // Normalize normalizer = new Normalize();
		// //// cd = normalizer.minMaxNormalization(f, min, max);
		// System.out.println(Arrays.toString(cd.getFeatures()));
	}

	public void findwhitespace() throws FileNotFoundException {
		String fileName = "../../TrainingSet/SingleTrain/cv732_13092.txt";
		Scanner scanner = new Scanner(new File(fileName));
		int totalWhitespacesCount = 0;
		int totlaCharactersCount = 0;
		String line = "";
		while (scanner.hasNext()) {
			line += scanner.nextLine();
		}
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '\r' || line.charAt(i) == '\t'
					|| line.charAt(i) == ' ') {
				totalWhitespacesCount++;
			}
		}

		totlaCharactersCount += line.length();
		System.out.println(totalWhitespacesCount + "," + totlaCharactersCount);
		scanner.close();

	}
}
// Pattern patternStr1 = Pattern.compile("\\d+\\_?(\\d*|NULL)");
// Pattern patternStr2 = Pattern.compile("\\_(PROS).+");

// Matcher matcher = patternStr2.matcher(text);
// if (matcher.find()) {
// String removePart = matcher.group();
// System.out.println(removePart);
// }

// Matcher matcher1 = patternStr1.matcher(sentence);
// String documentName = "";
// if (matcher1.find()) {
// documentName = matcher1.group();
// System.out.println(documentName);
// }
// Matcher matcher2 = patternStr2.matcher(sentence);
// sentence = sentence.substring(documentName.length());
// if (matcher2.find() ){//&& matcher2.find()) {
// String text = matcher2.group();
// // System.out.println(text);
//
// }
// String[] sentences = sentence.split("\\_(PROS).+\t");
// for (String str : sentences) {
// System.out.println (str);
// }
// System.out.println("**" + sentence);