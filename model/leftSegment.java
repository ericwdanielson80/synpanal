package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Represents the left edge of a pixel as a two-node segment running upward
 * from the pixel's lower-left corner to its upper-left corner. The class
 * is a link in the pixel-by-pixel outline chain and chooses edge versus
 * inner corner subtypes depending on whether the neighboring pixels below
 * and above belong to the same region.
 */
public class leftSegment extends punctaOutlineNode {

    /**
     * Builds the left segment for the pixel at (x, y). In is set to a
     * lowerLeftNode when the downward neighbor is part of the region
     * (hasDnNeighbor is true) or to lowerLeftInnerNode otherwise. Out is
     * chosen analogously between upperLeftNode and upperLeftNodeInner based
     * on hasUpNeighbor. The two corner nodes are wired with In.next
     * pointing to Out and Out.previous pointing to In so the outline can be
     * walked through this pixel in bottom-to-top order along its left edge.
     */
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
