package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Minimal doubly-linked list node used as a base class for richer outline
 * and geometry node types (for example segmentNode). The class carries no
 * payload itself; it supplies the forward/backward pointers and the
 * insertion/removal plumbing that subclasses inherit to form larger
 * ordered chains of analysis objects.
 */
public class LinkedList {
public LinkedList next;
public LinkedList previous;

    /**
     * No-argument constructor that creates an isolated node with both
     * next and previous left null. Subclasses typically add their own
     * payload initialization on top.
     */
    public LinkedList() {
    }

    /**
     * Splices the node l into the chain immediately after this node. If
     * this node already has a successor, that successor's previous
     * pointer is redirected at l so backward traversal stays consistent.
     * The new node's next is then wired up to the former successor, this
     * node's next is set to l, and l's previous pointer is aimed at this
     * node, completing the insertion.
     */
    public void Add(LinkedList l)
    {
        if(next != null)
            next.previous = l;
        l.next = next;
        next = l;
        l.previous = this;
    }

    /**
     * Unlinks this node from the chain. The successor (if any) has its
     * previous pointer redirected to this node's predecessor, and the
     * predecessor (if any) has its next pointer redirected to this
     * node's successor, so the surrounding nodes close up around the
     * removed element. Both of this node's own pointers are then cleared
     * so it no longer references the chain.
     */
    public void Remove()
    {
        if(next != null)
            next.previous = previous;
        if(previous != null)
            previous.next = next;

        next = null;
        previous = null;
    }
}
