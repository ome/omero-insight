/*
 * org.openmicroscopy.shoola.util.ui.login.ServerEditor 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2019 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.util.ui.login;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import omero.constants.GLACIER2PORT;
import org.openmicroscopy.shoola.util.ui.IconManager;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.openmicroscopy.shoola.util.ui.border.PartialLineBorder;

/** 
 * UI component display controls and list of servers.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ServerEditor 
	extends JPanel
{
	
	/** Bound property indicating to remove the warning message. */
	public static final String	REMOVE_MESSAGE_PROPERTY = "removeMessage";
	
	/** Bound property indicating to add the warning message. */
	public static final String	ADD_MESSAGE_PROPERTY = "addMessage";
	
	/** Bound property indicating that the edition is finished. */
	static final String			EDIT_PROPERTY = "edit";

	/** Bound property indicating that a server is removed from the list. */
	static final String 		REMOVE_PROPERTY = "remove";
	
	/** Bound property indicating to apply the selection. */
	static final String 		APPLY_SERVER_PROPERTY = "applyServer";

    /** Separator used when storing various servers. */
    static final String			SERVER_NAME_SEPARATOR = ",";

    /** The default port **/
    static final int			DEFAULT_PORT = GLACIER2PORT.value;

	/** Example of a new server. */
	private static final String	EXAMPLE = "<html>" +
            "<i>my.server.name</i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(default case)<br>" +
			"<i>my.server.name:port</i>&nbsp;&nbsp;(for non-default ports)<br>" +
			"<i>127.0.0.1:1234</i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(if you have an IP address)<br>" +
			"<i>wss://my.server.name</i>&nbsp;(connecting via websocket)<br><br>" +
            "</html>";

	/** The header of the table. */
	private static final String HEADER = "Server Address Examples";

    /** The property name for the host to connect to <i>OMERO</i>. */
    private static final String	OMERO_SERVER = "omeroServer";
    
	/** Font for progress bar label. */
	private static final Font	FONT = new Font("Monospaced", Font.PLAIN, 10);

	/** Button to remove server from the list. */
	private JButton			removeButton;
	
	/** Button to add new server to the list. */
	private JButton			addButton;
	
	/** Button to edit an existing server. */
	private JButton			editButton;

	private DefaultListModel servers;

	/** Component displaying the collection of available servers. */
	private JList<String> table;

    /** The panel displaying the message when no name is entered. */
    private JPanel          emptyMessagePanel;
    
	/** Helper reference to the icons manager. */
	private IconManager		icons;
	
    /** 
     * Sets to <code>true</code> if the message is displayed, 
     * <code>false</code> otherwise.
     */
    private boolean			warning;

    /**
	 * The server the user is currently connected to or <code>null</code>
	 * if not connected.
	 */
	private String			activeServer = "";

	/** 
	 * Initializes the components. 
	 * 
	 * @param servers   Collection of servers to display.
	 * 					
	 */
	private void initComponents(List<String> servers)
	{
		this.servers = new DefaultListModel() {
			@Override
			public void insertElementAt(Object var1, int var2) {
				if (!contains(var1))
					super.insertElementAt(var1, var2);
			}
			@Override
			public void addElement(Object var1) {
				if (!contains(var1))
					super.addElement(var1);
			}
		};
		servers.stream().forEach(s -> this.servers.addElement(s));
		this.table = new JList<>(this.servers);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				fireEditProperty(true);
			}
		});
		removeButton = new JButton(icons.getIcon(IconManager.REMOVE));
		removeButton.setName("remove server button");
		UIUtilities.unifiedButtonLookAndFeel(removeButton);
		removeButton.setToolTipText("Remove the selected server " +
									"from the list of servers.");
		addButton = new JButton(icons.getIcon(IconManager.ADD));
		addButton.setName("add server button");
		addButton.setToolTipText("Add a new server to the list of servers.");
		addButton.setBorder(new TitledBorder(""));
		UIUtilities.unifiedButtonLookAndFeel(addButton);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int row = table.getSelectedIndex();
				if (row > -1) {
					int sel = row - 1;
					if (sel < 0)
						sel = 0;
					ServerEditor.this.servers.remove(row);
					ServerEditor.this.table.setSelectedIndex(sel);
				}
			}
		});
		removeButton.setEnabled(!this.servers.isEmpty());
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String add = JOptionPane.showInputDialog(ServerEditor.this,
						"Enter server address:",
						"Add new server",
						JOptionPane.PLAIN_MESSAGE);
				if (add != null) {
					add = add.trim();
					add = add.replace(":"+DEFAULT_PORT, "");
					if (add.toLowerCase().startsWith("http://") ||
							add.toLowerCase().startsWith("https://"))
						add = add.substring(add.indexOf('/') + 2, add.length());
					if (add.length() > 0) {
						ServerEditor.this.servers.addElement(add);
						ServerEditor.this.table.setSelectedValue(add, true);
						removeButton.setEnabled(true);
					}
				}
			}
		});
		editButton = new JButton(icons.getIcon(IconManager.EDIT));
		editButton.setName("edit server button");
		UIUtilities.unifiedButtonLookAndFeel(editButton);
		editButton.setToolTipText("Edit an existing server.");
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{ 
				int row = table.getSelectedIndex();
				String edited = (String) JOptionPane.showInputDialog(ServerEditor.this, "", "Edit server", JOptionPane.PLAIN_MESSAGE,
						null, null, (String) table.getSelectedValue());
				if (edited != null) {
					edited = edited.replace(":"+DEFAULT_PORT, "");
					ServerEditor.this.servers.remove(row);
					ServerEditor.this.servers.add(row, edited);
					ServerEditor.this.table.setSelectedValue(edited, true);
				}
			}
		});
		editButton.setEnabled(!servers.isEmpty());
	}

	/** Builds and lays out the UI. */
	private void buildGUI()
	{
		JPanel labels = new JPanel();
		labels.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 2, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
		c.fill = GridBagConstraints.NONE;      //reset to default
		c.weightx = 0.0;
		JLabel label = UIUtilities.setTextFont(HEADER);
		labels.add(label, c);
		label = new JLabel(EXAMPLE);
		label.setFont(FONT);
		c.gridy++;// = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;     //end row
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		labels.add(label, c);
		if (activeServer != null) {
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
			c.fill = GridBagConstraints.NONE;      //reset to default
			c.weightx = 0.0;
			label = UIUtilities.setTextFont("Connecting to ");
			labels.add(label, c);
			c.gridwidth = GridBagConstraints.REMAINDER;     //end row
			//c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            label = new JLabel(activeServer);
            labels.add(label, c); 
        }
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JScrollPane pane = new JScrollPane(table);
        Dimension d = pane.getPreferredSize();
        pane.setPreferredSize(new Dimension(d.width, 150));
        p.add(pane);
        p.add(buildControls());
       
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel content = UIUtilities.buildComponentPanel(labels);
        add(content); 
        add(p);
	}
	
	/**
	 * Builds the component hosting the controls.
	 * 
	 * @return See above.
	 */
	private JPanel buildControls()
	{
		JToolBar bar = new JToolBar();
    	bar.setFloatable(false);
        bar.setRollover(true);
        bar.setBorder(null);
        bar.add(addButton);
        bar.add(removeButton);
        bar.add(editButton);
        return UIUtilities.buildComponentPanel(bar);
	}
	
	/**
	 * Sets the <code>enabled</code> flag of the 
	 * {@link #addButton} and {@link #removeButton}.
	 * 
	 * @param b The value to set.
	 */
	private void setButtonsEnabled(boolean b)
	{
		//addButton.setEnabled(b);
		removeButton.setEnabled(b);
		editButton.setEnabled(b);
	}
	
	/**
	 * Fires a property to 
	 * 
	 * @param b	Pass <code>true</code> when editing, 
	 * 			<code>false</code> otherwise.
	 */
	private void fireEditProperty(boolean b)
	{
		firePropertyChange(EDIT_PROPERTY, Boolean.valueOf(!b), 
				Boolean.valueOf(b));
	}
	
	/** Creates the {@link #emptyMessagePanel} if required. */
    private void buildEmptyPanel()
    {
        if (emptyMessagePanel != null) return;
        emptyMessagePanel = new JPanel();
        emptyMessagePanel.setOpaque(false);
        emptyMessagePanel.setBorder(new PartialLineBorder(Color.BLACK));
        emptyMessagePanel.setLayout(new BoxLayout(emptyMessagePanel,
         BoxLayout.X_AXIS));
    }

	/** 
	 * Creates a new instance.
	 */
	ServerEditor()
	{
		this(null);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param activeServer  The server the user is currently connected to.
	 */
	ServerEditor(String activeServer)
	{
		icons = IconManager.getInstance();
		this.activeServer = activeServer == null ? "" : activeServer;
		List<String> servers = getServers();
		initComponents(servers);
		buildGUI();
	}
	
	/**
	 * Shows the warning message if the passed value is <code>true</code>,
	 * hides it otherwise.
	 * 
	 * @param warning 	Pass <code>true</code> to show the message, 
	 * 					<code>false</code> otherwise.			
	 */
	void showMessagePanel(boolean warning)
	{
		this.warning = warning;
		fireEditProperty(!warning);
		setButtonsEnabled(!warning);
		if (warning) {
			if (emptyMessagePanel != null) return;
			buildEmptyPanel();
            firePropertyChange(ADD_MESSAGE_PROPERTY, null, emptyMessagePanel);
        } else {
        	if (emptyMessagePanel == null) return;
        	firePropertyChange(REMOVE_MESSAGE_PROPERTY, null, 
        			emptyMessagePanel);
            emptyMessagePanel = null;
        }
	}

	/**
	 * Returns the value of the selected server.
	 * 
	 * @return See above.
	 */
	String getSelectedServer()
	{
		return table.getSelectedValue();
	}

	boolean isOriginalSelected() {
		return getSelectedServer() == null ? false : getSelectedServer().equals(activeServer);
	}

	void addRow(String server) {
		this.servers.addElement(server);
		this.table.setSelectedValue(server, true);
	}
	
	/**
	 * Returns the list of existing servers.
	 * 
	 * @return See above.
	 */
	List<String> getServers()
	{
    	Preferences prefs = Preferences.userNodeForPackage(ServerEditor.class);
        String servers = prefs.get(OMERO_SERVER, null);
        if (servers == null || servers.length() == 0)
            return new ArrayList<>();
        String[] l = servers.split(SERVER_NAME_SEPARATOR, 0);
		for (int i=0; i<l.length; i++)
			l[i] = l[i].replace(":"+DEFAULT_PORT, "");
        if (l == null)
        	return new ArrayList<>();
		return Stream.of(l).collect(Collectors.toList());
	}
	
	/** 
	 * Saves the collection of servers. 
	 * 
	 * @param serverName 	The name of the server which has to be added at 
	 * 						the end of the list.
	 */
	void handleServers(String serverName)
	{
		serverName = serverName == null ? "" : serverName.trim();

		Preferences prefs = Preferences.userNodeForPackage(ServerEditor.class);

		List<String> l = new ArrayList<>();
		Stream.of(this.servers.toArray()).forEach(s -> l.add((String)s));

		if (l.isEmpty()) {
			prefs.put(OMERO_SERVER, "");
			return;
		}

		StringBuffer buffer = new StringBuffer();
		for (String s : l) {
			s = s.trim();
			if (!s.equals(serverName)) {
				buffer.append(s);
				buffer.append(SERVER_NAME_SEPARATOR);
			}
		}
		if (serverName.length()>0)
			buffer.append(serverName);

		prefs.put(OMERO_SERVER, buffer.toString());
	}

	/**
	 * Sets the focus on the row corresponding to the passed server.
	 * 
	 * @param server The server to handle.
	 */
	void setFocus(String server)
	{
		this.table.setSelectedValue(server, true);
	}
	
	/**
	 * Returns the number of rows.
	 * 
	 * @return See above.
	 */
	int getRowCount() { return this.servers.getSize(); }
	
}
