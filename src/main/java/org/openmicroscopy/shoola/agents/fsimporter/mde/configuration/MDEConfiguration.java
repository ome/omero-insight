package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.XMLWriter;
import org.openmicroscopy.shoola.util.MonitorAndDebug;

import ome.model.units.UnitEnum;


public class MDEConfiguration {
	/* microscope hardware configuration list - which objects values are available.
	 *  e.g.: <LatticeLightSheet,[<Objective,[o1,o2,o3]>,<Detector,[d1,d2,d3]>,...]> */
	LinkedHashMap<String, ModuleList> hConfiguration;
	
	/* object configuration list - which object is available and how the objects looks like (content) for a mic.
	 *  e.g.: <UNIVERSAL,[<Objective,objContent>,<Detector,detContent>,...]> */
	LinkedHashMap<String,HashMap<String,ModuleContent>> oConfiguration;
	public final static String UNIVERSAL="Universal";
	
	
	private HashMap<String, UnitEnum> defaultUnitMap;
	
	public MDEConfiguration() {
		//copy default ome unit map
		defaultUnitMap=new HashMap<>();
		for(Map.Entry<String, ome.model.units.UnitEnum> entry: TagNames.omeUnitEnumsDef.entrySet()) {
			defaultUnitMap.put(entry.getKey(), entry.getValue());
		}
		
		this.hConfiguration=new LinkedHashMap<String,ModuleList>();
		this.oConfiguration=new LinkedHashMap<String,HashMap<String,ModuleContent>>();
		// load config from file if available
		parse();
		
		ModuleController c=ModuleController.getInstance();
		// use implemented initialisation, because standard tree use standard ome objects-> catch oConf is empty or not include standard ome
		if(oConfiguration==null || oConfiguration.isEmpty()) {
			addContent(UNIVERSAL, c.initDefaultOMEObjects());
		}
		
		printObjects(ModuleController.getInstance().getCurrentMicName());
	}
	
