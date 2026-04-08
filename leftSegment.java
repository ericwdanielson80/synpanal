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
public class leftSegment extends punctaOutlineNode {

    public leftSegment(int x,int y,boolean hasUpNeighbor, boolean hasDnNeighbor)
    {
        super(x,y);
        if(hasDnNeighbor)
            In = new lowerLeftNode(x,y);
        else
            In = new lowerLeftInnerNode(x,y);
        if(hasUpNeighbor)
            Out = new upperLeftNode(x,y);
        else
            Out = new upperLeftNodeInner(x,y);
        In.next = Out;
        Out.previous = In;
    }


}
