/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2018-2022 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.agents.fsimporter.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import omero.gateway.model.TagAnnotationData;

import org.jdesktop.swingx.JXBusyLabel;
import org.openmicroscopy.shoola.agents.fsimporter.IconManager;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.util.FileImportComponentI;
import org.openmicroscopy.shoola.agents.fsimporter.util.LightFileImportComponent;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.env.data.model.ImportableObject;
import org.openmicroscopy.shoola.env.data.util.Status;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/**
 * A lightweight version of the ImporterUIElement.
 * 
 * @author Domink Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
class ImporterUIElementLight extends ImporterUIElement {

    private JProgressBar upload = new JProgressBar(SwingConstants.HORIZONTAL);
    private JProgressBar processed = new JProgressBar(SwingConstants.HORIZONTAL);
    
    private JXBusyLabel scanningBusy = new JXBusyLabel();
    private JXBusyLabel uploadBusy = new JXBusyLabel();
    private JXBusyLabel processedBusy = new JXBusyLabel();

    private JLabel errors = new JLabel("0");
    
    private JLabel cancelled = new JLabel("0");
    private JLabel cancelledLabel = new JLabel("Cancelled:");
    
    private Boolean isScanning = null;

    @Override
    FileImportComponentI buildComponent(ImportableFile importable,
            boolean browsable, boolean singleGroup, int index,
            Collection<TagAnnotationData> tags) {
        return new LightFileImportComponent(importable,
                getID(), object.getTags());
    }

    /**
     * Creates a new instance.
     * 
     * @param controller
     *            Reference to the control. Mustn't be <code>null</code>.
     * @param model
     *            Reference to the model. Mustn't be <code>null</code>.
     * @param view
     *            Reference to the model. Mustn't be <code>null</code>.
     * @param id
     *            The identifier of the component.
     * @param index
     *            The index of the component.
     * @param name
     *            The name of the component.
     * @param object
     *            the object to handle. Mustn't be <code>null</code>.
     */
    ImporterUIElementLight(ImporterControl controller, ImporterModel model,
            ImporterUI view, int id, int index, String name,
            ImportableObject object) {
        super(controller, model, view, id, index, name, object);
        buildGUI();
    }

    /** Builds and lays out the UI. */
    private void buildGUI() {
        
        // init
        scanningBusy.setBusy(false);
        
        upload.setBorderPainted(true);
        upload.setMinimum(0);
        upload.setStringPainted(false);
        
        processed.setBorderPainted(true);
        processed.setMinimum(0);
        processed.setStringPainted(false);
        
        uploadBusy.setBusy(false);
        processedBusy.setBusy(false);
        
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setBorder(new LineBorder(Color.LIGHT_GRAY));
        info.setBackground(UIUtilities.BACKGROUND);

        info.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        info.add(new JLabel("Scanning:"), c);

        c.gridx = 2;
        c.fill = GridBagConstraints.NONE;
        info.add(scanningBusy, c);
        
        c.gridy++;
        
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        info.add(new JLabel("Uploaded:"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        info.add(upload, c);

        c.gridx = 2;
        c.fill = GridBagConstraints.NONE;
        info.add(uploadBusy, c);
        
        c.gridy++;
        
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        info.add(new JLabel("Processed:"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        info.add(processed, c);

        c.gridx = 2;
        c.fill = GridBagConstraints.NONE;
        info.add(processedBusy, c);
        
        c.gridy++;

        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        cancelledLabel.setVisible(false);
        info.add(cancelledLabel, c);
        
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        cancelled.setVisible(false);
        info.add(cancelled, c);
        
        c.gridy++;
        
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        info.add(new JLabel("Errors:"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        info.add(errors, c);

        add(info, BorderLayout.CENTER);
    }

    // Prevent GUI updates queueing up and freezing
    AtomicBoolean updateRequest = new AtomicBoolean(false);

    @Override
    void setNumberOfImport() {
        if (!updateRequest.get()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ImporterUIElementLight.super.setNumberOfImport();
                    updateRequest.set(true);
                    updateGUI();
                }
            });
        }
    }

    private void updateGUI() {
        updateRequest.set(false);

        upload.setValue(super.countUploaded);
        upload.setMaximum(super.totalToImport);
        uploadBusy.setText(+super.countUploaded + "/" + super.totalToImport);

        processed.setValue(super.countImported);
        processed.setMaximum(super.countUploaded);
        processedBusy.setText(super.countImported + "/" + super.countUploaded);

        if (super.isDone()) {
            uploadBusy.setBusy(false);
            uploadBusy.setIcon(IconManager.getInstance().getIcon(IconManager.APPLY));
            processedBusy.setBusy(false);
            processedBusy.setIcon(IconManager.getInstance().getIcon(IconManager.APPLY));

            int cancelled = super.cancelled();
            if (cancelled > 0) {
                if (!this.cancelledLabel.isVisible())
                    this.cancelledLabel.setVisible(true);
                if (!this.cancelled.getText().equals(""+cancelled))
                    this.cancelled.setText(""+cancelled);
                if (!this.cancelled.isVisible())
                    this.cancelled.setVisible(true);
            }
        } else {
            if (!uploadBusy.isBusy())
                uploadBusy.setBusy(true);
            if (!processedBusy.isBusy())
                processedBusy.setBusy(true);
        }

        if (scanningBusy.isBusy() && super.countUploaded > 0) {
            scanningBusy.setBusy(false);
            scanningBusy.setIcon(IconManager.getInstance().getIcon(IconManager.APPLY));
        }

        if (super.countFailure > 0) {
            if (!super.filterButton.isEnabled())
                super.filterButton.setEnabled(true);
            if (!errors.getText().equals("" + super.countFailure))
                errors.setText("" + super.countFailure);
        }

        if (updateRequest.get())
            updateGUI();
    }

    @Override
    void showFailures() {
        Collection<FileImportComponentI> components = getMarkedFiles();
        if (components != null && components.size() > 0) {
            JFrame f = ImporterAgent.getRegistry().getTaskBar().getFrame();
            FailedImportDialog d = new FailedImportDialog(f, components);
            UIUtilities.centerAndShow(d);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        if (isScanning == null) {
            SwingUtilities.invokeLater(() -> {
                scanningBusy.setBusy(true);
            });
        }
    }
}
