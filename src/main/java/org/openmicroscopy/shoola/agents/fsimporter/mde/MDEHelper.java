/*
 * Copyright (C) <2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import ome.xml.model.LightPath;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;
import ome.xml.model.FilterSet;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * Helper functions for object tree.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class MDEHelper {
	
	public static final String SELECTOR="#";

	public static String printList(String string, Map<String,List<TagData>> list) {
		String result="\t## "+string+" ##\n";
		if (list == null || list.isEmpty()) {
			return result;
		}
		for (Map.Entry<String, List<TagData>> entry: list.entrySet()) {
			result+="\t"+entry.getKey()+" :\n ";
			for (TagData t: entry.getValue()) {
				result+="\t\t"+t.tagToString()+"\n";
			}
		}
		return result;
	}
	
	
	/**
	 * Get input from given tree and generate key for key-value annotation.
	 * @param contentTree
	 */
	public static HashMap<String,List<TagData>> getInput(DefaultMutableTreeNode contentTree) {
		if(contentTree==null)
			return null;
		
		HashMap<String,List<TagData>> result=new HashMap<>();
		
		//root node?
		if(((ModuleTreeElement) contentTree.getUserObject()).isContainer()) {
			for(int i = 0 ; i < contentTree.getChildCount(); i++) {
				HashMap r=getInput((DefaultMutableTreeNode) contentTree.getChildAt(i));
				if(r!=null && !r.isEmpty())
					result.putAll(r);
			}
		}else {
			Enumeration e = contentTree.breadthFirstEnumeration();
			// input of subtree
			while(e.hasMoreElements()) {
				DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
				// search for node with input and generate key backward, skip container nodes
				if(!(((ModuleTreeElement) node.getUserObject()).isContainer())) {
					List<TagData> res=((ModuleTreeElement) node.getUserObject()).getData().getInput();
					if(res!=null && !res.isEmpty()) {
						String id= generateTreePathForKey(node);
						result.put(id, res);
					}
				}
			}
		}
		return result;
	}
	
	private static String generateTreePathForKey(DefaultMutableTreeNode node) {
		String res = ((ModuleTreeElement) node.getUserObject()).getElementName();
		while(node.getParent()!=null) {
			res = ((ModuleTreeElement) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getElementName()+SELECTOR+res;
			node = (DefaultMutableTreeNode) node.getParent();
		}
		return res;
		
	}
	
	public static void resetInput(DefaultMutableTreeNode contentTree) {
		if(contentTree==null)
			return;
		
		Enumeration e = contentTree.breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
			if(!(((ModuleTreeElement) node.getUserObject()).isContainer())) {
				((ModuleTreeElement) node.getUserObject()).resetInput();
			}
		}
	}
	
	
	/**
	 * merge inTree into destTree.
	 * @param inTree
	 * @param destTree
	 * @return destTree with data of inTree
	 */
	public static DefaultMutableTreeNode mergeTrees(DefaultMutableTreeNode tree1, DefaultMutableTreeNode tree2,int depth) {
		
		if(((ModuleTreeElement) tree1.getUserObject()).isContainer()) {
			depth++;
			for(int i = 0 ; i < tree1.getChildCount(); i++) {
				String nodeName=((DefaultMutableTreeNode)tree1.getChildAt(i)).getUserObject().toString();
				DefaultMutableTreeNode n1=findNode((DefaultMutableTreeNode) tree1.getChildAt(i),tree2);
				
				if(n1==null) {
//					ImporterAgent.getRegistry().getLogger().debug(null, "-- Node "+nodeName+" doesn't exists, INSERT at depth "+depth));
				}else {
					DefaultMutableTreeNode p1=(DefaultMutableTreeNode) n1.getParent();
					mergeTrees((DefaultMutableTreeNode) tree1.getChildAt(i),n1,depth);
				}
				
			}
			// iterate through children
			// find first child node of same type in destTree
			// save depth of inserted node
			// get next child of inTree
			// find this node at return depth or insert
			
			//recursive for childs of childs
		}
		return tree2;
	}
	
	/**
	 * 
	 * @param tree
	 * @param structure
	 * @return tree with additional elements that are available in structure
	 */
	public static DefaultMutableTreeNode inheritTreeStructure(DefaultMutableTreeNode tree, DefaultMutableTreeNode structure) {
		
		return tree;
	}
	
	/**
	 * Trees are equal if all paths to leafs are equal
	 * @param n1
	 * @param n2
	 * @return true if trees are equal, else false
	 */
	public static boolean compareTrees(DefaultMutableTreeNode n1,DefaultMutableTreeNode n2) {
		List<String> leafsN1=getAllLeafPaths(n1, "");
		List<String> leafsN2=getAllLeafPaths(n2, "");
		
		if(leafsN1.containsAll(leafsN2) && leafsN1.size() == leafsN2.size()) {
			return true;
		}else {
			List<String> different=new ArrayList<>();
			different.addAll(leafsN1);
			different.addAll(leafsN2);
			
			// similar
			leafsN1.retainAll(leafsN2);
			// different
			different.removeAll(leafsN1);
			
			return false;
		}
	}
	

	
	/**
	 * @param tree1
	 * @param tree2
	 * @return list of paths to additional leafs in tree1
	 */
	public static List<String> getAdditionalLeafsInTree(DefaultMutableTreeNode tree1,DefaultMutableTreeNode tree2){
		List<String> leafsN1=getAllLeafPaths(tree1, "");
		List<String> leafsN2=getAllLeafPaths(tree2, "");
		
		if(leafsN1.containsAll(leafsN2) && leafsN1.size() == leafsN2.size()) {
			return null;
		}else {
			return getAdditionalLeafPaths(leafsN1, leafsN2);
		}
	}
	
	public static List<String> getAdditionalLeafPaths(List<String> leafsN1,List<String> leafsN2){
		List<String> result=new ArrayList<>();
		for(String s:leafsN1) {
			if(!leafsN2.contains(s)) {
				result.add(s);
			}
		}
		return result;
	}
	
	public static List<String> getAllLeafPaths(DefaultMutableTreeNode tree,String parent){
		String path = parent.equals("")?tree.getUserObject().toString(): parent+SELECTOR+tree.getUserObject().toString();
		List<String> leafPathList=new ArrayList<>();
		if(tree.getChildCount()==0) {
			leafPathList.add(path);
			return leafPathList;
		}else {
			for(int i = 0 ; i < tree.getChildCount(); i++) {
				List<String> result =getAllLeafPaths((DefaultMutableTreeNode) tree.getChildAt(i), path);
				leafPathList.addAll(result);
			}
		}
		
		return leafPathList;
	}
	
	
	
	/**
	 * Return list of direct childs of given tree with same type like child
	 * @param child
	 * @param tree
	 * @return
	 */
	public static List<DefaultMutableTreeNode> getListOfChilds(String type, DefaultMutableTreeNode tree){
		if(type==null || type.equals("") || tree==null)
			return null;
		
		List<DefaultMutableTreeNode> list=new ArrayList<>();
		for(int i = 0 ; i < tree.getChildCount(); i++) {
			if(((ModuleTreeElement) ((DefaultMutableTreeNode) tree.getChildAt(i)).getUserObject()).getType().equals(type)) {
				list.add((DefaultMutableTreeNode) tree.getChildAt(i));
			}
		}
		return list;
	}
	
	public static int getChildCount(String type, DefaultMutableTreeNode tree){
		if(type==null || type.equals("") || tree==null)
			return 0;
		int count=0;
		for(int i = 0 ; i < tree.getChildCount(); i++) {
			if(((DefaultMutableTreeNode) tree.getChildAt(i)).getUserObject() instanceof ModuleTreeElement &&
					((ModuleTreeElement) ((DefaultMutableTreeNode) tree.getChildAt(i)).getUserObject()).getType().equals(type)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @return true if type and child index are equal, else false
	 */
	public static boolean compareNodes(DefaultMutableTreeNode n1,DefaultMutableTreeNode n2) {
		if(n1== null && n2==null) 
			return true;

		if(n1==null && n2!=null || n1!=null && n2==null)
			return false;
		//TODO implement isEqual in ModuleTreeElement
		if(n1.getUserObject()!=null && n2.getUserObject()!=null && 
				((ModuleTreeElement) n1.getUserObject()).getType().equals(((ModuleTreeElement) n2.getUserObject()).getType())) {
			
				ModuleTreeElement mte1=(ModuleTreeElement) n1.getUserObject();
				ModuleTreeElement mte2=(ModuleTreeElement) n2.getUserObject();
				if(mte1.getType()!=null && mte1.getType().equals(mte2.getType()) && mte1.getChildIndex()==mte2.getChildIndex())
					return true;
		}
		
		return false;
	}
	
	/**
	 * Find MDETreeElement with type==mdeTreeElement.getType() and childIndex==mdeTreeElement.getchildIndex()
	 * @param node
	 * @param tree
	 * @return
	 */
	private static DefaultMutableTreeNode findNode(DefaultMutableTreeNode n, DefaultMutableTreeNode tree) {
		 for (Enumeration e = tree.preorderEnumeration(); e.hasMoreElements();) {
	            DefaultMutableTreeNode d =  (DefaultMutableTreeNode)e.nextElement();
	            if(compareNodes(n,d)) {
	            	return d;
	            } 
		 }
		 return null;
	}
	

	
	/**
	 * Find direct child node of tree by given name. Child is a {@link ModuleTreeElement}.
	 * 
	 * @param tree
	 * @param name
	 * @return child node as a {@link DefaultMutableTreeNode} or null if no child of this name exists.
	 */
	private static DefaultMutableTreeNode getChildByName(DefaultMutableTreeNode tree, String name) {
		if(tree!=null) {
			Enumeration e = tree.breadthFirstEnumeration();
			while(e.hasMoreElements()) {
				DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
				if(((ModuleTreeElement) node.getUserObject()).getElementName().trim().equals(name.trim())) {
					return node;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param tree that holds the prospected node
	 * @param path to node as string: p1#p2#p3#node
	 * @param createNew true if node should to be inserted if it does not exist
	 * @return prospected node as {@link DefaultMutableTreeNode} or null.
	 */
	private static DefaultMutableTreeNode getNodeByPath(DefaultMutableTreeNode tree, String path, boolean createNew) {
		if(path==null)
			return null;
		String[] nodeNames=path.split(SELECTOR);
		
		//get first parent:
		DefaultMutableTreeNode parent = getChildByName(tree, nodeNames[0]);
		
		if(parent!=null) {
			for(int i=1; i< nodeNames.length;i++) {
				DefaultMutableTreeNode child = getChildByName(parent,nodeNames[i]);
				if(child==null && createNew) {
					ImporterAgent.getRegistry().getLogger().debug(null, "-- Insert new node here "+parent.getUserObject().toString());
					child=createNode(nodeNames[i],parent);
				}
				parent=child;
			}
		}
		return parent;
	}
	
	private static DefaultMutableTreeNode createNode(String nodeName,DefaultMutableTreeNode parent) {
		ModuleTreeElement treeElem = ModuleController.getInstance().createElement(getType(nodeName),parent);
		DefaultMutableTreeNode node=new DefaultMutableTreeNode(treeElem);
		parent.add(node);
		
		return node;
	}

	/**
	 * 
	 * @param name string with form [<type>]{<Nr>}
	 * @return node type. For example: given [OME:Object]{0} returns OME:Object
	 */
	private static String getType(String name) {
		String result=null;
		result=name.substring(name.indexOf("[")+1, name.lastIndexOf("]"));
		return result;
	}


	/** set ImportUserData */
//	public void setImportUserData(ImportUserData data)
//	{
//		if(data!=null){
//			ImporterAgent.getRegistry().getLogger().debug(null, "# load import data\t[MDEModelManager::setImportData]");
//			importUserData=data;
//			try {
//				ExperimentModel expCont=new ExperimentModel();
//				expCont.setExperimenter(importUserData.getUser());
//				expCont.setGroupName(importUserData.getGroup());
//				expCont.setProjectName(importUserData.getProject());
//				modelObj.setExperimentModel(expCont);
////				model.setExtendedData(expCont);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
//	}
	
	public static final Dichroic convertFilterToDichroic(Filter f)
	{
		Dichroic d=new Dichroic();
		d.setID(f.getID());
		d.setModel(f.getModel());
		d.setManufacturer(f.getManufacturer());
		d.setLotNumber(f.getLotNumber());
		d.setSerialNumber(f.getSerialNumber());
		d.setInstrument(f.getInstrument());
		//TODO
		//		d.setLinkedAnnotation(index, o)
		//		d.setLinkedFilterSet(index, o)
		//		d.setLinkedLightPath(index, o)
		return d;
	}

	public static final Filter convertDichroicToFilter(Dichroic d)
	{
		Filter f=new Filter();
		f.setID(d.getID());
		f.setModel(d.getModel());
		f.setManufacturer(d.getManufacturer());
		f.setLotNumber(d.getLotNumber());
		f.setSerialNumber(d.getSerialNumber());
		f.setInstrument(d.getInstrument());
		f.setType(FilterType.DICHROIC);
		//		f.setLinkedAnnotation(index, o)

		return f;
	}

	
	
	/**
	 * Compare content of objects except ID and User settings (User::)
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean isEqual(ModuleContent c1, ModuleContent c2) {
		boolean result=false;
		if(c1!=null && c2!=null) {
			LinkedHashMap<String,TagData> tList1=c1.getList();
			LinkedHashMap<String,TagData> tList2=c2.getList();
			
			if(tList1==null && tList2==null) {
				return true;
			}else{ 
				if((tList1==null && tList2!=null) || (tList1!=null &&tList2==null) ||
						tList1.size()!=tList2.size()) {
					return false;
				}else {
					int falsePosCount=0;
					for (Map.Entry<String, TagData> entry : tList1.entrySet()) {
						String key=entry.getKey();
						if(!key.contains(TagNames.PREFIX_SETTINGS)) {
							if(tList2.containsKey(key)) {
								// special case ID: only compare if not null in both objects
								if(key.equals(TagNames.ID) ) {
									if(!entry.getValue().isEmptyValue() && !tList2.get(key).isEmptyValue() && 
										!tList2.get(key).getTagValue().equals(entry.getValue().getTagValue())){
										falsePosCount++;
									}
								}else {
									if(!tList2.get(key).getTagValue().equals(entry.getValue().getTagValue())){
										falsePosCount++;
									}
								}
							}else {
								falsePosCount++;
							}
						}
					}
					if(falsePosCount>0) {
						return false;
					}else {
						return true;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Add data in tree with entries in input, marke it has data has change
	 * @param tree
	 * @param input list of nodepath,list(tagData)
	 */
	public static void addData(DefaultMutableTreeNode tree,HashMap<String, List<TagData>> input) {
		for(Entry<String, List<TagData>> entry : input.entrySet()) {
			DefaultMutableTreeNode node = getNodeByPath(tree, entry.getKey(), false);
			if(node!=null) {
				ModuleContent c=((ModuleTreeElement) node.getUserObject()).getData();
				for(TagData t:entry.getValue()) {
					TagData newT=new TagData(t);
					newT.dataHasChanged(true);
					c.set(t.getTagName(), newT);
				}
			}
		}
	}
	
	/**
	 * Replace data in tree with entries in input
	 * @param tree
	 * @param input list of nodepath,list(tagData)
	 */
	public static void replaceData(DefaultMutableTreeNode tree,HashMap<String, List<TagData>> input, boolean insertNode) {
		for(Entry<String, List<TagData>> entry : input.entrySet()) {
			DefaultMutableTreeNode node = getNodeByPath(tree, entry.getKey(), insertNode);
			if(node!=null) {
				ModuleContent c=((ModuleTreeElement) node.getUserObject()).getData();
				for(TagData t:entry.getValue()) {
					c.set(t.getTagName(), new TagData(t));
				}
			}else {
				//node not found -> insert new ( no, because also used for reset objectTree)
				ImporterAgent.getRegistry().getLogger().debug(null, "-- TODO: add new node data for "+entry.getKey()+"?? [MDEHelper::replaceData]");
			}
		}
	}
	

	/**
	 * Set data of newC if newC(data)!=null and newC(data)!=""
	 * @param currentC ModuleContent of type X
	 * @param newC ModuleContent of type X
	 * @return modified currentC
	 */
	public static ModuleContent replaceData(ModuleContent currentC,ModuleContent newC) {
		ModuleContent result = currentC;
		if(newC==null ) {
			return currentC;
		}
		if(currentC==null) {
			result=new ModuleContent(newC);
			result.setAllDataChanged();
			return result;
		}
		
		//same type?
		if(currentC.getType()!=null && currentC.getType().equals(newC.getType())){
			LinkedHashMap<String, TagData> l1=currentC.getList();
			LinkedHashMap<String,TagData> l2=newC.getList();
			if(l2==null) {
				return currentC;
			}
			if(l1==null) {
				result=new ModuleContent(newC);
				result.setAllDataChanged();
				return result;
			}

			for (Map.Entry<String, TagData> entry : l2.entrySet()) {
				String key=entry.getKey();
				TagData valIn=new TagData(entry.getValue());
				if(valIn!=null && valIn.getTagValue()!=null && !valIn.getTagValue().equals("")) {
					if(l1.containsKey(key)) {
						valIn.dataHasChanged(true);
						result.set(key, valIn);
						ImporterAgent.getRegistry().getLogger().debug(null, "\t\t replace "+currentC.getType()+":"+key+"[replaceData]");
					} else {
						ImporterAgent.getRegistry().getLogger().debug(null, "\t\t replace "+currentC.getType()+":"+key+": no key available [replaceData]");
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Set data of newC if newC(data)!=null and newC(data)!=""
	 * @param currentC current ModuleContent of type X
	 * @param newC new input ModuleContent of type X
	 * @param origC content of object at init
	 * @return modified currentC
	 */
	public static ModuleContent replaceUnchangedData(ModuleContent currentC,ModuleContent origC,ModuleContent newC) {
		ModuleContent result = currentC;
		if(newC==null ) {
			return currentC;
		}
		if(currentC==null) {
			result=new ModuleContent(newC);
			result.setAllDataChanged();
			return result;
		}
		
		// no input
		if(origC==null || origC.getList()==null || MDEHelper.isEqual(currentC, origC)) {
			return replaceData(currentC, newC);
		}
		
		//same type?
		if(currentC.getType()!=null && currentC.getType().equals(newC.getType())){
			if(origC.getType()!=null && !currentC.getType().equals(origC.getType())) {
				return replaceData(currentC, newC);
			}
			LinkedHashMap<String, TagData> l1=currentC.getList();
			LinkedHashMap<String,TagData> l2=newC.getList();
			LinkedHashMap<String, TagData> l3=origC.getList();
			if(l2==null) {
				return currentC;
			}
			if(l1==null) {
				result=new ModuleContent(newC);
				result.setAllDataChanged();
				return result;
			}

			for (Map.Entry<String, TagData> entry : l2.entrySet()) {
				String key=entry.getKey();
				TagData valIn=new TagData(entry.getValue());
				if(valIn!=null && valIn.getTagValue()!=null && !valIn.getTagValue().equals("")) {
					if(l1.containsKey(key)) {
						if(  l1.get(key).getTagValue().trim().equals("")|| l1.get(key).equalContent(l3.get(key))) {
							// unchanged content will be replaced by new values
							valIn.dataHasChanged(true);
							result.set(key, valIn);
						}else {
							// content of current has still changed, don't replace this by new value
							ImporterAgent.getRegistry().getLogger().debug(null, "--Don't replace "+key +": "+l1.get(key).tagToString()+" != "+l3.get(key).tagToString());
						}
					} else {
						ImporterAgent.getRegistry().getLogger().debug(null, "\t\t replace "+currentC.getType()+":"+key+": no key available [MDEHelper::replaceUnchangedData]");
					}
				}
			}

		}
		return result;
	}
	
	/**
	 * Set data of newC if data(currentData)==null or data(currentData)==""
	 * @param currentC ModuleContent of type X
	 * @param newC ModuleContent of type X
	 * @return
	 */
	public static ModuleContent completeData(ModuleContent currentC,ModuleContent newC) {
		ModuleContent result = new ModuleContent(currentC);
		if(newC==null) {
			return currentC;
		}
		if(currentC==null) {
			return newC;
		}
		//same type?
		if(currentC.getType()!=null && currentC.getType().equals(newC.getType())){
			LinkedHashMap<String, TagData> l1=currentC.getList();
			LinkedHashMap<String,TagData> l2=newC.getList();
			if(l2==null) {
				return currentC;
			}
			if(l1==null) {
				return newC;
			}
		
			for (Map.Entry<String, TagData> entry : l1.entrySet()) {
				if(entry.getValue()==null || entry.getValue().getTagValue()==null || entry.getValue().getTagValue().equals("")) {
					if(l2.containsKey(entry.getKey()))
						result.set(entry.getKey(), l2.get(entry.getKey()));
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param leafPath list of treePaths as string, e.g.: nodeName1#childName#subchildName#leafName
	 */
	public static void insertObjects(List<String> leafPath,DefaultMutableTreeNode tree) {
		if(leafPath==null || leafPath.isEmpty())
			return;
		for(String path:leafPath) {
			String[] nodes = path.split(SELECTOR);
			if(tree.getUserObject().toString().equals(nodes[0])) {
				if(nodes.length>1) {
					DefaultMutableTreeNode cNode=tree;
					for(int depth=1; depth<nodes.length;depth++) {
						boolean found=false;
						for(int i=0; i<cNode.getChildCount();i++) {
							DefaultMutableTreeNode child=(DefaultMutableTreeNode) cNode.getChildAt(i);
							if(child.getUserObject().toString().equals(nodes[depth])) {
								cNode=child;
								found=true;
								break;
							}
						}
						if(!found) {
							// insert at depth
							cNode.add(new DefaultMutableTreeNode(
									ModuleController.getInstance().createElement(getType(nodes[depth]),cNode)));
							break;
						}
					}
				}
			}
		}
	}

	//TODO: correct? delete also all elements if an object that was not a leaf is deleted?
	public static void deleteObjects(List<String> nodeNames, DefaultMutableTreeNode tree) {
		if(nodeNames==null || nodeNames.isEmpty())
			return;
		for(String path:nodeNames) {
			DefaultMutableTreeNode node=getNodeByPath(tree, path, false);
			if(node!=null) {
				DefaultMutableTreeNode parent=(DefaultMutableTreeNode) node.getParent();
				parent.remove(node);
			}
		}

	}


	/**
	 * Merge list l1 and l2. Overwrite same values in l1 with value in l2.
	 * If one of the list is null or empty return a clone of the other list.
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static List<TagData> mergeTagDataList(List<TagData> l1, List<TagData> l2){
		if(l1==null && l2==null)
			return null;
		if(l1==null)
			return cloneTagList(l2);
		if(l2==null)
			return cloneTagList(l1);
	
		List<TagData> res=new ArrayList<>();
		res=cloneTagList(l2);
		for(TagData t1: l1) {
			boolean found=false;
			for(TagData t2:l2) {
				if(t1.getTagName().equals(t2.getTagName())) {
					found=true;
					break;
				}
			}
			if(!found) {
				res.add(new TagData(t1));
			}
		}
	
		return res;
	}
	

	public static List<TagData> cloneTagList(List<TagData> l) {
		List<TagData> res=new ArrayList<>();
		for(TagData t:l) {
			res.add(new TagData(t));
}
		return res;
	}

	/**
	 * Search for additional nodes in tree1 compare to tree2. 
	 * @param tree1
	 * @param tree2
	 * @return
	 */
	public static List<DefaultMutableTreeNode> getAdditionalNodes(DefaultMutableTreeNode tree1,
			DefaultMutableTreeNode tree2) {
		List<DefaultMutableTreeNode> list = new ArrayList<>();
		
		if(tree1.toString().equals(tree2.toString())) {
			for(int i=0; i< tree1.getChildCount(); i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) tree1.getChildAt(i);
				if(getChild(tree2,child)==null) {
					list.add(child);
				}else {
					list.addAll(getAdditionalNodes(child, getChild(tree2,child)));
				}
			}
			
		}
		return list;
	}
	
	private static DefaultMutableTreeNode getChild(DefaultMutableTreeNode tree, DefaultMutableTreeNode child) {
		for(int i=0; i<tree.getChildCount(); i++) {
			if(tree.getChildAt(i).toString().equals(child.toString()))
				return (DefaultMutableTreeNode) tree.getChildAt(i);
		}
		return null;
	}
	
	
	
}
