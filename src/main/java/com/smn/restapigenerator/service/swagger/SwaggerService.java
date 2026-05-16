package com.smn.restapigenerator.service.swagger;

import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.uml.DomainModel;
import com.smn.restapigenerator.model.uml.Entity;
import com.smn.restapigenerator.persistence.UserRepository;
import com.smn.restapigenerator.service.swagger.staruml.AdaptorStarUML;
import com.smn.restapigenerator.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwaggerService {

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
	private UserRepository userRepository;

	@Autowired
	private AdaptorStarUML adaptorStarUML;

	@Autowired
	private SwaggerGenerator swagger;

	public DtoReadUMLFile readUMLFile(String filename, byte[] fileContent) {
		String fileExtension = FileUtil.getExtension(filename);

		boolean starUMLFile = "mdj".equalsIgnoreCase(fileExtension);
		if (starUMLFile) {
			AdaptorStarUML.DtoReadUMLFile status = this.adaptorStarUML.readUMLFile(fileContent);
			return new DtoReadUMLFile(status.getEntities(), status.getError(), status.getIssues());
		}

		return new DtoReadUMLFile("Information model file has an unknown file type");
	}

	public void generateSwagger(
		User user,
		DomainModel api,
		boolean makeSEARCH, boolean makeGET, boolean makePOST, boolean makePUT, boolean makeDELETE,
		String serverDomain,
		String contextRoot,
		String port,
		Map<Entity, List<String>> issues)
		throws IOException {

		// Create a filesystem directory for the user's API file artifacts.
		File userDir = this.userRepository.getUserStorageDir(user);
		File swaggerFile = new File(userDir, ApiSpec.FILENAME);

		// Delete a previously existing apiSpecFile.
		if (swaggerFile.exists()) {
			FileUtil.deleteFile(swaggerFile);
			user.setApiSpec(null);
		}

		// Generate the Swagger text and store it in the apiSpecFile.
		String swaggerText = this.swagger.generate(api, serverDomain, contextRoot, makePOST, makeGET, makePUT, makeDELETE, makeSEARCH, issues);
		FileUtil.writeTextToFile(swaggerText, swaggerFile);

		// Create the ApiSpec object
		ApiSpec apiSpec = new ApiSpec(swaggerFile);
		apiSpec.setTitle(api.getTitle());
		apiSpec.setDescription(api.getDescription());
		apiSpec.setVersion(api.getVersion());
		apiSpec.setMakePOST(makePOST);
		apiSpec.setMakeGET(makeGET);
		apiSpec.setMakePUT(makePUT);
		apiSpec.setMakeDELETE(makeDELETE);
		apiSpec.setMakeSEARCH(makeSEARCH);
		apiSpec.setServerDomain(serverDomain);
		apiSpec.setContextRoot(contextRoot);
		apiSpec.setPort(port);

		// Indicate that the user generated a new ApiSpec.
		user.setApiSpec(apiSpec);
		this.userRepository.saveToJsonFile();
	}

	public void deleteSwagger(ApiSpec apiSpec) {
		if (apiSpec == null) {
			return;
		}
		apiSpec.deleteSwaggerFile();
	}

}
