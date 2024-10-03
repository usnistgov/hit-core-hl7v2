package gov.nist.hit.core.hl7v2.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.nist.hit.core.domain.CoConstraints;
import gov.nist.hit.core.domain.ConformanceProfile;
import gov.nist.hit.core.domain.Constraints;
import gov.nist.hit.core.domain.Slicings;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.domain.ValueSetBindings;
import gov.nist.hit.core.domain.VocabularyLibrary;
import gov.nist.hit.core.domain.util.Views;
import jakarta.persistence.Table;

@Entity
@Table(name="HL7V2TestContext")
public class HL7V2TestContext extends TestContext {

	private static final long serialVersionUID = 1L;

	private boolean dqa;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(unique = true, nullable = false, insertable = true, updatable = true)
	@JsonProperty(value = "profile")
	protected ConformanceProfile conformanceProfile;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	protected VocabularyLibrary vocabularyLibrary;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(nullable = true, insertable = true, updatable = true)
	protected Constraints constraints;

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(nullable = true, insertable = true, updatable = true)
	protected Constraints addditionalConstraints;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(nullable = true, insertable = true, updatable = true)
	protected CoConstraints coConstraints;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(nullable = true, insertable = true, updatable = true)
	protected Slicings slicings;

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(nullable = true, insertable = true, updatable = true)
	protected ValueSetBindings valueSetBindings;
	
	@JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinTable(name = "hl7v2tc_apikey",
	      joinColumns = {@JoinColumn(name = "hl7v2tc_id", referencedColumnName = "id")},
	      inverseJoinColumns = {@JoinColumn(name = "apikey_id", referencedColumnName = "id")})
    @JsonView(Views.NoData.class)
    private Set<APIKey> apikeys = new HashSet<APIKey>();

	public HL7V2TestContext() {
		this.format = "hl7v2";
	}

	public ConformanceProfile getConformanceProfile() {
		return conformanceProfile;
	}

	public void setConformanceProfile(ConformanceProfile conformanceProfile) {
		this.conformanceProfile = conformanceProfile;
	}

	public Constraints getConstraints() {
		return constraints;
	}

	public void setConstraints(Constraints constraints) {
		this.constraints = constraints;
	}

	public VocabularyLibrary getVocabularyLibrary() {
		return vocabularyLibrary;
	}

	public void setVocabularyLibrary(VocabularyLibrary vocabularyLibrary) {
		this.vocabularyLibrary = vocabularyLibrary;
	}

	public Constraints getAddditionalConstraints() {
		return addditionalConstraints;
	}

	public void setAddditionalConstraints(Constraints addditionalConstraints) {
		this.addditionalConstraints = addditionalConstraints;
	}

	public CoConstraints getCoConstraints() {
		return coConstraints;
	}

	public void setCoConstraints(CoConstraints coConstraints) {
		this.coConstraints = coConstraints;
	}

	public Slicings getSlicings() {
		return slicings;
	}

	public void setSlicings(Slicings slicings) {
		this.slicings = slicings;
	}

	public ValueSetBindings getValueSetBindings() {
		return valueSetBindings;
	}

	public void setValueSetBindings(ValueSetBindings valueSetBindings) {
		this.valueSetBindings = valueSetBindings;
	}

	public boolean isDqa() {
		return dqa;
	}

	public void setDqa(boolean dqa) {
		this.dqa = dqa;
	}

	public Set<APIKey> getApikeys() {
		return apikeys;
	}

	public void setApikeys(Set<APIKey> apikeys) {
		this.apikeys = apikeys;
	}

	
}
