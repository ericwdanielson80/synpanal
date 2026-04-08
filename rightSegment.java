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
public class rightSegment extends punctaOutlineNode {

    public rightSegment(int x,int y,boolean hasUpper, boolean hasLower)
    {
        super(x,y);
        if(hasUpper)
        In = new upperRightNode(x,y);
    else
        In = new upperRightNodeInner(x,y);
    if(hasLower)
        Out = new lowerRightNode(x,y);
    else
        Out = new lowerRightNodeInner(x,y);
    In.next = Out;
        Out.previous = In;
    }

}
