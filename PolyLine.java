package neuron_analyzer;

public class PolyLine {
int[] xList;
int[] yList;
int[] xListS;
int[] yListS;
double lastXScale = 1;
double lastYScale = 1;
int xMin;
int xMax;
int yMin;
int yMax;
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
	
	public int[] scaleX(double scale)
	{
		if(lastXScale == scale)
			return xListS;
		
		scaleXList(scale);
		return xListS;
	}
	
	private void scaleXList(double scale)
	{
		lastXScale = scale;
		for(int k = 0; k < xList.length; k++)
		{
			xListS[k] = (int)((double)xList[k] * scale);
		}
	}
	
	public int[] scaleY(double scale)
	{
		if(lastYScale == scale)
			return yListS;
		
		scaleYList(scale);
		return yListS;
	}
	
	private void scaleYList(double scale)
	{
		lastYScale = scale;
		for(int k = 0; k < yList.length; k++)
		{
			yListS[k] = (int)((double)yList[k] * scale);
		}
	}
	
	public int getLength()
	{
		return xList.length;
	}
	
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
