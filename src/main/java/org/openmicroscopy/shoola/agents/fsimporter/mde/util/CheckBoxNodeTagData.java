package org.openmicroscopy.shoola.agents.fsimporter.mde.util;


public class CheckBoxNodeTagData 
{
	
	    private TagData tag;
	    private String text;
	    private boolean selected;
	    public CheckBoxNodeTagData(TagData tag, boolean selected) {
	        this.tag = tag;
	        this.text=getText();
	        this.selected = selected;
	    }
	    
	    public CheckBoxNodeTagData(String text, boolean selected) {
	        this.tag = null;
	        this.text=text;
	        this.selected = selected;
	    }
	    @Override public String toString() {
	        return tag.toString();
	    }
	    
	    public String getText()
	    {
	    	return tag.getTagLabel().getText();
	    }
	    
	    public boolean isSelected()
	    {
	    	return selected;
	    }
}
