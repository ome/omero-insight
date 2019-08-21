package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

public abstract class EditorModel 
{
	private static final org.slf4j.Logger LOGGER =LoggerFactory.getLogger(EditorModel.class);
	protected LinkedHashMap<String,TagData> tagMap;
	protected String name;
	
	public abstract LinkedHashMap<String,TagData> getTagList();
	
	
	public String getName() {
		return name;
	}
	/**
	 * TODO: test only getChangesList of module
	 * @param m
	 * @return true if tagMap is equal to given hashMap m, or m is null
	 */
	public boolean isEqual(LinkedHashMap<String, TagData> m)
	{
		boolean equal=true;
		
		if(m==null) {
			MonitorAndDebug.printConsole("Input null!!");
			return equal;
		}
		if(getTagList()==null) {
			MonitorAndDebug.printConsole("tagMap null!!");
			return false;
		}
		for(Map.Entry<String, TagData> entry : m.entrySet()) {
			String key=entry.getKey();
			
			String val=null;
			if(entry.getValue()!=null)
				val=entry.getValue().getTagValue();
			
			String val2=null;
			if(getTagList().get(key)!=null)
				val2=getTagList().get(key).getTagValue();
			
			MonitorAndDebug.printConsole("-- Compare <"+key+">: ["+val+", "+val2+"] "+val.equals(val2));
			if(val==null && val2==null)
				equal = equal && true;
			else
				equal = equal && val.equals(val2);
		}
		System.out.println("-- ISEQUAL: "+equal);
		return equal;
	}
//	
//	public List<TagData> getDifferenceTo(ImageModel m)
//	{
//		return getDifferenceTo(m.getTagList());
//	}
	
	/**
	 * Returns the different fields of the input m 
	 * @param m
	 * @return list of changes
	 */
	protected List<TagData> getDifferenceTo(LinkedHashMap<String, TagData> m)
	{
		List<TagData> result= new ArrayList<>();
		for(Map.Entry<String, TagData> entry : m.entrySet()) {
			String key=entry.getKey();
			TagData val=entry.getValue();
			if(!val.equals(tagMap.get(key))){
				result.add(val);
			}
		}
		return result;
	}
	
	public void save(LinkedHashMap<String, TagData> input)
	{
		if(input ==null)
			return;
		
		if(tagMap==null) {
			System.out.println("# ERROR: Editormodel is null");
			tagMap=getTagList();
		}
		for(Map.Entry<String, TagData> entry:input.entrySet()) {
			if(tagMap!=null && tagMap.containsKey(entry.getKey())){
				MonitorAndDebug.printConsole("-- Save Tag: "+entry.getKey()+"\t[EditorModel::save]");
				tagMap.put(entry.getKey(), new TagData(entry.getValue()));
			}
		}
		
	}
	
	public void addData(LinkedHashMap<String, TagData> m, boolean overwrite, boolean onlyChangedObj)
	{
		String n = this.getClass().getSimpleName()+"::"+name;
		System.out.println("-- Add Data of tagList #"+n+", overwrite: "+overwrite);
		if(overwrite || tagMap==null){
			replaceData(m,onlyChangedObj);
		}else {
			try {
				completeData(m,onlyChangedObj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set src(tag!=null)=input(tag!=null) && src(tag==null)=input(tag!=null), but delete data by this not possible (input==null || input="")
	 * @param m
	 */
	private void replaceData(LinkedHashMap<String, TagData> tagMapIn,boolean onlyChangedObj)
	{
		if(tagMapIn!=null){
			System.out.println("-- Replace DATA ");
			for(Map.Entry<String, TagData> entry : tagMapIn.entrySet()) {
				String keyIn=entry.getKey();
				TagData valIn=entry.getValue();
					
				boolean replace=true;
				if(valIn==null || valIn.getTagValue()==null || valIn.getTagValue().equals("")) {
					replace=false;
//					System.out.println("-- Given data is empty -> replace=false");
				}
				
				if(onlyChangedObj && !valIn.valueHasChanged()) {
					replace = false;
//					System.out.println("-- No changes in given data -> replace = false");
				}
				
				String valInValue =valIn.getTagValue();
				String valExValue = (tagMap!=null && tagMap.get(keyIn)!=null) ? tagMap.get(keyIn).getTagValue():"null";
				MonitorAndDebug.printConsole("\t -> "+keyIn+" : \tin = "+valIn.getTagValue()+" => exist = "+valExValue+";\treplace= "+replace); 
				
				if(replace) {
					tagMap.put(keyIn, new TagData(valIn));
				}
			}
		}
	}

	/**
	 * Set src(tag==null)=input(tag!=null)
	 * @param i
	 * @throws Exception
	 */
	private void completeData(LinkedHashMap<String, TagData> tagMapIn,boolean onlyChangedObj) throws Exception
	{
		if(tagMap!=null) {
			System.out.println("-- Complete DATA");
			for(Map.Entry<String, TagData> entry : tagMapIn.entrySet()) {
				String keyIn=entry.getKey();
				TagData valIn=entry.getValue();
					
				boolean replace = false;
				if(tagMap.containsKey(keyIn) && tagMap.get(keyIn).getTagValue()!=null && tagMap.get(keyIn).getTagValue().equals("")) {
//					System.out.println("-- Existing data is empty -> replace = true");
					replace=true;
				}
				if(valIn.getTagValue()==null && !valIn.getTagValue().equals("")) {
					replace=true;
//					System.out.println("-- Given data is not empty -> replace=true");
				}
				
				if(onlyChangedObj && !valIn.valueHasChanged()) {
					replace = false;
//					System.out.println("-- No changes in given data -> replace = false");
				}
				
				MonitorAndDebug.printConsole("\t -> "+keyIn+" : \tin = "+valIn.getTagValue()+" => exist = "+(tagMap.get(keyIn)!=null?tagMap.get(keyIn).getTagValue():"null")+";\treplace= "+replace); 
				if(replace) {
					tagMap.put(keyIn, new TagData(valIn));
				}
			}
		}
	}
	
	/**
	 * Update data model for given modified tags.
	 * @param changes list of modified tags
	 * @throws Exception
	 */
	public void update(List<TagData> changes) throws Exception 
	{
		if(changes==null){
			MonitorAndDebug.printConsole("\t no changes for image");
			return;
		}
		LinkedHashMap<String, TagData> tagMap1=tagListToHashMap(changes);
		
		for(Map.Entry<String, TagData> entry : tagMap1.entrySet()) {
			String key=entry.getKey();
			TagData val=entry.getValue();
			boolean replace= val.getTagValue()!=null && !val.getTagValue().equals("");
			if( replace) {
				tagMap.put(key, new TagData(val));
			}
		}
	}
	
	public static LinkedHashMap<String,TagData> tagListToHashMap(List<TagData> list)
	{
		LinkedHashMap<String,TagData> map=new LinkedHashMap<String,TagData>();
		for(TagData t: list) {
			map.put(t.getTagName(), t);
		}
		return map;
	}
	
	public static List<TagData> mapToTagList(LinkedHashMap<String,TagData> map)
	{
		List<TagData> list= new ArrayList<>();
		for(TagData val:map.values()) {
			list.add(val);
		}
		return list;
	}
	
	public boolean isEqual(EditorModel m)
	{
		return isEqual(m.getTagList());
	}
}
