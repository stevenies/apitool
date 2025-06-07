package com.smn.apitool.service.adapter.staruml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smn.apitool.model.Attribute;
import com.smn.apitool.model.Entity;
import com.smn.apitool.model.MVA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AdaptorStarUML {

	public class DtoReadUMLFile {

		private List<Entity> entities = new ArrayList<>();
		private List<String> errors = new ArrayList<>();

		public DtoReadUMLFile(String error) {
			this.errors.add(error);
		}

		public DtoReadUMLFile(List<String> errors) {
			this.errors.addAll(errors);
		}

		public DtoReadUMLFile(List<Entity> entities, List<String> errors) {
			this.entities = entities;
			this.errors.addAll(errors);
		}

		public List<Entity> getEntities() {
			return entities;
		}

		public boolean hasErrors() {
			return errors != null && errors.size() > 0;
		}

		public List<String> getErrors() {
			return errors;
		}

	}

	public DtoReadUMLFile readUMLFile(byte[] fileContent) {
		List<String> errors = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Project infoModel = mapper.readValue(new String(fileContent), Project.class);

			Map<String, Entity> classMap = new HashMap<>();

			List<UMLModel> models = infoModel.getOwnedElements();
			for (UMLModel model : models) {
				List<OwnedElement> elementList = model.getOwnedElements();
				for (OwnedElement element : elementList) {
					if (!(element instanceof UMLClass)) {
						continue;
					}

					UMLClass umlClass = (UMLClass) element;
					String className = umlClass.getName();
					Entity entity = new Entity(className);

					String classId = umlClass.get_id();
					classMap.put(classId, entity);

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

							// TODO Verify type is a supported OpenAPI type
						}
					}
				}
			}
			for (UMLModel model : models) {
				List<OwnedElement> elementList = model.getOwnedElements();
				for (OwnedElement element : elementList) {
					if (!(element instanceof UMLClass)) {
						continue;
					}

					UMLClass umlClass = (UMLClass) element;
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
								Entity end1Entity = classMap.get(umlEnd1.getReference().get$ref());
								String end1Name = umlEnd1.getName();
								String end1Multiplicity = umlEnd1.getMultiplicity();
								boolean end1Navigable = umlEnd1.isNavigable();

								UMLAssociationEnd umlEnd2 = umlAssociation.getEnd2();
								Entity end2Entity = classMap.get(umlEnd2.getReference().get$ref());
								String end2Name = umlEnd2.getName();
								String end2Multiplicity = umlEnd2.getMultiplicity();
								boolean end2Navigable = umlEnd1.isNavigable();

								if (end2Navigable) {
									MVA mva1 = new MVA(end2Entity, end2Name, end2Multiplicity);
									end1Entity.addRelation(mva1);
								}

								if (end1Navigable) {
									MVA mva2 = new MVA(end1Entity, end1Name, end1Multiplicity);
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

			DtoReadUMLFile status = new DtoReadUMLFile(entityList, errors);
			return status;

		} catch (Throwable t) {
			t.printStackTrace();

			String error = t.getMessage();
			DtoReadUMLFile status = new DtoReadUMLFile(error);
			return status;
		}
	}
}
