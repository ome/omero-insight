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
package org.openmicroscopy.shoola.agents.fsimporter.mde.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;

/**
 * Datastructur:
 * <pre>{@code
 * ModuleList==HashMap<String,List<ModuleContent>>
 * }</pre>
 * Key:= instrument type,
 * values:= available values for the key
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleList extends HashMap<String, List<ModuleContent>> {
	
	/** 
	 * copy constructor
	 * @param orig
	 */
	public ModuleList(ModuleList orig) {
		if(orig!=null) {

			for (Map.Entry<String,List<ModuleContent>> entry: orig.entrySet()) {
				List<ModuleContent> newList = new ArrayList<>();
				if(entry.getValue()!=null) {
					for(ModuleContent c : entry.getValue()) {
						newList.add(new ModuleContent(c));
					}
					this.put(entry.getKey(), newList);
				}else {
					this.put(entry.getKey(),null);
				}
			}
		}
	}

	public ModuleList() {
		super();
	}
	
	public void remove(String type) {
		this.remove(type);
	}
	
	public void print(String header) {
		ImporterAgent.getRegistry().getLogger().debug(this, header);
		String result="{\n";
		for(Map.Entry<String,List<ModuleContent>> entry: this.entrySet()) {
			if(entry.getValue()!=null) {
				result+=entry.getKey()+": "+entry.getValue().size()+" Elements \n";
				for(ModuleContent c:entry.getValue()) {
					result+="\t"+c.getAttributeValue(TagNames.MODEL)+"\n";
				}
			}
			else {
				result+=entry.getKey()+": null Elements \n";
			}
		}
		ImporterAgent.getRegistry().getLogger().debug(this, result+"\n}");
	}
	
}
