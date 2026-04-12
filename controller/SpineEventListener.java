package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Listener contract for consumers that must react when the spine data for a
 * given group changes. Spine-related UI panels, counts, and measurement
 * tables register themselves so they can stay in sync as spines are
 * added, deleted, or recomputed on the underlying dendrites.
 */
public interface SpineEventListener {

	/**
	 * Called when spine data relevant to the listener's group has been
	 * updated. The listener should refresh any cached measurements and
	 * redraw the affected UI as appropriate.
	 */
	public void fireSpineUpdateEvent();

	/**
	 * Returns the group identifier (typically a color-channel group) this
	 * listener is tied to, allowing the event dispatcher to route updates to
	 * only the relevant listeners.
	 */
	public int getGroup();
}
