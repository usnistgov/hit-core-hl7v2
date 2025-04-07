package gov.nist.hit.core.hl7v2.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.resources.domain.XMLError;
import gov.nist.healthcare.resources.xds.XMLResourcesValidator;
import gov.nist.hit.core.hl7v2.service.FileValidationHandler;
import gov.nist.hit.core.service.ResourceLoader;
import gov.nist.hit.hl7.profile.validation.domain.ProfileValidationReport;
import gov.nist.hit.hl7.profile.validation.service.impl.ValidationServiceImpl;

@Service
public class FileValidationHandlerImpl implements FileValidationHandler {

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public List<XMLError> validateProfile(String contentTxt, InputStream contentIS) throws Exception {
		// Check if not a Constraint file or Value Set file
		List<XMLError> errors = new ArrayList<XMLError>();
		if (contentTxt.contains("<ConformanceContext")) {
			XMLError error = new XMLError(0, 0, "File is a Constraint file.");
			errors.add(error);
		} else if (contentTxt.contains("<ValueSetLibrary")) {
			XMLError error = new XMLError(0, 0, "File is a Value Set file.");
			errors.add(error);
		}

		return errors;

	}

	@Override
	public List<XMLError> validateConstraints(String contentTxt, InputStream contentIS) throws Exception {
		// Check if not a Profile file or Value Set file
		List<XMLError> errors = new ArrayList<XMLError>();
		if (contentTxt.contains("<ConformanceProfile")) {
			XMLError error = new XMLError(0, 0, "File is a Profile file.");
			errors.add(error);
		} else if (contentTxt.contains("<ValueSetLibrary")) {
			XMLError error = new XMLError(0, 0, "File is a Value Set file.");
			errors.add(error);
		}
		return errors;
	}

	@Override
	public List<XMLError> validateVocabulary(String contentTxt, InputStream contentIS) throws Exception {
		// Check if not a Profile file or Constraint file
		List<XMLError> errors = new ArrayList<XMLError>();
		if (contentTxt.contains("<ConformanceProfile")) {
			XMLError error = new XMLError(0, 0, "File is a Profile file.");
			errors.add(error);
		} else if (contentTxt.contains("<ConformanceContext")) {
			XMLError error = new XMLError(0, 0, "File is a Constraint file.");
			errors.add(error);
		}
		return errors;
	}

	@Override
	public Map<String, List<XMLError>> unbundleAndValidate(String dir) throws Exception {
		XMLResourcesValidator v = XMLResourcesValidator.createValidatorFromClasspath("/xsd");
		Map<String, List<XMLError>> errorsMap = new HashMap<String, List<XMLError>>();
		String rootPath = findFileDirectory(dir, "Profile.xml", false) + "/";

		// Profile
		Resource profile = resourceLoader.getResource("Profile.xml", rootPath);
		errorsMap.put("profileErrors", v.validateProfile(profile.getInputStream()));

		// Constraints
		Resource constraints = resourceLoader.getResource("Constraints.xml", rootPath);
		errorsMap.put("constraintsErrors", v.validateConstraints(constraints.getInputStream()));

		// VS
		Resource vs = resourceLoader.getResource("ValueSets.xml", rootPath);
		errorsMap.put("vsErrors", v.validateVocabulary(vs.getInputStream()));

		return errorsMap;
	}

