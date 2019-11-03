package xExtraClass;

import java.util.Arrays;
import java.util.List;

import shared.Word;
import shared.WordList;

import edu.stanford.nlp.trees.Tree;

public class ExteraMethodes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public WordList getListOfWordsWithCounts1(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word oneWord : sentence.getWords()) { // ??
				if (globalWordList.containsWord(oneWord)) {
					updateWordCount(globalWordList, oneWord);
				} else {
					String word = oneWord.getWord();
					String tag = oneWord.getTag();
					globalWordList.add(new Word(word, tag, 1));
				}
			}
		}
		return globalWordList;
	}

	private void updateWordCount(WordList wordList, Word word) {
		if (wordList.getWords().contains(word)) {
		}
	}

	public boolean containWord(WordList wordList, Word word) {// ?
		for (Word word2 : wordList.getWords()) {
			if (word2.equal(word)) {
				return true;
			}
		}
		return false;
		// word2.setCount(word2.getCount() + 1);

	}

	public String[] punctuations = { "~,", "~:", "~\"" };

	public boolean isExceptionWords(String word) {
		return Arrays.asList(punctuations).contains(word.toLowerCase());
	}

	public Tree ghabli(Tree tree, String word) {
		List<Tree> leaves = tree.getLeaves();
		int index = -1;
		for (Tree leaf : leaves) {
			String leafValue = leaf.value().toLowerCase();
			if (leafValue.equals(word)) {
				index = leaves.indexOf(leaf);
				if (isNegationCueAfterExceptionWords(index, leaves)) {
					return tree;
				} else {
					return editWrongNegatedWords(tree, index);
				}
			}
		}
		return tree;
	}

	// public Tree exceptionWordsProcess(Tree tree, String word) {
	// List<Tree> leaves = tree.getLeaves();
	// int index = -1;
	// for (Tree leaf : leaves) {
	// String leafValue = leaf.value().toLowerCase();
	// if (leafValue.equals(word)) {
	// index = leaves.indexOf(leaf);
	// if (isNegationCueAfterExceptionWords(index, leaves)) {
	// return tree;
	// } else {
	// return editWrongNegatedWords(tree, index);
	// }
	// }
	// }
	// return tree;
	// }

	public Tree editWrongNegatedWords(Tree tree, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNegationCueAfterExceptionWords(int index, List<Tree> leaves) {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	// public ResultSet getResultSet(long index, int count,
	// Map<String, Class<?>> map) throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public ResultSet getResultSet(long index, int count) throws SQLException
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public ResultSet getResultSet(Map<String, Class<?>> map)
	// throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public ResultSet getResultSet() throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getBaseTypeName() throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int getBaseType() throws SQLException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public Object getArray(long index, int count, Map<String, Class<?>> map)
	// throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Object getArray(long index, int count) throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Object getArray(Map<String, Class<?>> map) throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Object getArray() throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void free() throws SQLException {
	// // TODO Auto-generated method stub
	//
	// }
	//

}
