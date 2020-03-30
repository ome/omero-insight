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

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.w3c.dom.Element;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class ModuleTreeParser {
    final String ATTR_TYPE="Type";
    final String ATTR_ID="ID";
    public Element createXMLElement(){return null;}

    public ModuleTreeElement getModuleTreeElement(Element eElem,DefaultMutableTreeNode parent)
    {
        String type = eElem.getAttribute(ATTR_TYPE);
        String index = eElem.getAttribute(ATTR_ID);
        ModuleContentParser mc_parser = new ModuleContentParser();
        return new ModuleTreeElement(type,null,index,mc_parser.parseFromConfig(eElem,type,true,null),parent);
    }

    public ModuleTreeElement getModuleTreeElementData(Element eElem,DefaultMutableTreeNode parent,boolean getdata)
    {
        String type = eElem.getAttribute(ATTR_TYPE);
        String index = eElem.getAttribute(ATTR_ID);
        ModuleContentParser mc_parser = new ModuleContentParser();

        return new ModuleTreeElement(type,null,index,
                mc_parser.parseDataFromConfig(eElem,type,true,null,getdata),parent);
    }
}
