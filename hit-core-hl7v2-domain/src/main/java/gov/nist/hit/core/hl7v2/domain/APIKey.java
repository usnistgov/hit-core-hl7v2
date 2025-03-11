package gov.nist.hit.core.hl7v2.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Entity
public class APIKey implements Serializable {
	
	private static final long serialVersionUID = 3506426958342462919L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String bindingIdentifier;

	private String bindingUrl;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String bindingKey;
	
	//has an edit
	@Transient
	@JsonProperty(access = Access.READ_WRITE)
	private boolean editBindingKey;
		
	@Transient
	@JsonSerialize
	@JsonDeserialize
	private Boolean hasBindingKey;

	public APIKey(String bindingIdentifier, String bindingUrl, String bindingKey) {
		super();
		this.bindingIdentifier = bindingIdentifier;
		this.bindingUrl = bindingUrl;
		this.bindingKey = bindingKey;
	}

	public APIKey() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBindingIdentifier() {
		return bindingIdentifier;
	}

	public void setBindingIdentifier(String bindingIdentifier) {
		this.bindingIdentifier = bindingIdentifier;
	}

	public String getBindingUrl() {
		return bindingUrl;
	}

	public void setBindingUrl(String bindingUrl) {
		this.bindingUrl = bindingUrl;
	}

	@JsonIgnore
	public String getBindingKey() {
		return bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	public Boolean getHasBindingKey() {
		if(bindingKey != null && !bindingKey.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}

	public void setHasBindingKey(Boolean hasBindingKey) {
		this.hasBindingKey = hasBindingKey;
	}
	
	public boolean isEditBindingKey() {
		return editBindingKey;
	}

	public void setEditBindingKey(boolean editBindingKey) {
		this.editBindingKey = editBindingKey;
	}

	

}
