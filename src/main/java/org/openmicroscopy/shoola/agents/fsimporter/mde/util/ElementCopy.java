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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import omero.model.Experiment;
import omero.model.ExperimentI;
import omero.model.Experimenter;
import omero.model.ExperimenterI;

/** 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ElementCopy 
{
	/**
	 * Copy omero.model.Experiment object
	 * @param e
	 * @return copy
	 */
	public static Experiment copy(Experiment e){
		Experiment c=new ExperimentI();
		c.setId(e.getId());
		c.setType(e.getType());
		c.setDescription(e.getDescription());
//		c.setDetails(e.getDetails());
		c.addAllMicrobeamManipulationSet(e.copyMicrobeamManipulation());
		
		return e;
		
	}

	public static Experimenter copy(Experimenter e) {
		Experimenter c=new ExperimenterI();
		c.setId(e.getId());
		c.setFirstName(e.getFirstName());
		c.setMiddleName(e.getMiddleName());
		c.setLastName(e.getLastName());
		return c;
	}
}
