package deepqa.solr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import deepqa.constants.Constants;
import deepqa.wordnet.WordNetUtil;

public class SOLRUtil {
	
	static List<List<String>> parentList = null;
	public static Set<String> tokens = null;
	public static int limitingFactor;
	public static final String SEARCH_FIELD_CONTENT_RAW = "content_raw:";
	public static final String SEARCH_FIELD_CONTENT_ALT = "content_alt:";
	
	final static Logger LOGGER = Logger.getLogger(SOLRUtil.class);
	
	/**
	 * Basic solr querying
	 * @param taggedTokens
	 * @return
	 * @throws SolrServerException
	 */
	public static List<String> rawquerySolr(Map<String, String> taggedTokens)
			throws SolrServerException {

		LOGGER.debug("Initiating solr querying on content_raw");

		removeStopWords(taggedTokens);
		tokens = taggedTokens.keySet();
		
		HttpSolrServer server = new HttpSolrServer(
				Constants.SOLR_URL);
		SolrQuery query = new SolrQuery();
		String rawQuery = getRawQuery(taggedTokens);
		LOGGER.debug("Raw content matching query: "+rawQuery);
		query.setQuery(SEARCH_FIELD_CONTENT_RAW + rawQuery);
		query.setParam("df", "content");
		query.setParam("hl", "true");
		query.setParam("hl.fl", "content");
		query.setParam("hl.simple.pre", "#");
		query.setParam("hl.simple.post", "#");
		query.setParam("hl.snippets", "10");
		//query.setParam("hl.fragsize", "200");
		query.setParam("fl", "id");
		//query.setParam("sort", "score desc");
		query.setParam("hl.requireFieldMatch", true);
		query.setParam("hl.useFastVectorHighlighter", true);
		query.setParam("hl.boundaryScanner", "breakIterator"); 
//		query.setParam("hl.fragmenter", "regex");
//		query.setParam("hl.regex.pattern","\\w[^\\.\n!\\?]{0,200}[\\.\n!\\?]");
		//breakIterator works only for fast vector highlighting
//		query.setParam("hl.bs.country","US"); // this works for only fast vector highlighting
//		query.setParam("hl.bs.language","en"); // this works for only fast vector highlighting
		query.setParam("hl.bs.type", "LINE"); // this works for only fast vector highlighting

		List<String> cleanedHLContent = new ArrayList<String>();
		QueryResponse solrResponse = server.query(query);
		
		LOGGER.debug("Request URL: "+solrResponse.getRequestUrl());
		LOGGER.debug("Response length: "+solrResponse.getHighlighting().values().size());
		LOGGER.debug("Response: ");
		LOGGER.debug("------------------------------");
		//fetching highlighted content
		if (solrResponse.getResults().size() > 0) {
			for (Map<String, List<String>> highlightResponse : solrResponse
					.getHighlighting().values()) {
				final List<String> HLContent = highlightResponse.get("content");
				if(HLContent!=null && HLContent.size()>0) {
					for (String hlsent : HLContent) {
						hlsent = StringUtils.replace(
								StringUtils.replace(hlsent, "#", ""), "#", "");
						cleanedHLContent.add("" + hlsent + "\n");
					}
				}
			}		
		}
		LOGGER.debug("------------------------------");
		return cleanedHLContent;
	}
	
	
	/**
	 * Another alternative solr querying (uses WordNet)
	 * @param taggedTokens
	 * @return
	 * @throws SolrServerException
	 */
	public static List<String> querySolr(Map<String, String> taggedTokens)
			throws SolrServerException {
		tokens = taggedTokens.keySet();
		
		HttpSolrServer server = new HttpSolrServer(
				Constants.SOLR_URL);
		SolrQuery query = new SolrQuery();
		String querySuffix = createQuery(taggedTokens);
		
		LOGGER.debug(querySuffix);
		query.setQuery(querySuffix);
		query.setParam("df", "content");
		query.setParam("hl", "true");
		query.setParam("hl.fl", "content");
		query.setParam("hl.simple.pre", "#");
		query.setParam("hl.simple.post", "#");
		query.setParam("hl.snippets", "3");
		query.setParam("hl.fragsize", "300");

		query.setParam("sort", "score desc");
		query.setParam("hl.requireFieldMatch", true);
		query.setParam("hl.useFastVectorHighlighter", true);
		query.setParam("hl.boundaryScanner", "simple");
		//query.setParam("hl.bs.type", "SENTENCE");
		List<String> sentences = new ArrayList<String>();
		QueryResponse res = server.query(query);
		
		LOGGER.debug("Response length: "+res.getHighlighting().values().size());
		
		//fetching highlighted content
		if (res.getResults().size() > 0) {
			for (Map<String, List<String>> highlightResponse : res
					.getHighlighting().values()) {
				LOGGER.debug("***************");
				if(highlightResponse!=null && highlightResponse.get("content")!=null){
				for (String hlsent : highlightResponse.get("content")) {
					//if(isWithinLimit(hlsent)){
						
						sentences.add("" + hlsent + "\n");
					//}
				}
				}
				break; //only considering the first document highlighted //TODO: is it fine?
			}
		}
		
		
		LOGGER.debug("------------------------------\n");
		return sentences;

	}
	
