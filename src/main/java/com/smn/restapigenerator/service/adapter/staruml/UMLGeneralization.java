package com.smn.restapigenerator.service.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLGeneralization extends OwnedElement {

	@JsonProperty
	private Reference source;

	@JsonProperty
	private Reference target;

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
