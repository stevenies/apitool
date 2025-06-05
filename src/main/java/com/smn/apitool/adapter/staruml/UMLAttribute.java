package com.smn.apitool.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLAttribute {

	@JsonProperty
	private String _id;

	@JsonProperty
	private Reference _parent;

	@JsonProperty
	private String name;

	@JsonProperty
	private String type;

	@JsonProperty
	private boolean isReadOnly;

	@JsonProperty
	private String defaultValue;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
