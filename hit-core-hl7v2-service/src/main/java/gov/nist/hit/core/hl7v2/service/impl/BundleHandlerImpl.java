package gov.nist.hit.core.hl7v2.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.hit.core.domain.AbstractTestCase;
import gov.nist.hit.core.domain.CFTestPlan;
import gov.nist.hit.core.domain.CFTestStep;
import gov.nist.hit.core.domain.CFTestStepGroup;
import gov.nist.hit.core.domain.CoConstraints;
import gov.nist.hit.core.domain.ConformanceProfile;
import gov.nist.hit.core.domain.Constraints;
import gov.nist.hit.core.domain.GVTSaveInstance;
import gov.nist.hit.core.domain.IntegrationProfile;
import gov.nist.hit.core.domain.Message;
import gov.nist.hit.core.domain.Slicings;
import gov.nist.hit.core.domain.TestScope;
import gov.nist.hit.core.domain.TestingStage;
import gov.nist.hit.core.domain.ValueSetBindings;
import gov.nist.hit.core.domain.ValueSetDefinition;
import gov.nist.hit.core.domain.VocabularyLibrary;
import gov.nist.hit.core.hl7v2.domain.APIKey;
import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;
import gov.nist.hit.core.service.BundleHandler;
import gov.nist.hit.core.service.ResourceLoader;
import gov.nist.hit.core.service.exception.ProfileParserException;

