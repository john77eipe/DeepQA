package deepqa.rest.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AnswerBean implements Comparable<AnswerBean>{
	private String answer;
	private String confidence;
	private String source;
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getConfidence() {
		return confidence;
	}
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Override
	public String toString() {
		return "AnswerBean [answer=" + answer + ", confidence=" + confidence
				+ ", source=" + source + "]";
	}
	@Override
	public int compareTo(AnswerBean o) {
		return Integer.parseInt(o.confidence)-Integer.parseInt(confidence);
	}

}
