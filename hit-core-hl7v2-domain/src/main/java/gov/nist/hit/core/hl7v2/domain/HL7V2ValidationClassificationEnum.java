package gov.nist.hit.core.hl7v2.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HL7V2ValidationClassificationEnum {
	  ERROR("Error"),WARNING("Warning"),ALERT("Alert"),INFORMATIONAL("Informational"),AFFIRMATIVE("Affirmative"),SPEC_ERROR("Spec Error");
	  
	  private String text;
	  
		HL7V2ValidationClassificationEnum(String text) {
	        this.text = text;
	    }
	 
	    public String getText() {
	        return text;
	    }
	    
	    public static HL7V2ValidationClassificationEnum fromText(String text) {
	        for (HL7V2ValidationClassificationEnum e : HL7V2ValidationClassificationEnum.values()) {
	            if (e.getText().equalsIgnoreCase(text)) {
	                return e;
	            }
	        }
	        return null;
	    }
	}
