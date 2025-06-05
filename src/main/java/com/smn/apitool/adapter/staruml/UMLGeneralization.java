package com.smn.apitool.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLGeneralization {

	@JsonProperty
	private String _id;

	@JsonProperty
	private Reference _parent;

	@JsonProperty
	private Reference source;

	@JsonProperty
	private Reference target;

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
	public Reference getSource() {
		return source;
	}
	public void setSource(Reference source) {
		this.source = source;
	}
	public Reference getTarget() {
		return target;
	}
	public void setTarget(Reference target) {
		this.target = target;
	}

}
