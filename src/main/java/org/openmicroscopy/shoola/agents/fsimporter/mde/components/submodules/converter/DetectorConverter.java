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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.units.quantity.Time;
import ome.xml.model.StageLabel;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.Timestamp;
import ome.xml.model.Image;
import ome.units.UNITS;
import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.ui.IconManager;


import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml.DetectorSettings;

/**
 * Convert OME-XML Detector object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class DetectorConverter extends DataConverter{

	public DetectorConverter() {}

	@Override
	public  JComponent getLabel(String name, int index, List<ActionListener> listeners) {
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
		applyBtn.setActionCommand(String.valueOf(index));
		if(listeners!=null && listeners.size()>0)applyBtn.addActionListener(listeners.get(0));
		
		labelPane.add(applyBtn);

		return labelPane;
	}
	
	

	public LinkedHashMap<String, TagData> convertData(Detector detector, DetectorSettings settings)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(detector!=null) {
			try{tagMap.put(TagNames.ID,convertID(detector.getID(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ID,convertID(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MODEL,convertModel(detector.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(detector.getManufacturer(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			}
			try{
				tagMap.put(TagNames.D_TYPE,convertType(detector.getType(),  REQUIRED));
				//if(detector.getType()!=null)
				//TODOactivateAttributesForType(detector.getType());
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.D_TYPE,convertType(null,  REQUIRED));
			}

			try{tagMap.put(TagNames.ZOOM,convertZoom(detector.getZoom(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ZOOM,convertZoom(null, REQUIRED));
			}
			try{tagMap.put(TagNames.AMPLGAIN,convertAmplGain(detector.getAmplificationGain(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.AMPLGAIN,convertAmplGain(null,  REQUIRED));
			}

			try{tagMap.put(TagNames.VOLTAGE,convertVoltage(detector.getVoltage(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.VOLTAGE,convertVoltage(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.OFFSET,convertOffset(detector.getOffset(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.OFFSET,convertOffset(null, REQUIRED));
			}
		}else {
			tagMap.put(TagNames.ID,convertID(null,REQUIRED));
			tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			tagMap.put(TagNames.D_TYPE,convertType(null,  REQUIRED));
			tagMap.put(TagNames.ZOOM,convertZoom(null, REQUIRED));
			tagMap.put(TagNames.AMPLGAIN,convertAmplGain(null,  REQUIRED));
			tagMap.put(TagNames.VOLTAGE,convertVoltage(null, REQUIRED));
			tagMap.put(TagNames.OFFSET,convertOffset(null, REQUIRED));
		}

		//settings
		if(settings!=null) {
			try{tagMap.put(TagNames.GAIN,convertGain(settings.getGain(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.GAIN,convertGain(null, REQUIRED));
			}
			try{
				tagMap.put(TagNames.VOLTAGE,convertVoltage(settings.getVoltage(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.VOLTAGE,convertVoltage(null, REQUIRED));
			}
			try{
				tagMap.put(TagNames.OFFSET,convertOffset(settings.getOffset(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.OFFSET,convertOffset(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.CONFZOOM,convertConfocalZoom(settings.getZoom(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.CONFZOOM,convertConfocalZoom(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.BINNING,convertBinning(settings.getBinning(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.BINNING,convertBinning(null, REQUIRED));
			}
			try{
				tagMap.put(TagNames.SUBARRAY,convertSubarray(settings.getSubarray(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.SUBARRAY,convertSubarray(null, REQUIRED));
			}
		}else {
			tagMap.put(TagNames.GAIN,convertGain(null, REQUIRED));
			tagMap.put(TagNames.VOLTAGE,convertVoltage(null, REQUIRED));
			tagMap.put(TagNames.OFFSET,convertOffset(null, REQUIRED));
			tagMap.put(TagNames.CONFZOOM,convertConfocalZoom(null, REQUIRED));
			tagMap.put(TagNames.BINNING,convertBinning(null, REQUIRED));
			tagMap.put(TagNames.SUBARRAY,convertSubarray(null, REQUIRED));
		}
		return tagMap;
	}

	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	public TagData convertID(String value,boolean prop) {
		return new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.ID,value,prop,TagData.TEXTFIELD);
	}

	public TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}

	public TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}

	public TagData convertType(DetectorType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		TagData t= new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.D_TYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(DetectorType.class));
		t.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//activateAttributesForType(parseDetectorType(t.getTagValue()));
			}
		});
		return t;
	}

	//==em gain if type==PMT
	public TagData convertAmplGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t = new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.AMPLGAIN,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}


	public TagData convertZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t= new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.ZOOM,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}





	/*------------------------------------------------------
	 * Set methods settings Values
	 * -----------------------------------------------------*/
	public TagData convertGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t= new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.GAIN,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	//TODO
	public TagData convertBinning(Binning value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		return new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.BINNING,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(Binning.class));
	}
	public TagData convertVoltage(ElectricPotential value, boolean prop)
	{
		ome.model.units.ElectricPotential val=null;
		if(value!=null)
			val=new ome.model.units.ElectricPotential(value);
		return new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.VOLTAGE,val,ome.model.units.ElectricPotential.class,prop,TagData.TEXTFIELD);
	}
	public TagData convertOffset(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t= new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.OFFSET,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	public TagData convertConfocalZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t= new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.CONFZOOM,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	public TagData convertSubarray(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DETECTOR,TagNames.SUBARRAY,value,prop,TagData.TEXTFIELD);
	}


	/*---------------------------------------------------------
	 * Parser
	 * --------------------------------------------------------*/

//	private static DetectorType parseDetectorType(String c) 
//	{
//		if(c==null || c.equals(""))
//			return null;
//
//		DetectorType m=null;
//		try{
//			m=DetectorType.fromString(c);
//		}catch(EnumerationException e){
//			LOGGER.warn("[MDE] DetectorType: "+c+" is not supported");
//			//			m=DetectorType.OTHER;
//		}
//		return m;
//	}
	private static ElectricPotential parseElectricPotential(String c, Unit unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		ElectricPotential p=null;

		return new ElectricPotential(Double.valueOf(c), unit);
	}
	private static Binning parseBinning(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;

		return Binning.fromString(c);
	}
	
	
}
