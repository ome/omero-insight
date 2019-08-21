package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.hardware;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.ModuleConfiguration;

import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;

public class ZeissCellObserverSD extends MicroscopeProperties
{
	
	public ZeissCellObserverSD(){
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load ZeissCellObserverSD view");
		view.setMicName(ZEISSSD);
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
		l.setModel("Diode laser 405nm");
		l.setManufacturer("Zeiss");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setTuneable(false);
		l.setPockelCell(false);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPower(new Power(50, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("DPSS laser 488nm");
		l.setManufacturer("Zeiss");
//		l.setType(LaserType.DPSS);
//		l.setLaserMedium(LaserMedium.SOLIDSTATE);
		l.setTuneable(false);
		l.setPockelCell(false);
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		 l=new Laser();
		l.setModel("Diode laser 561nm");
		l.setManufacturer("Zeiss");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setTuneable(false);
		l.setPockelCell(false);
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPower(new Power(40, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Diode laser 635nm");
		l.setManufacturer("Zeiss");
		l.setType(LaserType.SEMICONDUCTOR);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setTuneable(false);
		l.setPockelCell(false);
		l.setWavelength(new Length(635, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NANOMETER)));
		l.setPower(new Power(30, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MEGAWATT)));
		list.add(l);
		
		Arc a=new Arc();
		a.setModel("HXP 120 C");
		a.setManufacturer("Zeiss");
//		a.setType(ArcType.GAS);
		a.setPower(new Power(120,UnitsPowerEnumHandler.getBaseUnit(UnitsPower.WATT)));
		list.add(a);
		
		LightEmittingDiode d=new LightEmittingDiode();
		d.setModel("Vis-LED");
		d.setManufacturer("Zeiss");
		list.add(d);
		
		return list;
	}

	@Override
	protected List<Object> getMicLightPathFilterList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Detector> getMicDetectorList() {
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("Evolve 512");
		d.setManufacturer("Photometrics");
		d.setType(DetectorType.EMCCD);
		d.setZoom(1.0);
		d.setAmplificationGain(1.0);
		list.add(d);
		
		 d=new Detector();
			d.setModel("Evolve 512");
			d.setManufacturer("Photometrics");
			d.setType(DetectorType.EMCCD);
			d.setZoom(1.0);
			d.setAmplificationGain(1.0);
			list.add(d);
		
		return list;
	}

	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		Objective o=new Objective();
		o.setModel("Plan-Apochromat 40x");
		o.setManufacturer("Zeiss");
		o.setNominalMagnification(40.0);
		o.setCalibratedMagnification(40.0);
		o.setLensNA(1.4);
		o.setImmersion(Immersion.OIL);
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(0.13, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		o=new Objective();
		o.setModel("LCI Plan-Neofluor 63x");
		o.setManufacturer("Zeiss");
		o.setNominalMagnification(63.0);
		o.setCalibratedMagnification(63.0);
		o.setLensNA(1.3);
		o.setImmersion(Immersion.WATER); // and glycerin
		o.setCorrection(Correction.PLANNEOFLUAR);
		o.setWorkingDistance(new Length(0.17, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		o=new Objective();
		o.setModel("alpha Plan-Apochromat");
		o.setManufacturer("Zeiss");
		o.setNominalMagnification(63.0);
		o.setCalibratedMagnification(63.0);
		o.setLensNA(1.46);
		o.setImmersion(Immersion.OIL); // and glycerin
		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(0.1, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROMETER)));
		list.add(o);
		
		return list;
	}

	

}
