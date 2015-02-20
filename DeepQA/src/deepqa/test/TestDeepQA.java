package deepqa.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

import deepqa.CategorizeQue;
import deepqa.InitiateQue;
import deepqa.dto.AnsConfRefBean;
import deepqa.nlp.NLPFactory;
import deepqa.nlp.NLPUtil;
import deepqa.nlp.POSTagger;
import deepqa.nlp.dc.DocumentCategory;
import deepqa.nlp.exceptions.CategoryNotComposableException;
import deepqa.nlp.exceptions.ContentUnavailableException;
import deepqa.solr.SOLRUtil;

public class TestDeepQA {

	static TreeMap<String, Double> responseMap;
	final static Logger LOGGER = Logger.getLogger(TestDeepQA.class);

	public static AnsConfRefBean askMe(String question, String knownAns) {
		try {

			// question = solveContext(question);

			LOGGER.debug("Question asked: " + question);
			String questionCategory = CategorizeQue
					.getQuestionCategory(question);

			LOGGER.debug("Question Category: " + questionCategory);

			Map<String, String> searchList = POSTagger.tagPOS(question);
			LOGGER.debug("Search List for Solr: " + searchList);

			List<String> highlightedText = null;
			highlightedText = new ArrayList<String>();

			highlightedText = SOLRUtil.rawquerySolr(searchList);
			LOGGER.debug("****RawQuery highlights****\n" + highlightedText);

			if (highlightedText.size() == 0) {
				highlightedText = SOLRUtil.querySolr(searchList);
				LOGGER.debug("****Query highlights****\n" + highlightedText);
			}
			
			if(highlightedText.size() == 0){
				throw new ContentUnavailableException();
			}
		
			DocumentCategory find = null;
			find = DocumentCategory.valueOf(questionCategory);
			LOGGER.debug("Loading Model: " + find.getCategory());

			SentenceDetector sdetector = NLPFactory.createSentenceDetector();

			/*
			 * sentences map stores the sentences ready for processing by the
			 * NLP system
			 */
			List<String> sentences = new ArrayList<String>();

			for (String highlightedSnip : highlightedText) {
				String[] detectedSentences = sdetector
						.sentDetect(highlightedSnip);
				/*
				 * These sentences might not be complete. Incomplete sentences 
				 * gets really bad results when fed into NLP
				 * isValidSentence checks if sentence is valid
				 */
				for (String detectedSentence : detectedSentences) {
					if (isValidSentence(detectedSentence)) {
						detectedSentence = StringUtils.replace(
								StringUtils.replace(detectedSentence, "#", ""),
								"#", "");
						sentences.add(detectedSentence);
					}
				}
			}
			LOGGER.debug(sentences);
			
			/*
			 * sentences are now fed into the NLP
			 */
			responseMap = (TreeMap<String, Double>) NLPUtil
					.fetchAnsWithConfidence(sentences, find.getCategory());

			if (responseMap != null && responseMap.size() > 0) {
				for (Map.Entry<String, Double> entry : responseMap.entrySet()) {
					String key = entry.getKey();
					Double value = entry.getValue();

					LOGGER.debug(key + " => " + value);
					
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
			// TestCases.log("Known Answer: " + knownAns);
			LOGGER.debug(responseMap.firstEntry().getKey());

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

			return ansConfRefBean;

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (SolrServerException e) {
			LOGGER.error(e.getMessage());
		} catch (CategoryNotComposableException e) {
			LOGGER.error(e.getMessage());
		} catch (ContentUnavailableException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Checks if a sentence starts with a Capital letter and has at least 3 words
	 * @param hlsent
	 * @return
	 */
	private static boolean isValidSentence(String hlsent) {
		return Character.isUpperCase(hlsent.charAt(0)) && hlsent.split(" ").length>3;
	}

	/**
	 * Solving context based on previous results 
	 * @param question
	 * @return
	 */
	private static String solveContext(String question) {

		if (Pattern.compile("\\she\\s").matcher(question).find())
			question = question.replaceAll("\\she\\s", " " + person_ctx + " ");
		if (Pattern.compile("\\sshe\\s").matcher(question).find())
			question = question.replaceAll("\\sshe\\s", " " + person_ctx + " ");
		if (Pattern.compile("\\sit\\s").matcher(question).find()) {
			question = question.replaceAll("\\sit\\s", " " + organization_ctx + " ");
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