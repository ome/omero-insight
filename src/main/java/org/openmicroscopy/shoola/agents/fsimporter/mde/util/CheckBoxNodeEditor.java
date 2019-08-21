package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.CheckBoxNodeTagData;


public class CheckBoxNodeEditor extends JCheckBox implements TreeCellEditor{
	    public CheckBoxNodeEditor() {
	        super();
	        setOpaque(false);
	        setFocusable(false);
	        addActionListener(new ActionListener() {
	            @Override public void actionPerformed(ActionEvent e) {
	                stopCellEditing();
	            }
	        });
	    }
	    @Override public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
	        if (leaf && value instanceof DefaultMutableTreeNode) {
	            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
	            if (userObject instanceof CheckBoxNodeTagData) {
	                this.setSelected(((CheckBoxNodeTagData) userObject).isSelected());
	            } else {
	                this.setSelected(false);
	            }
	            this.setText(value.toString());
	        }
	        return this;
	    }
	    @Override public Object getCellEditorValue() {
	        return new CheckBoxNodeTagData(getText(), isSelected());
	    }
	    @Override public boolean isCellEditable(EventObject e) {
	        return e instanceof MouseEvent;
	    }
	    //Copied from AbstractCellEditor
	    //protected EventListenerList listenerList = new EventListenerList();
	    //protected transient ChangeEvent changeEvent;
	    @Override public boolean shouldSelectCell(EventObject anEvent) {
	        return true;
	    }
	    @Override public boolean stopCellEditing() {
	        fireEditingStopped();
	        return true;
	    }
	    @Override public void  cancelCellEditing() {
	        fireEditingCanceled();
	    }
	    @Override public void addCellEditorListener(CellEditorListener l) {
	        listenerList.add(CellEditorListener.class, l);
	    }
	    @Override public void removeCellEditorListener(CellEditorListener l) {
	        listenerList.remove(CellEditorListener.class, l);
	    }
	    public CellEditorListener[] getCellEditorListeners() {
	        return listenerList.getListeners(CellEditorListener.class);
	    }
	    protected void fireEditingStopped() {
	        // Guaranteed to return a non-null array
	        Object[] listeners = listenerList.getListenerList();
	        // Process the listeners last to first, notifying
	        // those that are interested in this event
	        for (int i = listeners.length - 2; i >= 0; i -= 2) {
	            if (listeners[i] == CellEditorListener.class) {
	                // Lazily create the event:
	                if (changeEvent == null) {
	                    changeEvent = new ChangeEvent(this);
	                }
	                ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
	            }
	        }
	    }
	    protected void fireEditingCanceled() {
	        // Guaranteed to return a non-null array
	        Object[] listeners = listenerList.getListenerList();
	        // Process the listeners last to first, notifying
	        // those that are interested in this event
	        for (int i = listeners.length - 2; i >= 0; i -= 2) {
	            if (listeners[i] == CellEditorListener.class) {
	                // Lazily create the event:
	                if (changeEvent == null) {
	                    changeEvent = new ChangeEvent(this);
	                }
	                ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
	            }
	        }
	    }
}
