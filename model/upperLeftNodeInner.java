package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * A segmentNode representing the upper-left corner of a pixel when the
 * corner lies on the inside of a dendrite outline rather than on an
 * external edge. It is a link in the pixel-by-pixel outline chain; it
 * supplies integer polygon coordinates for rendering and fractional
 * coordinates that tuck the corner slightly into the pixel interior.
 */
public class upperLeftNodeInner extends segmentNode{
    /**
     * Constructs the upper-left inner corner for the pixel at (X, Y). The
     * superclass is initialized with (X, Y), the pixel's own top-left anchor,
     * and flagged as inner.
     */
    public upperLeftNodeInner(int X, int Y) {
       super(X,Y,true);
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
    * Returns the real-valued x coordinate nudged a quarter pixel rightward
    * (x + 0.25) so the outline sits just inside the pixel edge.
    */
   public double getRealX()
  {
      return getX() + 0.25;
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
