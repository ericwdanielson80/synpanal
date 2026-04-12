package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
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
 * Geometric and analytical model of a single dendrite branch drawn on an
 * 8-bit RGB TIFF image. A Dendrite stores the ordered vertices of its
 * skeleton (xList/yList), a width descriptor, and a derived outline polygon
 * built up as an Area (dendriteArea) from per-segment perpendicular quads
 * joined by rounded caps. From the outline it can count puncta per color
 * channel using a PunctaCounter, accumulate per-channel red/green/blue
 * intensity totals, hold a list of SpineInfo entries describing detected
 * spines along the shaft, and record parent/child relationships for
 * primary/secondary/tertiary dendrite hierarchies. The class supports
 * painting the shaft and area, selecting individual segments, saving and
 * loading its state through Savable, running puncta counting on a worker
 * thread via Runnable, and supplying measurements (length, puncta density,
 * intensities, spine densities, etc.) through the DataSource interface. It
 * is the base class used for fixed-width dendrites; ComplexDendrite extends
 * it with variable-width behaviour and ShollDendrite supports Sholl ring
 * analysis.
 */
public class Dendrite implements Savable, Runnable, DataSource {
int[] xList;//can optimize later
int[] yList;
Polygon outline;
dendriteWidth width;
public Area dendriteArea;
public Dendrite myParent; //for determining primary secondary...etc
public int parentSegment;
public int selectedSegment = -1; //for drawing a specific segment
public int myIndex; //for saving and loading
Point childPoint;

//groupVector[] puncta = new groupVector[3];
//PunctaGroupData[] punctaData = new PunctaGroupData[3];
//int[] punctaStart = new int[3];
//int[] thresholds = new int[3];
public boolean[] watchColor;
public Group groupMember;


public SpineInfo[] spineData;
public int spineNumber = 0;

//threaded stuff
int[] thresholdT;
int[] stthresholdT = new int[] {255,255,255};
int[] rT;
int iHT;
PunctaCounter pT;
neuronToolKit ntkT;
int imageWidthT;
int imageHeightT;
public int[] totalRedIntensity = new int[2];
public int[] totalBlueIntensity = new int[2];
public int[] totalGreenIntensity = new int[2];

BooleanContainer bC;

public PunctaContainer[] myPuncta = new PunctaContainer[3];

    /**
     * Constructs a new Dendrite with a given width descriptor, a per-channel
     * watch flag array (red/green/blue) indicating which color channels should
     * be analyzed, and the Group this dendrite belongs to. The constructor
     * stores these references, initializes the three PunctaContainer slots in
     * myPuncta with empty Puncta arrays (one per color channel), and creates a
     * fresh BooleanContainer bC used to hold the ignored/selected flags.
     */
    public Dendrite(dendriteWidth w,boolean[] color,Group group) {
        width = w;
        watchColor = color;
        groupMember = group;
        myPuncta[0] = new PunctaContainer(new Puncta[0]);        
        myPuncta[1] = new PunctaContainer(new Puncta[0]);
        myPuncta[2] = new PunctaContainer(new Puncta[0]);
        bC = new BooleanContainer(false,false);
    }

    /**
     * Triggers a regeneration of the dendriteArea outline. The parameter k
     * names a new width descriptor but is not stored here; the method simply
     * calls makeArea() so the outline is rebuilt using the current width
     * field.
     */
    public void setWidth(dendriteWidth k)
    {
        makeArea();
    }

    /**
     * Appends a new vertex (x, y) to the dendrite skeleton. If xList is null
     * the method allocates length-one xList/yList arrays and stores the point;
     * otherwise it allocates new arrays one element longer (tempX, tempY),
     * copies the existing contents in with System.arraycopy, places the new
     * (x, y) at the last index, and then replaces xList/yList with the new
     * arrays. After adding the vertex, if the width is not the sentinel value
     * -1 it calls makeArea() so the outline is kept up to date. The x and y
     * parameters are the pixel coordinates of the new vertex.
     */
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

    /**
     * Overwrites the coordinates of the last vertex in the skeleton with the
     * given (x, y), used while the user is rubber-banding the next dendrite
     * vertex. If xList is null the call is a no-op; if fewer than two vertices
     * exist it delegates to add(x, y) so a vertex is created. Otherwise it
     * directly rewrites the last slot of xList/yList. The parameters x and y
     * are the new pixel coordinates of the last vertex.
     */
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

