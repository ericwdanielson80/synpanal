package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Forwards mouseClicked events from the scroll pane to the TiffScrollPane's
 * this_mouseClicked hook.
 */
class TiffScrollPane_this_mouseAdapter extends MouseAdapter {
    private TiffScrollPane adaptee;
    /** Stores the enclosing TiffScrollPane reference. */
    TiffScrollPane_this_mouseAdapter(TiffScrollPane adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards to adaptee.this_mouseClicked. */
    public void mouseClicked(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}
