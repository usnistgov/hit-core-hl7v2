package gov.nist.hit.core.hl7v2.service.impl;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import gov.nist.hit.core.domain.Domain;
import gov.nist.hit.core.domain.ValidationClassifications;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassificationEnum;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassifications;
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
					} else {//default config
						config = ConfigFactory.load("application.conf").resolve();
					}
				} else {//default config
					config = ConfigFactory.load("application.conf").resolve();
	
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
			config = ConfigFactory.load("application.conf").resolve();
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
			if (config.hasPath("report.code-not-found.classification")) {
				classification.getCodeNotFound().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.code-not-found.classification").toUpperCase()));
			}
			if (config.hasPath("report.vs-not-found.classification")) {
				classification.getVsNotFound().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-not-found.classification").toUpperCase()));
			}
			if (config.hasPath("report.empty-vs.classification")) {
				classification.getEmptyVs().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.empty-vs.classification").toUpperCase()));
			}
			if (config.hasPath("report.vs-error.classification")) {
				classification.getVsError().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-error.classification").toUpperCase()));
			}
			if (config.hasPath("report.binding-location.classification")) {
				classification.getBindingLocation().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.binding-location.classification").toUpperCase()));
			}
			if (config.hasPath("report.vs-no-validation.classification")) {
				classification.getVsNoValidation().setClassfication(HL7V2ValidationClassificationEnum.fromText(config.getString("report.vs-no-validation.classification").toUpperCase()));
			}
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
			String file = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("validationConfigurationTemplate.conf"),"UTF-8");
			
			file = HL7V2ValidationClassificationsToString(classification, file); 
			file = HL7V2ValidationClassificationsToString(defaults, file);
			
			Domain d = domainService.findOneByKey(domain);
			d.setValidationConfiguration(file);
			domainService.save(d);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;			
		}
		
	}

	
	private String HL7V2ValidationClassificationsToString(HL7V2ValidationClassifications classification, String file) {
			
			if (file.contains("@{length-spec-error-no-valid.classification}") && classification.getLengthSpecErrorNoValid().getClassfication() != null) {
				file = file.replace("@{length-spec-error-no-valid.classification}", "\"" +  classification.getLengthSpecErrorNoValid().getClassfication().getText() + "\"");	
			}
			if (file.contains("@{length-spec-error-xor.classification}") && classification.getLengthSpecErrorXor().getClassfication() != null) {
				file = file.replace("@{length-spec-error-xor.classification}", "\"" +  classification.getLengthSpecErrorXor().getClassfication().getText() + "\"");
			}
			if (file.contains("@{r-usage.classification}") && classification.getrUsage().getClassfication() != null) {
				file = file.replace("@{r-usage.classification}", "\"" +  classification.getrUsage().getClassfication().getText() + "\"");
			}
			if (file.contains("@{x-usage.classification}") && classification.getxUsage().getClassfication() != null) {
				file = file.replace("@{x-usage.classification}", "\"" +  classification.getxUsage().getClassfication().getText() + "\"");
			}
			if (file.contains("@{w-usage.classification}") && classification.getwUsage().getClassfication() != null) {
				file = file.replace("@{w-usage.classification}", "\"" +  classification.getwUsage().getClassfication().getText() + "\"");
			}			
			if (file.contains("@{re-usage.classification}") && classification.getReUsage().getClassfication() != null) {
				file = file.replace("@{re-usage.classification}", "\"" +  classification.getReUsage().getClassfication().getText() + "\"");
			}			
			if (file.contains("@{o-usage.classification}") && classification.getoUsage().getClassfication() != null) {
				file = file.replace("@{o-usage.classification}", "\"" +  classification.getoUsage().getClassfication().getText() + "\"");
			}			
			if (file.contains("@{cardinality.classification}") && classification.getCardinality().getClassfication() != null) {
				file = file.replace("@{cardinality.classification}", "\"" +  classification.getCardinality().getClassfication().getText() + "\"");
			}			
			if (file.contains("@{null-cardinality.classification}") && classification.getNullCardinality().getClassfication() != null) {
				file = file.replace("@{null-cardinality.classification}", "\"" +  classification.getNullCardinality().getClassfication().getText() + "\"");
			}	
			if (file.contains("@{length.classification}") && classification.getLength().getClassfication() != null) {
				file = file.replace("@{length.classification}", "\"" +  classification.getLength().getClassfication().getText() + "\"");
			}
			if (file.contains("@{format.classification}") && classification.getFormat().getClassfication() != null) {
				file = file.replace("@{format.classification}", "\"" +  classification.getFormat().getClassfication().getText() + "\"");
			}			
			if (file.contains("@{extra.classification}") && classification.getExtra().getClassfication() != null) {
				file = file.replace("@{extra.classification}", "\"" +  classification.getExtra().getClassfication().getText() + "\"");
			}
			if (file.contains("@{unescaped.classification}") && classification.getUnescaped().getClassfication() != null) {
				file = file.replace("@{unescaped.classification}", "\"" +  classification.getUnescaped().getClassfication().getText() + "\"");
			}
			if (file.contains("@{unexpected.classification}") && classification.getUnexpected().getClassfication() != null) {
				file = file.replace("@{unexpected.classification}", "\"" +  classification.getUnexpected().getClassfication().getText() + "\"");
			}
			if (file.contains("@{invalid.classification}") && classification.getInvalid().getClassfication() != null) {
				file = file.replace("@{invalid.classification}", "\"" +  classification.getInvalid().getClassfication().getText() + "\"");
			}
			if (file.contains("@{unresolved-field.classification}") && classification.getUnresolvedField().getClassfication() != null) {
				file = file.replace("@{unresolved-field.classification}", "\"" +  classification.getUnresolvedField().getClassfication().getText() + "\"");
			}			
			if (file.contains("@{coconstraint-failure.classification}") && classification.getCoconstraintFailure().getClassfication() != null) {
			file = file.replace("@{coconstraint-failure.classification}", "\"" +  classification.getCoconstraintFailure().getClassfication().getText() + "\"");
			}
			if (file.contains("@{coconstraint-success.classification}") && classification.getCoconstraintSuccess().getClassfication() != null) {
			file = file.replace("@{coconstraint-success.classification}", "\"" +  classification.getCoconstraintSuccess().getClassfication().getText() + "\"");
			}
			if (file.contains("@{highlevel-content.classification}") && classification.getHighlevelContent().getClassfication() != null) {
			file = file.replace("@{highlevel-content.classification}", "\"" +  classification.getHighlevelContent().getClassfication().getText() + "\"");
			}
			if (file.contains("@{constraint-failure.classification}") && classification.getConstraintFailure().getClassfication() != null) {
			file = file.replace("@{constraint-failure.classification}", "\"" +  classification.getConstraintFailure().getClassfication().getText() + "\"");
			}
			if (file.contains("@{constraint-success.classification}") && classification.getConstraintSuccess().getClassfication() != null) {
			file = file.replace("@{constraint-success.classification}", "\"" +  classification.getConstraintSuccess().getClassfication().getText() + "\"");
			}
			if (file.contains("@{constraint-spec-error.classification}") && classification.getConstraintSpecError().getClassfication() != null) {
			file = file.replace("@{constraint-spec-error.classification}", "\"" +  classification.getConstraintSpecError().getClassfication().getText() + "\"");
			}
			if (file.contains("@{content-failure.classification}") && classification.getContentFailure().getClassfication() != null) {
			file = file.replace("@{content-failure.classification}", "\"" +  classification.getContentFailure().getClassfication().getText() + "\"");
			}
			if (file.contains("@{content-success.classification}") && classification.getContentSuccess().getClassfication() != null) {
			file = file.replace("@{content-success.classification}", "\"" +  classification.getContentSuccess().getClassfication().getText() + "\"");
			}
			if (file.contains("@{content-spec-error.classification}") && classification.getContentSpecError().getClassfication() != null) {
			file = file.replace("@{content-spec-error.classification}", "\"" +  classification.getContentSpecError().getClassfication().getText() + "\"");
			}
			if (file.contains("@{predicate-success.classification}") && classification.getPredicateSuccess().getClassfication() != null) {
			file = file.replace("@{predicate-success.classification}", "\"" +  classification.getPredicateSuccess().getClassfication().getText() + "\"");
			}
			if (file.contains("@{predicate-failure.classification}") && classification.getPredicateFailure().getClassfication() != null) {
			file = file.replace("@{predicate-failure.classification}", "\"" +  classification.getPredicateFailure().getClassfication().getText() + "\"");
			}
			if (file.contains("@{predicate-spec-error.classification}") && classification.getPredicateSpecError().getClassfication() != null) {
			file = file.replace("@{predicate-spec-error.classification}", "\"" +  classification.getPredicateSpecError().getClassfication().getText() + "\"");
			}
			if (file.contains("@{evs.classification}") && classification.getEvs().getClassfication() != null) {
			file = file.replace("@{evs.classification}", "\"" +  classification.getEvs().getClassfication().getText() + "\"");
			}
			if (file.contains("@{pvs.classification}") && classification.getPvs().getClassfication() != null) {
			file = file.replace("@{pvs.classification}", "\"" +  classification.getPvs().getClassfication().getText() + "\"");
			}
			if (file.contains("@{code-not-found.classification}") && classification.getCodeNotFound().getClassfication() != null) {
			file = file.replace("@{code-not-found.classification}", "\"" +  classification.getCodeNotFound().getClassfication().getText() + "\"");
			}
			if (file.contains("@{vs-not-found.classification}") && classification.getVsNotFound().getClassfication() != null) {
			file = file.replace("@{vs-not-found.classification}", "\"" +  classification.getVsNotFound().getClassfication().getText() + "\"");
			}
			if (file.contains("@{empty-vs.classification}") && classification.getEmptyVs().getClassfication() != null) {
			file = file.replace("@{empty-vs.classification}", "\"" +  classification.getEmptyVs().getClassfication().getText() + "\"");
			}
			if (file.contains("@{vs-error.classification}") && classification.getVsError().getClassfication() != null) {
			file = file.replace("@{vs-error.classification}", "\"" +  classification.getVsError().getClassfication().getText() + "\"");
			}
			if (file.contains("@{binding-location.classification}") && classification.getBindingLocation().getClassfication() != null) {
			file = file.replace("@{binding-location.classification}", "\"" +  classification.getBindingLocation().getClassfication().getText() + "\"");
			}
			if (file.contains("@{vs-no-validation.classification}") && classification.getVsNoValidation().getClassfication() != null) {
			file = file.replace("@{vs-no-validation.classification}", "\"" +  classification.getVsNoValidation().getClassfication().getText() + "\"");
			}
			if (file.contains("@{coded-element.classification}") && classification.getCodedElement().getClassfication() != null) {
			file = file.replace("@{coded-element.classification}", "\"" +  classification.getCodedElement().getClassfication().getText() + "\"");
			}

						
			return file;
		
				
	}



}
