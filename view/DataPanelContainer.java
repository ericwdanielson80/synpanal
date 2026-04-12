package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;
import java.io.*;
import javax.swing.JPanel;
import java.awt.GridLayout;

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
 * Holder panel that stacks the three data displays for a single
 * (group, color) tab: NeuronDataPanel on top, DendriteDataPanel in the
 * middle and PunctaDataPanel on the bottom. Presents helper methods that
 * forward repaint, ignore-list accessors and scroll-bar positioning down
 * to the appropriate child panel.
 */
public class DataPanelContainer extends JPanel {
    GridLayout gridLayout1 = new GridLayout();
    public NeuronDataPanel NDP;
    public DendriteDataPanel DDP;
    public PunctaDataPanel PDP;
    functionListener fL;
    Group myGroup; //what tab it belongs to
    int myColor;
    /**
     * Saves the controller, owning group tab and color channel, then builds
     * the three child panels via jbInit. The fl parameter is the shared
     * functionListener forwarded to each child. Group is the cell-type tab
     * this container belongs to (stored in myGroup) and Color is the RGB
     * channel index (stored in myColor). jbInit exceptions are caught and
     * their stack trace logged.
     */
    public DataPanelContainer(functionListener fl,Group Group,int Color) {
        fL = fl;
        myGroup = Group;
        myColor = Color;
        try {
            jbInit(Group,Color);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Configures a one-column, three-row GridLayout and creates the
     * NeuronDataPanel, DendriteDataPanel and PunctaDataPanel for the
     * supplied (Group, Color) and adds them to the panel in order.
     */
    private void jbInit(Group Group, int Color) throws Exception {
        this.setLayout(gridLayout1);
        gridLayout1.setColumns(1);
        gridLayout1.setRows(3);
        NDP = new NeuronDataPanel(fL,Group, Color);
        DDP = new DendriteDataPanel(fL,Group,Color);
        PDP = new PunctaDataPanel(fL,Group,Color);
        add(NDP,0);
        add(DDP,1);
        add(PDP,2);
        
    }
  
    
    /**
     * Delegates to NDP.printData to write the neuron-level data for the
     * current image. The pW parameter is the PrintWriter to emit into;
     * fileName, tcx (group/tab name), color and threshold form the leader
     * columns; and lI is the LogInfo controlling which channels are
     * reported.
     */
    public void printData(PrintWriter pW,String fileName,String tcx, String color, int threshold, LogInfo lI)
    {
    	NDP.printData(pW,fileName, tcx, color, threshold,lI);
    }
    
    /**
     * Returns the dendrite panel's boolean list describing which dendrites
     * are currently marked ignored, forwarded from DDP.getIgnoredList.
     */
    public boolean[] getDendriteIgnoredList()
    {
    	return DDP.getIgnoredList();
    }

    /**
     * Returns the puncta panel's boolean list of currently ignored puncta,
     * forwarded from PDP.getPunctaIgnoreList.
     */
    public boolean[] getPunctaIgnoreList()
    {
    	return PDP.getPunctaIgnoreList();
    }

    /**
     * Refreshes all three child panels after ensuring their layouts are
     * sized correctly via checkSize, then repaints NDP, DDP and PDP in
     * turn.
     */
    public void repaintDataPane()
    {
    	checkSize();
    	
    	NDP.repaint();
    	DDP.repaint();    	    	
    	PDP.repaint();
    }
    
    /**
     * Pushes a previously saved puncta-ignore list into the PunctaDataPanel.
     */
    public void loadPunctaIgnoreList(boolean[] list)
    {
    	PDP.loadPunctaIgnoreList(list);
    }

    /**
     * Pushes a previously saved dendrite-ignore list into the
     * DendriteDataPanel.
     */
    public void loadDendriteIgnoreList(boolean[] list)
    {
    	DDP.loadDendriteIgnoreList(list);
    }

    /**
     * Asks each child panel (NDP, DDP, PDP) to recompute its own size based
     * on the current data contents.
     */
    public void checkSize()
    {
    	NDP.checkSize();
    	DDP.checkSize();
    	PDP.checkSize();
    }
    
    /**
     * Forwards a scroll-bar value to the middle (dendrite) panel, so that
     * programmatic scrolling stays in sync.
     */
    public void setMiddlePanelScrollBar(int value)
    {
    	DDP.setMiddlePanelScrollBar(value);
    }

    /**
     * Forwards a scroll-bar value to the bottom (puncta) panel.
     */
    public void setBottomPanelScrollBar(int value)
    {
    	PDP.setBottomPanelScrollBar(value);
    }
}
