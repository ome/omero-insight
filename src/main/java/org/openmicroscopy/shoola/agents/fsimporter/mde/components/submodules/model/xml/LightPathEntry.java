package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml;

import ome.xml.model.Filter;
import ome.xml.model.Dichroic;
import ome.xml.model.enums.FilterType;

public class LightPathEntry extends ome.xml.model.Filter{
	
	private String id;
	private String model;
	private String manufacturer;
	private String filterwheel;
	private String width;
	
	
	/*main, secondary,...*/
	private String clazz;
	/* dichroic, longpass,...*/
	private FilterType type;
	/* Excitation, dichroic or emission*/
	private String category;
	
	//classList={"DICHROIC","EMISSION","ECXITATION"}
	//public final static String[] categoryList={"","Main Filter","Secondary Filter","Tertiary Filter","Quaternary Filter"};
	//public final static String[] typeList=getNames(FilterType.class); 

	public LightPathEntry(Filter f,String clazz) {
		this.id=f.getID();
		this.model=f.getModel();
		this.manufacturer=f.getManufacturer();
		this.filterwheel=f.getFilterWheel();
		this.type=f.getType();
		this.clazz=clazz;
	}
	//TODO: dichroic enum
	public LightPathEntry(Dichroic d) {
		this.id=d.getID();
		this.model=d.getModel();
		this.manufacturer=d.getManufacturer();
		this.type=FilterType.DICHROIC;//ome.model.enums.FilterType.VALUE_DICHROIC;
		this.clazz=FilterType.DICHROIC.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getFilterWheel() {
		return filterwheel;
	}

	public void setFilterWheel(String filterwheel) {
		this.filterwheel = filterwheel;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	
	public FilterType getType() {
		return type;
	}
	
	public void setType(FilterType type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
