package neuron_analyzer;
import java.awt.Polygon;
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
public class groupLeader {
//organizes the pixels into a group

PixelBinSortNode First;
PixelBinSortNode Last;
int counter;
int intensity;

int x1;
int y1;
int x2;
int y2;

Polygon border;


    public groupLeader(PixelBinSortNode n, int i) {
        First = n;
        Last = n;
        counter = 1;
        intensity = i;
    }

    public void Add(PixelBinSortNode n,int i)
    {
        if(counter == 0)
        {
            First = n;
            Last = n;
            counter++;
            return;
        }

        Last.Add(n);
        counter++;
        intensity+= i;
        Last = (PixelBinSortNode)Last.next;
    }



    public void Remove(PixelBinSortNode n, int i)
    {



        if(n == First)
            First = (PixelBinSortNode)First.next;

        if(n == Last)
            Last = (PixelBinSortNode)Last.previous;


        n.Remove();
        counter--;
        intensity -= i;
    }

    public int getCount()
    {
        return counter;
    }

    public void calcBounds(Integer[] list, int bx, int by)
    {
        //resets the xy coordinates so they are not relative to the dendrite

        PixelBinSortNode tmp = First;
        x1 = tmp.getX() + bx;
        x2 = x1;
        y1 = tmp.getY() + by;
        y2 = y1;

        while(tmp != null)
        {
            tmp.x = list[bx + tmp.getX()];
            tmp.y = list[by + tmp.getY()];
            if(x1 > tmp.getX())
            {
                x1 = tmp.getX();
            }

            if(x2 < tmp.getX())
            {
                x2 = tmp.getX();
            }

            if(y1 > tmp.getY())
            {
                y1 = tmp.getY();
            }

            if(y2 < tmp.getY())
            {
                y2 = tmp.getY();
            }

            tmp = (PixelBinSortNode)tmp.next;

        }

    }

    public void createBorder(neuronToolKit tool)
    {
        
        border = tool.makePuncta(First,x1,x2,y1,y2,getCount());
    }
    
    public Puncta makePuncta()
    {
    	return new Puncta(intensity,counter,border);
    }

}
