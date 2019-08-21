package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.awt.BorderLayout;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
 * Holds editable tables for specific hardware instruments
 * @author Kunis
 *
 */
public class HardwareConfigurator extends JDialog implements ActionListener, ListSelectionListener{
	public static final String CMD_APPLY = "apply";
	public static final String CMD_CANCEL = "cancel";
	public static final String CMD_SAVE = "save";
	public static final String CMD_NEW = "create new microsocope";
	public static final String CMD_CHOOSE_OBJ="choose object";
	
	private ModuleController controller;
	private DefaultListModel<String> listModel;
	private JList list;
	private JTextField txtMicName;
	private JPanel tablePanel;
	private MDEConfiguration conf;
	private MDEConfiguration originConf;
	private String currentMic;
	private String[] availableMics;
	private JComboBox<String> objects;
	private MetaDataDialog dialog;
	
	public HardwareConfigurator(JFrame parent,MetaDataDialog dialog) {
		super(parent,"Hardware Configurator");
		this.dialog=dialog;
		this.controller=ModuleController.getInstance();
		this.conf=controller.getMDEConfiguration();
		this.originConf=new MDEConfiguration(conf);
		this.currentMic=controller.getCurrentMicName();
		this.availableMics=conf.getMicNames();
		buildGUI();
		
		revalidate();
		repaint();
		pack();
		setVisible(true);
	}
	
	private void buildGUI() {
		this.setBounds(200,200,635,340);
		getContentPane().setLayout(new BorderLayout(5,5));
		setModal(true);
		
		loadMicroscopeList();
//		listModel=new DefaultListModel<>();
//		String[] names = controller.getHardwareConfiguration().getMicNames();
//		if(names !=null) {
//			for(String s:names) {
//				listModel.addElement(s);
//			}
//		}
	
		JPanel main=new JPanel(new BorderLayout());
		
		
		// list right side
		list=new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(controller.getMicIndex(currentMic));
        list.addListSelectionListener(this);
        list.setVisibleRowCount(10);
        
        JScrollPane listScrollPane = new JScrollPane(list);
        
        JPanel listPane=new JPanel(new BorderLayout());
        listPane.add(listScrollPane,BorderLayout.CENTER);
        
        JButton btnNew = new JButton("Add");
        btnNew.setBounds(300, 240, 80, 20);
        btnNew.setActionCommand(CMD_NEW);
        btnNew.addActionListener(this);
        
        txtMicName=new JTextField(50);
        
        JPanel newMicPane=new JPanel(new BorderLayout());
        newMicPane.add(txtMicName,BorderLayout.CENTER);
        newMicPane.add(btnNew,BorderLayout.EAST);
        
        listPane.add(newMicPane,BorderLayout.SOUTH);
        
        // button pane
		JPanel btnPanel=new JPanel();
		
		JButton btnApply=new JButton("Apply");
        btnApply.setBounds(300, 240, 80, 20);
        btnApply.setActionCommand(CMD_APPLY);
        btnApply.addActionListener(this);
        btnPanel.add(btnApply);
        
        JButton btnCancel=new JButton("Cancel");
        btnCancel.setBounds(300, 240, 80, 20);
        btnCancel.setActionCommand(CMD_CANCEL);
        btnCancel.addActionListener(this);
        btnPanel.add(btnCancel);
        
        JButton btnSave=new JButton("Save");
        btnSave.setBounds(300, 240, 80, 20);
        btnSave.setActionCommand(CMD_SAVE);
        btnSave.addActionListener(this);
        btnPanel.add(btnSave);
        
        JPanel newTablePanel=new JPanel(new BorderLayout());
        String[] items=conf.getNameOfObjects();//controller.getDefaultContentsName();
        objects=new JComboBox<String>(items);
        JButton btnAddObjects=new JButton("Add");
        btnAddObjects.setBounds(300, 240, 80, 20);
        btnAddObjects.setActionCommand(CMD_CHOOSE_OBJ);
        btnAddObjects.addActionListener(this);
        JLabel lblObjects = new JLabel("Add new table for:");
        lblObjects.setLabelFor(objects);
        newTablePanel.add(lblObjects,BorderLayout.WEST);
        newTablePanel.add(objects,BorderLayout.CENTER);
        newTablePanel.add(btnAddObjects,BorderLayout.EAST);
       
        tablePanel=new JPanel(new GridLayout(0, 1));
		getInstrumentsTables(currentMic); 
		JScrollPane scrollView = new JScrollPane(tablePanel);
		
		JPanel tbPane=new JPanel(new BorderLayout());
		tbPane.add(newTablePanel,BorderLayout.NORTH);
		tbPane.add(scrollView,BorderLayout.CENTER);
		
		main.add(listPane,BorderLayout.WEST);
		main.add(tbPane,BorderLayout.CENTER);
		main.add(btnPanel,BorderLayout.SOUTH);
		
		getContentPane().add(main,BorderLayout.CENTER);
		
	}
	
