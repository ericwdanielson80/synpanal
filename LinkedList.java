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
public class LinkedList {
LinkedList next;
LinkedList previous;

    public LinkedList() {
    }

    public void Add(LinkedList l)
    {
        if(next != null)
            next.previous = l;
        l.next = next;
        next = l;
        l.previous = this;
    }

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
