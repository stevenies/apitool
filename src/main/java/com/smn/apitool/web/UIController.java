package com.smn.apitool.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/starUML")
public class UIController {

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		String fileUploadStatus;

		try {

			// Creating an object of FileOutputStream class
			String fileBytes = new String(file.getBytes());
			System.out.print(fileBytes);

			// Closing the connection
			fileUploadStatus = "File Uploaded Successfully";
		}

		catch (Exception e) {
			e.printStackTrace();
			fileUploadStatus = "Error in uploading file: " + e;
		}
		return fileUploadStatus;
	}
}
