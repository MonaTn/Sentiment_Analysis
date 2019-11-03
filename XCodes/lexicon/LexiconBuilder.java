package lexicon;


import java.io.BufferedReader;
import java.io.FileReader;

public abstract class LexiconBuilder {


     void buildLexicon(String lexiconFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    lexiconFile));
            String line = bufferedReader.readLine(); // read header
            while ((line = bufferedReader.readLine()) != null) {
                addWordsAndScoreToMap(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     String[] tokenizedText(String text, String regEx) { // **
        return text.split(regEx);
    }
       abstract void addWordsAndScoreToMap(String line);
}
