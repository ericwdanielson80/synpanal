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
public class nodeLinker {
segmentNode[] In;
segmentNode[] Out;
    public nodeLinker() {
        In = new segmentNode[2];
        Out = new segmentNode[2];
    }

    public void addIn(segmentNode in)
    {
        if(In[0] == null)
            In[0] = in;
        else
            In[1] = in;
    }

    public void addOut(segmentNode out)
    {
        if(Out[0] == null)
            Out[0] = out;
        else
            Out[1] = out;
    }

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
