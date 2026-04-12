package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** MouseAdapter bridge that forwards blue-button clicks to the owning ImagePropertiesPanel. */
class ImagePropertiesPanel_BButton_mouseAdapter extends MouseAdapter {
    private ImagePropertiesPanel adaptee;
    /** Stores a reference to the panel that will receive forwarded events. */
    ImagePropertiesPanel_BButton_mouseAdapter(ImagePropertiesPanel adaptee) {
        this.adaptee = adaptee;
    }

    /** Forwards the click event to ImagePropertiesPanel.BButton_mouseClicked. */
    public void mouseClicked(MouseEvent e) {
        adaptee.BButton_mouseClicked(e);
    }
}
