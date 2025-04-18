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

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.hit.core.api.TestContextController;
import gov.nist.hit.core.domain.ResourceType;
import gov.nist.hit.core.domain.ResourceUploadAction;
import gov.nist.hit.core.domain.ResourceUploadResult;
import gov.nist.hit.core.domain.ResourceUploadStatus;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.domain.TestScope;
import gov.nist.hit.core.hl7v2.domain.APIKey;
import gov.nist.hit.core.hl7v2.domain.APIKeyCommand;
import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;
import gov.nist.hit.core.hl7v2.repo.HL7V2TestContextRepository;
import gov.nist.hit.core.hl7v2.service.HL7V2MessageParser;
import gov.nist.hit.core.hl7v2.service.HL7V2MessageValidator;
import gov.nist.hit.core.hl7v2.service.HL7V2TestContextService;
import gov.nist.hit.core.hl7v2.service.HL7V2ValidationReportConverter;
import gov.nist.hit.core.hl7v2.service.impl.HL7V2ResourceLoaderImpl;
import gov.nist.hit.core.service.AppInfoService;
import gov.nist.hit.core.service.UserIdService;
import gov.nist.hit.core.service.UserService;
import gov.nist.hit.core.service.ValidationReportConverter;
import gov.nist.hit.core.service.exception.NoUserFoundException;
import gov.nist.hit.core.service.exception.ProfileParserException;
import io.swagger.annotations.Api;

/**
 * @author Harold Affo (NIST)
 * 
 */
@Api(value = "HL7 V2 Test Context", tags = "HL7 V2 Test Context", position = 3)
@RequestMapping("/hl7v2/testcontext")
@RestController
public class HL7V2TestContextController extends TestContextController {

  Logger logger = LoggerFactory.getLogger(HL7V2TestContextController.class);

  @Autowired
  protected HL7V2TestContextRepository testContextRepository;

  @Autowired
  protected HL7V2MessageValidator messageValidator;

  @Autowired
  protected HL7V2MessageParser messageParser;

  @Autowired
  private HL7V2ValidationReportConverter validationReportConverter;
  
  @Autowired
	private AppInfoService appInfoService;
  
  @Autowired
   private HL7V2TestContextService hL7V2TestContextService;
  
	@Autowired
	private HL7V2ResourceLoaderImpl resourceLoader;

  @Override
  public TestContext getTestContext(Long testContextId) {
    logger.info("Fetching testContext with id=" + testContextId);
    return testContextRepository.findOne(testContextId);
  }


  public HL7V2TestContextRepository getTestContextRepository() {
    return testContextRepository;
  }

  
  public void setTestContextRepository(HL7V2TestContextRepository testContextRepository) {
    this.testContextRepository = testContextRepository;
  }

  @Override
  public HL7V2MessageValidator getMessageValidator() {
    return messageValidator;
  }

  public void setMessageValidator(HL7V2MessageValidator messageValidator) {
    this.messageValidator = messageValidator;
  }

  @Override
  public HL7V2MessageParser getMessageParser() {
    return messageParser;
  }

  public void setMessageParser(HL7V2MessageParser messageParser) {
    this.messageParser = messageParser;
  }


  @Override
  public ValidationReportConverter getValidatioReportConverter() {
    return validationReportConverter;
  }
  
  @Autowired
	private UserService userService;

	@Autowired
	private UserIdService userIdService;
  
  

