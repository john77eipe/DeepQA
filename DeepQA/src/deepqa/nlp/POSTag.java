package deepqa.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parts of speech tags that we are concered about.
 * @author admin
 *
 */
public enum POSTag {
	NN,
	NNS,
	NNP,
	NNPS,
	PDT,
	POS,
	PRP,
	PRP$,
	JJR,
	JJ,
	JJS,
	VB,
	VBD,
	VBG,
	VBN,
	VBP,
	VBZ;
	
	public static List<String> valuesAsStr(){
		List<String> tagsAsStr =  new ArrayList<String>();
		List<POSTag> tags = Arrays.asList(POSTag.values());
		for(POSTag tag:tags){
			tagsAsStr.add(tag.name());
		}
		return tagsAsStr;
	}
}
