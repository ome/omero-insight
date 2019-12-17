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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.ui.IconManager;

import ome.xml.model.Laser;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.enums.Enumeration;
import ome.xml.model.enums.EnumerationException;

/**
 * Convert OME-XML Laser object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class LaserConverter extends DataConverter{

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

	public LinkedHashMap<String, TagData> convertData(Laser l,LightSourceSettings settings)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(l!=null) {
			try{tagMap.put(TagNames.ID,convertID(l.getID(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ID,convertID(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(l.getManufacturer(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MANUFAC,convertManufact(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MODEL,convertModel(l.getModel(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.POWER,convertPower(l.getPower(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.POWER,convertPower(null,REQUIRED));
			}
			try{ tagMap.put(TagNames.L_TYPE,convertType(l.getType(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.L_TYPE,convertType(null,REQUIRED));
			}
			try{ tagMap.put(TagNames.MEDIUM,convertMedium(l.getLaserMedium(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MEDIUM,convertMedium(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.FREQMUL,convertFreqMultiplication(l.getFrequencyMultiplication(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.FREQMUL,convertFreqMultiplication(null,REQUIRED));
			}
			try{ tagMap.put(TagNames.TUNABLE,convertTunable(l.getTuneable(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.TUNABLE,convertTunable(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.PULSE,convertPulse(l.getPulse(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.PULSE,convertPulse(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.POCKELCELL,convertPocketCell(l.getPockelCell(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.POCKELCELL,convertPocketCell(null, REQUIRED));
			}
			try{tagMap.put(TagNames.REPRATE,convertRepititationRate(l.getRepetitionRate(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.REPRATE,convertRepititationRate(null,REQUIRED));
			}
			try{tagMap.put(TagNames.PUMP,convertPump(l.getLinkedPump().getID(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.PUMP,convertPump(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.WAVELENGTH,convertWavelength(l.getWavelength(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.WAVELENGTH,convertWavelength(null, REQUIRED));
			}	
		}else {
			tagMap.put(TagNames.ID,convertID(null,REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null, REQUIRED));
			tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			tagMap.put(TagNames.POWER,convertPower(null,REQUIRED));
			tagMap.put(TagNames.L_TYPE,convertType(null, REQUIRED));
			tagMap.put(TagNames.MEDIUM,convertMedium(null, REQUIRED));
			tagMap.put(TagNames.FREQMUL,convertFreqMultiplication(null,REQUIRED));
			tagMap.put(TagNames.TUNABLE,convertTunable(null, REQUIRED));
			tagMap.put(TagNames.PULSE,convertPulse(null, REQUIRED));
			tagMap.put(TagNames.POCKELCELL,convertPocketCell(null, REQUIRED));
			tagMap.put(TagNames.REPRATE,convertRepititationRate(null,REQUIRED));
			tagMap.put(TagNames.PUMP,convertPump(null, REQUIRED));
			tagMap.put(TagNames.WAVELENGTH,convertWavelength(null, REQUIRED));
		}
		//settings:
		if(settings!=null) {
			try{tagMap.put(TagNames.SET_WAVELENGTH,convertWavelengthSett(settings.getWavelength(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.SET_WAVELENGTH,convertWavelengthSett(null, REQUIRED));
			}
			try{tagMap.put(TagNames.ATTENUATION,convertAttenuation(settings.getAttenuation(), REQUIRED));
			}catch (NullPointerException e){
				tagMap.put(TagNames.ATTENUATION,convertAttenuation(null, REQUIRED));
			}
		}else {
			tagMap.put(TagNames.SET_WAVELENGTH,convertWavelengthSett(null, REQUIRED));
			tagMap.put(TagNames.ATTENUATION,convertAttenuation(null, REQUIRED));
		}
		return tagMap;
	}
	
	
	public TagData convertID(String value,boolean prop) {
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.ID,value,prop,TagData.TEXTFIELD);
	}

	public TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}
	public TagData convertType(Enumeration value, boolean prop)
	{
		String val= (value != null)? ((LaserType) value).getValue() : "";
			return new TagData(TagNames.OME_ELEM_LASER,TagNames.L_TYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(LaserType.class));
	}

	public TagData convertPower(Power value, boolean prop)
	{
		ome.model.units.Power val=null;
		if(value!=null)
			val=new ome.model.units.Power(value);
//		String val= (value != null)? String.valueOf(value.value()) : "";
//		Unit unit=(value!=null) ? value.unit() :TagNames.POWER_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_LASER,TagNames.POWER,val,ome.model.units.Power.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	public TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}




	public TagData convertMedium(LaserMedium value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.MEDIUM,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(LaserMedium.class));
	}
	public TagData convertFreqMultiplication(PositiveInteger value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.getNumberValue()) : "";
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.FREQMUL,val,prop,TagData.TEXTFIELD);
	}


	public TagData convertTunable(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "";
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.TUNABLE,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
	}


	public TagData convertPulse(Pulse value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.PULSE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(Pulse.class));
	}

	public TagData convertPocketCell(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "";
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.POCKELCELL,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
	}
	public TagData convertRepititationRate(Frequency value, boolean prop)
	{
		ome.model.units.Frequency val=null;
		if(value!=null)
			val=new ome.model.units.Frequency(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null) ? value.unit():TagNames.REPRATE_UNIT_HZ;
		TagData t = new TagData(TagNames.OME_ELEM_LASER,TagNames.REPRATE,val,ome.model.units.Frequency.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;

	}

	public TagData convertPump(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LASER,TagNames.PUMP,value,prop,TagData.TEXTFIELD);
	}
	public TagData convertWavelength(Length value, boolean prop)
	{
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null) ? value.unit() :TagNames.WAVELENGTH_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_LASER,TagNames.WAVELENGTH,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	
	/*------------------------------------------------------
	 * Set methods settings Values
	 * -----------------------------------------------------*/
	public TagData convertWavelengthSett(Length value, boolean prop)
	{
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null) ? value.unit():TagNames.WAVELENGTH_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_LASER,TagNames.SET_WAVELENGTH,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPosFloat(t,"Invalid input. Use float>0!"));
		return t;

	}
	public TagData convertAttenuation(PercentFraction value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.getNumberValue()):"";
		TagData t = new TagData(TagNames.OME_ELEM_LASER,TagNames.ATTENUATION,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPercentFraction(t,"Invalid input. Use percent fraction as dezimal between 0.0 and 1.0!"));
		return t;
	}

	
}
