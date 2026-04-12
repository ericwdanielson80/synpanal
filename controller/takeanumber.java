package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

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
 * Free-list based allocator used during the pixel-grouping phase of
 * threshold analysis. Real group numbers are handed out monotonically
 * by takeGroupNumber; associate (transient) groups come from an
 * internal PixelBinSortNode[] whose free indices are tracked by an
 * IntLinkedList so they can be reused quickly. Once an associate
 * group reaches a size threshold its pixel chain is released for
 * promotion into a real group and its index is recycled.
 */
public class takeanumber {
int groupCounter;
int assCounter;
PixelBinSortNode[] assList = new PixelBinSortNode[5];
int[] assListCounter = new int[5];
int assThresh;
IntLinkedList usedNumbers;
//this is to make the associated group crap more efficient
//associated groups are transient unlike real groups and i need a quick way to find an opne group number
//and a quick way to access groups in use
//using a combo of a vector and linked list
    /**
     * Stores the associate-group size threshold aThresh that
     * determines when a transient group is large enough to be
     * promoted into a real group.
     */
    public takeanumber(int aThresh) {
        assThresh = aThresh;
    }

    /** Returns the next sequential permanent group id by incrementing groupCounter. */
    public int takeGroupNumber()
    {
        groupCounter++;
        return groupCounter;
    }

    /**
     * Allocates an associate-group id for the pixel node p. If
     * usedNumbers has recycled entries, the head is removed and its
     * key reused: p is stored at assList[out], the slot counter is
     * bumped, and the popped linked-list node is destroyed. When no
     * recycled id is available a new slot is appended via
     * makeGroup. The local out holds the chosen id that is returned
     * to the caller.
     */
    public int takeAssociateGroupNumber(PixelBinSortNode p)
   {       
       int out;

       if(usedNumbers != null)
           {
               IntLinkedList temp;
               out = usedNumbers.myValue;
               temp = usedNumbers;
               usedNumbers = usedNumbers.next;
               temp.destroy();
               assList[out] = p;
               assListCounter[out]++;
               return out;
           }
       assCounter++;
       makeGroup(p);
       return assCounter;
   }

   /**
    * Grows the associate-group storage when assCounter would exceed
    * the current array capacity. assLength doubles the array size
    * and temp/temp2 hold the copies used to rebuild assList and
    * assListCounter before the new p pointer is written at
    * assCounter and its counter initialised to 1.
    */
   private void makeGroup(PixelBinSortNode p)
   {


       int assLength = 0;
       if(assCounter > assList.length - 1)
       {
           assLength = assList.length * 2;
           PixelBinSortNode[] temp = new PixelBinSortNode[assLength];
           int[] temp2 = new int[assLength];
           for (int k = 0; k < assList.length; k++) {
               temp[k] = assList[k];
               temp2[k] = assListCounter[k];
           }
           assList = temp;
           assListCounter = temp2;
           temp = null;
           temp2 = null;
           System.gc();
       }

       assList[assCounter] = p;
       assListCounter[assCounter]++;
   }

   /**
    * Adds a new PixelBinSortNode p to the existing associate group
    * index, appending it to the linked pixel chain at assList[group]
    * and incrementing that group's counter.
    */
   public void addGroup(int group, PixelBinSortNode p)
   {
       assList[group].Add(p);
       assListCounter[group]++;
   }

   /**
    * Inspects associate-group n and, if its pixel count has reached
    * assThresh, releases it: the index n is pushed onto usedNumbers
    * so it can be reused, the chain head is copied into the local
    * out, and assList/assListCounter are cleared for slot n. The
    * returned chain lets the caller build a permanent group from
    * those pixels; null is returned when the group is still too
    * small.
    */
   public PixelBinSortNode checkAssGroup(int n)
   {
       //checks if an assgroup has too many members
       //returns the assgroup so a new group can be made
       //inserts that assgroup number into the used number list for quick access
       if(assListCounter[n] < assThresh)
           return null;
       if(usedNumbers == null)
           usedNumbers = new IntLinkedList(n);
       else
           usedNumbers.Add(new IntLinkedList(n));
       PixelBinSortNode out = assList[n];
       assList[n] = null;
       assListCounter[n] = 0;

       return out;
   }

   /**
    * Placeholder for an associate-group merge operation; all logic
    * is currently commented out. The parameters n1/n2 were the two
    * groups to merge, group was the 2-D label map to update, and
    * xyList the per-bin coordinate lookup.
    */
   public void mergeGroups2(int n1, int n2, int[][] group,Integer[] xyList)
   {
      /* 
       PixelBinSortNode tmp = assList[-1* n2];

       while(tmp != null)
       {
           group[tmp.getX()][tmp.getY()] = n1;
           if(n1 < 0)
           {
               assList[ -1 *
                       n1].Add(new PixelBinSortNode(xyList[tmp.getX()],
                       xyList[tmp.getY()]));
               assListCounter[ -1 * n1]++;
           }
           tmp = tmp.next;
       }


       if(usedNumbers == null)
           usedNumbers = new IntLinkedList(-1*n2);
       else
           usedNumbers.Add(new IntLinkedList(-1*n2));

       assList[-1*n2] = null;
System.gc();       assListCounter[-1*n2] = 0;*/

   }

   /** Releases the internal arrays and linked lists so the allocator's memory can be collected. */
   public void destroy()
   {
       assListCounter = null;
       usedNumbers = null;
       assList = null;
   }


}
