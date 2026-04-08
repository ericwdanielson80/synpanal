package neuron_analyzer;

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
public class ImageandoptionPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    RegionOptionsPanel ROPanel;
    ImagePropertiesPanel IPPanel;
    ThresholdOptionsPanel TOPanel;
    functionListener fL;

    public ImageandoptionPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

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
