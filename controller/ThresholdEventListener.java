package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Listener contract for components that must react when an image or channel
 * intensity threshold value changes. Used so that controls driving the
 * brightness/threshold settings can notify downstream views (data panes,
 * overlays, analysis pipelines) that any cached results derived from the
 * previous threshold are stale and must be recomputed or redrawn.
 */
public interface ThresholdEventListener {

	/**
	 * Invoked to signal the listener that a threshold value has been updated.
	 * The implementation is responsible for whatever refresh is appropriate,
	 * typically recalculating channel statistics and requesting a repaint.
	 * No parameters are supplied; the listener reads the current threshold
	 * state from whatever source it was wired to when it was registered.
	 */
	public void fireTresholdUpdateEvent();
}
