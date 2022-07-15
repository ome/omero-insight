/*
 * org.openmicroscopy.shoola.Main
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2022 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola;

//Java imports

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.Container;
import org.openmicroscopy.shoola.util.CheckThreadViolationRepaintManager;

import javax.swing.RepaintManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** 
 * Application entry point.
 * This class implements the main method, which gets one or two optional
 * arguments to specify the path to the installation directory.
 * <p>If this argument doesn't specify an absolute path, then it'll be 
 * translated into an absolute path. Translation is system dependent -- in
 * many cases, the path is resolved against the user directory (typically the
 * directory in which the JVM was invoked).</p>
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class Main 
{
	
	/**
	 * Main method.
	 * 
	 * @param args	Optional configuration file and path to the installation 
	 * 				directory. If not specified, then the user directory is 
	 * 				assumed and the <code>container.xml</code> is used.
	 */
	public static void main(String[] args) 
	{
		String homeDir = "";
		String configFile = null;//Container.CONFIG_FILE;
		List<String> addArgs = Arrays.stream(args).filter(a -> a.startsWith("--")).collect(Collectors.toList());
		List<String> posArgs = Arrays.stream(args).filter(a -> !a.startsWith("--")).collect(Collectors.toList());
		if (posArgs.size() > 0) configFile = posArgs.get(0);
		if (posArgs.size() > 1) homeDir = posArgs.get(1);

		try {
			String path = Container.CONFIG_DIR+ File.separator+Container.CONFIG_FILE;
			StringBuilder configFileContent = new StringBuilder();
			try (FileReader fr = new FileReader(path);
				 BufferedReader br = new BufferedReader(fr)) {
				 String line = br.readLine();
				 while (line != null) {
					 line = br.readLine();
					 configFileContent.append(line);
				 }
			}
			Matcher m = Pattern.compile("DebugRepaintManager.+?>(.+?)<").matcher(configFileContent.toString());
			if (m.find() && Boolean.parseBoolean(m.group(1))) {
					RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
			}
		} catch (IOException e) {
			// Couldn't access config file, just ignore
		}

		Container.startup(homeDir, configFile, addArgs);
	}
	
}
