package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.slf4j.LoggerFactory;

public class WarningDialog extends JDialog
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(WarningDialog.class);
	
	private int dialogWidth = 600;
	private int dialogHeight = 200;
 
	private JLabel iconLabel = new JLabel();
 
	// is error panel opened up
	private boolean open = false;
 
	private JLabel label = new JLabel();
	private JTextArea textArea = new JTextArea("");
 
 
	private JButton okButton = new JButton("OK");
 
	private JPanel topPanel = new JPanel(new BorderLayout());
 
	
	
	public WarningDialog(String labelText, String tip,String clazz) 
	{
		LOGGER.warn("["+clazz+"] "+labelText+" \n"+tip);
		setSize(dialogWidth, dialogHeight);
		setLocation(100, 100);
		setModal(true);
		setResizable(false);
 
		textArea.setText(tip);
 
		label.setText(labelText);
		
		
		iconLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
 
		iconLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		setupUI();
 
		setUpListeners();
	}
 
 

	public void setupUI() {
 
		this.setTitle("Warning");
 
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
 
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
 
		buttonPanel.add(okButton);
 
		textArea.setBackground(iconLabel.getBackground());
 
//		JScrollPane textAreaSP = new JScrollPane(errorTextArea);
// 
//		textAreaSP.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
 
		label.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		
		Font txtFont=textArea.getFont();
		Font boldFont= new Font(txtFont.getFontName(), Font.BOLD, txtFont.getSize());
		label.setFont(boldFont);
 
		
		topPanel.add(iconLabel, BorderLayout.WEST);
 
		JPanel p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(new JScrollPane(textArea), BorderLayout.CENTER);
 
		topPanel.add(p);
 
		this.add(topPanel);
 
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
 
	private void setUpListeners() 
	{
 
		okButton.addActionListener(new ActionListener() {
 
			@Override
			public void actionPerformed(ActionEvent e) {
				WarningDialog.this.setVisible(false);
			}
		});
	}
}
