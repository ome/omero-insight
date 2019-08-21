package org.openmicroscopy.shoola.agents.fsimporter.mde.microscope;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.Dichroic;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.slf4j.LoggerFactory;


public class CustomViewProperties
{

	/** Logger for this class. */
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(CustomViewProperties.class);

    private String micName;
    private String micDesc;

	static enum MicSubmodule
	{
		IMAGE_DATA,
		OBJECTIVE_DATA,
		DETECTOR_DATA,
		LIGHTSOURCE_DATA,
		CHANNEL_DATA,
		LIGHTPATH_DATA,
		EXPERIMENT_DATA,
		SAMPLE_DATA,
		IMAGEENVIRONMENT_DATA,
		PLANE_DATA
	}

//	private List<Submodule> moduleList;

	private ModuleConfiguration oConf;
	private ModuleConfiguration detectorConf;
	private ModuleConfiguration lightSrcConf;
	private ModuleConfiguration imageConf;
	private ModuleConfiguration channelConf;
	private ModuleConfiguration lightPathConf;
	private ModuleConfiguration sampleConf;
	private ModuleConfiguration expConf;
	private ModuleConfiguration imgEnvConf;
	private ModuleConfiguration planeConf;

	private boolean init;

	private List<Objective> micObjList;
	private List<Detector> micDetectorList;
	private List<LightSource> micLightSrcList;
	private List<Object> micLightPathFilterList;
	private List<Filter> micFilterList;
	private List<Dichroic> micDichroicList;

	private File file;

	private HashMap mapper;

	

	public CustomViewProperties()
	{
		micName="Unspecified";
		micDesc=null;
		init=false;
//		moduleList=new ArrayList<Submodule>();
		
	}

	public void init()
	{
		if(!init)
			loadCommonView();
	}


	public void setObjConf(ModuleConfiguration conf)
	{
		oConf=conf;
		init=true;
	}
	public void setImageConf(ModuleConfiguration conf) {
		imageConf=conf;
		init=true;
	}

	public void setChannelConf(ModuleConfiguration conf) {
		channelConf=conf;
		init=true;
	}

	public void setExperimenterConf(ModuleConfiguration conf) {
		expConf = conf;
		init=true;
	}

	public void setDetectorConf(ModuleConfiguration conf) {
		detectorConf=conf;
		init=true;
	}

	public void setLightPathConf(ModuleConfiguration conf) {
		lightPathConf=conf;
		init=true;
	}

	public void setLightSrcConf(ModuleConfiguration conf) {
		lightSrcConf=conf;
		init=true;
	}

	public void setSampleConf(ModuleConfiguration conf) {
		sampleConf=conf;
		init=true;
	}

	public void setPlaneConf(ModuleConfiguration conf) {
		planeConf=conf;
		init=true;
	}

	public void setImgEnvConf(ModuleConfiguration conf) {
		imgEnvConf=conf;
		init=true;
	}

	public ModuleConfiguration getObjConf()
	{
		return oConf;
	}

	public ModuleConfiguration getExpConf() {
		return expConf;
	}


	public ModuleConfiguration getDetectorConf() {
		return detectorConf;
	}

	public ModuleConfiguration getLightSrcConf() {
		return lightSrcConf;
	}

	public ModuleConfiguration getImageConf() {
		return imageConf;
	}

	public ModuleConfiguration getChannelConf() {
		return channelConf;
	}

	public ModuleConfiguration getLightPathConf() {
		return lightPathConf;
	}

	public ModuleConfiguration getSampleConf() {
		return sampleConf;
	}

	public ModuleConfiguration getImgEnvConf() {
		return imgEnvConf;
	}

