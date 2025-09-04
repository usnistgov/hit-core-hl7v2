package gov.nist.hit.core.hl7v2.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import gov.nist.hit.core.domain.ValidationClassifications;

public class HL7V2ValidationClassifications extends ValidationClassifications{
	
	
	private HL7V2ValidationDetection lengthSpecErrorNoValid = new HL7V2ValidationDetection("length-spec-error-no-valid");
	private HL7V2ValidationDetection lengthSpecErrorXor = new HL7V2ValidationDetection("length-spec-error-xor");
	private HL7V2ValidationDetection xUsage = new HL7V2ValidationDetection("x-usage");
	private HL7V2ValidationDetection wUsage = new HL7V2ValidationDetection("w-usage");
	private HL7V2ValidationDetection rUsage = new HL7V2ValidationDetection("r-usage");
	private HL7V2ValidationDetection reUsage = new HL7V2ValidationDetection("re-usage");
	private HL7V2ValidationDetection oUsage = new HL7V2ValidationDetection("o-usage");
	private HL7V2ValidationDetection cardinality = new HL7V2ValidationDetection("cardinality");
	private HL7V2ValidationDetection nullCardinality = new HL7V2ValidationDetection("null-cardinality");
	private HL7V2ValidationDetection length = new HL7V2ValidationDetection("length");
	private HL7V2ValidationDetection format = new HL7V2ValidationDetection("format");
	private HL7V2ValidationDetection extra = new HL7V2ValidationDetection("extra");
	private HL7V2ValidationDetection unescaped = new HL7V2ValidationDetection("unescaped");	
	private HL7V2ValidationDetection unexpected = new HL7V2ValidationDetection("unexpected");
	private HL7V2ValidationDetection invalid = new HL7V2ValidationDetection("invalid");
	private HL7V2ValidationDetection unresolvedField = new HL7V2ValidationDetection("unresolved-field");
	private HL7V2ValidationDetection coconstraintFailure = new HL7V2ValidationDetection("coconstraint-failure");
	private HL7V2ValidationDetection coconstraintSuccess = new HL7V2ValidationDetection("coconstraint-success");
	private HL7V2ValidationDetection highlevelContent = new HL7V2ValidationDetection("highlevel-content");
	private HL7V2ValidationDetection constraintFailure = new HL7V2ValidationDetection("constraint-failure");
	private HL7V2ValidationDetection constraintSuccess = new HL7V2ValidationDetection("constraint-success");
	private HL7V2ValidationDetection constraintSpecError = new HL7V2ValidationDetection("constraint-spec-error");
	private HL7V2ValidationDetection contentFailure = new HL7V2ValidationDetection("content-failure");
	private HL7V2ValidationDetection contentSuccess = new HL7V2ValidationDetection("content-success");
	private HL7V2ValidationDetection contentSpecError = new HL7V2ValidationDetection("content-spec-error");
	private HL7V2ValidationDetection predicateSuccess = new HL7V2ValidationDetection("predicate-success");
	private HL7V2ValidationDetection predicateFailure = new HL7V2ValidationDetection("predicate-failure");
	private HL7V2ValidationDetection predicateSpecError = new HL7V2ValidationDetection("predicate-spec-error");
	private HL7V2ValidationDetection evs = new HL7V2ValidationDetection("evs");
	private HL7V2ValidationDetection pvs = new HL7V2ValidationDetection("pvs");
	//deprecated
	private HL7V2ValidationDetection codeNotFound = new HL7V2ValidationDetection("code-not-found");
	//deprecated
	private HL7V2ValidationDetection vsNotFound = new HL7V2ValidationDetection("vs-not-found");
	private HL7V2ValidationDetection emptyVs = new HL7V2ValidationDetection("empty-vs");
	//deprecated
	private HL7V2ValidationDetection vsError = new HL7V2ValidationDetection("vs-error");
	private HL7V2ValidationDetection bindingLocation = new HL7V2ValidationDetection("binding-location");
	private HL7V2ValidationDetection vsNoValidation = new HL7V2ValidationDetection("vs-no-validation");
	//deprecated
	private HL7V2ValidationDetection codedElement = new HL7V2ValidationDetection("coded-element");
	
