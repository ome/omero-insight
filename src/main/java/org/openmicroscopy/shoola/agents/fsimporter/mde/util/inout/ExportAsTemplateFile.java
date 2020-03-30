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
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Export input form values and object tree structure to a xml file that can
 * reloaded as template to fill out fields.
 * <MDETemplate>
 *     <ObjectPre ID=<ObjectID> Type=<ObjectName> UUID=<UUID>>
 *         <TagData../>
 *         ....
 *         <!--Child objects-->
 *         <ObjectChild ID=<ObjectID> Type=<ObjectName> UUID=<UUID>/>
 *         ....
 *     </ObjectPre>
 *     ....
 *     <ObjectTreeRoot ID=<ObjectID> Type=<ObjectName> UUID=<UUID>/>
 *     ...
 * </MDETemplate>
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class ExportAsTemplateFile {

    final String MDE_TEMPLATE = "MDETemplate";
    final String ELEM_ROOT="ObjectTreeRoot";
    final String ELEM_OBJECT_PRE="ObjectPre";
    final String ELEM_CHILD="ObjectChild";

    final String ELEM_TAGDATA="TagData";
    final String ATTR_UUID="UUID";
    final String ATTR_ID="ID";
    final String ATTR_NAME="Name";
    final String ATTR_DEFAULT_VAL="DefaultValues";
    final String ATTR_VALUE="Value";
    final String ATTR_VISIBLE="Visible";
    final String ATTR_UNIT="Unit";
    final String ATTR_TYPE="Type";

    String fName;
    public ExportAsTemplateFile(String fileName){
        this.fName=fileName;
    }

    public void export(DefaultMutableTreeNode tree,List<String> filter){
        Document doc=generateXML(tree,filter);
        if(doc!=null) {
            saveXML(doc,fName);
        }
    }

    /**
     * Saves given document to file fileName
     * @param doc
     * @param fileName
     */
    private void saveXML(Document doc,String fileName) {
        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(fileName));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging
            transformer.transform(domSource, streamResult);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    /**
     * generate template xml from given tree.
     * @param tree
     * @param filter list of objecttypes for that tagvalue should be saved
     * @return
     */
    private Document generateXML(DefaultMutableTreeNode tree, List<String> filter){
        Document document = null;
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            // root element
            Element rootElem = document.createElement(MDE_TEMPLATE);
            document.appendChild(rootElem);

            generateRoot_XMLElem(tree,document,filter,rootElem);


        }catch(Exception e){
            ImporterAgent.getRegistry().getLogger().error(this,"[MDE] Cannot generate template xml");
            e.printStackTrace();
        }
        return document;
    }

    /**
     * generate ObjectTreeRoot element
     * @param node tree
     * @param document
     * @param filter list of objecttypes for that tagvalue should be saved
     * @param rootElem MDE_Template elem
     */
    private void generateRoot_XMLElem(DefaultMutableTreeNode node, Document document, List<String> filter, Element rootElem) {

        if(node == null) {
            return;
        }

        if(((ModuleTreeElement)node.getUserObject())!=null) {
            String objectType = ((ModuleTreeElement) node.getUserObject()).getType();

            // if ome-model root -> generate rootElems of his children
            if (objectType.equals(TagNames.OME_ROOT)) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    generateRoot_XMLElem((DefaultMutableTreeNode) node.getChildAt(i), document, filter, rootElem);
                }
            } else  {
                Element objPre= generateObjectPre_XMLElem(node,document,filter,rootElem);
               if(objPre!=null){
                   Element rootObj = document.createElement(ELEM_ROOT);
                   rootObj.setAttribute(ATTR_ID, String.valueOf(((ModuleTreeElement) node.getUserObject()).getChildIndex()));
                   rootObj.setAttribute(ATTR_TYPE, objectType);
                   rootObj.setAttribute(ATTR_UUID,objPre.getAttribute(ATTR_UUID));

                   rootElem.appendChild(rootObj);
                   rootElem.appendChild(objPre);
               }
            }
        }
    }

    /**
     * generates ObjectPre element
     * @param node
     * @param doc
     * @param filter list of objecttypes for that tagvalue should be saved
     * @param root
     * @return if [node type is in filter list OR node is null OR xml element of content could not created]-> null,
     * else ->ObjectPre element
     */
    private Element generateObjectPre_XMLElem(DefaultMutableTreeNode node, Document doc, List<String> filter,
                                              Element root){

        if(node == null ) {
            return null;
        }
        String objectType=((ModuleTreeElement)node.getUserObject()).getType();

        // save only enabled objects
        if(!ModuleController.getInstance().configurationExists(objectType)){
            return null;
        }
        // only  data of objects available in filter will be saved
        ModuleContentParser mc_parser=new ModuleContentParser();
        Element nodeObj = mc_parser.createXMLElem(((ModuleTreeElement)node.getUserObject()).getData(),
                String.valueOf(((ModuleTreeElement)node.getUserObject()).getChildIndex()),doc,ELEM_OBJECT_PRE,filter.contains(objectType));
        nodeObj.setAttribute(ATTR_UUID,String.valueOf(generateUniqueID()));

        if(nodeObj!=null) {
            // add all child objects as objectPre
            for(int i = 0 ; i < node.getChildCount(); i++) {
                Element childObj = generateObjectPre_XMLElem((DefaultMutableTreeNode) node.getChildAt(i),
                        doc,filter,root);
                if(childObj!=null) {
                    nodeObj.appendChild(generateChild_XMLElem(childObj,doc));
                    root.appendChild(childObj);
                }
            }
            //ImporterAgent.getRegistry().getLogger().debug(null, "\nadd to template "+ node.getUserObject().toString());
            root.appendChild(nodeObj);
        }
        return nodeObj;
    }

    /**
     * generates ObjectChild element
     * @param childObj objectPre of child
     * @param doc
     * @return ObjectChild element
     */
    private Element generateChild_XMLElem(Element childObj, Document doc) {
        Element child = doc.createElement(ELEM_CHILD);
        child.setAttribute(ATTR_UUID,childObj.getAttribute(ATTR_UUID));
        child.setAttribute(ATTR_ID, childObj.getAttribute(ATTR_ID));
        child.setAttribute(ATTR_TYPE, childObj.getAttribute(ATTR_TYPE));
        return child;
    }

    /**
     * generates unique id
     * @return
     */
    private String generateUniqueID(){
        //generate random UUIDs
        UUID idOne = UUID.randomUUID();
        return idOne.toString();
    }





}
