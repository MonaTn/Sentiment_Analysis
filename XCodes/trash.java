import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import shared.CaseBase;
import shared.Document;
import xTestUtil.LexiconAndFrequency;

import Lexicon.Lexicon;
import TestUtil.ValueComparator;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class trash {
	 
	static Map<String, Integer> allPOSFrequency = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws Exception {
	

	}
	private static double computeEuclideanDistance(Document document,
			CaseBase caseBase) {
		double[] documentFeatures = document.getCaseDescription().getFeatures();
		double[] caseBaseFeatures = caseBase.getCaseDescription().getFeatures();
		double sum = 0;
		for (int i = 0; i < documentFeatures.length; i++) {
			sum += Math.pow(documentFeatures[i] - caseBaseFeatures[i], 2.0);
		}
		return Math.sqrt(sum);
	}

	private static List<LexiconAndFrequency> ranking(
			List<CaseBase> retrievedCaseBase) {
		List<LexiconAndFrequency> lexiconAndFrequency = new ArrayList<LexiconAndFrequency>();
		for (CaseBase cb : retrievedCaseBase) {
			for (Class<? extends Lexicon> lexicon : cb.getCaseSolution()) {
				buildListOfRetrievedLexicon(lexicon, lexiconAndFrequency);
			}

		}
		return lexiconAndFrequency;
	}
/*
 * 	   public static void sortingMap(Map<Class<? extends Lexicon>, Integer> wordMap) {

	        ValueComparator bvc =  new ValueComparator(wordMap);
	        TreeMap<Class<? extends Lexicon>,Integer> sorted_map = new TreeMap<Class<? extends Lexicon>,Integer>(bvc);

//	        System.out.println("unsorted map: "+wordMap);

	        sorted_map.putAll(wordMap);

//	        System.out.println("results: "+sorted_map);
	        int count =5;
	        for(Map.Entry<Class<? extends Lexicon>,Integer> entry : sorted_map.entrySet()) {
	          	  if (count-- > 0) 
	          		  System.out.println(entry.getKey().toString() + " => " + entry.getValue());
	          	  else break;
	        	}
	    }
	   
	   private static void selectCS (Map<Class<? extends Lexicon>, Integer> map) {
		   List<Class <? extends Lexicon>> caseSolution = new ArrayList<Class <? extends Lexicon>>();
		   for (Map.Entry<Class<? extends Lexicon>, Integer> entry : map.entrySet()) {
			   
		   }
	   }
}


class ValueComparator implements Comparator<Class<? extends Lexicon>> {

    Map<Class<? extends Lexicon>, Integer> base;
    public ValueComparator(Map<Class<? extends Lexicon>, Integer> base) {
        this.base = base;
    }

    public int compare(Class<? extends Lexicon> a, Class<? extends Lexicon> b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
    	
 */
	
	
	
	
	
	/** population case base
	 * 	private static boolean predictPolarity(Document document, Lexicon lexicon,
				MaxentTagger taggerModel) throws ClassNotFoundException,
				IOException {
			Map<String, WordProperties> opinionatedWords = extractVerbAndAdjective(
					document, taggerModel);
			double score = computeTotalScoreOfOpinionatedWords(opinionatedWords,
					lexicon);
			printlnOpinionatedMap(opinionatedWords);
			pw.println("Lexicon is " + lexicon.toString() + "   Score is : "
					+ score);
			return (score > 0) ? true : false;
		}// +

		// think about as a public method for both test & train 
		private static Map<String, WordProperties> extractVerbAndAdjective(
				Document document, MaxentTagger taggerModel)
				throws ClassNotFoundException, IOException {
			Map<String, WordProperties> opinionatedWords = new HashMap<String,WordProperties>();
			DocumentPreprocessor documentPreprocessor = documentPreprocessing(document); // ??????
			for (List<HasWord> sentence : documentPreprocessor) {
				List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
				for (TaggedWord taggedWord : tagSentence) {
					if (isSentimentalTagg(taggedWord.tag())) {
						String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer.stem(taggedWord.word()) : taggedWord.word();
						int count = (opinionatedWords.containsKey(word)) ? opinionatedWords
								.get(word).getCount() + 1 : 1;
						opinionatedWords.put(word, new WordProperties(extracTaggType(taggedWord.tag()),count,(double) 0));
					}
				}
			}
			return opinionatedWords;
		}
		
		private static double computeTotalScoreOfOpinionatedWords(
				Map<String, WordProperties> opinionatedWords, Lexicon lexicon)
				throws UnsupportedEncodingException {
			setScoresOfEachOpinionatedWord(opinionatedWords, lexicon);
			double totalScore = (double) 0;
			for (WordProperties valuePair : opinionatedWords.values()) {
				totalScore += valuePair.getScore() * (double) valuePair.getCount();
			}
			return totalScore;
		}

		private static void setScoresOfEachOpinionatedWord(
				Map<String,WordProperties> opinionatedWords, Lexicon lexicon)
				throws UnsupportedEncodingException {
			for (Map.Entry<String, WordProperties> entry : opinionatedWords
					.entrySet()) {
				String tagg = lexicon.taggAdapter(entry.getValue().getTagg());
				pw.println(tagg);
				double score = lexicon.extractScore(entry.getKey(), tagg);
				entry.setValue(new WordProperties(tagg, entry.getValue().getCount(), score));
			}
		}
		
		private static boolean isSentimentalTagg(String posTagg) {
			return (posTagg.contains("VB") || posTagg.contains("JJ"));
		}
		
		private static CaseDescription computeCaseDescription(Document document,
				MaxentTagger taggerModel) throws ClassNotFoundException,
				IOException {
			Map<String, Integer> allTaggs = new HashMap<String, Integer>();
			Map<String, Integer> allWords = new HashMap<String, Integer>();

			DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
			int totalSentences = 0;

			for (List<HasWord> sentence : documentPreprocessor) {
				List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
				// pw.println (Sentence.listToString(tagSentence, false));
				totalSentences++;
				addWordsToWordsMap(tagSentence, allWords);
				addTaggsToTaggsMap(tagSentence, allTaggs);
			}
			CaseDescription caseDescription = new CaseDescription();
			caseDescription.setFeatures(allWords, allTaggs, totalSentences);
			// pw.println(allWords.toString());
			// pw.println(allTaggs.toString());
			// pw.println (Arrays.toString(caseDescription.getFeatures()));
			return caseDescription;
		}// ++++

		private static void addWordsToWordsMap(List<TaggedWord> tagSentence,
				Map<String, Integer> words) {
			for (TaggedWord taggedWord : tagSentence) {
				String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer
						.stem(taggedWord.word()) : taggedWord.word();
				// pw.println(taggedWord.word()+"   "+taggedWord.tag()+ " = "
				// +extracTaggType(taggedWord.tag()));
				int count = (words.containsKey(word)) ? words.get(word) + 1 : 1;
				words.put(word, count);
			}
		}

		private static void addTaggsToTaggsMap(List<TaggedWord> tagSentence,
				Map<String, Integer> taggs) {
			for (TaggedWord taggedWord : tagSentence) {
				String tagg = extracTaggType(taggedWord.tag());
				int count = (taggs.containsKey(tagg)) ? taggs.get(tagg) + 1 : 1;
				taggs.put(tagg, count);
			}
		}

		private static DocumentPreprocessor documentPreprocessing(Document document)
				throws ClassNotFoundException, IOException {
			TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
					new CoreLabelTokenFactory(), "untokenizable=noneKeep");
			String absoluteDocName = document.getPath() + "/" + document.getName();
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(absoluteDocName), "utf-8"));
			DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
					bufferReader);
			documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
			return documentPreprocessor;
		}

		private static boolean isVerbNeedToStem(String tagg) {
			return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
					.equals("VBZ"));
		}

		private static String extracTaggType(String tagg) {
			String taggType = (tagg.matches("[\\$,\\.:#]") || tagg.endsWith("RB-")
					|| tagg.equals("\''") || tagg.matches("\\``") || tagg
					.equals("\"")) ? "PUNCT" : tagg;
			return POSType.valueOf(taggType).getName();
		}
		 private static void printlnOpinionatedMap(
				 Map<String, WordProperties> OpinionatedWordMap)
				 throws UnsupportedEncodingException {
				 PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,
				 "utf-8"), true);
				
				 for (Map.Entry<String, WordProperties> entry : OpinionatedWordMap
				 .entrySet())
				 pw.printf("\n Word : %25s  count : %5d Score = %3.2f ", entry
				 .getKey()+"#"+ entry.getValue().getTagg(),entry.getValue().getCount(), entry.getValue()
				 .getScore());
				
				 }
	 */
	// pw.println (sentence.size());
	// pw.println (Sentence.listToString(tSentence, false));
	
	
	private static Map<String, WordProperties> extractVerbAndAdjective(
			Document document, MaxentTagger taggerModel)
			throws ClassNotFoundException, IOException {
		Map<String, WordProperties> opinionatedWords = new HashMap<String,WordProperties>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document); // ??????
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
			for (TaggedWord taggedWord : tagSentence) {
				if (isSentimentalTagg(taggedWord.tag())) {
					String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer.stem(taggedWord.word()) : taggedWord.word();
					int count = (opinionatedWords.containsKey(word)) ? opinionatedWords
							.get(word).getCount() + 1 : 1;
					opinionatedWords.put(word, new WordProperties(extracTaggType(taggedWord.tag()),count,(double) 0));
				}
			}
		}
		return opinionatedWords;
	}
	
	private static void setScoresOfEachOpinionatedWord(
			Map<String,WordProperties> opinionatedWords, Lexicon lexicon)
			throws UnsupportedEncodingException {
		for (Map.Entry<String, WordProperties> entry : opinionatedWords
				.entrySet()) {
			String tagg = lexicon.taggAdapter(entry.getValue().getTagg());
			double score = lexicon.extractScore(entry.getKey(), tagg);
			entry.setValue(new WordProperties(tagg, entry.getValue().getCount(), score));
		}
	}	

