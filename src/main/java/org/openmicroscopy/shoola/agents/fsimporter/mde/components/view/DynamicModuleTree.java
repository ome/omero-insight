package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ome.xml.model.OME;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;

/**
 * You can add and remove elements. Kind of available Elements has to declare in a JSON or RDF file like:
 * Module: name:
 * 		   List of Tags:
 *         List of default values:
 * OME elements has to named by OME:<OMEName>. See also list of available tags for OME elements        
 * You can create your MDE specification by specification of the ModuleTree in the GUI and than-> export specification
 * 
 * @author Kunis
 *
 */
public class DynamicModuleTree extends JPanel {

	private static String ADD_NODE_CMD = "add";
	private static String DELETE_NODE_CMD = "delete";
	public static final String RESET_TREE_CMD = "reset";
	
	private ModuleTree treePanel;
	private ModuleController controller;
	
	
	public DynamicModuleTree(DefaultMutableTreeNode elem,ActionListener listener) {
		super(new BorderLayout());
		this.controller=ModuleController.getInstance();
		treePanel = new ModuleTree(elem,listener);
		
		
		add(treePanel,BorderLayout.CENTER);
		add(generateButtonPane(listener),BorderLayout.SOUTH);
	}

	public DynamicModuleTree(ActionListener listener) {
		this(ModuleController.getInstance().getTree(),listener);
	}
	
	
	private JPanel generateButtonPane(ActionListener l) {
		JButton resetBtn= new JButton("Reset");
		resetBtn.setActionCommand(RESET_TREE_CMD);
		resetBtn.addActionListener(l);
		
		JPanel btnPanel = new JPanel(new GridLayout(0, 1));
		btnPanel.add(resetBtn);
		
		return btnPanel;
	}


	public void addTreeSelectionListener(MDEContent mdeContent) {
		treePanel.addTreeSelectionListener(mdeContent);
		
	}

	public DefaultMutableTreeNode getLastSelectedPathComponent() {
		return treePanel.getLastSelectedPathComponent();
	}
	
	public DefaultMutableTreeNode getRootNode() {
		return treePanel.getRoot();
	}
	public ModuleTree getModuleTree() {
		return treePanel;
	}
	
	public DefaultMutableTreeNode selectFirstNode() {
		if(getRootNode()==null || getRootNode().getChildCount()==0)
			return null;
		
		DefaultMutableTreeNode firstNode= (DefaultMutableTreeNode) getRootNode().getFirstChild();
		if(firstNode !=null) {
			getTree().getSelectionModel().setSelectionPath(new TreePath(firstNode.getPath()));
		}
		return firstNode;
	}
	

	public JTree getTree() {
		if(treePanel== null)
			return null;
		return treePanel.getTree();
	}
	
}

