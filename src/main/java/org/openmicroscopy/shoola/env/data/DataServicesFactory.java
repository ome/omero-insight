/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2017 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.env.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openmicroscopy.shoola.env.Agent;
import org.openmicroscopy.shoola.env.Container;
import org.openmicroscopy.shoola.env.Environment;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.cache.CacheServiceFactory;
import org.openmicroscopy.shoola.env.config.AgentInfo;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.config.RegistryFactory;
import org.openmicroscopy.shoola.env.data.events.ConnectedEvent;
import org.openmicroscopy.shoola.env.data.events.ReloadRenderingEngine;
import org.openmicroscopy.shoola.env.data.login.LoginService;
import org.openmicroscopy.shoola.env.data.login.UserCredentials;

import omero.ServerError;
import omero.api.IConfigPrx;
import omero.gateway.LoginCredentials;
import omero.gateway.SecurityContext;
import omero.gateway.exception.DSAccessException;
import omero.gateway.exception.DSOutOfServiceException;
import omero.gateway.facility.AdminFacility;

import org.openmicroscopy.shoola.env.data.views.DataViewsFactory;
import org.openmicroscopy.shoola.env.event.EventBus;

import omero.log.LogMessage;
import omero.log.Logger;

import org.openmicroscopy.shoola.env.rnd.PixelsServicesFactory;
import org.openmicroscopy.shoola.env.rnd.RenderingControl;
import org.openmicroscopy.shoola.env.ui.AbstractIconManager;
import org.openmicroscopy.shoola.env.ui.UserNotifier;
import org.openmicroscopy.shoola.svc.proxy.ProxyUtil;
import org.openmicroscopy.shoola.util.CommonsLangUtils;
import org.openmicroscopy.shoola.util.VersionCompare;
import org.openmicroscopy.shoola.util.ui.IconManager;
import org.openmicroscopy.shoola.util.ui.MessageBox;
import org.openmicroscopy.shoola.util.ui.NotificationDialog;
import org.openmicroscopy.shoola.util.ui.ShutDownDialog;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import omero.gateway.model.ExperimenterData;
import omero.gateway.model.GroupData;

import ome.system.UpgradeCheck;

import ij.plugin.frame.Recorder;


/** 
 * A factory for the {@link OmeroDataService} and the {@link OmeroImageService}.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * @since OME2.2
 */
public class DataServicesFactory
{

    /** The sole instance. */
	private static DataServicesFactory singleton;
	
	/** The dialog indicating that the connection is lost.*/
	private JDialog connectionDialog;
	
	/** Flag indicating that the client and server are not compatible.*/
	private boolean compatible;

	/**
	 * Creates a new instance. This can't be called outside of container 
	 * b/c agents have no references to the singleton container.
	 * So we can be sure this method is going to create services just once.
	 * 
	 * @param c Reference to the singleton container. Mustn't be 
	 * 			<code>null</code>.
	 * @return See above.
	 * @throws DSOutOfServiceException 
	 */
	public static DataServicesFactory getInstance(Container c)
		throws DSOutOfServiceException
	{
		if (c == null)
			throw new NullPointerException();  //An agent called this method?
		if (singleton == null)
			singleton = new DataServicesFactory(c);
		return singleton;
	}
	
	/** 
	 * Reference to the container, to exit the application when the session
	 * has expired.
	 */
	private Container					container;

	/** A reference to the container's registry. */
	private static Registry         	registry;

	/** Unified access point to the various OMERO services. */
	private static OMEROGateway			omeroGateway;

	/** The OMERO service adapter. */
	private OmeroDataService			ds;

	/** The image service adapter. */
	private OmeroImageService			is;

	/** The metadata service adapter. */
	private OmeroMetadataService 		ms;
 
	/** The Administration service adapter. */
	private AdminService				admin;

    /** Flag indicating that we try to re-establish the connection.*/
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

