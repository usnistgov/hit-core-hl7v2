package gov.nist.hit.core.hl7v2.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.nist.hit.core.domain.ValidationArtifact;

@Entity
@Table(name = "ValueSetBindings")
public class ValueSetBindings extends ValidationArtifact implements Serializable {


  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @JsonIgnore

  @NotNull
  @Column(unique = true)
  protected String sourceId;

  @JsonIgnore
  protected String name;

  @JsonIgnore
  protected String description;

  @JsonIgnore
  @Column(columnDefinition = "LONGTEXT")
  protected String xml;



  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  public ValueSetBindings(String valueSetBindingsXml) {
    super();
    this.xml = valueSetBindingsXml;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ValueSetBindings() {
    super();
  }

}
