package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

//import javafx.scene.shape.Box;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class OverwriteTagDialog extends JDialog 
{
	private int dialogWidth = 600;
	private int dialogHeight = 200;
	
	private JButton yesBtn = new JButton("Yes");
	private JButton yesToAllBtn= new JButton("Yes To All");
	private JButton noBtn= new JButton("No");
	private JButton cancelBtn=new JButton("Cancel");
	
	private JLabel sectionLabel=new JLabel("Overwrite existing Tag in files!");
	
	private CheckBoxList listFiles;
	private CheckBoxList listTags;
	
	
	
	public OverwriteTagDialog(List<String> files, List<String> tagNames)
	{
		
		buildGUI(files,tagNames);
	}



	private void buildGUI(List<String> files, List<String> tagNames) 
	{
		setSize(dialogWidth, dialogHeight);
		setLocation(100, 100);
		setModal(true);
		setResizable(false);
		
		JPanel topPanel=new JPanel(new BorderLayout());
		
		JPanel listPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		listFiles = new CheckBoxList(files);	
		listTags = new CheckBoxList(tagNames);
		listPanel.add(listFiles);
		listPanel.add(listTags);

		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(yesBtn);
		buttonPane.add(yesToAllBtn);
		buttonPane.add(noBtn);
		buttonPane.add(cancelBtn);
		
		 
		 topPanel.add(sectionLabel, BorderLayout.NORTH);
		 topPanel.add(listPanel, BorderLayout.CENTER);
		 
		 this.add(topPanel, BorderLayout.CENTER);
		 this.add(buttonPane,BorderLayout.SOUTH);
	}
	
	public List<String> getUpdatedFileList()
	{
		return listFiles.getList();
	}
	
	public List<String> getUpdatedTagList()
	{
		return listTags.getList();
	}
}