	private static boolean isWithinLimit(String hlsent) {
		Pattern pattern = Pattern.compile("#\\w+#");
		Matcher matcher = pattern.matcher(hlsent);

		int count = 0;
		while (matcher.find())
			count++;
		
		return count>=limitingFactor-1;
	}

	/**
	 * Query title along with content
	 * @param taggedTokens
	 * @return
	 */
	private static String createTitleQuery(Map<String, String> taggedTokens){
		String titleQuery = "title:\"";
		boolean atleastOne = false;
		for(Entry<String, String> taggedToken: taggedTokens.entrySet()){
			if(taggedToken.getValue().equals("NNP")){
				titleQuery = titleQuery + taggedToken.getKey() + " ";
				atleastOne = true;
			}
		}
		
		if(!atleastOne){
			titleQuery="";
		}else {
			titleQuery= StringUtils.trim(titleQuery)+"\"";
		}
		return titleQuery;
	}
	
	
	
	private static String createQuery(Map<String, String> taggedTokens) {
		
		removeStopWords(taggedTokens);
		
		parentList = new ArrayList<List<String>>();
		for(Map.Entry<String, String> taggedToken: taggedTokens.entrySet()){
		   	Set<String> synonyms = WordNetUtil.getSynonyms(taggedToken);
		   	List<String> childList = new ArrayList<String>();
		   	childList.add(taggedToken.getKey());
		   	if(synonyms!=null) {
		   		childList.addAll(synonyms);
		   	}
		   	parentList.add(childList);
		}
		LOGGER.debug("Calculating Combinations and Ranks");
		limitingFactor = parentList.size();
		Set<List<String>> resultSet = allCombs(parentList);
		String result = assignRanks(resultSet);
		return result;
	}
	
	
	/**
	 * Create query with OR conditions (not used)
	 * @param taggedTokens
	 * @return
	 */
	private static String getAlternateQuery(Map<String, String> taggedTokens){
		String result = "(";
		List<String> keys = new ArrayList<String>(taggedTokens.keySet());
		for(int count=0; count<keys.size(); count++){
			result = result + keys.get(count);
			if(count+1<keys.size()){
				result = result +" OR ";
			}
		}
		result = result +")";
		return result;
	}
	
	/**
	 * Creates query search keys for "raw" solr search
	 * @param taggedTokens
	 * @return
	 */
	private static String getRawQuery(Map<String, String> taggedTokens){
		String result = "\"";
		List<String> keys = new ArrayList<String>(taggedTokens.keySet());

		for(int count=0; count<keys.size(); count++){
			result = result + keys.get(count);
			if(count+1<keys.size()){
				result = result +" ";
			}
		}
		result = result +"\"";
		return result;
	}
	private static void removeStopWords(Map<String, String> taggedTokens) {
		LOGGER.debug("Removing stopwords");
		Set<String> tokens = taggedTokens.keySet();
		try {
		//loading stopwords (based on http://www.ranks.nl/stopwords)
		BufferedReader br = new BufferedReader(new FileReader(Constants.STOPWORDS_LOC + "stopwords.txt"));
		String line;
		while ((line = br.readLine()) != null) {
		   if(tokens.contains(line.trim())) {
			   taggedTokens.remove(line.trim());
		   }
		}
		br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	static Set<List<String>> allCombs(List<List<String>> opts) {
		//LOGGER.debug(opts.size());
	    Set<List<String>> results = new HashSet<List<String>>();

	    if (opts.size() == 1) {
	        for (String s : opts.get(0))
	            results.add(new ArrayList<String>(Arrays.asList(s)));
	    } else
	        for (String str : opts.get(0)) {
	            List<List<String>> tail = opts.subList(1, opts.size());
	            for (List<String> combs : allCombs(tail)) {
	            	combs.add(str);
	                results.add(combs);
	            }
	        }
	    return results;
	}
	
//	public static void printGrid(String[][] a)
//	{
//		LOGGER.debug("*********");
//		for (String[] arr : a) {
//            LOGGER.debug(Arrays.toString(arr));
//        }
//		LOGGER.debug("---------");
//	}

	public static String assignRanks(Set<List<String>> set){
		String queryStr = "";
		int setCounter = 0;
		for(List<String> list: set){
			int rank = getRank(list);
			String subStr = "";
			subStr = subStr + "(";
			for(int i=0; i<list.size(); i++){
				subStr = subStr + list.get(i);
				if(i<list.size()-1){
					subStr = subStr + " AND ";
				}
			}
			subStr = subStr + ")^" + rank;
			if(setCounter < set.size()-1)
				subStr = subStr + " OR ";
			queryStr = queryStr + subStr;
			setCounter++;
		}
		LOGGER.debug(queryStr);
		return queryStr;
	}
	
	public static int getRank(List<String> list){
		int i=0;
		for(List<String> originalList: parentList){
			for(String s: list){
				if(s.equals(originalList.get(0))){
					i++;
				}
			}
		}
		return i;
	}
}
