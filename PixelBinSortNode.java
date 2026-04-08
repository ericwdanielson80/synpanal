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
    public PixelBinSortNode(Integer xv,Integer yv) {
        super();
        x = xv;
        y = yv;

    }

    public int getX()
    {
        return x.intValue();
    }

    public int getY()
   {
       return y.intValue();
   }

   public void destroy()
   {
       x = null;
       y = null;
   }

}
