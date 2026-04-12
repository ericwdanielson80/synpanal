package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/** ChangeListener adapter forwarding stateChanged events to its owning RegionGroupTab. */
class RegionGroupTab_this_changeAdapter implements ChangeListener {
    private RegionGroupTab adaptee;
    /** Stores the host RegionGroupTab whose callback will be invoked. */
    RegionGroupTab_this_changeAdapter(RegionGroupTab adaptee) {
        this.adaptee = adaptee;
    }

    /** Delegates the ChangeEvent to RegionGroupTab.this_stateChanged. */
    public void stateChanged(ChangeEvent e) {
        adaptee.this_stateChanged(e);
    }
}
