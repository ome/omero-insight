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

import java.awt.Cursor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEParser;

import ome.model.units.UnitEnum;
import ome.xml.model.Filter;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.MDEConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ArcConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ChannelConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DetectorConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DichroicConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ExperimentConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.FilamentConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.FilterConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.FilterSetConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.GenericExcitationSourceConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ImageConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ImagingEnvConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.LEDConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.LaserConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ObjectiveConverter;



/**
 * Singleton class
 * Holds definition of available modules, standard ome tree and hardware specific module data.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleController {

	private static ModuleController self=new ModuleController();
	
//	private LinkedHashMap<String, ObjectTable> hardwareTables;

	private MDEConfiguration mdeConf;
	private String micName;
	private String mdeConfigPath;
	
	/**
	 * Constructor for dir element
	 */
	public ModuleController(){}
	
	public static ModuleController getInstance() {
		return self;
	}
	
	
	
	public ModuleTreeElement createElement(String type,DefaultMutableTreeNode parent) {
		if(getContentOfType(type)==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] no object content found for type  "+type+"[ModuleController::createElement]");
		}
		return new ModuleTreeElement(type,null,"",getContentOfType(type),parent);
	}

	/**
	 * Return a copy of tree. If no tree specify in configuration file, return standard tree
	 * @return
	 */
	public DefaultMutableTreeNode getTree() {
		
		return ModuleTree.cloneTreeNode(mdeConf.getTree(micName));
	}
	
	public HashMap<String,ModuleContent> getAvailableContent(){
		if(mdeConf==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] mdeConf not available");
			return initDefaultOMEObjects();
		}
		return mdeConf.getAvailableContentList(getCurrentMicName());
	}

	/**
	 * Default OME objects content
	 * @return
	 */
	public HashMap<String,ModuleContent> initDefaultOMEObjects() {
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] init DEFAULT content [ModuleController]");
		HashMap<String,ModuleContent> defaultContent = new HashMap();
		
		String thisType =TagNames.OME_ELEM_IMAGE;
		ModuleContent img= new ModuleContent((new ImageConverter()).convertData(null),thisType,
				TagNames.getParents(thisType));
		defaultContent.put(thisType, img);
		
		thisType=TagNames.OME_ELEM_EXPERIMENT;
		ModuleContent exp= new ModuleContent((new ExperimentConverter()).convertData(null,null),thisType,
				TagNames.getParents(thisType));
		defaultContent.put(thisType, exp);
		
		thisType=TagNames.OME_ELEM_OBJECTIVE;
		ModuleContent obj=new ModuleContent((new ObjectiveConverter()).convertData(null, null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,obj);
		
		thisType=TagNames.OME_ELEM_DETECTOR;
		ModuleContent detector=new ModuleContent((new DetectorConverter()).convertData(null, null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,detector);
		
		thisType=TagNames.OME_ELEM_CHANNEL;
		ModuleContent channel=new ModuleContent((new ChannelConverter()).convertData(null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,channel);
		
		thisType=TagNames.OME_ELEM_LASER;
		ModuleContent laser=new ModuleContent((new LaserConverter()).convertData(null,null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,laser);
		
		thisType=TagNames.OME_ELEM_FILAMENT;
		ModuleContent fila=new ModuleContent((new FilamentConverter()).convertData(null,null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,fila);
		
		thisType=TagNames.OME_ELEM_ARC;
		ModuleContent arc=new ModuleContent((new ArcConverter()).convertData(null,null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,arc);
		
		thisType=TagNames.OME_ELEM_LED;
		ModuleContent led=new ModuleContent((new LEDConverter()).convertData(null,null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,led);
		
		thisType=TagNames.OME_ELEM_GENERICEXCITATIONSOURCE;
		ModuleContent ges=new ModuleContent((new GenericExcitationSourceConverter()).convertData(null,null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,ges);
		
		thisType=TagNames.OME_ELEM_FILTER;
		ModuleContent filter=new ModuleContent((new FilterConverter()).convertData((Filter)null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,filter);
		
		thisType=TagNames.OME_ELEM_DICHROIC;
		ModuleContent dich=new ModuleContent((new DichroicConverter()).convertData(null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,dich);
		
		thisType=TagNames.OME_ELEM_IMGENV;
		ModuleContent imEnv=new ModuleContent((new ImagingEnvConverter()).convertData(null),
				thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,imEnv);
		
		//container
		thisType=TagNames.OME_ELEM_LIGHTSOURCE;
		ModuleContent lSrc=new ModuleContent(null,thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,lSrc);
		
		thisType=TagNames.OME_ELEM_LIGHTPATH;
		ModuleContent lp=new ModuleContent(null,thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,lp);
		
		thisType=TagNames.OME_ELEM_LIGHTPATH_EM;
		ModuleContent lpEm=new ModuleContent(null,thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,lpEm);
		
		thisType=TagNames.OME_ELEM_LIGHTPATH_EX;
		ModuleContent lpEx=new ModuleContent(null,thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,lpEx);
		
//		thisType=TagNames.OME_ELEM_LIGHTPATH_FS;
//		ModuleContent lpFs=new ModuleContent(null,thisType,TagNames.getParents(thisType));
//		defaultContent.put(thisType,lpFs);
		
		//TODO: OME:Microscope, Filterset,
		return defaultContent;
		
	}
	
	
	public ModuleList getInstrumentsForCurrentMic() {
		if(mdeConf!=null)
			return mdeConf.getPredefinitions(micName);
		return null;
	}
	

	public ModuleList getInstrumentsForMic(String micName) {
		if(mdeConf!=null)
			return mdeConf.getPredefinitions(micName);
		return null;
		
	}
	
	/**
	 * @param instrumentType
	 * @return list of instruments for given type of current selected microscope
	 */
	public List<ModuleContent> getInstrumentsOfType(String instrumentType) {
		if(mdeConf!=null) {
			ModuleList val=mdeConf.getPredefinitions(micName);
			if(val!=null) {
				return val.get(instrumentType);
			}
		}
		
		return null;
	}
	
	
	/**
	 * @param moduleType
	 * @return copy of content for given module type
	 */
	public ModuleContent getContentOfType(String moduleType) {
		
		if(mdeConf==null ) {
			ImporterAgent.getRegistry().getLogger().error(this,"[MDE] no configuration available!");
			return null;
		}
		if(micName==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] No microscope is given, return object define in UNIVERSAL [MDEController::getContentOfType]");
			return mdeConf.getContent(MDEConfiguration.UNIVERSAL, moduleType);
		}
		return mdeConf.getContent(micName, moduleType);
	}
	
	//TODO docu
	public DefaultMutableTreeNode cloneTreeStructure(DefaultMutableTreeNode node,DefaultMutableTreeNode p) {
		DefaultMutableTreeNode cloneNode = null;
		
		cloneNode=new DefaultMutableTreeNode(ModuleController.getInstance().createElement(((ModuleTreeElement) node.getUserObject()).getType(), p));
//				new ModuleTreeElement(getContentOfType(((ModuleTreeElement) node.getUserObject()).getType()),p));
		for(int i = 0 ; i < node.getChildCount(); i++) {
			cloneNode.add(cloneTreeStructure((DefaultMutableTreeNode) node.getChildAt(i),cloneNode));
		}
		return cloneNode;
	}
	
	
	
	
	public void setCurrentMicName(String micName) {
		this.micName=micName;
	}
	
	
	public void initMDEConfiguration(String curMic,String configPath) {
		setCurrentMicName(curMic);
		mdeConfigPath=configPath;
		mdeConf=new MDEConfiguration(configPath);
	}
	
	
	
	private HashMap<String, ModuleContent> cloneContent(HashMap<String, ModuleContent> defaultContent) {
		HashMap<String, ModuleContent> clone=new HashMap<>();
		for(Map.Entry<String, ModuleContent> entry:defaultContent.entrySet()) {
			clone.put(entry.getKey(), new ModuleContent(entry.getValue()));
		}
		return clone;
	}

	public void setMDEConfiguration(MDEConfiguration conf) {
		mdeConf=conf;
//		mdeConf.printObjects(getCurrentMicName());
	}
	
	public MDEConfiguration getMDEConfiguration() {
		if(mdeConf==null) {
			initMDEConfiguration(getCurrentMicName(),mdeConfigPath);
		}
		return mdeConf;
	}
	
	
	
	

	public String[] getPossibleChilds(String type) {
		ArrayList<String> list=new ArrayList<>();
		if(mdeConf!=null) {
			HashMap<String,ModuleContent> contentList=mdeConf.getAvailableContentList(getCurrentMicName());
			if(contentList!=null) {
				for(Entry<String, ModuleContent> entry: contentList.entrySet()) {
					if(entry.getValue().hasParent(type)) {
						list.add(entry.getKey());
					}
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public String getCurrentMicName() {
		return micName;
	}

	/**
	 * @return list of  [UNIVERSAL, defined microscopes...].
	 */
	public String[] getMicNames() {
		if(mdeConf!=null) {
			String[] micNames = mdeConf.getMicNames();
			if(micNames!=null) {
				String[] list=new String[micNames.length+1];
				list[0]=mdeConf.UNIVERSAL;
				for(int i=0; i<micNames.length;i++) {
					list[i+1]=micNames[i];
				}
				return list;
			}
		}
		return new String[]{mdeConf.UNIVERSAL};
	}
	/**
	 * 
	 * @param microscope name
	 * @return index of given microscope in array availableMics
	 */
	public int getMicIndex(String microscope) {
		return Arrays.asList(getMicNames()).indexOf(microscope);
	}

	// see https://docs.openmicroscopy.org/omero/5.4.0/developers/Model/Units.html
	public String getStandardUnitSymbolByName(String parent,String tagName) {
		if(mdeConf==null)
			return "";
		return mdeConf.getStandardUnitSymbolByName(parent, tagName);
	}

	public void printObjects() {
		ImporterAgent.getRegistry().getLogger().debug(this, "-- PRINT objects for selected category/microscope:");
		if(mdeConf== null) {
			return;
		}
		mdeConf.printObjects(micName);
	}

	public boolean configurationExists(String type) {
		if(mdeConf!=null) {
			return mdeConf.configurationExists(getCurrentMicName(), type);
		}
		return false;
	}
}
