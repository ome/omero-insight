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

package org.openmicroscopy.shoola.agents.fsimporter.mde;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.actions.ImporterAction;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.DynamicModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.HardwareConfigurator;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.MDEConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.EditorFileBrowser;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.FNode;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.NodeContainer;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TemplateDialog;
import org.openmicroscopy.shoola.agents.fsimporter.view.Importer;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPaneComponent;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import ome.xml.model.Experimenter;
import ome.xml.model.Project;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.MapAnnotationData;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.IMetadata;
import ome.xml.model.OME;


/**
 * Main entry point MDE.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class MetaDataDialog extends ClosableTabbedPaneComponent
implements ActionListener,  TreeSelectionListener, TreeExpansionListener, ListSelectionListener, ItemListener
{
	/** The property name for the microscope who is used in metadataeditor of <i>OMERO</i>. */
	private static final String  	OMERO_MICROSCOPE = "omeroMicroscope";
	/** The property name for the template who is used in metadataeditor of <i>OMERO</i>. */
	private static final String  	OMERO_TEMPLATE = "omeroTemplate";

	/** The title of the dialog. */
	private static final String TITLE = "Specify Metadata";

	/** The owner related to the component. */
	private JFrame owner;
	/** Reference to the model.*/
	private Importer importer;
	/** The type associated to the import.The type of dialog e.g. screen view. */
	private int type;

	/** Button to cancel all imports. */
	private JButton cancelImportButton;
	/** Button to import the files. */
	private JButton importButton;

	/** reset metadata to data from image file*/
	private JButton resetFileDataButton;
	/** save tags in view as json template*/
	private JButton saveTemplate;

	/** save tags in view as json template*/
	private JButton loadTemplate;

	private JComboBox<String> mics;

	private EditorFileBrowser fileTree;

	private JPanel metaPanel;

	/** lastSelection types*/
	public static final int DIR=0;
	public static final int FILE=1;

	/** type of last selection in tree */
	private int lastSelectionType;
	/** last selected node */
	private FNode lastNode;

	private boolean holdData;
	private FileFilter fileFilter;

	//TODO: necessary?
	private boolean disableItemListener;
	/** flag for status tree listener*/
	private boolean disableTreeListener;

	/** Bound property indicating that the cancel button is pressed. */
	public static final String CANCEL_SELECTION_PROPERTY = "cancelSelection";
	/** Bound property indicating that the content or properties have changed.*/
	public static final String REFRESH_MIC_CONTENT="refreshMicContent";

	/** Action id indicating to import the selected files. */
	private static final int CMD_IMPORT = 1;
	/** Action id indicating to close the dialog. */
	private static final int CMD_CLOSE = 2;
	/** Action id indicating to reset input. */
	private static final int CMD_RESET = 6;

	private static final int CHOOSE_MIC=13;
	private static final int SAVE_TEMPLATE=14;
	private static final int LOAD_TEMPLATE=15;
	private static final int CMD_HARDCONF=16;

	private ModuleController controller;

	private DefaultMutableTreeNode contentTree;
	private ModuleList fileInstrumentList;
	/** template file handle */
	private File tempFile;


	//** Namespaces for parsing from xml */
	public static final String NS_2016_06_07="uos.de/omero/metadata/cellnanos/2015-06-07";


	public static final String CELLNANOS_NS="uos.de/omero/metadata/cellnanos/2015-06-07";

	public static final String MAP_ANNOT_ID = "Annotation:MDR";


	/**
	 * Creates a new instance.
	 * 
	 * @param owner
	 *            The owner of the dialog.
	 * @param filters
	 *            The list of filters.
	 * @param type TODO: necessary?
	 *            The type of dialog e.g. screen view.
	 * @param importerAction
	 *            The cancel-all-imports action.
	 * @param microscope TODO
	 * @param selectedContainer
	 *            The selected container if any.
	 * @param objects
	 *            The possible objects.
	 */
	public MetaDataDialog(JFrame owner, FileFilter[] filters, int type,
			ImporterAction importerAction, Importer importer,JButton importBtn,JButton cancelImportBtn,String microscope)
	{
		super(1, TITLE, TITLE);
		this.owner = owner;
		this.type = type;
		this.importer = importer;

		addImportButtonLink(importBtn);
		addCancelImportButtonLink(cancelImportBtn);
		setClosable(false);
		setCloseVisible(false);

		// read last selected mic from Preferences
		microscope=getMicroscopeName();
		controller = ModuleController.getInstance();

		// microscope of preferences available
		if(controller.getMicIndex(microscope)==-1) {
			microscope=MDEConfiguration.UNIVERSAL;
			controller.setCurrentMicName(microscope);
		}
		controller.initMDEConfiguration(microscope);

		//read template file path from Preferences
		if(getTemplateName()!=null) {
			tempFile=new File(getTemplateName());
		}


		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Load microscope conf: "+microscope);
		initMDE(microscope);
	}


	//private Experimenter convertExperimenter(ExperimenterData expData) 
	//{
	//    Experimenter exp=new Experimenter();
	//    try{exp.setFirstName(expData.getFirstName());}catch(Exception e){};
	//    try{exp.setLastName(expData.getLastName());}catch(Exception e){};
	////	try{exp.setID((expData.getId());}catch(Exception e){};
	//    
	//    return exp;
	//}

	private void initMDE(String microscope) {
		if(microscope == null || microscope.isEmpty())
			initComponents(null);
		else
			initComponents( microscope);

		buildGUI();

	}


	/**
	 * Init gui components like workstation and buttons, filetree, seriesList and metadataview
	 * @param filters
	 * @param importerAction
	 * @param microscope
	 */
	private void initComponents(String microscope)
	{
		holdData=false;
		disableTreeListener=false;

		// init reset button
		resetFileDataButton=new JButton("Clear Input");
		resetFileDataButton.setBackground(UIUtilities.BACKGROUND);
		resetFileDataButton.setToolTipText("Reset metadata. Show only metadata of selected image file.");
		resetFileDataButton.setActionCommand("" + CMD_RESET);
		resetFileDataButton.addActionListener(this);
		resetFileDataButton.setEnabled(false);

		loadTemplate = new JButton("Load Template...");
		loadTemplate.setBackground(UIUtilities.BACKGROUND);
		loadTemplate.setActionCommand(""+ LOAD_TEMPLATE);
		loadTemplate.addActionListener(this);
		loadTemplate.setEnabled(true);

		saveTemplate = new JButton("Save As Template...");
		saveTemplate.setBackground(UIUtilities.BACKGROUND);
		saveTemplate.setActionCommand(""+ SAVE_TEMPLATE);
		saveTemplate.addActionListener(this);
		saveTemplate.setEnabled(true);

		initWorkstationList(microscope);

		metaPanel = new JPanel(new BorderLayout());

		initFileTree();

		lastSelectionType=DIR;
	}


	private void initWorkstationList(String microscope) {
		if(mics==null) {
			mics = new JComboBox<String>(controller.getMicNames());
			mics.setActionCommand(""+CHOOSE_MIC);
			mics.addActionListener(this);
		}else {
			mics.removeActionListener(this);
			mics.removeAllItems();
			for(String s: controller.getMicNames()) {
				mics.addItem(s);
			}
			mics.addActionListener(this);
		}

		// set default microscope workstation
		int indexMic = controller.getMicIndex(microscope);
		if(indexMic!=-1){
			mics.setSelectedIndex(indexMic); 
		}else {
			mics.setSelectedIndex(0);
		}		
	}


	/**
	 * Create a tree that allows one selection at a time
	 */
	private void initFileTree() 
	{
		FNode rootNode=new FNode("ImportQueue");

		fileTree = new EditorFileBrowser(rootNode);
		//Listen for when the selection changes.
		fileTree.addTreeSelectionListener(this);
		fileTree.addTreeExpansionListener(this);

	}

	/**
	 * Builds and lays out file view right side
	 * @return JPanel
	 */
	private JPanel buildFileView(){
		JPanel fileView=new JPanel();
		fileView.setLayout(new BorderLayout(0,0));

		//Create the scroll pane and add the tree to it
		JScrollPane treeView = new JScrollPane(fileTree);

		fileView.add(treeView);

		return fileView;
	}


	/**
	 * Builds and lays out the toolbar for clear input, cancel and import.
	 * @return JPanel.
	 */
	private JPanel buildToolbar_right() {
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		bar.add(resetFileDataButton);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(cancelImportButton);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(importButton);
		bar.add(Box.createHorizontalStrut(10));
		return bar;
	}

	/** 
	 * Build toolbar at the bottom of panel 
	 * @returns JPanel
	 */
	private JPanel buildToolbar()
	{
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));

		JPanel barR=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//mics
		barR.add(new JLabel("Setup:"));
		barR.add(Box.createHorizontalStrut(2));
		barR.add(mics);

		//		JPanel barT = buildToolBarTemplate();

		JButton btnHardwConf=new JButton("Configuration...");
		btnHardwConf.setActionCommand("" + CMD_HARDCONF);
		btnHardwConf.addActionListener(this);
		btnHardwConf.setEnabled(false);


		bar.add(barR);
		bar.add(new JSeparator(SwingConstants.VERTICAL));
		bar.add(btnHardwConf);
		bar.add(new JSeparator(SwingConstants.VERTICAL));
		//		bar.add(btnObjConf);
		//		bar.add(new JSeparator(SwingConstants.VERTICAL));
		//		bar.add(barT);
		//		bar.add(new JSeparator(SwingConstants.VERTICAL));
		bar.add(buildToolbar_right());
		return bar;
	}

	private JPanel buildToolBarTemplate() {
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//      bar.add(Box.createHorizontalGlue());
		bar.add(loadTemplate);
		bar.add(saveTemplate);
		return bar;
	}


	/**
	 * Init and layout gui 
	 */
	private void buildGUI()
	{
		this.removeAll();
		setLayout(new BorderLayout(0,0));

		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,buildFileView(),metaPanel);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(150);

		this.add(splitPane, BorderLayout.CENTER);

		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

		// Lays out the buttons.
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
		bar.add(buildToolbar());
		controls.add(new JSeparator());
		controls.add(bar);

		this.add(controls, BorderLayout.SOUTH);
		revalidate();
		repaint();
	}


	/**
	 * load predefined value, parent, import and model data and visible MDE
	 * Tree: if node has a tree: load tree, else load parent tree + file content
	 * Data: if node has content: load content, else load file data + parent data, if available. 
	 * 		Parent data overwrite this file data.
	 * @param node selected node in the filetree
	 * @param reload TODO
	 */
	private void loadAndShowDataForSelection(FNode node, boolean reload)
	{
		String file=null;
		if(node==null || (file=fileTree.getSelectedFilePath(node))==null)
			return;

		DefaultMutableTreeNode pTree=null;
		HashMap<String,List<TagData>> input=null;
		
		if(node.getContainer()==null || node.getContainer().getTreeNode()==null) 
		{
			ImporterAgent.getRegistry().getLogger().debug(this, "-- nodeContainer is empty");
			//get parent tree and data 
			FNode pNode=getNextParentWithAvailableTree(node);
			if(pNode!=null && pNode.getContainer()!=null) {
				ImporterAgent.getRegistry().getLogger().debug(this, "-- use tree and input of "+pNode.getAbsolutePath());
				pTree=pNode.getContainer().getTreeNode();
				input=pNode.getInput();
			}
			
			// is selection a file or directory
			try {
				if(file.equals("")){
					loadDataForDirectory(node, file, null );
				}else{
					loadDataForFile(file, null, node );

				}
			}catch(Exception e){
				resetFileTreeAtError("Metadata Error!","Can't load metadata of "+file,e);
				return;
			}
		}
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] load content for "+node.getAbsolutePath());
		node.setMapAnnotation(input);
		showMDE(node.getContainer(),pTree);
	}





	private FNode getNextParentWithAvailableTree(FNode node) {
		FNode pNode=null;
		if(node!=null) {
			if((FNode) node.getParent()!=null) {
				//tree available?
				if(((FNode) node.getParent()).getContainer()!=null) {
					if(((FNode) node.getParent()).getContainer().getTreeNode()!=null) {
						pNode = (FNode) node.getParent();
					}
				}
			}
			if(pNode==null) {
				pNode=getNextParentWithAvailableTree((FNode) node.getParent());
			}
		}
		return pNode;
	}


	/**
	 * load predefined value, parent, import and model data.
	 * @param file
	 * @param parentModel
	 * @param node TODO
	 */
	private void loadDataForFile(String file,DefaultMutableTreeNode pTree, FNode node) throws Exception
	{
		lastSelectionType=FILE;

		ImporterAgent.getRegistry().getLogger().debug(this, "-- Show file data \t[MetaDataDialog::loadAndShowDataForFile]");

		NodeContainer current = node.getContainer();
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Create or copy container for node "+node.getAbsolutePath());
		NodeContainer cont = new NodeContainer(file, getImportData(), pTree, this, current, false);
		node.setContainer(cont);
	}


	/**
	 * Load data for DIR.
	 * @param node
	 * @param file
	 * @param parentModel
	 * @param showPreValues TODO
	 */
	private void loadDataForDirectory(FNode node, String file,DefaultMutableTreeNode pTree) throws Exception
	{
		lastSelectionType=DIR;

		ImporterAgent.getRegistry().getLogger().debug(this, "-- Show dir data \t[MetaDataDialog::loadAndShowDataForDir]");

		if(node!=null && !node.isLeaf() &&  node.getContainer()==null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Create container for node "+node.getAbsolutePath());
			NodeContainer cont=new NodeContainer(file, getImportData(), pTree, this, node.getContainer(), true);
			node.setContainer(cont);
		}
	}




	/**
	 * Call routines after deselect a node.
	 * Save tree structure. Override tree structure and data whose change for all childs that still have an own content
	 */
	private void deselectNodeAction(FNode node) {
		if(node!=null){
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Deselect node action for "+node.getAbsolutePath());
			if(getCurrentModuleTree()!=null) {
				DefaultMutableTreeNode contentTree=getCurrentModuleTree().getRoot();
				// get user input
				HashMap<String,List<TagData>> input = MDEHelper.getInput(contentTree);
				//ImporterAgent.getRegistry().getLogger().debug(this, MDEHelper.printList("-- input in current content", input));

				if(node.getContainer()!=null) {
					// save moduletree
					ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] save contentTree of "+node.getAbsolutePath());
					//ImporterAgent.getRegistry().getLogger().debug(this, MDEHelper.printList(contentTree, ""));


					node.getContainer().setTreeNode(contentTree);
					node.getContainer().setInstruments(fileInstrumentList);
				}else {
					try {
						//create container
						ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] create new contentTree of "+node.getAbsolutePath());
						NodeContainer cont=new NodeContainer(node.getAbsolutePath(), getImportData(), contentTree, this, null, !node.isLeaf());
						cont.setInstruments(fileInstrumentList);
						node.setContainer(cont);
					}catch(Exception e) {
					ImporterAgent.getRegistry().getLogger().error(this,"[MDE] Can't create new NodeContainer for saving content for "+node.getAbsolutePath());
						e.printStackTrace();
					}
				}


				//override childs object with new content
				if(!node.isLeaf()) {
					saveInputToChilds(node,input,getCurrentModuleTree().changeTreeStructure());
				}

				//Reset valhasChanged;
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] reset input for "+node.getAbsolutePath());
				MDEHelper.resetInput(contentTree);
				getCurrentModuleTree().setChangeTreeStructure(false);

				node.setMapAnnotation(input);
				ImporterAgent.getRegistry().getLogger().debug(this, "-- MAPAnnotation AT DESELECTED NODE:");
				MDEHelper.printList(node.getAbsolutePath(), node.getInput());
			}
			lastNode=node;
		}
	}

	/**
	 * Update childs with existing tree.
	 * @param node
	 * @param input map
	 */
	private void saveInputToChilds(FNode node, HashMap<String, List<TagData>> input, boolean treeChanges) {
		if(node !=null) {
			for(int i=0; i<node.getChildCount();i++){
				FNode child = (FNode) node.getChildAt(i);

				if(child.getContainer()!=null && child.getContainer().getTreeNode()!=null) {
					DefaultMutableTreeNode childTree=child.getContainer().getTreeNode();
					//update object input
					if(input!=null && !input.isEmpty()) {
						ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] update child: "+child.getAbsolutePath());
						MDEHelper.replaceData(childTree, input,treeChanges);
						child.setMapAnnotation(input);
					}
					// update object tree
					// changes in object tree of parent dir?
					if(treeChanges && node.getContainer()!=null) {
						List<String> leafPath=MDEHelper.getAdditionalLeafsInTree(node.getContainer().getTreeNode(), childTree);
						if(leafPath!=null && !leafPath.isEmpty()) {
							MDEHelper.insertObjects(leafPath,childTree);
						}
					}
				}
				if(!child.isLeaf()) {
					saveInputToChilds(child, input,treeChanges);
				}
			}
		}
	}
	
	private void resetObjectTree() {
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] PRESS RESET object tree");
		ModuleTree treePanel=getCurrentModuleTree();
		DefaultMutableTreeNode initRoot= getInitialFileObjectRootNode();
		if(initRoot == null)
			initRoot=controller.getTree();
		if(treePanel!=null) {
			// reload MDEContent to recover image container contains
			// first save user input
			FNode node=((FNode)fileTree.getLastSelectedPathComponent());
			HashMap<String,List<TagData>> minput=node.getInput();
			HashMap<String,List<TagData>> cinput = MDEHelper.getInput(treePanel.getRoot());
			
			if(minput!=null) {
				if(cinput==null) {
					cinput = new HashMap<>();
				}
				for(Entry<String, List<TagData>> entry:minput.entrySet()) {
					cinput.put(entry.getKey(),MDEHelper.cloneTagList(entry.getValue()));
				}
			}
			node.reset();
			//reload content
			selectNodeAction(node);
			// set input again
			ModuleTree newTree=getCurrentModuleTree();
			//			newTree.printTree(newTree.getRoot(), "New Tree after Reset");
			MDEHelper.addData(newTree.getRoot(), cinput);
		}
	}
	
	private void removeObject() {
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] PRESS DELETE object tree");
		ModuleTree treePanel=getCurrentModuleTree();
		if(treePanel!=null) {
			TreePath selectedNode = treePanel.getTree().getSelectionPath();
			if(selectedNode!=null) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) selectedNode.getLastPathComponent();
				ModuleTreeElement cElem=(ModuleTreeElement)current.getUserObject();
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] delete node : "+cElem.toString());

				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) current.getParent();
				if(parent!=null) {
					List<String> oldTreePaths=MDEHelper.getAllLeafPaths(treePanel.getRoot(), "");
					//delete node in current tree
					treePanel.removeNodeFromParent(current);
					if(lastSelectionType==DIR) {
						List<String> newTreePaths=MDEHelper.getAllLeafPaths(treePanel.getRoot(), "");
						List<String> deleteNodes = MDEHelper.getAdditionalLeafPaths(oldTreePaths, newTreePaths);
						resetObjectTreeOfChilds((FNode)fileTree.getLastSelectedPathComponent(),deleteNodes);
					}
					// update content tree
					((FNode)fileTree.getLastSelectedPathComponent()).getContainer().setTreeNode(treePanel.getRoot());
				}
			}
		}
	}
	
	
