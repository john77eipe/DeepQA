package gov.nist.msi.ssmpi.similarity.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.SortedSet;

import edu.mit.jwi.dict.Dictionary;
import edu.mit.jwi.dict.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.PartOfSpeech;
import edu.mit.jwi.morph.WordnetStemmer;
import gov.nist.msi.ssmpi.similarity.utils.WordStringUtils;

/**
 * @author Jaewook Kim
 *
 */
public abstract class AbstractSchemaSimilarityByWordNet {
	
	private static IDictionary dict = null;
	private static HashMap<String, String> cachedWordGross = new HashMap<String, String>();
	private static HashMap<String, String> cachedWordNetDef = new HashMap<String, String>();
		
	public AbstractSchemaSimilarityByWordNet() {
		super();
		initWordNetDb();
	}

	private void initWordNetDb() {
		if (dict == null) {
			String wnhome = "/Users/admin/Documents/nlpworkspace/DeepQASupport/WordNet-3.0/";
			String separator = System.getProperty("file.separator");
			String path = wnhome + separator + "dict";
			URL url = null;
			try {
				url = new URL("file", null, path);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if (url == null)
				return;

			// construct the dictionary object and open it
			dict = new Dictionary(url);
			dict.open();
		}
	}

	protected String getWordNetDef(final String text) {
		if (cachedWordNetDef.containsKey(text)) {
			return cachedWordNetDef.get(text);
		}

		String text2 = WordStringUtils.generateMultiWordString(text);
		String[] wordList = text2.split(" ");
		
		StringBuffer textDef = new StringBuffer(text + " ");
		for (String wordString : wordList) {
			String mergedWordGloss;
			if (cachedWordGross.containsKey(wordString)) {
				mergedWordGloss = cachedWordGross.get(wordString);
			}
			else {
				StringBuffer mergedWordGlossBuffer = new StringBuffer("");
		        for (PartOfSpeech pos : PartOfSpeech.values()) {
					WordnetStemmer stem = new WordnetStemmer(dict);
					SortedSet<String> wordRoots = stem.getRoots(wordString, pos);
					if (wordRoots == null) continue;
					IIndexWord idxWord = dict.getIndexWord(wordRoots.first(), pos);
					if (idxWord != null) {
						IWordID[] wordIdList = idxWord.getWordIDs();
						for (IWordID wordID : wordIdList) {
							IWord word = dict.getWord(wordID);
							String wordGloss = word.getGloss();
							if (wordGloss.indexOf(';') > 0) {
								wordGloss = wordGloss.substring(0, wordGloss.indexOf(';'));
							}
							mergedWordGlossBuffer.append(wordGloss + " ");
						}
					}
		        }
		        
		        mergedWordGloss = mergedWordGlossBuffer.toString();
		        cachedWordGross.put(wordString, mergedWordGloss);
			}
			
			textDef.append(mergedWordGloss);
		}
		
		String wordNetDef = WordStringUtils.removeStopwords(textDef.toString().toLowerCase());
		cachedWordNetDef.put(text, wordNetDef);
		
		return wordNetDef;
	}
	
}
