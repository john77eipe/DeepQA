package deepqa.ne;

//Test a custom OpenNLP model for NER of book titles
//See https://gist.github.com/johnmiedema/4020deea875ce306971e

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import deepqa.constants.Constants;

public class NamedEntityTest {

	//1. Test sentences that do not exist in training data
	// static String sentence = "During his first two years in office test";

	//2. More complex sentence structure
	// static String sentence = "What is the setting of Fyodor Dostoyevsky's novel Crime and Punishment?";

	//3. Title in quotes
	//static String sentence = "Who wrote \"Reading in the Brain?\"";	

	//4. Title at beginning of the sentence
	static String sentence = "When Disney was 16, he dropped out of school";

	//5. Mess around with case of title
	//static String sentence = "Who is the author of the Call of the Wild?";

	//6. All lower case
	//static String sentence = "Who is the author of the call of the wild?";

	//7. "the" is a different part of speech
	//static String sentence = "Who is the author of the Odyssey?";

	public static void main(String[] args) {

		InputStream modelInToken = null;
		InputStream modelIn = null;

		try {

			//convert sentence into tokens
	    	modelInToken = new FileInputStream(Constants.MODEL_LOC+"en-token.bin");
	    	TokenizerModel modelToken = new TokenizerModel(modelInToken); 
	    	Tokenizer tokenizer = new TokenizerME(modelToken);  
	    	String tokens[] = tokenizer.tokenize(sentence);
	    	System.out.println(tokens.length);
	    	//load custom titles model
	    	modelIn = new FileInputStream(Constants.MODEL_LOC+"en-ner-occupation.bin");

	    	   

	    	//create NameFinder and call find method
	    	TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
	    	NameFinderME nameFinder = new NameFinderME(model);
	    	Span nameSpans[] = nameFinder.find(tokens);
	    	System.out.println(nameSpans.length);

	    	//find probabilities for names
	    	double[] spanProbs = nameFinder.probs(nameSpans);

	    	//print titles with probabilities
	    	for( int i = 0; i<nameSpans.length; i++) {

	    		int tokensStart = nameSpans[i].getStart();
	    		int tokensEnd = nameSpans[i].getEnd();
	    		String title = "";	    		
	    		for (int j = tokensStart; j <= tokensEnd; j++) {
	    			title += tokens[j] + " ";
	    		}	    		
	    		System.out.println(title);

	    		System.out.println("Probability is: "+spanProbs[i]);
	    	}		    	

	    	//Results

	    	//1. Extra punctuation likely related to tokenization method.
	    	//The Call of the Wild ? 
	    	//Probability is: 0.9556878839087964

	    	//2. Lower probability. Maybe because of more complex sentence structure?
	    	//Crime and Punishment ? 
	    	//Probability is: 0.8622695215302271

	    	//3. Quotes not a problem.
	    	//Reading in the Brain ? 
	    	//Probability is: 0.95192707478283961

	    	//4. Lower probability. Maybe because title is at the beginning of sentence? More complex, like 2.
	    	//The Call of the Wild , 
	    	//Probability is: 0.8272024223804438

	    	//5. Lowercase "the" not included. Makes sense.
	    	//Call of the Wild ? 
	    	//Probability is: 0.8526001988043367

	    	//6. No title recognized when everything in lowercase. Clearly case plays a big role.

	    	//7. Odd. The lowercase "the" included with the title, unlike 5. Note lowest probability.
	    	//the Odyssey ? 
	    	//Probability is: 0.6439045773599029
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		finally {
			  try { if (modelInToken != null) modelInToken.close(); } catch (IOException e){};
			  try { if (modelIn != null) modelIn.close(); } catch (IOException e){};
		}
	}
}