    /**
     * Removes the last vertex from the dendrite skeleton. When xList is null
     * the method returns true to signal there was nothing to remove. Otherwise
     * it allocates new arrays tempX/tempY one element shorter, copies all but
     * the last element into them with System.arraycopy, and swaps them in
     * place of xList/yList. If the width is not -1 it rebuilds the outline by
     * calling makeArea(). Returns false when a vertex was actually removed.
     */
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

    /**
     * Dispatches outline construction for the dendrite. If the skeleton has
     * not yet been started (xList null) or only contains a single vertex, the
     * method returns without building anything because at least two vertices
     * are required to define a segment. Otherwise it delegates to
     * makeSimpleArea() which builds a fixed-width outline.
     */
    public void makeArea()
    {
        //seems to need at least two items in the list to function


        if(xList == null)
            return;
        if(xList.length == 1)
            return;      

        makeSimpleArea();
    }

    

    /**
     * Builds the dendriteArea outline as a constant-width polygon running
     * along the skeleton. A lineTools helper (lt) is used to compute, for each
     * consecutive pair of skeleton vertices, a perpendicular quad xy that
     * encloses the segment at the current width; that quad is wrapped in a
     * Polygon, converted to an Area a2, and unioned into the running total a1.
     * For every joint after the first, an Ellipse2D centered on the shared
     * vertex with diameter equal to width is also unioned in so the joints
     * look rounded rather than mitered. After the loop the accumulated area
     * a1 is stored in dendriteArea for later drawing and pixel sampling, and
     * the local Area references are cleared.
     */
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
    
    /**
     * Clips the dendriteArea to the image bounds if any part of its bounding
     * box extends outside the image. A Rectangle (0, 0, imageWidth,
     * imageHeight) is built from the parameters and tested against the area's
     * bounding box; if the image rectangle does not contain the bounding box,
     * dendriteArea is intersected with an Area of that rectangle so no
     * off-image pixels are sampled downstream.
     */
    public void checkArea(int imageWidth, int imageHeight)
    {
     if(!(new Rectangle(0,0,imageWidth,imageHeight).contains(dendriteArea.getBounds())))
    		 dendriteArea.intersect(new Area(new Rectangle(0,0,imageWidth,imageHeight)));
    	
    }
    /**
     * Caches the inputs needed for a background puncta count so that run()
     * can later invoke countPuncta() without extra arguments. The per-channel
     * threshold and stthreshold arrays, the raw raster data r, the image
     * height iH, a cloned PunctaCounter pT built from p, a fresh
     * neuronToolKit ntkT, and the image dimensions imageWidthT/imageHeightT
     * are all stored in the thread-local fields. This method does not perform
     * any counting itself.
     */
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
    
    /**
     * Runnable entry point for threaded puncta counting. It forwards the
     * previously cached thread-local inputs (thresholdT, stthresholdT, rT,
     * iHT, pT, ntkT, imageWidthT, imageHeightT) to countPuncta(), then nulls
     * out the references it is safe to release so the counting-related
     * objects can be garbage collected once the thread finishes.
     */
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

    /**
     * Performs per-color puncta counting over dendriteArea and stores the
     * results in the three myPuncta containers. It first calls checkArea() to
     * clip the outline to the image bounds, then for each color channel whose
     * watchColor flag is true it asks the PunctaCounter p to countPuncta()
     * over the bounded raster r at the matching threshold/stthreshold, also
     * accumulating the color channel's total and count into
     * totalRedIntensity/totalGreenIntensity/totalBlueIntensity. The returned
     * groupVector entries in the local puncta[] array then have calcBounds()
     * called (using p.xyList offset by the bounding box's minX/minY) and
     * makeBoundary(ntk) invoked before being converted to Puncta arrays. For
     * each channel, if myPuncta slot is null a new PunctaContainer is
     * allocated; otherwise updatePuncta is called so ignored/selected state
     * persists. The parameters iH is the image height, imageWidth and
     * imageHeight bound the raster, and the local puncta[] array of three
     * groupVectors holds intermediate per-channel counting results.
     */
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

    /**
     * Returns the total number of puncta detected for the given color
     * channel, including puncta flagged as ignored. The color parameter
     * selects a slot in myPuncta (0=red, 1=green, 2=blue) and the method
     * delegates to that container's getTotalPunctaNumber().
     */
    public int getPunctaNumber(int color)
    {
        //returns total puncta number (ignored included)
    	        	    
        return myPuncta[color].getTotalPunctaNumber();
        //return myPuncta[color].getTotalPunctaNumber();
    }
    
