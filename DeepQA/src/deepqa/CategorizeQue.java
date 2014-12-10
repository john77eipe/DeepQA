package deepqa;

import java.io.IOException;

import opennlp.tools.util.InvalidFormatException;
import deepqa.nlp.dc.DocumentCategorizer;
import deepqa.nlp.exceptions.CategoryNotComposableException;


public class CategorizeQue {
	
	public static String getQuestionCategory(String question) throws InvalidFormatException, IOException, CategoryNotComposableException{
		String questionCategory = DocumentCategorizer.getQuestionCategory(question);
		return questionCategory;
	}
}
