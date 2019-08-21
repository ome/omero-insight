package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.util.LinkedHashMap;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

import ome.xml.model.FilterSet;

public class FilterSetConverter extends DataConverter{

	public FilterSetConverter() {}
	
	public LinkedHashMap<String, TagData> convertData(FilterSet f)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(f!=null) {
			try{tagMap.put(TagNames.MODEL,convertModel(f.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(f.getManufacturer(),  REQUIRED));
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

	private TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LIGHTPATH_FS,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_LIGHTPATH_FS,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}
}
