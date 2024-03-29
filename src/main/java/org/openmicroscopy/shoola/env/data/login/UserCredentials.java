/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2021 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.env.data.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import omero.gateway.LoginCredentials;
import omero.gateway.model.GroupData;

/** 
 * Holds the user's credentials for logging onto <i>OMERO</i>.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * @since OME2.2
 */
public class UserCredentials extends LoginCredentials
{
    
	/** Identifies a high speed connection. */
	public static final int HIGH = 0;
	
	/** Identifies a medium speed connection. */
	public static final int MEDIUM = 1;
	
	/** Identifies a low speed connection. */
	public static final int LOW = 2;
    
    /** The connection speed level. */
    private int 	speedLevel;

    /** The selected group or <code>-1</code>. */
    private long 	group;
    
    /** Flag indicating to encrypt or not the data transfer. */
    private boolean encrypted;
    
	/** Indicates that the experimenter to handle is an administrator. */
	private Boolean administrator;
	
	/** Indicates if the experimenter is the owner of the group. */
	private Boolean owner;
	
	/** Indicates to active or not the user. */ 
	private Boolean active;
	
	/** 
	 * Map indicating if the user is the owner of the group or not.
	 * This map should only be used to change ownership status.
	 */
	private Map<GroupData, Boolean> groupsOwner;

	/** Additional command line arguments */
	private Set<String> cmdLineArgs = new HashSet<>();

	/** Characters which need to be escaped for the login username / password */
	private String escapeChars = null;

    /** 
     * Controls if the passed speed index is supported.
     * 
     * @param level The value to handle.
     */
    private void checkSpeedLevel(int level)
    {
    	switch (level) {
			case HIGH:
			case MEDIUM:
			case LOW:
				return;
	
			default:
				throw new IllegalArgumentException("Speed level not valid.");
		}
    }
    
    /**
     * Creates a new instance.
     * 
     * @param userName The <i>OMERO</i> login name of the user.
     *                 This is the <code>OME Name</code> that was assigned to 
     *                 the user when it was created in the DB.
     * @param password The <i>OMERO</i> login password of the user.
     *                 This is the password that was chosen for the user when
     *                 it was created in the DB.
     * @param hostName 	 The name of the selected server.
     * @param speedLevel The connection speed.
     * @throws IllegalArgumentException If the user name and/or the password is
     *                 <code>null</code> or has <code>0</code>-length.
     */
    public UserCredentials(String userName, String password, String hostName,
    		int speedLevel)
    {
    	super(userName, password, hostName);
    	checkSpeedLevel(speedLevel);
    	this.speedLevel = speedLevel;
        group = -1L;
        owner = null;
        administrator = null;
    }
    
    /**
     * Creates a new instance.
     * 
     * @param userName The <i>OMERO</i> login name of the user.
     *                 This is the <code>OME Name</code> that was assigned to 
     *                 the user when it was created in the DB.
     * @param password The <i>OMERO</i> login password of the user.
     *                 This is the password that was chosen for the user when
     *                 it was created in the DB.
     * @throws IllegalArgumentException If the user name and/or the password is
     *                 <code>null</code> or has <code>0</code>-length.
     */
    public UserCredentials(String userName, String password)
    {
    	this(userName, password, "", HIGH);
    }
    
    /**
     * Sets the group.
     * 
     * @param group The value to set.
     */
    public void setGroup(long group) { this.group = group; }
    
    /**
     * Returns the group or <code>-1</code>.
     * 
     * @return See above.
     */
    public long getGroup() { return group; }
    
    /**
     * Sets the flag indicating if the data transfer is encrypted or not.
     * 
     * @param encrypted The value to set.
     */
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    
    /**
     * Returns <code>true</code> if the data transfer is encrypted,
     * <code>false</code> otherwise.
     * 
     * @return See above.
     */
    public boolean isEncrypted() { return encrypted; }
    
    /**
     * Resets the password.
     * 
     * @param password The <i>OMERO</i> login password of the user.
     */
    public void resetPassword(String password)
    {
    	 if (password == null || password.trim().length() == 0)
             throw new IllegalArgumentException("Please specify a password.");
    	 super.getUser().setPassword(password);
    }

    /**
     * Returns the selected connection speed.
     * 
     * @return See above.
     */
    public int getSpeedLevel() { return speedLevel; }

