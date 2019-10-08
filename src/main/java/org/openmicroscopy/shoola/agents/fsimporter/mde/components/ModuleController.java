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
import org.openmicroscopy.shoola.util.MonitorAndDebug;


/**
 * Singleton class
 * Holds definition of available modules, standard ome tree and hardware specific module data.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleController {

	private static ModuleController self=new ModuleController();
	
	/* standard ome tree */
	private DefaultMutableTreeNode standardTree;
	
//	private LinkedHashMap<String, ObjectTable> hardwareTables;

	private MDEConfiguration mdeConf;
	private String micName;
	
	/**
	 * Constructor for dir element
	 */
	public ModuleController(){}
	
	public static ModuleController getInstance() {
		return self;
	}
	
	/**
	 * Create standard ome tree
	 */
	private void initStandardTree() {
		
		standardTree = new DefaultMutableTreeNode(new ModuleTreeElement(null, null));
		ModuleTreeElement img = createElement(TagNames.OME_ELEM_IMAGE,standardTree);
//		img.printContent();
		DefaultMutableTreeNode image= new DefaultMutableTreeNode(img);
		standardTree.add(image);
		
		ModuleTreeElement exp=createElement(TagNames.OME_ELEM_EXPERIMENT, standardTree);
		DefaultMutableTreeNode experiment = new DefaultMutableTreeNode(exp);
		standardTree.add(experiment);
		
		ModuleTreeElement obj = createElement(TagNames.OME_ELEM_OBJECTIVE,image);
		image.add(new DefaultMutableTreeNode(obj));
		
		ModuleTreeElement imgEnv=createElement(TagNames.OME_ELEM_IMGENV,image);
		image.add(new DefaultMutableTreeNode(imgEnv));
		
		ModuleTreeElement channel = createElement(TagNames.OME_ELEM_CHANNEL,image);
		DefaultMutableTreeNode ch = new DefaultMutableTreeNode(channel);
		image.add(ch);
		
		ModuleTreeElement detector = createElement(TagNames.OME_ELEM_DETECTOR,ch);
		ch.add(new DefaultMutableTreeNode(detector));
		
		ModuleTreeElement lightSrc = createElement(TagNames.OME_ELEM_LIGHTSOURCE,ch);
		DefaultMutableTreeNode lightSrcNode = new DefaultMutableTreeNode(lightSrc);
		ch.add(lightSrcNode); 
		
		ModuleTreeElement laser = createElement(TagNames.OME_ELEM_LASER,lightSrcNode);
		lightSrcNode.add(new DefaultMutableTreeNode(laser));
		
		ModuleTreeElement lp = createElement(TagNames.OME_ELEM_LIGHTPATH,ch);
		DefaultMutableTreeNode lpNode = new DefaultMutableTreeNode(lp);
		ch.add(lpNode);
		
		ModuleTreeElement lpEx = createElement(TagNames.OME_ELEM_LIGHTPATH_EX,lpNode);
		DefaultMutableTreeNode lpExNode = new DefaultMutableTreeNode(lpEx);
		lpNode.add(lpExNode);
		ModuleTreeElement dich = createElement(TagNames.OME_ELEM_DICHROIC,lpNode);
		lpNode.add(new DefaultMutableTreeNode(dich));
		ModuleTreeElement lpEm = createElement(TagNames.OME_ELEM_LIGHTPATH_EM,lpNode);
		DefaultMutableTreeNode lpEmNode = new DefaultMutableTreeNode(lpEm);
		lpNode.add(lpEmNode);
		ModuleTreeElement filterEx = createElement(TagNames.OME_ELEM_FILTER,lpExNode);
		lpExNode.add(new DefaultMutableTreeNode(filterEx));
		ModuleTreeElement filterEm = createElement(TagNames.OME_ELEM_FILTER,lpEmNode);
		lpEmNode.add(new DefaultMutableTreeNode(filterEm));
		
	}
	
	public ModuleTreeElement createElement(String type,DefaultMutableTreeNode parent) {
		if(getContentOfType(type)==null) {
			System.out.println("-- ERROR: no object content found for type  "+type+"[ModuleController::createElement]");
		}
		return new ModuleTreeElement(type,null,"",getContentOfType(type),parent);
	}

	/**
	 * Return a copy of tree. If no tree specify in configuration file, return standard tree
	 * @return
	 */
	public DefaultMutableTreeNode getTree() {
		if(mdeConf.getTree()==null) {
			if(standardTree ==null) {
				System.out.println("-- ERROR: standard tree is not initialize [ModuleController::getTree]");
				return null;
			}
			System.out.println("-- load standard tree [ModuleController]");
			return ModuleTree.cloneTreeNode(standardTree);
		}else {
			return ModuleTree.cloneTreeNode(mdeConf.getTree());
		}
	}
	
	public HashMap<String,ModuleContent> getAvailableContent(){
		if(mdeConf==null)
			return initDefaultOMEObjects();
		return mdeConf.getAvailableContentList(getCurrentMicName());
	}

	/**
	 * Default OME objects content
	 * @return
	 */
	public HashMap<String,ModuleContent> initDefaultOMEObjects() {
		System.out.println("-- init DEFAULT content [ModuleController]");
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
			return mdeConf.getInstruments(micName);
		return null;
	}
	

	public ModuleList getInstrumentsForMic(String micName) {
		if(mdeConf!=null)
			return mdeConf.getInstruments(micName);
		return null;
		
	}
	
	/**
	 * @param instrumentType
	 * @return list of instruments for given type of current selected microscope
	 */
	public List<ModuleContent> getInstrumentsOfType(String instrumentType) {
		if(mdeConf!=null) {
			ModuleList val=mdeConf.getInstruments(micName);
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
			System.out.println("-- ERROR: no configuration available! [ModuleController::getContentOfType]");
			return null;
		}
		if(micName==null) {
			System.out.println("-- No microscope is given, return object define in UNIVERSAL [MDEController::getContentOfType]");
			return new ModuleContent(mdeConf.getContent(MDEConfiguration.UNIVERSAL, moduleType));
		}
		return new ModuleContent(mdeConf.getContent(micName, moduleType));
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
	
	
	public void initMDEConfiguration(String curMic) {
		setCurrentMicName(curMic);
		mdeConf=new MDEConfiguration();
		initStandardTree();
	}
	
	
	
	private HashMap<String, ModuleContent> cloneContent(HashMap<String, ModuleContent> defaultContent) {
		HashMap<String, ModuleContent> clone=new HashMap<>();
		for(Map.Entry<String, ModuleContent> entry:defaultContent.entrySet()) {
			clone.put(entry.getKey(), new ModuleContent(entry.getValue()));
		}
		return clone;
	}

	//TODO repaint MDEContent
	public void setMDEConfiguration(MDEConfiguration conf) {
		mdeConf=conf;
		mdeConf.printObjects(getCurrentMicName());
	}
	
	public MDEConfiguration getMDEConfiguration() {
		return mdeConf;
	}
	
	
	
	

	public String[] getPossibleChilds(String type) {
		ArrayList<String> list=new ArrayList<>();
		HashMap<String,ModuleContent> contentList=mdeConf.getAvailableContentList(getCurrentMicName());
		for(Entry<String, ModuleContent> entry: contentList.entrySet()) {
			if(entry.getValue().hasParent(type)) {
				list.add(entry.getKey());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public String getCurrentMicName() {
		return micName;
	}

	public String[] getMicNames() {
		if(mdeConf==null)
			return new String[] {mdeConf.UNIVERSAL};

		return mdeConf.getMicNames();
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
		MonitorAndDebug.printConsole("-- PRINT objects for selected category/microscope:");
		if(mdeConf== null) {
			return;
		}
		mdeConf.printObjects(micName);
	}
}
