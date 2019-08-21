package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.openmicroscopy.shoola.util.ui.IconManager;

public class DichroicConverter extends DataConverter {
	@Override
	public JComponent getLabel(String name, int index, List<ActionListener> listeners) {
		JPanel labelPane=new JPanel();
		labelPane.setLayout(new BoxLayout(labelPane,BoxLayout.X_AXIS));
		labelPane.setBorder(new EmptyBorder(5, 2, 2, 2));
		labelPane.setOpaque(false);

		JLabel label=new JLabel(name);
		label.setBorder(new EmptyBorder(0, 0, 0, 10));
		labelPane.add(label);
		
		IconManager icons = IconManager.getInstance();
		JButton applyBtn = new JButton(icons.getIcon(IconManager.ADD_12));
		applyBtn.setToolTipText("Apply table selection");
		applyBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		labelPane.add(applyBtn);

		return labelPane;
	}
	
	
	
	
	public LinkedHashMap<String, TagData> convertData(Dichroic d)
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(d!=null) {
			try{tagMap.put(TagNames.ID,convertID(d.getID(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.ID,convertID(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MODEL,convertModel(d.getModel(), REQUIRED));
			} catch (NullPointerException e) { 
				tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			}
			try{tagMap.put(TagNames.MANUFAC,convertManufact(d.getManufacturer(),  REQUIRED));
			} catch (NullPointerException e) {
				tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
			}
		}else {
			tagMap.put(TagNames.ID,convertID(null,REQUIRED));
			tagMap.put(TagNames.MODEL,convertModel(null, REQUIRED));
			tagMap.put(TagNames.MANUFAC,convertManufact(null,  REQUIRED));
		}
		return tagMap;
	}
	
	
	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	public TagData convertID(String value,boolean prop) {
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.ID,value,prop,TagData.TEXTFIELD);
	}
	private TagData convertModel(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.MODEL,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertManufact(String value, boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertType(FilterType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.LP_TYPE,val,prop,TagData.COMBOBOX,OMEValueConverter.getNames(FilterType.class));
	}
	private TagData convertFilterwheel(String value,boolean prop)
	{
		return new TagData(TagNames.OME_ELEM_DICHROIC,TagNames.FILTERWHEEL,value,prop,TagData.TEXTFIELD);
	}
}
