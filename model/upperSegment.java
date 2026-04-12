package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Represents the upper edge of a pixel as a two-node segment, running from
 * the pixel's upper-left corner to its upper-right corner. The segment is
 * part of the linked outline used to trace puncta or dendrite borders, and
 * it chooses between "edge" and "inner" corner-node subtypes depending on
 * whether the neighboring pixel to the left or right is part of the same
 * region.
 */
public class upperSegment extends punctaOutlineNode {

    /**
     * Builds the upper segment for the pixel at (x, y). The constructor
     * picks In (the starting corner) to be an upperLeftNode when the pixel
     * to the left belongs to the same region (hasLeft is true) or an
     * upperLeftNodeInner otherwise, meaning the boundary turns inward at
     * that corner. Out (the ending corner) is chosen analogously between
     * upperRightNode and upperRightNodeInner using the hasRight flag. The
     * two nodes are then wired together as a mini-chain with In.next set to
     * Out and Out.previous set to In so that a traversal of the outline
     * will step from one to the other through this pixel.
     */
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
