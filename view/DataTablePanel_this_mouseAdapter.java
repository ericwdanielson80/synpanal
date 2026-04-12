package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Trivial MouseAdapter that forwards mouseClicked events to the wrapped
 * DataTablePanel so the panel can handle left- and right-click row actions.
 */
class DataTablePanel_this_mouseAdapter extends MouseAdapter {
    private DataTablePanel adaptee;
    /** Stores the DataTablePanel that this adapter will forward events to. */
    DataTablePanel_this_mouseAdapter(DataTablePanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the click event to DataTablePanel.this_mouseClicked. */
    public void mouseClicked(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}
