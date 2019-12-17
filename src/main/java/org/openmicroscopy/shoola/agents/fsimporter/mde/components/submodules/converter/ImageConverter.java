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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.units.quantity.Time;
import ome.xml.model.StageLabel;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.Timestamp;
import ome.xml.model.Image;
import ome.units.UNITS;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * Convert OME-XML Image object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ImageConverter extends DataConverter
{
	public ImageConverter() {
		
	}
	
	
	public LinkedHashMap<String, TagData> convertData(Image image)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(image!=null){ 
			try{
				tagMap.put(TagNames.IMG_NAME,convertName(image.getName(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.IMG_NAME,convertName(null,REQUIRED));
			}

			try{
				tagMap.put(TagNames.DESC,convertDescription(image.getDescription(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.DESC,convertDescription(null,REQUIRED));
			}
			
			try{ 
				tagMap.put(TagNames.ACQTIME,convertAcqTime(image.getAcquisitionDate(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ACQTIME,convertAcqTime(null,REQUIRED));
			}
			
			try{
				String[] dimXY={image.getPixels().getSizeX().toString(),
						image.getPixels().getSizeY().toString()};
				tagMap.put(TagNames.DIMXY,convertDimXY(dimXY,REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.DIMXY,convertDimXY(new String[] {null,null},REQUIRED));
			}

			try{
				tagMap.put(TagNames.PIXELTYPE,convertPixelType(image.getPixels().getType(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.PIXELTYPE,convertPixelType(null,REQUIRED));
			}
			
			try{ 
				tagMap.put(TagNames.PIXELSIZE,convertPixelSizeXY(image.getPixels().getPhysicalSizeX(),image.getPixels().getPhysicalSizeY(),
					REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.PIXELSIZE,convertPixelSizeXY(null,null,	REQUIRED));
			}

			try{ 
				String[] dimZTC={image.getPixels().getSizeZ().toString(),
						image.getPixels().getSizeT().toString(),
						image.getPixels().getSizeC().toString()};
				tagMap.put(TagNames.DIMZTC,convertDimZTC(dimZTC,REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.DIMZTC,convertDimZTC(new String[] {null,null,null},REQUIRED));
			}
					
			try{
				tagMap.put(TagNames.TIMEINC,convertTimeIncrement(image.getPixels().getTimeIncrement(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.TIMEINC,convertTimeIncrement(null, REQUIRED));
			}
			try{
				StageLabel stage=image.getStageLabel();
//				if(stage!=null){
					tagMap.put(TagNames.STAGELABEL,convertStagePos(stage.getX(),stage.getY(), REQUIRED));
//				}
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.STAGELABEL,convertStagePos(null,null, REQUIRED));
			}
			//TODO wellsample

//			try{
//				tagMap.put(TagNames.WELLNR,convertWellNr(null, REQUIRED));
//			} catch (NullPointerException e) { }
			
		}else {			
			//init default tags
			tagMap.put(TagNames.IMG_NAME,convertName(null,REQUIRED));
			tagMap.put(TagNames.DESC,convertDescription(null,REQUIRED));
			tagMap.put(TagNames.ACQTIME,convertAcqTime(null,REQUIRED));
			tagMap.put(TagNames.DIMXY,convertDimXY(new String[] {null,null},REQUIRED));
			tagMap.put(TagNames.PIXELTYPE,convertPixelType(null,REQUIRED));
			tagMap.put(TagNames.PIXELSIZE,convertPixelSizeXY(null,null,	REQUIRED));
			tagMap.put(TagNames.DIMZTC,convertDimZTC(new String[] {null,null,null},REQUIRED));
			tagMap.put(TagNames.TIMEINC,convertTimeIncrement(null, REQUIRED));
			tagMap.put(TagNames.STAGELABEL,convertStagePos(null,null, REQUIRED));
		}
		return tagMap;
	}
	
	private TagData convertName(String value, boolean prop){
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.IMG_NAME,value,prop,TagData.TEXTFIELD);
	}
	
	private TagData convertDescription(String value, Boolean prop) {
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.IMG_DESC,value,prop,TagData.TEXTFIELD);
	}
	//Datums- und Zeitfeld
	private TagData convertAcqTime(Timestamp value, boolean prop){
		String val= (value != null) ? value.getValue():"";
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.ACQTIME,val,prop,TagData.TIMESTAMP);
	}
	
	private TagData convertDimXY(String[] value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.DIMXY,value,prop,TagData.ARRAYFIELDS,2);
	}
	
	private TagData convertPixelType(PixelType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.PIXELTYPE,val,prop,TagData.TEXTFIELD);
	}
	
	private TagData convertPixelSizeXY(Length valueX, Length valueY, boolean prop)
	{
		ome.model.units.Length valX=null;
		if(valueX!=null)
			valX=new ome.model.units.Length(valueX);
		ome.model.units.Length valY=null;
		if(valueY!=null)
			valY=new ome.model.units.Length(valueY);
//		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
//		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
//		Unit unit=(valueX!=null) ? valueX.unit() : TagNames.PIXELSIZE_UNIT;
		ome.model.units.Unit[] val= {valX,valY};
		TagData pixelSize = new TagData(TagNames.OME_ELEM_IMAGE,TagNames.PIXELSIZE,val,ome.model.units.Length.class,prop,TagData.ARRAYFIELDS,2);
		pixelSize.setDocumentListener(createDocumentListenerPosFloat(pixelSize,"Invalid input. Use float >0!"));
		return pixelSize;		
	}
	
	private TagData convertDimZTC(String[] value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.DIMZTC,value,prop,TagData.ARRAYFIELDS,3);
	}

	//TODO
	private TagData convertStagePos(Length valueX, Length valueY, boolean prop)
	{
		ome.model.units.Length valX=null;
		if(valueX!=null)
			valX=new ome.model.units.Length(valueX);
		ome.model.units.Length valY=null;
		if(valueY!=null)
			valY=new ome.model.units.Length(valueY);
//		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
//		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
//		Unit unit=(valueX!=null) ? valueX.unit() : TagNames.STAGEPOS_UNIT;
//		String symbol = unit==UNITS.REFERENCEFRAME ? "rf" : unit.getSymbol();
//		String[] val= {valX,valY};
		ome.model.units.Unit[] val= {valX,valY};
		TagData stagePos = new TagData(TagNames.OME_ELEM_IMAGE,TagNames.STAGELABEL,val,ome.model.units.Length.class,prop,TagData.ARRAYFIELDS,2);
		return stagePos;
	}


	//unit field
	private TagData convertStepSize(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.STEPSIZE,value,prop,TagData.TEXTFIELD);
	}
	/**
	 * used for time series that have a global
timing specification instead of per-timepoint timing info.
For example in a video stream.
	 * @param value
	 * @param prop
	 */
	//unit field
	private TagData convertTimeIncrement(Time value, boolean prop)
	{
		ome.model.units.Time val=null;
		if(value!=null)
			val=new ome.model.units.Time(value);
//		String val= (value != null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null) ? value.unit() : TagNames.TIMEINC_UNIT;
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.TIMEINC,val,ome.model.units.Time.class,prop,TagData.TEXTFIELD);
	}
	private TagData convertWellNr(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_IMAGE,TagNames.WELLNR,value,prop,TagData.TEXTFIELD);
	}




}
