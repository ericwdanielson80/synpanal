package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.awt.Color;

/**
 * Specialized ComplexDendrite representing a single concentric ring used
 * in Sholl analysis, where dendritic branches and puncta are counted
 * within a ring at a fixed radius from a chosen center point. Instead of a
 * free-form polygon, its dendriteArea is the annular region between
 * radius1 (outer) and radius2 (inner). It also provides specialized
 * drawing, save/load, and ignore-filter helpers that operate on puncta
 * intersecting the ring.
 */
public class ShollDendrite extends ComplexDendrite {
Point circleCenter;
int radius1;
int radius2;
	
	
	/**
	 * Builds a Sholl ring with its center at cC, outer radius oR, and ring
	 * width rW (so radius2 = radius1 - rW). The dendriteWidth w is ignored
	 * because the ring area is defined by the two radii rather than a line
	 * stroke. The parameters color and group pass through to the
	 * ComplexDendrite superclass. finishArea is invoked to materialize the
	 * annular shape.
	 */
	public ShollDendrite(dendriteWidth w,boolean[] color,Group group, Point cC, int oR, int rW)
	{
		super(new dendriteWidth(-1),color,group);
		circleCenter = cC;
		radius1 = oR;
		radius2 = radius1 - rW;
		
		this.finishArea();
	}
	
	/**
	 * Experimental convenience constructor that creates a default Sholl ring
	 * at image center (512,512) with outer radius 200 and inner radius 180.
	 * Parameters w, color, and group pass through to the superclass.
	 */
	public ShollDendrite(dendriteWidth w,boolean[] color,Group group,int experimental)
	{
		super(new dendriteWidth(-1),color,group);
		radius1 = 200;
		radius2 = 180;
		circleCenter = new Point(512,512);
		this.finishArea();
	}
	
	
	
	/** No-op: width is irrelevant for the Sholl ring geometry. */
	public void setWidth(dendriteWidth w)
	{
		
	}
	
	/** No-op; the dendriteArea is instead produced by finishArea from the two radii. */
	public void makeArea()
	{
		
	}
	
	/** No-op because the Sholl area is defined by two circles, not by a clicked polygon. */
	public void generatePolygon(int x, int y)
	{
		
	}
	
	/** No-op; area points are not added interactively for a Sholl ring. */
	public void addAreaPoints(int x,int y)
	{
		
	}
	
	/** Returns the outer radius as the nominal "length" for reporting purposes; calibration is ignored here. */
	public float getLength(double calibration)
	{
		return radius1;
	}
	
	/**
	 * Builds the annular dendriteArea by subtracting an inner ellipse
	 * (centered on circleCenter with radius radius2) from an outer ellipse
	 * with radius radius1. The locals a and b are the raw ellipses; a1 and
	 * a2 are Area copies used to perform the subtraction.
	 */
	public void finishArea()
	{
		Ellipse2D a = new Ellipse2D.Double(circleCenter.x - radius1,circleCenter.y - radius1,radius1 * 2,radius1 * 2);
		Ellipse2D b = new Ellipse2D.Double(circleCenter.x - radius2,circleCenter.y - radius2,radius2 * 2,radius2 * 2);		
		Area a1 = new Area(a);
		Area a2 = new Area(b);		
		a1.subtract(a2);		
		dendriteArea = a1;
				
	}
	
	/** No-op; a Sholl ring has no vertex list to pop. */
	public void removeLastPoint()	
	{
		
	}
	
	/** Delegates to the ComplexDendrite painter and then outlines complexArea when it is present. */
	public void paintDendriteShaft(Graphics2D g)
	{
		super.paintDendriteShaft(g);
		if(complexArea != null)
			{
			g.drawPolygon(complexArea);
			}
	}
	
	/**
	 * Serializes the Sholl dendrite by writing its type marker, vertex
	 * lists, watched colors, group membership, spine info, and polygon
	 * outline in sequence through the IoContainer helper i.
	 */
	public void Save(DataOutputStream ds, IoContainer i)
    {
    	//save type 0 normal 1 complex
    	//save xList
    	//save yList
    	//save watchColor
    	//save groupMember
    	//save Polygon
    	//save SpineInfo
    	saveType(ds,i);
    	saveVertex(ds,i);
    	saveWatchColor(ds,i);
    	saveGroupMember(ds,i);    	
    	saveSpineInfo(ds,i);
    	savePolygon(ds,i);    	
    	
    }
    
