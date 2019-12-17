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

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * Convert OME-XML Objective object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ObjectiveConverter extends DataConverter{

	public ObjectiveConverter()
	{
		tagMap=new LinkedHashMap<String,TagData>();
	}
	
	/**
	 * create fields for given objective. Fields are null if objective doesn't exists
	 * @param objective
	 */
	public LinkedHashMap<String, TagData> convertData(Objective objective, ObjectiveSettings settings)
	{
		if(objective!=null) {
			try{tagMap.put(TagNames.ID,convertID(objective.getID(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ID,convertID(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MODEL,convertModel(objective.getModel(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MODEL,convertModel(null,REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(objective.getManufacturer(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MANUFAC,convertManufact(null,REQUIRED));
			}
			try{tagMap.put(TagNames.NOMMAGN,convertNomMagnification(objective.getNominalMagnification(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.NOMMAGN,convertNomMagnification(null,REQUIRED));
			}
			try{tagMap.put(TagNames.CALMAGN,convertCalMagnification(objective.getCalibratedMagnification(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.CALMAGN,convertCalMagnification(null,REQUIRED));
			}
			try{tagMap.put(TagNames.LENSNA,convertLensNA(objective.getLensNA(),REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.LENSNA,convertLensNA(null,REQUIRED));
			}
			try{tagMap.put(TagNames.IMMERSION,convertImmersion(objective.getImmersion(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.IMMERSION,convertImmersion(null,REQUIRED));
			}
			try{tagMap.put(TagNames.CORRECTION,convertCorrection(objective.getCorrection(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.CORRECTION,convertCorrection(null,REQUIRED));
			}
			try{tagMap.put(TagNames.WORKDIST,convertWorkingDist(objective.getWorkingDistance(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.WORKDIST,convertWorkingDist(null,REQUIRED));
			}
			try{tagMap.put("Iris",convertIris(objective.getIris(),REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put("Iris",convertIris(null,REQUIRED));
			}
		}else {
			tagMap.put(TagNames.ID,convertID(null,REQUIRED));
			tagMap.put(TagNames.MODEL,convertModel(null,REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null,REQUIRED));
			tagMap.put(TagNames.NOMMAGN,convertNomMagnification(null,REQUIRED));
			tagMap.put(TagNames.CALMAGN,convertCalMagnification(null,REQUIRED));
			tagMap.put(TagNames.LENSNA,convertLensNA(null,REQUIRED));
			tagMap.put(TagNames.IMMERSION,convertImmersion(null,REQUIRED));
			tagMap.put(TagNames.CORRECTION,convertCorrection(null,REQUIRED));
			tagMap.put(TagNames.WORKDIST,convertWorkingDist(null,REQUIRED));
			tagMap.put("Iris",convertIris(null,REQUIRED));
		}
		
		if(settings!=null) {
			//settings:
			try{tagMap.put(TagNames.REFINDEX,convertRefractIndex(settings.getRefractiveIndex(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.REFINDEX,convertRefractIndex(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.OBJ_MEDIUM,convertMedium(settings.getMedium(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.OBJ_MEDIUM,convertMedium(null, REQUIRED));
			}
			try{ tagMap.put(TagNames.CORCOLLAR,convertCorCollar(settings.getCorrectionCollar(), REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.CORCOLLAR,convertCorCollar(null, REQUIRED));
			}
		}else {
			tagMap.put(TagNames.REFINDEX,convertRefractIndex(null, REQUIRED));
			tagMap.put(TagNames.OBJ_MEDIUM,convertMedium(null, REQUIRED));
			tagMap.put(TagNames.CORCOLLAR,convertCorCollar(null, REQUIRED));
		}
		
		return tagMap;
	}
	

	public TagData convertID(String value,boolean prop) {
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.ID,value,prop,TagData.TEXTFIELD);
	}
	private TagData convertModel(String value,boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}
	

	private TagData convertManufact(String value,boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}
	

	private TagData convertNomMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t= new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.NOMMAGN,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}




	private TagData convertCalMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t = new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.CALMAGN,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	

	private TagData convertLensNA(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t = new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.LENSNA,val,prop,TagData.TEXTFIELD);
			t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	

	private TagData convertImmersion(Immersion value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.IMMERSION,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(Immersion.class));
	}
	

	private TagData convertCorrection(Correction value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.CORRECTION,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(Correction.class));
	}
	

	private TagData convertWorkingDist(Length value,boolean prop)
	{
		ome.model.units.Length val=null;
		if(value!=null)
			val=new ome.model.units.Length(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null) ? value.unit() : TagNames.WORKDIST_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.WORKDIST,val,ome.model.units.Length.class,prop,TagData.TEXTFIELD);
			t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	

	private TagData convertIris(Boolean value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.IRIS,val,prop,TagData.TEXTFIELD);
	}

	

	/*------------------------------------------------------
	 * Settings Values
	 * -----------------------------------------------------*/
	private TagData convertCorCollar(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t = new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.CORCOLLAR,val,prop,TagData.TEXTFIELD);
			t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
	private TagData convertMedium(Medium value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		return new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.OBJ_MEDIUM,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(Medium.class));
	}
	private TagData convertRefractIndex(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		TagData t = new TagData(TagNames.OME_ELEM_OBJECTIVE,TagNames.REFINDEX,val,prop,TagData.TEXTFIELD);
			t.setDocumentListener(createDocumentListenerDouble(t,"Invalid input. Use float!"));
		return t;
	}
}
