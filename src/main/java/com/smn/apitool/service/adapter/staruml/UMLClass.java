package com.smn.apitool.service.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLClass {

	@JsonProperty
	private String _id;

	@JsonProperty
	private Reference _parent;

	@JsonProperty
	private String name;

	@JsonProperty
	private List<OwnedElement> ownedElements;

	@JsonProperty
	private List<UMLAttribute> attributes;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Reference get_parent() {
		return _parent;
	}

	public void set_parent(Reference _parent) {
		this._parent = _parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<OwnedElement> getOwnedElements() {
		return ownedElements;
	}

	public void setOwnedElements(List<OwnedElement> ownedElements) {
		this.ownedElements = ownedElements;
	}

	public List<UMLAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<UMLAttribute> attributes) {
		this.attributes = attributes;
	}

}
