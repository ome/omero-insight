package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.MonitorAndDebug;

public class FNode extends DefaultMutableTreeNode
{
	private ImportUserData importData;
	private ImportableFile iFile;
	
	private Boolean saved;
	private HashMap<String,List<TagData>> input;
	private NodeContainer container;
	
	public FNode(File file){
		this.importData=null;
		iFile=null;
		setUserObject(file);
		saved=false;
	}
	public FNode(Object object)
	{
		this.importData=null;
		iFile=null;
		setUserObject(object);
		saved=false;
	}
	
	public FNode(File file,ImportUserData importData,ImportableFile iFile ){
		this.importData=importData;
		this.iFile=iFile;
		setUserObject(file);
	}
	
	public File getFile()
	{
		Object o=getUserObject();
		if(o instanceof File)
			return (File) o;
		else
			return null;
	}
	
	public ImportableFile getImportableFile()
	{
		return iFile;
	}
	

	
	public String toString() {
		String ad="";
		if(importData!=null){
			ad=" [Group: "+importData.getGroup()+", Project: "+
					importData.getProject()+"]";
		}
		if(getFile()==null){
			return (String)getUserObject();
		}
        return getFile().getName()+ad;
    } 
	
	public String getAbsolutePath()
	{
		if(getFile()==null)
			return null;
		
		return getFile().getAbsolutePath();
	}
	
	/**
	 * true if node is a directory or the root
	 */
	public boolean getAllowsChildren() {
		if(getFile()==null)
			return true;
		
		return getFile().isDirectory();
	} 
	
	public boolean hasImportData()
	{
		return (importData!=null);
	}
	
	public ImportUserData getImportData()
	{
		return importData;
	}
	

	/**
	 * A node can have a mapannotation (inherit from parent) but not a view.
	 * If a node has a view, mapAnnotation of parent will be automated loaded to the view at creation time.
	 * If input is still stored, overwrite same field with new input.
	 * @param map
	 */
	public void setMapAnnotation(HashMap<String,List<TagData>> input)
	{
		if(input==null) {
			MonitorAndDebug.printConsole("-- given input is empty");
			return;
	}
		if(this.input==null) {
			this.input=new HashMap<>();
		}
		System.out.println("-- merge input");
		for(Map.Entry<String, List<TagData>> entry:input.entrySet()) {
			List<TagData> list1 = entry.getValue();
			List<TagData> list2 = this.input.get(entry.getKey());
			this.input.put(entry.getKey(), MDEHelper.mergeTagDataList(list2, list1));
		}
	}
	
	/**TODO
	 * A node can have a mapannotation (inherit from parent) but not a view.
	 * If a node has a view, mapAnnotation of parent will be automated loaded to the view at creation time.
	 * @param map
	 */
	public MapAnnotationObject getMapAnnotation()
	{
		if(input!=null) {
			MonitorAndDebug.printConsole("-- return saved mapped annotation");
			return new MapAnnotationObject(input);
		}else if(getContainer()!=null) {
			MonitorAndDebug.printConsole("-- read out map annotation from contentTree");
			HashMap<String,List<TagData>> input=MDEHelper.getInput(getContainer().getTreeNode());
			MapAnnotationObject map=new MapAnnotationObject(input);
			return map;
		}
		return null;
	}
	
	
 
	public void setContainer(NodeContainer cont) {
		this.container= cont;
	}
	
	
	public void reset() {
		container=null;
		
	}
	public NodeContainer getContainer() {
		return container;
	}
	public HashMap<String, List<TagData>> getInput() {
		return input;
	}
	
	
	

}
