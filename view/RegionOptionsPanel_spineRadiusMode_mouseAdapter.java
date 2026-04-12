package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

    /** Mouse adapter forwarding press events to the enclosing panel's spineRadiusMode_mousePressed handler. */
    class RegionOptionsPanel_spineRadiusMode_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        /** Stores the RegionOptionsPanel that press events should be dispatched to. */
        RegionOptionsPanel_spineRadiusMode_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        /** Forwards the mouse press to the adaptee's spineRadiusMode_mousePressed handler. */
        public void mousePressed(MouseEvent e) {
            adaptee.spineRadiusMode_mousePressed(e);
        }
    }
