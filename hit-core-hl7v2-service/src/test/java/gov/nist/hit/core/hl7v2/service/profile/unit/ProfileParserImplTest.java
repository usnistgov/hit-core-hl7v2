package gov.nist.hit.core.hl7v2.service.profile.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import gov.nist.hit.core.domain.ProfileElement;
import gov.nist.hit.core.domain.ProfileModel;
import gov.nist.hit.core.hl7v2.service.impl.HL7V2ProfileParserImpl;
import gov.nist.hit.core.service.ProfileParser;
import gov.nist.hit.core.service.exception.ProfileParserException;

@Ignore
public class ProfileParserImplTest {

  ProfileParser parser = new HL7V2ProfileParserImpl();
  protected com.fasterxml.jackson.databind.ObjectMapper obm = new com.fasterxml.jackson.databind.ObjectMapper();
  
  
  //
  // @Test
  // public void testParse() throws ProfileParserException, IOException {
  // String profile =
  // IOUtils.toString(ProfileParserImplTest.class
  // .getResourceAsStream("/profiles/1_1_1_Profile.xml"));
  // String constraints =
  // IOUtils.toString(ProfileParserImplTest.class
  // .getResourceAsStream("/constraints/1_1_1_Constraints.xml"));
  // ProfileModel model = parser.parse(profile, "bfb1c703-c96e-4f2b-8950-3f5b1c9cd2d8",
  // constraints);
  //
  // }

  
  @Test
  public void testParseLRIProfile() throws ProfileParserException, IOException {
    String profile = IOUtils
        .toString(ProfileParserImplTest.class.getResourceAsStream("/profiles/1_1_2_Profile.xml"));
    String constraints = IOUtils.toString(
        ProfileParserImplTest.class.getResourceAsStream("/constraints/1_1_2_Constraints.xml"));
    ProfileModel model = parser.parse(profile, "ORU_R01:LRI_GU_FRN", constraints);
    ProfileElement message = model.getMessage();
    ProfileElement group = message.getChildren().get(2);
    assertEquals("PATIENT_RESULT", group.getName());
    group = group.getChildren().get(1);
    assertEquals("ORDER_OBSERVATION", group.getName());
    List<gov.nist.hit.core.domain.constraints.Predicate> predicates = group.getPredicates();
    assertFalse(predicates.size() == 0);
    group = group.getChildren().get(5);
    assertEquals("OBSERVATION", group.getName());
  }
  
  @Test
  public void testParseNewValidationFilsProfile() throws ProfileParserException, IOException {
	  obm = obm.setSerializationInclusion(Include.NON_NULL);
    String profile = IOUtils
        .toString(ProfileParserImplTest.class.getResourceAsStream("/cb/resources_works/Global/Profiles/new_Profile.xml"));
    String constraints = IOUtils.toString(
        ProfileParserImplTest.class.getResourceAsStream("/cb/resources_works/Global/Constraints/new_Constraints.xml"));
    String valueSetBindings = IOUtils.toString(
            ProfileParserImplTest.class.getResourceAsStream("/cb/resources_works/Global/Bindings/new_Bindings.xml"));
//    ProfileModel model = parser.parseEnhanced(profile, "ORU_R01:LRI_GU_FRN", constraints);
    ProfileModel model = parser.parseEnhanced(profile,"6349b594aa52d55524bee8d1_6351a40c22528b70eeebe592_630bbf61ef12678d0f3cb2a4"
    		,constraints,null,valueSetBindings,null,null);
    		
    
    ProfileElement message = model.getMessage();
    ProfileElement group = message.getChildren().get(2);
//    assertEquals("PATIENT_RESULT", group.getName());
//    group = group.getChildren().get(1);
//    assertEquals("ORDER_OBSERVATION", group.getName());
//    List<gov.nist.hit.core.domain.constraints.Predicate> predicates = group.getPredicates();
//    assertFalse(predicates.size() == 0);
//    group = group.getChildren().get(5);
//    assertEquals("OBSERVATION", group.getName());
    String json = obm.writeValueAsString(model);
    assertNotNull(model);
  }
  
  

  @Ignore
  @Test
  public void testParseNewValidationProfile() throws ProfileParserException, IOException {
    String profile = IOUtils
        .toString(ProfileParserImplTest.class.getResourceAsStream("/profiles/new_Profile.xml"));
    String constraints = IOUtils.toString(
        ProfileParserImplTest.class.getResourceAsStream("/constraints/new_Constraints.xml"));
    ProfileModel model = parser.parse(profile, "aa72383a-7b48-46e5-a74a-82e019591fe7", constraints);
    ProfileElement message = model.getMessage();
    ProfileElement group = message.getChildren().get(2);
    assertEquals("PATIENT_RESULT", group.getName());
    group = group.getChildren().get(1);
    assertEquals("ORDER_OBSERVATION", group.getName());
    List<gov.nist.hit.core.domain.constraints.Predicate> predicates = group.getPredicates();
    assertFalse(predicates.size() == 0);
    group = group.getChildren().get(5);
    assertEquals("OBSERVATION", group.getName());

  }

}
