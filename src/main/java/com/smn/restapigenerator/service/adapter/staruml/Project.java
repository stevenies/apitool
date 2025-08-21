package com.smn.restapigenerator.service.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

	@JsonProperty
	private String _id;

	@JsonProperty
	private String name;

	@JsonProperty
	private List<UMLModel> ownedElements;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UMLModel> getOwnedElements() {
		return ownedElements;
	}

	public void setOwnedElements(List<UMLModel> ownedElements) {
		this.ownedElements = ownedElements;
	}
	
	
}
