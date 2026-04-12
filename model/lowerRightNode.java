package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * {@link segmentNode} specialization representing the lower-right corner of
 * a pixel cell. Used by the outline-tracing code to position vertices at
 * the bottom-right grid point of a pixel when stitching together region or
 * dendrite polygons.
 */
public class lowerRightNode extends segmentNode {

    /**
     * Constructs a lower-right corner node for the pixel at column
     * {@code X} and row {@code Y}, offsetting both coordinates by one so
     * the stored position lies at the pixel's bottom-right grid
     * intersection. The {@code false} flag passed upward indicates the
     * node is not a connection endpoint.
     */
    public lowerRightNode(int X, int Y){
      super(X+1,Y+1,false);
  }


  /**
   * Returns the X coordinate this node contributes as a polygon vertex,
   * directly reusing the superclass X position.
   */
  public int getPolyX()
  {
      return getX();
  }

  /**
   * Returns the Y coordinate this node contributes as a polygon vertex,
   * directly reusing the superclass Y position.
   */
  public int getPolyY()
  {
      return getY();
  }

}
