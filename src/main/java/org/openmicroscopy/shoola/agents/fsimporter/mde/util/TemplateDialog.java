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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;

/**
 * Save input objects as template
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class TemplateDialog extends JDialog implements ActionListener{

	private static String title="Select Modules To Save As Template";
	
	private JPanel chBoxPanel;
	private JButton  buttonOK;
	private JButton  buttonCancel;
	private JButton destPath_btn;
	private JTextField destPath_txt;
	private Boolean[] moduleList;
	private File tempFile;
	
	private JCheckBox imageCB;
	private JCheckBox objectiveCB;
	private JCheckBox detectorCB;
	private JCheckBox lightSourceCB;
	private JCheckBox channelCB;
	private JCheckBox lightPathCB;
	private JCheckBox sampleCB;
	private JCheckBox experimentCB;

	private JButton srcPath_btn;
	
	public static int IMAGE_INDEX=0;
	public static int OBJECTIVE_INDEX=1;
	public static int DETECTOR_INDEX=2;
	public static int LIGHTSRC_INDEX=3;
	public static int CHANNEL_INDEX=4;
	public static int LIGHTPATH_INDEX=5;
	public static int SAMPLE_INDEX=6;
	public static int EXPERIMENT_INDEX=7;
	
	public TemplateDialog(JFrame parent,File tempFile,boolean load)
	{
		super(parent,title);
		this.tempFile=tempFile;
		moduleList=new Boolean[8];
		for(int i=0; i<moduleList.length; i++) {
			moduleList[i]=Boolean.TRUE;
		}
		buildGUI(load);
		
		pack();
		setVisible(true);
	}
	
	private void buildGUI(boolean load)
	{
		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout(5,5));
		setModal(true);
		
		chBoxPanel=new JPanel(new GridLayout(0, 1));
	    Border border = BorderFactory.createTitledBorder("Modules");
	    chBoxPanel.setBorder(border);
	    
	    ModuleController controller = ModuleController.getInstance();
	    HashMap<String,ModuleContent> list=controller.getAvailableContent();
	    for(Map.Entry<String, ModuleContent> entry:list.entrySet()) {
	    	JCheckBox ch=new JCheckBox(entry.getKey());
	    	ch.setSelected(true);
	    	ch.addActionListener(this);
	    	chBoxPanel.add(ch);
	    }
//	    imageCB = new JCheckBox("Image");
//	    imageCB.setSelected(true);
//	    imageCB.addActionListener(this);
//	    chBoxPanel.add(imageCB);
//	    
//	    objectiveCB = new JCheckBox("Objective");
//	    objectiveCB.setSelected(true);
//	    objectiveCB.addActionListener(this);
//	    chBoxPanel.add(objectiveCB);
//	    
//	    detectorCB = new JCheckBox("Detector");
//	    detectorCB.setSelected(true);
//	    detectorCB.addActionListener(this);
//	    chBoxPanel.add(detectorCB);
//	    
//	    lightSourceCB = new JCheckBox("LightSource");
//	    lightSourceCB.setSelected(true);
//	    lightSourceCB.addActionListener(this);
//	    chBoxPanel.add(lightSourceCB);
//	    
//	    channelCB = new JCheckBox("Channel");
//	    channelCB.setSelected(true);
//	    channelCB.addActionListener(this);
//	    chBoxPanel.add(channelCB);
//	    
//	    lightPathCB = new JCheckBox("LightPath");
//	    lightPathCB.setSelected(true);
//	    lightPathCB.addActionListener(this);
//	    chBoxPanel.add(lightPathCB);
//	    
//	    sampleCB = new JCheckBox("Sample");
//	    sampleCB.setSelected(true);
//	    sampleCB.addActionListener(this);
//	    chBoxPanel.add(sampleCB);
//	    
//	    experimentCB = new JCheckBox("Experiment");
//	    experimentCB.setSelected(true);
//	    experimentCB.addActionListener(this);
//	    chBoxPanel.add(experimentCB);
	    
	    buttonOK = new JButton("OK");
	    buttonOK.addActionListener(this);
	    buttonCancel = new JButton("Cancel");
	    buttonCancel.addActionListener(this);
	    Box btnPane=Box.createHorizontalBox();
	    btnPane.add(buttonCancel);
	    btnPane.add(Box.createHorizontalStrut(5));
	    btnPane.add(buttonOK);
	    
	    JPanel mainPanel=new JPanel();
	    mainPanel.setLayout(new BorderLayout(5,5));
	    mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	    mainPanel.add(chBoxPanel,BorderLayout.CENTER);
	    
	    if(!load) {
	    	JLabel destPath_Lbl=new JLabel("Destination");
	    	destPath_txt=new JTextField(50);
	    	destPath_txt.setEditable(true);
	    	destPath_txt.setToolTipText("Destination to store json template file");
	    	if(tempFile!=null)
	    		destPath_txt.setText(tempFile.getAbsolutePath());
	    	destPath_btn=new JButton("Browse");
	    	destPath_btn.addActionListener(this);
	    	JPanel destP=new JPanel();
	    	destP.add(destPath_Lbl);
	    	destP.add(destPath_txt);
	    	destP.add(destPath_btn);

	    	mainPanel.add(destP,BorderLayout.SOUTH);
	    }else {
	    	JLabel srcPath_Lbl=new JLabel("Source");
	    	destPath_txt=new JTextField(50);
	    	destPath_txt.setEditable(true);
	    	destPath_txt.setToolTipText("Source template file");
	    	if(tempFile!=null)
	    		destPath_txt.setText(tempFile.getAbsolutePath());
	    	srcPath_btn=new JButton("Browse");
	    	srcPath_btn.addActionListener(this);
	    	JPanel destP=new JPanel();
	    	destP.add(srcPath_Lbl);
	    	destP.add(destPath_txt);
	    	destP.add(srcPath_btn);

	    	mainPanel.add(destP,BorderLayout.SOUTH);
	    }
	    
	    getContentPane().add(mainPanel,BorderLayout.CENTER);
	    getContentPane().add(btnPane,BorderLayout.SOUTH);
	    
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonOK){
			setVisible(false);
			dispose();
		}else if(e.getSource()==buttonCancel) {
			moduleList=null;
			setVisible(false);
			dispose();
		}else if(e.getSource()==imageCB) {
			moduleList[IMAGE_INDEX]=imageCB.isSelected();
		}else if(e.getSource()==objectiveCB) {
			moduleList[OBJECTIVE_INDEX]=objectiveCB.isSelected();
		}else if(e.getSource()==detectorCB) {
			moduleList[DETECTOR_INDEX]=detectorCB.isSelected();
		}else if(e.getSource()==lightSourceCB) {
			moduleList[LIGHTSRC_INDEX]=lightSourceCB.isSelected();
		}else if(e.getSource()==channelCB) {
			moduleList[CHANNEL_INDEX]=channelCB.isSelected();
		}else if(e.getSource()==lightPathCB) {
			moduleList[LIGHTPATH_INDEX]=lightPathCB.isSelected();
		}else if(e.getSource()==sampleCB) {
			moduleList[SAMPLE_INDEX]=sampleCB.isSelected();
		}else if(e.getSource()==experimentCB) {
			moduleList[EXPERIMENT_INDEX]=experimentCB.isSelected();
		}else if(e.getSource()==destPath_btn) {
			JFileChooser fcSave =new JFileChooser();
			if(tempFile!=null)
				fcSave.setCurrentDirectory(new File(tempFile.getParent()));
        	int returnValSave=fcSave.showSaveDialog(this);
        	if(returnValSave==JFileChooser.APPROVE_OPTION) {
        		tempFile=fcSave.getSelectedFile();
        		destPath_txt.setText(tempFile.getAbsolutePath());
        	}
		}else if(e.getSource()==srcPath_btn) {
			JFileChooser fcOpen =new JFileChooser();
			if(tempFile!=null)
				fcOpen.setCurrentDirectory(new File(tempFile.getParent()));
        	int returnValOpen=fcOpen.showOpenDialog(this);
        	if(returnValOpen == JFileChooser.APPROVE_OPTION) {
        		tempFile = fcOpen.getSelectedFile();
        		destPath_txt.setText(tempFile.getAbsolutePath());
        	}
		}
		
	}
	
	public File getDestination()
	{
		return tempFile;
	}
	
	
	public Boolean[] getSelection()
	{
		if(moduleList!=null) {
			ImporterAgent.getRegistry().getLogger().debug(this, "Size module selection: "+moduleList.length);
			for(int i=0; i<moduleList.length; i++) {
				ImporterAgent.getRegistry().getLogger().debug(this, "module "+i+" : "+moduleList[i]);
			}
		}
		return moduleList;
	}

}

