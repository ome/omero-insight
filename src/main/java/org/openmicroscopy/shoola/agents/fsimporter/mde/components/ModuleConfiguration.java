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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagDataProp;

/**
 * Holds configuration of content of a module as list of TagDataProp.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class ModuleConfiguration {
	/* Configuration for attributes*/
	private LinkedHashMap<String, TagDataProp> tagConf;
	public ModuleConfiguration() {
		this.tagConf=new LinkedHashMap<>();
	}
	public ModuleConfiguration(LinkedHashMap<String, TagDataProp> conf) {
		this.tagConf=conf;
	}
	
	public ModuleConfiguration(ModuleConfiguration orig) {
		if(orig==null || orig.tagConf==null)
			return;
		this.tagConf=new LinkedHashMap<>();
		for(Entry<String, TagDataProp> entry : orig.tagConf.entrySet()) {
			if(entry.getValue()!=null) {
				this.tagConf.put(entry.getKey(), new TagDataProp(entry.getValue()));
			}
		}
	}
	
	public TagDataProp getConfigurationFor(String name) {
		if(tagConf!=null && tagConf.containsKey(name))
			return tagConf.get(name);
		
		return null;
	}
	public void put(String name,TagDataProp prop) {
		if(this.tagConf==null)
			this.tagConf=new LinkedHashMap<>();
		this.tagConf.put(name, prop);
	}
	public List<TagDataProp> getTagPropList() {
		if(tagConf!=null)
			return new ArrayList<TagDataProp>(tagConf.values());
		return null;
	}
}