	private HL7V2ValidationDetection vsNotFoundBinding = new HL7V2ValidationDetection("vs-not-found-binding");
	private HL7V2ValidationDetection codeNotFoundSimple = new HL7V2ValidationDetection("code-not-found-simple");
	private HL7V2ValidationDetection codeNotFoundCodedElement = new HL7V2ValidationDetection("code-not-found-coded-element");
	private HL7V2ValidationDetection codeNotFoundCS = new HL7V2ValidationDetection("code-not-found-cs");
	


	public HL7V2ValidationClassifications() {
		super();
		this.validationType = "hl7v2";		
	}

	





	public List<HL7V2ValidationDetection> getDetections() {
		List<HL7V2ValidationDetection> detections = new ArrayList<HL7V2ValidationDetection>();
		detections.add(nullCardinality);
		detections.add(length);
		detections.add(format);
		detections.add(extra);
		detections.add(unescaped);
		detections.add(unexpected);
		detections.add(invalid);
		detections.add(unresolvedField);
		detections.add(coconstraintFailure);
		detections.add(coconstraintSuccess);
		detections.add(highlevelContent);
		detections.add(constraintFailure);
		detections.add(constraintSuccess);
		detections.add(constraintSpecError);
		detections.add(contentFailure);
		detections.add(contentSuccess);
		detections.add(contentSpecError);
		detections.add(predicateSuccess);
		detections.add(predicateFailure);
		detections.add(predicateSpecError);
		detections.add(evs);
		detections.add(pvs);
		detections.add(codeNotFound);
		detections.add(vsNotFound);
		detections.add(emptyVs);
		detections.add(vsError);
		detections.add(bindingLocation);
		detections.add(vsNoValidation);
		detections.add(codedElement);
		detections.add(vsNotFoundBinding);
		detections.add(codeNotFoundSimple);
		detections.add(codeNotFoundCodedElement);
		detections.add(codeNotFoundCS);
		detections.add(lengthSpecErrorXor);
		detections.add(xUsage);
		detections.add(wUsage);
		detections.add(rUsage);
		detections.add(reUsage);
		detections.add(oUsage);
		detections.add(cardinality);	
		return detections;
	}







	public HL7V2ValidationDetection getLengthSpecErrorNoValid() {
		return lengthSpecErrorNoValid;
	}






	public void setLengthSpecErrorNoValid(HL7V2ValidationDetection lengthSpecErrorNoValid) {
		this.lengthSpecErrorNoValid = lengthSpecErrorNoValid;
	}






	public HL7V2ValidationDetection getLengthSpecErrorXor() {
		return lengthSpecErrorXor;
	}






	public void setLengthSpecErrorXor(HL7V2ValidationDetection lengthSpecErrorXor) {
		this.lengthSpecErrorXor = lengthSpecErrorXor;
	}






	public HL7V2ValidationDetection getxUsage() {
		return xUsage;
	}






	public void setxUsage(HL7V2ValidationDetection xUsage) {
		this.xUsage = xUsage;
	}






	public HL7V2ValidationDetection getwUsage() {
		return wUsage;
	}






	public void setwUsage(HL7V2ValidationDetection wUsage) {
		this.wUsage = wUsage;
	}






	public HL7V2ValidationDetection getReUsage() {
		return reUsage;
	}






	public void setReUsage(HL7V2ValidationDetection reUsage) {
		this.reUsage = reUsage;
	}






	public HL7V2ValidationDetection getoUsage() {
		return oUsage;
	}






	public void setoUsage(HL7V2ValidationDetection oUsage) {
		this.oUsage = oUsage;
	}






	public HL7V2ValidationDetection getCardinality() {
		return cardinality;
	}






