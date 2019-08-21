package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.ModuleConfiguration;

import ome.model.units.UNITS;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filament;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.TransmittanceRange;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.enums.UnitsFrequency;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.handlers.UnitsFrequencyEnumHandler;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;

public class OlympusTIRF4Line_STORM extends MicroscopeProperties
{
	public OlympusTIRF4Line_STORM()
	{
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load OlympusTIRF4Line_STORM view");
		view.setMicName(TIRF4LINE_STORM);
		view.setMicDesc(" ");
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
		
		Laser l=new Laser();
		l.setModel("BCL-100-405");
		l.setManufacturer("CrystaLaser");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LuxX 488-200");
		l.setManufacturer("Omicron");
		l.setType(LaserType.SEMICONDUCTOR);//??
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(200, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Cobolt Samba 532");
		l.setManufacturer("Cobolt");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(532, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(150, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Cobolt Jive 561");
		l.setManufacturer("Cobolt");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(200, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Luxx 642-140");
		l.setManufacturer("Omicron");
		l.setType(LaserType.OTHER);//??
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(642, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(140, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("iFLEX2000");
		l.setManufacturer("Qiotiq");
		l.setType(LaserType.OTHER);//??
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(730, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(40, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		
		return list;
	}



	@Override
	public List<Object> getMicLightPathFilterList() {
List<Object> list = new ArrayList<Object>();
		
		// TODO:
		//- cutIn, CutOut richtig?
		//-polarizer?		
		
		Dichroic d1= new Dichroic();
		d1.setModel("488nm");
		
		FilterSet fs1= new FilterSet();
		fs1.setModel("Pos.1");
		//TODO
//		fs1.linkExcitationFilter(
//				getFilter("No Filter",FilterType.OTHER,488,-1,UnitsLength.NANOMETER));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("HQ 570/30",FilterType.BANDPASS,555,585,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		
		d1= new Dichroic();
		d1.setModel("zt405/488/561/640/730rpc");
		d1.setManufacturer("Semrock");
		
		fs1= new FilterSet();
		fs1.setModel("Pos.3: pentacolor(bkue, green, yellow/orange,red, dark red");
		//TODO
//		fs1.linkExcitationFilter(
//				getFilter("No Filter",FilterType.OTHER,488,-1,UnitsLength.NANOMETER));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 440/521/607/694/809",FilterType.BANDPASS,440,809,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		
		d1= new Dichroic();
		d1.setModel("zt405/488/561/640rpc");
		
		fs1= new FilterSet();
		fs1.setModel("Pos.4: quadcolor (blue,green,yellow/orange,red)");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("BrightLine HC 390/482/563/640",FilterType.BANDPASS,390,640,UnitsLength.NANOMETER, null));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 446/523/500/677",FilterType.BANDPASS,446,677,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		
		d1= new Dichroic();
		d1.setModel("HC BS R488/561");
		
		fs1= new FilterSet();
		fs1.setModel("Pos.5: dualcolor (green, orange)");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("BrightLine HC 482/563",FilterType.BANDPASS,482,563,UnitsLength.NANOMETER, null));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 523/610",FilterType.BANDPASS,523,610,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		d1= new Dichroic();
		d1.setModel("zt532/642rpc");
		
		 fs1= new FilterSet();
		fs1.setModel("Pos.6: Cy3 + Cy5");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("HC 527/645",FilterType.BANDPASS,527,645,UnitsLength.NANOMETER, null));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("EdgeBasic LP 532",FilterType.LONGPASS,532,-1,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("ZET647NF",FilterType.OTHER,647,-1,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Cube 1:blue, green, yellow/orange, red");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 480dcxr",FilterType.LONGPASS,480,-1,UnitsLength.NANOMETER, null));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 565dcxr",FilterType.LONGPASS,565,-1,UnitsLength.NANOMETER, null));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 640dcxr",FilterType.LONGPASS,640,-1,UnitsLength.NANOMETER, null));
		
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 438/24 (blue:DAPI,BFP)",FilterType.BANDPASS,426,450,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 520/35 (green: GFP)",FilterType.BANDPASS,502,538,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 600/37 (orange: TMR,mCherry)",FilterType.BANDPASS,582,619,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 685/40 (red: Cy5,Atto 655)",FilterType.BANDPASS,665,705,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Cube 2:green, green-orange, red, dark red");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter T565LPXR",FilterType.LONGPASS,565,-1,UnitsLength.NANOMETER, null));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 630DCXR",FilterType.LONGPASS,630,-1,UnitsLength.NANOMETER, null));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 735DCXR",FilterType.LONGPASS,735,-1,UnitsLength.NANOMETER, null));
		
		
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 520/35 (green: GFP, Cy2)",FilterType.BANDPASS,502,538,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("ET 600/50 (green-orange: TMR,Cy3)",FilterType.BANDPASS,544,620,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("ET 685/50 (red: Cy5,Atto 655)",FilterType.BANDPASS,660,710,UnitsLength.NANOMETER, null));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 809/81 (dark-red: Cy7)",FilterType.BANDPASS,769,850,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		
		
