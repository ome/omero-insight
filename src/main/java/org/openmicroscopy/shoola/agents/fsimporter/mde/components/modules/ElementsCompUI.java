package org.openmicroscopy.shoola.agents.fsimporter.mde.components.modules;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.slf4j.LoggerFactory;

public abstract class ElementsCompUI extends JPanel
{
//	protected MetaDataControl controller;
	
	/** Logger for this class. */
//    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 protected static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ElementsCompUI.class);
	 
	// input required?
	public static final boolean REQUIRED=true;
	public static final boolean OPTIONAL =false;
	
	
	protected GridBagConstraints c;
	protected GridBagLayout gridbag;
	protected boolean buildComp;
	protected List<JComponent> labels;
	protected List<JComponent> comp;
	
	protected JPanel globalPane;
	
	protected List<TagData> tagList;
	protected boolean dataChanged;
	
	public ElementsCompUI(){
		
	}
	
	public abstract void buildComponents();
	protected abstract void createDummyPane(boolean inactive);
	public abstract void clearDataValues();
	public abstract List<TagData> getActiveTags();
	public abstract boolean userInput();
	public abstract void update(List<TagData> list);
	
	
//	public void initController(MetaDataControl _controller){
//		controller=_controller;
//	}
	
	/**
	 * Get enum values as string[]
	 * @param e Enum.class
	 * @return 
	 */
	public static String[] getNames(Class<? extends Enum<?>> e) {
		 return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
//	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
//	protected void setTag(TagData field, String title,String val,boolean prop,int type)
//	{
//		if(field == null) 
//			field = new TagData(title,val,prop,type);
//		else 
//			field.setTagValue(val,prop);
//	}
	
	public static PositiveInteger parseToPositiveInt(String c)
	{
		if(c==null || c.equals(""))
			return null;
		
		return new PositiveInteger(Integer.parseInt(c));
	}
	
	public static Length parseToLength(String c, Unit<Length> unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
		
		return new Length(Double.valueOf(c), unit);
	}
	
	public static Double parseToDouble(String c) throws NumberFormatException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Double.parseDouble(c);
	}
	
	protected void addTagToGUI(TagData tag)
	{
		if(tag != null && tag.isVisible()){
			labels.add(tag.getTagLabel());
			comp.add(tag.getInputField());
		}
	}
	
	protected void addLabelToGUI(JLabel l)
	{
		if(l!=null){
			Font font = l.getFont();
			// same font but bold
			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
			l.setFont(boldFont);
			labels.add(l);
			comp.add((JComponent) Box.createVerticalStrut(5));
		}
	}
	
	protected void addVSpaceToGui(int height)
	{
		labels.add(new JLabel(""));
		comp.add((JComponent) Box.createVerticalStrut(height));
	}
	
	//TODO
	protected void clearTagValue(TagData tag)
	{
		if(tag != null){
			tag.setTagValue(null);
		}
	}
	
	
	protected boolean isActive(TagData tag)
	{
		if(tag != null && tag.getTagStatus()!=TagData.INACTIVE){
			return true;
		}
		return false;
	}
	
	//TODO: equal vertical space between components
	protected void addLabelTextRows(List<JComponent> labels2,List<JComponent> fields,GridBagLayout gridbag,Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets( 0, 0, 1, 0);
		int numLabels = labels2.size();

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
			c.fill = GridBagConstraints.NONE;      //reset to default
			c.weightx = 0.0;                       //reset to default
			container.add(labels2.get(i), c);

			c.gridwidth = GridBagConstraints.REMAINDER;     //end row
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(fields.get(i), c);
			
		}
	}

	
	
//	class FilterRef{
//		static final String EMISSION="EmissionFilter";
//		static final String EXCITATION="ExcitationFilter";
//		static final String DICHROIC="Dichroic";
//				
//		private String type;
//		private String id;
//		
//		public FilterRef(String t, String id)
//		{
//			type=t;
//			this.id=id;
//		}
//		
//		public String getFilterId()
//		{
//			return this.id;
//		}
//	}



//	public void isUpToDate(boolean b) 
//	{
//		if(tagList!=null){
//			for(int i=0; i<tagList.size();i++){
//				if(tagList.get(i)!=null)
//					tagList.get(i).dataSaved(b);
//			}
//		}
//		dataChanged=!b;
//	}

	






	


	
}


