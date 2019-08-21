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
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

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
			
			PropertyChangeListener listener=new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					System.out.printf("-- Property change: '%s': '%s' -> '%s'%n ",e.getPropertyName(),e.getOldValue(),e.getNewValue());
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
			panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
			
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
	
	

	private JXTaskPane createTaskPane(String name) {
		JXTaskPane taskPane = new JXTaskPane();
		taskPane.setAnimated(false);

		Container c = taskPane.getContentPane();
		if (color != null) {
			c.setBackground(color);
			taskPane.setBackground(color);
		}
		if (c instanceof JComponent) 
			((JComponent) c).setBorder(BorderFactory.createEmptyBorder(
					1, 1, 1, 1));
		taskPane.setTitle(name);
		taskPane.setCollapsed(false);
		Font font = taskPane.getFont();
		taskPane.setFont(font.deriveFont(font.getSize2D()-2));
		
		return taskPane;
	}
	
	private void addContent(JXTaskPaneContainer parent,DefaultMutableTreeNode node) {
		if(((ModuleTreeElement) node.getUserObject()).isContainer())
			return;
		
		
		if(node.getChildCount()>0) {
//			System.out.println("-- vizualise content: "+node.getUserObject().toString());
			JXTaskPane taskPane=createTaskPane(node.getUserObject().toString());
			
//			System.out.println("\t vizualise content of childs of "+node.getUserObject().toString()+"...");
			JXTaskPaneContainer nodeContent = new JXTaskPaneContainer();
			nodeContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
			//add this content
			try {
				ContentViewer pane = new ContentViewer(node.getUserObject().toString(), 
						getHardwareTable(((ModuleTreeElement) node.getUserObject()).getType()), ((ModuleTreeElement)node.getUserObject()).getData());
				//			pane.addPropertyChangeListener(listener);
				nodeContent.add(pane);

			}catch(Exception e) {
				MonitorAndDebug.printConsole("ERROR: can't load content of "+node.getUserObject().toString());
				e.printStackTrace();
			}
			for(int i = 0 ; i < node.getChildCount(); i++) {
				addContent(nodeContent,(DefaultMutableTreeNode)node.getChildAt(i));
			}
//			System.out.println("\t ... vizualise content of childs of "+node.getUserObject().toString());
			taskPane.add(nodeContent);
			parent.add(taskPane);
		}else {
			addLeafContent(parent,node);
		}
	}
	
	private void addLeafContent(JXTaskPaneContainer parent,DefaultMutableTreeNode node) {
//		System.out.println("-- vizualise leaf content: "+node.getUserObject().toString());
		
		ModuleContent content=((ModuleTreeElement)node.getUserObject()).getData();
		JXTaskPane taskPane=createTaskPane(node.getUserObject().toString());
		
		if(controller!=null && content!=null) {
//			LinkedHashMap<String, TagData> tagList = content.getList();
			
			try {
				ContentViewer pane = new ContentViewer(node.getUserObject().toString(), 
						getHardwareTable(((ModuleTreeElement) node.getUserObject()).getType()), content);
				//			pane.addPropertyChangeListener(listener);
				taskPane.add(pane);

			}catch(Exception e) {
				MonitorAndDebug.printConsole("ERROR: can't load content of "+node.getUserObject().toString());
				e.printStackTrace();
			}
		}
		parent.add(taskPane);
	}
	
	private ObjectTable getHardwareTable(String key)
	{
		if(hardwareTables==null)
			return null;
		return hardwareTables.get(key);
	}
	
	
	private void addContentPanel(Object parent,ModuleTreeElement elem)
	{
//		if(elem!=null) {
//			String name=elem.getName()+"["+elem.getIndex()+"]";
//			if(elem.getChilds().size()>0) {
//				JXTaskPaneContainer containerElem = new JXTaskPaneContainer();
//				for(ModuleTreeElement child:elem.getChilds()) {
//					addContentPanel(containerElem, child);
//				}
//			}else {
//				try {
//					ContentViewer pane = new ContentViewer(controller.getDefaultValues(elem.getName()), controller.getModule(elem.getName(), elem.getIndex()));
//					//			pane.addPropertyChangeListener(listener);
//					Color color=new Color(240, 240, 240);
//					JXTaskPane taskPane = new JXTaskPane();
//					taskPane.setAnimated(false);
//
//					Container c = taskPane.getContentPane();
//					if (color != null) {
//						c.setBackground(color);
//						taskPane.setBackground(color);
//					}
//					if (c instanceof JComponent) 
//						((JComponent) c).setBorder(BorderFactory.createEmptyBorder(
//								1, 1, 1, 1));
//					taskPane.setTitle(name);
//					taskPane.setCollapsed(false);
//					Font font = taskPane.getFont();
//					taskPane.setFont(font.deriveFont(font.getSize2D()-2));
//					taskPane.add(pane);
//
//				}catch(Exception e) {
//					MonitorAndDebug.printConsole("ERROR: can't load content of "+name);
//					e.printStackTrace();
//				}
//			}
//		}
	}

	
}
