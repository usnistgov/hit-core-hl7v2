package gov.nist.hit.core.hl7v2.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public class HL7V2ValidationDetection {
	 
	  private String template;
	  private String category;
	  private HL7V2ValidationClassificationEnum	classfication;
	  	  
	public HL7V2ValidationDetection(String template, String category, HL7V2ValidationClassificationEnum classfication) {
		super();
		this.template = template;
		this.category = category;
		this.classfication = classfication;
	}
	
	public HL7V2ValidationDetection() {
		super();		
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
	public HL7V2ValidationClassificationEnum getClassfication() {
		return classfication;
	}
	public void setClassfication(HL7V2ValidationClassificationEnum classfication) {
		this.classfication = classfication;
	}
	  
	  
	  
	  
	}
