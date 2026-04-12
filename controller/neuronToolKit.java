package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.awt.Polygon;
/**
 * Geometry and pixel-scanning toolkit used throughout the image analysis to
 * trace puncta outlines. Given a sorted list of pixels it builds a boolean
 * grid, walks the grid to generate edge segments (left, right, upper, and
 * lower segments), links those segments into a closed polygon, and
 * translates the resulting polygon back into image coordinates via a
 * remembered (xOFFSET, yOFFSET) offset.
 */
public class neuronToolKit {
   punctaOutlineNode[] nodeList; //for making segments
   int nodeCounter;
   segmentNode[][] segmentLinker; //for linking segments
   nodeLinker[][] segmentLinker2;
   boolean[][] grid;
   int xOFFSET;
   int yOFFSET;
   int[] xPol;
   int[] yPol;

    /** No-argument constructor; fields are initialized lazily by the helper methods. */
    public neuronToolKit() {
    }

    /** Stores the minimum x/y of the puncta bounding box so later per-pixel work can be done in grid-local coordinates by subtracting these offsets. */
    public void getOFFset(int xMin, int yMin)
    {
        //calculates the offset for x&y values
        xOFFSET = xMin;
        yOFFSET = yMin;

    }

    /**
     * Allocates a boolean grid sized to the puncta bounding box and walks
     * the linked list beginning at first, marking each pixel's position
     * true. The locals xMin/yMin/xMax/yMax define the rectangle, and tmp
     * steps through the PixelBinSortNode list node by node.
     */
    public void makeGrid(int xMin, int yMin,int xMax,int yMax,PixelBinSortNode first)
    {
        grid = new boolean[xMax-xMin + 1][yMax-yMin + 1];
        PixelBinSortNode tmp = first;
        while(tmp != null)
        {
                //makes a grid of boolean values used to calculate segments
            grid[tmp.getX() - xOFFSET][tmp.getY() - yOFFSET] = true;
            tmp = (PixelBinSortNode)tmp.next;
        }

    }

    /**
     * Scans the boolean grid and produces one edge segment for every
     * pixel-edge that borders the outside of the puncta. The nodeList is
     * sized generously (Area * 3) to hold all generated segments and
     * nodeCounter tracks how many have been written. For each grid cell
     * the four helpers checkL, checkU, checkR, and checkD emit segments
     * on any exposed side.
     */
    public void makeSegments(int Area)
    {
        nodeList = new punctaOutlineNode[Area * 3];
        nodeCounter = 0;

        for(int k = 0; k < grid.length; k++)
        {
            for(int j = 0; j < grid[0].length; j++)
            {
                checkL(k,j);
                checkU(k,j);
                checkR(k,j);
                checkD(k,j);
            }
        }
    }

    /**
     * Builds segmentLinker2, a 2-D array of nodeLinker objects that
     * collects the In and Out endpoints at every shared grid vertex, and
     * then calls linkNodes on each populated entry to stitch neighboring
     * segments together. The outer while loop walks the nodeList, and
     * inside it the In and Out endpoints are added to the corresponding
     * grid positions; a second nested loop then links up each vertex.
     */
    public void linkSegments2()
    {
        segmentLinker2 = new nodeLinker[grid.length + 1][grid[0].length + 1]; //should always be just a bit bigger than the grid
        int k = 0;
        while(k < nodeList.length && nodeList[k] != null)
        {
            //for Ins
            if(segmentLinker2[nodeList[k].getInX()][nodeList[k].getInY()] == null)
            {//if there is nothing there put an In there
                segmentLinker2[nodeList[k].getInX()][nodeList[k].getInY()] = new nodeLinker();
                segmentLinker2[nodeList[k].getInX()][nodeList[k].getInY()].addIn(nodeList[k].In);
            }
            else
            {
             segmentLinker2[nodeList[k].getInX()][nodeList[k].getInY()].addIn(nodeList[k].In);
            }
            //for Outs
            if(segmentLinker2[nodeList[k].getOutX()][nodeList[k].getOutY()] == null)
            {//if there is nothing there put an Out there
                segmentLinker2[nodeList[k].getOutX()][nodeList[k].getOutY()] = new nodeLinker();
                segmentLinker2[nodeList[k].getOutX()][nodeList[k].getOutY()].addOut(nodeList[k].Out);
            }
            else
            {//if there is something there it must be an In bc can only be two and only an in or an out
              segmentLinker2[nodeList[k].getOutX()][nodeList[k].getOutY()].addOut(nodeList[k].Out);
            }


            k++;
        }
        for(int j = 0; j < segmentLinker2.length; j++)
        {
            for(int l = 0; l < segmentLinker2[0].length; l++)
            {
                if(segmentLinker2[j][l] != null)
                    segmentLinker2[j][l].linkNodes();
            }
        }

    }

