/*
 * Copyright (C) <2016-2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.util.HashMap;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.OMEValueConverter;

import com.google.common.collect.ObjectArrays;

import ome.model.units.UnitEnum;
import ome.units.UNITS;
import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Medium;
import ome.xml.model.enums.Pulse;
import ome.xml.model.enums.UnitsElectricPotential;
import ome.xml.model.enums.UnitsFrequency;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.UnitsPressure;
import ome.xml.model.enums.UnitsTemperature;
import ome.xml.model.enums.handlers.UnitsElectricPotentialEnumHandler;
import ome.xml.model.enums.handlers.UnitsFrequencyEnumHandler;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;
import ome.xml.model.enums.handlers.UnitsPressureEnumHandler;
import ome.xml.model.enums.handlers.UnitsTemperatureEnumHandler;
import ome.xml.model.primitives.PercentFraction;

/**
 * Names of OME-XML Objects and their properties, Units etc.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class TagNames 
{
	public static final String PREFIX_SETTINGS="User::";
	
	public static final String OME_ROOT="OME-Model";
	public static final String OME_ELEM_DETECTOR="OME:Detector";
	public static final String OME_ELEM_IMAGE="OME:Image";
	public static final String OME_ELEM_EXPERIMENT="OME:Experiment";
	public static final String OME_ELEM_PLANE="OME:Plane";
	public static final String OME_ELEM_OBJECTIVE="OME:Objective";
	public static final String OME_ELEM_CHANNEL="OME:Channel";
	public static final String OME_ELEM_IMGENV="OME:ImagingEnvironment";
	public static final String OME_ELEM_LIGHTSOURCE="OME:LightSource";
	public static final String OME_ELEM_LASER="OME:Laser";
	public static final String OME_ELEM_LED="OME:LightEmittingDiode";
	public static final String OME_ELEM_ARC="OME:Arc";
	public static final String OME_ELEM_FILAMENT="OME:Filament";
	public static final String OME_ELEM_GENERICEXCITATIONSOURCE="OME:Generic_Excitation_Src";
	public static final String OME_ELEM_LIGHTPATH="OME:LightPath";
	public static final String OME_ELEM_FILTER="OME:Filter";
	public static final String OME_ELEM_DICHROIC="OME:Dichroic";
	public static final String OME_ELEM_LIGHTPATH_EM="OME:EmissionFilter";
	public static final String OME_ELEM_LIGHTPATH_EX="OME:ExcitationFilter";
	public static final String OME_ELEM_LIGHTPATH_FS="OME:FilterSet";
	public static final String ELEM_SAMPLE="Sample";
	public static final String ELEM_EXPERIMENT="Experiment";
	
	
	public static final String[] BOOLEAN_COMBO = {"true","false"};
	public static final String ID = "ID";
	public static final String MODEL="Model";
	public static final String MANUFAC="Manufacturer";
//	public static final String TYPE="Type";
	public static final String DESC="Description";
	
	//--------------------------
	//	OBJECTIVE
	//--------------------------
	//	MODEL
	//	MANUFAC
	public static final String NOMMAGN="Nominal Magnification";
	public static final String CALMAGN="Calibration Magnification";
	public static final String LENSNA="Lens NA";
	public static final String IMMERSION="Immersion";
	public static final String CORRECTION="Correction";
	public static final String IRIS="Iris";
	public static final String WORKDIST="Working Distance";
	
	public static final ome.model.enums.UnitsLength WORKDIST_UNIT=ome.model.enums.UnitsLength.MICROMETER;
	
	//Objective Settings
	public static final String CORCOLLAR=PREFIX_SETTINGS+"Correction Collar";
	public static final String OBJ_MEDIUM=PREFIX_SETTINGS+"Medium";
	public static final String REFINDEX=PREFIX_SETTINGS+"Refraction Index";
	
	//--------------------------
	//	CHANNEL
	//--------------------------
	public static final String CH_NAME="Name";
	public static final String COLOR="Color";
	public static final String FLUOROPHORE="Fluorophore";
	public static final String ILLUMTYPE="Illumination Type";
	public static final String EXPOSURETIME="Exposure Time";
	public static final String EXCITWAVELENGTH="Excitation Wavelength";
	public static final String EMISSIONWAVELENGTH="Emission Wavelength";
	public static final String IMAGINGMODE="Imaging Mode";
	public static final String ILLUMINATIONMODE="Illumination Mode";
	public static final String CONTRASTMETHOD="Contrast Method";
	public static final String NDFILTER="ND Filter";
	public static final String PINHOLESIZE="Pinhole Size";
	
	public static final ome.model.enums.UnitsLength PINHOLESIZE_UNIT=ome.model.enums.UnitsLength.MICROMETER;
	public static final ome.model.enums.UnitsLength EXCITATIONWL_UNIT=ome.model.enums.UnitsLength.NANOMETER;
	public static final ome.model.enums.UnitsLength EMISSIONWL_UNIT=ome.model.enums.UnitsLength.NANOMETER;
	public static final ome.model.enums.UnitsTime EXPOSURETIME_UNIT=ome.model.enums.UnitsTime.SECOND;
	
	
	
	//--------------------------
	//	Detector
	//--------------------------
	//	MODEL="Model";
	//	MANUFAC="Manufacturer";
	public static final String D_TYPE="DetectorType";
	public static final String ZOOM="Zoom";
	public static final String AMPLGAIN="AmplificationGain";
	
	//DetectorSettings
	//TODO rename to SET_...
	public static final String GAIN=PREFIX_SETTINGS+"Gain";
	public static final String VOLTAGE=PREFIX_SETTINGS+"Voltage";
	public static final String OFFSET=PREFIX_SETTINGS+"Offset";
	public static final String CONFZOOM=PREFIX_SETTINGS+"Confocal Zoom";
	public static final String BINNING=PREFIX_SETTINGS+"Binning";
	public static final String SUBARRAY=PREFIX_SETTINGS+"Subarray";
	
//	public static final Unit<ElectricPotential> VOLTAGE_UNIT=UNITS.V;
	public static final ome.model.enums.UnitsElectricPotential VOLTAGE_UNIT = ome.model.enums.UnitsElectricPotential.VOLT;
	

	
	//--------------------------
	//	Experiment
	//--------------------------
	//	DESC="Description";
	public static final String E_TYPE="ExperimentType";
	public static final String EXPNAME="Experimenter Name";
	
	public static final String PROJECTNAME="Project Name";
	public static final String GROUP="Group";
	public static final String PROJECTPARTNER="Project Partner";
	
	
	
	//--------------------------
	//	Image
	//--------------------------
	public static final String IMG_NAME="Name";
	public static final String IMG_DESC="Description";
	public static final String ACQTIME="Acquisition Time";
	public static final String DIMXY="Dim X x Y";
	public static final String PIXELTYPE="Pixel Depth";
	public static final String PIXELSIZE="Pixel Size (XY)";
	public static final String DIMZTC="Dim Z x T x C";
	public static final String STAGELABEL="Stage Label (XY)";//new label since 02.11.2016
	public static final String STAGEPOS="Stage Position (XY)"; //former label
	public static final String STEPSIZE="Step Size";
	public static final String TIMEINC="Time Increment";
	public static final String WELLNR="Well #";
	
	public static final ome.model.enums.UnitsLength STAGEPOS_UNIT=ome.model.enums.UnitsLength.REFERENCEFRAME;
	public static final ome.model.enums.UnitsLength PIXELSIZE_UNIT=ome.model.enums.UnitsLength.MICROMETER;
	public static final ome.model.enums.UnitsTime TIMEINC_UNIT=ome.model.enums.UnitsTime.SECOND;
	
	//--------------------------
	//	ImageEnv
	//--------------------------
	public static final String TEMP="Temperature";
	public static final String AIRPRESS="Air Pressure";
	public static final String HUMIDITY="Humidity %";
	public static final String CO2="CO2 Percent %";
	
	public static final ome.model.enums.UnitsTemperature TEMPERATURE_UNIT=ome.model.enums.UnitsTemperature.CELSIUS;
	public static final ome.model.enums.UnitsPressure PRESSURE_UNIT=ome.model.enums.UnitsPressure.MEGABAR;
	
	
	//--------------------------
	//	LightSrc
	//--------------------------
	//	MODEL ="Model";
	//	MANUFAC="Manufacturer";
	// 	TYPE
	public static final String L_TYPE="L_Type"; //only for profile/hardware xml
	public static final String A_TYPE="A_Type"; //only for profile/hardware xml
	public static final String F_TYPE="F_Type"; //only for profile/hardware xml
	
	public static final String POWER="Power";
	public static final String MEDIUM="Laser Medium";
	public static final String FREQMUL="Frequency Multiplication";
	public static final String TUNABLE="Tunable";
	public static final String PULSE="Pulse";
	public static final String POCKELCELL="Pockel Cell";
	public static final String REPRATE="Repititation Rate";
	public static final String PUMP="Pump";
	public static final String WAVELENGTH="Wavelength";
	
//	private final String L_DESC="Description";
	public static final String MAP="Map";
	
	public static final ome.model.enums.UnitsFrequency REPRATE_UNIT_HZ= ome.model.enums.UnitsFrequency.HERTZ;
	public static final ome.model.enums.UnitsFrequency REPRATE_UNIT_MHZ= ome.model.enums.UnitsFrequency.MEGAHERTZ;
	public static final ome.model.enums.UnitsLength WAVELENGTH_UNIT=ome.model.enums.UnitsLength.NANOMETER;
	public static final ome.model.enums.UnitsPower POWER_UNIT=ome.model.enums.UnitsPower.MILLIWATT;
	
	
	//LightSrcSettings
	public static final String SET_WAVELENGTH=PREFIX_SETTINGS+"Wavelength";
	public static final String ATTENUATION=PREFIX_SETTINGS+"Attenuation";
	
	
	//--------------------------
	//	Sample
	//--------------------------
	public static final String PREPDATE="Prep Date";
	public static final String PREPDESC="Prep Description";
	public static final String RAWCODE="Code";
	public static final String RAWDESC="Description";
	public static final String GRIDBOXNR="Gridbox Nr";
	public static final String GRIDBOXTYPE="Gridbox Type";
	public static final String EXPGRID="Grid (XY)";
	public static final String EXPOBJNR="Observed Object Nr";
	public static final String EXPOBJTYPE="Observed Object Type";
	
	
	//--------------------------
	//	LightPath
	//--------------------------
	public static final String FILTER = "Filter";
	public static final String FILTER_CLASS="Class";
	public static final String FILTER_CLASS_EM="Emission Filter";
	public static final String FILTER_CLASS_EX="Exitation Filter";
	public static final String FILTER_CLASS_D="Dichroic";
	
//	public static final String MODEL="Model";
//	public static final String MANUFAC="Manufacturer";
	public static final String LP_TYPE="FilterType";
	public static final String FILTERWHEEL="Filterwheel";
	public static final String TRANSRANGE_IN="CutIn";
	public static final String TRANSRANGE_OUT="CutOut";
	
	public static final ome.model.enums.UnitsLength TRANSRANGE_UNIT=ome.model.enums.UnitsLength.NANOMETER;
	
	//--------------------------
	//	Plane
	//--------------------------
	public static final String DELTA_T="Delta T";
	public static final String STAGE_POS_X="Stage Pos X";
	public static final String STAGE_POS_Y="Stage Pos Y";
	public static final String STAGE_POS_Z="Stage Pos Z";
	
	public static final ome.model.enums.UnitsTime DELTA_T_UNIT=ome.model.enums.UnitsTime.SECOND;
	public static final ome.model.enums.UnitsLength STAGE_POS_X_UNIT=ome.model.enums.UnitsLength.REFERENCEFRAME;
	public static final ome.model.enums.UnitsLength STAGE_POS_Y_UNIT=ome.model.enums.UnitsLength.REFERENCEFRAME;
	public static final ome.model.enums.UnitsLength STAGE_POS_Z_UNIT=ome.model.enums.UnitsLength.REFERENCEFRAME;

	
	public static String[] getParents(String type) {
		switch(type) {
		case OME_ELEM_IMAGE: return new String[] {OME_ROOT};
		case OME_ELEM_IMGENV: return new String[] {OME_ELEM_IMAGE};
		case OME_ELEM_CHANNEL: return new String[] {OME_ELEM_IMAGE};
		case OME_ELEM_OBJECTIVE:return new String[] {OME_ELEM_IMAGE};
		case OME_ELEM_DETECTOR:return new String[] {OME_ELEM_CHANNEL};
//		case OME_ELEM_FILTER:return new String[] {OME_ELEM_LIGHTPATH_FS,OME_ELEM_LIGHTPATH_EM,OME_ELEM_LIGHTPATH_EX};
		case OME_ELEM_FILTER:return new String[] {OME_ELEM_LIGHTPATH_EM,OME_ELEM_LIGHTPATH_EX};
		case OME_ELEM_LIGHTSOURCE:return new String[] {OME_ELEM_CHANNEL};
		case OME_ELEM_LASER:return new String[] {OME_ELEM_LIGHTSOURCE};
		case OME_ELEM_ARC:return new String[] {OME_ELEM_LIGHTSOURCE};
		case OME_ELEM_FILAMENT:return new String[] {OME_ELEM_LIGHTSOURCE};
		case OME_ELEM_LED:return new String[] {OME_ELEM_LIGHTSOURCE};
		case OME_ELEM_GENERICEXCITATIONSOURCE:return new String[] {OME_ELEM_LIGHTSOURCE};
//		case OME_ELEM_DICHROIC:return new String[] {OME_ELEM_LIGHTPATH_FS,OME_ELEM_LIGHTPATH_EM,OME_ELEM_LIGHTPATH_EX};
		case OME_ELEM_DICHROIC:return new String[] {OME_ELEM_LIGHTPATH_EM,OME_ELEM_LIGHTPATH_EX};
//		case OME_ELEM_LIGHTPATH_FS: return new String[] {OME_ELEM_LIGHTPATH};
		case OME_ELEM_LIGHTPATH:return new String[] {OME_ELEM_CHANNEL};
		case OME_ELEM_LIGHTPATH_EM:return new String[] {OME_ELEM_LIGHTPATH};
		case OME_ELEM_LIGHTPATH_EX:return new String[] {OME_ELEM_LIGHTPATH};
		case OME_ELEM_EXPERIMENT:return new String[] {OME_ROOT};
		default:
			return null;
		}
	}
	
	
	
//	public static PreTagData[] getDetectorTags()
//	{
//		return detectorTags;
//	}
	
//	public static PreTagData[] getSampleTags() {
//		return sampleTags;
//	}
//
//	
//
//	public static PreTagData[] getLightSrcTags()
//	{
//		return lightSrcTags;
//	}
//
//
//	public static PreTagData[] getObjectiveTags() {
//		return objectiveTags;
//	}
//
//	public static PreTagData[] getImageTags() {
//		return imageTags;
//	}
//
//
//	public static PreTagData[] getExperimentTags() {
//		return experimentTags;
//	}
//
//
//	public static PreTagData[] getChannelTags() {
//		return channelTags;
//	}

	public static String[] getAvailableUnitClasses() {
//		UNITS[] u= UNITS.values();
//		String[] result= new String[u.length];
//		for(int i=0; i<u.length; i++) {
//			UNITS us=u[i];
//			result[i]=us.name();
//		}
//		return result;
		return null;
	}
	
	public static String[] getUnits(String name) 
		{
			String[] units=null;
			switch (name) {
			case AIRPRESS:
				units=OMEValueConverter.getNames(UnitsPressure.class);
				break;
			case TEMP:
				units=OMEValueConverter.getNames(UnitsTemperature.class);
				break;
			case REPRATE:
				units=OMEValueConverter.getNames(UnitsFrequency.class);
				break;
			case POWER:
				units=OMEValueConverter.getNames(UnitsPower.class);
				break;
			case WAVELENGTH:
				units =OMEValueConverter.getNames(UnitsLength.class);
				break;
			case SET_WAVELENGTH:
				units =OMEValueConverter.getNames(UnitsLength.class);
				break;
			case WORKDIST:
				units =OMEValueConverter.getNames( UnitsLength.class);
				break;
			case VOLTAGE:
				units=OMEValueConverter.getNames(UnitsElectricPotential.class);
			default:
//				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] no unit available for "+name);
				break;
			}
			return units;
		}


	public static Object[] getUnitList(String name) 
	{
		Object[] units=null;
		switch (name) {
		case AIRPRESS:
			units=UnitsPressure.values();
			break;
		case TEMP:
			units=UnitsTemperature.values();
			break;
		case REPRATE:
			units=UnitsFrequency.values();
			break;
		case POWER:
			units=UnitsPower.values();
			break;
		case WAVELENGTH:
			units = UnitsLength.values();
			break;
		case SET_WAVELENGTH:
			units = UnitsLength.values();
			break;
		case WORKDIST:
			units = UnitsLength.values();
			break;
		case VOLTAGE:
			units=UnitsElectricPotential.values();
		default:
			break;
		}
		return units;
	}

	public static String[] getEnumerationVal(String name) 
	{
		String[] values=null;
		switch (name) {
		case IMMERSION:
			values= OMEValueConverter.getNames(Immersion.class);
			break;
		case CORRECTION:
			values=OMEValueConverter.getNames(Correction.class);
			break;
		case OBJ_MEDIUM:
			values=OMEValueConverter.getNames(Medium.class);
			break;
		case E_TYPE:
			values=OMEValueConverter.getNames(ExperimentType.class);
			break;
		case D_TYPE:
			values=OMEValueConverter.getNames(DetectorType.class);
			break;
		case LP_TYPE:
			values=OMEValueConverter.getNames(FilterType.class);
			break;
		case BINNING:
			values=OMEValueConverter.getNames(Binning.class);
			break;
		case L_TYPE:
			values=OMEValueConverter.getNames(LaserType.class);
			break;
		case A_TYPE:
			values=OMEValueConverter.getNames(ArcType.class);
			break;
		case F_TYPE:
			values=OMEValueConverter.getNames(FilamentType.class);
			break;
		case MEDIUM:
			values=OMEValueConverter.getNames(LaserMedium.class);
			break;
		case TUNABLE:
			values=BOOLEAN_COMBO;
			break;
		case PULSE:
			values=OMEValueConverter.getNames(Pulse.class);
			break;
		case POCKELCELL:
			values=BOOLEAN_COMBO;
			break;
		default:
			break;
		}
		
		if(values!=null)
			values=ObjectArrays.concat(new String[]{""}, values, String.class);
		
		return values;
	}
	
	// see https://docs.openmicroscopy.org/omero/5.4.0/developers/Model/Units.html
	public static final HashMap<String,ome.model.units.UnitEnum> omeUnitEnumsDef = new HashMap<String,ome.model.units.UnitEnum>() {{
		put(OME_ELEM_DETECTOR+"::"+VOLTAGE,VOLTAGE_UNIT);
		//settings::readoutRate
		put(OME_ELEM_OBJECTIVE+"::"+WORKDIST,WORKDIST_UNIT);
		put(OME_ELEM_CHANNEL+"::"+PINHOLESIZE,PINHOLESIZE_UNIT);
		put(OME_ELEM_CHANNEL+"::"+EXCITWAVELENGTH,EXCITATIONWL_UNIT);
		put(OME_ELEM_CHANNEL+"::"+EMISSIONWAVELENGTH,EMISSIONWL_UNIT);
		put(OME_ELEM_CHANNEL+"::"+EXPOSURETIME,EXPOSURETIME_UNIT);
		put(OME_ELEM_PLANE+"::"+STAGE_POS_X,STAGEPOS_UNIT);
		put(OME_ELEM_PLANE+"::"+STAGE_POS_Y,STAGEPOS_UNIT);
		put(OME_ELEM_PLANE+"::"+STAGE_POS_Z,STAGEPOS_UNIT);
		put(OME_ELEM_IMAGE+"::"+STAGEPOS,STAGEPOS_UNIT);
		put(OME_ELEM_IMAGE+"::"+STAGELABEL,STAGEPOS_UNIT);
		put(OME_ELEM_IMAGE+"::"+PIXELSIZE,PIXELSIZE_UNIT);
		put(OME_ELEM_IMAGE+"::"+TIMEINC,TIMEINC_UNIT);
		put(OME_ELEM_IMGENV+"::"+TEMP,TEMPERATURE_UNIT);
		put(OME_ELEM_IMGENV+"::"+AIRPRESS,PRESSURE_UNIT);
		put(OME_ELEM_LASER+"::"+REPRATE,REPRATE_UNIT_MHZ);
		put(OME_ELEM_LASER+"::"+WAVELENGTH,WAVELENGTH_UNIT);
		put(OME_ELEM_LASER+"::"+SET_WAVELENGTH,WAVELENGTH_UNIT);
		put(OME_ELEM_LED+"::"+SET_WAVELENGTH,WAVELENGTH_UNIT);
		put(OME_ELEM_ARC+"::"+SET_WAVELENGTH,WAVELENGTH_UNIT);
		put(OME_ELEM_FILAMENT+"::"+SET_WAVELENGTH,WAVELENGTH_UNIT);
		put(OME_ELEM_GENERICEXCITATIONSOURCE+"::"+SET_WAVELENGTH,WAVELENGTH_UNIT);
		put(OME_ELEM_LASER+"::"+POWER,POWER_UNIT);
		put(OME_ELEM_FILAMENT+"::"+POWER,POWER_UNIT);
		put(OME_ELEM_GENERICEXCITATIONSOURCE+"::"+POWER,POWER_UNIT);
		put(OME_ELEM_FILTER+"::"+TRANSRANGE_IN,TRANSRANGE_UNIT);
		put(OME_ELEM_FILTER+"::"+TRANSRANGE_OUT,TRANSRANGE_UNIT);
		put(OME_ELEM_PLANE+"::"+DELTA_T,DELTA_T_UNIT);
	}};
	
	
	public static ome.model.units.Unit getUnit(String unitSymbol){
		if(unitSymbol==null || unitSymbol.equals("")) {
			return null;
		}
		if(ome.model.enums.UnitsElectricPotential.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.ElectricPotential(0, ome.model.enums.UnitsElectricPotential.bySymbol(unitSymbol));
		}else if(ome.model.enums.UnitsPower.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.Power(0, ome.model.enums.UnitsPower.bySymbol(unitSymbol));
		}else if(ome.model.enums.UnitsFrequency.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.Frequency(0, ome.model.enums.UnitsFrequency.bySymbol(unitSymbol));
		}else if(ome.model.enums.UnitsPressure.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.Pressure(0, ome.model.enums.UnitsPressure.bySymbol(unitSymbol));
		}else if(ome.model.enums.UnitsLength.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.Length(0, ome.model.enums.UnitsLength.bySymbol(unitSymbol));
		}else if(ome.model.enums.UnitsTemperature.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.Temperature(0, ome.model.enums.UnitsTemperature.bySymbol(unitSymbol));
		}else if(ome.model.enums.UnitsTime.bySymbol(unitSymbol)!=null) {
			return new ome.model.units.Time(0, ome.model.enums.UnitsTime.bySymbol(unitSymbol));
		}
		return null;
	}
	public static Class getUnitClassFromSymbol(String unitSymbol) {
		ome.model.units.Unit unit=getUnit(unitSymbol);
		if(unit==null)
			return null;
		return unit.getClass();
	}
	public static Class getUnitClass(String className) {
		if(className.equals(ome.model.units.ElectricPotential.class.getName()))
			return ome.model.units.ElectricPotential.class;
		if(className.equals(ome.model.units.Power.class.getName()))
			return ome.model.units.Power.class;
		if(className.equals(ome.model.units.Frequency.class.getName()))
			return ome.model.units.Frequency.class;
		if(className.equals(ome.model.units.Pressure.class.getName()))
			return ome.model.units.Pressure.class;
		if(className.equals(ome.model.units.Length.class.getName()))
			return ome.model.units.Length.class;
		if(className.equals(ome.model.units.Temperature.class.getName()))
			return ome.model.units.Temperature.class;
		if(className.equals(ome.model.units.Time.class.getName()))
			return ome.model.units.Time.class;
		
		return null;
	}
	
	public static ome.model.units.UnitEnum getUnitEnum(String className,String symbol) {
		if(className.equals(ome.model.units.ElectricPotential.class.getName()))
			return ome.model.enums.UnitsElectricPotential.bySymbol(symbol);
		if(className.equals(ome.model.units.Power.class.getName()))
			return ome.model.enums.UnitsPower.bySymbol(symbol);
		if(className.equals(ome.model.units.Frequency.class.getName()))
			return ome.model.enums.UnitsFrequency.bySymbol(symbol);
		if(className.equals(ome.model.units.Pressure.class.getName()))
			return ome.model.enums.UnitsPressure.bySymbol(symbol);
		if(className.equals(ome.model.units.Length.class.getName()))
			return ome.model.enums.UnitsLength.bySymbol(symbol);
		if(className.equals(ome.model.units.Temperature.class.getName()))
			return ome.model.enums.UnitsTemperature.bySymbol(symbol);
		if(className.equals(ome.model.units.Time.class.getName()))
			return ome.model.enums.UnitsTime.bySymbol(symbol);
		
		return null;
	}
	public static String[] getUnitsList(String className) {
		if(className.equals(ome.model.units.ElectricPotential.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsElectricPotential.class);
		if(className.equals(ome.model.units.Power.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsPower.class);
		if(className.equals(ome.model.units.Frequency.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsFrequency.class);
		if(className.equals(ome.model.units.Pressure.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsPressure.class);
		if(className.equals(ome.model.units.Length.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsLength.class);
		if(className.equals(ome.model.units.Temperature.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsTemperature.class);
		if(className.equals(ome.model.units.Time.class.getName()))
			return OMEValueConverter.getUnitSymbols(ome.model.enums.UnitsTime.class);
		
		return null;
	}
	
}
