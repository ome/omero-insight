package org.openmicroscopy.shoola.agents.fsimporter.mde.configuration;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.accessibility.Accessible;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleList;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.TagDataTableModel;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;


/**
 * TODO: load standard configuration at start, define new object, delete idents(unessarry)
 * @author Susanne Kunis
 *
 */
public class ObjectConfigurator extends JDialog implements ActionListener, ListSelectionListener{
	public static final String CMD_APPLY = "apply";
	public static final String CMD_CANCEL = "cancel";
	public static final String CMD_SAVE = "save";
	public static final String CMD_NEW = "create new microsocope";
	public static final String CMD_CHOOSE_OBJ="choose object";
	public static final String CMD_CREATE_OBJ="create object";

	private DefaultListModel<String> listModel;
	private JList list;
	private JTextField txtMicName;
	private JTextField txtObjName;
	private JPanel tablePanel;
	// working on this conf
	private MDEConfiguration conf;
	// copy of conf to reset configuration
	private MDEConfiguration originConf;
	private String currentMic;
	private String[] availableMics;
	private JComboBox<String> objects;
	private MetaDataDialog dialog;

	public ObjectConfigurator(JFrame parent,MetaDataDialog dialog) {
		super(parent,"Object Configurator");
		ModuleController controller=ModuleController.getInstance();
		this.dialog=dialog;

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
		this.setSize(635,340);
		getContentPane().setLayout(new BorderLayout(5,5));
		setModal(true);

		loadMicroscopeList();
		//		listModel=	new DefaultListModel<>();
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
		list.setSelectedIndex(ModuleController.getInstance().getMicIndex(currentMic));
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
		btnAddObjects.setEnabled(!currentMic.equals(MDEConfiguration.UNIVERSAL));
		btnAddObjects.setBounds(300, 240, 80, 20);
		btnAddObjects.setActionCommand(CMD_CHOOSE_OBJ);
		btnAddObjects.addActionListener(this);
		JLabel lblObjects = new JLabel("Add new table for:");
		lblObjects.setLabelFor(objects);
		newTablePanel.add(lblObjects,BorderLayout.WEST);
		newTablePanel.add(objects,BorderLayout.CENTER);
		newTablePanel.add(btnAddObjects,BorderLayout.EAST);
		
		JPanel createNewObjPane=new JPanel(new BorderLayout());
		JButton btnCreateObj=new JButton("Create New Object");
		btnCreateObj.setBounds(300, 240, 80, 20);
		btnCreateObj.setActionCommand(CMD_CREATE_OBJ);
		btnCreateObj.addActionListener(this);
//		txtObjName=new JTextField(50);
//		JLabel lblCreateObjects = new JLabel("Add new table for:");
//		lblCreateObjects.setLabelFor(txtObjName);
//		newTablePanel.add(lblCreateObjects,BorderLayout.WEST);
//		newTablePanel.add(txtObjName,BorderLayout.CENTER);
//		newTablePanel.add(btnCreateObj,BorderLayout.EAST);
		createNewObjPane.add(btnCreateObj);

		tablePanel=new JPanel(new GridLayout(0, 1));
		getObjectTables(currentMic); 
		JScrollPane scrollView = new JScrollPane(tablePanel);

		JPanel subTbPane=new JPanel(new GridLayout(2, 1));
		subTbPane.add(newTablePanel);
		subTbPane.add(createNewObjPane);
		JPanel tbPane=new JPanel(new BorderLayout());
		tbPane.add(subTbPane,BorderLayout.NORTH);
		tbPane.add(scrollView,BorderLayout.CENTER);

		main.add(listPane,BorderLayout.WEST);
		main.add(tbPane,BorderLayout.CENTER);
		main.add(btnPanel,BorderLayout.SOUTH);

		getContentPane().add(main,BorderLayout.CENTER);

	}

	/**
	 * Load object specifications for selected microscope micName.
	 * @param micName
	 */
	private void getObjectTables(String micName) {
		HashMap<String,ModuleContent> map=conf.getContentList(micName);
		currentMic=micName;

		if(map!= null) {
			System.out.println("-- load objects for "+currentMic);
			for (Entry<String, ModuleContent> entry : map.entrySet()) {
				tablePanel.add(new ObjectConfTable(entry.getValue(), entry.getKey(),currentMic));
			}
		}
	}
	
