package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * File: RegionOptionsPanel.java
 *
 * Defines the toolbar of icon buttons used by the neuron_analyzer application
 * to drive region-related actions on the active image (saving region data,
 * adding fixed and variable width dendrite regions, generic cell regions,
 * spine measurement and counting regions, puncta counting, navigation between
 * images, toggling blind analysis, toggling automatic logging, and toggling
 * the auto puncta-removal-by-proximity mode). Each button is wired to a
 * {@link functionListener} which delegates the actual work to the appropriate
 * controller in the application.
 *
 * This file contains the following classes:
 *
 *  - RegionOptionsPanel: the JPanel that lays out the toolbar buttons, sets
 *    up their icons/tool tips, and exposes the *_mousePressed handler methods
 *    that forward to the functionListener.
 *
 *  - Fifteen small package-private MouseAdapter inner classes (one per
 *    button): RegionOptionsPanel_SButton_mouseAdapter,
 *    RegionOptionsPanel_CButton_mouseAdapter,
 *    RegionOptionsPanel_blindButton_mouseAdapter,
 *    RegionOptionsPanel_RButton_mouseAdapter,
 *    RegionOptionsPanel_NButton_mouseAdapter,
 *    RegionOptionsPanel_speedButton_mouseAdapter,
 *    RegionOptionsPanel_CRButton_mouseAdapter,
 *    RegionOptionsPanel_spineButton_mouseAdapter,
 *    RegionOptionsPanel_spineButton2_mouseAdapter,
 *    RegionOptionsPanel_CLButton_mouseAdapter,
 *    RegionOptionsPanel_PButton_mouseAdapter,
 *    RegionOptionsPanel_removeSpineButton_mouseAdapter,
 *    RegionOptionsPanel_removeCellButton_mouseAdapter,
 *    RegionOptionsPanel_spineRadiusMode_mouseAdapter, and
 *    RegionOptionsPanel_printData_mouseAdapter. Each holds a reference to
 *    the enclosing RegionOptionsPanel and forwards mousePressed events to
 *    the matching X_mousePressed method on it.
 *
 * RegionOptionsPanel summary: Swing toolbar panel containing all
 * region-related action buttons for the neuron_analyzer main window. Each
 * button is tied via a MouseAdapter to a handler method on this panel that
 * delegates to a shared functionListener which performs the actual work
 * (saving, adding regions, counting puncta, navigating images, etc.).
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
    /**
     * Constructs the panel, storing the supplied functionListener as the
     * delegate for all button actions and then invoking jbInit to build the UI.
     */
    public RegionOptionsPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Builds and lays out the toolbar's buttons.
     *
     * <p>Creates each JButton with its image icon (loaded through
     * ImagePropertiesPanel's class loader), sets a tool tip describing the
     * action (save, add fixed/variable width dendrite regions, add generic
     * cell regions, count puncta, add or remove spine regions, toggle auto
     * puncta removal by proximity to spines, toggle blind analysis, print or
     * auto-log data, advance to next/previous image, etc.), gives each button
     * a 30x30 preferred size, and attaches the corresponding
     * RegionOptionsPanel_*_mouseAdapter so mouse presses are routed back to
     * this panel's handler methods.</p>
     *
     * <p>Buttons are then added to the panel in groups separated by 20-pixel
     * rigid horizontal struts. The groups correspond to the keyboard
     * shortcuts F1-F12 noted in the inline comments (puncta/region actions,
     * spine actions, blind/print/save/batch, and image navigation).</p>
     */
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

    /**
     * Handles a press on the Save button by asking the functionListener to
     * save the currently defined region information.
     */
    public void SButton_mousePressed(MouseEvent e) {
    	fL.saveRegionInformation();
    }

    /**
     * Handles a press on the Count button by triggering puncta counting
     * within the current dendrite regions.
     */
    public void CButton_mousePressed(MouseEvent e) {
        fL.countPuncta();
    }

    /**
     * Handles a press on the Region button by starting fixed-width dendrite
     * region addition mode.
     */
    public void RButton_mousePressed(MouseEvent e) {
        fL.addDendrite();        
    }
    
    /**
     * Handles a press on the Next button by loading the next image in the
     * current sequence.
     */
    public void NButton_mousePressed(MouseEvent e) {
        fL.nextImage();
        
    }
    
    /**
     * Handles a press on the batch / speed button by invoking the batch
     * processing hook on the functionListener.
     */
    public void speedButton_mousePressed(MouseEvent e) {
        fL.batchProcess(); //toggleSpeed();
        
    }
    
    /**
     * Handles a press on the blind button by toggling blind-analysis mode
     * on the functionListener.
     */
    public void blindButton_mousePressed(MouseEvent e) {
        fL.toggleBlind();               
        
    }
    
    /**
     * Flips the local blind state and swaps the blind button's icon between
     * the "blind" and "unblind" images, repainting to reflect the change.
     */
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
    
    /**
     * Handles a press on the complex region button by starting variable-width
     * (freehand) dendrite region addition mode.
     */
    public void CRButton_mousePressed(MouseEvent e) {
    	fL.addComplexDendrite();
    }
    
    /**
     * Handles a press on the spine-measure button by starting spine
     * measurement region addition mode.
     */
    public void spineButton_mousePressed(MouseEvent e) {
    	fL.addSpine();
    }
    
    /**
     * Handles a press on the spine-count button by starting spine counting
     * region addition mode.
     */
    public void spineButton2_mousePressed(MouseEvent e) {
    	fL.addSpine2();
    }
    
    /**
     * Handles a press on the remove-spine button by entering spine removal
     * mode.
     */
    public void removeSpineButton_mousePressed(MouseEvent e) {
    	fL.removeSpine();
    }
    
    /**
     * Handles a press on the spine radius button by toggling the auto puncta
     * removal / spine radius view on the functionListener.
     */
    public void spineRadiusMode_mousePressed(MouseEvent e) {
    	fL.toggleRadiusView();
    }
    
    /**
     * Handles clicks on the print data button. A left click prints the
     * current data immediately; a right click toggles auto log mode and
     * swaps the button's icon between the manual and auto print images
     * accordingly.
     */
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
    
    /**
     * Handles a press on the remove-cell button by entering generic region
     * removal mode.
     */
    public void removeCellButton_mousePressed(MouseEvent e) {
    	fL.removeCell();
    }
    
    /**
     * Handles a press on the cell button by starting generic cell / region
     * addition mode.
     */
    public void CLButton_mousePressed(MouseEvent e) {
    	fL.addCell();
    }
    
    /**
     * Handles a press on the Previous button by loading the previous image
     * in the current sequence.
     */
    public void PButton_mousePressed(MouseEvent e) {
    	fL.previousImage();
    }
    
    /**
     * Overrides the preferred size so the panel always reports a fixed
     * height of 35 pixels regardless of the requested height.
     */
    public void setPreferredSize(Dimension d)
    {
    	super.setPreferredSize(new Dimension(d.width,35));
    }
}
