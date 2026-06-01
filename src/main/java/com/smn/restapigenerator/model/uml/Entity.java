package com.smn.restapigenerator.model.uml;

import com.smn.restapigenerator.model.uml.MVA.TRelationDepth;
import com.smn.restapigenerator.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class Entity implements Comparable<Entity> {

	private String name;
	private Entity supertype;
	private List<Entity> subtypes = new ArrayList<>();
	private List<Attribute> attributes = new ArrayList<>();
	private Attribute explicitId;
	private List<MVA> relations = new ArrayList<>();
	private boolean hasShallowRelations;
	private boolean hasDeepRelations;

	public Entity(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/*
	 * @return name with spaces replaced by underscores
	 */
	public String getNameNoSpaces() {
		return this.name.replaceAll(" ", "_");
	}

	public String getNameCamelCase() {
		return StringUtil.toCamelCase(this.name);
	}

	public String getNamePascalCase() {
		return StringUtil.toPascalCase(this.name);
	}

	public String getNameKebabCase() {
		return StringUtil.toKebabCase(this.name);
	}

	public Entity getSupertype() {
		return this.supertype;
	}

	public void setSupertype(Entity supertype) {
		this.supertype = supertype;
		supertype.subtypes.add(this);
	}

	public boolean isSupertype() {
		boolean isSupertype = !this.subtypes.isEmpty();
		return isSupertype;
	}

	public List<Entity> getSubtypes() {
		return this.subtypes;
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public void addAttribute(Attribute attribute, boolean isID) {
		this.attributes.add(isID ? 0 : this.attributes.size(), attribute);
		if (isID) {
			this.explicitId = attribute;
		}
	}

	/**
	 * @return either the attribute serving as the Entity's unique identifier or null if an attribute serving as the Entity's primary key has not been defined.
	 */
	public Attribute getExplicitId() {
		Attribute id = this.explicitId;
		if (id == null && this.supertype != null) {
			id = this.supertype.getExplicitId();
		}
		return id;
	}

	public List<MVA> getRelations() {
		return this.relations;
	}

	public void addRelation(MVA relation) {
		this.relations.add(relation);
		this.hasShallowRelations = true;

		TRelationDepth relationDepth = relation.getRelationDepth();
		if (relationDepth == TRelationDepth.EMBED || relationDepth == TRelationDepth.EMBEDALL) {
			this.hasDeepRelations = true;
			this.hasShallowRelations = false;
		}
	}

	/**
	 * @return True if the Entity contains one or more relations containing the "link" stereotype.
	 */
	public boolean hasShallowRelations() {
		return this.hasShallowRelations;
	}

	/**
	 * @return True if the Entity contains one or more relations marked as a composite relation or containing the "embed" or "embedAll" stereotypes.
	 */
	public boolean hasDeepRelations() {
		return this.hasDeepRelations;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("class ").append(this.name);
		if (this.supertype != null) {
			String superTypeName = this.supertype.getName();
			buffer.append(" extends ").append(superTypeName);
		}
		buffer.append(" {\n");

		buffer.append("  hasDeepRelations: " + this.hasDeepRelations + "\n");
		buffer.append("  explicitId: " + (this.explicitId == null ? "none" : this.explicitId.getName()) + "\n");

		for (Attribute attribute : this.attributes) {
			buffer.append("  ").append(attribute);
		}

		for (MVA relation : this.relations) {
			buffer.append("  ").append(relation);
		}

		buffer.append("}\n");
		return buffer.toString();
	}

	@Override
	public int compareTo(Entity other) {
		String thisName = this.name;
		String otherName = other.name;
		return thisName.compareTo(otherName);
	}

}
