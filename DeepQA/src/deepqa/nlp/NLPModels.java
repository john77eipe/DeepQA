package deepqa.nlp;

public enum NLPModels {
	location("location"), organization("organization"), date("date"), money("money"),
	number("number"), occupation("occupation"), percentage("percentage"), person("person"),
	time("time");
	
	private String name;

	private NLPModels(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
