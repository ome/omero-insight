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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class DocumentListenerForPercentFraction implements DocumentListener
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