	public ModuleConfiguration getPlaneConf() {
		return planeConf;
	}





/** TODO: standard view if no profile file is given */
	private void loadCommonView()
	{
		LOGGER.info("[VIEW_PROP] Load general view");

		loadImageConf(true);

		loadChannelConf(true);

		loadObjectiveConf(true);

		loadDetectorConf(true);

		loadExperimentConf(true);


		loadImageEnvConf(true);

		loadLightSrcConf(true);

		
		loadSampleConf(true);
		loadLightPathConf(true);

		//TODO: plane,

	}

public void loadLightPathConf(boolean b) 
{
	lightPathConf= new ModuleConfiguration(b);
	int index=lightPathConf.addNewElement("Filter",null);
	if(index==-1)
		return;
	lightPathConf.setTag(TagNames.MODEL, null, null, true, index);
	lightPathConf.setTag(TagNames.MANUFAC, null, null, true, index);
	lightPathConf.setTag(TagNames.LP_TYPE, null, null, true, index);
	lightPathConf.setTag(TagNames.FILTERWHEEL, null, null, true, index);
	
}

/**
 * 
 */
public void loadSampleConf(boolean active) {
	sampleConf=new ModuleConfiguration(active);
	sampleConf.setTag(TagNames.PREPDATE,null,null,true, null, true);
	sampleConf.setTag(TagNames.PREPDESC,null,null,true, null, true);
	sampleConf.setTag(TagNames.RAWCODE,null,null,true, null, true);
	sampleConf.setTag(TagNames.RAWDESC,null,null,true, null, true);
	sampleConf.setTag(TagNames.GRIDBOXNR,null,null,true, null, true);
	sampleConf.setTag(TagNames.GRIDBOXTYPE,null,null,true, null, true);
	sampleConf.setTag(TagNames.EXPGRID,null,null,true, null, true);
	sampleConf.setTag(TagNames.EXPOBJNR,null,null,true, null, true);
	sampleConf.setTag(TagNames.EXPOBJTYPE,null,null,true, null, true);
}

/**
 * 
 */
public void loadLightSrcConf(boolean active) {
	// laser module for lightSrc
	lightSrcConf=new ModuleConfiguration(active);
	lightSrcConf.setTag(TagNames.MODEL,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.POWER,null,TagNames.POWER_UNIT.getSymbol(),true, null, true);
	lightSrcConf.setTag(TagNames.L_TYPE,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.MEDIUM,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.FREQMUL,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.TUNABLE,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.PULSE,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.POCKELCELL,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.REPRATE,null,TagNames.REPRATE_UNIT_HZ.getSymbol(),true, null, true);
	lightSrcConf.setTag(TagNames.PUMP,null,null,true, null, true);
	lightSrcConf.setTag(TagNames.WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
	lightSrcConf.setSettingTag(TagNames.SET_WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
	lightSrcConf.setSettingTag(TagNames.ATTENUATION,null,null,true, null, true);
}

/**
 * 
 */
public void loadImageEnvConf(boolean active) {
	imgEnvConf=new ModuleConfiguration(active);
	imgEnvConf.setTag(TagNames.TEMP,null,null,true, null, true);
	imgEnvConf.setTag(TagNames.AIRPRESS,null,null,true, null, true);
	imgEnvConf.setTag(TagNames.HUMIDITY,null,null,true, null, true);
	imgEnvConf.setTag(TagNames.CO2,null,null,true, null, true);
}

/**
 * 
 */
public void loadExperimentConf(boolean active) {
	expConf=new ModuleConfiguration(active);
	expConf.setTag(TagNames.E_TYPE,null,null,true, null, true);
	expConf.setTag(TagNames.DESC,null,null,true, null, true);
	expConf.setTag(TagNames.EXPNAME,null,null,true, null, true);
	expConf.setTag(TagNames.PROJECTNAME,null,null,true, null, true);
	expConf.setTag(TagNames.GROUP,null,null,true, null, true);
	expConf.setTag(TagNames.PROJECTPARTNER,null,null,true, null, true);
}

/**
 * 
 */
public void loadDetectorConf(boolean active) {
	detectorConf=new ModuleConfiguration(active);
	detectorConf.setTag(TagNames.MODEL,null,null,true, null, true);
	detectorConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
	detectorConf.setTag(TagNames.D_TYPE,null,null,true, null, true);
	detectorConf.setTag(TagNames.ZOOM,null,null,true, null, true);
	detectorConf.setTag(TagNames.AMPLGAIN,null,null,true, null, true);
	detectorConf.setSettingTag(TagNames.GAIN,null,null,true, null, true);
	detectorConf.setSettingTag(TagNames.VOLTAGE,null,TagNames.VOLTAGE_UNIT.getSymbol(),true, null, true);
	detectorConf.setSettingTag(TagNames.OFFSET,null,null,true, null, true);
	detectorConf.setSettingTag(TagNames.CONFZOOM,null,null,true, null, true);
	detectorConf.setSettingTag(TagNames.BINNING,null,null,true, null, true);
	detectorConf.setSettingTag(TagNames.SUBARRAY,null,null,true, null, true);
}

/**
 * 
 */
public void loadObjectiveConf(boolean active) {
	oConf=new ModuleConfiguration(active);
	oConf.setTag(TagNames.MODEL,null,null,true, null, true);
	oConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
	oConf.setTag(TagNames.NOMMAGN,null,null,true, null, true);
	oConf.setTag(TagNames.CALMAGN,null,null,true, null, true);
	oConf.setTag(TagNames.LENSNA,null,null,true, null, true);
	oConf.setTag(TagNames.IMMERSION,null,null,true, null, true);
	oConf.setTag(TagNames.CORRECTION,null,null,true, null, true);
	oConf.setTag(TagNames.WORKDIST,null,TagNames.WORKDIST_UNIT.getSymbol(),true, null, true);
	
	oConf.setSettingTag(TagNames.CORCOLLAR,null,null,true, null, true);
	oConf.setSettingTag(TagNames.OBJ_MEDIUM,null,null,true, null, true);
	oConf.setSettingTag(TagNames.REFINDEX,null,null,true, null, true);

}

/**
 * 
 */
public void loadChannelConf(boolean active) {
	ModuleConfiguration channel=new ModuleConfiguration(active);
	channel.setTag(TagNames.CH_NAME,null,null,true, null, true);
	channel.setTag(TagNames.COLOR,null,null,true, null, true);
	channel.setTag(TagNames.FLUOROPHORE,null,null,true, null, true);
	channel.setTag(TagNames.ILLUMTYPE,null,null,true, null, true);
	channel.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),true, null, true);
	channel.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),true, null, true);
	channel.setTag(TagNames.EMISSIONWAVELENGTH,null,null,true, null, true);
	channel.setTag(TagNames.IMAGINGMODE,null,null,true, null, true);
	channel.setTag(TagNames.ILLUMINATIONMODE,null,null,true, null, true);
	channel.setTag(TagNames.CONTRASTMETHOD,null,null,true, null, true);
	channel.setTag(TagNames.NDFILTER,null,null,true, null, true);
	channel.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),true, null, true);
	
	setChannelConf(channel);
}