	/**
	 * Sets to <code>true</code> if the experimenters are administrators,
	 * <code>false</code> otherwise.
	 * 
	 * @param administrator The value to set.
	 */
	public void setAdministrator(Boolean administrator)
	{
		this.administrator = administrator;
	}
	
	/**
	 * Returns <code>true</code> if the experimenter is an administrator,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public Boolean isAdministrator() { return administrator; }

	/**
	 * Sets to <code>true</code> if the experimenter is the owner of a group,
	 * <code>false</code> otherwise.
	 * 
	 * @param owner The value to set.
	 */
	public void setOwner(Boolean owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Returns <code>true</code> if the experimenter is the owner of a group,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public Boolean isOwner() { return owner; }

	/**
	 * Sets to <code>true</code> if the experimenter has to be activated,
	 * <code>false</code> otherwise.
	 * 
	 * @param active The value to set.
	 */
	public void setActive(Boolean active)
	{
		this.active = active;
	}
	
	/**
	 * Returns <code>true</code> if the experimenter has to be activated,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public Boolean isActive() { return active; }
	
	/**
	 * Sets the map indicating the ownership status of the groups i.e.
	 * indicate if the user is owner or not of the groups.
	 * 
	 * @param map The map indicating the ownership status of the groups.
	 */
	public void setGroupsOwner(Map<GroupData, Boolean> map)
	{
		groupsOwner = map;
	}
	
	/**
	 * Returns the map indicating the ownership status of the groups i.e.
	 * indicate if the user is owner or not of the groups.
	 * 
	 * @return See above.
	 */
	public Map<GroupData, Boolean> getGroupsOwner() { return groupsOwner; }
	
	/**
	 * Returns <code>true</code> if the user is the owner of the group,
	 * <code>false</code> otherwise.
	 * 
	 * @param group The group to handle.
	 * @return See above.
	 */
	public Boolean isGroupOwner(GroupData group)
	{
		if (groupsOwner == null) return null;
		return groupsOwner.get(group);
	}
	
	/**
	 * Returns the first group to handle or <code>null</code>.
	 * 
	 * @return See above.
	 */
	public GroupData getGroupToHandle()
	{
		if (groupsOwner == null || groupsOwner.size() == 0)
			return null;
		Set<GroupData> groups = groupsOwner.keySet();
		Iterator<GroupData> i = groups.iterator();
		while (i.hasNext()) {
			return i.next();
		}
		return null;
	}
	
    /**
     * Formats user name and password.
     * Each character of the password is replaced by a star.
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("User Name: ");
        buf.append(getUser().getUsername());
        buf.append(" -- Password: ");
		if (getUser().getPassword() != null)
			for (int i = 0; i < getUser().getPassword().length(); ++i)
				buf.append('*');
        return buf.toString();
    }

	/**
	 * Add additional command line args as "some.arg=somevalue"
	 * or "--some.arg=somevalue"
	 * @param args The arguments
	 */
	public void addCmdLineArgs(Collection<String> args) {
    	this.cmdLineArgs.addAll(args);
	}

	@Override
	public List<String> getArguments() {
		// Args login has precedence. By overriding this method
		// and making sure it's not null/empty the gateway will
		// use this argument list for login and additional command
		// line arguments can be passed through.
		List<String> res = new ArrayList<>();
		res.add("--omero.user="+escape(getUser().getUsername()));
		res.add("--omero.pass="+escape(getUser().getPassword()));
		res.add("--omero.host="+getServer().getHost());
		res.add("--omero.port="+getServer().getPort());
		for (String arg : cmdLineArgs) {
			if (arg.startsWith("--"))
				res.add(arg);
			else
				res.add("--"+arg);
		}
		return res;
	}

	/**
	 * Set characters which need to escaped for the login username / password
	 * @param escapeChars See above.
	 */
	public void setEscapeChars(String escapeChars) {
		this.escapeChars = escapeChars;
	}

	/**
	 * Escape characters, if escapeChars is set.
	 * @param arg The string to escape
	 * @return See above.
	 */
	private String escape(String arg) {
		if (escapeChars != null && escapeChars.length()>0) {
			return arg.replaceAll("([" + escapeChars + "])", "\\\\$1");
		}
		return arg;
	}
}
