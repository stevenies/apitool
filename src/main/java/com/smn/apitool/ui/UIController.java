package com.smn.apitool.ui;

import com.smn.apitool.service.Service;
import com.smn.apitool.service.Service.DtoReadUMLFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UIController {

	@Autowired
	private Service service;

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String hello(Model model) {
		return "view";
	}

	@RequestMapping(value = "/uploadInfoModel", method = RequestMethod.POST)
	public String uploadFile(@RequestParam MultipartFile file, HttpServletResponse response) {

		if (file == null || StringUtils.isEmpty(file.getOriginalFilename())) {
			return "view";
		}

		String filename = file.getOriginalFilename();
		try {

			// Creating an object of FileOutputStream class
			DtoReadUMLFile status = this.service.readUMLFile(filename, file.getBytes());

			// Load file as Resource
			Resource resource = new ClassPathResource("/swagger/swagger.json");

			try (InputStream inputStream = resource.getInputStream(); OutputStream outputStream = response.getOutputStream()) {

				// Set response headers
				response.setContentType("application/json");
				response.setHeader("Content-Disposition", "attachment; filename=" + resource.getFilename());

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
			return "view";
		}
	}
}
