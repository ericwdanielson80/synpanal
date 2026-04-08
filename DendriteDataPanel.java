package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.awt.event.*;

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
public class DendriteDataPanel extends JPanel implements AdjustmentListener {
    BorderLayout borderLayout1 = new BorderLayout();
    DataTablePanel dataPanel;
    functionListener fL;
    String columns[];
    int[] layout;
    Group myGroup;
    int myColor;
    JScrollPane scrollPane;
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
    
    public void updateInfo(DendriteGroupData dgd,DendriteGroupData old)
    {
    	for(int k = 0; k < old.bC.length; k++)
    	{
    		dgd.bC[k].isIgnored = old.bC[k].isIgnored;
    	}
    }
    
    public boolean[] getIgnoredList()
    {       	
    	return dataPanel.tableViews[0].getIgnoredArray();
    }
    
    public void repaintTableData()
    {
    	dataPanel.repaint();
    }
    
    public void loadDendriteIgnoreList(boolean[] list)
    {
    	((DendritePunctaTableData)dataPanel.tableViews[0]).loadDendriteIgnoreList(list);
    }
    
    public void checkSize()
    {
    	dataPanel.checkSize();
    }
    
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
    	int value = e.getValue();
    	fL.setMiddlePanelScrollBar(value);
    }
    
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
