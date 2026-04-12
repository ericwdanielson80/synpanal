package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Concrete {@link NeuronCommand} whose sole job is to ask a
 * {@code functionListener} (typically the main UI controller) to repaint
 * its data pane. It is the command-pattern wrapper used whenever code needs
 * to defer a data-pane refresh and hand it to the command-dispatch
 * machinery rather than invoking the repaint inline.
 */
public class RepaintDataPane implements NeuronCommand{
functionListener FL;

	/**
	 * Creates the command and stores the target {@code functionListener} so
	 * it can be called back later. The {@code fL} parameter is the listener
	 * that will ultimately perform the repaint; it is held in field
	 * {@code FL} for use during {@link #executeCommand()}.
	 */
	public RepaintDataPane(functionListener fL)
	{
		FL = fL;
	}

	/**
	 * Dispatches the deferred work by delegating to the stored listener's
	 * {@code repaintDataPane} method. The method takes no parameters; all
	 * state it needs was captured at construction.
	 */
	public void executeCommand()
	{
		FL.repaintDataPane();
	}
}