    /**
     * Returns the number of non-ignored puncta for the given color channel by
     * delegating to myPuncta[color].getPunctaNumber().
     */
    public int getVisiblePunctaNumber(int color)
    {
    	return myPuncta[color].getPunctaNumber();
    }
    
    /**
     * Returns a puncta density for the given color channel expressed per
     * 100 length units. The visible puncta count from myPuncta[color] is
     * divided by getLength(calibration) (micrometers at the given
     * pixel-to-micron calibration) and multiplied by 100.
     */
    public double getPunctaNumber(int color, double calibration)
    {   
    	return (double)(myPuncta[color].getPunctaNumber()) / getLength(calibration) * 100;
    }
    
    /**
     * Returns the average intensity of the visible puncta in the given color
     * channel by delegating to myPuncta[color].getAveIntensity().
     */
    public int getAvePunctaIntensity(int color)
    {    	
    	return myPuncta[color].getAveIntensity();
    }
    
    /**
     * Returns the average area of the visible puncta in the given color
     * channel in calibrated square units. The area returned by
     * myPuncta[color].getAveArea() is multiplied by calibration squared to
     * convert pixel-area to micrometer-area.
     */
    public double getAvePunctaArea(int color,double calibration)
    {   
    	return (double)(myPuncta[color].getAveArea()) * calibration * calibration;
    }
    
    /**
     * Returns the integrated intensity summed across the visible puncta in
     * the given color channel by delegating to
     * myPuncta[color].getIntegratedIntensity().
     */
    public int getPunctaIntegratedIntensity(int color)
    {
    	return myPuncta[color].getIntegratedIntensity();
    }
    
    /**
     * Returns the total integrated puncta intensity for the given color
     * channel divided by the dendrite length in calibrated units. The value
     * from myPuncta[color].getIntegratedIntensity() is cast to double and
     * divided by getLength(calibration).
     */
    public double getTotalPunctaIntegratedIntensityPerLength(int color, double calibration)
    {
    	return ((double)(myPuncta[color].getIntegratedIntensity())) / getLength(calibration);
    }
    
    /**
     * Returns the total summed pixel intensity inside the dendrite outline
     * for the requested color channel. The myColor parameter selects between
     * totalRedIntensity[0], totalGreenIntensity[0], and totalBlueIntensity[0]
     * (index 0 is the accumulated sum; index 1 holds the pixel count). Any
     * other value returns zero.
     */
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
    
    /**
     * Returns a calibrated average intensity per unit area for the requested
     * color channel. The switch on myColor picks totalRedIntensity,
     * totalGreenIntensity, or totalBlueIntensity; if the pixel count in slot
     * [1] is zero the method returns 0 to avoid division by zero, otherwise
     * it divides the accumulated sum by the count and multiplies by
     * calibration squared to express the result in calibrated area units.
     */
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

    /**
     * Writes this dendrite's puncta data for one color channel into a
     * PunctaGroupData buffer starting at index counter and returns the next
     * free index. The local start variable remembers the original counter so
     * each punctum can be stored at start + j. For each of the total puncta
     * in myPuncta[color], a composite name of "dendrite.j" is written,
     * together with the raw intensity and the calibrated area (area scaled by
     * calibration squared). The parameter k is currently unused by this
     * method, and dendrite gives the dendrite's numeric label used in the
     * composite name.
     */
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

    /**
     * Returns the total length of the dendrite skeleton in calibrated units.
     * The accumulator f sums Point.distance() between each consecutive pair
     * of skeleton vertices in xList/yList, and the result is multiplied by
     * the calibration parameter (pixels-to-micrometers) before being
     * returned as a float.
     */
    public float getLength(double calibration)
    {
    	
        float f = (float)0;
        for(int k = 0; k < xList.length - 1; k++)
        {
            f += Point.distance(xList[k],yList[k],xList[k+1],yList[k+1]);
        }

        return f * (float)calibration;
    }

    /**
     * Returns true when puncta k of the given color channel is currently
     * flagged as ignored, by forwarding to the Puncta's isIgnored().
     */
    public boolean isPunctaIgnored(int color,int k)
    {
        return myPuncta[color].myPuncta[k].isIgnored();
    }

    /**
     * Returns true when puncta k of the given color channel is currently
     * flagged as selected, by forwarding to the Puncta's isSelected().
     */
    public boolean isPunctaSelected(int color,int k)
    {
        return myPuncta[color].myPuncta[k].isSelected();
    }
    
