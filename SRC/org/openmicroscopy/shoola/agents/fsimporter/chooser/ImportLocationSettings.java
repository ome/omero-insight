/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2012 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.fsimporter.chooser;

import pojos.GroupData;

/** 
 * 
 *
 * @author Scott Littlewood, <a href="mailto:sylittlewood@dundee.ac.uk">sylittlewood@dundee.ac.uk</a>
 * @since Beta4.4
 */
public abstract class ImportLocationSettings {

	/** Defines the group data to be imported in to */
	private GroupData importGroup;
	
	/** Defines the type of data being imported */
	private int importDataType;
	
	protected ImportLocationSettings(int type, GroupData group)
	{
		this.importDataType = type;
		this.importGroup = group;
	}
	
	/**
	 * @return The group to import data in to.
	 */
	public GroupData getImportGroup()
	{
		return importGroup;
	}
	
	/**
	 * @return The type of data being imported, Project / Screen.
	 */
	public int getImportDataType()
	{
		return importDataType;
	}

}

