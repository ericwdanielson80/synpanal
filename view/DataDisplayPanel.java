package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/**
 * Top-level data-display panel that composes the region-group tabbed container
 * (RegionGroupTab) that hosts per-group data panels with a DataShowHidePanel
 * column on the east side for toggling which data rows are visible. It is a
 * thin wrapper that wires the two children together using BorderLayout and
 * forwards the shared functionListener controller reference to them.
 */
public class DataDisplayPanel extends JPanel {
    DataShowHidePanel DSHP;
    //JButton SHButton;
    RegionGroupTab RGT;
    BorderLayout borderLayout1 = new BorderLayout();
    functionListener fL;
    /**
     * Constructs the data-display panel bound to the supplied controller.
     * The fl parameter is the application-wide functionListener that child
     * panels use to request repaints and query application state. The
     * constructor stores the reference and delegates physical layout to
     * jbInit, printing any exception stack trace if initialization fails.
     */
    public DataDisplayPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Builds and arranges the child components. Sets the panel's layout to
     * borderLayout1, creates the DataShowHidePanel (DSHP), constructs the
     * RegionGroupTab (RGT) pre-seeded with the two initial groups "Ctrl" and
     * "S-SCAM" (these are the default colored tabs the app opens with) and
     * sizes it, then adds RGT to CENTER and DSHP to EAST. The method is
     * declared to throw Exception because jbInit-style initializers in this
     * project propagate any Swing setup failure up to the caller.
     */
    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        DSHP = new DataShowHidePanel();
        RGT = new RegionGroupTab(new String[]{"Ctrl","S-SCAM"},fL);
        RGT.setPreferredSize(new Dimension(300, 500));
        this.add(RGT, java.awt.BorderLayout.CENTER); //add(SHButton,borderLayout1.EAST);
        this.add(DSHP, java.awt.BorderLayout.EAST);
    }
}
