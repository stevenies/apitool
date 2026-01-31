package com.smn.restapigenerator.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static String readResource(String resourcePath) throws IOException {
		Resource resource = new ClassPathResource(resourcePath);
		String resourceText = "";
		try (InputStream inputStream = resource.getInputStream(); OutputStream outputStream = new ByteArrayOutputStream()) {

			// Copy input stream to output stream
			byte[] byteBuffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteBuffer)) != -1) {
				outputStream.write(byteBuffer, 0, bytesRead);
			}
			resourceText = outputStream.toString();
		}
		return resourceText;
	}
	
	public static String getExtension(String filename) {
		if (StringUtil.isEmpty(filename)) {
			return filename;
		}
		int dot = filename.lastIndexOf('.');
		String extension = dot >= 0 ? filename.substring(dot + 1) : "";
		return extension;
	}

    public static void writeTextToFile(String text, File apiSpecFile) throws IOException {
		try (OutputStream outputStream = new FileOutputStream(apiSpecFile)) {
			outputStream.write(text.getBytes());
		}
    }

    public static void deleteFile(File file) {
		if (file == null ||!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File childFile : files) {
					deleteFile(childFile);
				}
			}
		}
		if (!file.delete()) {	
			logger.error("Failed to delete file: {}", file.getAbsolutePath());
		}
    }

}
