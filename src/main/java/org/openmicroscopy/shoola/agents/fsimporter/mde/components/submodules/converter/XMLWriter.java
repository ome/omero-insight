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
 * OBJECTS: are available metadata groups with attributes. All available objects are define under Microscope name= UNIVERSAL. 
 * You can specify which objects are available for different microscopes and which attributes are visible, etc.
 * INSTRUMENTS: are available defined hardware specification for a certain microscope. For example you can define which Detector, Filter etc. are 
 * available for a microscope.
 * @author Kunis
 *
 */
public class XMLWriter {

	public static final String xmlFilePath_hardware = "C:\\Users\\Kunis.MB-Bordetella\\omero\\mdeConfiguration.xml";
	public static final String xmlFilePath_structure = "C:\\Users\\Kunis.MB-Bordetella\\omero\\mdeConfig_structure.xml";
	
	final String CONF = "MDEConfiguration";
	
	final String ATTR_ID="ID";
	final String ATTR_NAME="Name";
	final String HARDWARE_CONF="MDEHardwareConfiguration";
	final String MIC="Microscope";
	final String INSTRUMENT="Instrument";
	final String PARENTS="Parents";
	final String TAGDATA="TagData";
	final String TAGDATAPROP="TagDataProp";
	final String ATTR_TAGDATA_DEF="DefaultValues";
	final String ATTR_TAGDATA_VAL="Value";
	final String ATTR_TAGDATA_VIS="Visible";
	final String ATTR_TAGDATA_UNIT="Unit";
	final String ATTR_TYPE="Type";
	private final String MDE_OBJECTS="MDEObjects";
	private final String MDE_OBJECT="Object";
	private final String ATTR_VALUES="Values";
	private LinkedHashMap<String, ModuleList> hardwareConfiguration;
	private LinkedHashMap<String, HashMap<String,ModuleContent>> objectConfiguration;
	
	
	
	/**
	 * Save objects to file with following element structure:
	 * <pre>{@code
	 * 	   <Microscope Name=UNIVERSAL>  // content UNIVERSAL are all available objects with all available attributes
	 * 		<Object Type="">
	 * 			<TagData ...>
	 * 			<TagData ...>
	 * 				....
	 * 			<Parent Values=[]>  // string list: p1,p2,p3,...
	 * 		</Object>
	 * 		....
	 * 	   </Microscope>
	 * 	   <Microscope Name="">
	 * 	   		<TagDataProp ...>
	 * 			<TagDataProp ...>
	 * 			....	
	 * 	   </Microscope>
	 * }</pre>
	 * 
	 * @param conf
	 */
	private Element microscopeObjectsToXml(String micName,HashMap<String, ModuleContent> conf,Document doc) {
		if(conf==null)
			return null;

		Element result = doc.createElement(MIC);
		result.setAttribute(ATTR_NAME, micName);
		boolean fullData=micName.equals(MDEConfiguration.UNIVERSAL);
		for (Entry<String, ModuleContent> entry : conf.entrySet()) {
			Element child=createObjectElement(entry.getKey(),entry.getValue(),fullData,doc);
			if(child!=null)
				result.appendChild(child);
		}
		return result;
	}
	
	
	/**
	 * {@link ModuleContent} -> xmlElement: builds the element
	 * <pre>{@code
	 *	<Object Type="">
	 * 		<TagData ...>
	 * 		<TagData ...>
	 * 			....
	 * 		<Parent Values=[]>  // string list: p1,p2,p3,...
	 * 	</Object>
	 * }</pre>
	 * @param type
	 * @param content
	 * @param doc
	 * @return
	 */
	private Element createObjectElement(String type,ModuleContent content, boolean fullData,Document doc) {
		if(content==null)
			return null;

		Element result = doc.createElement(MDE_OBJECT);
		result.setAttribute(ATTR_TYPE, type);

		List<TagData> list= content.getTagList();
		if(list == null)
			return result;
		//add tagData
		if(fullData) {
			for(int i=0;i<list.size();i++) {
				Element child = createTagDataElement(list.get(i), doc);
				if(child!=null)
					result.appendChild(child);
			}
		}else {
			for(int i=0;i<list.size();i++) {
				Element child = createTagDataPropElement(list.get(i), doc);
				if(child!=null)
					result.appendChild(child);
			}
		}
		
		Element parents = doc.createElement(PARENTS);
		String parentVal=content.getParents()!=null? String.join(",",content.getParents()):"";
		parents.setAttribute(ATTR_VALUES,parentVal );

		result.appendChild(parents);
		
		return result;
	}
	
	
	
	
	/**
	 * Parse elements like
	 * <pre>{@code
	 *	<Object Type="">
	 * 		<TagData ...>
	 * 		<TagData ...>
	 * 			....
	 * 		<Parent Values=[]>  // string list: p1,p2,p3,...
	 * 	</Object>
	 * }</pre>
	 * @param universal all available objects
	 * @param nodeList
	 * @return
	 */
	private HashMap<String, ModuleContent> elementsToObjectList(HashMap<String,ModuleContent> universal,NodeList nodeList){
		if(nodeList==null)
			return null;
		HashMap<String, ModuleContent> list=new HashMap<>();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(MDE_OBJECT) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String type=eElement.getAttribute(ATTR_TYPE);
				ModuleContent defContent= universal!=null?universal.get(type):null;
				list.put(type, elementToModuleContent(defContent,eElement,type));
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
		if(eElement.getElementsByTagName(PARENTS)!=null && eElement.getElementsByTagName(PARENTS).getLength()>0)
			parents=((Element) eElement.getElementsByTagName(PARENTS).item(0)).getAttribute(ATTR_VALUES);
		if(universal==null) {
			return new ModuleContent(elementsToTagDataList(eElement.getElementsByTagName(TAGDATA),type), type,  parents.split(","));
		}else {
			return mergeProperties(elementsToTagDataPropList(eElement.getElementsByTagName(TAGDATAPROP)),universal);
		}
	}
	