    /**
     * Alternate linker that stitches segments directly into a doubly
     * linked list (previous/next) at each shared vertex, without the
     * intermediate nodeLinker objects. Kept for older code paths alongside
     * linkSegments2.
     */
    public void linkSegments()
    {
        segmentLinker = new segmentNode[grid.length + 1][grid[0].length + 1]; //should always be just a bit bigger than the grid
        int k = 0;
        while(nodeList[k] != null)
        {
            //for Ins
            if(segmentLinker[nodeList[k].getInX()][nodeList[k].getInY()] == null)
            {//if there is nothing there put an In there
                segmentLinker[nodeList[k].getInX()][nodeList[k].getInY()] = nodeList[k].In;
            }
            else
            {//if there is something there it must be an OUT bc can only be two and only an in or an out


             segmentLinker[nodeList[k].getInX()][nodeList[k].getInY()].next = nodeList[k].In;
             nodeList[k].In.previous = segmentLinker[nodeList[k].getInX()][nodeList[k].getInY()];
            }
            //for Outs
            if(segmentLinker[nodeList[k].getOutX()][nodeList[k].getOutY()] == null)
            {//if there is nothing there put an Out there
                segmentLinker[nodeList[k].getOutX()][nodeList[k].getOutY()] = nodeList[k].Out;
            }
            else
            {//if there is something there it must be an In bc can only be two and only an in or an out
              segmentLinker[nodeList[k].getOutX()][nodeList[k].getOutY()].previous = nodeList[k].Out;
              nodeList[k].Out.next = segmentLinker[nodeList[k].getOutX()][nodeList[k].getOutY()];
            }


            k++;
        }

    }

    /**
     * Emits a leftSegment if the pixel at (x,y) is inside the puncta but
     * its left neighbor is not. The local booleans isupperNeighbor and
     * islowerNeighbor encode whether the neighbors above and below are
     * also inside the puncta, so the segment knows how to join with its
     * siblings.
     */
    public void checkL(int x,int y)
    {
        boolean isupperNeighbor = false;
        boolean islowerNeighbor = false;
        if(grid[x][y])
        if(x == 0 || grid[x - 1][y] == false)
        {
            if(y == 0 || grid[x][y - 1] == false)
                isupperNeighbor = false;
            else
                isupperNeighbor = true;
            if(y == grid[0].length - 1 || grid[x][y + 1] == false)
                islowerNeighbor = false;
            else
                islowerNeighbor = true;

            nodeList[nodeCounter] = new leftSegment(x,y,isupperNeighbor,islowerNeighbor);
            nodeCounter++;
        }

    }

