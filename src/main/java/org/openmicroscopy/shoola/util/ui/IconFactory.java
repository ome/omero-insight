/*
 * org.openmicroscopy.shoola.util.ui.IconFactory
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.util.ui;


//Java imports

import java.net.URL;
import javax.swing.ImageIcon;

//Third-party libraries

//Application-internal dependencies

/**
 * We may not distribute the UI package with Shoola, so we have an internal
 * factory for Icon.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $ $Date: $)
 * </small>
 * @since OME2.2
 */
class IconFactory {

    /**
     * Points to the directory specified by the <i>BASE_RESOURCE_LOCATION</i> tag.
     * The path is relative to the application classpath.
     */
    private static final String BASE_RESOURCE_LOCATION = "/images/util/";

    /**
     * Returns the pathname of the specified file.
     * The returned pathname is relative to the application classpath.
     *
     * @param iconFileName The file name.
     * @return See above.
     */
    static String getResourcePathname(String iconFileName) {
        return BASE_RESOURCE_LOCATION + iconFileName;
    }

    /**
     * Creates an {@link ImageIcon} from the specified file.
     *
     * @param name The file name.  Must be a valid name within the BASE_RESOURCE_LOCATION
     *             specified in the configuration file.
     * @return An {@link ImageIcon} object created from the image file.  The
     * return value will be <code>null</code> if the file couldn't be
     * found or an image icon couldn't be created from that file.
     */
    static ImageIcon getIcon(String name) {
        String path = getResourcePathname(name);
        URL url = IconFactory.class.getResource(path);
        return new ImageIcon(url);
    }

}