    /**
     * Toggles the ignored flag on puncta k of the given color channel by
     * calling the Puncta's pushIgnored().
     */
    public void pushPunctaIgnored(int color,int k)
    {
        myPuncta[color].myPuncta[k].pushIgnored();
    }

    /**
     * Toggles the selected flag on puncta k of the given color channel by
     * calling the Puncta's pushSelected().
     */
    public void pushPunctaSelected(int color,int k)
    {
        myPuncta[color].myPuncta[k].pushSelected();
    }
    
    /**
     * Draws the dendrite skeleton as a connected polyline on the provided
     * Graphics2D context g using the current xList/yList vertex arrays.
     */
    public void paintDendriteShaft(Graphics2D g)
    {
    	g.drawPolyline(xList, yList, xList.length);
    }
    
    /**
     * Renders the dendriteArea outline. The current color c is remembered
     * first and a depth-based color is chosen by a switch on
     * getHierarchy() (2=blue, 3=cyan, 4=green, 5=pink). If the dendrite
     * isSelected() the color is overridden with red. A second switch on
     * dendriteViewMode either fills the dendriteArea (case 0) or only draws
     * its outline (case 1). The original color c is restored afterwards, and
     * if a segment is selected (selectedSegment >= 0) drawSegment is called
     * to highlight it.
     */
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
        
    
    /**
     * Appends a new SpineInfo s to the spineData array and bumps
     * spineNumber. If spineData is still null the method allocates an array
     * of 10 slots and drops s into slot 0. If spineData already has spare
     * capacity, s is written into the next free slot. When the array is
     * full, a new array tmp of double the current length is allocated, the
     * existing entries are copied in with System.arraycopy, spineData is
     * swapped for tmp, and s is placed at the next index.
     */
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
    
    /**
     * Returns the density of true (non-filopodia) spines along the dendrite,
     * expressed per 100 calibrated length units. The loop walks spineData,
     * incrementing counter whenever SpineInfo.calcSpineType(calibration) is
     * less than 3 (i.e. mushroom/thin/stubby rather than filopodia), then
     * divides counter * 100 by getLength(calibration).
     */
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
    
    /**
     * Returns the density of spines that have a specific type along the
     * dendrite, expressed per 100 calibrated length units. The type
     * parameter is the integer spine-type code returned by
     * SpineInfo.calcSpineType (0=mushroom, 1=thin, 2=stubby, 3=filopodia);
     * counter is incremented for every spine of that type, then divided by
     * getLength(calibration) and multiplied by 100.
     */
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
    
    /**
     * Returns the density of every dendritic protrusion (spines plus
     * filopodia) per 100 calibrated length units, computed as spineNumber *
     * 100 divided by getLength(calibration).
     */
    public double getProtrusionNum(double calibration)
    {
    	//returns num of spines + filopodia per 100um
    	return (((double)spineNumber) * 100.00) / getLength(calibration);
    }
    
    /**
     * Returns the mean head width of true spines along the dendrite. The
     * loop iterates over spineData; whenever
     * SpineInfo.calcSpineType(calibration) is below 3 (not filopodia) it
     * increments counter and adds SpineInfo.getWidth(calibration) to total.
     * If no qualifying spines were found the method returns 0, otherwise
     * total is divided by counter to give the average calibrated width.
     */
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
    
    /**
     * Returns the mean neck width of true spines along the dendrite using
     * the same pattern as getAveSpineWidth: the loop accumulates
     * SpineInfo.getNeckWidth(calibration) into total for each spine whose
     * calcSpineType is below 3, incrementing counter alongside. The result
     * is total divided by counter, or 0 when no qualifying spines were
     * found.
     */
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
    
    /**
     * Returns the mean length of true spines along the dendrite. The loop
     * accumulates SpineInfo.getLength(calibration) into total for every
     * spine with calcSpineType below 3, incrementing counter. The returned
     * value is total divided by counter, or 0 when no qualifying spines
     * exist.
     */
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
    
