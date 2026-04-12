package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

/**
 * Forwards mouseMoved events from the scroll pane to the TiffScrollPane's
 * this_mouseMoved hook.
 */
class TiffScrollPane_this_mouseMotionAdapter extends MouseMotionAdapter {
    private TiffScrollPane adaptee;
    /** Stores the enclosing TiffScrollPane so events can be forwarded back to it. */
    TiffScrollPane_this_mouseMotionAdapter(TiffScrollPane adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards to adaptee.this_mouseMoved. */
    public void mouseMoved(MouseEvent e) {
        adaptee.this_mouseMoved(e);
    }
}
