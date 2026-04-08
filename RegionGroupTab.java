package neuron_analyzer;

import javax.swing.*;
import java.io.*;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public class RegionGroupTab extends JTabbedPane implements MouseListener {
functionListener fL;
TabPopupMenu tM;

    public RegionGroupTab(String[] names,functionListener fl) {
        fL = fl;
        tM= new TabPopupMenu(this);
        for(int k = 0; k < names.length; k++)
        {
            addTab(names[k],new ColorTabs(fL,k));
        }
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.addChangeListener(new RegionGroupTab_this_changeAdapter(this));
        addMouseListener(this);

    }
    
    public void deleteTab(int group)
    {    	
    	this.removeTabAt(group);    	
    	for(int k = 0; k < this.getTabCount(); k++)
    	{
    		((ColorTabs)getComponentAt(k)).resetGroup(k);    		
    	}
    }

    public void addTab(String title,int Group)
    {
        super.addTab(title,new ColorTabs(fL,Group));
    }

    public int getCurrentColor()
    {
        return ((ColorTabs)getSelectedComponent()).getSelectedIndex();
    }

    public Group getCurrentDendriteGroup()
    {
        return ((ColorTabs)getSelectedComponent()).myGroup;
    }

    public void this_stateChanged(ChangeEvent e) {
        fL.repaintPane();
    }
    
    public void printData(int tab,int color, PrintWriter pW, String fileName, int threshold,LogInfo lI)
    {    	
    	((ColorTabs)getComponentAt(tab)).printData(color, pW, fileName,this.getTitleAt(tab),threshold,lI);
    	
    }
    
    public void setPreferredSize(Dimension d)
    {
    	super.setPreferredSize(new Dimension(200,d.height));
    }
    
    public Group[] getGroupList()
    {
    	Group[] g = new Group[getTabCount()];
    	for(int k = 0; k < g.length; k++)
    	{
    		g[k] = ((ColorTabs)this.getComponentAt(k)).myGroup;
    	}
    	return g;
    }
    
    public void newGroups()
    {
    	for(int k = 0; k < getTabCount(); k++)
    	{
    		((ColorTabs)this.getComponentAt(k)).myGroup = new Group(k);
    	}
    }
    
    public void mouseClicked(MouseEvent e)
    {
    	
    }
    
    public void mousePressed(MouseEvent e)
    {
    	if(e.getButton() == MouseEvent.BUTTON3)
		{
    		//tM.show(this.getParent(),this.getParent().getWidth() - 250 + e.getX(),e.getY());
    		int x = e.getX();
    		if(x + tM.getWidth() > getWidth())
    			x = getWidth() - tM.getWidth();
    		tM.show(this,x,e.getY());
		}
    }
    
    public void mouseReleased(MouseEvent e)
    {
    	if(e.getButton() == MouseEvent.BUTTON3)
    		{
    		tM.setVisible(false);
    		}
    }
    
    public void mouseExited(MouseEvent e)
    {
    	
    }
    
    public void mouseEntered(MouseEvent e)
    {
    	
    }
    
    public void setMiddlePanelScrollBar(int value)
    {
    	for(int k = 0; k < getTabCount(); k++)
    	{
    		((ColorTabs)this.getComponentAt(k)).setMiddlePanelScrollBar(value);
    	}
    }
    
    public void setBottomPanelScrollBar(int value)
    {
    	for(int k = 0; k < getTabCount(); k++)
    	{
    		((ColorTabs)this.getComponentAt(k)).setBottomPanelScrollBar(value);
    	}
    }


}


class RegionGroupTab_this_changeAdapter implements ChangeListener {
    private RegionGroupTab adaptee;
    RegionGroupTab_this_changeAdapter(RegionGroupTab adaptee) {
        this.adaptee = adaptee;
    }

    public void stateChanged(ChangeEvent e) {
        adaptee.this_stateChanged(e);
    }
}
