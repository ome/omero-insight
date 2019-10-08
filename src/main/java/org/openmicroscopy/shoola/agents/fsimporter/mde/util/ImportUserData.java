/*
 * Copyright (C) <2016> University of Dundee & Open Microscopy Environment.
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


import ome.xml.model.Experimenter;
import omero.gateway.model.DataObject;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.GroupData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;
import omero.model.Project;

/**
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ImportUserData 
{
	private GroupData group;
	/** name of project or screen */
	private String name;
	private ExperimenterData experimenter;
	
	public ImportUserData(GroupData group,DataObject parent,ExperimenterData experimenter)
	{
		this.group=group;
		if(parent instanceof ProjectData)
			this.name=parent.asProject().getName().getValue();
		else if(parent instanceof ScreenData)
			this.name=parent.asScreen().getName().getValue();
		this.experimenter=experimenter;
		
	}
	
	public String getGroup()
	{
		return group.getName();
	}
	
	public String getProject()
	{
		return name;
		
	}
	

	public Experimenter getUser()
	{
		Experimenter e= new Experimenter();
		e.setFirstName(experimenter.getFirstName());
		e.setLastName(experimenter.getLastName());
		return e;
	}
	public String getUserName()
	{
		return experimenter.getLastName();
	}
	
	public String[] getUserFullName()
	{
		String[] name={experimenter.getFirstName(),experimenter.getLastName()};
		return name;
	}
}
