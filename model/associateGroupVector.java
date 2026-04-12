package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Specialised groupVector used during connected-component labelling of
 * thresholded pixels when pixels are initially assigned "associate" group
 * numbers that can later be merged into real groups. It keeps a linked
 * free-list of currently unused group indices (stored as Keys nodes) so
 * that freshly allocated pixel runs get small, reusable identifiers, and
 * it provides operations to allocate a new id, merge two groups together
 * (patching the 2-D label map), and promote an associate group to a
 * permanent group once its pixel count exceeds a threshold.
 */
public class associateGroupVector extends groupVector {
Keys First;
int keyCounter;


    /**
     * Builds an empty associate-group vector by calling the base class
     * constructor and then walking every slot in the underlying vector
     * (skipping index 0 which is always reserved as null) to push a free
     * Keys entry onto the internal free list for each empty slot.
     */
    public associateGroupVector() {
        super();
        for(int k = 1; k < vector.length; k++)
        {
            if(vector[k] == null)
                addKey(k);
        }
    }

    /**
     * Removes the groupLeader stored at groupNum, returns it to the
     * caller, and pushes the now-free index back onto the Keys free list
     * so it can be reused. The local out variable captures the removed
     * leader so its pixel chain can be processed by the caller. The
     * parameter groupNum is the slot being freed.
     */
    private groupLeader remove(int groupNum)
    {
        groupLeader out = vector[groupNum];
        vector[groupNum] = null;
        addKey(groupNum);
        //System.gc();
        return out;
    }

    /**
     * Adds a free index to the internal Keys linked list that tracks
     * available slots in the vector, incrementing keyCounter. If the
     * list is empty, a new Keys head is created; otherwise the new Keys
     * is appended to the existing chain via First.Add. The parameter
     * groupNum is the integer slot that is now free.
     */
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

    /**
     * Returns the next free group index. If no free indices remain
     * (keyCounter == 0) the underlying vector is doubled in length,
     * tmp holds the old array during the copy, every newly created empty
     * slot is added back via addKey, and the method recurses to retrieve
     * a number from the enlarged pool. Otherwise the head of the Keys
     * list is consumed, decrementing keyCounter: when there is only one
     * free index it is returned directly and First is cleared; otherwise
     * the second entry is unlinked via First.next.Remove and its key is
     * returned. The local out holds the integer ultimately returned.
     */
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



    /**
     * Merges the associate group g2 into g1 and returns the updated 2-D
     * pixel-label map. The leader at g1 is kept; the leader at g2 is
     * removed (freeing its index back onto the key list). The two
     * doubly-linked pixel chains are stitched together so g.Last.next
     * becomes tmp.First and tmp.First.previous points back to g.Last.
     * The counters and intensity sums are added into g, and g.Last is
     * updated to the tail of tmp. Finally every pixel previously
     * labelled g2 in the data[x][y] map is relabelled g1 by walking the
     * tmp2 iterator down tmp.First. The parameters g1 and g2 are the
     * absorbing and absorbed group indices and data is the per-pixel
     * group-id map that will be patched and returned.
     */
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

    /**
     * Checks whether the associate group stored at groupNum has grown
     * large enough (its counter is at least limit) to be promoted. If it
     * has not reached the threshold, null is returned and the group
     * stays in place; otherwise it is removed from the vector (returning
     * its index to the free list) and the removed groupLeader is
     * returned for the caller to add as a permanent group elsewhere.
     * The parameters are the positive group index groupNum and the size
     * threshold limit.
     */
    public groupLeader checkAssociate(int groupNum, int limit)
    {//needs a positive number
        if(vector[groupNum].counter < limit)
        {
            return null;
        }

        return remove(groupNum);
    }


}
