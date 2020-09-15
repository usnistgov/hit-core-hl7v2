/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

package gov.nist.hit.core.hl7v2.api;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.hit.core.api.ValidationConfigurationController;
import gov.nist.hit.core.domain.Domain;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassifications;
import gov.nist.hit.core.hl7v2.repo.HL7V2TestContextRepository;
import gov.nist.hit.core.hl7v2.service.HL7V2MessageParser;
import gov.nist.hit.core.hl7v2.service.HL7V2MessageValidator;
import gov.nist.hit.core.hl7v2.service.HL7V2ValidationReportConverter;
import gov.nist.hit.core.hl7v2.service.impl.HL7V2ValidationConfigurationServiceImpl;
import gov.nist.hit.core.service.DomainService;
import gov.nist.hit.core.service.ValidationReportConverter;
import io.swagger.annotations.Api;

/**
 * @author Harold Affo (NIST)
 * 
 */
@Api(value = "HL7 V2 Validation Configuration", tags = "HL7 V2 Validation Configuration", position = 3)
@RequestMapping("/hl7v2/validationconfig")
@RestController
public class HL7V2ValidationConfigurationController extends ValidationConfigurationController {

  Logger logger = LoggerFactory.getLogger(HL7V2ValidationConfigurationController.class);

  @Autowired
  protected HL7V2TestContextRepository testContextRepository;

  @Autowired
  protected HL7V2MessageValidator messageValidator;

  @Autowired
  protected HL7V2MessageParser messageParser;

  @Autowired
  private HL7V2ValidationReportConverter validationReportConverter;
  
  
 

  @Autowired
  private HL7V2ValidationConfigurationServiceImpl validationConfigurationService;
  



  public HL7V2TestContextRepository getTestContextRepository() {
    return testContextRepository;
  }

  public void setTestContextRepository(HL7V2TestContextRepository testContextRepository) {
    this.testContextRepository = testContextRepository;
  }

  
  public HL7V2MessageValidator getMessageValidator() {
    return messageValidator;
  }

  public void setMessageValidator(HL7V2MessageValidator messageValidator) {
    this.messageValidator = messageValidator;
  }

  
  public HL7V2MessageParser getMessageParser() {
    return messageParser;
  }

  public void setMessageParser(HL7V2MessageParser messageParser) {
    this.messageParser = messageParser;
  }


  
  public ValidationReportConverter getValidatioReportConverter() {
    return validationReportConverter;
  }
  
  
  
  
  
  
	@RequestMapping(value = "/{domain}/getClassifications", method = RequestMethod.GET,  produces = "application/json")
	@ResponseBody
	public HL7V2ValidationClassifications getClassifications(ServletRequest request, Principal p,  @PathVariable("domain") String domain) throws Exception {
	  HL7V2ValidationClassifications res = (HL7V2ValidationClassifications)validationConfigurationService.getClassifications(domain);
		return res;
	}
	
	@RequestMapping(value = "/getDefaultClassifications", method = RequestMethod.GET,  produces = "application/json")
	@ResponseBody
	public HL7V2ValidationClassifications getDefaultClassifications(ServletRequest request, Principal p) throws Exception {
	  HL7V2ValidationClassifications res = (HL7V2ValidationClassifications)validationConfigurationService.getDefaultClassifications();
		return res;
	}
  
	@PreAuthorize("hasRole('tester')")
	@RequestMapping(value = "/{domain}/saveClassifications", method = RequestMethod.POST,  produces = "application/json")
	public  Map<String, Object> saveClassifications(ServletRequest request, Principal p,  @PathVariable("domain") String domain, @RequestBody HL7V2ValidationClassifications classifications) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean res = validationConfigurationService.saveClassifications(classifications,domain);
				
	
		resultMap.put("success", true);
		resultMap.put("classification", res);
		resultMap.put("message", "Classifications saved successfuly!");
		return resultMap;
	}



}
