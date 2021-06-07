/*
 * Copyright (C) <2016-2019> University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout.ExportToTextFormat;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout.TypeFilter_GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Dialog to export form input to file.
 * The dialog allows you to specify the target directory and file name of export file as well as configurations
 * regarding the structure of the key for the key-value pairs.
 *
 * 7/2/2020
 * @author Susanne Kunis<susannekunis at gmail dot com>
 **/
public class ExportDialog extends JDialog implements ActionListener {

    private JButton btn_OK;
    private JButton btn_cancel;
    private JButton btn_browse_save;
    private JTextField txt_path;
    private JCheckBox ch_addPath;
    private JCheckBox ch_addUnitToKey;
    private JCheckBox ch_exportAllData;
    private JRadioButton ch_tsv;
    private JRadioButton ch_csv;
    private JRadioButton ch_txt;
    private JRadioButton ch_idr;


    private DefaultMutableTreeNode tree;
    private boolean cancel;

    private Boolean appendToFile;

    private File exportfile;

    public ExportDialog(JFrame parent){
        super(parent,"Export to file");
        this.exportfile=null;
        cancel=false;
        buildGUI();
        pack();
        setVisible(true);
    }

    private void buildGUI() {
        setBounds(100, 100, 500, 600);
        getContentPane().setLayout(new BorderLayout(5,5));
        setModal(true);

        btn_OK = new JButton("OK");
        btn_OK.addActionListener(this);
        btn_cancel = new JButton("Cancel");
        btn_cancel.addActionListener(this);
        Box btnPane=Box.createHorizontalBox();
        btnPane.add(btn_cancel);
        btnPane.add(Box.createHorizontalStrut(5));
        btnPane.add(btn_OK);


        /** configuration panel **/
        JPanel subPanel= new JPanel();
        Border titleBorder = BorderFactory.createTitledBorder("Configuration:");
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBorder(titleBorder);
        ch_addPath = new JCheckBox("use tree path as addition to key");
        ch_addPath.setToolTipText("Set pattern of key: <objectName>#..#<objectName>|<propertyName>");
        ch_addPath.setSelected(true);
        subPanel.add(ch_addPath);
        ch_addUnitToKey=new JCheckBox("add unit to key");
        ch_addUnitToKey.setSelected(false);
        subPanel.add(ch_addUnitToKey);
        ch_exportAllData=new JCheckBox("Export all metadata");
        ch_exportAllData.setSelected(false);
        ch_exportAllData.setToolTipText("Export also metadata that available in the image container");
        subPanel.add(ch_exportAllData);

        /** configuration format */
        JPanel subPanel_format= new JPanel();
        Border titleBorder_format = BorderFactory.createTitledBorder("Format:");
        subPanel_format.setLayout(new BoxLayout(subPanel_format, BoxLayout.Y_AXIS));
        subPanel_format.setBorder(titleBorder_format);
        ch_tsv = new JRadioButton("text format seperated by tabs");
        ch_tsv.setSelected(true);
        ch_csv = new JRadioButton("text format seperated by commas");
        ch_csv.setSelected(false);
        ch_txt = new JRadioButton("text format seperated by spaces");
        ch_txt.setSelected(false);
        ch_idr = new JRadioButton("IDR submission format");
        ch_idr.setSelected(false);

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(ch_tsv);
        btnGroup.add(ch_csv);
        btnGroup.add(ch_txt);
        btnGroup.add(ch_idr);

        subPanel_format.add(ch_tsv);
        subPanel_format.add(ch_csv);
        subPanel_format.add(ch_txt);
        subPanel_format.add(ch_idr);


        subPanel.add(subPanel_format);


        /** main panel**/
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new BorderLayout(5,5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.add(subPanel,BorderLayout.CENTER);


        JLabel destPath_Lbl=new JLabel("Destination");
        txt_path =new JTextField(50);
        txt_path.setEditable(false);
        txt_path.setToolTipText("Destination to store file");
        if(exportfile!=null)
            txt_path.setText(exportfile.getAbsolutePath());
        btn_browse_save =new JButton("Browse");
        btn_browse_save.addActionListener(this);
        JPanel destP=new JPanel();
        destP.add(destPath_Lbl);
        destP.add(txt_path);
        destP.add(btn_browse_save);

        mainPanel.add(destP,BorderLayout.SOUTH);

        getContentPane().add(mainPanel,BorderLayout.CENTER);
        getContentPane().add(btnPane,BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn_OK){
            this.appendToFile=getWriteMode(exportfile);
            if(this.appendToFile==null){
                return;
            }
            setVisible(false);
            dispose();
        }else if(e.getSource()== btn_cancel) {
            exportfile=null;
            cancel =true;
            setVisible(false);
            dispose();
        }else if(e.getSource()== btn_browse_save) {
            String formatExtFileName = getFormatExtensionName();
            JFileChooser fcSave =new JFileChooser();
            fcSave.setSelectedFile(new File(fcSave.getCurrentDirectory(), formatExtFileName));
            if(exportfile!=null)
                fcSave.setCurrentDirectory(new File(exportfile.getParent()));
            int returnValSave=fcSave.showSaveDialog(this);
            if(returnValSave==JFileChooser.APPROVE_OPTION) {
                exportfile=fcSave.getSelectedFile();
                txt_path.setText(exportfile.getAbsolutePath());
            }
        }
    }

    private Boolean getWriteMode(File outputFile) {
        Object[] options = {"Overwrite file",
                "Append to file",
                "Cancel"};
        if (outputFile.exists()) {
            int result = JOptionPane.showOptionDialog(
                    btn_browse_save.getParent(),
                    "File exists!", "File exists",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            switch (result) {
                case 1:
                   return true;
                case 2:
                    return null;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * @return a suggestion for a filename with extensions depending on the selected delimeter (default: metadata_export_mde.txt)
     */
    private String getFormatExtensionName() {
        if(ch_csv.isSelected()) return "metadata_export_mde.csv";
        if(ch_tsv.isSelected()) return "metadata_export_mde.tsv";
        if(ch_txt.isSelected()) return "metadata_export_mde.txt";
        if(ch_idr.isSelected()) return "idr_study.txt";

        return "metadata_export_mde.txt";
    }

    public File getDestination()
    {
        return exportfile;
    }

    public boolean addPath() {
        //if(getFormatMode()==ExportToTextFormat.MODE_IDR)
         //   return false;
        return ch_addPath.isSelected();

    }
    public boolean addUnitToKey(){
        return ch_addUnitToKey.isSelected();
    }
    public boolean exportAll(){
        return ch_exportAllData.isSelected();
    }

    /**
     * @return selected delimeter (default: space)
     */
    public String getDelimeter(){
        if(ch_csv.isSelected()) return ",";
        if(ch_tsv.isSelected()) return "\t";
        if(ch_txt.isSelected()) return " ";
        if(ch_idr.isSelected()) return "\t";


        return " ";
    }
    public int getFormatMode(){
        if(ch_idr.isSelected()) return ExportToTextFormat.MODE_IDR;

        return ExportToTextFormat.MODE_DEFAULT;
    }

    public boolean getWritingMode(){
        if(appendToFile==null)
            return false;
        return appendToFile.booleanValue();
    }
    public Boolean isCancelled(){return cancel;}
}