    /**
     * Writes one tab-separated text line per non-ignored spine on this
     * dendrite to the PrintWriter pw, using dF to format width and length.
     * If spineNumber is zero, a single "NA" placeholder row is printed
     * instead. Each row begins with the caller-supplied prefix
     * (filename/group/color/threshold information), followed by the
     * dendrite number, the spine index (j+1), the calibrated width and
     * length, and the SpineInfo.getSpineType(calibration) string. The
     * parameters color and group are part of the text prefix context and
     * are not used directly by this method.
     */
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
    /**
     * Returns one bin of the puncta-per-spine histogram normalized to
     * calibrated dendrite length. getPunctaPerSpineData(myColor) is called
     * first to build the histogram pC, then bin k is divided by
     * getLength(calibration) and multiplied by 100 so the result is a
     * density per 100 length units.
     */
    public double getPunctaPerSpineData(int k, int myColor, double calibration)
    {
    	int[] pC = getPunctaPerSpineData(myColor);
    	return (double)(pC[k]) / getLength(calibration) * 100;
    }
    
    /**
     * Builds a four-bin histogram of how many puncta of the given color
     * each spine on this dendrite contains. The local counter array pC has
     * indices 0, 1, 2, and 3 for spines with zero, one, two, or three or
     * more puncta. Each SpineInfo's getPunctaNum(color) is switched on to
     * increment the matching bin, and the filled pC is returned.
     */
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
    
    /**
     * Writes the persistent state of this dendrite to the DataOutputStream
     * ds through the IoContainer i. The save order is: dendrite type code,
     * skeleton vertex arrays, watchColor flags, groupMember value, spine
     * data, and parent-link information. The dendriteWidth is intentionally
     * not written here because it is saved by the enclosing tiffPanel.
     */
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
    
    /**
     * Static factory that reads a single Dendrite (or subclass instance)
     * from the DataInputStream di via IoContainer i. The first int read is
     * a type discriminator: 0 constructs a plain Dendrite while anything
     * else constructs a ComplexDendrite. The instance is then populated by
     * Load() using the provided file-format version and groupList, and
     * finally loadParentInfo() reads any parent-link fields before the
     * fully built Dendrite is returned.
     */
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
    
    /**
     * Populates this dendrite from a previously saved stream. The
     * IoContainer i reads xList, yList, the watchColor flags, and an index
     * into groupList used to set groupMember. spineNumber is then read and,
     * if non-zero, a SpineInfo[spineNumber] array is allocated whose
     * entries are each constructed with empty references and filled by
     * their own Load() using the given version and groupList. The
     * dendriteWidth is assumed to have been loaded previously. Always
     * returns null.
     */
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
    
    /**
     * Writes the dendrite-type discriminator to ds via IoContainer i. The
     * base class always writes 0, signalling a plain Dendrite; subclasses
     * override this to write a different value (such as 1 for a
     * ComplexDendrite).
     */
    public void saveType(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex    	
    	i.writeInt(ds, new String("dendrite type"), 0);
    }
    
    /**
     * Writes the parent-hierarchy information for this dendrite. The local
     * dN starts at -1 (no parent) and, if myParent is not null, is replaced
     * with myParent.myIndex so the link can be rebuilt on load. dN is
     * written as "Parent index" and parentSegment (which skeleton segment
     * on the parent this dendrite branches off of) is written as "Parent
     * Segment Index".
     */
    public void saveParentInfo(DataOutputStream ds, IoContainer i)
    {
//    	save type 0 normal 1 complex
    	int dN = -1;
    	if(myParent != null)
    		dN = myParent.myIndex;
    	
    	i.writeInt(ds, new String("Parent index"), dN);
    	i.writeInt(ds, "Parent Segment Index", this.parentSegment);
    }
    
    /**
     * Restores the parent-hierarchy information written by
     * saveParentInfo. If the file-format version is earlier than 3 the
     * fields were not recorded and the method returns immediately;
     * otherwise it reads the parent index into myIndex (used as a
     * temporary holder until the owning container resolves it to an actual
     * Dendrite reference) and the parent segment index into
     * parentSegment.
     */
    public void loadParentInfo(DataInputStream di, IoContainer i,int version)
    {
    	if(version < 3) //starts using parent info after version 3
    		return;
    	
    	myIndex = i.readInt(di, "Setting my parent Index"); //will store myParents index here temporarily
    	parentSegment = i.readInt(di,"setting parent segment index");
    }
    
    /**
     * Writes the skeleton vertex arrays xList and yList to ds through the
     * IoContainer i under the names "Dendrite x Vertex" and "Dendrite y
     * Vertex".
     */
    public void saveVertex(DataOutputStream ds,IoContainer i)
    {    	
    	//save xList items
    	//save yList items
    	i.writeIntArray(ds,new String("Dendrite x Vertex"),xList);
    	i.writeIntArray(ds,new String("Dendrite y Vertex"),yList);   	
    	
    }
    
