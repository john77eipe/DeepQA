package deepqa.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

import deepqa.CategorizeQue;
import deepqa.InitiateQue;
import deepqa.constants.Constants;
import deepqa.dto.AnsConfRefBean;
import deepqa.nlp.NLPFactory;
import deepqa.nlp.NLPUtil;
import deepqa.nlp.POSTagger;
import deepqa.nlp.dc.DocumentCategory;
import deepqa.nlp.exceptions.CategoryNotComposableException;
import deepqa.solr.SOLRUtil;

public class TestDeepQA {

	static TreeMap<String, Double> responseMap;
	final static Logger LOGGER = Logger.getLogger(TestDeepQA.class);

	public static AnsConfRefBean askMe(String question, String knownAns) {
		try {
			
			//question = solveContext(question);
			
			LOGGER.debug("Question asked: " + question);
			String questionCategory = CategorizeQue
					.getQuestionCategory(question);

			LOGGER.debug("Question Category: " + questionCategory);

			Map<String, String> searchList = POSTagger.tagPOS(question);
			LOGGER.debug("Search List for Solr: " + searchList);
			
			List<String> highlightedParas = null;
			highlightedParas = new ArrayList<String>();
			
			highlightedParas = SOLRUtil.rawquerySolr(searchList);
			LOGGER.debug("****RawQuery highlights****\n" + highlightedParas);

//			if (hlsentences.size() == 0) {
//				hlsentences = SOLRUtil.querySolr(searchList);
//				LOGGER.debug("****Query highlights****\n" + hlsentences);
//			}
//
//			if (hlsentences.size() == 0) {
//				hlsentences = SOLRUtil.altquerySolr(searchList);
//				LOGGER.debug("****AltQuery highlights****\n" + hlsentences);
//			}
			
			/*
			DocumentCategory find = null;
			find = DocumentCategory.valueOf(questionCategory);
			LOGGER.debug("Loading Model: " + find.getCategory());

			
			SentenceDetector sdetector = NLPFactory.createSentenceDetector();
			
			//split sentences and find context information
			Map<String, Double> splitsentences = new LinkedHashMap<String, Double>();
			for (String hlsentense : highlightedParas) {
				String[] sentences = sdetector.sentDetect(hlsentense);
				for (String sentence : sentences) {
					double chanceScore = findChanceScore(sentence);
					sentence = StringUtils.replace(
							StringUtils.replace(sentence, "#", ""), "#", "");
					splitsentences.put(sentence, chanceScore);
				}
			}
			LOGGER.debug(splitsentences);
			responseMap = (TreeMap<String, Double>) NLPUtil.multiModel(
					splitsentences, find.getCategory());

			if (responseMap != null && responseMap.size() > 0) {
				for (Map.Entry<String, Double> entry : responseMap.entrySet()) {
					String key = entry.getKey();
					Double value = entry.getValue();

					LOGGER.debug(key + " => " + value);
					LOGGER.debug(NLPUtil.ansWithSentence.get(key));
					TestCases.log(key + " => " + value);
				}
			}

			if (responseMap == null || responseMap.size() == 0) {
				responseMap = (TreeMap<String, Double>) NLPUtil.multiModel_(
						splitsentences, find.getCategory());

				if (responseMap != null && responseMap.size() > 0) {
					for (Map.Entry<String, Double> entry : responseMap
							.entrySet()) {
						String key = entry.getKey();
						Double value = entry.getValue();

						LOGGER.debug(key + " => " + value);
						LOGGER.debug(NLPUtil.ansWithSentence.get(key));
						TestCases.log(key + " => " + value);
					}
				}
			}
			if (responseMap != null && responseMap.size() > 0) {
				if (find.toString().equals("P")) {
					person_ctx = responseMap.firstKey();
				} else if (find.toString().equals("O")) {
					organization_ctx = responseMap.firstKey();
				} else if (find.toString().equals("T")) {
					date_ctx = responseMap.firstKey();
				}
			}
			TestCases.log("Known Answer: " + knownAns);
			LOGGER.debug(responseMap.firstEntry().getKey());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			AnsConfRefBean ansConfRefBean = new AnsConfRefBean();
			ansConfRefBean.setAnsWithConf(responseMap);
			ansConfRefBean.setAnsWithSentence(NLPUtil.ansWithSentence);

			// LOGGER.debug("Would you like to train:(y/n): ");
			// Scanner scanIn = new Scanner(System.in);
			// String choice = scanIn.nextLine();
			// LOGGER.debug(choice);
			// if(choice.equalsIgnoreCase("y")){
			// TrainUtil.retrainModel(questionCategory);
			// }

			return ansConfRefBean;*/
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (SolrServerException e) {
			LOGGER.error(e.getMessage());
		} catch (CategoryNotComposableException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	private static double findChanceScore(String hlsent) {
		Pattern pattern = Pattern.compile("#\\w+#");
		Matcher matcher = pattern.matcher(hlsent);

		int count = 0;
		while (matcher.find())
			count++;

		LOGGER.debug("Count:" + count);
		double score = 0;
		if (SOLRUtil.limitingFactor > 0) {
			score = count / SOLRUtil.limitingFactor;
		}
		return score;
	}
	
	private static String solveContext(String question){

		if (Pattern.compile("\\she\\s").matcher(question).find())
			question = question.replaceAll("\\she\\s", " " + person_ctx + " ");
		if (Pattern.compile("\\sshe\\s").matcher(question).find())
			question = question.replaceAll("\\sshe\\s", " " + person_ctx + " ");
		if (Pattern.compile("\\sit\\s").matcher(question).find()) {
			question = question.replaceAll("\\sit\\s", " " + organization_ctx
					+ " ");
		}
		return question;
	}

	public static void main(String[] args) throws IOException,
			SolrServerException {
		String question = InitiateQue.askQuestion();
		askMe(question, null);

	}

	public static String person_ctx = "";
	public static String organization_ctx = "";
	public static String date_ctx = "";
}