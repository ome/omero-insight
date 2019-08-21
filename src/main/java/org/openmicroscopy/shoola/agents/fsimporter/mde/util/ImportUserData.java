package org.openmicroscopy.shoola.agents.fsimporter.mde.util;


import ome.xml.model.Experimenter;
import omero.gateway.model.DataObject;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.GroupData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;
import omero.model.Project;

public class ImportUserData 
{
	private GroupData group;
	/** name of project or screen */
	private String name;
	private ExperimenterData experimenter;
	
	public ImportUserData(GroupData group,DataObject parent,ExperimenterData experimenter)
	{
		this.group=group;
		if(parent instanceof ProjectData)
			this.name=parent.asProject().getName().getValue();
		else if(parent instanceof ScreenData)
			this.name=parent.asScreen().getName().getValue();
		this.experimenter=experimenter;
		
	}
	
	public String getGroup()
	{
		return group.getName();
	}
	
	public String getProject()
	{
		return name;
		
	}
	

	public Experimenter getUser()
	{
		Experimenter e= new Experimenter();
		e.setFirstName(experimenter.getFirstName());
		e.setLastName(experimenter.getLastName());
		return e;
	}
	public String getUserName()
	{
		return experimenter.getLastName();
	}
	
	public String[] getUserFullName()
	{
		String[] name={experimenter.getFirstName(),experimenter.getLastName()};
		return name;
	}
}
