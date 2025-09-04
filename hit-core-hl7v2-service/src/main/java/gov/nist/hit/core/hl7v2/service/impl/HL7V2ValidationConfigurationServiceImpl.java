package gov.nist.hit.core.hl7v2.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import gov.nist.hit.core.domain.Domain;
import gov.nist.hit.core.domain.ValidationClassifications;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassificationEnum;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassifications;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationDetection;
import gov.nist.hit.core.hl7v2.service.HL7V2ValidationConfigurationService;
import gov.nist.hit.core.service.DomainService;

@Service
public class HL7V2ValidationConfigurationServiceImpl extends HL7V2ValidationConfigurationService {

	@Autowired
	private DomainService domainService;
	

	@Override
	public HL7V2ValidationClassifications getClassifications(String domain_){			
			Domain domain = domainService.findOneByKey(domain_);
			try {
				Config config;
				if (domain != null) {
					String conf = domain.getValidationConfiguration();
					ConfigFactory.invalidateCaches();
					if (conf != null) {//domain config				
						StringReader sr = new StringReader(conf);			
						config = ConfigFactory.parseReader(sr).resolve();
					} else {//default config from validation jar
						config = ConfigFactory.load("reference.conf").resolve();
					}
				} else {//default config from validation jar
					config = ConfigFactory.load("reference.conf").resolve();
	
				}
				HL7V2ValidationClassifications classification = configToHL7V2ValidationClassifications(config);
				return classification;
			}catch(Exception e) {
				System.out.println(e);				
			}
			return null;
	
	}
	
