package xExtraClass;

/**
 * Last modification 25 February 2013
 */

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
//import edu.stanford.nlp.objectbank.TokenizerFactory; **by new version 
import edu.stanford.nlp.process.CoreLabelTokenFactory; 
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import shared.Document;
import shared.PorterStemmer;
import shared.Word;
import shared.WordList;

public class TextPreprocessor_Porter {
	private MaxentTagger taggerModel;
	private PorterStemmer stemmer;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public TextPreprocessor_Porter() {
		initializeTagger();
		stemmer = new PorterStemmer();
	}

	private void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String stem(Word w) {
		return stemmer.stem(w.getWord());
	}

	public WordList exteractWordList(Document document)
			throws IOException {
		List<WordList> sentences = getSentencesFromDocument(document);
		getSentenceCount(document, sentences);
		return  getListOfWordWithCounts(sentences);
	}

	public List<WordList> getSentencesFromDocument(Document document) throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tagedSentence = getTaggedSentence(sentence);
			WordList wordList = convertTagedsentenceToWordList(tagedSentence);
			sentences.add(wordList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessing(Document document) throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String absoluteDocumentName = document.getPath() + "/"
				+ document.getName();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(absoluteDocumentName), "utf-8"));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				bufferReader);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	private List<TaggedWord> getTaggedSentence(List<HasWord> sentence) {
		return taggerModel.tagSentence(sentence);
		// List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
		// return tagSentence;
	}

	private WordList convertTagedsentenceToWordList(List<TaggedWord> tagSentence) {
		WordList wordsList = new WordList();
		for (TaggedWord taggedWord : tagSentence) {
			wordsList.add(convertTaggerWordToWord(taggedWord));
		}
		return wordsList;
	}

	private Word convertTaggerWordToWord(TaggedWord taggedWord) {
		Word word = new Word();
		word.setWord(getStemmedToken(taggedWord));
		word.setTag(taggedWord.tag());
		return word;
	}

	private String getStemmedToken(TaggedWord taggedWord) {
		String token = (isVerbThatNeedsToStem(taggedWord.tag())) ? stemmer
				.stem(taggedWord.word()) : taggedWord.word();
		return token;
	}

	private boolean isVerbThatNeedsToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	public void getSentenceCount(Document document, List<WordList> sentences) {// ?
		int totalSentencesCount = sentences.size();
		document.setTotalSentencesCount(totalSentencesCount);
	}

	public WordList getListOfWordWithCounts(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word newWord : sentence.getWords()) {
				String word = newWord.getWord().toLowerCase(); // Dec 9
				String tag = newWord.getTag();
				if (!containWords(globalWordList, word, tag)) {
					word = editingMisspellingWords(word, tag); // Dec 9
					globalWordList.add(new Word(word, tag, 1));
				}
			}
		}
		return globalWordList;
	}
	
	private String editingMisspellingWords(
			String word, String tag) { // dic   //Dec 9 
			if (word.equalsIgnoreCase("ca") && tag.equals("Modal")) {
				return "can";
			} else if (word.equals("wo") && tag.equals("Modal")) {
				return "will";
			} else if (word.equals("'s")  && tag.equals("Verb_Z"))  { 
			
				return "is";
			}
			return word;
	}

	private boolean containWords(WordList globalWordList, String token,
			String tag) {// ?
		Word word = new Word(token, tag);
		for (Word word2 : globalWordList.getWords()) {
			if (word2.equal(word)) {
				word2.setCount(word2.getCount() + 1);
				return true;
			}
		}
		return false;
	}

}
