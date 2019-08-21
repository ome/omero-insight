package org.openmicroscopy.shoola.agents.fsimporter.mde;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ome.xml.model.LightPath;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;
import ome.xml.model.FilterSet;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;


public class MDEHelper {

	public static void printList(String string, HashMap<String,List<TagData>> list) {
		String result="\t## "+string+" ##\n";
		if(list==null) {
			System.out.println(result+" NO INPUT");
			return;
		}
		for(Map.Entry<String, List<TagData>> entry: list.entrySet()) {
			result+="\t"+entry.getKey()+" :\n ";
			for(TagData t: entry.getValue()) {
				result+="\t\t"+t.getTagName()+" : "+t.getTagValue()+"\n";
			}
		}
		System.out.println(result);
	}


	/**
	 * Get input from given tree
	 * @param contentTree
	 */
	public static HashMap<String,List<TagData>> getInput(DefaultMutableTreeNode contentTree) {
		if(contentTree==null)
			return null;

		HashMap<String,List<TagData>> result=new HashMap<>();

//		System.out.println("-- get input of "+contentTree.getUserObject().toString());
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
								id= ((ModuleTreeElement) p.getUserObject()).getElementName()+" | "+id;
								p=(DefaultMutableTreeNode) p.getParent();
							}
							if(p!=null)
								id= ((ModuleTreeElement) p.getUserObject()).getElementName()+" | "+id;

						}else {
							id=((ModuleTreeElement) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getElementName()+" | "+id;
						}
					}
//					System.out.println("-- Input of "+"["+id+"]");
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
					System.out.println("-- Node "+nodeName+" doesn't exists, INSERT at depth "+depth);
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
	 * Trees are equal if same type and same subtrees (same childIndex- what is about childindex?)
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static boolean compareTrees(DefaultMutableTreeNode n1,DefaultMutableTreeNode n2) {
		if(compareNodes(n1,n2)) {
			System.out.println("-- Compare Nodes [equal]: "+n1.getUserObject().toString()+" - "+n2.getUserObject().toString());
			for(int i = 0 ; i < n1.getChildCount(); i++) {
				if(findNode((DefaultMutableTreeNode) n1.getChildAt(i),n2)!=null) {
//					count++;
				}else {
					//find by subtree similarity
					List<DefaultMutableTreeNode> childList=
							getListOfChilds(((ModuleTreeElement)((DefaultMutableTreeNode)n1.getChildAt(i)).getUserObject()).getType(),n2);
				}

			}
		}
		return false;
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

		Enumeration e = tree.breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode node =
					(DefaultMutableTreeNode)e.nextElement();
			if(compareNodes(n, node)) {
				System.out.println("-- Search for "+n.getUserObject().toString() +" - FOUND [MDEParser::findNode]");
				return node;
			}
		}
		System.out.println("-- Search for "+n.getUserObject().toString() +" - NOT FOUND [MDEParser::findNode]");
		return null;
	}

	/**
	 * Find MDETreeElement in tree by name.
	 * @param tree
	 * @param name
	 * @return
	 */
