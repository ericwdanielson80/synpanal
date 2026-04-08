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
public class associateGroupVector extends groupVector {
Keys First;
int keyCounter;


    public associateGroupVector() {
        super();
        for(int k = 1; k < vector.length; k++)
        {
            if(vector[k] == null)
                addKey(k);
        }
    }

    private groupLeader remove(int groupNum)
    {
        groupLeader out = vector[groupNum];
        vector[groupNum] = null;
        addKey(groupNum);
        //System.gc();
        return out;
    }

    private void addKey(int groupNum)
    {
        //needs a positive number
        keyCounter++;
        if(First == null)
        {
            First = new Keys(groupNum);
            return;
        }
        First.Add(new Keys(groupNum));


    }

    public int getNewNumber()
    {
        int out;
        if(keyCounter == 0)
        {
            groupLeader[] tmp = vector;
            vector = new groupLeader[tmp.length *2];
            for(int k = 0; k < tmp.length; k++)
            {
                vector[k] = tmp[k];
            }
            for(int k = 1; k < vector.length; k++)
            {
                if(vector[k] == null)
                    addKey(k);
            }
            return getNewNumber();
        }
        if(keyCounter == 1)
        {
            out = First.key;
            First = null;
            keyCounter--;
            return out;
        }
        out = ((Keys)First.next).key;
        First.next.Remove();
        keyCounter--;
        return out;
    }



    public int[][] merge(int g1, int g2,int[][] data)
    {
        //not sure if needs positive or negative
        groupLeader g = vector[g1];
        groupLeader tmp = remove(g2); //adds g2 to keys

        //if(tmp == null)
            ////
        g.Last.next = tmp.First;
        tmp.First.previous = g.Last;

        g.counter += tmp.counter;
        g.intensity += tmp.intensity;

        g.Last = tmp.Last;

        vector[g1] = g;
        g = null;
        PixelBinSortNode tmp2 = tmp.First;
        while(tmp2 != null)
        {
            data[tmp2.getX()][tmp2.getY()] = g1;
            tmp2 = (PixelBinSortNode)tmp2.next;
        }
        tmp = null;
        tmp2 = null;
        //System.gc();
        return data;
    }

    public groupLeader checkAssociate(int groupNum, int limit)
    {//needs a positive number
        if(vector[groupNum].counter < limit)
        {
            return null;
        }

        return remove(groupNum);
    }


}
