/*
 * Copyright (C) <2016> University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
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

 
/**
 * 
 * ExceptionDialog.
 * 
 * Displays an exception stack trace in a panel south of the main dialog area.
 * 
 * see: http://blue-walrus.com/2015/01/swing-error-dialog-with-exception-displayer/
 * and  http://stackoverflow.com/questions/6498421/error-dialog-with-scrollable-area-to-display-big-multiline-message
 * @author Oliver Watkins (c), Susanne Kunis<susannekunis at gmail dot com>
 */
public class ExceptionDialog extends JDialog {
 
	private int dialogWidth = 600;
	private int dialogHeight = 200;
 
	private JLabel iconLabel = new JLabel();
 
	// is error panel opened up
	private boolean open = false;
 
	private JLabel errorLabel = new JLabel();
	private JTextArea errorTextArea = new JTextArea("");
 
	private JTextArea exceptionTextArea = new JTextArea("");
	private JPanel details  = new JPanel(new BorderLayout());
 
	private JButton okButton = new JButton("OK");
	private JButton viewButton = new JButton("View Error");
	private JButton emailButton = new JButton("Email Error");
 
	private JPanel topPanel = new JPanel(new BorderLayout());
 
	
	
	public ExceptionDialog(String errorLabelText, String errorDescription,
			Throwable e,String clazz) 
	{
		setSize(dialogWidth, dialogHeight);
		setLocation(100, 100);
		setModal(true);
		setResizable(false);
 
		errorTextArea.setText(errorDescription);
 
		errorLabel.setText(errorLabelText);
 
		if(e!=null){
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			exceptionTextArea.setText(errors.toString());
		}else{
			exceptionTextArea.setText("");
		}
		
		
		iconLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
 
		iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		setupUI();
 
		setUpListeners();
	}
 
	public ExceptionDialog(String errorLabelText, Throwable e) 
	{
		this(errorLabelText, null, e,"");
	}
 
	public ExceptionDialog(String label, String errorStr,String clazz) 
	{
		this(label,errorStr,null,clazz);
	}

	public void setupUI() {
 
		this.setTitle("Error");
 
		errorTextArea.setLineWrap(true);
		errorTextArea.setWrapStyleWord(true);
		errorTextArea.setEditable(false);
 
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
 
		buttonPanel.add(okButton);
		emailButton.setEnabled(false);
		buttonPanel.add(emailButton);
		buttonPanel.add(viewButton);
 
		errorTextArea.setBackground(iconLabel.getBackground());
 
//		JScrollPane textAreaSP = new JScrollPane(errorTextArea);
// 
//		textAreaSP.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
 
		errorLabel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		
		Font txtFont=errorTextArea.getFont();
		Font boldFont= new Font(txtFont.getFontName(), Font.BOLD, txtFont.getSize());
		errorLabel.setFont(boldFont);
 
		
		JScrollPane exceptionTextSP=new JScrollPane(exceptionTextArea);
		exceptionTextSP.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		exceptionTextSP.setPreferredSize(new Dimension(100, 200));
		exceptionTextSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		details.add(exceptionTextSP,BorderLayout.CENTER);
 
		topPanel.add(iconLabel, BorderLayout.WEST);
 
		JPanel p = new JPanel(new BorderLayout());
		p.add(errorLabel, BorderLayout.NORTH);
		p.add(errorTextArea, BorderLayout.CENTER);
 
		topPanel.add(p);
 
		this.add(topPanel);
 
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
 
	private void setUpListeners() 
	{
 
		okButton.addActionListener(new ActionListener() {
 
			@Override
			public void actionPerformed(ActionEvent e) {
				ExceptionDialog.this.setVisible(false);
			}
		});
 
		viewButton.addActionListener(new ActionListener() {
 
			@Override
			public void actionPerformed(ActionEvent e) {
 
				if (open) {
					viewButton.setText("View Error");
 
					topPanel.remove(details);
 
					ExceptionDialog.this.setSize(dialogWidth, dialogHeight);
 
					topPanel.revalidate();
 
					open = false;
 
				} else {
 
					viewButton.setText("Hide Error");
 
					topPanel.add(details, BorderLayout.SOUTH);
 
					ExceptionDialog.this.setSize(dialogWidth,
							dialogHeight + 200);
 
					topPanel.revalidate();
 
					open = true;
				}
			}
		});
 
	}
 
}
