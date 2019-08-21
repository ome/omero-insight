package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

//import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.ObjectConfigurator.JComboCheckBox.ComboBoxRenderer;

public class CheckBoxCombo extends JComboBox {
	JCheckBox[] items;
	
	public CheckBoxCombo() { addStuff(); }

	public CheckBoxCombo(JCheckBox[] items) { super(items); this.items=items; addStuff(); }

//	public CheckBoxCombo(Vector items) { super(items); addStuff(); }

//	public CheckBoxCombo(ComboBoxModel aModel) { super(aModel); addStuff(); }

	public CheckBoxCombo(String[] values) {
		if(values==null)
			addStuff();
		this.items = new JCheckBox[values.length];
		for(int i=0;i<values.length; i++) {
			items[i]=new JCheckBox(values[i]);
		}
	}
	private void addStuff() {
		setRenderer(new ComboBoxRenderer());
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) { itemSelected(); }
		});
	}
	private void itemSelected() {
		if (getSelectedItem() instanceof JCheckBox) {
			JCheckBox jcb = (JCheckBox)getSelectedItem();
			jcb.setSelected(!jcb.isSelected());
		}
	}
	
	public void init(String[] values) {
		List<String> t=Arrays.asList(values);
		for(int i=0; i<values.length; i++) {
			if(t.contains(values[i])) {
				items[i].setSelected(true);
			}else {
				items[i].setSelected(false);
			}
		}
	}
	public String[] getSelectedVal() {
		List<String> result= new ArrayList<>();
		for(JCheckBox cb:items) {
			if(cb.isSelected())
				result.add(cb.getText());
		}
		if(result.isEmpty())
			return null;
		return result.toArray(new String[result.size()]);
	}
	class ComboBoxRenderer implements ListCellRenderer {
		private JLabel defaultLabel=new JLabel("Parents");
		public ComboBoxRenderer() { setOpaque(true); }
		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (value instanceof Component) {
				Component c = (Component)value;
				if (isSelected) {
					c.setBackground(list.getSelectionBackground());
					c.setForeground(list.getSelectionForeground());
				} else {
					c.setBackground(list.getBackground());
					c.setForeground(list.getForeground());
				}
				return c;
			} else {
				//	        if (defaultLabel==null) defaultLabel = new JLabel(value.toString());
				//	        else defaultLabel.setText(value.toString());
				return defaultLabel;
			}
		}
	}
	
}