	public void setCardinality(HL7V2ValidationDetection cardinality) {
		this.cardinality = cardinality;
	}






	public HL7V2ValidationDetection getNullCardinality() {
		return nullCardinality;
	}






	public void setNullCardinality(HL7V2ValidationDetection nullCardinality) {
		this.nullCardinality = nullCardinality;
	}






	public HL7V2ValidationDetection getLength() {
		return length;
	}






	public void setLength(HL7V2ValidationDetection length) {
		this.length = length;
	}







	public HL7V2ValidationDetection getFormat() {
		return format;
	}






	public void setFormat(HL7V2ValidationDetection format) {
		this.format = format;
	}






	public HL7V2ValidationDetection getExtra() {
		return extra;
	}






	public void setExtra(HL7V2ValidationDetection extra) {
		this.extra = extra;
	}






	public HL7V2ValidationDetection getUnescaped() {
		return unescaped;
	}






	public void setUnescaped(HL7V2ValidationDetection unescaped) {
		this.unescaped = unescaped;
	}






	public HL7V2ValidationDetection getUnexpected() {
		return unexpected;
	}






	public void setUnexpected(HL7V2ValidationDetection unexpected) {
		this.unexpected = unexpected;
	}






	public HL7V2ValidationDetection getInvalid() {
		return invalid;
	}






	public void setInvalid(HL7V2ValidationDetection invalid) {
		this.invalid = invalid;
	}






	public HL7V2ValidationDetection getUnresolvedField() {
		return unresolvedField;
	}






	public void setUnresolvedField(HL7V2ValidationDetection unresolvedField) {
		this.unresolvedField = unresolvedField;
	}






	public HL7V2ValidationDetection getCoconstraintFailure() {
		return coconstraintFailure;
	}






	public void setCoconstraintFailure(HL7V2ValidationDetection coconstraintFailure) {
		this.coconstraintFailure = coconstraintFailure;
	}






	public HL7V2ValidationDetection getCoconstraintSuccess() {
		return coconstraintSuccess;
	}






	public void setCoconstraintSuccess(HL7V2ValidationDetection coconstraintSuccess) {
		this.coconstraintSuccess = coconstraintSuccess;
	}






	public HL7V2ValidationDetection getHighlevelContent() {
		return highlevelContent;
	}






	public void setHighlevelContent(HL7V2ValidationDetection highlevelContent) {
		this.highlevelContent = highlevelContent;
	}






	public HL7V2ValidationDetection getConstraintFailure() {
		return constraintFailure;
	}






	public void setConstraintFailure(HL7V2ValidationDetection constraintFailure) {
		this.constraintFailure = constraintFailure;
	}






	public HL7V2ValidationDetection getConstraintSuccess() {
		return constraintSuccess;
	}






	public void setConstraintSuccess(HL7V2ValidationDetection constraintSuccess) {
		this.constraintSuccess = constraintSuccess;
	}






	public HL7V2ValidationDetection getConstraintSpecError() {
		return constraintSpecError;
	}






	public void setConstraintSpecError(HL7V2ValidationDetection constraintSpecError) {
		this.constraintSpecError = constraintSpecError;
	}






	public HL7V2ValidationDetection getContentFailure() {
		return contentFailure;
	}






	public void setContentFailure(HL7V2ValidationDetection contentFailure) {
		this.contentFailure = contentFailure;
	}






	public HL7V2ValidationDetection getContentSuccess() {
		return contentSuccess;
	}






	public void setContentSuccess(HL7V2ValidationDetection contentSuccess) {
		this.contentSuccess = contentSuccess;
	}






	public HL7V2ValidationDetection getContentSpecError() {
		return contentSpecError;
	}






	public void setContentSpecError(HL7V2ValidationDetection contentSpecError) {
		this.contentSpecError = contentSpecError;
	}






	public HL7V2ValidationDetection getPredicateSuccess() {
		return predicateSuccess;
	}






