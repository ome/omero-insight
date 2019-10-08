/*
 * Copyright (C) <2018-2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ContentViewer;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleContentTableModel;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;


/**
 * TODO: Allgemeiner fuer tagDataListen
 * Table of predefined values for an object
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ObjectTable extends JPanel{
//TODO extends JPanel unessesarry after clean up
	private List<ModuleContent> availableObjects;
	private int currentSelection;
	
	public ObjectTable(List<ModuleContent> availableElems )
	{
		if(availableElems!=null) {
			this.availableObjects=availableElems;
		}
		currentSelection=-1;
	}
	
	public ObjectTable(ObjectTable orig) {
		if(orig!=null) {
			if(orig.availableObjects!=null) {
				this.availableObjects=new ArrayList<>();
				for(ModuleContent c:orig.availableObjects) {
					this.availableObjects.add(new ModuleContent(c));
				}
			}
			this.currentSelection=orig.currentSelection;
		}
	}
	
	
	public void setCurrentSelected(int index) {
		this.currentSelection=index;
	}
	
	
	public int getOriginalIndex(String id) {
		int oidx=0;
		for(ModuleContent c:availableObjects) {
			if(c.getAttributeValue(TagNames.ID).equals(id)) {
				return oidx;	
			}
			oidx++;
		}
		return -1;
	}
	
	
	/**
	 * 
	 * @param c
	 * @return index of element in the table that is equal with given elem
	 */
	public int getElementIndex(ModuleContent elem) {
		int idx=0;
		
		if(currentSelection!=-1)
			return currentSelection;
		
		if(availableObjects!=null && elem.getList()!=null) {
			for(ModuleContent c:availableObjects) {
				if(MDEHelper.isEqual(c, elem)) {
					return idx;
				}
				idx++;
			}
		}
		return -1;
	}
	
	
	protected JTable getObjectTable() {
		JTable table=new JTable();
		ModuleContentTableModel dataModel = null;
		if(availableObjects!=null && availableObjects.size()>0){
			dataModel =new ModuleContentTableModel(availableObjects.get(0).getIdents());
			for(int i=0; i<availableObjects.size(); i++){
				dataModel.addRow(availableObjects.get(i));
			}
		}
		if(dataModel!=null) {
			table.setModel(dataModel);
		}
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(), 60));
		table.setFillsViewportHeight(true);
		
		return table;
	}
	
	public JPanel buildGUI(int selectedIndex,final int originalIndex,final ContentViewer contentViewer)
	{
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		this.currentSelection=selectedIndex;
		
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(5, 5));
		pane.setBorder(new EmptyBorder(0,0,0,0));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));

		JTable table = getObjectTable();
		if(selectedIndex!=-1) {
			table.setRowSelectionInterval(selectedIndex, selectedIndex);
		}
		addListSelectionListener(contentViewer,table);
		JScrollPane scrollPane = new JScrollPane(table);
		
		
		pane.add(label,BorderLayout.NORTH);
		pane.add(scrollPane, BorderLayout.CENTER);
		
		panel.add(pane,BorderLayout.CENTER);
		
		return panel;
	}
	
	private void addListSelectionListener(final ContentViewer contentViewer,JTable table) {
		ListSelectionListener listener=new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//find out which elem is selected
				if(e.getValueIsAdjusting()) {
					ListSelectionModel lsm= (ListSelectionModel)e.getSource();
					if(lsm ==null || lsm.isSelectionEmpty())
						return;
					int idx=-1;
					if(lsm.isSelectedIndex(e.getFirstIndex())) {
						idx=e.getFirstIndex();
					}else if(lsm.isSelectedIndex(e.getLastIndex())) {
						idx=e.getLastIndex();
					}
					// replace current object element with selected element in the table
					if(idx>-1 && idx<availableObjects.size()) {
						ModuleContent c=availableObjects.get(idx);
						ModuleContent origContent=null;
						if(currentSelection!=-1) origContent=availableObjects.get(currentSelection);
						currentSelection=idx;
						contentViewer.replaceData(c,origContent);
					}
				}
			}
		};

		
		if(table!=null) {
			table.getSelectionModel().addListSelectionListener(listener);
		}
	}
	
}
