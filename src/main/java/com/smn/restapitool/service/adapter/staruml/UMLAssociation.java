package com.smn.restapitool.service.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLAssociation extends OwnedElement {

	@JsonProperty
	private UMLAssociationEnd end1;

	@JsonProperty
	private UMLAssociationEnd end2;

	public UMLAssociationEnd getEnd1() {
		return end1;
	}

	public void setEnd1(UMLAssociationEnd end1) {
		this.end1 = end1;
	}

	public UMLAssociationEnd getEnd2() {
		return end2;
	}

	public void setEnd2(UMLAssociationEnd end2) {
		this.end2 = end2;
	}

}
