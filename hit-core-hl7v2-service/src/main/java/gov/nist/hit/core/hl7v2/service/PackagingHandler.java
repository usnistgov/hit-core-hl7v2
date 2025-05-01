package gov.nist.hit.core.hl7v2.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;

import gov.nist.hit.core.hl7v2.domain.UploadedProfileModel;

public interface PackagingHandler {

	public List<UploadedProfileModel> getUploadedProfiles(String profileXML, String valueSetXML, String valueSetBindingsXML, String coConstraintsXML);

	public String removeUnusedAndDuplicateMessages(String content, Set<UploadedProfileModel> presentMessages);

	public File changeProfileId(File file) throws Exception;

	public File changeConstraintId(File file) throws Exception;

	public File changeVsId(File file) throws Exception;
	
	public File changeCoConstraintsId(File coConstraintsFile) throws Exception;

	public File changeSlicingsId(File slicingsFile) throws Exception;

	public File changeVsbId(File vsbFile) throws Exception;

	public File zip(List<File> files, String filename) throws Exception;

	public String changeProfileId(String file) throws Exception;

	public String changeConstraintId(String file) throws Exception;

	public String changeVsId(String file) throws Exception;
	
	public String changeCoConstraintsId(String coConstraintsFile) throws Exception;

	public String changeSlicingsId(String slicingsFile) throws Exception;

	public String changeVsbId(String vsbFile) throws Exception;

	public Document toDoc(String xmlSource);

	

}
