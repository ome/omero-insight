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
package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagDataProp;


/**
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class Editor_TagDataProp extends JPanel 
{
	private JPanel rowData;
	private ModuleContent content;
	private ModuleContent origContent;
	private int numberOfLabels=3;
	public Editor_TagDataProp(ModuleContent content) {
		super();
		if(content!=null) {
			this.content=new ModuleContent(content);
			this.origContent=content;
			rowData=new JPanel(new GridBagLayout());
			if(content!=null)
				addDataRow(content.getList());
			add(rowData);

		}
	}

	/**
	 * Save input to content. Requires input mask JLabel : JComboBox : JCheckBox. 
	 */
	public void saveContent()
	{
		if(rowData==null)
			return;
		// ignore labels
		for(int i=numberOfLabels;i<rowData.getComponentCount();i=i+3) {
			String name=((JLabel)rowData.getComponent(i)).getText();
			Object unit=null;
			if(rowData.getComponent(i+1) instanceof JComboBox) unit=((JComboBox)rowData.getComponent(i+1)).getSelectedItem();
			boolean visible=((JCheckBox)rowData.getComponent(i+2)).isSelected();
			
			TagData t= content.getTag(name);
			if(unit!=null) {
				t.setTagUnit(String.valueOf(unit));
			}
			t.setVisible(visible);
			content.set(((JLabel)rowData.getComponent(i)).getText(), t);
		}
	}
	public void resetContent()
	{
		rowData.removeAll();
		content=origContent;
		addDataRow(content.getList());
	}
	
	
	private void addDataRow(LinkedHashMap<String,TagData> list) {
		// define labels
		JLabel lName=new JLabel("TagName");lName.setFont(lName.getFont().deriveFont(Font.BOLD));
		JLabel lUnit=new JLabel("Unit");lUnit.setFont(lUnit.getFont().deriveFont(Font.BOLD));
		JLabel lVisible=new JLabel("Visible");lVisible.setFont(lVisible.getFont().deriveFont(Font.BOLD));
		JLabel lRequired=new JLabel("Required");lRequired.setFont(lRequired.getFont().deriveFont(Font.BOLD));
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;

		if(list!=null ) {
			c.insets = new Insets( 0, 1, 1, 0);
			c.gridwidth = 1;//GridBagConstraints.RELATIVE; //next-to-last
			c.fill = GridBagConstraints.HORIZONTAL;  
			// add labels at the top
			addGB(lName,0,0,c);
			addGB(lUnit,1,0,c);
			addGB(lVisible,2,0,c);
			addGB(lRequired,3,0,c);
			
			int y=1;
			for (Map.Entry<String, TagData> entry : list.entrySet()) {
				TagData t=entry.getValue();
				
				c.weightx = 1.0;   
				//c1
				addGB(new JLabel(t.getTagName()),0,y,c);
				
				//c2
				if(!t.getTagUnitString().equals("")) {
				JComboBox unitCB = t.getUnitCombo();
				addGB(unitCB, 1,y,c);
				}else {
					addGB(new JLabel(""),1,y,c);
				}
				
				//c3
				c.weightx = 0.5;
				JCheckBox cb=new JCheckBox();
				cb.setSelected(t.isVisible()); 
				addGB(cb,2,y,c);
				
				//c4
				c.weightx = 0.5;
				JCheckBox cb2=new JCheckBox();
				cb2.setSelected(t.getTagProp()); 
				addGB(cb2,2,y,c);
				
				y++;
			}
		}
	}
	private void addGB(Component comp,int x, int y,GridBagConstraints c) {
		c.gridx=x;
		c.gridy=y;
		rowData.add(comp,c);
	}
	public ModuleContent getContent() {
		if(rowData==null)
			return origContent;

		return content;
	}
	
	
	

}
