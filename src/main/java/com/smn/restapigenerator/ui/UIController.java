package com.smn.restapigenerator.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.smn.restapigenerator.exception.ExceptionUserDoesntExist;
import com.smn.restapigenerator.exception.ExceptionUserExists;
import com.smn.restapigenerator.model.ApiCode;
import com.smn.restapigenerator.model.ApiSpec;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.uml.DomainModel;
import com.smn.restapigenerator.model.uml.Entity;
import com.smn.restapigenerator.service.Service;
import com.smn.restapigenerator.service.Service.DtoReadUMLFile;
import com.smn.restapigenerator.util.Email;
import com.smn.restapigenerator.util.StringUtil;
import com.smn.restapigenerator.util.ZipUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
public class UIController {

	@Autowired
	private Service service;

	@Autowired
	private Email email;

	@PostMapping("/register")
	public String register(
		@RequestParam(required = false, defaultValue = "") String nameFirst,
		@RequestParam(required = false, defaultValue = "") String nameLast,
		@RequestParam(required = false, defaultValue = "") String company,
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String phone,
		HttpServletRequest request,
		HttpSession session) {

		List<String> errors = new ArrayList<>();
		session.setAttribute("loginErrors", null);
		session.setAttribute("registrationErrors", errors);
		session.setAttribute("registrationSuccess", false);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(nameFirst)) {
			errors.add("Enter your first name");
		} else {
			nameFirst = StringUtil.trim(nameFirst);
			session.setAttribute("nameFirst", nameFirst);
		}

		if (StringUtil.isEmpty(nameLast)) {
			errors.add("Enter your last name");
		} else {
			nameLast = StringUtil.trim(nameLast);
			session.setAttribute("nameLast", nameLast);
		}

		if (StringUtil.isEmpty(company)) {
			errors.add("Enter your company name or 'Self' if not employed");
		} else {
			company = StringUtil.trim(company);
			session.setAttribute("company", company);
		}

		if (StringUtil.isEmpty(email) || !StringUtil.isValidEmail(email)) {
			errors.add("Enter a valid email address using the format user@example.com");
		} else {
			email = StringUtil.trim(email);
			session.setAttribute("email", email);
		}

		if (StringUtil.isEmpty(phone) || !StringUtil.isValidPhone(phone)) {
			errors.add("Enter a valid phone number using the format 555-123-4567 (US) or<br/> +44 20 7946 0958 (International)");
		} else {
			phone = StringUtil.trim(phone);
			session.setAttribute("phone", phone);
		}

		if (errors.isEmpty()) {
			try {
				this.service.createUser(nameFirst, nameLast, company, email, phone);

				// Send the user an email to validate their email address.
				try {
					String urlDomain =
						request.getProtocol().toLowerCase().startsWith("https") ? "https://" : "http://"
						+ request.getServerName()
						+ (request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
					String subject = "Welcome to REST API Generator";
					String resourcePath = "templates/welcome.html";
					ClassPathResource resource = new ClassPathResource(resourcePath);
					String htmlBody = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
					htmlBody = String.format(htmlBody, urlDomain, email, nameFirst, nameLast);
					this.email.sendHtmlEmail(email, subject, htmlBody);
				} catch (Exception e) {
					// TODO Replace following with a logger.
					e.printStackTrace();
				}

			} catch (ExceptionUserExists e) {
				errors.add("Another user with the same name or email address already exists");
			} catch (Throwable t) {
				errors.add("An unexpected error occurred: " + t.getMessage());
			}
		}

		session.setAttribute("registrationSuccess", errors.isEmpty());
		return "login";
	}

	@GetMapping("/emailVerified")
	public String emailVerified(@RequestParam(required = false, defaultValue = "") String email, HttpSession session) {
		email = StringUtil.trim(email);
		session.setAttribute("email", email);
		return "enroll";
	}

	@PostMapping("/enroll")
	public String enroll(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String accessToken,
		@RequestParam(required = false, defaultValue = "") String accessPlan,
		HttpSession session) {

		List<String> errors = new ArrayList<>();
		session.setAttribute("enrollmentErrors", errors);

		email = StringUtil.trim(email);
		session.setAttribute("email", email);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(accessToken)) {
			errors.add("Enter your account's password");
		} else {
			accessToken = StringUtil.trim(accessToken);
			session.setAttribute("accessToken", accessToken);
		}

