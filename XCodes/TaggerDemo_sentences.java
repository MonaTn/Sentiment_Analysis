package xExtraClass;

import java.io.IOException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TaggerDemo_sentences {

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger(
				"TaggerModels/english-bidirectional-distsim.tagger");

		// The sample string
		String sample = "WhoF the **ck are GNR?? Plus they make awful music! I can't stand this annyoing album. The music is awful, so as the lyrics. There is not ONE good song on this piece of s**t. It is a album that contains 12 crappy songs. AXL ROSE is a very annoying lead singer just screams and sings horribly, and SLASH is terrible too. In THE WAY STAY AWAY FROM THIS AS WELL AS GNRS OTHER ALBUMS. LEAVE GNR ALONE....GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS GNR SUCKS.I recommend buy these albums insted.FOREIGNER 4LOVE OVER GOLD-DIRE STRAITSON EVERY STREET-DIRE STRAITSCOMBAT ROCK-THE CLASHHOW TO DISMANTLE AN ATOMIC BOMB-U2AMERICAN IDIOT-GREEN DAYANIMAL MAGNETISM-SCORPIONSLOAD-METALLICARELOAD-METALLICAAND JUSTICE FOR ALL-METALLICAMASTER OF PUPPETS-METALLICARIDE THE LIGHTNING-METALLICASELF TITLE BLACK ALBUM-METALLICAPAST MASTERS VOL 2-BEATLESPHYSICAL GRAFFITI-LED ZEPPELINSOME GIRLS-THE ROLLING STONESGREATEST HITS-NEIL YOUNGTHE LAST DJ-TOM PETTY-GNR ARE AS BAD AS PINK FLOYD, PEARL JAM, NIRVANA, and AC/DC. AVOID ALL ALBUMS FROM THOSE BANDS.";
//		String sample ="This is an example.";
		// The tagged string
		String tagged = tagger.tagString(sample);

		// Output the result
		System.out.println(tagged);
	}
}
