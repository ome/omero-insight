/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2016 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.env.ui;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.openmicroscopy.shoola.env.config.Registry;
import omero.gateway.SecurityContext;
import org.openmicroscopy.shoola.env.data.views.CallHandle;
import omero.gateway.model.DataObject;

/** 
 * Loads the image.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * @since 3.0-Beta4
 */
public class ArchivedLoader 
	extends UserNotifierLoader
{

    /** Handle to the asynchronous call so that we can cancel it. */
    private CallHandle handle;

    /** The archived images to load. */
    private List<DataObject> objects;

    /** The file where to download the content of the image. */
    private File file;

    /** Flag indicating that the export has been marked to be cancel.*/
    private boolean cancelled;

    /** Flag indicating to override or not the files when saving.*/
    private boolean override;

    /** Flag for zipping the downloaded images */
    private boolean zip = false;
    
    /**
     * Notifies that an error occurred.
     * @see UserNotifierLoader#onException(String, Throwable)
     */
    protected void onException(String message, Throwable ex)
    { 
    	activity.notifyError("Unable to download the image", 
				message, ex);
    }
    
	/**
     * Creates a new instance.
     * 
     * @param viewer The viewer this data loader is for.
     *               Mustn't be <code>null</code>.
     * @param registry Convenience reference for subclasses.
     * @param ctx The security context.
     * @param objects The objects to export.
     * @param file The location where to download the image.
     * @param override Flag indicating to override the existing file if it
     *                 exists, <code>false</code> otherwise.
     * @param zip Pass <code>true</code> to create a zip file
     * @param activity The activity associated to this loader.
     */
	public ArchivedLoader(UserNotifier viewer, Registry registry,
			SecurityContext ctx, List<DataObject> objects, File file,
			boolean override, boolean zip, ActivityComponent activity)
	{
		super(viewer, registry, ctx, activity);
		if (objects == null)
			throw new IllegalArgumentException("No objects provided.");
		this.objects = objects;
		this.file = file;
		this.override = override;
		this.zip = zip;
	}
    
    /**
     * Downloads the archived image.
     * 
     * @see UserNotifierLoader#load()
     */
    public void load() {
        if (CollectionUtils.isEmpty(objects))
            return;

        handle = mhView.loadArchivedImage(ctx, objects, file, override, zip, this);
    }

	/**
	 * Cancels the ongoing data retrieval.
	 * @see UserNotifierLoader#cancel()
	 */
	public void cancel()
	{
	    cancelled = true;
	    if (handle != null) handle.cancel();
	}

	/**
	 * Notifies the user that no archived images were found.
	 * @see UserNotifierLoader#handleNullResult()
	 */
	public void handleNullResult()
	{
	    activity.endActivity(new ArrayList<File>());
	}

    /** 
     * Feeds the result back to the viewer. 
     * @see UserNotifierLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    {
        if (result == null && !cancelled) handleNullResult();
        else {
            Map m = (Map) result;
            List l = (List) m.get(Boolean.valueOf(false));
            if (!CollectionUtils.isEmpty(l)) {
                if (!cancelled)
                    onException("Missing "+l.size()+" file(s) composing the image",
                            null);
            } else {
                List<File> files = (List<File>) m.get(Boolean.valueOf(true));
                if (cancelled) {
                    Iterator<File> i = files.iterator();
                    File f;
                    while (i.hasNext()) {
                        f = i.next();
                        if (f.isDirectory()) {
                            try {
                                FileUtils.deleteDirectory(f);
                            } catch (Exception e) {
                                registry.getLogger().error(this,
                                        "Cannot delete the directory");
                            }
                        } else f.delete();
                    }
                } else {
                    activity.endActivity(files);
                }
            }
        }
    }

}
