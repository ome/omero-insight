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
package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.CommonViewer;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleContentTableModel;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * TODO: hide "User::" fields, update mdecontent after changes; 
 * Edit MDE config file.
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class HardwareConfigurator extends JFrame implements ActionListener{
	public static final String CMD_APPLY = "apply";
	public static final String CMD_CANCEL = "cancel";
	public static final String CMD_SAVE = "save";
	public static final String CMD_NEW_MIC = "create new microsocope";
	public static final String CMD_DEL_MIC = "delete selected microsocope";
	private static final String CMD_EDIT_LAYOUT = "edit layout";
	
	private ListSelectionListener objSelectListener = null;
	
	final int WIDTH=1000;
	final int HEIGHT=600;
	private ModuleController controller;
	private DefaultListModel<String> listModelMic;
	private JList micList;
	private DefaultListModel<String> listModelObj;
	
	private JTextField txtMicName;
	private JPanel predefinitionsObject_Panel;
	private JPanel layoutObject_Panel;
	private MDEConfiguration conf;
	private String currentMic;
	private String[] availableMics;
	private JComboBox<String> objects;
	private MetaDataDialog dialog;
	private JPanel listPaneObj;
	
	private boolean deleteMic;
	
	public HardwareConfigurator(MetaDataDialog dialog) {
		super("Hardware Configurator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.dialog=dialog;
		this.controller=ModuleController.getInstance();
		deleteMic=false;
		// work on a copy
		this.conf=new MDEConfiguration(controller.getMDEConfiguration());
		this.currentMic=controller.getCurrentMicName();
		this.availableMics=conf.getMicNames();
		buildGUI();
		
		revalidate();
		repaint();
		setResizable(true);
		pack();
		setVisible(true);
	}
	
	private void buildGUI() {
		ImporterAgent.getRegistry().getLogger().debug(this, "--BUILD GUI : HardwareConfiguration");
		getContentPane().setLayout(new BorderLayout(5,5));
		
		loadMicroscopeList();
		
		objSelectListener=new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					JList source = (JList)e.getSource();
					String newObj=(String) source.getSelectedValue();
					if(!deleteMic) saveCurrentValues();
					// TODO: refresh view
					if(predefinitionsObject_Panel!=null) predefinitionsObject_Panel.removeAll();
					if(layoutObject_Panel!=null) layoutObject_Panel.removeAll();
					getPredefinitions(currentMic, newObj);
					getLayout(currentMic,newObj);
					revalidate();
					repaint();
				}
			}
		};
        
        JPanel listPaneMic=createMicPanel();
        listPaneObj=new JPanel();
        createObjPanel();
        
        JPanel listPane=new JPanel(new GridLayout(0,2));
        listPane.add(listPaneMic);
        listPane.add(listPaneObj);
        
        // button pane
		JPanel btnPanel=new JPanel();
		
		JButton btnApply=new JButton("Apply");
        btnApply.setActionCommand(CMD_APPLY);
        btnApply.addActionListener(this);
        btnPanel.add(btnApply);
        
        JButton btnCancel=new JButton("Cancel");
        btnCancel.setActionCommand(CMD_CANCEL);
        btnCancel.addActionListener(this);
        btnPanel.add(btnCancel);
        
        JButton btnSave=new JButton("Save To File");
        btnSave.setActionCommand(CMD_SAVE);
        btnSave.addActionListener(this);
        btnPanel.add(btnSave);
       
        predefinitionsObject_Panel=new JPanel(new BorderLayout());
		JScrollPane scrollView = new JScrollPane(predefinitionsObject_Panel);
		
		layoutObject_Panel=new JPanel(new BorderLayout());
		JScrollPane scrollViewLayout=new JScrollPane(layoutObject_Panel);
		
		JPanel valueViewPanel= new JPanel(new BorderLayout());
		valueViewPanel.add(scrollView,BorderLayout.NORTH);
		valueViewPanel.add(scrollViewLayout,BorderLayout.CENTER);
		
		JPanel main=new JPanel(new BorderLayout());
		main.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		main.add(listPane,BorderLayout.NORTH);
		main.add(valueViewPanel,BorderLayout.CENTER);
		main.add(btnPanel,BorderLayout.SOUTH);
		
		getContentPane().add(main,BorderLayout.CENTER);
	}
	
	private void createObjPanel() {
		loadObjectList(currentMic);
		listPaneObj.removeAll();
		listPaneObj.add(new ObjectList_Panel(currentMic, listModelObj, objSelectListener, conf));
		listPaneObj.revalidate();
		listPaneObj.repaint();
	}
	

	private JPanel createMicPanel() {
		// list right side
		micList=new JList(listModelMic);
		micList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if(currentMic!=MDEConfiguration.UNIVERSAL)
			micList.setSelectedIndex(controller.getMicIndex(currentMic));
		micList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if (micList.getSelectedIndex() != -1) {
						String newSelection=(String) micList.getSelectedValue();//availableMics[micList.getSelectedIndex()];
						// save input
						if(!deleteMic) saveCurrentValues();
						currentMic=newSelection;
						// remove table for preview selection
						if(predefinitionsObject_Panel!=null) predefinitionsObject_Panel.removeAll();
						// load new objects
						createObjPanel();
						
						revalidate();
						repaint();
					} 
				}	
			}
		});
		
		micList.setVisibleRowCount(10);

		JScrollPane listScrollPane = new JScrollPane(micList);
		
		JPanel pane=new JPanel(new BorderLayout());
		pane.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black), "Microscope"));
		pane.add(listScrollPane,BorderLayout.CENTER);
        
        JButton btnNewMic = new JButton("New");
