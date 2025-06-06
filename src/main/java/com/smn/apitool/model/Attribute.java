package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

public class Attribute {

	private String name;
	private String type;
	private boolean isReadOnly;
	private String defaultValue;

	public Attribute(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replace(" ", "_");
		}
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

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(type).append(" ").append(name);
		if (!StringUtil.isEmpty(defaultValue)) {
			buffer.append(" = ").append(defaultValue);
		}
		if (isReadOnly) {
			buffer.append(" (Read Only)");
		}
		buffer.append("\n");
		return buffer.toString();
	}

}