private void method () {
	int[] a = {1,2,3,4,5,6,7};
	int[] c = a;
	int[] b = Arrays.copyOf(a, a.length);
	a[1] = 22; 
//	b = Arrays.copyOf(a, a.length);
	System.out.println(Arrays.toString(a));
	System.out.println(Arrays.toString(b));

	System.out.println(Arrays.toString(c));
	
	Integer count = 1;
	Integer d = Integer.valueOf(count+1);
	System.out.println(d +""+ count);
}
//String[] subTrainingPath = {"/Movie/POSitive", "/Movie/Negative", "/Hotel", "/Electronics"};
//for (String subPath : subTrainingPath) {
//	String path = trainingSetPath + subPath;
//	List<Document> trainingDocumentsSet = DirectoryUtil
//			.getListOfDocument(path);
//	caseBaseSet.addAll(PopulationTheCaseBase.buildCaseBaseSet(trainingDocumentsSet, sentimentLexicons, taggerModel));
//}

  
  
  // PRIVATE //

  //Use Java's logging facilities to record exceptions.
  //The behavior of the logger can be configured through a
  //text file, or programmatically through the logging API.
//  private static final Logger fLogger =
//    Logger.getLogger(Xtest.class.getPackage().getName());
} 

//		Map <String , Pair<Integer, Double>> opinionatedWords = new HashMap <String, Pair<Integer, Double>> ();
//		Map <String , Integer> allPOSFrequency = new HashMap <String, Integer> ();
//		Document doc = new Document();
//		doc.setName("c.txt");
//		doc.setPolarity(true);
//		DocumentPreprocessor documentPreprocessor = PartOfSpeachTagger.applyPOSTagger(doc);
//
//		Map <String , Integer> words = new HashMap<String , Integer> ();
//		MaxentTagger tagger = new MaxentTagger(
//				"Taggers/english-bidirectional-distsim.tagger");
//		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,
//				"utf-8"), true);
////		int totalTokensCount = 0 ;
////		int totalSentencesCount = 0;
//		for (List<HasWord> sentence : documentPreprocessor) {
////			 totalSentencesCount ++;
////			 totalTokensCount += sentence.size();
//			 List<TaggedWord> tSentence = tagger.tagSentence(sentence);
////			 CreateParameters.addToOpinionatedMap(tSentence, opinionatedWords);
////			 CreateWordsMap.addWords(tSentence, words);
////			 pw.println(sentence.size());
////			 pw.println(Sentence.listToString(tSentence, false));
//		}
////		printlnOpinionatedMap(opinionatedWords, pw);
////		pw.println(words.toString());
//		List<Class<? extends Lexicon>> sentimentLexicons = new ArrayList<Class<? extends Lexicon>>();
////		CreateParameters.addToLexiconsSet(sentimentLexicons);
//		List<Class<? extends Lexicon>> caseSolution = CaseBasePopulating.findTrueLexicon(opinionatedWords, doc.getPolarity(), sentimentLexicons);
//		pw.println("Size of CaseSolution = "+caseSolution.size());
//		
//
//	}
//	
//}	
///*	
//	
//	
//		private static void printlnOpinionatedMap (Map<String, Pair<Integer, Double>> OpinionatedWordMap ) throws UnsupportedEncodingException {
//		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,"utf-8"), true);
//
//		for (Map.Entry<String, Pair<Integer, Double>> entry : OpinionatedWordMap.entrySet())
//			pw.println("Word : " + entry.getKey() + " count :   "
//					+ entry.getValue().getCount()+" Score = "+ entry.getValue().getScore()) ;
//		
//	}
//	
	
