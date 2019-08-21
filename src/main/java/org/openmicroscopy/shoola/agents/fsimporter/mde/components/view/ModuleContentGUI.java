package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.openmicroscopy.shoola.util.ui.JXTaskPaneContainerSingle;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

/**
 * Visualize ModuleContent
 * @author Kunis
 *
 */
public class ModuleContentGUI extends JPanel {
	Color color=new Color(240, 240, 240);
	
	private ModuleController controller;
	private LinkedHashMap<String, ObjectTable> hardwareTables;
	
//	private String name;
	
	public ModuleContentGUI() {
		this(null,null);
	}
	
	public ModuleContentGUI(DefaultMutableTreeNode root,LinkedHashMap<String, ObjectTable> hardwareTables) {
		setLayout(new BorderLayout());
		this.controller=ModuleController.getInstance();
		this.hardwareTables=hardwareTables;
		if(root!=null) {
//			this.name=root.getUserObject().toString();
			System.out.println("---------------------------------------------------------------------------");
//			System.out.println("-- vizualise content: "+root.getUserObject().toString()+"------------------------");
			PropertyChangeListener listener=new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					System.out.printf("-- Property change: '%s': '%s' -> '%s'%n [ModuleContent]",e.getPropertyName(),e.getOldValue(),e.getNewValue());
					//TODO: add/remove detector, lightSource, lightPath
//					if(e.getPropertyName().equals("numberOfChannels") && 
//							(Integer)e.getOldValue()<(Integer)e.getNewValue())
//						addModules();
//					else {
//						removeModules();
//					}
//					paintSubComponents(true);
				}
			};
			JXTaskPaneContainer panel= new JXTaskPaneContainer();
			panel.setBackground(UIUtilities.BACKGROUND);
			if (panel.getLayout() instanceof VerticalLayout) {
				VerticalLayout vl = (VerticalLayout) panel.getLayout();
				vl.setGap(2);
			}
			panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
			
			addContent(panel,root);
			add(panel,BorderLayout.CENTER);
			
			
			//		if(controller!=null && content!=null) {
			//			LinkedHashMap<String, TagData> tagList = content.getList();
			//availableElems = controller.getDefaultValues(name).addAll(controller.getModules(name))

			//		}
		}else {
			add(new JLabel("NO Content"),BorderLayout.CENTER);
		}
		
	}
	
	private void addContent(JXTaskPaneContainer parent,DefaultMutableTreeNode node) {
		if(node.getChildCount()>0) {
//			System.out.println("-- vizualise content: "+node.getUserObject().toString());
//			System.out.println("\t vizualise content of childs of "+node.getUserObject().toString()+"...");
			JXTaskPaneContainer nodeContent = new JXTaskPaneContainer();
			nodeContent.setBackground(UIUtilities.BACKGROUND);
			if (nodeContent.getLayout() instanceof VerticalLayout) {
				VerticalLayout vl = (VerticalLayout) nodeContent.getLayout();
				vl.setGap(2);
			}
			nodeContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
			//add this content
			try {
				JXTaskPane taskPane=new ContentViewer(node.getUserObject().toString(), 
						getHardwareTable(((ModuleTreeElement) node.getUserObject()).getType()), ((ModuleTreeElement)node.getUserObject()).getData());
				//			pane.addPropertyChangeListener(listener);
				for(int i = 0 ; i < node.getChildCount(); i++) {
					addContent(nodeContent,(DefaultMutableTreeNode)node.getChildAt(i));
				}
				//						System.out.println("\t ... vizualise content of childs of "+node.getUserObject().toString());
				taskPane.add(nodeContent);
				//						((JComponent) taskPane.getContentPane()).setBorder(BorderFactory.createEmptyBorder(0,1,0,0));
				parent.add(taskPane);
			}catch(Exception e) {
				MonitorAndDebug.printConsole("ERROR: can't load content of "+node.getUserObject().toString());
				e.printStackTrace();
			}
			
		}else {
			addLeafContent(parent,node);
		}
	}
	
	private void addLeafContent(JXTaskPaneContainer parent,DefaultMutableTreeNode node) {
//		System.out.println("-- vizualise leaf content: "+node.getUserObject().toString());

		ModuleContent content=((ModuleTreeElement)node.getUserObject()).getData();

		if(controller!=null && content!=null) {
			try {
				JXTaskPane taskPane=new ContentViewer(node.getUserObject().toString(), 
						getHardwareTable(((ModuleTreeElement) node.getUserObject()).getType()), content);
				//			pane.addPropertyChangeListener(listener);
				parent.add(taskPane);
			}catch(Exception e) {
				MonitorAndDebug.printConsole("ERROR: can't load content of "+node.getUserObject().toString());
				e.printStackTrace();
			}
		}else {
			System.out.println("\t content is empty [addLeafContent]");
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return a copy of hardware table
	 */
	private ObjectTable getHardwareTable(String key)
	{
		if(hardwareTables==null)
			return null;
		if(hardwareTables.get(key)==null)
			return null;
		return new ObjectTable(hardwareTables.get(key));
	}
}
