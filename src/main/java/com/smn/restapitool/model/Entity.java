package com.smn.restapitool.model;

import java.util.ArrayList;
import java.util.List;

import com.smn.restapitool.model.MVA.TRelationDepth;
import com.smn.restapitool.util.StringUtil;

public class Entity implements Comparable<Entity> {

	private String name;
	private Entity supertype;
	private List<Entity> subtypes = new ArrayList<>();
	private boolean isEmbedded;
	private List<Attribute> attributes = new ArrayList<>();
	private Attribute explicitId;
	private List<MVA> relations = new ArrayList<>();
	private boolean hasShallowRelations;
	private boolean hasDeepRelations;

	public Entity(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replace(" ", "_");
		}
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * @return the Entity's name with a prefix attached based on whether the Entity has relations or subtypes.
	 */
	public String getDeepName() {
		boolean hasRelations = this.hasShallowRelations() || this.hasDeepRelations();
		boolean hasSubtypes = this.getSubtypes().size() > 0;
		return this.name + (hasSubtypes ? "-SubtypesArray" : hasRelations ? "-DeepArray" : "-Array");
	}

	public Entity getSupertype() {
		return this.supertype;
	}

	public void setSupertype(Entity supertype) {
		this.supertype = supertype;
		supertype.subtypes.add(this);
	}

	public List<Entity> getSubtypes() {
		return this.subtypes;
	}

	/**
	 * @return True if the Entity is completely embedded within another Entity via a "Composite" relation.
	 */
	public boolean isEmbedded() {
		return this.isEmbedded;
	}

	public void setEmbedded(boolean isEmbedded) {
		this.isEmbedded = isEmbedded;
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
		return this.explicitId;
	}

	public List<MVA> getRelations() {
		return this.relations;
	}

	public void addRelation(MVA relation) {
		this.relations.add(relation);

		TRelationDepth relationDepth = relation.getRelationDepth();
		if (relationDepth == TRelationDepth.LINK) {
			this.hasShallowRelations = true;
		}
		if (relationDepth == TRelationDepth.EMBED || relationDepth == TRelationDepth.EMBED_ALL) {
			this.hasDeepRelations = true;
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

		buffer.append("  isEmbedded: " + this.isEmbedded + "\n");
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
