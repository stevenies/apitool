package com.smn.apitool.service;

import com.smn.apitool.model.API;
import com.smn.apitool.model.Entity;
import com.smn.apitool.service.adapter.staruml.AdaptorStarUML;
import com.smn.apitool.service.swagger.Swagger;
import com.smn.apitool.util.FileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Service {

	public static class DtoReadUMLFile {

		private List<Entity> entities = new ArrayList<>();
		private String error;
		private Map<Entity, List<String>> issues = new HashMap<>();

		public DtoReadUMLFile(String error) {
			this.error = error;
		}

		public DtoReadUMLFile(List<Entity> entityList, String error, Map<Entity, List<String>> issues) {
			this.entities.addAll(entityList);
			this.error = error;
			this.issues.putAll(issues);
		}

		public List<Entity> getEntities() {
			return this.entities;
		}

		public String getError() {
			return this.error;
		}

		public Map<Entity, List<String>> getIssues() {
			return this.issues;
		}
	}

	@Autowired
	private AdaptorStarUML adaptorStarUML;

	@Autowired
	private Swagger swagger;

	public DtoReadUMLFile readUMLFile(String filename, byte[] fileContent) {
		String fileExtension = FileUtil.getExtension(filename);

		boolean starUMLFile = "mdj".equalsIgnoreCase(fileExtension);
		if (starUMLFile) {
			AdaptorStarUML.DtoReadUMLFile status = this.adaptorStarUML.readUMLFile(fileContent);
			return new DtoReadUMLFile(status.getEntities(), status.getError(), status.getIssues());
		}

		return new DtoReadUMLFile("Information model file has an unknown file type");
	}

	public String generateSwagger(
		API api,
		String serverDomain,
		String contextRoot,
		boolean makePOST,
		boolean makeGET,
		boolean makePUT,
		boolean makePATCH,
		boolean makeDELETE,
		boolean makeSEARCH,
		Map<Entity, List<String>> issues)
		throws IOException {

		return this.swagger.generate(api, serverDomain, contextRoot, makePOST, makeGET, makePUT, makePATCH, makeDELETE, makeSEARCH, issues);
	}
}
