package gov.nist.hit.core.hl7v2.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HL7V2ValidationClassificationEnum {
	  ERROR("Error"),WARNING("Warning"),ALERT("Alert"),INFORMATIONAL("Informational"),AFFIRMATIVE("Affirmative");
	  
	  private String text;
	  
		HL7V2ValidationClassificationEnum(String text) {
	        this.text = text;
	    }
	 
	    public String getText() {
	        return text;
	    }
	}
