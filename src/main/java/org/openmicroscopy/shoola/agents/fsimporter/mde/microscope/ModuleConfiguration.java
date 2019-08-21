package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ome.units.unit.Unit;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagConfiguration;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Holds a list of visible tags and their configuration
 * @author Kunis
 *
 */
public class ModuleConfiguration 
{
	/** Logger for this class. */
//    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ModuleConfiguration.class);
	
	public static final String TAG_NAME="Name";
	public static final String TAG_VALUE="Value";
	public static final String TAG_UNIT="Unit";
	public static final String TAG_PROP="Optional";
	public static final String TAG_VISIBLE="Visible";
	
	private List<TagConfiguration> tagConfList; 
	private List<TagConfiguration> settingsTagConfList;
	private boolean visible;

	private List<LightPathElement> elementList;
	
	public ModuleConfiguration(boolean visible) 
	{
		this.visible=visible;
		tagConfList=new ArrayList<TagConfiguration>();
		settingsTagConfList=new ArrayList<TagConfiguration>();
	}
	
	/** 
	 * 
	 * @return list of tags
	 */
	public List<TagConfiguration> getTagList()
	{
		return tagConfList;
	}
	public void setTagList(List<TagConfiguration> list)
	{
		tagConfList=list;
	}
	
	/**
	 * 
	 * @return list of settings tags
	 */
	public List<TagConfiguration> getSettingList()
	{
		return settingsTagConfList;
	}
	public void setSettingList(List<TagConfiguration> list)
	{
		settingsTagConfList=list;
	}
	
	private void setTag(String name, String val,String unit, Boolean prop, List<TagConfiguration> thisList, boolean visible, String[] enums) 
	{
		Unit u=null;
		String[] pU=null;
		String[] eVal=null;
		try {
			u = TagNames.parseUnit(unit,name);
			pU= TagNames.getUnits(name);
			if(enums!=null){
				eVal=enums;
			}else{
				eVal=TagNames.getEnumerationVal(name);
			}
			thisList.add(new TagConfiguration(name, val,u, prop, visible,pU,eVal));
		} catch (Exception e) {
			LOGGER.warn("[HARDWARE] can't parse unit of tag "+name+" ("+unit+")");
			e.printStackTrace();
		}
	
		
	}
	
	public void setTag(String name, String val,String unit, Boolean prop,int elemIndex) 
	{
		List<TagConfiguration> list=elementList.get(elemIndex).getTagList();
		if(list!=null)
			setTag(name, val, unit,prop,list , true, null) ;
	}
	
	public void setTag(String name, String val,String unit, Boolean prop, String[] enums, Boolean visible) 
	{
		setTag(name, val, unit,prop, tagConfList, visible, enums) ;
	}
	
	public void setSettingTag(String name, String val,String unit, Boolean prop, String[] enums, Boolean visible) 
	{
		setTag(name, val, unit, prop, settingsTagConfList, visible, enums);
	}
	

	
	
	
	
	private Element tagToXML(Document doc,TagConfiguration tag)
	{
		Element modTag = doc.createElement("Tag");
		modTag.setAttribute(TAG_NAME, tag.getName());
		modTag.setAttribute(TAG_PROP, String.valueOf(tag.getProperty()));
		modTag.setAttribute(TAG_VISIBLE, String.valueOf(tag.isVisible()));
		modTag.setAttribute(TAG_VALUE, tag.getValue());
		if(!tag.getUnitSymbol().equals(""))
			modTag.setAttribute(TAG_UNIT, tag.getUnitSymbol());
		
		return modTag;
	}
	
	public void loadTags(Element node)
	{
//		NodeList tagList=node.getChildNodes();
		NodeList tagList=node.getElementsByTagName("Tag");
		
		if(tagList!=null && tagList.getLength()>0){
			for(int i=0; i<tagList.getLength(); i++){
				NamedNodeMap attr=tagList.item(i).getAttributes();
				if(tagList.item(i).getParentNode().getNodeName().equals("Settings")){
					parseTagFromXML(attr,settingsTagConfList,"settings");
				}else{
					parseTagFromXML(attr,tagConfList,"");
				}
			}
		}	
	}
	
