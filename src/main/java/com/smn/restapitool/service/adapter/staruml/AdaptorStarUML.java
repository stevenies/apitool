package com.smn.restapitool.service.adapter.staruml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.restapitool.model.Attribute;
import com.smn.restapitool.model.Entity;
import com.smn.restapitool.model.MVA;
import com.smn.restapitool.model.MVA.TRelationDepth;
import com.smn.restapitool.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AdaptorStarUML {

	public static class DtoReadUMLFile {

		private List<Entity> entities = new ArrayList<>();
		private String error;
		private Map<Entity, List<String>> issues = new HashMap<>();

		public DtoReadUMLFile(String error) {
			this.error = error;
		}

		public DtoReadUMLFile(Map<Entity, List<String>> issues) {
			this.issues.putAll(issues);
		}

		public DtoReadUMLFile(List<Entity> entities, Map<Entity, List<String>> issues) {
			this.entities = entities;
			this.issues.putAll(issues);
		}

		public List<Entity> getEntities() {
			return this.entities;
		}

		public String getError() {
			return this.error;
		}

		public Map<Entity, List<String>> getIssues() {
			return this.issues;
		}

	}

	public DtoReadUMLFile readUMLFile(byte[] fileContent) {
		Map<Entity, List<String>> issues = new HashMap<>();
		try {
			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Project infoModel = mapper.readValue(new String(fileContent), Project.class);

			Map<String, Entity> classMap = new HashMap<>();

			List<UMLModel> models = infoModel.getOwnedElements();
			for (UMLModel model : models) {
				List<OwnedElement> elementList = model.getOwnedElements();
				for (OwnedElement element : elementList) {

					// Ignore elements that are not Class definitions
					if (!(element instanceof UMLClass)) {
						continue;
					}

					// Define a new class Entity
					UMLClass umlClass = (UMLClass) element;
					String className = umlClass.getName();
					Entity entity = new Entity(className);
					String classId = umlClass.get_id();
					classMap.put(classId, entity);

					// Attach the class's attributes
					List<UMLAttribute> umlAttributes = umlClass.getAttributes();
					if (umlAttributes != null) {
						for (UMLAttribute umlAttribute : umlAttributes) {

							String name = umlAttribute.getName();
							String type = umlAttribute.getType();
							String defaultValue = umlAttribute.getDefaultValue();
							boolean isId = umlAttribute.isID();
							boolean isReadOnly = umlAttribute.isReadOnly();

							Attribute attribute = new Attribute(name, type, defaultValue, isId, isReadOnly);
							entity.addAttribute(attribute, isId);

							// Verify that the attribute's type is a type supported by OpenAPI
							if ("string".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
								type = type.toLowerCase();
							} else {
								String message = "Type '" + type + "' (attribute '" + name + "') is not supported by OpenAPI";
								this.addIssue(issues, entity, message);
							}
						}
					}
				}
			}
			for (UMLModel model : models) {
				List<OwnedElement> elementList = model.getOwnedElements();
				for (OwnedElement element : elementList) {

					// Ignore elements that are not Class definitions
					if (!(element instanceof UMLClass)) {
						continue;
					}

					// Process the class's relations to other classes
					UMLClass umlClass = (UMLClass) element;
					List<OwnedElement> umlRelations = umlClass.getOwnedElements();
					if (umlRelations != null) {
						for (OwnedElement umlElement : umlRelations) {

							if (umlElement instanceof UMLGeneralization umlGeneralization) {

								// Link the class to its superclass
								Reference source = umlGeneralization.getSource();
								Entity sourceEntity = classMap.get(source.get$ref());

								Reference target = umlGeneralization.getTarget();
								Entity targetEntity = classMap.get(target.get$ref());

								sourceEntity.setSupertype(targetEntity);

							} else if (umlElement instanceof UMLAssociation umlAssociation) {

								// Define Multi-Valued Attributes representing the class's relations to other classes
								UMLAssociationEnd umlEnd1 = umlAssociation.getEnd1();
								Entity end1Entity = classMap.get(umlEnd1.getReference().get$ref());
								String end1Name = umlEnd1.getName();
								String end1Multiplicity = umlEnd1.getMultiplicity();
								boolean end1Navigable = umlEnd1.isNavigable();
								boolean end1Composite = umlEnd1.isComposite();
								String end1Stereotype = umlEnd1.getStereotype();
								boolean end1MakeEndpoint = StringUtil.isEmpty(end1Stereotype)? false : end1Stereotype.toLowerCase().contains("endpoint");

								UMLAssociationEnd umlEnd2 = umlAssociation.getEnd2();
								Entity end2Entity = classMap.get(umlEnd2.getReference().get$ref());
								String end2Name = umlEnd2.getName();
								String end2Multiplicity = umlEnd2.getMultiplicity();
								boolean end2Navigable = umlEnd2.isNavigable();
								boolean end2Composite = umlEnd2.isComposite();
								String end2Stereotype = umlEnd2.getStereotype();
								boolean end2MakeEndpoint = StringUtil.isEmpty(end2Stereotype)? false : end2Stereotype.toLowerCase().contains("endpoint");

								TRelationDepth end1RelationDepth = end2Composite ? TRelationDepth.EMBED_ALL //
									: StringUtil.isEmpty(end1Stereotype) ? TRelationDepth.NONE //
									: end1Stereotype.contains("embedall") ? TRelationDepth.EMBED_ALL //
									: end1Stereotype.contains("embed") ? TRelationDepth.EMBED //
									: end1Stereotype.contains("link") ? TRelationDepth.LINK : TRelationDepth.NONE;
								TRelationDepth end2RelationDepth = end1Composite ? TRelationDepth.EMBED_ALL //
									: StringUtil.isEmpty(end2Stereotype) ? TRelationDepth.NONE //
									: end2Stereotype.contains("embedall") ? TRelationDepth.EMBED_ALL //
									: end2Stereotype.contains("embed") ? TRelationDepth.EMBED //
									: end2Stereotype.contains("link") ? TRelationDepth.LINK : TRelationDepth.NONE;

								if (end2Composite) {
									end1Entity.setEmbedded(true);
								}
								if (end2Navigable) {
									MVA mva1 = new MVA(end2Entity, end2Name, end2Multiplicity, end2RelationDepth);
									mva1.setMakeEndpoint(end2MakeEndpoint);
									end1Entity.addRelation(mva1);
								}

								if (end1Composite) {
									end2Entity.setEmbedded(true);
								}
								if (end1Navigable) {
									MVA mva2 = new MVA(end1Entity, end1Name, end1Multiplicity, end1RelationDepth);
									mva2.setMakeEndpoint(end1MakeEndpoint);
									end2Entity.addRelation(mva2);
								}
							}
						}
					}
				}
			}

			ArrayList<Entity> entityList = new ArrayList<>(classMap.values());
			for (Entity entity : entityList) {
				System.out.println(entity);
			}

			return new DtoReadUMLFile(entityList, issues);

		} catch (Throwable t) {
			t.printStackTrace();

			String error = t.getMessage();
			return new DtoReadUMLFile(error);
		}
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
