package neuron_analyzer;

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
public class groupVector {
groupLeader[] vector = new groupLeader[25];
int groupCounter;
    Integer integer;

//first one is always null

    public groupVector() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

    public void calcBounds(Integer[] list,int bX, int bY)
    {
        for(int k = 1; k < vector.length; k++)
        {
            if(vector[k] == null)
                break;

      vector[k].calcBounds(list, bX,bY);
        }
    }

    public void makeBoundary(neuronToolKit n)
    {
        for(int k = 1; k < vector.length; k++)
        {
            if(vector[k] == null)
                break;
            vector[k].createBorder(n);
        }

    }

    public String getName(int dendrite, int punctaNum)
    {
        //fix this it is stupid
        return  integer.toString(dendrite)+ "." + integer.toString(punctaNum);
    }

    public int getIntensity(int k)
    {
        return vector[k+1].intensity;
    }

    public double getArea(int k,double calibration)
    {
        return vector[k+1].counter * (calibration*calibration);
    }

    private void jbInit() throws Exception {
    }
    
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
