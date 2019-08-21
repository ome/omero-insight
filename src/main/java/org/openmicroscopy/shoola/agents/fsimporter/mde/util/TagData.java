package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.accessibility.Accessible;

//import javafx.util.StringConverter;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;

import loci.common.DateTools;
import ome.model.enums.UnitsLength;
import ome.model.units.Length;
import ome.model.units.UNITS;
import ome.model.units.Unit;
//import ome.units.UNITS;
//import ome.units.unit.Unit;
//import ome.units.quantity.*;
import ome.xml.model.Experimenter;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter.OMEValueConverter;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;


/**
 * TODO: file browser type -> select attachment files
 * @author Kunis
 *
 */
public class TagData 
{
	/** Logger for this class. */
//	private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(TagData.class);

	Color fillInfo=new Color(240,240,240);//Color.LIGHT_GRAY;
//	Color noInfo=new Color(217,229,220);//green;
	Color noInfo=Color.white;
	Color resetInfo=Color.white;

	private final Border normalBorder = UIManager.getBorder("TextField.border");
	private final Border errorBorder = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51));

	public static final String[] DATE_FORMATS_TAGS = {
	    "yyyy:MM:dd HH:mm:ss",
	    "dd/MM/yyyy HH:mm:ss",
	    "MM/dd/yyyy hh:mm:ss aa",
	    "yyyyMMdd HH:mm:ss",
	    
	    "yyyy/MM/dd",
	    "yyyy/MM/dd HH:mm:ss",
	    
	    "yyyy-MM-dd HH:mm:ss",
	    "yyyy-MM-dd HH:mm:ss:SSS",
	    "yyyy-MM-dd'T'HH:mm:ssZ",
	    "yyyy-MM-dd",
	    
	    "dd.MM.yyyy",
	    "dd.MM.yyyy HH:mm:ss",
	    "dd.MM.yyyy HH:mm:ss:SSS",
	    
	    "dd-MM-yyyy HH:mm:ss",
	    "dd-MM-yyyy"
	   
	  };
	
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
	    put("^\\d{8}$", "yyyyMMdd");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
	    put("^\\d{1,2}.\\d{1,2}.\\d{4}$", "dd.MM.yyyy");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
	    put("^\\d{12}$", "yyyyMMddHHmm");
	    put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
	    put("^\\d{14}$", "yyyyMMddHHmmss");
	    put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}:\\d{3}$", "yyyy-MM-dd HH:mm:ss:SSS");
	}};
	
	private final String datePattern = DateTools.TIMESTAMP_FORMAT;

	//status of inputfield
	public static final int INACTIVE=0;
	public static final int EMPTY =1;
	public static final int SET=2;
	public static final int OVERWRITE=3;

	// kind of inputfields
	public static final String TEXTFIELD="TextField";
	public static final String COMBOBOX="ComboBox";
	public static final String CHECK_COMBOBOX="CheckBoxComboBox";
	public static final String TEXTPANE="TextPane"; //unused?
	public static final String CHECKBOX="CheckBox"; //unused?
	public static final String ARRAYFIELDS="ArrayField";
	public static final String TIMESTAMP="TimeStamp";
	public static final String LIST="List";
	public static final String TEXTAREA="TextArea";

	private String tagInfo;
	
	private int status=INACTIVE;
	private boolean prop;
	private String type;
	private boolean visible;
	// flag for value has changed of user
	private boolean valChanged;
	//flag for data was saved after changing or not
	private boolean valSaved;
	
	// parent: object name that owned the tag
	private String parent;
	// tag name
	private String name;
	// tag value (text: value[0], value.length>1 ->
	private String[] value;
	// default values, e.g. for comboboxes
	private String[] defaultValue;
	// tag unit
	private ome.model.units.Unit[] unit;
	// class of unit //TODO: ableiten aus unit
	private Class unitClass;
	
	
	private ActionListener fieldActionListener;
	private DocumentListener fieldDocumentListener;

	private boolean actionListenerActiv;

	private Color backgroundColor;

	private Border fieldBorder;
	
	private JComponent inputField;

	

	//copy constructor
	public TagData(TagData orig)
	{
		unit=orig.unit;//TODO clone array
		if(orig.value!=null) value=orig.value.clone();
		status=orig.status;
		prop=orig.prop;
		type=orig.type;
		visible=orig.visible;
		valChanged=orig.valChanged;
		valSaved=orig.valSaved;
		name=orig.name;
		parent=orig.parent;
		tagInfo=orig.tagInfo;
		fieldActionListener=orig.fieldActionListener;
		actionListenerActiv=orig.actionListenerActiv;
		fieldDocumentListener=orig.fieldDocumentListener;
		backgroundColor=orig.backgroundColor;
		fieldBorder=orig.fieldBorder;
		inputField=null;
		if(orig.defaultValue!=null) defaultValue=orig.defaultValue.clone();
	}
	
	/**
	 * Constructor for TagData element for array fields
	 * @param name label for tagdata element
	 * @param val array of values
	 * @param prop property
	 * @param type type==ARRAYFILEDS
	 */
	public TagData(String parent, String name, String[] val, boolean prop,String type) 
	{
		initTagData(parent,name,val,null,null,prop,type,null);
	}
	

	public TagData(String parent, String name, String val, boolean prop,String type) 
	{
		String[] value=new String[] {val};
		initTagData(parent,name,value,null,null,prop,type,null);
	}
	
	public TagData(String parent, String name, ome.model.units.Unit unitObj, Class unitClass,boolean prop,String type) 
	{
		String[] valArr=new String[1];
		valArr[0]="";
		if(unitObj!=null) {
			valArr[0]=String.valueOf(unitObj.getValue());
		}
		initTagData(parent,name, valArr, new ome.model.units.Unit[] {unitObj},unitClass,prop, type, null);
	}
	public TagData(String parent, String name, ome.model.units.Unit[] unitObj, Class unitClass,boolean prop,String type) 
	{
		String[] valArr=null;
		if(unitObj!=null) {
			valArr=new String[unitObj.length];
			for(int i=0; i<unitObj.length;i++) {
				valArr[i]=null;
				if(unitObj[i]!=null)
					valArr[i]=String.valueOf(unitObj[i].getValue());

			}
			
		}
		initTagData(parent,name, valArr, unitObj,unitClass,prop, type, null);
	}

	public TagData(String parent, String name, String val, boolean prop,String type, String[] defaultVal) 
	{
		String[] valArr=new String[1];
		valArr[0]=val;
		initTagData(parent,name, valArr, null,null, prop, type, defaultVal);
	}
	
	public TagData(String parent, String name, String[] val, boolean prop, String type, int size) {
		String[] def= new String[1];
		def[0]=String.valueOf(size);
		initTagData(parent,name,val,null,null,prop,type,def);
	}
	public TagData(String parent, String name, String val, boolean prop, String type, int size) {
		String[] def= new String[1];
		def[0]=String.valueOf(size);
		String[] valArr=new String[1];
		valArr[0]=val;
		initTagData(parent,name,valArr,null,null,prop,type,def);
	}

	public TagData(String parent, String name, Unit[] unitObj,Class unitClass , boolean prop,
			String type, int size) {
		String[] def= new String[1];
		def[0]=String.valueOf(size);
		String[] valArr=null;
		if(unitObj!=null) {
			valArr=new String[unitObj.length];
			for(int i=0; i<unitObj.length;i++) {
				valArr[i]=null;
				if(unitObj[i]!=null)
					valArr[i]=String.valueOf(unitObj[i].getValue());
			}
		}
		initTagData(parent,name, valArr, unitObj,unitClass,prop, type, def);
		
	}

	private void initTagData(String parent, String name, String[] val, ome.model.units.Unit[] unit,Class unitClass,boolean prop,String type, String[] defaultVal)
	{
		if(val==null)
			val=new String[1];
		initListener();
		
		this.type=type;
		this.name=name;
		this.value=val;
		
		this.unit=unit;
		this.unitClass=unitClass;
		
		
		
		this.defaultValue=defaultVal;
		this.parent=parent;
		valSaved=true;
		valChanged=false;
		tagInfo="";
		setTagProp(prop);
		visible=true;
		status=EMPTY;
		
		if(unit==null) {
			getDefaultUnit();
		}
		
		fieldBorder=normalBorder;
		backgroundColor=noInfo;
		actionListenerActiv=true;
	}
	
