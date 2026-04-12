package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Listener interface for recipients that care about changes to object counts
 * within a particular grouping (for example, the number of puncta detected in
 * a specific channel/group). Registered listeners are notified whenever a
 * new count becomes available so they can refresh any dependent summary views.
 */
public interface CountEventListener {

	/**
	 * Notifies the listener that a count relevant to it has been recomputed.
	 * The implementation is expected to update its cached values and, where
	 * applicable, request a visual refresh. No parameters are passed; the
	 * listener retrieves current count data from the sources it already tracks.
	 */
	public void countEventFired();

	/**
	 * Returns the identifier of the group (color-channel based grouping) that
	 * this listener is associated with. The count-producing code uses this to
	 * decide whether a given update is relevant to this listener.
	 */
	public int getGroup();
}
