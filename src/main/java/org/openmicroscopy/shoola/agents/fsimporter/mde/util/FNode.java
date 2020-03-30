/*
 * Copyright (C) <2016-2019> University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;

/**
 * Node in file tree of MDE.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class FNode extends DefaultMutableTreeNode
{
	private ImportUserData importData;
	private ImportableFile iFile;
	
	private HashMap<String,List<TagData>> input;
	private NodeContainer container;
	
	public FNode(File file){
		this.importData=null;
		iFile=null;
		setUserObject(file);
	}
	public FNode(Object object)
	{
		this.importData=null;
		iFile=null;
		setUserObject(object);
	}
	
	public FNode(File file,ImportUserData importData,ImportableFile iFile ){
		this.importData=importData;
		this.iFile=iFile;
		setUserObject(file);
	}
	
	/**
	 * @return file object associated with this node or null.
	 */
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
	

	/**
	 * Returns the name of this node's user object and target informations like group and project name if the object is
	 * a file, or this node's user object as String if the object is a dir.
	 * @return a string representation of the FNode object.
	 */
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
	
	/**
	 * @return absolute path of given file. If given component is a dir return null.
	 */
	public String getAbsolutePath()
	{
		if(getFile()==null)
			return null;
		return getFile().getAbsolutePath();
	}
	
	/**
	 * @return true if node is a directory or the root, false if node is a file
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
	 * Set or merge given annotations to existing annotations.
	 * @param input Map of annotations sort by mde object type
	 */
	public void setAnnotation(HashMap<String,List<TagData>> input)
	{
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] save input as mapannotation");
		if(input==null || input.isEmpty()) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] merge map annotation: No");
			return;
	}
		if(this.input==null) {
			this.input=new HashMap<>();
		}
		// merge with existing
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] merge map annotation: Yes");
		for(Map.Entry<String, List<TagData>> entry:input.entrySet()) {
			List<TagData> list1 = entry.getValue();
			List<TagData> list2 = this.input.get(entry.getKey());
			this.input.put(entry.getKey(), MDEHelper.mergeTagDataList(list2, list1));
		}
	}
	
	/**
	 * Get annotations as MapAnnotationObject.
	 * A node can have annotations (inherited from parent) although it does not yet have a view.
	 * If a node has a view, annotation of parent will be automated loaded to the view at creation time.
	 * @return a MapAnnotationObject representing the annotations for this node or null if no annotations exists.
	 */
	public MapAnnotationObject getMapAnnotation()
	{
		if(input!=null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] return saved mapped annotation");
			return new MapAnnotationObject(input);
		}else if(getContainer()!=null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] read out map annotation from contentTree");
			HashMap<String,List<TagData>> input=MDEHelper.getInput(getContainer().getTreeNode());
			MapAnnotationObject map=new MapAnnotationObject(input);
			return map;
		}
		return null;
	}
	
	public void setContainer(NodeContainer cont) {
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Replace Container");
		this.container= cont;
	}
	
	/**
	 * Reset node container and annotations (to null).
	 */
	public void reset() {
		container=null;
		input=null;
	}

	public NodeContainer getContainer() {
		return container;
	}

	/**
	 * Get all previous stored annotations for this node as HashMap.
	 * @return a list of TagData sorted by mde object type
	 */
	public HashMap<String, List<TagData>> getInput() {
		return input;
	}
}
