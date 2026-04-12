package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
/**
 * Holds geometric and intensity measurements for a single dendritic spine.
 * Three int[4] coordinate arrays (sL for the length line, sW for the head
 * width, and sN for the neck) describe the three reference segments drawn
 * by the user; from these the class derives spine length, width, neck
 * width, and a classification (mushroom/thin/stubby/filopodia). It also
 * tracks per-channel puncta overlapping the spine, computed intensities for
 * the spine head and shaft, and the selection and ignore flags; it
 * implements Savable so the segment coordinates and derived values persist.
 */
public class SpineInfo implements Savable {
int[] sL;
int[] sW;
int[] sN;
public Rectangle myBounds;
Point center;
double spineLength;
double spineWidth;
double spineNeck;
Rectangle r;
boolean isSelected = false;
boolean isIgnored = false;
Puncta[] myRedPuncta;
Puncta[] myGreenPuncta;
Puncta[] myBluePuncta;
int[] SpineIntensity;
int[] ShaftIntensity;
int spineType;
boolean autoSpineType = true;

Double mWN; //for auto stuff
Double mWL; //for auto stuff
Double mNL; //for auto stuff

Double tWN; //for auto stuff
Double tWL; //for auto stuff
Double tNL; //for auto stuff

Double sWN; //for auto stuff
Double sWL; //for auto stuff
Double sNL; //for auto stuff

Double fWN; //for auto stuff
Double fWL; //for auto stuff
Double fNL; //for auto stuff


	/**
	 * Constructs a SpineInfo from the three segment endpoint arrays and a
	 * calibration factor. It populates sL, sW, and sN, computes the raw
	 * length/width/neck distances via calcLength, then builds the bounding
	 * rectangle myBounds containing all six endpoints and the center point,
	 * and finally classifies the spine type by calling calcSpineType. When
	 * length is null the constructor returns early before computing bounds.
	 */
	public SpineInfo(int[] length, int[] width, int[] neck, double calibration)
	{		
		sL = length;
		sW = width;
		sN = neck;
		spineLength = calcLength(length);
		spineWidth = calcLength(width);
		spineNeck = calcLength(neck);
		if(sL == null)
			return;
		myBounds = new Rectangle(new Point(sL[0],sL[1]));	
		myBounds.add(sL[2],sL[3]);
		myBounds.add(sW[0],sW[1]);
		myBounds.add(sW[2],sW[3]);
		myBounds.add(sN[0],sN[1]);
		myBounds.add(sN[2],sN[3]);
		center = new Point((int)myBounds.getCenterX(),(int)myBounds.getCenterY());
		spineType = calcSpineType(calibration);
	}
	
	/** Returns a placeholder name; per-spine naming has not been implemented. */
	public String getName()
	{
		return "notyet";
	}
	
	/** Returns the Euclidean distance between the two endpoints encoded in l, or zero if l is null. */
	public double calcLength(int[] l)
	{	
		if(l == null)
			return 0;
		return Point.distance(l[0],l[1], l[2],l[3]);
	}
	
	/** Returns the spine length in real units by multiplying the raw pixel length by calibration. */
	public double getLength(double calibration)
	{
		
		return spineLength * calibration;
	}
	
	/** Returns the spine head width in real units by multiplying the raw pixel width by calibration. */
	public double getWidth(double calibration)
	{		
		return spineWidth * calibration;
	}
	
	/** Returns the spine neck width in real units by multiplying the raw pixel neck width by calibration. */
	public double getNeckWidth(double calibration)
	{		
		return spineNeck * calibration;
	}
	
	/**
	 * Returns the human-readable spine type string. If autoSpineType is
	 * enabled the classification is recomputed via calcSpineType first; the
	 * numeric spineType is then mapped to Mushroom, Thin, Stubby, or
	 * Filopodia.
	 */
	public String getSpineType(double calibration){
		/*
		 * if spineNeck <<< spineHead return mushroom
		 * if spineNeck <= spineHead && spineNeck <<< spineLength return thin
		 * if spineNeck ~= spineLength return Stubby
		 * if >1 head return branched
		 */
		
		if(autoSpineType)
		spineType = calcSpineType(calibration);
		
		switch(spineType)
		{
		case 0: return "Mushroom";
		case 1: return "Thin";
		case 2: return "Stubby";
		case 3: return "Filopodia";		
		}
		
		return "error";	
	}
	
