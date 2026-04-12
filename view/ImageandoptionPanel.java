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
 * Container panel that frames the central image area with option bars on
 * three of its edges: a RegionOptionsPanel across the top, an
 * ImagePropertiesPanel down the left side and a ThresholdOptionsPanel
 * across the bottom. The center currently holds a placeholder JTextArea
 * that is intended to be replaced by the TiffPanel at runtime.
 */
public class ImageandoptionPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    RegionOptionsPanel ROPanel;
    ImagePropertiesPanel IPPanel;
    ThresholdOptionsPanel TOPanel;
    functionListener fL;

    /**
     * Saves the shared controller reference and invokes jbInit to lay out
     * the child panels. The fl parameter is the application-wide
     * functionListener propagated to each child options panel. Any Swing
     * setup exceptions are caught and their stack trace printed.
     */
    public ImageandoptionPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Instantiates and lays out the three option panels (ROPanel on the top
     * edge, IPPanel on the left edge and TOPanel on the bottom edge) plus a
     * temporary JTextArea in the center region. Each panel is given fixed
     * minimum/maximum/preferred sizes so the BorderLayout keeps the image
     * area a consistent 700x700 size. Throws Exception to match the
     * project's jbInit convention.
     */
    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        ROPanel = new RegionOptionsPanel(fL);
        IPPanel = new ImagePropertiesPanel(fL);
        TOPanel = new ThresholdOptionsPanel(fL);
        this.setPreferredSize(new Dimension(700, 700));
        ROPanel.setMaximumSize(new Dimension(700, 35));
        ROPanel.setMinimumSize(new Dimension(700, 35));
        ROPanel.setPreferredSize(new Dimension(700, 35));
        IPPanel.setMaximumSize(new Dimension(34, 700));
        IPPanel.setMinimumSize(new Dimension(34, 700));
        IPPanel.setPreferredSize(new Dimension(34, 700));
        TOPanel.setMinimumSize(new Dimension(700, 36));
        TOPanel.setPreferredSize(new Dimension(700, 36));
        add(TOPanel,BorderLayout.SOUTH);
        add(ROPanel,borderLayout1.NORTH);
        add(IPPanel,borderLayout1.WEST);
        JTextArea tmp = new JTextArea();
        tmp.setPreferredSize(new Dimension(600,600));
        //TiffPanel tmp = new TiffPanel();
        add(tmp,borderLayout1.CENTER);


    }
}
