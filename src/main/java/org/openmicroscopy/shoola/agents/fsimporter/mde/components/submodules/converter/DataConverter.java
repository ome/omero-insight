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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.DocumentListenerForDouble;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.DocumentListenerForPercentFraction;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * TODO: better as interface instead of abstract
 * Convert OME-XML object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public abstract class DataConverter {
	
	public static final boolean REQUIRED=true;
	public static final boolean OPTIONAL =false;
	
	protected LinkedHashMap<String,TagData> tagMap;
	
	public LinkedHashMap<String,TagData> getTagList()
	{
		return tagMap;
	}
	
	public JComponent getLabel(String name, int index, List<ActionListener> listeners) {
		return null;
	}
//	public abstract <T extends AbstractOMEModelObject> LinkedHashMap<String, TagData> convertData(T object);
	
	public DocumentListener createDocumentListenerDouble(TagData tag, String error) {
		return new DocumentListenerForDouble(tag,error, false); 
			
	}
	
	public DocumentListener createDocumentListenerPosFloat(TagData tag,String error){
		return new DocumentListenerForDouble(tag,error, true); 
	}
	
	/**
	 * @return
	 */
	public DocumentListener createDocumentListenerPercentFraction(TagData tag, String error) {
		return new DocumentListenerForPercentFraction(tag,error);
	}


	
}