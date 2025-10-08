package com.smn.restapigenerator.service.swagger;

import com.smn.restapigenerator.model.uml.Attribute;
import com.smn.restapigenerator.model.uml.DomainModel;
import com.smn.restapigenerator.model.uml.Entity;
import com.smn.restapigenerator.model.uml.MVA;
import com.smn.restapigenerator.model.uml.MVA.TRelationDepth;
import com.smn.restapigenerator.util.FileUtil;
import com.smn.restapigenerator.util.StringUtil;
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
	final static String MARKER_OPERATION_ID = ">>>operationId";
	final static String MARKER_DESCRIPTION = ">>>description";
	final static String MARKER_VERSION = ">>>version";
	final static String MARKER_SERVERS = "\">>>servers\": \"\"";
	final static String MARKER_TAGS = "{\">>>entityTags\": \"\"}";
	final static String MARKER_PATHS = "\">>>paths\": \"\"";
	final static String MARKER_SCHEMAS = "\">>>schemas\": \"\",";
	final static String MARKER_ENTITY_TAG = ">>>entityTag";
	final static String MARKER_ENTITY_NAME = ">>>entityName";
	final static String MARKER_ENTITY_ID = ">>>entityId";
	final static String MARKER_ENTITY_ID_TYPE = ">>>typeEntityId";
	final static String MARKER_TARGET_NAME = ">>>targetName";
	final static String MARKER_RESPONSE_SCHEMA = ">>>responseSchema";

	public String generate(
			DomainModel api,
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
		try (InputStream inputStream = resource.getInputStream();
				OutputStream outputStream = new ByteArrayOutputStream()) {

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

		String paths = this.makePaths(
			tabs++, api, makePOST, makeGET, makePUT, makePATCH, makeDELETE, makeSEARCH, issues);
		swagger = swagger.replace(Swagger.MARKER_PATHS, paths);

		String schemas = this.makeSchema(tabs--, api, issues);
		swagger = swagger.replace(Swagger.MARKER_SCHEMAS, schemas);

		// The FOSS component used by the API code generator cannot accept tabs.  Thus convert tabs to spaces.
		swagger = swagger.replace("\t", "  ");
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
		buffer.append(this.indent(tabs)).append("\"url\": \"https://").append(serverDomain).append("/")
				.append(contextRoot).append("\"");
		return buffer.toString();
	}

	private String makeTags(int tabs, DomainModel api) {
		StringBuilder buffer = new StringBuilder();
		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getNamePascalCase();
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

	private String makeSchema(int tabs, DomainModel api, Map<Entity, List<String>> issues) {
		StringBuilder buffer = new StringBuilder();

		boolean firstEntity = true;
		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getNamePascalCase();
			List<Entity> subtypes = entity.getSubtypes();

			if (!firstEntity) {
				buffer.append("\n");
			}

			// Determine if there are any issues for this entity
			StringBuilder issueBuffer = new StringBuilder();
			List<String> entityIssues = issues.get(entity);
			if (entityIssues != null && entityIssues.size() > 0) {
				for (String issue : entityIssues) {
					if (issueBuffer.length() > 0) {
						buffer.append("\n");
					}
					issueBuffer.append(issue);
				}
			}

			// Create a Entity containing attributes and relationships
			buffer.append(this.indent(tabs)).append("\"").append(entityName).append("\": {\n");
			buffer.append(this.indent(++tabs)).append("\"type\": \"object\",\n");
			buffer.append(this.indent(tabs)).append("\"properties\": {\n");
			buffer.append(this.makeRelations(++tabs, entity)).append("\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("},\n");

			// Create an Entity containing only fields (no relationships)
			buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-Fields\": {\n");
			buffer.append(this.indent(++tabs)).append("\"description\": \"").append(issueBuffer).append("\",\n");
			buffer.append(this.indent(tabs)).append("\"type\": \"object\",\n");
			buffer.append(this.indent(tabs)).append("\"properties\": {\n");
			buffer.append(this.makeProperties(++tabs, entity)).append("\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("},\n");

			// Create a paged array of Entities
			buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-ArrayPaged").append("\": {\n");
			buffer.append(this.indent(++tabs)).append("\"allOf\": [\n");
			buffer.append(this.indent(++tabs)).append("{\"$ref\": \"#/components/schemas/PageInfo\"},\n");
			buffer.append(this.indent(tabs)).append("{\n");
			buffer.append(this.indent(++tabs)).append("\"type\": \"object\",\n");
			buffer.append(this.indent(tabs)).append("\"properties\": {\n");
			buffer.append(this.indent(++tabs)).append("\"items\": {\n");
			buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
			buffer.append(this.indent(tabs)).append("\"items\": {\n");
			buffer.append(this.indent(++tabs)).append("\"$ref\": \"#/components/schemas/").append(entityName).append("\"\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("}\n");
			buffer.append(this.indent(--tabs)).append("]\n");
			buffer.append(this.indent(--tabs)).append("},");

			if (subtypes.size() > 0) {
				buffer.append("\n");

				// Create a Schema consisting of any possible subtype Schemas.
				buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-Subtypes\": {\n");
				buffer.append(this.indent(++tabs)).append("\"anyOf\": [\n");
				++tabs;

				boolean firstSubtype = true;
				for (Entity subtype : subtypes) {
					String subtypeName = subtype.getNamePascalCase();

					if (!firstSubtype) {
						buffer.append(",\n");
					}
					buffer.append(this.indent(tabs));
					buffer.append("{\"$ref\": \"#/components/schemas/").append(subtypeName).append("\"}");
					firstSubtype = false;
				}

				buffer.append("\n");
				buffer.append(this.indent(--tabs)).append("]\n");
				buffer.append(this.indent(--tabs)).append("},\n");

				// Create a paged array of subtype Schemas
				buffer.append(this.indent(tabs)).append("\"").append(entityName).append("-SubtypesArrayPaged").append("\": {\n");
				buffer.append(this.indent(++tabs)).append("\"allOf\": [\n");
				buffer.append(this.indent(++tabs)).append("{\"$ref\": \"#/components/schemas/PageInfo\"},\n");
				buffer.append(this.indent(tabs)).append("{\n");
				buffer.append(this.indent(++tabs)).append("\"type\": \"object\",\n");
				buffer.append(this.indent(tabs)).append("\"properties\": {\n");
				buffer.append(this.indent(++tabs)).append("\"items\": {\n");
				buffer.append(this.indent(++tabs)).append("\"type\": \"array\",\n");
				buffer.append(this.indent(tabs)).append("\"items\": {\n");
				buffer.append(this.indent(++tabs)).append("\"$ref\": \"#/components/schemas/").append(entityName).append("-Subtypes\"\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("}\n");
				buffer.append(this.indent(--tabs)).append("]\n");
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
		String name = attribute.getNameCamelCase();
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
		String name = relation.getNameCamelCase();
		Entity targetEntity = relation.getTargetEntity();
		Attribute targetIdAttribute = targetEntity.getExplicitId();
		String targetIdType = targetIdAttribute == null ? "string" : targetIdAttribute.getType();
		boolean isSingleValued = "1".equalsIgnoreCase(relation.getCardinality());
		TRelationDepth relationDepth = relation.getRelationDepth();
		boolean hasShallowRelations = relationDepth == TRelationDepth.LINK;
		boolean hasDeepRelations = relationDepth == TRelationDepth.EMBED || relationDepth == TRelationDepth.EMBEDALL;

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
				} else if (relationDepth == TRelationDepth.EMBEDALL) {
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
				} else if (relationDepth == TRelationDepth.EMBEDALL) {
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

	private String makePaths(
		int tabs,
		DomainModel api,
		boolean makePOST,
		boolean makeGET,
		boolean makePUT,
		boolean makePATCH,
		boolean makeDELETE,
		boolean makeSEARCH,
		Map<Entity,
		List<String>> issues) {
			
		StringBuilder buffer = new StringBuilder();

		List<Entity> entities = api.getEntities();
		for (Entity entity : entities) {
			String entityName = entity.getNameKebabCase();
			Attribute entityId = entity.getExplicitId();
			boolean isEmbedded = entity.isEmbedded();

			// TODO Post-process an entity to determine if it is reachable only via relationships with embed stereotype.
			// If so, then do not create endpoints for it.
			if (isEmbedded) {
				continue;
			}

			if (makeSEARCH) {
				String operationId = entityName + "-search";

				if (buffer.length() > 0) {
					buffer.append(",\n");
				}
				buffer.append(this.indent(tabs)).append("\"/").append(entityName).append("-$search\" : {\n");
				buffer.append(this.makeEndpoint(entity, "/swagger/pathSEARCH_POST.part", operationId)).append("\n");
				buffer.append(this.indent(tabs)).append("},\n");
				buffer.append(this.indent(tabs)).append("\"/").append(entityName).append("-$search/{search-id}\" : {\n");
				buffer.append(this.makeEndpoint(entity, "/swagger/pathSEARCH_GET.part", operationId)).append("\n");
				buffer.append(this.indent(tabs)).append("}");
			}

			if (makePOST || makeGET) {
				String operationId = entityName;

				StringBuilder endpointBuffer = new StringBuilder();
				if (makePOST) {
					if (!entity.isSupertype()) {
						endpointBuffer.append(this.makeEndpoint(entity, "/swagger/pathPOST.part", operationId));
					}
				}
				if (makeGET) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makeEndpoint(entity, "/swagger/pathGETAll.part", operationId));
				}

				if (buffer.length() > 0) {
					buffer.append(",\n");
				}
				buffer.append(this.indent(tabs)).append("\"/").append(entityName).append("\" : {\n");
				buffer.append(endpointBuffer).append("\n");
				buffer.append(this.indent(tabs)).append("}");
			}

			if (entityId == null) {
				if (makeGET || makePUT || makePATCH || makeDELETE) {
					this.addIssue(issues, entity,
							"One or more endpoints were not generated due to the resource not defining an ID attribute");
				}

			} else {
				String entityIdName = entityId.getNameKebabCase();
				List<MVA> relations = entity.getRelations();
				String operationId = entityName;

				StringBuilder endpointBuffer = new StringBuilder();
				if (makeGET) {
					endpointBuffer.append(this.makeEndpoint(entity, "/swagger/pathGETOne.part", operationId));
				}

				if (makePUT) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					String endpointText = this.makeEndpoint(entity, "/swagger/pathPUT.part", operationId);
					endpointBuffer.append(endpointText);
				}

				if (makePATCH) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makeEndpoint(entity, "/swagger/pathPATCH.part", operationId));
				}

				if (makeDELETE) {
					if (endpointBuffer.length() > 0) {
						endpointBuffer.append(",\n");
					}
					endpointBuffer.append(this.makeEndpoint(entity, "/swagger/pathDELETE.part", operationId));
				}

				if (endpointBuffer.length() > 0) {
					if (buffer.length() > 0) {
						buffer.append(",\n");
					}
					buffer.append(this.indent(tabs));
					buffer.append("\"/").append(entityName).append("/{").append(entityIdName).append("}\" : {\n");
					buffer.append(endpointBuffer).append("\n");
					buffer.append(this.indent(tabs)).append("}");
				}

				if (makeGET) {
					for (MVA relation : relations) {
						boolean needsEndpoint = relation.isMakeEndpoint();
						if (!needsEndpoint) {
							continue;
						}

						String relationName = relation.getNameKebabCase();

						if (buffer.length() > 0) {
							buffer.append(",\n");
						}
						buffer.append(this.indent(tabs));
						buffer.append("\"/").append(entityName).append("/{").append(entityIdName).append("}/").append(relationName).append("\" : {\n");
						buffer.append(this.makeRelationEndpoint(entity, "/swagger/pathGETRelated.part", operationId + "-" + relationName, relation)).append("\n");
						buffer.append(this.indent(tabs)).append("}");
					}
				}
			}
		}
		return buffer.toString();

	}

	private String makeEndpoint(Entity entity, String partFileURI, String operationId) {
		String entityTag = entity.getNamePascalCase();
		String entityName = entity.getNamePascalCase();
		boolean isSupertype = entity.isSupertype();

		Attribute entityId = entity.getExplicitId();
		String entityIdName = entityId == null ? "" : entityId.getNameKebabCase();
		String entityIdType = entityId == null ? "" : entityId.getType();

		String responseSchema = entityName + (isSupertype ? "-SubtypesArrayPaged" : "-ArrayPaged");

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource(partFileURI);

			resourceText = resourceText.replace(Swagger.MARKER_OPERATION_ID, operationId);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_TAG, entityTag);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_NAME, entityName);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID, entityIdName);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID_TYPE, entityIdType);
			resourceText = resourceText.replace(Swagger.MARKER_RESPONSE_SCHEMA, responseSchema);

		} catch (IOException e) {
			// TODO Display error message in UI
			System.err.println(e.getMessage());
		}
		return resourceText;
	}

	private String makeRelationEndpoint(Entity entity, String partFileURI, String operationId, MVA relation) {
		String entityTag = entity.getNamePascalCase();

		Attribute entityId = entity.getExplicitId();
		String entityIdName = entityId.getNameKebabCase();
		String entityIdType = entityId.getType();

		Entity targetEntity = relation.getTargetEntity();
		String targetEntityName = targetEntity.getNamePascalCase();

		String resourceText = "";
		try {
			resourceText = FileUtil.readResource(partFileURI);

			resourceText = resourceText.replace(Swagger.MARKER_OPERATION_ID, operationId);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_TAG, entityTag);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID, entityIdName);
			resourceText = resourceText.replace(Swagger.MARKER_ENTITY_ID_TYPE, entityIdType);
			resourceText = resourceText.replace(Swagger.MARKER_TARGET_NAME, targetEntityName);

		} catch (IOException e) {
			System.err.println(e.getMessage());
			// TODO Display error message in UI
		}
		return resourceText;
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
