package com.smn.apitool.model;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	private String name;

	private Entity supertype;

	private List<Relation> relations = new ArrayList<>();

	private List<Attribute> attributes = new ArrayList<>();

	public Entity(String name) {
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

	public List<Relation> getRelations() {
		return relations;
	}

	public void addRelation(Relation relation) {
		this.relations.add(relation);
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

}
