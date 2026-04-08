package neuron_analyzer;
import java.awt.image.WritableRaster;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Stack;
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
public class PunctaCounter {
    //looks like this will calculate the number&intensity of the puncta for each threshold value
    int[][] data;  //intensity data
    int[][] group; //groupdata
    int[][] smartSort;
    int[] smartSortBin;
    int groupCounter;
    xyStack groupStack;
    xyStack recheck;

    groupLeader[] sortedData;
    groupVector groupData;// = new groupVector();
    associateGroupVector assoData;// = new associateGroupVector();

    int x;
    int y;
    int threshold;

    int assThresh = 3; //num of pixels for a group
    Integer[] xyList;

    int[] one = new int[4];
    int[] two = new int[2];
    int three;
    int four;
    int type;
    int pixelCounter;

    int bitOP,tempInt,bitShift;
    int width;
    int height;
    int imageWidth;
    int imageHeight;
    int currentTh;
    int startTh = 255;
    int pixelNum;


    boolean joinedGroup = false;
    
    public PunctaCounter(int imagewidth,int imageheight)
    {
                imageWidth = imagewidth;
                imageHeight = imageheight;
                xyList = new Integer[imageWidth];
                for(int k = 0; k < xyList.length; k++)
        {
            xyList[k] = new Integer(k);
        }


    }
    
    public PunctaCounter(PunctaCounter p)
    {
    	imageWidth = p.imageWidth;
    	imageHeight = p.imageHeight;
    	xyList = p.xyList;
    }
    
    public int[] getCellBodyIntensity(int[] wr, int imageHeight,Rectangle r,int rgb,Area a,int th)
    {
    	width = r.width;//calculated from the bounds of dendrite area
        height = r.height;
        threshold = th;
        
        x = r.x;
        y = r.y;
        if(rgb == 0)
            {
                bitOP = 0x00FF0000;
                bitShift = 16;
            }
        if(rgb == 1){
            bitOP = 0x0000FF00;
        bitShift = 8;
        }
        if(rgb == 2){
            bitOP = 0x000000FF;
            bitShift = 0;
        }
        data = new int[width][height];

        copyData(wr,a);
        int CellIntensity = 0;
        for(int k = 0; k < data.length; k++)
        {
        	for(int j = 0; j < data[0].length; j++)
        	{
        		if(data[k][j] >= threshold)
        		{
        			CellIntensity += data[k][j];
        		}
        		
        	}
        }
        
        return new int[] {CellIntensity,pixelNum};
    	
    }
    
    public groupVector countPuncta(int[] wr,int imageheight, Rectangle r, int rgb,Area a, int th, int stth,int[] totalIntensity) {
        width = r.width;//calculated from the bounds of dendrite area
        height = r.height;
        threshold = th;
        groupData = new groupVector();
        assoData = new associateGroupVector();
        startTh = stth;


        x = r.x;
        y = r.y;        
        if(rgb == 0)
            {
                bitOP = 0x00FF0000;
                bitShift = 16;
            }
        if(rgb == 1){
            bitOP = 0x0000FF00;
        bitShift = 8;
        }
        if(rgb == 2){
            bitOP = 0x000000FF;
            bitShift = 0;
        }
        data = new int[width][height];

        copyData(wr,a);
        int totalI = 0;
        for(int k = 0; k < data.length; k++)
        {
        	for(int j = 0; j < data[0].length; j++)
        	{
        		if(data[k][j] >= threshold)
        		{
        			totalI += data[k][j];
        		}
        		
        	}
        }
        totalIntensity[0] = totalI;
        totalIntensity[1] = pixelNum;

        group = new int[width][height];

        cleverSort();

        //groupPuncta();

        groupCounter = 0;
        groupStack = new xyStack();
        recheck = new xyStack();

        createGroups();

        groupData.finalize();
        return groupData;


    }

