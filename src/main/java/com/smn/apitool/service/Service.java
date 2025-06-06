package com.smn.apitool.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.apitool.model.Attribute;
import com.smn.apitool.model.Entity;
import com.smn.apitool.service.adapter.staruml.OwnedElement;
import com.smn.apitool.service.adapter.staruml.Project;
import com.smn.apitool.service.adapter.staruml.Reference;
import com.smn.apitool.service.adapter.staruml.UMLAssociation;
import com.smn.apitool.service.adapter.staruml.UMLAssociationEnd;
import com.smn.apitool.service.adapter.staruml.UMLAttribute;
import com.smn.apitool.service.adapter.staruml.UMLClass;
import com.smn.apitool.service.adapter.staruml.UMLGeneralization;
import com.smn.apitool.service.adapter.staruml.UMLModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Service {

	public class DtoReadUMLFile {

		private List<Entity> entities = new ArrayList<>();

		private List<String> errors = new ArrayList<>();

		public DtoReadUMLFile() {
		}

		public DtoReadUMLFile(List<String> errorList) {
			this.errors.addAll(errorList);
		}

		public boolean hasErrors() {
			return errors != null && errors.size() > 0;
		}

		public List<String> getErrors() {
			return errors;
		}

		public List<Entity> getEntities() {
			return entities;
		}

		public void addEntity(Entity entity) {
			this.entities.add(entity);
		}

		public void addEntities(List<Entity> entityList) {
			this.entities.addAll(entityList);
		}

	}

	public DtoReadUMLFile readUMLFile(String filename, String file) {
		try {
			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Project infoModel = mapper.readValue(file, Project.class);

			Map<String, Entity> classMap = new HashMap<>();

			List<UMLModel> models = infoModel.getOwnedElements();
			for (UMLModel model : models) {
				List<UMLClass> classList = model.getOwnedElements();
				for (UMLClass umlClass : classList) {

					String className = umlClass.getName();
					Entity entity = new Entity(className);

					String classId = umlClass.get_id();
					classMap.put(classId, entity);

					List<UMLAttribute> umlAttributes = umlClass.getAttributes();
					if (umlAttributes != null) {
						for (UMLAttribute umlAttribute : umlAttributes) {

							String name = umlAttribute.getName();
							Attribute attribute = new Attribute(name);
							entity.addAttribute(attribute);

							String type = umlAttribute.getType();
							attribute.setType(type);

							boolean isReadOnly = umlAttribute.isReadOnly();
							attribute.setReadOnly(isReadOnly);

							String defaultValue = umlAttribute.getDefaultValue();
							attribute.setDefaultValue(defaultValue);
						}
					}
				}
			}
			for (UMLModel model : models) {
				List<UMLClass> classList = model.getOwnedElements();
				for (UMLClass umlClass : classList) {
					List<OwnedElement> umlRelations = umlClass.getOwnedElements();
					if (umlRelations != null) {
						for (OwnedElement umlElement : umlRelations) {

							if (umlElement instanceof UMLGeneralization) {
								UMLGeneralization umlGeneralization = (UMLGeneralization) umlElement;

								Reference source = umlGeneralization.getSource();
								Entity sourceEntity = classMap.get(source.get$ref());

								Reference target = umlGeneralization.getTarget();
								Entity targetEntity = classMap.get(target.get$ref());

								sourceEntity.setSupertype(targetEntity);

							} else if (umlElement instanceof UMLAssociation) {
								UMLAssociation umlAssociation = (UMLAssociation) umlElement;

								UMLAssociationEnd umlEnd1 = umlAssociation.getEnd1();

								UMLAssociationEnd umlEnd2 = umlAssociation.getEnd2();

							}
						}
					}
				}
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}
