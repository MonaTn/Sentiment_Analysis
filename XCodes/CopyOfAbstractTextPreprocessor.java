package xExtraClass;

/**
 * Last modification 25 February 2013
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public abstract class CopyOfAbstractTextPreprocessor implements TextProcessor {
	protected MaxentTagger taggerModel;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public CopyOfAbstractTextPreprocessor() {
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

	@Override
	public WordList extractWordList(Document document) throws IOException {
		List<WordList> sentences = getSentencesOfDocument(document);
		document.setTotalSentencesCount(sentences.size());
		return getListOfWordWithCounts(sentences);
	}

	protected List<WordList> getSentencesOfDocument(Document document)
			throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tagedSentence = getTaggedSentence(sentence); // **
			WordList wordList = convertTagedsentenceToWordList(tagedSentence);
			sentences.add(wordList);
		}
		return sentences;
	}

	protected DocumentPreprocessor documentPreprocessing(Document document)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String fileName = document.getAbsoluteFileName();
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				new FileReader(fileName));
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	protected List<TaggedWord> getTaggedSentence(List<HasWord> sentence) {
		return taggerModel.tagSentence(sentence);
	}

	protected WordList convertTagedsentenceToWordList(
			List<TaggedWord> tagSentence) {
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
		return (isVerbNeedsToStem(taggedWord.tag())) ? stem(taggedWord.word())
				: taggedWord.word();
	}

	protected boolean isVerbNeedsToStem(String tag) {
		return (tag.equals("VBD") || tag.equals("VBG") || tag.equals("VBN") || tag
				.equals("VBZ"));
	}

	protected WordList getListOfWordWithCounts(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word oneWord : sentence.getWords()) {
				String word = oneWord.getWord().toLowerCase();
				String tag = oneWord.getTag();
				if (!containWords(globalWordList, word, tag)) {
					word = editingMisspellingWords(word, tag); // Dec 9
					globalWordList.add(new Word(word, tag, 1));
				}
			}
		}
		return globalWordList;
	}

	protected String editingMisspellingWords(String word, String tag) { // dic
																		// //Dec
																		// 9
		if (word.equalsIgnoreCase("ca") && tag.equals("Modal")) {
			return "can";
		} else if (word.equals("wo") && tag.equals("Modal")) {
			return "will";
		} else if (word.equals("'s") && tag.equals("Verb_Z")) {
			return "is";
		} else if (word.equals("'m") && tag.equals("Verb_P")) {
			return "am";
		} else if (word.equals("doe") && tag.equals("Verb_Z")) {
			return "does";
		}
		return word;
	}

	protected boolean containWords(WordList wordList, String token, String tag) {// ?
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