	protected void editContent(ModuleContent content) {
		ModuleContent c=null;
		if(content!=null) {
			// copy src
			c=new ModuleContent(content);
			NewObjectConfDialog oDialog= new NewObjectConfDialog(new JFrame(), content, content.getType());
			content=oDialog.getModuleContent();
		}
		if(content!=null) {
			//replace content in current view
			for(Component comp:tablePanel.getComponents()) {
				if(comp instanceof ObjectConfTable) {
					if(((ObjectConfTable) comp).getObjectType().equals(content.getType())) {
						tablePanel.remove(comp);
						tablePanel.add(new ObjectConfTable(content, content.getType(), currentMic));
						tablePanel.revalidate();
						tablePanel.repaint();
						revalidate();
						repaint();
						return;
					}
				}
			}
			
		}
	}
	protected void removeContent(ModuleContent content) {
		if(content==null)
			return;
		System.out.println("-- remove "+content.getType());
		//get current selected microscope
//		String mic=availableMics[list.getSelectedIndex()];
		for(Component c:tablePanel.getComponents()) {
			if(c instanceof ObjectConfTable) {
				if(((ObjectConfTable) c).getObjectType().equals(content.getType())) {
					tablePanel.remove(c);
					tablePanel.revalidate();
					tablePanel.repaint();
					revalidate();
					repaint();
					return;
				}
			}
		}		
	}