	@Override
	public ProfileValidationReport getHTMLValidatioReport(String dir) throws Exception {
		ValidationServiceImpl vsi = new ValidationServiceImpl();

		String rootPath = findFileDirectory(dir, "Profile.xml", false) + "/";
		List<String> fileTypeErrors = new ArrayList<String>();

		// Profile
		Resource profile = resourceLoader.getResource("Profile.xml", rootPath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.commons.io.IOUtils.copy(profile.getInputStream(), baos);
		byte[] bytes = baos.toByteArray();
		String content = IOUtils.toString(new ByteArrayInputStream(bytes));
		if (content.contains("<ConformanceContext")) {
			fileTypeErrors.add("Profile file provided is a Constraint file.");
		} else if (content.contains("<ValueSetLibrary")) {
			fileTypeErrors.add("Profile file provided is a Value Set file.");
		} else if (content.contains("<CoConstraintContext")) {
			fileTypeErrors.add("Profile file provided is a CoConstraintContext file.");
		} else if (content.contains("<ProfileSlicing")) {
			fileTypeErrors.add("Profile file provided is a Slicings file.");
		} else if (content.contains("<ValueSetBindingsContext")) {
			fileTypeErrors.add("Profile file provided is a ValueSetBindingsContext file.");
		}

		// Constraints
		Resource constraints = resourceLoader.getResource("Constraints.xml", rootPath);
		baos = new ByteArrayOutputStream();
		org.apache.commons.io.IOUtils.copy(constraints.getInputStream(), baos);
		bytes = baos.toByteArray();
		content = IOUtils.toString(new ByteArrayInputStream(bytes));
		if (content.contains("<ConformanceProfile")) {
			fileTypeErrors.add("Constraints file provided is a Profile file.");
		} else if (content.contains("<ValueSetLibrary")) {
			fileTypeErrors.add("Constraints file provided is a Value Set file.");
		} else if (content.contains("<CoConstraintContext")) {
			fileTypeErrors.add("Constraints file provided is a CoConstraintContext file.");
		} else if (content.contains("<ProfileSlicing")) {
			fileTypeErrors.add("Contraints file provided is a Slicings file.");
		} else if (content.contains("<ValueSetBindingsContext")) {
			fileTypeErrors.add("Contraints file provided is a ValueSetBindingsContext file.");
		}

		// VS
		Resource vs = resourceLoader.getResource("ValueSets.xml", rootPath);
		baos = new ByteArrayOutputStream();
		org.apache.commons.io.IOUtils.copy(vs.getInputStream(), baos);
		bytes = baos.toByteArray();
		content = IOUtils.toString(new ByteArrayInputStream(bytes));
		if (content.contains("<ConformanceProfile")) {
			fileTypeErrors.add("Value Set file provided is a Profile file.");
		} else if (content.contains("<ConformanceContext")) {
			fileTypeErrors.add("Value Set file provided is a Constraint file.");
		} else if (content.contains("<CoConstraintContext")) {
			fileTypeErrors.add("Value Set file provided is a CoConstraintContext file.");
		} else if (content.contains("<ProfileSlicing")) {
			fileTypeErrors.add("Value Set file provided is a Slicings file.");
		} else if (content.contains("<ValueSetBindingsContext")) {
			fileTypeErrors.add("Value Set file provided is a ValueSetBindingsContext file.");
		}

		// CoConstraints
		Resource coConstraints = resourceLoader.getResource("CoConstraints.xml", rootPath);
		if (coConstraints != null) {
			baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(coConstraints.getInputStream(), baos);
			bytes = baos.toByteArray();
			content = IOUtils.toString(new ByteArrayInputStream(bytes));
			if (content.contains("<ConformanceProfile")) {
				fileTypeErrors.add("CoConstraints file provided is a Profile file.");
			} else if (content.contains("<ConformanceContext")) {
				fileTypeErrors.add("CoConstraints file provided is a Constraint file.");
			} else if (content.contains("<ValueSetLibrary")) {
				fileTypeErrors.add("CoConstraints file provided is a Value Set file.");
			} else if (content.contains("<ProfileSlicing")) {
				fileTypeErrors.add("CoConstraints file provided is a Slicings file.");
			} else if (content.contains("<ValueSetBindingsContext")) {
				fileTypeErrors.add("CoConstraints file provided is a ValueSetBindingsContext file.");
			}

		}

		// Slicings
		Resource slicings = resourceLoader.getResource("Slicings.xml", rootPath);
		if (slicings != null) {
			baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(slicings.getInputStream(), baos);
			bytes = baos.toByteArray();
			content = IOUtils.toString(new ByteArrayInputStream(bytes));
			if (content.contains("<ConformanceProfile")) {
				fileTypeErrors.add("Slicing file provided is a Profile file.");
			} else if (content.contains("<ConformanceContext")) {
				fileTypeErrors.add("Slicing file provided is a Constraint file.");
			} else if (content.contains("<ValueSetLibrary")) {
				fileTypeErrors.add("Slicing file provided is a Value Set file.");
			} else if (content.contains("<CoConstraintContext")) {
				fileTypeErrors.add("Slicing file provided is a CoConstraintContext file.");
			} else if (content.contains("<ValueSetBindingsContext")) {
				fileTypeErrors.add("Slicing file provided is a ValueSetBindingsContext file.");
			}
		}

		// ValueSetBindings
		Resource valueSetBindings = resourceLoader.getResource("ValueSetBindings.xml", rootPath);
		if (valueSetBindings != null) {
			baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(valueSetBindings.getInputStream(), baos);
			bytes = baos.toByteArray();
			content = IOUtils.toString(new ByteArrayInputStream(bytes));
			if (content.contains("<ConformanceProfile")) {
				fileTypeErrors.add("Value Set Bindings file provided is a Profile file.");
			} else if (content.contains("<ConformanceContext")) {
				fileTypeErrors.add("Value Set Bindings file provided is a Constraint file.");
			} else if (content.contains("<ValueSetLibrary")) {
				fileTypeErrors.add("Value Set Bindings file provided is a Value Set file.");
			} else if (content.contains("<CoConstraintContext")) {
				fileTypeErrors.add("Value Set Bindings file provided is a CoConstraintContext file.");
			} else if (content.contains("<ProfileSlicing")) {
				fileTypeErrors.add("Value Set Bindings file provided is a Slicings file.");
			}
		}

		if (fileTypeErrors.size() > 0) {
			throw new InvalidFileTypeException(fileTypeErrors);
		}

		ProfileValidationReport report = null;
		if (profile != null && profile.exists() && constraints != null && constraints.exists() && vs != null && vs.exists()) {
			InputStream coConstraintsIS = (coConstraints == null) ? null : coConstraints.getInputStream();
			InputStream slicingsIS = (slicings == null) ? null : slicings.getInputStream();
			InputStream valueSetBindingsIS = (valueSetBindings == null) ? null : valueSetBindings.getInputStream();
			report = vsi.validationXMLs(profile.getInputStream(), constraints.getInputStream(), vs.getInputStream(), coConstraintsIS, slicingsIS,
					valueSetBindingsIS);

		}

		return report;
	}

	@Override
	public List<ProfileValidationReport> getHTMLValidatioReportForContextBased(String dir) throws Exception {		
		ValidationServiceImpl vsi = new ValidationServiceImpl();
		List<ProfileValidationReport> reports = new ArrayList<ProfileValidationReport>();
		
		// Profiles
        List<String> profiles = getFileContent(dir+"/Global/Profiles","xml");
		//map all message id with the file
        Map<String, String> profileMap = new HashMap<String, String>();
        for(String profileString : profiles) {
        	for(String messageId : extractAttributeFromElement("Message","ID",profileString)) {
        		profileMap.put(messageId,profileString);
        	}        	
        }
        
		// Constraints
        List<String> constraints = getFileContent(dir+"/Global/Constraints","xml");
        //map all ConformanceContext id with the file
        Map<String, String> constraintsMap = new HashMap<String, String>();
        for(String constraintString : constraints) {
        	for(String confcontextId : extractAttributeFromElement("ConformanceContext","ID",constraintString)) {
        		constraintsMap.put(confcontextId,constraintString);
        	} 
        }
        
		// VS
        List<String> valuesets = getFileContent(dir+"/Global/Tables","xml");
        //map all ValueSetLibrary id with the file
        Map<String, String> vsMap = new HashMap<String, String>();
        for(String valuesetString : valuesets) {
        	for(String vsId : extractAttributeFromElement("ValueSetLibrary","ID",valuesetString)) {
        		vsMap.put(vsId,valuesetString);
        	} 
//        	vsMap.put(extractAttributeFromElement("ValueSetLibrary","ID",valuesetString),valuesetString);
        }
                
		// CoConstraints
        List<String> coConstraints = getFileContent(dir+"/Global/CoConstraints","xml");
        //map all CoConstraintContext id with the file
        Map<String, String> coConstraintsMap = new HashMap<String, String>();
        for(String coConstraint : coConstraints) {
        	for(String coConstId : extractAttributeFromElement("CoConstraintContext","ID",coConstraint)) {
        		coConstraintsMap.put(coConstId,coConstraint);
        	} 
//        	coConstraintsMap.put(extractAttributeFromElement("CoConstraintContext","ID",coConstraint),coConstraint);
        }
        
		// Slicings
        List<String> slicings = getFileContent(dir+"/Global/Slicings","xml");
        //map all ProfileSlicing id with the file
        Map<String, String> slicingsMap = new HashMap<String, String>();
        for(String slicing : slicings) {
        	for(String slicingId : extractAttributeFromElement("ProfileSlicing","ID",slicing)) {
        		slicingsMap.put(slicingId,slicing);
        	}
//        	slicingsMap.put(extractAttributeFromElement("ProfileSlicing","ID",slicing),slicing);
        }
        
		// valueSetBindings
        List<String> valuesetBindings = getFileContent(dir+"/Global/Bindings","xml");
        //map all ValueSetBindingsContext id with the file
        Map<String, String> valuesetBindingsMap = new HashMap<String, String>();
        for(String valuesetBinding : valuesetBindings) {
        	for(String valuesetBindingId : extractAttributeFromElement("ValueSetBindingsContext","ID",valuesetBinding)) {
        		valuesetBindingsMap.put(valuesetBindingId,valuesetBinding);
        	}
//        	valuesetBindingsMap.put(extractAttributeFromElement("ValueSetBindingsContext","ID",valuesetBinding),valuesetBinding);
        }
		
        List<File> testSteps = findTestStepJsonFiles(dir+"/ContextBased");
        
		for (File testStep : testSteps) {
			// load a local constraint maybe
			// go through all of them. Read and find the depdencies
			// make a list of all of them
			ProfileValidationReport report = null;

			
			String jsonString = new String(Files.readAllBytes(Paths.get(testStep.getAbsolutePath())));
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonString);
			
			String testName = rootNode.has("name") ? rootNode.get("name").asText() : null;		
			// Access the 'hl7v2' node
			JsonNode hl7v2Node = rootNode.get("hl7v2");

		
			if (hl7v2Node != null) {
				
				String messageId = hl7v2Node.has("messageId") ? hl7v2Node.get("messageId").asText() : null;									
				String constraintId = hl7v2Node.has("constraintId") ? hl7v2Node.get("constraintId").asText() : null;
				String valueSetLibraryId = hl7v2Node.has("valueSetLibraryId") ? hl7v2Node.get("valueSetLibraryId").asText() : null;
			
				//TODO: check for alternative names... but should be good currently  bindingId -> valueSetBindingsId  slicingId ->  slicingsId
				String bindingId = hl7v2Node.has("bindingId") ? hl7v2Node.get("bindingId").asText() : null;
				String coConstraintsId = hl7v2Node.has("coConstraintsId") ? hl7v2Node.get("coConstraintsId").asText() : null;
				String slicingId = hl7v2Node.has("slicingId") ?  hl7v2Node.get("slicingId").asText() : null;		
				
				
				//files
				String profile = profileMap.get(messageId);
				
				List<String> constraintList = new ArrayList<String>();
				//get local constraint file
				if (new File(testStep.getParentFile().getAbsolutePath()+ "/Constraints.xml").exists()){
					String localConstraints = new String(Files.readAllBytes(Paths.get(testStep.getParentFile().getAbsolutePath()+ "/Constraints.xml")));
					if (localConstraints != null ) constraintList.add(localConstraints);
				}
				String globalConstraint = constraintsMap.get(constraintId);				
				if (globalConstraint != null ) constraintList.add(globalConstraint);
				
				String vs = vsMap.get(valueSetLibraryId);
				String coConstraint = coConstraintsMap.get(coConstraintsId);
				String slicing = slicingsMap.get(slicingId);
				String vsb = valuesetBindingsMap.get(bindingId);
				
				
	        	if (profile != null && constraintList != null && !constraintList.isEmpty() && vs != null) {			
	    			report = vsi.validationXMLs(profile,constraintList,vs,coConstraint,slicing,vsb);
	    			if(testName != null) {
		    			report.setTestName(testName);
	    			}
	    		}
				reports.add(report);
				
			}


		}

//		if (profile != null && profile.exists() && constraints != null && constraints.exists() && vs != null &&  vs.exists()) {			
//			report = vsi.validationXMLs(profileXML,contraintList,vsXML,coConstraintsXML,slicingsXML,valueSetBindingsXML);
//		}

		return reports;
	}

