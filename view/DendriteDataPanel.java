package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
       
import java.awt.BorderLayout;
        
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.awt.event.*;
    	
/**
 * Scrollable per-dendrite data view hosting a DataTablePanel that can
 * switch between four tableData views: dendrite puncta summary,
 * dendrite spine summary, (reserved null) and colocalization
 * (A side). The panel owns a horizontal scroll bar whose motion is
 * forwarded to the main functionListener so other sibling panels can
 * stay in sync, and it exposes helpers to load dendrite ignore lists
 * and repaint when the underlying data changes.
 */
public class DendriteDataPanel extends JPanel implements AdjustmentListener {
    BorderLayout borderLayout1 = new BorderLayout();
    public DataTablePanel dataPanel;
    functionListener fL;
    String columns[];
    int[] layout;
    Group myGroup;
    int myColor;
    JScrollPane scrollPane;
    /**
     * Stores the functionListener fl, the Group and Color, then builds
     * the initial DendritePunctaTableData d (used to seed columns and
     * layout) and the DataTablePanel dataPanel. It populates a
     * four-entry tableView array (0 puncta, 1 spine, 2 null,
     * 3 colocalization A) and wires it into the DataTablePanel before
     * invoking jbInit to finish layout. The parameter fl is the main
     * listener, Group is the analysis group and Color is the channel
     * index (0 Red, 1 Green, 2 Blue).
     */
    public DendriteDataPanel(functionListener fl,Group Group,int Color) {
        fL = fl;       
       
        myGroup = Group;
        myColor = Color;
        tableData d = new DendritePunctaTableData(fl,Group,Color);
        columns = d.getTitles();
        layout = d.getLayout();
        dataPanel = new DataTablePanel(d.getTitles(),d.getLayout(),fl);        
        tableData[] tableView = new tableData[4];
        tableView[0] = d;
        tableView[1] = new DendriteSpineTableData(fl,Group,Color);
        tableView[2] = null;
        tableView[3] = new ColocalizationATableData(fl,Group,Color);
        dataPanel.setViews(tableView);
        dataPanel.setTableData(d);
        
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Finalises the UI: sets a tooltip on dataPanel, installs the
     * border layout, wraps dataPanel in a JScrollPane that always
     * shows scrollbars as needed, and subscribes this panel as an
     * AdjustmentListener on the horizontal bar so scroll events can
     * be broadcast.
     */
    private void jbInit() throws Exception {
    	dataPanel.setToolTipText("data per dendrite");
        this.setLayout(borderLayout1);
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        //dataPanel = new DataTablePanel(columns,layout,fL);
        scrollPane = new JScrollPane(dataPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        add(scrollPane,borderLayout1.CENTER);
        //add(new DataTablePanel(),borderLayout1.CENTER);
    }
    
    /**
     * Hook invoked when the group's dendrite data has been
     * reassigned. The body is currently just repaint()- the
     * commented-out block shows the former logic which forwarded the
     * listener and transferred the old ignore state. Parameters dgd
     * and old are the new and previous DendriteGroupData snapshots.
     */
    public void setData(DendriteGroupData dgd,DendriteGroupData old)
    {       	
    	
    	/*if(dgd != null)
    	{
    		dgd.punctaData.setListener(this);
    		if(old != null)
    			updateInfo(dgd,old);
    	}
    	dataPanel.setTableData(dgd);*/    	
    	repaint();
    }
    
    /**
     * Copies the ignored flag from each boolean container in old.bC
     * onto dgd.bC so the new dendrite snapshot inherits the previous
     * ignored selections.
     */
    public void updateInfo(DendriteGroupData dgd,DendriteGroupData old)
    {
    	for(int k = 0; k < old.bC.length; k++)
    	{
    		dgd.bC[k].isIgnored = old.bC[k].isIgnored;
    	}
    }
    
    /**
     * Returns the current dendrite ignore-mask as reported by the
     * primary (puncta) tableData view.
     */
    public boolean[] getIgnoredList()
    {
    	return dataPanel.tableViews[0].getIgnoredArray();
    }
    
    /** Asks the embedded DataTablePanel to repaint its cells. */
    public void repaintTableData()
    {
    	dataPanel.repaint();
    }
    
    /**
     * Forwards a previously saved dendrite ignore mask (list) to the
     * DendritePunctaTableData so each dendrite's isIgnored flag is
     * restored.
     */
    public void loadDendriteIgnoreList(boolean[] list)
    {
    	((DendritePunctaTableData)dataPanel.tableViews[0]).loadDendriteIgnoreList(list);
    }
    
    /** Delegates to DataTablePanel.checkSize so layout matches current row count. */
    public void checkSize()
    {
    	dataPanel.checkSize();
    }
    
    /**
     * AdjustmentListener callback that reads the horizontal scroll
     * position out of the event e and pushes it through the
     * functionListener so sibling middle panels stay synchronised.
     */
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
    	int value = e.getValue();
    	fL.setMiddlePanelScrollBar(value);
    }
    
    /**
     * Applies an externally supplied horizontal scroll position value
     * to this panel's scroll bar while temporarily detaching this
     * instance as a listener to avoid a feedback loop that would
     * rebroadcast the change.
     */
    public void setMiddlePanelScrollBar(int value)
    {
    	if(scrollPane.getHorizontalScrollBar().getValue() != value)
    	{
    		scrollPane.getHorizontalScrollBar().removeAdjustmentListener(this);
    		scrollPane.getHorizontalScrollBar().setValue(value);
    		scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
    	}
    }
    
    
}
    