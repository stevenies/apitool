package com.smn.apitool.model;

import com.smn.apitool.util.StringUtil;

public class Relation {

	private Entity end1Entity;
	private String end1Name;
	private String end1Cardinality;
	private boolean end1IsAggregation;
	private boolean end1IsComposite;

	private Entity end2Entity;
	private String end2Name;
	private String end2Cardinality;
	private boolean end2IsAggregation;
	private boolean end2IsComposite;

	public Entity getEnd1Entity() {
		return end1Entity;
	}

	public void setEnd1Entity(Entity end1Entity) {
		this.end1Entity = end1Entity;
	}

	public String getEnd1Name() {
		return end1Name;
	}

	public void setEnd1Name(String end1Name) {
		this.end1Name = end1Name;
	}

	public String getEnd1Cardinality() {
		return end1Cardinality;
	}

	public void setEnd1Cardinality(String end1Cardinality) {
		this.end1Cardinality = end1Cardinality;
	}

	public boolean isEnd1Aggregation() {
		return end1IsAggregation;
	}

	public void setEnd1Aggregation(boolean end1Aggregation) {
		this.end1IsAggregation = end1Aggregation;
	}

	public boolean isEnd1Composite() {
		return end1IsComposite;
	}

	public void setEnd1Composite(boolean end1Composition) {
		this.end1IsComposite = end1Composition;
	}

	public Entity getEnd2Entity() {
		return end2Entity;
	}

	public void setEnd2Entity(Entity end2Entity) {
		this.end2Entity = end2Entity;
	}

	public String getEnd2Name() {
		return end2Name;
	}

	public void setEnd2Name(String end2Name) {
		this.end2Name = end2Name;
	}

	public String getEnd2Cardinality() {
		return end2Cardinality;
	}

	public void setEnd2Cardinality(String end2Cardinality) {
		this.end2Cardinality = end2Cardinality;
	}

	public boolean isEnd2Aggregation() {
		return end2IsAggregation;
	}

	public void setEnd2Aggregation(boolean end2Aggregation) {
		this.end2IsAggregation = end2Aggregation;
	}

	public boolean isEnd2Composite() {
		return end2IsComposite;
	}

	public void setEnd2Composite(boolean end2Composition) {
		this.end2IsComposite = end2Composition;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		String end1EntityName = end1Entity.getName();
		String end2EntityName = end2Entity.getName();

		buffer.append(end1EntityName).append(" <");
		if (!StringUtil.isEmpty(end1Name)) {
			buffer.append(" ").append(end1Name);
		}
		if (!StringUtil.isEmpty(end1Cardinality)) {
			buffer.append(" (").append(end1Cardinality).append(")");
		}

		buffer.append(" -");

		if (!StringUtil.isEmpty(end2Name)) {
			buffer.append(" ").append(end2Name);
		}
		if (!StringUtil.isEmpty(end2Cardinality)) {
			buffer.append(" (").append(end2Cardinality).append(")");
		}
		buffer.append(" > ").append(end2EntityName);

		buffer.append("\n");
		return buffer.toString();
	}

}