    public void copyData(int[] wr, Area a)
    {
            //copies info that is within "dendrite area" from raster to data
            //so we don't have to work with a huge file everything in data is for a puncta
    		//can also use for cell body stuff
    	pixelNum = 0;
        for(int k = 0; k < data.length; k++)
        {
            for(int j = 0; j < data[0].length; j++)
            {
                if(a.contains((double)(x+k),(double)(y+j)))
                  {
                      data[k][j] = (wr[((y + j)*imageWidth) +(x + k)]&bitOP)>>bitShift;
            		  pixelNum++;
                  }
            }
        }
    }


    public void sortData()
     {
         
         sortedData = new groupLeader[256];
         //this makes a linked list of xy coor sorted by intensity values
         for(int k = 0; k < data.length; k++)
         {

             for(int j = 0; j < data[0].length; j++)
             {
                     if(sortedData[data[k][j]] == null)
                         sortedData[data[k][j]] = new groupLeader(new PixelBinSortNode(xyList[k],xyList[j]),0);
                     else
                     sortedData[data[k][j]].Add(new PixelBinSortNode(xyList[k],xyList[j]),0);
             }

             }
         }




      public void groupPuncta()
      {
          /*function that organizes pixels into a group
          needs a int array same size of data to store group info
          takes the data, sorts it by value and then checks each coor from the highest value
          to the thresholded (or 0 setting) and determines if it is in a group or not.
          when next to a group or associate group it joins the group, if not it makes an associate group
          end result is a data structure that contains the puncta sorted into groups
          for now the data structure will be a int[][] with group numbers for each pixel.
          */

         sortData(); //seems fast

        
         groupLeader tmp;
         PixelBinSortNode sortNode;


          for(currentTh = startTh; currentTh >= threshold; currentTh--)
          {


              tmp = sortedData[currentTh];             

              while(tmp != null && tmp.getCount() > 0)
            {
                sortNode = tmp.First;
                tmp.Remove(sortNode,0);
                groupPixel3(sortNode);
            }

            sortedData[currentTh] = null;
            //System.gc();

          }
          sortedData = null;
          //System.gc();

      }

      public void checkOne(int n)
      {
          if( n == 0)//0 == no group -1 == associate member 1 == group member
              return;

          pixelCounter++;

          if(type == -1)
          {
              if( n < 0)
                  type = 0;
              if(n > 0)
                  type = 1;
          }
          else
          {
              if (type == 0)
              {
                  if (n > 1)
                      type = 2;
              }
              else
              {
                  if (type == 1)
                  {
                      if (n < 0)
                          type = 2;
                  }
              }
          }

          for(int k = 0; k < 4; k++)
          {
              if(one[k] == 0)
                  {
                      one[k] = n;
                      break;
                  }
              else if(one[k] == n)
              {
                  checkTwo(n);
                  break;
              }
          }
      }

      public void checkTwo(int n)
      {
          for(int k = 0; k < 2; k++)
          {
              if(two[k] == 0)
              {
                  two[k] = n;
                  break;
              }
              else if(two[k] == n)
              {
                  checkThree(n);
                  break;
              }
          }
      }

      public void checkThree(int n)
      {
          if(three == 0)
              three = n;
          else if(three == n)
              checkFour(n);
      }

      public void checkFour(int n)
      {
          if(four == 0)
              four = n;
      }

      public void groupPixel3(PixelBinSortNode p)
      {
          one[0] = 0;
          one[1] = 0;
          one[2] = 0;
          one[3] = 0;

          two[0] = 0;
          two[1] = 0;

          three = 0;

          four = 0;

          type = -1; // 0ass 1group 2 both
          pixelCounter = 0;

          if (p.getY() > 0) {
              checkOne(group[p.getX()][p.getY() - 1]);
          }
          if (p.getX() > 0) {
              checkOne(group[p.getX() - 1][p.getY()]);
          }
          if (p.getX() < group.length - 1) {
              checkOne(group[p.getX() + 1][p.getY()]);
          }
          if (p.getY() < group[0].length - 1) {
              checkOne(group[p.getX()][p.getY() + 1]);
          }

          if(four != 0)
          {
              joinFour(p);
              return;
          }

          if (three != 0) {
              joinThree(p);
              return;
              }

          if(two[0] != 0)
          {
              joinTwo(p);
              return;
          }

          if(one[0] != 0)
          {
              joinOne(p);
              return;
          }

          makeAssociate(p);
      }


