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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util.inout;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleTreeElement;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Export user input as key,value to csv file.
 * 7/2/2020
 * @author Susanne Kunis<susannekunis at gmail dot com>
 **/
@Deprecated
public class ExportAsCsv {
    String fName;

    public ExportAsCsv(String fileName){
        this.fName=fileName;
    }

    /**
     * Convert input at given tree to list of String arrays [key,values] and write the list to csv file.
     * @param tree
     * @param addPath true: use as key whole path to property (object#object|tagname)
     * @param addUnitToKey true: add to generated key the tag unit in brackets : key (unit) : value
     * @throws IOException
     */
    public void export(DefaultMutableTreeNode tree, boolean addPath,boolean addUnitToKey) throws IOException {
        if(tree == null) {
            return;
        }
        HashMap<String,List<TagData>> input= MDEHelper.getInput(tree);
        List<String[]> data=convertData(input,addPath,addUnitToKey);
        createFile(data);
    }

    public void exportAll(DefaultMutableTreeNode tree, boolean addPath,boolean addUnitToKey) throws IOException {
        if (tree == null) {
            return;
        }
        HashMap<String, List<TagData>> input = MDEHelper.getAllData(tree);
        List<String[]> data=convertData(input,addPath,addUnitToKey);
        createFile(data);
    }

    /**
     * read out every not empty property to a list of String arrays [key,value]
     * @param input hashmap of parentid:tagList
     * @param addPath true: use as key whole path to property (object#object|tagname)
     * @param addUnitToKey true: add to generated key unit in brackets : key (unit) : value
     * @return
     */
    private List<String[]> convertData(HashMap<String, List<TagData>> input, boolean addPath, boolean addUnitToKey) {
        List<String[]> data = new ArrayList<>();
        for(Map.Entry<String, List<TagData>> entry: input.entrySet()) {
            if(entry.getValue()!=null) {
                for(TagData t:entry.getValue()) {
                    if(t.getTagValue()!=null && !t.getTagValue().trim().isEmpty()) {
                        String key = entry.getKey() + " | " + t.getTagName();
                        String value = t.getTagWholeValue();
                        if (!addPath) {
                            key = t.getTagName();
                        }
                        if(addUnitToKey && (t.getTagUnitString()!=null && !t.getTagUnitString().isEmpty())){
                            key = key + " ("+t.getTagUnitString()+")";
                            value = t.getTagValue();
                        }
                        data.add(new String[]{key,value});
                    }
                }
            }
        }

        return data;
    }

    /**
     * Create csv file and writes key,value
     * @param data list of string arrays [key,value]
     * @throws IOException
     */
    private void createFile(List<String[]> data) throws IOException {
        if(data!=null) {
            File csvOutputFile = new File(fName);
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                data.stream()
                        .map(this::convertToCSVString)
                        .forEach(pw::println);
            }
        }

    }
    private String convertToCSVString(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }
}