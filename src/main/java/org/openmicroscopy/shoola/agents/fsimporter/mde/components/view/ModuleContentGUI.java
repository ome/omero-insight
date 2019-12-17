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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

import org.openmicroscopy.shoola.util.ui.JXTaskPaneContainerSingle;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

/**
 * Visualize object content: ContentViewer of object + all childs. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleContentGUI extends JPanel {
	Color color=new Color(240, 240, 240);
	
	private ModuleController controller;
	private LinkedHashMap<String, ObjectTable> hardwareTables;
	
//	private String name;
	
	public ModuleContentGUI() {
		this(null,null);
	}
	
	public ModuleContentGUI(DefaultMutableTreeNode root,LinkedHashMap<String, ObjectTable> hardwareTables) {
		setLayout(new BorderLayout());
		this.controller=ModuleController.getInstance();
		this.hardwareTables=hardwareTables;
		if(root!=null) {
			JXTaskPaneContainer panel= new JXTaskPaneContainer();
			panel.setBackground(UIUtilities.BACKGROUND);
			if (panel.getLayout() instanceof VerticalLayout) {
				VerticalLayout vl = (VerticalLayout) panel.getLayout();
				vl.setGap(2);
			}
			panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
			
			addContent(panel,root);
			add(panel,BorderLayout.CENTER);
			
		}else {
			add(new JLabel("NO Content"),BorderLayout.CENTER);
		}
		
	}
	
	private void addContent(JXTaskPaneContainer parent,DefaultMutableTreeNode node) {
		if(node.getChildCount()>0) {
			JXTaskPaneContainer nodeContent = new JXTaskPaneContainer();
			nodeContent.setBackground(UIUtilities.BACKGROUND);
			if (nodeContent.getLayout() instanceof VerticalLayout) {
				VerticalLayout vl = (VerticalLayout) nodeContent.getLayout();
				vl.setGap(2);
			}
			nodeContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
			//add this content
			try {
				JXTaskPane taskPane=new ContentViewer(node.getUserObject().toString(), 
						getHardwareTable(((ModuleTreeElement) node.getUserObject()).getType()), ((ModuleTreeElement)node.getUserObject()).getData());
				for(int i = 0 ; i < node.getChildCount(); i++) {
					String type=((ModuleTreeElement) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject()).getType();
					if(controller.configurationExists(type)) {
						addContent(nodeContent,(DefaultMutableTreeNode)node.getChildAt(i));
					}
				}
				taskPane.add(nodeContent);
				parent.add(taskPane);
			}catch(Exception e) {
				ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] can't load content of "+node.getUserObject().toString());
				e.printStackTrace();
			}
			
		}else {
			addLeafContent(parent,node);
		}
	}
	
	private void addLeafContent(JXTaskPaneContainer parent,DefaultMutableTreeNode node) {
		ModuleContent content=((ModuleTreeElement)node.getUserObject()).getData();

		if(controller!=null && content!=null) {
			try {
				JXTaskPane taskPane=new ContentViewer(node.getUserObject().toString(), 
						getHardwareTable(((ModuleTreeElement) node.getUserObject()).getType()), content);
				parent.add(taskPane);
			}catch(Exception e) {
				ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] can't load content of "+node.getUserObject().toString());
				e.printStackTrace();
			}
		}else {
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] content of node "+node.getUserObject().toString()+" is empty [ModuleContentGUI::addLeafContent]");
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return a copy of hardware table
	 */
	private ObjectTable getHardwareTable(String key)
	{
		if(hardwareTables==null)
			return null;
		if(hardwareTables.get(key)==null)
			return null;
		return new ObjectTable(hardwareTables.get(key));
	}
}
