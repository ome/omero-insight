/*
 * Copyright (C) <2016-2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;

/**
 * Container holds view and modelobj for selection
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class NodeContainer 
{
//	private MDEModelManager modelManager;
	private boolean isDir;
	private String name;
	private DefaultMutableTreeNode rootNode;
	private ModuleList instrumentList;
	private HashMap<String, List<TagData>> input;
	
	
	 /**
     * Container for modelManager and view.
     * @param objName file name
	 * @param importData given import information for this data.
	 * @param pTree given parent information for this data.
	 * @param parentPanel parent JPanel of this component.
	 * @param current existing container for selected node
	 * @param isDir TODO
     */
	public NodeContainer(String objName,ImportUserData importData,
			DefaultMutableTreeNode pTree, MetaDataDialog parentPanel, NodeContainer current, boolean isDir) throws Exception
	{
		this.isDir=isDir;
		this.name=objName;
		this.rootNode=pTree;
	}

	
	public String getNodeObject()
	{
		return name;
	}

	public boolean isDir(){
		return isDir;
	}
	

	public DefaultMutableTreeNode getTreeNode() {
		return rootNode;
	}
	
	
	public void setTreeNode(DefaultMutableTreeNode treeNode) {
		rootNode=treeNode;
	}


	public ModuleList getInstruments() {
		return instrumentList;
	}
	
	public void setInstruments(ModuleList list) {
		this.instrumentList=list;
	}

	
}
