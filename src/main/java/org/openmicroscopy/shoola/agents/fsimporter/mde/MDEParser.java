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
package org.openmicroscopy.shoola.agents.fsimporter.mde;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import ome.xml.model.Objective;
import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.Dichroic;
import ome.xml.model.LightSource;
import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ArcConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DetectorConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DichroicConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.FilamentConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.FilterConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.GenericExcitationSourceConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ImageConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.LEDConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.LaserConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ObjectiveConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
/**
 * Function to parse ModuleContent lists.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class MDEParser {

	/**
	 * read out ome-xml module tree and values
	 * @param file
	 * @return
	 */
	public static DefaultMutableTreeNode getOMEContent(File file, ModuleController controller) {
		return null;
	}
	
	/**
	 * Read out definied modules and their content:
	 * JSON:
	 * <ModuleType>:{
	 * 	ModuleContent:{
	 * 		<tagName>:{
	 * 			type:	     //{value,array,list}
	 * 			required:	 //{true,false}
	 * 			value:    //default value
	 * 			values:		//possible values
	 * 			unit:     //default unit
	 * 			units:		//possible units
	 * 		}
	 *  }
	 * }
	 * @param file
	 * @return
	 */
	public static HashMap<String,ModuleContent> readContentSpec(File file){
		return null;
		
	}
	
	
	public static List<ModuleContent> parseObjectiveList(List<Objective> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(Objective o:list) {
			ModuleContent content= new ModuleContent((new ObjectiveConverter()).convertData(o,null),TagNames.OME_ELEM_OBJECTIVE,
					TagNames.getParents(TagNames.OME_ELEM_OBJECTIVE));
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_OBJECTIVE);
			if(content!=null) {
//				content.setAttributes((new ObjectiveConverter()).convertData(o, null));
				resultList.add(content);
			}
		}
		return resultList;
	}
	
	public static List<ModuleContent> parseDetectorList(List<Detector> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(Detector o:list) {
			ModuleContent content= new ModuleContent((new DetectorConverter()).convertData(o,null),TagNames.OME_ELEM_DETECTOR,
					TagNames.getParents(TagNames.OME_ELEM_DETECTOR));
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_DETECTOR);
			if(content!=null) {
//			content.setAttributes((new DetectorConverter()).convertData(o, null));
			resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseLightSourceLaser(List<LightSource> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(LightSource o:list) {
			ModuleContent content=null;
			if(o instanceof Laser) {
				content= new ModuleContent((new LaserConverter()).convertData((Laser)o,null),TagNames.OME_ELEM_LASER,
					TagNames.getParents(TagNames.OME_ELEM_LASER));
			}
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_LIGHTSOURCE);
			if(content!=null) {
//			content.setAttributes((new LightSourceConverter()).convertData(o, null));
			resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseLightSourceArc(List<LightSource> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(LightSource o:list) {
			ModuleContent content=null;
			if(o instanceof Arc) {
				content= new ModuleContent((new ArcConverter()).convertData((Arc)o,null),TagNames.OME_ELEM_ARC,
						TagNames.getParents(TagNames.OME_ELEM_ARC));
			}
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_LIGHTSOURCE);
			if(content!=null) {
//			content.setAttributes((new LightSourceConverter()).convertData(o, null));
			resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseLightSourceFilament(List<LightSource> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(LightSource o:list) {
			ModuleContent content=null;
			if(o instanceof Filament) {
				content= new ModuleContent((new FilamentConverter()).convertData((Filament)o,null),TagNames.OME_ELEM_FILAMENT,
						TagNames.getParents(TagNames.OME_ELEM_FILAMENT));
			}
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_LIGHTSOURCE);
			if(content!=null) {
//			content.setAttributes((new LightSourceConverter()).convertData(o, null));
			resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseLightSourceLED(List<LightSource> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(LightSource o:list) {
			ModuleContent content=null;
			if(o instanceof LightEmittingDiode) {
				content= new ModuleContent((new LEDConverter()).convertData((LightEmittingDiode)o,null),TagNames.OME_ELEM_LED,
						TagNames.getParents(TagNames.OME_ELEM_LED));
			}
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_LIGHTSOURCE);
			if(content!=null) {
//			content.setAttributes((new LightSourceConverter()).convertData(o, null));
			resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseLightSourceGES(List<LightSource> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(LightSource o:list) {
			ModuleContent content=null;
			if(o instanceof GenericExcitationSource) {
				content= new ModuleContent((new GenericExcitationSourceConverter()).convertData((GenericExcitationSource)o,null),TagNames.OME_ELEM_GENERICEXCITATIONSOURCE,
						TagNames.getParents(TagNames.OME_ELEM_GENERICEXCITATIONSOURCE));
			}
			//			ModuleContent content = c.getContent(TagNames.OME_ELEM_LIGHTSOURCE);
			if(content!=null) {
				//			content.setAttributes((new LightSourceConverter()).convertData(o, null));
				resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseFilterList(List<Filter> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(Filter o:list) {
			ModuleContent content= new ModuleContent((new FilterConverter()).convertData(o),TagNames.OME_ELEM_FILTER,
					TagNames.getParents(TagNames.OME_ELEM_FILTER));
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_FILTER);
			if(content!=null) {
//			content.setAttributes((new FilterConverter()).convertData(o));
			resultList.add(content);
			}
		}
		return resultList;
	}
	public static List<ModuleContent> parseDichroicList(List<Dichroic> list,ModuleController c){
		if(list==null)
			return null;
		List<ModuleContent> resultList=new ArrayList<>();
		for(Dichroic o:list) {
			ModuleContent content= new ModuleContent((new DichroicConverter()).convertData(o),TagNames.OME_ELEM_DICHROIC,
					TagNames.getParents(TagNames.OME_ELEM_DICHROIC));
//			ModuleContent content = c.getContent(TagNames.OME_ELEM_LIGHTSOURCE);
			if(content!=null) {
//			content.setAttributes((new DichroicConverter()).convertData(o));
			resultList.add(content);
			}
		}
		return resultList;
	}
	
	
	
	
}
