package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;

public class ContentViewer extends JPanel{
	private String title;
	private ModuleContent content;
	private ObjectTable availableElems;
	private CommonViewer tagPane;
	private String tagLayout;
//	private ModuleTable table;
	
	private final int LABEL_W=170;
	
	public ContentViewer(String title,ObjectTable avElems, ModuleContent content) {
		this.title=title;
		this.content=content;
		this.availableElems=avElems;
		layoutComponents();
		revalidate();
		repaint();
	}

	private void layoutComponents() {
		if(content!=null) {
			setLayout(new BorderLayout(0,0));

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			String tableLayout = BorderLayout.NORTH;
			tagLayout=BorderLayout.NORTH;

			
			if(availableElems!=null) {
//				System.out.println("-- layout hardware table "+title+" [ContenViewer]");
				int idx=availableElems.getElementIndex(content);
				JPanel p=availableElems.buildGUI(idx,this);
				add(p,tableLayout);
				tagLayout=BorderLayout.CENTER;
			}
			tagPane=new CommonViewer(content);
			add(tagPane,tagLayout);
			setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		}
	}
	
	/**
	 * @param c
	 */
	public void replaceData(ModuleContent newC,ModuleContent origC)
	{
//		System.out.println("-- replace data [ContentViewer]");
		content=MDEHelper.replaceUnchangedData(content, origC, newC);
		
		System.out.println("--input at "+(content.getInput()!=null? content.getInput().size():"null"));
		remove(tagPane);
		tagPane=new CommonViewer(content);
//		System.out.println("-- new elem: "+newContent.getList().get(TagNames.ID).getTagValue());
		add(tagPane,tagLayout);
		tagPane.revalidate();
		tagPane.repaint();
		revalidate();
		repaint();
	}
	
	

	// TODO: nur ein teil der infos in availableElem element -> test ID,Model,Manufacturer
//	private int getIndexOfCurrentObject() {
//		if(availableElems==null) {
//			return -1;
//		}
//		System.out.println("-- find current element in hardwarelist [ContenViewer]");
//		int index=0;
//		for(ModuleContent c:availableElems) {
//			if(MDEHelper.isEqual(c, content)) {
//				return index;
//			}
//			index++;
//		}
//		return -1;
//	}
}
