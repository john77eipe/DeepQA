package deepqa.dto;

import java.util.Map;
import java.util.TreeMap;

public class AnsConfRefBean {
	private Map<String, String> ansWithSentence;
	private TreeMap<String, Double> ansWithConf;
	public Map<String, String> getAnsWithSentence() {
		return ansWithSentence;
	}
	public void setAnsWithSentence(Map<String, String> ansWithSentence) {
		this.ansWithSentence = ansWithSentence;
	}
	public TreeMap<String, Double> getAnsWithConf() {
		return ansWithConf;
	}
	public void setAnsWithConf(TreeMap<String, Double> ansWithConf) {
		this.ansWithConf = ansWithConf;
	}
	@Override
	public String toString() {
		return "AnsConfRefBean [ansWithSentence=" + ansWithSentence
				+ ", ansWithConf=" + ansWithConf + "]";
	} 
	
}
