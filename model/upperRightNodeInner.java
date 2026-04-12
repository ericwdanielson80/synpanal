package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * A segmentNode representing the upper-right corner of a pixel when that
 * corner lies on the inside of a dendrite outline instead of an external
 * edge. It is a link in the pixel-by-pixel outline trace and reports
 * integer coordinates for polygon rendering alongside fractional
 * coordinates that shift the corner slightly inward.
 */
public class upperRightNodeInner extends segmentNode{
    /**
     * Constructs the upper-right inner corner for the pixel at (X, Y). The
     * superclass is initialized with (X+1, Y) because the upper-right lies
     * one column to the right of the pixel's top-left. The true flag marks
     * this as an inner corner.
     */
    public upperRightNodeInner(int X, int Y) {
      super(X + 1,Y,true);
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
   * Returns the real-valued x coordinate nudged a quarter pixel leftward
   * (x - 0.25) so the outline sits just inside the pixel edge.
   */
  public double getRealX()
  {
      return getX() - 0.25;
  }

  /**
   * Returns the real-valued y coordinate nudged a quarter pixel downward
   * (y + 0.25) so the outline sits just inside the pixel edge.
   */
  public double getRealY()
  {
      return getY() + 0.25;
  }


    }
