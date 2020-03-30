/*
 * Copyright (C) <2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.XMLWriter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagDataProp;

import ome.model.units.UnitEnum;

/**
 * holds MDE configuration parse from xml file or use standard implement OME configuration. 
 *@author Susanne Kunis<susannekunis at gmail dot com>
 */
public class MDEConfiguration {
	/* microscope hardware configuration list - which objects values are available.
	 *  e.g.: <LatticeLightSheet,[<Objective,[o1,o2,o3]>,<Detector,[d1,d2,d3]>,...]> */
	LinkedHashMap<String, ModuleList> hConfiguration;
	
	/* object configuration list - which object is available and how the objects looks like (content) for a mic.
	 *  e.g.: <UNIVERSAL,[<Objective,objContent>,<Detector,detContent>,...]> */
//	LinkedHashMap<String,HashMap<String,ModuleContent>> oConfiguration;
	public final static String UNIVERSAL="Universal";
	
	/** available object definitions*/
	HashMap<String,ModuleContent> oDefinition;
	/** object configuration for different microscopes ([micName,[objectName,ModuleConfiguration]]*/
	LinkedHashMap<String,HashMap<String,ModuleConfiguration>> oConfiguration;
	
	private HashMap<String, UnitEnum> defaultUnitMap;
	
	/* standard ome tree */
	private DefaultMutableTreeNode standardTree;
	private String configPath;
	
