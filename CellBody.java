package neuron_analyzer;
import java.awt.Polygon;
import java.awt.geom.*;
import java.awt.Graphics2D;
import java.io.*;

public class CellBody implements Savable, Runnable{
Polygon cellOutline;
Area cellArea;
int[] cellIntensity = new int[4];
boolean[] watchColor;
Group groupMember;

//for threading
int[] thresholdT;
int[] rT;
int iHT;
PunctaCounter pT;


	public CellBody(boolean[] color, Group group)
	{
		watchColor = color;
		groupMember = group;
	}
	
	public CellBody(DataInputStream di,IoContainer i,int version,Group[] groupList)
	{
		Load(di, i,version,groupList);
	}
	
	public void generatePolygon(int x, int y)
	{
		cellOutline = new Polygon();
		cellOutline.addPoint(x, y);
	}
	
	public void addAreaPoints(int x,int y)
	{
		if(cellOutline == null)
			generatePolygon(x,y);
		else
			cellOutline.addPoint(x, y);
	}
	
	public void finishArea()
	{
		cellArea = new Area(cellOutline);
	}
	
	public void removeLastPoint()	
	{
		System.arraycopy(cellOutline.xpoints,0, cellOutline.xpoints,0,cellOutline.xpoints.length - 1);
		System.arraycopy(cellOutline.ypoints,0, cellOutline.ypoints,0,cellOutline.ypoints.length - 1);
	}
	
	public void paintCell(Graphics2D g)
	{
		g.fill(cellArea);
	}
	
	public void paintOutline(Graphics2D g)
	{
		if(cellOutline != null)
		g.drawPolygon(cellOutline);
	}
	
	public void setupThread(int[] threshold,int[] r,int iH,PunctaCounter p)
	{
		thresholdT = threshold;
		rT = r;
		iHT = iH;
		pT= p;
	}
	
	public void run()
	{
		this.generateCellIntensity(thresholdT, rT, iHT, new PunctaCounter(pT));
		thresholdT = null;
		rT = null;
		//iHT = null;
		pT = null;
	}
	
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
	
	public double getAveCellIntensity(int color,double calibration)
	{
		return cellIntensity[color] / (cellIntensity[3] * calibration * calibration);
	}
	
	public double getIntegratedCellIntensity(int color)
	{
		return cellIntensity[color];
	}
	
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
