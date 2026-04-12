package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
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
/**
 * Aggregates a set of connected PixelBinSortNode entries into a single
 * pixel group used as a candidate puncta. It maintains head and tail
 * pointers into the doubly-linked pixel list, a running pixel count and
 * aggregate intensity, a bounding box, and an eventually-generated polygon
 * border. Provides operations to add and remove pixels, convert local
 * coordinates back to full-image coordinates, build the polygon border
 * through a neuronToolKit helper, and finally construct an immutable
 * Puncta from the accumulated data.
 */
public class groupLeader {
//organizes the pixels into a group

public PixelBinSortNode First;
PixelBinSortNode Last;
public int counter;
int intensity;

int x1;
int y1;
int x2;
int y2;

Polygon border;


    /**
     * Creates a new group seeded with a single pixel. The n parameter is
     * the initial PixelBinSortNode, stored as both the First and Last list
     * pointers; the i parameter is that pixel's intensity used to seed the
     * running total. The counter starts at one.
     */
    public groupLeader(PixelBinSortNode n, int i) {
        First = n;
        Last = n;
        counter = 1;
        intensity = i;
    }

    /**
     * Appends a pixel node to the group. The n parameter is the node to
     * attach and i is its intensity contribution. When the counter is zero
     * the group is re-seeded with this node; otherwise the node is linked
     * onto Last via PixelBinSortNode.Add, the counter and intensity are
     * incremented and Last is advanced to the newly inserted node.
     */
    public void Add(PixelBinSortNode n,int i)
    {
        if(counter == 0)
        {
            First = n;
            Last = n;
            counter++;
            return;
        }

        Last.Add(n);
        counter++;
        intensity+= i;
        Last = (PixelBinSortNode)Last.next;
    }



    /**
     * Detaches the node n from the group, decrementing the counter and
     * subtracting its intensity i from the running total. If n is the
     * current First the head pointer is advanced, and if it is Last the
     * tail pointer is moved backward, before the node unlinks itself via
     * its Remove method.
     */
    public void Remove(PixelBinSortNode n, int i)
    {



        if(n == First)
            First = (PixelBinSortNode)First.next;

        if(n == Last)
            Last = (PixelBinSortNode)Last.previous;


        n.Remove();
        counter--;
        intensity -= i;
    }

    /**
     * Returns the number of pixel nodes currently in the group (the value
     * of the counter).
     */
    public int getCount()
    {
        return counter;
    }

    /**
     * Rewrites every node's (x, y) into full-image coordinates by looking
     * each component up in the provided mapping arrays and re-computes the
     * group's bounding box (x1, y1, x2, y2). The list parameter is a
     * lookup table from local to image coordinates, bx and by are offsets
     * identifying the dendrite origin. The method walks the linked list
     * from First along next pointers, updating the bounding-box extrema
     * as it goes.
     */
    public void calcBounds(Integer[] list, int bx, int by)
    {
        //resets the xy coordinates so they are not relative to the dendrite

        PixelBinSortNode tmp = First;
        x1 = tmp.getX() + bx;
        x2 = x1;
        y1 = tmp.getY() + by;
        y2 = y1;

        while(tmp != null)
        {
            tmp.x = list[bx + tmp.getX()];
            tmp.y = list[by + tmp.getY()];
            if(x1 > tmp.getX())
            {
                x1 = tmp.getX();
            }

            if(x2 < tmp.getX())
            {
                x2 = tmp.getX();
            }

            if(y1 > tmp.getY())
            {
                y1 = tmp.getY();
            }

            if(y2 < tmp.getY())
            {
                y2 = tmp.getY();
            }

            tmp = (PixelBinSortNode)tmp.next;

        }

    }

    /**
     * Builds the polygon border for this group by delegating to
     * neuronToolKit.makePuncta with the head node and the bounding box
     * (x1, x2, y1, y2) plus the pixel count. The resulting polygon is
     * stored in the border field.
     */
    public void createBorder(neuronToolKit tool)
    {

        border = tool.makePuncta(First,x1,x2,y1,y2,getCount());
    }

    /**
     * Creates a final Puncta object from the accumulated intensity, pixel
     * count and polygon border. Called once createBorder has populated
     * border.
     */
    public Puncta makePuncta()
    {
    	return new Puncta(intensity,counter,border);
    }

}
