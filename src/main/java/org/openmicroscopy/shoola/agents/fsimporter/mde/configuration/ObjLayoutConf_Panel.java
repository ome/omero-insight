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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.CommonViewer;

/**
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ObjLayoutConf_Panel extends JPanel{
	
	private CommonViewer preview;
	private Editor_TagDataProp editor;
	private JPanel btnPanel;
	private ModuleContent content;
	private boolean contentHasChange;
	private JFrame parent;
	
	public ObjLayoutConf_Panel(ModuleContent content,JFrame parent) {
		super(new BorderLayout());
		this.content=content;
		this.contentHasChange=false;
		this.parent=parent;
		
//		content.print();
		
		editor=new Editor_TagDataProp(content);
		add(editor,BorderLayout.CENTER);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black), "Layout Configuration"));
		
		
		JButton btn_preview=new JButton("Preview");
		btn_preview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.saveContent();
				
				JDialog prev=new JDialog(getWindowParent(),"Preview Layout Object");
				prev.getContentPane().setLayout(new BorderLayout());
				prev.setModal(true);
				prev.add(new JLabel(editor.getContent().getType()),BorderLayout.NORTH);
				prev.add(new CommonViewer(editor.getContent()),BorderLayout.CENTER);
				prev.pack();
				prev.setVisible(true);
				
			}
		});
		JButton btn_apply=new JButton("Apply");
		btn_apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentHasChange=true;
				saveContent();
			}
		});

		JButton btn_reset=new JButton("Reset");
		btn_reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentHasChange=false;
				editor.resetContent();
			}
		});

		JPanel btnPane=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPane.add(btn_preview);
		btnPane.add(btn_apply);
		btnPane.add(btn_reset);
		
		add(btnPane,BorderLayout.SOUTH);
		
		revalidate();
		repaint();
	}
	
	private JFrame getWindowParent()
	{
		return parent;
	}
	
	private void saveContent() {
		editor.saveContent();
		this.content=editor.getContent();
	}
	public String getType() {
		return content.getType();
	}
	public ModuleConfiguration getConfiguration() {
		return content.getProperties();
	}
	
}