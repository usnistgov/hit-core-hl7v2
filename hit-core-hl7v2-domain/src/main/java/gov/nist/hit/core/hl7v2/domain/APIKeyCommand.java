package gov.nist.hit.core.hl7v2.domain;

import java.util.List;

public class APIKeyCommand {

	private Long id;
	
	private String bindingKey;
	
	//has an edit
	private boolean editBindingKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBindingKey() {
		return bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	public boolean isEditBindingKey() {
		return editBindingKey;
	}

	public void setEditBindingKey(boolean editBindingKey) {
		this.editBindingKey = editBindingKey;
	}
	
	
	

}
