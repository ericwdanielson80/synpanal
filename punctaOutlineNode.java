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
public class punctaOutlineNode {
segmentNode In;
segmentNode Out;



    public punctaOutlineNode(int x1,int x2,int y1,int y2,boolean inner1,boolean inner2) {
        In = new segmentNode(x1,y1,inner1);
        Out = new segmentNode(x2,y2,inner2);
        In.next = Out;
        Out.previous = In;

    }

    public punctaOutlineNode(int x1,int x2) {

   }


    public int getInX()
    {
        return In.getX();
    }

    public int getOutX()
    {
        return Out.getX();
    }

    public int getInY()
    {
        return In.getY();
    }

    public int getOutY()
    {
        return Out.getY();
    }

    public void link(segmentNode l,int x, int y)
    {
        Out.next = l;
        l.previous = Out;
    }



}
