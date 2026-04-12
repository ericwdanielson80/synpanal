package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * {@link segmentNode} specialization representing the upper-left corner of
 * a pixel cell, which is the pixel's canonical origin in image coordinates.
 * It is used by the polygon/outline construction code to place a vertex
 * exactly at the top-left grid intersection of a pixel.
 */
public class upperLeftNode extends segmentNode {

    /**
     * Constructs an upper-left corner node for the pixel at column
     * {@code X} and row {@code Y}, passing both coordinates through
     * unchanged to the superclass because the top-left corner of a pixel
     * coincides with the pixel's own integer coordinates. The {@code
     * false} argument marks the node as non-endpoint.
     */
    public upperLeftNode(int X, int Y){
       super(X,Y,false);
   }


   /**
    * Returns the X coordinate used when this node is emitted as a polygon
    * vertex, which is simply the stored superclass X value.
    */
   public int getPolyX()
   {
       return getX();
   }

   /**
    * Returns the Y coordinate used when this node is emitted as a polygon
    * vertex, which is simply the stored superclass Y value.
    */
   public int getPolyY()
   {
       return getY();
   }




}
