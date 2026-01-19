package com.smn.restapigenerator.service;

import com.smn.restapigenerator.model.ApiCode;
import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.uml.DomainModel;
import com.smn.restapigenerator.model.uml.Entity;
import com.smn.restapigenerator.persistence.UserRepository;
import com.smn.restapigenerator.service.adapter.staruml.AdaptorStarUML;
import com.smn.restapigenerator.service.swagger.Swagger;
import com.smn.restapigenerator.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.SpecValidationException;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToolService {

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
		File swaggerFile = new File(userDir, "api-spec.json");

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

	public ApiCode generateCode(User user, List<String> issues) throws IOException {

		// Create a filesystem directory for the user's API code artifacts.
		File userDir = this.userRepository.getUserStorageDir(user);
		File tempCodeDir = new File(userDir, "apiCode");

		// Delete a previously existing code directory.
		if (tempCodeDir.exists()) {
			FileUtil.deleteFile(tempCodeDir);
			user.setApiCode(null);
		}
		tempCodeDir.mkdirs();

		ApiSpec apiSpec = user.getApiSpec();
		File swaggerFile = apiSpec.getSwaggerFile();

		// Configure Swagger Codegen
		CodegenConfigurator configurator = new CodegenConfigurator();
        configurator.setGeneratorName("spring");
		String swaggerFileURI = swaggerFile.toString().replace("\\", "/");
		configurator.setInputSpec(swaggerFileURI);
		String tempCodeDirURI = tempCodeDir.toString().replace("\\", "/");
		configurator.setOutputDir(tempCodeDirURI);

		Map<String, Object> additionalProperties = new HashMap<>();
		additionalProperties.put("useSpringBoot3", "true");
		additionalProperties.put("generateAliasAsModel", "true");
        configurator.setAdditionalProperties(additionalProperties);

		// Generate code
		ApiCode apiCode = new ApiCode();
		try {
			ClientOptInput clientOptInput = configurator.toClientOptInput();
			new DefaultGenerator().opts(clientOptInput).generate();

			// Indicate that the user generated the API's skeleton implementation code.
			apiCode = new ApiCode(tempCodeDir);
			user.setApiCode(apiCode);
			this.userRepository.saveToJsonFile();

		} catch (SpecValidationException e) {
			issues.addAll(e.getErrors());

		} catch (Throwable t) {
			t.printStackTrace();
			issues.add("Unexpected error occurred during code generation");
		}
		return apiCode;
	}

}
