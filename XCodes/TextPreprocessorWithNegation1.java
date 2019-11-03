package xTestUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Negation.NegationElements;

import shared.Document;
import shared.PorterStemmer;
import shared.Word;
import shared.WordList;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class TextPreprocessorWithNegation1 {

	private List<String> patternsList;
	private TregexPatternCompiler macros;
	private LexicalizedParser parser = LexicalizedParser
			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	private List<Tree> listOfRestOfTrees;

	private MaxentTagger taggerModel;
	private PorterStemmer stemmer;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	private List<Tree> treesToFindNegation;
	public TextPreprocessorWithNegation1() {
		initializeTagger();
		stemmer = new PorterStemmer();
		patternsList = NegationElements.getPatternsList();
		macros = NegationElements.getMacros();
	}

	private void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String stem(String word) {
		return stemmer.stem(word);
	}

	public void extractWordsAndSentences(Document document) throws IOException {
		List<WordList> sentences = getSentencesFromDocument(document);
		getSentenceCount(document, sentences);
		WordList wordsList = getListOfWordWithCounts(sentences);
		document.setWordsList(wordsList);
	}

	public List<WordList> getSentencesFromDocument(Document document)
			throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tagedSentence = getTaggedSentence(sentence);
			WordList wordList = convertTagedsentenceToWordList(tagedSentence);
			sentences.add(wordList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessing(Document document)
			throws IOException {
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

	// private List<TaggedWord> getTaggedSentence(List<HasWord> sentence) {
	// return taggerModel.tagSentence(sentence);
	// }
	// *******************************************************************************************
	public List<TaggedWord> getTaggedSentence(List<HasWord> sentence) {
		listOfRestOfTrees = new ArrayList<Tree>();
		if (NegationElements.isNegated(sentence)) {
			System.out.println("___________________");
			System.out.println("Negation happend ! ==> " + sentence);
			return getTaggedWordsOfNegatedSentence(sentence);
		} else {
			return taggerModel.tagSentence(sentence);
		}
	}

	private List<TaggedWord> getTaggedWordsOfNegatedSentence(
			List<HasWord> sentence) {
		List<TaggedWord> tagedWords = new ArrayList<TaggedWord>();
		Tree pennTree = parser.apply(sentence);
		List<Tree> negatedTrees = extractNegatedSubTree(pennTree);
		if (negatedTrees.isEmpty()) {
			System.out.println("-- Negation tree is null!");
			return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTree = getRealNegatedTrees(negatedTrees);
			addNegationSymbolToWords(realNegatedTree);
			tagedWords.addAll(getTaggedwords(realNegatedTree));
			tagedWords.addAll(getTaggedwords(listOfRestOfTrees));
//			System.out.println(" %%%%%% All Tagged words of Sentence : "
//					+ Arrays.toString(tagedWords.toArray()));
			return tagedWords;
		}
	}
	
	private List<Tree> extractNegatedSubTree(Tree tree) {
		System.out.println("@@ Main Tree : " + tree.toString());
		List<Tree> negatedTrees = new ArrayList<Tree>();
			for (String pattern : patternsList) {
				TregexPattern tregexPattern = macros.compile(pattern);
				TregexMatcher matcher = tregexPattern.matcher(tree);
				while (matcher.findNextMatchingNode()) {
					Tree subTree = matcher.getMatch();
					negatedTrees.add(subTree);
					System.out.println("**Negated Pattern : " + pattern);
					System.out.println("** Negated Tree : " + subTree.toString());
				}
				deleteSubTree(tree, tregexPattern);
			}
		listOfRestOfTrees.add(tree);
		System.out.println("-- Rest of the tree : " + tree.toString());
		return negatedTrees;
	}
	private boolean isContainSBAR(Tree tree) {
		String pattern = "SBAR=sbar > __";
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		return matcher.find();
	}

	private List<Tree> extractNegatedSubTree1(Tree mainTree) {
		System.out.println("@@ Main Tree : " + mainTree.toString());
		List<Tree> negatedTrees = new ArrayList<Tree>();
		treesToFindNegation = new ArrayList<Tree>();
		treesToFindNegation.add(mainTree);
		System.out.println (treesToFindNegation.size());
		while(treesToFindNegation.iterator().hasNext()) {
			System.out.println("===== Size :"+treesToFindNegation.size());
			Tree tree = treesToFindNegation.iterator().next();//.get(treesToFindNegation.size()-1);
			for (String pattern : patternsList) {
				TregexPattern tregexPattern = macros.compile(pattern);
				TregexMatcher matcher = tregexPattern.matcher(tree);
				while (matcher.findNextMatchingNode()) {
					Tree subTree = matcher.getMatch();
					subTree = verifyContainSBAR1(subTree);
					negatedTrees.add(subTree);
					System.out.println("**Negated Pattern : " + pattern);
					System.out.println("** Negated Tree : " + subTree.toString());
				}
				deleteSubTree(tree, tregexPattern);
				
			}
		}
//		listOfRestOfTrees.add(mainTree);
//		System.out.println("-- Rest of the tree : " + tree.toString());
		return negatedTrees;
	}

	private Tree verifyContainSBAR1(Tree tree) {
		for (int i = 0; i < tree.numChildren(); i++) {
			if (tree.getChild(i).value().equals("SBAR")) {
				treesToFindNegation.add(tree.getChild(i));
				tree.removeChild(i);
			}
		}
		return tree;
	}

	private List<Tree> getRealNegatedTrees(List<Tree> tree) {
		for (int i = 0; i < tree.size(); i++) {
			if (NegationElements.isPseudoNegation(tree.get(i))) {
				System.out.println("--- pseudo negated tree : " + tree.get(i));
				listOfRestOfTrees.add(tree.get(i));
				tree.remove(i);
				System.out
						.println("** Negated tree after remove pseudo part : "
								+ tree.toString());
			}
//				else {
//				pruneTree(tree.get(i));
//			}
		}
		return tree;
	}

	public void pruneTree(Tree tree) {
		System.out.println("@@ Main Tree : " + tree.toString());
		System.out.println("Size of Tree : " + tree.numChildren());
		List<Tree> extra = new ArrayList<Tree>();
		for (int i = 0; i < tree.numChildren(); i++) {
			System.out.println ("get " + i + " : " + tree.getChild(i).value());
			if (tree.getChild(i).value().equals("SBAR")
					|| tree.getChild(i).value().equals("S")) {
				extra.add(tree.getChild(i));
				System.out.println("Extra at " + i + " is " + Arrays.toString(extra.toArray()));
				tree.removeChild(i);
			}
		}
		System.out.println("Tree: " + tree.toString());
		System.out.println("Extra: " + Arrays.toString(extra.toArray()));
	}

	private void addNegationSymbolToWords(List<Tree> trees) {
		for (Tree subTree : trees) {
			List<Tree> leaves = subTree.getLeaves();
			for (Tree leave : leaves) {
				leave.setValue("~" + leave.value());
			}
			System.out.println("** Negated words : " + leaves.toString());
		}
	}

	private List<TaggedWord> getTaggedwords(List<Tree> trees) {
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		for (Tree subTree : trees) {
			taggedWords.addAll(subTree.taggedYield());
		}
		return taggedWords;
	}

	public static Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	// *******************************************************************************************
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
		return (isVerbThatNeedsToStem(taggedWord.tag())) ? stem(taggedWord
				.word()) : taggedWord.word();
	}

	private boolean isVerbThatNeedsToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	public void getSentenceCount(Document document, List<WordList> sentences) {// ?
		int totalSentencesCount = sentences.size();
		document.setTotalSentenceCounts(totalSentencesCount);
	}

	public WordList getListOfWordWithCounts(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word newWord : sentence.getWords()) {
				String word = newWord.getWord();
				String tag = newWord.getTag();
				if (!containWords(globalWordList, word, tag)) {
					globalWordList.add(new Word(word, tag, 1));
				}
			}
		}
		return globalWordList;
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
