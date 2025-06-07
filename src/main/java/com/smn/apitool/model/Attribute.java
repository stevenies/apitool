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
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isId() {
		return isId;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(type).append(" ").append(name);
		if (!StringUtil.isEmpty(defaultValue)) {
			buffer.append(" = ").append(defaultValue);
		}
		if (isId) {
			buffer.append(" (ID)");
		}
		if (isReadOnly) {
			buffer.append(" (Read Only)");
		}
		buffer.append("\n");
		return buffer.toString();
	}

}
