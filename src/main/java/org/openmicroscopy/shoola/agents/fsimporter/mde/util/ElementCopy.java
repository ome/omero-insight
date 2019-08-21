package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import omero.model.Experiment;
import omero.model.ExperimentI;
import omero.model.Experimenter;
import omero.model.ExperimenterI;

public class ElementCopy 
{
	/**
	 * Copy omero.model.Experiment object
	 * @param e
	 * @return copy
	 */
	public static Experiment copy(Experiment e){
		Experiment c=new ExperimentI();
		c.setId(e.getId());
		c.setType(e.getType());
		c.setDescription(e.getDescription());
//		c.setDetails(e.getDetails());
		c.addAllMicrobeamManipulationSet(e.copyMicrobeamManipulation());
		
		return e;
		
	}

	public static Experimenter copy(Experimenter e) {
		Experimenter c=new ExperimenterI();
		c.setId(e.getId());
		c.setFirstName(e.getFirstName());
		c.setMiddleName(e.getMiddleName());
		c.setLastName(e.getLastName());
		return c;
	}
}
