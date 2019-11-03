package xExtraClass;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;

public class TokenizeTest {

	private static MaxentTagger taggerModel;
	private final static String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	private static LexicalizedParser lp;

	public static void main(String[] args) throws IOException {
		// Initialize the tagger
		initializeTagger();
		// Initialize the parser
		initializeLexicalizedParser();
		
		String fileName= "../../TrainingSet/Test/unisen.txt";
		processSentence(fileName);

	}
	private static void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initializeLexicalizedParser() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	    String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
	    lp = LexicalizedParser.loadModel(grammar, options);
	}
	
	private static DocumentPreprocessor documentPreprocessing(String fileName)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), "UTF-8"));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				bufferReader);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}
	
	public static void processSentence(String fileName)
			throws IOException {
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(fileName);
		for (List<HasWord> sentence : documentPreprocessor) {  
			Tree tree = lp.parse(sentence);
			tree.pennPrint();
			System.out.println("POS Tagger: "+ taggerModel.tagSentence(sentence).toString());
		}
	}
	
}
