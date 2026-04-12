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
 * Compact LIFO stack of (x, y) integer coordinates that packs each pair into
 * a single 32-bit slot (x in the upper 10 bits, y in the lower 10 bits). It
 * is used as scratch storage during flood-fill and region-growing passes.
 * The backing array grows geometrically when half-full.
 */
public class xyStack {
public int[] list;
int counter;
    /**
     * Creates an empty stack with an initial capacity of 200 slots and a
     * counter of zero. The backing list may grow later on push when the
     * counter exceeds half of its length.
     */
    public xyStack() {
        list = new int[200];
        counter = 0;
    }

    /**
     * Pushes a coordinate onto the top of the stack, packing x into the upper
     * 10 bits and y into the lower 10 bits of one int. If the counter has
     * exceeded half the length of the list, the backing array is doubled via
     * a fresh allocation (list2) and System.arraycopy before the new value is
     * written. The x and y parameters are the column and row being recorded.
     */
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

    /**
     * Removes and returns the top-of-stack coordinate as a two-element int
     * array {x, y}. The packed slot is unpacked by masking the low 10 bits
     * to recover y and right-shifting 10 bits to recover x; the counter is
     * decremented. Returns null if the stack is empty.
     */
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

    /**
     * Returns true when the counter is zero, indicating the stack contains
     * no entries.
     */
    public boolean isEmpty()
    {
        return counter == 0;
    }

    /**
     * Returns the number of coordinates currently held, i.e. the counter.
     */
    public int getLength()
    {
        return counter;
    }

    /**
     * Removes the entry at the supplied index in O(1) by copying the current
     * top-of-stack packed value into that slot and then popping the top.
     * The index parameter identifies which slot should be dropped; order of
     * remaining entries is not preserved.
     */
    public void removeElementAt(int index)
    {

        list[index] = list[counter-1]; //copies the int at the top to the index
                                     //removes the int at the top
                                     //effectively removes the desired element
        pop();
    }
}
