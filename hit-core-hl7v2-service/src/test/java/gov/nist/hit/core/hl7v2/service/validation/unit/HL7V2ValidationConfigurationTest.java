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


package gov.nist.hit.core.hl7v2.service.validation.unit;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassificationEnum;
import gov.nist.hit.core.hl7v2.domain.HL7V2ValidationClassifications;
import gov.nist.hit.core.service.exception.MessageParserException;


public class HL7V2ValidationConfigurationTest {

	@Test
	public void testFileParsesing() throws MessageParserException {
		HL7V2ValidationClassifications classification = new HL7V2ValidationClassifications();

//		 try {
//			System.out.println(getResourceFileAsString("reference.conf"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String file = "";
		Config reportConfig,categoryConfig,classificationConfig;
		try {
			file = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("validationConfigurationTemplateTest.conf"),"UTF-8");
//			reportConfig = ConfigFactory.parseString(file).getConfig("report");
//			categoryConfig = ConfigFactory.parseString(file).getConfig("report.category");
//			classificationConfig = ConfigFactory.parseString(file).getConfig("report.classification");
			
			StringReader sr = new StringReader(file);			
			reportConfig = ConfigFactory.parseReader(sr).resolve();
			
//			Config codedElementConfig = ConfigFactory.load("reference.conf").getConfig("context-based") ;
			System.out.println(reportConfig.hasPath("report.binding-location"));
//			System.out.println(config.toString());
			assertNotNull(reportConfig);
			
			if (reportConfig.hasPath("binding-location.classification")) {
				classification.getBindingLocation().setClassfication(HL7V2ValidationClassificationEnum.valueOf(reportConfig.getString("binding-location.classification").toUpperCase()));
			}

					
			Config newReportConfig = reportConfig.withValue("length-spec-error-no-valid.classification", ConfigValueFactory.fromAnyRef("CACACACAA"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		
//		System.out.println(newReportConfig.root().render(ConfigRenderOptions.defaults().setFormatted(true).setOriginComments(true).setJson(false)));
//		System.out.println(codedElementConfig.root().render(ConfigRenderOptions.defaults().setFormatted(true).setOriginComments(true).setJson(false)));
		
//		String file = "";
//		try {
//			file = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("validationConfigurationTemplate.conf"),"UTF-8");
//			
//			file =file.replace("@{length-spec-error-no-valid.classification}", "coucou");
//			
//			System.out.println(file);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		
		
		
//		Config config2 = ConfigFactory.parseString("report {length {\n" + 
//				"    template = \"The length of %s must be within the range [%s, %s]. Value = '%s'\"\n" + 
//				"    category = ${report.category.length}\n" + 
//				"    classification = ${report.classification.error}\n" + 
//				"  } }").getConfig("report");
//		
//		if (( config2.hasPath("length-spec-error-no-valid")) ) {
//			classificationConfig = config2.getConfig("length-spec-error-no-valid");
//			System.out.println(classificationConfig.getString("classification"));
//			classification.setLengthSpecErrorNoValid(HL7V2ValidationClassification.valueOf(classificationConfig.getString("classification").toUpperCase()));
//		}
//		assertNotNull(config2);
		
	}
	

	/**
	 * Reads given resource file as a string.
	 *
	 * @param fileName path to the resource file
	 * @return the file's contents
	 * @throws IOException if read fails for any reason
	 */
	private String getResourceFileAsString(String fileName) throws IOException {
	    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	    try (InputStream is = classLoader.getResourceAsStream(fileName)) {
	        if (is == null) return null;
	        try (InputStreamReader isr = new InputStreamReader(is);
	             BufferedReader reader = new BufferedReader(isr)) {
	            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
	        }
	    }
	}

}
