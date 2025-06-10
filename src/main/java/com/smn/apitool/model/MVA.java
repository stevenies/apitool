package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

/**
 * Represents a Multi-Value Attribute (aka a Relation between two classes)
 */
public class MVA {

	public enum TRelationDepth {
		NONE, // Relation will not be included in the Entity's schema definition
		SHALLOW, // Relation will be represented in the Entity's schema definition as the primary ID of the target class
		DEEP, // Relation will be represented in the Entity's schema definition by embedding only the attributes from target class's schema definition
		DEEP_RELATIONS // Relation will be represented in the Entity's schema definition by embedding the attributes and relations from target class's schema definition
	}

	private Entity targetEntity;
	private String name;
	private String cardinality;
	private boolean isComposite = false;
	private TRelationDepth relationDepth = TRelationDepth.NONE;

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

	public TRelationDepth getRelationDepth() {
		return this.relationDepth;
	}

	public void setRelationDepth(TRelationDepth relationDepth) {
		this.relationDepth = relationDepth;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		String targetEntityName = this.targetEntity.getName();
		buffer.append(targetEntityName).append(" ").append(this.name);
		if (StringUtil.isEmpty(this.cardinality)) {
			this.cardinality = "1";
		}
		buffer.append(" (cardinality:").append(this.cardinality).append(", isComposite:").append(this.isComposite).append(", relationDepth:").append(this.relationDepth).append(")\n");

		return buffer.toString();
	}

}
