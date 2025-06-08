package com.smn.apitool.model;

import java.util.List;

public class API {

	private String title;
	private String description;
	private String version;
	private List<Entity> entities;

	public API(String title, String description, String version, List<Entity> entities) {
		super();
		this.title = title;
		this.description = description;
		this.version = version;
		this.entities = entities;
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
