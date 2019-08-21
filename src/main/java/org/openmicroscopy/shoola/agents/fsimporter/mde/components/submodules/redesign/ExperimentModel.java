package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign;

import java.util.LinkedHashMap;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.ExperimentConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.handlers.ExperimentTypeEnumHandler;


public class ExperimentModel extends EditorModel
{
	private Experiment experiment;
	private Experimenter projectPartner;
	private String projectName;
	private String group;
	private Experimenter experimenter;
	
	public ExperimentModel()
	{
		tagMap=null;
	}
	
	public ExperimentModel(Experiment exp, Experimenter exper, String projPartner)
	{
		if(exp!=null)
			setExperiment(exp);
		if(exper!=null)
			setExperimenter(exper);
		if(projPartner!=null)
			setProjectPartner(projPartner);
	}
	
	public ExperimentModel(ExperimentModel orig)
	{
		//TODO deep copy
		tagMap=(LinkedHashMap<String, TagData>) orig.tagMap.clone();
	}
	
	/**
	 * Init with data
	 * @param img
	 */
	public void setData(ExperimentModel o)
	{
		addData(o,true);
	}
	
	/**
	 * Add additional data to model
	 * @param img
	 * @param overwrite true: overwrite all existing data; false: complete existing data src [ src(tag==null)=in(tag!=null)]
	 */
	public void addData(ExperimentModel o, boolean overwrite)
	{
		ExperimentConverter converter=new ExperimentConverter();
		addData(converter.convertData(o),overwrite, false);
	}
	
	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public Experiment getContainerAsExp()
	{
		//TODO ids
		experiment.linkExperimenter(experimenter);
		experiment.linkExperimenter(projectPartner);
		return experiment;
	}

	public Experimenter getProjectPartner() {
		return projectPartner;
	}
	public String getProjectPartnerName()
	{
		if(projectPartner==null)
			return "";
		
		return projectPartner.getLastName();
	}

	public void setProjectPartner(String name)
	{
		if(name.equals(""))
			return;
		
		if(projectPartner==null)
			projectPartner=new Experimenter();
		projectPartner.setLastName(name);
	}
//	public void setProjectPartner(Experimenter projectPartner) {
//		this.projectPartner = projectPartner;
//	}

	public Experimenter getExperimenter() {
		return experimenter;
	}

	public void setExperimenter(Experimenter experimenter) {
		this.experimenter = experimenter;
	}
	
	public void setGroupName(String group) {
		this.group=group;		
	}

	public void setProjectName(String project) {
		this.projectName=project;		
	}

	public String getProjectName() {
		return projectName;
	}

	public String getGroupName() {
		return group;
	}
	
	public static LinkedHashMap<String, TagData> getEmptyTagList()
	{
		ExperimentConverter converter=new ExperimentConverter();
		return converter.convertData(null);
	}
	public LinkedHashMap<String,TagData> getTagList()
	{
		if(tagMap==null)
			return getEmptyTagList();
		return tagMap;
	}
}