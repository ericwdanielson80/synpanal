package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Mouse adapter forwarding press events to the enclosing panel's CButton_mousePressed handler. */
class RegionOptionsPanel_CButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    /** Stores the RegionOptionsPanel that press events should be dispatched to. */
    RegionOptionsPanel_CButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the mouse press to the adaptee's CButton_mousePressed handler. */
    public void mousePressed(MouseEvent e) {
        adaptee.CButton_mousePressed(e);
    }
}
