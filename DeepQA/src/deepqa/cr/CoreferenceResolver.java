package deepqa.cr;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deepqa.constants.Constants;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class CoreferenceResolver {
	private static final String POS_MODEL_NAME = "en-pos-maxent.bin";
	private static final String TOKENIZER_MODEL_NAME = "en-token.bin";
	private static final String NER_PERSON_MODEL = "en-ner-person.bin";
	private static final String NER_PERSON_MODEL_CUSTOM = "en-ner-person-custom.bin";

	/**
	 * @param input
	 * @return output
	 * @throws IOException
	 */
	public static String parseAndResolve(String input) throws IOException {
		System.out.println("CoreferenceResolver.parseAndResolve()");
		List<WordTagBean> wordsAndTags = tagWords(input);
		// Iterate sentences
		for (WordTagBean wordTagBean : wordsAndTags) {
			String[] tags = wordTagBean.getTags();
			String[] words = wordTagBean.getWords();
			List<String> namesList = findNames(words);
			String name = null;
			for (int i = 0; i < words.length; i++) {
				if (tags[i].equals("NNP") && namesList.contains(words[i])) {
					// System.out.print("Name: ");
					name = words[i];
				} else if (tags[i].equals("PRP") || tags[i].equals("PRP$")) {
					words[i] = replacePreposition(words[i], name);
				}

				// System.out.println(words[i] + " " + tags[i]);
			}
		}
		String output = detokenize(wordsAndTags);
		return output;
	}

	private static String detokenize(List<WordTagBean> wordsAndTags) {
		System.out.println("CoreferenceResolver.detokenize()");
		StringBuffer strBuff = new StringBuffer();
		for (WordTagBean wordTagBean : wordsAndTags) {
			String[] words = wordTagBean.getWords();
			for (String word : words) {
				strBuff.append(word + " ");
			}
		}
		return strBuff.toString();
	}

	private static String replacePreposition(String preposition, String name) {
		if (preposition.equalsIgnoreCase("he")
				|| preposition.equalsIgnoreCase("she")) {
			return name;
		} else if (preposition.equalsIgnoreCase("his")
				|| preposition.equalsIgnoreCase("her")) {
			return name + "'s";
		}
		return preposition;
	}

	private static List<String> findNames(String[] words)
			throws FileNotFoundException {
		System.out.println("CoreferenceResolver.findNames()");
		List<String> namesList = new ArrayList<String>();
		if (words != null && words.length > 0) {
			System.out.println(Arrays.asList(words));
			extractNameWithGivenModel(words, namesList, NER_PERSON_MODEL);
			//extractNameWithGivenModel(words, namesList, NER_PERSON_MODEL_CUSTOM);
		}
		return namesList;
	}

	private static void extractNameWithGivenModel(String[] words,
			List<String> namesList, String modelName)
			throws FileNotFoundException {
		InputStream modelIn = new FileInputStream(Constants.MODEL_LOC+modelName);
		try {
			TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			NameFinderME nameFinder = new NameFinderME(model);
			Span nameSpans[] = nameFinder.find(words);
			for (Span span : nameSpans) {
				namesList.add(words[span.getStart()]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static List<WordTagBean> tagWords(String input) throws IOException {
		System.out.println("CoreferenceResolver.tagWords()");
		List<WordTagBean> wordTagList = new ArrayList<WordTagBean>();
		POSModel posModel = new POSModelLoader().load(new File(Constants.MODEL_LOC+POS_MODEL_NAME));
		InputStream tokenizerModel = new FileInputStream(Constants.MODEL_LOC+TOKENIZER_MODEL_NAME);
		TokenizerModel model = new TokenizerModel(tokenizerModel);

		Tokenizer tokenizer = new TokenizerME(model);
		POSTaggerME tagger = new POSTaggerME(posModel);

		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));

		String line;
		while ((line = lineStream.read()) != null) {
			WordTagBean wordTag = new WordTagBean();
			String words[] = tokenizer.tokenize(line);
			String[] tags = tagger.tag(words);
			wordTag.setTags(tags);
			wordTag.setWords(words);
			wordTagList.add(wordTag);
		}
		if (tokenizerModel != null) {
			tokenizerModel.close();
		}
		return wordTagList;
	}

	public static void main(String[] args) throws IOException {
		String input = " Krishna is in America. This was done by Krishna. "
				+ "Mary lives in India. She loves her job. Dave is good at cooking. He cooks for all of us.";
//		File f = new File(Constants.MODEL_LOC+"data.txt");
//		FileInputStream fin = new FileInputStream(f);
//		byte[] buffer = new byte[(int) f.length()];
//		new DataInputStream(fin).readFully(buffer);
//		fin.close();
//		input = new String(buffer, "UTF-8");
		System.out.println(input);
		String output = parseAndResolve(input);
		System.out.println("*********");
		System.out.println(output);
	}
}
