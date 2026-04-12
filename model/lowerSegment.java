package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Represents the lower edge of a pixel as a two-node segment running from
 * the pixel's lower-right corner to its lower-left corner. The class is a
 * link in the pixel-by-pixel outline chain and chooses edge versus inner
 * corner subtypes depending on whether the neighboring pixels to the right
 * and left belong to the same region.
 */
public class lowerSegment extends punctaOutlineNode {

    /**
     * Builds the lower segment for the pixel at (x, y). In is set to a
     * lowerRightNode when the right neighbor is in the region (hasRight is
     * true) or to lowerRightNodeInner otherwise, while Out is selected
     * between lowerLeftNode and lowerLeftInnerNode based on hasLeft. The
     * two corner nodes are linked together with In.next pointing to Out
     * and Out.previous pointing to In so the outline can be traversed
     * through this pixel in right-to-left order along its bottom edge.
     */
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
