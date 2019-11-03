
public class test2 {
//	
//	Map <String , Integer> allTaggs = new HashMap <String, Integer> ();
//	Map <String , Integer> allWords = new HashMap<String , Integer> ();
//	
//	extractListOfOpinionatedWordsAndAllWordsAndAllTaggs(documentPreprocessor, taggerModel, opinionatedWords, allWords, allTaggs);
//
//	pw.println("All Case Base : ");
////	List<Class<? extends Lexicon>> caseSolution = CaseBasePopulating.findTrueLexicon(opinionatedWords, truePolarityOfTrainingSet, sentimentLexicons);
//	if (! caseSolution.isEmpty()) {
//		CaseDescription caseDescription = new CaseDescription();
//		caseDescription.setFeatures(allWords, totalSentences);
//		CaseBase caseBase = new CaseBase(document.getName(), caseDescription, caseSolution);
//		caseBaseSet.add(caseBase);
//		pw.println(caseBase.getCaseName()+"  "+caseBase.getCaseSolution().toString()); //+" \n"+ Arrays.toString(caseBase.getCasedescription()));
//		document.setPolarity(truePolarityOfTrainingSet);
//		pw.println(document.getPolarity());
//
//		} 
//	else {
//		document.setPolarity(! truePolarityOfTrainingSet);
//		pw.println(document.getPolarity());
////	}
//	
//	bufferWrite.write(document.getName()+" , "+ document.getPolarity() +" , "+caseSolution.toString());
//	bufferWrite.newLine();
//}
//
//bufferWrite.close();
//for (CaseBase cb : caseBaseSet)
//	pw.println(cb.getCaseName()+"  "+cb.getCaseSolution().toString()+" \n"+ Arrays.toString(cb.getCasedescription().getFeatures()));
//pw.println("*********");
//caseBaseSetSerialization.serialize(caseBaseSet, "tt.ser");
//List<CaseBase> cb1 = caseBaseSetSerialization.deSerialize("tt.ser");
//for (CaseBase cb : cb1) {
//	System.out.println(cb.getCaseName()+"  "+cb.getCaseSolution().toString()+" \n"+ Arrays.toString(cb.getCasedescription().getFeatures()));
//}
//
//}
//	private static void extractListOfOpinionatedWordsAndAllWordsAndAllTaggs (DocumentPreprocessor documentPreprocessor , MaxentTagger tagger, Map <String , Pair<Integer, Double>> opinionatedWords, Map <String , Integer> words , Map <String , Integer> taggs ) throws UnsupportedEncodingException {
//	for (List<HasWord> sentence : documentPreprocessor ) {
//		totalSentences ++;
//		List<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
////		pw.println (Sentence.listToString(taggedSentence, false));
//		addAdjectiveAndVerbToOpinionatedMap(taggedSentence, opinionatedWords);
//	    addListOfWordsToWordsMap(taggedSentence, words);
//	    addListOfTaggsToTaggsMap(taggedSentence, taggs);
//	}
//}	


//private static void addAdjectiveAndVerbToOpinionatedMap (List<TaggedWord> tSentence, Map <String , Pair<Integer, Double>> opinionatedWords) {
//	for (TaggedWord taggedWord : tSentence) {
//		if (isSentimentalTagg(taggedWord.tag())) {
//			String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer.stem(taggedWord.word()) : taggedWord.word();
//			word +="#"+extracTaggType(taggedWord.tag());
//			int count = (opinionatedWords.containsKey(word)) ? opinionatedWords.get(word).getCount() + 1 : 1;
//			opinionatedWords.put(word, new Pair<Integer, Double>(count, (double) 0));
//		}
//	}
//}

}
