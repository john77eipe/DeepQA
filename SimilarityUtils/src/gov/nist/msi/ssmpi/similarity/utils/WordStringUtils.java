package gov.nist.msi.ssmpi.similarity.utils;

public class WordStringUtils {
	public static String generateMultiWordString(String s) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if ( s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') {
				result.append(" " + s.charAt(i));
				i++;
				if (i < s.length()) {
					result.append(s.charAt(i));
				}
			} else {
				result.append(s.charAt(i));
			}
		}
		
		String resultStr = result.toString();
		
		return resultStr.trim();
	}


	public static String removeStopwords(String t) {
	
		t = t.replaceAll("[();\"'.,]?", "");
		
		// extracted from http://www.ranks.nl/tools/stopwords.html
		t = t.replaceAll(" a ", " ")
			.replaceAll(" the ", " ")
			.replaceAll(" and ", " ")
			.replaceAll(" or ", " ")
			.replaceAll(" in ", " ")
			.replaceAll(" of ", " ")
			.replaceAll(" to ", " ")
			.replaceAll(" that ", " ")
			.replaceAll(" it ", " ")
			.replaceAll(" i ", " ")
			.replaceAll(" you ", " ")
			.replaceAll(" about ", " ")
			.replaceAll(" an ", " ")
			.replaceAll(" are ", " ")
			.replaceAll(" as ", " ")
			.replaceAll(" at ", " ")
			.replaceAll(" be ", " ")
			.replaceAll(" by ", " ")
			.replaceAll(" for ", " ")
			.replaceAll(" from ", " ")
			.replaceAll(" how ", " ")
			.replaceAll(" is ", " ")
			.replaceAll(" on ", " ")
			.replaceAll(" this ", " ")
			.replaceAll(" was ", " ")
			.replaceAll(" what ", " ")
			.replaceAll(" when ", " ")
			.replaceAll(" where ", " ")
			.replaceAll(" who ", " ")
			.replaceAll(" will ", " ")
			.replaceAll(" into ", " ")
			.replaceAll(" which ", " ")
			.replaceAll(" with ", " ");
		return t;
	}
	
}
