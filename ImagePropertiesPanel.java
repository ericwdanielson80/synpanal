package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

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
public class ImagePropertiesPanel extends JPanel {
    FlowLayout flowLayout1 = new FlowLayout();
    JButton ZButton;
    JButton RButton;
    JButton BButton;
    JButton GButton;
    int ZON = 1;
    int RON = 1;
    int GON = 1;
    int BON = 1;
    ImageIcon ZONIcon;
    ImageIcon ZOFFIcon;
    ImageIcon RONIcon;
    ImageIcon ROFFIcon;
    ImageIcon GONIcon;
    ImageIcon GOFFIcon;
    ImageIcon BONIcon;
    ImageIcon BOFFIcon;

    functionListener fL;
    ZoomLabel zLabel;

    public ImagePropertiesPanel(functionListener fl) {
        fL = fl;
        
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void setPreferredSize(Dimension d)
    {
    	super.setPreferredSize(new Dimension(35,d.height));
    }

    private void jbInit() throws Exception {
        this.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setHgap(2);
        flowLayout1.setVgap(2);
        this.setBorder(BorderFactory.createLineBorder(Color.black));        
        ZONIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("ZoomON.gif"));              
        ZOFFIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("Zoom.gif"));
        RONIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("Red.gif"));
        ROFFIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("RedOFF.gif"));
        GONIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("Green.gif"));
        GOFFIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("GreenOFF.gif"));
        BONIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("Blue.gif"));
        BOFFIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("BlueOFF.gif"));

        ZButton = new JButton(ZOFFIcon);
        ZButton.setToolTipText("activate/deactivate zoom (use scroll wheel or +/-) ");
        RButton = new JButton(RONIcon);
        RButton.setToolTipText("show/hide red channel");
        GButton = new JButton(GONIcon);
        GButton.setToolTipText("show/hide green channel");
        BButton = new JButton(BONIcon);
        BButton.setToolTipText("show/hide blue channel");
        ZButton.setPreferredSize(new Dimension(30,30));
        ZButton.addMouseListener(new ImagePropertiesPanel_ZButton_mouseAdapter(this));
        RButton.setPreferredSize(new Dimension(30,30));
        RButton.addMouseListener(new ImagePropertiesPanel_RButton_mouseAdapter(this));
        GButton.setPreferredSize(new Dimension(30,30));
        GButton.addMouseListener(new ImagePropertiesPanel_GButton_mouseAdapter(this));
        BButton.setPreferredSize(new Dimension(30,30));
        BButton.addMouseListener(new ImagePropertiesPanel_BButton_mouseAdapter(this));
        
        zLabel = new ZoomLabel(fL);
        zLabel.setPreferredSize(new Dimension(30,20));
        
        add(ZButton,0);
        add(zLabel,1);
        add(RButton,2);
        add(GButton,3);
        add(BButton,4);
        

    }

    public void RButton_mouseClicked(MouseEvent e) {
        if(RON == -1)
        {
            RButton.setIcon(RONIcon);
            fL.redColorActive();
        }
        else
        {
            RButton.setIcon(ROFFIcon);
            fL.redColorInActive();
        }
        RON *= -1;
    }

    public void GButton_mouseClicked(MouseEvent e) {
        if(GON == -1)
        {
            GButton.setIcon(GONIcon);
            fL.greenColorActive();
        }
        else
        {
            GButton.setIcon(GOFFIcon);
            fL.greenColorInActive();
        }
        GON *= -1;


    }

    public void BButton_mouseClicked(MouseEvent e) {
        if(BON == -1)
       {
           BButton.setIcon(BONIcon);
           fL.blueColorActive();           
       }
       else
       {
           BButton.setIcon(BOFFIcon);
           fL.blueColorInActive();
       }
       BON *= -1;


    }

    public void ZButton_mouseClicked(MouseEvent e) {
        if(ZON == 1)
      {
          ZButton.setIcon(ZONIcon);
          fL.setZoomMode();
      }
      else
      {
          ZButton.setIcon(ZOFFIcon);
          fL.removeZoomMode();
      }
      ZON *= -1;


    }
}


class ImagePropertiesPanel_RButton_mouseAdapter extends MouseAdapter {
    private ImagePropertiesPanel adaptee;
    ImagePropertiesPanel_RButton_mouseAdapter(ImagePropertiesPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.RButton_mouseClicked(e);
    }
}


class ImagePropertiesPanel_GButton_mouseAdapter extends MouseAdapter {
    private ImagePropertiesPanel adaptee;
    ImagePropertiesPanel_GButton_mouseAdapter(ImagePropertiesPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.GButton_mouseClicked(e);
    }
}


class ImagePropertiesPanel_BButton_mouseAdapter extends MouseAdapter {
    private ImagePropertiesPanel adaptee;
    ImagePropertiesPanel_BButton_mouseAdapter(ImagePropertiesPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.BButton_mouseClicked(e);
    }
}


class ImagePropertiesPanel_ZButton_mouseAdapter extends MouseAdapter {
    private ImagePropertiesPanel adaptee;
    ImagePropertiesPanel_ZButton_mouseAdapter(ImagePropertiesPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.ZButton_mouseClicked(e);
    }
}
