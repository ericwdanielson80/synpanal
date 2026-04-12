package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Simple Swing panel that renders a set of poly-lines on the same axes, used
 * as a lightweight charting component to visualize profile data (e.g. pixel
 * intensity or geometric traces) generated elsewhere in the analyzer. Each
 * PolyLine is drawn in a matching color from the supplied color array and is
 * scaled to the panel's current width and height at paint time.
 */
public class lineChart extends JPanel {
Color[] lineColors; //container for line colors
//Integer chartScale; //Integer for scaling size;
PolyLine[] lineData;
int maxX;
int minX;
int maxY;
int minY;

	/**
	 * Builds a line chart from a list of poly-lines and a parallel list of
	 * colors. The data parameter supplies the PolyLine series to draw and
	 * colors supplies the stroke color for each one. The constructor seeds
	 * minX/maxX/minY/maxY with the first series' bounds and then iterates all
	 * series, widening those bounds so they enclose every point across all
	 * lines; the resulting extrema are used later during paint to compute
	 * scaling factors. The unused local x and y arrays are declared here as
	 * scratch space.
	 */
	public lineChart(PolyLine[] data, Color[] colors)
	{
		lineData = data;
		lineColors = colors;
		 int[] x;
		 int[] y;
		 minX = data[0].xMin;
		 maxX = data[0].xMax;
		 minY = data[0].yMin;
		 maxY = data[0].yMax;
		for(int k = 0; k < lineData.length; k++)
		{
			if(data[k].xMin < minX)
			 minX = data[k].xMin;
			
			if(data[k].xMax > maxX)
			 maxX = data[k].xMax;
			
			if(data[k].yMin < minY)
			 minY = data[k].yMin;
			
			if(data[k].yMax > maxY)
			 maxY = data[k].yMax;
		}
	}
	
	/**
	 * Paints all configured poly-lines onto the panel. Derives an xScale and
	 * yScale by dividing the panel's current width and height by the maximum
	 * x and y values across the series, then iterates through each poly-line,
	 * sets the graphics color, and calls drawPolyline with the PolyLine's
	 * scaled x and y arrays and its length. The Graphics parameter g is the
	 * Swing-supplied context used to draw onto the panel.
	 */
	public void paint(Graphics g)
	{
		double xScale = this.getWidth() / maxX;
		double yScale = this.getHeight() / maxY;
		
		for(int k = 0; k < lineData.length; k++)
		{
			g.setColor(lineColors[k]);
			g.drawPolyline(lineData[k].scaleX(xScale),lineData[k].scaleY(yScale),lineData[k].getLength());
		}
	}
}
