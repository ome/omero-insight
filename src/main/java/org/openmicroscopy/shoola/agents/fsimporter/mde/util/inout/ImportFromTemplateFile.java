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

import omero.log.LogMessage;
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

                NodeList conf=doc.getElementsByTagName(ElementNames.MDE_TEMPLATE);
                if(conf!=null && conf.getLength()>0) {
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
                    objectTypes =getTypes(((Element) conf.item(0)).getElementsByTagName(ElementNames.ELEM_OBJECT_PRE),objectTypes);

                }else {
                    ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no object defined in mde template file");
                }
            }
        }catch(Exception e) {
            String s = "[MDE] An error occurred when parsing file: ";
            LogMessage msg = new LogMessage();
            msg.print(s);
            msg.print(e);
            ImporterAgent.getRegistry().getLogger().error(this, msg);
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

                NodeList conf=doc.getElementsByTagName(ElementNames.MDE_TEMPLATE);
                if(conf!=null && conf.getLength()>0) {
                    generateXMLObjectList(((Element)conf.item(0)).getElementsByTagName(ElementNames.ELEM_OBJECT_PRE));
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new ModuleTreeElement(null,null));
                    this.tempObjTree =elementsToObjTree(((Element) conf.item(0)).getElementsByTagName(ElementNames.ELEM_ROOT),root,filter);
                }else {
                    ImporterAgent.getRegistry().getLogger().info(this,"[MDE] no object defined in mde template file");
                }
            }
        }catch(Exception e) {
            String s = "[MDE] An error occurred when parsing TemplateFile: ";
            LogMessage msg = new LogMessage();
            msg.print(s);
            msg.print(e);
            ImporterAgent.getRegistry().getLogger().error(this, msg);
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
            if (n.getNodeName().equals(ElementNames.ELEM_OBJECT_PRE) && n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String type = eElement.getAttribute(ElementNames.ATTR_TYPE);

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
            if (n.getNodeName().equals(ElementNames.ELEM_OBJECT_PRE) && n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String type = eElement.getAttribute(ElementNames.ATTR_TYPE);
                Boolean tagdataDef = eElement.getElementsByTagName(ElementNames.ELEM_TAGDATA) != null &&
                        eElement.getElementsByTagName(ElementNames.ELEM_TAGDATA).getLength() > 0;

                // add only new types to list and if tagdata defs available
                if(type!=null && !type.isEmpty() && !objectTypes.contains(type) && tagdataDef){
                    objectTypes.add(type);
                }
                objectTypes=getTypes(eElement.getElementsByTagName(ElementNames.ELEM_OBJECT_PRE),objectTypes);
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
            if(n.getNodeName().equals(ElementNames.ELEM_ROOT) && n.getNodeType()==Node.ELEMENT_NODE) {
                Element eElement=(Element)n;

                Element objPre = getObject(eElement.getAttribute(ElementNames.ATTR_TYPE),eElement.getAttribute(ElementNames.ATTR_UUID));
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
        if (eElement.getNodeName().equals(ElementNames.ELEM_OBJECT_PRE) && eElement.getNodeType() == Node.ELEMENT_NODE) {
            String type = eElement.getAttribute(ElementNames.ATTR_TYPE);

            ModuleTreeElement mte = new ModuleTreeParser().getModuleTreeElementData(eElement, parent,filter.contains(type));

            thisNode = new DefaultMutableTreeNode(mte);
            NodeList childs = eElement.getElementsByTagName(ElementNames.ELEM_CHILD);
            if (childs != null) {
                for (int i = 0; i < childs.getLength(); i++) {
                    Element child = (Element) childs.item(i);
                    Element childObjPre = getObject(child.getAttribute(ElementNames.ATTR_TYPE),child.getAttribute(ElementNames.ATTR_UUID));
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
                if (elem.getAttribute(ElementNames.ATTR_UUID).equals(uuid)) {
                    return elem;
                }
            }
        }
        return null;
    }
}
