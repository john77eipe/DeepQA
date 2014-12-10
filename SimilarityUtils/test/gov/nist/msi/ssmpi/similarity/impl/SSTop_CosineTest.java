package gov.nist.msi.ssmpi.similarity.impl;

import gov.nist.msi.ssmpi.similarity.StringSimilarity;

public class SSTop_CosineTest {

	static String s = "stay";
	static String t = "arrest";
	//arrest, check, halt, hitch, stay, stop, stoppage
	
	public static void testGetSimilarity1() {
		StringSimilarity sim = new SSTop_Cosine();
		float r = sim.getSimilarity(s, t);
		System.out.println(r);
	}

	public static void testGetSimilarity2() {
		StringSimilarity sim = new SSTop_Jaccard();
		float r = sim.getSimilarity(s, t);
		System.out.println(r);
	}

	public static void testGetSimilarity3() {
		StringSimilarity sim = new SSTop_ICofWN();
		float r = sim.getSimilarity(s, t);
		System.out.println(r);
	}

	public static void testGetSimilarity4() {
		StringSimilarity sim = new SSTop_WN_Cosine();
		float r = sim.getSimilarity(s, t);
		System.out.println(r);
	}

	public static void testGetSimilarity5() {
		String[] informationList = { "Car", "Automobile", "Car", "Information",
				"Data", "Car", "Automobile", "File" };
		StringSimilarity sim = new SSTop_WN_IC_Cosine(informationList);
		float r = sim.getSimilarity(s, t);
		System.out.println(r);
	}

	public static void testGetSimilarity6() {
		StringSimilarity sim = new SSTop_WN_Jaccard();
		float r = sim.getSimilarity(s, t);
		System.out.println(r);
	}

	public static void main(String[] args) {
		//testGetSimilarity4();
		s = "eat";
		t = "sleep";
		testGetSimilarity6();

		s = "stay";
		t = "halt";
		testGetSimilarity6();

		s = "stay";
		t = "check";
		testGetSimilarity6();

		s = "stay";
		t = "stop";
		testGetSimilarity6();

		s = "stay";
		t = "hitch";
		testGetSimilarity6();
		
		s = "stay";
		t = "stoppage";
		testGetSimilarity6();

	}
}
