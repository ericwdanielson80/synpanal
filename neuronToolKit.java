package neuron_analyzer;
import java.awt.Polygon;
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

    public neuronToolKit() {
    }

    public void getOFFset(int xMin, int yMin)
    {
        //calculates the offset for x&y values
        xOFFSET = xMin;
        yOFFSET = yMin;

    }

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
