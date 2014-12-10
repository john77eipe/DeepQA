package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import gov.nist.msi.ssmpi.similarity.similaritymetrics.CosineSimilarity2;
import gov.nist.msi.ssmpi.similarity.utils.WordInformationContents;
import gov.nist.msi.ssmpi.similarity.utils.WordStringUtils;

import java.util.HashMap;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * @author Jaewook Kim
 * 
 */
public class SSTop_WN_IC_Cosine extends AbstractSchemaSimilarityByWordNet implements StringSimilarity{
	private WordInformationContents icHandler;
	private HashMap<String, String> cachedDef = new HashMap<String, String>();

	public SSTop_WN_IC_Cosine() {
		super();
		icHandler = new WordInformationContents();
	}

	public SSTop_WN_IC_Cosine(String[] informationList) {
		icHandler = new WordInformationContents(informationList);
	}

	private String getFixedLenWNDef(String name) {

		if (name.trim().length() == 0) {
			System.out
					.println("getFixedLenWNDef(String) - SSTop_WN_IC_Cosine: warn: empty string"); //$NON-NLS-1$
			return "";
		}

		if (cachedDef.containsKey(name)) {
			return cachedDef.get(name);
		}

		int FIXED_WNDEF_LEN = 200;

		// get the sum of the information contents of all splitted words
		double sumIC = 0;
		String text2 = WordStringUtils.generateMultiWordString(name);
		String[] wordList = text2.split(" ");
		for (String string : wordList) {
			string = string.trim();
			if (string.length() == 0) {
				System.out
						.println("getFixedLenWNDef(String) - SSTop_WN_IC_Cosine: warn: empty string in list"); //$NON-NLS-1$
				continue;
			}
			sumIC += icHandler.getPI(string);
		}

		StringBuffer fixedLenDef = new StringBuffer();
		// get the fixed lengh wordnet definition;
		// merging the definitions of all splitted words according to ratio
		// length
		for (String string : wordList) {
			string = string.trim();
			double icValue = icHandler.getPI(string);
			double ratioEffect = icValue / sumIC;
			int ratioWordLen = (int) (FIXED_WNDEF_LEN * ratioEffect);

			String def = getWordNetDef(string);

			String[] defSplit = def.split(" ");

			if (defSplit.length == 0) {
				System.out
						.println("getFixedLenWNDef(String) - SSTop_WN_IC_Cosine: info: no wordnet def: " + string); //$NON-NLS-1$
				continue;
			}

			for (int i = 0; i < ratioWordLen; i++) {
				int defSplitIndex = i % defSplit.length;
				fixedLenDef.append(defSplit[defSplitIndex] + " ");
			}
		}

		String fixedLenDefStr = fixedLenDef.toString();
		cachedDef.put(name, fixedLenDefStr);
		return fixedLenDefStr;
	}

	@Override
	public float getSimilarity(String s, String t) {
		String sNodeDef = getFixedLenWNDef(s);
		String tNodeDef = getFixedLenWNDef(t);

		AbstractStringMetric metric = new CosineSimilarity2();
		float sim = metric.getSimilarity(sNodeDef, tNodeDef);
		return sim;
	}
}
