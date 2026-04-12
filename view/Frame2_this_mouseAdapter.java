package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Small adapter that routes {@link MouseAdapter} press events from the
 * main {@link Frame2} panel to
 * {@link Frame2#this_mouseClicked(MouseEvent)}.
 */
class Frame2_this_mouseAdapter extends MouseAdapter {
    private Frame2 adaptee;
    /**
     * Stores a reference to the Frame2 instance to forward events to.
     */
    Frame2_this_mouseAdapter(Frame2 adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * Forwards mouse press events to the adapted frame's
     * {@code this_mouseClicked} handler.
     */
    public void mousePressed(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}
