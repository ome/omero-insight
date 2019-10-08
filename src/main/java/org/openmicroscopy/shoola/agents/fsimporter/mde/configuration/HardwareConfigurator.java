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
import java.awt.GridLayout;
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
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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

import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleContentTableModel;
import org.openmicroscopy.shoola.util.MonitorAndDebug;

/**
 * TODO: hide "User::" fields, update mdecontent after changes; 
 * Edit MDE config file.
 * 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class HardwareConfigurator extends JDialog implements ActionListener{
	public static final String CMD_APPLY = "apply";
	public static final String CMD_CANCEL = "cancel";
	public static final String CMD_SAVE = "save";
	public static final String CMD_NEW_MIC = "create new microsocope";
	public static final String CMD_DEL_MIC = "delete selected microsocope";
	public static final String CMD_NEW_OBJ = "add new object";
	public static final String CMD_DEL_OBJ ="delete selected object";
	
	final int WIDTH=1000;
	final int HEIGHT=600;
	private ModuleController controller;
	private DefaultListModel<String> listModelMic;
	private JList micList;
	private DefaultListModel<String> listModelObj;
	private JList objList;
	
	private JTextField txtMicName;
	private JPanel tablePanel;
	private MDEConfiguration conf;
	private String currentMic;
	private String[] availableMics;
	private JComboBox<String> objects;
	private MetaDataDialog dialog;
	
	public HardwareConfigurator(JFrame parent,MetaDataDialog dialog) {
		super(parent,"Hardware Configurator");
		this.dialog=dialog;
		this.controller=ModuleController.getInstance();
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
		MonitorAndDebug.printConsole("--BUILD GUI : HardwareConfiguration");
		getContentPane().setLayout(new BorderLayout(5,5));
		setModal(true);
		
		loadMicroscopeList();
        
        JPanel listPaneMic=createMicPanel();
        JPanel listPaneObj=createObjPanel();
        
        JPanel listPane=new JPanel(new GridLayout(0,2));
//        listPane.setPreferredSize(new Dimension(WIDTH,HEIGHT));
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
       
        tablePanel=new JPanel(new BorderLayout());
//        tablePanel.setPreferredSize(new Dimension(300,500));
//		getInstrumentTables(currentMic,(String) objList.getSelectedValue()); 
		JScrollPane scrollView = new JScrollPane(tablePanel);
		
		JPanel main=new JPanel(new BorderLayout());
		main.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		main.add(listPane,BorderLayout.NORTH);
		main.add(scrollView,BorderLayout.CENTER);
		main.add(btnPanel,BorderLayout.SOUTH);
		
		getContentPane().add(main,BorderLayout.CENTER);
		pack();
		
	}
	
	private JPanel createObjPanel() {
		loadObjectList(currentMic);
		objList=new JList<String>(listModelObj);
		objList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		objList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if (objList.getSelectedIndex() != -1) {
						String newObj=(String) objList.getSelectedValue();
						saveCurrentTable();
						// TODO: refresh view
						if(tablePanel!=null) tablePanel.removeAll();
						MonitorAndDebug.printConsole("-- Load table for "+currentMic+"::"+newObj);
						getInstrumentTables(currentMic, newObj);
						revalidate();
						repaint();
					} 
				}
			}
		});
		
		if(!listModelObj.isEmpty()) 
			objList.setSelectedIndex(0);
		objList.setVisibleRowCount(10);

		JScrollPane listScrollPane = new JScrollPane(objList);
		JPanel pane=new JPanel(new BorderLayout());
		pane.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black), "Objects"));
		pane.add(listScrollPane,BorderLayout.CENTER);

		JButton btnNewObj = new JButton("Add");
		btnNewObj.setActionCommand(CMD_NEW_OBJ);
		btnNewObj.addActionListener(this);
		
		 JButton btnDelObj = new JButton("Delete");
		 btnDelObj.setActionCommand(CMD_DEL_OBJ);
		 btnDelObj.addActionListener(this);
		 
		 JPanel btnPanelObj=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		 btnPanelObj.add(btnNewObj);
		 btnPanelObj.add(btnDelObj);

        String[] items=conf.getNameOfObjects();//controller.getDefaultContentsName();
        objects=new JComboBox<String>(items);

		JPanel newObjPane=new JPanel(new BorderLayout());
		newObjPane.add(objects,BorderLayout.CENTER);
		newObjPane.add(btnPanelObj,BorderLayout.EAST);

		pane.add(newObjPane,BorderLayout.SOUTH);
		return pane;
	}

	private JPanel createMicPanel() {
		// list right side
		micList=new JList(listModelMic);
		micList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		micList.setSelectedIndex(controller.getMicIndex(currentMic));
		micList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if (micList.getSelectedIndex() != -1) {
						String newSelection=availableMics[micList.getSelectedIndex()];
						// save input
						saveCurrentTable();
						currentMic=newSelection;
						// remove table for preview selection
						if(tablePanel!=null) tablePanel.removeAll();
						// load new objects
						loadObjectList(newSelection);
						// show table for first object
						if(!listModelObj.isEmpty()) 
							objList.setSelectedIndex(0);
						objList.revalidate();
						objList.repaint();
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
        
        JButton btnNewMic = new JButton("Add");
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

	
	private void deleteObject() {
		String objName= (String) objList.getSelectedValue();
		String micName = (String) micList.getSelectedValue();
		int objIndex = objList.getSelectedIndex();
		MonitorAndDebug.printConsole("-- DELETE ["+micName+"::"+objName+"]: on index "+objIndex+"/"+objList.getModel().getSize());
		// delete from objList
		((DefaultListModel)objList.getModel()).remove(objIndex);
		// delete from instrument list
		conf.removeHardwareConfInstrumentForMicroscope(objName,micName);
		ModuleList hardware = conf.getInstruments(micName);
		MonitorAndDebug.printConsole("\t\t Instruments["+micName+"]: "+hardware.keySet());
		
		tablePanel.removeAll();
		tablePanel.revalidate();
		tablePanel.repaint();
		
		
	}
	/**
	 * Load hardware specification for selected micrsocope micName.
	 * @param micName
	 */
	private void getInstrumentTables(String micName,String instrument) {
		if(tablePanel==null) {
			MonitorAndDebug.printConsole("-- ERROR: tablePanel is not defined");
			return;
		}
		MonitorAndDebug.printConsole("-- getInstrumentTable for: "+micName+"::"+instrument);
		ModuleList hardware = conf.getInstruments(micName);
		if(hardware==null) {
			MonitorAndDebug.printConsole("\tMic not available: "+micName);
			return;
		}
		List<ModuleContent> cInstrument=hardware.get(instrument);
		if(cInstrument==null) {
			if(instrument==null)
				return;
			MonitorAndDebug.printConsole("\tHardware not available: "+instrument);
			tablePanel.add(new InstrumentTable(null, instrument),BorderLayout.CENTER);
		}else {
			MonitorAndDebug.printConsole("\tload instrument "+instrument+"["+cInstrument.size()+"] for "+micName);
			tablePanel.add(new InstrumentTable(cInstrument,instrument),BorderLayout.CENTER);
		}
		tablePanel.revalidate();
		tablePanel.repaint();
		
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
			saveCurrentTable();
			controller.setMDEConfiguration(conf);
			dialog.reloadView();
			setVisible(false);
			dispose();
			//TODO: repaint MDECONTENT
			firePropertyChange(MetaDataDialog.REFRESH_MIC_CONTENT, false, true);
			break;
		case CMD_SAVE:
			saveCurrentTable();
			conf.writeToFile();
			//TODO: repaint MDECONTENT
			break;
		case CMD_NEW_MIC:
			saveCurrentTable();
			createNewMicroscope(txtMicName.getText());
			// delete textfield value
			txtMicName.setText("");
			break;
		case CMD_NEW_OBJ:
			MonitorAndDebug.printConsole("-- HardwareConf::Add new objectTable");
			String obj=String.valueOf(objects.getSelectedItem());
			createNewObject(obj);
			
			break;
			
		case CMD_DEL_MIC:
			deleteMicroscope();
			break;
		case CMD_DEL_OBJ:
			deleteObject();
			break;
		}
	}

	private void createNewObject(String obj) {
		String micName = (String) micList.getSelectedValue();
		conf.setInstrumentForMicroscope(micName, obj, null);
		listModelObj.addElement(obj);
		objList.setSelectedIndex(listModelObj.getSize()-1);
		objList.revalidate();
		objList.repaint();
		revalidate();
		repaint();
	}

	/**
	 * Delete selected microscope in micList.
	 */
	private void deleteMicroscope() {
		String micName= (String) micList.getSelectedValue();
		if(micName.equals(MDEConfiguration.UNIVERSAL))
			return;
		int micIndex = micList.getSelectedIndex();
		MonitorAndDebug.printConsole("-- DELETE ["+micName+"]: on index "+micIndex+"/"+micList.getModel().getSize());
		if(micName==null || micName.equals("") )
			return;
		// delete from micList
		((DefaultListModel)micList.getModel()).remove(micIndex);
		
		// delete in conf
		conf.removeHardwareConfForMicroscope(micName);
		micList.setSelectedIndex(0);
		
	}
	
	private void createNewMicroscope(String micName) {
		if(micName==null || micName.equals(""))
			return;
		conf.addInstrumentsForMicroscope(micName, new ModuleList());
		//reload list
		loadMicroscopeList();
//		listModel.addElement(micName);
		micList.setSelectedIndex(listModelMic.getSize());
//		availableMics=conf.getMicNames();
		revalidate();
		repaint();
	}

	private void loadMicroscopeList() {
		if(listModelMic==null)
			listModelMic=new DefaultListModel<>();
		listModelMic.clear();
		String[] names = conf.getMicNames();
		if(names !=null) {
			for(String s:names) {
				listModelMic.addElement(s);
			}
		}
		if(micList!=null) {
			micList.revalidate();
			micList.repaint();
		}
		availableMics=names;
		System.out.println("-- Load available mic list: ");
		System.out.println("\t"+Arrays.toString(availableMics));
	}
	
	
	
	private void loadObjectList(String mic) {
		if(listModelObj==null) {
			listModelObj=new DefaultListModel<>();
		}
		listModelObj.clear();
		
		ModuleList hardware = conf.getInstruments(mic);
		
		if(hardware!=null) {
			MonitorAndDebug.printConsole("\t\t Instruments["+mic+"]: "+hardware.keySet());
			System.out.println("-- Load objects for "+mic);
			for (Entry<String, List<ModuleContent>> entry : hardware.entrySet()) {
				if(entry.getValue()!=null && entry.getValue().size()>0) {
					listModelObj.addElement(entry.getKey());
					System.out.println("\t list object : "+entry.getKey()+"["+entry.getValue().size()+"]");
				}
			}
			if(objList!=null) {
				objList.revalidate();
				objList.repaint();
			}
			
		}
	}
	
	
	//TODO
	private void saveCurrentTable() {
		if(tablePanel!=null && tablePanel.getComponentCount()>0) {
			for(Component c:tablePanel.getComponents()) {
				if(c instanceof InstrumentTable) {
					System.out.println("-- Save instrument definition : "+currentMic+" : "+((InstrumentTable) c).getInstrumentName());
					List<ModuleContent> list = ((InstrumentTable) c).getContent();
					conf.setInstrumentForMicroscope(currentMic, ((InstrumentTable) c).getInstrumentName(), ((InstrumentTable) c).getContent());
				}
			}
		}
	}

	// show tables for current selected microscope, save content of last selection
//	@Override
//	public void valueChanged(ListSelectionEvent e) {
//		System.out.println("List selection for: "+e.getSource());
//		if (e.getValueIsAdjusting() == false) {
//			if (micList.getSelectedIndex() != -1) {
//				String newSelection=availableMics[micList.getSelectedIndex()];
//				saveCurrentTable();
//				// TODO: refresh view
//				tablePanel.removeAll();
//				getInstrumentsTables(newSelection);
//				tablePanel.revalidate();
//				tablePanel.repaint();
//				revalidate();
//				repaint();
//
//			} 
//		}		
//	}
	
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
//            MonitorAndDebug.printConsole("Row   : " + tcl.getRow());
//            MonitorAndDebug.printConsole("Column: " + tcl.getColumn());
            MonitorAndDebug.printConsole("EDIT Table: Old   : " + tcl.getOldValue()+", New   : " + tcl.getNewValue());
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
//					MonitorAndDebug.printConsole("--Add instrument content for "+name+" : "+rowContent.getAttributeValue(TagNames.MODEL));
					result.add(rowContent);
				}
			}
			
			return result;
		}
		
	}
	
	
}
