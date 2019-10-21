/*
 * org.openmicroscopy.shoola.env.log.PluginLoggerImpl
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.env.log;


import omero.log.LogMessage;

import omero.log.Logger;


/**
 * Provides the log service for cases
 *
 * @since 5.5.3
 */
class ConsoleLoggerImpl
    implements Logger
{

    private boolean console;

    /**
     * Initializes
     *
     * @param console Print out the message to the console
     */
    ConsoleLoggerImpl(boolean console)
    {
        this.console = console;
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#debug(Object, String)
     */
    public void debug(Object c, String logMsg)
    {
        if (console) {
            System.err.println(logMsg);
        }
    }

    /**
     * Implemented as specified by {@link Logger}
     * @see Logger#debug(Object, LogMessage)
     */
    public void debug(Object c, LogMessage msg)
    {
        if (console) {
            System.err.println(msg == null ? null : msg.toString());
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#error(Object, String)
     */
    public void error(Object c, String logMsg)
    {
        if (console) {
            System.err.println(logMsg);
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#error(Object, LogMessage)
     */
    public void error(Object c, LogMessage msg)
    {
        if (console) {
            System.err.println(msg == null ? null : msg.toString());
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#fatal(Object, String)
     */
    public void fatal(Object c, String logMsg)
    {
        if (console) {
            System.err.println(logMsg);
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#fatal(Object, LogMessage)
     */
    public void fatal(Object c, LogMessage msg)
    {
        if (console) {
            System.err.println(msg == null ? null : msg.toString());
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#info(Object, String)
     */
    public void info(Object c, String logMsg)
    {
        if (console) {
            System.err.println(logMsg);
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#info(Object, LogMessage)
     */
    public void info(Object c, LogMessage msg)
    {
        if (console) {
            System.err.println(msg == null ? null : msg.toString());
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#warn(Object, String)
     */
    public void warn(Object c, String logMsg)
    {
        if (console) {
            System.err.println(logMsg);
        }
    }

    /**
     * Implemented as specified by {@link Logger}.
     * @see Logger#warn(Object, LogMessage)
     */
    public void warn(Object c, LogMessage msg)
    {
        if (console) {
            System.err.println(msg == null ? null : msg.toString());
        }
    }

    @Override
    public String getLogFile() {
        return null;
    }
}
