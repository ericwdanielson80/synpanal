package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

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
/**
 * Helper used when stitching neighbouring dendrite segments together. It
 * buffers up to two inbound endpoints and two outbound endpoints and then
 * pairs them by matching real-world coordinates so that each outbound
 * segmentNode's next pointer, and the corresponding inbound node's previous
 * pointer, reference the correct neighbour on the other side of the join.
 */
public class nodeLinker {
segmentNode[] In;
segmentNode[] Out;
    /**
     * Allocates the two In and two Out two-slot buffers used to stage the
     * endpoints that will later be linked. Entries are null until addIn or
     * addOut is called.
     */
    public nodeLinker() {
        In = new segmentNode[2];
        Out = new segmentNode[2];
    }

    /**
     * Stores an inbound segmentNode into the first free slot of the In array.
     * The in parameter is a segment endpoint that will later be matched
     * against an outbound endpoint with the same real-world coordinates.
     */
    public void addIn(segmentNode in)
    {
        if(In[0] == null)
            In[0] = in;
        else
            In[1] = in;
    }

    /**
     * Stores an outbound segmentNode into the first free slot of the Out
     * array. The out parameter will later have its next pointer populated
     * when linkNodes pairs it with its matching inbound counterpart.
     */
    public void addOut(segmentNode out)
    {
        if(Out[0] == null)
            Out[0] = out;
        else
            Out[1] = out;
    }

    /**
     * Pairs the buffered inbound and outbound endpoints by matching
     * coordinates. For each Out slot, compares getRealX/getRealY against
     * In[0]; if they match that Out is linked to In[0] (and In[0].previous to
     * the same Out.next), otherwise it is linked to In[1]. The method
     * returns early after processing Out[0] if Out[1] is null, supporting
     * the case where only one outbound endpoint exists. The local j is
     * declared but unused.
     */
    public void linkNodes()
    {
        int j = 0;

        if(Out[0].getRealX() == In[0].getRealX() && Out[0].getRealY() == In[0].getRealY())
        {
            Out[0].next = In[0];
            In[0].previous = Out[0].next;
        }
        else
        {
           Out[0].next = In[1];
           In[1].previous = Out[0].next;
        }

        if(Out[1] == null)
            return;

        if(Out[1].getRealX() == In[0].getRealX() && Out[1].getRealY() == In[0].getRealY())
        {
            Out[1].next = In[0];
            In[0].previous = Out[1].next;
        }
        else
        {
           Out[1].next = In[1];
           In[1].previous = Out[1].next;
        }


    }

}
