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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.util.LinkedHashMap;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

import ome.xml.model.FilterSet;

/**
 * Convert OME-XML FilterSet object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class FilterSetConverter extends DataConverter{

	public FilterSetConverter() {}
	
	public LinkedHashMap<String, TagData> convertData(FilterSet f)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(f!=null) {
			try{tagMap.put(TagNames.MODEL,convertModel(f.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(f.getManufacturer(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			}
		}else {
			tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
		}
		
		return tagMap;
	}
	
	
	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/

	private TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LIGHTPATH_FS,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LIGHTPATH_FS,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}
}
