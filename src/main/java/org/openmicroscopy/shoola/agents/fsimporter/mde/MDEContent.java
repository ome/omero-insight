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
package org.openmicroscopy.shoola.agents.fsimporter.mde;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ome.xml.model.*;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
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
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml.Channel;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml.DetectorSettings;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.DynamicModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleContentGUI;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;


/**
 * MDE main panel. Contains ModuleTree and ModuleContentGUI. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class MDEContent extends JPanel implements TreeSelectionListener{
	
	private DynamicModuleTree moduleTree;
	private JPanel moduleContentPanel;
	private ModuleController controller;
	private ModuleList fileInstrumentValues;
	private LinkedHashMap<String, ObjectTable> hardwareTables;
	
	// copy of initial file object tree
	private DefaultMutableTreeNode fileObjectTree;
	
	
	/**
	 * Build  module tree from given file
	 * @param root
	 * @param controller
	 */
	public MDEContent(OME ome, DefaultMutableTreeNode root,ModuleController controller,ActionListener listener) {
		super(new BorderLayout());
		
		this.controller = controller;
//		controller.printObjects();
		
		if(ome!=null) {
			moduleTree =new DynamicModuleTree(initTree(ome, root),listener);
			createInstrumentTables(fileInstrumentValues);

			moduleContentPanel = new JPanel(new BorderLayout());

			JScrollPane scrollView=new JScrollPane(moduleContentPanel);

			JSplitPane splitPane;		
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,moduleTree,scrollView);
			splitPane.setResizeWeight(0.5);
			splitPane.setDividerLocation(150);
			add(splitPane,BorderLayout.CENTER);

			selectModuleAction(moduleTree.selectFirstNode());

			moduleTree.addTreeSelectionListener(this);

			// save file tree for reset action of object tree
			fileObjectTree = ModuleTree.cloneTreeNode(getRootNode());
		}else {
			add(new JLabel("\tNot a valid microscopy file format!"), BorderLayout.CENTER);
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Build empty module tree from given node
	 * @param root
	 * @param controller
	 */
	public MDEContent(DefaultMutableTreeNode root, ModuleController controller,boolean isDir,ModuleList instrumentList,ActionListener listener) {
		super(new BorderLayout());
		this.controller = controller;
//		controller.printObjects();
		this.fileInstrumentValues=instrumentList;
		createInstrumentTables(instrumentList);
		moduleTree = root==null? new DynamicModuleTree(listener): new DynamicModuleTree(root,listener);
		moduleContentPanel = new JPanel(new BorderLayout());
		
		JScrollPane scrollView=new JScrollPane(moduleContentPanel);
		
		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,moduleTree,scrollView);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(150);
		add(splitPane,BorderLayout.CENTER);
		
		selectModuleAction(moduleTree.selectFirstNode());
		
		moduleTree.addTreeSelectionListener(this);
		revalidate();
		repaint();
	}

	
	
	/**
	 * Display content of module
	 * @param object
	 */
	private void selectModuleAction(DefaultMutableTreeNode object) {
		if(object!=null) {
			showModuleContent(object);
		}
	}
	
	
	private void showModuleContent(DefaultMutableTreeNode object) {
		//remove former content
		moduleContentPanel.removeAll();
		moduleContentPanel.add(new ModuleContentGUI(object,hardwareTables),BorderLayout.CENTER);
		moduleContentPanel.revalidate();
		moduleContentPanel.repaint();
		revalidate();
		repaint();
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) moduleTree.getLastSelectedPathComponent();
		if(selectedNode == null || !controller.configurationExists(((ModuleTreeElement)selectedNode.getUserObject()).getType()))
			return;
		try {
			selectModuleAction(selectedNode);
		}catch(Exception ex) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] module tree selection event: select root node?");
		}
		
	}
	
	public DefaultMutableTreeNode getInitialFileObjectRootNode() {
		return fileObjectTree;
	}
	
	public DefaultMutableTreeNode getRootNode() {
		if(moduleTree==null)
			return null;
		return moduleTree.getRootNode();
	}
	
	public ModuleTree getModuleTree() {
		if(moduleTree==null)
			return null;
		return moduleTree.getModuleTree();
	}
	
	/**
	 * Read ome-xml from image file and return tree generated from file 
	 * @param fName
	 */
	private DefaultMutableTreeNode initTree(OME ome,DefaultMutableTreeNode root) {
		if(ome ==null ) {
			// nothing to load
			return controller.getTree();
		}
		
		if(root==null) {
			// use defaultTree
			root=controller.getTree();//new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
		}
		for(int i=0; i<ome.sizeOfImageList() ;i++) {
			root=initImageContent(root,ome,i);
		}
		
		Experiment exp=null;
		Experimenter exper=null;
		if(ome.copyExperimentList()!=null && ome.copyExperimentList().size()>0)
			exp=ome.getExperiment(0);
		if(ome.copyExperimenterList()!=null && ome.copyExperimenterList().size()>0)
			exper=ome.getExperimenter(0);
		addContent(createContent(TagNames.OME_ELEM_EXPERIMENT, (new ExperimentConverter()).convertData(exp, exper)), 0, root);
		
		return root;
	}
	
	
	public ModuleContent createContent(String type,LinkedHashMap<String, TagData> data ) {
		ModuleContent content = controller.getContentOfType(type);
		if(content !=null)
			content.setAttributes(data);
		return content;
	}
	
	
	
	public DefaultMutableTreeNode addContent(ModuleContent c, int index, DefaultMutableTreeNode parent) {
		if(parent==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Can't add content. Tree is null [MDEContent::addContent]");
			return null;
		}
		if(c==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Can't add content - is null [MDEContent::addContent]");
			return null;
		}
		List<DefaultMutableTreeNode> childs=MDEHelper.getListOfChilds(c.getType(), parent);
		DefaultMutableTreeNode node=null;
		if(childs!=null && !childs.isEmpty()) {
			if(childs.size()<=index) { 
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] insert new subtree of type : "+c.getType());
				DefaultMutableTreeNode newChild=controller.cloneTreeStructure(childs.get(0), parent);
				parent.add(newChild);
				childs.add(index, newChild);
			}
			node = childs.get(index);
			ModuleContent newC=MDEHelper.completeData(((ModuleTreeElement) node.getUserObject()).getData(), c);
			((ModuleTreeElement)node.getUserObject()).setData(newC);
		}else {
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] Can't add content! No child nodes of given type "+c.getType()+" at "+parent.getUserObject().toString());
		}
		return node;
	}
	
	/**
	 * Read out file metadata
	 * @param tree
	 * @param ome
	 * @param i
	 * @return
	 */
	private DefaultMutableTreeNode initImageContent(DefaultMutableTreeNode tree, OME ome, int i)
	{
		if(ome.getImage(i)==null) {
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] image content is empty");
			return tree;
		}
		if(tree==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] No tree is given to init image content");
			return null;
		}
		
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Read file content index: "+i);
		List<Objective> objList=null;
		List<Detector> detectorList=null;
		List<LightSource> lightSourceList=null;
		List<Filter> filterList=null;
		List<Dichroic> dichList=null;
		List<Channel> chList=null;
		List<FilterSet> filtersetList=null;
		
		// convert ome annotation data to mdemodel data
		Image imgObj=ome.getImage(i);
		
		//read out list of instruments
		Instrument instruments=imgObj.getLinkedInstrument();
		if(instruments==null) {
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] no instruments are defined for image");
		}else {
			objList=instruments.copyObjectiveList();
			detectorList=instruments.copyDetectorList();
			lightSourceList=instruments.copyLightSourceList();
			filterList=instruments.copyFilterList();
			dichList=instruments.copyDichroicList();
//			filtersetList=instruments.getFilterSetList();
			
			// save instrument lists to listOfDefaultValues
			ModuleList hardwareList=new ModuleList();
			hardwareList.put(TagNames.OME_ELEM_OBJECTIVE, MDEParser.parseObjectiveList(objList,controller));
			hardwareList.put(TagNames.OME_ELEM_DETECTOR, MDEParser.parseDetectorList(detectorList,controller));
//			hardwareList.put(TagNames.OME_ELEM_LIGHTSOURCE, MDEParser.parseLightSourceList(lightSourceList,controller));
			hardwareList.put(TagNames.OME_ELEM_LASER, MDEParser.parseLightSourceLaser(lightSourceList,controller));
			hardwareList.put(TagNames.OME_ELEM_FILAMENT, MDEParser.parseLightSourceFilament(lightSourceList,controller));
			hardwareList.put(TagNames.OME_ELEM_ARC, MDEParser.parseLightSourceArc(lightSourceList,controller));
			hardwareList.put(TagNames.OME_ELEM_LED, MDEParser.parseLightSourceLED(lightSourceList,controller));
			hardwareList.put(TagNames.OME_ELEM_GENERICEXCITATIONSOURCE, MDEParser.parseLightSourceGES(lightSourceList,controller));
			hardwareList.put(TagNames.OME_ELEM_FILTER, MDEParser.parseFilterList(filterList,controller));
			hardwareList.put(TagNames.OME_ELEM_DICHROIC, MDEParser.parseDichroicList(dichList,controller));
			
			fileInstrumentValues=hardwareList;
		}
		
		DefaultMutableTreeNode currentNode=null;
		// add image content to tree
		currentNode =addContent(createContent(TagNames.OME_ELEM_IMAGE,(new ImageConverter()).convertData(imgObj)),i,tree);
		
		// load objective and objective settings annotations
		ObjectiveSettings os = imgObj.getObjectiveSettings();
		String id=os==null?"":os.getID();
		Objective o= getElementByID(objList, id);
		String oName=o!=null? o.getModel():null;
		
		addContent(createContent(TagNames.OME_ELEM_OBJECTIVE,(new ObjectiveConverter()).convertData(o,os)),0,currentNode);
		
		addContent(createContent(TagNames.OME_ELEM_IMGENV,(new ImagingEnvConverter()).convertData(null)),0,currentNode);
		
		//load channel data
		DefaultMutableTreeNode chNode=null;
		int idx=0;
		for(ome.xml.model.Channel c: imgObj.getPixels().copyChannelList()){
			chNode=addContent(createContent(TagNames.OME_ELEM_CHANNEL,(new ChannelConverter()).convertData(new Channel(c))),idx,currentNode);
			
			// load detector and detectorsettings annotations
			ome.xml.model.DetectorSettings ds = c.getDetectorSettings();
			String idDs=ds==null?"":ds.getID();
			Detector d=getElementByID(detectorList,idDs);
			DetectorSettings dsNew = (ds==null?null:new DetectorSettings(ds));
			String dName=d!=null?d.getModel():null;
			addContent(createContent(TagNames.OME_ELEM_DETECTOR,(new DetectorConverter()).convertData(d, dsNew)),0,chNode);
			
			//load lightSource and lightsourcesettings annotations
			LightSourceSettings ls =c.getLightSourceSettings();
			String idLs=ls==null?"":ls.getID();
			LightSource l=getElementByID(lightSourceList, idLs);
			String lSrcName=l!=null?l.getModel():null;
			if(l!=null) {
				DefaultMutableTreeNode lSrcNode=addContent(controller.getContentOfType(TagNames.OME_ELEM_LIGHTSOURCE),0,chNode);
			if(l instanceof Laser) {
			addContent(createContent(TagNames.OME_ELEM_LASER,(new LaserConverter()).convertData((Laser)l, ls)),0,lSrcNode);
			}else if(l instanceof Arc) {
				addContent(createContent(TagNames.OME_ELEM_ARC,(new ArcConverter()).convertData((Arc)l, ls)),0,lSrcNode);	
			}else if(l instanceof Filament) {
				addContent(createContent(TagNames.OME_ELEM_FILAMENT,(new FilamentConverter()).convertData((Filament)l, ls)),0,lSrcNode);	
			}else if(l instanceof LightEmittingDiode) {
				addContent(createContent(TagNames.OME_ELEM_LED,(new LEDConverter()).convertData((LightEmittingDiode)l, ls)),0,lSrcNode);	
			}else if(l instanceof GenericExcitationSource) {
				addContent(createContent(TagNames.OME_ELEM_GENERICEXCITATIONSOURCE,(new GenericExcitationSourceConverter()).convertData((GenericExcitationSource)l, ls)),0,lSrcNode);	
			}
			}
			
			LightPath lp=c.getLightPath();
			FilterSet fs=c.getLinkedFilterSet();
			
			if(lp!=null) {
				DefaultMutableTreeNode lpNode = addContent(controller.getContentOfType(TagNames.OME_ELEM_LIGHTPATH),0,chNode);
				// add LightPath
				if(lp.sizeOfLinkedExcitationFilterList() >0) {
					// add excitation filter
					createNode(TagNames.OME_ELEM_LIGHTPATH_EX,lp.copyLinkedExcitationFilterList(),filterList,lpNode);
				}
				Dichroic dich=lp.getLinkedDichroic();
				if(dich!=null) {
					addContent(createContent(TagNames.OME_ELEM_DICHROIC,(new DichroicConverter()).convertData(dich)),0,lpNode);
				}
				if(lp.sizeOfLinkedEmissionFilterList() >0) {
					// add emission filter
					createNode(TagNames.OME_ELEM_LIGHTPATH_EX,lp.copyLinkedEmissionFilterList(),filterList,lpNode);
				}
				
			}
			
			if(fs!=null) {
				//TODO container or not?
//				DefaultMutableTreeNode fsNode = addContent(createContent(TagNames.OME_ELEM_LIGHTPATH_FS,(new FilterSetConverter()).convertData(fs)),0,chNode);
//				
//				List<Filter> fs_exc=fs.copyLinkedExcitationFilterList();
//				List<Filter> fs_em=fs.copyLinkedEmissionFilterList();
//				
//				if(fs_exc!=null && fs_exc.size() >0) {
//					createNode(TagNames.OME_ELEM_LIGHTPATH_EX,fs_exc,filterList,fsNode);
//				}
//				Dichroic dich=fs.getLinkedDichroic();
//				if(dich!=null) {
//					addContent(createContent(TagNames.OME_ELEM_DICHROIC,(new DichroicConverter()).convertData(dich)),0,fsNode);
//				}
//				if(fs_em!=null && fs_em.size() >0) {
//					createNode(TagNames.OME_ELEM_LIGHTPATH_EM,fs_em,filterList,fsNode);
//				}
			}
			idx++;
		}
		
		return tree;
	}
	

	
	private <T extends ManufacturerSpec> T getElementByID(List<T> list,String id)
	{
		int result=-1;
		if(id==null || list==null)
			return null;
		for(int i=0; i<list.size(); i++){
			if(getElementID(list.get(i)).equals(id)){
				result=i;
				break;
			}
		}
		if(result!=-1) {
			return (T)list.get(result);
		}
		if(list.size()==1) {
			return (T)list.get(0);
		}
		return null;
	}
	
	private <T extends ManufacturerSpec> String getElementID(T object)
	{
		if(object ==null)
			return "";
		if(object instanceof Objective)
			return ((Objective)object).getID();
		else if(object instanceof Detector)
			return ((Detector)object).getID();
		else if(object instanceof LightSource)
			return ((LightSource)object).getID();
		
		return "";
	}
	
	private DefaultMutableTreeNode createNode(String type,List<Filter> reflist,List<Filter> filterList,DefaultMutableTreeNode p) {
		DefaultMutableTreeNode listNode = addContent(controller.getContentOfType(type),0,p);
		int idx=0;
		for(Filter f:reflist) {
			String thisType = TagNames.OME_ELEM_FILTER;
			ModuleContent content = controller.getContentOfType(thisType);
			if(content !=null)
				content.setAttributes((new FilterConverter()).convertData(f));
			addContent(content,idx,listNode);
			idx++;
		}
		return listNode;
	}
	
	public void setInstrumentList(ModuleList list) {
		fileInstrumentValues=list;
	}
	public ModuleList getInstrumentList() {
		return fileInstrumentValues;
	}
	
	
	private void createInstrumentTables(ModuleList fileInstruments) {
		
		if(hardwareTables==null)
			hardwareTables=new LinkedHashMap<>();
		else
			hardwareTables.clear();
		
		if(fileInstruments!=null) {
			// create tables with predefined objects in image container and mde config file
			for (Entry<String, List<ModuleContent>> entry : fileInstruments.entrySet()) {
				String key = entry.getKey();
				List<ModuleContent> hardware=new ArrayList<>();
				List<ModuleContent> valFile = entry.getValue();
				List<ModuleContent> values = controller.getInstrumentsOfType(key); 
				ObjectTable objTable=null;
				if(valFile!=null) {
					hardware.addAll(valFile);
					if(values!=null) {
						//merge fileInstruments and hardware stations
						hardware.addAll(values);
					}
					objTable=new ObjectTable(hardware);

				}else {
					objTable= new ObjectTable(values);
				}
				hardwareTables.put(key,objTable );
			}
		}else {
			// create tables with predefined objects of mde config file
			ModuleList mList=controller.getInstrumentsForCurrentMic();
			if(mList!=null) {
				for (Entry<String, List<ModuleContent>> entry : mList.entrySet()) {
					String key = entry.getKey();
					List<ModuleContent> values = entry.getValue();
					if(values!=null) {
						hardwareTables.put(key, new ObjectTable(values));
					}
				}
			}
		}
	}
	

}
