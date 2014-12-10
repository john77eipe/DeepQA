package deepqa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InitiateQue {
	
	public static String askQuestion() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Ask Question:");
		String question = null;
		try {
			question = br.readLine().replaceAll("[();\"'.,]?", "");
		}finally {
//			br.close(); //System.in should't be closed as it's used again
		}
		return question;
	}
}
