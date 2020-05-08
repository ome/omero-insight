/*
 * Copyright (C) <2019> University of Dundee & Open Microscopy Environment.
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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for MDE object selection to specify objects that should be saved/loaded.
 * Part of save/load template functionality.
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class TypeFilter_GUI extends JDialog implements ActionListener {

    private JPanel panel_chBoxes;
    private JButton btn_OK;
    private JButton btn_cancel;
    private JCheckBox btn_select_all;
    private List<String > filterList;

    /**
     * Generates selection dialog with mde objects available in given tree.
     * @param parent pane
     * @param root mde objecttree
     */
    public TypeFilter_GUI(JFrame parent,DefaultMutableTreeNode root){
        super(parent,"Filter For Object Type");
        filterList=new ArrayList<>();
        panel_chBoxes=new JPanel(new GridLayout(0, 1));
        Border border = BorderFactory.createTitledBorder("Object Types");
        panel_chBoxes.setBorder(border);
        generateListFromTree(root);
        revalidate();
        repaint();

        build_GUI();
        pack();
        setVisible(true);
    }

    /**
     * Generates selection dialog with mde objects available in given file.
     * @param parent pane
     * @param path template file
     */
    public TypeFilter_GUI(JFrame parent,String path){
        super(parent,"Filter For Object Type");
        filterList=new ArrayList<>();
        panel_chBoxes=new JPanel(new GridLayout(0, 1));
        Border border = BorderFactory.createTitledBorder("Object Types");
        panel_chBoxes.setBorder(border);
        generateListFromFile(path);
        revalidate();
        repaint();

        build_GUI();
        pack();
        setVisible(true);
    }

    private void build_GUI(){
        btn_OK = new JButton("OK");
        btn_OK.addActionListener(this);
        btn_cancel = new JButton("Cancel");
        btn_cancel.addActionListener(this);
        Box btnPane=Box.createHorizontalBox();
        btnPane.add(btn_cancel);
        btnPane.add(Box.createHorizontalStrut(5));
        btnPane.add(btn_OK);

        btn_select_all=new JCheckBox("Select all");
        btn_select_all.setSelected(true);
        btn_select_all.addActionListener(this);

        setBounds(100, 100, 500, 600);
        getContentPane().setLayout(new BorderLayout(5,5));
        setModal(true);
        getContentPane().add(btn_select_all,BorderLayout.NORTH);
        getContentPane().add(panel_chBoxes,BorderLayout.CENTER);
        getContentPane().add(btnPane,BorderLayout.SOUTH);
    }

    /**
     * Read out mde objects from file, added objects to filterList and creates
     * object checkboxes
     * @param path template file
     */
    private void generateListFromFile(String path) {
        ImportFromTemplateFile importer=new ImportFromTemplateFile(path);
        List<String> list=importer.createTypeList();
        if(list !=null) {
            for (String type : list) {
                JCheckBox ch = new JCheckBox(type);
                ch.setSelected(true);
                ch.addActionListener(this);
                panel_chBoxes.add(ch);
                filterList.add(type);
            }
        }
    }

    /**
     * Read out mde objects from tree, added objects to filterList and creates
     * object checkboxes
     * @param root mde objecttree
     */
    private void generateListFromTree(DefaultMutableTreeNode root) {
        List<String> list = MDEHelper.getTypes(root);
        if(list !=null) {
            for (String type : list) {
                JCheckBox ch = new JCheckBox(type);
                ch.setSelected(true);
                ch.addActionListener(this);
                panel_chBoxes.add(ch);
                filterList.add(type);
            }
        }
    }

    /**
     * @return list of strings of object types
     */
    public List<String> getTypeFilter(){
        return filterList;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn_OK){
            setVisible(false);
            dispose();
        }else if(e.getSource()== btn_cancel) {
            filterList=null;
            setVisible(false);
            dispose();
        }else if(e.getSource() instanceof JCheckBox){
            if(e.getSource()==btn_select_all){
                selectAll(btn_select_all.isSelected());
            }else if(((JCheckBox)e.getSource()).isSelected()) {
                filterList.add(((JCheckBox) e.getSource()).getText());
            }else if(!((JCheckBox)e.getSource()).isSelected()){
                filterList.remove(((JCheckBox) e.getSource()).getText());
            }

        }
    }

    private void selectAll(boolean b) {
        for(Component c:panel_chBoxes.getComponents()){
            if(b && !((JCheckBox)c).isSelected()){
                filterList.add(((JCheckBox)c).getText());
            }
            ((JCheckBox)c).setSelected(b);
        }
        if(!b){
            filterList=new ArrayList<>();
        }
    }
}
