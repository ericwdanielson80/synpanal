package neuron_analyzer;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.awt.Color;

public class ShollDendrite extends ComplexDendrite {
Point circleCenter;
int radius1;
int radius2;
	
	
	public ShollDendrite(dendriteWidth w,boolean[] color,Group group, Point cC, int oR, int rW)
	{
		super(new dendriteWidth(-1),color,group);
		circleCenter = cC;
		radius1 = oR;
		radius2 = radius1 - rW;
		
		this.finishArea();
	}
	
	public ShollDendrite(dendriteWidth w,boolean[] color,Group group,int experimental)
	{
		super(new dendriteWidth(-1),color,group);
		radius1 = 200;
		radius2 = 180;
		circleCenter = new Point(512,512);
		this.finishArea();
	}
	
	
	
	public void setWidth(dendriteWidth w)
	{
		
	}
	
	public void makeArea()
	{
		
	}
	
	public void generatePolygon(int x, int y)
	{
		
	}
	
	public void addAreaPoints(int x,int y)
	{
		
	}
	
	public float getLength(double calibration)
	{
		return radius1;
	}
	
	public void finishArea()
	{
		Ellipse2D a = new Ellipse2D.Double(circleCenter.x - radius1,circleCenter.y - radius1,radius1 * 2,radius1 * 2);
		Ellipse2D b = new Ellipse2D.Double(circleCenter.x - radius2,circleCenter.y - radius2,radius2 * 2,radius2 * 2);		
		Area a1 = new Area(a);
		Area a2 = new Area(b);		
		a1.subtract(a2);		
		dendriteArea = a1;
				
	}
	
	public void removeLastPoint()	
	{
		
	}
	
	public void paintDendriteShaft(Graphics2D g)
	{
		super.paintDendriteShaft(g);
		if(complexArea != null)
			{
			g.drawPolygon(complexArea);
			}
	}
	
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
    
    public void saveType(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex    	
    	i.writeInt(ds, new String("complex dendrite type"), 1);
    }
    
    public void savePolygon(DataOutputStream ds, IoContainer i)
    {
    	//save npoints
    	//save xlist
    	//save ylist
    	i.writeInt(ds, new String("complex dendrite npoints"), complexArea.npoints);
    	i.writeIntArray(ds, new String("complex dendrite xpoints"), complexArea.xpoints);
    	i.writeIntArray(ds, new String("complex dendrite ypoints"), complexArea.ypoints);
    }
    
    public void loadPolygon(DataInputStream di, IoContainer i,int version)
{
	int n = i.readInt(di, "complex dendrite npoints");
	int[] x = i.readIntArray(di,"complex dendrite xpoints");
	int[] y = i.readIntArray(di,"complex dendrite ypoints");
	complexArea = new Polygon(x,y,n);
	
	
}    
    
    
    public void countPuncta(int[] threshold,int[] stthreshold, int[] r,int iH,PunctaCounter p,neuronToolKit ntk,int imageWidth, int imageHeight)
    {
    	//super.countPuncta(threshold, threshold, r, iH, p, ntk, imageWidth, imageHeight);
    	//this.ShollFilter(1);
    	
    	//auto ignore puncta that do not intersect with outside and inside radius...
    }
    
    public void countPunctaT(int[] threshold,int[] stthreshold, int[] r,int iH,PunctaCounter p,neuronToolKit ntk,int imageWidth, int imageHeight)
    {
    	//super.countPunctaT(threshold, threshold, r, iH, p, ntk, imageWidth, imageHeight);
    	//this.ShollFilter(1);
    }
    
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
