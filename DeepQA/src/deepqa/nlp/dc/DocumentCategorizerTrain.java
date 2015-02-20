package deepqa.nlp.dc;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import deepqa.constants.Constants;

public class DocumentCategorizerTrain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DoccatModel model = null;

		InputStream dataIn = null;
		try {
		  dataIn = new FileInputStream(Constants.MODEL_TRAIN_LOC + "en-category.train");
		  ObjectStream<String> lineStream =
				new PlainTextByLineStream(dataIn, "UTF-8");
		  ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

		  model = DocumentCategorizerME.train("en", sampleStream);
		}
		catch (IOException e) {
		  // Failed to read or parse training data, training failed
		  e.printStackTrace();
		}
		finally {
		  if (dataIn != null) {
		    try {
		      dataIn.close();
		    }
		    catch (IOException e) {
		      // Not an issue, training already finished.
		      // The exception should be logged and investigated
		      // if part of a production system.
		      e.printStackTrace();
		    }
		  }
		}
		
		OutputStream modelOut = null;
		try {
		  modelOut = new BufferedOutputStream(new FileOutputStream(Constants.MODEL_LOC+"en-category.bin"));
		  model.serialize(modelOut);
		}
		catch (IOException e) {
		  // Failed to save model
		  e.printStackTrace();
		}
		finally {
		  if (modelOut != null) {
		    try {
		       modelOut.close();
		    }
		    catch (IOException e) {
		      // Failed to correctly save model.
		      // Written model might be invalid.
		      e.printStackTrace();
		    }
		  }
		}

	}

}
