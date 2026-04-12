package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.Graphics2D;
import java.io.DataOutputStream;
import java.io.DataInputStream;
/**
 * Variant of Dendrite whose analysis region is an arbitrary polygon the
 * user draws point by point rather than a fixed-width line. The polygon
 * is accumulated into complexArea and, once closed, is converted into
 * the parent class's dendriteArea field so downstream code (puncta,
 * spine, intensity routines) treats it the same as an ordinary
 * dendrite. Save/Load include all the base fields plus the polygon.
 */
public class ComplexDendrite extends Dendrite{
Polygon complexArea;
	/**
	 * Invokes the Dendrite superclass constructor with a sentinel
	 * width of -1 (width is irrelevant for polygon regions), passing
	 * through the watch-color mask color and the Group membership.
	 * The w parameter is ignored in favour of the sentinel width.
	 */
	public ComplexDendrite(dendriteWidth w,boolean[] color,Group group)
	{
		super(new dendriteWidth(-1),color,group);
	}
	
	/** No-op override: complex dendrites do not have a scalar width. */
	public void setWidth(dendriteWidth w)
	{
	
	}
	
	/** No-op override: the area is built in finishArea once the polygon is complete. */
	public void makeArea()
	{
		
	}
	
	/**
	 * Initialises complexArea with a fresh Polygon and seeds it with
	 * the first vertex (x, y).
	 */
	public void generatePolygon(int x, int y)
	{
		complexArea = new Polygon();
		complexArea.addPoint(x, y);
	}
	
	/**
	 * Appends a new vertex (x, y) to the in-progress polygon. If the
	 * polygon has not been created yet, generatePolygon is called
	 * instead to set it up with the first point.
	 */
	public void addAreaPoints(int x,int y)
	{
		if(complexArea == null)
			generatePolygon(x,y);
		else
			complexArea.addPoint(x, y);
	}
	
	/**
	 * Closes the polygon by wrapping complexArea in a Geometry Area
	 * and storing it in the inherited dendriteArea so normal
	 * area-based computations can proceed.
	 */
	public void finishArea()
	{
		dendriteArea = new Area(complexArea);
	}
	
	/**
	 * Attempts to shrink the xpoints and ypoints coordinate arrays by
	 * one element using System.arraycopy (used to undo the last vertex
	 * addition while drawing).
	 */
	public void removeLastPoint()	
	{
		System.arraycopy(complexArea.xpoints,0, complexArea.xpoints,0,complexArea.xpoints.length - 1);
		System.arraycopy(complexArea.ypoints,0, complexArea.ypoints,0,complexArea.ypoints.length - 1);
	}
	
	/**
	 * Delegates to the base Dendrite painter to draw the standard
	 * shaft, then draws the polygon outline on top using the provided
	 * Graphics2D g when complexArea has been built.
	 */
	public void paintDendriteShaft(Graphics2D g)
	{
		super.paintDendriteShaft(g);
		if(complexArea != null)
			g.drawPolygon(complexArea);
	}
	
	/**
	 * Serialises this complex dendrite to ds via the helpers on the
	 * parent class. It first writes the dendrite type (1 for complex),
	 * then the vertex/watchColor/group-member/spine-info blocks, then
	 * the polygon. The IoContainer i wraps the stream and centralises
	 * error reporting.
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
     * Reconstructs a complex dendrite from the stream. xList and yList
     * are read as int arrays, watchColor as a boolean array, and
     * groupMember is resolved by reading an integer index into the
     * provided groupList. spineNumber is read; if positive, spineNumber
     * SpineInfo objects are created with null constructor args and each
     * is asked to Load its own fields. Finally loadPolygon rebuilds
     * complexArea and finishArea promotes it to a Geometry Area. The
     * version parameter is passed through to per-field load helpers.
     * Always returns null.
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
    
    /**
     * Writes the dendrite type discriminator to the stream: 0 for a
     * normal dendrite, 1 for this complex subclass.
     */
    public void saveType(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex    	
    	i.writeInt(ds, new String("complex dendrite type"), 1);
    }
    
    /**
     * Writes the in-memory Polygon: the vertex count npoints, and
     * both integer coordinate arrays xpoints and ypoints.
     */
    public void savePolygon(DataOutputStream ds, IoContainer i)
    {
    	//save npoints
    	//save xlist
    	//save ylist
    	i.writeInt(ds, new String("complex dendrite npoints"), complexArea.npoints);
    	i.writeIntArray(ds, new String("complex dendrite xpoints"), complexArea.xpoints);
    	i.writeIntArray(ds, new String("complex dendrite ypoints"), complexArea.ypoints);
    }
    
    /**
     * Reads npoints (n), xpoints (x) and ypoints (y) from the stream
     * and rebuilds complexArea as a new Polygon(x, y, n). The version
     * argument is accepted for future format changes.
     */
    public void loadPolygon(DataInputStream di, IoContainer i,int version)
{
	int n = i.readInt(di, "complex dendrite npoints");
	int[] x = i.readIntArray(di,"complex dendrite xpoints");
	int[] y = i.readIntArray(di,"complex dendrite ypoints");
	complexArea = new Polygon(x,y,n);
	
		
}
	
}