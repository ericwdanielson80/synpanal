package neuron_analyzer;

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
public class DataPanelContainer extends JPanel {
    GridLayout gridLayout1 = new GridLayout();
    NeuronDataPanel NDP;
    DendriteDataPanel DDP;
    PunctaDataPanel PDP;
    functionListener fL;
    Group myGroup; //what tab it belongs to
    int myColor;
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
  
    
    public void printData(PrintWriter pW,String fileName,String tcx, String color, int threshold, LogInfo lI)
    {
    	NDP.printData(pW,fileName, tcx, color, threshold,lI);
    }
    
    public boolean[] getDendriteIgnoredList()
    {
    	return DDP.getIgnoredList();
    }
    
    public boolean[] getPunctaIgnoreList()
    {
    	return PDP.getPunctaIgnoreList();
    }
    
    public void repaintDataPane()
    {
    	checkSize();
    	
    	NDP.repaint();
    	DDP.repaint();    	    	
    	PDP.repaint();
    }
    
    public void loadPunctaIgnoreList(boolean[] list)
    {
    	PDP.loadPunctaIgnoreList(list);
    }
    
    public void loadDendriteIgnoreList(boolean[] list)
    {
    	DDP.loadDendriteIgnoreList(list);
    }
    
    public void checkSize()
    {
    	NDP.checkSize();
    	DDP.checkSize();
    	PDP.checkSize();
    }
    
    public void setMiddlePanelScrollBar(int value)
    {
    	DDP.setMiddlePanelScrollBar(value);
    }
    
    public void setBottomPanelScrollBar(int value)
    {
    	PDP.setBottomPanelScrollBar(value);
    }
}