    /**
     * Writes the watchColor boolean array (which color channels are under
     * analysis for this dendrite) to ds via i under the label "dendrite
     * watch color".
     */
    public void saveWatchColor(DataOutputStream ds,IoContainer i)
    {
    	//save booleanArray
    	i.writeBooleanArray(ds,new String("dendrite watch color"), watchColor);
    }
    
    /**
     * Writes the owning Group's integer value, obtained from
     * groupMember.getValue(), to ds via i under the label "groupmember" so
     * group membership can be restored on load.
     */
    public void saveGroupMember(DataOutputStream ds,IoContainer i)
    {
    	//save groupMember
    	i.writeInt(ds,new String("groupmember"), groupMember.getValue());
    	
    } 
    
    /**
     * Writes the spine list for this dendrite. spineNumber is written
     * first as "dendrite spine number", then each of the first spineNumber
     * entries of spineData is asked to save itself through its own Save()
     * method.
     */
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
    
    /**
     * Writes the per-color PunctaContainer state so ignored-puncta flags
     * can be recovered on load (the puncta themselves are intended to be
     * recomputed from the raster). Each of the three myPuncta entries is
     * asked to save itself through its own Save() method.
     */
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
    
    /**
     * Flags every puncta of the given color channel as ignored by walking
     * myPuncta[color].myPuncta and setting its bC.isIgnored boolean to
     * true.
     */
    public void ignoreAllPuncta(int color)
    {
    	for(int k = 0; k < myPuncta[color].myPuncta.length; k++)
    	{
    		myPuncta[color].myPuncta[k].bC.isIgnored = true;
    	}
    }
    
    /**
     * Clears the ignored flag on every puncta of the given color channel
     * by walking myPuncta[color].myPuncta and setting each bC.isIgnored to
     * false.
     */
    public void resetAllPuncta(int color)
    {
    	for(int k = 0; k < myPuncta[color].myPuncta.length; k++)
    	{
    		myPuncta[color].myPuncta[k].bC.isIgnored = false;    	
    	}
    }
    
    /**
     * Draws all puncta of the given color channel onto the Graphics2D g.
     * If myPuncta is unallocated, or the specific myPuncta[color] slot is
     * null, or the underlying Puncta[] array p is null, the method returns
     * without drawing. Otherwise it iterates over p and asks each Puncta
     * to draw itself, passing the dendrite label and its own index k so
     * identifying text can be placed next to each spot.
     */
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
    
    /**
     * Replaces this dendrite's BooleanContainer bC with a caller-supplied
     * instance bc so ignored/selected state can be shared with outside
     * code.
     */
    public void linkBoolean(BooleanContainer bc)
    {
    	bC = bc;    	
    }
    
    /**
     * Returns whether this dendrite is currently flagged as ignored, as
     * stored in its BooleanContainer bC.
     */
    public boolean isIgnored()
    {
    	return bC.isIgnored();
    }
    
    /**
     * Returns whether this dendrite is currently flagged as selected, as
     * stored in its BooleanContainer bC.
     */
    public boolean isSelected()
    {
    	return bC.isSelected();
    }
    
    /**
     * Restores the per-color PunctaContainer state from di via IoContainer
     * i, forwarding to each of the three myPuncta slots' Load() method in
     * turn so their ignored/selected state is rebuilt. The version
     * parameter is passed through to support older save formats.
     */
    public void LoadPunctaInfo(DataInputStream di, IoContainer i,int version)
    {   
    	myPuncta[0].Load(di, i,version);
    	myPuncta[1].Load(di, i,version);
    	myPuncta[2].Load(di, i,version);
    }
    
    /**
     * Applies per-channel automatic ignore rules to every color slot. The
     * thresholds array gives per-channel intensity thresholds, ignoreCriteria
     * supplies an IgnoreCriteria rule for each color, and the Raster r is
     * the source of pixel data. Each myPuncta slot's autoIgnore() is
     * invoked in turn to mark puncta that fail its matching criterion.
     */
    public void autoIgnore(int[] thresholds,IgnoreCriteria[] ignoreCriteria,Raster r)
    {
    	myPuncta[0].autoIgnore(thresholds,ignoreCriteria[0],r);
    	myPuncta[1].autoIgnore(thresholds,ignoreCriteria[1],r);
    	myPuncta[2].autoIgnore(thresholds,ignoreCriteria[2],r);
    }
    
