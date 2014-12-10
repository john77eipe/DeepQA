package gov.nist.msi.ssmpi.similarity.utils;

import java.util.Hashtable;


/**
 * @author Jaewook Kim
 *
 */
public class WordInformationContents {
	
	private long totalTermsPopulation = 0;
	private Hashtable<String, Long> totalTermsFreqMap;
	
	public WordInformationContents() {
		super();
	}

	public WordInformationContents(String[] t) {
		calculatePI(t);
	}

	private void addTermFreq(String[] nodeList) {

		for (String node : nodeList) {
			String text2 = WordStringUtils.generateMultiWordString(node);
			String[] wordList = text2.split(" ");
			for (String string : wordList) {
				string = string.trim();
				Long count = totalTermsFreqMap.get(string);
				if ( count == null)
					count = 0L;
					
				totalTermsFreqMap.put(string, ++count);
				totalTermsPopulation++;
			}
		}
	}
	/**
	 * calculate Atoms' probability information again 
	 * 	after merging the population of the source and target atoms.
	 * This should be called before calculating the similarities.
	 */
	protected void calculatePI(String[] t) {
		totalTermsFreqMap = new Hashtable<String, Long>();
		
		totalTermsPopulation = 0;
		
		addTermFreq(t);
	}
	
	/**
	 * @param atom
	 * @return
	 */
	public double getPI(String nodeName) {
		Long nodeCount = totalTermsFreqMap.get(nodeName);
		if (nodeCount == null) {
			return 0;
		}
		
		double pi = - Math.log(((double)nodeCount) / totalTermsPopulation);
		return pi;
	}

}
