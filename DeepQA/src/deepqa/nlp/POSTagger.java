package deepqa.nlp;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import org.apache.log4j.Logger;

public class POSTagger {
	
	final static Logger LOGGER = Logger.getLogger(POSTagger.class);
	
	public static Map<String, String> tagPOS(String sentence) throws IOException {
		
		Map<String, String> taggedTokens = new HashMap<String, String>();
		
		// Load only the needed POS tags as string
		List<String> posTags = POSTag.valuesAsStr(); 
		LOGGER.debug("Selected Tags: "+posTags);
		
		
		// PerformanceMonitor perfMon = new PerformanceMonitor(System.err,"sent");

		POSTaggerME tagger = NLPFactory.createTaggger();

		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(sentence));

		// perfMon.start();

		// reading questions line by line - though in our case there is only one question
		String line;
		while ((line = lineStream.read()) != null) {

			// word tokens
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			
			String[] tags = tagger.tag(whitespaceTokenizerLine);
			LOGGER.debug("Tagged Tokens:" + new POSSample(whitespaceTokenizerLine, tags));
			for (int tagCounter = 0; tagCounter < tags.length; tagCounter++) {
				if (posTags.contains(tags[tagCounter])) {
					taggedTokens.put(whitespaceTokenizerLine[tagCounter], tags[tagCounter]);
				}
			}
			// perfMon.incrementCounter();
		}
		lineStream.close();
		// perfMon.stopAndPrintFinalResult();
		return taggedTokens;

	}
}
