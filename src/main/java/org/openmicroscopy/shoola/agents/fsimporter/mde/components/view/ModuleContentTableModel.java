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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

/**
 *  Table model for predefined objects.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleContentTableModel extends DefaultTableModel{
	private String[] idents;
	private boolean isEditable;
	private Class[] columnTypes;
	
	public ModuleContentTableModel()
	{
		super(new Object[][] {},
				new String[] {"ID","Model", "Manufactur"});
		columnTypes = new Class[] {String.class,String.class,String.class};
		isEditable=false;

	}
	public ModuleContentTableModel(String[] idents) {
		super(new Object[][] {},idents);
		this.idents=idents;
		if(idents!=null)
			this.columnTypes=new Class[idents.length];
		this.isEditable=false;
	}
	
	public void addRow(ModuleContent o)
	{
		super.addRow(parse(o));
	}
	
	//TODO: add jcomponents
	private Object[] parse(ModuleContent c) {
		if(idents==null)
			return new Object[0];
		LinkedHashMap<String, TagData> l= c.getList();
//		String[] idents=c.getIdents();
		
		Object[] o=new Object[idents.length];
		if(l!=null) {
			for(int i=0; i< idents.length;i++) {
				try {
						o[i]=l.containsKey(idents[i])? l.get(idents[i]).getTagValue():"";
						columnTypes[i]=l.get(idents[i]).getTagTypeClass();
				}catch(Exception err){
					err.printStackTrace();
				}
			}
		}
		return o;
	}
	
	public ModuleContent getRowData(int rowIndex, ModuleContent content) {
		 //test the index
        if ( (rowIndex  >  getRowCount()) || rowIndex  <  0)
            return null;

        ArrayList<String> data = new ArrayList<String>();
        for (int c = 0; c  <  getColumnCount(); c++)
        {
        	String val=(String) getValueAt(rowIndex, c);
        	content.set(getColumnName(c),val.split(",") );
        }
       return content;
	}
	
	public void insertRow(int index, ModuleContent o)
	{
		super.insertRow(index, parse(o));
	}
	
	public String getIdents() {
		if(idents!=null) {
			return Arrays.toString(idents);
		}else
			return "";
	}
	
	@Override
    public boolean isCellEditable(int row, int column) {
       //all cells false
       return isEditable;
    }
	
	public void setEditable(boolean isEditable) {
		this.isEditable=isEditable;
	}
	
	public Class getColumnClass(int columnIndex) {
//		return getValueAt(0, c).getClass();
		return columnTypes[columnIndex];
	}
}
