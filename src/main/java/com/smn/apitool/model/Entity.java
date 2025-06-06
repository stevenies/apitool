package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class Entity {

	private String name;
	private Entity supertype;
	private List<Attribute> attributes = new ArrayList<>();
	private List<MVA> relations = new ArrayList<>();

	public Entity(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replace(" ", "_");
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Entity getSupertype() {
		return supertype;
	}

	public void setSupertype(Entity supertype) {
		this.supertype = supertype;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public List<MVA> getRelations() {
		return relations;
	}

	public void addRelation(MVA relation) {
		this.relations.add(relation);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("class ").append(name);
		if (supertype != null) {
			String superTypeName = supertype.getName();
			buffer.append(" extends ").append(superTypeName);
		}
		buffer.append(" {\n");

		for (Attribute attribute : attributes) {
			buffer.append("  ").append(attribute);
		}

		for (MVA relation : relations) {
			buffer.append("  ").append(relation);
		}

		buffer.append("}\n");
		return buffer.toString();
	}

}
