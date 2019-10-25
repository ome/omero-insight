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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import ome.model.units.UnitEnum;

import ome.model.enums.UnitsTime;
import ome.model.units.ElectricPotential;
import ome.model.units.Frequency;
import ome.model.units.Power;
import ome.model.units.Pressure;
import ome.model.units.Temperature;
import ome.model.units.Time;
import ome.model.units.UNITS;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 * TODO: rename class or move methods to other classes
 * Converter for OME-XML Unit class/ Unit symbol helper function. DocumentListener for TagData input fields. 
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
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
	 * Get enum unit values as string[]
	 * @param e Enum.class
	 * @return 
	 */
	public static String[] getUnitSymbols(Class<? extends Enum<?>> e) {
		ome.model.units.UnitEnum[] enums=(UnitEnum[]) e.getEnumConstants();
		String[] symbols=new String[enums.length];
		for(int i=0; i<enums.length; i++ ) {
			symbols[i]=enums[i].getSymbol();
		}
		return symbols;
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
		result=new PositiveInteger(t);
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


	public static ome.model.units.Unit[] convert(String[] value, String unitSymbol, String targetUnit) throws Exception {
		if(value==null || value.length==0 || unitSymbol==null) {
			return null;
		}
		ome.model.units.Unit[] result=new ome.model.units.Unit[value.length];
		int counter=0;

		Class unitClass=TagNames.getUnitClassFromSymbol(unitSymbol);

		if(unitClass.equals(ome.model.units.ElectricPotential.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.ElectricPotential tOld=new omero.model.ElectricPotentialI(Double.parseDouble(value[i]),ome.model.enums.UnitsElectricPotential.bySymbol(unitSymbol));
					omero.model.ElectricPotential tNew= new omero.model.ElectricPotentialI(tOld,
							omero.model.enums.UnitsElectricPotential.valueOf(ome.model.enums.UnitsElectricPotential.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.ElectricPotential(tNew.getValue(),ome.model.enums.UnitsElectricPotential.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}
		}
		if(unitClass.equals(ome.model.units.Power.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.Power tOld=new omero.model.PowerI(Double.parseDouble(value[i]),ome.model.enums.UnitsPower.bySymbol(unitSymbol));
					omero.model.Power tNew= new omero.model.PowerI(tOld,
							omero.model.enums.UnitsPower.valueOf(ome.model.enums.UnitsPower.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.Power(tNew.getValue(),ome.model.enums.UnitsPower.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}			
		}
		if(unitClass.equals(ome.model.units.Frequency.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.Frequency tOld=new omero.model.FrequencyI(Double.parseDouble(value[i]),ome.model.enums.UnitsFrequency.bySymbol(unitSymbol));
					omero.model.Frequency tNew= new omero.model.FrequencyI(tOld,
							omero.model.enums.UnitsFrequency.valueOf(ome.model.enums.UnitsFrequency.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.Frequency(tNew.getValue(),ome.model.enums.UnitsFrequency.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}
		}
		if(unitClass.equals(ome.model.units.Pressure.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.Pressure tOld=new omero.model.PressureI(Double.parseDouble(value[i]),ome.model.enums.UnitsPressure.bySymbol(unitSymbol));
					omero.model.Pressure tNew= new omero.model.PressureI(tOld,
							omero.model.enums.UnitsPressure.valueOf(ome.model.enums.UnitsPressure.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.Pressure(tNew.getValue(),ome.model.enums.UnitsPressure.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}
		}
		if(unitClass.equals(ome.model.units.Length.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.Length tOld=new omero.model.LengthI(Double.parseDouble(value[i]),ome.model.enums.UnitsLength.bySymbol(unitSymbol));
					omero.model.Length tNew= new omero.model.LengthI(tOld,
							omero.model.enums.UnitsLength.valueOf(ome.model.enums.UnitsLength.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.Length(tNew.getValue(),ome.model.enums.UnitsLength.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}
		}
		if(unitClass.equals(ome.model.units.Temperature.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.Temperature tOld=new omero.model.TemperatureI(Double.parseDouble(value[i]),ome.model.enums.UnitsTemperature.bySymbol(unitSymbol));
					omero.model.Temperature tNew= new omero.model.TemperatureI(tOld,
							omero.model.enums.UnitsTemperature.valueOf(ome.model.enums.UnitsTemperature.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.Temperature(tNew.getValue(),ome.model.enums.UnitsTemperature.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}
		}
		if(unitClass.equals(ome.model.units.Time.class)) {
			for(int i=0; i<value.length;i++) {
				if(value[i]!=null && !value[i].isEmpty()) {
					omero.model.Time tOld=new omero.model.TimeI(Double.parseDouble(value[i]),UnitsTime.bySymbol(unitSymbol));
					omero.model.Time tNew= new omero.model.TimeI(tOld,
							omero.model.enums.UnitsTime.valueOf(ome.model.enums.UnitsTime.bySymbol(targetUnit).toString())); 
					result[i]= new ome.model.units.Time(tNew.getValue(),UnitsTime.bySymbol(tNew.getSymbol()));
					counter++;
				}
			}
		}
			
				
		if(counter>0)
			return result;
		return null;

	}
}
