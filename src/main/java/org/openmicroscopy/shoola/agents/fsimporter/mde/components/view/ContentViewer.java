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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.jdesktop.swingx.JXTaskPane;

/**
 * Viewer for an object with ObjectTable, CommonViewer. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ContentViewer extends JXTaskPane{
	private String title;
	private ModuleContent content;
	private ObjectTable availableElems;
	private CommonViewer tagPane;
	private String tagLayout;
	private JPanel mainPanel;
//	private ModuleTable table;
	
	private final int LABEL_W=170;
	
	public ContentViewer(String title,ObjectTable avElems, ModuleContent content) {
		this.title=title;
		this.content=content;
		this.availableElems=avElems;

		layoutComponents();
		
		Font font = getFont();
		setFont(font.deriveFont(font.getStyle(), font.getSize() - 2));
		setCollapsed(false);
		setTitle(title);
		add(mainPanel);
	}

	private void layoutComponents() {
		mainPanel=new JPanel();
		mainPanel.setLayout(new BorderLayout(0,0));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		if(content!=null) {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			String tableLayout = BorderLayout.NORTH;
			tagLayout=BorderLayout.NORTH;

			
			if(availableElems!=null) {
				// get index of content
				int idx=availableElems.getElementIndex(content);
				JPanel p=availableElems.buildGUI(idx, -1, this);
				mainPanel.add(p,tableLayout);
				tagLayout=BorderLayout.CENTER;
			}
			tagPane=new CommonViewer(content);
			mainPanel.add(tagPane,tagLayout);
			mainPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		}
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	/**
	 * take over data from selected elem in available object table
	 * @param newC object values that should be the new value
	 * @param origC original object content
	 */
	public void replaceData(ModuleContent newC,ModuleContent origC)
	{
		content=MDEHelper.replaceUnchangedData(content, origC, newC);
		title=newC.getAttributeValue(TagNames.MODEL)!=null?newC.getAttributeValue(TagNames.MODEL):"";
		
		mainPanel.remove(tagPane);
		tagPane=new CommonViewer(content);
		mainPanel.add(tagPane,tagLayout);
		tagPane.revalidate();
		tagPane.repaint();
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
}