	/**
	 * Load hardware specification for selected micrsocope micName.
	 * @param micName
	 */
	private void getInstrumentsTables(String micName) {
		
		ModuleList hardware = conf.getInstruments(micName);
		currentMic=micName;
//		System.out.println("-- load instruments for "+currentMic);
		for (Entry<String, List<ModuleContent>> entry : hardware.entrySet()) {
			if(entry.getValue()!=null) {
//			System.out.println("-- load content "+entry.getKey()+": "+entry.getValue().size());
			tablePanel.add(new InstrumentTable(entry.getValue(), entry.getKey()));
			}
		}
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

	    TableCellListener tcl = new TableCellListener(table, new EditAction(table, type));
		
		return table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch(cmd) {
		case CMD_CANCEL:
			conf=originConf;
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
			break;
		case CMD_SAVE:
			saveCurrentTable();
			conf.writeToFile();
			//TODO: repaint MDECONTENT
			break;
		case CMD_NEW:
			saveCurrentTable();
			createNewMicroscope(txtMicName.getText());
			// delete textfield value
			txtMicName.setText("");
			break;
		case CMD_CHOOSE_OBJ:
			String obj=String.valueOf(objects.getSelectedItem());
			tablePanel.add(new InstrumentTable(null, obj));
			 tablePanel.revalidate();
             tablePanel.repaint();
             revalidate();
             repaint();
			break;
		}
			
		
	}

	private void createNewMicroscope(String micName) {
		conf.addInstrumentsForMicroscope(micName, new ModuleList());
		//reload list
		loadMicroscopeList();
//		listModel.addElement(micName);
		list.setSelectedIndex(listModel.getSize());
//		availableMics=conf.getMicNames();
		revalidate();
		repaint();
	}

	private void loadMicroscopeList() {
		if(listModel==null)
			listModel=new DefaultListModel<>();
		listModel.clear();
		String[] names = conf.getMicNames();
		if(names !=null) {
			for(String s:names) {
				listModel.addElement(s);
			}
		}
		if(list!=null) {
			list.revalidate();
			list.repaint();
		}
		availableMics=conf.getMicNames();
	}
	
	//TODO
	private void saveCurrentTable() {
		System.out.println("-- Save current instrument definition for microscope: "+currentMic);
		ModuleList result=new ModuleList();
		for(Component c:tablePanel.getComponents()) {
			if(c instanceof InstrumentTable) {
				List<ModuleContent> list = ((InstrumentTable) c).getContent();
//				System.out.println("------------ parse from "+((InstrumentTable) c).getInstrumentName());
//				for(ModuleContent mc:list) {
//					System.out.println("\t"+mc.getAttributeValue(TagNames.MODEL));
//				}
				result.put(((InstrumentTable) c).getInstrumentName(), list);
			}
		}
		
		conf.addInstrumentsForMicroscope(currentMic, result);
	}

	// show tables for current selected microscope, save content of last selection
	@Override
	public void valueChanged(ListSelectionEvent e) {
		 if (e.getValueIsAdjusting() == false) {
	            if (list.getSelectedIndex() != -1) {
	            	String newSelection=availableMics[list.getSelectedIndex()];
	            	saveCurrentTable();
	    			// TODO: refresh view
	            	tablePanel.removeAll();
	                getInstrumentsTables(newSelection);
	                tablePanel.revalidate();
	                tablePanel.repaint();
	                revalidate();
	                repaint();
	 
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
//				System.out.println("Editing table");
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
//			System.out.println("--Edit table: "+type);
			TableCellListener tcl = (TableCellListener)e.getSource();
//            MonitorAndDebug.printConsole("Row   : " + tcl.getRow());
//            MonitorAndDebug.printConsole("Column: " + tcl.getColumn());
            MonitorAndDebug.printConsole("Old   : " + tcl.getOldValue());
            MonitorAndDebug.printConsole("New   : " + tcl.getNewValue());
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
			
			JButton addBtn = new JButton("+");
			addBtn.addActionListener(new AddAction(t, name));
			JButton delBtn = new JButton("-");
			delBtn.addActionListener(new DelAction(t, name));
			
			JPanel keyPanel=new JPanel(new FlowLayout());
			keyPanel.add(new JLabel(name));
			keyPanel.add(addBtn);
			keyPanel.add(delBtn);
			
			add(keyPanel,BorderLayout.NORTH);
			add(scrollPane,BorderLayout.CENTER);
		}
		
		public String getInstrumentName() {
			return name;
		}
		
		public List<ModuleContent> getContent(){
			List<ModuleContent> result = new ArrayList<>();
			for(int i=0; i<t.getModel().getRowCount(); i++) {
				
				ModuleContent rowContent=((ModuleContentTableModel) t.getModel()).getRowData(i,controller.getContentOfType(name));
				if(rowContent!=null) {
//					System.out.println("--Add instrument content for "+name+" : "+rowContent.getAttributeValue(TagNames.MODEL));
					result.add(rowContent);
				}
			}
			
			return result;
		}
		
	}
	
	
}