	@PreAuthorize("hasRole('tester')")
	@RequestMapping(value = "/{testContextId}/apikey", method = RequestMethod.POST)
	public ResourceUploadStatus updateTestContextApiKeys(HttpServletRequest request, @PathVariable("testContextId") Long testContextId, Principal p,
			@RequestBody List<APIKeyCommand> apiKeyCommands)  {
		
		try {
			checkManagementSupport();
			HL7V2TestContext testContext = hL7V2TestContextService.findOne(testContextId);
			checkPermission(testContextId, testContext, p);
			
			Set<APIKey> existingApiKeys = testContext.getApikeys();
			List<APIKeyCommand> modifiedApiKeys = apiKeyCommands;
	
			boolean tcHasModifications =false;
			for (APIKeyCommand key : modifiedApiKeys) {						
					for(APIKey key2 : existingApiKeys) {
						if (key.getId().equals(key2.getId()) && key.isEditBindingKey()) {
							if (key.getBindingKey() ==null || key.getBindingKey().isEmpty()) {	
								//remove key
								key2.setBindingKey(null);
								tcHasModifications = true;
							}else {
								key2.setBindingKey(key.getBindingKey());
								tcHasModifications = true;
							}
						}
					}							
			}		
			if (tcHasModifications) {
				hL7V2TestContextService.save(testContext);
			}
									
			
			ResourceUploadStatus result = new ResourceUploadStatus();
			result.setType(ResourceType.TESTCONTEXT);
			result.setAction(ResourceUploadAction.UPDATE);
			result.setId(testContext.getId());
			result.setStatus(ResourceUploadResult.SUCCESS);
			return result;
		
		} catch (Exception e) {
			e.printStackTrace();	
			return null;
		}
	}
	
	@PreAuthorize("hasRole('tester')")
	@RequestMapping(value = "/{testContextId}/updateConformanceProfileModel", method = RequestMethod.POST)
	public ResourceUploadStatus updateConformanceProfileJson(HttpServletRequest request, @PathVariable("testContextId") Long testContextId, Principal p)  {		
		HL7V2TestContext testContext = hL7V2TestContextService.findOne(testContextId);
		//has to be textcontext user or admin
			try {
				checkPermission(testContextId, testContext, p);
		
				resourceLoader.updateConformanceProfileJson(testContext);
				hL7V2TestContextService.save(testContext);
				
				ResourceUploadStatus result = new ResourceUploadStatus();
				result.setType(ResourceType.TESTCONTEXT);
				result.setAction(ResourceUploadAction.UPDATE);
				result.setId(testContext.getId());
				result.setStatus(ResourceUploadResult.SUCCESS);
				result.setMessage("model updated");
				return result;
			} catch (ProfileParserException | IOException e) {				
				ResourceUploadStatus result = new ResourceUploadStatus();
				result.setType(ResourceType.TESTCONTEXT);
				result.setAction(ResourceUploadAction.UPDATE);
				result.setId(testContext.getId());
				result.setStatus(ResourceUploadResult.FAILURE);
				result.setMessage("An error occured while updating ");
				return result;
			} catch  (NoUserFoundException e) {
				ResourceUploadStatus result = new ResourceUploadStatus();
				result.setType(ResourceType.TESTCONTEXT);
				result.setAction(ResourceUploadAction.UPDATE);
				result.setId(testContext.getId());
				result.setStatus(ResourceUploadResult.FAILURE);
				result.setMessage(e.getMessage());
				return result;
			} catch (Exception e) {
				ResourceUploadStatus result = new ResourceUploadStatus();
				result.setType(ResourceType.TESTCONTEXT);
				result.setAction(ResourceUploadAction.UPDATE);
				result.setId(testContext.getId());
				result.setStatus(ResourceUploadResult.FAILURE);
				result.setMessage("An unknonwn error occured.");
				return result;

			}
						
	}
	
	

	private void checkManagementSupport() throws Exception {
		if (!appInfoService.get().isCbManagementSupported()) {
			throw new Exception("This operation is not supported by this tool");
		}
	}
	
	private void checkPermission(Long id, TestContext testContext, Principal p) throws Exception {
		String username = userIdService.getCurrentUserName(p);
		if (username == null)
			throw new NoUserFoundException("User could not be found");
		if (testContext == null)
			throw new Exception("No testcontext (" + id + ") found");
		TestScope scope = testContext.getScope();		
		if (!username.equals(testContext.getAuthorUsername()) && !userService.isAdmin(username)) {
			throw new NoUserFoundException("You do not have the permission to perform this task");
		}
	}

}
