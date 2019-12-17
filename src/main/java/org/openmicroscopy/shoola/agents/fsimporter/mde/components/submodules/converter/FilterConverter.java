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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;
import ome.units.unit.Unit;
import ome.units.quantity.Length;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.ui.IconManager;

/**
 * Convert OME-XML Filter object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class FilterConverter extends DataConverter{

	public FilterConverter(){}
	
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
	
	
	public LinkedHashMap<String, TagData> convertData(Filter f)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(f!=null) {
			try{tagMap.put(TagNames.ID,convertID(f.getID(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ID,convertID(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MODEL,convertModel(f.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(f.getManufacturer(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			}
			try{tagMap.put(TagNames.LP_TYPE,convertType(f.getType(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.LP_TYPE,convertType(null, REQUIRED));
			}
			try{tagMap.put(TagNames.FILTERWHEEL,convertFilterwheel(f.getFilterWheel(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.FILTERWHEEL,convertFilterwheel(null,  REQUIRED));
			}
			try{tagMap.put(TagNames.TRANSRANGE_IN,convertTransRange_In(f.getTransmittanceRange().getCutIn(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.TRANSRANGE_IN,convertTransRange_In(null, REQUIRED));
			}
			try{tagMap.put(TagNames.TRANSRANGE_OUT,convertTransRange_Out(f.getTransmittanceRange().getCutOut(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.TRANSRANGE_OUT,convertTransRange_Out(null, REQUIRED));
			}
		}else {
			tagMap.put(TagNames.ID,convertID(null,REQUIRED));
			tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			tagMap.put(TagNames.LP_TYPE,convertType(null, REQUIRED));
			tagMap.put(TagNames.FILTERWHEEL,convertFilterwheel(null,  REQUIRED));
			//transmitted range
			tagMap.put(TagNames.TRANSRANGE_IN,convertTransRange_In(null, REQUIRED));
			tagMap.put(TagNames.TRANSRANGE_OUT,convertTransRange_Out(null,  REQUIRED));
			
		}
		return tagMap;
	}
	
	

	public LinkedHashMap<String, TagData> convertData(Dichroic d)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(d!=null) {
			try{tagMap.put(TagNames.MODEL,convertModel(d.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(d.getManufacturer(),  REQUIRED));
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
	public TagData convertID(String value,boolean prop) {
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.ID,value,prop,TagData.TEXTFIELD);
	}
	private TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertType(FilterType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.LP_TYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(FilterType.class));
	}
	private TagData convertFilterwheel(String value,boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.FILTERWHEEL,value,prop,TagData.TEXTFIELD);
	}
	
	private TagData convertTransRange_In(Length value, boolean prop) {
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val= (value != null)? String.valueOf(value.value()) : "";
//		Unit unit=(value!=null) ? value.unit() :TagNames.TRANSRANGE_UNIT;
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.TRANSRANGE_IN,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
	}
	
	private TagData convertTransRange_Out(Length value, boolean prop) {
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val= (value != null)? String.valueOf(value.value()) : "";
//		Unit unit=(value!=null) ? value.unit() :TagNames.TRANSRANGE_UNIT;
		return new TagData(TagNames.OME_ELEM_FILTER,TagNames.TRANSRANGE_OUT,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
	}
	
}
