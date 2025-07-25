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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nist.hit.core.domain.CFTestPlan;
import gov.nist.hit.core.domain.CFTestStep;
import gov.nist.hit.core.domain.CFTestStepGroup;
import gov.nist.hit.core.domain.GVTSaveInstance;
import gov.nist.hit.core.domain.Message;
import gov.nist.hit.core.domain.ResourceUploadResult;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.domain.TestScope;
import gov.nist.hit.core.domain.UploadStatus;
import gov.nist.hit.core.domain.ValueSetDefinition;
import gov.nist.hit.core.hl7v2.domain.APIKey;
import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;
import gov.nist.hit.core.hl7v2.domain.TestCaseWrapper;
import gov.nist.hit.core.hl7v2.domain.UploadedProfileModel;
import gov.nist.hit.core.hl7v2.service.FileValidationHandler;
import gov.nist.hit.core.hl7v2.service.HL7V2TestContextService;
import gov.nist.hit.core.hl7v2.service.PackagingHandler;
import gov.nist.hit.core.hl7v2.service.impl.FileValidationHandlerImpl.InvalidFileTypeException;
import gov.nist.hit.core.hl7v2.service.impl.HL7V2ProfileParserImpl;
import gov.nist.hit.core.hl7v2.service.impl.HL7V2ResourceLoaderImpl;
import gov.nist.hit.core.repo.CoConstraintsRepository;
import gov.nist.hit.core.repo.ConstraintsRepository;
import gov.nist.hit.core.repo.IntegrationProfileRepository;
import gov.nist.hit.core.repo.SlicingsRepository;
import gov.nist.hit.core.repo.UserTestCaseGroupRepository;
import gov.nist.hit.core.repo.ValueSetBindingsRepository;
import gov.nist.hit.core.repo.VocabularyLibraryRepository;
import gov.nist.hit.core.service.AppInfoService;
import gov.nist.hit.core.service.BundleHandler;
import gov.nist.hit.core.service.CFTestPlanService;
import gov.nist.hit.core.service.CFTestStepGroupService;
import gov.nist.hit.core.service.CFTestStepService;
import gov.nist.hit.core.service.ProfileParser;
import gov.nist.hit.core.service.UserIdService;
import gov.nist.hit.core.service.UserService;
import gov.nist.hit.core.service.exception.MessageUploadException;
import gov.nist.hit.core.service.exception.NoUserFoundException;
import gov.nist.hit.core.service.exception.NotValidToken;
import gov.nist.hit.hl7.profile.validation.domain.ProfileValidationReport;

/**
 * @author Nicolas Crouzier (NIST)
 * @author Harold Affo (NIST)
 * 
 */

@RequestMapping("/cf/hl7v2/management")
@Controller
public class HL7V2CFManagementController {

  static final Logger logger = LoggerFactory.getLogger(HL7V2CFManagementController.class);
  
//  @Value("${UPLOADED_RESOURCE_BUNDLE:/sites/data/uploaded_resource_bundles}")
//  private String UPLOADED_RESOURCE_BUNDLE;

  private String CF_RESOURCE_BUNDLE_DIR;
  
  ProfileParser parser = new HL7V2ProfileParserImpl();

  @Autowired
  private CFTestPlanService tpService;

  @Autowired
  private UserTestCaseGroupRepository testCaseGroupRepository;

  @Autowired
  private IntegrationProfileRepository ipRepository;

  @Autowired
  private ConstraintsRepository csRepository;

  @Autowired
  private VocabularyLibraryRepository vsRepository;
  
  @Autowired
  private SlicingsRepository slicingsRepository;
  
  @Autowired
  private ValueSetBindingsRepository vsbRepository;
  
  @Autowired
  private CoConstraintsRepository cocsRepository;

  @Autowired
  private UserIdService userIdService;

  @Autowired
  private BundleHandler bundleHandler;

  @Autowired
  private PackagingHandler packagingHandler;

  @Autowired
  private FileValidationHandler fileValidationHandler;

  @Autowired
  private UserService userService;

  @Autowired
  private CFTestPlanService testPlanService;

  @Autowired
  private CFTestStepService testStepService;

  @Autowired
  private AppInfoService appInfoService;
  
  @Autowired
  private CFTestStepGroupService testStepGroupService;
  
  @Autowired
  private HL7V2ResourceLoaderImpl resourceLoader;

	@PostConstruct
	  public void init() {
	    CF_RESOURCE_BUNDLE_DIR = appInfoService.getUploadsFolderPath() + "/cf";
	  }

  private void checkManagementSupport() throws Exception {
    if (!appInfoService.get().isCfManagementSupported()) {
      throw new Exception("This operation is not supported by this tool");
    }
  }



