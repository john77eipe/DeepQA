package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

/**
 * @author Jaewook Kim
 * 
 */
public class SSTop_WN_Jaccard extends AbstractSchemaSimilarityByWordNet
		implements StringSimilarity {

	public SSTop_WN_Jaccard() {
		super();
	}

	@Override
	public float getSimilarity(String s, String t) {
		String sourceDef = getWordNetDef(s);
		String targetDef = getWordNetDef(t);

		// AbstractStringMetric metric = new MongeElkan();
		AbstractStringMetric metric = new JaccardSimilarity();
		float sim = metric.getSimilarity(sourceDef, targetDef);
		// System.out.println(source + " : " + target + " = " + sim);
		return sim;
	}

}
