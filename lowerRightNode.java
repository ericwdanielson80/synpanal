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
public class lowerRightNode extends segmentNode {

    public lowerRightNode(int X, int Y){
      super(X+1,Y+1,false);
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
