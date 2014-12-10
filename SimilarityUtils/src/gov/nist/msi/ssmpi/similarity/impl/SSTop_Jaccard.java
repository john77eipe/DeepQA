package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;
import gov.nist.msi.ssmpi.similarity.utils.WordStringUtils;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

/**
 * @author Jaewook Kim
 * 
 */
public class SSTop_Jaccard implements
		StringSimilarity {

	public SSTop_Jaccard() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getSimilarity(String s, String t) {
		String source = WordStringUtils
				.generateMultiWordString(s);
		String target = WordStringUtils
				.generateMultiWordString(t);
		AbstractStringMetric metric = new JaccardSimilarity();
		float sim = metric.getSimilarity(source, target);
		return sim;
	}

}
