package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class Entity {

	private String name;
	private Entity supertype;
	private Attribute explicitId;
	private List<Attribute> attributes = new ArrayList<>();
	private List<MVA> relations = new ArrayList<>();

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

	public Attribute getExplicitId() {
		return this.explicitId;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public void addAttribute(Attribute attribute, boolean isID) {
		this.addAttribute(attribute);
		if (isID) {
			this.explicitId = attribute;
		}
	}

	public List<MVA> getRelations() {
		return this.relations;
	}

	public void addRelation(MVA relation) {
		this.relations.add(relation);
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
