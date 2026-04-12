package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Command pattern interface for deferred actions within the neuron analyzer.
 * Implementations encapsulate a unit of work (for example, repainting a data
 * pane or updating a UI state) that can be scheduled and later dispatched by
 * invoking {@link #executeCommand()}. This provides a uniform way for the
 * application to queue and fire off operations without the caller needing to
 * know the concrete behavior being performed.
 */
public interface NeuronCommand {

	/**
	 * Runs the action encapsulated by this command. Concrete implementations
	 * perform whatever work they represent, such as triggering a repaint or
	 * mutating application state. The method takes no parameters; all required
	 * context is supplied to the implementation at construction time.
	 */
	public void executeCommand();
}
