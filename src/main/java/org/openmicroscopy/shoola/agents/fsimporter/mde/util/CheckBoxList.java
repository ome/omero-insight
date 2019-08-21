package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;


public class CheckBoxList extends JList
{
	private JList<CheckboxListItem> list;
	private List<String> elementList;
	
	public CheckBoxList(List<String> elements)
	{
		// Create a list containing CheckboxListItem's
		 
		
//	  list = new JList<CheckboxListItem>(
//	            new CheckboxListItem[] { new CheckboxListItem("apple"),
//	                  new CheckboxListItem("orange"),
//	                  new CheckboxListItem("mango"),
//	                  new CheckboxListItem("paw paw"),
//	                  new CheckboxListItem("banana") });
		
		
		
		DefaultListModel model = new DefaultListModel();

		for(int i=0; i<elements.size();i++)
			model.addElement(elements.get(i));
		
		list=new JList<CheckboxListItem>(model);
	 
	 
	      // Use a CheckboxListRenderer (see below)
	      // to renderer list cells
	      setCellRenderer(new CheckboxListRenderer());
	      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	 
	      // Add a mouse listener to handle changing selection
	      addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent event) {
	            JList<CheckboxListItem> list =
	               (JList<CheckboxListItem>) event.getSource();
	 
	            // Get index of item clicked
	            int index = list.locationToIndex(event.getPoint());
	            CheckboxListItem item = (CheckboxListItem) list.getModel()
	                  .getElementAt(index);
	 
	            // Toggle selected state
	            item.setSelected(!item.isSelected());
	            
	            // mark element
	            if(!item.isSelected()){
	            	elementList.set(index, "");
	            }else{
	            	elementList.set(index,item.label);
	            }
	 
	            // Repaint cell
	            list.repaint(list.getCellBounds(index, index));
	         }
	      });
	}
	
	public JList getJList()
	{
		return list;
	}
	
	/**
	 * 
	 * @return elementList with "" for deselected elements
	 */
	public List<String> getList() {
		return elementList;
	}
	
	
	// Represents items in the list that can be selected
	 
	class CheckboxListItem {
	   private String label;
	   private boolean isSelected = false;
	 
	   public CheckboxListItem(String label) {
	      this.label = label;
	   }
	 
	   public boolean isSelected() {
	      return isSelected;
	   }
	 
	   public void setSelected(boolean isSelected) {
	      this.isSelected = isSelected;
	   }
	 
	   public String toString() {
	      return label;
	   }
	}
	 
	// Handles rendering cells in the list using a check box
	 
	class CheckboxListRenderer extends JCheckBox implements
	      ListCellRenderer<CheckboxListItem> {
	 
	   @Override
	   public Component getListCellRendererComponent(
	         JList<? extends CheckboxListItem> list, CheckboxListItem value,
	         int index, boolean isSelected, boolean cellHasFocus) {
	      setEnabled(list.isEnabled());
	      setSelected(value.isSelected());
	      setFont(list.getFont());
	      setBackground(list.getBackground());
	      setForeground(list.getForeground());
	      setText(value.toString());
	      return this;
	   }
	}

	
}
