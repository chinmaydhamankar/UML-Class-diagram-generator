package com.parser.beans;


import java.util.ArrayList;
import java.util.List;

public class Collection_Unit {

	private List<Unit> units;
	private List<Edge> edge;
	public Collection_Unit()
	{
		this.units = new ArrayList<Unit>(0);
		this.edge = new ArrayList<Edge>(0);
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public List<Edge> getEdge() {
		return edge;
	}

	public void setEdge(List<Edge> edge) {
		this.edge = edge;
	}
	
	public Unit getUnit(String name)
	{
		for (Unit unit : units) 
		{
			if(unit.getClassName().equals(name))
			{
				return unit;
			}
		}
		return null;
	}
	
	public boolean checkEdgePresent(String source, String destination, EdgeTypes type)
	{
		for (Edge edge2 : edge)
		{
			if(edge2.getSource().getClassName().equals(source) && edge2.getDestination().getClassName().equals(destination)
					&& edge2.getType().equals(type))
			{
				return true;
			}
		}
		
		return false;
		
	}
	
}
