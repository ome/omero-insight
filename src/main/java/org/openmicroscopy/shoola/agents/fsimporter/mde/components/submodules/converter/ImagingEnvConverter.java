package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.util.LinkedHashMap;

import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.unit.Unit;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.primitives.PercentFraction;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

public class ImagingEnvConverter extends DataConverter{

	public ImagingEnvConverter()
	{
		tagMap=new LinkedHashMap<String,TagData>();
	}

	public LinkedHashMap<String, TagData> convertData(ImagingEnvironment env)
	{
		if(env!=null) {
		try {tagMap.put(TagNames.TEMP,convertTemperature(env.getTemperature(), REQUIRED));	} 
		catch (NullPointerException e) {
			tagMap.put(TagNames.TEMP,convertTemperature(null, REQUIRED));
		}
		try {tagMap.put(TagNames.AIRPRESS,convertAirPressure(env.getAirPressure(), REQUIRED));	} 
		catch (NullPointerException e) {
			tagMap.put(TagNames.AIRPRESS,convertAirPressure(null, REQUIRED));
		}
		try {tagMap.put(TagNames.HUMIDITY,convertHumidity(env.getHumidity(), REQUIRED));	} 
		catch (NullPointerException e) {
			tagMap.put(TagNames.HUMIDITY,convertHumidity(null, REQUIRED));
		}
		try {tagMap.put(TagNames.CO2,convertCo2Percent(env.getCO2Percent(), REQUIRED));	} 
		catch (NullPointerException e) {
			tagMap.put(TagNames.CO2,convertCo2Percent(null, REQUIRED));	
		}
		}else {
				tagMap.put(TagNames.TEMP,convertTemperature(null, REQUIRED));
				tagMap.put(TagNames.AIRPRESS,convertAirPressure(null, REQUIRED));
				tagMap.put(TagNames.HUMIDITY,convertHumidity(null, REQUIRED));
				tagMap.put(TagNames.CO2,convertCo2Percent(null, REQUIRED));	
		}
		return tagMap;
	}


	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/

	private TagData convertTemperature(Temperature value, boolean prop)
	{
		ome.model.units.Temperature val=null;
		if(value!=null)
			val=new ome.model.units.Temperature(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
		//	temperatureUnit=(value!=null) ? value.unit():temperatureUnit;
//		Unit unit=(value!=null)?value.unit() : TagNames.TEMPERATURE_UNIT;
		return new TagData(TagNames.OME_ELEM_IMGENV,TagNames.TEMP,val,ome.model.units.Temperature.class,prop,TagData.TEXTFIELD);
	}

	private TagData convertAirPressure(Pressure value, boolean prop)
	{
		ome.model.units.Pressure val=null;
		if(value!=null)
			val=new ome.model.units.Pressure(value);
//		String val=(value!=null) ? String.valueOf(value.value()) :"";
		//	airPressureUnit=(value!=null) ? value.unit() :airPressureUnit;
//		Unit unit= value!=null ? value.unit() : TagNames.PRESSURE_UNIT;
		return new TagData(TagNames.OME_ELEM_IMGENV,TagNames.AIRPRESS,val,ome.model.units.Pressure.class,prop,TagData.TEXTFIELD);
	}

	private TagData convertHumidity(PercentFraction value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.getValue()) :"";
//		Unit unit=TagNames.PERCENT_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_IMGENV,TagNames.HUMIDITY,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPercentFraction(t,"Invalid input. Use float between 0.0 and 1.0!"));
		return t;
	}

	private TagData convertCo2Percent(PercentFraction value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.getValue()) :"";
//		Unit unit=TagNames.PERCENT_UNIT;
		TagData t = new TagData(TagNames.OME_ELEM_IMGENV,TagNames.CO2,val,prop,TagData.TEXTFIELD);
		t.setDocumentListener(createDocumentListenerPercentFraction(t,"Invalid input. Use float between 0.0 and 1.0!"));
		return t;
	}

}
