package neuron_analyzer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class lineChart extends JPanel {
Color[] lineColors; //container for line colors
//Integer chartScale; //Integer for scaling size;
PolyLine[] lineData;
int maxX;
int minX;
int maxY;
int minY;
	
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
