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
public class segmentNode extends LinkedList{
int x;
int y;
boolean isInner;

    public segmentNode(int X, int Y, boolean inner) {
        x = X;
        y = Y;
        isInner = inner;
    }

    public boolean isInner()
    {
        return isInner;
    }

    public int getborderX()
    {
            return x;
    }

    public int getborderY()
    {
            return y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getPolyX()
    {
        return -1;
    }

    public int getPolyY()
    {
        return -1;
    }

    public double getRealX()
    {
        return x;
    }

    public double getRealY()
    {
        return y;
    }

}
