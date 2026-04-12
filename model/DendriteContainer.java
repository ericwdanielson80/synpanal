package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Simple wrapper around an array of Dendrite objects that provides
 * housekeeping over the collection. The analyzer stores dendrites in a
 * sparse, possibly fragmented array (with null slots where dendrites have
 * been removed), and this class offers a compaction helper that rebuilds
 * the backing array without those gaps while preserving order.
 */
public class DendriteContainer {
public Dendrite[] myDendrites;

/**
 * Stores the supplied Dendrite array as the container's backing storage.
 * The array is retained by reference; the parameter d is simply assigned
 * to myDendrites for later use.
 */
public DendriteContainer(Dendrite[] d)
{
	myDendrites = d;
}

/**
 * Rebuilds myDendrites so that any null slots left by earlier removals
 * are eliminated. The method first sweeps the array once to count the
 * non-null entries into counter, then allocates a new array sized
 * counter + 1 (leaving a trailing null slot, matching the container's
 * expected layout). A second pass copies each non-null element into the
 * new array in original order, after which myDendrites is swapped to
 * point at the compacted copy.
 */
public void compactDendrites()
{
	int counter = 0;
	for(int k = 0; k < myDendrites.length; k++)
	{
		if(myDendrites[k] != null)
			counter++;
	}
	Dendrite[] out = new Dendrite[counter + 1];
	counter = 0;
	for(int k = 0; k < myDendrites.length; k++)
	{
		if(myDendrites[k] != null)
			{
			out[counter] = myDendrites[k];
			counter++;
			}
	}
	myDendrites = out;

}

/*public void removeGroup(int group)
{
	for(int k = 0; k < myDendrites.length; k++)
	{
		if(myDendrites[k] != null)
		{
			if(myDendrites[k].groupMember == group)
				myDendrites[k] = null;
		}
	}
}*/
}
