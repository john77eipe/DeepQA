package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import gov.nist.msi.ssmpi.similarity.utils.WordStringUtils;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;

/**
 * @author Jaewook Kim
 * 
 */
public class SSTop_Cosine implements
		StringSimilarity {

	public SSTop_Cosine() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getSimilarity(String s, String t) {
		String source = WordStringUtils.generateMultiWordString(s);
		String target = WordStringUtils.generateMultiWordString(t);
		AbstractStringMetric metric = new CosineSimilarity();
		float sim = metric.getSimilarity(source, target);
		return sim;
	}
}
