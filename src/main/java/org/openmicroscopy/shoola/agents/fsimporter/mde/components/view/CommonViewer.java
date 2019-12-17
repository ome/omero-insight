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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;


import javax.swing.plaf.FontUIResource;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

import org.openmicroscopy.shoola.util.ui.ScrollablePanel;



/**
 * Viewer for mdecontent with:
 * title,tags
 * 
 * @author Susanne Kunis <susannekunis at gmail dot com>
 * 
 *
 */
public class CommonViewer extends JPanel{
	
	/** list of key-values */
	private ModuleContent content;
	private boolean inputEvent;

	/** holds key-values*/
	private JPanel tagPane;
	private boolean labelVisible;

	public CommonViewer() 
	{
		this(null,true);
	}
	
	public CommonViewer(ModuleContent c) {
		this(c,true);
	}
	
	/**
	 * visualize given tagList -> direkt link to model
	 * @param title
	 * @param list
	 * @param conf
	 * @param labelVisible TODO
	 */
	public CommonViewer(ModuleContent c,boolean labelVisible)
	{
		this.content=c;
		this.inputEvent=false;
		this.labelVisible=labelVisible;
		showModule();
		revalidate();
		repaint();
	}
	
	
	private void showModule()
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		if(content!=null && content.getTagList()!=null) {
			layoutComponents();
			buildGUI();
		}
//		else {
//			add(new JLabel("Container"));
//		}
	}
	
	public LinkedHashMap<String, TagData> getTagList()
	{
		if(content!=null)
			return content.getList();
		return null;
	}
	
	
	
	private void layoutComponents() {
		tagPane=new JPanel();
		tagPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		add(tagPane,BorderLayout.CENTER);
	}
	
	
//	/**
//	 * Init given tags and mark it as visible.(Predefinition of gui and values). 
//	 * @param list
//	 */
//	private void initTags(List<TagConfiguration> list) 
//	{
//		if(list==null || content ==null)
//			return;
//		LinkedHashMap<String, TagData> tagList=content.getList();
//		for(int i=0; i<list.size();i++){
//			TagConfiguration t=list.get(i);
//			if(t.getName()!=null){
//				if(tagList.containsKey(t.getName()))
//					tagList.get(t.getName()).setVisible(t.isVisible());
//			}
//		}
//	}
	
	
	/**
	 * Builds and lay out tags.
	 */
	private void buildGUI()
	{
		List<JComponent> labels= new ArrayList<JComponent>();
		List<JComponent> comp=new ArrayList<JComponent>();
		
		for(TagData t:content.getTagList()) {
			addTagToGUI(t,labels,comp);
		}
		
		if(labelVisible)
			addLabelTextRows(labels, comp, tagPane);
		else
			addLabelTextRows(null, comp, tagPane);
	}
	
	
	
	private void addTagToGUI(TagData tag,List<JComponent> labels,List<JComponent> comp)
	{
		if(tag != null && tag.isVisible()){
			labels.add(tag.getTagLabelAndUnit());
			comp.add(tag.getInputField());
		}
	}

	
	public static void addLabelTextRows(List<JComponent> labels,List<JComponent> fields,Container container) {
		
		GridBagLayout gridbag = new GridBagLayout();
		container.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		
		if(labels!=null && fields!=null) {
			int numLabels = labels.size();
			c.insets = new Insets( 0, 1, 1, 0);
			for (int i = 0; i < numLabels; i++) {
				c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
				c.fill = GridBagConstraints.HORIZONTAL;      
				c.weightx = 0.0;                       
				container.add(labels.get(i), c);

				c.gridwidth = GridBagConstraints.REMAINDER;     //end row
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;
				container.add(fields.get(i), c);
			}
		}else if(fields!=null){
			int numElem = fields.size();
			c.insets = new Insets( 0, 0, 1, 0);
			for (int i = 0; i < numElem; i++) {
				c.gridwidth = 1;//GridBagConstraints.REMAINDER; //next-to-last
				c.fill = GridBagConstraints.HORIZONTAL;      //reset to default
				c.weightx = 1.0;                       //reset to default
				c.gridx=0;
				c.gridy=i;
				container.add(fields.get(i), c);
			}
		}else if(labels!=null) {
			int numElem = labels.size();
			c.insets = new Insets( 0, 0, 1, 0);
			for (int i = 0; i < numElem; i++) {
				c.gridwidth = GridBagConstraints.REMAINDER; //next-to-last
				c.fill = GridBagConstraints.HORIZONTAL;      //reset to default
				c.weightx = 1.0;                       //reset to default
				container.add(labels.get(i), c);
			}
		}
	}
	
	
	/**
	 * 
	 * @return list of tagdata with tagData.valueHasChanged()==true
	 */
	public LinkedHashMap<String, TagData> getChangedTags()
	{
		if(content==null)
			return null;
		LinkedHashMap<String, TagData> list = new LinkedHashMap<>();
		for(TagData t:content.getTagList()) {
			if(t.valueHasChanged())
				list.put(t.getTagName(), t);
		}
		
		return list;
	}
	/*
	 * Reset input event, set all data as saved
	 * Attention: wrong input( at saveData() use catch case) will not be save
	 */
	public void afterSavingData() {
//		resetInputEvent();
		if(content==null){
			return;
		}
		for(TagData t: content.getTagList()){
			if(t!=null) t.dataSaved(true);
		}
	}


}
