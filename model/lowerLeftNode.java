package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * {@link segmentNode} specialization representing the lower-left corner of
 * a pixel cell when tracing dendrite or region outlines. During polygon
 * construction this node is placed at the bottom-left vertex of the pixel
 * at {@code (X, Y)} so that edges between adjacent pixels line up on
 * integer grid positions.
 */
public class lowerLeftNode extends segmentNode {

    /**
     * Constructs a lower-left corner node for the pixel at column {@code X}
     * and row {@code Y}. The superclass coordinate is offset by adding one
     * to {@code Y} so the node sits at the bottom edge of the pixel; the
     * third argument {@code false} signals that this node is not marked as
     * an endpoint for segment-connection purposes.
     */
    public lowerLeftNode(int X, int Y){
      super(X,Y+1,false);
  }


  /**
   * Returns the X coordinate to use when emitting a polygon vertex for
   * this node; it simply exposes the stored segment-node X position.
   */
  public int getPolyX()
  {
      return getX();
  }

  /**
   * Returns the Y coordinate to use when emitting a polygon vertex for
   * this node; it simply exposes the stored segment-node Y position.
   */
  public int getPolyY()
  {
      return getY();
  }

}
