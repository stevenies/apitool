package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

public class Attribute {

	private String name;
	private String type;
	private boolean isId;
	private boolean isReadOnly;
	private String defaultValue;

	public Attribute(String name, String type, String defaultValue, boolean isId, boolean isReadOnly) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replace(" ", "_");
		}
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.isId = isId;
		this.isReadOnly = isReadOnly;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public boolean isId() {
		return this.isId;
	}

	public boolean isReadOnly() {
		return this.isReadOnly;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.type).append(" ").append(this.name);
		if (!StringUtil.isEmpty(this.defaultValue)) {
			buffer.append(" = ").append(this.defaultValue);
		}
		if (this.isId) {
			buffer.append(" (ID)");
		}
		if (this.isReadOnly) {
			buffer.append(" (Read Only)");
		}
		buffer.append("\n");
		return buffer.toString();
	}

}