      public void joinFour(PixelBinSortNode p)
      {
          if(four >0)
              group(p,four);
          else
              associate(p,four);

      }

      public void joinThree(PixelBinSortNode p)
      {
          if(three > 0)
          {
              group(p,three);
          }
          else
          {
              if(type == 2)
                  associate(p,three);
              else
              {
                  if(type == 0)
                  {
                      mergeandJoinOnes(p);
                  }
              }
          }

      }

      public void joinTwo(PixelBinSortNode p)
      {
          if(type == 0)
              mergeandJoinOnes(p);
          else
          {
              if(type == 1)
              {
                  joinLargestTwo(p);
              }
              else
              {
                  if(type == 2)
                  {
                      if(two[1] == 0)
                      {
                          if(two[0] > 0)
                              group(p,two[0]);
                          else
                              associate(p,two[0]);
                      }
                      else
                      {
                          if(two[0] < 0)
                              associate(p,two[0]);
                          else
                              associate(p,two[1]);
                      }
                  }
              }
          }

      }

      public void joinOne(PixelBinSortNode p)
      {
          if(type != 1)
          {
              mergeandJoinOnes(p);
          }
          else
          {
              joinLargestOne(p);
          }

      }

      public void mergeandJoinOnes(PixelBinSortNode p)
      {
          //for associate groups only
          int g1 = 1;
          for(int k = 0; k < 4; k++)
          {
              if(one[k] == 0)
                  break;

              if(g1 > 0)
                  g1 = one[k];
              else
              {
                  if(g1 != one[k] && one[k] < 0)
                      group = assoData.merge(-1*g1,-1*one[k],group);
              }

          }
          if(g1 >= 0)
              System.out.println("error in mergejoin");
          else
              associate(p,g1);

      }

      public void joinLargestTwo(PixelBinSortNode p)
      {
          //only for groups will join the largest
          int g1 = two[0];
          if(two[1] != 0 && groupData.vector[g1].counter < groupData.vector[two[1]].counter)
          {
              g1 = two[1];
          }
          group(p,g1);
      }

      public void joinLargestOne(PixelBinSortNode p)
      {
          int g1 = one[0];
          for(int k = 1; k < 4; k++)
          {
              if(one[k] == 0)
                  break;

              if (groupData.vector[g1].counter < groupData.vector[one[k]].
                  counter) {
                  g1 = one[k];
              }
          }
           group(p,g1);


      }


      public void associate(PixelBinSortNode p,int groupNum)
      {
          
          //sets p's xy in group to the proper associate group number
          //then checks to see if that group is large enough for a true group
          //if(groupNum > 0)
          

          group[p.getX()][p.getY()] = groupNum;
          assoData.Add(p,-1*groupNum,data[p.getX()][p.getY()]);//need to give +
          if(currentTh != 255)
          checkAssociate(groupNum);
      }

      public void group(PixelBinSortNode p,int groupNum)
     {
         //if(groupNum < 0)
             
         group[p.getX()][p.getY()] = groupNum;
         groupData.Add(p,groupNum,data[p.getX()][p.getY()]);
     }

     public void makeAssociate(PixelBinSortNode p)
     {
         int number = assoData.getNewNumber();
         group[p.getX()][p.getY()] = -1*number; //need to make it negative
         //if(number < 0)
         
         assoData.Add(p,number,data[p.getX()][p.getY()]);//needs to be positive DONT CHANGE NOT OVVERIDE
     }

