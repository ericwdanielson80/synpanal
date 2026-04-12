package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseAdapter that forwards press/release events to the owning
 * DataShowHidePanel so it can swap the button icon.
 */
class DataShowHidePanel_SHButton_mouseAdapter extends MouseAdapter {
    private DataShowHidePanel adaptee;
    /** Stores a reference to the host panel. */
    DataShowHidePanel_SHButton_mouseAdapter(DataShowHidePanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Delegates mouse-press events to the host panel. */
    public void mousePressed(MouseEvent e) {
        adaptee.SHButton_mousePressed(e);
    }
    /** Delegates mouse-release events to the host panel. */
    public void mouseReleased(MouseEvent e) {
        adaptee.SHButton_mouseReleased(e);
    }

}
