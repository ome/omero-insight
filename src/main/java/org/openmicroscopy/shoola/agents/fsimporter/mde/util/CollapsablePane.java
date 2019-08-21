package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;
import org.openmicroscopy.shoola.util.ui.IconManager;

/**
 * 
 * see https://coderanch.com/t/341737/java/Expand-Collapse-Panels
 */
public class CollapsablePane extends JPanel{
	private boolean selected;
	JPanel contentPanel_;
	HeaderPanel headerPanel_;
	String title;
//final char arrowUp='\u10835';2A53
//final char arrowDown = '\u2A54';//'\u0033'//10836, 2A54
	private Icon OPEN;
	private Icon CLOSE;
	
	public CollapsablePane(String text, JPanel panel) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 3, 0, 3);
		gbc.weightx = 1.0;
		gbc.fill = gbc.HORIZONTAL;
		gbc.gridwidth = gbc.REMAINDER;

		IconManager icons = IconManager.getInstance();
		
		OPEN=icons.getIcon(IconManager.PLUS_9);//new ImageIcon("/icons/baseline_add_circle_outline_black_18dp.png");
		CLOSE=icons.getIcon(IconManager.MINUS_9);//new ImageIcon("/icons/baseline_remove_circle_outline_black_18dp.png");
		this.title=text;
		selected = false;
		headerPanel_ = new HeaderPanel(text);
		headerPanel_.setIcon(CLOSE);

//		setBackground(new Color(200, 200, 220));
		contentPanel_ = panel;

		add(headerPanel_, gbc);
		add(contentPanel_, gbc);
		contentPanel_.setVisible(true);

		JLabel padding = new JLabel();
		gbc.weighty = 1.0;
		add(padding, gbc);

	}

	public void toggleSelection() {
		selected = !selected;
//		String arrow="";
		Icon i=null;
		if (contentPanel_.isShowing()) {
			contentPanel_.setVisible(false);
//			arrow="+";
			i=OPEN;
		}else {
			contentPanel_.setVisible(true);
//			arrow="-";
			i=CLOSE;
		}
		validate();
//		headerPanel_.setText(arrow+ "   " +title);
		headerPanel_.setIcon(i);
		headerPanel_.repaint();
	}

	private class HeaderPanel extends JPanel implements MouseListener {
		Icon icon;
		String text_;
		Font font;
		BufferedImage open, closed;
		final int OFFSET = 10, PAD = 5;

		public HeaderPanel(String text) {
			addMouseListener(this);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			text_ = text;
//			font = new Font("sans-serif", Font.PLAIN, 12);
			font=new Font("Tahoma", Font.BOLD, 11);
			// setRequestFocusEnabled(true);
			setPreferredSize(new Dimension(200, 20));
			
			setBackground(new Color(191, 191, 191));
			int w = getWidth();
			int h = getHeight();

//			try {
//	                open = ImageIO.read(new File("/icons/baseline_add_circle_outline_black_18dp.png"));
//	                closed = ImageIO.read(new File("/icons/baseline_remove_circle_outline_black_18dp.png"));
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
		}
		public void setText(String text) {
			text_=text;
			repaint();
		}
		public void setIcon(Icon i) {
			this.icon=i;
			repaint();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int h = getHeight();
			
			
//			if (selected)
//	                g2.drawImage(open, PAD, 0, h, h, this);
//	            else
			icon.paintIcon(this, g2, getWidth()-PAD-10, PAD);
//	                g2.drawImage(null, PAD, 0, h, h, this);
			  // Uncomment once you have your own images
			g2.setFont(font);
			FontRenderContext frc = g2.getFontRenderContext();
			LineMetrics lm = font.getLineMetrics(text_, frc);
			float height = lm.getAscent() + lm.getDescent();
			float x = OFFSET;
			float y = (h + height) / 2 - lm.getDescent();
//			g2.drawImage(icon.getImage(),(int) x-5, (int)y, null);
			g2.drawString(text_, x, y);
		}

		public void mouseClicked(MouseEvent e) {
			toggleSelection();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}
}
