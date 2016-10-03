/*
 * org.openmicroscopy.shoola.agents.dataBrowser.view.WellFieldsCanvas 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2016 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
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
package org.openmicroscopy.shoola.agents.dataBrowser.view;

import java.awt.Point;

import javax.swing.JPanel;

import org.openmicroscopy.shoola.agents.dataBrowser.browser.WellSampleNode;

/**
 * Display all the fields for a given well.
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 *
 */
public abstract class WellFieldsCanvas extends JPanel {

    /**
     * Refresh the UI
     */
    public abstract void refreshUI();

    /**
     * Set loading state
     * 
     * @param loading
     *            Pass <code>true</code> to indicate loading state
     */
    public abstract void setLoading(boolean loading);

    /**
     * Get the {@link WellSampleNode} at a certain position
     * 
     * @param p
     *            The Position
     * @return The {@link WellSampleNode} at the given position
     */
    public abstract WellSampleNode getNode(Point p);

}
