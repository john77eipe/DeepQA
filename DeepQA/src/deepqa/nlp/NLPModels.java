package deepqa.nlp;

public enum NLPModels {
	LOCATION("location"), ORGANIZATION("organization"), DATE("date"), MONEY("money"),
	NUMBER("number"), OCCUPATION("occupation"), PERCENTAGE("percentage"), PERSON("person"),
	TIME("time");
	
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
