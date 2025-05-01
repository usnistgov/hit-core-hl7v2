package gov.nist.hit.core.hl7v2.domain;

import gov.nist.hit.core.domain.TestCaseDocument;

import java.io.Serializable;

public class HLV2TestCaseDocument extends TestCaseDocument implements Serializable {

  private static final long serialVersionUID = 1L;
  protected boolean exMsgPresent;
  protected boolean xmlConfProfilePresent;
  protected boolean xmlValueSetLibraryPresent;
  protected boolean xmlConstraintsPresent;
  protected boolean xmlCoConstraintsPresent;
  protected boolean xmlValueSetBindingsPresent;
  protected boolean xmlSlicingsPresent;

  public HLV2TestCaseDocument() {
    super();
    this.format = "hl7v2";
  }

  public boolean isExMsgPresent() {
    return exMsgPresent;
  }

  public void setExMsgPresent(boolean exMsgPresent) {
    this.exMsgPresent = exMsgPresent;
  }

  public boolean isXmlConfProfilePresent() {
    return xmlConfProfilePresent;
  }

  public void setXmlConfProfilePresent(boolean xmlConfProfilePresent) {
    this.xmlConfProfilePresent = xmlConfProfilePresent;
  }

  public boolean isXmlValueSetLibraryPresent() {
    return xmlValueSetLibraryPresent;
  }

  public void setXmlValueSetLibraryPresent(boolean xmlValueSetLibraryPresent) {
    this.xmlValueSetLibraryPresent = xmlValueSetLibraryPresent;
  }

  public boolean isXmlConstraintsPresent() {
    return xmlConstraintsPresent;
  }

  public void setXmlConstraintsPresent(boolean xmlConstraintsPresent) {
    this.xmlConstraintsPresent = xmlConstraintsPresent;
  }

	public boolean isXmlCoConstraintsPresent() {
		return xmlCoConstraintsPresent;
	}
	
	public void setXmlCoConstraintsPresent(boolean xmlCoConstraintsPresent) {
		this.xmlCoConstraintsPresent = xmlCoConstraintsPresent;
	}
	
	public boolean isXmlValueSetBindingsPresent() {
		return xmlValueSetBindingsPresent;
	}
	
	public void setXmlValueSetBindingsPresent(boolean xmlValueSetBindingsPresent) {
		this.xmlValueSetBindingsPresent = xmlValueSetBindingsPresent;
	}
	
	public boolean isXmlSlicingsPresent() {
		return xmlSlicingsPresent;
	}
	
	public void setXmlSlicingsPresent(boolean xmlSlicingsPresent) {
		this.xmlSlicingsPresent = xmlSlicingsPresent;
	}

  	


}
