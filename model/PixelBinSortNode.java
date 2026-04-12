package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

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
 * Linked-list node that carries an (x, y) pixel coordinate and chains into
 * per-intensity bins used by the puncta-detection pipeline. All pixels that
 * share an intensity value are inserted into the same linked list, read out
 * in raster order from (0, 0) to (width, height), so each node's successor
 * is always further along the image. This supports group-joining logic that
 * inspects adjacent coordinates and merges or creates groups during puncta
 * counting.
 */
public class PixelBinSortNode extends LinkedList {
Integer x;
Integer y;

    /*This class will create nodes for a linked list
  the nodes will contain x,y
0 designates no association with a group; positive is a group, negative is an associative group
the purpose of this class is to allow for the binsorting of pixel locations to make puncta counting easier
all pixels of the same value will be inserted into the same linked list. The data will be read from 0,0 to width, height
so all next nodes will be further down the list.
algortihm should function something like this...get value...check adj coor to see if adj are part of a group
or ass group,
if yes join group(need to think about rules), if no make new ass group.
there will be
  */
    /**
     * Creates a new pixel node storing the supplied coordinate values. The
     * xv and yv parameters are the pixel's column and row, respectively;
     * they are boxed as Integer so the destroy method can later null the
     * references to help garbage collection. The superclass LinkedList
     * constructor is invoked to initialize the node's forward and backward
     * link slots.
     */
    public PixelBinSortNode(Integer xv,Integer yv) {
        super();
        x = xv;
        y = yv;

    }

    /**
     * Returns the stored x (column) coordinate unboxed as a primitive int.
     */
    public int getX()
    {
        return x.intValue();
    }

    /**
     * Returns the stored y (row) coordinate unboxed as a primitive int.
     */
    public int getY()
   {
       return y.intValue();
   }

   /**
    * Clears the boxed coordinate references so the Integer instances become
    * eligible for garbage collection once the node is otherwise unreachable.
    */
   public void destroy()
   {
       x = null;
       y = null;
   }

}
