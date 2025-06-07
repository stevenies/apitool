package com.smn.apitool.service;

import com.smn.apitool.model.Entity;
import com.smn.apitool.service.adapter.staruml.AdaptorStarUML;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Service {

	public class DtoReadUMLFile {

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
			return entities;
		}

		public boolean hasErrors() {
			return errors != null && errors.size() > 0;
		}

		public List<String> getErrors() {
			return errors;
		}
	}

	@Autowired
	private AdaptorStarUML adaptorStarUML;

	public DtoReadUMLFile readUMLFile(String filename, byte[] fileContent) {

		boolean starUMLFile = true; // TODO Check file extension
		if (starUMLFile) {
			AdaptorStarUML.DtoReadUMLFile status = adaptorStarUML.readUMLFile(fileContent);
			return new DtoReadUMLFile(status.getEntities(), status.getErrors());
		} else {
			return new DtoReadUMLFile("Invalid File Type");
		}
	}
}
