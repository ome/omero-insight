/*
 * org.openmicroscopy.shoola.agents.util.flim.HistogramCanvas 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2011 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.util.flim;

//Java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

//Third-party libraries
import processing.core.PApplet;
import processing.core.PVector;

//Application-internal dependencies
import org.openmicroscopy.shoola.util.processing.chart.FillType;
import org.openmicroscopy.shoola.util.processing.chart.HeatMap;
import org.openmicroscopy.shoola.util.processing.chart.HistogramChart;
import org.openmicroscopy.shoola.util.processing.chart.ImageData;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/**
 * Component displaying the histogram.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
class HistogramCanvas 
extends PApplet
{
//	/** The chart selected property. */
	public final static String CHARTSELECTED_PROPERTY = "HistogramCanvas.ChartSelected";

	/** The histogram. */
	private HistogramChart chart;
	
	/** The heatMap. */
	private HeatMap map;
	
	/** The object holding the data. */
	private ImageData data;
	
	/** The ordered data to display. */
	private List<Double> orderedData;
	
	/** The number of bins.*/
	private int bins;

	/** The button used to open or close the heatMap.*/
	private Rectangle button;
	
	/** Flag indicating that open or close heatMap.*/
	private boolean displayHeatMap = true;
	
	/** The value by which to translate the heatMap along the X-axis.*/
	private int translateX = 15;
	
	/** The value by which to translate the heatMap along the X-axis.*/
	private int translateY = 15;
	
	/** The width of the border between the graph and the window border.*/
	private int borderWidth = 10;
	
	/** The background colour of the windows. */
	final static Color windowsBackground = new Color(230,230,230);

	/**
	 * Creates a new instance.
	 * 
	 * @param orderedData The ordered data.
	 * @param data The data to display.
	 * @param bins The number of bins.
	 */
	HistogramCanvas(List<Double> orderedData, ImageData data, int bins)
	{
		this(orderedData, data, bins, true, -1);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param orderedData The ordered data.
	 * @param data The data to display.
	 * @param bins The number of bins.
	 * @param showHeatMap 	Pass <code>true</code> to show the heatMap,
	 * 						<code>false</code> otherwise.
	 * @param valueIsBlack Values equal to or less in the heatmap are black.
	 */
	HistogramCanvas(List<Double> orderedData, ImageData data, int bins, 
			boolean showHeatMap, int valueIsBlack)
	{
		if (data == null)
			throw new IllegalArgumentException("No data to display.");
		if (orderedData == null)
			throw new IllegalArgumentException("No data to display.");
		if (bins <= 0) bins = 1;
		this.bins = bins;
		this.orderedData = orderedData;
		this.data = data;
		button = new Rectangle(2, 2, 12, 12);
		chart = new HistogramChart(this, orderedData, bins, valueIsBlack,FillType.NONE);
		chart.setRGB(true, 40, 80);
		map = new HeatMap(this, data, chart, valueIsBlack); 
		this.displayHeatMap = showHeatMap;
		chart.setPastelColours();
		init();
	}
	/**
	 * Returns the colour of the bin containing the value.
	 * 
	 * @param value See above.
	 * @return See above.
	 */
	public int findColour(double value)
	{
		return chart.findColour(value);
	}
	
	/**
	 * Get the pixel position in the heat map.
	 * @param x The x coord.
	 * @param y The y coord.
	 * @return return the pixels position, or return <code>null</code>.
	 */
	public PVector getHeatMapPosition(int x, int y)
	{
		int mapX, mapY;
		
		mapX = x-translateX;
		mapY = y-translateY;
		if(mapX>0&&mapX<width)
			if(mapY>0&&mapY<height)
			{
				return new PVector(mapX, mapY);
			}
		return null;
	}
	
	/**
	 * Return true if the heat map has been clicked.
	 * @param x The x coordinate on the dialog.
	 * @param y The y coordinate on the dialog.
	 * @return See above.
	 */
	public boolean heatMapClicked(int x, int y)
	{
		int mapX, mapY;
		if(!displayHeatMap)
			return false;
		
		mapX = x-translateX;
		mapY = y-translateY;
		if(mapX>0&&mapX<map.getWidth())
			if(mapY>0&&mapY<map.getHeight())
				return true;
		return false;
	}
	
	/**
	 * Return true if the chart has been clicked.
	 * @param x The x coordinate on the dialog.
	 * @param y The y coordinate on the dialog.
	 * @return See above.
	 */
	public boolean chartClicked(int x, int y)
	{
		int mapX, mapY;
		Rectangle chartBounds = chart.getBounds();
		if(displayHeatMap)
		{
			mapX = x-translateX-data.getWidth();
			mapY = y-translateY;
			
			if(mapX>0&&mapX<chartBounds.getWidth())
				if(mapY>0&&mapY<chartBounds.getHeight())
					return true;
			return false;
		}
		else
		{
			return chartBounds.contains(x, y); 
		
		}
	}
	
	/**
	 * Get the value of the Chart at position (x,y)
	 * @param x The x coordinate on the dialog.
	 * @param y The y coordinate on the dialog.
	 * @return See above.
	 */
	public PVector getChartValue(int x, int y)
	{
		int mapX, mapY;
		if(displayHeatMap)
		{
			mapX = x-translateX-data.getWidth();
			mapY = y-translateY;
		}
		else
		{
			mapX = x;
			mapY = y;
		}
		
		return chart.getScreenToData(new PVector(mapX, mapY));
	}
	
	/** 
	 * Overridden to build the chart. 
	 * 
	 * @see {@link PApplet#setup()}
	 */
	public void setup()
	{
		Dimension d = new Dimension(1000, 300);
		setSize(d);
		smooth();
		textFont(createFont("Helvetica", 10));
		textSize(10);
		chart.transposeAxes(false);
		chart.setXAxisLabel("Bins");
		chart.setYAxisLabel("Frequency");
		chart.setLineColour(Color.black.getRGB());
		chart.setLineWidth(2);
		//Scale line graph to use same space as bar graph.
		chart.setMinY(chart.getMinX()); 
		chart.setMaxY(chart.getMaxY());  
		chart.setPointSize(4);
		chart.drawBackground(true);
		//chart.setRGB(true, -1.1, 1.0);
		chart.showXAxis(true);
		chart.showYAxis(true);
	}

	/** Draws the histogram.
	 *
	 * @see {@link PApplet#draw()}
	 */
	public void draw()
	{
		if (chart == null) return;

		background(windowsBackground.getRGB());
		// Draw the bar chart first, then overlay the line chart.
		if (displayHeatMap && map != null) {
			Color c = new Color(102, 102, 102);
			pushMatrix();
			translate(translateX, translateY);
			map.draw();
			popMatrix();
		}
		if(displayHeatMap)
		{
			pushMatrix();
			translate(data.getWidth()+translateX, 0);
			chart.draw(1, 1, width-2-data.getWidth()-borderWidth, height-2);
			popMatrix();

			button = new Rectangle(data.getWidth()+translateX,height-2,20,20);
			drawButton();
		}
		else
		{
			pushMatrix();
			chart.draw(1, 1, width-2-borderWidth, height-2);
			popMatrix();
		}
	}
	
	private void drawButton()
	{
		
	}

	public void mouseReleased()
	{
		pick(mouseX, mouseY);
	}

	/**
	 * Get the stats of the bin.
	 * @param bin The bin.
	 * @return The stats as a map.
	 */
	public Map<String, Double> getBinStats(int bin)
	{
		return chart.getBinStats(bin);
	}
	
	/**
	 * Show the value at position x as picked.
	 * @param point See above.
	 */
	public void pick(int x, int y)
	{
		if(chartClicked(x,y))
		{
			int chartX, chartY;
			if(displayHeatMap)
			{
				chartX = x-translateX-data.getWidth();
				chartY = y-translateY;
			}
			else
			{
				chartX = x;
				chartY = y;
			}
			int binPicked = chart.pick(new PVector(chartX,chartY));
			firePropertyChange(CHARTSELECTED_PROPERTY, null, binPicked);
		}
		if(heatMapClicked(x, y))
		{
			
		}
		
	}
}
