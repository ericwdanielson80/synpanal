package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Polygon;
import java.awt.geom.*;
import java.awt.Graphics2D;
import java.io.*;
	
/**
 * Domain object representing a single cell body (soma) outlined on the
 * image. Holds the user-drawn Polygon cellOutline and its derived
 * Shape/Area form, three-channel intensity totals plus a
 * pixel-area tally, group membership, and the per-channel
 * "watchColor" flags that decide which colors to measure. Implements
 * Runnable so intensity computation can be dispatched to a worker
 * thread, and Savable for serialisation through IoContainer.
 */
public class CellBody implements Savable, Runnable{
Polygon cellOutline;
public Area cellArea;
int[] cellIntensity = new int[4];
public boolean[] watchColor;
public Group groupMember;
	
//for threading
int[] thresholdT;
int[] rT;
int iHT;
PunctaCounter pT;
	
	
	/**
	 * Constructs an empty CellBody with per-channel watchColor
	 * flags color and Group membership group; the polygon outline
	 * must be populated separately via addAreaPoints.
	 */
	public CellBody(boolean[] color, Group group)
	{
		watchColor = color;
		groupMember = group;
	}
	
	/**
	 * Deserialising constructor that immediately calls Load so
	 * every field is initialised from the stream di using
	 * IoContainer i, the file-format version, and the groupList
	 * array used to resolve the stored integer group index.
	 */
	public CellBody(DataInputStream di,IoContainer i,int version,Group[] groupList)
	{
		Load(di, i,version,groupList);
	}
	
	/**
	 * Starts a fresh outline by allocating a new Polygon and
	 * seeding it with vertex (x, y).
	 */
	public void generatePolygon(int x, int y)
	{
		cellOutline = new Polygon();
		cellOutline.addPoint(x, y);
	}
	
	/**
	 * Appends a vertex (x, y) to cellOutline, creating the
	 * polygon via generatePolygon when this is the first point.
	 */
	public void addAreaPoints(int x,int y)
	{
		if(cellOutline == null)
			generatePolygon(x,y);
		else
			cellOutline.addPoint(x, y);
	}
	
	/** Wraps the completed outline polygon in a geometry Area for hit-testing and rendering. */
	public void finishArea()
	{
		cellArea = new Area(cellOutline);
	}
	
	/**
	 * Attempts to shorten the xpoints and ypoints arrays of the
	 * outline polygon by one element (undoing the most recent
	 * vertex added during drawing).
	 */
	public void removeLastPoint()	
	{
		System.arraycopy(cellOutline.xpoints,0, cellOutline.xpoints,0,cellOutline.xpoints.length - 1);
		System.arraycopy(cellOutline.ypoints,0, cellOutline.ypoints,0,cellOutline.ypoints.length - 1);
	}
	
	/** Fills the filled cellArea on Graphics2D g with the current color. */
	public void paintCell(Graphics2D g)
	{
		g.fill(cellArea);
	}
	
	/** Strokes the polygon outline on g; no-op when outline is not yet built. */
	public void paintOutline(Graphics2D g)
	{
		if(cellOutline != null)
		g.drawPolygon(cellOutline);
	}
	
	/**
	 * Stashes arguments into instance fields so run() can later
	 * perform the intensity computation without further
	 * parameters. The parameters are the per-channel threshold
	 * array, the flattened raster r, the image height iH, and the
	 * PunctaCounter p used as the work helper.
	 */
	public void setupThread(int[] threshold,int[] r,int iH,PunctaCounter p)
	{
		thresholdT = threshold;
		rT = r;
		iHT = iH;
		pT= p;
	}
	
	/**
	 * Runnable entry point. Invokes generateCellIntensity with
	 * the previously stashed fields and a fresh PunctaCounter
	 * copy, then nulls the field references so the worker state
	 * can be garbage collected.
	 */
	public void run()
	{
		this.generateCellIntensity(thresholdT, rT, iHT, new PunctaCounter(pT));
		thresholdT = null;
		rT = null;
		//iHT = null;
		pT = null;
	}
	
