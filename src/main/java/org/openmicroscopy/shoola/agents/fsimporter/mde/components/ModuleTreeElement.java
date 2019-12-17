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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.w3c.dom.Element;

/**
 * Element/node in of object tree.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleTreeElement {
	/* module name TODO: better link to tagData for update issues?*/
	private String name;
	/* module type, e.g. OME:Detector*/
	private String type;
	/* index of module regarding ome-id */
	private String id;
	/*describes which child of parent node this node is */
	private int childIndex;
	
	/*data*/
	private ModuleContent data;
	
	public ModuleTreeElement(ModuleContent c,DefaultMutableTreeNode parent) {
		this.type = c==null?TagNames.OME_ROOT:c.getType();
//		this.name="Container";
		this.id=null;
		this.data=null;
		this.childIndex=MDEHelper.getChildCount(type, parent);
	}
	public ModuleTreeElement(String type,String name,String index,ModuleContent data,DefaultMutableTreeNode parent) {
		this.type = type;
		this.name=name;
		this.id=index;
		this.data=data;
		this.childIndex=MDEHelper.getChildCount(type, parent);
	}

	public ModuleTreeElement(ModuleTreeElement orig) {
		this.type=orig.type;
		this.id=orig.id;
		this.childIndex=orig.childIndex;
		// do i really want to clone the content or better reset to empty content for this type (clear all tag values)
		this.data=new ModuleContent(orig.getData());
	}

	
	public ModuleContent getData()
	{
		return data;
	}
	
	public void setData(ModuleContent data) {
		this.data=data;
		if(data!=null) {
			this.id=data.getAttributeValue(TagNames.ID);
			this.name=data.getAttributeValue(TagNames.MODEL);
		}
	}
	
	public void setProperties(ModuleConfiguration conf) {
		if(data!=null)
			data.setProperties(conf);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndex() {
		return id;
	}

	public void setChildIndex(DefaultMutableTreeNode node) {
		this.childIndex = MDEHelper.getChildCount(type, node);
	}

	
	public void setType(String type)
	{
		this.type = type;
	}
	public String getType()
	{
		return type;
	}
	
	public boolean isContainer() {
		return data==null || data.getList()==null;
	}
	
	/** 
	 * needed to set name of DefaultMutableTreeNode inside the tree.
	 */
	@Override
	public String toString() {
		String elementID="["+type+"]" + "{"+childIndex+"}";
		return elementID;
	}
	
	public String getElementName() {
		return "["+type+"]"+"{"+childIndex+"}";
	}

	public int getChildIndex() {
		return childIndex;
	}
	
	public void printContent() {
		if(data==null) {
			ImporterAgent.getRegistry().getLogger().debug(this, "-- PRINT ModuleContent: \n\tcontent is null!");
		}else {
			data.print();
		}
	}
	public void resetInput() {
		if(data==null)
			return;
		data.resetInput();
		
	}
	
	
	
}
