package gov.nist.hit.core.hl7v2.service;

import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;

public interface HL7V2TestContextService {

	HL7V2TestContext save(HL7V2TestContext testContext);

	HL7V2TestContext findOne(Long testContextId);

}
