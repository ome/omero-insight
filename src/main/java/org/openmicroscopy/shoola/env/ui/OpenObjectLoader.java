/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2018 University of Dundee. All rights reserved.
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

import org.openmicroscopy.shoola.env.config.Registry;

import omero.gateway.SecurityContext;

import org.openmicroscopy.shoola.env.data.views.CallHandle;
import org.openmicroscopy.shoola.util.filter.file.OMETIFFFilter;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import omero.gateway.model.DataObject;
import omero.gateway.model.FileAnnotationData;
import omero.gateway.model.ImageData;

/** 
 * Downloads if the passed object is a file or an archived image
 * or exports as OME-TIFF if it is non-archived image.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * @since 3.0-Beta4
 */
public class OpenObjectLoader 
	extends UserNotifierLoader
{

	/** Handle to the asynchronous call so that we can cancel it. */
    private CallHandle  			handle;
    
    /** The object to handle. */
    private DataObject 				object;

    /** The file where to export the object. */
    private String					folderPath;

    private boolean originalImage;

    /**
     * Creates a new instance.
     * 
     * @param viewer    The viewer this data loader is for.
     *                  Mustn't be <code>null</code>.
     * @param registry  Convenience reference for subclasses.
     * @param ctx The security context.
     * @param object    The object to handle.
     * @param folderPath The folder where to copy locally the object.
     * @param originalImage Download the original image if <code>True</code>
     *                     otherwise export as OME-TIFF.
     * @param activity  The activity associated to this loader.
     */
    public OpenObjectLoader(UserNotifier viewer, Registry registry,
            SecurityContext ctx, DataObject object, String folderPath,
            boolean originalImage, ActivityComponent activity)
    {
        super(viewer, registry, ctx, activity);
        if (object == null)
            throw new IllegalArgumentException("Object not valid.");
        if (!(object instanceof ImageData || 
                object instanceof FileAnnotationData))
            throw new IllegalArgumentException("Object not valid.");
        this.object = object;
        this.folderPath = folderPath;
        this.originalImage = originalImage;
    }

    /**
     * Creates a new instance.
     * 
     * @param viewer	The viewer this data loader is for.
     *               	Mustn't be <code>null</code>.
     * @param registry	Convenience reference for subclasses.
     * @param ctx The security context.
     * @param object	The object to handle.
     * @param folderPath The folder where to copy locally the object.
     * @param activity 	The activity associated to this loader.
     */
	public OpenObjectLoader(UserNotifier viewer,  Registry registry,
			SecurityContext ctx, DataObject object, String folderPath,
			ActivityComponent activity)
	{
		this(viewer, registry, ctx, object, folderPath, false, activity);
	}
	
	/**
     * Downloads or exports the object.
     * @see UserNotifierLoader#load()
     */
    public void load()
    {
    	String path = folderPath+File.separator;
    	File f;
    	if (object instanceof ImageData) {
    		ImageData image = (ImageData) object;
    		if (originalImage) {
    		    List<DataObject> objects = new ArrayList<DataObject>();
    		    objects.add(image);
    		    f = new File(folderPath);
    		    handle = mhView.loadArchivedImage(ctx, objects, f, false, false,
    	                 this);
    		} else {
    		    String name = image.getName();
                name += image.getName();
                name += "_"+image.getId();
                path += UIUtilities.replaceNonWordCharacters(name)+"."+OMETIFFFilter.OME_TIFF;
                f = new File(path);
                f.deleteOnExit();
                handle = ivView.exportImageAsOMETiff(ctx, image.getId(), f, null,
                        this); 
    		}
    	} else {
    	    //Make sure originalImage is set to false in that case
    	    originalImage = false;
    		FileAnnotationData fa = (FileAnnotationData) object;
    		path += UIUtilities.replaceNonWordCharacters(fa.getFileName());
    		f = new File(path);
    		f.deleteOnExit();
    		handle = mhView.loadFile(ctx, f, fa.getId(), 
    				FileLoader.FILE_ANNOTATION, this);
    	}
    }
    
    /**
     * Notifies the user that it wasn't possible to retrieve the file.
     * @see UserNotifierLoader#handleNullResult()
     */
    public void handleNullResult() 
    {
    	if (activity != null && object instanceof FileAnnotationData) {
    		activity.notifyError("File no longer exists", 
    				"The file you wish to open no longer exists.", null);
    	}
    }
    
    @Override
    protected void onException(String message, Throwable ex) {
        super.onException(message, ex);
        activity.notifyError("Error- Could not load object", ex.getMessage(), ex);
    }

    /**
     * Cancels the ongoing data retrieval.
     * @see UserNotifierLoader#cancel()
     */
    public void cancel() { handle.cancel(); }
 
    /** 
     * Feeds the result back to the viewer. 
     * @see UserNotifierLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    { 
    	if (result == null) onException(MESSAGE_RESULT, null);
    	else {
    	    if (originalImage) {
    	        Map<Boolean, List<File>> r = (Map<Boolean, List<File>>) result;
    	        List<File> files = r.get(true);
    	        if (files.size() > 0) {
    	            Iterator<File> i = files.iterator();
    	            while (i.hasNext()) {
                        i.next().deleteOnExit();
                    }
    	            activity.endActivity(files.get(0));
    	        } else {
    	            onException(MESSAGE_RESULT, null);
    	        }
    	    } else {
    	        activity.endActivity(result);
    	    }
    	}
    }
    
}
