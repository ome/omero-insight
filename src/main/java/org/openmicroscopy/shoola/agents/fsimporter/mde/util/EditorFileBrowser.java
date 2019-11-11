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
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.env.data.model.FileObject;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;

/** 
 * File browser of MDE.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class EditorFileBrowser extends JTree 
{
    private FileFilter fileFilter;
    
	public EditorFileBrowser(FNode rootNode)
	{
		super(rootNode);
		//TODO fileFilter=
		
		setRootVisible(true);
    	setShowsRootHandles(true);
    	getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    	
	}

	public void createNodes(List<ImportableFile> files, boolean holdData)
	{
		DefaultTreeModel treeModel=(DefaultTreeModel)getModel();
		FNode root =(FNode)treeModel.getRoot();

//		root.setModelObject(null);

		if(!holdData){
			root.removeAllChildren();

			if(files==null){
				return;
			}
			ImportableFile f;
			Iterator<ImportableFile> j=files.iterator();

			while (j.hasNext()) {
				f = j.next();
				addNode(root,f.getFile(),f);

				String name="";
				if(f.getParent() instanceof ProjectData)
					name=f.getParent().asProject().getName().getValue();
				else if(f.getParent() instanceof ScreenData)
					name=f.getParent().asScreen().getName().getValue();

				String dName=f.getDataset()!=null? f.getDataset().getName():"from folder";
			}
			treeModel.reload();
		}else{
			TreePath path=getSelectionPath();

			FNode node = (FNode)getLastSelectedPathComponent();

			String dirName=node.getFile().getName();
			FNode dirNode=node;
			if(node!=null && node.isLeaf()){
				dirName=node.getFile().getParentFile().getName();
				dirNode=(FNode) node.getParent();
			}

			insertNodes(files,dirName,dirNode);
			updateUI();
			expandPath(path);

		}
	}
	
	private void addNode(FNode parent, FileObject f,ImportableFile fileObj)
    {
        FNode dir = null;
        FNode file = null;
        ImportUserData data=null;
        
        if(fileObj!=null){
            data = new ImportUserData(fileObj.getGroup(), fileObj.getParent(), fileObj.getUser());
        }
        
        if(f.isDirectory()){

            dir=new FNode(new File(f.getAbsolutePath()),data,null);
            parent.add(dir);
            File[] files=(new File(f.getAbsolutePath()).listFiles((java.io.FileFilter)fileFilter));

            if(files != null && files.length>0){
                for(int i=0; i<files.length;i++){
                    addNode(dir,new FileObject(files[i]),null);
                }
            }

            //add files, attention: only image files?
        }else{
            try {
                file=new FNode(new File(f.getAbsolutePath()),data,fileObj);
                parent.add(file);
            } catch (Exception e) {
            	ImporterAgent.getRegistry().getLogger().warn(this,
                        "[MDE]  Wrong import format \"+f.getAbsolutePath()!");
            }
        }
    }
	
	/**
     * Reload node. If node is a directory, load all files of the directory, if is importable file. 
     * If node==null or node.getFile()==null -> The import queue holds single files. Import only files from list.
     * @param files
     * @param dirName
     * @param dir
     */
    private void insertNodes(List<ImportableFile> files,String dirName,FNode node) 
    {
        if(node==null || node.getFile()==null){
            node=(FNode)getModel().getRoot();
            ImportableFile f;
            Iterator<ImportableFile> j=files.iterator();
            node.removeAllChildren();
            while (j.hasNext()) {
                f = j.next();
                // single file in the importQueue, only insert this and their ome file
                addNode(node,f.getFile(),f);
            }
        }else{
            node.removeAllChildren();
            File[] fileList=(new File(node.getFile().getAbsolutePath())).listFiles((java.io.FileFilter)fileFilter);

            if(fileList != null && fileList.length>0){
                for(int i=0; i<fileList.length;i++){
                    addNode(node,new FileObject(fileList[i]),null);
                }
            }
        
        }
    }

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter=fileFilter;
	}
	
	
	/**
     * 
     * @return absolute path of given file. If given component is a dir return empty string 
     */
    public String getSelectedFilePath(FNode node)
    {
        String fname="";

        if (node!=null && node.isLeaf()) {
            fname=node.getAbsolutePath();
        } 
        return fname;
    }
    
    /**
     * 
     * @return absolute path of selected file. If selected component is a dir return empty string 
     */
    public String getSelectedFilePath()
    {
        return getSelectedFilePath((FNode) getLastSelectedPathComponent());
    }
    
    
}
