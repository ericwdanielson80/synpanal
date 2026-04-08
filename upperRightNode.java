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
public class upperRightNode extends segmentNode {

    public upperRightNode(int X, int Y){
      super(X + 1,Y,false);
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