	/**
	 * Classifies the spine numerically (0 mushroom, 1 thin, 2 stubby,
	 * 3 filopodia) using several heuristic comparisons of spineNeck,
	 * spineWidth, and spineLength. Returns the stored spineType if
	 * autoSpineType is disabled. The ordering matches the author's original
	 * rule set.
	 */
	public int calcSpineType(double calibration){
		/*
		 * if spineNeck <<< spineHead return mushroom
		 * if spineNeck <= spineHead && spineNeck <<< spineLength return thin
		 * if spineNeck ~= spineLength return Stubby
		 * if >1 head return branched
		 */
		if(!autoSpineType)
			return spineType;
		
		if(spineNeck >= spineWidth && spineNeck >= spineLength)
		{
			return 2;//stubby
			/*
			 * 1 >= spineWidth/spineNeck && spineWidth/spineLength && 1 >- spineLength/spineNeck
			 */
		}
		
		if(spineLength > (3 * spineWidth) && spineLength > (3 * spineNeck))
			{
			
			return 3;//"Filopodia";
			}
		
		if(spineNeck <= spineWidth * 0.6)
			{
			
			return 0;//"Mushroom";
			}
		
		if(spineNeck * 1.5 < spineLength)
			{
			
			return 1;//"Thin";
			}
		
		
			return 2;
		//"Stubby";
		
	}
	
	/** Draws the three reference segments (length, width, neck) into g in a color that encodes the spine type. */
	public void drawSpine(Graphics2D g)
	{
		
		switch(spineType)
		{
		case 3: g.setColor(Color.ORANGE);break; 
		case 0: g.setColor(Color.BLUE);break;
		case 1: g.setColor(Color.RED);break;
		case 2: g.setColor(Color.PINK);break;
		}
				
		g.drawLine(sL[0],sL[1],sL[2],sL[3]);
		g.drawLine(sW[0],sW[1],sW[2],sW[3]);
		g.drawLine(sN[0],sN[1],sN[2],sN[3]);
		
	}
	
	/** Draws the three reference segments plus an oval of radius r centered on the spine, used for highlighting an area around the spine. */
	public void drawSpineRadius(Graphics2D g, int r)
	{
		
		g.drawLine(sL[0],sL[1],sL[2],sL[3]);
		g.drawLine(sW[0],sW[1],sW[2],sW[3]);
		g.drawLine(sN[0],sN[1],sN[2],sN[3]);		
		g.drawOval(center.x - r, center.y - r,2 * r,2 *r);
		//g.drawRect(myBounds.x, myBounds.y, myBounds.width, myBounds.height);
		
	}
	
	/** Placeholder for drawing the spine shaft overlay; currently a no-op. */
	public void drawSpineShaft(Graphics2D g, int r)
	{
		
	}
	
