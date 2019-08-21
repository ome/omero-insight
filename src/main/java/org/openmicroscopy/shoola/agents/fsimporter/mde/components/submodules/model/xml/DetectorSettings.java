package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.model.xml;



public class DetectorSettings extends ome.xml.model.DetectorSettings {
	private String subarray;

	/** Default constructor. */
	public DetectorSettings(){}
	
	/**Copy Constructor*/
	public DetectorSettings(DetectorSettings orig){
		super(orig);
		this.subarray=orig.subarray;
	}
	
	public DetectorSettings(ome.xml.model.DetectorSettings orig){
		super(orig);
		this.subarray=null;
	}
	
	public String getSubarray() {
		return subarray;
	}

	public void setSubarray(String subarray) {
		this.subarray = subarray;
	}
}
