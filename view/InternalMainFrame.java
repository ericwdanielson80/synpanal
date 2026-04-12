package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.JInternalFrame;

/**
 * Thin {@link JInternalFrame} subclass used as the top-level internal frame
 * hosting the neuron analyzer's main UI inside a desktop pane. It currently
 * adds no behavior of its own but exists as a dedicated type so other code
 * can distinguish the application's primary frame from other internal
 * frames and hang future customization off of it.
 */
public class InternalMainFrame extends JInternalFrame {

    /**
     * Default constructor; simply invokes the {@link JInternalFrame}
     * no-argument constructor, producing an empty internal frame ready to
     * have content added by the surrounding application code.
     */
    public InternalMainFrame() {
    }
}
