package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Represents a spatial coincidence of up to three colored puncta (one per
 * red/green/blue channel) that overlap at the same image location. The object
 * holds references to the participating Puncta and provides accessors used by
 * colocalization analysis to query which channels are represented and to
 * selectively restore channels from the ignored state.
 */
public class OverlapObject {
Puncta[] rgb = new Puncta[3]; // slots 0=red, 1=green, 2=blue
OwnershipObject oo = new OwnershipObject();
int number = 0;

    /**
     * Creates an empty overlap record with no puncta assigned to any channel
     * and a count of zero. Channel slots are populated later via the
     * setRed/setGreen/setBlue methods.
     */
    public OverlapObject()
	{

	}

	/**
	 * Returns the running count of channels that have had a puncta assigned to
	 * this overlap. The count is incremented by each successful set call.
	 */
	public int numberOverlap()
	{
		return number;
	}

	/**
	 * Assigns the given puncta to the red channel slot and increments the
	 * overlap count. The parameter p is the red-channel puncta participating
	 * in this overlap.
	 */
	public void setRed(Puncta p)
	{
		rgb[0] = p;
		number++;
	}

	/**
	 * Assigns the given puncta to the green channel slot and increments the
	 * overlap count. The parameter p is the green-channel puncta participating
	 * in this overlap.
	 */
	public void setGreen(Puncta p)
	{
		rgb[1] = p;
		number++;
	}

	/**
	 * Assigns the given puncta to the blue channel slot and increments the
	 * overlap count. The parameter p is the blue-channel puncta participating
	 * in this overlap.
	 */
	public void setBlue(Puncta p)
	{
		rgb[2] = p;
		number++;
	}

	/**
	 * Returns true if a red-channel puncta has been assigned to this overlap,
	 * determined by a non-null entry in the red slot of the rgb array.
	 */
	public boolean hasRed()
	{
		if(rgb[0] != null)
			return true;
		return false;
	}
	
	/**
	 * Returns true if a green-channel puncta has been assigned to this
	 * overlap, determined by a non-null entry in the green slot.
	 */
	public boolean hasGreen()
	{
		if(rgb[1] != null)
			return true;
		return false;
	}

	/**
	 * Returns true if a blue-channel puncta has been assigned to this overlap,
	 * determined by a non-null entry in the blue slot.
	 */
	public boolean hasBlue()
	{
		if(rgb[2] != null)
			return true;
		return false;
	}

	/**
	 * Conditionally clears the ignored flag on one or more of the assigned
	 * puncta when this overlap's current channel occupancy matches the
	 * requested predicate. The ifRed, ifGreen and ifBlue parameters form the
	 * channel-presence pattern that this overlap must match; if the actual
	 * occupancy (from hasRed/hasGreen/hasBlue) agrees, then for each of
	 * restoreRed, restoreGreen and restoreBlue that is true the corresponding
	 * puncta's BooleanContainer isIgnored flag is set to false, re-enabling it
	 * for analysis.
	 */
	public void restore(boolean restoreRed, boolean restoreGreen, boolean restoreBlue, boolean ifRed, boolean ifGreen, boolean ifBlue)
	{
		if(ifRed == hasRed() && ifGreen == hasGreen() && ifBlue == hasBlue())
		{
			if(restoreRed)				
				rgb[0].bC.isIgnored = false;				
			if(restoreGreen)
				rgb[1].bC.isIgnored = false;
			if(restoreBlue)
				rgb[2].bC.isIgnored = false;
		}
	}
	
		
}