	/**
	 * Create object attribute table for given type.
	 * @param content
	 * @param type
	 * @return
	 */
	private JTable createTable(ModuleContent content,String type,boolean allEditable) {
		JTable table=new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TagDataTableModel dataModel = new TagDataTableModel(type,conf,allEditable);
		if(content!=null && content.getTagList()!=null){
//			System.out.println("-- Add attribute table for "+type+" with num elems: "+content.getTagList().size());
			for(int i=0; i<content.getTagList().size(); i++){
				dataModel.addRow(content.getTagList().get(i));
			}
		}
		table.setModel(dataModel);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(), 60));
		table.setFillsViewportHeight(true);

		//	    TableCellListener tcl = new TableCellListener(table, new EditAction(table, type));

		return table;
	}

	public JCheckBox[] createCheckBoxItems(String[] parents) {
		HashMap<String,ModuleContent> contentList=conf.getContentList(currentMic);
		if(contentList==null)
			return null;
		String[] objectList=contentList.keySet().toArray(new String[contentList.keySet().size()]);
		
		JCheckBox[] myitems =new JCheckBox[objectList.length];
		for(int i=0; i<objectList.length;i++) {
			myitems[i]=new JCheckBox(objectList[i]);
			if(parents!=null) {
				for(int j=0; j<parents.length;j++) {
					if(objectList[i].equals(parents[j])) {
						myitems[i].setSelected(true);
					}else {
						myitems[i].setSelected(false);
					}
				}
			}
		}
		
		return myitems;
	}

	public CheckableItem[] createCheckableItemList(
			String[] parents) {
		HashMap<String,ModuleContent> contentList=conf.getContentList(currentMic);
		if(contentList==null)
			return null;
		String[] objectList=contentList.keySet().toArray(new String[contentList.keySet().size()]);
		CheckableItem[] result =new CheckableItem[objectList.length];
		for(int i=0; i<objectList.length;i++) {
			result[i]=new CheckableItem(objectList[i], false);
		}
		return result;
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
			MonitorAndDebug.printConsole("-------------- APPLY OCONF-------------");
			saveCurrentTable();
			ModuleController.getInstance().setMDEConfiguration(conf);
			dialog.reloadView();
			setVisible(false);
			dispose();
			//TODO: repaint MDECONTENT
			break;
		case CMD_SAVE:
			MonitorAndDebug.printConsole("-------------- SAVE OCONF-------------");
			saveCurrentTable();
			conf.writeToFile();
			//TODO: repaint MDECONTENT
			break;
		case CMD_NEW:
			saveCurrentTable();
			createNewMicroscope(txtMicName.getText());
			txtMicName.setText("");
			break;
		case CMD_CHOOSE_OBJ:
			MonitorAndDebug.printConsole("-------------- ADD OBJ-------------");
			String obj=String.valueOf(objects.getSelectedItem());
			if(!conf.contentExists(currentMic, obj)) {
				tablePanel.add(new ObjectConfTable(conf.getContent(MDEConfiguration.UNIVERSAL, obj), obj,currentMic));
				tablePanel.revalidate();
				tablePanel.repaint();
				revalidate();
				repaint();
			}
			break;
		case CMD_CREATE_OBJ:
			NewObjectConfDialog oDialog= new NewObjectConfDialog(new JFrame(), null, "");
			ModuleContent co=oDialog.getModuleContent();
			createNewObject(co);
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
	
	/**
	 * Create new object element. New object are also added to all other workstation. You can change
	 * the visibility for object attributes.
	 * @param objname
	 */
	private void createNewObject(ModuleContent c) {
		if(c==null) {
			System.out.println("--cannot create new object: empty content");
			return;
		}
		System.out.println("-- add new obj "+c.getType());
		if(!conf.contentExists(MDEConfiguration.UNIVERSAL, c.getType())) {
			conf.addContent(MDEConfiguration.UNIVERSAL, c);
			tablePanel.add(new ObjectConfTable(c, c.getType(),currentMic));
			tablePanel.revalidate();
			tablePanel.repaint();
			//update list of available objects
			//		objects=new JComboBox<String>(conf.getNameOfObjects());
			objects.addItem(c.getType());
			conf.addContent(currentMic, c);
		}
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
		System.out.println("-- Save current object definition for microscope: "+currentMic);
		HashMap<String, ModuleContent> result=new HashMap<String,ModuleContent>();
		for(Component c:tablePanel.getComponents()) {
			if(c instanceof ObjectConfTable) {
				//TODO: make a copy here?
				ModuleContent content=((ObjectConfTable)c).getModuleContent();
				System.out.println("-- Save attributes for types "+content.getType()+
						" with num elems: "+content.getTagList().size()+", p: "+content.getParents().length);
				result.put(content.getType(), content);
			}
		}
		conf.addContent(currentMic, result);
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
				getObjectTables(newSelection);
				tablePanel.revalidate();
				tablePanel.repaint();
				revalidate();
				repaint();

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
			((TagDataTableModel) actionTable.getModel()).addNewRow();
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
				((TagDataTableModel) actionTable.getModel()).removeRow(actionTable.getSelectedRow());
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
			//			MonitorAndDebug.printConsole("--Edit table: "+type);
			//			TableCellListener tcl = (TableCellListener)e.getSource();
			//            MonitorAndDebug.printConsole("Row   : " + tcl.getRow());
			//            MonitorAndDebug.printConsole("Column: " + tcl.getColumn());
			//            MonitorAndDebug.printConsole("Old   : " + tcl.getOldValue());
			//            MonitorAndDebug.printConsole("New   : " + tcl.getNewValue());
		}

	}

	//TODO parents
	/**
	 * Panel with table of attributes ({@link TagData} and parent field for given object type
	 * @author Kunis
	 *
	 */
	private class ObjectConfTable extends JPanel implements ActionListener{
		private JTable t;
		private String type;
		private JCheckBox[] items;
		private ModuleContent content;
		private JButton editBtn;
		private JButton delBtn;
		private boolean editable;
		private boolean removeable;
		private JTextField txtParents;
		
		public ObjectConfTable(ModuleContent val,String type,String micName) {
			super(new BorderLayout());

			editable=false;
			removeable=false;
			if(micName.equals(MDEConfiguration.UNIVERSAL) ) {
				editable=true;
//				removeable=false;
			}
			if(type!=null && !type.contains("OME")){
				editable=true;
				removeable=true;
			}
			
			
			this.t=createTable(val,type,false);
			this.type=type;
			this.content=val;
			JPanel thisTable = new JPanel(new BorderLayout());
			thisTable.add(t.getTableHeader(),BorderLayout.PAGE_START);
			thisTable.add(t,BorderLayout.CENTER);
			JScrollPane scrollPane = new JScrollPane(thisTable);
			
			
			editBtn=new JButton("Edit");
			editBtn.addActionListener(this);
			editBtn.setEnabled(editable);
			
			delBtn=new JButton("Remove");
			delBtn.addActionListener(this);
			delBtn.setEnabled(removeable);
			
			JPanel keyPanel=new JPanel(new FlowLayout());
			keyPanel.add(new JLabel(type));
			keyPanel.add(editBtn);
			keyPanel.add(delBtn);
			
			add(keyPanel,BorderLayout.NORTH);
			add(scrollPane,BorderLayout.CENTER);

			txtParents=new JTextField();
			JLabel lblParents=new JLabel("Parents");

			String[] parents=null;
			if(val!=null)
				parents=val.getParents();
			if(parents!=null)
				txtParents.setText(String.join(",", parents));
			txtParents.setEditable(false);
			JPanel subpaneParent=new JPanel(new GridLayout(1, 2));
			subpaneParent.add(lblParents);
			subpaneParent.add(txtParents);

			add(subpaneParent,BorderLayout.SOUTH);
		}

		private String[] getObjectParents() {
			String[] result=txtParents.getText()!=null? txtParents.getText().split(","):null;
			return result;
		}
//			if(items==null)
//				return null;
//			List<String> result=new ArrayList<>();
//			for(JCheckBox s:items) {
//				if(s.isSelected()) {
//					result.add(s.getText());
//				}
//			}
//			if(result.isEmpty())
//				return null;
//			return result.toArray(new String[result.size()]);
//		}

		public String getObjectType() {
			return type;
		}

		public ModuleContent getModuleContent() {
			ModuleContent c= new ModuleContent(getContent(), getObjectType(), getObjectParents());
			return c;
		}
		
		private LinkedHashMap<String,TagData> getContent(){
			LinkedHashMap<String,TagData> result = new LinkedHashMap<>();
			for(int i=0; i<t.getModel().getRowCount(); i++) {
				TagData tagD=((TagDataTableModel) t.getModel()).getRowData(i);
				result.put(tagD.getTagName(),tagD);
			}
			return result;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == editBtn){
				editContent(content);
			}else if(e.getSource() ==delBtn) {
				removeContent(content);
			}
		}

	}

	
	private class NewObjectConfDialog extends JDialog implements ActionListener{
		private JTable t;
		private JTextField typeTxt;
		private JButton  buttonOK;
		private JButton  buttonCancel;
		private ModuleContent content;
		private JCheckBox[] items;
		private JComboCheckBox chcb;
		
		public NewObjectConfDialog(JFrame parent,ModuleContent val,String type) {
			super(parent,"Define Object Attributes");
			setBounds(100,100,500,600);
			getContentPane().setLayout(new BorderLayout(5,5));
			setModal(true);
			
			JPanel mainPanel=new JPanel(new BorderLayout());
			
			this.t=createTable(val,type,true);

			JPanel thisTable = new JPanel(new BorderLayout());
			thisTable.add(t.getTableHeader(),BorderLayout.PAGE_START);
			thisTable.add(t,BorderLayout.CENTER);
			JScrollPane scrollPane = new JScrollPane(thisTable);

			JButton addBtn = new JButton("+");
			addBtn.addActionListener(new AddAction(t, type));
			JButton delBtn = new JButton("-");
			delBtn.addActionListener(new DelAction(t, type));

			JPanel keyPanel=new JPanel(new FlowLayout());
			
			typeTxt=new JTextField(50);
			typeTxt.setText(type);
			if(!type.equals(""))
				typeTxt.setEditable(false);
			keyPanel.add(typeTxt);
			keyPanel.add(addBtn);
			keyPanel.add(delBtn);
			
			
			JTextField txtParents=new JTextField();
			JLabel lblParents=new JLabel("Parents");

			String[] parents=null;
			if(val!=null)
				parents=val.getParents();
//			CheckableItem[] m = createCheckableItemList(parents);
			items=createCheckBoxItems(parents);
			
			JPanel subpaneParent=new JPanel(new GridLayout(1, 2));
			subpaneParent.add(lblParents);
			
			chcb=new JComboCheckBox(items);
			subpaneParent.add(chcb);
//			subpaneParent.add(new CheckedComboBox<>(new DefaultComboBoxModel<>(m)));

			JPanel subPane=new JPanel(new BorderLayout());
			subPane.add(subpaneParent,BorderLayout.WEST);
			subPane.add(subpaneParent,BorderLayout.CENTER);

			mainPanel.add(keyPanel,BorderLayout.NORTH);
			mainPanel.add(scrollPane,BorderLayout.CENTER);
			mainPanel.add(subPane,BorderLayout.SOUTH);
			
			buttonOK = new JButton("OK");
			buttonOK.addActionListener(this);
			buttonCancel = new JButton("Cancel");
			buttonCancel.addActionListener(this);
			Box btnPane=Box.createHorizontalBox();
			btnPane.add(buttonCancel);
			btnPane.add(Box.createHorizontalStrut(5));
			btnPane.add(buttonOK);

			getContentPane().add(mainPanel, BorderLayout.CENTER);
			  getContentPane().add(btnPane,BorderLayout.SOUTH);
			pack();
			setVisible(true);
		}

//		private String getObjectType() {
//			return type;
//		}

		private LinkedHashMap<String,TagData> getContent(){
			LinkedHashMap<String,TagData> result = new LinkedHashMap<>();
			for(int i=0; i<t.getModel().getRowCount(); i++) {
				TagData tagD=((TagDataTableModel) t.getModel()).getRowData(i);
				result.put(tagD.getTagName(),tagD);
			}
			return result;
		}
		private String[] getObjectParents() {
			List<String> result=new ArrayList<>();
			for(JCheckBox s:items) {
				if(s.isSelected()) {
					result.add(s.getText());
				}
			}
			if(result.isEmpty())
				return null;
			return result.toArray(new String[result.size()]);
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == buttonOK){
				String[] p=getObjectParents();
				if(typeTxt.getText().equals("") || p==null) {
					if(typeTxt.getText().equals(""))
						typeTxt.setBorder(new LineBorder(Color.red,1));
					if(p==null)
						chcb.setBorder(new LineBorder(Color.red,1));
				}else {
					content=new ModuleContent(getContent(), typeTxt.getText(),getObjectParents() );
					setVisible(false);
					dispose();
				}
			}else if(e.getSource()==buttonCancel) {
				content=null;
				setVisible(false);
				dispose();
			}
		}
		
		public ModuleContent getModuleContent() {
			return content;
		}
	}

	
	//----------------------------------------------------------------------------------
	//see https://stackoverflow.com/questions/1573159/java-check-boxes-in-a-jcombobox
	public class JComboCheckBox extends JComboBox {
		  public JComboCheckBox() { addStuff(); }
		  public JComboCheckBox(JCheckBox[] items) { super(items); addStuff(); }
		  public JComboCheckBox(Vector items) { super(items); addStuff(); }
		  public JComboCheckBox(ComboBoxModel aModel) { super(aModel); addStuff(); }
		  private void addStuff() {
		    setRenderer(new ComboBoxRenderer());
		    addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ae) { itemSelected(); }
		    });
		  }
		  private void itemSelected() {
		    if (getSelectedItem() instanceof JCheckBox) {
		      JCheckBox jcb = (JCheckBox)getSelectedItem();
		      jcb.setSelected(!jcb.isSelected());
		    }
		  }
		  class ComboBoxRenderer implements ListCellRenderer {
		    private JLabel defaultLabel=new JLabel("Parents");
		    public ComboBoxRenderer() { setOpaque(true); }
		    public Component getListCellRendererComponent(JList list, Object value, int index,
		                boolean isSelected, boolean cellHasFocus) {
		      if (value instanceof Component) {
		        Component c = (Component)value;
		        if (isSelected) {
		          c.setBackground(list.getSelectionBackground());
		          c.setForeground(list.getSelectionForeground());
		        } else {
		          c.setBackground(list.getBackground());
		          c.setForeground(list.getForeground());
		        }
		        return c;
		      } else {
//		        if (defaultLabel==null) defaultLabel = new JLabel(value.toString());
//		        else defaultLabel.setText(value.toString());
		        return defaultLabel;
		      }
		    }
		  }
		}
	//----------------------------------------------------------------------------------

	//see https://java-swing-tips.blogspot.com/2016/07/select-multiple-jcheckbox-in-jcombobox.html
	class CheckableItem {
		public final String text;
		private boolean selected;

		protected CheckableItem(String text, boolean selected) {
			this.text = text;
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		@Override public String toString() {
			return text;
		}
	}

	class CheckBoxCellRenderer<E extends CheckableItem> implements ListCellRenderer<E> {
		private final JLabel label = new JLabel(" ");
		private final JCheckBox check = new JCheckBox(" ");

		@Override public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
			if (index < 0) {
				String txt = getCheckedItemString(list.getModel());
				label.setText(txt.isEmpty() ? " " : txt);
				return label;
			} else {
				check.setText(Objects.toString(value, ""));
				check.setSelected(value.isSelected());
				if (isSelected) {
					check.setBackground(list.getSelectionBackground());
					check.setForeground(list.getSelectionForeground());
				} else {
					check.setBackground(list.getBackground());
					check.setForeground(list.getForeground());
				}
				return check;
			}
		}

		private <E extends CheckableItem> String getCheckedItemString(ListModel<E> model) {
//			return IntStream.range(0, model.getSize())
//					.mapToObj(model::getElementAt)
//					.filter(CheckableItem::isSelected)
//					.map(Objects::toString)
//					.sorted()
//					.collect(Collectors.joining(", "));
			 List<String> sl = new ArrayList<>();
			 for (int i = 0; i < model.getSize(); i++) {
			   CheckableItem v = model.getElementAt(i);
			   if (v.isSelected()) {
			     sl.add(v.toString());
			   }
			 }
			 if (sl.isEmpty()) {
			   return " "; // When returning the empty string, the height of JComboBox may become 0 in some cases.
			 } else {
			   return sl.stream().sorted().collect(Collectors.joining(", "));
			 }
		}
	}

	class CheckedComboBox<E extends CheckableItem> extends JComboBox<E> {
		private boolean keepOpen;
		private transient ActionListener listener;

		protected CheckedComboBox() {
			super();
			initActionListener();
		}

		protected CheckedComboBox(ComboBoxModel<E> model) {
			super(model);
			initActionListener();
		}
		private void initActionListener() {
			listener = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if ((e.getModifiers() & AWTEvent.MOUSE_EVENT_MASK) != 0) {
						MonitorAndDebug.printConsole("CheckBoxComboBox action performed");
						updateItem(getSelectedIndex());
						keepOpen = true;
					}
				}
			};
		}
		// protected CheckedComboBox(E[] m) {
		//   super(m);
		// }

		@Override public Dimension getPreferredSize() {
			return new Dimension(200, 20);
		}

		@Override public void updateUI() {
			MonitorAndDebug.printConsole("CheckBoxComboBox update UI");
			setRenderer(null);
			removeActionListener(listener);
			super.updateUI();
//			listener = e -> {
//				if ((e.getModifiers() & AWTEvent.MOUSE_EVENT_MASK) != 0) {
//					updateItem(getSelectedIndex());
//					keepOpen = true;
//				}
//			};
			setRenderer(new CheckBoxCellRenderer<>());
			addActionListener(listener);
			getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "checkbox-select");
			getActionMap().put("checkbox-select", new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					Accessible a = getAccessibleContext().getAccessibleChild(0);
					if (a instanceof ComboPopup) {
						updateItem(((ComboPopup) a).getList().getSelectedIndex());
					}
				}
			});
		}

		protected void updateItem(int index) {
			if (isPopupVisible()) {
				E item = getItemAt(index);
				item.setSelected(!item.isSelected());
				// item.selected ^= true;
				// ComboBoxModel m = getModel();
				// if (m instanceof CheckableComboBoxModel) {
				//   ((CheckableComboBoxModel) m).fireContentsChanged(index);
				// }
				// removeItemAt(index);
				// insertItemAt(item, index);
				setSelectedIndex(-1);
				setSelectedItem(item);
			}
		}

		@Override public void setPopupVisible(boolean v) {
			MonitorAndDebug.printConsole("CheckBoxComboBox keep open: "+keepOpen);
			if (keepOpen) {
				keepOpen = false;
			} else {
				super.setPopupVisible(v);
			}
		}

	}

	



}