	/**
	 * Persists the spine measurements to the output stream via the
	 * IoContainer: the three endpoint arrays, the derived length/width/neck
	 * distances, the selection and ignored flags, and the automatic-mode
	 * and classification fields.
	 */
	public void Save(DataOutputStream ds, IoContainer i)
    {
        /*
         save int[] sL
         save int[] sW
         save int[] sN
         
         save double spineLength
         save double spineWidth
         save double spineNeck
         
         save boolean isSelected
         save boolean isIgnored
         
         save automode
         save spine type
         */	
		
		i.writeIntArray(ds,"Spine info sL", sL);
		i.writeIntArray(ds,"Spine info sW", sW);
		i.writeIntArray(ds,"Spine info sN", sN);
		
		i.writeDouble(ds,"Spine info spineLength", spineLength);
		i.writeDouble(ds,"Spine info spineWidth", spineWidth);
		i.writeDouble(ds,"Spine info spineNeck", spineNeck);
		
		i.writeBoolean(ds, "Spine info isSelected", isSelected);
		i.writeBoolean(ds, "Spine info isIgnored", isIgnored);
		
		i.writeBoolean(ds, "Spine automode info",autoSpineType);
		i.writeInt(ds, "Spine type",spineType);
    }
		
    
    /**
     * Reads the saved spine measurements back from the input stream using
     * the IoContainer helper. Depending on version it also restores the
     * auto-classification flag and a saved spineType; myBounds, center
     * and the spineType classification are then recomputed. Parameters
     * di and i are the stream and serialization helper, version controls
     * which fields are present, and groupList is unused for SpineInfo.
     */
    public Object Load(DataInputStream di, IoContainer i,int version, Group[] groupList)
    {
    	/*
        load int[] sL
        load int[] sW
        load int[] sN
        
        load double spineLength
        load double spineWidth
        load double spineNeck
        
        load boolean isSelected
        load boolean isIgnored
        */	
    	sL = i.readIntArray(di, "load sL");
    	sW = i.readIntArray(di,"load sW");
    	sN = i.readIntArray(di, "load sN");
    	
    	spineLength = i.readDouble(di,"load spineLength");
    	spineWidth = i.readDouble(di,"load spineWidth");
    	spineNeck = i.readDouble(di,"load spineNeck");
    	
    	isSelected = i.readBoolean(di, "load selected");
    	isIgnored = i.readBoolean(di,"load ignored");
    	
    	if(version > 0)
    	{
    		autoSpineType = i.readBoolean(di, "load autoMode");
    		int type = i.readInt(di, "load spine type");
    		if(!autoSpineType)
    			spineType = type;
    	}
    	
    	myBounds = new Rectangle(new Point(sL[0],sL[1]));		
		myBounds.add(sW[0],sW[1]);
		myBounds.add(sN[0],sN[1]);
		
		center = new Point((int)myBounds.getCenterX(),(int)myBounds.getCenterY());		
    	spineType = calcSpineType(0.12726);
    	return null;
    }        
    
    /** Returns an Ellipse2D of radius r centered on the spine for use as a selection/overlap shape. */
    public Shape getSpineRadius(int r)
    {
    	Shape s = new Ellipse2D.Float(center.x - r, center.y - r,2 * r,2 *r);
    	return s;
    }
    
    /**
     * Counts the non-null, non-ignored puncta attached to this spine for
     * the requested color channel. The switch picks myRedPuncta,
     * myGreenPuncta, or myBluePuncta; local out accumulates the count.
     */
    public int getPunctaNum(int color)
    {
    	Puncta[] myPuncta = null;
    	switch(color)
    	{
    	case 0: myPuncta = myRedPuncta; break;
    	case 1: myPuncta = myGreenPuncta; break;
    	case 2: myPuncta = myBluePuncta; break;
    	}
    	
    	if(myPuncta == null)
    		return 0;
    	
    	int out = 0;
    	for(int k = 0; k < myPuncta.length; k++)
    	{
    		if(myPuncta[k] != null && !myPuncta[k].isIgnored())
    			out++;
    		
    	}
    	return out;
    }
    
