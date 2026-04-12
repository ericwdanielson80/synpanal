package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Mouse adapter forwarding press events to the enclosing panel's SButton_mousePressed handler. */
class RegionOptionsPanel_SButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    /** Stores the RegionOptionsPanel that press events should be dispatched to. */
    RegionOptionsPanel_SButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the mouse press to the adaptee's SButton_mousePressed handler. */
    public void mousePressed(MouseEvent e) {
        adaptee.SButton_mousePressed(e);
    }
}
