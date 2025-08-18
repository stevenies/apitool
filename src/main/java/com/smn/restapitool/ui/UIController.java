package com.smn.restapitool.ui;

import jakarta.servlet.http.HttpServletResponse;
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
	public String viewAPISpecForm(Model model) {
		model.addAttribute("nameFirst", "");
		model.addAttribute("nameLast", "");
		model.addAttribute("company", "");
		model.addAttribute("email", "");
		model.addAttribute("accessToken", "");
		model.addAttribute("title", "");
		model.addAttribute("description", "Description TBD");
		model.addAttribute("version", "1.0");
		model.addAttribute("serverDomain", "");
		model.addAttribute("contextRoot", "");
		model.addAttribute("port", "443");
		return "apiSpecForm";
	}

	@PostMapping("/uploadDomainModel")
	public String uploadFile(
		@RequestParam(required = false, defaultValue = "") String nameFirst,
		@RequestParam(required = false, defaultValue = "") String nameLast,
		@RequestParam(required = false, defaultValue = "") String company,
		@RequestParam(required = false, defaultValue = "") String email,
		@RequestParam(required = false, defaultValue = "") String accessToken,
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
		Model model) {

		List<String> errors = new ArrayList<>();

		if (StringUtil.isEmpty(nameFirst)) {
			errors.add("You must enter your first name");
		} else {
			model.addAttribute("nameFirst", nameFirst);
		}

		if (StringUtil.isEmpty(nameLast)) {
			errors.add("You must enter your last name");
		} else {
			model.addAttribute("nameLast", nameLast);
		}

		if (StringUtil.isEmpty(company)) {
			errors.add("You must enter either the name of your company or 'self'");
		} else {
			model.addAttribute("company", company);
		}

		if (StringUtil.isEmpty(email)) {
			errors.add("You must enter your email address");
		} else {
			model.addAttribute("email", email);
			if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
				errors.add("Invalid email address format");
			}
		}

		if (StringUtil.isEmpty(accessToken)) {
			errors.add("You must enter your access token to gain access to the REST API Generator tool");
		} else {
			model.addAttribute("accessToken", accessToken);
			this.validateAccessToken(accessToken, errors);
		}

		if (StringUtil.isEmpty(title)) {
			errors.add("You must specify the title developers use to refer to the API");
		} else {
			model.addAttribute("title", title);
		}

		String filename = file.getOriginalFilename();
		if (file == null || StringUtil.isEmpty(filename)) {
			errors.add("You must specify the filename of the API's business domain model");
		}

		if (StringUtil.isEmpty(description)) {
			description = "Description TBD";
		}
		model.addAttribute("description", description);

		if (StringUtil.isEmpty(version)) {
			version = "1.0";
		}
		model.addAttribute("version", version);

		if (StringUtil.isEmpty(serverDomain)) {
			errors.add("You must specify the domain where the API will be hosted");
		} else {
			model.addAttribute("serverDomain", serverDomain);
		}

		if (StringUtil.isEmpty(contextRoot)) {
			errors.add("You must specify the context root for the API's various endpoint URIs");
		} else {
			model.addAttribute("contextRoot", contextRoot);
		}

		if (StringUtil.isEmpty(port)) {
			port = "443";
		}
		model.addAttribute("port", port);

		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "apiSpecForm";
		}

		try {

			// Read the information model into memory.
			DtoReadUMLFile status = this.service.readUMLFile(filename, file.getBytes());
			
			String error = status.getError();
			if (error != null) {
				errors.add(error);
				model.addAttribute("errors", errors);
				return "view";
			}

			// Create a new API instance.
			List<Entity> entities = status.getEntities();
			DomainModel api = new DomainModel(title, description, version, entities);

			// Generate the API's swagger.
			String swagger = this.service.generateSwagger(api, serverDomain, contextRoot, makePOST, makeGET, makePUT, makePATCH, makeDELETE, makeSEARCH, status.getIssues());
			
			// Download the swagger to the client's browser.
			try (InputStream inputStream = new ByteArrayInputStream(swagger.getBytes()); OutputStream outputStream = response.getOutputStream()) {

				// Set response headers
				response.setContentType("application/json");
				response.setHeader("Content-Disposition", "attachment; filename=apiSwagger.json");

				// Copy input stream to output stream
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			return null;

		} catch (Throwable t) {
			t.printStackTrace();

			errors.add(t.getMessage());
			model.addAttribute("errors", errors);
			return "view";
		}
	}

	private void validateAccessToken(String accessToken, List<String> errors) {
		if (!"zzzsmn".equalsIgnoreCase(accessToken)) {
			errors.add("Invalid access token");
		}
	}

	@GetMapping("/viewAPICodeForm")
	public String viewAPICodeForm(Model model) {
		return "apiCodeForm";
	}

}
