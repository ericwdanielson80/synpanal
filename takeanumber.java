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
    public takeanumber(int aThresh) {
        assThresh = aThresh;
    }

    public int takeGroupNumber()
    {
        groupCounter++;
        return groupCounter;
    }

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

   public void addGroup(int group, PixelBinSortNode p)
   {       
       assList[group].Add(p);
       assListCounter[group]++;
   }

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

   public void destroy()
   {
       assListCounter = null;
       usedNumbers = null;
       assList = null;
   }


}
