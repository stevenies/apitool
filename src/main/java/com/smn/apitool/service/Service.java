package com.smn.apitool.service;

import com.smn.apitool.model.API;
import com.smn.apitool.model.Entity;
import com.smn.apitool.service.adapter.staruml.AdaptorStarUML;
import com.smn.apitool.service.swagger.Swagger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Service {

	public static class DtoReadUMLFile {

		private List<Entity> entities = new ArrayList<>();
		private List<String> errors = new ArrayList<>();

		public DtoReadUMLFile(String error) {
			this.errors.add(error);
		}

		public DtoReadUMLFile(List<Entity> entityList, List<String> errors) {
			this.entities.addAll(entityList);
			this.errors.addAll(errors);
		}

		public List<Entity> getEntities() {
			return this.entities;
		}

		public boolean hasErrors() {
			return this.errors != null && this.errors.size() > 0;
		}

		public List<String> getErrors() {
			return this.errors;
		}
	}

	@Autowired
	private AdaptorStarUML adaptorStarUML;

	@Autowired
	private Swagger swagger;

	public DtoReadUMLFile readUMLFile(String filename, byte[] fileContent) {
		boolean starUMLFile = true; // TODO Check file extension
		if (starUMLFile) {
			AdaptorStarUML.DtoReadUMLFile status = this.adaptorStarUML.readUMLFile(fileContent);
			return new DtoReadUMLFile(status.getEntities(), status.getErrors());
		}
		return new DtoReadUMLFile("Invalid File Type");
	}

	public String generateSwagger(API api, String serverDomain, String contextRoot) throws IOException {
		return this.swagger.generate(api, serverDomain, contextRoot);
	}
}
