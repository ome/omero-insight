package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

public class OMEValueConverter {
	private static String pattern_double = "^[\\+\\-]{0,1}[0-9]+[\\.\\,]{1}[0-9]+$";//"[+-]|[0-9]+.*[0-9]*";//"\\s|[0-9]+.*[0-9]*";//"\\d*+\\.\\d{1,}";
	private static String pattern_posDouble="\\s|[1-9]+.*[0-9]*";
//	private static String pattern_number="\d";
	/* http://stackoverflow.com/questions/6400955/how-to-get-1-100-using-regex
	 * match 0 oder 0.0-0.99 oder 1 oder 1.0*/
//	private static String pattern_percentFraction="[0]{1}.[0-9]{1,2}|1|1.0|0";
	private static String pattern_percentFraction="[0]{1}.*[0-9]*|1|1.0|0";
	
	/**
	 * Get enum values as string[]
	 * @param e Enum.class
	 * @return 
	 */
	public static String[] getNames(Class<? extends Enum<?>> e) {
		 return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
//	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
	/**
	 * Parse String to a simple type PercentFraction that restricts the value to a float between 0 and 1 (inclusive)
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static PercentFraction parseToPercentFraction(String c) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		
		return new PercentFraction(Float.valueOf(c));
	}
	
	public static Boolean parseToBoolean(String val) 
	{
		if(val==null || val.equals("")){
			return null;
		}
		
		return Boolean.valueOf(val);
	}
	
	public static PositiveInteger parseToPositiveInt(String c) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
		PositiveInteger result=null;
		Integer t=Integer.parseInt(c);
//		if(t!=null && t>0){
//			MonitorAndDebug.printConsole("\t...parseToPositiveInt() "+t);
			result=new PositiveInteger(t);
//		}else{
//			MonitorAndDebug.printConsole("ERROR: parseToPositiveInt() "+c);
//		}
		return result;
	}
	
	/**
	 * If positiveVal==true, c has to be a positive float >0. Test by parse PositiveFloat
	 * @param c
	 * @param unit
	 * @param positiveVal
	 * @return
	 * @throws Exception
	 */
	public static Length parseToLength(String c, Unit<Length> unit, boolean positiveVal) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
		
		Double value=Double.valueOf(c);
		Length result=null;
		if(positiveVal){
			// if value isn't a positive number-> throws error
			PositiveFloat pF=new PositiveFloat(value);
			result=new Length(value,unit);
		}else{
			result=new Length(value,unit);
		}
		
		return result;
	}
	
	public static Double parseToDouble(String c) throws NumberFormatException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Double.parseDouble(c);
	}
	
	protected Double validateInput(TagData tag,String error) 
	{
		return validateInput(tag,error,null);
	}
	
	protected Double validateInput(TagData tag,String error,String pattern) 
	{
		
		String text = tag.getTagValue();
		if(text.equals(""))
			return null;
		
		String errorString="";
		tag.setTagInfoError("");
		try{
			return Double.parseDouble(text);
			
		}catch(NumberFormatException e){
			// The string value might be either 99.99 or 99,99, depending on Locale.
	        // We can deal with this safely, by forcing to be a point for the decimal separator, and then using Double.valueOf ...
	        //http://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator
	        String text2 = text.replaceAll(",",".");
	        try {
	          return Double.parseDouble(text2);
	        } catch (NumberFormatException e2)  {
	            // This happens if we're trying (say) to parse a string that isn't a number, as though it were a number!
	            // If this happens, it should only be due to application logic problems.
	            // In this case, the safest thing to do is return 0, having first fired-off a log warning.
	        	errorString=error;
	        }
		}
		tag.setTagInfoError(errorString);
		return null;
	}
	
	public void inputKeyPressed()
	{
//		inputEvent=true;
	}
	
	/**
	 * @return
	 */
	public DocumentListener createDocumentListenerDouble(TagData tag, String error) {
		return new DocumentListenerForDouble(tag,error, false); 
			
	}
	
	public DocumentListener createDocumentListenerPosFloat(TagData tag,String error){
		return new DocumentListenerForDouble(tag,error, true); 
	}
	
	class DocumentListenerForDouble implements DocumentListener
	{
		private TagData tag;
		private String error;
		private boolean posVal;
		
		public DocumentListenerForDouble(TagData tag,String error, boolean positiveVal)
		{
			this.tag=tag;
			this.error=error;
			this.posVal=positiveVal;
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			validate();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			validate();
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			validate();
		}
		
		private void validate(){
			tag.setTagInfoError("");
			
			Double res=validateInput(tag,error);
			if(posVal){
				if( res!=null && res <0){
					tag.setTagInfoError(error);
				}
			}
		}
	}
	
	
	/**
	 * @return
	 */
	public DocumentListener createDocumentListenerPercentFraction(TagData tag, String error) {
		return new DocumentListenerForPercentFraction(tag,error); 
			
	}
	
	class DocumentListenerForPercentFraction implements DocumentListener
	{
		private TagData tag;
		private String error;
		public DocumentListenerForPercentFraction(TagData tag,String error)
		{
			this.tag=tag;
			this.error=error;
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			validate();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			validate();
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			validate();
		}
		
		private void validate()
		{
			tag.setTagInfoError("");
			Double res=validateInput(tag,error);
			if(res!=null && (res <0 || res >1)){
				tag.setTagInfoError(error);
			}
		}
	}
	
//	class TagActionListener implements ActionListener
//	{
//		private TagData tag;
//		public TagActionListener(TagData tag)
//		{
//			this.tag=tag;
//		}
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			tag.dataHasChanged(true);
//			inputKeyPressed();
//		}
//		
//	}
	
}
