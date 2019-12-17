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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.ui.IconManager;

/**
 * Convert OME-XML Dichroic object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class DichroicConverter extends DataConverter {
	@Override
	public JComponent getLabel(String name, int index, List<ActionListener> listeners) {
		JPanel labelPane=new JPanel();
		labelPane.setLayout(new BoxLayout(labelPane,BoxLayout.X_AXIS));
		labelPane.setBorder(new EmptyBorder(5, 2, 2, 2));
		labelPane.setOpaque(false);

		JLabel label=new JLabel(name);
		label.setBorder(new EmptyBorder(0, 0, 0, 10));
		labelPane.add(label);
		
		IconManager icons = IconManager.getInstance();
		JButton applyBtn = new JButton(icons.getIcon(IconManager.ADD_12));
		applyBtn.setToolTipText("Apply table selection");
		applyBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		labelPane.add(applyBtn);

		return labelPane;
	}
	
	
	
	
	public LinkedHashMap<String, TagData> convertData(Dichroic d)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(d!=null) {
			try{tagMap.put(TagNames.ID,convertID(d.getID(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ID,convertID(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MODEL,convertModel(d.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(d.getManufacturer(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			}
		}else {
			tagMap.put(TagNames.ID,convertID(null,REQUIRED));
			tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
		}
		return tagMap;
	}
	
	
	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	public TagData convertID(String value,boolean prop) {
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.ID,value,prop,TagData.TEXTFIELD);
	}
	private TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertType(FilterType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.LP_TYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(FilterType.class));
	}
	private TagData convertFilterwheel(String value,boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.FILTERWHEEL,value,prop,TagData.TEXTFIELD);
	}
}
