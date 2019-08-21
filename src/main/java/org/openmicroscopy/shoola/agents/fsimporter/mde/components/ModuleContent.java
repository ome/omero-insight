package org.openmicroscopy.shoola.agents.fsimporter.mde.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

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
//				System.out.println("\t "+entry.getKey()+": "+entry.getValue().getTagValue());
				result.add(entry.getValue());
			}
		}
		return result;
	}
	
	public LinkedHashMap<String,TagData> getList(){
		return tagList;
	}
	
	public void setAttributes(LinkedHashMap<String, TagData> list) {
		tagList=list;
	}
	
	public List<TagData> getTagList(){
		if(tagList!=null)
			return new ArrayList<TagData>(tagList.values());
		return null;
	}
	
	public void specifyView(ModuleConfiguration conf) {
		if(conf==null)
			return;
		List<TagConfiguration> list=conf.getTagList();
		if(list==null)
			return;
		
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			if(t.getName()!=null){
				if(tagList.containsKey(t.getName()))
					tagList.get(t.getName()).setVisible(t.isVisible());
			}
		}
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
			System.out.println("-- PRINT DATA: no data");
		}else {
			for(TagData t:getTagList()) {
				if(t!=null)
					System.out.println("\t "+t.tagToString()+", visible: "+t.isVisible());
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


//	public Class[] getClasses() {
//		if(tagList==null) {
//			return null;
//		}
//		
//		List<TagData> list=getTagList();
//		Class[] result=new Class[list.size()];
//		for(int i=0; i<list.size();i++) {
//			result[i]=list.
//		}
//		return result;
//	}
}
