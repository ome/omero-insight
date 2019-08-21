package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.CustomViewProperties;
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

public class OlympusTIRF3Line extends MicroscopeProperties
{
	public OlympusTIRF3Line()
	{
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load OlympusTIRF3Line view");
		view.setMicName(TIRF3LINE);
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
		
		//TODO: LaserMedium?
		
		Laser l=new Laser();
		l.setModel("BCL-100-405");
//		l.setManufacturer("");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Sapphire LP");
//		l.setManufacturer("");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(200, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Compass 215M.75");
//		l.setManufacturer("");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(532, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(75, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("CL-561-200");
//		l.setManufacturer("");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(200, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Luxx 642-140");
//		l.setManufacturer("");
		l.setType(LaserType.OTHER);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(642, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(140, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MILLIWATT)));
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
		d1.setModel("z488rdc");
		
		FilterSet fs1= new FilterSet();
		fs1.setModel("Pos.1: green");
		fs1.linkExcitationFilter(
				getFilter("No Filter",FilterType.OTHER,488,-1,UnitsLength.NANOMETER, null));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 531/40",FilterType.BANDPASS,511,551,UnitsLength.NANOMETER, null));
		list.add(fs1);
		
		Dichroic d2= new Dichroic();
		d2.setModel("zt532/642rpc");
		
		FilterSet fs2= new FilterSet();
		fs2.setModel("Pos.2: dualcolor (green-yellow,red)");
		fs2.linkExcitationFilter(
				getFilter("HC 527/645",FilterType.BANDPASS,527,-1,UnitsLength.NANOMETER, null));
		fs2.linkDichroic(d1);
		fs2.linkEmissionFilter(
				getFilter("Edge Basic 532 LP, zet635nf",FilterType.LONGPASS,532,-1,UnitsLength.NANOMETER, null));
		list.add(fs2);
		
		Dichroic d3= new Dichroic();
		d3.setModel("HC BS R532");
		
		FilterSet fs3= new FilterSet();
		fs3.setModel("Pos.3");
		fs3.linkDichroic(d3);
		fs3.linkEmissionFilter(
				getFilter("ET 700/75M",FilterType.BANDPASS,662,738,UnitsLength.NANOMETER, null));
		list.add(fs3);
		
		Dichroic d4= new Dichroic();
		d4.setModel("zt405/488/561/640rpc");
		
		FilterSet fs4= new FilterSet();
		fs4.setModel("Pos. 4: quadcolor imaging (blue, green, orange, red) ");
		fs4.linkDichroic(d4);
		fs4.linkEmissionFilter(
				getFilter("BrightLine HC 446/523/600/677",FilterType.BANDPASS,446,-1,UnitsLength.NANOMETER, null));
		list.add(fs4);
		
		
		Dichroic d5= new Dichroic();
		d5.setModel("zt 405/488/561rpc");
		FilterSet fs5= new FilterSet();
		fs5.setModel("Pos. 5: dualcolor imaging (green, orange) plus photomanipulation with 405 nm  ");
		fs5.linkDichroic(d5);
		fs5.linkEmissionFilter(
				getFilter("BrightLine HC 523/610",FilterType.BANDPASS,523,-1,UnitsLength.NANOMETER, null));
		list.add(fs5);
		
		Dichroic d6= new Dichroic();
		d6.setModel("BrightLine HC BS R561");
		FilterSet fs6= new FilterSet();
		fs6.setModel("Pos. 6: orange ");
		fs6.linkDichroic(d6);
		fs6.linkEmissionFilter(
				getFilter("BrightLine HC 607/36",FilterType.BANDPASS,589,625,UnitsLength.NANOMETER, null));
		list.add(fs6);
		
		//DualView filter cubes:
		Dichroic d7= new Dichroic();
		d7.setModel("505 dcxr");
		FilterSet fs7= new FilterSet();
		fs7.setModel("CFP/YFP");
		fs7.linkDichroic(d7);
		fs7.linkEmissionFilter(
				getFilter("BrightLine HC 460/30",FilterType.BANDPASS,450,480,UnitsLength.NANOMETER, null));
		fs7.linkEmissionFilter(
				getFilter("HQ 535/30",FilterType.BANDPASS,520,550,UnitsLength.NANOMETER, null));
		list.add(fs7);
		
		Dichroic d8= new Dichroic();
		d8.setModel("585 dcxu");
		FilterSet fs8= new FilterSet();
		fs8.setModel("green/orange");
		fs8.linkDichroic(d8);
		fs8.linkEmissionFilter(
				getFilter("HQ 525/50",FilterType.BANDPASS,500,550,UnitsLength.NANOMETER, null));
		fs8.linkEmissionFilter(
				getFilter("BrightLine HC 620/52",FilterType.BANDPASS,594,646,UnitsLength.NANOMETER, null));
		list.add(fs8);
		
		Dichroic d9= new Dichroic();
		d9.setModel("640 dcxr");
		FilterSet fs9= new FilterSet();
		fs9.setModel("orange/red");
		fs9.linkDichroic(d9);
		fs9.linkEmissionFilter(
				getFilter("BrightLine HC 600/37",FilterType.BANDPASS,582,619,UnitsLength.NANOMETER, null));
		fs9.linkEmissionFilter(
				getFilter("HQ 690/70",FilterType.BANDPASS,655,725,UnitsLength.NANOMETER, null));
		list.add(fs9);
		
		Dichroic d10= new Dichroic();
		d10.setModel("Polarizing beamsplitter");
		FilterSet fs10= new FilterSet();
		fs10.setModel("fluorescence anisotropy imaging");
		fs10.linkDichroic(d10);
//		fs10.linkEmissionFilter(
//				getFilter("Polarizer",FilterType.BANDPASS,...,...,UnitsLength.NANOMETER));
//		fs10.linkEmissionFilter(
//				getFilter("Polarizer",FilterType.BANDPASS,...,...,UnitsLength.NANOMETER));
		list.add(fs10);
		
		
		
		return list;
	}



	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		//TODO: zoom, amplificationGain?
		Detector d=new Detector();
		d.setModel("Andor iXon3 897 Single Photon Detection");
		d.setType(DetectorType.EBCCD);
//		d.setZoom(...);
//		d.setAmplificationGain(...);
		list.add(d);
		
		
		
		return list;
	}



	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		//TODO:correction, wavelength? 
		
		Objective o=new Objective();
		o.setModel("LUMPLFLN 40x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(40.0);
		o.setCalibratedMagnification(40.0);
		o.setLensNA(0.8);
		o.setImmersion(Immersion.WATER);
//		o.setCorrection(Correction.PLANAPO);
//		o.setWorkingDistance(new Length(600, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		o=new Objective();
		o.setModel("PLAPON 60x TIRF");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.42);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANFLUOR);
//		o.setWorkingDistance(new Length(4000, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		o=new Objective();
		o.setModel("UAPON 150x TIRF");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(150.0);
		o.setCalibratedMagnification(150.0);
		o.setLensNA(1.45);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANAPO);
//		o.setWorkingDistance(new Length(280, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		

		return list;
	}
	
	
}
