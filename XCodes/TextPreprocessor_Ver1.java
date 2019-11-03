package xExtraClass;

/**
 * Last modification 25 February 2013
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import TextProcess.Stemmer;


import shared.Document;
import shared.TextProcessor;
import shared.Word;
import shared.WordList;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public abstract class TextPreprocessor_Ver1 implements TextProcessor  {
	private MaxentTagger taggerModel;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public TextPreprocessor_Ver1(Stemmer stemmerName) {
		initializeTagger();
	}

	protected void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract String stem(String word);

	public WordList extractWordList(Document document)
			throws IOException {
		List<WordList> sentences = getSentencesFromDocument(document);
		getSentenceCount(document, sentences);
		return getListOfWordWithCounts(sentences);
//		WordList wordsList = getListOfWordWithCounts(sentences);
//		document.setWordsList(wordsList);
	}

	protected List<WordList> getSentencesFromDocument(Document document) throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tagedSentence = getTaggedSentence(sentence);
			WordList wordList = convertTagedsentenceToWordList(tagedSentence);
			sentences.add(wordList);
		}
		return sentences;
	}

	protected DocumentPreprocessor documentPreprocessing(Document document) throws IOException {
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

	protected List<TaggedWord> getTaggedSentence(List<HasWord> sentence) {
		return taggerModel.tagSentence(sentence);
	}

	protected WordList convertTagedsentenceToWordList(List<TaggedWord> tagSentence) {
		WordList wordsList = new WordList();
		for (TaggedWord taggedWord : tagSentence) {
			wordsList.add(convertTaggerWordToWord(taggedWord));
		}
		return wordsList;
	}

	protected Word convertTaggerWordToWord(TaggedWord taggedWord) {
		Word word = new Word();
		word.setWord(getStemmedToken(taggedWord));
		word.setTag(taggedWord.tag());
		return word;
	}

	protected String getStemmedToken(TaggedWord taggedWord) {
		String token = (isVerbThatNeedsToStem(taggedWord.tag())) ? stem(taggedWord.word()) : taggedWord.word();
		return token;
	}

	protected boolean isVerbThatNeedsToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	protected void getSentenceCount(Document document, List<WordList> sentences) {// ?
		int totalSentencesCount = sentences.size();
		document.setTotalSentencesCount(totalSentencesCount);
	}

	protected WordList getListOfWordWithCounts(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word newWord : sentence.getWords()) {
				String word = newWord.getWord().toLowerCase();
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
			} else if (word.equals("'s") && tag.equals("Verb_Z")) {
				return "is";
			} else if (word.equals("'m") && tag.equals("Verb_P")){
				return "am";
			}else if (word.equals("doe") && tag.equals("Verb_Z")){
				return "does";
			}
			return word;
	}
	
	protected boolean containWords(WordList wordList, String token,
			String tag) {// ?
		Word word = new Word(token, tag);
		for (Word word2 : wordList.getWords()) {
			if (word2.equal(word)) {
				word2.setCount(word2.getCount() + 1);
				return true;
			}
		}
		return false;
	}

}
