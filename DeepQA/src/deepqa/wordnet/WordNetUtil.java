package deepqa.wordnet;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import deepqa.constants.Constants;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import gov.nist.msi.ssmpi.similarity.impl.SSTop_WN_Jaccard;

public class WordNetUtil {
	
	public static Set<String> getSynonyms(Entry<String, String> taggedToken) {
	
		final String tag = taggedToken.getValue();
		final String word = taggedToken.getKey();
		
		System.setProperty("wordnet.database.dir",
				Constants.WORDNET_DIC_LOC);
		Set<String> synonyms = null;
		if (word != null) {
			// Concatenate the command-line arguments
			StringBuffer buffer = new StringBuffer();

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
					System.out.println("Verb identified");
					synsets = database.getSynsets(word, SynsetType.VERB);
					break;
				case("RB"):
				case("RBR"):
				case("RBS"):
					System.out.println("Adverb identified");
					synsets = database.getSynsets(word, SynsetType.ADVERB);
					break;
				case("JJ"):
				case("JJR"):
				case("JJS"):
					System.out.println("Adjective identified");
					synsets = database.getSynsets(word, SynsetType.ADJECTIVE);
					break;
				default:
					System.out.println("Avoiding WordNet search for: "+word);
			}
			// Display the word forms and definitions for synsets retrieved
			if (synsets != null && synsets.length > 0) {
				synonyms = new HashSet<String>();
				System.out.println("The following synsets contain '"
						+ word
						+ "' or a possible base form of that text:");
				for (int i = 0; i < synsets.length; i++) {
					String[] wordForms = synsets[i].getWordForms();
					for (int j = 0; j < wordForms.length; j++) {
						// System.out.println(wordForms[j]);
						if (!synonyms.contains(wordForms[j])) {
							SSTop_WN_Jaccard sim = new SSTop_WN_Jaccard();
							float r = sim.getSimilarity(word,
									wordForms[j]);
							if (r > 0.3)
								synonyms.add(wordForms[j]);
						}

					}
					// System.out.println(": " + synsets[i].getDefinition());
				}
			} else {
				System.err.println("No synsets exist that contain "
						+ "the word form '" + word + "'");
			}
		} else {
			System.err.println("You must specify "
					+ "a word form for which to retrieve synsets.");
		}
		return synonyms;
	}
}