    /**
     * Inverts the ignored flag on every puncta in every color channel by
     * forwarding to each myPuncta slot's invertIgnored() method.
     */
    public void invertIgnored()
    {
    	myPuncta[0].invertIgnored();
    	myPuncta[1].invertIgnored();
    	myPuncta[2].invertIgnored();
    }
    
    /**
     * Restores the previously saved ignored flags on every puncta in every
     * color channel by forwarding to each myPuncta slot's
     * restoreIgnored() method.
     */
    public void restoreIgnored()
    {
    	myPuncta[0].restoreIgnored();
    	myPuncta[1].restoreIgnored();
    	myPuncta[2].restoreIgnored();
    }
    
    /**
     * Applies overlap-based auto-ignore rules across the three color
     * channels using an OverlapObjectArray oA constructed from
     * dendriteArea.getBounds(). The rgb array's entries say which channels
     * are active; their Puncta arrays are loaded via loadRed/loadGreen/
     * loadBlue so oA knows which objects are present. Then for each active
     * channel, oA.autoIgnore is called with that channel's row of
     * restoreRed/restoreGreen/restoreBlue (channels that must be restored
     * when hit) and ifRed/ifGreen/ifBlue (channels that trigger the rule).
     */
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
    
    /**
     * Associates puncta with spines: any puncta that falls within a
     * pixel-radius r of a spine is assigned to that spine, while puncta
     * outside the spine radii are ignored. A Shape array spineR is filled
     * with each SpineInfo.getSpineRadius(r). The current ignored-flags
     * snapshot bb is saved so it can be replayed. An OverlapObjectArray
     * oA is built over dendriteArea.getBounds() and loaded with the
     * myPuncta[color] array through loadRed/loadGreen/loadBlue depending
     * on color. oA.spineAreaRestore returns a two-dimensional Puncta array
     * num where num[j] is the set of puncta on spine j, and this is
     * assigned back to spineData[j].myRedPuncta/myGreenPuncta/myBluePuncta
     * depending on color. Finally the earlier ignored-state bb is re-applied
     * via reignorepuncta so the caller's prior choices are preserved.
     */
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
    
    /**
     * Flags every puncta in the given color channel whose area is less
     * than size as ignored. The local p is the underlying Puncta array of
     * myPuncta[color]; the loop sets p[k].bC.isIgnored to true for each
     * small punctum so they stop being counted.
     */
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
    
    /**
     * Re-applies a previously captured ignored-state snapshot b onto the
     * Puncta array p. For every index where b[k] is true but the current
     * puncta at p[k] is not already ignored, invertIgnored() is called so
     * its flag is toggled back on. Used after automatic re-analysis to
     * restore user choices.
     */
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
    
    /**
     * Iterates over spineData and asks every SpineInfo to compute its
     * shaft intensity via
     * SpineInfo.calculateSpineShaftIntensity(threshold, r, iH, p), forwarding
     * the per-channel thresholds, raster array r, image height iH, and
     * PunctaCounter helper p.
     */
    public void calculateSpineShaftIntensity(int[] threshold,int[] r,int iH,PunctaCounter p)
    {
    	for(int k = 0; k < spineData.length; k++)
    	{
    		spineData[k].calculateSpineShaftIntensity(threshold, r, iH, p);
    	}
    }
    
    /**
     * DataSource entry point returning a single scalar measurement for
     * this dendrite. The parameter i is a DataType code selecting which
     * measurement to compute: DendriteLength, PunctaDensity,
     * PunctaIntegratedIntensity, PunctaArea, SpineDesnity (sic),
     * MushroomSpineDensity, ThinSpineDensity, StubbySpineDensity, or
     * FilopodiaDensity. The appropriate getter is called and its result
     * cast to float. calibration is the pixel-to-unit scale and color
     * selects which channel's puncta to query. Any unknown i returns 0.
     */
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
    
    /**
     * Hook for Sholl-based filtering of puncta. The base class
     * implementation is empty; ShollDendrite overrides it to honour Sholl
     * rings when rendering into Graphics2D g and g2 using the dendrite
     * label dendriteNum and the selected color.
     */
    public void ShollFilter(Graphics2D g, Graphics2D g2, int dendriteNum,int color)
    {
    	
    }
    
    /**
     * Hook for ignoring puncta outside Sholl ring ranges. The base class
     * implementation is empty; ShollDendrite overrides it to use the
     * position list pL and the requested color channel when deciding
     * which puncta to flag as ignored.
     */
    public void ShollIgnore(int[] pL,int color)
    {
    	
    }
    
