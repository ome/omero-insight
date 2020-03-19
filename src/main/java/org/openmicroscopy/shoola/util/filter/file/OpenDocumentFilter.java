/*------------------------------------------------------------------------------
 *  Copyright (C) 2020 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.util.filter.file;


import javax.swing.filechooser.FileFilter;
import java.io.File;


/** 
 * 
 * Filters the <code>Open Document</code> files.
 * 
 * @author  Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 * @since OME3.0
 */
public class OpenDocumentFilter
	extends CustomizedFileFilter
{

	/** Possible file extension. */
	public static final String  ODT = "odt";

	/** Possible file extension. */
	public static final String  OTT = "ott";

	/** Possible file extension. */
	public static final String  ODS = "ods";

	/** Possible file extension. */
	public static final String  ODM = "odm";

	/** Possible file extension. */
	public static final String  ODP = "odp";

	/** Possible file extension. */
	public static final String  ODG = "odg";

	/** The possible extensions. */
	public static final String[] extensions;
	
	/** The description of the filter. */
	private static final String	description;
	
	static {
		extensions = new String[6];
		extensions[0] = ODT;
		extensions[1] = OTT;
		extensions[2] = ODS;
		extensions[3] = ODM;
		extensions[4] = ODP;
		extensions[5] = ODG;

		StringBuffer s = new StringBuffer();
		s.append( "Open Document Format (");
		for (int i = 0; i < extensions.length; i++) {
			s.append("*."+extensions[i]);
			if (i < extensions.length-1)
				s.append(", ");
		}
		s.append(")");
		description = s.toString();
	}
	
	/**
	 * 	Overridden to return the MIME type.
	 * 	@see CustomizedFileFilter#getMIMEType()
	 */
	public String getMIMEType() { return "application/vnd.oasis.opendocument"; }
	
	/**
	 * 	Overridden to return the extension of the filter.
	 * 	@see CustomizedFileFilter#getExtension()
	 */
	public String getExtension() { return ODT; }
	
	/**
	 * 	Overridden to return the description of the filter.
	 * 	@see FileFilter#getDescription()
	 */
	public String getDescription() { return description; }
    
	/**
	 * 	Overridden to accept file with the declared file extensions.
	 * @see FileFilter#accept(File)
	 */
	public boolean accept(File f)
	{
		if (f == null) return false;
		if (f.isDirectory()) return true;
		return isSupported(f.getName(), extensions);
	}
	
	/**
	 * Overridden to accept the file identified by its name.
	 * @see CustomizedFileFilter#accept(String)
	 */
	public boolean accept(String fileName)
	{
		return isSupported(fileName, extensions);
	}
	
}

