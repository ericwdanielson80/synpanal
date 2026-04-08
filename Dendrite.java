package neuron_analyzer;
import java.awt.geom.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.awt.image.WritableRaster;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.image.Raster;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Dendrite implements Savable, Runnable, DataSource {
int[] xList;//can optimize later
int[] yList;
Polygon outline;
dendriteWidth width;
Area dendriteArea;
Dendrite myParent; //for determining primary secondary...etc
int parentSegment;
int selectedSegment = -1; //for drawing a specific segment
int myIndex; //for saving and loading
Point childPoint;

//groupVector[] puncta = new groupVector[3];
//PunctaGroupData[] punctaData = new PunctaGroupData[3];
//int[] punctaStart = new int[3];
//int[] thresholds = new int[3];
boolean[] watchColor;
Group groupMember;


SpineInfo[] spineData;
int spineNumber = 0;

//threaded stuff
int[] thresholdT;
int[] stthresholdT = new int[] {255,255,255};
int[] rT;
int iHT;
PunctaCounter pT;
neuronToolKit ntkT;
int imageWidthT;
int imageHeightT;
int[] totalRedIntensity = new int[2];
int[] totalBlueIntensity = new int[2];
int[] totalGreenIntensity = new int[2];

BooleanContainer bC;

PunctaContainer[] myPuncta = new PunctaContainer[3];

    public Dendrite(dendriteWidth w,boolean[] color,Group group) {
        width = w;
        watchColor = color;
        groupMember = group;
        myPuncta[0] = new PunctaContainer(new Puncta[0]);        
        myPuncta[1] = new PunctaContainer(new Puncta[0]);
        myPuncta[2] = new PunctaContainer(new Puncta[0]);
        bC = new BooleanContainer(false,false);
    }

    public void setWidth(dendriteWidth k)
    {
        makeArea();
    }

    public void add(int x, int y)
    {
        //add points to a list to make dendrite
        //seems a bit wasteful maybe convert to linked list later
        if(xList == null)
            {
                xList = new int[1];
                yList = new int[1];
                xList[0] = x;
                yList[0] = y;
                return;
            }

        int[] tempX = new int[xList.length + 1];
        int[] tempY = new int[xList.length + 1];

        System.arraycopy(xList,0,tempX,0,xList.length);
        System.arraycopy(yList,0,tempY,0,yList.length);

        tempX[xList.length] = x;
        tempY[xList.length] = y;
        xList = null;
        yList = null;
        //System.gc();
        xList = tempX;
        yList = tempY;
        tempX = null;
        tempY = null;
        //System.gc();
        if(width.intValue() != -1)
        makeArea();
    }

    public void editLast(int x, int y)
    {
        //can't remember the purpose of this
        if(xList == null)
            return;
        if(xList.length < 2)
            {
                add(x,y);
                return;
            }
        xList[xList.length - 1] = x;
        yList[xList.length - 1] = y;
        //makeArea();
    }

    public boolean remove()
    {
        //removes the last vertex of the dendrite
        //doesn't seem to take into account what happens when nothing is left
        if(xList == null)
            {
                return true;
            }

        int[] tempX = new int[xList.length - 1];
        int[] tempY = new int[xList.length - 1];

        System.arraycopy(xList,0,tempX,0,xList.length-1);
        System.arraycopy(yList,0,tempY,0,yList.length-1);

        xList = null;
        yList = null;
        //System.gc();
        xList = tempX;
        yList = tempY;
        tempX = null;
        tempY = null;
        //System.gc();
        if(width.intValue() != -1)
        makeArea();
        return false;
    }

    public void makeArea()
    {
        //seems to need at least two items in the list to function


        if(xList == null)
            return;
        if(xList.length == 1)
            return;      

        makeSimpleArea();
    }

    

    public void makeSimpleArea()
    {
        //this function is for a fixed width dendrite
        lineTools lt = new lineTools();
        Area a1 = new Area();
        Area a2;
        int[][] xy;
        
        for(int k = 0; k < xList.length - 1; k++)
        {
            //this makes lines perpendicular to the dendrite skeleton
         xy = lt.getPerpendicularLine(xList[k],yList[k],xList[k+1],yList[k+1],width.intValue());
         //this makes a new area from each segment of the dendrite skeleton
         a2 = new Area(new Polygon(xy[0],xy[1],4));         
         a1.add(a2);
         if( k != 0)
             {
                 //this is to make the joints of the segments rounded
                 a2 = new Area(new Ellipse2D.Double(xList[k] - (width.intValue() / 2),
                                                    yList[k] - (width.intValue() / 2), width.intValue(),
                                                    width.intValue()));
                  a1.add(a2);
             }
        }
        //puts the area of interest into dendrite area can use this for drawing and getting pixel info
        dendriteArea = a1;        
       
        a1 = null;
        a2 = null;
        //System.gc();
    }
    
    public void checkArea(int imageWidth, int imageHeight)
    {
     if(!(new Rectangle(0,0,imageWidth,imageHeight).contains(dendriteArea.getBounds())))
    		 dendriteArea.intersect(new Area(new Rectangle(0,0,imageWidth,imageHeight)));
    	
    }
    public void countPunctaT(int[] threshold,int[] stthreshold, int[] r,int iH,PunctaCounter p,neuronToolKit ntk,int imageWidth, int imageHeight)
    {
    	thresholdT = threshold;
    	stthresholdT = stthreshold;
    	rT = r;
    	iHT = iH;
    	pT = new PunctaCounter(p);
    	ntkT = new neuronToolKit();
    	imageWidthT = imageWidth;
    	imageHeightT = imageHeight;
    	
    }
    
    public void run()
    {    	
     countPuncta(thresholdT,stthresholdT,rT,iHT,pT,ntkT,imageWidthT,imageHeightT);
     stthresholdT = null;
     thresholdT = null;
     rT= null;
     //int iHT;
     pT = null;
     ntkT = null;
     
     //int imageWidthT;
     //int imageHeightT;
     
    }

    public void countPuncta(int[] threshold,int[] stthreshold, int[] r,int iH,PunctaCounter p,neuronToolKit ntk,int imageWidth, int imageHeight)
    {
        //gives a writeable raster to extract info from, a color value to determine what information is
        //gathered, a threshold for the color and an area to focus out attention on
        //returns a punctaCounter object
    	
    	groupVector[] puncta = new groupVector[3];
    	//PunctaGroupData[] punctaData = new PunctaGroupData[3];
    	//int[] punctaStart = new int[3];
    	
    	checkArea(imageWidth, imageHeight);
        if(watchColor[0])//rgb
        {
        	//thresholds[0] = threshold[0];
            puncta[0] = p.countPuncta(r,iH,dendriteArea.getBounds(),0,dendriteArea,threshold[0],stthreshold[0],totalRedIntensity);
            puncta[0].calcBounds(p.xyList, (int) dendriteArea.getBounds().getMinX(),
                              (int) dendriteArea.getBounds().getMinY());
           
            puncta[0].makeBoundary(ntk);
           
        }
        if(watchColor[1])//rgb
        {
        	//thresholds[1] = threshold[1];
            puncta[1] = p.countPuncta(r,iH,dendriteArea.getBounds(),1,dendriteArea,threshold[1],stthreshold[1],totalGreenIntensity);
            puncta[1].calcBounds(p.xyList, (int) dendriteArea.getBounds().getMinX(),
                              (int) dendriteArea.getBounds().getMinY());
            
            puncta[1].makeBoundary(ntk);
            
        }
        if(watchColor[2])//rgb
        {
        	//thresholds[2] = threshold[2];
            puncta[2] = p.countPuncta(r,iH,dendriteArea.getBounds(),2,dendriteArea,threshold[2],stthreshold[2],totalBlueIntensity);
            puncta[2].calcBounds(p.xyList, (int) dendriteArea.getBounds().getMinX(),
                              (int) dendriteArea.getBounds().getMinY());
            
            puncta[2].makeBoundary(ntk);
            
        }
        
        if(myPuncta[0] == null)
        	myPuncta[0] = new PunctaContainer(puncta[0].makePuncta());
        else
        	myPuncta[0].updatePuncta(puncta[0].makePuncta());
        
        if(myPuncta[1] == null)
            myPuncta[1] = new PunctaContainer(puncta[1].makePuncta());
        else
            	myPuncta[1].updatePuncta(puncta[1].makePuncta());
        
        if(myPuncta[2] == null)
            myPuncta[2] = new PunctaContainer(puncta[2].makePuncta());
        else
            myPuncta[2].updatePuncta(puncta[2].makePuncta());
        
    }

    public int getPunctaNumber(int color)
    {
        //returns total puncta number (ignored included)
    	        	    
        return myPuncta[color].getTotalPunctaNumber();
        //return myPuncta[color].getTotalPunctaNumber();
    }
    
    public int getVisiblePunctaNumber(int color)
    {
    	return myPuncta[color].getPunctaNumber();
    }
    
    public double getPunctaNumber(int color, double calibration)
    {   
    	return (double)(myPuncta[color].getPunctaNumber()) / getLength(calibration) * 100;
    }
    
    public int getAvePunctaIntensity(int color)
    {    	
    	return myPuncta[color].getAveIntensity();
    }
    
    public double getAvePunctaArea(int color,double calibration)
    {   
    	return (double)(myPuncta[color].getAveArea()) * calibration * calibration;
    }
    
    public int getPunctaIntegratedIntensity(int color)
    {
    	return myPuncta[color].getIntegratedIntensity();
    }
    
    public double getTotalPunctaIntegratedIntensityPerLength(int color, double calibration)
    {
    	return ((double)(myPuncta[color].getIntegratedIntensity())) / getLength(calibration);
    }
    
    public int getDendriteIntensity(int myColor)
    {
    switch(myColor)
  	  {
  	  case 0: return totalRedIntensity[0]; 
	  case 1: return totalGreenIntensity[0];
	  case 2: return totalBlueIntensity[0]; 	  
  	  }
  	  return 0;
    }
    
    public float getDendriteAveIntensity(int myColor, double calibration)
    {
        switch(myColor)
    	  {
    	  case 0: if(totalRedIntensity[1] == 0) return 0; return (float)(totalRedIntensity[0]/totalRedIntensity[1]) * (float)calibration * (float)calibration; 
    	  case 1: if(totalGreenIntensity[1] == 0) return 0; return (float)(totalGreenIntensity[0]/totalGreenIntensity[1]) * (float)calibration * (float)calibration;
    	  case 2: if(totalBlueIntensity[1] == 0) return 0; return (float)(totalBlueIntensity[0]/totalBlueIntensity[1]) * (float)calibration * (float)calibration; 	  
    	  }
    	  return 0;
    }

    public int loadData(PunctaGroupData pgd,int counter,int k,double calibration,int color,int dendrite)
    {
    	//this is stupid replace this
        int start = counter;
        //punctaStart[color] = counter;       
       
        for(int j = 0; j < myPuncta[color].getTotalPunctaNumber(); j++)
        {
            pgd.punctaNames[start + j] = dendrite + "." + j;
            pgd.punctaIntensity[start + j] = myPuncta[color].myPuncta[j].intensity;
            pgd.punctaArea[start + j] = myPuncta[color].myPuncta[j].area * calibration * calibration;            
            counter++;
        }        
        return counter;
    }
    
    /*public int loadSpineData(SpineGroupData sgd,int counter,int k,double calibration,int color)
    {
        int start = counter;        
        for(int j = counter; j < start + spineNumber; j++)
        {
            sgd.spineNames[j] = spineData[j-start].getName(k);
            sgd.spineWidth[j] = spineData[j-start].getWidth(calibration);
            sgd.spineLength[j] = spineData[j-start].getLength(calibration);
            sgd.spineType[j] = spineData[j-start].getType(calibration);
            counter++;
        }        
        return counter;
    }*/

    public float getLength(double calibration)
    {
    	
        float f = (float)0;
        for(int k = 0; k < xList.length - 1; k++)
        {
            f += Point.distance(xList[k],yList[k],xList[k+1],yList[k+1]);
        }

        return f * (float)calibration;
    }

    public boolean isPunctaIgnored(int color,int k)
    {
        return myPuncta[color].myPuncta[k].isIgnored();
    }

    public boolean isPunctaSelected(int color,int k)
    {
        return myPuncta[color].myPuncta[k].isSelected();
    }
    
    public void pushPunctaIgnored(int color,int k)
    {
        myPuncta[color].myPuncta[k].pushIgnored();
    }

    public void pushPunctaSelected(int color,int k)
    {
        myPuncta[color].myPuncta[k].pushSelected();
    }
    
    public void paintDendriteShaft(Graphics2D g)
    {
    	g.drawPolyline(xList, yList, xList.length);
    }
    
    public void paintDendriteArea(Graphics2D g,int dendriteViewMode)
    {
    	Color c = g.getColor();
    	switch(getHierarchy())
    	{
    	case 2: g.setColor(Color.BLUE); break;
    	case 3: g.setColor(Color.CYAN); break;
    	case 4: g.setColor(Color.GREEN); break;
    	case 5: g.setColor(Color.PINK); break;
    	}
    	
    	if(isSelected())
    	{
    		g.setColor(Color.RED);
    	}
    	
    	switch(dendriteViewMode)
    	{
    	case 0: g.fill(dendriteArea); break;
    	case 1: g.draw(dendriteArea); break;
    	}
    	g.setColor(c);
    	
    	if(selectedSegment >=0)
    		drawSegment(g);
    }
        
    
    public void addSpine(SpineInfo s)
    {
    	if(spineData == null)
    	{
    		spineData = new SpineInfo[10];
    		spineData[0] = s;
    		spineNumber++;
    	}
    	else
    	{
    		if(spineNumber < spineData.length)
    		{
    			spineData[spineNumber] = s;
    			spineNumber++;
    		}
    		else
    		{
    			SpineInfo[] tmp = new SpineInfo[spineData.length * 2];
    			System.arraycopy(spineData,0,tmp,0,spineData.length);
    			spineData = null;
    			spineData = tmp;
    			spineData[spineNumber] = s;
    			spineNumber++;
    		}
    	}
    	
    	
    }
    
    public double getSpineNum(double calibration)
    {    	
    	//returns number of true (non filopodia) spines
    	int counter = 0;
    	for(int k = 0; k < spineNumber; k++)
    	{
    		if(spineData[k].calcSpineType(calibration) < 3)
    			counter++;
    	}    	
    	return (double)(counter * 100.00) / this.getLength(calibration);
    	
    }
    
    public double getSpineTypeNum(int type,double calibration)
    {
    	//returns number of spines of a given type
    	int counter = 0;
    	for(int k = 0; k < spineNumber; k++)
    	{
    		if(spineData[k].calcSpineType(calibration) == type)
    			counter++;
    	}
    return (((double)counter) * 100.00) / this.getLength(calibration);
    	
    }
    
    public double getProtrusionNum(double calibration)
    {
    	//returns num of spines + filopodia per 100um
    	return (((double)spineNumber) * 100.00) / getLength(calibration);
    }
    
    public double getAveSpineWidth(double calibration)
    {
    	int counter = 0;
    	double total = 0;
    	for(int k = 0; k < spineNumber; k++)
    	{
    		if(spineData[k].calcSpineType(calibration) < 3)
    			{
    			counter++;
    			total += spineData[k].getWidth(calibration);
    			}
    	}
    	
    	if(counter == 0)
    		return 0;
    	return total / ((double)counter);
    }
    
    public double getAveSpineNeckWidth(double calibration)
    {
    	int counter = 0;
    	double total = 0;
    	for(int k = 0; k < spineNumber; k++)
    	{
    		if(spineData[k].calcSpineType(calibration) < 3)
    			{
    			counter++;
    			total += spineData[k].getNeckWidth(calibration);
    			}
    	}
    	if(counter == 0)
    		return 0;
    	return total / ((double)counter);
    }
    
    public double getAveSpineLength(double calibration)
    {
    	int counter = 0;
    	double total = 0;
    	for(int k = 0; k < spineNumber; k++)
    	{
    		if(spineData[k].calcSpineType(calibration) < 3)
    			{
    			counter++;
    			total += spineData[k].getLength(calibration);
    			}
    	}
    	if(counter == 0)
    		return 0;
    	return total / ((double)counter);
    }     
    
    public void printSpineData(PrintWriter pw,int dendrite, int color, int group,String prefix, DecimalFormat dF, double calibration)
    {	
    	if(spineNumber == 0)
    	{
    		pw.println(prefix + "\t" + "NA" + "\t" + "NA" + "\t"+ "\t" + "NA" + "\t" + "\t" + "NA");
    		return;
    	}
    	for(int j = 0; j < spineNumber; j++)
    			{
    				//prefix: filename groupmember color threshold dendrite number
    				//after prefix dendrite name, spine number, spine head Width, length, type
    				if(!spineData[j].isIgnored)
    				pw.println(prefix 
    						+ "\t" + dendrite 
    						+ "\t" + j+1 /*spineData[j].getName()*/ 
    						+ "\t" + dF.format(spineData[j].getWidth(calibration))
    						+ "\t" +  dF.format(spineData[j].getLength(calibration)) 
    						+ "\t" + spineData[j].getSpineType(calibration)
    				);
    			}
    
    }
    public double getPunctaPerSpineData(int k, int myColor, double calibration)
    {
    	int[] pC = getPunctaPerSpineData(myColor);
    	return (double)(pC[k]) / getLength(calibration) * 100;
    }
    
    public int[] getPunctaPerSpineData(int color)
    {
    	
    	int[] pC = new int[4];
    	for(int j = 0; j < spineNumber; j++)
    		{
    			switch(spineData[j].getPunctaNum(color))
    			{
    			case 0: pC[0]++; break;
    			case 1: pC[1]++; break;
    			case 2: pC[2]++; break;    			
    			default: pC[3]++; break;
    			}
    		}    				
    	
    	return pC;
    }
    
    public void Save(DataOutputStream ds, IoContainer i)
    {
    	//save type 0 normal 1 complex
    	//save xList
    	//save yList
    	//save watchColor
    	//save groupMember
    	//dendriteWidth will be saved by tiffPanel
    	//save SpineInfo
    	//save PunctaInfo
    	saveType(ds,i);
    	saveVertex(ds,i);
    	saveWatchColor(ds,i);
    	saveGroupMember(ds,i);
    	saveSpineInfo(ds,i);
    	//savePunctaInfo(ds,i);    	
    	saveParentInfo(ds,i);
    	
    	    	
    }
    
    public static Dendrite loadDendrite(DataInputStream di, IoContainer i, dendriteWidth d,int version,Group[] groupList)
    {
    	int type = i.readInt(di,"making dendrite");
    	Dendrite out;
    	if(type == 0)
    		out = new Dendrite(d,null,null);
    	else
    		out = new ComplexDendrite(d,null,null);
    	out.Load(di, i,version,groupList);
    	out.loadParentInfo(di, i, version);
    	return out;
    }
    
    public Object Load(DataInputStream di, IoContainer i,int version,Group[] groupList)
    {
    	//load xList
    	//load yList
    	//load watchColor
    	//load groupMember
    	//dendriteWidth already loaded
    	//load SpineInfo
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
    	return null;
    }
    
    public void saveType(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex    	
    	i.writeInt(ds, new String("dendrite type"), 0);
    }
    
    public void saveParentInfo(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex
    	int dN = -1;
    	if(myParent != null)
    		dN = myParent.myIndex;
    	
    	i.writeInt(ds, new String("Parent index"), dN);
    	i.writeInt(ds, "Parent Segment Index", this.parentSegment);
    }
    
    public void loadParentInfo(DataInputStream di, IoContainer i,int version)
    {
    	if(version < 3) //starts using parent info after version 3
    		return;
    	
    	myIndex = i.readInt(di, "Setting my parent Index"); //will store myParents index here temporarily
    	parentSegment = i.readInt(di,"setting parent segment index");
    }
    
    public void saveVertex(DataOutputStream ds,IoContainer i)
    {    	
    	//save xList items
    	//save yList items
    	i.writeIntArray(ds,new String("Dendrite x Vertex"),xList);
    	i.writeIntArray(ds,new String("Dendrite y Vertex"),yList);   	
    	
    }
    
    public void saveWatchColor(DataOutputStream ds,IoContainer i)
    {
    	//save booleanArray
    	i.writeBooleanArray(ds,new String("dendrite watch color"), watchColor);
    }
    
    public void saveGroupMember(DataOutputStream ds,IoContainer i)
    {
    	//save groupMember
    	i.writeInt(ds,new String("groupmember"), groupMember.getValue());
    	
    } 
    
    public void saveSpineInfo(DataOutputStream ds, IoContainer i)
    {
    	//save SpineNumber
    	//save SpineInfo for each spine
    	i.writeInt(ds,"dendrite spine number", spineNumber);
    	for(int k = 0; k < spineNumber; k++)
    	{
    		spineData[k].Save(ds, i);
    	}
    }
    
    public void savePunctaInfo(DataOutputStream ds, IoContainer i)
    {
    	/*
    	 * will save which puncta were ignored
    	   and will just recalculate the puncta when needed
    	   save punctaGroupData
    	 */    	
    	for(int k = 0; k < myPuncta.length; k++)
    	{    		
    		myPuncta[k].Save(ds, i);
    	}
    	    	
    
    }
    
    public void ignoreAllPuncta(int color)
    {
    	for(int k = 0; k < myPuncta[color].myPuncta.length; k++)
    	{
    		myPuncta[color].myPuncta[k].bC.isIgnored = true;
    	}
    }
    
    public void resetAllPuncta(int color)
    {
    	for(int k = 0; k < myPuncta[color].myPuncta.length; k++)
    	{
    		myPuncta[color].myPuncta[k].bC.isIgnored = false;    	
    	}
    }
    
    public void drawPuncta(Graphics2D g, int color, int dendrite)
    {
    	if(myPuncta == null)
    		return;    	
    	if(myPuncta[color] == null)
    		return;
    	Puncta[] p = myPuncta[color].myPuncta;
    	if(p == null)
    		return;
    	
    	for(int k = 0; k < p.length; k++)
    	{
    		p[k].drawPuncta(g,dendrite,k);
    	}
    }
    
    public void linkBoolean(BooleanContainer bc)
    {
    	bC = bc;    	
    }
    
    public boolean isIgnored()
    {
    	return bC.isIgnored();
    }
    
    public boolean isSelected()
    {
    	return bC.isSelected();
    }
    
    public void LoadPunctaInfo(DataInputStream di, IoContainer i,int version)
    {   
    	myPuncta[0].Load(di, i,version);
    	myPuncta[1].Load(di, i,version);
    	myPuncta[2].Load(di, i,version);
    }
    
    public void autoIgnore(int[] thresholds,IgnoreCriteria[] ignoreCriteria,Raster r)
    {
    	myPuncta[0].autoIgnore(thresholds,ignoreCriteria[0],r);
    	myPuncta[1].autoIgnore(thresholds,ignoreCriteria[1],r);
    	myPuncta[2].autoIgnore(thresholds,ignoreCriteria[2],r);
    }
    
    public void invertIgnored()
    {
    	myPuncta[0].invertIgnored();
    	myPuncta[1].invertIgnored();
    	myPuncta[2].invertIgnored();
    }
    
    public void restoreIgnored()
    {
    	myPuncta[0].restoreIgnored();
    	myPuncta[1].restoreIgnored();
    	myPuncta[2].restoreIgnored();
    }
    
    public void autoIgnore(boolean rgb[], boolean[] restoreRed, boolean[] restoreGreen, boolean[] restoreBlue, boolean[] ifRed, boolean[] ifGreen, boolean[] ifBlue)
    {
    	OverlapObjectArray oA = new OverlapObjectArray(dendriteArea.getBounds());
    	oA.loadRed(myPuncta[0].myPuncta,rgb[0]);
    	oA.loadGreen(myPuncta[1].myPuncta,rgb[1]);
    	oA.loadBlue(myPuncta[2].myPuncta,rgb[2]);
    	
    	if(rgb[0])
    	oA.autoIgnore(restoreRed[0], restoreGreen[0], restoreBlue[0], ifRed[0], ifGreen[0], ifBlue[0]);
    	if(rgb[1])
    	oA.autoIgnore(restoreRed[1], restoreGreen[1], restoreBlue[1], ifRed[1], ifGreen[1], ifBlue[1]);
    	if(rgb[2])
    	oA.autoIgnore(restoreRed[2], restoreGreen[2], restoreBlue[2], ifRed[2], ifGreen[2], ifBlue[2]);
    }
    
    public void autoIgnoreSpineRadius(int color, int r)
    {
    	Shape[] spineR = new Shape[spineNumber]; 
    	for(int k = 0; k < spineNumber; k++)
    	{
    		spineR[k] = spineData[k].getSpineRadius(r);
    	}
    	 
    	boolean[] bb = myPuncta[color].getIgnoredList();    	
    	OverlapObjectArray oA = new OverlapObjectArray(dendriteArea.getBounds());
    	
    	if(color == 0)  
    	oA.loadRed(myPuncta[0].myPuncta,true);
    	
    	if(color == 1)    	   	
    	oA.loadGreen(myPuncta[1].myPuncta,true);
    	
    	if(color == 2)
    	oA.loadBlue(myPuncta[2].myPuncta,true);    	
    	
    	
    	Puncta[][] num  = oA.spineAreaRestore(spineR, color);
    	
    	for(int j = 0; j < spineNumber; j++ )
    	{
    		switch(color)
    		{
    		case 0: spineData[j].myRedPuncta = num[j]; break;
    		case 1: spineData[j].myGreenPuncta = num[j]; break;
    		case 2: spineData[j].myBluePuncta = num[j]; break;
    		}
    	}
    	    	
    		reignorepuncta(bb, myPuncta[color].myPuncta);
    		
    	
    }
    
    public void autoIgnorePunctaSize(int color, int size)
    {
    	Puncta[] p = myPuncta[color].myPuncta;
    	for(int k = 0; k < p.length; k++)
    	{
    		if(p[k].area < size)
    		{
    			p[k].bC.isIgnored = true;
    		}
    	}
    }
    
    private void reignorepuncta(boolean[] b, Puncta[] p)
    {    	
    	for(int k = 0; k < b.length; k++ )
    	{
    		if(!p[k].isIgnored() && b[k])
    		{
    			p[k].invertIgnored();
    		}
    	}
    }
    
    public void calculateSpineShaftIntensity(int[] threshold,int[] r,int iH,PunctaCounter p)
    {
    	for(int k = 0; k < spineData.length; k++)
    	{
    		spineData[k].calculateSpineShaftIntensity(threshold, r, iH, p);
    	}
    }
    
    public float getData(int i, double calibration,int color)
    {
    	switch(i)
    	{
    	case DataType.DendriteLength: return this.getLength(calibration);
    	case DataType.PunctaDensity: return (float)this.getPunctaNumber(color, calibration);
    	case DataType.PunctaIntegratedIntensity: return (float)this.getPunctaIntegratedIntensity(color);
    	case DataType.PunctaArea: return (float) this.getAvePunctaArea(color, calibration);
    	case DataType.SpineDesnity: return (float) this.getSpineNum(calibration);
    	case DataType.MushroomSpineDensity: return (float) this.getSpineTypeNum(0, calibration);
    	case DataType.ThinSpineDensity: return (float) this.getSpineTypeNum(1, calibration);
    	case DataType.StubbySpineDensity: return (float) this.getSpineTypeNum(2, calibration);
    	case DataType.FilopodiaDensity: return (float) this.getSpineTypeNum(3, calibration);
    	}
    	
    	return (float)0;
    }
    
    public void ShollFilter(Graphics2D g, Graphics2D g2, int dendriteNum,int color)
    {
    	
    }
    
    public void ShollIgnore(int[] pL,int color)
    {
    	
    }
    
    public void drawPoint(Graphics g, Point p)
    {
    	/*this function will take the current pointer position and use this information to draw a point
    	along the center of the dendrite skeleton.
    	*/ 
    	if(selectedSegment < 0)
    		return;
    	
    	int dy = yList[selectedSegment + 1]- yList[selectedSegment];
    	int dx = xList[selectedSegment + 1]- xList[selectedSegment];
    	double slope = (double)dy/(double)dx;
    	double intercept = (double)yList[selectedSegment] - (slope * (double)xList[selectedSegment]);
    	if(Math.abs(dx) > Math.abs(dy))
    	g.fillOval(p.x, (int)(p.x * slope + intercept), 2, 2);
    	else
    	g.fillOval((int)((p.y - intercept)/slope), p.y, 2, 2);
    }
    
    public Point getPoint(Point p)
    {
    	/*
    	 * this function will return the position drawn in drawPoint.
    	 */
   
    	int dy = yList[selectedSegment + 1]- yList[selectedSegment];
    	int dx = xList[selectedSegment + 1]- xList[selectedSegment];
    	double slope = (double)dy/(double)dx;
    	double intercept = (double)yList[selectedSegment] - (slope * (double)xList[selectedSegment]);
    	if(Math.abs(dx) > Math.abs(dy))
    	return new Point(p.x, (int)(p.x * slope + intercept));
    	else
    	return new Point((int)((p.y - intercept)/slope), p.y);
    }
    
    public void drawSegment(Graphics g)
    {
    	if(selectedSegment >= 0)
    	{
    		Color c = g.getColor();
    		g.setColor(Color.orange);
    		g.drawLine(xList[selectedSegment], yList[selectedSegment], xList[selectedSegment + 1], yList[selectedSegment + 1]);
    		g.setColor(c);
    		this.drawPoint(g, childPoint);
    	}
    }
    
    public int withinSegments(Point p)
    {
    	for(int k = 0; k < xList.length - 1; k++)
    	{
    		if(withinSegment(p,k))
    			return k;
    	}
    	
    	return -1;
    }
    
    private boolean withinSegment(Point p, int segment)
    {
    	/*
    	 * for segment n (xList and yList) check if p.x is between xList[n] and xList[n+1]
    	 * same for y. If yes then check to see if Point p lays along the line of the segment +/- 5 pixels.
    	 * this will fail for overlaping segments, may have to implement an ability to select a segment
    	 * show segment, click segment, add dendrtie to segment
    	 */
    	int x1,x2,y1,y2;
    	if(xList[segment] < xList[segment + 1])
    		{
    		x1=xList[segment];
    		x2=xList[segment + 1];
    		}
    	else
    	{
    		x2=xList[segment];
    		x1=xList[segment + 1];
    	}
    	
    	if(yList[segment] < yList[segment + 1])
		{
		y1=yList[segment];
		y2=yList[segment + 1];
		}
	else
	{
		y2=yList[segment];
		y1=yList[segment + 1];
	}
    	
    	
    	if(p.x >= x1 && p.x <= x2)
    		if(p.y >= y1 && p.y <= y2)
    			return true;
    	
    	return false;
    }
    
        
    public void SelectSegment(int k)
    {
    	selectedSegment = k;
    }
    
    public boolean isWithin(Point p)
    {
    	if(this.dendriteArea == null)
    		return false;
    	
    	return dendriteArea.contains(p);	
    }
    
    public void setChildPoint(Point p)
    {
    	childPoint = p;
    }
    
    public int getHierarchy()
    {
    	/*
    	 * returns the depth the dendrite exists within the hierachy...primary secondary...etc
    	 */
    	Dendrite d = myParent;
    	int k = 1; 
    	while(d != null)
    	{
    		d = d.myParent;
    		k++;
    	}
    	return k;
    }
    
    public int getLengthtoPoint()
    {
    	//returns the distance from the point to the origin of the dendrite 
    	int f = 0;
    	int k = 0;
    	
    	if(myParent == null)
    		return 0;
    	
    	for(k = 0; k < parentSegment; k++)
    	{
    	     f += Point.distance(myParent.xList[k],myParent.yList[k],myParent.xList[k+1],myParent.yList[k+1]);
    	}
    	
    	f+= Point.distance(myParent.xList[k], myParent.yList[k], xList[0], yList[0]);

    return f;
    	    
    }
    
    public float getLengthFromRoot(double calibration)
    {
    	/*
    	 * this will give the start point of the dendrite if the dendrite were straight
    	 * taking into account the position of it's parents.
    	 * this startpoint can be used to calculate a normalized sholl
    	 * the endpoint will be the startpoin + the length.
    	 */
    	int f = 0;
    	Dendrite d = this;
    	while(d != null)
    	{
    		f+= d.getLengthtoPoint();
    		d = d.myParent;
    	}
    	
    	float out = (float)f * (float)calibration;
    	//out += this.getLength(calibration); //adds the length of this dendrite to the segment
    	
    	return out;
    	
    	
    }
    
    
    
        

}
