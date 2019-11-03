import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Training_Main_good {
	

	static int totalSentences = 0;
	static PrintWriter pw;
	static PorterStemmer stemmer = new PorterStemmer();;
	
	public static void main(String[] args) throws Exception {
		
		MaxentTagger taggerModel = new MaxentTagger("TaggerModels/english-bidirectional-distsim.tagger");
		
		pw = new PrintWriter(new OutputStreamWriter(System.out,"utf-8"), true);

		List<Class<? extends Lexicon>> sentimentLexicons = new ArrayList<Class<? extends Lexicon>>();
		addSentimentalLexiconsToLexiconsList(sentimentLexicons);

		
		String trainingSetPath = "C:/Master_Project/Programming/Java_Project/TrainingSet";
		List<Document> trainingDocuments = DirectoryUtil.getListOfDocument(trainingSetPath);
		boolean truePolarityOfTrainingSet = false;

		File resultFileOfDocuments = new File ("Result/result_test.csv");
		if (! resultFileOfDocuments.exists()) {
			resultFileOfDocuments.createNewFile();
		}
		BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(resultFileOfDocuments));
		
		List<CaseBase> caseBaseSet = new ArrayList<CaseBase> ();
		for (Document doc : trainingDocuments) {
			Map <String , Pair<Integer, Double>> opinionatedWords = new HashMap <String, Pair<Integer, Double>> ();
			Map <String , Integer> allTaggs = new HashMap <String, Integer> ();
			Map <String , Integer> allWords = new HashMap<String , Integer> ();
			
			DocumentPreprocessor documentPreprocessor = applyPOSTaggerOnDocument(doc);
			extractListOfOpinionatedWordsAndAllWordsAndAllTaggs(documentPreprocessor, taggerModel, opinionatedWords, allWords, allTaggs);
		
			pw.println("All Case Base : ");
			List<Class<? extends Lexicon>> caseSolution = CaseBasePopulating.findTrueLexicon(opinionatedWords, truePolarityOfTrainingSet, sentimentLexicons);
			if (! caseSolution.isEmpty()) {
				CaseDescription caseDescription = new CaseDescription();
				caseDescription.setFeatures(allWords, totalSentences);
				CaseBase caseBase = new CaseBase(doc.getName(), caseDescription, caseSolution);
				caseBaseSet.add(caseBase);
				pw.println(caseBase.getCaseName()+"  "+caseBase.getCaseSolution().toString()); //+" \n"+ Arrays.toString(caseBase.getCasedescription()));
				doc.setPolarity(truePolarityOfTrainingSet);
				pw.println(doc.getPolarity());

				} 
			else {
				doc.setPolarity(! truePolarityOfTrainingSet);
				pw.println(doc.getPolarity());
			}
			
			bufferWrite.write(doc.getName()+" , "+ doc.getPolarity() +" , "+caseSolution.toString());
			bufferWrite.newLine();
		}
		
		bufferWrite.close();
		for (CaseBase cb : caseBaseSet){
			pw.println(cb.getCaseName()+"  "+cb.getCaseSolution().toString()+" \n"+ Arrays.toString(cb.getCasedescription().getFeatures()));
		}
		pw.println("*********");
		caseBaseSetSerialization.serialize(caseBaseSet, "tt.ser");
		List<CaseBase> cb1 = caseBaseSetSerialization.deSerialize("tt.ser");
		for (CaseBase cb : cb1) {
			System.out.println(cb.getCaseName()+"  "+cb.getCaseSolution().toString()+" \n"+ Arrays.toString(cb.getCasedescription().getFeatures()));
		}
	
	}
	
	private static void extractListOfOpinionatedWordsAndAllWordsAndAllTaggs (DocumentPreprocessor documentPreprocessor , MaxentTagger tagger, Map <String , Pair<Integer, Double>> opinionatedWords, Map <String , Integer> words , Map <String , Integer> taggs ) throws UnsupportedEncodingException {
		for (List<HasWord> sentence : documentPreprocessor ) {
			totalSentences ++;
			List<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
//			pw.println (Sentence.listToString(taggedSentence, false));
			addAdjectiveAndVerbToOpinionatedMap(taggedSentence, opinionatedWords);
		    addListOfWordsToWordsMap(taggedSentence, words);
		    addListOfTaggsToTaggsMap(taggedSentence, taggs);
		}
	}	
	
	private static void addSentimentalLexiconsToLexiconsList (List<Class<? extends Lexicon>> sentimentLexicons) {
		sentimentLexicons.add(SentiWordNet.class);
		sentimentLexicons.add(MSOL.class);
	}
	
	private static void addAdjectiveAndVerbToOpinionatedMap (List<TaggedWord> tSentence, Map <String , Pair<Integer, Double>> opinionatedWords) {
		for (TaggedWord taggedWord : tSentence) {
			if (isSentimentalTagg(taggedWord.tag())) {
				String word = (isVerbTaggNeedToStem(taggedWord.tag())) ? stemmer.stem(taggedWord.word()) : taggedWord.word();
				word +="#"+extracTaggType(taggedWord.tag());
				int count = (opinionatedWords.containsKey(word)) ? opinionatedWords.get(word).getCount() + 1 : 1;
				opinionatedWords.put(word, new Pair<Integer, Double>(count, (double) 0));
			}
		}
	}
	
	private static boolean isSentimentalTagg (String posTagg) {
		return (posTagg.contains("VB") || posTagg.contains("JJ") );//|| posTagg.equals("RB")|| posTagg.equals("RBR") || posTagg.equals("RBS")) ; 
	}
	
	private static boolean isVerbTaggNeedToStem (String tagg) {
		return  (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg.equals("VBZ")); 
	}
	
	private static void addListOfWordsToWordsMap (List<TaggedWord> tSentence , Map <String , Integer> words) {
		for (TaggedWord taggedWord : tSentence) {
			String word = (isVerbTaggNeedToStem(taggedWord.tag())) ? stemmer.stem(taggedWord.word()) : taggedWord.word();
//			pw.println(taggedWord.word()+"   "+taggedWord.tag()+ " = " +extracTaggType(taggedWord.tag()));
			int count = (words.containsKey(word)) ? words.get(word) + 1 : 1;
			words.put(word, count);
		}
	}
	
	private static void addListOfTaggsToTaggsMap (List<TaggedWord> tSentence , Map <String , Integer> taggs) {
		for (TaggedWord taggedWord : tSentence) {
			String tagg = extracTaggType(taggedWord.tag());
			int count = (taggs.containsKey(tagg)) ? taggs.get(tagg) + 1 : 1;
			taggs.put(tagg, count);
		}
	}
	
	private static DocumentPreprocessor applyPOSTaggerOnDocument(Document document)
			throws ClassNotFoundException, IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String absoluteDocName = document.getPath()+"/"+document.getName(); 
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(absoluteDocName), "utf-8"));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}
	
	private static String extracTaggType (String tagg) {
		String taggType = (tagg.matches("[\\$,\\.:#]") || tagg.endsWith("RB-") || tagg.equals("\''") || tagg.matches("\\``") || tagg.equals("\"")) ? "PUNCT" : tagg;
		return POSType.valueOf(taggType).getName();
	}
}

//pw.println (sentence.size());
//pw.println (Sentence.listToString(tSentence, false));