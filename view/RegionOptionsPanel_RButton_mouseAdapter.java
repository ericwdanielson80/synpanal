package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Mouse adapter forwarding press events to the enclosing panel's RButton_mousePressed handler. */
class RegionOptionsPanel_RButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    /** Stores the RegionOptionsPanel that press events should be dispatched to. */
    RegionOptionsPanel_RButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the mouse press to the adaptee's RButton_mousePressed handler. */
    public void mousePressed(MouseEvent e) {
        adaptee.RButton_mousePressed(e);
    }
}
