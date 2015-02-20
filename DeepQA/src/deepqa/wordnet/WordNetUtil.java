package deepqa.wordnet;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import deepqa.constants.Constants;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import gov.nist.msi.ssmpi.similarity.impl.SSTop_WN_Jaccard;

public class WordNetUtil {
	
	public static final double similarityCriteria = 0.45; //used by Jaccard's similarity index
	
	final static Logger LOGGER = Logger.getLogger(WordNetUtil.class);
	
	public static Set<String> getSynonyms(Entry<String, String> taggedToken) {
	
		final String tag = taggedToken.getValue();
		final String word = taggedToken.getKey();
		
		System.setProperty("wordnet.database.dir",
				Constants.WORDNET_DIC_LOC);
		Set<String> synonyms = null;
		if (word != null) {

			//Loading WordNet Database
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			
			Synset[] synsets = null;
			
			// Get the synsets containing the word form
			switch (tag) {
				case("VB"):
				case("VBD"):
				case("VBG"):
				case("VBN"):
				case("VBP"):
				case("VBZ"):
					LOGGER.debug("Verb identified");
					synsets = database.getSynsets(word, SynsetType.VERB);
					break;
				case("RB"):
				case("RBR"):
				case("RBS"):
					LOGGER.debug("Adverb identified");
					synsets = database.getSynsets(word, SynsetType.ADVERB);
					break;
				case("JJ"):
				case("JJR"):
				case("JJS"):
					LOGGER.debug("Adjective identified");
					synsets = database.getSynsets(word, SynsetType.ADJECTIVE);
					break;
				default:
					LOGGER.debug("Avoiding WordNet search for: "+word);
			}
			// Display the word forms and definitions for synsets retrieved
			if (synsets != null && synsets.length > 0) {
				synonyms = new HashSet<String>();
				LOGGER.debug("The following synsets contain '"
						+ word
						+ "' or a possible base form of that text:");
				for (int i = 0; i < synsets.length; i++) {
					String[] wordForms = synsets[i].getWordForms();
					for (int j = 0; j < wordForms.length; j++) {
						if (!synonyms.contains(wordForms[j])) {
							SSTop_WN_Jaccard sim = new SSTop_WN_Jaccard();
							float r = sim.getSimilarity(word,
									wordForms[j]);
							if (r > similarityCriteria)
								synonyms.add(wordForms[j]);
						}
					}
				}
				
			} else {
				LOGGER.debug("No synsets exist that contain "
						+ "the word form '" + word + "'");
			}
		} else {
			LOGGER.debug("You must specify "
					+ "a word form for which to retrieve synsets.");
		}
		return synonyms;
	}
}
