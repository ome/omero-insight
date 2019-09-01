package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.DichroicConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.util.MonitorAndDebug;

/**
 * TODO: define in config if tree is editable or not
 * @author Kunis
 *
 */
public class ModuleTree extends JPanel implements ActionListener{

	private static String POPUP_COPY = "copy";
	private static String POPUP_PASTE = "paste";
//	private static String POPUP_CUT = "cut";
	public static final String POPUP_DEL = "delete";
	private static String POPUP_INSERT ="insert";
	private static String POPUP_INSERT_ALL ="insertAll";
	
	private JMenu insert;
	
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;
	private ModuleController controller;
	
	private DefaultMutableTreeNode copyVal;
	private JPopupMenu popup;
	
	private boolean changeTreeStructure;
	
	
	public ModuleTree(DefaultMutableTreeNode elem, ActionListener l) {
		super(new GridLayout(1,0));
		this.controller=ModuleController.getInstance();
		this.changeTreeStructure=false;
		buildContextMenu(l);
		buildTree(elem);
//		selectFirstNode();
		
	}
	
	
	private void buildTree(DefaultMutableTreeNode elem)
	{
		if(((ModuleTreeElement) elem.getUserObject()).getType().equals(TagNames.OME_ROOT)) {
			root=elem;
		}else {
			root= new DefaultMutableTreeNode(new ModuleTreeElement(null, null));//new DefaultMutableTreeNode("Modules:");
			root.add(elem);
		}
		tree = new JTree(root);
		treeModel=(DefaultTreeModel) tree.getModel();
		treeModel.addTreeModelListener(new ModuleTreeListener());
		
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}
		});
		JScrollPane treeView=new JScrollPane(tree);
		add(treeView);
		
	}
	
	private void buildContextMenu(ActionListener l)
	{
		popup = new JPopupMenu();
//		JMenu view = new JMenu("View");
//		JMenuItem view_browse = new JMenuItem("Browse");
//		JMenuItem view_compare = new JMenuItem("Compare");
//		view.add(view_browse);
//		view.add(view_compare);
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem edit_copy = new JMenuItem("Copy");
		edit_copy.setActionCommand(POPUP_COPY);
		edit_copy.addActionListener(this);
		
		JMenuItem edit_paste = new JMenuItem("Paste");
		edit_paste.setActionCommand(POPUP_PASTE);
		edit_paste.addActionListener(this);
		
		JMenuItem edit_delete = new JMenuItem("Delete");
		edit_delete.setActionCommand(POPUP_DEL);
		edit_delete.addActionListener(l);
		
		edit.add(edit_copy);
		edit.add(edit_paste);
		edit.add(edit_delete);
		
		insert = new JMenu("Insert Node");
		insert.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				insert.removeAll();
				addMenuItems(null);
				insert.revalidate();
				insert.repaint();
			}
			@Override
			public void menuDeselected(MenuEvent e) {}
			@Override
			public void menuCanceled(MenuEvent e) {	}
		});
		popup.add(insert);