     public void checkAssociate(int num)
     {
         if(num > 0)
             System.out.println("check ass num < 0");
        groupLeader tmp = assoData.checkAssociate(-1*num,assThresh);//needs a positive number
        if(tmp != null)
            {
                int number = groupData.Add(tmp);
                PixelBinSortNode tmp2 = tmp.First;
                while(tmp2 != null)
                {
                    group[tmp2.getX()][tmp2.getY()] = number;
                    //if(number < 0)
                        
                    tmp2 = (PixelBinSortNode)tmp2.next;

                }
            }
     }
     
     public int getIT(int t)
     {
    	 if(t > startTh)
    		 return startTh;
    	 else return t;
     }

     public void cleverSort()
     {
         smartSortBin = new int[256];
         for(int k = 0; k < data.length;k++)//find how many for each bin
         {
             for(int j = 0; j < data[0].length; j++)
             {
                 smartSortBin[getIT(data[k][j])]++;
             }
         }
         smartSort = new int[256][];
         for(int k = 0; k < smartSort.length; k++)//make bins the right size
         {
             smartSort[k] = new int[smartSortBin[k]+1];
         }

         for(int k = 0; k < data.length;k++)
         {
             for(int j = 0; j < data[0].length;j++)
             {//0x3FF= 10 bits, 0x7FF = 11 bits x&y can be 11 bits,
                 smartSort[getIT(data[k][j])][smartSortBin[getIT(data[k][j])]] = ((k<<10)&(0x3FF<<10))+(j&0x3FF);//x&y = 10bits each, 8 bits for threshold value 28 bits needed
                 smartSortBin[getIT(data[k][j])]--;
             }
         }
     }

     public int getX(int info)
     {//gets xcoor from int
         return info>>10;
     }

     public int getY(int info)
    {//get ycoor from int
        return info&0x3FF;
    }


     public void createGroups()
     {
        cleverSort();
        int xyInfo[];
        int x;
        int y;
        for(int k = 255; k >= threshold; k--)
        {
            xyInfo = smartSort[k];

            for (int j = 0; j < xyInfo.length; j++) {
            x = getX(xyInfo[j]);
            y = getY(xyInfo[j]);
                if (isGroup(x,y)) {

                } else if (joinGroup(x,y)) {

                } /*else if (makeGroup(x,y,k)) {

                }*/ else {
                    addtorecheckstack(x,y);
                }
            }
            recheckStack(k);
        }

        makeGroupFromList();
     }

     public boolean isGroup(int x,int y)
     {
         return group[x][y] != 0;
     }

     public boolean joinGroup(int x,int y)
     {
         if(checkUp(y))
         {
             if(group[x][y-1] != 0)
                 {
                     group[x][y] = group[x][y - 1];
                     return true;
                 }
         }
         if(checkLeft(x))
         {
             if(group[x-1][y] != 0)
             {
                 group[x][y] = group[x-1][y];
                 return true;
             }
         }
         if(checkRight(x))
         {
             if(group[x + 1][y] != 0)
             {
                 group[x][y] = group[x + 1][y];
                 return true;
             }
         }
         if(checkDn(y))
         {
             if(group[x][y+1] != 0)
             {
                 group[x][y] = group[x][y+1];
                 return true;
             }
         }

         return false;
     }

