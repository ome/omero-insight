package org.openmicroscopy.shoola.agents.fsimporter.mde.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;

/**
 * Datastructur:
 * <pre>{@code
 * ModuleList==HashMap<String,List<ModuleContent>>
 * }</pre>
 * Key:= instrument type,
 * values:= available values for the key
 * @author Kunis
 *
 */
public class ModuleList extends HashMap<String, List<ModuleContent>> {
	
	/** 
	 * copy constructor
	 * @param orig
	 */
	public ModuleList(ModuleList orig) {
		for (Map.Entry<String,List<ModuleContent>> entry: orig.entrySet()) {
			List<ModuleContent> newList = new ArrayList<>();
			if(entry.getValue()!=null) {
				for(ModuleContent c : entry.getValue()) {
					newList.add(new ModuleContent(c));
				}
				this.put(entry.getKey(), newList);
			}else {
				this.put(entry.getKey(),null);
			}
		}
	}

	public ModuleList() {
		super();
	}
	
	public void print(String header) {
		System.out.println(header);
		String result="{\n";
		for(Map.Entry<String,List<ModuleContent>> entry: this.entrySet()) {
			if(entry.getValue()!=null) {
				result+=entry.getKey()+": "+entry.getValue().size()+" Elements \n";
				for(ModuleContent c:entry.getValue()) {
					result+="\t"+c.getAttributeValue(TagNames.MODEL)+"\n";
				}
			}
			else {
				result+=entry.getKey()+": null Elements \n";
			}
		}
		System.out.println(result+"\n}");
	}
	
}
