package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.JPanel;
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
public class InternalMFPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    ImageandoptionPanel IAOPanel;
    DataDisplayPanel DDPanel;
    functionListener fL;
    public InternalMFPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        IAOPanel = new ImageandoptionPanel(fL);
        DDPanel = new DataDisplayPanel(fL);
        add(IAOPanel,borderLayout1.CENTER);
        add(DDPanel,borderLayout1.EAST);
    }
}
