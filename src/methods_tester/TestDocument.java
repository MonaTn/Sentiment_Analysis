package methods_tester;

import java.util.Arrays;

import nlp.TextProcessor;
import nlp.TextProcessorFactory;
import nlp.text_processor.Document;
import nlp.text_processor.WordList;
import cbr.CaseDescription;

public class TestDocument {

	public static void main(String[] args) throws Throwable, Exception {
		String strFileName = "neg920.text";
		String strPath = "../../Results/2014/ErrorAnalysis/";
		Document document = new Document(strFileName, strPath, false);

		TextProcessor prs;
		TextProcessorFactory factory = new TextProcessorFactory();
		factory.setStemmer("SnowBall_Stemmer");
		prs = factory.create();
		prs.enableNegationAnalysis(false);


		WordList wl = prs.extractWordList(document);
		document.setWordsList(wl);

		CaseDescription cd = document.computeCaseDescription();
		System.out
				.println("New Document: " + Arrays.toString(cd.getFeatures()));

		// CaseDescription_2014Version oldCd = new CaseDescription_2014Version(
		// document);
		// System.out.println("Old Document: "
		// + Arrays.toString(oldCd.getFeatures()));

		// String NEW_LINE = System.getProperty("line.separator");
		// stringBuilder.append(NEW_LINE);

	}

}
