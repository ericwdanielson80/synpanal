package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Tracks which spine (if any) owns a particular pixel or location during
 * spine-assignment passes. Each instance remembers the best (lowest weight)
 * claim it has seen so far; ties are broken by order, with later spines
 * only replacing the current owner when their weight is strictly smaller.
 * This is the mechanism by which boundary pixels between competing spines
 * get deterministically assigned to a single owning spine.
 */
public class OwnershipObject {
int mySpine = -1;
int ownership = -1;

	/**
	 * Creates an ownership record in its initial, unclaimed state. Both
	 * {@code mySpine} and {@code ownership} retain their sentinel value of
	 * {@code -1}, indicating no spine has claimed this location yet.
	 */
	public OwnershipObject()
	{

	}

	/**
	 * Offers a claim from a candidate spine. The first call seeds the
	 * record with the candidate's spine index and weight; subsequent calls
	 * only replace the stored owner when the offered weight is strictly
	 * less than the current one, implementing a "best (lowest) weight
	 * wins" policy. The {@code spineIndex} parameter identifies the
	 * candidate spine, and {@code ownershipWeight} is the strength of its
	 * claim (lower is stronger, typically a distance metric).
	 */
	public void addSpine(int spineIndex, int ownershipWeight)
	{
		if(mySpine == -1)
		{
			ownership = ownershipWeight;
			mySpine  = spineIndex;
			return;
		}

		if(ownershipWeight < ownership)
		{
			ownership = ownershipWeight;
			mySpine  = spineIndex;
		}
	}
}
