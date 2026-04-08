package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.*;
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
public class RegionOptionsPanel extends JPanel {
    JButton SButton; //save
    JButton IButton; //image
    JButton RButton; //regions
    JButton CButton; //count
    JButton NButton; //next image
    JButton PButton; //previous image
    JButton CRButton; //complex region button
    JButton CLButton; //cell region button
    JButton spineButton; //measure spines
    JButton spineButton2; //count spines
    JButton batchButton; //faster graphics
    JButton blindButton; //blind analysis
    JButton removeCellButton;
    JButton removeSpineButton;
    JButton spineRadiusMode;//toggles puncta / spine mode
    JButton printData; //prints data to logfile
    JButton blankButtonF11;
    ImageIcon AutoPrint;
    ImageIcon ManualPrint;
    boolean blind = false;
    functionListener fL;
    FlowLayout flowLayout1 = new FlowLayout();
    public RegionOptionsPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(flowLayout1);
        AutoPrint = new ImageIcon(ImagePropertiesPanel.class.getResource("Print_auto.gif"));
        ManualPrint = new ImageIcon(ImagePropertiesPanel.class.getResource("Print.gif"));
        removeCellButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("RemoveCell.gif")));
        removeCellButton.setToolTipText("remove generic regions");
        removeSpineButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("RemoveSpine.gif")));
        removeSpineButton.setToolTipText("remove spine regions (click outside dendrite region to deactivate)");
        spineRadiusMode = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Spine3.gif")));
        spineRadiusMode.setToolTipText("activate auto puncta removal by proximity to spines (Page UP/Page Down alters radius)");
        printData = new JButton(AutoPrint);
        printData.setToolTipText("left click: manually print data. right click: activate/deactivate auto log mode");
        blankButtonF11 = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Blank.gif")));
        //blankButton = new JButton("Blank.gif");
        SButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Save.gif")));
        SButton.setToolTipText("manually save region information");
        RButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Region.gif")));
        RButton.setToolTipText("add fixed width dendrite regions");
        CButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Puncta.gif")));
        CButton.setToolTipText("count puncta within dendrite regions");
        CRButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("FreRegion.gif")));
        CRButton.setToolTipText("add variable width dendrite regions");
        spineButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Spine.gif")));
        spineButton.setToolTipText("add spine measuring regions (click outside dendrite region to deactivate)");
        spineButton2 = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Spine2.gif")));
        spineButton2.setToolTipText("add spine counting regions (click outside dendrite region to deactivate)");
        batchButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Batch.gif")));
        batchButton.setToolTipText("experimental feature currently disabled");
        NButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Next.gif")));
        NButton.setToolTipText("load next image (region information will automatically be saved)");
        PButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Previous.gif")));
        PButton.setToolTipText("load previous image (region information will automatically be saved)");
        CLButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Cell.gif")));
        CLButton.setToolTipText("add generic region (double-click image to deactivate)");
        blindButton = new JButton(new ImageIcon(ImagePropertiesPanel.class.getResource("Unblind.gif"))); 
        blindButton.setToolTipText("show/hide filenames for blind analysis");
        removeCellButton.setPreferredSize(new Dimension(30,30));
        removeCellButton.setToolTipText("remove generic region");
        removeCellButton.addMouseListener(new RegionOptionsPanel_removeCellButton_mouseAdapter(this));
        removeSpineButton.setPreferredSize(new Dimension(30,30));
        removeSpineButton.setToolTipText("remove spine region (click outside dendrite region to deactivate)");
        removeSpineButton.addMouseListener(new RegionOptionsPanel_removeSpineButton_mouseAdapter(this));
        spineRadiusMode.setPreferredSize(new Dimension(30,30));
        spineRadiusMode.addMouseListener(new RegionOptionsPanel_spineRadiusMode_mouseAdapter(this));
        printData.setPreferredSize(new Dimension(30,30));
        printData.addMouseListener(new RegionOptionsPanel_printData_mouseAdapter(this));
        blankButtonF11.setPreferredSize(new Dimension(30,30));
        SButton.setPreferredSize(new Dimension(30,30));
        SButton.addMouseListener(new RegionOptionsPanel_SButton_mouseAdapter(this));
        RButton.setPreferredSize(new Dimension(30,30));
        RButton.addMouseListener(new RegionOptionsPanel_RButton_mouseAdapter(this));
        CButton.setPreferredSize(new Dimension(30,30));
        CButton.addMouseListener(new RegionOptionsPanel_CButton_mouseAdapter(this));
        //NButton.setPreferredSize(new Dimension(30,30));
        batchButton.setPreferredSize(new Dimension(30,30));
        batchButton.addMouseListener(new RegionOptionsPanel_speedButton_mouseAdapter(this));
        NButton.setPreferredSize(new Dimension(30,30));
        NButton.addMouseListener(new RegionOptionsPanel_NButton_mouseAdapter(this));
        PButton.setPreferredSize(new Dimension(30,30));
        PButton.addMouseListener(new RegionOptionsPanel_PButton_mouseAdapter(this));
        CRButton.setPreferredSize(new Dimension(30,30));
        CRButton.addMouseListener(new RegionOptionsPanel_CRButton_mouseAdapter(this));
        //spineButton.setPreferredSize(new Dimension(30,30));
        spineButton.addMouseListener(new RegionOptionsPanel_spineButton_mouseAdapter(this));
        spineButton.setPreferredSize(new Dimension(30,30));
        spineButton2.addMouseListener(new RegionOptionsPanel_spineButton2_mouseAdapter(this));
        spineButton2.setPreferredSize(new Dimension(30,30));
        blindButton.setPreferredSize(new Dimension(30,30));
        blindButton.addMouseListener(new RegionOptionsPanel_blindButton_mouseAdapter(this));
        CLButton.setPreferredSize(new Dimension(30,30));
        CLButton.addMouseListener(new RegionOptionsPanel_CLButton_mouseAdapter(this));
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setHgap(2);
        flowLayout1.setVgap(2);
        this.setBorder(BorderFactory.createLineBorder(Color.black));        
        this.add(CButton, null); //Escape
        this.add(Box.createRigidArea(new Dimension(20,0)));
        this.add(RButton, null); //F1
        this.add(CRButton, null); //F2
        this.add(CLButton,null); //F3       
        this.add(removeCellButton,null); //F4
        this.add(Box.createRigidArea(new Dimension(20,0)));
        this.add(spineButton2,null); //F5
        this.add(spineButton, null); //F6
        this.add(removeSpineButton, null); //F7
        this.add(spineRadiusMode,null); //F8
        this.add(Box.createRigidArea(new Dimension(20,0)));
        this.add(blindButton,null); //F9
        this.add(printData,null); //F10
        this.add(SButton,null);	//F11
        this.add(batchButton,null);	//F12
        this.add(Box.createRigidArea(new Dimension(20,0)));
        this.add(PButton,null);
        this.add(NButton,null);
    }

    public void SButton_mousePressed(MouseEvent e) {
    	fL.saveRegionInformation();
    }

    public void CButton_mousePressed(MouseEvent e) {
        fL.countPuncta();
    }

    public void RButton_mousePressed(MouseEvent e) {
        fL.addDendrite();        
    }
    
    public void NButton_mousePressed(MouseEvent e) {
        fL.nextImage();
        
    }
    
    public void speedButton_mousePressed(MouseEvent e) {
        fL.batchProcess(); //toggleSpeed();
        
    }
    
    public void blindButton_mousePressed(MouseEvent e) {
        fL.toggleBlind();               
        
    }
    
    public void toggleBlind()
    {
    	if(blind)
        {
        	blindButton.setIcon(new ImageIcon(ImagePropertiesPanel.class.getResource("Unblind.gif")));
        	blind = false;
        }
        else
        {
        	blindButton.setIcon(new ImageIcon(ImagePropertiesPanel.class.getResource("Blind.gif")));
        	blind = true;
        }
        repaint();
    }
    
    public void CRButton_mousePressed(MouseEvent e) {
    	fL.addComplexDendrite();
    }
    
    public void spineButton_mousePressed(MouseEvent e) {
    	fL.addSpine();
    }
    
    public void spineButton2_mousePressed(MouseEvent e) {
    	fL.addSpine2();
    }
    
    public void removeSpineButton_mousePressed(MouseEvent e) {
    	fL.removeSpine();
    }
    
    public void spineRadiusMode_mousePressed(MouseEvent e) {
    	fL.toggleRadiusView();
    }
    
    public void printData_mousePressed(MouseEvent e) {
    	if(e.getButton() == MouseEvent.BUTTON1)
    	{
    		fL.printData();    		
    	}
    	else if(e.getButton() == MouseEvent.BUTTON3)
    	{
    		if(fL.isAutoLog())
    			{    			
    			printData.setIcon(ManualPrint);    			
    			fL.toggleAutoLog();
    			}
    		else
    		{    			
    			printData.setIcon(AutoPrint);
    			fL.toggleAutoLog();
    		}
    			
    	}
    }
    
    public void removeCellButton_mousePressed(MouseEvent e) {
    	fL.removeCell();
    }
    
    public void CLButton_mousePressed(MouseEvent e) {
    	fL.addCell();
    }
    
    public void PButton_mousePressed(MouseEvent e) {
    	fL.previousImage();
    }
    
    public void setPreferredSize(Dimension d)
    {
    	super.setPreferredSize(new Dimension(d.width,35));
    }
}


