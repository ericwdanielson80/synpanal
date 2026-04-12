package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Corner-of-a-pixel node used as a building block for pixel-by-pixel
 * outlines. Every segmentNode carries an integer (x, y) coordinate and a
 * flag indicating whether it represents an inner corner (tucked into the
 * interior of the pixel) or a border corner on the region edge. Because
 * it extends the custom doubly-linked LinkedList base, instances chain
 * together to form a complete outline, and subclasses override the
 * polygon/real coordinate getters to supply per-corner geometry.
 */
public class segmentNode extends LinkedList{
int x;
int y;
boolean isInner;

    /**
     * Stores the integer position (X, Y) and whether this corner is an
     * inner corner (the inner flag) for later geometry queries. The
     * corresponding fields x, y, and isInner are set directly from the
     * parameters.
     */
    public segmentNode(int X, int Y, boolean inner) {
        x = X;
        y = Y;
        isInner = inner;
    }

    /**
     * Returns true when this corner is an inner corner, false when it
     * sits on the region border.
     */
    public boolean isInner()
    {
        return isInner;
    }

    /**
     * Returns the border x coordinate, which is just the stored x.
     */
    public int getborderX()
    {
            return x;
    }

    /**
     * Returns the border y coordinate, which is just the stored y.
     */
    public int getborderY()
    {
            return y;
    }

    /**
     * Returns the integer x coordinate of this corner.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the integer y coordinate of this corner.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Placeholder polygon-rendering x getter. Returns -1 by default;
     * concrete corner subclasses override this to provide the
     * appropriate polygon coordinate.
     */
    public int getPolyX()
    {
        return -1;
    }

    /**
     * Placeholder polygon-rendering y getter. Returns -1 by default;
     * concrete corner subclasses override this to provide the
     * appropriate polygon coordinate.
     */
    public int getPolyY()
    {
        return -1;
    }

    /**
     * Returns the real-valued x coordinate of this corner, defaulting to
     * the integer x. Inner-corner subclasses override this to nudge the
     * value a quarter pixel inward.
     */
    public double getRealX()
    {
        return x;
    }

    /**
     * Returns the real-valued y coordinate of this corner, defaulting to
     * the integer y. Inner-corner subclasses override this to nudge the
     * value a quarter pixel inward.
     */
    public double getRealY()
    {
        return y;
    }

}