     public boolean makeGroup(int x, int y, int currentTh)
     {
         int pixcounter =0;
         boolean[] pixList = new boolean[8];
         pixList[0] = false;
         pixList[1] = false;
         pixList[2] = false;
         pixList[3] = false;
         pixList[4] = false;
         pixList[5] = false;
         pixList[6] = false;
         pixList[7] = false;
         //210
         //3X7
         //456

         if(checkUp(y))
         {
             if(dataAboveorEqTh(x,y-1,currentTh))
                 {
                     pixcounter++;
                     pixList[1] = true;
                     if(checkRight(x))
                     {
                         if(dataAboveorEqTh(x+1,y-1,currentTh))
                         {
                             pixcounter++;
                             pixList[0] = true;
                         }
                     }
                     if(checkLeft(x))
                     {
                         if(dataAboveorEqTh(x-1,y-1,currentTh))
                         {
                             pixcounter++;
                             pixList[2] = true;
                         }
                     }
                 }
         }
         if(checkLeft(x))
         {
             if(dataAboveorEqTh(x-1,y,currentTh))
                     {
                         pixcounter++;
                         pixList[3] = true;
                         if(pixList[2] == false && checkUp(y))
                         {
                             if(dataAboveorEqTh(x-1,y-1,currentTh))
                             {
                                 pixcounter++;
                                 pixList[2] = true;
                             }
                         }
                         if(checkDn(y))
                         {
                             if(dataAboveorEqTh(x-1,y+1,currentTh))
                             {
                                 pixcounter++;
                                 pixList[4] = true;
                             }
                         }
                     }
         }

         if(checkRight(x))
         {
             {
                 if (dataAboveorEqTh(x + 1, y, currentTh)) {
                     pixcounter++;
                     pixList[7] = true;
                     if (pixList[0] == false && checkUp(y)) {
                         if (dataAboveorEqTh(x + 1, y - 1, currentTh)) {
                             pixcounter++;
                             pixList[0] = true;
                         }
                     }
                     if (checkDn(y)) {
                         if (dataAboveorEqTh(x + 1, y + 1, currentTh)) {
                             pixcounter++;
                             pixList[6] = true;
                         }
                     }
                 }
             }
         }
         if(checkDn(y))
         {
             if(dataAboveorEqTh(x,y+1,currentTh))
                  {
                      pixcounter++;
                      pixList[5] = true;
                      if(checkRight(x))
                      {
                          if(pixList[6] == false && dataAboveorEqTh(x+1,y+1,currentTh))
                          {
                              pixcounter++;
                              pixList[6] = true;
                          }
                      }
                      if(checkLeft(x))
                      {
                          if(pixList[4] == false && dataAboveorEqTh(x-1,y+1,currentTh))
                          {
                              pixcounter++;
                              pixList[4] = true;
                          }
                      }
                  }
          }
         if(pixcounter < 3)
         return false;

         groupCounter++;
         group[x][y] = groupCounter;
         if(pixList[0])
             {
                 group[x + 1][y - 1] = groupCounter;
                 groupStack.add(x+1,y-1);
             }
         if(pixList[1]){
             group[x][y - 1] = groupCounter;
             groupStack.add(x,y-1);
         }
         if(pixList[2]){
             group[x - 1][y - 1] = groupCounter;
             groupStack.add(x-1,y-1);
         }
         if(pixList[3]){
             group[x - 1][y] = groupCounter;
             groupStack.add(x-1,y);
         }
         if(pixList[4]){
             group[x - 1][y + 1] = groupCounter;
             groupStack.add(x-1,y+1);
         }
         if(pixList[5]){
             group[x][y + 1] = groupCounter;
             groupStack.add(x,y+1);
         }
         if(pixList[6]){
             group[x + 1][y + 1] = groupCounter;
             groupStack.add(x+1,y+1);
         }
         if(pixList[7]){
             group[x + 1][y] = groupCounter;
             groupStack.add(x+1,y);
         }
         expandGroup(group[x][y],currentTh);
         return true;
     }