	@Override
	public HL7V2ValidationClassifications getDefaultClassifications() {
		try {
			Config config;	
			//get default config from the validation jar (unless another is call like that in the project, then it will be merged)
			config = ConfigFactory.load("reference.conf").resolve();
			HL7V2ValidationClassifications classification = configToHL7V2ValidationClassifications(config);
			return classification;
		}catch(Exception e) {
			System.out.println(e);				
		}
		return null;
	}
	
	
	private HL7V2ValidationClassifications configToHL7V2ValidationClassifications(Config config) {
		try {			
			HL7V2ValidationClassifications classification = new HL7V2ValidationClassifications();

			if (config.hasPath("report.length-spec-error-no-valid.classification")) {
				classification.getLengthSpecErrorNoValid().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.length-spec-error-no-valid.classification").toUpperCase()));
			}
			if (config.hasPath("report.length-spec-error-xor.classification")) {
				classification.getLengthSpecErrorXor().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.length-spec-error-xor.classification").toUpperCase()));
			}
			if (config.hasPath("report.r-usage.classification")) {
				classification.getrUsage().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.r-usage.classification").toUpperCase()));
			}
			if (config.hasPath("report.x-usage.classification")) {
				classification.getxUsage().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.x-usage.classification").toUpperCase()));
			}
			if (config.hasPath("report.w-usage.classification")) {
				classification.getwUsage().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.w-usage.classification").toUpperCase()));
			}
			if (config.hasPath("report.re-usage.classification")) {
				classification.getReUsage().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.re-usage.classification").toUpperCase()));
			}
			if (config.hasPath("report.o-usage.classification")) {
				classification.getoUsage().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.o-usage.classification").toUpperCase()));
			}
			if (config.hasPath("report.cardinality.classification")) {
				classification.getCardinality().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.cardinality.classification").toUpperCase()));
			}
			if (config.hasPath("report.null-cardinality.classification")) {
				classification.getNullCardinality().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.null-cardinality.classification").toUpperCase()));
			}
			if (config.hasPath("report.length.classification")) {
				classification.getLength().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.length.classification").toUpperCase()));
			}
			if (config.hasPath("report.format.classification")) {
				classification.getFormat().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.format.classification").toUpperCase()));
			}
			if (config.hasPath("report.extra.classification")) {
				classification.getExtra().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.extra.classification").toUpperCase()));
			}
			if (config.hasPath("report.unescaped.classification")) {
				classification.getUnescaped().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.unescaped.classification").toUpperCase()));
			}
			if (config.hasPath("report.unexpected.classification")) {
				classification.getUnexpected().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.unexpected.classification").toUpperCase()));
			}
			if (config.hasPath("report.invalid.classification")) {
				classification.getInvalid().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.invalid.classification").toUpperCase()));
			}
			if (config.hasPath("report.unresolved-field.classification")) {
				classification.getUnresolvedField().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.unresolved-field.classification").toUpperCase()));
			}
			if (config.hasPath("report.coconstraint-failure.classification")) {
				classification.getCoconstraintFailure().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.coconstraint-failure.classification").toUpperCase()));
			}
			if (config.hasPath("report.coconstraint-success.classification")) {
				classification.getCoconstraintSuccess().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.coconstraint-success.classification").toUpperCase()));
			}
			if (config.hasPath("report.highlevel-content.classification")) {
				classification.getHighlevelContent().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.highlevel-content.classification").toUpperCase()));
			}
			if (config.hasPath("report.constraint-failure.classification")) {
				classification.getConstraintFailure().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.constraint-failure.classification").toUpperCase()));
			}
			if (config.hasPath("report.constraint-success.classification")) {
				classification.getConstraintSuccess().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.constraint-success.classification").toUpperCase()));
			}
			if (config.hasPath("report.constraint-spec-error.classification")) {
				classification.getConstraintSpecError().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.constraint-spec-error.classification").toUpperCase()));
			}
			if (config.hasPath("report.content-failure.classification")) {
				classification.getContentFailure().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.content-failure.classification").toUpperCase()));
			}
			if (config.hasPath("report.content-success.classification")) {
				classification.getContentSuccess().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.content-success.classification").toUpperCase()));
			}
			if (config.hasPath("report.content-spec-error.classification")) {
				classification.getContentSpecError().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.content-spec-error.classification").toUpperCase()));
			}
			if (config.hasPath("report.predicate-success.classification")) {
				classification.getPredicateSuccess().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.predicate-success.classification").toUpperCase()));
			}
			if (config.hasPath("report.predicate-failure.classification")) {
				classification.getPredicateFailure().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.predicate-failure.classification").toUpperCase()));
			}
			if (config.hasPath("report.predicate-spec-error.classification")) {
				classification.getPredicateSpecError().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.predicate-spec-error.classification").toUpperCase()));
			}
			if (config.hasPath("report.evs.classification")) {
				classification.getEvs().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.evs.classification").toUpperCase()));
			}
			if (config.hasPath("report.pvs.classification")) {
				classification.getPvs().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.pvs.classification").toUpperCase()));
			}
			//deprecated	
			if (config.hasPath("report.code-not-found.classification")) {
				classification.getCodeNotFound().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.code-not-found.classification").toUpperCase()));
			}
			if (config.hasPath("report.code-not-found-simple.classification")) {
				classification.getCodeNotFoundSimple().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.code-not-found-simple.classification").toUpperCase()));
			}
			if (config.hasPath("report.code-not-found-coded-element.classification")) {
				classification.getCodeNotFoundCodedElement().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.code-not-found-coded-element.classification").toUpperCase()));
			}
			if (config.hasPath("report.code-not-found-cs.classification")) {
				classification.getCodeNotFoundCS().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.code-not-found-cs.classification").toUpperCase()));
			}					
			//deprecated
			if (config.hasPath("report.vs-not-found.classification")) {
				classification.getVsNotFound().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-not-found.classification").toUpperCase()));
			}
			if (config.hasPath("report.vs-not-found-binding.classification")) {
				classification.getVsNotFoundBinding().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-not-found-binding.classification").toUpperCase()));
			}			
			if (config.hasPath("report.empty-vs.classification")) {
				classification.getEmptyVs().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.empty-vs.classification").toUpperCase()));
			}
			//deprecated
			if (config.hasPath("report.vs-error.classification")) {
				classification.getVsError().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-error.classification").toUpperCase()));
			}
			if (config.hasPath("report.binding-location.classification")) {
				classification.getBindingLocation().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.binding-location.classification").toUpperCase()));
			}
			if (config.hasPath("report.vs-no-validation.classification")) {
				classification.getVsNoValidation().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-no-validation.classification").toUpperCase()));
			}
			//deprecated
			if (config.hasPath("report.coded-element.classification")) {
				classification.getCodedElement().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.coded-element.classification").toUpperCase()));
			}
			return classification;
		}catch(Exception e) {
			System.out.println(e);				
		}
		return null;
	}

