package com.smn.apitool.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.apitool.model.Attribute;
import com.smn.apitool.model.Entity;
import com.smn.apitool.model.MVA;
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

		public DtoReadUMLFile(String error) {
			this.errors.add(error);
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

	public DtoReadUMLFile readUMLFile(String filename, byte[] fileContent) {
		try {
			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Project infoModel = mapper.readValue(new String(fileContent), Project.class);

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
							
							// TODO Verify type is a supported OpenAPI type

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
								String end1Ref = umlEnd1.getReference().get$ref();
								String end1Name = umlEnd1.getName();
								String end1Multiplicity = umlEnd1.getMultiplicity();
								String end1Aggregation = umlEnd1.getAggregation();
								Entity end1Entity = classMap.get(end1Ref);

								UMLAssociationEnd umlEnd2 = umlAssociation.getEnd2();
								String end2Ref = umlEnd2.getReference().get$ref();
								String end2Name = umlEnd2.getName();
								String end2Multiplicity = umlEnd2.getMultiplicity();
								String end2Aggregation = umlEnd2.getAggregation();
								Entity end2Entity = classMap.get(end2Ref);

								MVA mva1 = new MVA(end2Entity, end2Name, end2Multiplicity);
								mva1.setTargetIsAggregation("shared".equalsIgnoreCase(end2Aggregation));
								mva1.setTargetIsComposite("composite".equalsIgnoreCase(end2Aggregation));
								end1Entity.addRelation(mva1);

								MVA mva2 = new MVA(end1Entity, end1Name, end1Multiplicity);
								mva2.setTargetIsAggregation("shared".equalsIgnoreCase(end1Aggregation));
								mva2.setTargetIsComposite("composite".equalsIgnoreCase(end1Aggregation));
								end2Entity.addRelation(mva2);
							}
						}
					}
				}
			}
			
			ArrayList<Entity> entityList = new ArrayList<>(classMap.values());
			for (Entity entity : entityList) {
				System.out.println(entity);
			}
			
			DtoReadUMLFile status = new DtoReadUMLFile();
			status.addEntities(entityList);
			return status;

		} catch (Throwable t) {
			t.printStackTrace();

			String error = t.getMessage();
			DtoReadUMLFile status = new DtoReadUMLFile(error);
			return status;
		}
	}
}
