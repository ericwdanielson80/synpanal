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
public class upperRightNodeInner extends segmentNode{
    public upperRightNodeInner(int X, int Y) {
      super(X + 1,Y,true);
  }


  public int getPolyX()
  {
      return getX();
  }

  public int getPolyY()
  {
      return getY();
  }

  public double getRealX()
  {
      return getX() - 0.25;
  }

  public double getRealY()
  {
      return getY() + 0.25;
  }


    }