    /**
     * Deserializes the Sholl dendrite from the input stream, restoring the
     * xList/yList vertex arrays, watched colors, group membership, any
     * spine data, and finally the complex polygon. Calls finishArea to
     * rebuild the annular shape. Parameters di and i are the input stream
     * and IO helper, version selects which fields are present, and
     * groupList resolves group IDs back to Group references.
     */
    public Object Load(DataInputStream di, IoContainer i,int version,Group[] groupList)
    {
//    	load xList
    	//load yList
    	//load watchColor
    	//load groupMember
    	//dendriteWidth already loaded
    	//load SpineInfo
    	//load polygoninfo
    	xList = i.readIntArray(di, "loading xList");
    	yList = i.readIntArray(di, "loading yList");
    	watchColor = i.readBooleanArray(di, "load watchColor");
    	groupMember = groupList[i.readInt(di, "loading groupMember")];
    	spineNumber = i.readInt(di, "loading spine number");
    	if(spineNumber > 0)
    	{
    		spineData = new SpineInfo[spineNumber];
    		for(int k = 0; k < spineNumber; k++)
    		{
    			spineData[k] = new SpineInfo(null,null,null,0);
    			spineData[k].Load(di, i,version,groupList);    		
    		}
    	}
    	loadPolygon(di,i,version);
    	finishArea();
    	
    	return null;
    }
    
    /** Writes the type marker (1 meaning "complex dendrite") so the loader can pick the correct subclass. */
    public void saveType(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex    	
    	i.writeInt(ds, new String("complex dendrite type"), 1);
    }
    
    /** Writes complexArea's point count and the x/y vertex arrays for later reconstruction. */
    public void savePolygon(DataOutputStream ds, IoContainer i)
    {
    	//save npoints
    	//save xlist
    	//save ylist
    	i.writeInt(ds, new String("complex dendrite npoints"), complexArea.npoints);
    	i.writeIntArray(ds, new String("complex dendrite xpoints"), complexArea.xpoints);
    	i.writeIntArray(ds, new String("complex dendrite ypoints"), complexArea.ypoints);
    }
    
    /** Reads the point count and x/y vertex arrays from the input stream and rebuilds complexArea as a java.awt.Polygon. */
    public void loadPolygon(DataInputStream di, IoContainer i,int version)
{
	int n = i.readInt(di, "complex dendrite npoints");
	int[] x = i.readIntArray(di,"complex dendrite xpoints");
	int[] y = i.readIntArray(di,"complex dendrite ypoints");
	complexArea = new Polygon(x,y,n);
	
	
}    
    
    
    /** Puncta-counting hook for a Sholl dendrite; intentionally left blank because the superclass implementation is skipped in favor of the ring-specific logic handled elsewhere. */
    public void countPuncta(int[] threshold,int[] stthreshold, int[] r,int iH,PunctaCounter p,neuronToolKit ntk,int imageWidth, int imageHeight)
    {
    	//super.countPuncta(threshold, threshold, r, iH, p, ntk, imageWidth, imageHeight);
    	//this.ShollFilter(1);
    	
    	//auto ignore puncta that do not intersect with outside and inside radius...
    }
    
    /** Threshold-counting hook mirror of countPuncta; left blank so the ring-specific handling is used instead. */
    public void countPunctaT(int[] threshold,int[] stthreshold, int[] r,int iH,PunctaCounter p,neuronToolKit ntk,int imageWidth, int imageHeight)
    {
    	//super.countPunctaT(threshold, threshold, r, iH, p, ntk, imageWidth, imageHeight);
    	//this.ShollFilter(1);
    }
    
