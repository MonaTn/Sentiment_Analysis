package xExtraClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Word;
import shared.WordList;

public class ListTester {

	public static void main(String[] args) {
	}

	public static void testList() {
		List<String> strList = new ArrayList<String>();
		strList.add("a");
		int i = 0;
		while (strList.iterator().hasNext()) {
			System.out.println(strList.toString());
			addToList(strList, i);
			i++;
			if (i > 10)
				break;
		}
	}

	public static void addToList(List<String> strList, int i) {
		strList.add(Integer.toString(i));

	}

	public static void addToList() {
		List<String> strList = new ArrayList<String>();
		strList.add("I");
		strList.add("am");
		strList.add("a");
		strList.add("student");
		strList.add("but");
		strList.add("you");
		strList.add("aren't");
		if (strList.contains("but")) {
			System.out.println(strList.indexOf("but"));
		}
	}

	public static void contains() {
		Word word = new Word("wall", "n", 4);
		WordList wordList = new WordList();
		wordList.add(new Word("Mona", "n", 2));
		wordList.add(new Word("Can", "MD", 1));
		wordList.add(new Word("write", "v", 1));
		wordList.add(new Word("cat", "n", 3));
		wordList.add(new Word("wall", "n", 4));
		wordList.add(new Word("want", "v", 10));
		if (wordList.containsWord(word)) {
			wordList.updateCount(word);
			System.out.println(Arrays.toString(wordList.getWords().toArray())
					+ wordList.containsWord(word));
		} else {
			System.out.println("No");
		}

	}

}