	private ModuleContent mergeProperties(LinkedHashMap<String, TagDataProp> propList,
			ModuleContent universal) {
		if(universal==null)
			return null;
		ModuleContent result= new ModuleContent(universal);
		LinkedHashMap<String, TagData> list=result.getList();
		for(Map.Entry<String, TagDataProp> entry:propList.entrySet()) {
			if(list.containsKey(entry.getKey())) {
				list.get(entry.getKey()).setProperties(entry.getValue());
			}
		}
		return result;
	}


	
	//-------------------------------------------------------------------------------------
	//					Structure
	//-------------------------------------------------------------------------------------
	
	
	//see: https://examples.javacodegeeks.com/core-java/xml/parsers/documentbuilderfactory/create-xml-file-in-java-using-dom-parser-example/
	public void saveToXML(DefaultMutableTreeNode tree,String rootName) {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			// root element
			Element root = document.createElement(rootName);
			document.appendChild(root);


			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;

			transformer = transformerFactory.newTransformer();

			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(xmlFilePath_structure));

			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging 


			transformer.transform(domSource, streamResult);
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Done creating XML File at "+xmlFilePath_structure);
        
	}
	/**
	 * Builds structure node {@link ModuleTreeElement}.
	 * <pre>{@code
	 * <type ATTR_ID="" Name="">
	 * 		<Parents Val=<>>
	 * 		<TagData...>
	 *		.... 			
	 * </type>
	 * }</pre>
	 * @param elem
	 */
	private Element elementToXml(ModuleTreeElement elem, Document doc) {
		if(elem==null)
			return null;
		
		Element result = doc.createElement(elem.getType());
		Attr attr = doc.createAttribute(ATTR_ID);
		attr.setValue(elem.getIndex());
		attr = doc.createAttribute(ATTR_NAME);
		attr.setValue(elem.getName());
		
		
		List<TagData> list= elem.getData()!=null ? elem.getData().getTagList():null;
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
	
	
	
	//---------------------------------------------------------------------------------
	//				HardwareConfiguration and ObjectConfiguration save to file
	//---------------------------------------------------------------------------------
	/**
	 * save Hardwareconfiguration and ObjectConfiguration to file with following element structure:
	 * <pre>{@code
	 * <xml>
	 * 	<MDEHardwareConfiguration>
	 * 		<Microscope Name=<>>
	 * 			<Instrument Type=<> ATTR_ID=<>>
	 * 				<TagData ...>
	 * 				<TagData ...>
	 * 				....
	 * 			</Instrument>
	 * 		</Microscope>
	 * 	</MDEHardwareConfiguration>
	 *  <MDEObjects>
	 *  	<Microscope Name =""> // Name=Universal -> all available objects
	 *  		<Object Type="">
	 *  			<TagData ...>
	 *  			...
	 *  			<Parent Val=""> //val:=p1,p2,..
	 *  		</Object>
	 *  	</Microscope>
	 *  </MDEObjects>
	 * </xml>
	 * }</pre>
	 * @param conf
	 */
	public void saveToXML(LinkedHashMap<String, ModuleList> hconf, LinkedHashMap<String,HashMap<String,ModuleContent>> oConf) {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			// root element
			Element root = document.createElement(CONF);
			document.appendChild(root);
			
			Element hardwareConf = document.createElement(HARDWARE_CONF);
			root.appendChild(hardwareConf);
			
			for (Entry<String, ModuleList> entry : hconf.entrySet()) {
				Element child=microscopeInstrumentsToXml(entry.getKey(), entry.getValue(), document);
				if(child!=null)
					hardwareConf.appendChild(child);
			}
			
			Element objectConf = document.createElement(MDE_OBJECTS);
			root.appendChild(objectConf);
			
			for (Entry<String, HashMap<String, ModuleContent>> entry : oConf.entrySet()) {
				Element child=microscopeObjectsToXml(entry.getKey(), entry.getValue(),document);
				if(child!=null)
					objectConf.appendChild(child);
			}

			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();

			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(xmlFilePath_hardware));

			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging 
			transformer.transform(domSource, streamResult);
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Done creating XML File at "+xmlFilePath_hardware);
        
	}
	
	//TOD=: Desc for mic?
	/**
	 * Builds the element
	 * <pre>{@code
	 * <Microsocope Name=<>>
	 * 	....<Instrument>
	 * </Microsocope>
	 * }</pre>
	 * @param micName
	 * @param list
	 * @param doc
	 * @return
	 */
	private Element microscopeInstrumentsToXml(String micName,ModuleList list, Document doc) {
		if(list==null)
			return null;

		Element result = doc.createElement(MIC);
		result.setAttribute(ATTR_NAME, micName);

		for (Entry<String, List<ModuleContent>> entry : list.entrySet()) {
			if(entry.getValue()!=null) {
				for(ModuleContent c:entry.getValue()) {
					Element child=createInstrumentElement(c, doc);
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
	 * <Instrument Type="" ATTR_ID="" >
	 * 		<TagData...>
	 * 			
	 * </Instrument>
	 * }</pre>
	 * @param c    {@link ModuleContent} object holds instrument values
	 * @param doc
	 */
	private Element createInstrumentElement(ModuleContent c, Document doc) {
		if(c==null)
			return null;
		
		Element result = doc.createElement(INSTRUMENT);
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
		
		Element result=doc.createElement(TAGDATA);
		
		result.setAttribute(ATTR_NAME, t.getTagName());
		result.setAttribute(ATTR_TYPE, String.valueOf(t.getTagType()));
		result.setAttribute(ATTR_TAGDATA_VIS,String.valueOf( t.isVisible()));
		result.setAttribute(ATTR_TAGDATA_VAL, t.getTagValue());
		result.setAttribute(ATTR_TAGDATA_UNIT, t.getTagUnitString());
		result.setAttribute(ATTR_TAGDATA_DEF, t.getDefaultValuesAsString());
		
		
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
	private Element createTagDataPropElement(TagData t,Document doc) {
		if(t==null)
			return null;
		
		Element result=doc.createElement(TAGDATAPROP);
		
		result.setAttribute(ATTR_NAME, t.getTagName());
		result.setAttribute(ATTR_TAGDATA_VIS,String.valueOf( t.isVisible()));
		result.setAttribute(ATTR_TAGDATA_UNIT, t.getTagUnitString());
		
		
//		attr=doc.createAttribute("Required");
//		attr.setValue(t.);
		
		return result;
	}
	
	public void parseConfiguration(){
		try {
			File hardwareFile = new File(xmlFilePath_hardware);
			if(hardwareFile.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(hardwareFile);
				doc.getDocumentElement().normalize();
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList hardwareConf=doc.getElementsByTagName(HARDWARE_CONF);
				if(hardwareConf!=null && hardwareConf.getLength()>0) {
					this.hardwareConfiguration =elementsToHardwareConf(((Element) hardwareConf.item(0)).getElementsByTagName(MIC));
				}
				NodeList objectConf=doc.getElementsByTagName(MDE_OBJECTS);
				if(objectConf!=null && objectConf.getLength()>0) {
					this.objectConfiguration =elementsToObjectConf(((Element) objectConf.item(0)).getElementsByTagName(MIC));
				}
			}else {
				System.out.println("ERROR: can't find configuration file for MDE");
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
	private LinkedHashMap<String,TagDataProp> elementsToTagDataPropList(NodeList nodeList){
		if(nodeList==null)
			return null;
		LinkedHashMap<String,TagDataProp> list = new LinkedHashMap<>();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(TAGDATAPROP) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String tagName=eElement.getAttribute(ATTR_NAME);
				String tagUnit=eElement.getAttribute(ATTR_TAGDATA_UNIT);
				String tagVis=eElement.getAttribute(ATTR_TAGDATA_VIS);
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
			if(n.getNodeName().equals(TAGDATA) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String tagName=eElement.getAttribute(ATTR_NAME);
				String tagVal=eElement.getAttribute(ATTR_TAGDATA_VAL);
				String tagUnit=eElement.getAttribute(ATTR_TAGDATA_UNIT);
				String tagVis=eElement.getAttribute(ATTR_TAGDATA_VIS);
				String defaultVal = eElement.getAttribute(ATTR_TAGDATA_DEF);
				String tagType=eElement.getAttribute(ATTR_TYPE);
				//TODO Arrayfield size
				TagData t= new TagData(parent,tagName, tagVal, false, tagType, defaultVal.split(","));
				t.setTagUnit(tagUnit);
				t.setVisible(Boolean.parseBoolean(tagVis));
				list.put(tagName,t);
			}
		}
		return list;
	}
	
	
	/**
	 * Parse list of instruments to {@link ModuleList} .For instrument elements :
	 * <pre>{@code 
	 * <Instrument Type="" ATTR_ID="" >
	 * 		<TagData...>
	 * 			
	 * </type>
	 * }</pre>
	 */
	private ModuleList elementsToModuleList(NodeList nodeList){
		if(nodeList==null)
			return null;
		ModuleList list=new ModuleList();
		for(int i=0; i<nodeList.getLength();i++) {
			Node n=nodeList.item(i);
			if(n.getNodeName().equals(INSTRUMENT) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String type=eElement.getAttribute(ATTR_TYPE);
				ModuleContent c=new ModuleContent(elementsToTagDataList(eElement.getElementsByTagName(TAGDATA),type), type, null);
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
			if(n.getNodeName().equals(MIC) && n.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement=(Element)n;
				String name=eElement.getAttribute(ATTR_NAME);
				list.put(name, elementsToModuleList(eElement.getElementsByTagName(INSTRUMENT)));
			}
		}
		return list;
	}
	
	private LinkedHashMap<String, HashMap<String,ModuleContent>> elementsToObjectConf(NodeList nodeList){
		if(nodeList==null)
			return null;
		 LinkedHashMap<String, HashMap<String,ModuleContent>> list= new LinkedHashMap<>();
		 
		 // get UNIVERSAL object list
		 int universalIdx=-1;
		 for(int i=0; i<nodeList.getLength();i++) {
			 Node n=nodeList.item(i);
			 if(n.getNodeName().equals(MIC) && n.getNodeType()==Node.ELEMENT_NODE) {
				 Element eElement =(Element)n;
				 String name= eElement.getAttribute(ATTR_NAME);
				 if(name.equals(MDEConfiguration.UNIVERSAL)) {
					 list.put(name, elementsToObjectList(null,eElement.getElementsByTagName(MDE_OBJECT)));
				 }
				 universalIdx=i;
				 break;
			 }
		 }
		 HashMap<String,ModuleContent> uni;
		 if((uni=list.get(MDEConfiguration.UNIVERSAL)) !=null) {
			 for(int i=0; i<nodeList.getLength();i++) {
				 if(i!=universalIdx) {
					 Node n=nodeList.item(i);
					 //TODO: first get UNIVERSAL object list, after that add prop for different mics to this content
					 if(n.getNodeName().equals(MIC) && n.getNodeType()==Node.ELEMENT_NODE) {
						 Element eElement =(Element)n;
						 String name= eElement.getAttribute(ATTR_NAME);
						 list.put(name, elementsToObjectList(uni,eElement.getElementsByTagName(MDE_OBJECT)));
					 }
				 }
			 }
		 }else {
			 System.out.println("ERROR: no objects are given [XMLWriter::elementsToObjects]");
		 }
		return list;
		
	}


	public LinkedHashMap<String, ModuleList> getHardwareConfiguration() {
		// TODO Auto-generated method stub
		return hardwareConfiguration;
	}


	public LinkedHashMap<String, HashMap<String, ModuleContent>> getObjectConfiguration() {
		// TODO Auto-generated method stub
		return objectConfiguration;
	}
	
	
}
