package X.Negation.copy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;

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

public class TextPreprocessorWithNegationFinder1 {

	private List<Tree> restOfTrees;
	private List<String> patterns;
	private List<Tree> treesToProcess;
	private PorterStemmer stemmer;
	private MaxentTagger taggerModel;
	private TregexPatternCompiler macros;
	private LexicalizedParser lexicalizedParser;
	private NegationPreprocessor negationProcessor;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public TextPreprocessorWithNegationFinder1() {
		initializeTagger();
		initializeLexicalizedParser();
		stemmer = new PorterStemmer();
		negationProcessor = new NegationPreprocessor();
		patterns = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
	}

	private void initializeLexicalizedParser() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		lexicalizedParser = LexicalizedParser.loadModel(grammar, options);
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

	public void extractWordsListOfDocument(Document document)
			throws IOException {
		List<WordList> sentences = getTaggedWordListsOfDocument(document);
		document.setTotalSentencesCount(sentences.size());
		WordList wordsList = getListOfWordsWithCounts(sentences);
		document.setWordsList(wordsList);
	}

	private List<WordList> getTaggedWordListsOfDocument(Document document)
			throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessor(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> taggedWordsOfSentence = getTaggedWords(sentence); // **
			WordList wordsList = convertTagedWordsToWordList(taggedWordsOfSentence);
			sentences.add(wordsList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessor(Document document)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String fileName = document.getAbsoluteFileName();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), "utf-8"));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				bufferReader);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	@SuppressWarnings("unused")
	private DocumentPreprocessor documentPreprocessor1(Document document) {
		return new DocumentPreprocessor(document.getAbsoluteFileName());
	}

	public void testProcessor(List<HasWord> sentence) {
		List<TaggedWord> taggedWords = getTaggedWords(sentence);
		System.out.println(" %%%%%% All Tagged words of Sentence : "
				+ Arrays.toString(taggedWords.toArray()));
	}

	// *******************************************************************************************
	public List<TaggedWord> getTaggedWords(List<HasWord> sentence) {
		if (negationProcessor.containNegationCues(sentence)) {
			System.out.println("___________________");
			System.out.println("Negation happend ! ==> " + sentence);
			return getTaggedWordsOfNegatedSentence(sentence);
		} else {
			return taggerModel.tagSentence(sentence);
		}
	}

	private List<TaggedWord> getTaggedWordsOfNegatedSentence(
			List<HasWord> sentence) {
		restOfTrees = new ArrayList<Tree>();
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		Tree treeOfSentence = lexicalizedParser.parse(sentence);
		System.out
				.println("@@ Tree of sentence : " + treeOfSentence.toString());
		treesToProcess = Collections.synchronizedList(new ArrayList<Tree>());
//		treesToProcess = new CopyOnWriteArrayList<Tree>();
		treesToProcess.add(treeOfSentence);

		Iterator<Tree> iterator = treesToProcess.iterator();
		while (iterator.hasNext()) {
			negatedTrees.addAll(exteractNegatedSubTrees(iterator.next()));
		}
		if (negatedTrees.isEmpty()) {
			System.out.println("-- Negation tree is null!");
			return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTree = getRealNegatedTrees(negatedTrees);
			addNegationSymbolToWords(realNegatedTree);
			taggedWords.addAll(getTaggedwords(realNegatedTree));
			taggedWords.addAll(getTaggedwords(restOfTrees));

			return taggedWords;
		}
	}

	public List<Tree> exteractNegatedSubTrees(Tree tree) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				System.out.println("**Negated Pattern : " + pattern);
				TregexTree subTree = new TregexTree(matcher.getMatch());
				System.out.println("** Negated Tree : ");
				subTree.toPrint();
				treesToProcess.addAll(subTree.getSBARs());
				synchron(treesToProcess);
				treesToProcess.addAll(subTree.getParenthetical());
				synchron(treesToProcess);
				treesToProcess.addAll(subTree.getPPs());
				synchron(treesToProcess);
				negatedTrees.add(subTree.prune());
				System.out.println("%%% Negated Tree After prunning: ");
				subTree.toPrint();
			}
			deleteSubTree(tree, tregexPattern);
		}
		restOfTrees.add(tree);
		return negatedTrees;
	}

	private synchronized List<Tree> synchron(List<Tree> trees) {
		synchronized (trees) {
			Iterator<Tree> iterator = trees.iterator();
			while (iterator.hasNext()) {
				iterator.next();
			}
		}
		return trees;
	}

	private List<Tree> getRealNegatedTrees(List<Tree> tree) {
		for (int i = 0; i < tree.size(); i++) {
			if (negationProcessor.isPseudoNegation(tree.get(i))) {
				System.out.println("--- pseudo negated tree : " + tree.get(i));
				restOfTrees.add(tree.get(i));
				tree.remove(i);
				System.out
						.println("** Negated tree after remove pseudo part : "
								+ tree.toString());
			}
		}
		return tree;
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

	public Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private WordList convertTagedWordsToWordList(List<TaggedWord> tagSentence) {
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

	public WordList getListOfWordsWithCounts(List<WordList> sentences) {
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
