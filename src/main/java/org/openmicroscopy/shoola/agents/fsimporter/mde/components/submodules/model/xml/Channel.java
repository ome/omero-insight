/*
 * Copyright (C) <2017-2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml;

import ome.units.quantity.Time;
import ome.xml.model.OMEModel;
import ome.xml.model.enums.EnumerationException;
import org.w3c.dom.Element;

/**
 * Extends OME-XML Channel object. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class Channel extends ome.xml.model.Channel{
	
	private String illuminationTypeString;
	private String imagingMode;
	private Time defaultExposureTime;
	
	
	public Time getDefaultExposureTime() {
		return defaultExposureTime;
	}

	public void setDefaultExposureTime(Time defaultExposureTime) {
		this.defaultExposureTime = defaultExposureTime;
	}

	// -- Constructors --
	/** Default constructor. */
	public Channel()
	{
	}

	/**
	 * Constructs Channel recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Channel(Element element, OMEModel model)
			throws EnumerationException
	{
		update(element, model);
	}
	/** Copy constructor. */
	public Channel(Channel orig)
	{
		super(orig);
		this.illuminationTypeString=orig.illuminationTypeString;
		this.imagingMode=orig.imagingMode;
		this.defaultExposureTime=orig.defaultExposureTime;
	}
	
	public Channel(ome.xml.model.Channel c){
		super(c);
		this.illuminationTypeString=null;
		this.imagingMode=null;
		this.defaultExposureTime=null;
	}
	
	public void setAcquisitionMode(String imagingMode)
	{
		this.imagingMode=imagingMode;
	}
	public String getAcquisitionModeAsString()
	{
		if(this.imagingMode==null && getAcquisitionMode()!=null)
			this.imagingMode=getAcquisitionMode().getValue();
		
		return imagingMode;
	}

	public void setIlluminationType(String illuminationType)
	{
		this.illuminationTypeString = illuminationType;
	}
	
	public String getIlluminationTypeAsString()
	{
		if(this.illuminationTypeString==null && getIlluminationType()!=null)
			this.illuminationTypeString=getIlluminationType().getValue();
		return this.illuminationTypeString;
	}
	
	

}
