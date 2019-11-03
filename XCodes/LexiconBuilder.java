package lexicon;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


public class LexiconBuilder {
	
//	countPolarities();
//	}
	private Map<String, Integer> wordAndScore;

	@SuppressWarnings("unused")
	private void countPolarities() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("Clues.csv"));

			System.out.println(wordAndScore.size());
			int pos, neg, nut;
			pos = 0;
			neg = 0;
			nut = 0;
			for (Map.Entry<String, Integer> entry : wordAndScore.entrySet()) {
				int score = entry.getValue();
				if (score > 0)
					pos++;
				else if (score < 0)
					neg++;
				else if (score == 0)
					nut++;
				bw.write(entry.getKey() + ", " + entry.getValue());
				bw.newLine();
			}
			System.out.println("pos= " + pos);
			System.out.println("neg= " + neg);
			System.out.println("nut= " + nut);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
