/**
 * SimMetrics - SimMetrics is a java library of Similarity or Distance
 * Metrics, e.g. Levenshtein Distance, that provide float based similarity
 * measures between String Data. All metrics return consistant measures
 * rather than unbounded similarity scores.
 *
 * Copyright (C) 2005 Sam Chapman - Open Source Release v1.1
 *
 * Please Feel free to contact me about this library, I would appreciate
 * knowing quickly what you wish to use it for and any criticisms/comments
 * upon the SimMetric library.
 *
 * email:       s.chapman@dcs.shef.ac.uk
 * www:         http://www.dcs.shef.ac.uk/~sam/
 * www:         http://www.dcs.shef.ac.uk/~sam/stringmetrics.html
 *
 * address:     Sam Chapman,
 *              Department of Computer Science,
 *              University of Sheffield,
 *              Sheffield,
 *              S. Yorks,
 *              S1 4DP
 *              United Kingdom,
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package gov.nist.msi.ssmpi.similarity.similaritymetrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.tokenisers.InterfaceTokeniser;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserWhitespace;

/**
 * Package: uk.ac.shef.wit.simmetrics.similaritymetrics.jaccard
 * Description: uk.ac.shef.wit.simmetrics.similaritymetrics.jaccard implements a

 * Date: 02-Apr-2004
 * Time: 15:26:19
 * @author Sam Chapman <a href="http://www.dcs.shef.ac.uk/~sam/">Website</a>, <a href="mailto:sam@dcs.shef.ac.uk">Email</a>.
 * @version 1.1
 */
public final class JaccardSimilarity2 extends AbstractStringMetric implements Serializable {

    /**
     * a constant for calculating the estimated timing cost.
     */
    private final float ESTIMATEDTIMINGCONST = 1.4e-4f;

    /**
     * private tokeniser for tokenisation of the query strings.
     */
    private final InterfaceTokeniser tokeniser;

    /**
     * constructor - default.
     */
    public JaccardSimilarity2() {
        tokeniser = new TokeniserWhitespace();
    }

    /**
     * constructor.
     *
     * @param tokeniserToUse - the tokeniser to use should a different tokeniser be required
     */
    public JaccardSimilarity2(final InterfaceTokeniser tokeniserToUse) {
        tokeniser = tokeniserToUse;
    }

    /**
     * returns the string identifier for the metric .
     *
     * @return the string identifier for the metric
     */
    public String getShortDescriptionString() {
        return "JaccardSimilarity";
    }

    /**
     * returns the long string identifier for the metric.
     *
     * @return the long string identifier for the metric
     */
    public String getLongDescriptionString() {
        return "Implements the Jaccard Similarity algorithm providing a similarity measure between two strings";
    }

    /**
     * gets a div class xhtml similarity explaining the operation of the metric.
     *
     * @param string1 string 1
     * @param string2 string 2
     *
     * @return a div class html section detailing the metric operation.
     */
    public String getSimilarityExplained(String string1, String string2) {
        //todo this should explain the operation of a given comparison
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * gets the estimated time in milliseconds it takes to perform a similarity timing.
     *
     * @param string1 string 1
     * @param string2 string 2
     *
     * @return the estimated time in milliseconds taken to perform the similarity measure
     */
    public float getSimilarityTimingEstimated(final String string1, final String string2) {
        //timed millisecond times with string lengths from 1 + 50 each increment
        //0	0.02	0.03	0.05	0.07	0.11	0.14	0.18	0.23	0.27	0.34	0.38	0.45	0.51	0.59	0.67	0.75	0.83	0.94	1	1.15	1.22	1.49	1.46	1.93	1.69	2.11	1.95	2.42	2.21	2.87	2.51	3.27	2.86	3.69	3.22	3.9	3.5	4.74	3.9	4.95	4.23	5.49	4.72	5.8	5.21	6.38	5.64	7.25	5.97	7.81	6.55	8.46	7	9.27	7.52	10.15	8.12	10.15	8.46
        final float str1Tokens = tokeniser.tokenizeToArrayList(string1).size();
        final float str2Tokens = tokeniser.tokenizeToArrayList(string2).size();
        return (str1Tokens * str2Tokens) * ESTIMATEDTIMINGCONST;
    }

    /**
     * gets the similarity of the two strings using JaccardSimilarity.
     *
     * @param string1
     * @param string2
     * @return a value between 0-1 of the similarity
     */
    public float getSimilarity(final String string1, final String string2) {
/*
Each instance is represented as a Jaccard vector similarity function. The Jaccard between two vectors X and Y is

(X*Y) / (|X||Y|-(X*Y))

where (X*Y) is the inner product of X and Y, and |X| = (X*X)^1/2, i.e. the Euclidean norm of X.

This can more easily be described as ( |X & Y| ) / ( | X or Y | )
*/
        //todo this needs checking
        final ArrayList<String> str1Tokens = tokeniser.tokenizeToArrayList(string1);
        final ArrayList<String> str2Tokens = tokeniser.tokenizeToArrayList(string2);

        Hashtable<String, Integer> str1TokensFreq = new Hashtable<String, Integer>();
        Hashtable<String, Integer> str2TokensFreq = new Hashtable<String, Integer>();

        for(String str1 : str1Tokens) {
        	int freq = 1;
        	if (str1TokensFreq.containsKey(str1)) {
        		freq = str1TokensFreq.get(str1);
        		freq++;
        	}
    		str1TokensFreq.put(str1, freq);
        }

        for(String str2 : str2Tokens) {
        	int freq = 1;
        	if (str2TokensFreq.containsKey(str2)) {
        		freq = str2TokensFreq.get(str2);
        		freq++;
        	}
    		str2TokensFreq.put(str2, freq);
        }

        Enumeration<String> str1TokensEnum = str1TokensFreq.keys();
        long commonTerms = 0;
        while(str1TokensEnum.hasMoreElements()) {
        	String str1 = str1TokensEnum.nextElement();

        	if (str2TokensFreq.containsKey(str1)) {
        		commonTerms += Math.min(str1TokensFreq.get(str1), str2TokensFreq.get(str1));
        	}
        }

        //return JaccardSimilarity
        return (float) (commonTerms) / (float) (str1Tokens.size() + str2Tokens.size() - commonTerms);
    }

    /**
     * gets the un-normalised similarity measure of the metric for the given strings.
     *
     * @param string1
     * @param string2
     * @return returns the score of the similarity measure (un-normalised)
     */
    public float getUnNormalisedSimilarity(String string1, String string2) {
        return getSimilarity(string1, string2);
    }
}