	public MDEConfiguration(String configPath) {
		//copy default ome unit map
		defaultUnitMap=new HashMap<>();
		this.configPath=configPath;
		
		for(Map.Entry<String, ome.model.units.UnitEnum> entry: TagNames.omeUnitEnumsDef.entrySet()) {
			defaultUnitMap.put(entry.getKey(), entry.getValue());
		}
		
//		this.hConfiguration=new LinkedHashMap<String,ModuleList>();
//		this.oConfiguration=new LinkedHashMap<String,HashMap<String,ModuleContent>>();
		// load def,config and predef from file if available
		parse(configPath);
		
		ModuleController c=ModuleController.getInstance();
		if(oDefinition==null || oDefinition.isEmpty()) {
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] can't parse object definitions from conf file!");
			// no object definitions available in file -> use implemented standard ome object definition
			oDefinition= c.initDefaultOMEObjects();
		}
	}
	
	// TODO test add unit function: cancel, apply configurators
	public void addDefaultUnit(String unitSymbol, String className, String tagName, String parent) {
		if(defaultUnitMap!=null  && !defaultUnitMap.containsKey(parent+"::"+tagName)) {
			ome.model.units.UnitEnum u=TagNames.getUnitEnum(className, unitSymbol);
			if(u!=null) {
				defaultUnitMap.put(parent+"::"+tagName, u);
			}
		}
	}
	
	public String getStandardUnitSymbolByName(String parent,String tagName) {
		if(defaultUnitMap!=null && defaultUnitMap.containsKey(parent+"::"+tagName) && defaultUnitMap.get(parent+"::"+tagName)!=null) {
			return defaultUnitMap.get(parent+"::"+tagName).getSymbol();
		}
		return "";
	}
	
	/**
	 * Copy constructor. Clone configuration.
	 * @param conf
	 */
	public MDEConfiguration(MDEConfiguration orig) {
		this.hConfiguration=new LinkedHashMap<String,ModuleList>();
		this.oConfiguration=new LinkedHashMap<>();
		this.oDefinition=new HashMap<String,ModuleContent>();
		if(orig.hConfiguration!=null) {
			for(Map.Entry<String, ModuleList> entry : orig.hConfiguration.entrySet()) {
				this.hConfiguration.put(entry.getKey(), new ModuleList(entry.getValue()));
			}
		}
		if(orig.oDefinition!=null) {
			for(Entry<String,ModuleContent> entry : orig.oDefinition.entrySet()) {
				this.oDefinition.put(entry.getKey(), new ModuleContent(entry.getValue()));
			}
		}
		if(orig.oConfiguration!=null) {
			for(Entry<String, HashMap<String, ModuleConfiguration>> entry:orig.oConfiguration.entrySet()) {
				HashMap<String,ModuleConfiguration> list = new HashMap<String,ModuleConfiguration>();
				if(entry.getValue()!=null) {
					for(Entry<String, ModuleConfiguration> c:entry.getValue().entrySet()) {
						// c.getValue can be null for container objects (catch in copy constructor
						list.put(c.getKey(), new ModuleConfiguration(c.getValue()));
					}
				}
				this.oConfiguration.put(entry.getKey(), list);
			}
		}
		this.defaultUnitMap=new HashMap<>();
		if(orig.defaultUnitMap!=null) {
			for(Entry<String, UnitEnum> entry:orig.defaultUnitMap.entrySet()) {
				String uClass = TagNames.getUnitClassFromSymbol(entry.getValue().getSymbol())!=null?TagNames.getUnitClassFromSymbol(entry.getValue().getSymbol()).getName():null;
				UnitEnum uEnum = TagNames.getUnitEnum(uClass, entry.getValue().getSymbol());
				this.defaultUnitMap.put(entry.getKey(), uEnum);
			}
		}
	}

	/**
	 * Initialize predefinitions for given microscope.
	 * @param micName microscope name as key
	 * @param conf  list of predefined objects as  {@link ModuleList}
	 */
	public void initPredefinitionsForMicroscope(String micName,ModuleList conf) {
		if(hConfiguration!=null) {
//			conf.print("------ Set Instruments for "+micName+" [MDEConfiguration]");
			this.hConfiguration.put(micName, conf);
		}
		//new mic: add also to object conf
		if(!oConfiguration.containsKey(micName))
			oConfiguration.put(micName, getDefaultProperties());
	}
	
	/**
	 * Initialize new created microscope with standard configuration of available objects.
	 * @return
	 */
	private HashMap<String, ModuleConfiguration> getDefaultProperties() {
		if(oDefinition==null)
			return null;
		HashMap<String,ModuleConfiguration> list=new HashMap<>();
		for(Entry<String,ModuleContent> entry:oDefinition.entrySet()) {
			if(entry.getValue()!=null)
				list.put(entry.getKey(), entry.getValue().getProperties());
		}
		return list;
	}

	/**
	 * Set/replace the predefined object list for given object 
	 * @param micName
	 * @param objectName
	 * @param predefineObjectList
	 */
	public void setPredefinitionsForMicroscope(String micName, String objectName,List<ModuleContent> predefineObjectList) {
		if(hConfiguration!=null) {
			ModuleList hardware=this.hConfiguration.get(micName);
			if(hardware==null) {
				hardware=new ModuleList();
			}
			hardware.put(objectName, predefineObjectList);
			this.hConfiguration.put(micName, hardware);
		}
	}
	
	/**
	 * Remove microscope and all predefined objects of this microscope, despite UNIVERSAL.
	 * @param micName
	 */
	public void removeAllPredefsForMicroscope(String micName) {
		if(hConfiguration!=null && !micName.equals(UNIVERSAL)) {
			this.hConfiguration.remove(micName);
		}
	}
	
	/**
	 * Remove all predefined objects of given type from list of predefined objects for
	 * given microscope.
	 * @param objType
	 * @param micName
	 */
	public void removePredefsOfObjectForMicroscope(String objType, String micName) {
		if(hConfiguration!=null && !micName.equals(UNIVERSAL)) {
			ModuleList list= this.hConfiguration.get(micName);
			if(list!=null) {
				list.remove(objType);
				this.hConfiguration.put(micName, list);
			}
		}
	}

	/**
	 * Return list as {@link ModuleList} of predefined object values for given microscope.
	 * @param micName microscope name
	 * @return
	 */
	public ModuleList getPredefinitions(String micName) {
		if(hConfiguration!=null && !hConfiguration.isEmpty())
			return hConfiguration.get(micName);
		
		return null;
	}
	
	/**
	 * Get list of configurated objects for given microscope.
	 * @param micName
	 * @return
	 */
	public HashMap<String, ModuleConfiguration> getConfiguratedObjects(String micName) {
		if(oConfiguration!=null && !oConfiguration.isEmpty())
			return oConfiguration.get(micName);
		
		return null;
	}
	
	/**
	 * Remove configuration of object objectName and predefined values for given mic.
	 * @param objName
	 * @param mic
	 */
	public void removeObjectForMicroscope(String objName, String mic) {
		if(oConfiguration==null || oConfiguration.get(mic)==null)
			return;
		
		oConfiguration.get(mic).remove(objName);
		if(hConfiguration==null || hConfiguration.get(mic)==null)
			return;
		hConfiguration.get(mic).remove(objName);
	}

	/**
	 * Assign configuration of given type to microscope, if not still exists.
	 * @param micName
	 * @param c
	 */
	public void addConfiguration(String micName,String objectType,ModuleConfiguration conf) {
		if(oConfiguration==null) {
			oConfiguration=new LinkedHashMap<>();
		}
		if(oConfiguration.get(micName)==null) {
			HashMap<String,ModuleConfiguration> list=new HashMap<>();
			list.put(objectType,conf);
			oConfiguration.put(micName, list);
		}else {
			if(!configurationExists(micName, objectType)) {
				this.oConfiguration.get(micName).put(objectType, conf);
			}
		}
	}
	
	/**
	 * Assign/replace configuration to/for given microscope.
	 * @param micName
	 * @param c
	 */
	public void setConfiguration(String micName,String objectType,ModuleConfiguration conf) {
		if(oConfiguration==null) {
			oConfiguration=new LinkedHashMap<>();
		}
		if(oConfiguration.get(micName)==null) {
			HashMap<String,ModuleConfiguration> list=new HashMap<>();
			list.put(objectType,conf);
			oConfiguration.put(micName, list);
		}else {
			this.oConfiguration.get(micName).put(objectType, conf);
		}
	}
	
	public boolean contentExists(String type) {
		if(this.oDefinition!=null) {
			if(oDefinition.get(type)!=null ) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean isContainer(String type) {
		if(oDefinition==null || oDefinition.get(type)==null)
			return null;
		return oDefinition.get(type).isContainer();
	}
	
	public boolean configurationExists(String micName,String type) {
		if(micName.equals(UNIVERSAL) && oDefinition!=null && oDefinition.get(type)!=null) {
			return true;
		}
			
		if(this.oConfiguration!=null) {
			if(oConfiguration.get(micName)!=null && oConfiguration.get(micName).containsKey(type)) {
				return true;
			}
		}
		return false;
	}
	
	public void addConfiguration(String micName,HashMap<String,ModuleConfiguration> contList) {
		if(this.oConfiguration==null) {
			oConfiguration=new LinkedHashMap<>();
		}
		
		this.oConfiguration.put(micName, contList);
		
		//new mic add also to hardwareConf
		if(hConfiguration==null)
			hConfiguration=new LinkedHashMap<>();
		
		if(!hConfiguration.containsKey(micName))
			hConfiguration.put(micName, null);
	}

	private void printDefinedObjects() {
		if(oDefinition==null)
			ImporterAgent.getRegistry().getLogger().debug(this, "-- PRINT Content List: list is empty");
		else {
			for(Map.Entry<String, ModuleContent> entry:oDefinition.entrySet()) {
				if(entry.getValue()!=null && entry.getValue().getList()!=null) {
					entry.getValue().print();
				}else {
					ImporterAgent.getRegistry().getLogger().debug(this, "-- PRINT ModuleContent: Content "+entry.getKey()+" elements: 0");
				}
			}
		}
	}

	/**
	 * Get layout of type for selected microscope.
	 * @param micName
	 * @param type
	 * @return
	 */
	public ModuleConfiguration getConfiguration(String micName,String type) {
		if(oConfiguration!=null && !oConfiguration.isEmpty()&& oConfiguration.get(micName)!=null) {
			return oConfiguration.get(micName).get(type);
		}
		return null;
	}
	
	/**
	 * Get copy of content of type for selected microscope and specified layout.
	 * If no layout specification available means the object is not visible for selected microscope
	 * -> return null.
	 * @param micName
	 * @param type
	 * @return
	 */
	public ModuleContent getContent(String micName,String type) {
		if(oDefinition== null || oDefinition.get(type)==null) {
			if(!type.equals(TagNames.OME_ROOT))
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] content of type "+type+" NOT exists!");
			return null;
		}
		if(micName.equals(UNIVERSAL)) {
			return new ModuleContent(oDefinition.get(type));
		}
		
		ModuleContent content=new ModuleContent(oDefinition.get(type));
		if(oConfiguration!=null && !oConfiguration.isEmpty()) {
			// get tagdataprop for this mic
			HashMap<String, ModuleConfiguration> map =oConfiguration.get(micName);
			if(map==null || map.get(type)==null) {
				ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] Configurator: does not exists:  content of type: "+type+"::"+micName);
				return null;
			}
			content.setProperties(map.get(type));
			return content;
		}
		ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] Configurator: objectConfiguration is empty");
		return null;
	}
	
	private HashMap<String,ModuleContent> getContentList(String micName) {
		if(oDefinition== null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] NO object specifications exists!");
			return null;
		}
		if(micName.equals(UNIVERSAL)) {
			return oDefinition;
			
		}
		if(oConfiguration!=null && !oConfiguration.isEmpty() && oConfiguration.get(micName)!=null) {
			HashMap<String, ModuleContent> list=new HashMap<>();
			for(Entry<String,ModuleConfiguration> entry : oConfiguration.get(micName).entrySet()) {
				ModuleContent cont=new ModuleContent(oDefinition.get(entry.getKey()));
				cont.setProperties(entry.getValue());
				list.put(entry.getKey(), cont);
			}
			return list;
		}
		return null;
	}
	
	public HashMap<String,ModuleContent> getAvailableContentList(String mic) {
		return getContentList(mic);
	}
	
	public String[] getMicNames() {
		if(oConfiguration!=null) {
			return oConfiguration.keySet().toArray(new String[oConfiguration.size()]);
		}
		return null;
	}
	
	public void parse(String configPath) {
		XMLWriter writer=new XMLWriter();
		writer.parseConfiguration(configPath);
		hConfiguration=writer.getHardwareConfiguration();
		oConfiguration=writer.getObjectConfiguration();
		oDefinition=writer.getObjectDefinition();
	}
	
	public void writeToFile(String configPath) {
		XMLWriter writer=new XMLWriter();
		writer.saveToXML(hConfiguration,oDefinition,oConfiguration,configPath);
	}

	/**
	 * Returns list of available object names stored under Micrsocope=UNIVERSAL
	 * @return
	 */
	public String[] getNameOfObjects() {
		if(oDefinition==null || oDefinition.isEmpty())
			return null;
		
		return oDefinition.keySet().toArray(new String[oDefinition.size()]);
	}

	//TODO read out tree from conf file
	/**
	 * return standard tree for universal and adapted standard tree for others.
	 * @return
	 */
	public DefaultMutableTreeNode getTree(String micName) {
		return getStandardTree(micName);
	}

	public void printObjects(String mic) {
		ImporterAgent.getRegistry().getLogger().debug(this, "------------  Objects for "+mic+" -------------");
	
		if(mic.equals(UNIVERSAL)) {
			for(Entry<String, ModuleContent> entry : oDefinition.entrySet()) {
				ImporterAgent.getRegistry().getLogger().debug(this, "\t"+entry.getKey());
			}
		}else {
			if(oConfiguration==null || !oConfiguration.containsKey(mic) || oConfiguration.get(mic)==null) {
				ImporterAgent.getRegistry().getLogger().debug(this, "\tNONE");
				return;
			}
			for(Entry<String, ModuleConfiguration> entry : oConfiguration.get(mic).entrySet()) {
				ImporterAgent.getRegistry().getLogger().debug(this, "\t"+entry.getKey());
			}
		}
	}

	//TODO
	public void createNewObject(String mic, String obj) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * Create standard ome tree

 * @return 	 */
	private DefaultMutableTreeNode getStandardTree(String micName) {
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] load standard tree for "+micName);
		DefaultMutableTreeNode tree = new DefaultMutableTreeNode(new ModuleTreeElement(null, null));
		ModuleTreeElement img = createElement(TagNames.OME_ELEM_IMAGE,standardTree,micName);
		if(img!=null) {
			DefaultMutableTreeNode image= new DefaultMutableTreeNode(img);
			tree.add(image);
			
			ModuleTreeElement obj = createElement(TagNames.OME_ELEM_OBJECTIVE,image,micName);
			if(obj!=null) image.add(new DefaultMutableTreeNode(obj));

			ModuleTreeElement imgEnv=createElement(TagNames.OME_ELEM_IMGENV,image,micName);
			if(imgEnv!=null) image.add(new DefaultMutableTreeNode(imgEnv));

			ModuleTreeElement channel = createElement(TagNames.OME_ELEM_CHANNEL,image,micName);
			if(channel!=null) {
				DefaultMutableTreeNode ch = new DefaultMutableTreeNode(channel);
				image.add(ch);

				ModuleTreeElement detector = createElement(TagNames.OME_ELEM_DETECTOR,ch,micName);
				if(detector!=null) ch.add(new DefaultMutableTreeNode(detector));

				ModuleTreeElement lightSrc = createElement(TagNames.OME_ELEM_LIGHTSOURCE,ch,micName);
				if(lightSrc!=null) {
					DefaultMutableTreeNode lightSrcNode = new DefaultMutableTreeNode(lightSrc);
					ch.add(lightSrcNode); 
					
					ModuleTreeElement laser = createElement(TagNames.OME_ELEM_LASER,lightSrcNode,micName);
					if(laser!=null) lightSrcNode.add(new DefaultMutableTreeNode(laser));
				}

				ModuleTreeElement lp = createElement(TagNames.OME_ELEM_LIGHTPATH,ch,micName);
				if(lp!=null) {
					DefaultMutableTreeNode lpNode = new DefaultMutableTreeNode(lp);
					ch.add(lpNode);

					ModuleTreeElement lpEx = createElement(TagNames.OME_ELEM_LIGHTPATH_EX,lpNode,micName);
					if(lpEx!=null) {
						DefaultMutableTreeNode lpExNode = new DefaultMutableTreeNode(lpEx);
						lpNode.add(lpExNode);
						ModuleTreeElement filterEx = createElement(TagNames.OME_ELEM_FILTER,lpExNode,micName);
						if(filterEx!=null) lpExNode.add(new DefaultMutableTreeNode(filterEx));
					}
					ModuleTreeElement dich = createElement(TagNames.OME_ELEM_DICHROIC,lpNode,micName);
					if(dich!=null) lpNode.add(new DefaultMutableTreeNode(dich));
					
					ModuleTreeElement lpEm = createElement(TagNames.OME_ELEM_LIGHTPATH_EM,lpNode,micName);
					if(lpEm!=null) {
						DefaultMutableTreeNode lpEmNode = new DefaultMutableTreeNode(lpEm);
						lpNode.add(lpEmNode);

						ModuleTreeElement filterEm = createElement(TagNames.OME_ELEM_FILTER,lpEmNode,micName);
						if(filterEm!=null) lpEmNode.add(new DefaultMutableTreeNode(filterEm));
					}
				}
				
			}
		}
		ModuleTreeElement exp=createElement(TagNames.OME_ELEM_EXPERIMENT, standardTree,micName);
		if(exp!=null) {
			DefaultMutableTreeNode experiment = new DefaultMutableTreeNode(exp);
			tree.add(experiment);
		}
		
		
		
		return tree;
		
	}
	
	public ModuleTreeElement createElement(String type,DefaultMutableTreeNode parent,String micName) {
		if(getContent(UNIVERSAL,type)==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] No object content configurate for type  "+type+"[MDEConfiguration::createElement]");
			return null;
		}
		return new ModuleTreeElement(type,null,"",getContent(micName,type),parent);
	}

	
	
}
