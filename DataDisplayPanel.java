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
public class DataDisplayPanel extends JPanel {
    DataShowHidePanel DSHP;
    //JButton SHButton;
    RegionGroupTab RGT;
    BorderLayout borderLayout1 = new BorderLayout();
    functionListener fL;
    public DataDisplayPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        DSHP = new DataShowHidePanel();
        RGT = new RegionGroupTab(new String[]{"Ctrl","S-SCAM"},fL);
        RGT.setPreferredSize(new Dimension(300, 500));
        this.add(RGT, java.awt.BorderLayout.CENTER); //add(SHButton,borderLayout1.EAST);
        this.add(DSHP, java.awt.BorderLayout.EAST);
    }
}
