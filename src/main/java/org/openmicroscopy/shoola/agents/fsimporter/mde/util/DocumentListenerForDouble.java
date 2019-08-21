package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentListenerForDouble implements DocumentListener
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
	
	private Double validateInput(TagData tag,String error) 
	{
		return validateInput(tag,error,null);
	}
	
	private Double validateInput(TagData tag,String error,String pattern) 
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
}
