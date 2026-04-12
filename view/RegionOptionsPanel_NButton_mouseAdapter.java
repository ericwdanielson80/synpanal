package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Mouse adapter forwarding press events to the enclosing panel's NButton_mousePressed handler. */
class RegionOptionsPanel_NButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    /** Stores the RegionOptionsPanel that press events should be dispatched to. */
    RegionOptionsPanel_NButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the mouse press to the adaptee's NButton_mousePressed handler. */
    public void mousePressed(MouseEvent e) {
        adaptee.NButton_mousePressed(e);
    }
}
