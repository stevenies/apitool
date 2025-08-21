package com.smn.restapigenerator.model.uml;

import java.util.ArrayList;
import java.util.List;

public class DomainModel {

	private String title;
	private String description;
	private String version;
	private List<Entity> entities;

	public DomainModel(String title, String description, String version, List<Entity> entities) {
		super();
		this.title = title;
		this.description = description;
		this.version = version;
		this.entities = new ArrayList<>(entities);
		this.entities.sort(null);;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public String getVersion() {
		return this.version;
	}

	public List<Entity> getEntities() {
		return this.entities;
	}

}
