package xTestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Document;

import X.Negation.copy.TextPreprocessorWithNegationFinder_1109013;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;

public class TreeTest {
	static final String fileName = "no1.txt";
	static final String path = "../../TrainingSet/Test/";
	static final boolean polarity = false;
	
	private static MaxentTagger taggerModel;
	private static final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public static void main(String[] args) {
		pennTreeTest();
//		exteractWordList();
//		exteractWordFromFile();
	}
	private static void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pennTreeTest() {
		initializeTagger();
		LexicalizedParser parser = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		for (List<HasWord> sentence : new DocumentPreprocessor(path+fileName)) {
			System.out.println("**** Sentence : " + sentence);
			Tree pennTree = parser.apply(sentence);
			System.out.println (pennTree.toString());
			System.out.println (Arrays.toString(pennTree.taggedYield().toArray()));
			System.out.println(taggerModel.tagSentence(sentence).toString());
			
		}
	}
	
	public static void exteractWordFromFile() {
		List<TaggedWord> list = new ArrayList<TaggedWord>();
		TextPreprocessorWithNegationFinder_1109013 processor = new TextPreprocessorWithNegationFinder_1109013();
		for (List<HasWord> sentence : new DocumentPreprocessor(fileName)) { // **
			System.out.println("**** Sentence : " + sentence);
			List<TaggedWord> list1 = processor.getTaggedWords(sentence);
			list.addAll(list1);
			System.out.println(" List of tagged words : "
					+ Arrays.toString(list1.toArray()));
			System.out
					.println("------------------------------------------------------------------");
		}
		System.out.println(Arrays.toString(list.toArray()));
	}

	public static void exteractWordList() {
		try {
			Document document = new Document(fileName, path, polarity);
			TextPreprocessorWithNegationFinder_1109013 processor = new TextPreprocessorWithNegationFinder_1109013();
			processor.extractWordsListOfDocument(document);
			document.getWordsList().toPrint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
