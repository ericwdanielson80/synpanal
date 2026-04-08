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
public class xyStack {
int[] list;
int counter;
    public xyStack() {
        list = new int[200];
        counter = 0;
    }

    public void add(int x, int y)
    {
      if(counter > list.length / 2)
      {
          int[] list2 = new int[list.length * 2];
          System.arraycopy(list,0,list2,0,list.length);
          list = list2;
      }

      list[counter] = ((x<<10)&(0x3FF<<10))+(y&0x3FF);
      counter++;
    }

    public int[] pop()
    {
        if(counter == 0)
            return null;

        int y = list[counter-1] & 0x3FF;
        int x = list[counter-1] >>10;
        //list[counter] = 0;
        counter--;
        return new int[] {x,y};
    }

    public boolean isEmpty()
    {
        return counter == 0;
    }

    public int getLength()
    {
        return counter;
    }

    public void removeElementAt(int index)
    {

        list[index] = list[counter-1]; //copies the int at the top to the index
                                     //removes the int at the top
                                     //effectively removes the desired element
        pop();
    }
}
