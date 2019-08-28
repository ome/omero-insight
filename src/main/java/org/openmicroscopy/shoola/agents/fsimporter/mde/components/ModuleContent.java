package org.openmicroscopy.shoola.agents.fsimporter.mde.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;

/**
 * Holds content(properties==tags) of a module as list of tags
 * @author Kunis
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
		setProperties(list);
		tagList=list;
	}
	
	/**
	 * set properties like visible, unit in given list according the setting in tagList
	 * @param list data for object
	 */
	private void setProperties(LinkedHashMap<String, TagData> list) {
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
			MonitorAndDebug.printConsole("-- PRINT ModuleContent: \n\tno tag data!");
		}else {
			for(TagData t:getTagList()) {
				if(t!=null)
					MonitorAndDebug.printConsole("\t "+t.tagToString()+", visible: "+t.isVisible());
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

}