// public static double[] testMethod(double[] features) {
//	 features[0]++;
//	 features[3]++;
//	 return features; 
// }
	
//	 public static  void    buildPOSTagMap(List<TaggedWord>  tSentence){
//          Map <String , Integer> allPOSFrequency = new HashMap <String, Integer> ();
//		  for (TaggedWord taggedWord : tSentence) {
//				int count = (allPOSFrequency.containsKey(taggedWord.tag())) ? allPOSFrequency.get(taggedWord.tag()) + 1 : 1;
//				allPOSFrequency.put(taggedWord.tag(), count);
//		  }
//	 } 
//	 
//	public static void classifier (List<TaggedWord> tSentence) {
//		Map <String , Integer> allPOSFrequency = new HashMap <String, Integer> ();
//		for (TaggedWord taggedWord : tSentence) {
//			int count = (allPOSFrequency.containsKey(taggedWord.tag())) ? allPOSFrequency.get(taggedWord.tag()) + 1 : 1;
//			allPOSFrequency.put(taggedWord.tag(), count);
//			if (validPOSTagg(taggedWord.tag())) 
//				allPOSFrequency.put(taggedWord.tag(),count);
//		}
//	}
//	
//	@SuppressWarnings("unused")
//	private static Double roundTwoDecimals(double score) {
//		// DecimalFormat twoDForm = new DecimalFormat("#.##");
//		// return Double.valueOf(twoDForm.format(score));
//		return Double.valueOf(new DecimalFormat("#.##").format(score));
//	}
//
//	public static boolean validPOSTagg(String posTagg) {
//		return (posTagg.contains("VB") || posTagg.contains("JJ")
//				|| posTagg.equals("RB") || posTagg.equals("RBR") || posTagg
//					.equals("RBS"));
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static void Twokey4map() {
//		Pair<Integer, Double> cpn = new Pair<Integer, Double>();
//		Map<Pair<String, String>, Pair<Integer, Double>> map = new HashMap<Pair<String, String>, Pair<Integer, Double>>();
//		Map<String, Integer> map2 = new HashMap<String, Integer>();
//		cpn.setCount(1);
//		cpn.setScore(1.1);
//		map.put(new Pair("first 1", "first 2"), new Pair(1, 1.1));
//		map2.put("first", 1);
//		cpn.setCount(2);
//		cpn.setScore(2.2);
//		map.put(new Pair("second 1", "second 2"), new Pair(2, 2.2));
//		map2.put("second", 2);
//		// cps.setCount("third 1");
//		// cps.setScore("third 2");
//		// cpn.setCount(3);
//		// cpn.setScore(3.3);
//		// map.put(cps, cpn);
//		// map2.put ("third", 3);
//		Pair<String, String> cps = new Pair<String, String>();
//		cps.setCount("first 1");
//		cps.setScore("first 2");
//		if (map.containsKey(cps)) { // cps = third
//			map.get(cps).setCount(500);
//			double d = map.get(cps).getScore() + (double) 2;
//			System.out.println("d= " + d);
//		} else
//			System.out.println("NO!");
//		System.out.println(map.size());
//		System.out.println(map2.size());
//		System.out.println(map2.toString());
//		for (Map.Entry<Pair<String, String>, Pair<Integer, Double>> entry : map
//				.entrySet())
//			System.out.println("Type 1 S : " + entry.getKey().getCount()
//					+ " Type 2 s : " + entry.getKey().getScore()
//					+ " Type 1 int : " + entry.getValue().getCount()
//					+ " Type2 double : " + entry.getValue().getScore());
//	}
//
//	public static void trash() {
//		// import java.io.File;
//		// import java.io.IOException;
//		// import java.util.Scanner;
//		//
//		// import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//		//
//		//
//		// public class test {
//		//
//		// public static void main(String[] args) throws ClassNotFoundException,
//		// IOException {
//		// MaxentTagger tagger = new
//		// MaxentTagger("Taggers/english-bidirectional-distsim.tagger");
//		// // The sample string
//		//
//		// Scanner scanner = new Scanner(new File("cv000_29590.txt"));
//		// String sample = scanner.nextLine();
//		//
//		// // The tagged string
//		//
//		// String tagged = tagger.tagString(sample);
//		//
//		// // Output the result
//		//
//		// System.out.println(tagged);
//		// }
//		//
//		// }
//		// import java.util.Vector;
//		//
//		// public class test {
//		//
//		// public static void main(String[] args) {
//		//
//		// Vector<String> vc=new Vector<String>();
//		//
//		// // <E> Element type of Vector e.g. String, Integer, Object ...
//		//
//		// // add vector elements
//		// vc.add("Vector Object 1");
//		// vc.add("Vector Object 2");
//		// vc.add("Vector Object 3");
//		// vc.add("Vector Object 4");
//		// vc.add("Vector Object 5");
//		//
//		// // add vector element at index
//		// vc.add(3, "Element at fix position");
//		//
//		// // vc.size() inform number of elements in Vector
//		// System.out.println("Vector Size :"+vc.size());
//		//
//		// // get elements of Vector
//		// for(int i=0;i<vc.size();i++)
//		// {
//		// System.out.println("Vector Element "+i+" :"+vc.get(i));
//		// }
//		// }
//		// }
//
//	}
//}*/
	
//	int[] a = new int[2];
//	int[] b = Arrays.copyOf(a, a.length);
////	b = Arrays.copyOf(a, a.length);
//	System.out.println(b);
//	
	
	public static void printlnOpinionatedMap (Map<String, Pair<Integer, Double>> OpinionatedWordMap , PrintWriter pw  ) {
		for (Map.Entry<String, Pair<Integer, Double>> entry : OpinionatedWordMap.entrySet())
			pw.println("Word : " + entry.getKey() + " count :   "
					+ entry.getValue().getCount() + " Score : "
					+ entry.getValue().getScore());
	}
	public static void testMethod(List<TaggedWord> tSentence) {

		for (TaggedWord taggedWord : tSentence) {
			if (taggedWord.tag().contains("JJ")) {
				int count = (adjectiveMap.containsKey(taggedWord.word())) ? adjectiveMap
						.get(taggedWord.word()).getCount() + 1 : 1;
				Pair<Integer, Double> pair = new Pair<Integer, Double>(count,
						(double) 0);

				adjectiveMap.put(taggedWord.word(), pair);
			}

		}

	}
/*
 * 	Document document = new Document();
		document.setName("training.txt");
		document.setPolarity(true);

		MaxentTagger tagger = new MaxentTagger(
				"Taggers/english-bidirectional-distsim.tagger");
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(document.getName()), "utf-8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,
				"utf-8"), true);
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		int i = 0;
		for (List<HasWord> sentence : documentPreprocessor) {
			pw.println(sentence.size());
			List<TaggedWord> tSentence = tagger.tagSentence(sentence);
			testMethod(tSentence);
			findOpinionatedword(tSentence, OpinionatedWordMap);
			pw.println(Sentence.listToString(tSentence, false));

			i++;
		}
		pw.println(i);
		for (Map.Entry<String, Pair<Integer, Double>> entry : adjectiveMap
				.entrySet())
			pw.println("Word : " + entry.getKey() + " count :   "
					+ entry.getValue().getCount() + " Score : "
					+ entry.getValue().getScore());
				
				printlnOpinionatedMap(OpinionatedWordMap, pw);

 */
	
	
//	public static void findOpinionatedword(List<TaggedWord> tSentence,
//			Map<String, Pair<Integer, Double>> opinionatedWords) {
//		for (TaggedWord taggedWord : tSentence)
//			if (isValidPOSTagg(taggedWord.tag())) {
//				String key = taggedWord.word() + "#"
//						+ TaggTypes.adaptingTagg(taggedWord.tag());
//				int count = (opinionatedWords.containsKey(key)) ? opinionatedWords
//						.get(key).getCount() + 1 : 1;
//				Pair<Integer, Double> pair = new Pair<Integer, Double>(count,
//						(double) 0);
//				opinionatedWords.put(key, pair);
//			}
//	}

	public static boolean isValidPOSTagg(String posTagg) {
		return ( posTagg.contains("VB"));// ||
																	// posTagg.equals("RB")||
																	// posTagg.equals("RBR")
																	// ||
																	// posTagg.equals("RBS"))
																	// ;
	} 
//	List<Class<? extends Lexicon>> set = new ArrayList<Class<? extends Lexicon>>();
//	set.add(SentiWordNet.class);double[] 
//	set.add(MSOL.class);
//	List<Class<? extends Lexicon>> set1 = new ArrayList<Class<? extends Lexicon>>();
//			set1.addAll(set) ;
//	findTrueLexicon(set1);
	// System.out.println();
	// System.out.println(lexicon.getClass().getName());
	// System.out.printf("\n word =%15s tagg = %10s count = %5d score = %2.2f ",
	// word, tagg, entry.getValue().getCount(), score);
	
	
//	for (Class<? extends Lexicon> lexicon : caseSolution)
//	this.caseSolution.add(lexicon);
//List<Class<? extends Lexicon>> lexicons = new ArrayList<Class<? extends Lexicon>>();
//lexicons.add(SentiWordNet.class);
//lexicons.add(MSOL.class);
//List<Class<? extends Lexicon>> lc = new ArrayList<Class<? extends Lexicon>>();
//for (Class<? extends Lexicon> lcc : lexicons)
//	  lc.add(lcc);
//lc.add(MSOL.class);
//System.out.println(lc.toString());
//System.out.println(lexicons.toString());
//CaseBase cb = new CaseBase();
//cb.setCaseSolution(lc);
//lc.remove(MSOL.class);
//System.out.println(lc.toString());
//
//System.out.println(cb.getCaseSolution().toString());
	
//	private static void printlnOpinionatedMap (Map<String, Pair<Integer, Double>> OpinionatedWordMap ) throws UnsupportedEncodingException {
//	PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,"utf-8"), true);
//
//	for (Map.Entry<String, Pair<Integer, Double>> entry : OpinionatedWordMap.entrySet())
//		pw.printf("\n Word : %25s  count : %5d Score = %3.2f ", entry.getKey(), entry.getValue().getCount(), entry.getValue().getScore()) ;
//	
//	}
  
  public static double[] createCaseDescription(Map<String, Integer> words,
			int totalSentencesCount) {
		int stopWordsCount = 0;
		int syllableCount = 0;
		int monoSyllableCount = 0;
		int uniqueWordCount = 0;
		int totalTokensCount = 0;
		EnglishSyllableCounter syllableCounter = new EnglishSyllableCounter();
		for (Map.Entry<String, Integer> entry : words.entrySet()) {
			totalTokensCount += entry.getValue();
			if (StopWords.isStopWord(entry.getKey()))
				stopWordsCount++;
			syllableCount += syllableCounter.countSyllables(entry.getKey());
			if (syllableCounter.countSyllables(entry.getKey()) == 1)
				monoSyllableCount++;
			if (entry.getValue() == 1)
				uniqueWordCount++;
		}
		CaseDescription caseDescription = new CaseDescription();
		caseDescription.setTotalSentens((double) totalSentencesCount);
		caseDescription.setTotalTokens((double) totalTokensCount);
		caseDescription.setTotalWords((double) words.size());
		caseDescription.setAverageSentencesSize((double) totalTokensCount
				/ (double) totalSentencesCount);
		caseDescription.setStopWordsRatio((double) stopWordsCount
				/ (double) words.size());
		caseDescription.setAverageSyllablesCount((double) syllableCount
				/ (double) words.size());
		caseDescription.setMonosyllableRatio((double) monoSyllableCount
				/ (double) (words.size() - monoSyllableCount));
		caseDescription.setWordsToTokensRatio((double) words.size()
				/ (double) totalTokensCount);
		caseDescription.setUniqueWordsRatio((double) uniqueWordCount
				/ (double) words.size());
		return caseDescription.getFeatures();
	}

	public static List<Class<? extends Lexicon>> findTrueLexicon(
			Map<String, Pair<Integer, Double>> opinionatedWords,
			boolean truePolarity,
			List<Class<? extends Lexicon>> sentimentLexicons) throws Exception,
			Exception {
		List<Class<? extends Lexicon>> solutionSet = new ArrayList<Class<? extends Lexicon>>();
		for (int i = 0; i < sentimentLexicons.size(); i++) {
			Lexicon lexicon = sentimentLexicons.get(i).newInstance();
//			setScoresOfOpinionatedWords(opinionatedWords, lexicon);
//			if (obtainedPolarity(opinionatedWords) == truePolarity)
				solutionSet.add(sentimentLexicons.get(i));
		}
		// System.out.println (solution.toString());
		return solutionSet;
	}
  
	public static List<Class<? extends Lexicon>> findTrueLexicon(
				List<Class<? extends Lexicon>> sentimentLexicons) throws Exception,
			Exception {
		List<Class<? extends Lexicon>> solutionSet = new ArrayList<Class<? extends Lexicon>>();
		for (Class<? extends Lexicon> lc : sentimentLexicons) {
			Lexicon lexicon = lc.newInstance();
			solutionSet.add(lc);
		}
		 System.out.println (solutionSet.toString());
		return solutionSet;
	}
	public static boolean isVowel (char character) {
		char[] vowels = {'a','e','o','i','u','y'};
		for (char vowelChar : vowels)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			if (character == vowelChar) 
				return true;
		return false;		
	}
  @SuppressWarnings("unused")
private static void usePoint() {
	  Point a = returnCombo();
	  System.out.println (a.x +", "+a.y);
  }
  private static Point returnCombo (){
	  int a = 2;
	  int b = 3;
	  return new Point( a , b );
  }
}

