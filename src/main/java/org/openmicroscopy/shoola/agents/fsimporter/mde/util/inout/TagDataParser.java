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

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class TagDataParser {

    final String ELEM_TAGDATA="TagData";
    final String ATTR_ID="ID";
    final String ATTR_NAME="Name";
    final String ATTR_DEFAULT_VAL="DefaultValues";
    final String ATTR_VALUE="Value";
    final String ATTR_VISIBLE="Visible";
    final String ATTR_UNIT="Unit";
    final String ATTR_TYPE="Type";

    /**
     * Builds {@link TagData} element with his properties as attributes.
     * {@code
     * <TagData Name="" Type="" Visible="" Value="" Unit="" DefaultValues="">
     * }
     * @param t given TagData
     * @param doc owning xml document
     * @param elemName element name
     * @param saveVal true if also tagdata value should saved
     * @return
     */
    public Element createXMLElem(TagData t, Document doc, String elemName,boolean saveVal) {
        if(t==null) {
            return null;
        }
        Element result=doc.createElement(elemName);

        result.setAttribute(ATTR_NAME, t.getTagName());
        result.setAttribute(ATTR_TYPE, String.valueOf(t.getTagType()));
        result.setAttribute(ATTR_VISIBLE,String.valueOf( t.isVisible()));
        if(saveVal)
            result.setAttribute(ATTR_VALUE, t.getTagValue());
        else
            result.setAttribute(ATTR_VALUE, "");
        result.setAttribute(ATTR_UNIT, t.getTagUnitString());
        result.setAttribute(ATTR_DEFAULT_VAL, t.getDefaultValuesAsString());
        //result.setAttribute(ATTR_REQ,t.isRequired());

        return result;
    }

    public TagData parseFromConfig(Node n,boolean pre,String parent) {
        TagData t = null;
        if (n.getNodeName().equals(ELEM_TAGDATA) && n.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) n;
            String tagName = eElement.getAttribute(ATTR_NAME);
            String tagVal = "";
            if (pre) {
                tagVal = eElement.getAttribute(ATTR_VALUE);
            }
            String tagUnit = eElement.getAttribute(ATTR_UNIT);
            String tagVis = eElement.getAttribute(ATTR_VISIBLE);
            String defaultVal = eElement.getAttribute(ATTR_DEFAULT_VAL);
            String tagType = eElement.getAttribute(ATTR_TYPE);

            boolean standardConstr=true;
            if (tagType.equals(TagData.ARRAYFIELDS) ){
                if(tagVal!=null && tagVal.length()>0) {
                    standardConstr=false;
                    String[] values=tagVal.split(",");
                    t = new TagData(parent, tagName, values, false, tagType,values.length);
                    if (tagUnit != null && !tagUnit.isEmpty()) {
                        t.setTagUnit(tagUnit);
                    }
                }
            }
            if(standardConstr){
                t = new TagData(parent, tagName, tagVal, tagUnit, false, tagType, defaultVal.split(","));
            }
            t.setVisible(Boolean.parseBoolean(tagVis));

        }
        return t;
    }
}
