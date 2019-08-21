package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

 
/**
 *
 * see https://coderanch.com/t/341737/java/Expand-Collapse-Panels
 */
public class CollapsablePane2 extends JPanel {
 
    String title;
    TitledBorder border;
    JPanel contentPanel_;
 
    public CollapsablePane2(String text,JPanel panel) {
    	this.title=text;
    	this.contentPanel_=panel;
        border = BorderFactory.createTitledBorder(title);
        setBorder(border);
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        addMouseListener(mouseListener);
        
        add(contentPanel_);
    }
 
    MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            toggleVisibility();
        }
    };
 
    ComponentListener contentComponentListener = new ComponentAdapter() {
        @Override
        public void componentShown(ComponentEvent e) {
            updateBorderTitle();
        }
        @Override
        public void componentHidden(ComponentEvent e) {
            updateBorderTitle();
        }
    };
 
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        firePropertyChange("title", this.title, this.title = title);
    }
 
    @Override
    public Component add(Component comp) {
        comp.addComponentListener(contentComponentListener);
        Component r = super.add(comp);
        updateBorderTitle();
        return r;
    }
 
    @Override
    public Component add(String name, Component comp) {
        comp.addComponentListener(contentComponentListener);
        Component r = super.add(name, comp);
        updateBorderTitle();
        return r;
    }
 
    @Override
    public Component add(Component comp, int index) {
        comp.addComponentListener(contentComponentListener);
        Component r = super.add(comp, index);
        updateBorderTitle();
        return r;
    }
 
    @Override
    public void add(Component comp, Object constraints) {
        comp.addComponentListener(contentComponentListener);
        super.add(comp, constraints);
        updateBorderTitle();
    }
 
    @Override
    public void add(Component comp, Object constraints, int index) {
        comp.addComponentListener(contentComponentListener);
        super.add(comp, constraints, index);
        updateBorderTitle();
    }
 
    @Override
    public void remove(int index) {
        Component comp = getComponent(index);
        comp.removeComponentListener(contentComponentListener);
        super.remove(index);
    }
 
    @Override
    public void remove(Component comp) {
        comp.removeComponentListener(contentComponentListener);
        super.remove(comp);
    }
 
    @Override
    public void removeAll() {
        for (Component c : getComponents()) {
            c.removeComponentListener(contentComponentListener);
        }
        super.removeAll();
    }
 
    protected void toggleVisibility() {
        toggleVisibility(hasInvisibleComponent());
    }
 
    protected void toggleVisibility(boolean visible) {
        for (Component c : getComponents()) {
            c.setVisible(visible);
        }
        updateBorderTitle();
    }
 
    protected void updateBorderTitle() {
        String arrow = "";
        if (getComponentCount() > 0) {
            arrow = (hasInvisibleComponent()?"▽":"△");
        }
        border.setTitle(title +" "+ arrow);
        repaint();
    }
 
    protected final boolean hasInvisibleComponent() {
        for (Component c : getComponents()) {
            if (!c.isVisible()) {
                return true;
            }
        }
        return false;
    }
    
    
 
}
