package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
    
import javax.swing.*;
import java.io.*;
import java.awt.Dimension;
import java.awt.event.*;
    
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
    	
/**
 * This file defines the top-level JTabbedPane used to organize analysis
 * groups (experimental conditions) along with a small adapter class
 * that bridges ChangeEvents back into the main tab.
 * <br>
 * Classes in this file:
 * <ul>
 * <li>RegionGroupTab: the JTabbedPane whose tabs are ColorTabs
 * instances, one per analysis group, plus right-click popup and
 * scroll-bar synchronisation across tabs.</li>
 * <li>RegionGroupTab_this_changeAdapter: ChangeListener adapter that
 * forwards tab-selection events to the owning RegionGroupTab.</li>
 * </ul>
 */
/**
 * Top-level tab holder whose tabs are ColorTabs representing analysis
 * groups. Responsible for tab management (add/delete/rename),
 * exposing the current color and Group, dispatching per-tab print
 * operations, showing a right-click TabPopupMenu, and propagating
 * shared scroll positions across all group tabs.
 */
public class RegionGroupTab extends JTabbedPane implements MouseListener {
functionListener fL;
TabPopupMenu tM;
    
    /** Stores the functionListener, creates the popup menu, and installs one ColorTabs tab per entry in names. */
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
    
    /** Subscribes the change adapter and registers this as a mouse listener for right-click detection. */
    private void jbInit() throws Exception {
        this.addChangeListener(new RegionGroupTab_this_changeAdapter(this));
        addMouseListener(this);
    
    }
    
    /** Removes the tab at index group and renumbers all remaining ColorTabs via resetGroup. */
    public void deleteTab(int group)
    {    	
    	this.removeTabAt(group);    	
    	for(int k = 0; k < this.getTabCount(); k++)
    	{
    		((ColorTabs)getComponentAt(k)).resetGroup(k);    		
    	}
    }
    
    /** Adds a new tab labelled title whose content is a fresh ColorTabs bound to Group. */
    public void addTab(String title,int Group)
    {    	
        super.addTab(title,new ColorTabs(fL,Group));
    }
    	
    /** Returns the selected color-tab index inside the currently selected group tab. */
    public int getCurrentColor()
    {
        return ((ColorTabs)getSelectedComponent()).getSelectedIndex();
    }
    
    /** Returns the Group object of the currently selected tab. */
    public Group getCurrentDendriteGroup()
    {
        return ((ColorTabs)getSelectedComponent()).myGroup;
    }
    
    /** ChangeListener callback that repaints the image pane whenever the selected tab changes. */
    public void this_stateChanged(ChangeEvent e) {
        fL.repaintPane();
    }
    
    /** Forwards a print request for the given tab/color to that ColorTabs, passing the tab's title as the condition string. */
    public void printData(int tab,int color, PrintWriter pW, String fileName, int threshold,LogInfo lI)
    {
    	((ColorTabs)getComponentAt(tab)).printData(color, pW, fileName,this.getTitleAt(tab),threshold,lI);
    
    }
    
    /** Overrides the preferred size to fix the width at 200 while preserving the requested height. */
    public void setPreferredSize(Dimension d)
    {
    	super.setPreferredSize(new Dimension(200,d.height));
    }
    
    /** Collects the Group object from every ColorTabs tab in order and returns them as an array. */
    public Group[] getGroupList()
    {
    	Group[] g = new Group[getTabCount()];
    	for(int k = 0; k < g.length; k++)
    	{
    		g[k] = ((ColorTabs)this.getComponentAt(k)).myGroup;
    	}
    	return g;
    }
    
    /** Replaces every tab's Group with a freshly numbered Group(k), resetting any shared state. */
    public void newGroups()
    {
    	for(int k = 0; k < getTabCount(); k++)
    	{
    		((ColorTabs)this.getComponentAt(k)).myGroup = new Group(k);
    	}
    }
    
    /** MouseListener hook; unused for plain clicks. */
    public void mouseClicked(MouseEvent e)
    {
    	
    }
    
    /** Shows the TabPopupMenu on right-click, clamping its x position so it remains within the tab bar. */
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
    
    /** Hides the popup menu when the right mouse button is released. */
    public void mouseReleased(MouseEvent e)
    {
    	if(e.getButton() == MouseEvent.BUTTON3)
    		{
    		tM.setVisible(false);
    		}
    }
    
    /** MouseListener hook; unused. */
    public void mouseExited(MouseEvent e)
    {
    
    }
    
    /** MouseListener hook; unused. */
    public void mouseEntered(MouseEvent e)
    {
    	
    }
    
    /** Broadcasts a middle-panel scroll position to every tab's ColorTabs. */
    public void setMiddlePanelScrollBar(int value)
    {
    	for(int k = 0; k < getTabCount(); k++)
    	{
    		((ColorTabs)this.getComponentAt(k)).setMiddlePanelScrollBar(value);
    	}
    }
    
    /** Broadcasts a bottom-panel scroll position to every tab's ColorTabs. */
    public void setBottomPanelScrollBar(int value)
    {
    	for(int k = 0; k < getTabCount(); k++)
    	{
    		((ColorTabs)this.getComponentAt(k)).setBottomPanelScrollBar(value);
    	}
    }
    
    
}
