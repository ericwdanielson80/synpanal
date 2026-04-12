package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Represents the right edge of a pixel as a two-node segment running
 * downward from the pixel's upper-right corner to its lower-right corner.
 * The class participates in the linked outline used to trace puncta or
 * dendrite borders and chooses edge versus inner corner node subtypes
 * based on whether the neighboring pixels above and below are part of the
 * same region.
 */
public class rightSegment extends punctaOutlineNode {

    /**
     * Builds the right segment for the pixel at (x, y). In is set to an
     * upperRightNode when the upward neighbor is part of the region
     * (hasUpper is true) or to the inner variant otherwise; Out is
     * similarly chosen between lowerRightNode and lowerRightNodeInner based
     * on hasLower. The two corner nodes are then linked so that In.next
     * refers to Out and Out.previous refers to In, producing a traversable
     * fragment of outline.
     */
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
