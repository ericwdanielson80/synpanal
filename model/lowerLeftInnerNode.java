package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * A segmentNode representing the lower-left corner of a pixel when that
 * corner falls on the inside of a dendrite outline rather than on an
 * external edge. It participates in the linked chain of corner nodes that
 * trace a puncta or dendrite boundary pixel-by-pixel, contributing integer
 * polygon coordinates for screen rendering and fractional real coordinates
 * that nudge the corner slightly inward for more accurate geometry.
 */
public class lowerLeftInnerNode extends segmentNode {
    /**
     * Constructs the lower-left inner corner for the pixel at (X, Y). The
     * corner's logical position is shifted to (X, Y+1), because the lower-left
     * corner of a pixel lies one row below the pixel's top. The true flag
     * passed to the superclass marks this node as an inner corner.
     */
    public lowerLeftInnerNode(int X, int Y) {
        super(X,Y+1,true);
    }

    /**
     * Returns the x component used when this corner is emitted as part of a
     * polygon for drawing: simply the stored integer x coordinate.
     */
    public int getPolyX()
  {
      return getX();
  }

  /**
   * Returns the y component used when this corner is emitted as part of a
   * polygon for drawing: simply the stored integer y coordinate.
   */
  public int getPolyY()
  {
      return getY();
  }

  /**
   * Returns the real-valued x coordinate of the corner for precise
   * geometric work. Because this is an inner corner, the value is nudged a
   * quarter pixel to the right (x + 0.25) so the outline sits just inside
   * the pixel boundary.
   */
  public double getRealX()
  {
      return getX() + 0.25;
  }

  /**
   * Returns the real-valued y coordinate of the corner for precise
   * geometric work. Because this is an inner corner, the value is nudged a
   * quarter pixel upward (y - 0.25) so the outline sits just inside the
   * pixel boundary.
   */
  public double getRealY()
  {
      return getY() - 0.25;
  }
}
