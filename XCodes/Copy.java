import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Copy {
	
	static Map <String , Pair<Integer, Double>> opinionatedWords = new HashMap <String, Pair<Integer, Double>> ();
	static Map <String , Integer> allPOSFrequency = new HashMap <String, Integer> ();
	
	public static void POSTaggerADocument(Document document)
			throws ClassNotFoundException, IOException {

		MaxentTagger tagger = new MaxentTagger(
				"Taggers/english-bidirectional-distsim.tagger");
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(document.getName()), "utf-8"));
		
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,
				"utf-8"), true);
		Map <String , Integer> words = new HashMap<String , Integer> ();
		double[] caseFeatures = new double[20];
		Arrays.fill(caseFeatures, 0);
		for (List<HasWord> sentence : documentPreprocessor) {
			 List<TaggedWord> tSentence = tagger.tagSentence(sentence);
			 CreateParameters.addToOpinionatedMap(tSentence, opinionatedWords);
			 CreateParameters.addToWordsMap( tSentence, words);
			 pw.println(sentence.size());
			 pw.println(Sentence.listToString(tSentence, false));
		}
		
		printlnOpinionatedMap(opinionatedWords, pw);
		pw.println(words.toString());

	}
	
	public static void printlnOpinionatedMap (Map<String, Pair<Integer, Double>> OpinionatedWordMap , PrintWriter pw  ) {
		for (Map.Entry<String, Pair<Integer, Double>> entry : OpinionatedWordMap.entrySet())
			pw.println("Word : " + entry.getKey() + " count :   "
					+ entry.getValue().getCount());
	}
}

 
