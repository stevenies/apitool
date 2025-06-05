package com.smn.apitool.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLAssociation extends OwnedElement {

	@JsonProperty
	private String _id;

	@JsonProperty
	private Reference _parent;

	@JsonProperty
	private UMLAssociationEnd end1;

	@JsonProperty
	private UMLAssociationEnd end2;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Reference get_parent() {
		return _parent;
	}

	public void set_parent(Reference _parent) {
		this._parent = _parent;
	}

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
