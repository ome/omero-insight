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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout.ModuleTreeParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * import input form values and object tree structure from a xml file .
 * <MDETemplate>
 *     <ObjectPre ID=<ObjectID> Name=<ObjectName>>
 *         <TagData../>
 *         <!--Child objects-->
 *         <ObjectPre ID=<ObjectID> Name=<ObjectName>>
 *             <TagData.../>
 *         </ObjectPre>
 *         ....
 *     </ObjectPre>
 *     ....
 * </MDETemplate>
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class ImportFromTemplateFile {
    private final String ATTR_UUID = "UUID";
    private final String MDE_TEMPLATE = "MDETemplate";
    private final String ELEM_ROOT="ObjectTreeRoot";
    final String ELEM_OBJECT_PRE="ObjectPre";
    final String ELEM_CHILD="ObjectChild";
    final String ELEM_TAGDATA="TagData";
    final String ATTR_PTYPE="P_Type";
    final String ATTR_PIDX="P_ID";
    final String ATTR_TYPE="Type";
    final String ATTR_ID="ID";

    private HashMap<String,List<Element>> xmlObjectPreList;
    private DefaultMutableTreeNode tempObjTree;
    private String fName;


    public ImportFromTemplateFile(String fName) {
        this.fName=fName;
    }

    public List<String> createTypeList(){
        List<String> objectTypes=new ArrayList<>();
        try {
            File tempFile = new File(fName);
            if(tempFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(tempFile);
                doc.getDocumentElement().normalize();

                NodeList conf=doc.getElementsByTagName(MDE_TEMPLATE);
                if(conf!=null && conf.getLength()>0) {
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
                    objectTypes =getTypes(((Element) conf.item(0)).getElementsByTagName(ELEM_OBJECT_PRE),objectTypes);

                }else {
                    ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no object defined in mde template file");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return objectTypes;
    }


    /**
     * Read out objects from template file
     * @param filter
     */
    public void parseTemplateFile(List<String> filter)
    {
        try {
            File tempFile = new File(fName);
            if(tempFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(tempFile);
                doc.getDocumentElement().normalize();

                NodeList conf=doc.getElementsByTagName(MDE_TEMPLATE);
                if(conf!=null && conf.getLength()>0) {
                    generateXMLObjectList(((Element)conf.item(0)).getElementsByTagName(ELEM_OBJECT_PRE));
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
                    this.tempObjTree =elementsToObjTree(((Element) conf.item(0)).getElementsByTagName(ELEM_ROOT),root,filter);
                }else {
                    ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no object defined in mde template file");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * generate hashmap of xml elements objectPre group by type
     * @param nodeList
     */
    private void generateXMLObjectList(NodeList nodeList) {
        xmlObjectPreList=new HashMap<>();
        if(nodeList==null)
            return ;

        for(int i=0; i<nodeList.getLength();i++) {
            Node n = nodeList.item(i);
            if (n.getNodeName().equals(ELEM_OBJECT_PRE) && n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String type = eElement.getAttribute(ATTR_TYPE);

                if (type != null && !type.isEmpty()) {
                    if (xmlObjectPreList.containsKey(type)) {
                        List elemList = xmlObjectPreList.get(type);
                        elemList.add(eElement);
                        xmlObjectPreList.replace(type, elemList);
                    } else {
                        List elemlist=new ArrayList();
                        elemlist.add(eElement);
                        xmlObjectPreList.put(type, elemlist);
                    }
                }
            }
        }
    }


    public DefaultMutableTreeNode getTempObjTree(){
        return tempObjTree;
    }

    private DefaultMutableTreeNode buildTreeFromXML(NodeList nodeList,List<String> filter, NodeList objectPre){
        DefaultMutableTreeNode tree = new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
        if(nodeList==null)
            return tree;

        for(int i=0; i<nodeList.getLength();i++) {
            Node n = nodeList.item(i);
            if (n.getNodeName().equals(ELEM_ROOT) && n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n.getFirstChild();
                String type = eElement.getAttribute(ATTR_TYPE);
                if (filter != null && filter.contains(type)) {
                    // get node + childs

                    ModuleTreeElement mte = new ModuleTreeParser().getModuleTreeElement(eElement, tree);
                    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(mte);
                    parseChildsFromXml(eElement,filter,objectPre,currentNode);
                    tree.add(currentNode);
                }
            }
        }
        return null;

    }

    private void parseChildsFromXml(Element eElement, List<String> filter, NodeList objectPre,
                                    DefaultMutableTreeNode currentNode) {
        if(eElement!=null){
            NodeList listOfChilds = eElement.getElementsByTagName(ELEM_CHILD);
            if(listOfChilds==null){
                return;
            }
            for(int i=0; i<listOfChilds.getLength();i++) {
                DefaultMutableTreeNode tNode= buildChildNode(listOfChilds.item(i),objectPre,eElement.getAttribute(ATTR_ID),
                        eElement.getAttribute(ATTR_TYPE));
                if(tNode!=null)
                    currentNode.add(tNode);
            }
        }
    }

    private DefaultMutableTreeNode buildChildNode(Node chNode, NodeList objectPre,String pIdx, String pType) {
        if(chNode==null || objectPre==null){
            return null;
        }
        Element eElement = (Element) chNode.getFirstChild();
        if(eElement!=null){
            String cNodeID=eElement.getAttribute(ATTR_ID);
            String cNodeType=eElement.getAttribute(ATTR_TYPE);

            //getElementChildsByAttributes(pIdx,pType)
            String xpathExp=MDE_TEMPLATE+"/"+ELEM_OBJECT_PRE+"/@"+ATTR_TYPE+"[.=="+cNodeType+"]";
        }
        return null;
    }

//    private NodeList evaluateXPath(Document doc, String xpathexpression) throws Exception{
//        // Create XPathFactory object
//        XPathFactory xpathFactory = XPathFactory.newInstance();
//
//        // Create XPath object
//        XPath xpath = xpathFactory.newXPath();
//
//        NodeList nodes=null;
//        try
//        {
//            // Create XPathExpression object
//            XPathExpression expr = xpath.compile(xpathExpression);
//
//            // Evaluate expression result on XML document
//            nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//
//        } catch (XPathExpressionException e) {
//            e.printStackTrace();
//        }
//        return nodes;
//    }

    /**
     *
     * @param nodeList list of
     * @param objectTypes
     * @return a list of objecttypes defined in nodelist
     */
    private List<String> getTypes(NodeList nodeList,List<String> objectTypes) {

        if(nodeList==null)
            return objectTypes;

        for(int i=0; i<nodeList.getLength();i++) {
            Node n = nodeList.item(i);
            if (n.getNodeName().equals(ELEM_OBJECT_PRE) && n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String type = eElement.getAttribute(ATTR_TYPE);
                Boolean tagdataDef = eElement.getElementsByTagName(ELEM_TAGDATA) != null &&
                        eElement.getElementsByTagName(ELEM_TAGDATA).getLength() > 0;

                // add only new types to list and if tagdata defs available
                if(type!=null && !type.isEmpty() && !objectTypes.contains(type) && tagdataDef){
                    objectTypes.add(type);
                }
                objectTypes=getTypes(eElement.getElementsByTagName(ELEM_OBJECT_PRE),objectTypes);
            }
        }
        return objectTypes;
    }

    /**
     *
     * @param nodeList list of ObjectTreeRoot elements
     * @param root parent tree node
     * @param filter list of objecttypes which should be parsed
     * @return given root not with child inserted. Childs will be parsed from given nodelist
     */
    private DefaultMutableTreeNode elementsToObjTree(NodeList nodeList,DefaultMutableTreeNode root,List<String> filter) {
        if(nodeList==null)
            return null;

        for(int i=0; i<nodeList.getLength();i++) {
            Node n=nodeList.item(i);
            if(n.getNodeName().equals(ELEM_ROOT) && n.getNodeType()==Node.ELEMENT_NODE) {
                Element eElement=(Element)n;

                Element objPre = getObject(eElement.getAttribute(ATTR_TYPE),eElement.getAttribute(ATTR_UUID));
                DefaultMutableTreeNode subTree = createObjectSubTree(objPre,root,filter);
                if(subTree!=null) {
                    root.add(subTree);
                }

            }
        }
        return root;
    }

    /**
     *
     * @param eElement ObjectPre element
     * @param parent parent tree node
     * @param filter list of objecttypes which should be parsed
     * @return DefaultMutableTreeNode parse from ObjectPre element with all childnodes defined in ObjectChild element
     */
    private DefaultMutableTreeNode createObjectSubTree(Element eElement, DefaultMutableTreeNode parent, List<String> filter) {
        if (eElement == null) {
            return null;
        }
        DefaultMutableTreeNode thisNode = null;
        if (eElement.getNodeName().equals(ELEM_OBJECT_PRE) && eElement.getNodeType() == Node.ELEMENT_NODE) {
            String type = eElement.getAttribute(ATTR_TYPE);

            ModuleTreeElement mte = new ModuleTreeParser().getModuleTreeElementData(eElement, parent,filter.contains(type));
            //System.out.println("Create Node: " + mte.getType()+" -- withData: "+filter.contains(type));
            thisNode = new DefaultMutableTreeNode(mte);
            NodeList childs = eElement.getElementsByTagName(ELEM_CHILD);
            if (childs != null) {
                for (int i = 0; i < childs.getLength(); i++) {
                    Element child = (Element) childs.item(i);
                    Element childObjPre = getObject(child.getAttribute(ATTR_TYPE),child.getAttribute(ATTR_UUID));
                    DefaultMutableTreeNode subTree = createObjectSubTree(childObjPre, thisNode, filter);
                    if (subTree != null) {
                        thisNode.add(subTree);
                    }

                }
            }
        }
        return thisNode;
    }

    /**
     *
     * @param type of object
     * @param uuid of object
     * @return xml element from ObjectPre list of given type with specified uuid
     */
    private Element getObject(String type,String uuid) {
        if(xmlObjectPreList==null)
            return null;
        List<Element> elemList=xmlObjectPreList.get(type);
        if(elemList!=null) {
            for (Element elem : elemList) {
                if (elem.getAttribute(ATTR_UUID).equals(uuid)) {
                    return elem;
                }
            }
        }else{
            //System.out.println("Cannot parse elements of type: "+type);
        }
        return null;
    }
}
