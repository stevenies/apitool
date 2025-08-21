package com.smn.restapitool.ui;

import com.smn.restapitool.exception.ExceptionAccessTokenInUse;
import com.smn.restapitool.exception.ExceptionUserExists;
import com.smn.restapitool.model.ApiSpec;
import com.smn.restapitool.model.User;
import com.smn.restapitool.model.uml.DomainModel;
import com.smn.restapitool.model.uml.Entity;
import com.smn.restapitool.service.Service;
import com.smn.restapitool.service.Service.DtoReadUMLFile;
import com.smn.restapitool.util.StringUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
@Controller
public class UIController {

	@Autowired
	private Service service;

	@PostMapping("/register")
	public String register(
		@RequestParam(required = false, defaultValue = "") String nameFirst,
		@RequestParam(required = false, defaultValue = "") String nameLast,
		@RequestParam(required = false, defaultValue = "") String company,
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String accessPlan,
		@RequestParam(required = false, defaultValue = "") String accessToken,
		HttpServletResponse response,
        HttpSession session,
		Model model) {

		List<String> registrationErrors = new ArrayList<>();

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(nameFirst)) {
			registrationErrors.add("Enter your first name");
		} else {
			nameFirst = StringUtil.trim(nameFirst);
		}

		if (StringUtil.isEmpty(nameLast)) {
			registrationErrors.add("Enter your last name");
		} else {
			nameLast = StringUtil.trim(nameLast);
		}

		if (StringUtil.isEmpty(company)) {
			registrationErrors.add("Enter your company name or 'Self' if not employed");
		} else {
			company = StringUtil.trim(company);
		}

		if (StringUtil.isEmpty(email) || !StringUtil.isValidEmail(email)) {
			registrationErrors.add("Enter a valid email address");
		} else {
			email = StringUtil.trim(email);
		}

		if (StringUtil.isEmpty(accessToken)) {
			registrationErrors.add("Specify an access token you wish to use for your account");
		} else {
			accessToken = StringUtil.trim(accessToken);
		}

		User user = null;
		try {
			user = this.service.createUser(nameFirst, nameLast, company, email, accessPlan, accessToken);
		} catch (ExceptionUserExists e) {
			registrationErrors.add("Another user with the same name or email address already exists");
		} catch (ExceptionAccessTokenInUse e) {
			registrationErrors.add("That access token is already in use");
		} catch (Throwable t) {
			registrationErrors.add("An unexpected error occurred: " + t.getMessage());
		}

		if (user == null || !registrationErrors.isEmpty()) {
			model.addAttribute("registrationErrors", registrationErrors);
			return "registration";
		}

		session.setAttribute("user", user);
		model.addAttribute("user", user);

		ApiSpec apiSpec = user.getApiSpec();
		model.addAttribute("apiSpec", apiSpec);

