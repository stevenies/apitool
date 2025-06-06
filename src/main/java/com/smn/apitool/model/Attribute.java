package com.smn.apitool.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {

	@JsonProperty
	private String name;

	@JsonProperty
	private String type;

	@JsonProperty
	private boolean isReadOnly;

	@JsonProperty
	private String defaultValue;

	public Attribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