	public void setPredicateSuccess(HL7V2ValidationDetection predicateSuccess) {
		this.predicateSuccess = predicateSuccess;
	}






	public HL7V2ValidationDetection getPredicateFailure() {
		return predicateFailure;
	}






	public void setPredicateFailure(HL7V2ValidationDetection predicateFailure) {
		this.predicateFailure = predicateFailure;
	}






	public HL7V2ValidationDetection getPredicateSpecError() {
		return predicateSpecError;
	}






	public void setPredicateSpecError(HL7V2ValidationDetection predicateSpecError) {
		this.predicateSpecError = predicateSpecError;
	}






	public HL7V2ValidationDetection getEvs() {
		return evs;
	}






	public void setEvs(HL7V2ValidationDetection evs) {
		this.evs = evs;
	}






	public HL7V2ValidationDetection getPvs() {
		return pvs;
	}






	public void setPvs(HL7V2ValidationDetection pvs) {
		this.pvs = pvs;
	}






	public HL7V2ValidationDetection getCodeNotFound() {
		return codeNotFound;
	}






	public void setCodeNotFound(HL7V2ValidationDetection codeNotFound) {
		this.codeNotFound = codeNotFound;
	}






	public HL7V2ValidationDetection getVsNotFound() {
		return vsNotFound;
	}






	public void setVsNotFound(HL7V2ValidationDetection vsNotFound) {
		this.vsNotFound = vsNotFound;
	}






	public HL7V2ValidationDetection getEmptyVs() {
		return emptyVs;
	}






	public void setEmptyVs(HL7V2ValidationDetection emptyVs) {
		this.emptyVs = emptyVs;
	}






	public HL7V2ValidationDetection getVsError() {
		return vsError;
	}






	public void setVsError(HL7V2ValidationDetection vsError) {
		this.vsError = vsError;
	}






	public HL7V2ValidationDetection getBindingLocation() {
		return bindingLocation;
	}






	public void setBindingLocation(HL7V2ValidationDetection bindingLocation) {
		this.bindingLocation = bindingLocation;
	}






	public HL7V2ValidationDetection getVsNoValidation() {
		return vsNoValidation;
	}






	public void setVsNoValidation(HL7V2ValidationDetection vsNoValidation) {
		this.vsNoValidation = vsNoValidation;
	}






	public HL7V2ValidationDetection getCodedElement() {
		return codedElement;
	}






	public void setCodedElement(HL7V2ValidationDetection codedElement) {
		this.codedElement = codedElement;
	}






	public HL7V2ValidationDetection getrUsage() {
		return rUsage;
	}






	public void setrUsage(HL7V2ValidationDetection rUsage) {
		this.rUsage = rUsage;
	}






	public HL7V2ValidationDetection getCodeNotFoundSimple() {
		return codeNotFoundSimple;
	}






	public void setCodeNotFoundSimple(HL7V2ValidationDetection codeNotFoundSimple) {
		this.codeNotFoundSimple = codeNotFoundSimple;
	}






	public HL7V2ValidationDetection getCodeNotFoundCodedElement() {
		return codeNotFoundCodedElement;
	}






	public void setCodeNotFoundCodedElement(HL7V2ValidationDetection codeNotFoundCodedElement) {
		this.codeNotFoundCodedElement = codeNotFoundCodedElement;
	}






	public HL7V2ValidationDetection getCodeNotFoundCS() {
		return codeNotFoundCS;
	}






	public void setCodeNotFoundCS(HL7V2ValidationDetection codeNotFoundCS) {
		this.codeNotFoundCS = codeNotFoundCS;
	}






	public HL7V2ValidationDetection getVsNotFoundBinding() {
		return vsNotFoundBinding;
	}






	public void setVsNotFoundBinding(HL7V2ValidationDetection vsNotFoundBinding) {
		this.vsNotFoundBinding = vsNotFoundBinding;
	}


	
	

}
