package methods_tester;

import java.io.IOException;
import java.util.List;

import lexicon.Lexicon;
import nlp.text_processor.Document;
import utilities.DirectoryUtil;
import utilities.Serialization;
import cbr.Case;
import cbr.CaseBase;
import classifier.LexiconBasedClassifier;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class Test {
	private static LexicalizedParser parser;

	public static void main(String[] args) throws Throwable, IOException {
		initializeLexicalizedParser();
		String strPath = "../../DataSet/Test/3/";
		List<Document> documentList = DirectoryUtil.getListOfDocument(strPath,
				false);
		for (Document document : documentList) {
			TestDisPOSTagFile.isIncorrectPOSTag(document, parser);
		}
	}

	static void initializeLexicalizedParser() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		parser = LexicalizedParser.loadModel(grammar, options);
	}

	void testLexicons() throws Throwable {
		String fileName = "All-test2.ser";
		String path = "../../Results/2014/Train/Jan/";
		try {
			Document testDocument = new Document(fileName, path, false);
			CaseBase cb = (CaseBase) Serialization.deSerialize(fileName);
			System.out.println(cb.getCases().size());
			List<Case> cases = cb.getCases();
			Case case1 = cases.get(0);

			List<Lexicon> lexicons = case1.getCaseSolution().getLexicons();
			LexiconBasedClassifier cls = new LexiconBasedClassifier();
			double score = 0;
			for (Lexicon lexicon : lexicons) {
				score += cls.calculateDocumentScore(testDocument, lexicon);
			}
			System.out.println(testDocument.getName() + " ==> " + score);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