//	private void resetObjectTreeOfChilds2(FNode node,List<DefaultMutableTreeNode> deleteObjectNodes) {
//		if(node !=null) {
//			for(int i=0; i<node.getChildCount();i++){
//				FNode child = (FNode) node.getChildAt(i);
//
//				if(child.getContainer()!=null && child.getContainer().getTreeNode()!=null) {
//					// changes in object tree of parent dir?
//					if(deleteObjectNodes!=null && !deleteObjectNodes.isEmpty()) {
//						((Defau) child.getContainer().getTreeNode()).getModel()
//					}
//				}
//				if(!child.isLeaf()) {
//					resetObjectTreeOfChilds(child, deleteObjectPaths);
//				}
//			}
//		}
//	}
	
	private void resetObjectTreeOfChilds(FNode node,List<String> deleteObjectPaths) {
		if(node !=null) {
			for(int i=0; i<node.getChildCount();i++){
				FNode child = (FNode) node.getChildAt(i);

				if(child.getContainer()!=null && child.getContainer().getTreeNode()!=null) {
					// changes in object tree of parent dir?
					if(deleteObjectPaths!=null && !deleteObjectPaths.isEmpty()) {
						MDEHelper.deleteObjects(deleteObjectPaths,child.getContainer().getTreeNode());
					}
				}
				if(!child.isLeaf()) {
					resetObjectTreeOfChilds(child, deleteObjectPaths);
				}
			}
		}
	}


	private ImportUserData getImportData()
	{
		ImportUserData data=null;
		try{
			FNode node = (FNode)fileTree.getLastSelectedPathComponent();

			if(node == null) return null;

			while(!node.hasImportData()){
				node=(FNode) node.getParent();
			}
			data=node.getImportData();
		}catch(Exception e){
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] issue on get import data");
			return null;
		}
		return data;
	}

	/**
	 * TODO : warn if files selected which will not be imported!
	 * @param files
	 * @param fileFilter
	 */
	public void refreshFileView(List<ImportableFile> files, FileFilter fileFilter)
	{
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] refresh file view");
		this.fileFilter=fileFilter;
		fileTree.setFileFilter(fileFilter);
			

		metaPanel.removeAll();
		fileTree.createNodes(files,holdData);
		disableTreeListener=false;
	}



	/**
	 * Cancels or imports the files.
	 * 
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		if(DynamicModuleTree.RESET_TREE_CMD.equals(evt.getActionCommand())) {
			resetObjectTree();
		}else if(ModuleTree.POPUP_DEL.equals(evt.getActionCommand())){
			removeObject();
		}else {
			int commandId = Integer.parseInt(evt.getActionCommand());

			switch (commandId) {
			case CMD_CLOSE:
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] -- close");
				firePropertyChange(CANCEL_SELECTION_PROPERTY,
						Boolean.valueOf(false), Boolean.valueOf(true));
				break;

			case CHOOSE_MIC:
				if(mics.getSelectedIndex()!=-1) {
					String newSelection=controller.getMicNames()[mics.getSelectedIndex()];
					ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] load configuration for "+newSelection);
					setMicroscopeName(newSelection);
					controller.setCurrentMicName(newSelection);
					
//					controller.printObjects();
					//TODO: mapr?
					if(fileTree!=null){
						//save input
						deselectNodeAction((FNode)fileTree.getLastSelectedPathComponent());
						
						//update objects with objectConf of selected setup
						updateObjectConf((FNode)fileTree.getLastSelectedPathComponent());
						
						//TODO reload current view if changes
						loadAndShowDataForSelection((FNode)fileTree.getLastSelectedPathComponent(), true);
					}
					
					// inform ImporterControl about this changes
					//String newTitle=customSettings.getMicName()+(customSettings.getMicDesc()!=null?(": "+customSettings.getMicDesc()): "");
					String newTitle=controller.getCurrentMicName();
					firePropertyChange(ImportDialog.REFRESH_TITLE,null,newTitle);
					
				}
				break;
			case CMD_RESET:
				ImporterAgent.getRegistry().getLogger().info(this,"[MDE] -- reset");

				FNode selection=(FNode)fileTree.getLastSelectedPathComponent();
				String file = fileTree.getSelectedFilePath(selection);

				resetObjectTree();
				//clear node model data
				if(selection!=null) {
				selection.reset();
				//TODO get parentTree
				try {
					if(!selection.isLeaf()){
						loadDataForDirectory(selection, file, null);
					}else{
						loadDataForFile(file, null, selection );
					}
					//    		((MetaDataView)metaPanel.getComponent(0)).reset();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showMDE(selection.getContainer(),null);
				}
				break;
			case SAVE_TEMPLATE:
				String template=null;
				TemplateDialog jsonDialog=new TemplateDialog(new JFrame(),tempFile,false);
				Boolean[] selectedModules=jsonDialog.getSelection();
				tempFile= jsonDialog.getDestination();
				setTemplateName(tempFile);

				if(selectedModules!=null && tempFile!=null) {
					ImporterAgent.getRegistry().getLogger().debug(this, "TODO: Save to tempfile: "+tempFile.getAbsolutePath());
					//				MetaDataView currentView=getMetaDataView(metaPanel);
					//				if(currentView!=null) {
					//					try {
					//						currentView.saveModel();
					//					}catch (Exception e){
					//						LOGGER.warn("Can't save model for this view ");
					//					}
					//					template=currentView.saveDataAsTemplate(selectedModules);
					//				}
				}
				if(template!=null) {
					try {
						FileWriter tfileWriter = new FileWriter(tempFile);
						tfileWriter.write(template);
						tfileWriter.flush();
						tfileWriter.close();
						ImporterAgent.getRegistry().getLogger().debug(this, "-- successfully Copied JSON Object to File...");
						ImporterAgent.getRegistry().getLogger().debug(this, "\nJSON Object:\n " + template);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case LOAD_TEMPLATE:
				TemplateDialog openF=new TemplateDialog(new JFrame(),tempFile,true);
				Boolean[] selectedModulesO=openF.getSelection();
				tempFile= openF.getDestination();
				setTemplateName(tempFile);
				ImporterAgent.getRegistry().getLogger().debug(this, "TODO: load template");
				//			MetaDataModel myModel=JSONModelWrapper.parseJSON(tempFile);
				//			MetaDataView currentViewL=getMetaDataView(metaPanel);
				//			if(currentViewL!=null) {
				//				currentViewL.resfreshView(myModel,selectedModulesO);
				//			}

				break;
			case CMD_HARDCONF:
				HardwareConfigurator conf=new HardwareConfigurator(this);
				break;

			}
		}
	}

	
	/**
	 * Update current object tree with objec configuration for selected setup.
	 * @param fnode
	 */
	private void updateObjectConf(FNode fnode) {
		if(fnode==null)
			return;
		if(fnode.getContainer()!=null) {
			DefaultMutableTreeNode treeNode=fnode.getContainer().getTreeNode();
			Enumeration e = treeNode.breadthFirstEnumeration();
			while(e.hasMoreElements()) {
				DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
				ModuleContent content = controller.getContentOfType(((ModuleTreeElement) node.getUserObject()).getType());
				if(content!=null)
					((ModuleTreeElement) node.getUserObject()).setProperties(content.getProperties());
			}
			
		}
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) 
	{
		if(!disableTreeListener){
			FNode selectedNode=null;
			FNode lastSelectedNode=null;

			TreePath[] paths = e.getPaths();

			// maximum 2 paths in the list -> last and current
			for (int i = 0; i < paths.length; i++) {
				if (e.isAddedPath(i)) {
					selectedNode=(FNode)paths[i].getLastPathComponent();
				} else {
					lastSelectedNode = (FNode)paths[i].getLastPathComponent();
				}
			}

			if(lastSelectedNode==null)
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] INIT ");
			deselectNodeAction(lastSelectedNode);
			selectNodeAction(selectedNode);
		}
	}

	/**
	 * Call methods for selected node in the tree.
	 * @param selectedNode
	 */
	private void selectNodeAction(FNode selectedNode) 
	{
		if(selectedNode!=null ){
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] Select node action for "+selectedNode.getAbsolutePath());

			resetFileDataButton.setEnabled(true);
			loadAndShowDataForSelection(selectedNode, false);

			revalidate();
			repaint();
		}		
	}


	@Override
	public void treeCollapsed(TreeExpansionEvent arg0) 
	{ }


	@Override
	public void treeExpanded(TreeExpansionEvent arg0) 
	{}

	/**
	 * Show selected series
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		if (e.getValueIsAdjusting() == false) {
			//			if (seriesList.getSelectedIndex() != -1) {
			//				FNode node=(FNode)fileTree.getLastSelectedPathComponent();
			//				showMDE(node.getContainer(),seriesList.getSelectedIndex());
			//                if(metaPanel.getComponentCount()>0){
			//                    Component c=metaPanel.getComponent(0);
			//                    if(c instanceof MetaDataView){
			//                        ((MetaDataView) c).showSeries((String)seriesList.getSelectedValue());
			//                    }
			//                }
			//			}
		}
	}


	@Override
	public void itemStateChanged(ItemEvent e) 
	{
		if(!disableItemListener) {
			FNode node=(FNode)fileTree.getLastSelectedPathComponent();
			String file=fileTree.getSelectedFilePath(node);
			if(node==null || file==null)
				return;

			//TODO vererbung/load parent data
			DefaultMutableTreeNode pTree=null;
			if(node.getParent()!=null && ((FNode) node.getParent()).getContainer()!=null)
				pTree=((FNode) node.getParent()).getContainer().getTreeNode();

			// is selection a file or directory
			try{
				if(file.equals("")){
					loadDataForDirectory(node, file, null);
				}else{
					loadDataForFile(file, null, node);
				}
			}catch(Exception ex){
				ImporterAgent.getRegistry().getLogger().error(this,"[MDE] can't read METADATA of selection "+node.getAbsolutePath());
				resetFileTreeAtError("Metadata Error!","Can't read given metadata of "+file,ex);
				return;
			}
			showMDE(node.getContainer(),pTree);
		}
	}

	/**
	 * Show contentTree and contenData
	 * @param container TODO
	 */
	private void showMDE(NodeContainer container,DefaultMutableTreeNode pTree)
	{
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] show mde content for selection");
		metaPanel.removeAll();
		if(container==null)
			return;

		MDEContent content=null;
		DefaultMutableTreeNode contentTree= container.getTreeNode();
		
		// load node for the first time?
		if(contentTree == null) {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] no content tree available for selected node ");
			//TODO : that should not be the case!! pTree is tree of next available parent tree else standardtree
			// this is the case at the moment if parent of parent was only select, but not parent
			if(pTree==null) {
				contentTree=controller.getTree();
			}else {
				ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] use parent tree ");
				contentTree=ModuleTree.cloneTreeNode(pTree);
			}
			//contentTree=controller.cloneTreeStructure(pTree, null);
			if(!container.isDir()) {
				//load file data
				content = new MDEContent(createMetadataReader(container.getNodeObject()),contentTree,controller,this);
			}else {
				content = new MDEContent(contentTree,controller,container.isDir(),null,this);
			}
		}else {
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] use available content tree");
//			ModuleTree.printTree(contentTree, "");
			content = new MDEContent(contentTree,controller,container.isDir(),container.getInstruments(),this);
		}
		contentTree=content.getRootNode();
		fileInstrumentList=content.getInstrumentList();
		// load user input
		metaPanel.add(content,BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * @return a handle to current object tree
	 */
	private ModuleTree getCurrentModuleTree() {
		if(metaPanel==null)
			return null;
		for(Component c: metaPanel.getComponents()) {
			if(c instanceof MDEContent) {
				return ((MDEContent) c).getModuleTree();
			}
		}
		return null;
	}
	
	/**
	 * @return a handle to current initial object tree, null if current selected fnode is a dir
	 */
	private DefaultMutableTreeNode getInitialFileObjectRootNode() {
		if(metaPanel==null)
			return null;
		for(Component c: metaPanel.getComponents()) {
			if(c instanceof MDEContent) {
				return ((MDEContent) c).getInitialFileObjectRootNode();
			}
		}
		return null;
	}

	private void resetFileTreeAtError(String error1,String error2,Exception e)
	{
		ExceptionDialog ld = new ExceptionDialog(error1,error2,e,this.getClass().getSimpleName());
		ld.setVisible(true);
		fileTree.setSelectionPath(fileTree.getSelectionPath().getParentPath());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}


	private void addImportButtonLink(JButton importButton){
		this.importButton=importButton;
	}

	private void addCancelImportButtonLink(JButton cancelImportBtn) {
		this.cancelImportButton=cancelImportBtn;
	}


	/**
	 * Save all input of editor as mapannotation for import
	 * @param text
	 */
	public void saveChanges(String text) 
	{
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] PRESS import: save changes");
		deselectNodeAction((FNode)fileTree.getLastSelectedPathComponent());
		saveMapAnnotations();
		
		ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] clean up");
		initMDE(getMicName());
	}

	private void saveMapAnnotations() {
		DefaultTreeModel treeModel=(DefaultTreeModel)fileTree.getModel();
		FNode root =(FNode)treeModel.getRoot();

		// walk trough file tree
		saveMapAnnotationForLeafs(root, null);
	}

	private void saveMapAnnotationForLeafs(FNode node,MapAnnotationObject parentMap)
	{
		ImporterAgent.getRegistry().getLogger().debug(this, "-- save map annotation: "+node.getAbsolutePath());
			MapAnnotationObject maps=node.getMapAnnotation();
		// no view exists and no changes input for this node -> use parent maps
			if(maps==null && parentMap!=null){
				ImporterAgent.getRegistry().getLogger().debug(this, "\t => use parent mapAnnotation");
				maps=new MapAnnotationObject(parentMap);
			}
		
		if(node.isLeaf()){
			ImporterAgent.getRegistry().getLogger().debug(this, "-- LEAF NODE MAP");
			if(maps!=null){
				maps.setFileName(node.getAbsolutePath());
				// add mapannotation with key file name to ImportDialog mapAnnotation object -> element of ImportableObject
				firePropertyChange(ImportDialog.ADD_MAP_ANNOTATION,null,maps);
//				maps.printObject();
			}else{
				ImporterAgent.getRegistry().getLogger().debug(this, "\t mapAnnotation is null");
			}
		}else{
			// current node is not a leaf:
			// check: if current node has map -> save child of this map with this map or with her own,
			// else save childs with parentmap of current node
			Enumeration children =node.children();
			while(children.hasMoreElements()){
				saveMapAnnotationForLeafs((FNode)children.nextElement(), maps);
				}
			}
		}


	/** PREFERENCE SETTINGS */

	/**
	 * Sets the name of the microscope workstation in the preferences.
	 * 
	 * @param name The name to set.
	 */
	public void setMicroscopeName(String  name)
	{
		if (name == null) return;
		Preferences prefs = Preferences.userNodeForPackage(MetaDataDialog.class);
		prefs.put(OMERO_MICROSCOPE, name);
	}

	/**
	 * @return name of the microscope workstation in the preferences if saved.
	 */
	private String getMicroscopeName()
	{
		Preferences prefs = Preferences.userNodeForPackage(MetaDataDialog.class);
		return prefs.get(OMERO_MICROSCOPE, null);
	}

	/**
	 * @return name of microscope of selected workstation.
	 */
	public String getMicName() {
		return controller.getCurrentMicName();
	}

	/**
	 * @return description for universal setup
	 */
	public String getMicDesc() {
		return "\t (no filter for objects; no predefinitions)";
	}


	/**
	 * Read meta data from given file into OMEXMLMetadata format and set it as the MetadataStore
	 * for given reader. Set global ome as MetadataRetrieve OMEXMLRoot.
	 * @param path of source image file
	 * @return metadata as OMEXMLMetadata format
	 */
	private OME createMetadataReader(String fName)
	{
		ImageReader reader = new ImageReader();
		OME ome=null;
		// show load cursor
		Cursor cursor=this.getCursor();
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try{
			//record metadata to ome-xml format
			ServiceFactory factory=new ServiceFactory();
			OMEXMLService service = factory.getInstance(OMEXMLService.class);
			IMetadata metadata =  service.createOMEXMLMetadata();
			reader.setMetadataStore((MetadataStore) metadata);


			reader.setId(fName);
			ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] -- use READER: "+reader.getReader().getClass().getName());
			ImporterAgent.getRegistry().getLogger().debug(this, "-- Use Reader: "+reader.getReader().getClass().getSimpleName());

			//load original data
			//			series = reader.getSeriesMetadata();
			//load ome
			String xml = service.getOMEXML((MetadataRetrieve) metadata);

//			ImporterAgent.getRegistry().getLogger().debug(this, "Create Reader: FILE XML:\n"+xml);
			ome = (OME) service.createOMEXMLRoot(xml);
			//			companionFiles=reader.getUsedFiles();
		}catch(Exception e){
			// not a bioFormat readable file
			ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] file format of "+fName+"not readable by bio-formats!");
			if(cursor!=null) this.setCursor(cursor);
		}
		//reset cursor
		if(cursor!=null) this.setCursor(cursor);
		return ome;
	}

	/**
	 * Sets the name of the template file in the preferences.
	 * @param file The handle of the template file.
	 */
	public void setTemplateName(File file)
	{
		if (file == null) 
			return;
		String name=file.getAbsolutePath();
		Preferences prefs = Preferences.userNodeForPackage(MetaDataDialog.class);
		prefs.put(OMERO_TEMPLATE, name);
	}

	/**
	 * Get the name of the template if available in preferences.
	 * @return name of the MDE template file.
	 */
	private String getTemplateName()
	{
		Preferences prefs = Preferences.userNodeForPackage(MetaDataDialog.class);
		return prefs.get(OMERO_TEMPLATE, null);
	}

	public void reloadView() {
		initWorkstationList(controller.getCurrentMicName());
	}


}
