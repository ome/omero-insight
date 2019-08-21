package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmicroscopy.shoola.agents.fsimporter.mde.util.DocumentListenerForDouble;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.DocumentListenerForPercentFraction;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import org.slf4j.LoggerFactory;

/**
 * TODO: better as interface instead of abstract
 * @author Kunis
 *
 */
public abstract class DataConverter {
	protected static final org.slf4j.Logger LOGGER =LoggerFactory.getLogger(DataConverter.class);
	
	public static final boolean REQUIRED=true;
	public static final boolean OPTIONAL =false;
	
	protected LinkedHashMap<String,TagData> tagMap;
	
	public LinkedHashMap<String,TagData> getTagList()
	{
		return tagMap;
	}
	
	public JComponent getLabel(String name, int index, List<ActionListener> listeners) {
		return null;
	}
//	public abstract <T extends AbstractOMEModelObject> LinkedHashMap<String, TagData> convertData(T object);
	
	public DocumentListener createDocumentListenerDouble(TagData tag, String error) {
		return new DocumentListenerForDouble(tag,error, false); 
			
	}
	
	public DocumentListener createDocumentListenerPosFloat(TagData tag,String error){
		return new DocumentListenerForDouble(tag,error, true); 
	}
	
	/**
	 * @return
	 */
	public DocumentListener createDocumentListenerPercentFraction(TagData tag, String error) {
		return new DocumentListenerForPercentFraction(tag,error);
	}


	
}