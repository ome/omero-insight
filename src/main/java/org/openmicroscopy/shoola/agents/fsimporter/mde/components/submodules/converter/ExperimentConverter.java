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

import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * Convert OME-XML Experiment object to list of TagData. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ExperimentConverter extends DataConverter{
	

	public ExperimentConverter()
	{
		tagMap=new LinkedHashMap<String,TagData>();
	}
	
	public LinkedHashMap<String, TagData> convertData(Experiment exp, Experimenter exper)
	{
		if(exp!=null) {
			try{ 
				tagMap.put(TagNames.DESC,convertDescription(exp.getDescription(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.DESC,convertDescription(null, REQUIRED));
			}
			try{ 
				tagMap.put(TagNames.E_TYPE,convertType(exp.getType(), REQUIRED));
			}catch(NullPointerException e){
				tagMap.put(TagNames.E_TYPE,convertType(null, REQUIRED));
			}
//			try{ 
//				tagMap.put(TagNames.PROJECTPARTNER,convertProjectPartner(exp.getProjectPartnerName(), REQUIRED));
//			}catch(NullPointerException e){
//				tagMap.put(TagNames.PROJECTPARTNER,convertProjectPartner(null, REQUIRED));
//			}
			try{
				//			setName(expContainer.getExperimenter(),REQUIRED);
				tagMap.put(TagNames.EXPNAME,convertName(exper,REQUIRED));
			}catch(NullPointerException e){
				tagMap.put(TagNames.EXPNAME,convertName((Experimenter)null,REQUIRED));
			}
//			try{
//				tagMap.put(TagNames.GROUP,convertGroupName(exp.getGroupName(), OPTIONAL));
//			}catch(NullPointerException e){
//				tagMap.put(TagNames.GROUP,convertGroupName(null, OPTIONAL));
//			}
//			try{tagMap.put(TagNames.PROJECTNAME,convertProjectName(exp.getProjectName(), OPTIONAL));
//			}catch(NullPointerException e){
//				tagMap.put(TagNames.PROJECTNAME,convertProjectName(null, OPTIONAL));
//			}
		}else {
			tagMap.put(TagNames.DESC,convertDescription(null, REQUIRED));
			tagMap.put(TagNames.E_TYPE,convertType(null, REQUIRED));
//			tagMap.put(TagNames.PROJECTPARTNER,convertProjectPartner(null, REQUIRED));
			tagMap.put(TagNames.EXPNAME,convertName((Experimenter)null,REQUIRED));
//			tagMap.put(TagNames.GROUP,convertGroupName(null, OPTIONAL));
//			tagMap.put(TagNames.PROJECTNAME,convertProjectName(null, OPTIONAL));
		}
		return tagMap;
	}
	
	
	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	private TagData convertType(ExperimentType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():null;
		return new TagData(TagNames.ELEM_EXPERIMENT,TagNames.E_TYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(ExperimentType.class));
	}

	private ExperimentType parseExperimentType(String value)
	{
		if(value==null || value.equals(""))
			return null;

		ExperimentType t=null;
		try{
			t=ExperimentType.fromString(value);
		}catch(EnumerationException e){
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] ExperimentType: "+value+"is not supported");
		}
		return t;
	}

	private TagData convertDescription(String value, boolean prop)
	{
		return new TagData(TagNames.ELEM_EXPERIMENT,TagNames.DESC,value,prop,TagData.TEXTAREA);
	}


	
	private TagData convertName(Experimenter value, boolean prop)
	{
		return new TagData(TagNames.ELEM_EXPERIMENT,TagNames.EXPNAME,getExperimenterName(value),prop,TagData.TEXTFIELD);
	}

	private String getExperimenterName(Experimenter e)
	{

		String res=null;
		if(e!=null){
			String fName= (e.getFirstName()!=null && !e.getFirstName().equals("")) ? e.getFirstName():"";
			String lName=(e.getLastName()!=null && !e.getLastName().equals("")) ? e.getLastName() : "";

			if(fName.equals(""))
				res=lName;
			else
				res=fName+" "+lName;
		}
		return res;
	}
	



	private TagData convertProjectName(String value, boolean prop)
	{
		return new TagData(TagNames.ELEM_EXPERIMENT,TagNames.PROJECTNAME,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertGroupName(String value, boolean prop)
	{
		return new TagData(TagNames.ELEM_EXPERIMENT,TagNames.GROUP,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertProjectPartner(String value, boolean prop)
	{
		return new TagData(TagNames.ELEM_EXPERIMENT,TagNames.PROJECTPARTNER,value,prop,TagData.TEXTFIELD);
	}

//	private Experimenter parseExperimenter(String str)
//	{
//		Experimenter ex= null;
//		
//		if(str!=null && str.length()>0){
//			String[] split=str.split("\\s+");
//			if(split.length >1){
//				ex=new Experimenter();
//				ex.setFirstName(split[0]);
//				ex.setLastName(split[1]);
//			}else{
//				return null;
//			}
//		}
//		return ex;
//	}

}
