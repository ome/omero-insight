package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.ModuleConfiguration;

import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Laser;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;

public class LatticeScope extends MicroscopeProperties {

	private Detector CAM_B;
	private Detector CAM_A;
	
	private Objective obj_CFI_75;
	private Objective obj_54_10;
	
	private Laser LA_405;
	private Laser LA_445;
	private Laser LA_488;
	private Laser LA_532;
	private Laser LA_560;
	private Laser LA_589;
	private Laser LA_642;
	
	public LatticeScope() {
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load LatticeScope view");
		view.setMicName(LATTICESCOPE);
//		view.setMicDesc(" -- Contains: automatic Detector mapping;");
		view.setImageConf(getImageConf());
    	
		view.setObjConf(getObjectiveConf());
		view.setDetectorConf(getDetectorConf());
		view.setLightSrcConf(getLightSrcConf());
		view.setChannelConf(getChannelConf());
		view.setLightPathConf(getLightPathConf());
		view.setSampleConf(getSampleConf());
		view.setExperimenterConf(getExperimentConf());
		view.setImgEnvConf(getImageEnvConf());
		
	}
	
	@Override
	public List<LightSource> getMicLightSrcList() {
		List<LightSource> list=new ArrayList<>();
		
		LA_405=new Laser();
		LA_405.setModel("LBX-405-300-CSB-PP");
		LA_405.setManufacturer("Oxxius");
		LA_405.setType(LaserType.SEMICONDUCTOR);
		LA_405.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_405.setPower(new Power(300, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_405);
		
		LA_445=new Laser();
		LA_445.setModel("LBX-445-100-CSB-PP");
		LA_445.setManufacturer("Oxxius");
		LA_445.setType(LaserType.SEMICONDUCTOR);
		LA_445.setWavelength(new Length(445, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_445.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_445);
		
		LA_488=new Laser();
		LA_488.setModel("2RU-VFL-P-300-488-B1R");
		LA_488.setManufacturer("MPB Communications");
		LA_488.setType(LaserType.OTHER);
		LA_488.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_488.setPower(new Power(300, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_488);
		
		LA_532=new Laser();
		LA_532.setModel("2RU-VFL-P-500-532-B1R");
		LA_532.setManufacturer("MPB Communications");
		LA_532.setType(LaserType.OTHER);
		LA_532.setWavelength(new Length(532, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_532.setPower(new Power(500, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_532);
		
		LA_560=new Laser();
		LA_560.setModel("2RU-VFL-P-2000-560-B1R");
		LA_560.setManufacturer("MPB Communications");
		LA_560.setType(LaserType.OTHER);
		LA_560.setWavelength(new Length(560, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_560.setPower(new Power(2000, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_560);
		
		LA_589=new Laser();
		LA_589.setModel("2RU-VFL-P-500-589-B1R");
		LA_589.setManufacturer("MPB Communications");
		LA_589.setType(LaserType.OTHER);
		LA_589.setWavelength(new Length(589, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_589.setPower(new Power(500, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_589);
		
		LA_642=new Laser();
		LA_642.setModel("2RU-VFL-P-2000-642-B1R");
		LA_642.setManufacturer("MPB Communications");
		LA_642.setType(LaserType.OTHER);
		LA_642.setWavelength(new Length(642, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		LA_642.setPower(new Power(2000, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(LA_642);
		
		return list;
	}
	
	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		CAM_A=new Detector();
		CAM_A.setModel("ORCAFlash 4.0 V2");
		CAM_A.setManufacturer("Hamamatsu");
		CAM_A.setType(DetectorType.CMOS);
		list.add(CAM_A);
		
		CAM_B=new Detector();
		CAM_B.setModel("ORCAFlash 4.0 V3");
		CAM_B.setManufacturer("Hamamatsu");
		CAM_B.setType(DetectorType.CMOS);
		list.add(CAM_B);
		
		
		
		return list;
	}
	
	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		obj_CFI_75=new Objective();
		obj_CFI_75.setModel("CFI-75 Apo 25x W MP");
		obj_CFI_75.setManufacturer("Nikon");
//		o.setNominalMagnification(20.0);
		obj_CFI_75.setLensNA(1.1);
		obj_CFI_75.setImmersion(Immersion.WATERDIPPING);
		obj_CFI_75.setCorrection(Correction.PLANAPO);
		obj_CFI_75.setWorkingDistance(new Length(2, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MILLIMETER)));
		list.add(obj_CFI_75);
		
		obj_54_10=new Objective();
		obj_54_10.setModel("54-10-7@488-910");
		obj_54_10.setManufacturer("Special Optics");
		obj_54_10.setNominalMagnification(28.6);
		obj_54_10.setLensNA(0.66);
		obj_54_10.setImmersion(Immersion.WATERDIPPING);
//		o.setCorrection(Correction.PLANAPO);
		obj_54_10.setWorkingDistance(new Length(3.74, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MILLIMETER)));
		list.add(obj_54_10);
		
		return list;
	}
	@Override
	protected List<Object> getMicLightPathFilterList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//---- Panel configuration methods ------//
	
	@Override
	protected ModuleConfiguration loadImageConf(boolean active)
	{
		ModuleConfiguration imageConf=new ModuleConfiguration(active);
		imageConf.setTag(TagNames.IMG_NAME,null,null,true, null, true);
		imageConf.setTag(TagNames.IMG_DESC,null,null,true, null, true);
		imageConf.setTag(TagNames.ACQTIME,null,null,true, null, true);
		imageConf.setTag(TagNames.DIMXY,null,null,true, null, true);
		imageConf.setTag(TagNames.PIXELTYPE,null,null,true, null, true);
		imageConf.setTag(TagNames.PIXELSIZE,null,null,true, null, true);
		imageConf.setTag(TagNames.DIMZTC,null,null,true, null, true);
		imageConf.setTag(TagNames.STAGELABEL,null,null,true, null, false);
		imageConf.setTag(TagNames.STEPSIZE,null,null,true, null, true);
		imageConf.setTag(TagNames.TIMEINC,null,null,true, null, true);
		imageConf.setTag(TagNames.WELLNR,null,null,true, null, false);
		
		return imageConf;
	}
	
	@Override
	protected  ModuleConfiguration loadObjectiveConf(boolean active){
		ModuleConfiguration oConf=new ModuleConfiguration(active);
		oConf.setTag(TagNames.MODEL,null,null,true, null, true);
		oConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		oConf.setTag(TagNames.NOMMAGN,null,null,true, null, false);
		oConf.setTag(TagNames.CALMAGN,null,null,true, null, true);
		oConf.setTag(TagNames.LENSNA,null,null,true, null, true);
		oConf.setTag(TagNames.IMMERSION,null,null,true, null, true);
		oConf.setTag(TagNames.CORRECTION,null,null,true, null, true);
		oConf.setTag(TagNames.WORKDIST,null,TagNames.WORKDIST_UNIT.getSymbol(),true, null, true);
		
		oConf.setSettingTag(TagNames.CORCOLLAR,null,null,true, null, false);
		oConf.setSettingTag(TagNames.OBJ_MEDIUM,null,null,true, null, false);
		oConf.setSettingTag(TagNames.REFINDEX,null,null,true, null, false);
		return oConf;
	}
	
	@Override
	protected  ModuleConfiguration loadDetectorConf(boolean active){
		ModuleConfiguration detectorConf=new ModuleConfiguration(active);
		detectorConf.setTag(TagNames.MODEL,null,null,true, null, true);
		detectorConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		detectorConf.setTag(TagNames.D_TYPE,null,null,true, null, true);
		detectorConf.setTag(TagNames.ZOOM,null,null,true, null, false);
		detectorConf.setTag(TagNames.AMPLGAIN,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.GAIN,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.VOLTAGE,null,TagNames.VOLTAGE_UNIT.getSymbol(),true, null, false);
		detectorConf.setSettingTag(TagNames.OFFSET,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.CONFZOOM,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.BINNING,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.SUBARRAY,null,null,true, null, true);
		return detectorConf;
	}
	
	@Override
	protected  ModuleConfiguration loadLightSrcConf(boolean active){
		// laser module for lightSrc
		ModuleConfiguration lightSrcConf=new ModuleConfiguration(active);
		lightSrcConf.setTag(TagNames.MODEL,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.POWER,null,TagNames.POWER_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setTag(TagNames.L_TYPE,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.MEDIUM,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.FREQMUL,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.TUNABLE,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.PULSE,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.POCKELCELL,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.REPRATE,null,TagNames.REPRATE_UNIT_HZ.getSymbol(),true, null, false);
		lightSrcConf.setTag(TagNames.PUMP,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setSettingTag(TagNames.SET_WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setSettingTag(TagNames.ATTENUATION,null,null,true, null, true);
		return lightSrcConf;
	}	
	
	@Override
	protected ModuleConfiguration loadChannelConf(boolean active) {
		String[] ILLUMTYPES={"Scanned Gaussian Beam","Scanned Bessel Beam","Lattice Light-Sheet","Dithered Lattice-Light-Sheet"};
		String[] CONTRASTMETHOD={"Fluorescence"};
		String[] IMAGINGMODES={"SPIM","SIM","SingleMoleculeImaging","PALM","STORM","Other"};
		
		ModuleConfiguration channelConf=new ModuleConfiguration(active);
		channelConf.setTag(TagNames.CH_NAME,null,null,true, null, true);
		channelConf.setTag(TagNames.COLOR,null,null,true, null, false);
		channelConf.setTag(TagNames.FLUOROPHORE,null,null,true, null, true);
		channelConf.setTag(TagNames.ILLUMTYPE,null,null,true, ILLUMTYPES, true);
		channelConf.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EMISSIONWAVELENGTH,null,null,true, null, true);
		channelConf.setTag(TagNames.IMAGINGMODE,null,null,true, IMAGINGMODES, true);
		channelConf.setTag(TagNames.CONTRASTMETHOD,null,null,true, CONTRASTMETHOD, true);
		channelConf.setTag(TagNames.NDFILTER,null,null,true, null, false);
		channelConf.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),true, null, false);
		return channelConf;
	}

	
	
	
	//---- MAPR ----------------------------//
	@Override
	public HashMap getMapr(){
		HashMap map=new HashMap<>();
		//detector
//		map.put("CamA", CAM_A);
//		map.put("CamB",CAM_B);
		//objective
//		map.put("CFI-75 Apo 25x W MP", obj_CFI_75);
//		map.put("54-10-7@488-910", obj_54_10);
		
		//lightsrc
//		map.put("405.0",LA_405);
//		map.put("445.0",LA_445);
//		map.put("488.0",LA_488);
//		map.put("532.0",LA_532);
//		map.put("560.0",LA_560);
//		map.put("589.0",LA_589);
//		map.put("642.0",LA_642);
		
		return map;
	}
	
}
