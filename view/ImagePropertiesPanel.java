package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * File-level overview: compact JPanel toolbar that lets the user toggle
 * zoom mode and show/hide the red, green, and blue channels of the
 * currently displayed image. Contains five classes: ImagePropertiesPanel is
 * the main panel with the four toggle buttons and a ZoomLabel, while
 * ImagePropertiesPanel_RButton_mouseAdapter,
 * ImagePropertiesPanel_GButton_mouseAdapter,
 * ImagePropertiesPanel_BButton_mouseAdapter, and
 * ImagePropertiesPanel_ZButton_mouseAdapter are thin MouseAdapter bridges
 * that forward click events to the panel for each of the four buttons.
 */
/**
 * JPanel hosting the red/green/blue channel visibility toggles and the
 * zoom-mode toggle, with the current zoom level shown via a ZoomLabel.
 * Each click flips the corresponding on/off state and notifies the
 * functionListener so the image display can redraw.
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

    /** Wires up the shared functionListener and runs jbInit to build the four buttons and zoom label. */
    public ImagePropertiesPanel(functionListener fl) {
        fL = fl;
        
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /** Forces a narrow 35-pixel-wide preferred size regardless of the caller's requested width. */
    public void setPreferredSize(Dimension d)
    {
    	super.setPreferredSize(new Dimension(35,d.height));
    }

    /** Creates the icons, buttons, and ZoomLabel, attaches mouse adapters to the four buttons, and adds them to the panel in left-to-right order. */
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

    /** Toggles the red channel visibility, updating the button icon and notifying the functionListener to activate or deactivate the red channel. */
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

    /** Toggles the green channel visibility, updating the button icon and notifying the functionListener to activate or deactivate the green channel. */
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

    /** Toggles the blue channel visibility, updating the button icon and notifying the functionListener to activate or deactivate the blue channel. */
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

    /** Toggles zoom mode, updating the button icon and calling setZoomMode or removeZoomMode on the functionListener. */
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
