package com.smn.apitool.service.swagger;

import com.smn.apitool.model.API;
import com.smn.apitool.model.Attribute;
import com.smn.apitool.model.Entity;
import com.smn.apitool.util.StringUtil;
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

	public String generate(API api, String serverDomain, String contextRoot, boolean makePOST, boolean makeGET, boolean makePUT, boolean makePATCH, boolean makeDELETE, boolean makeSEARCH)
		throws IOException {

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
		int tabs = 2;

		String title = api.getTitle();
		swagger = swagger.replace(Swagger.MARKER_TITLE, title);

		String description = api.getDescription();
		swagger = swagger.replace(Swagger.MARKER_DESCRIPTION, description);

		String version = api.getVersion();
		swagger = swagger.replace(Swagger.MARKER_VERSION, version);

		String servers = this.makeServers(tabs + 1, serverDomain, contextRoot);
		swagger = swagger.replace(Swagger.MARKER_SERVERS, servers);

		String tags = this.makeTags(tabs, api);
		swagger = swagger.replace(Swagger.MARKER_TAGS, tags);

		String paths = this.makePaths(tabs++, api);
		swagger = swagger.replace(Swagger.MARKER_PATHS, paths);

		String schemas = this.makeSchema(tabs--, api);
		swagger = swagger.replace(Swagger.MARKER_SCHEMAS, schemas);

		return swagger;
	}

	private String indent(int tabs) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < tabs; i++) {
			buffer.append("\t");
		}
		return buffer.toString();
	}

	private String makeServers(int tabs, String serverDomain, String contextRoot) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.indent(tabs)).append("\"url\": \"https://").append(serverDomain).append("/").append(contextRoot).append("\"");
		return buffer.toString();
	}

	private String makeTags(int tabs, API api) {
		StringBuilder buffer = new StringBuilder();
		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getName();
			if (buffer.length() > 0) {
				buffer.append(",\n");
			}
			buffer.append(this.indent(tabs++)).append("{\n");
			buffer.append(this.indent(tabs--)).append("\"name\": \"").append(entityName).append("\"\n");
			buffer.append(this.indent(tabs)).append("}");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	private String makeSchema(int tabs, API api) {
		StringBuilder buffer = new StringBuilder();
		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getName();
			List<Attribute> attributes = entity.getAttributes();

			// Create the shallow Entity containing only attributes
			buffer.append(this.indent(tabs++)).append("\"").append(entityName).append("-shallow\": {\n");
			buffer.append(this.indent(tabs)).append("\"type\": \"object\",\n");
			buffer.append(this.indent(tabs++)).append("\"properties\": {\n");

			boolean firstAttribute = true;
			for (Attribute attribute : attributes) {
				String name = attribute.getName();
				String type = attribute.getType();
				String defaultValue = attribute.getDefaultValue();
				boolean readOnly = attribute.isReadOnly();

				if (!firstAttribute) {
					buffer.append(",\n");
				}
				buffer.append(this.indent(tabs++)).append("\"").append(name).append("\": {\n");
				buffer.append(this.indent(tabs--)).append("\"type\": \"").append(type).append("\"\n");
				if (readOnly) {
					tabs++;
					buffer.append(this.indent(tabs--)).append("\"readonly\": \"true\"\n");
				}
				if (!StringUtil.isEmpty(defaultValue)) {
					tabs++;
					buffer.append(this.indent(tabs--)).append("\"example\": \"").append(defaultValue).append("\"\n");
				}
				buffer.append(this.indent(tabs)).append("}");
				firstAttribute = false;
			}
			buffer.append("\n");
			tabs--;

			buffer.append(this.indent(tabs--)).append("}\n");
			buffer.append(this.indent(tabs)).append("},\n");

			// Create a set of shallow Entities
			buffer.append(this.indent(tabs++)).append("\"").append(entityName).append("-set\": {\n");
			buffer.append(this.indent(tabs)).append("\"type\": \"array\",\n");
			buffer.append(this.indent(tabs++)).append("\"items\": {\n");
			buffer.append(this.indent(tabs--)).append("\"$ref\": \"#/components/schemas/").append(entityName).append("-shallow\"\n");
			buffer.append(this.indent(tabs--)).append("}\n");
			buffer.append(this.indent(tabs)).append("},\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	private String makePaths(int tabs, API api) {
		StringBuilder buffer = new StringBuilder();
		// TODO Implement
		return buffer.toString();
	}
}
