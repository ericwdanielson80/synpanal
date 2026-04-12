package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * A minimal singly-linked list node that stores a single integer value. Used
 * throughout the neuron analyzer as a lightweight forward-only chain of ints
 * when building collections of pixel values, indices, or intensity samples
 * without the overhead of java.util containers.
 */
public class IntLinkedList {
public IntLinkedList next;
public int myValue;
    /**
     * Constructs a new node holding the given integer value. The node starts
     * detached (its next pointer is null) until Add is called. The parameter
     * v is simply stored in myValue for later retrieval.
     */
    public IntLinkedList(int v) {
        myValue = v;
    }

    /**
     * Inserts the supplied node immediately after this node in the chain. The
     * new node p inherits whatever this node's next pointer was previously
     * referencing, and this node's next pointer is updated to point at p,
     * effectively splicing p into the list at position this+1.
     */
    public void Add(IntLinkedList p)
    {
        p.next = next;
        next = p;
    }

    /**
     * Breaks the forward link from this node by clearing its next pointer,
     * which detaches the remainder of the chain so it can be garbage-collected
     * (assuming no other references are held elsewhere).
     */
    public void destroy()
    {
        next = null;
    }
}
