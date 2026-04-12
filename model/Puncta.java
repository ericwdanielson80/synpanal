package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.awt.image.Raster;
import java.awt.Rectangle;
import java.awt.Point;

/**
 * Represents a single detected puncta (a bright blob associated with a
 * synaptic marker). A Puncta stores its aggregate intensity and pixel area,
 * a Polygon outline used for drawing and point-in-polygon tests, its upper
 * left corner coordinate, a BooleanContainer of selected/ignored state, and
 * an OwnershipObject used when the puncta is grouped into overlaps. The
 * class supports drawing onto the TIFF panel, toggling selected/ignored
 * state, and an auto-ignore rule driven by per-channel threshold percentages.
 */
public class Puncta {
int intensity;
int area; //in pixels
public Polygon border;
int x;
int y;


public BooleanContainer bC;
OwnershipObject oO;

/**
 * Creates a puncta with explicit measurements. The i parameter is the
 * aggregate intensity, a is the area in pixels, and p is the outline
 * polygon. The (x, y) upper-left corner is computed from the polygon's
 * bounding box and bC is initialized to a fresh BooleanContainer with both
 * selected and ignored set to false.
 */
public Puncta(int i, int a, Polygon p)
{
	intensity = i;
	area = a;
	border = p;
	x = p.getBounds().x;
	y = p.getBounds().y;
	bC = new BooleanContainer(false,false);
	
}
/**
 * Lightweight constructor that only sets up the selected/ignored state,
 * used for placeholder or ghost puncta that do not have a polygon. The b
 * parameter becomes the ignored flag; selected is initialized false.
 */
public Puncta(boolean b)
{
	bC = new BooleanContainer(false,b);
}

/**
 * Draws the puncta outline and its "dendrite.puncta" label onto the given
 * Graphics2D context. Returns early without drawing when the puncta is
 * ignored. Selected puncta are drawn in pink and then restore the graphics
 * color to yellow afterwards, so subsequent draws by the caller pick up
 * the expected color. The dendrite parameter is the parent dendrite
 * number and puncta is this puncta's index within the dendrite, used to
 * form the on-image label.
 */
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

/**
 * Toggles the selected flag by forwarding to BooleanContainer.pushSelected.
 */
public void pushSelected()
{
	bC.pushSelected();
}

/**
 * Toggles the ignored flag by forwarding to BooleanContainer.pushIgnored.
 */
public void pushIgnored()
{
	bC.pushIgnored();
}

/**
 * Returns the current ignored state from the BooleanContainer.
 */
public boolean isIgnored()
{
	return bC.isIgnored();
}

/**
 * Returns the current selected state from the BooleanContainer.
 */
public boolean isSelected()
{
	return bC.isSelected();
}

/**
 * Automatically sets the ignored flag based on per-channel pixel statistics
 * inside the puncta outline. The thresholds parameter is the per-channel
 * intensity cutoff (index 0 red, 1 green, 2 blue), criteria is the
 * IgnoreCriteria rule evaluated after statistics are collected, and raster
 * is the image raster sampled pixel by pixel. The method walks the
 * bounding rectangle (x, y, width, height) pulled from the polygon,
 * counts how many pixels inside the polygon exceed each channel's
 * threshold into redPixels/greenPixels/bluePixels, converts those counts
 * into percentages of the puncta's area, stuffs them into an input array
 * and asks criteria.testCriteria to decide whether bC.isIgnored should be
 * set to true. The Point p is reused for point-in-polygon checks.
 */
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

	/**
	 * Flips the ignored flag: if currently ignored it becomes not-ignored,
	 * and vice versa. Used by the "Invert all puncta" popup action.
	 */
	public void invertIgnored()
	{
		if(bC.isIgnored)
			bC.isIgnored = false;
		else
			bC.isIgnored = true;
	}

	/**
	 * Forces the ignored flag off so this puncta is included in downstream
	 * analyses again.
	 */
	public void restoreIgnored()
	{
		bC.isIgnored = false;
	}

}
