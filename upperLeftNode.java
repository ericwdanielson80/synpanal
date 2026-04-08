package neuron_analyzer;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class upperLeftNode extends segmentNode {

    public upperLeftNode(int X, int Y){
       super(X,Y,false);
   }


   public int getPolyX()
   {
       return getX();
   }

   public int getPolyY()
   {
       return getY();
   }




}
