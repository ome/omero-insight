package org.openmicroscopy.shoola.agents.fsimporter.mde;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ome.xml.model.*;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ArcConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ChannelConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DetectorConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DichroicConverter;
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
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;



public class MDEContent extends JPanel implements TreeSelectionListener{
	
	private DynamicModuleTree moduleTree;
	private JPanel moduleContentPanel;
	private ModuleController controller;
	private ModuleList fileInstrumentValues;
	private LinkedHashMap<String, ObjectTable> hardwareTables;
	
	
	/**
	 * Build  module tree from given file
	 * @param root
	 * @param controller
	 */
	public MDEContent(OME ome, DefaultMutableTreeNode root,ModuleController controller) {
		super(new BorderLayout());
		
		System.out.println("-- create Content from file [MDEContent]");
		this.controller = controller;
		controller.printObjects();
		moduleTree =new DynamicModuleTree(initTree(ome, root),controller);
		createInstrumentTables(fileInstrumentValues);
		
		moduleContentPanel = new JPanel(new BorderLayout());
		showModuleContent(moduleTree.getRootNode());
		
		JScrollPane scrollView=new JScrollPane(moduleContentPanel);
		
		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,moduleTree,scrollView);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(150);
		add(splitPane,BorderLayout.CENTER);
		
//		add(moduleTree,BorderLayout.WEST);
//		add(scrollView,BorderLayout.CENTER);
		
		selectModuleAction(moduleTree.selectFirstNode());
		
		moduleTree.addTreeSelectionListener(this);
		revalidate();
		repaint();
	}
	
	/**
	 * Build empty module tree from given node
	 * @param root
	 * @param controller
	 */
	public MDEContent(DefaultMutableTreeNode root, ModuleController controller,boolean isDir,ModuleList instrumentList) {
		super(new BorderLayout());
		System.out.println("-- create Content from given tree [MDEContent]");
		this.controller = controller;
		controller.printObjects();
		this.fileInstrumentValues=instrumentList;
		createInstrumentTables(instrumentList);
		moduleTree = root==null? new DynamicModuleTree(controller): new DynamicModuleTree(root,controller);
		moduleContentPanel = new JPanel(new BorderLayout());
		showModuleContent(root);
		
		JScrollPane scrollView=new JScrollPane(moduleContentPanel);
		
		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,moduleTree,scrollView);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(150);
		add(splitPane,BorderLayout.CENTER);
		
