package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml;

import ome.units.quantity.Time;
import ome.xml.model.OMEModel;
import ome.xml.model.enums.EnumerationException;
import org.w3c.dom.Element;

public class Channel extends ome.xml.model.Channel{
	
	private String illuminationTypeString;
	private String imagingMode;
	private Time defaultExposureTime;
	
	
	public Time getDefaultExposureTime() {
		return defaultExposureTime;
	}

	public void setDefaultExposureTime(Time defaultExposureTime) {
		this.defaultExposureTime = defaultExposureTime;
	}

	// -- Constructors --
	/** Default constructor. */
	public Channel()
	{
	}

	/**
	 * Constructs Channel recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Channel(Element element, OMEModel model)
			throws EnumerationException
	{
		update(element, model);
	}
	/** Copy constructor. */
	public Channel(Channel orig)
	{
		super(orig);
		this.illuminationTypeString=orig.illuminationTypeString;
		this.imagingMode=orig.imagingMode;
		this.defaultExposureTime=orig.defaultExposureTime;
	}
	
	public Channel(ome.xml.model.Channel c){
		super(c);
		this.illuminationTypeString=null;
		this.imagingMode=null;
		this.defaultExposureTime=null;
	}
	
	public void setAcquisitionMode(String imagingMode)
	{
		this.imagingMode=imagingMode;
	}
	public String getAcquisitionModeAsString()
	{
		if(this.imagingMode==null && getAcquisitionMode()!=null)
			this.imagingMode=getAcquisitionMode().getValue();
		
		return imagingMode;
	}

	public void setIlluminationType(String illuminationType)
	{
		this.illuminationTypeString = illuminationType;
	}
	
	public String getIlluminationTypeAsString()
	{
		if(this.illuminationTypeString==null && getIlluminationType()!=null)
			this.illuminationTypeString=getIlluminationType().getValue();
		return this.illuminationTypeString;
	}
	
	

}
