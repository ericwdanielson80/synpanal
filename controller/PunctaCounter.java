package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.awt.image.WritableRaster;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Stack;
/**
 * Core image-analysis engine for the neuron_analyzer application that scans
 * raster pixel data to detect, group, and count puncta within a supplied
 * dendrite or cell-body region. The class extracts one color channel (R, G,
 * or B) from a packed RGB raster into a local two-dimensional intensity
 * array bounded by a Rectangle and clipped by an Area, applies a per-color
 * intensity threshold, and then groups suprathreshold pixels into connected
 * components using either a sorted walk (groupPuncta/sortData path) or a
 * descending-threshold flood-fill (createGroups/makeGroup/expandGroup path).
 * During scanning it accumulates integrated intensities for each candidate
 * group, promotes associate groups to true groups once they exceed a pixel
 * threshold, and also provides a simple variant that sums the intensity of a
 * cell body. Results are accumulated into a groupVector of Puncta and are
 * consumed by TiffPanel and Dendrite. A copy constructor is provided so
 * another counter can reuse the same image dimensions and xyList cache.
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
    public Integer[] xyList;

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
    
    /**
     * Constructs a fresh PunctaCounter sized to match the full source image.
     * The constructor stores the image width and height so later scans can
     * unpack raster indices, and pre-builds xyList, an array of boxed
     * Integer objects covering the index range [0, imageWidth), which is
     * reused when creating PixelBinSortNode coordinates so that the same
     * Integer instances can be shared across many nodes. The parameter
     * imagewidth is the full source image width and imageheight is the full
     * source image height.
     */
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
    
    /**
     * Copy constructor that builds a new PunctaCounter sharing the source
     * counter's image dimensions and cached xyList index array. The
     * parameter p is the counter to copy; imageWidth, imageHeight, and the
     * xyList reference are taken directly from p so callers can create a
     * second working counter without rebuilding the index cache.
     */
    public PunctaCounter(PunctaCounter p)
    {
    	imageWidth = p.imageWidth;
    	imageHeight = p.imageHeight;
    	xyList = p.xyList;
    }
    
    /**
     * Computes the total integrated intensity of a cell body for one color
     * channel over a supplied area. The method sets up the bounding width
     * and height from the rectangle r, stores the threshold th, selects the
     * bitmask and bitshift for the requested color channel (rgb == 0 for
     * red, 1 for green, 2 for blue), allocates data sized to the rectangle,
     * and calls copyData to unpack only the pixels inside the Area a from
     * the packed raster wr. It then walks every cell of data and sums the
     * value of each pixel whose intensity is at or above threshold into the
     * local CellIntensity accumulator. The parameter wr is the packed RGB
     * raster (row-major, length imageWidth*imageHeight); imageHeight is the
     * image height (unused by the body, kept for interface parity); r gives
     * the bounding box of the cell body in image coordinates; rgb selects
     * the color channel; a is the cell-body Area used to mask which pixels
     * contribute; th is the intensity threshold. The returned int[] holds
     * the summed cell intensity in slot 0 and the pixel count (pixelNum,
     * set by copyData) in slot 1.
     */
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
    
    /**
     * Main entry point that detects and groups puncta inside a dendrite
     * region for one color channel. The method first sets the working
     * rectangle size, stores the lower threshold th and upper/start
     * threshold stth, creates fresh groupData and assoData containers, and
     * picks the appropriate bitmask/bitshift for the requested rgb channel
     * so copyData can unpack a single-channel intensity map. After copying
     * the raster pixels bounded by r and clipped to the Area a, it sweeps
     * the intensity array once to accumulate totalI, the sum of intensities
     * at or above threshold, and writes totalI and pixelNum into slots 0
     * and 1 of totalIntensity. It then allocates the group label array,
     * invokes cleverSort to place each pixel in an intensity bin, resets
     * groupCounter and the groupStack and recheck stacks, and calls
     * createGroups to run the descending-threshold grouping algorithm. A
     * final finalize on groupData trims/consolidates the accumulated
     * puncta. The parameter wr is the packed RGB raster; imageheight is
     * currently unused; r is the dendrite bounding rectangle; rgb selects
     * the color channel; a is the dendrite Area used as a pixel mask; th
     * is the detection threshold; stth is the starting (upper) threshold
     * used by the descending sweep; totalIntensity is an out-parameter
     * that receives the summed intensity and pixel count. The local
     * variable totalI accumulates the raw intensity sum, while pixelNum is
     * populated by copyData to report how many masked pixels were actually
     * read. The method returns the populated groupVector of puncta groups.
     */
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

    /**
     * Unpacks the selected color channel from the packed RGB raster into
     * the local data array, restricting extraction to pixels that fall
     * inside the supplied Area. The method iterates the data array in x/y
     * order; for each position (k, j) it computes the image-space
     * coordinate (x+k, y+j) and, if that coordinate is contained by Area
     * a, reads the corresponding int from wr at index
     * ((y+j)*imageWidth)+(x+k), applies the pre-selected bitOP mask and
     * bitShift to extract the channel byte, stores it in data[k][j], and
     * increments pixelNum. This keeps the per-scan working array small and
     * scoped to the dendrite or cell-body region. The parameter wr is the
     * packed RGB raster and a is the mask Area; the class-level x, y,
     * width, height, bitOP, bitShift, and imageWidth fields are read, and
     * data and pixelNum are written.
     */
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


    /**
     * Builds a 256-entry array of groupLeader linked lists keyed by pixel
     * intensity so the sorted-walk grouping routine can visit pixels from
     * bright to dim. The method allocates sortedData with one slot per
     * possible 8-bit intensity value, then loops over every pixel in the
     * data array and appends a PixelBinSortNode carrying that pixel's x
     * and y boxed coordinates (sourced from the xyList cache) to the
     * linked list at sortedData[data[k][j]]; if that slot is still null it
     * is initialized with a new groupLeader whose first node is the pixel.
     * The resulting data structure is effectively a bucketized list sorted
     * by intensity.
     */
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




      /**
       * Sorted-walk grouping algorithm that organizes pixels into puncta
       * groups by visiting them in descending intensity order. It first
       * calls sortData to fill sortedData, then walks currentTh from
       * startTh down to threshold; for each threshold level it pulls the
       * first PixelBinSortNode (sortNode) off the linked list stored in
       * the groupLeader tmp at sortedData[currentTh], removes it, and
       * passes it to groupPixel3, which inspects the four-connected
       * neighbors to decide whether the pixel joins an existing group,
       * becomes an associate group member, or starts a new associate
       * group. Once a level is fully processed the slot in sortedData is
       * nulled so it can be garbage-collected, and at the end the entire
       * sortedData array reference is cleared. The local groupLeader tmp
       * holds the current threshold bucket and sortNode is the pixel
       * being classified on each iteration. This method is an alternative
       * to createGroups and is not called by the default countPuncta path.
       */
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

      /**
       * Records a neighbor's group label n during the groupPixel3 scan of
       * a candidate pixel's four-connected neighborhood. A label of zero
       * means "no group" and is ignored; negative values denote associate
       * group membership, positive values denote true group membership.
       * The method increments pixelCounter to track how many non-zero
       * neighbors were seen, updates the type state machine (starting at
       * -1 "unset", moving to 0 for pure associates, 1 for pure groups,
       * or 2 for mixed), and then stores the label into the first free
       * slot of the one[] array; if the label already appears in one[]
       * the method escalates to checkTwo so the algorithm can track how
       * many times the same label appears among the neighbors. The
       * parameter n is the neighbor pixel's group label.
       */
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

      /**
       * Second-tier neighbor-count bookkeeping used when a label n has
       * already been seen once in one[]. It records n in the first free
       * slot of the two[] array (which can hold up to two distinct
       * labels) and, if the same label appears again, escalates to
       * checkThree. The parameter n is the repeated neighbor group
       * label.
       */
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

      /**
       * Third-tier counter that records a label seen three times among
       * the four-connected neighbors of the candidate pixel. If three is
       * still zero it stores n there, otherwise if n matches the
       * existing three value it escalates to checkFour. The parameter n
       * is the thrice-seen neighbor group label.
       */
      public void checkThree(int n)
      {
          if(three == 0)
              three = n;
          else if(three == n)
              checkFour(n);
      }

      /**
       * Fourth-tier counter that stores a label seen four times (the
       * maximum possible for a four-connected neighborhood). If four is
       * zero it is set to n. The parameter n is the four-times-seen
       * neighbor group label.
       */
      public void checkFour(int n)
      {
          if(four == 0)
              four = n;
      }

      /**
       * Classifies a single pixel p drawn from the sorted intensity list
       * by examining the group labels of its four-connected neighbors
       * (up, left, right, down) in the group array. The method clears
       * the tally arrays one[] and two[], the scalar three and four
       * counters, the type state (initialized to -1), and pixelCounter,
       * then calls checkOne for each in-bounds neighbor which may
       * cascade into checkTwo/checkThree/checkFour so the algorithm
       * knows whether a single label dominates the neighborhood. After
       * collecting the tallies, it dispatches in priority order: if one
       * label appears four times it calls joinFour; else three times
       * joinThree; else two times joinTwo; else once joinOne; otherwise
       * the pixel has no grouped neighbors and is seeded as a new
       * associate via makeAssociate. The parameter p is the
       * PixelBinSortNode whose coordinates (p.getX(), p.getY()) are
       * being classified.
       */
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


      /**
       * Handles the case where all four neighbors share a single group
       * label (stored in the four field). If that shared label is
       * positive the pixel is added to the true group via group();
       * otherwise (negative label) it is added to that associate group
       * via associate(). The parameter p is the pixel being placed.
       */
      public void joinFour(PixelBinSortNode p)
      {
          if(four >0)
              group(p,four);
          else
              associate(p,four);

      }

      /**
       * Handles the case where three of the four neighbors share a
       * label (stored in three). If that label is positive the pixel
       * joins that true group. If the label is negative and type is 2
       * (mixed associates and groups seen) the pixel is placed into the
       * associate group; if type is 0 (only associates seen) it calls
       * mergeandJoinOnes to combine the multiple associate labels that
       * touch this pixel. The parameter p is the pixel being placed.
       */
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

      /**
       * Handles the case where exactly two distinct labels appear among
       * the neighbors (captured in the two[] array). If type is 0 (all
       * associates) the associate components are merged via
       * mergeandJoinOnes; if type is 1 (all true groups) the pixel is
       * added to whichever of the two groups already has more pixels
       * via joinLargestTwo; if type is 2 (mixed), the pixel is placed
       * into whichever component is preferred by the sign-based
       * disambiguation in the if/else ladder (favoring the positive
       * true-group label when available, otherwise the associate
       * label). The parameter p is the pixel being placed.
       */
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

      /**
       * Handles the case where each non-zero neighbor label appears
       * only once. If any associates are involved (type != 1) it merges
       * those associate components together via mergeandJoinOnes; if
       * only true groups appear (type == 1) it picks the largest of the
       * neighboring groups via joinLargestOne. The parameter p is the
       * pixel being placed.
       */
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

      /**
       * Merges all the distinct associate group labels currently listed
       * in one[] into a single associate group and places pixel p into
       * that merged group. The local variable g1 tracks the surviving
       * (negative) associate label; it is seeded with the first
       * non-zero entry of one[] and then, for each additional distinct
       * negative label found, the assoData.merge call folds the other
       * associate group into g1 by rewriting the group label array.
       * After the loop, if g1 is not negative that indicates an
       * unexpected condition and an error message is printed; otherwise
       * the pixel is attached to the merged associate via associate().
       * The parameter p is the pixel being placed.
       */
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

      /**
       * Adds pixel p to the larger of the two true-group labels held in
       * the two[] array. The local variable g1 is initialized to
       * two[0]; if two[1] is non-zero and its groupData entry has a
       * larger counter (pixel count) than g1's, g1 is switched to
       * two[1]. The pixel is then placed into g1 via group(). The
       * parameter p is the pixel being placed.
       */
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

      /**
       * Adds pixel p to the largest of the true-group labels currently
       * listed in the one[] array. The local variable g1 is initialized
       * to one[0], then the loop scans one[1..3] and switches g1 to any
       * entry whose groupData counter (pixel count) is larger than the
       * current candidate's. The pixel is then placed into g1 via
       * group(). The parameter p is the pixel being placed.
       */
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


      /**
       * Attaches pixel p to an associate group identified by groupNum
       * (expected to be negative, because associate labels are stored
       * as negative numbers in the group array). The method writes
       * groupNum into group[p.getX()][p.getY()], appends the pixel to
       * assoData with the positive form of the label and its intensity
       * data[p.getX()][p.getY()], and then (as long as currentTh is not
       * 255) calls checkAssociate to see whether the associate group
       * has grown large enough to be promoted to a true group. The
       * parameter p is the pixel being placed and groupNum is the
       * associate group label (negative).
       */
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

      /**
       * Attaches pixel p to a true (positive-labeled) puncta group
       * identified by groupNum. It writes groupNum into
       * group[p.getX()][p.getY()] and appends the pixel (along with its
       * intensity data[p.getX()][p.getY()]) to groupData so that the
       * group's running pixel count and integrated intensity stay up to
       * date. The parameter p is the pixel being placed and groupNum is
       * the target group label.
       */
      public void group(PixelBinSortNode p,int groupNum)
     {
         //if(groupNum < 0)
             
         group[p.getX()][p.getY()] = groupNum;
         groupData.Add(p,groupNum,data[p.getX()][p.getY()]);
     }

     /**
      * Seeds a brand-new associate group around pixel p when none of
      * its neighbors are yet part of any group. The local variable
      * number is obtained from assoData.getNewNumber() as a fresh
      * positive associate index; the group array at p's coordinates is
      * set to its negation (associate labels are stored negative), and
      * the pixel is appended to assoData under the positive number
      * along with its intensity data[p.getX()][p.getY()]. The parameter
      * p is the pixel being seeded.
      */
     public void makeAssociate(PixelBinSortNode p)
     {
         int number = assoData.getNewNumber();
         group[p.getX()][p.getY()] = -1*number; //need to make it negative
         //if(number < 0)
         
         assoData.Add(p,number,data[p.getX()][p.getY()]);//needs to be positive DONT CHANGE NOT OVVERIDE
     }

     /**
      * Checks whether an associate group has grown large enough to be
      * promoted into a true puncta group. The method first guards
      * against being called with a positive num (an error condition
      * that prints a message), then asks assoData.checkAssociate to
      * return the associate's groupLeader if it now has at least
      * assThresh pixels; the negation of num is passed because assoData
      * stores associate numbers as positives. If a groupLeader tmp is
      * returned, groupData.Add converts it into a new true group and
      * returns its new label; the method then walks the linked list
      * starting at tmp.First, rewriting the group array entry for every
      * pixel (tmp2) belonging to the old associate so it carries the
      * new true-group label. The parameter num is the (negative)
      * associate label being checked.
      */
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
     
     /**
      * Clamps a per-pixel intensity value t to the starting threshold
      * startTh so cleverSort can place all pixels whose intensity is
      * above startTh into the same top bin. Returns startTh if t is
      * larger, otherwise returns t unchanged.
      */
     public int getIT(int t)
     {
    	 if(t > startTh)
    		 return startTh;
    	 else return t;
     }

     /**
      * Builds a compact, bin-sorted index of every pixel in the data
      * array for later descending-threshold scans. The method performs
      * two passes: in the first it counts, for each 0..255 intensity
      * value (clamped by getIT), how many pixels of that intensity
      * exist, storing the tallies in smartSortBin. It then allocates
      * smartSort, a 256-row jagged array whose k-th row is sized to
      * hold all pixels at intensity k (plus one slack slot). In the
      * second pass it revisits every pixel and packs its (x, y) into a
      * single 20-bit int with x in bits 10..19 and y in bits 0..9
      * (values are limited to 10 bits each, so up to 1024 pixels per
      * axis), placing the packed value at the tail of its intensity
      * bin and decrementing smartSortBin for that bin so the next
      * pixel lands in the preceding slot. The outcome is a structure
      * from which createGroups can pop pixels efficiently in
      * descending-intensity order.
      */
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

     /**
      * Extracts the packed x coordinate (stored in the upper bits) from
      * an int produced by cleverSort. Returns info shifted right by 10
      * bits.
      */
     public int getX(int info)
     {//gets xcoor from int
         return info>>10;
     }

     /**
      * Extracts the packed y coordinate (stored in the lower 10 bits)
      * from an int produced by cleverSort. Returns info masked with
      * 0x3FF.
      */
     public int getY(int info)
    {//get ycoor from int
        return info&0x3FF;
    }


     /**
      * Performs the descending-threshold flood-fill puncta grouping
      * pass used by countPuncta. It first calls cleverSort to build
      * the bin-sorted pixel index, then for each threshold level k
      * from 255 down to the configured threshold it walks the packed
      * (x, y) entries in smartSort[k]. For each pixel the method
      * unpacks x and y via getX/getY and tries, in order: isGroup (the
      * pixel already belongs to a group, skip), joinGroup (an adjacent
      * pixel carries a group label, inherit it), or, if neither
      * applies, addtorecheckstack which defers the pixel to be
      * reconsidered once the neighborhood has been filled in. After
      * each threshold level is processed, recheckStack is called to
      * revisit deferred pixels that may now have neighbors. Finally,
      * makeGroupFromList consolidates the group labels in the group
      * array into the groupData vector.
      */
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

     /**
      * Returns true if the pixel at (x, y) already carries a non-zero
      * group label, i.e. it has already been placed.
      */
     public boolean isGroup(int x,int y)
     {
         return group[x][y] != 0;
     }

     /**
      * Attempts to adopt a neighbor's group label for the pixel at
      * (x, y) by checking, in order, the pixel directly above, to the
      * left, to the right, and below. For each direction it uses
      * checkUp/checkLeft/checkRight/checkDn to guard against bounds
      * violations and, if the neighbor carries a non-zero group label,
      * copies that label into group[x][y] and returns true immediately.
      * If no neighbor has a group label the method returns false. The
      * parameters x and y are the coordinates of the pixel being
      * tested.
      */
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

     /**
      * Attempts to start a new puncta group centered on the pixel at
      * (x, y), requiring at least three suprathreshold pixels in its
      * 8-connected neighborhood. The pixList boolean array indexes the
      * eight surrounding positions in this diagram (top row: 2-1-0,
      * middle row: 3-X-7, bottom row: 4-5-6), with the local
      * pixcounter tallying how many of those positions hold pixels at
      * or above currentTh (using dataAboveorEqTh and the
      * checkUp/Left/Right/Dn bounds guards). Each cardinal arm (up,
      * left, right, down) also sweeps its two diagonal neighbors so
      * each diagonal is only counted once. If pixcounter is less than
      * three the method returns false without mutating state. Otherwise
      * groupCounter is incremented to create a fresh label, that label
      * is written into group[x][y], each flagged neighbor receives the
      * same label and is pushed onto groupStack, and expandGroup is
      * called to flood-fill the rest of the group at currentTh. The
      * parameters x and y are the pixel coordinates and currentTh is
      * the current threshold level being processed.
      */
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

     /**
      * Flood-fills a puncta group identified by groupNum, starting
      * from whatever seed pixels are already sitting on groupStack,
      * extending the group through all four-connected suprathreshold
      * neighbors at intensity at or above currentTh. The local xy
      * array holds the (x, y) popped from groupStack on each
      * iteration, with x and y locals unpacking it for convenience.
      * For every popped pixel the method checks each of the four
      * cardinal neighbors: if the neighbor is in bounds (checkUp /
      * checkLeft / checkRight / checkDn), has not yet been labeled
      * (group entry is zero), and is at or above currentTh
      * (dataAboveorEqTh), it receives the groupNum label and is
      * pushed onto groupStack so its own neighbors are explored in
      * turn. The loop runs until groupStack is empty. The parameter
      * groupNum is the target group label and currentTh is the
      * threshold below which pixels are not absorbed into the group.
      */
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

     /**
      * Pushes the pixel at (x, y) onto the recheck stack so it can be
      * revisited after the rest of the current threshold level has
      * been processed.
      */
     public void addtorecheckstack(int x, int y)
     {
         recheck.add(x,y);
     }

     /**
      * Drains the recheck stack of pixels that could not be placed on
      * their first visit at the given currentTh, repeatedly trying
      * both joinGroup (attach to an existing neighboring group) and
      * makeGroup (seed a brand-new group that meets the neighborhood
      * density requirement). The implementation uses two nested
      * loops: the outer while on madeGroup keeps iterating as long as
      * at least one new group was created in the previous round; the
      * inner while on joinedGroup keeps iterating as long as at least
      * one pixel successfully joined an existing group. Inside those
      * loops the method walks recheck.list, unpacking each entry via
      * getX/getY into the local x and y, and removes entries as they
      * are either already in a group (isGroup), successfully joined
      * to an existing group (joinGroup, which also sets joinedGroup
      * so another pass is made), or seeded as a new group (makeGroup,
      * which sets madeGroup so another outer pass is made). A large
      * commented-out block at the top of the method preserves the
      * original single-pass implementation. The parameter currentTh
      * is the threshold level being processed.
      */
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

     /**
      * Returns true when the intensity at data[x][y] is greater than
      * or equal to the provided threshold th.
      */
     public boolean dataAboveorEqTh(int x, int y, int th)
     {
         return data[x][y] >= th;
     }
     /**
      * Returns true when y has a valid neighbor above (y greater than
      * zero).
      */
     public boolean checkUp(int y)
     {
         return y > 0;
     }

     /**
      * Returns true when y has a valid neighbor below (y less than
      * data height minus one).
      */
     public boolean checkDn(int y)
     {
         return y < data[0].length - 1;
     }

     /**
      * Returns true when x has a valid neighbor to the left (x
      * greater than zero).
      */
     public boolean checkLeft(int x)
     {
         return x > 0;
     }

     /**
      * Returns true when x has a valid neighbor to the right (x less
      * than data width minus one).
      */
     public boolean checkRight(int x)
     {
         return x < data.length - 1;
     }

     /**
      * Sweeps the final group label array into the groupData vector,
      * producing one groupLeader per distinct positive group label.
      * The method allocates punctaGroups, an array sized
      * groupCounter + 1 so each label index maps directly to a slot.
      * It then walks every pixel in group: for any pixel with a
      * positive label it appends a new PixelBinSortNode carrying the
      * cached xyList-boxed coordinates (xyList[k], xyList[j]) and the
      * pixel's intensity data[k][j] to punctaGroups[group[k][j]],
      * lazily creating a fresh groupLeader on first sight. Finally it
      * adds each groupLeader to groupData so downstream consumers
      * (TiffPanel and Dendrite) see the completed puncta set.
      */
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
