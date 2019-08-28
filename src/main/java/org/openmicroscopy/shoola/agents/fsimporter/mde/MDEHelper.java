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

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;


public class MDEHelper {
	
	public static final String SELECTOR="#";

	public static void printList(String string, HashMap<String,List<TagData>> list) {
		String result="\t## "+string+" ##\n";
		if(list==null || list.isEmpty()) {
			MonitorAndDebug.printConsole(result);
			return;
		}
		for(Map.Entry<String, List<TagData>> entry: list.entrySet()) {
			result+="\t"+entry.getKey()+" :\n ";
			for(TagData t: entry.getValue()) {
				result+="\t\t"+t.tagToString()+"\n";
			}
		}
		MonitorAndDebug.printConsole(result);
	}
	
	
	/**
	 * Get input from given tree
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
			while(e.hasMoreElements()) {
				DefaultMutableTreeNode node =
						(DefaultMutableTreeNode)e.nextElement();
				if(!(((ModuleTreeElement) node.getUserObject()).isContainer())) {
					String id=node.getUserObject().toString();
					
					if(node.getParent()!=null) {
						if(((ModuleTreeElement) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).isContainer()) {
							DefaultMutableTreeNode p=(DefaultMutableTreeNode) node.getParent();
							while(p!=null && ((ModuleTreeElement) p.getUserObject()).isContainer()) {
								id= ((ModuleTreeElement) p.getUserObject()).getElementName()+SELECTOR+id;
								p=(DefaultMutableTreeNode) p.getParent();
							}
							if(p!=null)
								id= ((ModuleTreeElement) p.getUserObject()).getElementName()+SELECTOR+id;
							
						}else {
							id=((ModuleTreeElement) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getElementName()+" | "+id;
						}
					}
					List<TagData> res=((ModuleTreeElement) node.getUserObject()).getData().getInput();
					if(res!=null && !res.isEmpty())
						result.put(id, res);
				}
			}
		}
		return result;
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
					MonitorAndDebug.printConsole("-- Node "+nodeName+" doesn't exists, INSERT at depth "+depth);
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
		System.out.println("-- Compare Trees: "+n1.getUserObject().toString()+" -- "+n2.getUserObject().toString());
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
			System.out.println("\t SIMILAR: "+leafsN1);
			System.out.println("\t DIFFERENT: "+different);
			
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
		System.out.println("\t => Additional elements in first tree: "+result);
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
	 * @param nodeString
	 * @param parentString
	 * @param tree
	 * @return
	 */
	private static DefaultMutableTreeNode findNode(DefaultMutableTreeNode n, DefaultMutableTreeNode tree) {
		 for (Enumeration e = tree.preorderEnumeration(); e.hasMoreElements();) {
	            DefaultMutableTreeNode d =  (DefaultMutableTreeNode)e.nextElement();
	            if(compareNodes(n,d)) {
//	            	System.out.println("-- Search for "+n.getUserObject().toString() +" - FOUND at level "+d.getLevel()+" [MDEParser::findNode]");
	            	return d;
	            } 
		 }
//		 System.out.println("-- Search for "+n.getUserObject().toString() +" - NOT FOUND [MDEParser::findNode]");
		 return null;
	}
	

	
	/**
	 * Find MDETreeElement direct child of tree by name.
	 * @param tree
	 * @param name
	 * @return
	 */
	private static DefaultMutableTreeNode getChildByName(DefaultMutableTreeNode tree, String name) {
		if(tree==null)
			return null;
		
		Enumeration e = tree.breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
			if(((ModuleTreeElement) node.getUserObject()).getElementName().trim().equals(name.trim())) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param tree
	 * @param path
	 * @return
	 */
	private static DefaultMutableTreeNode getNodeByPath(DefaultMutableTreeNode tree, String path) {
		if(path==null)
			return null;
		String[] nodeNames=path.split(SELECTOR);
		
		//get first parent:
		DefaultMutableTreeNode parent = getChildByName(tree, nodeNames[0]);
		
		if(parent!=null) {
			for(int i=1; i< nodeNames.length;i++) {
				DefaultMutableTreeNode child = getChildByName(parent,nodeNames[i]);
				if(child==null) {
					MonitorAndDebug.printConsole("TODO: insert new node here "+parent.getUserObject().toString());
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
//			MonitorAndDebug.printConsole("# load import data\t[MDEModelManager::setImportData]");
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
//										MonitorAndDebug.printConsole("\t\t "+c1.getType()+": "+key+" not equal: "+tList2.get(key).getTagValue()+" -- "+entry.getValue().getTagValue());
										falsePosCount++;
									}
								}else {
									if(!tList2.get(key).getTagValue().equals(entry.getValue().getTagValue())){
										//									MonitorAndDebug.printConsole("\t\t "+c1.getType()+": "+key+" not equal: "+tList2.get(key).getTagValue()+" -- "+entry.getValue().getTagValue());
										falsePosCount++;
									}
								}
							}else {
//								MonitorAndDebug.printConsole("\t\t "+c1.getType()+": has no key: "+key);
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
	 * Replace data in tree with entries in input
	 * @param tree
	 * @param input list of nodepath,list(tagData)
	 */
	public static void replaceData(DefaultMutableTreeNode tree,HashMap<String, List<TagData>> input) {
		for(Entry<String, List<TagData>> entry : input.entrySet()) {
			DefaultMutableTreeNode node = getNodeByPath(tree, entry.getKey());
			if(node!=null) {
				System.out.println("-- replace data for "+entry.getKey()+" [MDEHelper::replaceData]");
				ModuleContent c=((ModuleTreeElement) node.getUserObject()).getData();
				for(TagData t:entry.getValue()) {
					c.set(t.getTagName(), new TagData(t));
				}
			}else {
				//node not found -> insert new
				System.out.println("-- cannot replace data for "+entry.getKey()+" [MDEHelper::replaceData]");
				MonitorAndDebug.printConsole("-- TODO: add new node data for "+entry.getKey()+" [MDEHelper::replaceData]");
				
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
						MonitorAndDebug.printConsole("\t\t replace "+currentC.getType()+":"+key+"[replaceData]");
					}else {
						MonitorAndDebug.printConsole("\t\t replace "+currentC.getType()+":"+key+": no key available [replaceData]");
					}
				}
			}

		}else {
			System.out.println("-- WARNING: Elements type not equal ("+currentC.getType()+" - "+newC.getType()+") [MDEHelper::replaceData]");
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
		ModuleContent result = currentC;//new ModuleContent(currentC);
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
				System.out.println("-- ERROR: original and current element has different types [MDEHelper::replaceUnchangedData]]");
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
						if( l1.get(key).equals(l3.get(key))) {
							// unchanged content will be replaced by new values
							valIn.dataHasChanged(true);
							result.set(key, valIn);
						}else {
							// content of current has still changed, don't replace this by new value
							MonitorAndDebug.printConsole("--Don't replace "+key +": "+l1.get(key).tagToString()+" != "+l3.get(key).tagToString());
						}
					}else {
						MonitorAndDebug.printConsole("\t\t replace "+currentC.getType()+":"+key+": no key available [MDEHelper::replaceUnchangedData]");
					}
				}
			}

		}else {
			System.out.println("--ERROR: Elements type not equal ("+currentC.getType()+" - "+newC.getType()+") [MDEHelper::replaceUnchangedData]");
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
		}else {
			System.out.println("--ERROR: Elements type not equal [MDEHelper::completeData]");
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
			}else {
				System.out.println("-- WARNING: root is not equal "+nodes[0]+" -- "+tree.getUserObject().toString()+"[MDEHelper::insertObjects()]");
			}
		}
	}

	//TODO: correct? delete also all elements if an object that was not a leaf is deleted?
	public static void deleteObjects(List<String> nodeNames, DefaultMutableTreeNode tree) {
		if(nodeNames==null || nodeNames.isEmpty())
			return;
		for(String path:nodeNames) {
			DefaultMutableTreeNode node=getNodeByPath(tree, path);
			DefaultMutableTreeNode parent=(DefaultMutableTreeNode) node.getParent();
			parent.remove(node);
		}

	}


	
	
	
}
