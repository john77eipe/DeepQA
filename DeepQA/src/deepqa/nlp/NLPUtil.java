package deepqa.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import deepqa.constants.Constants;
import deepqa.solr.SOLRUtil;


public class NLPUtil {
	
	public static Map<String, String> ansWithSentence;
	
	private static String currentSentence; 
	
	final static Logger LOGGER = Logger.getLogger(NLPUtil.class);
	
	public static Map<String, Double> multiModel(Map<String, Double> splitsentences, String find)
	{
		NameFinderME finder =null;
		finder = NLPFactory.createNameFinder(NLPModels.valueOf(find));
		
		
		Span[] spans = null;
		Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
		Map<String, Double> ansWithConf = new HashMap<String, Double>();
		ansWithSentence = new HashMap<String, String>();
		LOGGER.debug("Total sentences: "+splitsentences.size());
		
		for(Entry<String, Double> sentence: splitsentences.entrySet()) {
			LOGGER.debug("\nStarting processing:");
			String[] tokens = tokenizer.tokenize(sentence.getKey());
			currentSentence =  sentence.getKey();
			
			LOGGER.debug(Arrays.asList(tokens));
			spans = finder.find(tokens);

			double[] confidences = finder.probs(spans);
			
			createAnsWithConf(spans, tokens, confidences, ansWithConf, sentence.getValue());
		}
		
		LOGGER.debug("\n\n");
		LOGGER.debug(ansWithConf);
		return sortByComparator(ansWithConf);
	}
	
	
	public static Map<String, Double> multiModel_(Map<String, Double> splitsentences, String find)
			throws IOException {
		NameFinderME finder =null;
		try {
			finder = new NameFinderME(new TokenNameFinderModel(
					new FileInputStream(new File(Constants.MODEL_LOC+"en-ner-" + find + "-custom.bin"))));
		} catch (IOException e) {
			LOGGER.debug("File Not Found");
			return null;
		}

		Span[] spans = null;
		Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
		Map<String, Double> ansWithConf = new HashMap<String, Double>();
		ansWithSentence = new HashMap<String, String>();

		for(Entry<String, Double> sentence: splitsentences.entrySet()) {
			LOGGER.debug("\nStarting processing:");
			String[] tokens = tokenizer.tokenize(sentence.getKey());
			
			
			LOGGER.debug(Arrays.asList(tokens));
			spans = finder.find(tokens);

			double[] confidences = finder.probs(spans);
			
			createAnsWithConf(spans, tokens, confidences, ansWithConf, sentence.getValue());
		}

		LOGGER.debug("\n\n");
		LOGGER.debug(ansWithConf);
		return sortByComparator(ansWithConf);
	}
	

	private static void createAnsWithConf(Span[] spans, String[] tokens, double[] probs, Map<String, Double> ansWithConf, Double chanceScore) {
		
		double p = 0.0;
		for (int spanCounter = 0; spanCounter < spans.length; spanCounter++) {
			StringBuilder cb = new StringBuilder();
			for (int ti = spans[spanCounter].getStart(); ti < spans[spanCounter].getEnd(); ti++) {
				cb.append(tokens[ti]).append(" ");
				p = probs[spanCounter];
			}
			
			LOGGER.debug(spans[spanCounter].getType() + " : ");
			LOGGER.debug(cb.substring(0, cb.length() - 1));
			String foundWord = cb.substring(0, cb.length() - 1);
			//reducing the confidence if the same word occurs in the question TODO:case sensitivity
			if(checkIfWordExistsInQuestion(foundWord)){
				p = p - 0.5;
			}
			p = p + chanceScore;
			LOGGER.debug(p);
			if(ansWithConf.containsKey(foundWord)){
				if(ansWithConf.get(foundWord) < p) {
					ansWithConf.put(foundWord, p);
				}
			}else {
				ansWithConf.put(foundWord, p);
			}
			ansWithSentence.put(foundWord, currentSentence);
			LOGGER.debug("ansWithSentence: "+ansWithSentence);
		}
	}
	
	private static boolean checkIfWordExistsInQuestion(String word){
		word = word.toLowerCase();
		for(String token: SOLRUtil.tokens){
			token = token.toLowerCase();
			if(word.contains(token)){
				return true;
			}
		}
		return false;
	}

	private static int checkSimilarityBetweenQuestionAndSentense(String[] words){
		int similarity = 0;
		for(String word: words){
			word = word.toLowerCase();
			for(String token: SOLRUtil.tokens){
				token = token.toLowerCase();
				if(word.contains(token)){
					similarity++;
				}
			}
		}
		return similarity;
	}
	
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap) {
		 
		// Convert Map to List
		List<Map.Entry<String, Double>> list = 
			new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
                                           Map.Entry<String, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<String, Double> sortedMap = new TreeMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
