package gov.nist.hit.core.hl7v2.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nist.hit.core.domain.ContentDefinitionType;
import gov.nist.hit.core.domain.ExtensibilityType;
import gov.nist.hit.core.domain.ProfileModel;
import gov.nist.hit.core.domain.StabilityType;
import gov.nist.hit.core.domain.UsageType;
import gov.nist.hit.core.domain.ValueSetDefinition;
import gov.nist.hit.core.domain.ValueSetDefinitions;
import gov.nist.hit.core.domain.ValueSetElement;
import gov.nist.hit.core.domain.valuesetbindings.Binding;
import gov.nist.hit.core.domain.valuesetbindings.ValueSetBinding;
import gov.nist.hit.core.hl7v2.domain.UploadedProfileModel;
import gov.nist.hit.core.hl7v2.service.HL7V2ProfileParser;
import gov.nist.hit.core.hl7v2.service.PackagingHandler;
import gov.nist.hit.core.service.CachedRepository;
import gov.nist.hit.core.service.exception.ProfileParserException;
import gov.nist.hit.core.service.util.FileUtil;

@Service
public class PackagingHandlerImpl implements PackagingHandler {

	@Autowired
	protected CachedRepository cachedRepository;
	

	@Override
	public List<UploadedProfileModel> getUploadedProfiles(String profileXML, String valueSetXML, String valueSetBindingsXML, String coConstraintsXML) {
		
		List<ValueSetDefinition> listOfExternalVSD = new ArrayList<ValueSetDefinition>();
		if (valueSetXML != null) {
			listOfExternalVSD = this.getExternalValueSets(valueSetXML);

		}
		
		Document profileDoc = this.toDoc(profileXML);
		NodeList nodes = profileDoc.getElementsByTagName("Message");
		List<UploadedProfileModel> list = new ArrayList<UploadedProfileModel>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element elmIntegrationProfile = (Element) nodes.item(i);
			UploadedProfileModel upm = new UploadedProfileModel();
			upm.setActivated(false);
			upm.setId(elmIntegrationProfile.getAttribute("ID"));
			upm.setName(elmIntegrationProfile.getAttribute("Name"));
			upm.setType(elmIntegrationProfile.getAttribute("Type"));
			upm.setEvent(elmIntegrationProfile.getAttribute("Event"));
			upm.setStructID(elmIntegrationProfile.getAttribute("StructID"));
			upm.setIdentifier(elmIntegrationProfile.getAttribute("Identifier"));
			upm.setDescription(elmIntegrationProfile.getAttribute("Description"));
			
			
			//add external from valueSetBinding and coConstraints file
			if (valueSetXML != null && valueSetBindingsXML != null) {
				HL7V2ProfileParser profileParser = new HL7V2ProfileParserImpl();
				ProfileModel profileModel;
				try {
					profileModel = profileParser.parseEnhanced(profileXML, elmIntegrationProfile.getAttribute("ID"), null,
							null,valueSetXML,valueSetBindingsXML, coConstraintsXML, null);
					
					List<ValueSetDefinition> externalVS = new ArrayList<ValueSetDefinition>();
					for (ValueSetBinding vsb  : profileModel.getValueSetBinding()) {						
						for (Binding binding  : vsb.getBindingList()) {
							Optional<ValueSetDefinition> matchedObject = listOfExternalVSD.stream()
									  .filter(item -> item.getBindingIdentifier().equals(binding.getBindingIdentifier()))
									  .findFirst();
							if(matchedObject.isPresent()) {
								externalVS.add(matchedObject.get());
							}				
						}
					}
					
					for (ValueSetBinding vsb  : profileModel.findValueSetBindingsFromCoConstraints()) {						
						for (Binding binding  : vsb.getBindingList()) {
							Optional<ValueSetDefinition> matchedObject = listOfExternalVSD.stream()
									  .filter(item -> item.getBindingIdentifier().equals(binding.getBindingIdentifier()))
									  .findFirst();
							if(matchedObject.isPresent()) {
								externalVS.add(matchedObject.get());
							}				
						}
					}
					
					upm.setExternalValueSets(externalVS);
					
				} catch (ProfileParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
						
			if (cachedRepository.getCachedProfiles().containsKey(elmIntegrationProfile.getAttribute("ID"))) {
				// remove them from cache or it will trigger an error.
				cachedRepository.getCachedProfiles().remove(elmIntegrationProfile.getAttribute("ID"));
				upm.setUsed(false);
			} else {
				upm.setUsed(false);
			}
			list.add(upm);
		}
		
		
		return list;
	}
	
	
	public List<ValueSetDefinition> getExternalValueSets(String xml){
		Document valueSetDoc = this.toDoc(xml);
		List<ValueSetDefinition> listOfExternalVSD = new ArrayList<ValueSetDefinition>();
		//legacy location of ExternalValueSetDefinitions
		NodeList extValSetDefNodes = valueSetDoc.getElementsByTagName("ExternalValueSetDefinitions");
		for (int i = 0; i < extValSetDefNodes.getLength(); i++) {
			
			Element elmIntegrationProfile = (Element) extValSetDefNodes.item(i);
			NodeList valSetDefNodes = elmIntegrationProfile.getElementsByTagName("ValueSetDefinition");
			for (int j = 0; j < valSetDefNodes.getLength(); j++) {
				Element elmTable = (Element) valSetDefNodes.item(j);
				ValueSetDefinition externalVSD = new ValueSetDefinition(true);
		          externalVSD.setBindingIdentifier(elmTable.getAttribute("BindingIdentifier"));
		          externalVSD.setName(elmTable.getAttribute("Name"));

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("NoCodeDisplayText"))) {
		        	  externalVSD.setNoCodeDisplayText(elmTable.getAttribute("NoCodeDisplayText"));
		          }

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Description"))) {
		        	  externalVSD.setDescription(elmTable.getAttribute("Description"));
		          }

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("URL"))) {
		        	  externalVSD.setUrl(elmTable.getAttribute("URL"));
		          }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Extensibility"))) {
		        	  externalVSD.setExtensibility(ExtensibilityType.fromValue(elmTable
		                .getAttribute("Extensibility")));
		          }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Version"))) {
		        	  externalVSD.setVersion(elmTable.getAttribute("Version"));
		          }

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Stability"))) {
		        	  externalVSD.setStability(StabilityType.fromValue(elmTable.getAttribute("Stability")));
		          }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Oid"))) {
		        	  externalVSD.setOid(elmTable.getAttribute("Oid"));
		            }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("ContentDefinition"))) {
		        	  externalVSD.setContentDefinition(ContentDefinitionType.fromValue(elmTable
		                  .getAttribute("ContentDefinition")));
		           }			
				listOfExternalVSD.add(externalVSD);
			}
		}
		
		
		NodeList valueSetDefinitionsElements =
				valueSetDoc.getElementsByTagName("ValueSetDefinitions");

		    if (valueSetDefinitionsElements != null && valueSetDefinitionsElements.getLength() > 0) {
		      for (int k = 0; k < valueSetDefinitionsElements.getLength(); k++) {
		        Element valueSetDefinitionsElement = (Element) valueSetDefinitionsElements.item(k);		        
		        		       		        
		        //ExternalValueSetDefinition 
		        NodeList externalValueSetDefinitionNodes = valueSetDefinitionsElement.getElementsByTagName("ExternalValueSetDefinition");
		        for (int i = 0; i < externalValueSetDefinitionNodes.getLength(); i++) {
		          Element elmTable = (Element) externalValueSetDefinitionNodes.item(i);
		          ValueSetDefinition externalVSD = new ValueSetDefinition(true);
		          externalVSD.setBindingIdentifier(elmTable.getAttribute("BindingIdentifier"));
		          externalVSD.setName(elmTable.getAttribute("Name"));

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("NoCodeDisplayText"))) {
		        	  externalVSD.setNoCodeDisplayText(elmTable.getAttribute("NoCodeDisplayText"));
		          }

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Description"))) {
		        	  externalVSD.setDescription(elmTable.getAttribute("Description"));
		          }

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("URL"))) {
		        	  externalVSD.setUrl(elmTable.getAttribute("URL"));
		          }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Extensibility"))) {
		        	  externalVSD.setExtensibility(ExtensibilityType.fromValue(elmTable
		                .getAttribute("Extensibility")));
		          }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Version"))) {
		        	  externalVSD.setVersion(elmTable.getAttribute("Version"));
		          }

		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Stability"))) {
		        	  externalVSD.setStability(StabilityType.fromValue(elmTable.getAttribute("Stability")));
		          }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("Oid"))) {
		        	  externalVSD.setOid(elmTable.getAttribute("Oid"));
		            }
		          if (StringUtils.isNotEmpty(elmTable.getAttribute("ContentDefinition"))) {
		        	  externalVSD.setContentDefinition(ContentDefinitionType.fromValue(elmTable
		                  .getAttribute("ContentDefinition")));
		           }

		          listOfExternalVSD.add(externalVSD);
		        }
		        
		        
		      }
		    }
		
		
		
		return listOfExternalVSD;
	}
	
	
	
	
	

	@Override
	public String removeUnusedAndDuplicateMessages(String content, Set<UploadedProfileModel> presentMessages) {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("ConformanceProfile").item(0);
		// make ConformanceProfile id unique
		profileElement.setAttribute("ID", profileElement.getAttribute("ID") + Instant.now().toEpochMilli());
		Element conformanceProfilElementRoot = (Element) profileElement.getElementsByTagName("Messages").item(0);
		NodeList messages = conformanceProfilElementRoot.getElementsByTagName("Message");

		for (int j = messages.getLength() - 1; j >= 0; j--) {
			Element elmCode = (Element) messages.item(j);
			String id = elmCode.getAttribute("ID");

			boolean found = false;

			if (cachedRepository.getCachedProfiles().containsKey(id)) {

			} else {
				for (UploadedProfileModel upm : presentMessages) {
					if (upm.getId().equals(id)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				conformanceProfilElementRoot.removeChild(elmCode);
			}
		}

		return toString(doc);
	}

	@Override
	public File zip(List<File> files, String filename) throws Exception {
		File zipfile = new File(filename);
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		// create the ZIP file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
		// compress the files
		for (int i = 0; i < files.size(); i++) {
			FileInputStream in = new FileInputStream(files.get(i));
			// add ZIP entry to output stream
			out.putNextEntry(new ZipEntry(files.get(i).getName()));
			// transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			// complete the entry
			out.closeEntry();
			in.close();
		}
		// complete the ZIP file
		out.close();
		return zipfile;
	}

	@Override
	public Document toDoc(String xmlSource) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setXIncludeAware(false);
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlSource)));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public File changeProfileId(File file) throws Exception {
		InputStream targetStream = new FileInputStream(file);
		String content = changeProfileId(IOUtils.toString(targetStream));
		FileUtils.writeStringToFile(file, content);
		return file;
	}

	@Override
	public String changeProfileId(String content) throws Exception {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("ConformanceProfile").item(0);
		profileElement.setAttribute("ID", profileElement.getAttribute("ID") + Instant.now().toEpochMilli());
		return toString(doc);
	}

	@Override
	public File changeConstraintId(File file) throws Exception {
		InputStream targetStream = new FileInputStream(file);
		String content = changeConstraintId(IOUtils.toString(targetStream));
		FileUtils.writeStringToFile(file, content);
		return file;
	}

	@Override
	public String changeConstraintId(String content) throws Exception {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("ConformanceContext").item(0);
		profileElement.setAttribute("UUID", profileElement.getAttribute("UUID") + Instant.now().toEpochMilli());
		return toString(doc);
	}

	@Override
	public File changeVsId(File file) throws Exception {
		InputStream targetStream = new FileInputStream(file);
		String content = changeVsId(IOUtils.toString(targetStream));
		FileUtils.writeStringToFile(file, content);
		return file;
	}

	@Override
	public String changeVsId(String content) throws Exception {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("ValueSetLibrary").item(0);
		profileElement.setAttribute("ValueSetLibraryIdentifier",
				profileElement.getAttribute("ValueSetLibraryIdentifier") + Instant.now().toEpochMilli());
		return toString(doc);
	}
	
	@Override
	public File changeCoConstraintsId(File file) throws Exception {
		InputStream targetStream = new FileInputStream(file);
		String content = changeCoConstraintsId(IOUtils.toString(targetStream));
		FileUtils.writeStringToFile(file, content);
		return file;
	}

	@Override
	public String changeCoConstraintsId(String content) throws Exception {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("CoConstraintContext").item(0);
		profileElement.setAttribute("ID", profileElement.getAttribute("ID") + Instant.now().toEpochMilli());
		return toString(doc);
	}
	
	@Override
	public File changeSlicingsId(File file) throws Exception {
		InputStream targetStream = new FileInputStream(file);
		String content = changeSlicingsId(IOUtils.toString(targetStream));
		FileUtils.writeStringToFile(file, content);
		return file;
	}

	@Override
	public String changeSlicingsId(String content) throws Exception {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("ProfileSlicing").item(0);
		profileElement.setAttribute("ID", profileElement.getAttribute("ID") + Instant.now().toEpochMilli());
		return toString(doc);
	}
	
	@Override
	public File changeVsbId(File file) throws Exception {
		InputStream targetStream = new FileInputStream(file);
		String content = changeVsbId(IOUtils.toString(targetStream));
		FileUtils.writeStringToFile(file, content);
		return file;
	}

	@Override
	public String changeVsbId(String content) throws Exception {
		Document doc = stringToDom(content);
		Element profileElement = (Element) doc.getElementsByTagName("ValueSetBindingsContext").item(0);
		profileElement.setAttribute("ID", profileElement.getAttribute("ID") + Instant.now().toEpochMilli());
		return toString(doc);
	}

	public static Document stringToDom(String xmlSource) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setXIncludeAware(false);
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlSource)));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toString(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}

}
