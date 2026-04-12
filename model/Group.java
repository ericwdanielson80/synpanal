package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Small mutable wrapper around an integer identifier used to tag objects
 * (dendrites, spines, puncta, cells) with the logical group they belong to.
 * Groups in the neuron analyzer generally correspond to the color-channel
 * based buckets (R/G/B) under which detected features are organized.
 * Because this class holds the value by reference, many objects can share
 * the same {@code Group} instance and observe updates to the group number
 * when it is reassigned in one place.
 */
public class Group {
private int myGroup;

	/**
	 * Constructs a new group wrapper initialized to the given identifier.
	 * The {@code i} parameter is stored in the {@code myGroup} field and
	 * becomes the value returned by {@link #getValue()} until changed.
	 */
	public Group(int i)
	{
		myGroup = i;
	}

	/**
	 * Replaces the currently held group identifier with a new value. The
	 * {@code i} parameter overwrites {@code myGroup}, so any subsequent
	 * reads through this shared instance will see the new group number.
	 */
	public void setGroup(int i)
	{
		myGroup = i;
	}

	/**
	 * Returns the current integer value of this group wrapper. Callers use
	 * this to compare feature membership against the known color-channel
	 * group identifiers elsewhere in the application.
	 */
	public int getValue()
	{
		return myGroup;
	}
}
