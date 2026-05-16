package com.smn.restapigenerator.service.codegen;

import com.smn.restapigenerator.model.ApiCode;
import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.persistence.UserRepository;
import com.smn.restapigenerator.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.SpecValidationException;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeGenService {
    
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConfigProperties codeGenConfig;

	public ApiCode generateCode(User user, List<String> issues) throws IOException {

		// Create a filesystem directory for the user's API code artifacts.
		File userDir = this.userRepository.getUserStorageDir(user);
		File codeDir = new File(userDir, ApiCode.DIRNAME);

		// Delete a previously existing code directory.
		if (codeDir.exists()) {
			FileUtil.deleteFile(codeDir);
			user.setApiCode(null);
		}
		codeDir.mkdirs();

		ApiSpec apiSpec = user.getApiSpec();
		File swaggerFile = apiSpec.getSwaggerFile();

		// Configure Swagger Codegen
		CodegenConfigurator configurator = new CodegenConfigurator();
        configurator.setGeneratorName("spring");
		String swaggerFileURI = swaggerFile.toString().replace("\\", "/");
		configurator.setInputSpec(swaggerFileURI);
		String tempCodeDirURI = codeDir.toString().replace("\\", "/");
		configurator.setOutputDir(tempCodeDirURI);

		Map<String, Object> additionalProperties = this.codeGenConfig.getConfig();
        configurator.setAdditionalProperties(additionalProperties);

		// Generate code
		ApiCode apiCode = new ApiCode();
		try {
			ClientOptInput clientOptInput = configurator.toClientOptInput();
			new DefaultGenerator().opts(clientOptInput).generate();

			// Indicate that the user generated the API's skeleton implementation code.
			apiCode = new ApiCode(codeDir);
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
