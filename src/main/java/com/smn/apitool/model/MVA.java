package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

/**
 * Represents a Multi-Value Attribute (aka a Relation between two classes)
 */
public class MVA {

	private Entity targetEntity;
	private String name;
	private String cardinality;
	private boolean targetIsAggregation;
	private boolean targetIsComposite;
	
	public MVA(Entity targetEntity, String name, String cardinality) {
		String targetEntityName = targetEntity.getName();
		
		if (StringUtil.isEmpty(name)) {
			name = targetEntityName;  // TODO convert to camelcase
		} else {
			name = name.replace(" ", "_");
		}
		if (StringUtil.isEmpty(cardinality)) {
			cardinality = "1";
		}
		
		this.targetEntity = targetEntity;
		this.name = name;
		this.cardinality = cardinality;
	}

	public Entity getTargetEntity() {
		return targetEntity;
	}

	public String getName() {
		return name;
	}

	public String getCardinality() {
		return cardinality;
	}

	public boolean isTargetIsAggregation() {
		return targetIsAggregation;
	}

	public void setTargetIsAggregation(boolean targetIsAggregation) {
		this.targetIsAggregation = targetIsAggregation;
	}

	public boolean isTargetIsComposite() {
		return targetIsComposite;
	}

	public void setTargetIsComposite(boolean targetIsComposite) {
		this.targetIsComposite = targetIsComposite;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		String targetEntityName = targetEntity.getName();
		buffer.append(targetEntityName).append(" ").append(name);
		if (StringUtil.isEmpty(cardinality)) {
			cardinality = "1";
		}
		buffer.append(" (").append(cardinality).append(")\n");

		return buffer.toString();
	}

}