@Service
public class BundleHandlerImpl implements BundleHandler {

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public String unzip(byte[] bytes, String path) throws Exception {
	    File tmpDir = new File(path);
	    tmpDir.mkdirs();
	    if (!tmpDir.isDirectory()) {
	        throw new Exception("Could not create TMP directory at " + tmpDir.getAbsolutePath());
	    }

	    try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
	        ZipEntry ze;
	        while ((ze = zip.getNextEntry()) != null) {
	            // Replace all whitespace (space, tab, etc.) in the entry name with underscores
	            String cleanName = ze.getName().replaceAll("\\s+", "_");

	            File outFile = new File(tmpDir, cleanName);
	            if (ze.isDirectory()) {
	                // Make directory (and any parent dirs)
	                if (!outFile.isDirectory() && !outFile.mkdirs()) {
	                    throw new IOException("Failed to create directory " + outFile.getAbsolutePath());
	                }
	            } else {
	                // Make sure parent directory exists
	                File parent = outFile.getParentFile();
	                if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
	                    throw new IOException("Failed to create directory " + parent.getAbsolutePath());
	                }
	                // Write file data
	                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile))) {
	                    byte[] buffer = new byte[1024];
	                    int len;
	                    while ((len = zip.read(buffer)) != -1) {
	                        bos.write(buffer, 0, len);
	                    }
	                }
	            }
	            zip.closeEntry();
	        }
	    }

	    return tmpDir.getAbsolutePath();
	}

	//not used
	
	@Override 
	public GVTSaveInstance createSaveInstance(String dir, String domain, String authorUsername, boolean preloaded)
			throws IOException, ProfileParserException {
		GVTSaveInstance save = new GVTSaveInstance();
		File testCasesFile = new File(dir + "/TestCases.json");
		if (!testCasesFile.exists()) {
			throw new IllegalArgumentException("No TestCases.json found");
		}

		CFTestPlan gtcg = new CFTestPlan();
		gtcg.setPersistentId(new Random().nextLong());
		String descriptorContent = FileUtils.readFileToString(testCasesFile);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode testCasesObj = mapper.readTree(descriptorContent);
		gtcg.setDomain(domain);
		gtcg.setName(testCasesObj.get("name").asText());
		gtcg.setDescription(testCasesObj.get("description").asText());
		gtcg.setPreloaded(false);
		gtcg.setScope(testCasesObj.get("scope") != null && testCasesObj.get("scope").asText() != null
				? TestScope.valueOf(testCasesObj.get("scope").asText()) : TestScope.USER);
		gtcg.setAuthorUsername(authorUsername);
		gtcg.setPreloaded(preloaded);
		save.tcg = gtcg;

		// Profile
		String profileName = testCasesObj.findValue("profile").asText();
		File profileFile = new File(dir + "/" + profileName);
		if (!profileFile.exists()) {
			throw new IllegalArgumentException("Profile " + profileName + " not found");
		}
		IntegrationProfile p = resourceLoader.integrationProfile(FileUtils.readFileToString(profileFile), domain,
				gtcg.getScope(), authorUsername, preloaded);
		save.ip = p;

		// Constraints
		String constraintName = testCasesObj.findValue("constraints").asText();
		File constraintsFile = new File(dir + "/" + constraintName);
		if (!constraintsFile.exists()) {
			throw new IllegalArgumentException("Constraints " + constraintName + " not found");
		}
		Constraints c = resourceLoader.constraint(FileUtils.readFileToString(constraintsFile), domain, gtcg.getScope(),
				authorUsername, preloaded);
		save.ct = c;

		// VS
		String vocabName = testCasesObj.findValue("vs").asText();
		File vsFile = new File(dir + "/" + vocabName);
		if (!vsFile.exists()) {
			throw new IllegalArgumentException("VocabularyLibrary " + vocabName + " not found");
		}
		VocabularyLibrary v = resourceLoader.vocabLibrary(FileUtils.readFileToString(vsFile), domain, gtcg.getScope(),
				authorUsername, preloaded);

		save.vs = v;

		Iterator<JsonNode> testCasesIter = testCasesObj.findValue("testCases").elements();
		int i = 1;
		while (testCasesIter.hasNext()) {
			JsonNode tcO = testCasesIter.next();
			CFTestStep cfti = new CFTestStep();
			cfti.setPreloaded(false);
			cfti.setScope(tcO.get("scope") != null && tcO.get("scope").asText() != null
					? TestScope.valueOf(tcO.get("scope").asText()) : TestScope.USER);
			cfti.setPosition(i++);
			String messageId = tcO.findValue("messageId").asText();
			String name = tcO.findValue("name").asText();
			String description = tcO.findValue("description").asText();
			Long id = new Random().nextLong();
			cfti.setDomain(domain);
			cfti.setScope(gtcg.getScope());
			// ---
			ConformanceProfile conformanceProfile = new ConformanceProfile();
			conformanceProfile.setJson(resourceLoader.jsonConformanceProfile(p.getXml(), messageId, c.getXml(), null));
			conformanceProfile.setDomain(domain);
			conformanceProfile.setScope(gtcg.getScope());
			conformanceProfile.setAuthorUsername(authorUsername);
			conformanceProfile.setPreloaded(preloaded);
			conformanceProfile.setSourceId(messageId);

			// ---
			HL7V2TestContext testContext = new HL7V2TestContext();
			testContext.setVocabularyLibrary(v);
			testContext.setConstraints(c);
			testContext.setConformanceProfile(conformanceProfile);
			testContext.setDqa(false);
			testContext.setStage(TestingStage.CF);
			testContext.setDomain(domain);
			testContext.setScope(gtcg.getScope());
			testContext.setAuthorUsername(authorUsername);

			// ---
			cfti.setName(name);
			cfti.setDescription(description);
			// cfti.setRoot(true);
			cfti.setTestContext(testContext);
			cfti.setPersistentId(id);
			// ---
			gtcg.getTestSteps().add(cfti);
		}

		return save;
	}

	
	private GVTSaveInstance setSaveInstanceValues(String dir, GVTSaveInstance save, Set<CFTestStep> testSteps,
			AbstractTestCase tp) throws IOException, ProfileParserException {
		File testCasesFile = new File(dir + "/TestCases.json");
		if (!testCasesFile.exists()) {
			throw new IllegalArgumentException("No TestCases.json found");
		}

		String descriptorContent = FileUtils.readFileToString(testCasesFile);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode testCasesObj = mapper.readTree(descriptorContent);

		// Profile
		String profileName = testCasesObj.findValue("profile").asText();
		File profileFile = new File(dir + "/" + profileName);
		if (!profileFile.exists()) {
			throw new IllegalArgumentException("Profile " + profileName + " not found");
		}
		IntegrationProfile p = resourceLoader.integrationProfile(FileUtils.readFileToString(profileFile),
				tp.getDomain(), tp.getScope(), tp.getAuthorUsername(), tp.isPreloaded());
		p.setDomain(tp.getDomain());
		save.ip = p;

		// Constraints
		String constraintName = testCasesObj.findValue("constraints").asText();
		File constraintsFile = new File(dir + "/" + constraintName);
		if (!constraintsFile.exists()) {
			throw new IllegalArgumentException("Constraints " + constraintName + " not found");
		}
		Constraints c = resourceLoader.constraint(FileUtils.readFileToString(constraintsFile), tp.getDomain(),
				tp.getScope(), tp.getAuthorUsername(), tp.isPreloaded());
		save.ct = c;

		// VS
		String vocabName = testCasesObj.findValue("vs").asText();
		File vsFile = new File(dir + "/" + vocabName);
		if (!vsFile.exists()) {
			throw new IllegalArgumentException("VocabularyLibrary " + vocabName + " not found");
		}
		VocabularyLibrary v = resourceLoader.vocabLibrary(FileUtils.readFileToString(vsFile), tp.getDomain(),
				tp.getScope(), tp.getAuthorUsername(), tp.isPreloaded());
		save.vs = v;
		
		// VSB
		String valueSetBingingsName = testCasesObj.findValue("valueSetBingings").asText();
		File valueSetBingingsFile = new File(dir + "/" + valueSetBingingsName);
		ValueSetBindings vsb = null;
		if (valueSetBingingsFile.exists()) {
			vsb = resourceLoader.valuesetbindings(FileUtils.readFileToString(valueSetBingingsFile), tp.getDomain(),
					tp.getScope(), tp.getAuthorUsername(), tp.isPreloaded());
			save.vsBindings = vsb;
			//			throw new IllegalArgumentException("valueSetBingings " + valueSetBingingsName + " not found");
		}
		
		
		// CoConstraints
		String coConstraintName = testCasesObj.findValue("coConstraints").asText();
		File coConstraintsFile = new File(dir + "/" + coConstraintName);
		CoConstraints cc = null;
		if (coConstraintsFile.exists()) {
			cc = resourceLoader.coConstraints(FileUtils.readFileToString(coConstraintsFile), tp.getDomain(),
					tp.getScope(), tp.getAuthorUsername(), tp.isPreloaded());
			save.coct = cc;
//			throw new IllegalArgumentException("CoConstraints " + coConstraintName + " not found");
		}
		
		
		// Slicings
		String slicingsName = testCasesObj.findValue("slicings").asText();
		File slicingsFile = new File(dir + "/" + slicingsName);
		Slicings slice = null;
		if (slicingsFile.exists()) {
			slice = resourceLoader.slicings(FileUtils.readFileToString(slicingsFile), tp.getDomain(),
					tp.getScope(), tp.getAuthorUsername(), tp.isPreloaded());
			save.slicings = slice;
//			throw new IllegalArgumentException("CoConstraints " + slicingsName + " not found");
		}
		
		

		Iterator<JsonNode> testCasesIter = testCasesObj.findValue("testCases").elements();
		int size = testSteps.size();
		while (testCasesIter.hasNext()) {
			JsonNode tcO = testCasesIter.next();
			CFTestStep cfti = new CFTestStep();
			cfti.setPreloaded(tp.isPreloaded());
			cfti.setScope(tcO.get("scope") != null && tcO.get("scope").asText() != null
					? TestScope.valueOf(tcO.get("scope").asText()) : TestScope.USER);
			String messageId = tcO.findValue("messageId").asText();
			String name = tcO.findValue("name").asText();
			String description = tcO.findValue("description").asText();
			Long id = new Random().nextLong();
			cfti.setDomain(tp.getDomain());
			cfti.setScope(tp.getScope());
			cfti.setAuthorUsername(tp.getAuthorUsername());
			cfti.setPreloaded(tp.isPreloaded());

			// ---
			ConformanceProfile conformanceProfile = new ConformanceProfile();
//			conformanceProfile.setJson(resourceLoader.jsonConformanceProfile(p.getXml(), messageId, c.getXml(), null));
//TODO make sure this enhanced works
			
			String vsXML = null, vsbXML = null,ccXML = null,sliceXML = null;
			if(v != null) {
				vsXML = v.getXml();
			}
			if (vsb != null) {
				vsbXML = vsb.getXml();
			}
			if (cc != null) {
				ccXML = cc.getXml();
			}
			if (slice != null) {
				sliceXML = slice.getXml();
			}
			conformanceProfile.setJson(resourceLoader.jsonConformanceProfileEnhanced(p.getXml(), messageId, c.getXml(), null,vsXML,vsbXML,ccXML,sliceXML));


			
			conformanceProfile.setXml(resourceLoader.getConformanceProfileContent(p.getXml(), messageId));
			conformanceProfile.setDomain(tp.getDomain());
			conformanceProfile.setScope(tp.getScope());
			conformanceProfile.setSourceId(messageId);
			conformanceProfile.setAuthorUsername(tp.getAuthorUsername());
			// ---
			HL7V2TestContext testContext = new HL7V2TestContext();
			testContext.setVocabularyLibrary(v);
			testContext.setConstraints(c);
			if(cc != null) {
				testContext.setCoConstraints(cc);
			}
			if(slice != null) {
				testContext.setSlicings(slice);
			}
			if(vsb != null) {
				testContext.setValueSetBindings(vsb);
			}
			testContext.setConformanceProfile(conformanceProfile);
			testContext.setDqa(false);
			testContext.setStage(TestingStage.CF);
			testContext.setDomain(tp.getDomain());
			testContext.setScope(tp.getScope());
			testContext.setAuthorUsername(tp.getAuthorUsername());

			Message message = testContext.getMessage();
			if (tcO.findValue("exampleMessage") != null) {
				if (message == null) {
					message = new Message();
					message.setName(name);
					message.setDomain(tp.getDomain());
					message.setScope(tp.getScope());
					message.setAuthorUsername(tp.getAuthorUsername());
					message.setPreloaded(tp.isPreloaded());
					message.setDescription(description);
					testContext.setMessage(message);
				}
				message.setContent(tcO.findValue("exampleMessage").asText());
			}
			
			Iterator<JsonNode> externalValueSetKeysIter = tcO.findValue("externalValueSetKeys").elements();
			while (externalValueSetKeysIter.hasNext()) {
				JsonNode exvs = externalValueSetKeysIter.next();
				APIKey key = new APIKey();
				if(exvs.get("bindingIdentifier") != null) {
					key.setBindingIdentifier(exvs.get("bindingIdentifier").asText(""));
				}
				if(exvs.get("url") != null) {
					key.setBindingUrl(exvs.get("url").asText(""));
				}
				if(exvs.get("key") != null) {
					key.setBindingKey(exvs.get("key").asText(""));
				}							
				testContext.getApikeys().add(key);
			}
						
			// ---
			cfti.setName(name);
			cfti.setDescription(description);
			// cfti.setRoot(true);
			cfti.setTestContext(testContext);
			cfti.setPersistentId(id);
			cfti.setPosition(size + tcO.findValue("position").asInt());
			// ---
			testSteps.add(cfti);
		}
		return save;

	}

	@Override
	@Transactional(value = "transactionManager")
	public GVTSaveInstance createSaveInstance(String dir, CFTestStepGroup tp)
			throws IOException, ProfileParserException {
		GVTSaveInstance save = new GVTSaveInstance();
		Set<CFTestStep> testSteps = tp.getTestSteps();
		if (testSteps == null) {
			testSteps = new HashSet<CFTestStep>();
			tp.setTestSteps(testSteps);
		}
		save.tcg = tp;
		setSaveInstanceValues(dir, save, tp.getTestSteps(), tp);
		save.tcg.updateUpdateDate();
		return save;
	}

	@Override
	@Transactional(value = "transactionManager")
	public GVTSaveInstance createSaveInstance(String dir, CFTestPlan tp) throws IOException, ProfileParserException {
		GVTSaveInstance save = new GVTSaveInstance();
		Set<CFTestStep> testSteps = tp.getTestSteps();
		if (testSteps == null) {
			testSteps = new HashSet<CFTestStep>();
			tp.setTestSteps(testSteps);
		}
		save.tcg = tp;
		setSaveInstanceValues(dir, save, tp.getTestSteps(), tp);
		save.tcg.updateUpdateDate();
		return save;
	}

	@Override
	public String getProfileContentFromZipDirectory(String dir) throws IOException {
		File f = findFileDirectory(dir, "Profile.xml");
		if (f != null) {
			return FileUtils.readFileToString(findFileDirectory(dir, "Profile.xml"));
		}else {
			return null;
		}	
	}

	@Override
	public String getValueSetContentFromZipDirectory(String dir) throws IOException {
		File f = findFileDirectory(dir, "ValueSets.xml");
		if (f != null) {
			return FileUtils.readFileToString(findFileDirectory(dir, "ValueSets.xml"));
		}else {
			return null;
		}	
	}

	@Override
	public String getConstraintContentFromZipDirectory(String dir) throws IOException {
		File f = findFileDirectory(dir, "Constraints.xml");
		if (f != null) {
			return FileUtils.readFileToString(findFileDirectory(dir, "Constraints.xml"));
		}else {
			return null;
		}	
	}
	
	@Override
	public String getCoConstraintContentFromZipDirectory(String dir) throws IOException {
		File f = findFileDirectory(dir, "CoConstraints.xml");
		if (f != null) {
			return FileUtils.readFileToString(findFileDirectory(dir, "CoConstraints.xml"));
		}else {
			return null;
		}	
	}

	@Override
	public String getSlicingsContentFromZipDirectory(String dir) throws IOException {
		File f = findFileDirectory(dir, "Slicings.xml");
		if (f != null) {
			return FileUtils.readFileToString(findFileDirectory(dir, "Slicings.xml"));
		}else {
			return null;
		}		
	}

	@Override
	public String getValueSetBindingsContentFromZipDirectory(String dir) throws IOException {
		File f = findFileDirectory(dir, "ValueSetBindings.xml");
		if (f != null) {
			return FileUtils.readFileToString(findFileDirectory(dir, "ValueSetBindings.xml"));
		}else {
			return null;
		}
		
		
	}
	

	// finds file in dir and sub-dir
	private File findFileDirectory(String dir, String fileName) {
		Collection<File> files = FileUtils.listFiles(new File(dir), null, true);
		for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			File file = iterator.next();
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}
	
	public Set<File> findFiles(String dir,String fileName){
		Set<File> resfiles = new HashSet<File>();	
		
		Collection<File> files = FileUtils.listFiles(new File(dir), null, true);
		for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			File file = iterator.next();
			if (file.getName().equals(fileName)) {
				resfiles.add(file);
			}
		}
		return resfiles;
	}



}
