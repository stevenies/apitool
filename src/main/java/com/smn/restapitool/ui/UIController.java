package com.smn.restapitool.ui;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smn.restapitool.model.ApiSpec;
import com.smn.restapitool.model.User;
import com.smn.restapitool.model.uml.DomainModel;
import com.smn.restapitool.model.uml.Entity;
import com.smn.restapitool.service.Service;
import com.smn.restapitool.service.Service.DtoReadUMLFile;
import com.smn.restapitool.util.StringUtil;

@Controller
public class UIController {

	@Autowired
	private Service service;

	@PostMapping("/login")
	public String login(
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String accessToken,
		HttpServletResponse response,
        HttpSession session,
		Model model) {

		List<String> errors = new ArrayList<>();

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(email)) {
			errors.add("Enter a valid email address");
		} else if (!StringUtil.isValidEmail(email)) {
			errors.add("Enter a valid email address");
		}

		if (StringUtil.isEmpty(accessToken)) {
			errors.add("Enter the access token you received when your account was registered");
		}

		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "registration";
		}

		User user = this.service.findUser(accessToken);
		if (user == null || !email.equals(user.getEmail())) {
			errors.add("Either the email address or access token is invalid");
			model.addAttribute("errors", errors);
			return "registration";
		}
		session.setAttribute("user", user);
		model.addAttribute("user", user);

		ApiSpec apiSpec = user.getApiSpec();
		if (apiSpec == null) {
			apiSpec = new ApiSpec();
		}
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
		if (!isAccessAllowed) {
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

	public String downloadApiSpec(User user, HttpServletResponse response) {
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

	@GetMapping("/viewAPICodeForm")
	public String viewAPICodeForm(Model model) {
		return "apiCodeForm";
	}

}
