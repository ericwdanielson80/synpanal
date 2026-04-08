package neuron_analyzer;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.Graphics2D;
import java.io.DataOutputStream;
import java.io.DataInputStream;
public class ComplexDendrite extends Dendrite{
Polygon complexArea;
	public ComplexDendrite(dendriteWidth w,boolean[] color,Group group)
	{
		super(new dendriteWidth(-1),color,group);
	}
	
	public void setWidth(dendriteWidth w)
	{
		
	}
	
	public void makeArea()
	{
		
	}
	
	public void generatePolygon(int x, int y)
	{
		complexArea = new Polygon();
		complexArea.addPoint(x, y);
	}
	
	public void addAreaPoints(int x,int y)
	{
		if(complexArea == null)
			generatePolygon(x,y);
		else
			complexArea.addPoint(x, y);
	}
	
	public void finishArea()
	{
		dendriteArea = new Area(complexArea);
	}
	
	public void removeLastPoint()	
	{
		System.arraycopy(complexArea.xpoints,0, complexArea.xpoints,0,complexArea.xpoints.length - 1);
		System.arraycopy(complexArea.ypoints,0, complexArea.ypoints,0,complexArea.ypoints.length - 1);
	}
	
	public void paintDendriteShaft(Graphics2D g)
	{
		super.paintDendriteShaft(g);
		if(complexArea != null)
			g.drawPolygon(complexArea);
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

}