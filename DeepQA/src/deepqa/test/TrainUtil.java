package deepqa.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import deepqa.ne.NamedEntityTrain;
import deepqa.nlp.dc.DocumentCategory;

public class TrainUtil {
	public static void retrainModel(String questionCategory) {
		String selectedModel = DocumentCategory.valueOf(questionCategory).getCategory();
		String lineToAdd;
		Scanner scanIn = new Scanner(System.in);
		System.out.println("Enter line:");
		lineToAdd = scanIn.nextLine();
		scanIn.close();

		boolean status = trainModel(selectedModel, lineToAdd);
		System.out.println("Training status: "+(status?"success":"failed"));

	}

	public static boolean trainModel(String selectedModel, String line) {

		String modelFileName = "";
		System.out.println("selectedModel: "+selectedModel);
		switch (selectedModel) {
		case "person":
			modelFileName = "en-ner-person.train";
			break;
		case "occupation":
			modelFileName = "en-ner-occupation.train";
			break;
		case "number":
			modelFileName = "en-ner-number.train";
			break;
		case "location":
			modelFileName = "en-ner-location.train";
			break;
		case "date":
			modelFileName = "en-ner-date.train";
			break;
		default:
			System.out.println("Model not found");
			return false;

		}
		addToTrainFile(modelFileName, line);
		NamedEntityTrain.trainModel(selectedModel);

		return true;
	}

	public static void addToTrainFile(String modelFileName, String line) {
		try {
			System.out.println(modelFileName);
			FileWriter fw = new FileWriter(modelFileName, true); 
			fw.write("\n" + line);// appends the string to the file
			fw.flush();
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
//	public static void retrainModel2(String questionCategory, List<String> splitsentences) {
//		Scanner scanIn = new Scanner(System.in);
//		System.out.println("Correct Ans: ");
//		Integer lineNo = Integer.valueOf(scanIn.nextLine());
//		 System.out.println(NLPUtil.ansWithSentence.get(lineNo));
//		List<String> oldWords = new ArrayList(TestDeepQA.responseMap.keySet());
//		
//		System.out.println("oldWord: "+oldWords.get(lineNo));
//		
//		String newWord = "<START> "+oldWords.get(lineNo)+" <END>";
//		System.out.println("newWord: "+newWord);
//		String newLine = NLPUtil.ansWithSentence.get(lineNo).replaceAll(oldWords.get(lineNo),newWord);
//		System.out.println(newLine);
//		String selectedModel = DocumentCategory.valueOf(questionCategory).getCategory();
//		
//		boolean status = trainModel(selectedModel, newLine);
//		System.out.println("Training status: "+(status?"success":"failed"));
//	}
}
