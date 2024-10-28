package gov.nist.hit.core.hl7v2.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import jakarta.persistence.Table;

@Entity
public class APIKey implements Serializable {
	
	private static final long serialVersionUID = 3506426958342462919L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String bindingIdentifier;

	private String bindingUrl;

	private String bindingKey;

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

	public String getBindingKey() {
		return bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	

}
