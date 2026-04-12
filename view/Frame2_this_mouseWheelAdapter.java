package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

/**
 * Small adapter that routes {@link MouseWheelListener} events from the
 * main {@link Frame2} panel back to its
 * {@link Frame2#this_mouseWheelMoved(MouseWheelEvent)} handler.
 */
class Frame2_this_mouseWheelAdapter implements MouseWheelListener {
    private Frame2 adaptee;
    /**
     * Stores a reference to the Frame2 instance to forward events to.
     */
    Frame2_this_mouseWheelAdapter(Frame2 adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * Forwards the wheel event to the adapted frame.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        adaptee.this_mouseWheelMoved(e);
    }
}