		return list;
	}



	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("Andor iXon Ultra 897");
		d.setManufacturer("Andor");
		d.setType(DetectorType.CCD);
		list.add(d);
		
		
		
		return list;
	}



	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		Objective o=new Objective();
		o.setModel("UPLAFLN 20x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(20.0);
		o.setCalibratedMagnification(20.0);
		o.setLensNA(0.5);
		o.setImmersion(Immersion.AIR);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(2100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		o=new Objective();
		o.setModel("PLAPON 60x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.42);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANFLUOR);
		o.setWorkingDistance(new Length(150, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		o=new Objective();
		o.setModel("UAPON 150x TIRF");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(150.0);
		o.setCalibratedMagnification(150.0);
		o.setLensNA(1.45);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(80, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		
		return list;
	}
	@Override
	public ModuleConfiguration loadLightSrcConf(boolean active) {
		ModuleConfiguration lightSrcConf=new ModuleConfiguration(active);
		lightSrcConf.setTag(TagNames.MODEL,null,null,true, null, false);
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
		lightSrcConf.setSettingTag(TagNames.SET_WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, false);
		lightSrcConf.setSettingTag(TagNames.ATTENUATION,null,null,true, null, true);
		return lightSrcConf;
	}
	@Override
	public ModuleConfiguration loadDetectorConf(boolean active) {
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
	protected ModuleConfiguration loadChannelConf(boolean active) {
		String[] ILLUMINATIONTYPE_TIRF4LINE_SMT={"Transmitted","Epifluorescence","TIR","HILO"};
		String[] CONTRASTMETHOD={"Brightfield","DIC","Fluorescence"};
		String[] IMAGINGMODE={"Widefield","TIRF","SingleMoleculeImaging","PALM","STORM","Other"};


		ModuleConfiguration channelConf=new ModuleConfiguration(active);
		channelConf.setTag(TagNames.CH_NAME,null,null,true, null, true);
		channelConf.setTag(TagNames.CONTRASTMETHOD,null,null,true, CONTRASTMETHOD, true);
		channelConf.setTag(TagNames.FLUOROPHORE,null,null,true, null, true);
		channelConf.setTag(TagNames.ILLUMTYPE,null,null,true, ILLUMINATIONTYPE_TIRF4LINE_SMT, true);
		channelConf.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EMISSIONWAVELENGTH,null,null,true, null, true);
		channelConf.setTag(TagNames.IMAGINGMODE,null,null,true, IMAGINGMODE, true);
		channelConf.setTag(TagNames.NDFILTER,null,null,true, null, false);
		channelConf.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),true, null, false);

		return channelConf;
	}


	@Override
	protected  ModuleConfiguration loadSampleConf(boolean active){
		ModuleConfiguration sampleConf=new ModuleConfiguration(active);
		sampleConf.setTag(TagNames.PREPDATE,null,null,true, null, true);
		sampleConf.setTag(TagNames.PREPDESC,null,null,true, null, true);
		sampleConf.setTag(TagNames.RAWCODE,null,null,true, null, false);
		sampleConf.setTag(TagNames.RAWDESC,null,null,true, null, true);
		sampleConf.setTag(TagNames.GRIDBOXNR,null,null,true, null, false);
		sampleConf.setTag(TagNames.GRIDBOXTYPE,null,null,true, null, true);
		sampleConf.setTag(TagNames.EXPGRID,null,null,true, null, false);
		sampleConf.setTag(TagNames.EXPOBJNR,null,null,true, null, false);
		sampleConf.setTag(TagNames.EXPOBJTYPE,null,null,true, null, false);
		return sampleConf;
	}

	@Override
	protected  ModuleConfiguration loadImageEnvConf(boolean active){
		ModuleConfiguration imgEnvConf=new ModuleConfiguration(active);
		imgEnvConf.setTag(TagNames.TEMP,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.AIRPRESS,null,null,true, null, false);
		imgEnvConf.setTag(TagNames.HUMIDITY,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.CO2,null,null,true, null, true);
		return imgEnvConf;
	}
	@Override
	protected  ModuleConfiguration loadObjectiveConf(boolean active){
		ModuleConfiguration oConf=new ModuleConfiguration(active);
		oConf.setTag(TagNames.MODEL,null,null,true, null, true);
		oConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		oConf.setTag(TagNames.NOMMAGN,null,null,true, null, true);
		oConf.setTag(TagNames.CALMAGN,null,null,true, null, false);
		oConf.setTag(TagNames.LENSNA,null,null,true, null, true);
		oConf.setTag(TagNames.IMMERSION,null,null,true, null, true);
		oConf.setTag(TagNames.CORRECTION,null,null,true, null, true);
		oConf.setTag(TagNames.WORKDIST,null,TagNames.WORKDIST_UNIT.getSymbol(),true, null, true);

		oConf.setSettingTag(TagNames.CORCOLLAR,null,null,true, null, true);
		oConf.setSettingTag(TagNames.OBJ_MEDIUM,null,null,true, null, true);
		oConf.setSettingTag(TagNames.REFINDEX,null,null,true, null, true);
		return oConf;
	}

	
}
