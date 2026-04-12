package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

/**
 * Forwards mouse-wheel events from the scroll pane to the TiffScrollPane's
 * this_mouseWheelMoved hook.
 */
class TiffScrollPane_this_mouseWheelAdapter implements MouseWheelListener {
    private TiffScrollPane adaptee;
    /** Stores the enclosing TiffScrollPane reference. */
    TiffScrollPane_this_mouseWheelAdapter(TiffScrollPane adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards to adaptee.this_mouseWheelMoved. */
    public void mouseWheelMoved(MouseWheelEvent e) {
        adaptee.this_mouseWheelMoved(e);
    }
}
