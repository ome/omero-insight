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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ome.xml.model.OME;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;

/**
 * Panel to visualize object tree.
 * 
 * You can add and remove elements. Kind of available Elements has to declare in a JSON or RDF file like:
 * Module: name:
 * 		   List of TagData:
 *         List of predefined values for this module:
 *         parents:
 * OME elements has to named by OME:<OMEName>. See also list of available tags for OME elements.        
 * TODO: You can create your MDE specification by specification of the ModuleTree in the GUI and than-> export specification
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class DynamicModuleTree extends JPanel {

	private static String ADD_NODE_CMD = "add";
	private static String DELETE_NODE_CMD = "delete";
	public static final String RESET_TREE_CMD = "reset";
	
	private ModuleTree treePanel;
	private ModuleController controller;
	
	
	public DynamicModuleTree(DefaultMutableTreeNode elem,ActionListener listener) {
		super(new BorderLayout());
		this.controller=ModuleController.getInstance();
		treePanel = new ModuleTree(elem,listener);
		
		
		add(treePanel,BorderLayout.CENTER);
		add(generateButtonPane(listener),BorderLayout.SOUTH);
	}

	public DynamicModuleTree(ActionListener listener) {
		this(ModuleController.getInstance().getTree(),listener);
	}
	
	
	private JPanel generateButtonPane(ActionListener l) {
		JButton resetBtn= new JButton("Reset object tree");
		resetBtn.setActionCommand(RESET_TREE_CMD);
		resetBtn.addActionListener(l);
		resetBtn.setToolTipText("Reset object tree to initial state. Initial state of a child node is inherit last state of parent node."
				+ "				Reload user input to initial state objects.");
		
		JPanel btnPanel = new JPanel(new GridLayout(0, 1));
		btnPanel.add(resetBtn);
		
		return btnPanel;
	}


	public void addTreeSelectionListener(MDEContent mdeContent) {
		treePanel.addTreeSelectionListener(mdeContent);
		
	}

	public DefaultMutableTreeNode getLastSelectedPathComponent() {
		return treePanel.getLastSelectedPathComponent();
	}
	
	public DefaultMutableTreeNode getRootNode() {
		return treePanel.getRoot();
	}
	public ModuleTree getModuleTree() {
		return treePanel;
	}
	
	public DefaultMutableTreeNode selectFirstNode() {
		if(getRootNode()==null || getRootNode().getChildCount()==0)
			return null;
		
		DefaultMutableTreeNode firstNode= (DefaultMutableTreeNode) getRootNode().getFirstChild();
		if(firstNode !=null) {
			getTree().getSelectionModel().setSelectionPath(new TreePath(firstNode.getPath()));
		}
		return firstNode;
	}
	

	public JTree getTree() {
		if(treePanel== null)
			return null;
		return treePanel.getTree();
	}
	
}