	/**
	 * Attempts to create a new instance.
     * 
	 * @param c	Reference to the container.
	 * @throws DSOutOfServiceException If the connection can't be established
	 * 									or the credentials are invalid.	
	 */
	private DataServicesFactory(Container c)
		throws DSOutOfServiceException
	{
		registry = c.getRegistry();
		container = c;
		//Check what to do if null.
        omeroGateway = new OMEROGateway(this);
        
		//Create the adapters.
        ds = new OmeroDataServiceImpl(omeroGateway, registry);
        is = new OmeroImageServiceImpl(omeroGateway, registry);
        ms = new OmeroMetadataServiceImpl(omeroGateway, registry);
        admin = new AdminServiceImpl(omeroGateway, registry);
        
        // pass the adapters on to the registry
        RegistryFactory.linkOS(ds, registry);
        RegistryFactory.linkMS(ms, registry);
        RegistryFactory.linkAdmin(admin, registry);
        RegistryFactory.linkIS(is, registry);
        RegistryFactory.linkGateway(omeroGateway.getGateway(), registry);
        
        //Initialize the Views Factory.
        DataViewsFactory.initialize(c);
	}
	
	/**
     * Determines the quality of the compression depending on the
     * connection speed.
     * 
     * @param connectionSpeed The connection speed.
     * @return See above.
     */
    private float determineCompression(int connectionSpeed)
    {
        Float value;
        switch (connectionSpeed) {
            case UserCredentials.MEDIUM:
            case UserCredentials.HIGH:
                value = (Float) registry.lookup(
                        LookupNames.COMPRESSIOM_MEDIUM_QUALITY);
                return value.floatValue();
            case UserCredentials.LOW:
            default:
                value = (Float) registry.lookup(
                        LookupNames.COMPRESSIOM_LOW_QUALITY);
                return value.floatValue();
        }
    }
    
    /**
     * Returns the image quality with respect to the
     * connection speed
     * 
     * @param connectionSpeed The connection speed.
     * @return See above.
     */
    private int determineImageQuality(int connectionSpeed)
    {
        switch (connectionSpeed) {
            case UserCredentials.HIGH:
                return RenderingControl.UNCOMPRESSED;
            case UserCredentials.MEDIUM:
                return RenderingControl.MEDIUM;
            case UserCredentials.LOW:
            default:
                return RenderingControl.LOW;
        }
    }

    /**
     * Returns the credentials.
     * 
     * @return See above.
     */
    UserCredentials getCredentials()
    {
    	return (UserCredentials) 
    		registry.lookup(LookupNames.USER_CREDENTIALS);
    }

    /**
     * Returns the time before each network check.
     * 
     * @return See above.
     */
    Integer getElapseTime()
    {
        return (Integer) registry.lookup(LookupNames.ELAPSE_TIME);
    }