//		popup.add(view);
		popup.add(edit);
		
		
	}

	
	
	protected void addMenuItems(DefaultMutableTreeNode current) {
		if(current == null) {
			current=(DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
		}
		if(((ModuleTreeElement) current.getUserObject()).getType().equals(TagNames.OME_ROOT)) {
			JMenuItem node = new JMenuItem("Standard tree");
			node.setActionCommand(POPUP_INSERT_ALL);
			node.addActionListener(this);
			insert.add(node);
		}
		String[] list = controller.getPossibleChilds(((ModuleTreeElement)current.getUserObject()).getType());
		if(list!=null && list.length>0) {
			for(String s:list) {
				JMenuItem node = new JMenuItem(s);
				node.setActionCommand(POPUP_INSERT);
				node.addActionListener(this);
				insert.add(node);
			}
		}else {
			JMenuItem node = new JMenuItem("No childs to insert");
			insert.add(node);
		}
	}


	/**
	 * show node at jtree and his childs according moduleList
	 * @param parent
	 * @param elem
	 */
	private void visualizeNodes(DefaultMutableTreeNode parent, DefaultMutableTreeNode thisElem) {
		if(thisElem!=null) {
			if(parent==null) {
				parent = root;
			}
			treeModel.insertNodeInto(thisElem, parent, parent.getChildCount());

			//make sure the tree is visible in the panel on this path
			if(thisElem!=null && tree!=null)
				tree.scrollPathToVisible(new TreePath(thisElem.getPath()));
		}
	}
	
	private String getName(DefaultMutableTreeNode thisElem) {
		return thisElem.getUserObject().toString();
	}

	private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent,DefaultMutableTreeNode thisElem)
	{
		if(parent==null) {
			parent = root;
		}
		String pType=((ModuleTreeElement) parent.getUserObject()).getType();
		// it is alowed to insert object at this point?
		if(thisElem!=null) {
			if(((ModuleTreeElement) thisElem.getUserObject()).getData().hasParent(pType)) {
				MonitorAndDebug.printConsole("-- addNode: "+getName(thisElem) + " at "+parent.getUserObject().toString());
				this.changeTreeStructure=true;
				((ModuleTreeElement)thisElem.getUserObject()).setChildIndex(parent);

				//visualize node in jtree
				treeModel.insertNodeInto(thisElem, parent, parent.getChildCount());
				//			parent.add(thisElem);
				//			treeModel.reload();
				//make sure the tree is visible in the panel on this path
				if(thisElem!=null && tree!=null)
					tree.scrollPathToVisible(new TreePath(thisElem.getPath()));

				return thisElem;
			}else {
				System.out.println("-- can't insert node at selected parent! Allowed parents: "+Arrays.toString(((ModuleTreeElement) thisElem.getUserObject()).getData().getParents()));
			}
		}
		return null;
		
	}

	//TODO: doesn't work clean
	public void reset(DefaultMutableTreeNode elem,ModuleController controller)
	{
		this.controller=controller;
		this.removeAll();
		this.changeTreeStructure=true;
		buildTree(elem);
		revalidate();
		repaint();
	}

	
	
	public void removeNodeFromParent(DefaultMutableTreeNode current) {
		treeModel.removeNodeFromParent(current);
	}
	
	/**
	 * Paste node and it's childs. Update indizes of ModuleTreeElements
	 * @param node
	 */
	public void pasteNode(DefaultMutableTreeNode node) {
		this.changeTreeStructure=true;
		DefaultMutableTreeNode cNode=insertNode(node);
	}

	public DefaultMutableTreeNode insertNode(String type) {
		this.changeTreeStructure=true;
		TreePath parentPath=tree.getSelectionPath();
		DefaultMutableTreeNode parent=root;
		if(parentPath!=null) {
			parent=(DefaultMutableTreeNode)parentPath.getLastPathComponent();
		}
		ModuleContent c=controller.getContentOfType(type);
		ModuleTreeElement choice=null;
		if(c.getList()==null) {
			choice=new ModuleTreeElement(c,parent);
		}else {
			choice=new ModuleTreeElement(type,null, "", c,parent);
		}
		return addNode(parent,new DefaultMutableTreeNode(choice));
	}
	/**
	 * Insert node to moduleList.
	 * @param node
	 */
	public DefaultMutableTreeNode insertNode(DefaultMutableTreeNode node) {
		this.changeTreeStructure=true;
		if(node==null)
			return null;
		
		TreePath parentPath=tree.getSelectionPath();
		DefaultMutableTreeNode pNode = root;
		if(parentPath!=null) {
			pNode=(DefaultMutableTreeNode) parentPath.getLastPathComponent();
		}
		DefaultMutableTreeNode cNode= addNode(pNode,node);
		
		return cNode;
	}
	
	
	
	
	
	/*
	 * This code is based on an example provided by Richard Stanford, 
	 * a tutorial reader.
	 * see: https://docs.oracle.com/javase/tutorial/uiswing/examples/components/DynamicTreeDemoProject/src/components/DynamicTree.java
	 */
	class ModuleTreeListener implements TreeModelListener{

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

			/*
			 * If the event lists children, then the changed
			 * node is the child of the node we've already
			 * gotten.  Otherwise, the changed node and the
			 * specified node are the same.
			 */

			int index = e.getChildIndices()[0];
			node = (DefaultMutableTreeNode)(node.getChildAt(index));
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

	public void addTreeSelectionListener(MDEContent mdeContent) {
		tree.addTreeSelectionListener(mdeContent);
		
	}
	

	public DefaultMutableTreeNode getLastSelectedPathComponent() {
		return (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
	}
	
	public void printTree(DefaultMutableTreeNode node, String title) {
		if(node == null)
			node=root;
		
		try {
			MonitorAndDebug.printConsole(title+ node.getUserObject().toString());
			for(int i = 0 ; i < node.getChildCount(); i++)
				printTree((DefaultMutableTreeNode)node.getChildAt(i), title + "  "); 
		}catch(Exception ex) {
			for(int i = 0 ; i < node.getChildCount(); i++)
				printTree((DefaultMutableTreeNode)node.getChildAt(i), title + "  "); 
		}
	}

	

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TreePath selectedNode = tree.getSelectionPath();
		DefaultMutableTreeNode current=null;
		if(selectedNode!=null) {
			current = (DefaultMutableTreeNode) selectedNode.getLastPathComponent();
			if(current ==null)
				return;
		}
		if(POPUP_COPY.equals(cmd)) {
			copyVal=current;
			
		}
		if(POPUP_PASTE.equals(cmd)) {
			if(copyVal!=null) {
				// clone node here for multi paste operation
				pasteNode(cloneTreeNode(copyVal));
			}
		}
		
		if(POPUP_INSERT.equals(cmd)) {
			insertNode(((JMenuItem) e.getSource()).getText());
		}
		if(POPUP_INSERT_ALL.equals(cmd)) {
			DefaultMutableTreeNode tree=controller.getTree();
			for(int i=0; i<tree.getChildCount();i++) {
				insertNode((DefaultMutableTreeNode)tree.getChildAt(i));
			}
		}
		
	}


	
	public static DefaultMutableTreeNode cloneTreeNode(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode cloneNode = null;
		if(node ==null || !(node.getUserObject() instanceof ModuleTreeElement) ) {
			System.out.println("--WARNING: node is null or not a ModuleTreeElement "+node.toString()+"[ModuleTree::cloneTreeNode]");
		}else {
			cloneNode=new DefaultMutableTreeNode(new ModuleTreeElement((ModuleTreeElement) node.getUserObject()));
			for(int i = 0 ; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode child=cloneTreeNode((DefaultMutableTreeNode) node.getChildAt(i));
				if(child!=null)
					cloneNode.add(child);
			}
		}
		return cloneNode;
	}

	/**
	 * Returns a copy of JTree without data
	 * @param root
	 * @return
	 */
	public static DefaultMutableTreeNode getEmptyStructurTree(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode newTree = new DefaultMutableTreeNode(new ModuleTreeElement((ModuleTreeElement) node.getUserObject()));
		for(int i = 0 ; i < node.getChildCount(); i++) {
			newTree.add(getEmptyStructurTree((DefaultMutableTreeNode) node.getChildAt(i)));
		}
		return null;
	}
	
	
	public DefaultMutableTreeNode getRoot()
	{
		return root;
	}
	
	public JTree getTree()
	{
		return tree;
	}
	
	public boolean changeTreeStructure() {
		return changeTreeStructure;
	}
	
}