  /**
   * Upload a single XML profile file and may returns errors
   * 
   * @param request Client request
   * @param part Profile XML file
   * @param token Token used for saving file
   * @param p Principal
   * @return A list of profiles or a list of errors
   * @throws Exception
   */
	@PreAuthorize("hasRole('tester')")
	@RequestMapping(value = "/validate", method = RequestMethod.GET,  produces = "application/json")
	@ResponseBody
	public Map<String, Object> validate(ServletRequest request,
			@RequestParam("token") String token, Principal p) throws Exception {
		checkManagementSupport();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			String userName = userIdService.getCurrentUserName(p);
			if (userName == null)
				throw new NoUserFoundException("User could not be found");


			ProfileValidationReport report = fileValidationHandler.getHTMLValidatioReport(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token);
			
			if ((report == null || (report != null && report.isSuccess()))) {
			resultMap.put("success", true);
			}else {
				resultMap.put("success", false);
				if(report != null) {
					resultMap.put("report", report.generateHTML());
				}				
			}
			
			return resultMap;
		} catch (NoUserFoundException e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not validate the files sent");
			resultMap.put("debugError", ExceptionUtils.getMessage(e));
			return resultMap;
		}  catch (InvalidFileTypeException e) {
			resultMap.put("success", false);
			resultMap.put("report", String.join("<br>", e.getErrors()));
			return resultMap;
		} catch (MessageUploadException e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not validate the files sent");
			resultMap.put("debugError", ExceptionUtils.getMessage(e));
			return resultMap;
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not validate the files sent");
			resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
			return resultMap;
		}
	}
  
  /**
   * Upload a single XML profile file and may returns errors
   * 
   * @param request Client request
   * @param part Profile XML file
   * @param token Token used for saving file
   * @param p Principal
   * @return A list of profiles or a list of errors
   * @throws Exception
   */
	@PreAuthorize("hasRole('tester')")
	@RequestMapping(value = "/uploadProfiles", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ResponseBody
	public Map<String, Object> uploadProfile(ServletRequest request, @RequestPart("file") MultipartFile part,
			@RequestParam("token") String token, Principal p) throws Exception {
		checkManagementSupport();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String filename = part.getOriginalFilename();
			String extension = filename.substring(filename.lastIndexOf(".") + 1);
			if (!extension.equalsIgnoreCase("xml")) {
				throw new MessageUploadException(
			            "Unsupported content type. Supported content types are: '.xml' ");
			}			

			String userName = userIdService.getCurrentUserName(p);
			if (userName == null)
				throw new NoUserFoundException("User could not be found");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(part.getInputStream(), baos);
			byte[] bytes = baos.toByteArray();

			String content = IOUtils.toString(new ByteArrayInputStream(bytes));
			File profileFile = new File(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token + "/Profile.xml");
			FileUtils.writeStringToFile(profileFile, content);

		
			//TODO get profile list when validation is done.
//			List<UploadedProfileModel> list = packagingHandler.getUploadedProfiles(content,null,null,null);
			resultMap.put("success", true);
//			resultMap.put("profiles", list);
					

			return resultMap;
		} catch (NoUserFoundException e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not upload the profile file sent");
			resultMap.put("debugError", ExceptionUtils.getMessage(e));
			return resultMap;
		} catch (MessageUploadException e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not upload the profile file sent");
			resultMap.put("debugError", ExceptionUtils.getMessage(e));
			return resultMap;
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not upload the profile file sent");
			resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
			return resultMap;
		}
	}

  /**
   * Upload a single XML value set file and may returns errors
   * 
   * @param request Client request
   * @param part Value Set XML file
   * @param token Token used for saving file
   * @param p Principal
   * @return May return a list of errors
   * @throws Exception
   */
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/uploadValueSets", method = RequestMethod.POST,
      consumes = {"multipart/form-data"})
  @ResponseBody
  public Map<String, Object> uploadVS(ServletRequest request,
      @RequestPart("file") MultipartFile part, @RequestParam("token") String token, Principal p)
      throws Exception {
    checkManagementSupport();
    Map<String, Object> resultMap = new HashMap<String, Object>();
    try {
    		String filename = part.getOriginalFilename();
		String extension = filename.substring(filename.lastIndexOf(".") + 1);
		if (!extension.equalsIgnoreCase("xml")) {
			throw new MessageUploadException(
		            "Unsupported content type. Supported content types are: '.xml' ");
		}

      String userName = userIdService.getCurrentUserName(p);
      if (userName == null)
        throw new NoUserFoundException("User could not be found");

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      org.apache.commons.io.IOUtils.copy(part.getInputStream(), baos);
      byte[] bytes = baos.toByteArray();
      String content = IOUtils.toString(new ByteArrayInputStream(bytes));
      File vsFile = new File(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token + "/ValueSets.xml");
      FileUtils.writeStringToFile(vsFile, content);
      
		resultMap.put("success", true);
	
      return resultMap;
    } catch (NoUserFoundException e) {
      resultMap.put("success", false);
      resultMap.put("message",
          "An error occured. The tool could not upload the valueset file sent");
      resultMap.put("debugError", ExceptionUtils.getMessage(e));
      return resultMap;
    } catch (MessageUploadException e) {
      resultMap.put("success", false);
      resultMap.put("message",
          "An error occured. The tool could not upload the valueset file sent");
      resultMap.put("debugError", ExceptionUtils.getMessage(e));
      return resultMap;
    } catch (Exception e) {
      resultMap.put("success", false);
      resultMap.put("message",
          "An error occured. The tool could not upload the valueset file sent");
      resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
      return resultMap;
    }
  }

  /**
   * Upload a single XML constraints file and may returns errors
   * 
   * @param request Client request
   * @param part Constraints XML file
   * @param token Token used for saving file
   * @param p Principal
   * @return May return a list of errors
   * @throws Exception
   */
	@PreAuthorize("hasRole('tester')")
	@RequestMapping(value = "/uploadConstraints", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ResponseBody
	public Map<String, Object> uploadContraints(ServletRequest request, @RequestPart("file") MultipartFile part,
			@RequestParam("token") String token, Principal p) throws Exception {
		checkManagementSupport();

		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String filename = part.getOriginalFilename();
			String extension = filename.substring(filename.lastIndexOf(".") + 1);
			if (!extension.equalsIgnoreCase("xml")) {
				throw new MessageUploadException(
			            "Unsupported content type. Supported content types are: '.xml' ");
			}

			String userName = userIdService.getCurrentUserName(p);
			if (userName == null)
				throw new NoUserFoundException("User could not be found");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(part.getInputStream(), baos);
			byte[] bytes = baos.toByteArray();
			String content = IOUtils.toString(new ByteArrayInputStream(bytes));
			File constraintFile = new File(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token + "/Constraints.xml");
			FileUtils.writeStringToFile(constraintFile, content);


			resultMap.put("success", true);

			return resultMap;
		} catch (NoUserFoundException e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not upload the constraints file sent");
			resultMap.put("debugError", ExceptionUtils.getMessage(e));
			return resultMap;
		} catch (MessageUploadException e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not upload the constraints file sent");
			resultMap.put("debugError", ExceptionUtils.getMessage(e));
			return resultMap;
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "An error occured. The tool could not upload the constraints file sent");
			resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
			return resultMap;
		}
	}

	
	 /**
	   * Upload a single XML coConstraints file and may returns errors
	   * 
	   * @param request Client request
	   * @param part coConstraints XML file
	   * @param token Token used for saving file
	   * @param p Principal
	   * @return May return a list of errors
	   * @throws Exception
	   */
		@PreAuthorize("hasRole('tester')")
		@RequestMapping(value = "/uploadCoConstraints", method = RequestMethod.POST, consumes = { "multipart/form-data" })
		@ResponseBody
		public Map<String, Object> uploadCoContraints(ServletRequest request, @RequestPart("file") MultipartFile part,
				@RequestParam("token") String token, Principal p) throws Exception {
			checkManagementSupport();

			Map<String, Object> resultMap = new HashMap<String, Object>();
			try {
				String filename = part.getOriginalFilename();
				String extension = filename.substring(filename.lastIndexOf(".") + 1);
				if (!extension.equalsIgnoreCase("xml")) {
					throw new MessageUploadException(
				            "Unsupported content type. Supported content types are: '.xml' ");
				}

				String userName = userIdService.getCurrentUserName(p);
				if (userName == null)
					throw new NoUserFoundException("User could not be found");
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				org.apache.commons.io.IOUtils.copy(part.getInputStream(), baos);
				byte[] bytes = baos.toByteArray();
				String content = IOUtils.toString(new ByteArrayInputStream(bytes));
				File coConstraintFile = new File(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token + "/CoConstraints.xml");
				FileUtils.writeStringToFile(coConstraintFile, content);


				resultMap.put("success", true);

				return resultMap;
			} catch (NoUserFoundException e) {
				resultMap.put("success", false);
				resultMap.put("message", "An error occured. The tool could not upload the coConstraints file sent");
				resultMap.put("debugError", ExceptionUtils.getMessage(e));
				return resultMap;
			} catch (MessageUploadException e) {
				resultMap.put("success", false);
				resultMap.put("message", "An error occured. The tool could not upload the coConstraints file sent");
				resultMap.put("debugError", ExceptionUtils.getMessage(e));
				return resultMap;
			} catch (Exception e) {
				resultMap.put("success", false);
				resultMap.put("message", "An error occured. The tool could not upload the coConstraints file sent");
				resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
				return resultMap;
			}
		}
		
		/**
		   * Upload a single XML Value Set Bindings file and may returns errors
		   * 
		   * @param request Client request
		   * @param part Value Set Bindings XML file
		   * @param token Token used for saving file
		   * @param p Principal
		   * @return May return a list of errors
		   * @throws Exception
		   */
			@PreAuthorize("hasRole('tester')")
			@RequestMapping(value = "/uploadValueSetBindings", method = RequestMethod.POST, consumes = { "multipart/form-data" })
			@ResponseBody
			public Map<String, Object> uploadValueSetBindings(ServletRequest request, @RequestPart("file") MultipartFile part,
					@RequestParam("token") String token, Principal p) throws Exception {
				checkManagementSupport();

				Map<String, Object> resultMap = new HashMap<String, Object>();
				try {
					String filename = part.getOriginalFilename();
					String extension = filename.substring(filename.lastIndexOf(".") + 1);
					if (!extension.equalsIgnoreCase("xml")) {
						throw new MessageUploadException(
					            "Unsupported content type. Supported content types are: '.xml' ");
					}

					String userName = userIdService.getCurrentUserName(p);
					if (userName == null)
						throw new NoUserFoundException("User could not be found");
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					org.apache.commons.io.IOUtils.copy(part.getInputStream(), baos);
					byte[] bytes = baos.toByteArray();
					String content = IOUtils.toString(new ByteArrayInputStream(bytes));
					File valueSetBindingsFile = new File(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token + "/ValueSetBindings.xml");
					FileUtils.writeStringToFile(valueSetBindingsFile, content);
					resultMap.put("success", true);

					return resultMap;
				} catch (NoUserFoundException e) {
					resultMap.put("success", false);
					resultMap.put("message", "An error occured. The tool could not upload the Value Set Bindings file sent");
					resultMap.put("debugError", ExceptionUtils.getMessage(e));
					return resultMap;
				} catch (MessageUploadException e) {
					resultMap.put("success", false);
					resultMap.put("message", "An error occured. The tool could not upload the Value Set Bindings file sent");
					resultMap.put("debugError", ExceptionUtils.getMessage(e));
					return resultMap;
				} catch (Exception e) {
					resultMap.put("success", false);
					resultMap.put("message", "An error occured. The tool could not upload the Value Set Bindings file sent");
					resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
					return resultMap;
				}
			}
			
			/**
			   * Upload a single XML Slicings file and may returns errors
			   * 
			   * @param request Client request
			   * @param part Slicings XML file
			   * @param token Token used for saving file
			   * @param p Principal
			   * @return May return a list of errors
			   * @throws Exception
			   */
				@PreAuthorize("hasRole('tester')")
				@RequestMapping(value = "/uploadSlicings", method = RequestMethod.POST, consumes = { "multipart/form-data" })
				@ResponseBody
				public Map<String, Object> uploadSlicings(ServletRequest request, @RequestPart("file") MultipartFile part,
						@RequestParam("token") String token, Principal p) throws Exception {
					checkManagementSupport();

					Map<String, Object> resultMap = new HashMap<String, Object>();
					try {
						String filename = part.getOriginalFilename();
						String extension = filename.substring(filename.lastIndexOf(".") + 1);
						if (!extension.equalsIgnoreCase("xml")) {
							throw new MessageUploadException(
						            "Unsupported content type. Supported content types are: '.xml' ");
						}

						String userName = userIdService.getCurrentUserName(p);
						if (userName == null)
							throw new NoUserFoundException("User could not be found");
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						org.apache.commons.io.IOUtils.copy(part.getInputStream(), baos);
						byte[] bytes = baos.toByteArray();
						String content = IOUtils.toString(new ByteArrayInputStream(bytes));
						File slicingsFile = new File(CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token + "/Slicings.xml");
						FileUtils.writeStringToFile(slicingsFile, content);
						resultMap.put("success", true);

						return resultMap;
					} catch (NoUserFoundException e) {
						resultMap.put("success", false);
						resultMap.put("message", "An error occured. The tool could not upload the Slicings file sent");
						resultMap.put("debugError", ExceptionUtils.getMessage(e));
						return resultMap;
					} catch (MessageUploadException e) {
						resultMap.put("success", false);
						resultMap.put("message", "An error occured. The tool could not upload the Slicings file sent");
						resultMap.put("debugError", ExceptionUtils.getMessage(e));
						return resultMap;
					} catch (Exception e) {
						resultMap.put("success", false);
						resultMap.put("message", "An error occured. The tool could not upload the Slicings file sent");
						resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
						return resultMap;
					}
				}
		
  /**
   * Uploads zip file and stores it in a temporary directory
   * 
   * @param request Client request
   * @param part Zip file
   * @param p Principal
   * @return a token or some errors
   * @throws Exception
   */
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/uploadZip", method = RequestMethod.POST,
      consumes = {"multipart/form-data"})
  @ResponseBody
  public Map<String, Object> uploadZip(ServletRequest request,
      @RequestPart("file") MultipartFile part,@RequestParam("domain") String domain, Principal p) throws Exception {
    checkManagementSupport();

    Map<String, Object> resultMap = new HashMap<String, Object>();
    try {
    		String filename = part.getOriginalFilename();
		String extension = filename.substring(filename.lastIndexOf(".") + 1);
		if (!extension.equalsIgnoreCase("zip")) { 
			throw new MessageUploadException(
		            "Unsupported content type. Supported content types are: '.zip' ");
		}
    	     

      String userName = userIdService.getCurrentUserName(p);
      if (userName == null)
        throw new NoUserFoundException("User could not be found");

      String token = UUID.randomUUID().toString();
      String directory =  bundleHandler.unzip(part.getBytes(), CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token);
      ProfileValidationReport report = fileValidationHandler.getHTMLValidatioReport(directory);
      
     
      if (!report.isSuccess()) {
            resultMap.put("success", false);
            resultMap.put("report", report.generateHTML());            
            FileUtils.deleteDirectory(new File(directory));
            logger.info("Uploaded profile file with errors " + part.getName());
          } else {
            resultMap.put("success", true);
            resultMap.put("report", report.generateHTML());  
            resultMap.put("token", token);
            resultMap.put("domain", domain);

            logger.info("Uploaded valid zip File file " + part.getName());
       }
      

    } catch (NoUserFoundException e) {
      resultMap.put("success", false);
      resultMap.put("message", "An error occured. The tool could not upload the zip file sent");
      resultMap.put("debugError", ExceptionUtils.getMessage(e));
      return resultMap;
    } catch (MessageUploadException e) {
      resultMap.put("success", false);
      resultMap.put("message", "An error occured. The tool could not upload the zip file sent");
      resultMap.put("debugError", ExceptionUtils.getMessage(e));
      return resultMap;
    } catch (Exception e) {
      resultMap.put("success", false);
      resultMap.put("message", "An error occured. The tool could not upload the zip file sent");
      resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
      return resultMap;
    }
    return resultMap;
  }

  /**
   * Retrieves a list of profiles from previously uploaded files
   * 
   * @param request Client request
   * @param token token from uploaded files
   * @param p Principal
   * @return A list of profiles
   * @throws Exception
   */
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/tokens/{token}/profiles", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> getTokenProfiles(ServletRequest request,
      @PathVariable("token") String token, Principal p) throws Exception {
    checkManagementSupport();

    Map<String, Object> resultMap = new HashMap<String, Object>();
    try {

      String userName = userIdService.getCurrentUserName(p);
      if (userName == null)
        throw new NoUserFoundException("User could not be found");

      String directory = CF_RESOURCE_BUNDLE_DIR + "/" + userName + "/" + token;
      if (!new File(directory).exists())
        throw new NotValidToken("The provided token is not valid for this account.");

      String profileContent = bundleHandler.getProfileContentFromZipDirectory(directory);
      String valueSetContent = bundleHandler.getValueSetContentFromZipDirectory(directory);
      String valueSetBindingContent = bundleHandler.getValueSetBindingsContentFromZipDirectory(directory);
      String coConstraintContent = bundleHandler.getCoConstraintContentFromZipDirectory(directory);
      
      if (profileContent == null)
        throw new MessageUploadException("Could not retrieve the profile list");

      List<UploadedProfileModel> list = packagingHandler.getUploadedProfiles(profileContent,valueSetContent,valueSetBindingContent,coConstraintContent);
      resultMap.put("success", true);
      resultMap.put("profiles", list);

      logger.info("retrieved profile info from upload");

    } catch (NoUserFoundException e) {
      resultMap.put("success", false);
      resultMap.put("message",
          "An error occured. The tool could not retrieve the uploaded profiles");
      resultMap.put("debugError", ExceptionUtils.getMessage(e));
      return resultMap;
    } catch (NotValidToken e) {
      resultMap.put("success", false);
      resultMap.put("message",
          "An error occured. The tool could not retrieve the uploaded profiles");
      resultMap.put("debugError", ExceptionUtils.getMessage(e));
      return resultMap;
    } catch (Exception e) {
      resultMap.put("success", false);
      resultMap.put("message",
          "An error occured. The tool could not retrieve the uploaded profiles");
      resultMap.put("debugError", ExceptionUtils.getStackTrace(e));
      return resultMap;
    }
    return resultMap;
  }



  private CFTestStep findTestStep(Long id, CFTestPlan testPlan) {
    if (testPlan != null && testPlan.getTestSteps() != null) {
      for (CFTestStep testStep : testPlan.getTestSteps()) {
        if (testStep.getId().equals(id)) {
          return testStep;
        }
      }
    }
    return null;
  }
  
  private CFTestStep findTestStep(Long id, Set<CFTestStep> testSteps) {
	    if (testSteps != null && testSteps != null) {
	      for (CFTestStep testStep : testSteps) {
	        if (testStep.getId().equals(id)) {
	          return testStep;
	        }
	      }
	    }
	    return null;
	  }


  /**
   * Add selected profiles to the database
   * 
   * @param request Client request
   * @param wrapper Selected profile information
   * @param p Principal
   * @return UploadStatus
   */
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/testPlans/{testPlanId}", method = RequestMethod.POST)
  @ResponseBody
  public UploadStatus saveTestPlan(HttpServletRequest request,
      @PathVariable("testPlanId") Long testPlanId, @RequestBody TestCaseWrapper wrapper,
      Authentication auth) {
    try {
      checkManagementSupport();
      CFTestPlan testPlan = testPlanService.findOne(testPlanId);
      if (testPlan == null) {
        throw new Exception("Profile Group could not be found");
      }
      String authUsername = auth.getName();
      String username = testPlan.getAuthorUsername(); //
      if (!authUsername.equals(username) && !userService.isAdmin(authUsername)) {
        throw new Exception("You do not have sufficient right to change this profile group");
      }

      // String username = null;
      Set<CFTestStep> testSteps = testPlan.getTestSteps();
      if (testSteps == null) {
        testSteps = new HashSet<CFTestStep>();
        testPlan.setTestSteps(testSteps);
      }

      testPlan.setName(wrapper.getTestcasename());
      testPlan.setDescription(wrapper.getTestcasedescription());
      //creates the TestCases.json in the token folder 
      createTestStepFiles(testPlan.getDomain(), testSteps, wrapper, auth);
      if (wrapper.getToken() != null) {
        // Use files to save to database
        GVTSaveInstance si = bundleHandler.createSaveInstance(
        		CF_RESOURCE_BUNDLE_DIR + "/" + authUsername + "/" + wrapper.getToken(), testPlan);
        ipRepository.save(si.ip);
        csRepository.save(si.ct);
        vsRepository.save(si.vs);
        //optionals
        
        if(si.slicings != null) {
        	slicingsRepository.save(si.slicings);
        }
        if(si.coct != null) {
        	//if not id generate one random
        	cocsRepository.save(si.coct);
        }
        
        if(si.vsBindings != null) {
        	//if not id generate one random
        	vsbRepository.save(si.vsBindings);
        }            
        si.tcg.setAuthorUsername(username);
        testPlanService.save((CFTestPlan) si.tcg);
        testPlanService.removeCacheElement(testPlan.getId());
        FileUtils.deleteDirectory(new File(CF_RESOURCE_BUNDLE_DIR + "/" + authUsername + "/" + wrapper.getToken()));
      } else {
        testPlanService.save(testPlan);
      }
      return new UploadStatus(ResourceUploadResult.SUCCESS, "Profile Group save successfully",
          testPlan.getId());
    } catch (IOException e) {
      return new UploadStatus(ResourceUploadResult.FAILURE, "IO Error could not read files",
          ExceptionUtils.getStackTrace(e));
    } catch (NoUserFoundException e) {
      return new UploadStatus(ResourceUploadResult.FAILURE, "User could not be found",
          ExceptionUtils.getStackTrace(e));
    } catch (Exception e) {
      return new UploadStatus(ResourceUploadResult.FAILURE,
          "An error occured while adding profiles", ExceptionUtils.getStackTrace(e));
    }

  }
  
  public void createTestStepFiles(String domain, Set<CFTestStep> testSteps, TestCaseWrapper wrapper,
	      Authentication auth) throws Exception {

	    String username = auth.getName();
	    if (wrapper.getScope() == null)
	      throw new NoUserFoundException("Scope not be found");
	    TestScope scope = TestScope.valueOf(wrapper.getScope().toUpperCase());
	    if (scope.equals(TestScope.GLOBAL) && !userService.hasGlobalAuthorities(username)) {
	      throw new NoUserFoundException("You do not have the permission to perform this task");
	    }

	    Set<UploadedProfileModel> removed = wrapper.getRemoved();
	    if (removed != null && !removed.isEmpty()) {
	      for (UploadedProfileModel model : removed) {
	        Long id = Long.valueOf(model.getId());
	        CFTestStep found = findTestStep(id, testSteps);
	        if (found != null) {
	          testSteps.remove(found);
	        }
	      }
	    }

	    Set<UploadedProfileModel> updated = wrapper.getUpdated();
	    if (updated != null && !updated.isEmpty()) {
	      for (UploadedProfileModel model : updated) {
	        Long id = Long.valueOf(model.getId());
	        CFTestStep found = findTestStep(id, testSteps);
	        if (found != null) {
	          found.setName(model.getName());
	          found.setDescription(model.getDescription());
	          found.setPosition(model.getPosition());
	          TestContext context = found.getTestContext();
	          Message message = context.getMessage();
	          if (model.getExampleMessage() != null) {
	            if (message == null) {
	              message = new Message();
	              message.setName(model.getName());
	              message.setDescription(model.getDescription());
	              message.setDomain(context.getDomain());
	              message.setScope(scope);
	              message.setAuthorUsername(auth.getName());
	              context.setMessage(message);
	            }
	            message.setContent(model.getExampleMessage());
	          }
	          
	         
	          
	          //new steps 
	          if (model.getExternalVS() != null) {
	        	  for(ValueSetDefinition vsd : model.getExternalVS()) {
	        		  if (vsd.getApiKey() != null) {
	        			  if (!vsd.getApiKey().equals("***")) {
		        			  boolean exists = false;
			        		  HL7V2TestContext tc = (HL7V2TestContext) found.getTestContext();	        		 	        		  
			        		  for(APIKey key: tc.getApikeys()) {
			        			  if (vsd.getBindingIdentifier().equals(key.getBindingIdentifier())) {
			        				  //we update
			        				  exists = true;
			        				  key.setBindingKey(vsd.getApiKey());
			        			  }
			        		  }
			        		  if(!exists) {
			        			  APIKey newKey = new APIKey(vsd.getBindingIdentifier(), vsd.getUrl(), vsd.getApiKey());
			        			  tc.getApikeys().add(newKey);
			        		  }	   
	        			  }
	        		  }	        			  	         		
	        	  }
	          }else
	          //update step and  deal with external vs and api keys
	          if (model.getApikeys() != null) {
	        	  HL7V2TestContext tc = (HL7V2TestContext) found.getTestContext();	 
	        	  for(APIKey wrapperKey : model.getApikeys()) {
	        		  for(APIKey key: tc.getApikeys()) {
	        			  if (wrapperKey.getId().equals(key.getId()) && wrapperKey.isEditBindingKey()) {
	        				  if (wrapperKey.getBindingKey() ==null || wrapperKey.getBindingKey().isEmpty()) {	
	        					  key.setBindingKey(null);
	        				  }else {
	  							key.setBindingKey(wrapperKey.getBindingKey());
							}
	        				 
	        			  }
	        		  }	        		  	        		
	        	  }
	        	  
	        		        	
	        	  
	          }
	          
	        }
	      }
	    }

	    if (wrapper.getToken() != null) {
	      // Create needed files
	      JSONObject testCaseJson = new JSONObject();
	      testCaseJson.put("name", wrapper.getTestcasename());
	      testCaseJson.put("description", wrapper.getTestcasedescription());
	      testCaseJson.put("profile", "Profile.xml");
	      testCaseJson.put("constraints", "Constraints.xml");
	      testCaseJson.put("vs", "ValueSets.xml");
	      //optional 3 new validation files
	      testCaseJson.put("coConstraints", "CoConstraints.xml");
	      testCaseJson.put("slicings", "Slicings.xml");
	      testCaseJson.put("valueSetBingings", "ValueSetBindings.xml");
	      
	      testCaseJson.put("scope", scope);
	      testCaseJson.put("domain", domain);
	      // testCaseJson.put("category", wrapper.getCategory());

	      Set<UploadedProfileModel> added = wrapper.getAdded();
	      if (added != null && !added.isEmpty()) {
	        JSONArray testStepArray = new JSONArray();
	        for (UploadedProfileModel upm : added) {
	          JSONObject ts = new JSONObject();
	          ts.put("name", upm.getName());
	          ts.put("messageId", upm.getId());
	          ts.put("description", upm.getDescription());
	          ts.put("position", upm.getPosition());
	          ts.put("scope", scope);
	          ts.put("domain", domain);
	          if (upm.getExampleMessage() != null) {
	            ts.put("exampleMessage", upm.getExampleMessage());
	          }
	          JSONArray externalVSKeysArray = new JSONArray();
	          if (upm.getExternalVS() != null) {
	        	  for ( ValueSetDefinition exvs : upm.getExternalVS()) {
		        	  JSONObject ex = new JSONObject();
		        	  ex.put("bindingIdentifier", exvs.getBindingIdentifier());
		        	  ex.put("url",exvs.getUrl());
		        	  ex.put("key",exvs.getApiKey());
		        	  externalVSKeysArray.put(ex);
		          }	 
	          }	                  
	          ts.put("externalValueSetKeys",externalVSKeysArray);
	          testStepArray.put(ts);
	        }
	        testCaseJson.put("testCases", testStepArray);
	      }

	      File jsonFile =
	          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/TestCases.json");
	      File profileFile =
	          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/Profile.xml");
	      File constraintsFile =
	          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/Constraints.xml");
	      File vsFile =
	          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/ValueSets.xml");
	      File coConstraintsFile =
		          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/CoConstraints.xml");
		  File slicingsFile =
		          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/Slicings.xml"); 
		  File vsbFile =
		          new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken() + "/ValueSetBindings.xml");
			      
	      
	      if (constraintsFile != null && constraintsFile.exists()) {
	        packagingHandler.changeConstraintId(constraintsFile);
	      }
	      if (vsFile != null && vsFile.exists()) {
	        packagingHandler.changeVsId(vsFile);
	      }
	      if (coConstraintsFile != null && coConstraintsFile.exists()) {
	        packagingHandler.changeCoConstraintsId(coConstraintsFile);
	      }
	      if (slicingsFile != null && slicingsFile.exists()) {
		        packagingHandler.changeSlicingsId(slicingsFile);
	      }
	      if (vsbFile != null && vsbFile.exists()) {
		        packagingHandler.changeVsbId(vsbFile);
	      }
	      
	      
	      InputStream targetStream = new FileInputStream(profileFile);
	      String content = IOUtils.toString(targetStream);
	      String cleanedContent = packagingHandler.removeUnusedAndDuplicateMessages(content, added);
	      FileUtils.writeStringToFile(profileFile, cleanedContent);
	      packagingHandler.changeProfileId(profileFile);
	      FileUtils.writeStringToFile(jsonFile, testCaseJson.toString());



	    }
	  }

  
  
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/testStepGroups/{testStepGroupId}", method = RequestMethod.POST)
  @ResponseBody
  public UploadStatus updateTestStepGroupProfiles(HttpServletRequest request,
      @PathVariable("testStepGroupId") Long testStepGroupId, @RequestBody TestCaseWrapper wrapper,
      Authentication auth) {
    try {
      checkManagementSupport();
      CFTestStepGroup testStepGroup = testStepGroupService.findOne(testStepGroupId);
      if (testStepGroup == null) {
        throw new Exception("Profile Group could not be found");
      }
      String username = auth.getName();
      if (!username.equals(testStepGroup.getAuthorUsername())) {
        throw new Exception("You do not have sufficient right to change this profile group");
      }

      // String username = null;
      Set<CFTestStep> testSteps = testStepGroup.getTestSteps();
      if (testSteps == null) {
        testSteps = new HashSet<CFTestStep>();
        testStepGroup.setTestSteps(testSteps);
      }

      testStepGroup.setName(wrapper.getTestcasename());
      testStepGroup.setDescription(wrapper.getTestcasedescription());
      createTestStepFiles(testStepGroup.getDomain(), testSteps, wrapper, auth);
      if (wrapper.getToken() != null) {
        // Use files to save to database
        GVTSaveInstance si = bundleHandler.createSaveInstance(
        		CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken(), testStepGroup);
        ipRepository.save(si.ip);
        csRepository.save(si.ct);
        vsRepository.save(si.vs);
        
        
        //optionals    
        if(si.slicings != null) {
        	slicingsRepository.save(si.slicings);
        }
        if(si.coct != null) {
        	//if not id generate one random
        	cocsRepository.save(si.coct);
        }
        
        if(si.vsBindings != null) {
        	//if not id generate one random
        	vsbRepository.save(si.vsBindings);
        } 
        
        si.tcg.setAuthorUsername(username);
        testStepGroupService.save((CFTestStepGroup) si.tcg);       
        
        FileUtils
            .deleteDirectory(new File(CF_RESOURCE_BUNDLE_DIR + "/" + username + "/" + wrapper.getToken()));
      } else {
        testStepGroupService.save(testStepGroup);
      }
      CFTestPlan tp = testPlanService.findCFTestPlanContainingAbstractTestCase(testStepGroup);
      tp.updateUpdateDate();
      testPlanService.save(tp);
      return new UploadStatus(ResourceUploadResult.SUCCESS, "Profile Group save successfully",
          testStepGroup.getId());

    } catch (IOException e) {
      return new UploadStatus(ResourceUploadResult.FAILURE, "IO Error could not read files",
          ExceptionUtils.getStackTrace(e));
    } catch (NoUserFoundException e) {
      return new UploadStatus(ResourceUploadResult.FAILURE, "User could not be found",
          ExceptionUtils.getStackTrace(e));
    } catch (Exception e) {
      return new UploadStatus(ResourceUploadResult.FAILURE,
          "An error occured while adding profiles", ExceptionUtils.getStackTrace(e));
    }

  }
  
  
  
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/testStepGroups/{testStepGroupId}/refreshTestContext", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> refreshTestStepGroupsTestContextModels(HttpServletRequest request,
      @PathVariable("testStepGroupId") Long testStepGroupId,
      Authentication auth) {
    
        Map<String, Object> resultMap = new HashMap<String, Object>();
      try {
		checkManagementSupport();
	
      CFTestStepGroup testStepGroup = testStepGroupService.findOne(testStepGroupId);
      if (testStepGroup == null) {
        throw new Exception("Profile Group could not be found");
      }
      String username = auth.getName();
      if (!username.equals(testStepGroup.getAuthorUsername()) && !userService.isAdmin(username)) {
        throw new Exception("You do not have sufficient right to change this profile group");
      }
        
      resourceLoader.updateCFTestStepGroupConformanceProfileJson(testStepGroup);
      testStepGroupService.save(testStepGroup);

      return resultMap;
      } catch (Exception e) {
       resultMap.put("error", e.getMessage());
       return resultMap;
  	}
  }
  
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/testPlans/{testPlanId}/refreshTestContext", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> refreshTestPlanTestContextModels(HttpServletRequest request,
      @PathVariable("testPlanId") Long testPlanId,
      Authentication auth) {
    
        Map<String, Object> resultMap = new HashMap<String, Object>();
      try {
		checkManagementSupport();
	
      CFTestPlan testPlan = testPlanService.findOne(testPlanId);
      if (testPlan == null) {
        throw new Exception("Test Plan could not be found");
      }
      String username = auth.getName();
      if (!username.equals(testPlan.getAuthorUsername()) && !userService.isAdmin(username)) {
        throw new Exception("You do not have sufficient right to change this profile group");
      }
        
      resourceLoader.updateCFTestPlanConformanceProfileJson(testPlan);
      testPlanService.save(testPlan);

      return resultMap;
      } catch (Exception e) {
       resultMap.put("error", e.getMessage());
       return resultMap;
  	}
  }
  
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/testSteps/{testStepId}/refreshTestContext", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> refreshTestStepTestContextModels(HttpServletRequest request,
      @PathVariable("testStepId") Long testStepId,
      Authentication auth) {
    
        Map<String, Object> resultMap = new HashMap<String, Object>();
      try {
		checkManagementSupport();
	
      CFTestStep testStep = testStepService.findOne(testStepId);
      if (testStep == null) {
        throw new Exception("Test Step could not be found");
      }
      String username = auth.getName();
      if (!username.equals(testStep.getAuthorUsername()) && !userService.isAdmin(username)) {
        throw new Exception("You do not have sufficient right to change this profile group");
      }
        
      resourceLoader.updateTestStepCFConformanceProfileJson(testStep);
      testStepService.save(testStep);

      return resultMap;
      } catch (Exception e) {
       resultMap.put("error", e.getMessage());
       return resultMap;
  	}
  }
  
  

  /**
   * Delete a profile from the database
   * 
   * @param request Client request
   * @param lr Profile id
   * @param p Principal
   * @return True/False as success indicator
   * @throws Exception
   */
  @PreAuthorize("hasRole('tester')")
  @RequestMapping(value = "/profiles/{profileId}/delete", method = RequestMethod.POST)
  @ResponseBody
  @Transactional(value = "transactionManager")
  public boolean deleteProfile(ServletRequest request, @PathVariable("profileId") Long profileId,
      Principal p) throws Exception {
    checkManagementSupport();

    boolean res = true;
    String userName = userIdService.getCurrentUserName(p);

    if (userName == null) {
      throw new NoUserFoundException("User could not be found");
    }

    List<CFTestPlan> list = testCaseGroupRepository.userExclusive(userName);
    boolean found = false;
    for (CFTestPlan utg : list) {
      for (Iterator<CFTestStep> iterator = utg.getTestSteps().iterator(); iterator.hasNext();) {
        CFTestStep ucf = iterator.next();
        if (ucf.getId().equals(profileId)) {
          iterator.remove();
          found = true;
        }
      }
      if (found) {
        if (utg.getTestSteps().size() == 0) {
          testCaseGroupRepository.delete(utg);
          res = true;
        } else {
          testCaseGroupRepository.save(utg);
          res = false;
        }
        break;
      }

    }

    // return true if testPlan is also deleted, false otherwise
    return res;
  }
  
  
  private String checkAndFixIdinXML(String xml,String element) {
	  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;
	try {
		builder = factory.newDocumentBuilder();	
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();    
		
		XPathExpression expr = xpath.compile("//"+element+"/@ID");
		Node attrNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
		if (attrNode != null && !attrNode.getNodeValue().isEmpty()) {
			return xml;
		}else {
			return xml;//TODO
		}
		
		
	} catch (XPathExpressionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return null;
  }

}