	// TODO test add unit function: cancel, apply configurators
	public void addDefaultUnit(String unitSymbol, String className, String tagName, String parent) {
		if(defaultUnitMap!=null  && !defaultUnitMap.containsKey(parent+"::"+tagName)) {
//			System.out.println("-- Add default unit of type "+className+" for "+tagName+" [ModuleController]");
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
		this.oConfiguration=new LinkedHashMap<String,HashMap<String,ModuleContent>>();
		if(orig.hConfiguration!=null) {
			for(Map.Entry<String, ModuleList> entry : orig.hConfiguration.entrySet()) {
				this.hConfiguration.put(entry.getKey(), new ModuleList(entry.getValue()));
			}
		}
		if(orig.oConfiguration!=null) {
			for(Entry<String, HashMap<String, ModuleContent>> entry:orig.oConfiguration.entrySet()) {
				HashMap<String,ModuleContent> list = new HashMap<String,ModuleContent>();
				if(entry.getValue()!=null) {
					for(Entry<String, ModuleContent> c:entry.getValue().entrySet()) {
						list.put(c.getKey(), new ModuleContent(c.getValue()));
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
	 * Add to LinkedHashMap hardwareConfiguration: micName,conf
	 * @param micName microscope name as key
	 * @param conf {@link ModuleList} of available hardware as value
	 */
	public void addInstrumentsForMicroscope(String micName,ModuleList conf) {
		if(hConfiguration!=null) {
//			conf.print("------ Set Instruments for "+micName+" [MDEConfiguration]");
			this.hConfiguration.put(micName, conf);
		}
		//new mic add also to object conf
		if(!oConfiguration.containsKey(micName))
			oConfiguration.put(micName, ModuleController.getInstance().initDefaultOMEObjects());
	}
	
	/**
	 * Set/replace the instrument list for given instrumentName 
	 * @param micName
	 * @param instrumentName
	 * @param instruments
	 */
	public void setInstrumentForMicroscope(String micName, String instrumentName,List<ModuleContent> instruments) {
		if(hConfiguration!=null) {
			ModuleList hardware=this.hConfiguration.get(micName);
			if(hardware==null) {
				hardware=new ModuleList();
			}
			hardware.put(instrumentName, instruments);
			this.hConfiguration.put(micName, hardware);
		}
	}
	
	/**
	 * Remove microscope from hardwareConfigurationList, despite UNIVERSAL.
	 * @param micName
	 */
	public void removeHardwareConfForMicroscope(String micName) {
		if(hConfiguration!=null && !micName.equals(UNIVERSAL)) {
			this.hConfiguration.remove(micName);
		}
	}
	
	/**
	 * Remove given instrument/object from list of available hardware elements.
	 * @param objName
	 * @param micName
	 */
	public void removeHardwareConfInstrumentForMicroscope(String objName, String micName) {
		if(hConfiguration!=null && !micName.equals(UNIVERSAL)) {
			ModuleList list= this.hConfiguration.get(micName);
			if(list!=null) {
				System.out.println("-- DELETE "+micName+"::"+objName+"MDEConfiguration::removeHardwareConf..");
				list.remove(objName);
				this.hConfiguration.put(micName, list);
			}
		}
	}

	/**
	 * Return {@link ModuleList} of available hardware
	 * @param micName microscope name
	 * @return
	 */
	public ModuleList getInstruments(String micName) {
		if(hConfiguration!=null && !hConfiguration.isEmpty())
			return hConfiguration.get(micName);
		
		return null;
	}
	
	public void addContent(String micName,ModuleContent c) {
		if(oConfiguration==null) {
			oConfiguration=new LinkedHashMap<>();
		}
		HashMap<String,ModuleContent> list=getContentList(micName);
		if(!list.containsKey(c.getType())) {
			list.put(c.getType(), c);
			this.oConfiguration.put(micName, list);
		}
	}
	
	public boolean contentExists(String micName,String type) {
		if(this.oConfiguration!=null) {
			if(oConfiguration.get(micName)!=null && oConfiguration.get(micName).containsKey(type)) {
				return true;
			}
		}
		return false;
	}
	
	public void addContent(String micName,HashMap<String,ModuleContent> contList) {
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

	private void print(HashMap<String, ModuleContent> contList) {
		if(contList==null)
			MonitorAndDebug.printConsole("-- PRINT Content List: list is empty");
		else {
			for(Map.Entry<String, ModuleContent> entry:contList.entrySet()) {
				if(entry.getValue()!=null && entry.getValue().getList()!=null) {
					entry.getValue().print();
				}else {
					MonitorAndDebug.printConsole("-- PRINT ModuleContent: Content "+entry.getKey()+" elements: 0");
				}
			}
		}
	}

	public ModuleContent getContent(String micName,String type) {
		if(oConfiguration!=null && !oConfiguration.isEmpty()) {
			HashMap<String, ModuleContent> map =oConfiguration.get(micName);
			if(map==null) {
				System.out.println("-- WARNING: does not exists:  content of type: "+type+"::"+micName);
				return null;
			}
			return map.get(type);
		}
		System.out.println("-- ERROR: objectConfiguration is empty");
		return null;
	}
	
	public HashMap<String,ModuleContent> getContentList(String micName) {
		if(oConfiguration!=null && !oConfiguration.isEmpty()) {
			return oConfiguration.get(micName);
		}
		return null;
	}
	
	public HashMap<String,ModuleContent> getAvailableContentList(String mic) {
		return getContentList(mic);
	}
	
	public String[] getMicNames() {
		if(hConfiguration!=null) {
			return hConfiguration.keySet().toArray(new String[hConfiguration.size()]);
		}
		return new String[] {UNIVERSAL};
	}
	
	//TODO parse xml from conf file
	public void parse() {
		XMLWriter writer=new XMLWriter();
		writer.parseConfiguration();
		hConfiguration=writer.getHardwareConfiguration();
		oConfiguration=writer.getObjectConfiguration();
	}
	
	public void writeToFile() {
		XMLWriter writer=new XMLWriter();
		writer.saveToXML(hConfiguration,oConfiguration);
	}

	/**
	 * Returns list of available object names stored under Micrsocope=UNIVERSAL
	 * @return
	 */
	public String[] getNameOfObjects() {
		if(oConfiguration==null || oConfiguration.isEmpty())
			return null;
		HashMap<String,ModuleContent> objectListUniversal=oConfiguration.get(MDEConfiguration.UNIVERSAL);
		if(objectListUniversal==null)
			return null;
		return objectListUniversal.keySet().toArray(new String[objectListUniversal.size()]);
	}

	//TODO read out tree from conf file
	public DefaultMutableTreeNode getTree() {
		// TODO Auto-generated method stub
		return null;
	}

	public void printObjects(String mic) {
		MonitorAndDebug.printConsole("------------  Objects for "+mic+" -------------");
		if(oConfiguration==null || !oConfiguration.containsKey(mic) || oConfiguration.get(mic)==null) {
			MonitorAndDebug.printConsole("-- objectConfiguration is null");
			return;
		}
		for(Entry<String, ModuleContent> entry : oConfiguration.get(mic).entrySet()) {
			String[] parents=entry.getValue()!=null ? entry.getValue().getParents():null;
			if(parents!=null) {
				String p=String.join(",", parents);
				MonitorAndDebug.printConsole("\t"+entry.getKey()+", parent: "+p+" ["+parents.length+"]");
			}
		}
	}

	

	
	
	

	
}
