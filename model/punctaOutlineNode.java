package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Base class for a single-pixel contribution to a puncta or dendrite
 * outline. Each instance owns two segmentNode corners, In and Out, that
 * represent where the outline enters and leaves this pixel. Subclasses
 * (upperSegment, rightSegment, lowerSegment, leftSegment) pick specific
 * edge- or inner-variants of the corner nodes to produce the correct
 * pixel-by-pixel boundary geometry; this base also provides accessors
 * for the corner coordinates and a helper for linking two pixels'
 * outlines together.
 */
public class punctaOutlineNode {
public segmentNode In;
public segmentNode Out;



    /**
     * Constructs an outline node whose entry corner is a segmentNode at
     * (x1, y1) flagged by inner1, and whose exit corner is a segmentNode
     * at (x2, y2) flagged by inner2. The two corners are linked so that
     * In.next points to Out and Out.previous points to In, giving a
     * minimal traversable fragment for this pixel.
     */
    public punctaOutlineNode(int x1,int x2,int y1,int y2,boolean inner1,boolean inner2) {
        In = new segmentNode(x1,y1,inner1);
        Out = new segmentNode(x2,y2,inner2);
        In.next = Out;
        Out.previous = In;

    }

    /**
     * Stub constructor used by subclasses that set up their own In and
     * Out nodes. The parameters are accepted but not used here; callers
     * are expected to populate the corner nodes themselves.
     */
    public punctaOutlineNode(int x1,int x2) {

   }


    /**
     * Returns the integer x coordinate of the entry corner (In).
     */
    public int getInX()
    {
        return In.getX();
    }

    /**
     * Returns the integer x coordinate of the exit corner (Out).
     */
    public int getOutX()
    {
        return Out.getX();
    }

    /**
     * Returns the integer y coordinate of the entry corner (In).
     */
    public int getInY()
    {
        return In.getY();
    }

    /**
     * Returns the integer y coordinate of the exit corner (Out).
     */
    public int getOutY()
    {
        return Out.getY();
    }

    /**
     * Chains this outline fragment into the next pixel's outline by
     * attaching l (the next pixel's entry corner) after this fragment's
     * exit corner. Out.next is set to l and l.previous is set to Out so
     * forward and backward traversal of the combined outline remain
     * consistent. The x and y arguments are provided for parity with
     * related linking APIs but are not used here.
     */
    public void link(segmentNode l,int x, int y)
    {
        Out.next = l;
        l.previous = Out;
    }



}
