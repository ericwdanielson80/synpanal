package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import java.awt.*;

/**
 * Top-level workspace panel for a single open image. It arranges two main
 * sub-panels side by side using a BorderLayout: the ImageandoptionPanel
 * (the image canvas and per-image analysis options) occupies the center,
 * and the DataDisplayPanel (tabular results and readouts) is docked to
 * the east. A shared functionListener is passed into both child panels
 * so user actions on either side can invoke analysis callbacks.
 */
public class InternalMFPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    ImageandoptionPanel IAOPanel;
    DataDisplayPanel DDPanel;
    functionListener fL;
    /**
     * Creates the workspace panel bound to the given functionListener
     * fl, stores it in the field fL, and invokes jbInit to assemble the
     * layout. Any exception raised during sub-panel construction is
     * caught and its stack trace printed so UI bootstrapping can fail
     * visibly without aborting the whole application.
     */
    public InternalMFPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Assembles the component hierarchy: the panel's layout manager is
     * set to borderLayout1, an ImageandoptionPanel and a DataDisplayPanel
     * are constructed with the shared functionListener, and they are
     * placed in the CENTER and EAST regions respectively.
     */
    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        IAOPanel = new ImageandoptionPanel(fL);
        DDPanel = new DataDisplayPanel(fL);
        add(IAOPanel,borderLayout1.CENTER);
        add(DDPanel,borderLayout1.EAST);
    }
}
