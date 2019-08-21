package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.ModuleConfiguration;

import ome.model.units.Unit;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.Filament;
import ome.xml.model.Filter;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.FilamentType;
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

public class OlympusLSMFV1000 extends MicroscopeProperties
{
		
	public OlympusLSMFV1000()
	{
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
		
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load OlympusLSMFV1000 view");
		view.setMicName(FLUOVIEW1000);
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
		l.setModel("LD 405");
//		l.setManufacturer("");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(50, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.AR);
		l.setWavelength(new Length(457, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(30, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(30, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Multiline Argon");
//		l.setManufacturer("");
		l.setType(LaserType.GAS);
		l.setLaserMedium(LaserMedium.AR);
		l.setWavelength(new Length(514, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(30, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LD 559");
//		l.setManufacturer("");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(559, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(15, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LD 635");
//		l.setManufacturer("");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(635, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(20, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LDH-P-C-440B");
		l.setManufacturer("PicoQuant");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(440, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.REPETITIVE);
		l.setRepetitionRate(new Frequency(40, UnitsFrequencyEnumHandler.getBaseUnit(UnitsFrequency.MEGAHERTZ)));
		l.setPower(new Power(5.0, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LDH-P-C-485");
		l.setManufacturer("PicoQuant");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(485, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.REPETITIVE);
		l.setRepetitionRate(new Frequency(40, UnitsFrequencyEnumHandler.getBaseUnit(UnitsFrequency.MEGAHERTZ)));
		l.setPower(new Power(2.0, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("D-TA-560");
		l.setManufacturer("PicoQuant");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.REPETITIVE);
		l.setRepetitionRate(new Frequency(80, UnitsFrequencyEnumHandler.getBaseUnit(UnitsFrequency.MEGAHERTZ)));
		l.setPower(new Power(0.5, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LDH-D-C-635");
		l.setManufacturer("PicoQuant");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(635, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPulse(Pulse.REPETITIVE);
		l.setRepetitionRate(new Frequency(80, UnitsFrequencyEnumHandler.getBaseUnit(UnitsFrequency.MEGAHERTZ)));
		l.setPower(new Power(2.5, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		Arc a=new Arc();
		a.setModel("U-LH100HG");
		a.setManufacturer("Olympus");
//		a.setType(ArcType.GAS);
		a.setPower(new Power(100,UnitsPowerEnumHandler.getBaseUnit(UnitsPower.WATT)));
		list.add(a);
		
		Filament f=new Filament();
		f.setModel("Halogen");
		f.setManufacturer("Olympus");
		f.setType(FilamentType.OTHER);
		f.setPower(new Power(100,UnitsPowerEnumHandler.getBaseUnit(UnitsPower.WATT)));
		
		
		return list;
	}



	@Override
	public List<Object> getMicLightPathFilterList() {
		
		
		return null;
	}



	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("ChS1");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		d=new Detector();
		d.setModel("ChS2");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		d=new Detector();
		d.setModel("Ch3");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		d=new Detector();
		d.setModel("ChT");
		d.setType(DetectorType.PMT);
		list.add(d);
		
		return list;
	}



	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		Objective o=new Objective();
		o.setModel("UPLSAPO 20x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(20.0);
		o.setCalibratedMagnification(20.0);
		o.setLensNA(0.75);
		o.setImmersion(Immersion.AIR);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(600, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		o=new Objective();
		o.setModel("UPLSAPO 40x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(40.0);
		o.setCalibratedMagnification(40.0);
		o.setLensNA(0.6);
		o.setImmersion(Immersion.AIR);
		o.setCorrection(Correction.PLANFLUOR);
		o.setWorkingDistance(new Length(4000, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		o=new Objective();
		o.setModel("UPLSAPO 60x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.2);
		o.setImmersion(Immersion.WATER);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(280, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		o=new Objective();
		o.setModel("UPLSAPO 60x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.35);
		o.setImmersion(Immersion.OIL);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(150, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		o=new Objective();
		o.setModel("UPLSAPO 100x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(100.0);
		o.setCalibratedMagnification(100.0);
		o.setLensNA(1.40);
		o.setImmersion(Immersion.OIL);
		o.setCorrection(Correction.APO);
		o.setWorkingDistance(new Length(100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);

		return list;
	}

	
	
}
