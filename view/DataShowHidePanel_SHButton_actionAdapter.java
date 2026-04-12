package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Thin ActionListener adapter that forwards button clicks to the owning
 * DataShowHidePanel.
 */
class DataShowHidePanel_SHButton_actionAdapter implements ActionListener {
    private DataShowHidePanel adaptee;
    /** Stores a reference to the host panel whose handler should be invoked. */
    DataShowHidePanel_SHButton_actionAdapter(DataShowHidePanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Delegates the action event to the host panel. */
    public void actionPerformed(ActionEvent e) {
        adaptee.SHButton_actionPerformed(e);
    }
}
