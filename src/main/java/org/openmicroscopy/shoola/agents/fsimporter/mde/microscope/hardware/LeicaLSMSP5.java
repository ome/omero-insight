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

public class LeicaLSMSP5 extends MicroscopeProperties 
{
	
	public LeicaLSMSP5() {
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load LeicaLSMSP5 view");
		view.setMicName(LEICASP5);
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
	protected List<LightSource> getMicLightSrcList() {
		List<LightSource> list=new ArrayList<>();
		
		Laser l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.AR);
		l.setWavelength(new Length(458, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(5, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.AR);
		l.setWavelength(new Length(476, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(5, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(20, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.AR);
		l.setWavelength(new Length(514, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(20, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("He-Ne 543 nm");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.HENE);
		l.setWavelength(new Length(543, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(1, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("He-Ne 633 nm");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.HENE);
		l.setWavelength(new Length(633, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(10, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		
		Arc a=new Arc();
		a.setModel("EL6000");
		a.setManufacturer("Leica");
//		a.setType(ArcType.GAS);
		a.setPower(new Power(120,UnitsPowerEnumHandler.getBaseUnit(UnitsPower.WATT)));
		list.add(a);
		
		Filament f=new Filament();
		f.setModel("Halogen");
		f.setManufacturer("Leica");
		f.setType(FilamentType.OTHER);
		f.setPower(new Power(100,UnitsPowerEnumHandler.getBaseUnit(UnitsPower.WATT)));
		
		
		return list;
	}

	@Override
	protected List<Object> getMicLightPathFilterList() {
		List<Object> list = new ArrayList<Object>();
		
		TransmittanceRange tR_340_380=new TransmittanceRange();
		tR_340_380.setCutIn(new Length(340,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		tR_340_380.setCutOut(new Length(380,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		
		TransmittanceRange tR_450_490=new TransmittanceRange();
		tR_450_490.setCutIn(new Length(450,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		tR_450_490.setCutOut(new Length(490,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		
		TransmittanceRange tR_515_560=new TransmittanceRange();
		tR_515_560.setCutIn(new Length(515,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		tR_515_560.setCutOut(new Length(560,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		
		TransmittanceRange tR_515=new TransmittanceRange();
		tR_515.setCutIn(new Length(515,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		
		TransmittanceRange tR_590=new TransmittanceRange();
		tR_590.setCutIn(new Length(590,UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		
		Filter ex1 = new Filter();
		ex1.setModel("BP 340/380");
		ex1.setType(FilterType.BANDPASS);
		ex1.setTransmittanceRange(tR_340_380);
		
		Filter ex2 = new Filter();
		ex2.setModel("BP 450/490");
		ex2.setType(FilterType.BANDPASS);
		ex2.setTransmittanceRange(tR_450_490);
		
		Filter ex3 = new Filter();
		ex3.setModel("BP 515/560");
		ex3.setType(FilterType.BANDPASS);
		ex3.setTransmittanceRange(tR_515_560);
		
		Filter em1 = new Filter();
		em1.setModel("BP 450/490");
		em1.setType(FilterType.BANDPASS);
		em1.setTransmittanceRange(tR_450_490);
		
		Filter em2 = new Filter();
		em2.setModel("LP 515");
		em2.setType(FilterType.LONGPASS);
		em2.setTransmittanceRange(tR_515);
		
		Filter em3 = new Filter();
		em3.setModel("LP 590");
		em3.setType(FilterType.LONGPASS);
		em3.setTransmittanceRange(tR_590);
		
		Dichroic d1= new Dichroic();
		d1.setModel("BS Mirror 400");
		
		Dichroic d2= new Dichroic();
		d2.setModel("BS Mirror 510");
		
		Dichroic d3= new Dichroic();
		d3.setModel("BS Mirror 580");
		
		
		FilterSet fs1= new FilterSet();
		fs1.setModel("FilterSet A");
		fs1.linkExcitationFilter(ex1);
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(em1);
		
		FilterSet fs2 = new FilterSet();
		fs2.setModel("FilterSet l3");
		fs2.linkExcitationFilter(ex2);
		fs2.linkDichroic(d2);
		fs2.linkEmissionFilter(em2);
		
		FilterSet fs3 = new FilterSet();
		fs3.setModel("FilterSet N2.1");
		fs3.linkExcitationFilter(ex3);
		fs3.linkDichroic(d3);
		fs3.linkEmissionFilter(em3);
		
		list.add(fs1);
		list.add(fs2);
		list.add(fs3);
		
		return list;
	}

	@Override
	protected List<Detector> getMicDetectorList() {
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("Ch1");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		d=new Detector();
		d.setModel("Ch2");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		d=new Detector();
		d.setModel("Ch3");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		d=new Detector();
		d.setModel("HyD1");
		d.setType(DetectorType.APD);
		list.add(d);
		
		d=new Detector();
		d.setModel("HyD2");
		d.setType(DetectorType.APD);
		list.add(d);
		
		d=new Detector();
		d.setModel("ChT");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		return list;
	}

	@Override
	protected List<Objective> getMicObjectiveList() 
	{
		List<Objective> list=new ArrayList<>();
		
		Objective o=new Objective();
		o.setModel("HC PL FL 10x/0.30 BD");
		o.setManufacturer("Leica");
		o.setNominalMagnification(10.0);
		o.setCalibratedMagnification(10.0);
		o.setLensNA(0.3);
		o.setImmersion(Immersion.AIR);
		o.setCorrection(Correction.OTHER);
		o.setWorkingDistance(new Length(11000, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		o=new Objective();
		o.setModel("HC PL APO CS 20x/0.70");
		o.setManufacturer("Leica");
		o.setNominalMagnification(20.0);
		o.setCalibratedMagnification(20.0);
		o.setLensNA(0.7);
		o.setImmersion(Immersion.AIR);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(620, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		
		o=new Objective();
		o.setModel("HCX PL APO CS 100x/0.7-1.4");
		o.setManufacturer("Leica");
		o.setNominalMagnification(100.0);
		o.setCalibratedMagnification(100.0);
//		o.setLensNA(0.7-1.4);
		o.setImmersion(Immersion.OIL);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(130, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		o.setIris(true);
		list.add(o);
		
		
		o=new Objective();
		o.setModel("HCX PL APO CS 40x/0.75-1.25");
		o.setManufacturer("Leica");
		o.setNominalMagnification(40.0);
		o.setCalibratedMagnification(40.0);
//		o.setLensNA(0.7-1.25);
		o.setImmersion(Immersion.OIL);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(220, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		o.setIris(true);
		list.add(o);
		
		
		
		
		return list;
	}

	
}
