package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Listener interface for components that want to be informed when the set of
 * cell bodies (or their properties) changes within a specific group. Views
 * that render or summarize cell-body data implement this interface so the
 * rest of the application can push cell updates to them.
 */
public interface CellEventListener {

	/**
	 * Invoked when cell-body data associated with this listener's group has
	 * been added, removed, or modified. Implementations should refresh any
	 * derived state and typically trigger a repaint.
	 */
	public void fireCellUpdateEvent();

	/**
	 * Returns the group identifier (typically corresponding to a color
	 * channel) that this listener is bound to, used to filter which cell
	 * events reach it.
	 */
	public int getGroup();
}
