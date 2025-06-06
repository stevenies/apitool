package com.smn.apitool.service.adapter.staruml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLAssociationEnd {

	@JsonProperty
	private String name;

	@JsonProperty
	private Reference reference;

	@JsonProperty
	private String multiplicity;

	@JsonProperty
	private String aggregation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	public String getAggregation() {
		return aggregation;
	}

	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}

}
