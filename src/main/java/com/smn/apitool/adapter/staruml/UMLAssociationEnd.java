package com.smn.apitool.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLAssociationEnd {

	@JsonProperty
	private String _id;

	@JsonProperty
	private Reference _parent;

	@JsonProperty
	private Reference reference;

	@JsonProperty
	private String aggregation;

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

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public String getAggregation() {
		return aggregation;
	}

	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}

}
