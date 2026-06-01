package com.smn.restapigenerator.model.uml;

import com.smn.restapigenerator.util.StringUtil;

/**
 * Represents a Multi-Value Attribute (aka a Relation between two classes)
 */
public class MVA {

	// The following represent stereotypes that can be applied to a relationship in order to specify how the relationship
	// should be represented in the Entity's schema definition
	public enum TRelationDepth {
		NONE, // Relation will be represented in the Entity's schema definition as the primary ID of the target class
		EMBED, // Relation will be represented in the Entity's schema definition by embedding only the attributes from target class's schema definition
		EMBEDALL // Relation will be represented in the Entity's schema definition by embedding the attributes and relations from target class's schema definition
	}

	private Entity targetEntity;
	private String name;
	private String cardinality;
	private TRelationDepth relationDepth = TRelationDepth.NONE;

	public MVA(Entity targetEntity, String name, String cardinality, TRelationDepth relationDepth) {
		String targetEntityName = targetEntity.getNameCamelCase();

		this.targetEntity = targetEntity;
		this.name = StringUtil.isEmpty(name) ? targetEntityName : name;
		this.cardinality = StringUtil.isEmpty(cardinality) ? "1" : cardinality;
		this.relationDepth = relationDepth;
	}

	public Entity getTargetEntity() {
		return this.targetEntity;
	}

	public String getName() {
		return this.name;
	}

	public String getNameCamelCase() {
		return StringUtil.toCamelCase(this.name);
	}

	public String getNameKebabCase() {
		String relationName = StringUtil.toKebabCase(this.name);

		// Make the relation name plural
		boolean isSingleRelation = "1".equalsIgnoreCase(this.cardinality);
		if (!isSingleRelation) {
			if (relationName.length() > 1 && relationName.endsWith("y")) {
				relationName = relationName.substring(0, relationName.length() - 1) + "ies";
			} else if (relationName.endsWith("ed")) {
				// do nothing
			} else if (!relationName.endsWith("s")) {
				relationName += "s";
			}
		}
		return relationName;
	}

	public String getCardinality() {
		return this.cardinality;
	}

	public TRelationDepth getRelationDepth() {
		return this.relationDepth;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		String targetEntityName = this.targetEntity.getName();
		buffer.append(targetEntityName).append(" ").append(this.name);
		buffer.append(" (cardinality:").append(this.cardinality).append(", relationDepth:").append(this.relationDepth).append(")\n");

		return buffer.toString();
	}

}
