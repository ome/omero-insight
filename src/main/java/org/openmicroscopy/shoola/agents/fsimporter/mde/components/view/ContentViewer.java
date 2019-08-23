package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign.ObjectTable;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.jdesktop.swingx.JXTaskPane;

public class ContentViewer extends JXTaskPane{
	private String title;
	private ModuleContent content;
	private ObjectTable availableElems;
	private CommonViewer tagPane;
	private String tagLayout;
	private JPanel mainPanel;
//	private ModuleTable table;
	
	private final int LABEL_W=170;
	
	public ContentViewer(String title,ObjectTable avElems, ModuleContent content) {
		this.title=title;
		this.content=content;
		this.availableElems=avElems;
//		System.out.println("-- layout Content "+title);

		layoutComponents();
		
		
		Font font = getFont();
//		taskPane.setFont(font.deriveFont(font.getSize2D()-2));
		setFont(font.deriveFont(font.getStyle(), font.getSize() - 2));
		setCollapsed(false);
		setTitle(title);
		add(mainPanel);
	}

	private void layoutComponents() {
		mainPanel=new JPanel();
		mainPanel.setLayout(new BorderLayout(0,0));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		if(content!=null) {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			String tableLayout = BorderLayout.NORTH;
			tagLayout=BorderLayout.NORTH;

			
			if(availableElems!=null) {
				// get original content (define in image file )based by id
//				int oIdx=availableElems.getOriginalIndex(content.getAttributeValue(TagNames.ID));
				// get index of content
				int idx=availableElems.getElementIndex(content);
				
				JPanel p=availableElems.buildGUI(idx, -1, this);
//				System.out.println("-- layout hardware table "+title+", select "+idx+" [ContenViewer]");
				mainPanel.add(p,tableLayout);
				tagLayout=BorderLayout.CENTER;
			}
			tagPane=new CommonViewer(content);
			mainPanel.add(tagPane,tagLayout);
			mainPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		}else {
			System.out.println("\t\t -> empty content "+title);
		}
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	/**
	 * take over data from selected elem in available object table
	 * @param newC object values that should be the new value
	 * @param origC original object content
	 */
	public void replaceData(ModuleContent newC,ModuleContent origC)
	{
//		System.out.println("-- replace data [ContentViewer]");
		content=MDEHelper.replaceUnchangedData(content, origC, newC);
		title=newC.getAttributeValue(TagNames.MODEL)!=null?newC.getAttributeValue(TagNames.MODEL):"";
		
//		System.out.println("--input at "+content.getType()+", change elems:"+(content.getInput()!=null? content.getInput().size():"null"));
		mainPanel.remove(tagPane);
		tagPane=new CommonViewer(content);
//		System.out.println("-- new elem: "+newContent.getList().get(TagNames.ID).getTagValue());
		mainPanel.add(tagPane,tagLayout);
		tagPane.revalidate();
		tagPane.repaint();
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
}