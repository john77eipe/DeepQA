package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import gov.nist.msi.ssmpi.similarity.utils.WordStringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import shef.nlp.wordnet.similarity.SimilarityMeasure;

/**
 * @author Jaewook Kim
 * 
 */
public class SSTop_ICofWN implements StringSimilarity {

	SimilarityMeasure simMeasure;

	public SSTop_ICofWN() {
		super();
		// Initialize WordNet - this must be done before you try
		// and create a similarity measure otherwise nasty things
		// might happen!
		try {
			JWNL.initialize(new FileInputStream("test/wordnet.xml"));

			// Create a map to hold the similarity config params
			Map<String, String> params = new HashMap<String, String>();

			// the simType parameter is the class name of the measure to use
			params.put("simType", "shef.nlp.wordnet.similarity.JCn");

			// this param should be the URL to an infocontent file (if required
			// by the similarity measure being loaded)
			params.put("infocontent", "file:test/ic-bnc-resnik-add1.dat");

			// this param should be the URL to a mapping file if the
			// user needs to make synset mappings
			params.put("mapping", "file:test/domain_independent.txt");

			// create the similarity measure
			simMeasure = SimilarityMeasure.newInstance(params);
//			System.out.println("simMeasure configured.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public float getSimilarity(String s, String t) {

		float sim = 0;
		String[] sourceList = WordStringUtils.generateMultiWordString(s).split(
				" ");
		String[] targetList = WordStringUtils.generateMultiWordString(t).split(
				" ");

		float sSimSum = 0;
		for (String source : sourceList) {
			float best = 0;
			for (String target : targetList) {
				try {
					sim = (float) simMeasure.getSimilarity(source, target)
							.getSimilarity();
					if (sim > 5.1472476E9) {
						sim = 1;
					}
					if (best < sim) {
						best = sim;
					}
				} catch (JWNLException e) {
					e.printStackTrace();
				}
			}
			sSimSum += best;
		}
		float sSim = sSimSum / sourceList.length;

		float tSimSum = 0;
		for (String target : targetList) {
			float best = 0;
			for (String source : sourceList) {
				try {
					sim = (float) simMeasure.getSimilarity(source, target)
							.getSimilarity();
					if (sim > 5.1472476E9) {
						sim = 1;
					}
					if (best < sim) {
						best = sim;
					}
				} catch (JWNLException e) {
					e.printStackTrace();
				}
			}
			tSimSum += best;
		}
		float tSim = tSimSum / targetList.length;

		sim = (sSim + tSim) / 2;

		return sim;
	}
}