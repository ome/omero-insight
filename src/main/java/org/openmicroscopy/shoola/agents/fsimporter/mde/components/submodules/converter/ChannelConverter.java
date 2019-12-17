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

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PositiveFloat;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml.Channel;
import java.awt.event.ActionListener;

/**
 * Convert OME-XML Channel object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ChannelConverter extends DataConverter{

	public ChannelConverter (){}
	
	@Override
	public JComponent getLabel(String name, int index, List<ActionListener> listeners) {
		JPanel channelLabelPane=new JPanel();
		channelLabelPane.setLayout(new BoxLayout(channelLabelPane,BoxLayout.X_AXIS));
		channelLabelPane.setBorder(new EmptyBorder(5, 2, 2, 2));
		channelLabelPane.setOpaque(false);
		
		JLabel label=new JLabel(name);
		label.setBorder(new EmptyBorder(0, 0, 0, 10));
		channelLabelPane.add(label);
		
		return channelLabelPane;
	}
	
	

	public LinkedHashMap<String, TagData> convertData(Channel channel)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(channel!=null) {
			try{ tagMap.put(TagNames.CH_NAME,convertName(channel.getName(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.CH_NAME,convertName(null,REQUIRED));
			}
			try{ tagMap.put(TagNames.COLOR,convertColor(channel.getColor(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.COLOR,convertColor(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.FLUOROPHORE,convertFluorophore(channel.getFluor(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.FLUOROPHORE,convertFluorophore(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.ILLUMTYPE,convertIllumType(channel.getIlluminationTypeAsString(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ILLUMTYPE,convertIllumType(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.EXPOSURETIME,convertExposureTime(channel.getDefaultExposureTime(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.EXPOSURETIME,convertExposureTime(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.EXCITWAVELENGTH,convertExcitWavelength(channel.getExcitationWavelength(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.EXCITWAVELENGTH,convertExcitWavelength(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.EMISSIONWAVELENGTH,convertEmissionWavelength(channel.getEmissionWavelength(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.EMISSIONWAVELENGTH,convertEmissionWavelength(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.IMAGINGMODE,convertImagingMode(channel.getAcquisitionModeAsString(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.IMAGINGMODE,convertImagingMode(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.CONTRASTMETHOD,convertContrastMethod(channel.getContrastMethod(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.CONTRASTMETHOD,convertContrastMethod(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.NDFILTER,convertNDFilter(channel.getNDFilter(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.NDFILTER,convertNDFilter(null, REQUIRED));
			}
			try {tagMap.put(TagNames.PINHOLESIZE,convertPinholeSize(channel.getPinholeSize(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.PINHOLESIZE,convertPinholeSize(null, REQUIRED));
			}
		}else {
				tagMap.put(TagNames.CH_NAME,convertName(null,REQUIRED));
				tagMap.put(TagNames.COLOR,convertColor(null, REQUIRED));
				tagMap.put(TagNames.FLUOROPHORE,convertFluorophore(null, REQUIRED));
				tagMap.put(TagNames.ILLUMTYPE,convertIllumType(null, REQUIRED));
				tagMap.put(TagNames.EXPOSURETIME,convertExposureTime(null, REQUIRED));
				tagMap.put(TagNames.EXCITWAVELENGTH,convertExcitWavelength(null, REQUIRED));
				tagMap.put(TagNames.EMISSIONWAVELENGTH,convertEmissionWavelength(null, REQUIRED));
				tagMap.put(TagNames.IMAGINGMODE,convertImagingMode(null, REQUIRED));
				tagMap.put(TagNames.CONTRASTMETHOD,convertContrastMethod(null, REQUIRED));
				tagMap.put(TagNames.NDFILTER,convertNDFilter(null, REQUIRED));
				tagMap.put(TagNames.PINHOLESIZE,convertPinholeSize(null, REQUIRED));
		}
		return tagMap;
		
	}


	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/


	private TagData convertName(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.CH_NAME,value,prop,TagData.TEXTFIELD);
	}


	private TagData convertColor(Color value, boolean prop)
	{
		String val= (value != null) ? Integer.toHexString(value.getValue()):"";
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.COLOR,val,prop,TagData.TEXTFIELD);
	}
	private TagData convertFluorophore(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.FLUOROPHORE,value,prop,TagData.TEXTFIELD);
	}
	private TagData convertIllumType(String value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.ILLUMTYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(IlluminationType.class));
	}
	private TagData convertExposureTime(Time value,boolean prop)
	{
		ome.model.units.Time val=null;
		if(value!=null)
			val= new ome.model.units.Time(value);
		
//		String val=(value!=null)? String.valueOf(value.value()):"";
//		Unit unit=(value!=null)?value.unit():TagNames.EXPOSURETIME_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.EXPOSURETIME,val,ome.model.units.Time.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPosFloat(t,"Invalid input. Use float >0!"));
		return t;
	}
	private TagData convertExcitWavelength(Length value, boolean prop)
	{
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null)? value.unit(): TagNames.EXCITATIONWL_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.EXCITWAVELENGTH,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPosFloat(t,"Invalid input. Use float >0!"));
		return t;
	}
	private TagData convertEmissionWavelength(Length value, boolean prop)
	{
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null)? value.unit(): TagNames.EMISSIONWL_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.EMISSIONWAVELENGTH,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPosFloat(t,"Invalid input. Use float >0!"));
		return t;
	}

	private TagData convertPinholeSize(Length value, boolean prop)
	{
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val = (value!=null) ? String.valueOf(value.value()) : "";
//		Unit unit=(value!=null)? value.unit(): TagNames.PINHOLESIZE_UNIT;
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.PINHOLESIZE,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
	}

	private TagData convertImagingMode(String value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.IMAGINGMODE,value,prop,TagData.COMBOBOX,OMEValueConverter.getNames(AcquisitionMode.class));
	}

	private TagData convertContrastMethod(ContrastMethod value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.CONTRASTMETHOD,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(ContrastMethod.class));
	}
	private TagData convertNDFilter(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_CHANNEL,TagNames.NDFILTER,val,prop,TagData.TEXTFIELD);
	}
	
	

}
