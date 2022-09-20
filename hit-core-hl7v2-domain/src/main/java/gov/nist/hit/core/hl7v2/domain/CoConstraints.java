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

/**
 * 
 * @author Harold Affo
 * 
 */
@Entity
@Table(name = "CoConstraints")
public class CoConstraints extends ValidationArtifact implements Serializable {


  private static final long serialVersionUID = 1L;


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @JsonIgnore

  @NotNull
  @Column(unique = true)
  protected String sourceId;

  @Column(nullable = true)
  protected String description;

  @NotNull
  @Column(columnDefinition = "LONGTEXT", nullable = false)
  protected String xml;

  public CoConstraints() {
    super();
  }

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  public CoConstraints(String coConstraintContent) {
    super();
    this.xml = coConstraintContent;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
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



}