		return "apiSpecForm";
	}

	@PostMapping("/login")
	public String login(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String accessToken,
		HttpServletResponse response,
        HttpSession session,
		Model model) {

		List<String> loginErrors = new ArrayList<>();

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(email) || !StringUtil.isValidEmail(email)) {
			loginErrors.add("Enter a valid email address");
		}

		if (StringUtil.isEmpty(accessToken)) {
			loginErrors.add("Enter the access token you received when your account was registered");
		}

		User user = this.service.findUser(accessToken);
		if (user == null || !email.equals(user.getEmail())) {
			loginErrors.add("Either the email address or access token is invalid");
		}

		if (user == null || !loginErrors.isEmpty()) {
			model.addAttribute("loginErrors", loginErrors);
			return "registration";
		}

		session.setAttribute("user", user);
		model.addAttribute("user", user);

		ApiSpec apiSpec = user.getApiSpec();
		model.addAttribute("apiSpec", apiSpec);

		return "apiSpecForm";
	}

	@GetMapping("/viewApiSpecForm")
	public String viewAPISpecForm(HttpSession session, Model model) {

		// Verify that the user session is valid.
		User user = null;
		if (session != null && !session.isNew()) {
            user = (User) session.getAttribute("user");
        }
		boolean isAccessAllowed = user != null && user.getAccessToken() != null && !user.getAccessToken().isEmpty();
		if (user == null || !isAccessAllowed) {
			return "registration";
		}

		ApiSpec apiSpec = user.getApiSpec();
		if (apiSpec == null) {
			apiSpec = new ApiSpec();
		}
		model.addAttribute("user", user);
		model.addAttribute("apiSpec", apiSpec);
		return "apiSpecForm";
	}

	@PostMapping("/doApiSpecForm")
	public String doApiSpecForm (
		@RequestParam String action,
		@RequestParam(required = false, defaultValue = "") String title,
		@RequestParam(required = false, defaultValue = "") String description,
		@RequestParam(required = false, defaultValue = "") String version,
		@RequestParam MultipartFile file,
		@RequestParam(required = false, defaultValue = "false") boolean makeSEARCH,
		@RequestParam(required = false, defaultValue = "false") boolean makeGET,
		@RequestParam(required = false, defaultValue = "false") boolean makePOST,
		@RequestParam(required = false, defaultValue = "false") boolean makePUT,
		@RequestParam(required = false, defaultValue = "false") boolean makePATCH,
		@RequestParam(required = false, defaultValue = "false") boolean makeDELETE,
		@RequestParam(required = false, defaultValue = "") String serverDomain,
		@RequestParam(required = false, defaultValue = "") String contextRoot,
		@RequestParam(required = false, defaultValue = "") String port,
		HttpServletResponse response,
        HttpSession session,
		Model model) {

		// Verify that the user session is valid.
		User user = null;
		if (session != null && !session.isNew()) {
            user = (User) session.getAttribute("user");
        }
		boolean isAccessAllowed = user != null && user.getAccessToken() != null && !user.getAccessToken().isEmpty();
		if (!isAccessAllowed) {
			return "registration";
		}
		model.addAttribute("user", user);

		List<String> errors = new ArrayList<>();
		switch (action) {
			case "generate": {
				return this.generateApiSpec(
					user, title, description, version, file, makeSEARCH, makeGET, makePOST, makePUT, makePATCH,
					makeDELETE, serverDomain, contextRoot, port, model, errors);
			}
			case "download": {
				return this.downloadApiSpec(user, response);
			}
			default: {
				errors.add("Invalid action specified");
				model.addAttribute("errors", errors);
				return "apiSpecForm";
			}
		}
	}

	private String generateApiSpec(
		User user,
		String title,
		String description,
		String version,
		MultipartFile file,
		boolean makeSEARCH,
		boolean makeGET,
		boolean makePOST,
		boolean makePUT,
		boolean makePATCH,
		boolean makeDELETE,
		String serverDomain,
		String contextRoot,
		String port,
		Model model,
		List<String> errors) {
	
		ApiSpec apiSpec = user.getApiSpec();
		model.addAttribute("apiSpec", apiSpec);

		// Delete a previously generated Swagger file, if any.
		this.service.deleteSwagger(apiSpec);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(title)) {
			errors.add("Specify the title clients will use to refer to the API");
		} else {
			title = StringUtil.trim(title);
		}
		apiSpec.setTitle(title);
		apiSpec.setDescription(description);

		if (StringUtil.isEmpty(version) || !StringUtil.isValidVersion(version)) {
			errors.add("Specify the API's version using the format 'major.minor' where major and minor are numeric values (e.g., 1.0)");
		} else {
			version = StringUtil.trim(version);
		}
		apiSpec.setVersion(version);

		String filename = file.getOriginalFilename();
		if (file == null || StringUtil.isEmpty(filename)) {
			errors.add("Select the file containing the API's business domain model");
		}

		apiSpec.setMakeSEARCH(makeSEARCH);
		apiSpec.setMakeGET(makeGET);
		apiSpec.setMakePOST(makePOST);
		apiSpec.setMakePUT(makePUT);
		apiSpec.setMakePATCH(makePATCH);
		apiSpec.setMakeDELETE(makeDELETE);
		
		if (StringUtil.isEmpty(serverDomain)) {
			errors.add("Specify the domain where the API will be hosted");
		} else {
			serverDomain = StringUtil.trim(serverDomain);
		}
		apiSpec.setServerDomain(serverDomain);

		if (StringUtil.isEmpty(contextRoot)) {
			errors.add("Specify the context root for the API's various endpoint URIs");
		} else {
			contextRoot = StringUtil.trim(contextRoot);
		}
		apiSpec.setContextRoot(contextRoot);

		if (StringUtil.isEmpty(port)) {
			errors.add("Specify the server port where the API will be hosted");
		} else {
			port = StringUtil.trim(port);
		}
		apiSpec.setPort(port);

		if (user == null || !errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "apiSpecForm";
		}

		// Generate the API specification.
		try {

			// Read the information model into memory.
			DtoReadUMLFile status = this.service.readUMLFile(filename, file.getBytes());
			String error = status.getError();
			if (error != null) {
				errors.add(error);
				model.addAttribute("errors", errors);
				return "apiSpecForm";
			}

			// Create a new Domain Model instance populated with entities from the UML file.
			List<Entity> entities = status.getEntities();
			DomainModel domainModel = new DomainModel(title, description, version, entities);

			// Process the domain model to generate the API's swagger.
			apiSpec = this.service.generateSwagger(user, domainModel, makePOST, makeGET, makePUT, makePATCH, makeDELETE, makeSEARCH, serverDomain, contextRoot, port, status.getIssues());
			model.addAttribute("apiSpec", apiSpec);
			return "apiSpecForm";

		} catch (Throwable t) {
			t.printStackTrace();

			errors.add(t.getMessage());
			model.addAttribute("errors", errors);
			return "apiSpecForm";
		}
	}

	private String downloadApiSpec(User user, HttpServletResponse response) {
		try {
			ApiSpec apiSpec = user.getApiSpec();
			File swaggerFile = apiSpec.getSwaggerFile();
			try (FileInputStream inputStream = new FileInputStream(swaggerFile); OutputStream outputStream = response.getOutputStream()) {

				response.setContentType("application/json");
				response.setHeader("Content-Disposition", "attachment; filename=apiSpec.json");

				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				response.flushBuffer();
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null; // Indicate that the response has been handled
	}

    @GetMapping("/apiSpecFile")
    public ResponseEntity<InputStreamResource> getApiSpecFile(HttpSession session) {

		User user = null;
		if (session != null && !session.isNew()) {
            user = (User) session.getAttribute("user");
        }
		boolean isAccessAllowed = user != null && user.getAccessToken() != null && !user.getAccessToken().isEmpty();
		if (user == null || !isAccessAllowed) {
            return ResponseEntity.notFound().build();
		}

		// Obtain the filesystem path to the API specification file.
		ApiSpec apiSpec = user.getApiSpec();
		if (apiSpec == null || !apiSpec.isValid()) {
			return ResponseEntity.notFound().build();
		}
		File swaggerFile = apiSpec.getSwaggerFile();
        if (!swaggerFile.exists()) {
            return ResponseEntity.notFound().build();
        }

		// Stream the file's contents to the caller.
        InputStreamResource resource;
		try {
			resource = new InputStreamResource(new FileInputStream(swaggerFile));
			return ResponseEntity.ok()
				.contentLength(swaggerFile.length())
				.contentType(MediaType.APPLICATION_JSON)
				.body(resource);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
            return ResponseEntity.notFound().build();
		}
     }

	@PostMapping("/apiSpecFile")
	public ResponseEntity<String> saveApiSpecFile(
			@RequestBody String content,
			HttpSession session) {

		User user = null;
		if (session != null && !session.isNew()) {
			user = (User) session.getAttribute("user");
		}
		boolean isAccessAllowed = user != null && user.getAccessToken() != null && !user.getAccessToken().isEmpty();
		if (user == null || !isAccessAllowed) {
			return ResponseEntity.status(403).body("Unauthorized");
		}

		// Obtain the filesystem path to the API specification file.
		ApiSpec apiSpec = user.getApiSpec();
		if (apiSpec == null || !apiSpec.isValid()) {
			return ResponseEntity.notFound().build();
		}
		File swaggerFile = apiSpec.getSwaggerFile();
        if (!swaggerFile.exists()) {
            return ResponseEntity.notFound().build();
        }

		// Save the uploaded text to the specified file (adjust path as needed)
		try {
			Path filePath = swaggerFile.toPath();
			Files.createDirectories(filePath.getParent());
			Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return ResponseEntity.ok("File saved successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Failed to save file: " + e.getMessage());
		}
	}

}