    /**
     * Draws a small marker on the currently selected dendrite segment
     * projected along its axis from the pointer position p. If
     * selectedSegment is negative the method does nothing. Otherwise it
     * computes the segment's dx/dy delta, the slope dy/dx, and the
     * intercept of the line through the selected segment's starting
     * vertex. When |dx| > |dy| the marker is placed at (p.x, p.x*slope +
     * intercept); otherwise, to avoid division artefacts for nearly
     * vertical segments, the point is placed at ((p.y - intercept)/slope,
     * p.y). The marker is drawn as a 2x2 filled oval on Graphics g.
     */
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
    
    /**
     * Returns the point that drawPoint would mark for the given pointer
     * position p. Using the same slope and intercept derived from the
     * selectedSegment's endpoints, it projects p onto the segment's line:
     * when |dx| > |dy| the returned Point has x = p.x and y = p.x*slope +
     * intercept; otherwise the returned Point has x = (p.y -
     * intercept)/slope and y = p.y.
     */
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
    
    /**
     * Highlights the currently selected skeleton segment on Graphics g.
     * When selectedSegment is non-negative the current color c is saved,
     * the segment is drawn in orange between xList[selectedSegment]/
     * yList[selectedSegment] and xList[selectedSegment + 1]/yList[...+1],
     * the previous color is restored, and drawPoint(g, childPoint) is
     * called to also mark the attachment point used when branching.
     */
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
    
    /**
     * Finds which skeleton segment (if any) the point p lies on. The loop
     * calls withinSegment(p, k) for every segment index k from 0 to
     * xList.length - 2 and returns the first matching index. If no
     * segment contains p the method returns -1.
     */
    public int withinSegments(Point p)
    {
    	for(int k = 0; k < xList.length - 1; k++)
    	{
    		if(withinSegment(p,k))
    			return k;
    	}
    	
    	return -1;
    }
    
    /**
     * Tests whether point p falls within the axis-aligned bounding box of
     * segment number segment. The local x1/x2 and y1/y2 are computed as
     * the min and max of the two segment endpoints in xList and yList so
     * the box is correctly oriented regardless of segment direction. p is
     * reported as within the segment when p.x is between x1 and x2 and
     * p.y is between y1 and y2. This implementation relies only on the
     * bounding box and can misclassify when segments overlap.
     */
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
    
        
    /**
     * Records k as the currently selected skeleton segment index, used by
     * drawSegment and drawPoint when rendering.
     */
    public void SelectSegment(int k)
    {
    	selectedSegment = k;
    }
    
    /**
     * Returns true when the given Point p lies inside dendriteArea. When
     * dendriteArea is null (outline not yet built) the method returns
     * false.
     */
    public boolean isWithin(Point p)
    {
    	if(this.dendriteArea == null)
    		return false;
    	
    	return dendriteArea.contains(p);	
    }
    
    /**
     * Stores p as the attachment point used when a child dendrite
     * branches off this one; drawSegment marks this location so the user
     * can see where branches originate.
     */
    public void setChildPoint(Point p)
    {
    	childPoint = p;
    }
    
    /**
     * Walks up the myParent chain to determine this dendrite's depth in
     * the branching hierarchy. A local Dendrite d starts at myParent and
     * the counter k starts at 1; while d is non-null, d advances to its
     * own myParent and k increments. The final k is returned (1 for a
     * primary dendrite, 2 for a secondary, and so on).
     */
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
    
    /**
     * Returns the distance along the parent dendrite from its first
     * vertex to the point where this dendrite branches off. When there is
     * no parent the method returns 0. Otherwise the accumulator f sums
     * Point.distance() between consecutive vertices of myParent up to
     * parentSegment, then adds the distance from
     * myParent.xList[parentSegment]/myParent.yList[parentSegment] to this
     * dendrite's own first vertex (xList[0], yList[0]) so the branch
     * attachment point is included in the total.
     */
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
    
    /**
     * Computes a straightened-out starting distance for this dendrite so
     * normalized Sholl analysis can be performed on branched trees. The
     * accumulator f starts at zero and the local Dendrite d starts at
     * this dendrite. In the loop f adds d.getLengthtoPoint() at each
     * level and d advances to its myParent until the chain ends. The
     * accumulated pixel distance is then multiplied by calibration and
     * returned as a float.
     */
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