	/**
	 * read lightPath elements from xml like
	 * <clazz>
	 * 	<tag name="" value="" />
	 * </clazz>
	 * @param node
	 */
	public void loadElements(Element node) 
	{
		NodeList clazzList=node.getChildNodes();
		if(clazzList!=null && clazzList.getLength()>0){
			MonitorAndDebug.printConsole("LightPath has childs: "+clazzList.getLength());
			for(int i=0;i<clazzList.getLength();i++){	
				if(clazzList.item(i).getNodeType() == Node.ELEMENT_NODE){
					List<TagConfiguration> list=new ArrayList<TagConfiguration>();
					String clazz=clazzList.item(i).getNodeName();
					MonitorAndDebug.printConsole("\tLightPath has childs: "+clazz);
					NodeList tagList=clazzList.item(i).getChildNodes();
					if(tagList!=null && tagList.getLength()>0){
						for(int j=0; j<tagList.getLength(); j++){
							NamedNodeMap attr=tagList.item(j).getAttributes();
							parseTagFromXML(attr, list, "");
						}
					}
					addNewElement(clazz, list);
				}
			}
		}
	}



	/**
	 * Parse tag: <Tag Name="" Optional="" Unit="" Value=""	Visible="" /> from given nodemap and add to list.
	 * @param attr
	 * @param list
	 * @param sett
	 */
	private void parseTagFromXML(NamedNodeMap attr,List<TagConfiguration> list,String sett) 
	{
		
		String name="";String value=null; boolean prop=false;String unitStr=null;
		boolean visible=false;

		if(attr!=null && attr.getLength()>0)
		{
			if(attr.getNamedItem(TAG_NAME)!=null){
				name=attr.getNamedItem(TAG_NAME).getNodeValue();
			}
			if(attr.getNamedItem(TAG_VALUE)!=null){
				value=attr.getNamedItem(TAG_VALUE).getNodeValue();
			}
			if(attr.getNamedItem(TAG_UNIT)!=null){
				unitStr=attr.getNamedItem(TAG_UNIT).getNodeValue();
			}
			if(attr.getNamedItem(TAG_PROP)!=null){
				prop=stringToBool(attr.getNamedItem(TAG_PROP).getNodeValue());
			}
			if(attr.getNamedItem(TAG_VISIBLE)!=null){
				visible=BooleanUtils.toBoolean(attr.getNamedItem(TAG_VISIBLE).getNodeValue());
			}
		}
//		if(visible)
			setTag(name,value,unitStr,prop,list, visible, null);
	}
	
	
	public static boolean stringToBool(String s) {
        s = s.toLowerCase();
        Set<String> trueSet = new HashSet<String>(Arrays.asList("1", "true", "yes"));
        Set<String> falseSet = new HashSet<String>(Arrays.asList("0", "false", "no"));

        if (trueSet.contains(s))
            return true;
        if (falseSet.contains(s))
            return false;

        throw new IllegalArgumentException(s + " is not a boolean.");
    }
	

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean selected) 
	{
		visible=selected;
	}



	
	
	public void printConfig()
	{
		for(int i=0; i<tagConfList.size();i++){
			MonitorAndDebug.printConsole(tagConfList.get(i).getName()+" = "+tagConfList.get(i).getValue()+" "+tagConfList.get(i).getUnitSymbol());
		}
		for(int i=0; i<settingsTagConfList.size();i++){
			MonitorAndDebug.printConsole(settingsTagConfList.get(i).getName()+" = "+settingsTagConfList.get(i).getValue()+" "+settingsTagConfList.get(i).getUnitSymbol());
		}
	}

	public List<LightPathElement> getElementList() 
	{
		MonitorAndDebug.printConsole("# ModulConfiguration::getElementList(): "+(elementList==null? "null":elementList.size()));
		return elementList;
	}

	public void setElementList(List<LightPathElement> list) 
	{
		elementList=list;
	}

	/** add new filter element to list only if one parameter of the filter is not empty
	 * 
	 * @param clazz filter class
	 * @param list of parameters of the filter
	 * @return
	 */
	public int addNewElement(String clazz, List<TagConfiguration> list) 
	{
		if(elementList==null)
			elementList=new ArrayList<LightPathElement>();
		
		LightPathElement newElem=new LightPathElement(clazz, list);
		if(!newElem.isEmpty()){
			elementList.add(newElem);
		}
		return elementList.size()-1;
	}

	
	
}
