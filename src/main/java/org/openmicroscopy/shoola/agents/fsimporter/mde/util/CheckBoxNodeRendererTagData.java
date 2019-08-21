package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.CheckBoxNodeTagData;


public class CheckBoxNodeRendererTagData extends JCheckBox implements TreeCellRenderer  {
		private TreeCellRenderer renderer = new DefaultTreeCellRenderer();
		public Component getTreeCellRendererComponent(
				JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			if(leaf && value != null && value instanceof DefaultMutableTreeNode) {
				this.setOpaque(false);
				Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
				if(userObject!=null && userObject instanceof CheckBoxNodeTagData) {
					CheckBoxNodeTagData node = (CheckBoxNodeTagData)userObject;
					this.setText(node.getText());
					this.setSelected(node.isSelected());
				}
				return this;
			}
			return renderer.getTreeCellRendererComponent(
					tree, value, selected, expanded, leaf, row, hasFocus);
		}
}
