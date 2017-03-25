package com.parser.beans;

public class Edge {

	private Unit source;
	private Unit destination;
	private Enum type;
	private String cardinality;
	
	public Enum getType() {
		return type;
	}
	public void setType(Enum type) {
		this.type = type;
	}
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public Unit getSource() {
		return source;
	}
	public void setSource(Unit source) {
		this.source = source;
	}
	public Unit getDestination() {
		return destination;
	}
	public void setDestination(Unit destination) {
		this.destination = destination;
	} 
}