//        btnNewMic.setBounds(300, 240, 80, 20);
        btnNewMic.setActionCommand(CMD_NEW_MIC);
        btnNewMic.addActionListener(this);
        
        JButton btnDelMic = new JButton("Delete");
        btnDelMic.setActionCommand(CMD_DEL_MIC);
        btnDelMic.addActionListener(this);
        
        JPanel btnPanelMic=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanelMic.add(btnNewMic);
        btnPanelMic.add(btnDelMic);
        
        txtMicName=new JTextField();
        
//        JPanel newMicPane=new JPanel(new BorderLayout());
//        newMicPane.add(txtMicName,BorderLayout.CENTER);
//        newMicPane.add(btnNewMic,BorderLayout.EAST);
        JPanel newMicPane=new JPanel(new BorderLayout());
        newMicPane.add(txtMicName,BorderLayout.CENTER);
//        newMicPane.add(Box.createHorizontalStrut(2));
        newMicPane.add(btnPanelMic,BorderLayout.EAST);
        
        pane.add(newMicPane,BorderLayout.SOUTH);
        pane.revalidate();
        pane.repaint();
		return pane;
	}

	/**
	 * Load hardware specification for selected microscope micName.
	 * @param micName
	 */
	private void getPredefinitions(String micName,String instrument) {
		if(predefinitionsObject_Panel==null || instrument==null) {
			return;
		}
		System.out.println("-- getPredefinition for: "+micName+"::"+instrument);
		ModuleList hardware = conf.getPredefinitions(micName);
		if(hardware==null) {
			predefinitionsObject_Panel.add(new InstrumentTable(null, instrument),BorderLayout.CENTER);
			ImporterAgent.getRegistry().getLogger().debug(this, "--no predefinitions for selected mic "+micName+" and object "+instrument);
			return;
		}
		List<ModuleContent> cInstrument=hardware.get(instrument);
		if(cInstrument==null) {
			predefinitionsObject_Panel.add(new InstrumentTable(null, instrument),BorderLayout.CENTER);
		}else {
			ImporterAgent.getRegistry().getLogger().debug(this, "\tload instrument "+instrument+"["+cInstrument.size()+"] for "+micName);
			predefinitionsObject_Panel.add(new InstrumentTable(cInstrument,instrument),BorderLayout.CENTER);
		}
		predefinitionsObject_Panel.revalidate();
		predefinitionsObject_Panel.repaint();
	}
	
	private void getLayout(String micName, String objName) {
		if(layoutObject_Panel==null || objName==null)
			return;
		
		System.out.println("-- getLayout for: "+micName+"::"+objName);
		ModuleContent content = conf.getContent(micName, objName);
		layoutObject_Panel.add(new ObjLayoutConf_Panel(content,this));
		layoutObject_Panel.revalidate();
		layoutObject_Panel.repaint();
	}

	/**
	 * Create instrument table for given type.
	 * @param availableObjects
	 * @param type
	 * @return
	 */
	private JTable createTable(List<ModuleContent> availableObjects,String type) {
		JTable table=new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ModuleContentTableModel dataModel = new ModuleContentTableModel(controller.getContentOfType(type).getAttributeNames());
		if(availableObjects!=null){
//			dataModel =new ModuleContentTableModel(availableObjects.get(0).getAttributeNames());
			for(int i=0; i<availableObjects.size(); i++){
				dataModel.addRow(availableObjects.get(i));
			}
		}else {
			dataModel.addRow(controller.getContentOfType(type));
		}
		
		dataModel.setEditable(true);
		table.setModel(dataModel);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(), 60));
		table.setFillsViewportHeight(true);
		// hide ID column
		try {
			table.getColumn(TagNames.ID).setMinWidth(0); // Must be set before maxWidth!!
			table.getColumn(TagNames.ID).setMaxWidth(0);
			table.getColumn(TagNames.ID).setWidth(0);
		}catch(Exception e) {}

	    TableCellListener tcl = new TableCellListener(table, new EditAction(table, type));
		
		return table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch(cmd) {
		case CMD_CANCEL:
			setVisible(false);
			dispose();
			break;
		case CMD_APPLY:
			saveCurrentValues();
			controller.setMDEConfiguration(conf);
			dialog.reloadView();
			setVisible(false);
			dispose();
			//TODO: repaint MDECONTENT
			firePropertyChange(MetaDataDialog.REFRESH_MIC_CONTENT, false, true);
			break;
		case CMD_SAVE:
			saveCurrentValues();
			conf.writeToFile(dialog.getMDEConfigPath());
			//TODO: repaint MDECONTENT
			break;
		case CMD_NEW_MIC:
			saveCurrentValues();
			createNewMicroscope(txtMicName.getText());
			// delete textfield value
			txtMicName.setText("");
			break;

			
		case CMD_DEL_MIC:
			System.out.println("Start delete mic...");
			// to disable selectionListener
			deleteMic=true;
			deleteMicroscope();
			deleteMic=false;
			System.out.println("...End delete mic");
			break;
		}
	}


	/**
	 * Delete selected microscope in micList.
	 */
	private void deleteMicroscope() {
		String micName= (String) micList.getSelectedValue();
		if(micName.equals(MDEConfiguration.UNIVERSAL))
			return;
		int micIndex = micList.getSelectedIndex();
		ImporterAgent.getRegistry().getLogger().debug(this, "-- DELETE ["+micName+"]: on index "+micIndex+"/"+micList.getModel().getSize());
		if(micName==null || micName.equals("") )
			return;
		// delete from micList
		((DefaultListModel)micList.getModel()).remove(micIndex);
		
		// delete in conf
		conf.removeAllPredefsForMicroscope(micName);
		micList.setSelectedIndex(0);
		
	}
	
	private void createNewMicroscope(String micName) {
		if(micName==null || micName.equals(""))
			return;
		ImporterAgent.getRegistry().getLogger().debug(this, "--create new category: "+micName);
		conf.initPredefinitionsForMicroscope(micName, new ModuleList());
		//reload list
		loadMicroscopeList();
		micList.setSelectedIndex(listModelMic.getSize());
		revalidate();
		repaint();
	}

	private void loadMicroscopeList() {
		if(listModelMic==null)
			listModelMic=new DefaultListModel<>();
		listModelMic.clear();
		availableMics=conf.getMicNames();
		if(availableMics !=null) {
			for(String s:availableMics) {
				listModelMic.addElement(s);
			}
		}
		if(micList!=null) {
			micList.revalidate();
			micList.repaint();
		}
		ImporterAgent.getRegistry().getLogger().debug(this, "-- MDEConfigurator: Load available mic list: ");
		ImporterAgent.getRegistry().getLogger().debug(this, "\t"+Arrays.toString(availableMics));
	}
	
	
	/**
	 * Load list ob object configuration, despite container
	 * @param mic
	 */
	private void loadObjectList(String mic) {
		if(listModelObj==null) {
			listModelObj=new DefaultListModel<>();
		}
		listModelObj.clear();
		
		HashMap<String, ModuleConfiguration> currentObjConf = conf.getConfiguratedObjects(mic);
		
		if(currentObjConf!=null) {
			System.out.println("-- Load objects for "+mic);
			for (Entry<String, ModuleConfiguration> entry : currentObjConf.entrySet()) {
				if(entry.getValue()!=null && !conf.isContainer(entry.getKey())) {
					listModelObj.addElement(entry.getKey());
				}
			}
		}
	}
	
	
	//TODO
	private void saveCurrentValues() {
		if(predefinitionsObject_Panel!=null && predefinitionsObject_Panel.getComponentCount()>0) {
			for(Component c:predefinitionsObject_Panel.getComponents()) {
				if(c instanceof InstrumentTable) {
					System.out.println("-- Save predefinitions for : "+currentMic+" : "+((InstrumentTable) c).getInstrumentName());
					List<ModuleContent> list = ((InstrumentTable) c).getContent();
					conf.setPredefinitionsForMicroscope(currentMic, ((InstrumentTable) c).getInstrumentName(), ((InstrumentTable) c).getContent());
				}
			}
		}
		if(layoutObject_Panel!=null && layoutObject_Panel.getComponentCount()>0) {
			for(Component c:layoutObject_Panel.getComponents()) {
				if(c instanceof ObjLayoutConf_Panel) {
					conf.setConfiguration(currentMic, ((ObjLayoutConf_Panel) c).getType(),((ObjLayoutConf_Panel) c).getConfiguration());
				}
			}
		}
	}

	
	/*
	 *  This class listens for changes made to the data in the table via the
	 *  TableCellEditor. When editing is started, the value of the cell is saved
	 *  When editing is stopped the new value is saved. When the oold and new
	 *  values are different, then the provided Action is invoked.
	 *
	 *  The source of the Action is a TableCellListener instance.
	 */
	public class TableCellListener implements PropertyChangeListener, Runnable
	{
		private JTable table;
		private Action action;

		private int row;
		private int column;
		private Object oldValue;
		private Object newValue;

		/**
		 *  Create a TableCellListener.
		 *  @param table   the table to be monitored for data changes
		 *  @param action  the Action to invoke when cell data is changed
		 */
		public TableCellListener(JTable table, Action action)
		{
			this.table = table;
			this.action = action;
			this.table.addPropertyChangeListener( this );
		}

		/**
		 *  Create a TableCellListener with a copy of all the data relevant to
		 *  the change of data for a given cell.
		 *  @param row  the row of the changed cell
		 *  @param column  the column of the changed cell
		 *  @param oldValue  the old data of the changed cell
		 *  @param newValue  the new data of the changed cell
		 */
		private TableCellListener(JTable table, int row, int column, Object oldValue, Object newValue)
		{
			this.table = table;
			this.row = row;
			this.column = column;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		/**
		 *  Get the column that was last edited
		 *  @return the column that was edited
		 */
		public int getColumn()
		{
			return column;
		}

		/**
		 *  Get the new value in the cell
		 *  @return the new value in the cell
		 */
		public Object getNewValue()
		{
			return newValue;
		}

		/**
		 *  Get the old value of the cell
		 *  @return the old value of the cell
		 */
		public Object getOldValue()
		{
			return oldValue;
		}

		/**
		 *  Get the row that was last edited
		 *  @return the row that was edited
		 */
		public int getRow()
		{
			return row;
		}

		/**
		 *  Get the table of the cell that was changed
		 *  @return the table of the cell that was changed
		 */
		public JTable getTable()
		{
			return table;
		}
	//
	//  Implement the PropertyChangeListener interface
	//
		@Override
		public void propertyChange(PropertyChangeEvent e)
		{
			//  A cell has started/stopped editing

			if ("tableCellEditor".equals(e.getPropertyName()))
			{
				if (table.isEditing())
					processEditingStarted();
				else
					processEditingStopped();
			}
		}

		/*
		 *  Save information of the cell about to be edited
		 */
		private void processEditingStarted()
		{
			//  The invokeLater is necessary because the editing row and editing
			//  column of the table have not been set when the "tableCellEditor"
			//  PropertyChangeEvent is fired.
			//  This results in the "run" method being invoked

			SwingUtilities.invokeLater( this );
		}
		/*
		 *  See above.
		 */
		@Override
		public void run()
		{
			row = table.convertRowIndexToModel( table.getEditingRow() );
			column = table.convertColumnIndexToModel( table.getEditingColumn() );
			oldValue = table.getModel().getValueAt(row, column);
			newValue = null;
		}

		/*
		 *	Update the Cell history when necessary
		 */
		private void processEditingStopped()
		{
			newValue = table.getModel().getValueAt(row, column);

			//  The data has changed, invoke the supplied Action

			if (! newValue.equals(oldValue))
			{
				//  Make a copy of the data in case another cell starts editing
				//  while processing this change

				TableCellListener tcl = new TableCellListener(
					getTable(), getRow(), getColumn(), getOldValue(), getNewValue());

				ActionEvent event = new ActionEvent(
					tcl,
					ActionEvent.ACTION_PERFORMED,
					"");
				action.actionPerformed(event);
			}
		}
	}
	
	private class AddAction extends AbstractAction{
		private final JTable actionTable;
		private final String type;
		public AddAction(JTable table,String type) {
			actionTable=table;
			this.type=type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			((ModuleContentTableModel) actionTable.getModel()).addRow(controller.getContentOfType(type));
		}
		
	}
	
	private class DelAction extends AbstractAction{
		private final JTable actionTable;
		private final String type;
		public DelAction(JTable table,String type) {
			actionTable=table;
			this.type=type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(actionTable.getSelectedRow()!=-1)
				((ModuleContentTableModel) actionTable.getModel()).removeRow(actionTable.getSelectedRow());
		}
		
	}
	
	private class EditAction extends AbstractAction{
		private final JTable actionTable;
		private final String type;
		public EditAction(JTable table,String type) {
			actionTable=table;
			this.type=type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TableCellListener tcl = (TableCellListener)e.getSource();
			ImporterAgent.getRegistry().getLogger().debug(this, "EDIT Table: Old   : " + tcl.getOldValue()+", New   : " + tcl.getNewValue());
		}
		
	}
	
	private class InstrumentTable extends JPanel{
		private JTable t;
		private String name;
		public InstrumentTable(List<ModuleContent> val,String name) {
			super(new BorderLayout());
			
			this.t=createTable(val,name);
			this.name=name;
			
			JPanel thisTable = new JPanel(new BorderLayout());
			thisTable.add(t.getTableHeader(),BorderLayout.PAGE_START);
			
			thisTable.add(t,BorderLayout.CENTER);
			JScrollPane scrollPane = new JScrollPane(thisTable);
			
			JButton addBtn = new JButton("Add");
			addBtn.addActionListener(new AddAction(t, name));
			JButton delBtn = new JButton("Delete");
			delBtn.addActionListener(new DelAction(t, name));
			
			JPanel keyPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
//			keyPanel.add(new JLabel(name));
			keyPanel.add(addBtn);
			keyPanel.add(delBtn);
			
			add(keyPanel,BorderLayout.SOUTH);
			add(scrollPane,BorderLayout.CENTER);
			setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black), name));
			
			revalidate();
			repaint();
		}
		
		public String getInstrumentName() {
			return name;
		}
		
		public List<ModuleContent> getContent(){
			List<ModuleContent> result = new ArrayList<>();
			for(int i=0; i<t.getModel().getRowCount(); i++) {
				
				ModuleContent rowContent=((ModuleContentTableModel) t.getModel()).getRowData(i,controller.getContentOfType(name));
				if(rowContent!=null) {
//					ImporterAgent.getRegistry().getLogger().debug(this, "--Add instrument content for "+name+" : "+rowContent.getAttributeValue(TagNames.MODEL));
					result.add(rowContent);
				}
			}
			
			return result;
		}
	}
	
	
	
}
