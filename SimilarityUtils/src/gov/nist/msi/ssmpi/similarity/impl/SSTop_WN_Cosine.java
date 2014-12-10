package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import gov.nist.msi.ssmpi.similarity.similaritymetrics.CosineSimilarity2;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * @author Jaewook Kim
 * 
 */
public class SSTop_WN_Cosine extends AbstractSchemaSimilarityByWordNet
		implements StringSimilarity {

	public SSTop_WN_Cosine() {
		super();
	}

	@Override
	public float getSimilarity(String s, String t) {

		String sourceDef = getWordNetDef(s);
		String targetDef = getWordNetDef(t);

		AbstractStringMetric metric = new CosineSimilarity2();
		float sim = metric.getSimilarity(sourceDef, targetDef);
		return sim;
	}

}
