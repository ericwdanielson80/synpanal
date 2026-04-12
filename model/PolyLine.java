package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Container for a series of (x, y) integer points with cached scaling state.
 * Used primarily by lineChart to supply the scaled arrays that are handed to
 * Graphics.drawPolyline. On construction the bounding box of the series is
 * computed; subsequent scaleX/scaleY calls memoize the last applied scale so
 * recomputing is skipped when the factor has not changed.
 */
public class PolyLine {
int[] xList;
int[] yList;
int[] xListS;
int[] yListS;
double lastXScale = 1;
double lastYScale = 1;
public int xMin;
public int xMax;
public int yMin;
public int yMax;
	/**
	 * Builds a poly-line from parallel x and y arrays. The x and y parameters
	 * are the coordinate lists; they are retained as both the unscaled
	 * reference (xList, yList) and as the current scaled buffers (xListS,
	 * yListS) since at construction no scaling has been applied. The
	 * constructor then walks the arrays, seeding xMin/xMax/yMin/yMax from
	 * element zero and updating them with each point to capture the series'
	 * bounding box.
	 */
	public PolyLine(int[] x, int[] y)
	{
		xList = x;
		yList = y;
		xListS = x;
		yListS = y;
		xMin = xList[0];
		xMax = xList[0];
		yMin = yList[0];
		yMax = yList[0];
		for(int k = 0; k < x.length; k++)
		{
			if(x[k] < xMin)
				xMin = x[k];
			if(x[k] > xMax)
				xMax = x[k];
			if(y[k] < yMin)
				yMin = y[k];
			if(y[k] > yMax)
				yMax = y[k];
		}
	}
	
	/**
	 * Returns the x coordinates scaled by the given factor, caching the
	 * result. The scale parameter is the multiplier; if it matches the last
	 * applied scale the cached xListS is returned directly, otherwise
	 * scaleXList is invoked to rebuild the scaled buffer before returning.
	 */
	public int[] scaleX(double scale)
	{
		if(lastXScale == scale)
			return xListS;
		
		scaleXList(scale);
		return xListS;
	}
	
	/**
	 * Multiplies every x coordinate by the given scale and writes the result
	 * into xListS, updating lastXScale to the new factor. The scale parameter
	 * is cast to double during the multiplication and truncated back to int
	 * per element.
	 */
	private void scaleXList(double scale)
	{
		lastXScale = scale;
		for(int k = 0; k < xList.length; k++)
		{
			xListS[k] = (int)((double)xList[k] * scale);
		}
	}
	
	/**
	 * Returns the y coordinates scaled by the given factor, caching the
	 * result. The scale parameter is the multiplier; if it matches
	 * lastYScale the cached yListS is returned, otherwise scaleYList is
	 * invoked to rebuild the scaled buffer.
	 */
	public int[] scaleY(double scale)
	{
		if(lastYScale == scale)
			return yListS;
		
		scaleYList(scale);
		return yListS;
	}
	
	/**
	 * Multiplies every y coordinate by the supplied scale and stores the
	 * rounded result into yListS, recording the factor in lastYScale.
	 */
	private void scaleYList(double scale)
	{
		lastYScale = scale;
		for(int k = 0; k < yList.length; k++)
		{
			yListS[k] = (int)((double)yList[k] * scale);
		}
	}
	
	/**
	 * Returns the number of points in the series, i.e. the length of xList.
	 */
	public int getLength()
	{
		return xList.length;
	}

	/**
	 * Dumps the y coordinates of the series to standard output on one line,
	 * each value followed by five spaces and terminated by a newline. Used
	 * for ad-hoc debugging of profile data.
	 */
	public void printData()
	{
		for(int k = 0; k < yList.length; k++)
		{
			System.out.print(yList[k]);
			System.out.print("     ");
		}
		System.out.println();
	}
}
