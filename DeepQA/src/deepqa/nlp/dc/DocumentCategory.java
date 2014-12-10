package deepqa.nlp.dc;

public enum DocumentCategory {
	P("person"),   	//pre-exits
	L("location"), 	//pre-exits
	T("date"), 		//pre-exits
	O("organization"),	 //pre-exits
	R("time"),	 	//pre-exits
	M("money"), 	//pre-exits
	C("percentage"),//pre-exits
	A("number"),	//amount
	D("distance"),
	F("person"), //description
	W("title"),
	B("definition"),
	Y("number"),
	X("occupation"); //others
	
	private String type;
	
	DocumentCategory(String type){
		this.type =type;
	}
	
	public String getCategory(){
		return this.type;
	}
	
}
