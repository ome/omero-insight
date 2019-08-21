package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

public class TagDataProp {
	String name;
	Boolean visible;
	String unitSymbol;
	
	public TagDataProp(String name, String unitSymbol, Boolean vis) {
		this.name = name;
		this.visible=vis;
		this.unitSymbol=unitSymbol;
	}

	public String getName() {
		return name;
	}

	public Boolean getVisible() {
		return visible;
	}


	public String getUnitSymbol() {
		return unitSymbol;
	}
}
