package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.NodeContainer;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;

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
	
//	/**
//	 * Returns true if the node is a leaf or the root node.
//	 */
//	public boolean isLeaf()
//	{
//		File f=getFile();
//		boolean result=f!=null? !f.isDirectory() : true;
//		return result;
//	}
	
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
	
	
//	public MetaDataModelObject getModelObject()
//	{
//		if(container!=null)
//			return container.getModelObj();
//		
//		return null;
//	}
	
//	public MetaDataModel getModelOfSeries(int index)
//	{
//		if(container==null)
//			return null;
//		
//		return container.getModelObj().getModel(index);
//	}
	
//	public boolean hasModelObject() {
//		if(container==null)
//			return false;
//		return container.getModelObj()!=null;
//	}
	
	/**
	 * Make a copy of given object to set this modelobject
	 * @param nodeModel
	 */
//	public void inheritModel(MetaDataModelObject nodeModel,boolean isDir) {
//		if(container==null) {
//			try {
//				container = new NodeContainer(getAbsolutePath(), null, null, null, null, isDir);
//				container.setParentData(nodeModel.getModel(0));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//			
//	}
	
	
	/**TODO
	 * Save model if view exists.
	 */
//	public void saveModel() throws Exception
//	{
//		if(container==null )
//			return;
//		container.saveInput();
//		
//		
//	}
	
	
	//TODO necessarry?
//	public void saveExtendedData()
//	{
//		if(view==null)
//			return;
//		
//		view.saveExtendedMetaData();
//	}

	/**
	 * A node can have a mapannotation (inherit from parent) but not a view.
	 * If a node has a view, mapAnnotation of parent will be automated loaded to the view at creation time.
	 * @param map
	 */
	public void setMapAnnotation(HashMap<String,List<TagData>> input)
	{
		this.input=input;
	}
	
	/**TODO
	 * A node can have a mapannotation (inherit from parent) but not a view.
	 * If a node has a view, mapAnnotation of parent will be automated loaded to the view at creation time.
	 * @param map
	 */
	public MapAnnotationObject getMapAnnotation()
	{
		if(input!=null) {
			System.out.println("-- return saved mapped annotation");
			return new MapAnnotationObject(input);
		}else if(getContainer()!=null) {
			System.out.println("-- read out map annotation from contentTree");
			HashMap<String,List<TagData>> input=MDEHelper.getInput(getContainer().getTreeNode());
			//TODO parse input to MapAnnotationObject
			MapAnnotationObject map=new MapAnnotationObject(input);
			return map;
		}
		return null;
	}
	
	
	/**TODO
	 * Function to control map data
	 */
	public void printMaps()
	{
//		MonitorAndDebug.printConsole("FNODE :: "+getAbsolutePath());
//		if(view!=null){
//			MonitorAndDebug.printConsole("\t View Map:");
//			if(view.getMapAnnotation()!=null)view.getMapAnnotation().printObject();
//		}
//		if(mapAnnot!=null){
//			MonitorAndDebug.printConsole("\t Intern Map:");	
//			mapAnnot.printObject();
//		}
	}
//	public void addFiles(int showHidden) {
//		File[] files = getFile().listFiles();
//		for (File f : files) {
//			if (showHidden == ImportFileTree.SHOW_HIDDEN) {
//				if (f.isHidden())
//					this.add(new FNode(f));
//			} else if (showHidden == ImportFileTree.SHOW_VISIBLE) {
//				if (!f.isHidden())
//					this.add(new FNode(f));
//			} else {
//				this.add(new FNode(f));
//			}
//		}
//	} 
	public void setContainer(NodeContainer cont) {
		this.container= cont;
	}
	
	
	public void reset() {
		container=null;
		
	}
	public NodeContainer getContainer() {
		return container;
	}
	
	
	

}
