package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseAdapter used by the "Neuron Data" header label so that clicks on
 * the label are forwarded to the owning panel, triggering a cycle of the
 * displayed label set.
 */
class NeuronDataPanel_ND_mouseAdapter extends MouseAdapter {
    private NeuronDataPanel adaptee;
    /**
     * Stores a reference to the NeuronDataPanel that will receive
     * forwarded click events.
     */
    NeuronDataPanel_ND_mouseAdapter(NeuronDataPanel adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * Forwards the mouse click to the associated panel's
     * {@code ND_mouseClicked} handler.
     */
    public void mouseClicked(MouseEvent e) {
        adaptee.ND_mouseClicked(e);
    }
}
