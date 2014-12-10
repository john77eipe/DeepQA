package deepqa.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import opennlp.tools.coref.DefaultLinker;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;

import deepqa.constants.Constants;

public class NLPFactory {

	final static Logger LOGGER = Logger.getLogger(NLPFactory.class);
	
	public static DocumentCategorizerME _documentCategorizer = null;
	/**
	 * DocumentCategorizer factory
	 * @return
	 */
	public static DocumentCategorizerME createCategorizer() {
		if(_documentCategorizer == null){
			InputStream modelIn = null;
			try {
				modelIn = new FileInputStream(Constants.MODEL_LOC+"en-category.bin");
				final DoccatModel m = new DoccatModel(modelIn);
				
				_documentCategorizer =   new DocumentCategorizerME(m);
				
			}catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (final IOException e) {
					} // oh well!
				}
			}
		}
		return _documentCategorizer;
	}
	
	private static SentenceDetector _sentenceDetector = null;
	/**
	 * SentenceDetector factory
	 * @return
	 */
	public static SentenceDetector createSentenceDetector() {
		if (_sentenceDetector == null) {
			InputStream modelIn = null;
			try {
				// Loading sentence detection model
				modelIn = new FileInputStream(Constants.MODEL_LOC+"en-sent.bin");
				final SentenceModel sentenceModel = new SentenceModel(modelIn);

				_sentenceDetector = new SentenceDetectorME(sentenceModel);

			} catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (final IOException e) {
					} // oh well!
				}
			}
		}
		return _sentenceDetector;
	}

	private static Tokenizer _tokenizer = null;
	/**
	 * Tokenizer factory
	 * @return
	 */
	public static Tokenizer createTokenizer() {
		if (_tokenizer == null) {

			InputStream modelIn = null;
			try {
				// Loading tokenizer model
				modelIn = new FileInputStream(Constants.MODEL_LOC+"en-token.bin");
				final TokenizerModel tokenModel = new TokenizerModel(modelIn);

				_tokenizer = new TokenizerME(tokenModel);

			} catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (final IOException e) {
					}
				}
			}
		}
		return _tokenizer;
	}

	private static POSTaggerME _posTagger = null;
	/**
	 * POSTaggerME factory
	 * @return
	 */
	public static POSTaggerME createTaggger() {
		InputStream modelIn = null;
		try {
			// Loading tokenizer model
			modelIn = new FileInputStream(Constants.MODEL_LOC+"en-pos-maxent.bin");
			final POSModel posModel = new POSModel(modelIn);

			_posTagger = new POSTaggerME(posModel);

		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {
				}
			}
		}
		return _posTagger;
	}

	private static Parser _nameFinder = null;
	/**
	 * Parser factory
	 * @return
	 */
	public static Parser createParser() {
		if (_nameFinder == null) {
			InputStream modelIn = null;
			try {
				// Loading the parser model
				modelIn = new FileInputStream(Constants.MODEL_LOC+"en-parser-chunking.bin");
				final ParserModel parseModel = new ParserModel(modelIn);

				_nameFinder = ParserFactory.create(parseModel);
				
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (final IOException e) {
					} // oh well!
				}
			}
		}
		return _nameFinder;
	}

	private static Linker _linker = null;
	/**
	 * Linker factory
	 * @return
	 */
	public static Linker createLinker() {
		try {
			// coreference resolution linker
			_linker = new DefaultLinker(
			// LinkerMode should be TEST
			// Note: I tried LinkerMode.EVAL for a long time
			// before realizing that this was the problem
					"coref", LinkerMode.TEST);

		} catch (final IOException ioe) {
			ioe.printStackTrace();
		}
		return _linker;
	}

	private static NameFinderME _locationFinder = null;
	private static NameFinderME _dateFinder = null;
	private static NameFinderME _percentageFinder = null;
	private static NameFinderME _moneyFinder = null;
	private static NameFinderME _numberFinder = null;
	private static NameFinderME _occupationFinder = null;
	private static NameFinderME _organizationFinder = null;
	private static NameFinderME _personFinder = null;
	private static NameFinderME _timeFinder = null;
	
	public static NameFinderME createNameFinder(NLPModels modelType) {
		switch(modelType){
			case LOCATION:
				if(_locationFinder == null){
					_locationFinder = createSpecificFinder(modelType.getName());
				}
				return _locationFinder;
			case DATE:
				if(_dateFinder == null){
					_dateFinder = createSpecificFinder(modelType.getName());
				}
				return _dateFinder;	
			case PERCENTAGE:
				if(_percentageFinder == null){
					_percentageFinder = createSpecificFinder(modelType.getName());
				}
				return _percentageFinder;
			case MONEY:
				if(_moneyFinder == null){
					_moneyFinder = createSpecificFinder(modelType.getName());
				}
				return _moneyFinder;
			case NUMBER:
				if(_numberFinder == null){
					_numberFinder = createSpecificFinder(modelType.getName());
				}
				return _numberFinder;
			case OCCUPATION:
				if(_occupationFinder == null){
					_occupationFinder = createSpecificFinder(modelType.getName());
				}
				return _occupationFinder;
			case ORGANIZATION:
				if(_organizationFinder == null){
					_organizationFinder = createSpecificFinder(modelType.getName());
				}
				return _organizationFinder;
			case PERSON:
				if(_personFinder == null){
					_personFinder = createSpecificFinder(modelType.getName());
				}
				return _personFinder;
			case TIME:
				if(_timeFinder == null){
					_timeFinder = createSpecificFinder(modelType.getName());
				}
				return _timeFinder;
			default:
				LOGGER.error("Should always match some model");
		}
		return null;
	}

	public static NameFinderME createSpecificFinder(String modelType) {
		NameFinderME nameFinder = null;
		InputStream modelIn = null;
		try {
			// Loading the parser model
			modelIn = new FileInputStream(Constants.MODEL_LOC + "en-ner-"
					+ modelType + ".bin");
			nameFinder = new NameFinderME(new TokenNameFinderModel(modelIn));

		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {
				} // oh well!
			}
		}
		return nameFinder;
	}
	
	public static void main(String[] args) {
		// testing sentence detector
		// SentenceDetector sentenceDetector = createSentenceDetector();
		// System.out.println(Arrays.asList(sentenceDetector
		// .sentDetect("There is a model. I used that model.")));

		// testing tokenizer
		// Tokenizer tokenizer = createTokenizer();
		// System.out.println(Arrays.asList(tokenizer
		// .tokenize("There is a model, far away")));

		// testing tagger
		 POSTaggerME tagger = createTaggger();
		 Tokenizer tokenizer = createTokenizer();
		 String[] tokens = tokenizer.tokenize("Who is founder of apple");
		 System.out.println(Arrays.asList(tokens));
		 System.out.println(Arrays.asList(tagger.tag(tokens)));

		// testing parsing
		// Tokenizer tokenizer = createTokenizer();
		// String[] tokens = tokenizer.tokenize("There is a model, far away");
		// final Parse parse = parseSentence("There is a model, far away");
		// parse.show();
	
		
		// testing co-ref
		// http://blog.dpdearing.com/2011/12/how-to-use-the-opennlp-1-5-0-parser/
		
		// list of document mentions
		
		/*
		final List<Mention> document = new ArrayList<Mention>();

		// generate the sentence parse tree
		final Parse parse = parseSentence("There is a model, far away");

		final DefaultParse parseWrapper = new DefaultParse(parse, 0);
		final Mention[] extents = createLinker().getMentionFinder()
				.getMentions(parseWrapper);

		// Note: taken from TreebankParser source...
		for (int ei = 0, en = extents.length; ei < en; ei++) {
			// construct parses for mentions which don't have constituents
			if (extents[ei].getParse() == null) {
				// not sure how to get head index, but it doesn't seem to be
				// used at this point
				final Parse snp = new Parse(parse.getText(),
						extents[ei].getSpan(), "NML", 1.0, 0);
				parse.insert(snp);
				// setting a new Parse for the current extent
				extents[ei].setParse(new DefaultParse(snp, 0));
			}

			document.addAll(Arrays.asList(extents));
		}
		 
		   if (!document.isEmpty()) {
			   //System.out.println(document.toArray(new Mention[0]));
			   System.out.println("*********");
			   Mention[] tt = document.toArray(new Mention[0]);
			   System.out.println(document.size());
			   System.out.println(tt.length);
			   for(Mention t:tt){
				   System.out.println(t);
			   }
		      DiscourseEntity[] discourseEntities = createLinker().getEntities(document.toArray(new Mention[0]));
		      
		      for(DiscourseEntity discourseEntity: discourseEntities){
		    	  System.out.println(discourseEntity);
		      }
		   }
		   */
	}

	private static Parse parseSentence(final String text) {
		final Parse p = new Parse(text,
		// a new span covering the entire text
				new Span(0, text.length()),
				// the label for the top if an incomplete node
				AbstractBottomUpParser.INC_NODE,
				// the pro bability of this parse...uhhh...?
				1,
				// the token index of the head of this parse
				0);

		// make sure to initialize the _tokenizer correctly
		final Span[] spans = createTokenizer().tokenizePos(text);

		for (int idx = 0; idx < spans.length; idx++) {
			final Span span = spans[idx];
			// flesh out the parse with individual token sub-parses
			p.insert(new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 0,
					idx));
		}

		Parse actualParse = createParser().parse(p);
		return actualParse;
	}
}