		User user = null;
		if (errors.isEmpty()) {
			try {
				user = this.service.registerUser(email, accessToken, accessPlan);
			} catch (ExceptionUserDoesntExist e) {
				errors.add("An account doesn't exist with the specified email address");
			} catch (Throwable t) {
				errors.add("An unexpected error occurred: " + t.getMessage());
			}
		}

		if (user == null || !errors.isEmpty()) {
			return "enroll";
		} else {
			ApiSpec apiSpec = user.getApiSpec();
			ApiCode apiCode = user.getApiCode();

			session.setAttribute("user", user);
			session.setAttribute("apiSpec", apiSpec);
			session.setAttribute("apiCode", apiCode);
			return "apiSpecForm";
		}
	}

	@PostMapping("/login")
	public String login(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String accessToken,
		HttpSession session,
		HttpServletResponse response) {

		List<String> errors = new ArrayList<>();
		session.setAttribute("loginErrors", errors);
		session.setAttribute("registrationErrors", null);

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(email) || !StringUtil.isValidEmail(email)) {
			errors.add("Enter a valid email address");
		}

		if (StringUtil.isEmpty(accessToken)) {
			errors.add("Enter the password you specified when your account was registered");
		}

		User user = null;
		if (errors.isEmpty()) {
			try {
				user = this.service.findUserByEmail(email);
			} catch (ExceptionUserDoesntExist e) {
				errors.add("The email address is invalid");
			}
		}

		if (user == null || !errors.isEmpty()) {
			return "login";
		} else {
			ApiSpec apiSpec = user.getApiSpec();
			ApiCode apiCode = user.getApiCode();

			session.setAttribute("user", user);
			session.setAttribute("apiSpec", apiSpec);
			session.setAttribute("apiCode", apiCode);

			String referrer = (String) session.getAttribute("referrer");
			return "viewApiCodeForm".equalsIgnoreCase(referrer) ? "apiCodeForm" : "apiSpecForm";
		}
	}

	@GetMapping("/viewApiSpecForm")
	public String viewAPISpecForm(Model model, HttpSession session) {

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
			session.setAttribute("referrer", "viewAPISpecForm");
			return "login";
		}

		ApiSpec apiSpec = user.getApiSpec();

		session.setAttribute("user", user);
		session.setAttribute("apiSpec", apiSpec);
		return "apiSpecForm";
	}

	@PostMapping("/doApiSpecForm")
	public void doApiSpecForm (
		@RequestParam(required = false) MultipartFile domainModel,
		@RequestParam String formFields,
		HttpSession session,
		HttpServletResponse response) {

		List<String> errors = new ArrayList<>();
		session.setAttribute("errors", errors);

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

        try {
            // Parse the JSON object containing the various form fields.
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(formFields);

            String action = this.jsonToString(jsonNode, "action");
            String title = this.jsonToString(jsonNode, "title");
            String description = this.jsonToString(jsonNode, "description");
			String version = this.jsonToString(jsonNode, "version");
			boolean makeGET = this.jsonToBoolean(jsonNode, "makeGET");
			boolean makePOST = this.jsonToBoolean(jsonNode, "makePOST");
			boolean makePUT = this.jsonToBoolean(jsonNode, "makePUT");
			boolean makeDELETE = this.jsonToBoolean(jsonNode, "makeDELETE");
			boolean makeSEARCH = this.jsonToBoolean(jsonNode, "makeSEARCH");
			String serverDomain = this.jsonToString(jsonNode, "serverDomain");
			String contextRoot = this.jsonToString(jsonNode, "contextRoot");
			String port = this.jsonToString(jsonNode, "port");

			switch (action) {
				case "generate": {
					this.generateApiSpec(
						user, title, description, version, domainModel,
						makeSEARCH, makeGET, makePOST, makePUT, makeDELETE,
						serverDomain, contextRoot, port, errors);
					response.setStatus(errors.size() == 0 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
					break;
				}
				default: {
					errors.add("Invalid action specified");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/viewApiCodeForm")
	public String viewAPICodeForm(HttpSession session) {

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
			session.setAttribute("referrer", "viewAPICodeForm");
			return "login";
		}

		ApiSpec apiSpec = user.getApiSpec();
 		ApiCode apiCode = user.getApiCode();

		session.setAttribute("user", user);
		session.setAttribute("apiSpec", apiSpec);
		session.setAttribute("apiCode", apiCode);
		return "apiCodeForm";
	}

	@PostMapping("/doApiCodeForm")
	public void doApiCodeForm (
		@RequestParam String formFields,
		HttpSession session,
		HttpServletResponse response) {

		List<String> errors = new ArrayList<>();
		session.setAttribute("errors", errors);

		// Verify that the user session is valid.
		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

        try {
            // Parse the JSON object containing the various form fields.
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(formFields);

            String action = this.jsonToString(jsonNode, "action");

			switch (action) {
				case "generate": {
					ApiCode apiCode = this.service.generateCode(user, errors);
					session.setAttribute("apiCode", apiCode);
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				}
				default: {
					errors.add("Invalid action specified");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("errors", List.of("Failed to process form data: " + e.getMessage()));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

    @GetMapping("/apiSpecFile")
    public ResponseEntity<InputStreamResource> getApiSpecFile(HttpSession session) {

		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
            return ResponseEntity.notFound().build();
		}

		// Obtain the filesystem path to the API specification file.
		ApiSpec apiSpec = user.getApiSpec();
		if (!apiSpec.isValid()) {
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
	public ResponseEntity<String> saveApiSpecFile(@RequestBody String content, HttpSession session) {

		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Unauthorized");
		}

		// Obtain the filesystem path to the API specification file.
		ApiSpec apiSpec = user.getApiSpec();
		if (!apiSpec.isValid()) {
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

    @GetMapping("/apiCodeZipFile")
    public ResponseEntity<StreamingResponseBody> apiCodeZipFile(HttpSession session) {

		User user = (User) session.getAttribute("user");
		if (user == null || user.getAccessToken() == null || user.getAccessToken().isEmpty()) {
            return ResponseEntity.notFound().build();
		}

		// Obtain the filesystem path to the API code zip file.
		ApiCode apiCode = user.getApiCode();
		if (!apiCode.isValid()) {
			return ResponseEntity.notFound().build();
		}
		File apiCodeDir = apiCode.getApiCodeDir();
        if (!apiCodeDir.exists()) {
            return ResponseEntity.notFound().build();
		}

		// Stream the directory as a ZIP file.
		StreamingResponseBody stream = outputStream -> {
			try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
				ZipUtil.zipDirectoryRecursive(apiCodeDir, apiCodeDir.getName(), zos);
				zos.finish();
			}
		};
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + apiCodeDir.getName() + ".zip\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(stream);
	}

	boolean jsonToBoolean(JsonNode parentJsonNode, String fieldName) {
		JsonNode fieldJsonNode = parentJsonNode.get(fieldName);
		String value = fieldJsonNode != null ? fieldJsonNode.asText() : "";
		return Boolean.parseBoolean(value);
	}

	String jsonToString(JsonNode parentJsonNode, String fieldName) {
		JsonNode fieldJsonNode = parentJsonNode.get(fieldName);
		String value = fieldJsonNode != null ? fieldJsonNode.asText() : "";
		return value;
	}

	private void generateApiSpec(
		User user,
		String title,
		String description,
		String version,
		MultipartFile file,
		boolean makeSEARCH, boolean makeGET, boolean makePOST, boolean makePUT, boolean makeDELETE,
		String serverDomain,
		String contextRoot,
		String port,
		List<String> errors) throws IOException {
	
		// Delete a previously generated Swagger file, if any.
		ApiSpec apiSpec = user.getApiSpec();
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

		String filename = file == null ? "" : file.getOriginalFilename();
		if (StringUtil.isEmpty(filename)) {
			errors.add("Select the file containing the API's business domain model");
		}

		apiSpec.setMakeSEARCH(makeSEARCH);
		apiSpec.setMakeGET(makeGET);
		apiSpec.setMakePOST(makePOST);
		apiSpec.setMakePUT(makePUT);
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

		if (user == null || file == null ||!errors.isEmpty()) {
			return;
		}

		// Read the information model into memory.
		DtoReadUMLFile status = this.service.readUMLFile(filename, file.getBytes());
		String error = status.getError();
		if (error != null) {
			errors.add(error);
			return;
		}

		// Create a new Domain Model instance populated with entities from the UML file.
		List<Entity> entities = status.getEntities();
		DomainModel domainModel = new DomainModel(title, description, version, entities);

		// Process the domain model to generate the API's swagger.
		apiSpec = this.service.generateSwagger(user, domainModel, makePOST, makeGET, makePUT, makeDELETE, makeSEARCH, serverDomain, contextRoot, port, status.getIssues());
	}

}