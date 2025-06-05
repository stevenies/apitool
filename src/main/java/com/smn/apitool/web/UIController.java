package com.smn.apitool.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.apitool.adapter.staruml.Project;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ui")
public class UIController {

	@RequestMapping(value = "/uploadInfoModel", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		String fileUploadStatus;

		try {

			// Creating an object of FileOutputStream class
			String jsonAsString = new String(file.getBytes());
			System.out.print(jsonAsString);

			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Project infoModel = mapper.readValue(jsonAsString, Project.class);

			// Closing the connection
			fileUploadStatus = "File Uploaded Successfully";
		}

		catch (Throwable t) {
			t.printStackTrace();
			fileUploadStatus = "Error in uploading file: " + t;
		}
		return fileUploadStatus;
	}
}
