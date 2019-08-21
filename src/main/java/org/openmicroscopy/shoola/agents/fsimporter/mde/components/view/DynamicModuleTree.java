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
public class DynamicModuleTree extends JPanel implements ActionListener{

	private static String ADD_NODE_CMD = "add";
	private static String DELETE_NODE_CMD = "delete";
	private static String RESET_TREE_CMD = "reset";
	
	private ModuleTree treePanel;
	private DefaultMutableTreeNode element;
	private ModuleController initController;
	private ModuleController controller;
	
	
//	public DynamicModuleTree(OME ome,ModuleController controller) {
//		super(new BorderLayout());
//		this.element=controller.initContent(ome, null);
//		this.initController=controller;
//		this.controller=new ModuleController(controller);
//		treePanel = new ModuleTree(element,this.controller);
//		
//		add(treePanel,BorderLayout.CENTER);
//		add(generateButtonPane(),BorderLayout.SOUTH);
//	}
	
	public DynamicModuleTree(DefaultMutableTreeNode elem, ModuleController controller) {
		super(new BorderLayout());
		this.element=elem;
		this.initController=controller;
		this.controller=controller;
		treePanel = new ModuleTree(elem,this.controller);
		
		
		add(treePanel,BorderLayout.CENTER);
		add(generateButtonPane(),BorderLayout.SOUTH);
	}

	public DynamicModuleTree(ModuleController controller) {
		this(controller.getTree(),controller);
	}
	
	
	private JPanel generateButtonPane() {
		JButton resetBtn= new JButton("Reset");
		resetBtn.setActionCommand(RESET_TREE_CMD);
		resetBtn.addActionListener(this);
		
		JPanel btnPanel = new JPanel(new GridLayout(0, 1));
		btnPanel.add(resetBtn);
		
		return btnPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(RESET_TREE_CMD.equals(cmd)) {
			treePanel.reset(element,initController);
			treePanel.printTree(null," ");
		}
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
	
	
	public DefaultMutableTreeNode selectFirstNode() {
		if(getRootNode()==null || getRootNode().getChildCount()==0)
			return null;
		
		DefaultMutableTreeNode firstNode= (DefaultMutableTreeNode) getRootNode().getFirstChild();
		if(firstNode !=null) {
			getTree().getSelectionModel().setSelectionPath(new TreePath(firstNode.getPath()));
			
//			tree.expandPath(new TreePath(firstNode.getPath()));
//			TreeSelectionListener[] listener=tree.getTreeSelectionListeners();
//			if(listener!=null && listener.length>0) {
//			for(TreeSelectionListener l:listener) {
//				l.valueChanged(null);
//			}
//			}
		}
		return firstNode;
	}
	
	
	//see: https://stackoverflow.com/questions/23882640/how-to-set-the-title-of-a-jcombobox-when-nothing-is-selected
//	class NodeList_CB_Renderer extends JLabel implements ListCellRenderer
//    {
//        private String _title;
//
//        public NodeList_CB_Renderer(String title){
//            _title = title;
//        }
//
//        @Override
//        public Component getListCellRendererComponent(JList list, Object value,
//                int index, boolean isSelected, boolean hasFocus)
//        {
//            if (index == -1 && value == null) setText(_title);
//            else setText(value.toString());
//            return this;
//        }
//    }

	public JTree getTree() {
		if(treePanel== null)
			return null;
		return treePanel.getTree();
	}
	
	
}

