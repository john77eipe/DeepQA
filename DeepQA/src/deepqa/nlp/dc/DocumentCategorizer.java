package deepqa.nlp.dc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.util.InvalidFormatException;
import deepqa.constants.Constants;
import deepqa.nlp.NLPFactory;
import deepqa.nlp.exceptions.CategoryNotComposableException;

public class DocumentCategorizer {

	final static Logger LOGGER = Logger.getLogger(DocumentCategorizer.class);
	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		
		InputStream is = new FileInputStream(Constants.MODEL_LOC + "en-category.bin");
		DoccatModel m = new DoccatModel(is);
		String inputText = "Where was the last?";
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(m);
		double[] outcomes = myCategorizer.categorize(inputText);
		for (double d : outcomes) {
			LOGGER.debug(d);
		}
		int category = myCategorizer.getNumberOfCategories();
		LOGGER.debug(category);
		LOGGER.debug(myCategorizer.getBestCategory(outcomes));
	}
	
	public static String getQuestionCategory(String inputText) throws CategoryNotComposableException {
		DocumentCategorizerME myCategorizer = NLPFactory.createCategorizer();
		double[] outcomes = myCategorizer.categorize(inputText);
		
		String category = myCategorizer.getBestCategory(outcomes);
		LOGGER.debug("Category formulated: "+category);
		
		//for cateogries W, B, X we translate it to other categories since 
		//no models exists for those
		
		//translation is straight forward: and doesn't use any NLP principles
		if(specialCategories.contains(category)){
			//find the question type - old style
			List<String> input = Arrays.asList(inputText.toLowerCase().split(" "));
			LOGGER.debug("matched: "+input);
			QuestionType[] questionTypes = QuestionType.values();
			for(QuestionType questionType: questionTypes){
				if(input.contains(questionType.toString())){
					return questionType.getCategory();
				}
			}
			//if it reaches here it means the category was not able to be formulated
			throw new CategoryNotComposableException();
		}
		
		return category;
	}
	
	static List<String> specialCategories = new ArrayList<String>();
	static {
		specialCategories.add("W");
		specialCategories.add("B");
		specialCategories.add("X");
	}
	
	enum QuestionType {
		where("L"),
		when("T"),
		who("P");
		private String type;
		QuestionType(String type){
			this.type = type;
		}
		public String getCategory(){
			return this.type;
		}
	}
	

}