     public void expandGroup(int groupNum, int currentTh)
     {

         int[] xy;
         int x;
         int y;
         while(groupStack.isEmpty() == false)
         {
             xy = groupStack.pop();
             x = xy[0];
             y = xy[1];
             


            if(checkUp(y) && group[x][y - 1] == 0 && dataAboveorEqTh(x,y - 1,currentTh))
                 {
                     group[x][y-1] = groupNum;
                     groupStack.add(x, y - 1);
                 }


            if(checkLeft(x) && group[x-1][y] == 0 && dataAboveorEqTh(x-1,y,currentTh))
                 {
                     group[x-1][y] = groupNum;
                     groupStack.add(x-1, y);
                 }
            if(checkRight(x) && group[x+1][y] == 0 && dataAboveorEqTh(x+1,y,currentTh))
                 {
                     group[x+1][y] = groupNum;
                     groupStack.add(x+1, y);
                 }

            if(checkDn(y) && group[x][y+1] == 0 && dataAboveorEqTh(x,y+1,currentTh))
               {
                   group[x][y+1] = groupNum;
                   groupStack.add(x, y + 1);
               }


         }
     }

     public void addtorecheckstack(int x, int y)
     {
         recheck.add(x,y);
     }

     public void recheckStack(int currentTh)
     {
         /*int x; int y;

         for(int k = 0; k < recheck.getLength(); k++)
         {
             x = getX(recheck.list[k]);
             y = getY(recheck.list[k]);
             if(isGroup(x,y))
             {
                 recheck.removeElementAt(k);
                 k--;
             }
             else if(joinGroup(x,y))
             {
                 recheck.removeElementAt(k);
                 k--;
             }
             else if(makeGroup(x,y,currentTh))
             {
                 recheck.removeElementAt(k);
                 k--;
             }
         }*/
    	 
    	 int x; int y;
    	 joinedGroup = true;
    	 boolean madeGroup = true;
    	 
    	 while(madeGroup)
    	 {
    		 madeGroup = false;
    		 while(joinedGroup)
    		 {
    			 joinedGroup = false;
    			 for(int k = 0; k < recheck.getLength(); k++)
    	         {
    	             x = getX(recheck.list[k]);
    	             y = getY(recheck.list[k]);
    	             if(isGroup(x,y))
    	             {
    	                 recheck.removeElementAt(k);
    	                 k--;
    	             }
    	             else if(joinGroup(x,y))
    	             {
    	                 recheck.removeElementAt(k);
    	                 joinedGroup = true;
    	                 k--;
    	             }
    	         }    	             
    		 }
    		 
    		 for(int k = 0; k < recheck.getLength(); k++)
	         {
	             x = getX(recheck.list[k]);
	             y = getY(recheck.list[k]);
	             if(isGroup(x,y))
	             {
	                 recheck.removeElementAt(k);
	                 k--;
	             }
	             else if(makeGroup(x,y,currentTh))
	             {
	                 recheck.removeElementAt(k);
	                 k--;
	                 madeGroup = true;
	             }
	         }
    	 }
    	 
     }

     public boolean dataAboveorEqTh(int x, int y, int th)
     {
         return data[x][y] >= th;
     }
     public boolean checkUp(int y)
     {
         return y > 0;
     }

     public boolean checkDn(int y)
     {
         return y < data[0].length - 1;
     }

     public boolean checkLeft(int x)
     {
         return x > 0;
     }

     public boolean checkRight(int x)
     {
         return x < data.length - 1;
     }

     public void makeGroupFromList()
     {
         groupLeader[] punctaGroups = new groupLeader[groupCounter + 1];
         for(int k = 0; k < group.length; k++)
         {
             for(int j = 0; j < group[0].length; j++)
             {
                 if(group[k][j] > 0)
                 {
                     if(punctaGroups[group[k][j]] == null)
                         punctaGroups[group[k][j]] = new groupLeader(new PixelBinSortNode(xyList[k],xyList[j]),data[k][j]);
                     else
                     punctaGroups[group[k][j]].Add(new PixelBinSortNode(xyList[k],xyList[j]),data[k][j]);
                 }
             }
         }

         for(int k = 0; k < punctaGroups.length; k++)
         {
             groupData.Add(punctaGroups[k]);
         }
     }



}
