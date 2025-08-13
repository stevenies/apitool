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

import com.smn.restapitool.model.API;
import com.smn.restapitool.model.Entity;
import com.smn.restapitool.service.Service;
import com.smn.restapitool.service.Service.DtoReadUMLFile;
import com.smn.restapitool.util.StringUtil;

@Controller
public class UIController {

	@Autowired
	private Service service;

	@GetMapping("/")
	public String view(Model model) {
		return "view";
	}

	@PostMapping("/uploadInfoModel")
	public String uploadFile(
		@RequestParam String title,
		@RequestParam String description,
		@RequestParam String version,
		@RequestParam MultipartFile file,
		@RequestParam String serverDomain,
		@RequestParam String contextRoot,
		@RequestParam(required = false, defaultValue = "false") boolean makeSEARCH,
		@RequestParam(required = false, defaultValue = "false") boolean makeGET,
		@RequestParam(required = false, defaultValue = "false") boolean makePOST,
		@RequestParam(required = false, defaultValue = "false") boolean makePUT,
		@RequestParam(required = false, defaultValue = "false") boolean makePATCH,
		@RequestParam(required = false, defaultValue = "false") boolean makeDELETE,
		HttpServletResponse response,
		Model model) {

		List<String> errors = new ArrayList<>();

		String filename = file.getOriginalFilename();
		if (file == null || StringUtil.isEmpty(filename)) {
			errors.add("You must specify the filename of the API's information model diagram");
			model.addAttribute("errors", errors);
			return "view";
		}

		if (StringUtil.isEmpty(title)) {
			title = "Title TBD";
		}
		if (StringUtil.isEmpty(description)) {
			description = "Description TBD";
		}
		if (StringUtil.isEmpty(version)) {
			version = "1.0";
		}
		if (StringUtil.isEmpty(serverDomain)) {
			serverDomain = "<domain>";
		}
		if (StringUtil.isEmpty(contextRoot)) {
			contextRoot = "<contextRoot>";
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
			API api = new API(title, description, version, entities);

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
}
