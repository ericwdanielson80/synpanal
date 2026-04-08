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
public class lowerSegment extends punctaOutlineNode {

    public lowerSegment(int x,int y,boolean hasLeft,boolean hasRight)
    {
        super(x,y);
        if(hasRight)
        In = new lowerRightNode(x,y);
      else
        In = new lowerRightNodeInner(x,y);
    if(hasLeft)
        Out = new lowerLeftNode(x,y);
    else
        Out = new lowerLeftInnerNode(x,y);

        In.next = Out;
        Out.previous = In;
    }

}