    /** Placeholder filter kept for API compatibility; the full ring-intersection filter is implemented in the Graphics2D-based overload. */
    public void ShollFilter(int color)
    {
    	//System.out.println("doShollFilter1");
    	/*int radius3 = radius1 - 1;
		
		Ellipse2D a = new Ellipse2D.Double(circleCenter.x - radius1,circleCenter.y - radius1,radius1 * 2,radius1 * 2);
		Ellipse2D b = new Ellipse2D.Double(circleCenter.x - radius3,circleCenter.y - radius3,radius3 * 2,radius3 * 2);		
		Area ring1 = new Area(a);
		Area a2 = new Area(b);		
		ring1.subtract(a2);	
		
		int radius4 = radius2 + 1;
		
		a = new Ellipse2D.Double(circleCenter.x - radius4,circleCenter.y - radius4,radius4 * 2,radius4 * 2);
		b = new Ellipse2D.Double(circleCenter.x - radius2,circleCenter.y - radius2,radius2 * 2,radius2 * 2);	
		Area ring2 = new Area(a);
		a2 = new Area(b);		
		ring2.subtract(a2);	
		
				
    	Area test;
    	Area test2;
    	for(int k = 0; k < myPuncta[color].myPuncta.length; k++)
    	{    		
    		myPuncta[color].myPuncta[k].bC.isIgnored = false;
    		
    		test = new Area(myPuncta[color].myPuncta[k].border);
    		test2 = new Area(myPuncta[color].myPuncta[k].border);
    		test.subtract(ring1); 
    		if(test.equals(test2)) //if they are equal then ring 1 could not be subtracted from the area threfore does not intersect
    			myPuncta[color].myPuncta[k].bC.isIgnored = true; 
    		
    		test = new Area(myPuncta[color].myPuncta[k].border);
    		test.subtract(ring2);	
    		if(test.equals(test2)) //if they are equal then ring 2 could not be subtracted from the area threfore does not intersect
    			myPuncta[color].myPuncta[k].bC.isIgnored = true;
    		
    		
    	}*/
    }
    
    /**
     * Paints the two one-pixel-wide "sentinel" rings (ring1 just inside the
     * outer boundary and ring2 just outside the inner boundary) into the
     * graphics context g, encoding the dendrite number in the red and green
     * channels respectively, then fills each puncta of the chosen color
     * into g2 with a grayscale encoding of its index. Downstream code can
     * read back whether each puncta overlaps both rings and thus
     * effectively crosses the Sholl annulus. The locals radius3/radius4
     * are the slightly shifted radii defining the one-pixel rings; ring1
     * and ring2 are the resulting annuli, and pL is the puncta list.
     */
    public void ShollFilter(Graphics2D g, Graphics2D g2, int dendriteNum,int color)
    {
    	//System.out.println("doShollFilter2");
    	int radius3 = radius1 - 1;
		
		Ellipse2D a = new Ellipse2D.Double(circleCenter.x - radius1,circleCenter.y - radius1,radius1 * 2,radius1 * 2);
		Ellipse2D b = new Ellipse2D.Double(circleCenter.x - radius3,circleCenter.y - radius3,radius3 * 2,radius3 * 2);		
		Area ring1 = new Area(a);
		Area a2 = new Area(b);		
		ring1.subtract(a2);	
		
		int radius4 = radius2 + 1;
		
		a = new Ellipse2D.Double(circleCenter.x - radius4,circleCenter.y - radius4,radius4 * 2,radius4 * 2);
		b = new Ellipse2D.Double(circleCenter.x - radius2,circleCenter.y - radius2,radius2 * 2,radius2 * 2);	
		Area ring2 = new Area(a);
		a2 = new Area(b);		
		ring2.subtract(a2);	
		
		g.setColor(new Color(dendriteNum,0,0));
		g.fill(ring1);
		g.setColor(new Color(0,dendriteNum,0));
		g.fill(ring2);
		Puncta pL[] = this.myPuncta[color].myPuncta;
		for(int k = 0; k < pL.length; k++)
		{			
			g2.setColor(new Color(k+1,k+1,k+1));
			g2.fill(pL[k].border);
		}
    }
    
    /** Placeholder that would mark puncta as ignored when they fail to intersect both sentinel rings; currently retained for API use but left as a no-op. */
    public void ShollIgnore(int[] pL, int color)
    {
    	//System.out.println("doShollIgnore");
    	/*for(int k = 0; k < pL.length / 2; k++)
    	{
    		if(pL[k] == 0 || pL[2*k] == 0)
    			myPuncta[color].myPuncta[k].bC.isIgnored = true;
    	}*/
    }
    
       
    
}