/**
 * 
 */
public void loadImageConf(boolean active) {
	imageConf=new ModuleConfiguration(active);
	imageConf.setTag(TagNames.IMG_NAME,null,null,true, null, true);
	imageConf.setTag(TagNames.IMG_DESC,null,null,true, null, true);
	imageConf.setTag(TagNames.ACQTIME,null,null,true, null, true);
	imageConf.setTag(TagNames.DIMXY,null,null,true, null, true);
	imageConf.setTag(TagNames.PIXELTYPE,null,null,true, null, true);
	imageConf.setTag(TagNames.PIXELSIZE,null,null,true, null, true);
	imageConf.setTag(TagNames.DIMZTC,null,null,true, null, true);
	imageConf.setTag(TagNames.STAGELABEL,null,null,true, null, true);
	imageConf.setTag(TagNames.STEPSIZE,null,null,true, null, true);
	imageConf.setTag(TagNames.TIMEINC,null,null,true, null, true);
	imageConf.setTag(TagNames.WELLNR,null,null,true, null, true);
}

	

	public String getMicName() {
		return micName;
	}

	public void setMicName(String micName) {
		this.micName = micName;
	}
	
	public String getMicDesc() {
		return micDesc;
	}
	
	public void setMicDesc(String micDesc) {
		this.micDesc=micDesc;
	}

	public List<Objective> getMicObjList() {
		return micObjList;
	}

	public void setMicObjList(List<Objective> micObjList) {
		this.micObjList = micObjList;
	}

	public List<Detector> getMicDetectorList() {
		return micDetectorList;
	}

	public void setMicDetectorList(List<Detector> micDetectorList) {
		this.micDetectorList = micDetectorList;
	}

	public List<LightSource> getMicLightSrcList() {
		return micLightSrcList;
	}

	public void setMicLightSrcList(List<LightSource> micLightSrcList) {
		this.micLightSrcList = micLightSrcList;
	}

	public List<Object> getMicLightPathFilterList() {
		return micLightPathFilterList;
	}
	public void setMicLightPathFilterList(List<Object> miclightPathFilterList) {
		this.micLightPathFilterList = miclightPathFilterList;
	}
	
	public List<Filter> getMicFilterList() {
		return micFilterList;
	}
	public void setMicFilterList(List<Filter> filterList) {
		this.micFilterList=filterList;
	}
	
	public List<Dichroic> getMicDichroicList() {
		return micDichroicList;
	}
	public void setMicDichroicList(List<Dichroic> dList) {
		this.micDichroicList=dList;
	}
	
	
	

	public void setFile(File file) {
		this.file=file;
	}

	public File getFile()
	{
		return file;
	}

	public void setMapr(HashMap mapr) {
		mapper=mapr;
	}
	public HashMap getMapr()
	{
		return mapper;
	}


}
