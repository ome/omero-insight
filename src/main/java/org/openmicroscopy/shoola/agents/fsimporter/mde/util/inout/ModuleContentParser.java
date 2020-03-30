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

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class ModuleContentParser {

    final String ATTR_ID="ID";
    final String ATTR_TYPE="Type";

    final String ELEM_TAGDATA="TagData";



    /**
     * Builds the tag for a certain instrument from {@link ModuleContent} object
     * <pre>{@code
     * <ObjectPre Type="" ATTR_ID="" >
     * 		<TagData...>
     *
     * </ObjectPre>
     * }</pre>
     * @param c    {@link ModuleContent} object holds instrument values
     * @param idx value for attribute id
     * @param doc owning xml document
     * @param elemName name of generated element
     */
    public Element createXMLElem(ModuleContent c, String idx, Document doc,String elemName,boolean saveVal) {
        if(c==null) {
            return null;
        }
        Element result = doc.createElement(elemName);
        result.setAttribute(ATTR_ID, idx);
        result.setAttribute(ATTR_TYPE, c.getType());

        List<TagData> list= c.getTagList();
        if(list == null || !saveVal)
            return result;
        //add tagData
        TagDataParser td_parser=new TagDataParser();
        for (TagData tagData : list) {
            Element child = td_parser.createXMLElem(tagData, doc, ELEM_TAGDATA, saveVal);
            if (child != null)
                result.appendChild(child);
        }
        return result;
    }

    public ModuleContent parseFromConfig(Element eElement,String type,boolean pre,String[] parents) {
        return new ModuleContent(
                elementsToTagDataList(eElement.getElementsByTagName(ELEM_TAGDATA), type,pre), type, parents);
    }

    /**
     * Parse only tagdata from given element. Ignore object configuration changes.
     * @param eElement given xml element
     * @param type type of ModuleContent
     * @param pre
     * @param parents
     * @param getdata true: load data value from element, false: return empty modulcontent for given type
     * @return ModuleContent parse from given element
     */
    public ModuleContent parseDataFromConfig(Element eElement,String type,boolean pre,String[] parents,boolean getdata) {
        ModuleContent mc = ModuleController.getInstance().getContentOfType(type);
        if(mc!=null) {
            mc.setParents(parents);
            if(getdata)
                mc.setData(elementsToTagDataList(eElement.getElementsByTagName(ELEM_TAGDATA), type, pre));
            else
                mc.setData(null);
        }
        return mc;
    }


    /**
     * Parse list of {@link TagData} from given NodeList
     * {@code
     * <TagData Name="" Type="" Visible="" Value="" Unit="" DefaultValues="">
     * }
     * @param nodeList list of elements TAGDATA
     * @param parent owned object
     * @param pre is true if TagData element is part of predefinitions (objPre), else false (disable value specification for objectConf)
     * @return
     */
    private LinkedHashMap<String,TagData> elementsToTagDataList(NodeList nodeList, String parent, boolean pre){
        if(nodeList==null)
            return null;
        LinkedHashMap<String,TagData> list = new LinkedHashMap<>();
        for(int i=0; i<nodeList.getLength();i++) {
            Node n=nodeList.item(i);
            TagData t=new TagDataParser().parseFromConfig(n,pre,parent);
            if(t!=null){
                list.put(t.getTagName(),t);
            }
        }
        return list;
    }

}
