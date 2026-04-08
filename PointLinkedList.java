package neuron_analyzer;
import java.awt.Point;
public class PointLinkedList {
Point myP;
PointLinkedList next;
PointLinkedList previous;
	public PointLinkedList(Point p)
	{
		myP = p;
	}
	
	public PointLinkedList add(Point p)
	{
		next = new PointLinkedList(p);
		next.previous = this;
		return next;		
	}
	
	public PointLinkedList remove()
	{
		if(previous == null)
			return this;
		previous.next = null;
		myP = null;
		return previous;
	}
	
	public int getX()
	{
		return myP.x;
	}
	
	public int getY()
	{
		return myP.y;
	}

}
