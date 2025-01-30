package gov.nist.hit.core.hl7v2.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.hit.core.domain.TestPlan;
import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;
import gov.nist.hit.core.hl7v2.repo.HL7V2TestContextRepository;
import gov.nist.hit.core.hl7v2.service.HL7V2TestContextService;

@Service
public class HL7V2TestContextServiceImpl implements HL7V2TestContextService {

	
	static private Map<Long,TestPlan> cache = new HashMap<Long,TestPlan>();
	
	@Autowired
	private HL7V2TestContextRepository hL7V2TestContextRepository;

	
	@Override
	public HL7V2TestContext findOne(Long testContextId) {
		return hL7V2TestContextRepository.findHL7V2TestContextByTestContextId(testContextId);
	}
	
	
	@Override
	public HL7V2TestContext save(HL7V2TestContext testContext) {
		return hL7V2TestContextRepository.save(testContext);
	}
	
	
	

}
