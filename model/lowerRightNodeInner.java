package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * A segmentNode representing the lower-right corner of a pixel when that
 * corner lies on the inside of a dendrite outline rather than on an
 * external edge. Like its sibling corner-node classes it is a link in the
 * pixel-by-pixel outline trace, returning integer polygon coordinates for
 * rendering and shifted real coordinates that pull the corner slightly
 * inward for accurate geometry.
 */
public class lowerRightNodeInner extends segmentNode{
    /**
     * Constructs the lower-right inner corner for the pixel at (X, Y). The
     * corner is stored at (X+1, Y+1) because the lower-right of a pixel sits
     * one column to the right and one row below its top-left anchor. Passing
     * true to the superclass flags this node as an inner corner.
     */
    public lowerRightNodeInner(int X, int Y) {
      super(X+1,Y+1,true);
  }


  /**
   * Returns the polygon-drawing x coordinate, which is just the stored
   * integer x.
   */
  public int getPolyX()
  {
      return getX();
  }

  /**
   * Returns the polygon-drawing y coordinate, which is just the stored
   * integer y.
   */
  public int getPolyY()
  {
      return getY();
  }

  /**
   * Returns the real-valued x coordinate of this inner corner, shifted a
   * quarter pixel to the left (x - 0.25) so that the outline sits just
   * inside the pixel edge.
   */
  public double getRealX()
  {
      return getX() - 0.25;
  }

  /**
   * Returns the real-valued y coordinate of this inner corner, shifted a
   * quarter pixel upward (y - 0.25) so that the outline sits just inside
   * the pixel edge.
   */
  public double getRealY()
  {
      return getY() - 0.25;
  }



}