    /**
     * Adds a listener to the dialog and shows the dialog depending on the
     * specified value.
     */
    private void addListenerAndShow()
    {
        if (connectionDialog instanceof ShutDownDialog) {
            ShutDownDialog d = (ShutDownDialog) connectionDialog;
            d.setGateway(omeroGateway.getGateway());
            d.setCheckupTime(5);
        }
        connectionDialog.setModal(false);
        connectionDialog.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (NotificationDialog.CLOSE_NOTIFICATION_PROPERTY.equals(name))
                {
                    reconnecting.set(false);
                    connectionDialog = null;
                    exitApplication(true, true);
                } else if (
                    NotificationDialog.CANCEL_NOTIFICATION_PROPERTY.equals(
                            name))
                {
                    connectionDialog = null;
                    reconnecting.set(false);
                    int index = (Integer) evt.getNewValue();
                    if (index == ConnectionExceptionHandler.LOST_CONNECTION)
                        reconnect();
                }
            }
        });
        connectionDialog.setModal(true);
        UIUtilities.centerAndShow(connectionDialog);
    }

    /** Attempts to reconnect.*/
    private void reconnect()
    {
        JFrame f = registry.getTaskBar().getFrame();
        String message;
        Map<SecurityContext, Set<Long>> l =
                omeroGateway.getRenderingEngines();
        boolean b = omeroGateway.joinSession();
        if (b) {
            //reactivate the rendering engine. Need to review that
            Iterator<Entry<SecurityContext, Set<Long>>> i =
                    l.entrySet().iterator();
            OmeroImageService svc = registry.getImageService();
            Long id;
            Entry<SecurityContext, Set<Long>> entry;
            Map<SecurityContext, List<Long>> 
            failures = new HashMap<SecurityContext, List<Long>>();
            Iterator<Long> j;
            SecurityContext ctx;
            List<Long> failure;
            RenderingControl p;
            while (i.hasNext()) {
                entry = i.next();
                j = entry.getValue().iterator();
                ctx = entry.getKey();
                while (j.hasNext()) {
                    id = j.next();
                    try {
                        p = PixelsServicesFactory.getRenderingControl(
                                registry, Long.valueOf(id), false);
                        if (!p.isShutDown()) {
                            registry.getLogger().debug(this,
                                    "loading re "+id);
                            svc.reloadRenderingService(ctx, id);
                        }
                    } catch (Exception e) {
                        failure = failures.get(ctx);
                        if (failure == null) {
                            failure = new ArrayList<Long>();
                            failures.put(ctx, failure);
                        }
                        registry.getLogger().debug(this,
                                "Failed to load re for "+id+" "+e);
                        failure.add(id);
                    }
                }
            }
            if (failures.size() > 0) {
                registry.getEventBus().post(
                        new ReloadRenderingEngine(failures));
            }
            connectionDialog.setVisible(false);
            connectionDialog.dispose();
            connectionDialog = null;
            reconnecting.set(false);
        } else {
            //connectionDialog.setVisible(false);
            message = "A failure occurred while attempting to " +
                    "reconnect.\nThe application will now exit.";
            connectionDialog = new NotificationDialog(f,
                    "Reconnection Failure", message, null);
            addListenerAndShow();
        }
    }

    /**
     * Returns the value of the plug-in or <code>-1</code>.
     * 
     * @return See above.
     */
    int runAsPlugin()
    {
        Integer v = (Integer) container.getRegistry().lookup(
                LookupNames.PLUGIN);
        if (v == null) return -1;
        return v.intValue();
    }

	/**
	 * Brings up a dialog indicating that the session has expired and
	 * quits the application.
	 * 
	 * @param index One of the connection constants defined by the gateway.
	 * @param exc The exception to register.
	 */
	public void sessionExpiredExit(int index, Throwable exc)
	{
	    if (reconnecting.get()) return;
		reconnecting.set(true);
		String message;
		if (exc != null) {
			LogMessage msg = new LogMessage();
			msg.print("Connection Error");
			msg.print(exc);
			registry.getLogger().debug(this, msg);
		}
		JFrame f = registry.getTaskBar().getFrame();
		switch (index) {
			case ConnectionExceptionHandler.DESTROYED_CONNECTION:
				message = "The connection has been destroyed." +
						"\nThe application will now exit.";
				connectionDialog = new NotificationDialog(f,
				        "Connection Refused", message, null);
				addListenerAndShow();
				break;
			case ConnectionExceptionHandler.NETWORK:
				message = "The network is down.\n";
				connectionDialog = new ShutDownDialog(f, "Network down",
				        message, -1);
				addListenerAndShow();
				break;
			case ConnectionExceptionHandler.LOST_CONNECTION:
			    connectionDialog = new ShutDownDialog(f, "Lost connection",
                        "Trying to reconnect...", index);
			    addListenerAndShow();
				break;
			case ConnectionExceptionHandler.SERVER_OUT_OF_SERVICE:
				message = "The server is no longer " +
				"running.\nPlease contact your system administrator." +
				"\nThe application will now exit.";
				connectionDialog = new NotificationDialog(f,
				        "Connection Refused", message, null);
				addListenerAndShow();
		}
	}

    /**
     * Returns the {@link OmeroDataService}.
     * 
     * @return See above.
     */
    public OmeroDataService getOS() { return ds; }
    
    /**
     * Returns the {@link OmeroImageService}.
     * 
     * @return See above.
     */
    public OmeroImageService getIS() { return is; }
    
    /**
     * Returns the {@link OmeroMetadataService}.
     * 
     * @return See above.
     */
    public OmeroMetadataService getMS() { return ms; }
    
    /**
     * Returns the {@link AdminService}.
     * 
     * @return See above.
     */
    public AdminService getAdmin() { return admin; }
    
    /**
     * Returns the {@link LoginService}. 
     * 
     * @return See above.
     */
    public LoginService getLoginService()
    {
        return (LoginService) registry.lookup(LookupNames.LOGIN);
    }
    
    /**
     * Returns the {@link Logger}
     * 
     * @return See above.
     */
    Logger getLogger()
    {
        return (Logger) registry.getLogger();
    }
    
	/**
	 * Attempts to connect to <i>OMERO</i> server.
	 * 
     * @param uc The user's credentials for logging onto <i>OMERO</i> server.
	 * @throws DSOutOfServiceException If the connection can't be established
     *                                 or the credentials are invalid.
	 */
	public void connect(UserCredentials uc)
		throws DSOutOfServiceException
	{
	    
		if (uc == null)
            throw new NullPointerException("No user credentials.");
        LogMessage msg;
		String name = (String) 
		container.getRegistry().lookup(LookupNames.MASTER);
        if (CommonsLangUtils.isBlank(name)) {
            name = LookupNames.MASTER_INSIGHT;
        }
		LoginCredentials cred = new LoginCredentials();
        cred.getUser().setUsername(uc.getUserName());
        cred.getUser().setPassword(uc.getPassword());
        cred.getServer().setHostname( uc.getHostName());
        cred.getServer().setPort(uc.getPort());
        cred.setApplicationName(name);
        cred.setCheckNetwork(true);
        cred.setCompression(determineCompression(uc.getSpeedLevel()));
        cred.setEncryption(uc.isEncrypted());
        
		ExperimenterData exp = omeroGateway.connect(cred);
		//Record the session key if run as plugin
        String sessionId = omeroGateway.getSessionId(exp);
        Recorder recorder = new Recorder(false);
        recorder.record("omero.session:"+sessionId);

		//check client server version
		compatible = true;
        //Register into log file.
        Object v = container.getRegistry().lookup(LookupNames.VERSION);
    	String clientVersion = "";
    	if (v != null && v instanceof String)
    		clientVersion = (String) v;
    	if (uc.getUserName().equals(sessionId)) {
    	    container.getRegistry().bind(LookupNames.SESSION_KEY, Boolean.TRUE);
    	}
        //Check if client and server are compatible.
        String version = omeroGateway.getServerVersion();

        IConfigPrx cs = omeroGateway.getGateway().getConfigService(new SecurityContext(exp.getGroupId()));
        try {
            String val = cs.getConfigValue("omero.pixeldata.max_plane_width");
            if (val != null)
                container.getRegistry().bind(LookupNames.MAX_PLANE_WIDTH, Integer.parseInt(val));
            val = cs.getConfigValue("omero.pixeldata.max_plane_height");
            if (val != null)
                container.getRegistry().bind(LookupNames.MAX_PLANE_HEIGHT, Integer.parseInt(val));

            String checkname = name;
            if (name.startsWith("OMERO.")) {
                checkname = name.substring("OMERO.".length());
            }
            //Register insight
            UpgradeCheck check = new UpgradeCheck(cs.getConfigValue("omero.upgrades.url"), clientVersion, checkname);
            check.run();
        } catch (ServerError e2) {
            msg = new LogMessage("Could not access ConfigService", e2);
            registry.getLogger().warn(this, msg);
        }

        //Post an event to indicate that the user is connected.
        EventBus bus = container.getRegistry().getEventBus();
        bus.post(new ConnectedEvent());
        //Post an event to notify 
        compatible = true;
        //Register into log file.
        Map<String, String> info = ProxyUtil.collectOsInfoAndJavaVersion();
        msg = new LogMessage();
        msg.println("Server version: "+version);
        msg.println("Client version: "+clientVersion);
        Entry<String, String> entry;
        Iterator<Entry<String, String>> k = info.entrySet().iterator();
        while (k.hasNext()) {
            entry = k.next();
            msg.println(entry.getKey()+": "+entry.getValue());
        }
        registry.getLogger().info(this, msg);
        registry.bind(LookupNames.CURRENT_USER_DETAILS, exp);
        registry.bind(LookupNames.IMAGE_QUALITY_LEVEL, 
        		determineImageQuality(uc.getSpeedLevel()));

        try {
            // Load the omero client properties from the server
            List agents = (List) registry.lookup(LookupNames.AGENTS);
            Map<String, String> props = omeroGateway.getOmeroClientProperties(exp.getGroupId());
            for (String key : props.keySet()) {
                if (registry.lookup(key) == null)
                    registry.bind(key, props.get(key));

                Registry agentReg;
                for (Object agent : agents) {
                    agentReg = ((AgentInfo) agent).getRegistry();
                    if (agentReg != null && agentReg.lookup(key) == null)
                        agentReg.bind(key, props.get(key));
                }
            }
        } catch (DSAccessException e1) {
            msg = new LogMessage("Could not load omero client properties from the server", e1);
            registry.getLogger().warn(this, msg);
        }
        
        Collection<GroupData> groups;
        Set<GroupData> available;
        List<ExperimenterData> exps = new ArrayList<ExperimenterData>();
        String ldap = null;
        long gid = exp.getDefaultGroup().getId();
        SecurityContext ctx = new SecurityContext(gid);
        boolean canCreate = omeroGateway.canCreate(ctx);

        if (!canCreate && LookupNames.MASTER_IMPORTER.equals(name)) {
            registry.getUserNotifier().notifyError("Read-only server",
                    "Cannot import on a read-only server!");
            exitApplication(true, true);
        }

        try {
            GroupData defaultGroup = null;
        	registry.bind(LookupNames.CAN_CREATE, canCreate);
        	groups = omeroGateway.getAvailableGroups(ctx, exp);
        	registry.bind(LookupNames.SYSTEM_ROLES,
                    omeroGateway.getSystemRoles(ctx));
        	//Check if the current experimenter is an administrator 
        	Iterator<GroupData> i = groups.iterator();
        	GroupData g;
        	available = new HashSet<GroupData>();
        	while (i.hasNext()) {
        		g = i.next();
        		if (gid == g.getId()) defaultGroup = g;
        		if (!admin.isSecuritySystemGroup(g.getId())) {
        			available.add(g);
        		} else {
        			if (admin.isSecuritySystemGroup(g.getId(),
        			        GroupData.SYSTEM)) {
        				available.add(g);
        				uc.setAdministrator(true);
        			}
        		}
        	}
        	//to be on the safe side.
        	if (available.size() ==  0) {
        	    //group with loaded users.
        	    if (defaultGroup != null) available.add(defaultGroup);
        	    else available.add(exp.getDefaultGroup());
        	}
        	registry.bind(LookupNames.USER_GROUP_DETAILS, available);
        	List<Long> ids = new ArrayList<Long>();
        	i = available.iterator();
        	Set set;
        	Iterator j;
        	ExperimenterData e;
        	while (i.hasNext()) {
        		g = (GroupData) i.next();
        		set = g.getExperimenters();
        		j = set.iterator();
        		while (j.hasNext()) {
        			e = (ExperimenterData) j.next();
        			if (!ids.contains(e.getId())) {
        				ids.add(e.getId());
        				exps.add(e);
        			}
        		}
        	}
        	registry.bind(LookupNames.USERS_DETAILS, exps);
        	registry.bind(LookupNames.USER_ADMINISTRATOR, uc.isAdministrator());
        	
            try {
                List<String> privs = omeroGateway.getGateway()
                        .getAdminService(ctx).getEventContext().adminPrivileges;
                registry.bind(LookupNames.PRIV_FULL, omeroGateway.getGateway()
                        .getFacility(AdminFacility.class).isFullAdmin(ctx));
                registry.bind(
                        LookupNames.PRIV_EDIT_USER,
                        privs.contains(omero.model.enums.AdminPrivilegeModifyUser.value));
                registry.bind(
                        LookupNames.PRIV_EDIT_GROUP,
                        privs.contains(omero.model.enums.AdminPrivilegeModifyGroup.value));
                registry.bind(LookupNames.PRIV_GROUP_ADD, privs
                        .contains(omero.model.enums.AdminPrivilegeModifyGroupMembership.value));
                registry.bind(LookupNames.PRIV_MOVE_GROUP, privs
                        .contains(omero.model.enums.AdminPrivilegeChgrp.value));
                registry.bind(LookupNames.PRIV_UPLOAD_SCRIPT, privs
                        .contains(omero.model.enums.AdminPrivilegeWriteScriptRepo.value));
                registry.bind(LookupNames.PRIV_SUDO, privs
                        .contains(omero.model.enums.AdminPrivilegeSudo.value));
            } catch (Exception e1) {
                registry.bind(LookupNames.PRIV_FULL, false);
                registry.bind(LookupNames.PRIV_EDIT_USER, false);
                registry.bind(LookupNames.PRIV_EDIT_GROUP, false);
                registry.bind(LookupNames.PRIV_GROUP_ADD, false);
                registry.bind(LookupNames.PRIV_MOVE_GROUP, false);
                msg = new LogMessage("Could not retrieve admin privileges.", e1);
                registry.getLogger().warn(this, msg);
            }
        	
		} catch (DSAccessException e) {
			throw new DSOutOfServiceException("Cannot retrieve groups", e);
		} 
        //Bind user details to all agents' registry.
        List agents = (List) registry.lookup(LookupNames.AGENTS);
		Iterator kk = agents.iterator();
		AgentInfo agentInfo;
		Registry reg;
		Boolean b = (Boolean) registry.lookup(LookupNames.BINARY_AVAILABLE);
		String url = (String) registry.lookup(LookupNames.HELP_ON_LINE_SEARCH);
		while (kk.hasNext()) {
			agentInfo = (AgentInfo) kk.next();
			if (agentInfo.isActive()) {
				reg = agentInfo.getRegistry();
				reg.bind(LookupNames.CAN_CREATE, canCreate);
				reg.bind(LookupNames.CURRENT_USER_DETAILS, exp);
				reg.bind(LookupNames.USER_GROUP_DETAILS, available);
				reg.bind(LookupNames.USERS_DETAILS, exps);
				reg.bind(LookupNames.USER_ADMINISTRATOR, uc.isAdministrator());
				reg.bind(LookupNames.IMAGE_QUALITY_LEVEL, 
				        determineImageQuality(uc.getSpeedLevel()));
				reg.bind(LookupNames.BINARY_AVAILABLE, b);
				reg.bind(LookupNames.HELP_ON_LINE_SEARCH, url);
				
				reg.bind(LookupNames.PRIV_FULL, registry.lookup(LookupNames.PRIV_FULL));
				reg.bind(LookupNames.PRIV_EDIT_USER, registry.lookup(LookupNames.PRIV_EDIT_USER));
				reg.bind(LookupNames.PRIV_EDIT_GROUP, registry.lookup(LookupNames.PRIV_EDIT_GROUP));
				reg.bind(LookupNames.PRIV_MOVE_GROUP, registry.lookup(LookupNames.PRIV_MOVE_GROUP));
				reg.bind(LookupNames.PRIV_GROUP_ADD, registry.lookup(LookupNames.PRIV_GROUP_ADD));
				reg.bind(LookupNames.PRIV_SUDO, registry.lookup(LookupNames.PRIV_SUDO));
				reg.bind(LookupNames.PRIV_UPLOAD_SCRIPT, registry.lookup(LookupNames.PRIV_UPLOAD_SCRIPT));
				reg.bind(LookupNames.OFFLINE_IMPORT_ENABLED, registry.lookup(LookupNames.OFFLINE_IMPORT_ENABLED));
			}
		}
	}
	
	/**
	 * Tells whether the communication channel to <i>OMEDS</i> is currently
	 * connected.
	 * This means that we have established a connection and have successfully
	 * logged in.
	 * 
	 * @return	<code>true</code> if connected, <code>false</code> otherwise.
	 */
	public boolean isConnected() { return omeroGateway.isConnected(); }
	
	/**
	 * Returns <code>true</code> if the client and server are compatible,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean isCompatible() { return compatible; }
	
    /** 
     * Shuts down the connection.
     * 
     * @param ctx The security context.
     */
	public void shutdown(SecurityContext ctx)
    { 
        try {
            if (omeroGateway != null)
                omeroGateway.logout();
            DataServicesFactory.registry.getCacheService().clearAllCaches();
            PixelsServicesFactory.shutDownRenderingControls(container
                    .getRegistry());
        } catch (Exception e) {
            LogMessage msg = new LogMessage(
                    "Could not properly shutdown OMERO connection", e);
            container.getRegistry().getLogger().error(this, msg);
        }
        singleton = null;
        omeroGateway = null;
    }
	
	/** Shuts the services down and exits the application.
	 * 
	 * @param forceQuit Pass <code>true</code> to force i.e. do not check if
	 * 					the application can terminate,
	 * 					<code>false</code> otherwise.
	 * @param exit		Pass <code>true</code> to quit, <code>false</code> to
	 * 					only shut down the services.
	 */
	public void exitApplication(boolean forceQuit, boolean exit)
	{
		if (!forceQuit) {
			List<AgentInfo> agents = (List<AgentInfo>)
				registry.lookup(LookupNames.AGENTS);
			Iterator<AgentInfo> i = agents.iterator();
			AgentInfo agentInfo;
			Agent a;
			//Agents termination phase.
			i = agents.iterator();
			List<AgentInfo> notTerminated = new ArrayList<AgentInfo>();
			while (i.hasNext()) {
				agentInfo = i.next();
				if (agentInfo.isActive()) {
					a = agentInfo.getAgent();
					if (a.canTerminate()) {
						a.terminate();
					} else notTerminated.add(agentInfo);
				}
			}
			if (notTerminated.size() > 0) {
				i = notTerminated.iterator();
				StringBuffer buffer = new StringBuffer();
				while (i.hasNext()) {
					agentInfo = i.next();
					buffer.append(agentInfo.getName());
					buffer.append("\n");
				}
				String message = "The following components " +
				"could not be closed safely:\n"+buffer.toString()+"\n" +
				"Please check.";
				String title = "Exit Application";
				Environment env = (Environment) registry.lookup(LookupNames.ENV);
				if (env != null && env.isRunAsPlugin())
					title = "Exit Plugin";
				MessageBox box = new MessageBox(
						DataServicesFactory.registry.getTaskBar().getFrame(),
						title, message,
						IconManager.getInstance().getIcon(
								IconManager.INFORMATION_MESSAGE_48));
				box.setNoText("OK");
				box.setYesText("Force Quit");
				box.setSize(400, 250);
				if (!env.isRunAsPlugin() && 
						box.centerMsgBox() == MessageBox.NO_OPTION)
					return;
			}
		}
		shutdown(null);
		if (exit) {
			CacheServiceFactory.shutdown(container);
			container.exit();
		}
		singleton = null;
	}

	/**
	 * Remove the security group.
	 * 
	 * @param ctx The security context to handle.
	 * @throws Throwable Thrown if the connector cannot be closed.
	 */
	public void removeGroup(SecurityContext ctx)
		throws Exception
	{
		omeroGateway.removeGroup(ctx);
	}

	/**
	 * Checks if the rendering engines 
	 */
	public void checkServicesStatus()
	{
		PixelsServicesFactory.checkRenderingControls(container.getRegistry());
	}

}
