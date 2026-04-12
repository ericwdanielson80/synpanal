package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

/**
 * Small adapter that routes {@link MouseMotionAdapter} move events from
 * the main {@link Frame2} panel to
 * {@link Frame2#this_mouseMoved(MouseEvent)}.
 */
class Frame2_this_mouseMotionAdapter extends MouseMotionAdapter {
    private Frame2 adaptee;
    /**
     * Stores a reference to the Frame2 instance to forward events to.
     */
    Frame2_this_mouseMotionAdapter(Frame2 adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * Forwards mouse move events to the adapted frame.
     */
    public void mouseMoved(MouseEvent e) {
        adaptee.this_mouseMoved(e);
    }
}
