package deepqa.test;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import opennlp.tools.util.InvalidFormatException;

import org.apache.solr.client.solrj.SolrServerException;

public class TestCases {

	static FileWriter fw = null;
	
	public static void main(String[] args) throws IOException{
		fw = new FileWriter("testResults2.txt", true); 
		
		Map<String, String> questionAnswers = new HashMap<String, String>();
		//set 1
		//check this
		questionAnswers.put("When was Bill Gates born", 
				"Bill Gates was born William Henry Gates III on October 28, 1955, in Seattle, Washington.");
		questionAnswers.put("When did Bill Gates graduate", 
				"Bill Gates graduated from Lakeside in 1973.");
		questionAnswers.put("Who is Bill Gates college friend", 
				"It was at Lakeside School where Bill met Paul Allen, who was two years his senior. The two became fast friends");
		questionAnswers.put("When did Lincoln give his second Inaugural Address", 
				"Instead, he gave it momentum in his fiery second Inaugural Address of March 1865.");
		questionAnswers.put("Who is Lincoln mother", 
				"On the other hand, Lincoln's mother, Nancy Hanks, was the flower of his heart.");
		questionAnswers.put("When did Lincoln enter politics", 
				"When he entered politics in his bid for the Illinois state assembly in 1834,");
		questionAnswers.put("Where did Bill Gates work", 
				"Bill Gates was glad to be home again in the Pacific Northwest, and threw himself into his work.");
		questionAnswers.put("When was Walt Disney born", 
				"Walter Elias \"Walt\" Disney was born on December 5, 1901, in the Hermosa section of Chicago, Illinois");
		questionAnswers.put("When was the new Walt Disney Studio opened", 
				"In December 1939, a new campus for Walt Disney Studios was opened in Burbank.");
		
		//set 2
		questionAnswers.put("At what age did Disney drop out of school",
						"When Disney was 16, he dropped out of school to join the army but was rejected for being underage");
		//TODO : check this
		questionAnswers.put("How much is Disneyland",
						"picture Mary Poppins, which mixed live action and animation. Disney's $17 million Disneyland theme park opened in 1955.");
		questionAnswers.put("When did Disney die", 
				"It was still under construction when, in 1966, Disney was diagnosed with lung cancer. He died on December 15, 1966, at the age of 65.");
		questionAnswers.put("When was Jeff Bezos born", 
				"Jeff Bezos was born on January 12, 1964, in Albuquerque, New Mexico, to a teenage mother, Jacklyn Gise Jorgensen, and his biological father, Ted Jorgensen.");
		questionAnswers.put("Where was Jeff Bezos born", 
				"Jeff Bezos was born on January 12, 1964, in Albuquerque, New Mexico, to a teenage mother, Jacklyn Gise Jorgensen, and his biological father, Ted Jorgensen.");
		questionAnswers.put("In which college did Jeff Bezos graduate", 
				"Bezos pursued his interest in computers at Princeton University, where he graduated summa cum laude in 1986 with a degree in");
		questionAnswers.put("How many friends beta tested the site", 
				"and eventually developed a test site. After inviting 300 friends to beta test the site, Bezos opened Amazon.com, named after the meandering South American River, on July 16, 1995.");
		questionAnswers.put("When did Cook join as chief operating officer", 
				"Following a 12-year career at IBM, in 1994, Cook became a chief operating officer (Reseller Division) at Intelligent Electronics.");
		questionAnswers.put("When was Steven Paul Jobs born", 
				"Steven Paul Jobs was born on February 24, 1955, in San Francisco, California, to Joanne Schieble (later Joanne Simpson) and Abdulfattah \"John\" Jandali, t");
		questionAnswers.put("When did Nobel discover nitroglycerin", 
				"In 1863 he succeeded in exploding nitroglycerin from a distance with a gunpowder charge, and two years later he patented the mercury ful");
		questionAnswers.put("When did Nobel establish Nobel Prize", 
				"During November 1895, at the Swedish-Norwegian Club in Paris, Nobel signed his last will and testament and established the Nobel Prizes, to be awarded annually without distinction of nationality.");
		
		for (Entry<String, String> mapEntry : questionAnswers.entrySet()) {

				TestDeepQA.askMe(mapEntry.getKey(), mapEntry.getValue());
			
		}
		fw.flush();
		fw.close();
	}
	
	public static void log(String line) throws IOException{
			fw.write("\n" + line);// appends the string to the file
	}

}