//	private static DefaultMutableTreeNode getNodeByName(DefaultMutableTreeNode tree, String name) {
//		Enumeration e = tree.breadthFirstEnumeration();
//		while(e.hasMoreElements()) {
//			DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
//			if(node.getUserObject().toString().equals(name)) {
//				return node;
//			}else {
//				//search in subtree
//				DefaultMutableTreeNode result=getNodeByName(node,name);
//				if(result!=null)
//					return result;
//			}
//		}
//		return null;
//	}

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
//			System.out.println("Compare: "+node.getUserObject().toString()+" - "+name);
			if(((ModuleTreeElement) node.getUserObject()).getElementName().trim().equals(name.trim())) {
				return node;
			}
		}
		return null;
	}

	private static DefaultMutableTreeNode getNodeByPath(DefaultMutableTreeNode tree, String path) {
		if(path==null)
			return null;
		String[] nodeNames=path.split("\\|");

//		String res="";
//		for(String s:nodeNames) {
//			res+=s+" -> ";
//		}
//		System.out.println("-- path elements "+nodeNames.length+"; paths: "+res);

		//get first parent:
		DefaultMutableTreeNode parent = getChildByName(tree, nodeNames[0]);
		if(parent!=null) {
			for(int i=1; i< nodeNames.length;i++) {
				parent = getChildByName(parent,nodeNames[i]);
			}
		}
		return parent;
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
	 * Identify object c2 by id or if id is null, by fields that are set in c1.
	 * The match should be above the given percentage.
	 * @param c1
	 * @param c2
	 * @param percent
	 * @return
	 */
	public static boolean isEqual(ModuleContent c1, ModuleContent c2,int percent) {
		boolean result=false;
		if(c1!=null && c2!=null) {
			LinkedHashMap<String,TagData> tList1=c1.getList();
			LinkedHashMap<String,TagData> tList2=c2.getList();

			if(tList1==null && tList2==null) {
				result=false;
			}
			else{
				if((tList1==null && tList2!=null) || (tList1!=null &&tList2==null) ||
						tList1.size()!=tList2.size()) {
					result=false;
				}else {
					//identify by id
					if(tList2.containsKey(TagNames.ID) && tList1.containsKey(TagNames.ID) && tList2.get(TagNames.ID)!=null) {
							return tList2.get(TagNames.ID).equalContent(tList1.get(TagNames.ID));

					}
					//ni id available
					int falsePosCount=0;
					int truePosCount=0;
					for (Map.Entry<String, TagData> entry : tList1.entrySet()) {
						String key=entry.getKey();
						TagData val1=entry.getValue();
						//TODO: value c1 has to set and equal to c2 ->truePosCount++
						if(tList2.containsKey(key)&& val1.equalContent(tList2.get(key))) {
							truePosCount++;
						}else {
							falsePosCount++;
						}
					}
					if(percent == 100 && falsePosCount>0) {
						result=false;
					}else {
						int truePosPercent = (100*truePosCount)/tList1.entrySet().size();
						System.out.println("-- equal procent: "+truePosPercent);
						if(truePosPercent<percent) {
							result= false;
						}else {
							result=true;
						}
					}
				}
			}
		}
		return result;
	}

	public static boolean isEqual(ModuleContent c1, ModuleContent c2) {
		boolean result=false;
		if(c1!=null && c2!=null) {
			LinkedHashMap<String,TagData> tList1=c1.getList();
			LinkedHashMap<String,TagData> tList2=c2.getList();

			if(tList1==null && tList2==null) {
				result=true;
			}
			else{
				if((tList1==null && tList2!=null) || (tList1!=null &&tList2==null) ||
						tList1.size()!=tList2.size()) {
					result=false;
				}else {
					int falsePosCount=0;
					for (Map.Entry<String, TagData> entry : tList1.entrySet()) {
						String key=entry.getKey();
						if(!(tList2.containsKey(key)&& tList2.get(key).equals(entry.getValue().getTagValue()))){
							falsePosCount++;
						}
					}
					if(falsePosCount>0) {
						result=false;
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
			System.out.println("-- replace data for "+entry.getKey()+" [MDEHelper::replaceData]");
			DefaultMutableTreeNode node = getNodeByPath(tree, entry.getKey());
			if(node!=null) {
//				System.out.println("-- replace data at "+node.getUserObject().toString());
				ModuleContent c=((ModuleTreeElement) node.getUserObject()).getData();
				for(TagData t:entry.getValue()) {
					c.set(t.getTagName(), new TagData(t));
				}
			}
		}
	}

	private static ModuleTreeElement getNodeObject(String key) {
		// TODO Auto-generated method stub
		return null;
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
			System.out.println("-- given new element is null -> return current element [replaceData]");
			return currentC;
		}
		if(currentC==null) {
			System.out.println("-- current element is null -> return new element [replaceData]");
			result=new ModuleContent(newC);
			result.setAllDataChanged();
			return result;
		}

		//same type?
		if(currentC.getType()!=null && currentC.getType().equals(newC.getType())){
			LinkedHashMap<String, TagData> l1=currentC.getList();
			LinkedHashMap<String,TagData> l2=newC.getList();
			if(l2==null) {
				System.out.println("-- data of given new element are null -> return current element [replaceData]");
				return currentC;
			}
			if(l1==null) {
				System.out.println("-- data of current element are null -> return new element [replaceData]");
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
					}
				}
			}

		}else {
			System.out.println("--ERROR: Elements type not equal ("+currentC.getType()+" - "+newC.getType()+") [MDEHelper::replaceData]");
		}
		return result;
	}

	/**
	 * Set data of newC if newC(data)!=null and newC(data)!=""
	 * @param currentC ModuleContent of type X
	 * @param newC ModuleContent of type X
	 * @return modified currentC
	 */
	public static ModuleContent replaceUnchangedData(ModuleContent currentC,ModuleContent origC,ModuleContent newC) {
		ModuleContent result = currentC;//new ModuleContent(currentC);
		if(newC==null ) {
			System.out.println("-- given new element is null -> return current element [replaceData]");
			return currentC;
		}
		if(currentC==null) {
			System.out.println("-- current element is null -> return new element [replaceData]");
			result=new ModuleContent(newC);
			result.setAllDataChanged();
			return result;
		}



		if(origC==null || origC.getList()==null || MDEHelper.isEqual(currentC, origC)) {
			return replaceData(currentC, newC);
		}

		//same type?
		if(currentC.getType()!=null && currentC.getType().equals(newC.getType())){
			if(origC.getType()!=null && !currentC.getType().equals(origC.getType())) {
				System.out.println("ERROR: original and current element has different types [MDEHelper::replaceUnchangedData]]");
				return replaceData(currentC, newC);
			}
			LinkedHashMap<String, TagData> l1=currentC.getList();
			LinkedHashMap<String,TagData> l2=newC.getList();
			LinkedHashMap<String, TagData> l3=origC.getList();
			if(l2==null) {
				System.out.println("-- data of given new element are null -> return current element [replaceData]");
				return currentC;
			}
			if(l1==null) {
				System.out.println("-- data of current element are null -> return new element [replaceData]");
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
							valIn.dataHasChanged(true);
							result.set(key, valIn);
						}else {
							System.out.println("--Don't replace "+key);
						}
					}
				}
			}

		}else {
			System.out.println("--ERROR: Elements type not equal ("+currentC.getType()+" - "+newC.getType()+") [MDEHelper::replaceData]");
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
//			System.out.println("-- given new element is null -> return current element [completeData]");
			return currentC;
		}
		if(currentC==null) {
//			System.out.println("-- current element is null -> return new element [completeData]");
			return newC;
		}
		//same type?
		if(currentC.getType()!=null && currentC.getType().equals(newC.getType())){
			LinkedHashMap<String, TagData> l1=currentC.getList();
			LinkedHashMap<String,TagData> l2=newC.getList();
			if(l2==null) {
//				System.out.println("-- data of given new element are null -> return current element [completeData]");
				return currentC;
			}
			if(l1==null) {
//				System.out.println("-- data of current element are null -> return new element [completeData]");
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





}
