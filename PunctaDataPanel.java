package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;
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
public class PunctaDataPanel extends JPanel implements AdjustmentListener{
    BorderLayout borderLayout1 = new BorderLayout();
    DataTablePanel dataPanel;
    functionListener fL;
    Group myGroup;
    int myColor;
    JScrollPane scrollPane;
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

    private void jbInit() throws Exception {
    	dataPanel.setToolTipText("individual data");
        this.setLayout(borderLayout1);
        this.setBorder(BorderFactory.createLineBorder(Color.black));   
        scrollPane = new JScrollPane(dataPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        add(scrollPane,borderLayout1.CENTER);
    }

    public void setData(PunctaGroupData dgd)
    {    	
        //dataPanel.setTableData(dgd);
        //may need to addpaint listener
        //repaint();
    }
    
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
    	int value = e.getValue();
    	fL.setBottomPanelScrollBar(value);
    }
    
    public boolean[] getPunctaIgnoreList()
    {
    	return dataPanel.tableViews[0].getIgnoredArray();    	
    }
    
    public void loadPunctaIgnoreList(boolean[] list)
    {
    	((PunctaTableData)dataPanel.tableViews[0]).loadPunctaIgnoreList(list);
    }
    
    public void checkSize()
    {
    	dataPanel.checkSize();
    }
    
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
