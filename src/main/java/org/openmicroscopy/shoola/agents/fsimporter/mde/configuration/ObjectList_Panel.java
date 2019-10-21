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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;


/**
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ObjectList_Panel extends JPanel implements ActionListener{
	private static final String CMD_NEW_OBJ = "create new object";
	private static final String CMD_DEL_OBJ ="delete selected object";
	private static final String CMD_ADD_OBJ = "add object";
	
	private JList objList;
	private String mic;
	private MDEConfiguration conf;
	private JComboBox<String> objects;
	
	public ObjectList_Panel(String mic,DefaultListModel<String> listModel, 
			ListSelectionListener selectListener,MDEConfiguration conf) {
		
		super(new BorderLayout());
		this.removeAll();
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black), "Objects"));
		
		this.mic=mic;
		this.conf=conf;
		
		objList=new JList<String>(listModel);
		objList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		objList.addListSelectionListener(selectListener);

		if(!listModel.isEmpty()) 
			objList.setSelectedIndex(0);
		objList.setVisibleRowCount(10);

		JScrollPane listScrollPane = new JScrollPane(objList);
		add(listScrollPane,BorderLayout.CENTER);

		JButton btnDelObj = new JButton("Delete");
		btnDelObj.setActionCommand(CMD_DEL_OBJ);
		btnDelObj.addActionListener(this);
		JPanel newObjPane=new JPanel();
		
			JButton btnNewObj = new JButton("New");
			btnNewObj.setActionCommand(CMD_NEW_OBJ);
			btnNewObj.addActionListener(this);
			btnNewObj.setEnabled(false);
			
			
		
			JButton btnAddObj = new JButton("Add");
			btnAddObj.setActionCommand(CMD_ADD_OBJ);
			btnAddObj.addActionListener(this);
			btnAddObj.setEnabled(false);
			
//			objects=new JComboBox<String>(conf.getNameOfObjects());
			JPanel btnPanelObj=new JPanel(new FlowLayout(FlowLayout.RIGHT));

			btnPanelObj.add(btnNewObj);
			btnPanelObj.add(btnAddObj);
			btnPanelObj.add(btnDelObj);
			
			newObjPane.setLayout(new BorderLayout());
//			newObjPane.add(objects,BorderLayout.CENTER);
			newObjPane.add(btnPanelObj,BorderLayout.EAST);

		add(newObjPane,BorderLayout.SOUTH);
		revalidate();
		repaint();
		
	}
	
	private void addObject(String objectName) {
		conf.addConfiguration(mic, objectName, conf.getConfiguration(MDEConfiguration.UNIVERSAL, objectName));
		
		//repaint object list
		DefaultListModel<String> listModel=(DefaultListModel<String>) objList.getModel();
		listModel.addElement(objectName);
		objList.setSelectedIndex(listModel.getSize()-1);
		objList.revalidate();
		objList.repaint();
		revalidate();
		repaint();
	}
	
	private void createNewObject(String obj) {
		// TODO: add to oDef and apply for all mics?
		conf.createNewObject(mic, obj);
		
		//repaint object list
		DefaultListModel<String> listModel=(DefaultListModel<String>) objList.getModel();
		listModel.addElement(obj);
		objList.setSelectedIndex(listModel.getSize()-1);
		objList.revalidate();
		objList.repaint();
		revalidate();
		repaint();
	}
	
	private void deleteObject() {
		String objName= (String) objList.getSelectedValue();
		DefaultListModel<String> listModel=(DefaultListModel<String>) objList.getModel();
		int objIndex = objList.getSelectedIndex();
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] DELETE ["+mic+"::"+objName+"]: on index "+objIndex+"/"+listModel.getSize());
		// delete from objList
		listModel.remove(objIndex);
		// delete from instrument list
		conf.removeObjectForMicroscope(objName,mic);
		objList.setSelectedIndex(0);
		
//		ModuleList hardware = conf.getPredefinitions(mic);
//		ImporterAgent.getRegistry().getLogger().debug(this, "\t\t Instruments["+mic+"]: "+hardware.keySet());
	}
	
	public String getSelectedValue() {
		if(objList.getSelectedIndex()!=-1)
			return (String) objList.getSelectedValue();
		
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		switch(cmd) {
		case CMD_ADD_OBJ:
			String obj=String.valueOf(objects.getSelectedItem());
			addObject(obj);
			break;
		case CMD_NEW_OBJ:
			//TODO
			break;
		case CMD_DEL_OBJ:
			deleteObject();
			break;
		
		}
	}

	public void setSelectedIndex(int i) {
		if(!((DefaultListModel<String>) objList.getModel()).isEmpty())
			objList.setSelectedIndex(i);
		
	}
}
