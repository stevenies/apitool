package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

/**
 * Represents a Multi-Value Attribute (aka a Relation between two classes)
 */
public class MVA {

	public enum TRelationDepth {
		NONE, // Relation will not be included in the Entity's schema definition
		LINK, // Relation will be represented in the Entity's schema definition as the primary ID of the target class
		EMBED, // Relation will be represented in the Entity's schema definition by embedding only the attributes from target class's schema definition
		EMBED_ALL // Relation will be represented in the Entity's schema definition by embedding the attributes and relations from target class's schema definition
	}

	private Entity targetEntity;
	private String name;
	private String cardinality;
	private TRelationDepth relationDepth = TRelationDepth.NONE;
	private boolean makeEndpoint;

	public MVA(Entity targetEntity, String name, String cardinality, TRelationDepth relationDepth) {
		String targetEntityName = targetEntity.getName();

		this.targetEntity = targetEntity;
		this.name = StringUtil.isEmpty(name) ? StringUtil.toCamelCase(targetEntityName) : name.replace(" ", "_");
		this.cardinality = StringUtil.isEmpty(cardinality) ? "1" : cardinality;
		this.relationDepth = relationDepth;
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

	public TRelationDepth getRelationDepth() {
		return this.relationDepth;
	}

	public boolean isMakeEndpoint() {
		return this.makeEndpoint;
	}

	public void setMakeEndpoint(boolean makeEndpoint) {
		this.makeEndpoint = makeEndpoint;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		String targetEntityName = this.targetEntity.getName();
		buffer.append(targetEntityName).append(" ").append(this.name);
		buffer.append(" (cardinality:").append(this.cardinality).append(", relationDepth:").append(this.relationDepth).append(", makeEndpoint:").append(this.makeEndpoint).append(")\n");

		return buffer.toString();
	}

}
