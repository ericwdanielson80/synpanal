package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
    
import java.awt.BorderLayout;
    
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
    
/**
 * Scrollable individual-object data view that hosts a DataTablePanel
 * with four tableData views: individual puncta, individual spines,
 * individual cell bodies, and colocalization (B side). Each tableData
 * is subscribed to its appropriate event stream (count/spine/cell) so
 * it refreshes when the underlying analysis state changes. The
 * panel's horizontal scroll bar is mirrored across all sibling
 * PunctaDataPanels through the functionListener.
 */
public class PunctaDataPanel extends JPanel implements AdjustmentListener{
    BorderLayout borderLayout1 = new BorderLayout();
    public DataTablePanel dataPanel;
    functionListener fL;
    Group myGroup;
    int myColor;
    JScrollPane scrollPane;
    /**
     * Stores fl/Group/Color and builds the four tableData views.
     * The primary PunctaTableData d is created first and registered
     * as a CountEventListener; the DataTablePanel is then built and
     * loaded with a four-entry tableView array (0 puncta, 1 spine,
     * 2 cell, 3 colocalization B), and the spine/cell views are
     * subscribed to their own event streams. Finally jbInit is
     * called to finish the layout. Parameters: fl is the main
     * functionListener, Group is the owning analysis group, and
     * Color is the channel index (0 Red, 1 Green, 2 Blue).
     */
    public PunctaDataPanel(functionListener fl, Group Group,int Color) {
    	 fL = fl;                
         myGroup = Group;
         myColor = Color;
         tableData d = new PunctaTableData(fl,Group,Color); 
         fL.addCountEventListener((PunctaTableData)d);
         dataPanel = new DataTablePanel(d.getTitles(),d.getLayout(),fl);        
         tableData[] tableView = new tableData[4];
         tableView[0] = d;
         tableView[1] = new SpineTableData(fl,Group,Color);
         fL.addSpineEventListener((SpineTableData)tableView[1]);
         tableView[2] = new CellTableData(fl,Group,Color);
         fL.addCellEventListener((CellEventListener)tableView[2]);
         tableView[3] = new ColocalizationBTableData(fl,Group,Color);
         dataPanel.setViews(tableView);
         dataPanel.setTableData(d);
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Sets a tooltip on dataPanel, installs the BorderLayout,
     * wraps dataPanel in a JScrollPane with as-needed horizontal/
     * vertical bars, and subscribes this class as an
     * AdjustmentListener on the horizontal bar to broadcast scroll
     * position changes.
     */
    private void jbInit() throws Exception {
    	dataPanel.setToolTipText("individual data");
        this.setLayout(borderLayout1);
        this.setBorder(BorderFactory.createLineBorder(Color.black));   
        scrollPane = new JScrollPane(dataPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        add(scrollPane,borderLayout1.CENTER);
    }
    
    /**
     * Legacy hook retained for API compatibility. The body is
     * commented out; dgd is the new PunctaGroupData that would
     * previously have been installed as the active table data.
     */
    public void setData(PunctaGroupData dgd)
    {    	
        //dataPanel.setTableData(dgd);
        //may need to addpaint listener
        //repaint();
    }
    
    /**
     * AdjustmentListener callback that reads the new scroll bar
     * position from e and pushes it through the functionListener so
     * sibling bottom panels stay synchronised.
     */
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
    	int value = e.getValue();
    	fL.setBottomPanelScrollBar(value);
    }
    
    /** Returns the ignored-flag array from the primary PunctaTableData. */
    public boolean[] getPunctaIgnoreList()
    {
    	return dataPanel.tableViews[0].getIgnoredArray();    	
    }
    
    /** Delegates a saved ignore mask (list) to the PunctaTableData so each puncta's ignored flag is restored. */
    public void loadPunctaIgnoreList(boolean[] list)
    {
    	((PunctaTableData)dataPanel.tableViews[0]).loadPunctaIgnoreList(list);
    }
    
    /** Forwards a size refresh to the embedded DataTablePanel. */
    public void checkSize()
    {
    	dataPanel.checkSize();
    }
    
    /**
     * Applies an externally supplied scroll position value to the
     * horizontal scroll bar, temporarily removing this listener so
     * setting the value does not immediately rebroadcast.
     */
    public void setBottomPanelScrollBar(int value)
    {
    	if(scrollPane.getHorizontalScrollBar().getValue() != value)
    	{
    		scrollPane.getHorizontalScrollBar().removeAdjustmentListener(this);
    		scrollPane.getHorizontalScrollBar().setValue(value);
    		scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
    	}
    }
        
        
}
