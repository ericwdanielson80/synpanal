package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * {@link segmentNode} specialization representing the upper-right corner of
 * a pixel cell. Used during outline tracing to place vertices at the top-
 * right grid intersection when building polygons around detected features.
 */
public class upperRightNode extends segmentNode {

    /**
     * Constructs an upper-right corner node for the pixel at column
     * {@code X} and row {@code Y}. The X coordinate is incremented so the
     * vertex sits on the pixel's right edge while Y remains at the top
     * edge; the {@code false} argument marks the node as non-endpoint.
     */
    public upperRightNode(int X, int Y){
      super(X + 1,Y,false);
  }


  /**
   * Returns the X coordinate used when this node appears as a polygon
   * vertex; it simply delegates to the superclass X accessor.
   */
  public int getPolyX()
  {
      return getX();
  }

  /**
   * Returns the Y coordinate used when this node appears as a polygon
   * vertex; it simply delegates to the superclass Y accessor.
   */
  public int getPolyY()
  {
      return getY();
  }

}
