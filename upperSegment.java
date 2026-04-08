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
public class upperSegment extends punctaOutlineNode {

    public upperSegment(int x,int y,boolean hasLeft, boolean hasRight)
    {
        super(x,y);
        if(hasLeft)
        In = new upperLeftNode(x,y);
    else
        In = new upperLeftNodeInner(x,y);
    if(hasRight)
        Out = new upperRightNode(x,y);
    else
        Out = new upperRightNodeInner(x,y);

        In.next = Out;
        Out.previous = In;
    }
}
