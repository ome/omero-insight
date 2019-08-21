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
import java.util.prefs.Preferences;

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

import org.openmicroscopy.shoola.agents.fsimporter.actions.ImporterAction;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.DynamicModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleTree;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.HardwareConfigurator;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.MDEConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.ObjectConfigurator;
import org.openmicroscopy.shoola.agents.fsimporter.mde.microscope.NodeContainer;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.EditorFileBrowser;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.FNode;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TemplateDialog;
import org.openmicroscopy.shoola.agents.fsimporter.view.Importer;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
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

import org.slf4j.LoggerFactory;

public class MetaDataDialog extends ClosableTabbedPaneComponent
implements ActionListener, PropertyChangeListener, TreeSelectionListener, TreeExpansionListener, ListSelectionListener, ItemListener
{

	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MetaDataDialog.class);


	/** The property name for the microscope who is used in metadataeditor of <i>OMERO</i>. */
	private static final String  	OMERO_MICROSCOPE = "omeroMicroscope";
	/** The property name for the template who is used in metadataeditor of <i>OMERO</i>. */
 	private static final String  	OMERO_TEMPLATE = "omeroTemplate";

	private boolean DEBUG=false;

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

	/** Button to refresh the file chooser. */
	//private JButton refreshFilesButton;


	/** reset metadata to data from image file*/
	private JButton resetFileDataButton;
	/** save tags in view as json template*/
    private JButton saveTemplate;
    
    /** save tags in view as json template*/
    private JButton loadTemplate;

	private JComboBox<String> mics;

	private EditorFileBrowser fileTree;

	private JPanel metaPanel;

	/** Identifies the style of the document.*/
	private static final String STYLE = "StyleName";
	/** The maximum number of characters in the debug text.*/
	private static final int 	MAX_CHAR = 200000;

	/** lastSelection types*/
	public static final int DIR=0;
	public static final int FILE=1;

	/** type of last selection in tree */
	private int lastSelectionType;

	private FNode lastNode;

	private boolean holdData;
	private FileFilter fileFilter;

	private boolean disableItemListener;
	private boolean disableTreeListener;

	/** Bound property indicating that the cancel button is pressed. */
	public static final String CANCEL_SELECTION_PROPERTY = "cancelSelection";
	/** Action id indicating to import the selected files. */
	private static final int CMD_IMPORT = 1;
	/** Action id indicating to close the dialog. */
	private static final int CMD_CLOSE = 2;
	/** Action id indicating to reset the names. */
	//private static final int CMD_REFRESH = 3;
	private static final int LOAD_MIC_SETTINGS=4;

	private static final int CMD_RESET = 6;

	private static final int CHOOSE_MIC=13;
	private static final int SAVE_TEMPLATE=14;
    private static final int LOAD_TEMPLATE=15;
    private static final int CMD_HARDCONF=16;
    private static final int CMD_OBJECTCONF=17;

	public static final String CHANGE_CUSTOMSETT = "changesCustomSettings";

	private ModuleController controller;

	private DefaultMutableTreeNode contentTree;
	private ModuleList fileInstrumentList;
	private File tempFile;


	//** Namespaces for parsing from xml */
	public static final String NS_2016_06_07="uos.de/omero/metadata/cellnanos/2015-06-07";


	public static final String CELLNANOS_NS="uos.de/omero/metadata/cellnanos/2015-06-07";

	public static final String MAP_ANNOT_ID = "Annotation:CellNanOs";


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
		controller.initMDEConfiguration(microscope);
		
		// microscope of preferences available
		if(controller.getMicIndex(microscope)==-1) {
			microscope=MDEConfiguration.UNIVERSAL;
			controller.setCurrentMicName(microscope);
		}
		
		 //read template file path from Preferences
        if(getTemplateName()!=null) {
        	tempFile=new File(getTemplateName());
        }
        
		
		
		System.out.println("Microscope conf: "+microscope);
		if(microscope == null || microscope.isEmpty())
			initComponents(filters, importerAction,null);
		else
			initComponents(filters, importerAction, microscope);

		buildGUI();
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

	/**
	 * Init gui components like workstation and buttons, filetree, seriesList and metadataview
	 * @param filters
	 * @param importerAction
	 * @param microscope
	 */
	private void initComponents(FileFilter[] filters,ImporterAction importerAction, String microscope)
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
			System.out.println("rebuild mic list");
			for(String s: controller.getMicNames()) {
				System.out.println("\t "+s);
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
		barR.add(new JLabel("Load Hardware Specification:"));
		barR.add(Box.createHorizontalStrut(2));
		barR.add(mics);

		JPanel barT = buildToolBarTemplate();
		
		JButton btnHardwConf=new JButton("Hardware...");
		btnHardwConf.setActionCommand("" + CMD_HARDCONF);
		btnHardwConf.addActionListener(this);
		
		JButton btnObjConf=new JButton("Objects...");
		btnObjConf.setActionCommand("" + CMD_OBJECTCONF);
		btnObjConf.addActionListener(this);
		
		bar.add(barR);
		bar.add(new JSeparator(SwingConstants.VERTICAL));
		bar.add(btnHardwConf);
		bar.add(new JSeparator(SwingConstants.VERTICAL));
		bar.add(btnObjConf);
		bar.add(new JSeparator(SwingConstants.VERTICAL));
		bar.add(barT);
		bar.add(new JSeparator(SwingConstants.VERTICAL));
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

		MonitorAndDebug.printConsole("[TREE] -- Node: "+node.toString()+" ##############################################");
		DefaultMutableTreeNode pTree=null;
		if(node.getContainer()==null || node.getContainer().getTreeNode()==null) 
		{
			MonitorAndDebug.printConsole("-- create new container [MetaDataDialog::loadAndShowDataForSelection]");
			//get parent tree and data 
			
			if((FNode) node.getParent()!=null && ((FNode) node.getParent()).getContainer()!=null) {
				pTree = ((FNode) node.getParent()).getContainer().getTreeNode();
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
		System.out.println("-- show content of "+node.getAbsolutePath());
		showMDE(node.getContainer(),pTree);
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
		
		MonitorAndDebug.printConsole("-- Show file data \t[MetaDataDialog::loadAndShowDataForFile]");

		NodeContainer current = node.getContainer();
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
		
		MonitorAndDebug.printConsole("-- Show dir data \t[MetaDataDialog::loadAndShowDataForDir]");
		
		if(node!=null && !node.isLeaf() &&  node.getContainer()==null) {
			System.out.println("-- Create container for node "+node.getAbsolutePath());
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
			MonitorAndDebug.printConsole("## Deselect "+node.getAbsolutePath()+" [MetaDataDialog::deselectNodeAction]");
//			if(node.getModelObject()!=null) {
//				System.out.println("-- deselect node model obj:");
//				node.getModelObject().print();
//			}
			// get user input
			HashMap<String,List<TagData>> input=MDEHelper.getInput(contentTree);
			System.out.println("-- Input of:");
			MDEHelper.printList(node.getAbsolutePath(), input);
			
			// save moduletree
			System.out.println("-- save contentTree of "+node.getAbsolutePath());
			node.getContainer().setTreeNode(contentTree);
			node.getContainer().setInstruments(fileInstrumentList);
			
			//deselect view to fire focusLost action
			//		showMDE(null);
			LOGGER.debug("MetaDataDialog::Deselect node action for "+node.getAbsolutePath());

			//override childs with content
			if(!node.isLeaf()) {
				saveInputToChilds(node,input);
				//Reset valhasChanged;
				System.out.println("-- reset input for "+node.getAbsolutePath());
				MDEHelper.resetInput(contentTree);
			}
			else {
				// TODO save changes
				System.out.println("-- TODO: Save data of current node for subnodes without own tree");
				
			}
			node.setMapAnnotation(input);
			lastNode=node;
		}
	}


	private void saveInputToChilds(FNode node, HashMap<String, List<TagData>> input) {
		if(node !=null) {
			for(int i=0; i<node.getChildCount();i++){
				FNode child = (FNode) node.getChildAt(i);
				// TODO: changes in tree structure?
				if(child.getContainer()!=null && child.getContainer().getTreeNode()!=null) {
					if(input!=null && !input.isEmpty()) {
						
						MDEHelper.replaceData(child.getContainer().getTreeNode(), input);
					}
				}
				if(!child.isLeaf()) {
					saveInputToChilds(child, input);
				}
			}
		}
	}


	/**TODO
	 * save data model of  node, if any user input available and update all childs 
	 * that still have a model if deselected node== directory
	 */
	private void saveInputToModel(FNode node,boolean showSaveDialog) 
	{
//		if(node!=null){
//			//		if(node.getView()!=null){
//			//			MonitorAndDebug.printConsole("# Save input of: "+node.getAbsolutePath()+"\t[MetaDataDialog::saveInputToModel)]");
//			//			try{
//			//				node.saveModel();
//			//			}catch (Exception e){
//			//				MonitorAndDebug.printConsole("# ERROR: can't save model \t[MetaDataDialog::saveInputToModel]");
//			//				LOGGER.warn("# ERROR Can't save model for this node: "+node.getAbsolutePath());
//			//			}
//			if(!node.isLeaf())
////				updateChildsOfDirectory(node, node.getModelObject());
//			//			else{
//			//				if(node.hasModelObject()){
//			//					MonitorAndDebug.printConsole("\t clear list for "+node.getAbsolutePath());
//			//					node.getModelObject().clearListOfModifications();
//			//				}
//			//			}
//					}
//		}
	}


	/**
	 * GUI input : Update all child views of type directory with EXISTING MODEL with tagdata changes
	 * @param node
	 */
//	private void updateChildsOfDirectory(FNode node,MetaDataModelObject modelToInherit) 
//	{
//		MonitorAndDebug.printConsole("# MetaDataDialog::updateChildsOfDirectories of "+node.getAbsolutePath());
//
//		LOGGER.debug("Update childs of "+node.getAbsolutePath());
//
//		int numChilds=node.getChildCount();
//		MetaDataModelObject nodeModel=null;
//
//		//deselected node model
//		//	if(node.hasModelObject()){
//		//		MonitorAndDebug.printConsole("\t Use own model");
//		//		nodeModel=node.getModelObject();
//		////		nodeModel.print();
//		//	}
//		//	else 
//		if(modelToInherit!=null){
//			MonitorAndDebug.printConsole("\t Use parent model");
//			nodeModel=modelToInherit;
//			//		nodeModel.print();
//		}else
//			return;
//
//		for(int i=0; i<numChilds;i++){
//			FNode child = (FNode) node.getChildAt(i);
//
//			if(child.hasModelObject() ){
//				MonitorAndDebug.printConsole("\t ...update existing model/view of "+child.getAbsolutePath());
//				//			child.getModelObject().print();
//				LOGGER.debug("[DEBUG] Update "+child.getAbsolutePath());
////				try {
////					child.getModelObject().updateData(nodeModel);
////
////
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//				//for all subdirectories updateChilds
//				if(!child.isLeaf()){
//					updateChildsOfDirectory(child,nodeModel);
//				}
//			}else{
//				// model wird nach unten weitergereicht aber keine aenderungen gestzt solange noch kein eigenes model vorhanden
//				// TODO: besser waere doch schon die speicherung der aenderung fuer alle childs, auch die ohne model, dann ist laden der Elterndataen unnoetig!
//
//				//for all subdirectories updateChilds
//
//				if(!child.isLeaf()){
////					child.inheritModel(nodeModel,true);
//					updateChildsOfDirectory(child,nodeModel);
//				}else {
//					child.inheritModel(nodeModel,false);
//				}
//			}
//		}//for
//		if(node.hasModelObject()){
//			MonitorAndDebug.printConsole("# MetaDataDialog::updateChildsOfDirectory(): clear list for "+node.getAbsolutePath());
////			node.getModelObject().clearListOfModifications();
//		}
//	}

//	private MetaDataModel getParentMetaDataModel(FNode node) 
//	{
//		if(node!=null){
//			FNode parent=(FNode) node.getParent();
//
//			if(parent!=null){
//				if(parent.hasModelObject()){
//					// parent is a directory with only one metadatamodel
//					return parent.getModelOfSeries(0);
//				}else{
//					return getParentMetaDataModel(parent);
//				}
//			}
//		}
//		return null;
//	}


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
			LOGGER.warn("No import data available");
			return null;
		}
		return data;
	}

	public void refreshFileView(List<ImportableFile> files, FileFilter fileFilter)
	{
		this.fileFilter=fileFilter;
		fileTree.setFileFilter(fileFilter);
		if(files==null || files.size()==0){
			LOGGER.info("No data select");
			// TODO: changes should be save
			MonitorAndDebug.printConsole("# MetaDataDialog::resfreshFileView(): Filelist is null -> IMPORT ?");
			//    	disableTreeListener=true;
		}else
			MonitorAndDebug.printConsole("# MetaDataDialog::refreshFileView(): list= "+files.size());

		metaPanel.removeAll();
		fileTree.createNodes(files,holdData);
		disableTreeListener=false;
	}

	/**
	 * Reacts to property fired by the table.
	 * 
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		System.out.println("[DEBUG] MetaDataDialog notice propertyChange "+name);
	}

	/**
	 * Cancels or imports the files.
	 * 
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		int commandId = Integer.parseInt(evt.getActionCommand());

		switch (commandId) {
		case CMD_CLOSE:
			LOGGER.info("[GUI-ACTION] -- close");
			firePropertyChange(CANCEL_SELECTION_PROPERTY,
					Boolean.valueOf(false), Boolean.valueOf(true));
			break;

		case CHOOSE_MIC:
			if(mics.getSelectedIndex()!=-1) {
			String newSelection=controller.getMicNames()[mics.getSelectedIndex()];
			System.out.println("--- LOAD "+newSelection+" HARDWARE SETTINGS ---");
			setMicroscopeName(newSelection);
			controller.setCurrentMicName(newSelection);
			//TODO: mapr?
			if(fileTree!=null){
				System.out.println("RELOAD-----------------");
				deselectNodeAction((FNode)fileTree.getLastSelectedPathComponent());

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
			LOGGER.info("[GUI-ACTION] -- reset");
			MonitorAndDebug.printConsole("\n +++ EVENT RESET INPUT +++\n");

			FNode selection=(FNode)fileTree.getLastSelectedPathComponent();
			String file = fileTree.getSelectedFilePath(selection);

			//clear node model data
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
			System.out.println("--ActionPerformed: show content of "+selection.getAbsolutePath());
			showMDE(selection.getContainer(),null);

			break;
		case SAVE_TEMPLATE:
			String template=null;
			TemplateDialog jsonDialog=new TemplateDialog(new JFrame(),tempFile,false);
			Boolean[] selectedModules=jsonDialog.getSelection();
			tempFile= jsonDialog.getDestination();
			setTemplateName(tempFile);

			if(selectedModules!=null && tempFile!=null) {
				System.out.println("TODO: Save to tempfile: "+tempFile.getAbsolutePath());
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
					System.out.println("Successfully Copied JSON Object to File...");
					System.out.println("\nJSON Object:\n " + template);
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
			System.out.println("TODO: load template");
//			MetaDataModel myModel=JSONModelWrapper.parseJSON(tempFile);
//			MetaDataView currentViewL=getMetaDataView(metaPanel);
//			if(currentViewL!=null) {
//				currentViewL.resfreshView(myModel,selectedModulesO);
//			}

			break;
		case CMD_HARDCONF:
			HardwareConfigurator conf=new HardwareConfigurator(new JFrame(),this);
			break;
			
		case CMD_OBJECTCONF:
			ObjectConfigurator oconf=new ObjectConfigurator(new JFrame(),this);
			break;
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

			String action=lastSelectedNode==null?"\n+++ INIT TREE +++":"\n+++ EVENT TREE DESELECT "+lastSelectedNode.getAbsolutePath()+"+++\n";
			MonitorAndDebug.printConsole(action);
			deselectNodeAction(lastSelectedNode);

			action=selectedNode==null?"":"\n+++ EVENT TREE SELECT "+selectedNode.getAbsolutePath()+"+++\n";
			MonitorAndDebug.printConsole(action);
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
			System.out.println("=====================================================================");
			MonitorAndDebug.printConsole("\n# MetaDataDialog::selectNodeAction("+selectedNode.getAbsolutePath()+")");
			//	   selectedNode.printMaps();
			LOGGER.debug("Select node action for "+selectedNode.getAbsolutePath());

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


	/**TODO: select series
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
			System.out.println("--ItemStateChanged");
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
				LOGGER.error("[DATA] CAN'T read METADATA");
				resetFileTreeAtError("Metadata Error!","Can't read given metadata of "+file,ex);
				return;
			}
			System.out.println("-- show content of "+node.getAbsolutePath());
			showMDE(node.getContainer(),pTree);
		}
	}

	/**
	 * Show contentTree and contenData
	 * @param container TODO
	 */
	private void showMDE(NodeContainer container,DefaultMutableTreeNode pTree)
	{
		System.out.println("-- SHOW MDE Content");
		metaPanel.removeAll();
		if(container==null)
			return;

		MDEContent content=null;
		contentTree= container.getTreeNode();
		System.out.println("\t contentTree : "+(contentTree==null?"null":"available"));
		// load node for the first time?
		if(contentTree == null) {
			//TODO : that should not be the case!! pTree is tree of next available parent tree else standardtree
			if(pTree==null)
				contentTree=controller.getTree();
			else
				contentTree=ModuleTree.cloneTreeNode(pTree);
				//contentTree=controller.cloneTreeStructure(pTree, null);
			if(!container.isDir()) {
				//load file data
				content = new MDEContent(createMetadataReader(container.getNodeObject()),contentTree,controller);
			}else {
				content = new MDEContent(contentTree,controller,container.isDir(),null);
			}
		}else {
			content = new MDEContent(contentTree,controller,container.isDir(),container.getInstruments());
		}
		contentTree=content.getRootNode();
		fileInstrumentList=content.getInstrumentList();
		// load user input
		//TODO
		metaPanel.add(content,BorderLayout.CENTER);
		revalidate();
		repaint();
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
		MonitorAndDebug.printConsole("\n+++ EVENT: IMPORT SAVE CHANGES ++++\n");
		deselectNodeAction((FNode)fileTree.getLastSelectedPathComponent());
		FNode node=(FNode)fileTree.getLastSelectedPathComponent();
//		saveInputToModel(node, true);
		saveMapAnnotations();
	}

	private void saveMapAnnotations() {
		DefaultTreeModel treeModel=(DefaultTreeModel)fileTree.getModel();
		FNode root =(FNode)treeModel.getRoot();

		// walk trough tree
		saveMapAnnotationOfSubNodes(root, null);
	}

	private void saveMapAnnotationOfSubNodes(FNode node,MapAnnotationObject parentMap)
	{
		if(node.isLeaf()){
			System.out.println("TODO: Check node is an image");
			
			MapAnnotationObject maps=node.getMapAnnotation();
			

			// no view exists and no changes input for node
			if(maps==null && parentMap!=null){
				MonitorAndDebug.printConsole("\t"+node.getAbsolutePath()+"\t use parent mapAnnotation");
				maps=new MapAnnotationObject(parentMap);
			}
			if(maps!=null){
				maps.setFileName(node.getAbsolutePath());
				firePropertyChange(ImportDialog.ADD_MAP_ANNOTATION,null,maps);
				MonitorAndDebug.printConsole("\t"+maps.getMapAnnotationList());
			}else{
				MonitorAndDebug.printConsole("\t"+node.getAbsolutePath()+"\t mapAnnotation is null");
			}
		}else{
			Enumeration children =node.children();
			while(children.hasMoreElements()){
				FNode subNode=(FNode)children.nextElement();
				MapAnnotationObject maps=subNode.getMapAnnotation();

				// no view exists and no changes input for node
				if(maps==null && parentMap!=null){
					MonitorAndDebug.printConsole("\t"+subNode.getAbsolutePath()+"\t use parent mapAnnotation");
					maps=new MapAnnotationObject(parentMap);
				}
				saveMapAnnotationOfSubNodes(subNode, maps);
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
	 * Returns the name of the microscope workstation if saved.
	 * 
	 * @return See above.
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
	 * @return description for selected microscope workstation
	 */
	public String getMicDesc() {
		return "TODO: define desc";//customSettings.getMicDesc();
	}

	
	/**
	 * Read meta data from given file into OMEXMLMetadata format and set it as the MetadataStore
	 * for given reader. Set global ome as MetadataRetrieve OMEXMLRoot.
	 * @param file source file
	 * @param reader of the source file
	 * @return metadata as OMEXMLMetadata format
	 * @throws DependencyException
	 * @throws ServiceException
	 */
	public OME createMetadataReader(String fName)
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
			LOGGER.info("[DATA] -- use READER: "+reader.getReader().getClass().getName());
			MonitorAndDebug.printConsole("Use Reader: "+reader.getReader().getClass().getSimpleName());
			
			//load original data
//			series = reader.getSeriesMetadata();
			//load ome
			String xml = service.getOMEXML((MetadataRetrieve) metadata);
			
			MonitorAndDebug.printConsole("Create Reader: FILE XML:\n"+xml);
			ome = (OME) service.createOMEXMLRoot(xml);
//			companionFiles=reader.getUsedFiles();
		}catch(Exception e){
			e.printStackTrace();
			if(cursor!=null) this.setCursor(cursor);
		}
		//reset cursor
		if(cursor!=null) this.setCursor(cursor);
		return ome;
	}
	
	/**
	 * Sets the name of the template file in the preferences.
	 * 
	 * @param name The name to set.
	 */
	public void setTemplateName(File file)
	{
		if (file == null) return;
		String name=file.getAbsolutePath();
		Preferences prefs = Preferences.userNodeForPackage(MetaDataDialog.class);
		prefs.put(OMERO_TEMPLATE, name);
	}

	/**
	 * Returns the name of the template if saved.
	 * 
	 * @return See above.
	 */
	private String getTemplateName()
	{
		Preferences prefs = Preferences.userNodeForPackage(MetaDataDialog.class);
		return prefs.get(OMERO_TEMPLATE, null);
	}

	public void reloadView() {
		initWorkstationList(controller.getCurrentMicName());
//		if(fileTree!=null){
//			System.out.println("RELOAD-----------------");
//			deselectNodeAction((FNode)fileTree.getLastSelectedPathComponent());
//
//			//TODO reload current view if changes
//			loadAndShowDataForSelection((FNode)fileTree.getLastSelectedPathComponent(), true);
//		}
	}


}
