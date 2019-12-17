/*
 * Copyright (C) <2018-2019> University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openmicroscopy.shoola.agents.fsimporter.mde.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagDataProp;

/**
 * Holds content(properties==tags) of a module as list of TagData.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleContent {
	/* Attributes for module */
	private LinkedHashMap<String,TagData> tagList;
	/* type of module */
	private String type;
	/* possible parents*/
	private String[] parents;

	public ModuleContent(LinkedHashMap<String,TagData> list,String type,String[] parents) {
		this.tagList=list;
		this.type=type;
		this.parents=parents;
	}
	
	
	/**
	 * Copy constructor
	 * @param orig
	 */
	public ModuleContent(ModuleContent orig) {
		if(orig!=null) {
			if(orig.tagList!=null) {
				this.tagList=new LinkedHashMap<>();
				for(Map.Entry<String, TagData> entry : orig.tagList.entrySet()) {
					this.tagList.put(entry.getKey(), new TagData(entry.getValue()));
				}
			}
			this.type=orig.type;
			if(orig.parents!=null)this.parents=orig.parents.clone();
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

	public String[] getParents() {
		return parents;
	}
	public void setParents(String[] parents) {
		this.parents = parents;
	}
	
	public boolean hasParent(String type) {
		if(parents!=null && type !=null) {
			for(String s:parents) {
				if(type.equals(s))
					return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @return identifier for element of type type
	 */
	public String[] getIdents() {
		if(tagList==null) {
			return new String[] {"ID","Model", "Manufactur"};
		}
		String[] attrNames=getAttributeNames();
		if(attrNames!=null) {
			//eliminate User:: attributes
			List<String> list=new ArrayList<>();
			for(String s:attrNames) {
				if(!s.contains("User::"))
					list.add(s);
			}
			return list.toArray(new String[list.size()]);
		}
		return null;
	}
	
	/**
	 * 
	 * @return list of tagdata whose content has changed
	 */
	public List<TagData> getInput() {
		List<TagData> result=new ArrayList<>();
		
		for (Map.Entry<String,TagData> entry : tagList.entrySet()) {
			if(entry.getValue().valueHasChanged()) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
	
	public LinkedHashMap<String,TagData> getList(){
		return tagList;
	}
	
	public void setAttributes(LinkedHashMap<String, TagData> list) {
		takeOverProperties(list);
		tagList=list;
	}
	
	/**
	 * set properties like visible, unit in given list according the setting in current tagList
	 * @param list data for object
	 */
	private void takeOverProperties(LinkedHashMap<String, TagData> list) {
		if(tagList==null)
			return;
		for(Map.Entry<String, TagData> entry : tagList.entrySet()) {
			if(list.containsKey(entry.getKey())) {
				TagData data=list.get(entry.getKey());
				data.setVisible(entry.getValue().isVisible());
				if(!data.getTagUnitString().equals(entry.getValue().getTagUnitString())) {
					//TODO umrechnen:
					data.setTagUnit(entry.getValue().getTagUnitString());
				}
			}
		}
	}


	public List<TagData> getTagList(){
		if(tagList!=null)
			return new ArrayList<TagData>(tagList.values());
		return null;
	}
	
	public TagData getTag(String tagName) {
		if(tagList==null || !tagList.containsKey(tagName))
			return null;
		return tagList.get(tagName);
	}
	
	
	/**
	 * Replace element at i with given tagdata
	 * @param i
	 * @param tagData
	 */
	public void set(String key, TagData tagData) {
		if(tagList!=null && tagList.containsKey(key)) {
			tagList.put(key, tagData);
		}
	}


	public String getAttributeValue(String id) {
		if(tagList==null || !tagList.containsKey(id))
			return null;
		return tagList.get(id).getTagValue();
	}
	
	public String[] getAttributeNames() {
		if(tagList==null) {
			return null;
		}
		return tagList.keySet().toArray(new String[tagList.size()]);
	}


	public void print() {
		if(tagList==null) {
			ImporterAgent.getRegistry().getLogger().debug(this, "-- PRINT ModuleContent: \n\tno tag data!");
		}else {
			for(TagData t:getTagList()) {
				if(t!=null)
					t.print();
			}
		}
	}


	public void set(String attributeName, String[] valueAt) {
		if(tagList!=null && tagList.containsKey(attributeName)) {
			TagData t = tagList.get(attributeName);
			t.saveTagValue(valueAt);
			tagList.put(attributeName, t);
		}
	}


	public void resetInput() {
		if(tagList==null)
			return;
		for (Map.Entry<String,TagData> entry : tagList.entrySet()) {
			entry.getValue().dataHasChanged(false);
		}
		
	}
	
	public void setAllDataChanged() {
		if(tagList==null)
			return;
		for (Map.Entry<String,TagData> entry : tagList.entrySet()) {
			entry.getValue().dataHasChanged(true);
		}
	}


	public ModuleConfiguration getProperties() {
		if(tagList==null)
			return null;
		ModuleConfiguration conf = new ModuleConfiguration();
		for (Map.Entry<String,TagData> entry : tagList.entrySet()) {
			TagData t= entry.getValue();
			if(t!=null)
				conf.put(entry.getKey(), new TagDataProp(t.getProperties()));
		}
		return conf;
	}


	public void setProperties(ModuleConfiguration conf) {
		if(tagList==null)
			return;
		
		for(Entry<String, TagData> entry: tagList.entrySet()) {
			TagDataProp p=conf.getConfigurationFor(entry.getKey());
			if(p!=null) {
				entry.getValue().setProperties(p);
			}
		}
	}


	public Boolean isContainer() {
		if(tagList==null || tagList.isEmpty())
			return true;
		return false;
	}

}