//		add(moduleTree,BorderLayout.WEST);
//		add(scrollView,BorderLayout.CENTER);
		
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
			System.out.println("-- select module: "+object.getUserObject().toString()+", childs: "+object.getChildCount());
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
		if(selectedNode == null )
			return;
		try {
			System.out.println("-- module tree selection event at "+selectedNode.getPath().toString());
//			ModuleTreeElement object=(ModuleTreeElement) selectedNode.getUserObject();
			selectModuleAction(selectedNode);
		}catch(Exception ex) {
			System.out.println("Select root node?");
		}
		
	}
	
	public DefaultMutableTreeNode getRootNode() {
		if(moduleTree==null)
			return null;
		return moduleTree.getRootNode();
	}
	
	
	/**
	 * Read ome-xml from image file and return tree generated from file 
	 * @param fName
	 */
	private DefaultMutableTreeNode initTree(OME ome,DefaultMutableTreeNode root) {
		if(ome ==null ) {
			System.out.println("-- ome is null - load empty standard ome tree"); 
			return controller.getTree();
		}
		
		
		if(root==null) {
			System.out.println("-- init content from file: use defaultTree ");
			root=controller.getTree();//new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
		}
		for(int i=0; i<ome.sizeOfImageList() ;i++) {
			root=initImageContent(root,ome,i);
		}
		
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
			System.out.println("ERROR: parent is null [MDEContent::addContent]");
			return null;
		}
		if(c==null) {
			System.out.println("ERROR: content is null [MDEContent::addContent]");
			return null;
		}
		List<DefaultMutableTreeNode> childs=MDEHelper.getListOfChilds(c.getType(), parent);
		DefaultMutableTreeNode node=null;
		if(childs!=null && !childs.isEmpty()) {
			System.out.println("-- addContent of type "+c.getType()+",childlist : "+childs.size()+", index: "+index);
			if(childs.size()<=index) { 
				System.out.println("--insert new subtree of type : "+c.getType());
				DefaultMutableTreeNode newChild=controller.cloneTreeStructure(childs.get(0), parent);
				parent.add(newChild);
				childs.add(index, newChild);
			}
			node = childs.get(index);
			ModuleContent newC=MDEHelper.completeData(((ModuleTreeElement) node.getUserObject()).getData(), c);
			((ModuleTreeElement)node.getUserObject()).setData(newC);
		}else {
			System.out.println("ERROR: Can't find child nodes of given type "+c.getType()+" at "+parent.getUserObject().toString());
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
			System.out.println("-- image content is empty");
			return tree;
		}
		if(tree==null) {
			System.out.println("ERROR: no tree is given");
		}
		
		System.out.println("--Read file content [ModuleController::initImageContent]");
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
			System.out.println("-- CAN'T FIND INSTRUMENTS");
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
		System.out.println("-- add image content to "+tree.getUserObject().toString());
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
			//TODO load LAser, Arc or something else
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
				System.out.println("-- add lightPath");
				if(lp.sizeOfLinkedExcitationFilterList() >0) {
					System.out.println("-- add excitation filter : "+lp.sizeOfLinkedExcitationFilterList());
					createNode(TagNames.OME_ELEM_LIGHTPATH_EX,lp.copyLinkedExcitationFilterList(),filterList,lpNode);
				}
				Dichroic dich=lp.getLinkedDichroic();
				if(dich!=null) {
					addContent(createContent(TagNames.OME_ELEM_DICHROIC,(new DichroicConverter()).convertData(dich)),0,lpNode);
				}
				if(lp.sizeOfLinkedEmissionFilterList() >0) {
					System.out.println("-- add emission filter : "+lp.sizeOfLinkedEmissionFilterList());
					createNode(TagNames.OME_ELEM_LIGHTPATH_EX,lp.copyLinkedEmissionFilterList(),filterList,lpNode);
				}
				
			}
			
			if(fs!=null) {
				//TODO container or not?
				DefaultMutableTreeNode fsNode = addContent(createContent(TagNames.OME_ELEM_LIGHTPATH_FS,(new FilterSetConverter()).convertData(fs)),0,chNode);
				
				List<Filter> fs_exc=fs.copyLinkedExcitationFilterList();
				List<Filter> fs_em=fs.copyLinkedEmissionFilterList();
				
				if(fs_exc!=null && fs_exc.size() >0) {
					createNode(TagNames.OME_ELEM_LIGHTPATH_EX,fs_exc,filterList,fsNode);
				}
				Dichroic dich=fs.getLinkedDichroic();
				if(dich!=null) {
					addContent(createContent(TagNames.OME_ELEM_DICHROIC,(new DichroicConverter()).convertData(dich)),0,fsNode);
				}
				if(fs_em!=null && fs_em.size() >0) {
					createNode(TagNames.OME_ELEM_LIGHTPATH_EM,fs_em,filterList,fsNode);
				}
			}
			idx++;
		}
		
		return tree;
	}
	
