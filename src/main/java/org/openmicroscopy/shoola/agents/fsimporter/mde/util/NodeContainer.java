package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;

/**
 * Container holds view and modelobj for selection
 * @author Kunis
 *
 */
public class NodeContainer 
{
//	private MDEModelManager modelManager;
	private boolean isDir;
	private String name;
	private DefaultMutableTreeNode rootNode;
	private ModuleList instrumentList;
	private HashMap<String, List<TagData>> input;
	
	
	 /**
     * Container for modelManager and view.
     * @param objName file name
	 * @param importData given import information for this data.
	 * @param pTree given parent information for this data.
	 * @param parentPanel parent JPanel of this component.
	 * @param current existing container for selected node
	 * @param isDir TODO
     */
	public NodeContainer(String objName,ImportUserData importData,
			DefaultMutableTreeNode pTree, MetaDataDialog parentPanel, NodeContainer current, boolean isDir) throws Exception
	{
		this.isDir=isDir;
		this.name=objName;
		this.rootNode=pTree;
	}

	
	public String getNodeObject()
	{
		return name;
	}

	public boolean isDir(){
		return isDir;
	}
	

	public DefaultMutableTreeNode getTreeNode() {
		return rootNode;
	}
	
	public void setTreeNode(DefaultMutableTreeNode treeNode) {
		rootNode=treeNode;
	}


	public ModuleList getInstruments() {
		return instrumentList;
	}
	
	public void setInstruments(ModuleList list) {
		this.instrumentList=list;
	}


	


	//TODO
//	public void setParentData(MetaDataModel parentData)
//	{
//		MonitorAndDebug.printConsole("-- Add parent data for "+(isDir?"directory":"file")+" \t[NodeContainer::generateModel]");
//		// set parent data single view
//		if(parentData!=null) {
//			try {
//				if(isDir || !(modelManager.fileDataLoaded() )) {
//					MonitorAndDebug.printConsole("# Load parent data \t[NodeContainer::generateModel]");
//					boolean load=modelManager.addData(0,parentData, true);
//					modelManager.setParentLoaded(load);
//				}else {
//					if(modelManager.getFileObject().getSeriesCount()<2) {
//						MonitorAndDebug.printConsole("# Load parent data \t[NodeContainer::generateModel]");
//						boolean load=modelManager.addData(0,parentData, true);
//						modelManager.setParentLoaded(load);
//					}
//					else {//series
//						for(int j=0; j<modelManager.getFileObject().getSeriesCount();j++) {
//							MonitorAndDebug.printConsole("# Load parent data: "+j+"\t[NodeContainer::generateModel]");
//							boolean load=modelManager.addData(j,parentData, true);
//							modelManager.setParentLoaded(modelManager.parentDataLoaded()||load);
//						}
//					}
//				}
//				
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return;
//			}
//		}
//	}

	
}