/*
 * 	  	boolean  word = true;
	  	boolean vers = !word;
	  	
	  	EnglishSyllableCounter es = new EnglishSyllableCounter();
	  	System.out.println (vers);
 */
/*
 * 	  
	  	String word = "salient";
	  	EnglishSyllableCounter es = new EnglishSyllableCounter();
	  	System.out.println (es.countSyllables(word));
	  	
		char[] wordToChar = word.toCharArray();
		int syllable=0;
		int iteration = wordToChar.length;
		for (int i=0; i < iteration-1; i++) 
			 if (isVowel(wordToChar[i])) {
				if (isVowel(wordToChar[i+1]))
					i++;
				syllable++;
				}
		if (isVowel(wordToChar[iteration-1]) && wordToChar[iteration-1] != 'e')
			syllable++;
		if (syllable == 0)
			syllable = 1;
		
		System.out.println (syllable);
 */
/*	Scanner scanner = new Scanner (new File("External_Libraries/StopWords.txt"));
	while (scanner.hasNext()) {
		System.out.print("\""+scanner.next()+"\" ,");
	}*/



/*  	String path = "C:/Master_Project/Programming/Java_Project/TrainingSet";
		List<Document> documentList = new ArrayList <Document> ();
		File[] listOfFiles = DirectoryUtil.getListOfFiles(path);
		for (File file : listOfFiles) {
			if (!file.isFile()) continue;
			Document doc = new Document(file.getName(), path);
			documentList.add(doc);
		}
		for (Document doc : documentList)
			System.out.println (doc.getName()+"   "+doc.getPath());
		System.out.println (documentList.size());
		*/


//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import edu.stanford.nlp.ling.Sentence;
//import edu.stanford.nlp.ling.TaggedWord;
//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//MaxentTagger tagger = new MaxentTagger("Taggers/english-bidirectional-distsim.tagger");
//List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("sample-input.txt")));
//for (List<HasWord> sentence : sentences) {
//ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
//System.out.println(Sentence.listToString(tSentence, false));
//}


/*
String str = "greater#ADJECTIVE";
int i = str.indexOf("#");
String word = str.substring(0, i);
String tagg = str.substring(i+1, str.length());
System.out.printf("\n word = %10s tagg = %5s", word, tagg);
Stemmer stemmer = new Stemmer();
String str2 = "was";
char[] chr = str2.toCharArray();
stemmer.add(chr, chr.length);
stemmer.stem();
System.out.println();
System.out.println(stemmer.toString()); */
}