//	/**
//	 * Read out file metadata
//	 * @param tree
//	 * @param ome
//	 * @param i
//	 * @return
//	 */
//	private DefaultMutableTreeNode initImageContent(DefaultMutableTreeNode tree, OME ome, int i)
//	{
//		if(ome.getImage(i)==null) {
//			System.out.println("-- image content is empty");
//			return tree;
//		}
//		DefaultMutableTreeNode template=controller.getTree();
//		
//		System.out.println("--Read file content [ModuleController::initImageContent]");
//		List<Objective> objList=null;
//		List<Detector> detectorList=null;
//		List<LightSource> lightSourceList=null;
//		List<Filter> filterList=null;
//		List<Dichroic> dichList=null;
//		List<Channel> chList=null;
//		List<FilterSet> filtersetList=null;
//		
//		// convert ome annotation data to mdemodel data
//		Image imgObj=ome.getImage(i);
//		
//		//read out list of instruments
//		Instrument instruments=imgObj.getLinkedInstrument();
//		if(instruments==null) {
//			System.out.println("-- CAN'T FIND INSTRUMENTS");
//		}else {
//			objList=instruments.copyObjectiveList();
//			detectorList=instruments.copyDetectorList();
//			lightSourceList=instruments.copyLightSourceList();
//			filterList=instruments.copyFilterList();
//			dichList=instruments.copyDichroicList();
////			filtersetList=instruments.getFilterSetList();
//			
//			// save instrument lists to listOfDefaultValues
//			System.out.println("-- read out instruments to default values [MDEController]");
//			ModuleList hardwareList=new ModuleList();
//			hardwareList.put(TagNames.OME_ELEM_OBJECTIVE, MDEParser.parseObjectiveList(objList,controller));
//			hardwareList.put(TagNames.OME_ELEM_DETECTOR, MDEParser.parseDetectorList(detectorList,controller));
//			hardwareList.put(TagNames.OME_ELEM_LIGHTSOURCE, MDEParser.parseLightSourceList(lightSourceList,controller));
//			hardwareList.put(TagNames.OME_ELEM_FILTER, MDEParser.parseFilterList(filterList,controller));
//			hardwareList.put(TagNames.OME_ELEM_DICHROIC, MDEParser.parseDichroicList(dichList,controller));
//			//TODO save local to this MDEContent
//			controller.setDefaultValues(hardwareList);
//		}
//		
//		String thisType = TagNames.OME_ELEM_IMAGE;
//		ModuleContent content = controller.getContent(thisType);
//		if(content !=null)
//			content.setAttributes((new ImageConverter()).convertData(imgObj));
//		
//		ModuleTreeElement img = new ModuleTreeElement(thisType,imgObj.getName(),imgObj.getID(),content,tree);
//		DefaultMutableTreeNode imgNode=new DefaultMutableTreeNode(img);
//		
//		// load objective and objective settings annotations
//		ObjectiveSettings os = imgObj.getObjectiveSettings();
//		String id=os==null?"":os.getID();
//		Objective o= getElementByID(objList, id);
//		String oName=o!=null? o.getModel():null;
//		
//		thisType = TagNames.OME_ELEM_OBJECTIVE;
//		content = controller.getContent(thisType);
//		if(content !=null)
//			content.setAttributes((new ObjectiveConverter()).convertData(o,os));
//		ModuleTreeElement obj = new ModuleTreeElement(thisType,oName,id,content,imgNode);
//		
//		
//		imgNode.add(new DefaultMutableTreeNode(obj));
//		
//		thisType = TagNames.OME_ELEM_IMGENV;
//		content = controller.getContent(thisType);
//		if(content !=null)
//			content.setAttributes((new ImagingEnvConverter()).convertData(null));
//		ModuleTreeElement imgEnv = new ModuleTreeElement(thisType,"","",content,imgNode);
//		imgNode.add(new DefaultMutableTreeNode(imgEnv));
//		
//		//load channel data
//		for(ome.xml.model.Channel c: imgObj.getPixels().copyChannelList()){
//			thisType = TagNames.OME_ELEM_CHANNEL;
//			content = controller.getContent(thisType);
//			if(content !=null)
//				content.setAttributes((new ChannelConverter()).convertData(new Channel(c)));
//			ModuleTreeElement channel = new ModuleTreeElement(thisType,c.getName(),c.getID(),content,imgNode);
//			DefaultMutableTreeNode channelNode = new DefaultMutableTreeNode(channel);
//			
//			// load detector and detectorsettings annotations
//			ome.xml.model.DetectorSettings ds = c.getDetectorSettings();
//			String idDs=ds==null?"":ds.getID();
//			Detector d=getElementByID(detectorList,idDs);
//			DetectorSettings dsNew = (ds==null?null:new DetectorSettings(ds));
//			String dName=d!=null?d.getModel():null;
//			thisType = TagNames.OME_ELEM_DETECTOR;
//			content = controller.getContent(thisType);
//			if(content !=null)
//				content.setAttributes((new DetectorConverter()).convertData(d, dsNew));
//			ModuleTreeElement detector = new ModuleTreeElement(thisType,dName,idDs,	content,channelNode);
//			
//			channelNode.add(new DefaultMutableTreeNode(detector));
//			
//			//load lightSource and lightsourcesettings annotations
//			LightSourceSettings ls =c.getLightSourceSettings();
//			String idLs=ls==null?"":ls.getID();
//			LightSource l=getElementByID(lightSourceList, idLs);
//			String lSrcName=l!=null?l.getModel():null;
//			thisType = TagNames.OME_ELEM_LIGHTSOURCE;
//			content = controller.getContent(thisType);
//			if(content !=null)
//				content.setAttributes((new LightSourceConverter()).convertData(l, ls));
//			ModuleTreeElement lSrc = new ModuleTreeElement(thisType,lSrcName,idLs,content,channelNode);
//			
//			channelNode.add(new DefaultMutableTreeNode(lSrc));
//			
//			LightPath lp=c.getLightPath();
//			FilterSet fs=c.getLinkedFilterSet();
//			
//			if(lp!=null) {
//				DefaultMutableTreeNode lightPathNode =new DefaultMutableTreeNode(
//						new ModuleTreeElement(controller.getContent(TagNames.OME_ELEM_LIGHTPATH),channelNode));
//				System.out.println("-- add lightPath");
//				if(lp.sizeOfLinkedExcitationFilterList() >0) {
//					System.out.println("-- add excitation filter : "+lp.sizeOfLinkedExcitationFilterList());
//					lightPathNode.add(createNode(TagNames.OME_ELEM_LIGHTPATH_EX,lp.copyLinkedExcitationFilterList(),filterList,lightPathNode));
//				}
//				Dichroic dich=lp.getLinkedDichroic();
//				if(dich!=null) {
//					thisType = TagNames.OME_ELEM_DICHROIC;
//					content = controller.getContent(thisType);
//					if(content !=null)
//						content.setAttributes((new DichroicConverter()).convertData(dich));
//					ModuleTreeElement dichElem = new ModuleTreeElement(thisType,dich.getModel(),dich.getID(),content,lightPathNode);
//
//					lightPathNode.add(new DefaultMutableTreeNode(dichElem));
//				}
//				if(lp.sizeOfLinkedEmissionFilterList() >0) {
//					System.out.println("-- add emission filter : "+lp.sizeOfLinkedEmissionFilterList());
//					lightPathNode.add(createNode(TagNames.OME_ELEM_LIGHTPATH_EX,lp.copyLinkedEmissionFilterList(),filterList,lightPathNode));
//				}
//				channelNode.add(lightPathNode);
//			}
//			
//			if(fs!=null) {
//				//TODO container or not?
//				thisType = TagNames.OME_ELEM_LIGHTPATH_FS;
//				content = controller.getContent(thisType);
//				if(content !=null)
//					content.setAttributes((new FilterSetConverter()).convertData(fs));
//				ModuleTreeElement fsElem = new ModuleTreeElement(thisType, fs.getModel(), fs.getID(),content,channelNode);
//				DefaultMutableTreeNode filterSetNode =new DefaultMutableTreeNode(fsElem);
//				
//				List<Filter> fs_exc=fs.copyLinkedExcitationFilterList();
//				List<Filter> fs_em=fs.copyLinkedEmissionFilterList();
//				
//				if(fs_exc!=null && fs_exc.size() >0) {
//					filterSetNode.add(createNode(TagNames.OME_ELEM_LIGHTPATH_EX,fs_exc,filterList,filterSetNode));
//				}
//				Dichroic dich=fs.getLinkedDichroic();
//				if(dich!=null) {
//					thisType = TagNames.OME_ELEM_DICHROIC;
//					content = controller.getContent(thisType);
//					if(content !=null)
//						content.setAttributes((new DichroicConverter()).convertData(dich));
//					ModuleTreeElement dichElem = new ModuleTreeElement(thisType,dich.getModel(),dich.getID(),content,filterSetNode);
//
//					filterSetNode.add(new DefaultMutableTreeNode(dichElem));
//				}
//				if(fs_em!=null && fs_em.size() >0) {
//					filterSetNode.add(createNode(TagNames.OME_ELEM_LIGHTPATH_EM,fs_em,filterList,filterSetNode));
//				}
//				channelNode.add(filterSetNode);
//			}
//			
//			imgNode.add(channelNode);
//		}
//		
//		tree.add(imgNode);
//		return tree;
//	}
	
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
		System.out.println("-- create Instrument tables");
		if(hardwareTables==null)
			hardwareTables=new LinkedHashMap<>();
		else
			hardwareTables.clear();
		
		if(fileInstruments!=null) {
//			fileInstruments.print("---------------------FILE INSTRUMENTS:");
			if(controller.getInstrumentsForCurrentMic()!=null)
//				controller.getInstrumentsForCurrentMic().print("--------------CONF INSTRUMENTS:");
			for (Entry<String, List<ModuleContent>> entry : fileInstruments.entrySet()) {
				String key = entry.getKey();
				List<ModuleContent> hardware=new ArrayList<>();
				List<ModuleContent> valFile = entry.getValue();
				List<ModuleContent> values = controller.getInstrumentsOfType(key); 
				ObjectTable objTable=null;
				if(valFile!=null) {
//					System.out.println("-- add FILE instruments "+key+" : "+valFile.size()+" [MDEContent]");
					hardware.addAll(valFile);
					if(values!=null) {
//						System.out.println("-- add CONF instruments "+key+" : "+values.size()+" [MDEContent]");
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
			ModuleList mList=controller.getInstrumentsForCurrentMic();
			if(mList!=null) {
				for (Entry<String, List<ModuleContent>> entry : mList.entrySet()) {
					String key = entry.getKey();
					List<ModuleContent> values = entry.getValue();
					if(values!=null) {
						System.out.println("-- add instruments "+key+" : "+values.size()+" [MDEContent]");
						hardwareTables.put(key, new ObjectTable(values));
					}
				}
			}
		}
	}
	

}