	/**
	 * Computes the integrated intensity of the cell area for each
	 * watched channel. temp is a {totalIntensity,pixelCount}
	 * pair returned by p.getCellBodyIntensity; that call is
	 * issued for channels 0, 1 and 2 but only when watchColor
	 * marks them active. The per-channel totals are written into
	 * cellIntensity[0..2] and the pixel count (shared across
	 * channels) ends up in cellIntensity[3]. The parameters are
	 * the per-channel thresholds, the packed raster r, the image
	 * height iH, and the PunctaCounter helper p that does the
	 * actual pixel scan.
	 */
	public void generateCellIntensity(int[] threshold,int[] r,int iH,PunctaCounter p)
	{
		int[] temp = new int[] {0,0};
		if(watchColor[0])
			{
			temp = p.getCellBodyIntensity(r, iH, cellArea.getBounds(),0, cellArea, threshold[0]);
			cellIntensity[0] = temp[0];			
			}
		if(watchColor[1])
		{
			temp = p.getCellBodyIntensity(r, iH, cellArea.getBounds(),1, cellArea, threshold[1]);
			cellIntensity[1] = temp[0];			
			}
		if(watchColor[2])
		{
			temp = p.getCellBodyIntensity(r, iH, cellArea.getBounds(),2, cellArea, threshold[2]);
			cellIntensity[2] = temp[0];			
		}
		cellIntensity[3] = temp[1]; //the number of pixels in the area
	}
	
	/**
	 * Returns the integrated intensity of the cell in the given
	 * color channel divided by the calibrated area (pixels scaled
	 * by calibration squared). The color parameter selects the
	 * intensity bin, calibration is the microns-per-pixel factor.
	 */
	public double getAveCellIntensity(int color,double calibration)
	{
		return cellIntensity[color] / (cellIntensity[3] * calibration * calibration);
	}
	
	/** Returns the raw integrated intensity stored for the given color. */
	public double getIntegratedCellIntensity(int color)
	{
		return cellIntensity[color];
	}
	
	/**
	 * Serialises the cell body to ds via IoContainer i. The
	 * group-member value, watchColor boolean array, cellIntensity
	 * int array, polygon vertex count, and xpoints/ypoints
	 * coordinate arrays are written in sequence; the block
	 * comment enumerates this layout for reference.
	 */
	public void Save(DataOutputStream ds, IoContainer i)
	{
		//save group
		//save watchColor
		//save CellIntensity
		//polygon
		//save npoints
		//save xlist
		//save ylist
		
		i.writeInt(ds, "CellBody write group", groupMember.getValue());
		i.writeBooleanArray(ds,"Cell body write watchColor",watchColor);
		i.writeIntArray(ds,"CellBody write Intensity", cellIntensity);
		i.writeInt(ds,"Cell body write n", cellOutline.npoints);
		i.writeIntArray(ds,"Cell Body write xlist", cellOutline.xpoints);
		i.writeIntArray(ds,"Cell Body write ylist", cellOutline.ypoints);
		
	}
	
	/**
	 * Deserialises the cell body from di by mirroring Save. The
	 * group index is resolved into groupList to restore
	 * groupMember; watchColor and cellIntensity are read as
	 * arrays; then the polygon is rebuilt from n, x, y and
	 * finishArea promotes it to a geometry Area. The version
	 * parameter is reserved for future format changes; always
	 * returns null.
	 */
	public Object Load(DataInputStream di, IoContainer i,int version,Group[] groupList)
	{
		groupMember = groupList[i.readInt(di,"Cell body read groupMember")];
		watchColor = i.readBooleanArray(di,"Cellbody read watchColor");
		cellIntensity = i.readIntArray(di,"CellBody read Cell Intensity");
		
		int n = i.readInt(di,"Cell body read n");
		int[] x = i.readIntArray(di,"Cell body read xlist");
		int[] y = i.readIntArray(di,"Cell body read ylist");
		cellOutline = new Polygon(x,y,n);
		finishArea();
		return null;
	}
}
	