	@Override
	public boolean saveClassifications(ValidationClassifications validationClassification, String domain) {
		
		HL7V2ValidationClassifications classification = (HL7V2ValidationClassifications)validationClassification;
		HL7V2ValidationClassifications defaults = getDefaultClassifications();
				
		try {
			//could use the default but want to make sure we know what we are working with.
			String file = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("validationConfigurationTemplate173.conf"),"UTF-8");
						
			
//			file = HL7V2ValidationClassificationsToString(classification, file); 
//			file = HL7V2ValidationClassificationsToString(defaults, file);
//			Config config = ConfigFactory.load("validationConfigurationTemplate173.conf").resolve();
			file  = updateAndRender(file,classification.getDetections());
			
			Domain d = domainService.findOneByKey(domain);
			d.setValidationConfiguration(file);
			domainService.save(d);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;			
		}
		
	}
	
	public String updateAndRender(String confString, List<HL7V2ValidationDetection> detections) {
	    List<String> lines = Arrays.asList(confString.split("\\r?\\n"));
	    List<String> updated = new ArrayList<>();

	    String currentBlock = null;

	    for (String line : lines) {
	        String trimmed = line.trim();

	        // detect entering a block by name
	        for (HL7V2ValidationDetection detection : detections) {
	            String name = detection.getName();
	            if (trimmed.startsWith(name + " {")) {
	                currentBlock = name;
	                break;
	            }
	        }

	        if (trimmed.startsWith("classification =") && currentBlock != null) {
	            HL7V2ValidationDetection match = null;
	            for (HL7V2ValidationDetection d : detections) {
	                if (d.getName().equals(currentBlock)) {
	                    match = d;
	                    break;
	                }
	            }

	            if (match != null && match.getClassification() != null) {
	                HL7V2ValidationClassificationEnum enumVal = match.getClassification();
	                String refName = enumVal.name().toLowerCase().replace("_", "-");
	                String substitution = "classification = ${report.classification." + refName + "}";

	                int indentPos = line.indexOf("classification");
	                String indent = indentPos > 0 ? line.substring(0, indentPos) : "";
	                updated.add(indent + substitution);
	                continue; // skip adding the old line
	            }
	        }

	        // reset block on closing brace
	        if (trimmed.equals("}")) {
	            currentBlock = null;
	        }

	        updated.add(line);
	    }

	    return String.join("\n", updated);
	}


	
	private String HL7V2ValidationClassificationsToString(HL7V2ValidationClassifications classification, String file) {
			
			if (file.contains("@{length-spec-error-no-valid.classification}") && classification.getLengthSpecErrorNoValid().getClassification() != null) {
				file = file.replace("@{length-spec-error-no-valid.classification}", "\"" +  classification.getLengthSpecErrorNoValid().getClassification().getText() + "\"");	
			}
			if (file.contains("@{length-spec-error-xor.classification}") && classification.getLengthSpecErrorXor().getClassification() != null) {
				file = file.replace("@{length-spec-error-xor.classification}", "\"" +  classification.getLengthSpecErrorXor().getClassification().getText() + "\"");
			}
			if (file.contains("@{r-usage.classification}") && classification.getrUsage().getClassification() != null) {
				file = file.replace("@{r-usage.classification}", "\"" +  classification.getrUsage().getClassification().getText() + "\"");
			}
			if (file.contains("@{x-usage.classification}") && classification.getxUsage().getClassification() != null) {
				file = file.replace("@{x-usage.classification}", "\"" +  classification.getxUsage().getClassification().getText() + "\"");
			}
			if (file.contains("@{w-usage.classification}") && classification.getwUsage().getClassification() != null) {
				file = file.replace("@{w-usage.classification}", "\"" +  classification.getwUsage().getClassification().getText() + "\"");
			}			
			if (file.contains("@{re-usage.classification}") && classification.getReUsage().getClassification() != null) {
				file = file.replace("@{re-usage.classification}", "\"" +  classification.getReUsage().getClassification().getText() + "\"");
			}			
			if (file.contains("@{o-usage.classification}") && classification.getoUsage().getClassification() != null) {
				file = file.replace("@{o-usage.classification}", "\"" +  classification.getoUsage().getClassification().getText() + "\"");
			}			
			if (file.contains("@{cardinality.classification}") && classification.getCardinality().getClassification() != null) {
				file = file.replace("@{cardinality.classification}", "\"" +  classification.getCardinality().getClassification().getText() + "\"");
			}			
			if (file.contains("@{null-cardinality.classification}") && classification.getNullCardinality().getClassification() != null) {
				file = file.replace("@{null-cardinality.classification}", "\"" +  classification.getNullCardinality().getClassification().getText() + "\"");
			}	
			if (file.contains("@{length.classification}") && classification.getLength().getClassification() != null) {
				file = file.replace("@{length.classification}", "\"" +  classification.getLength().getClassification().getText() + "\"");
			}
			if (file.contains("@{format.classification}") && classification.getFormat().getClassification() != null) {
				file = file.replace("@{format.classification}", "\"" +  classification.getFormat().getClassification().getText() + "\"");
			}			
			if (file.contains("@{extra.classification}") && classification.getExtra().getClassification() != null) {
				file = file.replace("@{extra.classification}", "\"" +  classification.getExtra().getClassification().getText() + "\"");
			}
			if (file.contains("@{unescaped.classification}") && classification.getUnescaped().getClassification() != null) {
				file = file.replace("@{unescaped.classification}", "\"" +  classification.getUnescaped().getClassification().getText() + "\"");
			}
			if (file.contains("@{unexpected.classification}") && classification.getUnexpected().getClassification() != null) {
				file = file.replace("@{unexpected.classification}", "\"" +  classification.getUnexpected().getClassification().getText() + "\"");
			}
			if (file.contains("@{invalid.classification}") && classification.getInvalid().getClassification() != null) {
				file = file.replace("@{invalid.classification}", "\"" +  classification.getInvalid().getClassification().getText() + "\"");
			}
			if (file.contains("@{unresolved-field.classification}") && classification.getUnresolvedField().getClassification() != null) {
				file = file.replace("@{unresolved-field.classification}", "\"" +  classification.getUnresolvedField().getClassification().getText() + "\"");
			}			
			if (file.contains("@{coconstraint-failure.classification}") && classification.getCoconstraintFailure().getClassification() != null) {
			file = file.replace("@{coconstraint-failure.classification}", "\"" +  classification.getCoconstraintFailure().getClassification().getText() + "\"");
			}
			if (file.contains("@{coconstraint-success.classification}") && classification.getCoconstraintSuccess().getClassification() != null) {
			file = file.replace("@{coconstraint-success.classification}", "\"" +  classification.getCoconstraintSuccess().getClassification().getText() + "\"");
			}
			if (file.contains("@{highlevel-content.classification}") && classification.getHighlevelContent().getClassification() != null) {
			file = file.replace("@{highlevel-content.classification}", "\"" +  classification.getHighlevelContent().getClassification().getText() + "\"");
			}
			if (file.contains("@{constraint-failure.classification}") && classification.getConstraintFailure().getClassification() != null) {
			file = file.replace("@{constraint-failure.classification}", "\"" +  classification.getConstraintFailure().getClassification().getText() + "\"");
			}
			if (file.contains("@{constraint-success.classification}") && classification.getConstraintSuccess().getClassification() != null) {
			file = file.replace("@{constraint-success.classification}", "\"" +  classification.getConstraintSuccess().getClassification().getText() + "\"");
			}
			if (file.contains("@{constraint-spec-error.classification}") && classification.getConstraintSpecError().getClassification() != null) {
			file = file.replace("@{constraint-spec-error.classification}", "\"" +  classification.getConstraintSpecError().getClassification().getText() + "\"");
			}
			if (file.contains("@{content-failure.classification}") && classification.getContentFailure().getClassification() != null) {
			file = file.replace("@{content-failure.classification}", "\"" +  classification.getContentFailure().getClassification().getText() + "\"");
			}
			if (file.contains("@{content-success.classification}") && classification.getContentSuccess().getClassification() != null) {
			file = file.replace("@{content-success.classification}", "\"" +  classification.getContentSuccess().getClassification().getText() + "\"");
			}
			if (file.contains("@{content-spec-error.classification}") && classification.getContentSpecError().getClassification() != null) {
			file = file.replace("@{content-spec-error.classification}", "\"" +  classification.getContentSpecError().getClassification().getText() + "\"");
			}
			if (file.contains("@{predicate-success.classification}") && classification.getPredicateSuccess().getClassification() != null) {
			file = file.replace("@{predicate-success.classification}", "\"" +  classification.getPredicateSuccess().getClassification().getText() + "\"");
			}
			if (file.contains("@{predicate-failure.classification}") && classification.getPredicateFailure().getClassification() != null) {
			file = file.replace("@{predicate-failure.classification}", "\"" +  classification.getPredicateFailure().getClassification().getText() + "\"");
			}
			if (file.contains("@{predicate-spec-error.classification}") && classification.getPredicateSpecError().getClassification() != null) {
			file = file.replace("@{predicate-spec-error.classification}", "\"" +  classification.getPredicateSpecError().getClassification().getText() + "\"");
			}
			if (file.contains("@{evs.classification}") && classification.getEvs().getClassification() != null) {
			file = file.replace("@{evs.classification}", "\"" +  classification.getEvs().getClassification().getText() + "\"");
			}
			if (file.contains("@{pvs.classification}") && classification.getPvs().getClassification() != null) {
			file = file.replace("@{pvs.classification}", "\"" +  classification.getPvs().getClassification().getText() + "\"");
			}
			if (file.contains("@{code-not-found.classification}") && classification.getCodeNotFound().getClassification() != null) {
			file = file.replace("@{code-not-found.classification}", "\"" +  classification.getCodeNotFound().getClassification().getText() + "\"");
			}
			if (file.contains("@{vs-not-found.classification}") && classification.getVsNotFound().getClassification() != null) {
			file = file.replace("@{vs-not-found.classification}", "\"" +  classification.getVsNotFound().getClassification().getText() + "\"");
			}
			if (file.contains("@{empty-vs.classification}") && classification.getEmptyVs().getClassification() != null) {
			file = file.replace("@{empty-vs.classification}", "\"" +  classification.getEmptyVs().getClassification().getText() + "\"");
			}
			if (file.contains("@{vs-error.classification}") && classification.getVsError().getClassification() != null) {
			file = file.replace("@{vs-error.classification}", "\"" +  classification.getVsError().getClassification().getText() + "\"");
			}
			if (file.contains("@{binding-location.classification}") && classification.getBindingLocation().getClassification() != null) {
			file = file.replace("@{binding-location.classification}", "\"" +  classification.getBindingLocation().getClassification().getText() + "\"");
			}
			if (file.contains("@{vs-no-validation.classification}") && classification.getVsNoValidation().getClassification() != null) {
			file = file.replace("@{vs-no-validation.classification}", "\"" +  classification.getVsNoValidation().getClassification().getText() + "\"");
			}
			if (file.contains("@{coded-element.classification}") && classification.getCodedElement().getClassification() != null) {
			file = file.replace("@{coded-element.classification}", "\"" +  classification.getCodedElement().getClassification().getText() + "\"");
			}
			if (file.contains("@{vs-not-found-binding.classification}") && classification.getVsNotFoundBinding().getClassification() != null) {
			file = file.replace("@{vs-not-found-binding.classification}", "\"" +  classification.getVsNotFoundBinding().getClassification().getText() + "\"");
			}
			if (file.contains("@{code-not-found-simple.classification}") && classification.getCodeNotFoundSimple().getClassification() != null) {
			file = file.replace("@{code-not-found-simple.classification}", "\"" +  classification.getCodeNotFoundSimple().getClassification().getText() + "\"");
			}
			if (file.contains("@{code-not-found-coded-element.classification}") && classification.getCodeNotFoundCodedElement().getClassification() != null) {
			file = file.replace("@{code-not-found-coded-element.classification}", "\"" +  classification.getCodeNotFoundCodedElement().getClassification().getText() + "\"");
			}
			if (file.contains("@{code-not-found-cs.classification}") && classification.getCodeNotFoundCS().getClassification() != null) {
			file = file.replace("@{code-not-found-cs.classification}", "\"" +  classification.getCodeNotFoundCS().getClassification().getText() + "\"");
			}
			
						
			return file;
		
				
	}



}
