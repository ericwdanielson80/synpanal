package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** MouseAdapter bridge that forwards green-button clicks to the owning ImagePropertiesPanel. */
class ImagePropertiesPanel_GButton_mouseAdapter extends MouseAdapter {
    private ImagePropertiesPanel adaptee;
    /** Stores a reference to the panel that will receive forwarded events. */
    ImagePropertiesPanel_GButton_mouseAdapter(ImagePropertiesPanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the click event to ImagePropertiesPanel.GButton_mouseClicked. */
    public void mouseClicked(MouseEvent e) {
        adaptee.GButton_mouseClicked(e);
    }
}
