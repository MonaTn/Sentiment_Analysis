package xTestUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import shared.Document;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class test33 {
	
	@SuppressWarnings("unused")
	private static MaxentTagger taggerModel;
	private final static String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger(
				"TaggerModels/english-bidirectional-distsim.tagger");

		// The sample string
		String sample = "Lexmark X125   This is now the top of my list of worthless crap.  I've gone through 3 BRAND new x125's thinking that when they went to expense of sending me a NEW one, it would actually run.  Even LExmark can't make this junk work.    RUN FROM THIS JUNK!!!!    The letter I sent to Lexmark: Umm, this is a device that was just unboxed (actually the third, the   first one I took back, the second one did the same and third one you guys   sent me as a replacement but really I think it's a joke, right?????)and   again, does not work.         Now we both know what a worthless piece of crap the x125 is.  Not only do   I have the one hunk-of-junk I bought, but you mistakenly sent me a  \"replacement\".  Can't even use them as boat anchors.  What a waste on the   GDP.         Why bother selling this?  Just tell me to go get drunk and spend the   money at a bar.  (oops I forgot, you need to realize \"revenue\".)         It'll be a \"BIG\" surprise when in 4q2004 or 1q2005 the Wall Street   Journal sez, \"...in other news today, LEXMARK announce decreased profits on   decreased earnings 'cause customers across America returned their   equipment....in droves....\".         (Repair technician jobs should be highly available at a certain Kentucky   manufacturing plant.)         But really it's not all your fault.  I bought this \"piece of (insert   George Carlin word)\" at a GROCERY store!  What a dumbass I was!         HEB makes real good salsa, has the best prices in central Texas and even   salmon chorizo for heart guys like me.  Back to the drawing board on thios   idea (don't worry, I'll let them know this isn't a great channel idea).         What does LEXMARK do?  They foist off el-junko at the local grocery   store.....is this what you guys call the grey market?   More like the   \"brown\" market.         Somebody's ass in product marketing OUGHT TO HANG!  How could you do this   to your fellow Americans?         BTW, if you need me to fix this problem, we can talk.         RECALL RECALL RECALL RECALL RECALL RECALL RECALL RECALL RECALL RECALL   RECALL RECALL RECALL RECALL RECALL RECALL RECALL RECALL RECALL RECALL   RECALL RECALL RECALL RECALL ";

		// The tagged string
		String tagged = tagger.tagString(sample);

		// Output the result
		System.out.println(tagged);
	}
	
	@SuppressWarnings("unused")
	private static void exteractor() throws FileNotFoundException, IOException, ClassNotFoundException {
		 taggerModel = new MaxentTagger(englishModel);
		BufferedReader br = new BufferedReader(
				new FileReader(
						"../../TrainingSet/Hotel/Baccianella,Esuli,Sebastiani (2009) -DATA- TripAdvisor_corpus/TripAdvisor_15763.txt"));
		 BufferedReader br1 = new BufferedReader(new
		 FileReader("../../TrainingSet/Small Test/Hotel&/test.txt"));

//		 ExtractReviews extractor = new ExtractReviews();
//		 extractor.exteractReviewsFromProcessedText("../../Results/", br1);
//		preprocessingText(br1);
	}

	


	@SuppressWarnings("unused")
	private static void readFileByScanner() throws FileNotFoundException,
			ClassNotFoundException, IOException {
		Scanner scanner = new Scanner(new File(
				"../../TrainingSet/Small Test/test.txt"));
		while (scanner.hasNext()) {
			String[] sentences = scanner.nextLine().split("\t");
			createDocumentParameters(sentences);

		}
		scanner.close();
	}

	private static Document createDocumentParameters(String[] sentences)
			throws ClassNotFoundException, IOException {
		String documentName = sentences[0];
		String text = sentences[1].replaceAll("\\_(PROS).+", "");
		System.out.println(text);
		int rankingScore = Integer.valueOf(sentences[2]);
		boolean truePolarity = (rankingScore >= 4) ? true : false;
		return new Document(documentName, "", truePolarity);
	}

	@SuppressWarnings("unused")
	private static DocumentPreprocessor documentPreprocessing(BufferedReader br)
			throws ClassNotFoundException, IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(br);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	@SuppressWarnings("unused")
	private static void tttt() throws IOException {
		String line = "";
//		ExtractReviews exteraxter = new ExtractReviews();
		// File reviewsFile =
		// exteraxter.exteractReviewsFromRawText("../../TrainingSet/Small Test/",
		// "revOfHotel.txt", br);
		BufferedReader br = new BufferedReader(new FileReader(
				"../../TrainingSet/Small Test/rev.txt"));
		while ((line = br.readLine()) != null) {
			// System.out.println(line);
			line = line.replaceAll("\\_(PROS).+\t", "\t");
			String[] text = line.split("\t");
			// System.out.println("Reviews : "+review);
			for (String str : text) {
				System.out.println(str);
			}

			// List<List<HasWord>> sentences = MaxentTagger.tokenizeText(br);
			// String tagged = taggerModel.tagString(text[1]);
			//
			// String review =
			// "Wonderful for families - a find.   We were lucky enough to learn about the Hotel Suisse from friends in our hometown and it was such a treat.  We loved staying there.  The location could not be better and the family room was huge.  Our friends stayed at the Hassler during the smae time up the block and our room was 3 times the size.  I loved the security the place had as well with only a few guests at a time and our kids loved Rome.  The hotel is a little tricky as it is on the second floor of a mixed use building after several design shops.  This hotel was the best value of all of the hotels we stayed in on our 3 week trip.  I would not go back to Rome and not stay at the Suisse.  ";
			//
			// String test =
			// " Wonderful for families - a find   We were lucky enough to learn about the Hotel Suisse from friends in our hometown and it was such a treat  We loved staying there  The location could not be better and the family room was huge  Our friends stayed at the Hassler during the smae time up the block and our room was 3 times the size  I loved the security the place had as well with only a few guests at a time and our kids loved Rome  The hotel is a little tricky as it is on the second floor of a mixed use building after several design shops  This hotel was the best value of all of the hotels we stayed in on our 3 week trip  I would not go back to Rome and not stay at the Suisse ";
			// System.out.println(taggerModel.tagString(test));
			// System.out.println(taggerModel.tagString(review));

			// for (List<HasWord> sentence : sentences) {
			// ArrayList<TaggedWord> tSentence =
			// taggerModel.tagSentence(sentence);
			// System.out.println(Sentence.listToString(tSentence, false));
			// }
		}
	}
}
