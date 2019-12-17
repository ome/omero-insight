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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

/**
 * Configurable properties of an key-value.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 */
public class TagDataProp {
	String name;
	Boolean visible;
	String unitSymbol;
	
	public TagDataProp(String name, String unitSymbol, Boolean vis) {
		this.name = name;
		this.visible=vis;
		this.unitSymbol=unitSymbol;
	}

	public TagDataProp(TagDataProp orig) {
		if(orig==null)
			return;
		this.name=orig.name;
		this.visible=orig.visible;
		this.unitSymbol=orig.unitSymbol;
	}

	public String getName() {
		return name;
	}

	public Boolean isVisible() {
		return visible;
	}


	public String getUnitSymbol() {
		return unitSymbol;
	}
}
