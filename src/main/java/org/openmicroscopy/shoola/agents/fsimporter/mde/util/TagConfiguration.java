package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import org.openmicroscopy.shoola.util.MonitorAndDebug;

import ome.units.unit.Unit;

public class TagConfiguration 
{
	private String name;
	private String value;
	private Unit unit;
	private Boolean property;
	private boolean visible;
	private String[] possibleUnits;
	private String[] possibleValues;
	
	public TagConfiguration(String name, String value, Unit unit,Boolean property, 
			boolean visible,String[] possUnits,String[] possValues)
	{
		this.name=name;
		this.value=value;
		this.unit=unit;
		this.property=property;
		this.visible=visible;
		this.possibleUnits=possUnits;
		this.possibleValues=possValues;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getProperty() {
		return property;
	}
	public void setProperty(Boolean property) {
		this.property = property;
	}

	public String getUnitSymbol() {
		return unit==null ? "" :unit.getSymbol();
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public String[] getPossibleUnits(){
		return possibleUnits;
	}
	
	public void setPossibleUnits(String[] units){
		possibleUnits=units;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void printf()
	{
		MonitorAndDebug.printConsole("TAG "+name+" = "+value+" "+getUnitSymbol());
	}

	public String[] getPossibleValues() {
		
		return possibleValues;
	}
	
}
