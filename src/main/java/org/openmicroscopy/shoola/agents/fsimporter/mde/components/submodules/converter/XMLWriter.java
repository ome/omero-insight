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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.MDEConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagDataProp;
import org.openmicroscopy.shoola.agents.treeviewer.actions.CreateObjectWithChildren;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Write/read MDE config file.
 * TODO: revise comment
 * OBJECTS: are available metadata groups with attributes. All available objects are define under Microscope name= UNIVERSAL. 
 * You can specify which objects are available for different microscopes and which attributes are visible, etc.
 * INSTRUMENTS: are available defined hardware specification for a certain microscope. For example you can define which Detector, Filter etc. are 
 * available for a microscope.
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class XMLWriter {

	
	final String MDE_CONFIGURATION = "MDEConfiguration";
	final String MDE_PREDEFINITIONS="MDEPredefinitions";
	private final String MDE_OBJECTS="MDEObjects";

	// microscope/setup/group classification
	final String ELEM_SETUP_PRE="SetupPre";
	final String ELEM_SETUP_CONF="SetupConf";
	
	final String ELEM_DEFINITION="Definitions";
	final String ELEM_CONFIGURATION="Configurations";
	
	final String ELEM_OBJECT_PRE="ObjectPre";
	final String ELEM_OBJECT_DEF="ObjectDef";
	final String ELEM_OBJECT_CONF="ObjectConf";
	
	final String ELEM_PARENTS="Parents";
	final String ELEM_TAGDATA="TagData";
	final String ELEM_TAGDATAPROP="TagDataProp";
	
	final String ATTR_ID="ID";
	final String ATTR_NAME="Name";
	final String ATTR_DEFAULT_VAL="DefaultValues";
	final String ATTR_VALUE="Value";
	final String ATTR_VISIBLE="Visible";
	final String ATTR_UNIT="Unit";
	final String ATTR_TYPE="Type";
	
	private final String ATTR_VALUES="Values";
	private LinkedHashMap<String, ModuleList> hardwareConfiguration;
//	private LinkedHashMap<String, HashMap<String,ModuleContent>> objectConfiguration;
	
	/** available object definitions*/
	HashMap<String,ModuleContent> objectDefinition;
	/** object configuration for different microscopes ([micName,[objectName,ModuleConfiguration]]*/
	LinkedHashMap<String,HashMap<String,ModuleConfiguration>> objectConfiguration;
	
	
	
	/**
	 * Save configuration of objects for different microscopes to file with following element structure:
	 * <pre>{@code
	 * 	   <Configuration>
	 * 	   	<SetupConf Name="">
	 * 			<ObjectConf Type="">
	 * 	   			<TagDataProp ...>
	 * 				<TagDataProp ...>
	 * 			....	
	 * 			</ObjectConf>
	 * 	   	</SetupConf>
	 * 		....
	 *     </Configuration>
	 * }</pre>
	 * 
	 * @param conf
	 */
	private Element configurationToXML(LinkedHashMap<String, HashMap<String, ModuleConfiguration>> oConf,Document doc) {
		Element configElem=doc.createElement(ELEM_CONFIGURATION);
		if(oConf==null)
			return configElem;
		
		for (Entry<String, HashMap<String, ModuleConfiguration>> entry : oConf.entrySet()) {
			
			HashMap<String,ModuleConfiguration> micConf=entry.getValue();
			if(micConf!=null) {
				Element micElem = doc.createElement(ELEM_SETUP_CONF);
				micElem.setAttribute(ATTR_NAME, entry.getKey());
				for (Entry<String, ModuleConfiguration> object : micConf.entrySet()) {
					Element objElem=objectConfToXML(object.getKey(),object.getValue(),doc);
					if(objElem!=null)
						micElem.appendChild(objElem);
				}
				if(micElem!=null)
					configElem.appendChild(micElem);
			}
		}
		return configElem;
	}
	/**
	 * Save definition of objects to file:
	 * <pre>{@code
	 * 	   <Definition>  // content UNIVERSAL are all available objects with all available attributes
	 * 		<ObjectDef Type="">
	 * 			<TagData ...>
	 * 			<TagData ...>
	 * 				....
	 * 			<Parent Values=[]>  // string list: p1,p2,p3,...
	 * 		</ObjectDef>
	 * 		....
	 * 	   </Definition>
	 * }</pre>
	 * @param oDef
	 * @param doc
	 * @return
	 */
	private Node definitionToXML(HashMap<String, ModuleContent> oDef, Document doc) {
		Element defElem=doc.createElement(ELEM_DEFINITION);
		if(oDef==null)
			return defElem;
		for (Entry<String, ModuleContent> object : oDef.entrySet()) {
			Element objElem=objectDefToXML(object.getKey(),object.getValue(),doc);
			if(objElem!=null)
				defElem.appendChild(objElem);
		}
		return defElem;
	}
	
	private Element mde_objectsToXml(LinkedHashMap<String, HashMap<String, ModuleConfiguration>> oConf,
			HashMap<String,ModuleContent> oDef, Document doc) {
		
		Element mdeObjects = doc.createElement(MDE_OBJECTS);
		mdeObjects.appendChild(definitionToXML(oDef,doc));
		mdeObjects.appendChild(configurationToXML(oConf, doc));
		
		return mdeObjects;
	}
	
	
	


	

	/**
	 * {@link ModuleContent} -> xmlElement: builds the element
	 * <pre>{@code
	 *	<ObjectDef Type="">
	 * 		<TagData ...>
	 * 		<TagData ...>
	 * 			....
	 * 		<Parent Values=[]>  // string list: p1,p2,p3,...
	 * 	</ObjectDef>
	 * }</pre>
	 * @param type
	 * @param content
	 * @param doc
	 * @return
	 */
	private Element objectDefToXML(String type,ModuleContent content,Document doc) {
		if(content==null)
			return null;

		Element result = doc.createElement(ELEM_OBJECT_DEF);
		result.setAttribute(ATTR_TYPE, type);

		List<TagData> list= content.getTagList();
		if(list == null)
			return result;

		for(int i=0;i<list.size();i++) {
			Element child = createTagDataElement(list.get(i), doc);
			if(child!=null)
				result.appendChild(child);
		}
		
		Element parents = doc.createElement(ELEM_PARENTS);
		String parentVal=content.getParents()!=null? String.join(",",content.getParents()):"";
		parents.setAttribute(ATTR_VALUES,parentVal );

		result.appendChild(parents);
		
		return result;
	}
	/**
	 * {@link ModuleContent} -> xmlElement: builds the element
	 * <pre>{@code
	 *	<ObjectConf Type="">  //MDE_OBJECT
	 * 		<TagDataProp ...>
	 * 		<TagDataProp ...>
	 * 	</ObjectConf>
	 * }</pre>
	 * @param type
	 * @param content
	 * @param doc
	 * @return
	 */
	private Element objectConfToXML(String type, ModuleConfiguration conf, Document doc) {
		if(conf==null)
			return null;

		Element result = doc.createElement(ELEM_OBJECT_CONF);
		result.setAttribute(ATTR_TYPE, type);

		List<TagDataProp> list= conf.getTagPropList();
		if(list == null)
			return result;

		for(int i=0;i<list.size();i++) {
			Element child = createTagDataPropElement(list.get(i), doc);
			if(child!=null)
				result.appendChild(child);
		}

		return result;
	}
	
	
	
	/**
	 * Parse elements like
	 * <pre>{@code
	 *	<ObjectDef Type="">
	 * 		<TagData ...>
	 * 		<TagData ...>
	 * 			....
	 * 		<Parent Values=[]>  // string list: p1,p2,p3,...
	 * 	</ObjectDef>
	 * }</pre>
	 * @param universal all available objects
	 * @param nodeList
	 * @return
	 */
	private HashMap<String, ModuleContent> elementsToObjectDefList(HashMap<String,ModuleContent> universal,NodeList nodeList){
		if(nodeList==null)
			return null;
		HashMap<String, ModuleContent> list=new HashMap<>();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_OBJECT_DEF) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String type=eElement.getAttribute(ATTR_TYPE);
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] XML: parse ObjectDef: "+type);
				ModuleContent defContent= universal!=null?universal.get(type):null;
				list.put(type, elementToModuleContent(defContent,eElement,type));
			}
		}
		return list;
	}
	
	/**
	 * Parse elements like
	 * <pre>{@code
	 *	<ObjectConf Type="">
	 * 		<TagDataProp ...>
	 * 		<TagDataProp ...>
	 * 			....
	 * 	</ObjectConf>
	 * }</pre>
	 * @param nodeList
	 * @return
	 */
	private HashMap<String, ModuleConfiguration> elementsToObjectConfList(NodeList nodeList){
		if(nodeList==null)
			return null;
		HashMap<String, ModuleConfiguration> list=new HashMap<>();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_OBJECT_CONF) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String type=eElement.getAttribute(ATTR_TYPE);
				list.put(type, elementsToTagDataPropList(eElement.getElementsByTagName(ELEM_TAGDATAPROP)));
			}
		}
		return list;
	}
	
	/**
	 * xmlElement -> {@link ModuleContent}
	 * @param eElement
	 * @param type
	 * @return
	 */
	private ModuleContent elementToModuleContent(ModuleContent universal,Element eElement,String type) {
		String parents="";
		if(eElement.getElementsByTagName(ELEM_PARENTS)!=null && eElement.getElementsByTagName(ELEM_PARENTS).getLength()>0) {
			parents=((Element) eElement.getElementsByTagName(ELEM_PARENTS).item(0)).getAttribute(ATTR_VALUES);
		}
		return new ModuleContent(elementsToTagDataList(eElement.getElementsByTagName(ELEM_TAGDATA),type), type,  parents.split(","));
	}

	
	//---------------------------------------------------------------------------------
	//				HardwareConfiguration and ObjectConfiguration save to file
	//---------------------------------------------------------------------------------
	/**
	 * save Predefinitions and ObjectDefinition and ~Configuration to file with following element structure:
	 * <pre>{@code
	 * <xml>
	 * 	<MDEPredefinition>
	 * 		<SetupPre Name=<>>
	 * 			<ObjectPre Type=<> ATTR_ID=<>>
	 * 				<TagData ...>
	 * 				<TagData ...>
	 * 				....
	 * 			</ObjectPre>
	 * 		</SetupPre>
	 * 	</MDEPredefinition>
	 *  <MDEObjects>
	 *  	<Definition>
	 *  		<ObjectDef Type="">
	 *  			<TagData ...>
	 *  			...
	 *  			<Parent Val=""> //val:=p1,p2,..
	 *  		</ObjectDef>
	 *  	</Definition>
	 *  	<Configuration>
	 *  		<SetupConf Name =""> 
	 *  			<ObjectConf Type="">
	 *  				<TagDataProp ...>
	 *  				...
	 *  			</ObjectConf>
	 *  		</SetupConf>
	 *  	</Configuration>
	 *  </MDEObjects>
	 * </xml>
	 * }</pre>
	 * @param conf
	 */
	public void saveToXML(LinkedHashMap<String, ModuleList> hconf, HashMap<String,ModuleContent> oDef,
			LinkedHashMap<String,HashMap<String,ModuleConfiguration>> oConf,String configPath) {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			// root element
			Element root = document.createElement(MDE_CONFIGURATION);
			document.appendChild(root);
			
			Element hardwareConf = document.createElement(MDE_PREDEFINITIONS);
			root.appendChild(hardwareConf);
			
			for (Entry<String, ModuleList> entry : hconf.entrySet()) {
				Element child=setupPreToXML(entry.getKey(), entry.getValue(), document);
				if(child!=null)
					hardwareConf.appendChild(child);
			}
			
			Element objElem=mde_objectsToXml(oConf,oDef,document);
			root.appendChild(objElem);

			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();

			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(configPath));

			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging 
			transformer.transform(domSource, streamResult);
		} catch (TransformerConfigurationException e1) {
			ImporterAgent.getRegistry().getLogger().error(this,"[MDE] Cannot parse mde configuration file");
			e1.printStackTrace();
		} catch (TransformerException e) {
			ImporterAgent.getRegistry().getLogger().error(this,"[MDE] Cannot parse mde configuration file");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			ImporterAgent.getRegistry().getLogger().error(this,"[MDE] Cannot parse mde configuration file");
			e.printStackTrace();
		}

		ImporterAgent.getRegistry().getLogger().info(this,"[MDE] Create XML File at "+configPath);
        
	}
	
	





	//TOD=: Desc for mic?
	/**
	 * Builds the element
	 * <pre>{@code
	 * <SetupPre Name=<>>
	 * 	....<ObjectPre>
	 * </SetupPre>
	 * }</pre>
	 * @param micName
	 * @param list
	 * @param doc
	 * @return
	 */
	private Element setupPreToXML(String micName,ModuleList list, Document doc) {
		if(list==null)
			return null;

		Element result = doc.createElement(ELEM_SETUP_PRE);
		result.setAttribute(ATTR_NAME, micName);

		for (Entry<String, List<ModuleContent>> entry : list.entrySet()) {
			if(entry.getValue()!=null) {
				for(ModuleContent c:entry.getValue()) {
					Element child=objectPreToXML(c, doc);
					if(child!=null)
						result.appendChild(child);
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Builds the tag for a certain instrument from {@link ModuleContent} object
	 * <pre>{@code 
	 * <ObjectPre Type="" ATTR_ID="" >
	 * 		<TagData...>
	 * 			
	 * </ObjectPre>
	 * }</pre>
	 * @param c    {@link ModuleContent} object holds instrument values
	 * @param doc
	 */
	private Element objectPreToXML(ModuleContent c, Document doc) {
		if(c==null)
			return null;
		
		Element result = doc.createElement(ELEM_OBJECT_PRE);
		result.setAttribute(ATTR_ID, c.getAttributeValue(TagNames.ID));//TODO necessary?
		result.setAttribute(ATTR_TYPE, c.getType());
		
		List<TagData> list= c.getTagList();
		if(list == null)
			return result;
		//add tagData
		for(int i=0;i<list.size();i++) {
			Element child = createTagDataElement(list.get(i), doc);
			if(child!=null)
				result.appendChild(child);
		}
		return result;
	}
	
	
	
	/**
	 * Builds {@link TagData} element with his properties as attributes.
	 * {@code
	 * <TagData Name="" Type="" Visible="" Value="" Unit="" DefaultValues="">
	 * }
	 * @param t
	 * @param doc
	 * @return
	 */
	private Element createTagDataElement(TagData t,Document doc) {
		if(t==null)
			return null;
		
		Element result=doc.createElement(ELEM_TAGDATA);
		
		result.setAttribute(ATTR_NAME, t.getTagName());
		result.setAttribute(ATTR_TYPE, String.valueOf(t.getTagType()));
		result.setAttribute(ATTR_VISIBLE,String.valueOf( t.isVisible()));
		result.setAttribute(ATTR_VALUE, t.getTagValue());
		result.setAttribute(ATTR_UNIT, t.getTagUnitString());
		result.setAttribute(ATTR_DEFAULT_VAL, t.getDefaultValuesAsString());
		
		
//		attr=doc.createAttribute("Required");
//		attr.setValue(t.);
		
		return result;
	}
	
	/**
	 * Builds {@link TagData} element with his properties as attributes.
	 * {@code
	 * <TagDataProp Name="" Visible="" Unit="">
	 * }
	 * @param t
	 * @param doc
	 * @return
	 */
	private Element createTagDataPropElement(TagDataProp t,Document doc) {
		if(t==null)
			return null;
		
		Element result=doc.createElement(ELEM_TAGDATAPROP);
		
		result.setAttribute(ATTR_NAME, t.getName());
		result.setAttribute(ATTR_VISIBLE,String.valueOf( t.isVisible()));
		result.setAttribute(ATTR_UNIT, t.getUnitSymbol());
		
		
//		attr=doc.createAttribute("Required");
//		attr.setValue(t.);
		
		return result;
	}
	
	public void parseConfiguration(String configPath){
		try {
			File hardwareFile = new File(configPath);
			if(hardwareFile.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(hardwareFile);
				doc.getDocumentElement().normalize();
				NodeList hardwareConf=doc.getElementsByTagName(MDE_PREDEFINITIONS);
				if(hardwareConf!=null && hardwareConf.getLength()>0) {
					this.hardwareConfiguration =elementsToHardwareConf(((Element) hardwareConf.item(0)).getElementsByTagName(ELEM_SETUP_PRE));
				}else {
					ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no setup predefinitions defined in mde configuration file");
				}
				NodeList mdeObjects=doc.getElementsByTagName(MDE_OBJECTS);
				if(mdeObjects!=null && mdeObjects.getLength()>0) {
					// get object definition element
					NodeList objDef=((Element) mdeObjects.item(0)).getElementsByTagName(ELEM_DEFINITION);
					if(objDef!=null && objDef.getLength()>0) {
						this.objectDefinition = elementsToObjectDefList(null,((Element) objDef.item(0)).getElementsByTagName(ELEM_OBJECT_DEF));
					}else {
						ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no object definitions defined in mde configuration file");
					}
					// get object configurations
					NodeList objConf=((Element) mdeObjects.item(0)).getElementsByTagName(ELEM_CONFIGURATION);
					if(objConf!=null && objConf.getLength()>0) {
						this.objectConfiguration =elementsToObjectConf(((Element) objConf.item(0)).getElementsByTagName(ELEM_SETUP_CONF));
					}else {
						ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no setup configurations defined in mde configuration file");
					}
				}else {
					ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] No configuration file available for MDE");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Parse list of {@link TagDataProp} from given NodeList
	 * {@code
	 * <TagDataProp Name="" Visible="" Unit="">
	 * }
	 * @param nodeList list of elements TagDataProp
	 * @return
	 */
	private ModuleConfiguration elementsToTagDataPropList(NodeList nodeList){
		if(nodeList==null)
			return null;
		ModuleConfiguration list=new ModuleConfiguration();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_TAGDATAPROP) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String tagName=eElement.getAttribute(ATTR_NAME);
				String tagUnit=eElement.getAttribute(ATTR_UNIT);
				String tagVis=eElement.getAttribute(ATTR_VISIBLE);
				TagDataProp t= new TagDataProp(tagName, tagUnit,Boolean.parseBoolean(tagVis));
 				list.put(tagName,t);
			}
		}
		return list;
	}
	
	
	/**
	 * Parse list of {@link TagData} from given NodeList
	 * {@code
	 * <TagData Name="" Type="" Visible="" Value="" Unit="" DefaultValues="">
	 * }
	 * @param nodeList list of elements TAGDATA
	 * @param parent owned object
	 * @return
	 */
	private LinkedHashMap<String,TagData> elementsToTagDataList(NodeList nodeList,String parent){
		if(nodeList==null)
			return null;
		LinkedHashMap<String,TagData> list = new LinkedHashMap<>();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_TAGDATA) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String tagName=eElement.getAttribute(ATTR_NAME);
				String tagVal=eElement.getAttribute(ATTR_VALUE);
				String tagUnit=eElement.getAttribute(ATTR_UNIT);
				String tagVis=eElement.getAttribute(ATTR_VISIBLE);
				String defaultVal = eElement.getAttribute(ATTR_DEFAULT_VAL);
				String tagType=eElement.getAttribute(ATTR_TYPE);
				
				TagData t= new TagData(parent,tagName, tagVal, tagUnit,false, tagType, defaultVal.split(","));
				t.setVisible(Boolean.parseBoolean(tagVis));
				list.put(tagName,t);
			}
		}
		return list;
	}
	
	
	/**
	 * Parse list of instruments to {@link ModuleList} .For instrument elements :
	 * <pre>{@code 
	 * <ObjectPre Type="" ATTR_ID="" >
	 * 		<TagData...>
	 * 			
	 * </ObjectPre>
	 * }</pre>
	 */
	private ModuleList elementsToModuleList(NodeList nodeList){
		if(nodeList==null)
			return null;
		ModuleList list=new ModuleList();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_OBJECT_PRE) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String type=eElement.getAttribute(ATTR_TYPE);
				ModuleContent c=new ModuleContent(elementsToTagDataList(eElement.getElementsByTagName(ELEM_TAGDATA),type), type, null);
				List<ModuleContent> cList=list.get(type);
				if(cList==null) {
					cList = new ArrayList<>();
				}
				cList.add(c);
				list.put(type, cList);
			}
		}
		return list;
	}
	
	private LinkedHashMap<String, ModuleList> elementsToHardwareConf(NodeList nodeList){
		if(nodeList==null)
			return null;
		LinkedHashMap<String,ModuleList> list=new LinkedHashMap<>();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_SETUP_PRE) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String name=eElement.getAttribute(ATTR_NAME);
				list.put(name, elementsToModuleList(eElement.getElementsByTagName(ELEM_OBJECT_PRE)));
			}
		}
		return list;
	}
	
	private LinkedHashMap<String, HashMap<String, ModuleConfiguration>> elementsToObjectConf(NodeList nodeList){
		if(nodeList==null)
			return null;
		LinkedHashMap<String, HashMap<String,ModuleConfiguration>> list= new LinkedHashMap<>();

		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(ELEM_SETUP_CONF) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement =(Element)n;
				String micname= eElement.getAttribute(ATTR_NAME);
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] XML: parse SetupConf for "+micname);
				list.put(micname, elementsToObjectConfList(eElement.getElementsByTagName(ELEM_OBJECT_CONF)));
			}
		 }
		return list;
		
	}
	


	public LinkedHashMap<String, ModuleList> getHardwareConfiguration() {
		return hardwareConfiguration;
	}


	public LinkedHashMap<String, HashMap<String, ModuleConfiguration>> getObjectConfiguration() {
		return objectConfiguration;
	}


	public HashMap<String, ModuleContent> getObjectDefinition() {
		return objectDefinition;
	}
	
	
}
