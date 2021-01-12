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

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 1/11/2021
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *     see: https://github.com/aterai/java-swing-tips/blob/master/CheckedComboBox/src/java/example/MainPanel.java
 **/
public class CheckedComboBox<E extends CheckableItem> extends JComboBox<E> {

    private static String ITEM_DELIMETER=";";
    private boolean keepOpen;
    private transient ActionListener listener;

    protected CheckedComboBox() {
        super();
    }

    protected CheckedComboBox(ComboBoxModel<E> model) {
        super(model);
    }

    protected CheckedComboBox(String[] values){
        super();
        CheckableItem[] m = new CheckableItem[values.length];
        for(int i=0;i<values.length; i++) {
            m[i]=new CheckableItem(values[i],false);
        }
        setModel(new DefaultComboBoxModel(m));
    }

    public void init_selectedValues(String[] values){
        if(values==null || values.length==0)
            return;
        List<String> t;
        if(values.length==1)
            t=Arrays.asList(values[0].split(ITEM_DELIMETER+" "));
        else
            t=Arrays.asList(values);

        for (int i = 0; i < getItemCount(); i++) {
            if(t.contains(getItemAt(i).toString())){
                getItemAt(i).setSelected(true);
            }
        }
    }


    @Override
    public void updateUI() {
        setRenderer(null);
        removeActionListener(listener);
        super.updateUI();
        listener = e -> {
            if ((e.getModifiers() & AWTEvent.MOUSE_EVENT_MASK) != 0) {
                updateItem(getSelectedIndex());
                keepOpen = true;
            }
        };
        JLabel label = new JLabel(" ");
        JCheckBox check = new JCheckBox(" ");
        setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (index < 0) {
                String txt = getCheckedItemString(list.getModel());
                label.setText(txt.isEmpty() ? " " : txt);
                return label;
            } else {
                check.setText(Objects.toString(value, ""));
                check.setSelected(value.isSelected());
                if (isSelected) {
                    check.setBackground(list.getSelectionBackground());
                    check.setForeground(list.getSelectionForeground());
                } else {
                    check.setBackground(list.getBackground());
                    check.setForeground(list.getForeground());
                }
                return check;
            }
        });
        addActionListener(listener);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "checkbox-select");
        getActionMap().put("checkbox-select", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Accessible a = getAccessibleContext().getAccessibleChild(0);
                if (a instanceof ComboPopup) {
                    updateItem(((ComboPopup) a).getList().getSelectedIndex());
                }
            }
        });
    }
    private static <E extends CheckableItem> String getCheckedItemString(ListModel<E> model) {
       return IntStream.range(0, model.getSize())
                .mapToObj(model::getElementAt)
                .filter(CheckableItem::isSelected)
                .map(Objects::toString)
                .sorted()
                .collect(Collectors.joining(ITEM_DELIMETER+" "));
    }

    protected void updateItem(int index) {
        if (isPopupVisible()) {
            E item = getItemAt(index);
            item.setSelected(!item.isSelected());
            setSelectedIndex(-1);
            setSelectedItem(item);
        }
    }

    @Override public void setPopupVisible(boolean v) {
        if (keepOpen) {
            keepOpen = false;
        } else {
            super.setPopupVisible(v);
        }
    }


    public String[] getSelectedItemsAsArray() {
        return getCheckedItemString(getModel()).split(ITEM_DELIMETER);
    }
    public String getSelectedItemsAsString() {
        return getCheckedItemString(getModel());
    }


}
