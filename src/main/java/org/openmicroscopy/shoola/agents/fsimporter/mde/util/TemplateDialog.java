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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout.TypeFilter_GUI;

/**
 * Save input objects as template
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class TemplateDialog extends JDialog implements ActionListener{

	
	
	private JPanel chBoxPanel;
	private JButton btn_OK;
	private JButton btn_cancel;
	private JButton btn_filter_save;
	private JButton btn_filter_load;
	private JButton btn_browse_save;
	private JTextField txt_path;
	private JCheckBox cb_loadObjectData;
	private JCheckBox cb_loadTreeStructure;
	private JCheckBox cb_loadObjectDef;
	private List<String> moduleList;
	private File tempFile;
	private final String suffix=".xml";
	private DefaultMutableTreeNode tree;
	private boolean cancel;
	
	private JButton btn_browse_load;

	
	public TemplateDialog(JFrame parent, File tempFile, boolean load, DefaultMutableTreeNode root)
	{
		super(parent,"");
		this.tempFile=tempFile;
		this.tree=root;
		cancel =false;
		if(load) {
			this.setTitle("Load Metadata from Template File ");
			buildGUI_loadFile();
		}else {
			this.setTitle("Save Metadata to Template File ");
			buildGUI_saveFile();
		}
		pack();
		setVisible(true);
	}
	
	private void buildGUI_loadFile(){
		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout(5,5));
		setModal(true);
		
	    
		btn_OK = new JButton("OK");
		btn_OK.addActionListener(this);
		btn_cancel = new JButton("Cancel");
		btn_cancel.addActionListener(this);
		Box btnPane=Box.createHorizontalBox();
		btnPane.add(btn_cancel);
		btnPane.add(Box.createHorizontalStrut(5));
		btnPane.add(btn_OK);

		JPanel subPanel= new JPanel();
		Border titleBorder = BorderFactory.createTitledBorder("Configuration:");
		//subPanel.setLayout(new BorderLayout(5,5));
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		//subPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		subPanel.setBorder(titleBorder);

		btn_filter_load = new JButton("Filter by objects: data to load...");
		btn_filter_load.addActionListener(this);
		subPanel.add(btn_filter_load);

		cb_loadObjectData = new JCheckBox("Load object data");
		cb_loadObjectData.setSelected(true);
		cb_loadObjectData.setEnabled(false);
		subPanel.add(cb_loadObjectData);

		cb_loadObjectDef = new JCheckBox("Load object definition");
		cb_loadObjectDef.setSelected(false);
		cb_loadObjectDef.setEnabled(false);
		subPanel.add(cb_loadObjectDef);

		cb_loadTreeStructure = new JCheckBox("Load tree structure");
		cb_loadTreeStructure.setSelected(false);
		cb_loadTreeStructure.setEnabled(false);
		subPanel.add(cb_loadTreeStructure);

		JLabel srcPath_Lbl=new JLabel("Source");
		txt_path =new JTextField(50);
		txt_path.setEditable(false);
		txt_path.setToolTipText("Source template file");
		if(tempFile!=null)
			txt_path.setText(tempFile.getAbsolutePath());
		btn_browse_load =new JButton("Browse");
		btn_browse_load.addActionListener(this);
		JPanel destP=new JPanel();
		destP.add(srcPath_Lbl);
		destP.add(txt_path);
		destP.add(btn_browse_load);

		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BorderLayout(5,5));
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		mainPanel.add(subPanel,BorderLayout.CENTER);
		mainPanel.add(destP,BorderLayout.SOUTH);

		getContentPane().add(mainPanel,BorderLayout.CENTER);
		getContentPane().add(btnPane,BorderLayout.SOUTH);
	    }
	    
	private void buildGUI_saveFile()
	{
		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout(5,5));
		setModal(true);
	    
	    btn_OK = new JButton("OK");
	    btn_OK.addActionListener(this);
	    btn_cancel = new JButton("Cancel");
	    btn_cancel.addActionListener(this);
	    Box btnPane=Box.createHorizontalBox();
	    btnPane.add(btn_cancel);
	    btnPane.add(Box.createHorizontalStrut(5));
	    btnPane.add(btn_OK);
	    
	    btn_filter_save = new JButton("Filter by objects: data to save...");
	    btn_filter_save.addActionListener(this);

		JPanel subPanel= new JPanel();
		Border titleBorder = BorderFactory.createTitledBorder("Configuration:");
		//subPanel.setLayout(new BorderLayout(5,5));
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		//subPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		subPanel.setBorder(titleBorder);
		subPanel.add(btn_filter_save);

	    JPanel mainPanel=new JPanel();
	    mainPanel.setLayout(new BorderLayout(5,5));
	    mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	    mainPanel.add(subPanel,BorderLayout.CENTER);
	    

	    	JLabel destPath_Lbl=new JLabel("Destination");
		txt_path =new JTextField(50);
		txt_path.setEditable(false);
		txt_path.setToolTipText("Destination to store json template file");
	    	if(tempFile!=null)
			txt_path.setText(tempFile.getAbsolutePath());
		btn_browse_save =new JButton("Browse");
		btn_browse_save.addActionListener(this);
	    	JPanel destP=new JPanel();
	    	destP.add(destPath_Lbl);
		destP.add(txt_path);
		destP.add(btn_browse_save);

	    	mainPanel.add(destP,BorderLayout.SOUTH);

	    getContentPane().add(mainPanel,BorderLayout.CENTER);
	    getContentPane().add(btnPane,BorderLayout.SOUTH);
	    
	}
	
	




	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_OK){
			setVisible(false);
			dispose();
		}else if(e.getSource()==btn_filter_load){
			TypeFilter_GUI filterGUI = new TypeFilter_GUI(new JFrame(),tempFile.getAbsolutePath());
			moduleList=filterGUI.getTypeFilter();
		}else if(e.getSource()==btn_filter_save){
			TypeFilter_GUI filterGui = new TypeFilter_GUI(new JFrame(),tree);
			moduleList=filterGui.getTypeFilter();
		}else if(e.getSource()== btn_cancel) {
			cancel =true;
			moduleList=null;
			setVisible(false);
			dispose();
		}else if(e.getSource()== btn_browse_save) {
			FileFilter filter = new FileNameExtensionFilter("XML file", "xml");
			JFileChooser fcSave =new JFileChooser();
			fcSave.addChoosableFileFilter(filter);
			fcSave.setFileFilter(filter);
			if(tempFile!=null)
				fcSave.setCurrentDirectory(new File(tempFile.getParent()));
        	int returnValSave=fcSave.showSaveDialog(this);
        	if(returnValSave==JFileChooser.APPROVE_OPTION) {
        		tempFile=fcSave.getSelectedFile();
				if(!fcSave.getSelectedFile().getAbsolutePath().endsWith(suffix)){
					tempFile = new File(fcSave.getSelectedFile() + suffix);
        	}
        		txt_path.setText(tempFile.getAbsolutePath());
        	}
		}else if(e.getSource()== btn_browse_load) {
			FileFilter filter = new FileNameExtensionFilter("XML file", "xml");
			JFileChooser fcOpen =new JFileChooser();
			fcOpen.addChoosableFileFilter(filter);
			fcOpen.setFileFilter(filter);
			if(tempFile!=null)
				fcOpen.setCurrentDirectory(new File(tempFile.getParent()));
        	int returnValOpen=fcOpen.showOpenDialog(this);
        	if(returnValOpen == JFileChooser.APPROVE_OPTION) {
        		tempFile = fcOpen.getSelectedFile();
        		txt_path.setText(tempFile.getAbsolutePath());
        	}
		}
		
	}
	
	public File getDestination()
	{
		return tempFile;
	}
	
	
	public List<String> getSelectionSave()
	{
		if(moduleList==null) {
			//get list from chosen setup
			HashMap<String,ModuleContent> list= ModuleController.getInstance().getAvailableContent();
			if(list!=null)
				moduleList = new ArrayList<>(list.keySet());

			}
		return moduleList;
		}

	/**
	 *
	 * @param availableTypelist list of types for that tagdata definition is available in file
	 * @return list of objectTypes that are part of chosen setup and tagdata definition is available
	 */
	public List<String> getSelectionLoad(List<String> availableTypelist)
	{
		if(moduleList==null) {
			//get list from chosen setup
			HashMap<String,ModuleContent> list= ModuleController.getInstance().getAvailableContent();
			List<String> objectTypes=null;
			if(list!=null)
				objectTypes = new ArrayList<>(list.keySet());

			if(objectTypes!=null && availableTypelist!=null && !availableTypelist.isEmpty()){
				moduleList=new ArrayList<>();
				// merge lists: keep all available contents that are def in file
				for(String s:objectTypes){
					if(availableTypelist.contains(s)){
						moduleList.add(s);
					}
				}
			}
		}
		return moduleList;
	}

    public Boolean loadTreeStructure() {
		return cb_loadTreeStructure.isSelected();
}
    public Boolean isCancelled(){return cancel;}

}

