package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.util.LinkedHashMap;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import ome.xml.model.AbstractOMEModelObject;

public interface ObjectTableModel<T extends AbstractOMEModelObject> {
	void addRow(T o);
	void insertRow(int i,T o);
	boolean elementEqualsRow(int rowIdx,LinkedHashMap<String, TagData> list);
	Object[] parse(T o);
}