class RegionOptionsPanel_SButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_SButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.SButton_mousePressed(e);
    }
}


class RegionOptionsPanel_CButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_CButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.CButton_mousePressed(e);
    }
}

class RegionOptionsPanel_blindButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_blindButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.blindButton_mousePressed(e);
    }
}


class RegionOptionsPanel_RButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_RButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.RButton_mousePressed(e);
    }
}

class RegionOptionsPanel_NButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_NButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.NButton_mousePressed(e);
    }
}

class RegionOptionsPanel_speedButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_speedButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.speedButton_mousePressed(e);
    }
}

class RegionOptionsPanel_CRButton_mouseAdapter extends MouseAdapter {
    private RegionOptionsPanel adaptee;
    RegionOptionsPanel_CRButton_mouseAdapter(RegionOptionsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.CRButton_mousePressed(e);
    }
}
    class RegionOptionsPanel_spineButton_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_spineButton_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.spineButton_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_spineButton2_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_spineButton2_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.spineButton2_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_CLButton_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_CLButton_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.CLButton_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_PButton_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_PButton_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.PButton_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_removeSpineButton_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_removeSpineButton_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.removeSpineButton_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_removeCellButton_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_removeCellButton_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.removeCellButton_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_spineRadiusMode_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_spineRadiusMode_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.spineRadiusMode_mousePressed(e);
        }
    }
    
    class RegionOptionsPanel_printData_mouseAdapter extends MouseAdapter {
        private RegionOptionsPanel adaptee;
        RegionOptionsPanel_printData_mouseAdapter(RegionOptionsPanel adaptee) {
            this.adaptee = adaptee;
        }

        public void mousePressed(MouseEvent e) {
            adaptee.printData_mousePressed(e);
        }
    }
