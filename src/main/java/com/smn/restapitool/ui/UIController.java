package com.smn.restapitool.ui;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

	@GetMapping("/viewAPISpecForm")
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

	@PostMapping("/uploadDomainModel")
	public String uploadFile(
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

		List<String> errors = new ArrayList<>();

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

		// Verify that the required fields are provided.
		if (StringUtil.isEmpty(title)) {
			errors.add("Specify the title clients will use to refer to the API");
		}

		String filename = file.getOriginalFilename();
		if (file == null || StringUtil.isEmpty(filename)) {
			errors.add("Select the file containing the API's business domain model");
		}

		if (StringUtil.isEmpty(serverDomain)) {
			errors.add("Specify the domain where the API will be hosted");
		}

		if (StringUtil.isEmpty(contextRoot)) {
			errors.add("Specify the context root for the API's various endpoint URIs");
		}

		if (StringUtil.isEmpty(port)) {
			errors.add("Specify the server port where the API will be hosted");
		}

		if (user == null || !errors.isEmpty()) {
			model.addAttribute("errors", errors);

			// Delete a previously generated apiSpecFile, if any.
			this.service.deleteApiSpec(user);
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

	@GetMapping("/downloadAPISpec")
	public String downloadAPISpec(
		@RequestParam(required = false, defaultValue = "") String accessToken,
		HttpServletResponse response,
		Model model) {

		// if (StringUtil.isEmpty(accessToken)) {
		// 	model.addAttribute("errors", List.of("Access token is required to download the API specification."));
		// 	return "view";
		// }

		// if (!this.service.userVerified(accessToken)) {
		// 	model.addAttribute("errors", List.of("Access token is either invalid or expired."));
		// 	return "view";
		// }

		// try {
		// 	String apiSpec = this.service.getApiSpec();
		// 	if (apiSpec == null) {
		// 		model.addAttribute("errors", List.of("No API specification available for download."));
		// 		return "view";
		// 	}

		// 	try (InputStream inputStream = new ByteArrayInputStream(apiSpec.getBytes()); OutputStream outputStream = response.getOutputStream()) {

		// 		response.setContentType("application/json");
		// 		response.setHeader("Content-Disposition", "attachment; filename=apiSpec.json");

		// 		byte[] buffer = new byte[1024];
		// 		int bytesRead;
		// 		while ((bytesRead = inputStream.read(buffer)) != -1) {
		// 			outputStream.write(buffer, 0, bytesRead);
		// 		}
		// 	}
		// 	return null;

		// } catch (Throwable t) {
		// 	t.printStackTrace();
		// 	model.addAttribute("errors", List.of(t.getMessage()));
		// 	return "view";
		// }
		return null;
	}

	@GetMapping("/viewAPICodeForm")
	public String viewAPICodeForm(Model model) {
		return "apiCodeForm";
	}

}
