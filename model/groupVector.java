package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

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
/**
 * Dynamic array of groupLeader objects used to accumulate pixel runs
 * during connected-component puncta detection. Index 0 is always
 * reserved null; real groups start at index 1. The class offers
 * helpers to append a new groupLeader, add further pixels into an
 * existing group (growing the array as needed), prune empty slots
 * in finalize, compute bounding boxes and pixel borders for every
 * group, build Puncta objects for the non-null entries, and retrieve
 * per-group integrated intensity and calibrated area.
 */
public class groupVector {
public groupLeader[] vector = new groupLeader[25];
int groupCounter;
    Integer integer;

//first one is always null

    /**
     * Default constructor; invokes jbInit (currently a no-op) and
     * prints any exception's stack trace.
     */
    public groupVector() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Appends a groupLeader g to the array, doubling the array size
     * repeatedly through local j and v2 if the next slot would
     * overflow the current capacity. groupCounter is incremented
     * and the new slot index is returned.
     */
    public int Add(groupLeader g)
    {
        if(groupCounter +1 > vector.length-1)
        {
            int j = vector.length-1;
            while(j < groupCounter + 1)
            {
                j*=2;
            }
            groupLeader[] v2 = vector;
            vector = new groupLeader[j];
            for(int k = 0; k < v2.length; k++)
            {
                vector[k] = v2[k];
            }
            v2 = null;
        }

        groupCounter++;
        vector[groupCounter] = g;
        return groupCounter;


    }

    /**
     * Compacts the vector: first pass counts non-null entries into
     * groupCounter to size a fresh v2 array, second pass copies each
     * non-null groupLeader into v2 at a newly incremented
     * groupCounter index. The old vector is discarded and v2 takes
     * its place.
     */
    public void finalize()
    {
        groupCounter = 0;
        for(int k = 0; k < vector.length; k++)
        {
            if(vector[k] != null)
                groupCounter++;
        }
        groupLeader[] v2 = new groupLeader[groupCounter + 1];
        groupCounter = 0;
        for(int k = 0; k < vector.length; k++)
        {
            if(vector[k] != null)
                {
                    groupCounter++;
                    v2[groupCounter] = vector[k];

                }
        }

        vector = null;
        vector = v2;


    }

    /**
     * Adds the pixel n with the given intensity to the group at
     * groupNum, growing the internal array (via the local j and v2
     * variables) if groupNum lies beyond the current capacity. If
     * the slot is currently null a new groupLeader is constructed
     * there and groupCounter is bumped; otherwise the pixel is
     * appended to the existing chain through groupLeader.Add.
     */
    public void Add(PixelBinSortNode n, int groupNum, int intensity)//adds a pixel to the group
    {
        if(groupNum > vector.length - 1) //makes array bigger
        {
            int j = vector.length - 1;
            while(j < groupNum)
            {
                j*=2;
            }
            groupLeader[] v2 = new groupLeader[j];
            System.arraycopy(vector,0,v2,0,vector.length);
            vector = null;
            vector = v2;
        }

        if(vector[groupNum] == null)
            {
                vector[groupNum] = new groupLeader(n, intensity);
                groupCounter++;
            }
        else
        {
            vector[groupNum].Add(n,intensity);
        }


    }

    /**
     * Walks every non-null group in the vector and invokes
     * calcBounds on it to compute its bounding rectangle. The list,
     * bX and bY parameters are forwarded unchanged to each call.
     * The loop exits early at the first null slot on the assumption
     * that the array has been compacted.
     */
    public void calcBounds(Integer[] list,int bX, int bY)
    {
        for(int k = 1; k < vector.length; k++)
        {
            if(vector[k] == null)
                break;

      vector[k].calcBounds(list, bX,bY);
        }
    }

    /**
     * Asks every non-null groupLeader to construct its border
     * polygon using the provided neuronToolKit n; the loop exits at
     * the first null slot.
     */
    public void makeBoundary(neuronToolKit n)
    {
        for(int k = 1; k < vector.length; k++)
        {
            if(vector[k] == null)
                break;
            vector[k].createBorder(n);
        }

    }

    /**
     * Returns a "dendrite.punctaNum" style label string. Uses the
     * integer field for formatting (the inline comment acknowledges
     * the awkward implementation).
     */
    public String getName(int dendrite, int punctaNum)
    {
        //fix this it is stupid
        return  integer.toString(dendrite)+ "." + integer.toString(punctaNum);
    }

    /** Returns the accumulated intensity of the k-th real group (slot k+1). */
    public int getIntensity(int k)
    {
        return vector[k+1].intensity;
    }

    /**
     * Returns the calibrated area of the k-th real group: pixel
     * count times calibration squared (microns^2 per pixel).
     */
    public double getArea(int k,double calibration)
    {
        return vector[k+1].counter * (calibration*calibration);
    }

    /** Legacy initialiser hook; currently empty. */
    private void jbInit() throws Exception {
    }
    
    /**
     * Builds a Puncta[] containing one Puncta per non-null group.
     * The first pass counts non-null entries into counter to size
     * the array; the second pass calls vector[k].makePuncta() into
     * the next p slot. Returns the fully populated array.
     */
    public Puncta[] makePuncta()
    {
    	int counter = 0;
    	for(int k = 0; k < vector.length; k++)
    	{
    		if(vector[k] != null)
    			counter++;
    	}
    	Puncta[] p = new Puncta[counter];
    	counter = 0;
    	for(int k = 0; k < vector.length; k++)
    	{
    		if(vector[k] != null)
    			{
    			p[counter] = vector[k].makePuncta();
    			counter++;
    			}
    	}
    	
    	return p;
    }

}
