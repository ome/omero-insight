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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;

/**
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ObjectConf_Panel extends JPanel{
	private boolean editable;
	private ModuleContent content;
	public ObjectConf_Panel(ModuleContent content) {
		super(new GridBagLayout());
		this.editable=true;
		this.content=content;
		if(content!=null) {
			if(content.getType().startsWith("OME")) {
				editable=false;
			}
			buildGUI();
		}
	}
	private void buildGUI() {
		GridBagConstraints c= new GridBagConstraints();
		
	}
}
