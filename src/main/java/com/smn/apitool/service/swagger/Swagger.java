package com.smn.apitool.service.swagger;

import com.smn.apitool.model.API;
import com.smn.apitool.model.Entity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class Swagger {

	final static String MARKER_TITLE = ">>>title";
	final static String MARKER_DESCRIPTION = ">>>description";
	final static String MARKER_VERSION = ">>>version";
	final static String MARKER_SERVERS = "\">>>servers\": \"\"";
	final static String MARKER_TAGS = "{\">>>tags\": \"\"}";
	final static String MARKER_PATHS = "\">>>paths\": \"\"";
	final static String MARKER_SCHEMAS = "\">>>schemas\": \"\",";

	public String generate(API api, String serverDomain, String contextRoot) throws IOException {

		// Read the Swagger template into memory
		String swagger = null;
		Resource resource = new ClassPathResource("/swagger/swagger.json");
		try (InputStream inputStream = resource.getInputStream(); OutputStream outputStream = new ByteArrayOutputStream()) {

			// Copy input stream to output stream
			byte[] byteBuffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteBuffer)) != -1) {
				outputStream.write(byteBuffer, 0, bytesRead);
			}
			swagger = outputStream.toString();
		}

		// Update the template with the various substitution sections.
		String title = api.getTitle();
		swagger = swagger.replace(MARKER_TITLE, title);
		
		String description = api.getDescription();
		swagger = swagger.replace(MARKER_DESCRIPTION, description);

		String version = api.getVersion();
		swagger = swagger.replace(MARKER_VERSION, version);

		String servers = this.makeServers(serverDomain, contextRoot);
		swagger = swagger.replace(MARKER_SERVERS, servers);

		String tags = this.makeTags(api);
		swagger = swagger.replace(MARKER_TAGS, tags);

		String paths = "";  // TODO Implement
		swagger = swagger.replace(MARKER_PATHS, paths);

		String schemas = "";  // TODO Implement
		swagger = swagger.replace(MARKER_SCHEMAS, schemas);

		return swagger;
	}

	private String makeServers(String serverDomain, String contextRoot) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\t\t\t\"url\": \"https://").append(serverDomain).append("/").append(contextRoot).append("\"");
		return buffer.toString();
	}

	private String makeTags(API api) {
		StringBuilder buffer = new StringBuilder();
		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getName();
			if (buffer.length() > 0) {
				buffer.append(",\n");
			}
		    buffer.append("\t\t").append("{\n");
		    buffer.append("\t\t\t\"name\": \"").append(entityName).append("\"\n");
		    buffer.append("\t\t").append("}");
		}
		buffer.append("\n");
		return buffer.toString();
	}
}