    /**
     * Computes integrated intensity and area within two rectangular regions
     * derived from the spine segments: a shaft rectangle (sL-bottom plus
     * sN endpoints) and a spine-head rectangle (sW-top plus sN endpoints).
     * For each threshold value the helper p.getCellBodyIntensity is asked
     * to sum the intensities in both regions; the per-channel results are
     * stored into SpineIntensity and ShaftIntensity, with the area (index
     * 3) filled from the last iteration. The locals Shafttemp/Spinetemp
     * collect the returned sums, and ShaftArea/SpineArea are Area objects
     * used for the intensity scan.
     */
    public void calculateSpineShaftIntensity(int[] threshold,int[] r,int iH,PunctaCounter p)
    {
    	SpineIntensity = new int[4]; //rgb & area
    	ShaftIntensity = new int[4]; //rgb & area
    	
    	/*sL will store shaft bottom segment of rectangle info
    	 * sW will store spinetop of rectangle info
    	 * sN will store spineBase of rectangle inf0 (divider of spineshaft)
    	 */
    	
    	int[] Shafttemp = new int[] {0,0};
    	int[] Spinetemp = new int[] {0,0};
    	Polygon ShaftRect = new Polygon(); //may have to change to a polygon
    	ShaftRect.addPoint(sL[0],sL[1]);
    	ShaftRect.addPoint(sL[2],sL[3]);
    	ShaftRect.addPoint(sN[0],sL[1]);
    	ShaftRect.addPoint(sN[2],sL[3]);
    	
    	Polygon SpineRect = new Polygon();
    	SpineRect.addPoint(sW[0],sW[1]); 
    	SpineRect.addPoint(sW[2],sW[3]);
    	SpineRect.addPoint(sN[0],sL[1]);
    	SpineRect.addPoint(sN[2],sL[3]);
    	
    	
    	Area ShaftArea = new Area(ShaftRect);
    	Area SpineArea = new Area(SpineRect);
    	
    	for(int k = 0; k < threshold.length; k++)
    	{
    	Shafttemp = p.getCellBodyIntensity(r, iH,ShaftRect.getBounds(),0,ShaftArea, threshold[k]);
    	Spinetemp = p.getCellBodyIntensity(r, iH,SpineRect.getBounds(),0,SpineArea, threshold[k]);		
		ShaftIntensity[k] = Shafttemp[k];		
		SpineIntensity[k] = Spinetemp[k];
		
    	}
    	ShaftIntensity[3] = Shafttemp[3];
    	SpineIntensity[3] = Spinetemp[3];
    	
    }
    
    /** Manually sets the spine classification to i and optionally toggles autoSpineType; disables auto-classification when auto is false. */
    public void setSpineType(int i,boolean auto)
    {
    	autoSpineType = auto;
    	spineType = i;
    }
    
    /** Returns whether the configured width-to-neck rule for the given spine type is satisfied; a stored threshold of -1 means "always true". */
    private boolean getWidthtoNeck(int type)
    {
    	switch(type)
    	{
    	case 0: if(mWN.doubleValue() == -1) return true; return mWN.doubleValue() >= spineWidth/spineLength;
    	case 1: if(tWN.doubleValue() == -1) return true; return tWN.doubleValue() >= spineWidth/spineLength;
    	case 2: if(sWN.doubleValue() == -1) return true; return sWN.doubleValue() >= spineWidth/spineLength;
    	case 3: if(fWN.doubleValue() == -1) return true; return fWN.doubleValue() >= spineWidth/spineLength;
    	}
    	
    	return false;
    }
    
    /** Returns whether the configured width-to-length rule for the given spine type is satisfied; a stored threshold of -1 means "always true". */
    private boolean getWidthtoLength(int type)
    {
    	switch(type)
    	{
    	case 0: if(mWL.doubleValue() == -1) return true; return mWL.doubleValue() >= spineWidth/spineLength;
    	case 1: if(tWL.doubleValue() == -1) return true; return tWL.doubleValue() >= spineWidth/spineLength;
    	case 2: if(sWL.doubleValue() == -1) return true; return sWL.doubleValue() >= spineWidth/spineLength;
    	case 3: if(fWL.doubleValue() == -1) return true; return fWL.doubleValue() >= spineWidth/spineLength;
    	}
    	
    	return false;
    }
    
    /** Returns whether the configured neck-to-length rule for the given spine type is satisfied; a stored threshold of -1 means "always true". */
    private boolean getNecktoLength(int type)
    {
    	switch(type)
    	{
    	case 0: if(mNL.doubleValue() == -1) return true; return mNL.doubleValue() >= spineWidth/spineLength;
    	case 1: if(tNL.doubleValue() == -1) return true; return tNL.doubleValue() >= spineWidth/spineLength;
    	case 2: if(sNL.doubleValue() == -1) return true; return sNL.doubleValue() >= spineWidth/spineLength;
    	case 3: if(fNL.doubleValue() == -1) return true; return fNL.doubleValue() >= spineWidth/spineLength;
    	}
    	
    	return false;
    }
}
