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
public class IntLinkedList {
IntLinkedList next;
int myValue;
    public IntLinkedList(int v) {
        myValue = v;
    }

    public void Add(IntLinkedList p)
    {
        p.next = next;
        next = p;
    }

    public void destroy()
    {
        next = null;
    }
}
