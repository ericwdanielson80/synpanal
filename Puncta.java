package neuron_analyzer;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.awt.image.Raster;
import java.awt.Rectangle;
import java.awt.Point;

public class Puncta {
int intensity;
int area; //in pixels
Polygon border;
int x;
int y;


BooleanContainer bC;
OwnershipObject oO;

public Puncta(int i, int a, Polygon p)
{
	intensity = i;
	area = a;
	border = p;
	x = p.getBounds().x;
	y = p.getBounds().y;
	bC = new BooleanContainer(false,false);
	
}
public Puncta(boolean b)
{
	bC = new BooleanContainer(false,b);
}

public void drawPuncta(Graphics2D g,int dendrite, int puncta)
{
	if(bC.isIgnored())
		return;
	if(bC.isSelected())
		g.setColor(Color.pink);	
	if(border!= null)
	g.drawPolygon(border);
	g.drawString(new String(dendrite + "." + puncta),x, y);	
	
	if(bC.isSelected())
		g.setColor(Color.yellow);
}

public void pushSelected()
{
	bC.pushSelected();
}

public void pushIgnored()
{
	bC.pushIgnored();
}

public boolean isIgnored()
{
	return bC.isIgnored();
}

public boolean isSelected()
{
	return bC.isSelected();
}

public void autoIgnore(int[] thresholds, IgnoreCriteria criteria, Raster raster ){
	int x = border.getBounds().x;
	int y = border.getBounds().y;
	int width = border.getBounds().width;
	int height = border.getBounds().height;	
	Point p = new Point(0,0);
	
	int redPixels = 0; //number of red Pixels greater than specified threshold 
	int greenPixels = 0;
	int bluePixels = 0;
	
	int r;
	int g;
	int b;
	
	for(int k = y; k < y + height; k++)
	{
		for(int j = x; j < x + width; j++)
		{
			p.x = j;
			p.y = k;
			if(border.contains(p))
			{
				r = raster.getSample(j, k, 0);
				g = raster.getSample(j, k, 1);
				b = raster.getSample(j, k, 2);
				
				if(r >= thresholds[0])
					redPixels++;
				if(g >= thresholds[1])
					greenPixels++;
				if(b >= thresholds[2])
					bluePixels++;
			}
		}
	}
	redPixels = (redPixels * 100) / area;
	greenPixels = (greenPixels * 100) / area;
	bluePixels = (bluePixels * 100) / area;
	int[] input = {redPixels, greenPixels, bluePixels};
	if(criteria.testCriteria(input))
	{		    
			bC.isIgnored = true;			
	}
	
	
	
}

	public void invertIgnored()
	{
		if(bC.isIgnored)
			bC.isIgnored = false;
		else
			bC.isIgnored = true;
	}
	
	public void restoreIgnored()
	{
		bC.isIgnored = false;
	}

}
