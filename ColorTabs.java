package neuron_analyzer;

import javax.swing.*;
import java.io.*;
import javax.swing.event.*;


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
public class ColorTabs extends JTabbedPane implements ChangeListener{
	Group myGroup;
    DataPanelContainer Red;
    DataPanelContainer Green;
    DataPanelContainer Blue;
    functionListener fL;
    public ColorTabs(functionListener fl,int Group) {
    	
        fL = fl;
        myGroup = new Group(Group);
        addChangeListener(this);
        try {
            jbInit(myGroup);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit(Group Group) throws Exception {
        Red = new DataPanelContainer(fL,Group,0);
        Green = new DataPanelContainer(fL,Group,1);
        Blue = new DataPanelContainer(fL,Group,2);

        addTab("Red",Red);
        addTab("Green",Green);
        addTab("Blue",Blue);
    }
    
    public void printData(int color,PrintWriter pW,String fileName, String tcx, int threshold, LogInfo lI)
    {
    	switch (color)
    	{
    	case 0: Red.printData(pW, fileName, tcx,"Red",threshold,lI); break;
    	case 1: Green.printData(pW,fileName, tcx,"Green",threshold,lI); break;
    	case 2: Blue.printData(pW,fileName, tcx,"Blue",threshold,lI); break;
    	default: break;
    	}
    }
    
    public void stateChanged(ChangeEvent e)
    {
    	fL.repaintPane();
    }    
    
    public void loadPunctaIgnoreList(boolean[] red, boolean[] green, boolean[] blue)
    {
    	Red.loadPunctaIgnoreList(red);
    	Green.loadPunctaIgnoreList(green);
    	Blue.loadPunctaIgnoreList(blue);
    }
    
    public void loadDendriteIgnoreList(boolean[] list)
    {
    	Red.loadDendriteIgnoreList(list);
    }
    
    public void resetGroup(int group)
    {
    	myGroup.setGroup(group);
    }
    
    public void checkSize()
    {
    	Red.checkSize();
    	Green.checkSize();
    	Blue.checkSize();
    }
    
    public void setMiddlePanelScrollBar(int value)
    {
    	Red.setMiddlePanelScrollBar(value);
    	Green.setMiddlePanelScrollBar(value);
    	Blue.setMiddlePanelScrollBar(value);
    }
    
    public void setBottomPanelScrollBar(int value)
    {
    	Red.setBottomPanelScrollBar(value);
    	Green.setBottomPanelScrollBar(value);
    	Blue.setBottomPanelScrollBar(value);
    }
    
    
    

}
