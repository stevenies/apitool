package com.smn.apitool.ui;

import com.smn.apitool.service.Service;
import com.smn.apitool.service.Service.DtoReadUMLFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ui")
public class UIController {

	@Autowired
	private Service service;

	@RequestMapping(value = "/uploadInfoModel", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		String filename = file.getOriginalFilename();
		try {

			// Creating an object of FileOutputStream class
			DtoReadUMLFile status = this.service.readUMLFile(filename, file.getBytes());

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}
}
