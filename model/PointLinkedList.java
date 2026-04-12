package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Point;
/**
 * A doubly-linked list node that carries a java.awt.Point payload. It is used
 * throughout the neuron analyzer to build ordered chains of (x, y) pixel
 * coordinates — for example, as pixels are traced along dendrite outlines or
 * accumulated into spine/puncta regions — while supporting easy append and
 * backwards removal semantics.
 */
public class PointLinkedList {
Point myP;
PointLinkedList next;
PointLinkedList previous;
	/**
	 * Constructs a new node wrapping the given Point. The node is created in
	 * isolation; both next and previous remain null until add or a caller
	 * explicitly wires it into an existing chain. The parameter p becomes the
	 * coordinate carried by this node (stored in myP).
	 */
	public PointLinkedList(Point p)
	{
		myP = p;
	}

	/**
	 * Appends a new node containing the given Point immediately after this
	 * node and returns the freshly created tail. The method allocates a new
	 * PointLinkedList around p, back-links it to this node via its previous
	 * pointer, and stores it as this.next. Returning the new node lets callers
	 * chain appends in a fluent style while always holding the current tail.
	 */
	public PointLinkedList add(Point p)
	{
		next = new PointLinkedList(p);
		next.previous = this;
		return next;
	}

	/**
	 * Removes this node from the end of the chain and returns the node that
	 * now becomes the tail. If this node has no predecessor it is the head of
	 * a single-element list and nothing can be removed, so the method simply
	 * returns this. Otherwise, the previous node's next pointer is cleared,
	 * this node's stored Point myP is nulled out for garbage collection, and
	 * the predecessor is returned as the new tail.
	 */
	public PointLinkedList remove()
	{
		if(previous == null)
			return this;
		previous.next = null;
		myP = null;
		return previous;
	}

	/**
	 * Returns the x coordinate of the Point stored at this node by reading
	 * the x field directly from myP.
	 */
	public int getX()
	{
		return myP.x;
	}

	/**
	 * Returns the y coordinate of the Point stored at this node by reading
	 * the y field directly from myP.
	 */
	public int getY()
	{
		return myP.y;
	}

}