    /** Mirror of checkL that emits a rightSegment when the right neighbor of an interior pixel is outside the puncta. */
    public void checkR(int x,int y)
    {
        boolean isupperNeighbor = false;
        boolean islowerNeighbor = false;

        if(grid[x][y])
        if(x == grid.length - 1 || grid[x + 1][y] == false)
        {
            if(y == 0 || grid[x][y - 1] == false)
               isupperNeighbor = false;
           else
               isupperNeighbor = true;
           if(y == grid[0].length - 1 || grid[x][y + 1] == false)
               islowerNeighbor = false;
           else
               islowerNeighbor = true;


            nodeList[nodeCounter] = new rightSegment(x,y,isupperNeighbor,islowerNeighbor);
            nodeCounter++;
        }

    }

    /** Mirror of checkL for the top edge: emits an upperSegment when the pixel above an interior cell is outside the puncta. */
    public void checkU(int x,int y)
    {
        boolean hasLeft = false;
        boolean hasRight = false;

        if(grid[x][y])
        if(y == 0 || grid[x][y - 1] == false)
        {
            if(x == 0 || grid[x - 1][y] == false)
                hasLeft = false;
            else
                hasLeft = true;
            if(x == grid.length - 1 || grid[x + 1][y] == false)
                hasRight = false;
            else
                hasRight = true;

            nodeList[nodeCounter] = new upperSegment(x,y,hasLeft,hasRight);
            nodeCounter++;
        }

    }

    /** Mirror of checkL for the bottom edge: emits a lowerSegment when the pixel below an interior cell is outside the puncta. */
    public void checkD(int x,int y)
    {
        boolean hasLeft = false;
        boolean hasRight = false;

        if(grid[x][y])
        if(y == grid[0].length - 1 || grid[x][y + 1] == false)
        {
            if(x == 0 || grid[x - 1][y] == false)
                hasLeft = false;
            else
                hasLeft = true;
            if(x == grid.length - 1 || grid[x + 1][y] == false)
                hasRight = false;
            else
                hasRight = true;

            nodeList[nodeCounter] = new lowerSegment(x,y,hasLeft,hasRight);
            nodeCounter++;
        }

    }




    /**
     * Walks the linked segment list beginning at inNode, skipping every
     * other node (the paired Out entries) so only In nodes are visited,
     * and collects the polygon vertex coordinates into xPol/yPol. The
     * local segmentCounter first tallies the perimeter length so the
     * arrays can be sized correctly; tmp then re-traverses the list to
     * fill the coordinate arrays, translating each back into image space
     * via xOFFSET/yOFFSET.
     */
    public void makePolygon(segmentNode inNode)
    {
        //get rid of duplicate entries by linking together all the Ins
        segmentNode tmp = inNode;
        int segmentCounter = 0;
        tmp.next = (segmentNode)tmp.next.next; //connects to next In
        tmp = (segmentNode)tmp.next; //shifts over one
        segmentCounter++;        
        while(tmp != inNode)
        {
            tmp.next = (segmentNode)tmp.next.next;
            tmp = (segmentNode)tmp.next;
            segmentCounter++;
        }
        
        xPol = new int[segmentCounter];
        yPol = new int[segmentCounter];
        int k = 0;
        while(k < segmentCounter)//tmp.next != inNode)
        {
            xPol[k] = xOFFSET + tmp.getPolyX();
            yPol[k] = yOFFSET + tmp.getPolyY();
            k++;
            tmp = (segmentNode)tmp.next;
        }

    }

    /**
     * End-to-end convenience that turns a raw pixel list into a
     * java.awt.Polygon describing the puncta outline. It sets the offset,
     * builds the grid, generates and links segments, and finally traces
     * the polygon. The parameters x1/y1 and x2/y2 are the pixel bounding
     * box, first is the start of the sorted pixel list, and area is the
     * number of pixels (used to size the nodeList).
     */
    public Polygon makePuncta(PixelBinSortNode first, int x1, int x2, int y1, int y2,int area)
    {
        getOFFset(x1,y1);
        makeGrid(x1,y1,x2,y2,first);
        makeSegments(area);
        linkSegments2();
        makePolygon(nodeList[0].In);
        return new Polygon(xPol,yPol,xPol.length);
    }



}
