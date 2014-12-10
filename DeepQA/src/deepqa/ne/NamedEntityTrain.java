package deepqa.ne;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import deepqa.constants.Constants;

public class NamedEntityTrain {

	public static void trainModel(String modelName){

		//load trained data into memory
		//titles marked up with <START> and <END> tags
		//one sentence per line
		File inFile = new File(Constants.MODEL_LOC+"en-ner-"+modelName+".train");

		//create NameSampleDataStream
		//converts tagged strings from trained data into NameSample objects
		//populated in next step

		NameSampleDataStream nss = null;		
		try {
			nss = new NameSampleDataStream( 
			new PlainTextByLineStream(
			new java.io.FileReader(inFile)));
			
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		//create "title" model

		TokenNameFinderModel model = null;
		int iterations = 1000; 
		int cutoff = 5; 

		try {
			model = NameFinderME.train(
			"en", //language of the training data (relevant to tokenization)
			modelName, //type of model
			nss, //the NameSample collection, created above
			(AdaptiveFeatureGenerator) null, //null=use default set of feature generators for NE detection
			Collections.<String,Object>emptyMap(), //empty, not adding additional resources to the model
			iterations, //number of iterations before the model outputs, not important
			cutoff); //lower bound for the number of times a feature exists before it is included in the model
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		//save the model to disk
		//used in testing and production

		File outFile = null;
		try {
			outFile = new File(Constants.MODEL_LOC+"en-ner-"+modelName+".bin");			
			FileOutputStream outFileStream = new FileOutputStream(outFile);
			model.serialize(outFileStream);
			outFileStream.flush();
			outFileStream.close();
			nss.close();
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static void main(String[] args) {
		trainModel("occupation");
//		trainModel("number");
//		trainModel("location");
//		trainModel("money");
//		trainModel("organization");
//		trainModel("percentage");
		//trainModel("date");
	}
}