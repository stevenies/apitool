package com.smn.restapigenerator.service.swagger.staruml;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "UMLClassDiagram", value = UMLClassDiagram.class),
	@JsonSubTypes.Type(name = "UMLClass", value = UMLClass.class),
	@JsonSubTypes.Type(name = "UMLAssociation", value = UMLAssociation.class),
	@JsonSubTypes.Type(name = "UMLGeneralization", value = UMLGeneralization.class)
})
public class OwnedElement {

}
