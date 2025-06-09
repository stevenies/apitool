package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

/**
 * Represents a Multi-Value Attribute (aka a Relation between two classes)
 */
public class MVA {

	private Entity targetEntity;
	private String name;
	private String cardinality;
	private boolean isComposite;
	private boolean isDeepRelation;

	public MVA(Entity targetEntity, String name, String cardinality) {
		String targetEntityName = targetEntity.getName();

		if (StringUtil.isEmpty(name)) {
			name = targetEntityName; // TODO convert to camelcase
		} else {
			name = name.replace(" ", "_");
		}
		if (StringUtil.isEmpty(cardinality)) {
			cardinality = "1";
		}

		this.targetEntity = targetEntity;
		this.name = name;
		this.cardinality = cardinality;
	}

	public Entity getTargetEntity() {
		return this.targetEntity;
	}

	public String getName() {
		return this.name;
	}

	public String getCardinality() {
		return this.cardinality;
	}

	public boolean isComposite() {
		return this.isComposite;
	}

	public void setComposite(boolean isComposite) {
		this.isComposite = isComposite;
	}

	public boolean isDeepRelation() {
		return this.isDeepRelation;
	}

	public void setDeepRelation(boolean isDeepRelation) {
		this.isDeepRelation = isDeepRelation;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		String targetEntityName = this.targetEntity.getName();
		buffer.append(targetEntityName).append(" ").append(this.name);
		if (StringUtil.isEmpty(this.cardinality)) {
			this.cardinality = "1";
		}
		buffer.append(" (cardinality:").append(this.cardinality).append(", isComposite:").append(this.isComposite).append(", isDeepRelation:").append(this.isDeepRelation).append(")\n");

		return buffer.toString();
	}

}
