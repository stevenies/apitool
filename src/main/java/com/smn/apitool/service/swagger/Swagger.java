package com.smn.apitool.service.swagger;

import com.smn.apitool.model.API;
import com.smn.apitool.model.Attribute;
import com.smn.apitool.model.Entity;
import com.smn.apitool.model.MVA;
import com.smn.apitool.model.MVA.TRelationDepth;
import com.smn.apitool.util.FileUtil;
import com.smn.apitool.util.StringUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	final static String MARKER_ENTITY_NAME = ">>>entityName";
	final static String MARKER_ENTITY_TYPE = ">>>entityType";
	final static String MARKER_ENTITY_ID = ">>>entityId";
	final static String MARKER_ENTITY_ID_TYPE = ">>>idType";
	final static String MARKER_TAG = ">>>tag";

	public String generate(
		API api,
		String serverDomain,
		String contextRoot,
		boolean makePOST,
		boolean makeGET,
		boolean makePUT,
		boolean makePATCH,
		boolean makeDELETE,
		boolean makeSEARCH,
		Map<Entity, List<String>> issues)
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
		String title = api.getTitle();
		swagger = swagger.replace(Swagger.MARKER_TITLE, title);

		String description = api.getDescription();
		swagger = swagger.replace(Swagger.MARKER_DESCRIPTION, description);

		String version = api.getVersion();
		swagger = swagger.replace(Swagger.MARKER_VERSION, version);

		int tabs = 2;
		String servers = this.makeServers(tabs + 1, serverDomain, contextRoot);
		swagger = swagger.replace(Swagger.MARKER_SERVERS, servers);

		String tags = this.makeTags(tabs, api);
		swagger = swagger.replace(Swagger.MARKER_TAGS, tags);

		String paths = this.makePaths(tabs++, api, makePOST, makeGET, makePUT, makePATCH, makeDELETE, makeSEARCH, issues);
		swagger = swagger.replace(Swagger.MARKER_PATHS, paths);

		String schemas = this.makeSchema(tabs--, api, issues);
		return swagger.replace(Swagger.MARKER_SCHEMAS, schemas);
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
			boolean isEmbedded = entity.isEmbedded();

			// Don't make tags for embedded Entities.
			if (isEmbedded) {
				continue;
			}

			if (buffer.length() > 0) {
				buffer.append(",\n");
			}
			buffer.append(this.indent(tabs++)).append("{\n");
			buffer.append(this.indent(tabs)).append("\"name\": \"").append(entityName).append("\"\n");
			buffer.append(this.indent(--tabs)).append("}");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	private String makeSchema(int tabs, API api, Map<Entity, List<String>> issues) {
		StringBuilder buffer = new StringBuilder();

		boolean firstEntity = true;
		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getName();
			boolean hasShallowRelations = entity.hasShallowRelations();
			boolean hasDeepRelations = entity.hasDeepRelations();
			List<Entity> subtypes = entity.getSubtypes();

			if (!firstEntity) {
				buffer.append("\n");
			}

			// Create an Entity containing only attributes
			buffer.append(this.indent(tabs)).append("\"").append(entityName).append("\": {\n");
			
			List<String> entityIssues = issues.get(entity);
			if (entityIssues != null && entityIssues.size() > 0) {
				StringBuilder issueBuffer = new StringBuilder();
				for (String issue : entityIssues) {
					if (issueBuffer.length() > 0) {
						buffer.append("\n");
					}
					issueBuffer.append(issue);
				}
				buffer.append(this.indent(++tabs)).append("\"description\": \"").append(issueBuffer).append("\",\n");
			} else {
				++tabs;
			}
			
			buffer.append(this.indent(tabs)).append("\"type\": \"object\",\n");
			buffer.append(this.indent(tabs)).append("\"properties\": {\n");
			buffer.append(this.makeProperties(++tabs, entity)).append("\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("},\n");

			// Create an array of Entities
			buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-Array").append("\": {\n");
			buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
			buffer.append(this.indent(tabs)).append("\"items\": {\n");
			buffer.append(this.indent(++tabs)).append("\"$ref\": \"#/components/schemas/").append(entityName).append("\"\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("},");

			if (hasShallowRelations || hasDeepRelations) {
				buffer.append("\n");

				// Create a deep Entity containing attributes and related Entity(s)
				buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-Deep\": {\n");
				buffer.append(this.indent(++tabs)).append("\"type\": \"object\",\n");
				buffer.append(this.indent(tabs)).append("\"properties\": {\n");
				buffer.append(this.makeRelations(++tabs, entity)).append("\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("},\n");

				// Create an array of Deep Entities
				buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-DeepArray").append("\": {\n");
				buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
				buffer.append(this.indent(tabs)).append("\"items\": {\n");
				buffer.append(this.indent(++tabs)).append("\"$ref\": \"#/components/schemas/").append(entityName).append("-Deep\"\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("},");
			}

			if (subtypes.size() > 0) {
				buffer.append("\n");

				// Create a Schema consisting of any possible subtype Schemas.
				buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-Subtypes\": {\n");
				buffer.append(this.indent(++tabs)).append("\"anyOf\": [\n");
				++tabs;

				boolean firstSubtype = true;
				for (Entity subtype : subtypes) {
					String subtypeName = subtype.getName();

					if (!firstSubtype) {
						buffer.append(",\n");
					}
					buffer.append(this.indent(tabs)).append("\"$ref\": \"#/components/schemas/").append(subtypeName).append("\"");
					firstSubtype = false;
				}

				buffer.append("\n");
				buffer.append(this.indent(--tabs)).append("]\n");
				buffer.append(this.indent(--tabs)).append("},\n");

				// Create an array of subtype Schemas
				buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-SubtypesArray").append("\": {\n");
				buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
				buffer.append(this.indent(tabs)).append("\"items\": {\n");
				buffer.append(this.indent(++tabs)).append("\"$ref\": \"#/components/schemas/").append(entityName).append("-Subtypes\"\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("},");
			}
			firstEntity = false;
		}
		return buffer.toString();
	}

	private String makeProperties(int tabs, Entity entity) {
		StringBuilder buffer = new StringBuilder();

		Entity superType = entity.getSupertype();
		if (superType != null) {
			buffer.append(this.makeProperties(tabs, superType));
		}

		boolean firstAttribute = buffer.length() == 0;
		List<Attribute> attributes = entity.getAttributes();
		for (Attribute attribute : attributes) {
			String propertyText = this.makeProperty(tabs, attribute);

			if (!firstAttribute) {
				buffer.append(",\n");
			}
			buffer.append(propertyText);
			firstAttribute = false;
		}
		return buffer.toString();
	}

	private String makeProperty(int tabs, Attribute attribute) {
		String name = StringUtil.toCamelCase(attribute.getName());
		String type = attribute.getType();
		boolean readOnly = attribute.isReadOnly();
		String defaultValue = attribute.getDefaultValue();

		StringBuilder buffer = new StringBuilder();
		buffer.append(this.indent(tabs)).append("\"").append(name).append("\": {\n");
		buffer.append(this.indent(++tabs)).append("\"type\": \"").append(type).append("\"");
		if (readOnly) {
			buffer.append(",\n");
			buffer.append(this.indent(tabs)).append("\"readOnly\": true");
		}
		if (!StringUtil.isEmpty(defaultValue)) {
			buffer.append(",\n");
			buffer.append(this.indent(tabs)).append("\"example\": \"").append(defaultValue).append("\"");
		}
		buffer.append("\n");
		buffer.append(this.indent(--tabs)).append("}");
		return buffer.toString();
	}

	private String makeRelations(int tabs, Entity entity) {
		StringBuilder buffer = new StringBuilder();

		Entity superType = entity.getSupertype();
		if (superType != null) {
			buffer.append(this.makeProperties(tabs, superType));
		}

		boolean firstAttribute = buffer.length() == 0;
		List<Attribute> attributes = entity.getAttributes();
		for (Attribute attribute : attributes) {
			String propertyText = this.makeProperty(tabs, attribute);

			if (!firstAttribute) {
				buffer.append(",\n");
			}
			buffer.append(propertyText);
			firstAttribute = false;
		}

		List<MVA> mvaList = entity.getRelations();
		for (MVA relation : mvaList) {
			String relationText = this.makeRelation(tabs, relation);

			if (StringUtil.isEmpty(relationText)) {
				continue;
			}

			if (!firstAttribute) {
				buffer.append(",\n");
			}
			buffer.append(relationText);
			firstAttribute = false;
		}
		return buffer.toString();
	}

	private String makeRelation(int tabs, MVA relation) {
		String name = StringUtil.toCamelCase(relation.getName());
		Entity targetEntity = relation.getTargetEntity();
		Attribute targetIdAttribute = targetEntity.getExplicitId();
		String targetIdType = targetIdAttribute == null ? "string" : targetIdAttribute.getType();
		boolean isSingleValued = "1".equalsIgnoreCase(relation.getCardinality());
		TRelationDepth relationDepth = relation.getRelationDepth();
		boolean hasShallowRelations = relationDepth == TRelationDepth.LINK;
		boolean hasDeepRelations = relationDepth == TRelationDepth.EMBED || relationDepth == TRelationDepth.EMBED_ALL;

		StringBuilder buffer = new StringBuilder();
		if (hasShallowRelations) {
			buffer.append(this.indent(tabs)).append("\"").append(name).append("\": {\n");
			if (isSingleValued) {
				buffer.append(this.indent(++tabs)).append("\"type\": \"").append(targetIdType).append("\"\n");
			} else {
				buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
				buffer.append(this.indent(tabs)).append("\"items\": {\n");
				buffer.append(this.indent(++tabs)).append("\"type\": \"").append(targetIdType).append("\"\n");
				buffer.append(this.indent(--tabs)).append("}\n");
			}
			buffer.append(this.indent(--tabs)).append("}");
		}
		if (hasDeepRelations) {
			buffer.append(this.indent(tabs)).append("\"").append(name).append("\": {\n");
			if (isSingleValued) {
				buffer.append(this.indent(++tabs)).append("\"type\": \"object\",\n");
				buffer.append(this.indent(tabs)).append("\"properties\": {\n");

				if (relationDepth == TRelationDepth.EMBED) {
					buffer.append(this.makeProperties(tabs + 1, targetEntity));
				} else if (relationDepth == TRelationDepth.EMBED_ALL) {
					buffer.append(this.makeRelations(tabs + 1, targetEntity));
				}
				buffer.append("\n");

				buffer.append(this.indent(tabs)).append("}\n");
			} else {
				buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
				buffer.append(this.indent(tabs)).append("\"items\": {\n");
				buffer.append(this.indent(++tabs)).append("\"properties\": {\n");

				if (relationDepth == TRelationDepth.EMBED) {
					buffer.append(this.makeProperties(tabs + 1, targetEntity));
				} else if (relationDepth == TRelationDepth.EMBED_ALL) {
					buffer.append(this.makeRelations(tabs + 1, targetEntity));
				}
				buffer.append("\n");

				buffer.append(this.indent(tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("}\n");
			}
			buffer.append(this.indent(--tabs)).append("}");
		}
		return buffer.toString();
	}

	private String makePaths(int tabs, API api, boolean makePOST, boolean makeGET, boolean makePUT, boolean makePATCH, boolean makeDELETE, boolean makeSEARCH, Map<Entity, List<String>> issues) {
		StringBuilder buffer = new StringBuilder();

		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getName();
			Attribute entityId = entity.getExplicitId();
			boolean isEmbedded = entity.isEmbedded();

			// Don't make endpoints for embedded Entities.
			if (isEmbedded) {
				continue;
			}

			if (makeSEARCH && (buffer.length() > 0)) {
				buffer.append(",\n");
			}
			// TODO Implement

			if (makePOST || makeGET) {
				StringBuilder endpointBuffer = new StringBuilder();
				if (makePOST) {
					endpointBuffer.append(this.makePost(entity));
				}
				if (makeGET) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makeGetAll(entity));
				}

				if (buffer.length() > 0) {
					buffer.append(",\n");
				}
				buffer.append(this.indent(tabs)).append("\"/").append(entityName).append("\" : {\n");
				buffer.append(endpointBuffer).append("\n");
				buffer.append(this.indent(tabs)).append("}");
			}

			if (entityId == null && (makeGET || makePUT || makePATCH || makeDELETE)) {
				this.addIssue(issues, entity, "One or more endpoints were not generated due to the resource not defining an ID attribute");

			} else {
				String entityIdName = entityId.getName();

				StringBuilder endpointBuffer = new StringBuilder();
				if (makeGET) {
					endpointBuffer.append(this.makeGetOne(entity));
				}

				if (makePUT) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makePut(entity));
				}

				if (makePATCH) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makePatch(entity));
				}

				if (makeDELETE) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makeDelete(entity));
				}

				if (endpointBuffer.length() > 0) {
					if (buffer.length() > 0) {
						buffer.append(",\n");
					}
					buffer.append(this.indent(tabs)).append("\"/").append(entityName).append("/{").append(entityIdName).append("}\" : {\n");
					buffer.append(endpointBuffer);
					buffer.append(this.indent(tabs)).append("}");
				}
			}
		}
		return buffer.toString();

	}

	private String makePost(Entity entity) {
		String entityName = entity.getName();
		String entityType = entity.getType();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource("/swagger/pathPOST.part");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		resourceText = resourceText.replace(Swagger.MARKER_TAG, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
		return resourceText.replace(Swagger.MARKER_ENTITY_TYPE, entityType);
	}

	private String makeGetAll(Entity entity) {
		String entityName = entity.getName();
		String entityType = entity.getType();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource("/swagger/pathGETAll.part");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		resourceText = resourceText.replace(Swagger.MARKER_TAG, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
		return resourceText.replace(Swagger.MARKER_ENTITY_TYPE, entityType);
	}

	private String makeGetOne(Entity entity) {
		String entityName = entity.getName();
		String entityType = entity.getType();
		Attribute entityId = entity.getExplicitId();
		String entityIdName = entityId.getName();
		String entityIdType = entityId.getType();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource("/swagger/pathGETOne.part");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		resourceText = resourceText.replace(Swagger.MARKER_TAG, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_TYPE, entityType);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID, entityIdName);
		return resourceText.replace(Swagger.MARKER_ENTITY_ID_TYPE, entityIdType);
	}

	private String makePut(Entity entity) {
		String entityName = entity.getName();
		String entityType = entity.getType();
		Attribute entityId = entity.getExplicitId();
		String entityIdName = entityId.getName();
		String entityIdType = entityId.getType();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource("/swagger/pathPUT.part");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		resourceText = resourceText.replace(Swagger.MARKER_TAG, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_TYPE, entityType);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID, entityIdName);
		return resourceText.replace(Swagger.MARKER_ENTITY_ID_TYPE, entityIdType);
	}

	private String makePatch(Entity entity) {
		String entityName = entity.getName();
		String entityType = entity.getType();
		Attribute entityId = entity.getExplicitId();
		String entityIdName = entityId.getName();
		String entityIdType = entityId.getType();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource("/swagger/pathPATCH.part");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		resourceText = resourceText.replace(Swagger.MARKER_TAG, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_TYPE, entityType);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID, entityIdName);
		return resourceText.replace(Swagger.MARKER_ENTITY_ID_TYPE, entityIdType);
	}

	private String makeDelete(Entity entity) {
		String entityName = entity.getName();
		String entityType = entity.getType();
		Attribute entityId = entity.getExplicitId();
		String entityIdName = entityId.getName();
		String entityIdType = entityId.getType();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource("/swagger/pathDELETE.part");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		resourceText = resourceText.replace(Swagger.MARKER_TAG, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_TYPE, entityType);
		resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID, entityIdName);
		return resourceText.replace(Swagger.MARKER_ENTITY_ID_TYPE, entityIdType);
	}

	private void addIssue(Map<Entity, List<String>> issues, Entity entity, String message) {
		List<String> issueList = issues.get(entity);
		if (issueList == null) {
			issueList = new ArrayList<>();
			issues.put(entity, issueList);
		}
		issueList.add(message);
	}

}