	// finds folder where file is found (first occurence)
	private String findFileDirectory(String dir, String fileName, boolean reg) {
		Collection files = FileUtils.listFiles(new File(dir), null, true);
		for (Iterator iterator = files.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			if (reg) {
				if (file.getName().matches(fileName)) {
					return file.getParentFile().getAbsolutePath();
				}
			} else {
				if (file.getName().equals(fileName)) {
					return file.getParentFile().getAbsolutePath();
				}
			}

		}
		return null;
	}

	public class InvalidFileTypeException extends Exception {

		private List<String> errors = new ArrayList<String>();

		public InvalidFileTypeException(List<String> errors_) {
			super();
			errors = errors_;
		}

		public List<String> getErrors() {
			return errors;
		}

		public void setErrors(List<String> errors) {
			this.errors = errors;
		}

	}

	private List<String> getFileContent(String dir, String fileType) {
		List<String> xmls = new ArrayList<String>();
		File directory = new File(dir);
		File[] files = directory.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().toLowerCase().endsWith("." + fileType)) {
					try {
						String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
						xmls.add(content);
					} catch (IOException e) {
						System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
					}
				}
			}
		} else {
			System.err.println("Error: Could not list files in directory '" + dir + "'.");
		}
		return xmls;
	}

	private List<File> findTestStepJsonFiles(String dir) {
		File directory = new File(dir);
		List<File> files = new ArrayList<File>();
		findTestStepJsonFilesRecursive(directory, files);
		return files;
	}

	private void findTestStepJsonFilesRecursive(File directory, List<File> files) {
		File[] dirFiles = directory.listFiles();
		if (dirFiles != null) {
			for (File file : dirFiles) {
				if (file.isDirectory()) {
					findTestStepJsonFilesRecursive(file, files);
				} else if (file.getName().equals("TestStep.json")) {
					files.add(file);
				}
			}
		}
	}

	public List<String> extractAttributeFromElement(String element, String attribute, String xmlString) {
		List<String> ids = new ArrayList<>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setXIncludeAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(xmlString));
			Document document = builder.parse(inputSource);

			// Get all elements with the tag name "Message"
			NodeList messageList = document.getElementsByTagName(element);

			// Iterate through the Message elements
			for (int i = 0; i < messageList.getLength(); i++) {
				Element messageElement = (Element) messageList.item(i);

				// Get the value of the "ID" attribute
				String id = messageElement.getAttribute(attribute);
				if (!id.isEmpty()) {
					ids.add(id);
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Error parsing XML: " + e.getMessage());
			e.printStackTrace();
		}

		return ids;
	}

}
