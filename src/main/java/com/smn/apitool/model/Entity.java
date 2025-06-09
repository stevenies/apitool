package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class Entity {

	private String name;
	private Entity supertype;
	private List<Attribute> attributes = new ArrayList<>();
	private Attribute explicitId;
	private List<MVA> relations = new ArrayList<>();
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

	public Entity getSupertype() {
		return this.supertype;
	}

	public void setSupertype(Entity supertype) {
		this.supertype = supertype;
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

	public Attribute getExplicitId() {
		return this.explicitId;
	}

	public List<MVA> getRelations() {
		return this.relations;
	}

	public void addRelation(MVA relation) {
		this.relations.add(relation);

		boolean isComposite = relation.isComposite();
		boolean isDeepRelation = relation.isDeepRelation();
		if (isComposite || isDeepRelation) {
			this.hasDeepRelations = true;
		}
	}

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
		
		buffer.append("  hasDeepRelations: " + hasDeepRelations + "\n");
		buffer.append("  explicitAttribute: " + (explicitId == null ? "none" : explicitId.getName()) + "\n");

		for (Attribute attribute : this.attributes) {
			buffer.append("  ").append(attribute);
		}

		for (MVA relation : this.relations) {
			buffer.append("  ").append(relation);
		}

		buffer.append("}\n");
		return buffer.toString();
	}

}
