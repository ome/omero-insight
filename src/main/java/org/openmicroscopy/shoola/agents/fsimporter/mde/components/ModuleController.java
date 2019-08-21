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
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.LatticeScope;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.LeicaLSMSP5;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.OlympusLSMFV1000;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.OlympusTIRF3Line;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.OlympusTIRF4Line_SMT;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.OlympusTIRF4Line_STORM;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.StandardMic;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware.ZeissCellObserverSD;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
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
import org.openmicroscopy.shoola.util.MonitorAndDebug;


/**
 * Singleton class
 * Holds definition of available modules, standard ome tree and hardware specific module data.
 * @author Kunis
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
		img.printContent();
		DefaultMutableTreeNode image= new DefaultMutableTreeNode(img);
		standardTree.add(image);
		
		
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
	
	private ModuleTreeElement createElement(String type,DefaultMutableTreeNode parent) {
		if(getContentOfType(type)==null) {
			System.out.println("ERROR: no content found for type  "+type);
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
				System.out.println("-- standard tree is not initialize [ModuleController]");
				return null;
			}
			System.out.println("-- return standard tree [ModuleController]");
			return ModuleTree.cloneTreeNode(standardTree);
		}else {
			return ModuleTree.cloneTreeNode(mdeConf.getTree());
		}
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
		
		thisType=TagNames.OME_ELEM_LIGHTPATH_FS;
		ModuleContent lpFs=new ModuleContent(null,thisType,TagNames.getParents(thisType));
		defaultContent.put(thisType,lpFs);
		
		//TODO: OME:Microscope, Filterset, Experiment
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
			System.out.println("ERROR: mdeConf is empty! [MDEController::getContent]");
			return null;
		}
		if(micName==null) {
			System.out.println("-- No microscope is given, return content for UNIVERSAL [MDEController::getContent]");
			return new ModuleContent(mdeConf.getContent(MDEConfiguration.UNIVERSAL, moduleType));
		}
		return new ModuleContent(mdeConf.getContent(micName, moduleType));
	}
	
	
	public DefaultMutableTreeNode cloneTreeStructure(DefaultMutableTreeNode node,DefaultMutableTreeNode p) {
		DefaultMutableTreeNode cloneNode = null;
		
		cloneNode=new DefaultMutableTreeNode(
				new ModuleTreeElement(getContentOfType(((ModuleTreeElement) node.getUserObject()).getType()),p));
		for(int i = 0 ; i < node.getChildCount(); i++) {
			cloneNode.add(cloneTreeStructure((DefaultMutableTreeNode) node.getChildAt(i),cloneNode));
		}
		return cloneNode;
	}
	
	
	public void setCurrentMicName(String micName) {
		this.micName=micName;
	}
	
	//TODO filter and dichroic list missing
	//TODO:replace by configuration file values
	public ModuleList getCustomSettings(CustomViewProperties customSettings) {
		// save microscope instrument lists to listOfDefaultMicValues
//		System.out.println("-- read out microscope instruments to default mic values [MDEController]");
		ModuleList hardwareList=new ModuleList();
		hardwareList.put(TagNames.OME_ELEM_OBJECTIVE, MDEParser.parseObjectiveList(customSettings.getMicObjList(),this));
		hardwareList.put(TagNames.OME_ELEM_DETECTOR, MDEParser.parseDetectorList(customSettings.getMicDetectorList(),this));
		hardwareList.put(TagNames.OME_ELEM_LASER, MDEParser.parseLightSourceLaser(customSettings.getMicLightSrcList(),this));
		hardwareList.put(TagNames.OME_ELEM_FILAMENT, MDEParser.parseLightSourceFilament(customSettings.getMicLightSrcList(),this));
		hardwareList.put(TagNames.OME_ELEM_ARC, MDEParser.parseLightSourceArc(customSettings.getMicLightSrcList(),this));
		hardwareList.put(TagNames.OME_ELEM_LED, MDEParser.parseLightSourceLED(customSettings.getMicLightSrcList(),this));
		hardwareList.put(TagNames.OME_ELEM_GENERICEXCITATIONSOURCE, MDEParser.parseLightSourceGES(customSettings.getMicLightSrcList(),this));
//		hardwareList.put("OME:Filter", MDEParser.parseFilterList(customSettings.getMicLightPathFilterList()));
//		hardwareList.put("OME:Dichroic", MDEParser.parseDichroicList(customSettings.getMicLightPathFilterList()));
		return hardwareList;
	}
	
	public void initMDEConfiguration(String curMic) {
		System.out.println("-- load MDE configuration");
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
		System.out.println("----------- APPLY MDE configuration ------------------------");
		mdeConf=conf;
		mdeConf.printObjects(getCurrentMicName());
		System.out.println("--------INSTRUMENTS------\n"+String.join(",",mdeConf.getMicNames()));
	}
	
	public MDEConfiguration getMDEConfiguration() {
		return mdeConf;
	}
	
	
	
	

	public String[] getPossibleChilds(String type) {
		ArrayList<String> list=new ArrayList<>();
//		System.out.println("-- serach for parent: "+type);
		HashMap<String,ModuleContent> contentList=mdeConf.getAvailableContentList(getCurrentMicName());
		for(Entry<String, ModuleContent> entry: contentList.entrySet()) {
			System.out.println("-- check moduleContent for possible insert: "+entry.getKey());
			if(entry.getValue().hasParent(type)) {
//				System.out.println("-- ok");
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
			return null;

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
		System.out.println("-- PRINT objects:");
		if(mdeConf== null) {
			System.out.println("-- MDEConfiguration is empty [ModuleController]\n TODO: load default ome objects");
			return;
		}
		mdeConf.printObjects(micName);
		
			
	}
}
