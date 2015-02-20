package deepqa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class InitiateQue {
	
	final static Logger LOGGER = Logger.getLogger(InitiateQue.class);
	
	public static String askQuestion() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		LOGGER.debug("Ask Question:");
		String question = null;
		try {
			question = br.readLine().replaceAll("[();\"'.,]?", "");
		}finally {
			//br.close(); //System.in should't be closed as it's used again
		}
		return question;
	}
}
