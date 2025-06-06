package com.smn.apitool.model;

public class Relation {

	private String end1Name;

	private String end1Cardinality;

	private boolean end1Aggregation;

	private boolean end1Composition;

	private String end2Name;

	private String end2Cardinality;

	private boolean end2Aggregation;

	private boolean end2Composition;

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
		return end1Aggregation;
	}

	public void setEnd1Aggregation(boolean end1Aggregation) {
		this.end1Aggregation = end1Aggregation;
	}

	public boolean isEnd1Composition() {
		return end1Composition;
	}

	public void setEnd1Composition(boolean end1Composition) {
		this.end1Composition = end1Composition;
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
		return end2Aggregation;
	}

	public void setEnd2Aggregation(boolean end2Aggregation) {
		this.end2Aggregation = end2Aggregation;
	}

	public boolean isEnd2Composition() {
		return end2Composition;
	}

	public void setEnd2Composition(boolean end2Composition) {
		this.end2Composition = end2Composition;
	}

}
