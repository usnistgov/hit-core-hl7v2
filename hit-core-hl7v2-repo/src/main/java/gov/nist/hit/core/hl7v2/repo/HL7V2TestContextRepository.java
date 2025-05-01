package gov.nist.hit.core.hl7v2.repo;

import gov.nist.hit.core.domain.ConformanceProfile;
import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface HL7V2TestContextRepository extends JpaRepository<HL7V2TestContext, Long> {

  @Query("select tc.conformanceProfile from TestContext tc where tc.id = :id")
  public ConformanceProfile findConformanceProfileByTestContextId(@Param("id") Long id);
  
  @Query("select tc from TestContext tc where tc.id = :id")
  public HL7V2TestContext findHL7V2TestContextByTestContextId(@Param("id") Long id);
  
  @Modifying
  @Transactional(value = "transactionManager")
  @Query("delete from HL7V2TestContext tc where tc.preloaded = true")
  public void deletePreloaded();
}