//	/**
//	 * Constructor for TagData element for list fields
//	 * @param name
//	 * @param model list model
//	 * @param prop
//	 * @param type
//	 */
//	public TagData(String name, List<Experimenter> expList, boolean prop, int type)
//	{
//		initListener();
//		this.type=type;
//		this.name=name;
//		valSaved=true;
//		label = new JLabel(name+":");
//		tagInfo="";
//		
//		switch (type) {
//		case LIST:
//			initListField(expList);
//			break;
//		default:
//			initTextField();
//			break;
//		}
//
//		label.setLabelFor(inputField);
//		setTagProp(prop);
//		visible=false;
//		actionListenerActiv=true;
//	}

	private void getDefaultUnit() {
		String unitSymbol=ModuleController.getInstance().getStandardUnitSymbolByName(this.parent,getTagName());
		setTagUnit(unitSymbol);
	}

	/**
	 * Compare this tagdata to specified tagdata.
	 * @param t The tagdata to compares this {@code TagData} against.
	 * @return {@code true} if t!=null and elements name, unit and value are equal or both null or empty strings.
	 */
	public boolean equals(TagData t) {
		if(t!=null  && t.tagToString().equals(tagToString())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Compare this tagdata to specified tagdata.
	 * @param t The tagdata to compare this {@code TagData} against.
	 * @return {@code true} if t!=null and elements name, unit and value are equal and not null or empty (except unit)
	 */
	public boolean equalContent(TagData t) {
		if(t!=null && t.tagToString().equals(tagToString())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get tagdata as string. 
	 * @return {@code "name: value unit}.
	 */
	public String tagToString() {
		String result=name+": "+getTagWholeValue();
		return result;
	}
	
	

	
	public JPanel getTagLabelAndUnit() {
		JPanel labelPane=new JPanel(new BorderLayout());
		// define size of label panel
		labelPane.setPreferredSize(new Dimension(200,20));
		JTextField labelName=new JTextField(""+name+": ");
		labelName.setEditable(false);
		
		if(getTagUnitString().equals("")) {
			labelPane.add(labelName,BorderLayout.CENTER);
		}else {
			JComboBox units=createUnitCombo();
			if(units!=null) {
				units.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setTagUnit((String)((JComboBox<String>) e.getSource()).getSelectedItem());
						repaintInputField();
					}
				});
				labelPane.add(labelName,BorderLayout.CENTER);
				labelPane.add(units,BorderLayout.EAST);
			}
		}
		
		if(status==INACTIVE) {
			labelName.setEnabled(false);
		}

		return labelPane;
	}
	
	private JComboBox createUnitCombo() {
		if(getUnitType()!=null) {
//			System.out.println("--Create Unit Combo for "+getUnitType());
			String[] unitsList=TagNames.getUnitsList(getUnitType());

			if(unitsList!=null) {
				JComboBox units = new JComboBox<>(unitsList);

				// set unit
				String symbol=getTagUnitString();
				if(symbol.isEmpty() || symbol.equals("")) {
					symbol=UnitsLength.REFERENCEFRAME.getSymbol();
				}
				for(int i=0; i< units.getItemCount(); i++) {
					
					if(units.getItemAt(i).equals(symbol)) {
						units.setSelectedIndex(i);
						break;
					}
				}
				return units;
			}
		}
		return null;
	}
	
	public JTextField getTagLabel()
	{
		JTextField label;
		if(unit==null) {
			label = new JTextField(name+":");
		}else {
//			String unitSymbol=unit.getUnit().getSymbol().equals(UnitsLength.REFERENCEFRAME)? "rf" : unit.getUnit().getSymbol();
			label = new JTextField(name+" ["+getTagUnitString()+"]:");
		}
		
		if(status==INACTIVE) {
			label.setEnabled(false);
		}
		label.setEditable(false);
		EmptyBorder emptyBorder = (EmptyBorder) BorderFactory.createEmptyBorder(0,10,0,0);
		Border lineBorder=label.getBorder();
		label.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
//		label.setPreferredSize(new Dimension(150,20));
		return label;
	}
	
	private void repaintInputField() {
		switch (type) {
		case ARRAYFIELDS:
			setValArrayField((JArray) inputField);
			break;
		case TEXTFIELD:
			setValTextField(inputField);
			break;
		case COMBOBOX:
			setValComboBox(inputField);
			break;
		case CHECK_COMBOBOX:
			setValCheckComboBox(inputField);
			break;
		case TEXTPANE:
			setValTextPane(inputField);
			break;
		case TEXTAREA:
			setValTextArea(inputField);
			break;
		case CHECKBOX:
			setValCheckbox(inputField);
			break;
		case TIMESTAMP:
			setValTimestamp(inputField);
			break;
		default:
			break;
		}
		inputField.revalidate();
		inputField.repaint();
	}
	
	public JComponent getInputField()
	{
//		MonitorAndDebug.printConsole("-- Create tagField: "+name+","+listToString(value)+","+unit+","+type+","+status+","+visible);
		
		// save tagdata if focus of inputfield lost
		FocusListener listener=new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					saveTagValue((JComponent) e.getSource());
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		};
		
		KeyListener listenerKey=new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
//				System.out.println("-- KEY TYPED "+e.getSource().getClass().getName());
			}
			@Override
			public void keyReleased(KeyEvent e) {
//				System.out.println("-- KEY RELEASED "+e.getSource().getClass().getName());
				try {
					saveTagValue((JComponent) e.getSource());
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				valChanged=true;	
				valSaved=false;
			}
			@Override
			public void keyPressed(KeyEvent e) {
//				System.out.println("-- KEY PRESSED "+e.getSource().getClass().getName());
				// TODO Auto-generated method stub
			}
		};
		
		switch (type) {
		case ARRAYFIELDS:
			int defaultSize = (defaultValue!=null && defaultValue[0]!=null && !defaultValue[0].equals(""))? Integer.parseInt(defaultValue[0]):1;
			int size=value!=null ? value.length : defaultSize;
			inputField=new JArray(defaultSize);
			setValArrayField((JArray) inputField);
			inputField.setToolTipText(tagInfo);
//			inputField.setPreferredSize(new Dimension(250,20));
			break;
		case TEXTFIELD:
			inputField=new JTextField(10);
			setValTextField(inputField);
			((JTextField) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
			inputField.setToolTipText(tagInfo);
//			inputField.setPreferredSize(new Dimension(250,20));
			break;
		case COMBOBOX:
			inputField=initComboBox();
			setValComboBox(inputField);
			((JComboBox) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
			inputField.setToolTipText(tagInfo);
//			inputField.setPreferredSize(new Dimension(250,20));
			break;
		case CHECK_COMBOBOX:
			inputField =initCheckComboBox();
			setValCheckComboBox(inputField);
			((CheckBoxCombo) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
			inputField.setToolTipText(tagInfo);
			break;
		case TEXTPANE:
			inputField=new JTextPane();
			setValTextPane(inputField);
//			((JTextPane) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
			inputField.setToolTipText(tagInfo);
//			inputField.setPreferredSize(new Dimension(250,40));
			break;
		case TEXTAREA:
			inputField=new ScrollableTextPane();
			((ScrollableTextPane) inputField).setTextAreaRows(2);
			setValTextArea(inputField);
//			((JTextArea) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
			inputField.setToolTipText(tagInfo);
//			inputField.setPreferredSize(new Dimension(250,40));
			break;
		case CHECKBOX:
			inputField=new JCheckBox("",Boolean.parseBoolean(value[0]));
			((JCheckBox) inputField).addActionListener(fieldActionListener);
			setValCheckbox(inputField);
			((JCheckBox) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
			inputField.setToolTipText(tagInfo);
//			inputField.setPreferredSize(new Dimension(250,20));
			break;
		case TIMESTAMP:
			inputField= new JTextField(10);
			inputField.setToolTipText("Format e.g: "+datePattern+" or dd.MM.yyyy");
			setValTimestamp(inputField);
			((JTextField) inputField).addActionListener(fieldActionListener);
			inputField.addFocusListener(listener);
			inputField.addKeyListener(listenerKey);
//			inputField.setPreferredSize(new Dimension(250,20));
			break;
		default:
			break;
		}
		setTagValue(value);
		
		addDocumentListener(inputField);
		
		inputField.setBackground(backgroundColor);
		inputField.setBorder(fieldBorder);
		if(status==INACTIVE) {
			inputField.setEnabled(false);
		}
		
		// disable edit of ID field
		if(name.equals(TagNames.ID))
			inputField.setEnabled(false);
		
		return inputField;
	}

	private JComponent initListField(List<Experimenter> expList)
	{
		ExperimenterBox field = new ExperimenterBox(expList);
		return field;
	}

	private JComponent initCheckComboBox()
	{
		CheckBoxCombo field;
		if(defaultValue!=null) {
			field = new CheckBoxCombo(defaultValue);
		}else {
			field= new CheckBoxCombo();
		}
		return field;
	}

	private JComponent initComboBox()
	{
		JComboBox field;
		if(defaultValue!=null){
			field = new JComboBox<String>(defaultValue);
			((JComboBox<String>) field).insertItemAt("",0);
			((JComboBox<String>) field).setSelectedItem(0);
		}else{
			field = new JComboBox<String>();
		}
		return field;
	}

	private void initListener()
	{
		fieldActionListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (actionListenerActiv){
					System.out.println("-- ACTION PERFORMED "+e.getSource().getClass().getName());
					valChanged=true;
//					saveTagValue((JComponent) e.getSource());
					valSaved=false;

					if(fieldBorder.equals(errorBorder)){
						setTagInfoError("");
					}
				}
			}
		};
		
		


	}
	
	public void setActionListener(ActionListener a) {
		fieldActionListener=a;
	}
	private void activateActionListener(boolean a)
	{
		actionListenerActiv=a;
	}
	
			
	/** Action Listener for tagData*/
	private void addActionListener(JComponent field)
	{
		if(fieldActionListener==null)
			return;
		if(type==COMBOBOX)
			((JComboBox)field).addActionListener(fieldActionListener); 
		
//		switch (type) {
//		case TEXTFIELD:
//			((JTextField)inputField).addActionListener(l);
//			break;
//		case COMBOBOX:
//			((JComboBox)inputField).addActionListener(l); 
//			break;
//		case TEXTPANE:
//			break;
//		case TEXTAREA:
//			break;
//		case CHECKBOX:
//			((JCheckBox)inputField).addActionListener(l);
//			break;
//		case ARRAYFIELDS:
//
//			break;
//		case TIMESTAMP:
//			break;
//		default:
//			((JTextField)inputField).addActionListener(l);
//			break;
//		}
	}
	
	public void setDocumentListener(DocumentListener l)
	{
		fieldDocumentListener=l;
	}
	
	private void addDocumentListener(JComponent field) {
		if(fieldDocumentListener==null)
			return;
		
		switch (type) {
		case TEXTFIELD:
			((JTextField)field).getDocument().addDocumentListener(fieldDocumentListener);
			break;
		case COMBOBOX:
			break;
		case TEXTPANE:
			((JTextPane)field).getDocument().addDocumentListener(fieldDocumentListener);
			break;
		case TEXTAREA:
			((JTextArea)field).getDocument().addDocumentListener(fieldDocumentListener);
			break;
		case CHECKBOX:
			break;
		case ARRAYFIELDS:
			break;
		case TIMESTAMP:
			break;
		default:
//			((JTextField)field).getDocument().addDocumentListener(fieldDocumentListener);
			break;
		}
	}

	public String getDefaultValuesAsString() {
		return arrayToString(defaultValue);
	}
	public String getDefaultValuesAsArrayString() {
		return Arrays.toString(defaultValue);
	}
	
	public void setDefaultValues(String[] list)
	{
		if(list==null)
			return;
		defaultValue=list;
	}

	/**
	 * Get tagdata value and unit as string.
	 * @return {@code "value"} for textfield without unit; {@code "value unit"} for textfield with unit; {@code "value1,value2 unit"} for arrayfield with unit;
	 */
	public String getTagWholeValue() {
		if(unit==null)
			return getTagValue();
		else {
			return getTagValue()+" "+getTagUnitString();
		}
	}
	
	/**
	 * Get name of this tag.
	 * @return tag name.
	 */
	public String getTagName(){
		return this.name;
	}
	
	/**
	 * Check if tag value is null or "".
	 * @return {@code true} if value is null or "", else {@code false}. 
	 */
	public boolean isEmptyValue() {
		if(value==null || getTagValue().equals("")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get value array.
	 * @return
	 */
	public String[] getTagValueAsArray()
	{
		return value;
	}
	
	/**
	 * Get tag value as string, separated by comma.
	 * @return tag values as string: {@code value1,value2,value3,...}
	 */
	public String getTagValue()
	{
		return arrayToString(value);
	}
	
	/**
	 * Get value at given index. If tag value is null or index is bigger then value.size return "".
	 * @param index
	 * @return
	 */
	public String getTagValue(int index) 
	{
		if(index >= value.length) {
			return "";
		}else {
			return value[index];
		}
	}
	
	
	public void saveTagValue(String[] newValue) {
		if(newValue!=null && value!=null) {
			if(!arrayToString(value).equals(arrayToString(newValue)) ) {
				dataHasChanged(true);
				value=newValue;
			}
		}
	}
	
	private void saveTagValue(JComponent source) throws Exception
	{
		if(source==null)
			return;
		
		String[] val=null;
			switch (type) {
			case ARRAYFIELDS:
				System.out.println("ARRAYFIELD: \n"+source);
				if(source instanceof JArray) {
					try {
						val=((JArray) source).getValuesAsArray();
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}else {
					val=new String[1];
					val[0]=((JTextField) source).getText();
				}
				break;
			case TIMESTAMP:
				val=new String[1];
				val[0] = readTimestamp(source,val[0]);
				break;
			case TEXTPANE:
				val=new String[1];
				val[0]=((JTextPane) source).getText();
				break;
			case TEXTAREA:
				val=new String[1];
				val[0]=((JTextArea)source).getText();
				break;
			case TEXTFIELD:
				val=new String[1];
				val[0]=((JTextField) source).getText();
				break;
			case COMBOBOX:
				val=new String[1];
				val[0]=(String) ((JComboBox<String>) source).getSelectedItem();
				break;
			case CHECKBOX:
				val=new String[1];
				val[0]= ((JCheckBox)source).isSelected()? "true" : "false";
				break;
			case CHECK_COMBOBOX:
				val = ((CheckBoxCombo) source).getSelectedVal();
				break;
			default:
				break;
			}
//			MonitorAndDebug.printConsole("-- CHECK tagdata "+name+" = ["+listToString(val)+"] \t old: ["+listToString(value)+"]");
			if(val!=null && value!=null) {
				if(!arrayToString(value).equals(arrayToString(val)) ) {
					
					dataHasChanged(true);
					value=val;
//					MonitorAndDebug.printConsole("-- SAVE tagdata "+name+" = "+listToString(value));
				}
			}
	}
	
//	public List<Experimenter> getListValues()
//	{
//		if(type == LIST){
//			
//			List<Experimenter> list = ((ExperimenterBox) inputField).getExperimenterList();
//			
//			return list;
//		}
//		
//		return null;
//	}

	
	
	private String listToString(List<String> list)
	{
		String res="";
		
		if(list==null || list.isEmpty())
			return res;
		
		for(String s:list){
			if(!s.equals(""))
				res+=s+",";
		}
		if(res.endsWith(","))
			res=res.substring(0, res.length()-1);
		
		return res;
	}
	
	private String arrayToString(String[] list)
	{
		String res=null;
		
		if(list==null)
			return res;
		
		res="";
		for(String s:list){
			if(s!=null && !s.equals(""))
				res+=s+",";
		}
		if(res.endsWith(","))
			res=res.substring(0, res.length()-1);
		
		return res;
	}
	

	
	public String getTagUnitString()
	{
		if(unit!=null && unit[0]!=null)
			return unit[0].getUnit().getSymbol();
		
		return ModuleController.getInstance().getStandardUnitSymbolByName(this.parent,getTagName());
	}
	
	private Unit[] getTagUnit()
	{
		return unit;
	}

	private void setTagData(ome.model.units.Unit[] u) {
		if(u==null || u.length==0)
			return;
		for(int i=0; i<u.length; i++) {
			value[i]=u[i]!=null? String.valueOf(u[i].getValue()):"";
		}
		unit=u;
	}
	public void setTagUnit(ome.model.units.Unit[] u)
	{
		unit=u;
		if(unit!=null && unit.length>0 && unit[0]!=null) unitClass=unit[0].getClass();
	}
	
	public String getUnitType()
	{
		if(unitClass==null) {
			if(getTagUnitString()!=null && !getTagUnitString().equals("")) {
				return TagNames.getUnit(getTagUnitString()).getClass().getName();
			}
			return "";
		}
		return unitClass.getName();

	}
	
	/**
	 * set unitsymbol. If unitsymbol is differ from current unit -> convert value
	 * @param unitsymbol
	 */
	public void setTagUnit(String unitsymbol) {
		if(unitsymbol==null || unitsymbol.equals("") || getTagValue().trim().equals(""))
			return;
		Unit[] newVal=null;
	
		// conversion necessary?
		if(value!= null &&  unit!=null && unit[0]!=null &&
				unit[0].getUnit()!=null && 
				!unitsymbol.equals(unit[0].getUnit().getSymbol())) {
			System.out.println("--Convert unit of "+name+": "+unit[0].getUnit().getSymbol()+" -> "+unitsymbol);
			newVal=OMEValueConverter.convert(value,unit,unitsymbol);
			if(newVal!=null) {
				setTagData(newVal);
				return;
			}
		}
		
		newVal=new Unit[1];
		newVal[0]=TagNames.getUnit(unitsymbol);
		setTagUnit(newVal);
		
	}
	
//	public void setTagValue(Experimenter val)
//	{
//		activateActionListener(false);
//		if(val==null || val.equals("")){
//			inputField.setBackground(noInfo);
//		}else{
//			inputField.setBackground(fillInfo);
//		}
////		inputField.setBorder(normalBorder);
//		if(type==LIST){
//			((ExperimenterBox) inputField).addElement(val);
//		}
//		activateActionListener(true);
//	}
//	
//	public void setTagValue(List<Experimenter> val)
//	{
//		activateActionListener(false);
//		if(val==null || val.isEmpty()){
//			inputField.setBackground(noInfo);
//		}else{
//			inputField.setBackground(fillInfo);
//		}
////		inputField.setBorder(normalBorder);
//		if(type==LIST){
//			((ExperimenterBox) inputField).addExperimenterList(val);
//		}
//		activateActionListener(true);
//	}
	
	public void setTagValue(Unit unit, Class unitClass, boolean property) {
		setTagValue(new ome.model.units.Unit[] {unit},unitClass,property);
	}
	public void setTagValue(Unit[] unit, Class unitClass, boolean property)
	{
		this.unit=unit;
		this.unitClass=unitClass;
		if(unit!=null) {
			for(int i=0; i<unit.length;i++) {
				setTagValue(String.valueOf(unit[i].getValue()),i);
			}
		}else {
			setTagValue("");
		}
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String val, boolean property)
	{
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String val, int index, boolean property)
	{
		setTagValue(val,index);
		setTagProp(property);
		valChanged=false;
	}
	
	public void setTagValue(ome.model.units.Unit val, int index, boolean property)
	{
		if(val!=null) {
			setTagValue(String.valueOf(val.getValue()), index);
		}else {
			setTagValue("",index);
		}
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String[] val, boolean property)
	{
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
		
	}

	private void setTagValue(String val, int index)
	{
		if(val!=null && !val.equals(""))
			valSaved=false;
		
		switch (type) {
		case ARRAYFIELDS:
			if(value==null)
				value=new String[index+1];
			if(value.length<=index) {
				System.out.println("ERROR add arraystring \t[TagData::setTagValue]");
			}
			value[index]=val;
			break;
		default:
			setTagValue(val);
			break;
		}
		setTagStatus( val.equals("") ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
	}

	/**
	 * Set value of tag.
	 * @param val
	 */
	private void setTagValue(String[] val) 
	{
		value=val;
		setTagStatus( (val.length==0) ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
	}

	public void setTagValue(String val) 
	{
		if(val==null || val.equals("")){
			backgroundColor=noInfo;
		}else{
			valSaved=false;
			backgroundColor=fillInfo;
		}

		value[0]=val;
		setTagStatus( val.equals("") ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
		valChanged=false;
		
	}
	
	private String readTimestamp(JComponent inputField,String val) 
	{
		String creationDate = ((JTextField)inputField).getText();
		try{
			// parse to yyyy-MM-ddT00:00:00
			String date = DateTools.formatDate(creationDate, DATE_FORMATS_TAGS);
			
			//parsing successfull?
			if(creationDate!= null && !creationDate.equals("") && !creationDate.equals(datePattern) && date ==null){
				date = parseDate(creationDate);
				
				
				// show warn dialog
				if(date==null){
					String formats="";
					for(String s: DATE_FORMATS_TAGS){
						formats=formats+s+"\n";
					}
					LOGGER.warn("unknown creation date format: {}", creationDate);
					MonitorAndDebug.printConsole("unknown creation date format: "+ creationDate);
//					WarningDialog ld = new WarningDialog("Unknown Timestamp Format!", 
//							"Can't parse given timestamp ["+name+": "+creationDate+"] ! Please use one of the following date formats:\n"+formats,
//							this.getClass().getSimpleName());
//					ld.setVisible(true);
				}
			}
			
			val=date;//DateTools.formatDate(((JTextField)inputField).getText(), DateTools.TIMESTAMP_FORMAT);
		}catch(Exception e){
			LOGGER.warn("unknown creation date format: {}", creationDate);
			MonitorAndDebug.printConsole("unknown creation date format: "+ creationDate);
//			LOGGER.error("Wrong string input format timestamp: "+name+": "+creationDate);
//			ExceptionDialog ld = new ExceptionDialog("Timestamp Format Error!", 
//					"Wrong timestamp format at input at "+name,e,
//					this.getClass().getSimpleName());
//			ld.setVisible(true);
		}
		return val;
	}
	
	//http://stackoverflow.com/questions/3389348/parse-any-date-in-java
	/**
	 * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
	 * format is unknown. You can simply extend DateUtil with more formats if needed.
	 * @param dateString The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString) {
	    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
	        if (dateString.toLowerCase().matches(regexp)) {
	            return DATE_FORMAT_REGEXPS.get(regexp);
	        }
	    }
	    LOGGER.warn("Can't parse date: "+dateString+". Unknown date format!");
	   System.out.println("Can't parse date: "+dateString+". Unknown date format!");
	    return null; // Unknown format.
	}
	

	private String parseDate(String val) throws Exception
	{
		String dateformat= DateTools.ISO8601_FORMAT_MS;
		String s=DateTools.formatDate(val,dateformat);
		if(s==null){
			dateformat=DateTools.ISO8601_FORMAT;
			s=DateTools.formatDate(val, dateformat);
			
		}
		DateTimeFormatter formatter=DateTimeFormatter.ofPattern(dateformat);
		DateFormat df=new SimpleDateFormat(dateformat);
		
		Date d=null;
		try {
			d=df.parse(s);
			SimpleDateFormat f=new SimpleDateFormat(DateTools.TIMESTAMP_FORMAT);
			return f.format(d);

		} catch (ParseException | NullPointerException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("Parse error date for format "+dateformat+"\n"+e1.toString());
			return null;
		}
	}

	private void setValTimestamp(JComponent inputField) 
	{
//		if(val==null || val.equals("")){
//			((JTextField) inputField).setText(datePattern.toLowerCase());
//			((JTextField) inputField).setForeground(Color.gray);
//		}else{
		String val=value[0];
		if(val!=null && !val.equals("")){
			((JTextField) inputField).setForeground(Color.black);
			String dateformat= DateTools.ISO8601_FORMAT_MS;
			String s=DateTools.formatDate(val,dateformat);
			if(s==null){
				dateformat=DateTools.ISO8601_FORMAT;
				s=DateTools.formatDate(val, dateformat);
				
			}
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern(dateformat);
			DateFormat df=new SimpleDateFormat(dateformat);
			
			Date d=null;
			try {
				d=df.parse(s);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				LOGGER.error("Parse error date for format "+dateformat);
				setTagInfoError(val+". Invalid Date Format!");
				((JTextField) inputField).setText("");
				e1.printStackTrace();
			}
			
			try {
				//			((JTextField) inputField).setText( d.toString());
				SimpleDateFormat f=new SimpleDateFormat(DateTools.TIMESTAMP_FORMAT);
				((JTextField) inputField).setText( f.format(d));
			} catch (Exception e) {
				LOGGER.error("Parse error for timestamp");
				((JTextField) inputField).setText("");
				setTagInfoError(val+". Invalid Date Format!");
				e.printStackTrace();
			}
			
		}
				
	}

	//TODO value==String[]
	private void setValArrayField(JArray field) 
	{
		if(value==null){
			field.setBackground(noInfo,"");
			
		}else{
			field.setValues(value);
//			if(value.length!= inputField.getComponentCount()){
//				LOGGER.error("Wrong input for "+name);
//			}
//			for(int i=0; i<value.length;i++)
//			{
//				setValArrayField(inputField,value[i], i);
//			}
		}
	}
//	private void setValArrayField(JComponent inputField,String s,int i)
//	{
//		JTextField txtF=(JTextField) inputField.getComponent(i);
//		txtF.setText(s);
//		if(s==null || s.equals("")){
//			s="";
//			txtF.setBackground(noInfo);
//		}else{
//			txtF.setBackground(fillInfo);
//		}
//	}

	private void setValCheckbox(JComponent inputField) 
	{
		boolean bVal=BooleanUtils.toBoolean(value[0]);
		((JCheckBox) inputField).setSelected(bVal);
	}

	private void setValTextPane(JComponent inputField) {
		((JTextPane) inputField).setText(value[0]);
	}
	
	private void setValTextArea(JComponent inputField){
		((ScrollableTextPane)inputField).setText(value[0]);
	}

	private void setValComboBox(JComponent inputField) {
		for(int c=0; c< ((JComboBox<String>) inputField).getItemCount(); c++)
		{
			if(((JComboBox<String>) inputField).getItemAt(c).equals(value[0])){
				((JComboBox<String>) inputField).setSelectedIndex(c);
//				  UIManager.put("ComboBox.background", new ColorUIResource(fillInfo));
//				  UIManager.put("JTextField.background", new ColorUIResource(fillInfo));
//				((JTextField) ((JComboBox<String>) inputField).getEditor().getEditorComponent()).setBackground(fillInfo); 
			}
		}
	}
	
	private void setValCheckComboBox(JComponent inputField) {
		((CheckBoxCombo) inputField).init(value);
	}

	private void setValTextField(JComponent inputField) {
		((JTextField) inputField).setText(value[0]==null?"":value[0]);
	}




	public int getTagStatus() {
		return status;
	}
	private void setTagStatus(int status) {
		this.status = status;
	}
	public boolean getTagProp() {
		return prop;
	}
	public void setTagProp(boolean prop) {
		this.prop = prop;
	}
	
	public String getTagType() {
		return type;
	}
	
	public Class getTagTypeClass() {
		if(inputField==null)
			return String.class;
		return inputField.getClass();
	}

	public void setEnable(boolean val)
	{
		if(!val)
			status=INACTIVE;
	}

	

	public boolean valueHasChanged()
	{
//		if(type==LIST)
//			return ((ExperimenterBox)inputField).valueChanged();
	
		return valChanged;
	}
	public boolean isDataSaved(){
		return valSaved;
	}
	public void dataSaved(boolean b){
		valSaved=b;
	}
	
	public void dataHasChanged(boolean b){
		valChanged=b;
		if(b)
			valSaved=false;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getTagInfo() {
		return tagInfo;
	}

	public void setTagInfoError(String tagInfo) 
	{
		this.tagInfo = tagInfo;
		
		if(!tagInfo.equals("")){
			fieldBorder=errorBorder;
			
		}else{
			fieldBorder=normalBorder;
		}
		
		
	}

	class ScrollableTextPane extends JScrollPane
	{
		JTextArea area;
		
		public ScrollableTextPane()
		{
			area=new JTextArea();
			area.setRows(5);
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
			setViewportView(area);
			
		}
		public void setTextAreaRows(int r)
		{
			area.setRows(r);
		}
		
		public void setText(String val)
		{
			area.setText(val);
		}
		
		public String getText()
		{
			return area.getText();
		}

		@Override
		public void setBackground(Color bg) {
			// TODO Auto-generated method stub
			super.setBackground(bg);
			if(area!=null)
				area.setBackground(bg);
		}
		
		@Override
		public void addKeyListener(KeyListener l) {
			area.addKeyListener(l);
		}
		

	}
	
	/**
	 * Own class for arrayfield component
	 * @author Kunis
	 *
	 */
	class JArray extends JPanel{
		private JTextField[] comp;
		public JArray(int size) {
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setBorder(new EmptyBorder(0, 0, 0, 0));
			
			FocusListener flTextField=new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					System.out.println("-- Call TextField focus listener");
					try {
						saveTagValue(getValuesAsArray());
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void focusGained(FocusEvent e) {
					
				}
			};
			KeyListener klTextField=new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}
				@Override
				public void keyReleased(KeyEvent e) {
					System.out.println("-- KEY RELEASED "+e.getSource().getClass().getName());
					try {
						saveTagValue(getValuesAsArray());
					}catch(Exception ex) {
						ex.printStackTrace();
					}
					valChanged=true;	
					valSaved=false;
				}
				@Override
				public void keyPressed(KeyEvent e) {
				}
			};
			
			
			comp=new JTextField[size];
			for(int i=0; i<size; i++){
				JTextField txtF=new JTextField(10);
//				txtF.setRequestFocusEnabled(false);
				txtF.addFocusListener(flTextField);
				txtF.addKeyListener(klTextField);
				comp[i]=txtF;
			
				this.add(txtF);
			}
			
		}
		public void setValue(String val,int index) {
			if(comp!=null && comp.length>index && comp[index]!=null) {
				comp[index].setText(val);
			}
		}
		public void setValues(String[] values) {
			if(comp!=null) {
				for(int i=0; i<comp.length;i++) {
					if(values.length>i) {
						
						if(values[i]==null || values[i].equals("")){
							values[i]="";
							comp[i].setBackground(noInfo);
						}else{
							comp[i].setBackground(fillInfo);
						}
						comp[i].setText(values[i]);
					}
				}
			}
		}
		public String getValues() {
			String res="";
			for(JTextField s:comp){
				if(s!=null && !s.getText().equals(""))
					res+=s.getText()+",";
			}
			if(res.endsWith(","))
				res=res.substring(0, res.length()-1);
			return res;
		}
		
		public String[] getValuesAsArray() throws Exception
		{
			if(comp==null)
				return null;
			String[] res = new String[comp.length];
			for(int i=0; i<comp.length; i++) {
				res[i]=comp[i].getText();
			}
			return res;
		}
		
		
		public void setBackground(Color c, String text) {
			if(comp==null)
				return;
			for(JTextField t :comp){
				t.setText(text);
				t.setBackground(c);
			}
		}
		@Override
		public void setBackground(Color bg) {
			if(comp!=null) {
				for(JTextField t:comp) {
					t.setBackground(bg);
				}
			}
		}
		@Override
		public void setPreferredSize(Dimension dim)
		{
			if(comp==null || comp.length<2)
				super.setPreferredSize(dim);
			else {
				Dimension subD=new Dimension(dim.width/comp.length,dim.height);
				for(JTextField t:comp)
					t.setPreferredSize(subD);
			}
		}
		
	}

	public void setProperties(TagDataProp prop) {
		setVisible(prop.getVisible());
		setTagUnit(prop.getUnitSymbol());
	}

	


}
