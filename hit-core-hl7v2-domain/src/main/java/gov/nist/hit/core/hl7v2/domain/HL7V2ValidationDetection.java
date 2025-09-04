package gov.nist.hit.core.hl7v2.domain;

public class HL7V2ValidationDetection {
	
	private String name;
	private String template;
	private String category;
	private HL7V2ValidationClassificationEnum classification;
	  	  
	public HL7V2ValidationDetection(String template, String category, HL7V2ValidationClassificationEnum classification) {
		super();
		this.template = template;
		this.category = category;
		this.classification = classification;
	}
	
	public HL7V2ValidationDetection() {
		super();				
	}
	
	public HL7V2ValidationDetection(String name) {
		super();		
		this.name = name;
	}
		
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public HL7V2ValidationClassificationEnum getClassification() {
		return classification;
	}
	public void setClassfication(HL7V2ValidationClassificationEnum classification) {
		this.classification = classification;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	  
	
	  
	  
	  
	}
