package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagConfiguration;

public class LightPathElement 
{
	private List<TagConfiguration> tagList;
	private String clazz;
	
	public LightPathElement(String clazz, List<TagConfiguration> list)
	{
		this.clazz=clazz;
		this.tagList=list;
	}
	
	public boolean isEmpty()
	{
		if(tagList==null)
			return true;
		
		int counter=0;
		for(TagConfiguration t:tagList){
			if(t.getValue()!=null && !t.getValue().equals("") )
				counter++;
		}
		if(counter>0)
			return false;
		else
			return true;
	}

	public List<TagConfiguration> getTagList() {
		return tagList;
	}

	public void setTagList(List<TagConfiguration> tagList) {
		this.tagList = tagList;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	public String getProperty(String name)
	{
		if(tagList==null || tagList.isEmpty())
			return "";
		
		for(TagConfiguration t: tagList){
			if(t.getName().equals(name))
				return t.getValue();
		}
		
		return "";
	}
	
	public void setTag(String name, String val)
	{
		TagConfiguration t= new TagConfiguration(name, val, null, true, true, null, TagNames.getEnumerationVal(name));
		
		if(tagList==null){
			tagList=new ArrayList<TagConfiguration>();
		}
		
		if(tagList.isEmpty()){
			tagList.add(t);
			return;
		}
		
		boolean setTag=false;
		for(TagConfiguration tC:tagList){
			if(tC.getName().equals(name)){
				tC=t;
				setTag=true;
			}
		}
		
		if(!setTag){
			tagList.add(t);
		}
		
		
	}
}
