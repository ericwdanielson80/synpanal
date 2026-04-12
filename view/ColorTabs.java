package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
    	
import javax.swing.*;
import java.io.*;
import javax.swing.event.*;
    
    
/**
 * A JTabbedPane with one tab per color channel (Red, Green, Blue) that holds
 * the data panels for a single analysis group (experimental condition tab).
 * Each color tab contains a DataPanelContainer whose widgets display puncta,
 * spine, dendrite, and cell measurements filtered to that channel. ColorTabs
 * also listens for tab-selection changes and forwards them to the main
 * functionListener so the rest of the UI can refresh.
 */
public class ColorTabs extends JTabbedPane implements ChangeListener{
	Group myGroup;
    public DataPanelContainer Red;
    public DataPanelContainer Green;
    public DataPanelContainer Blue;
    functionListener fL;
    /**
     * Constructs the color tab container for a given group index. Stores the
     * main functionListener reference, wraps the incoming integer group id in
     * a Group object, registers itself as a change listener on tab selection,
     * and then delegates to jbInit to build the three per-color data panels.
     * The parameter fl is the shared action-dispatch listener used throughout
     * the app, and Group is the group/condition index this tab represents.
     */
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
    
    /**
     * Creates the three per-channel DataPanelContainers (indexes 0=Red,
     * 1=Green, 2=Blue) bound to the same Group and adds each as a named tab.
     * The Group parameter is the shared Group object that identifies which
     * analysis group these panels belong to.
     */
    private void jbInit(Group Group) throws Exception {
        Red = new DataPanelContainer(fL,Group,0);
        Green = new DataPanelContainer(fL,Group,1);
        Blue = new DataPanelContainer(fL,Group,2);
    
        addTab("Red",Red);
        addTab("Green",Green);
        addTab("Blue",Blue);
    }
    
    /**
     * Dispatches the data-print request to the appropriate DataPanelContainer
     * based on the color argument (0 Red, 1 Green, 2 Blue). Each
     * DataPanelContainer writes its contents to the shared PrintWriter, along
     * with the current image filename, the transfection/condition tag tcx,
     * the color name, the threshold value, and the LogInfo settings lI that
     * describe which tables should be emitted.
     */
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
    
    /**
     * ChangeListener callback invoked when the user switches color tab. It
     * simply asks the main functionListener to repaint the image pane so
     * overlays reflect the newly selected channel. The ChangeEvent e is
     * unused.
     */
    public void stateChanged(ChangeEvent e)
    {
    	fL.repaintPane();
    }
    
    /**
     * Applies per-puncta "ignored" flags to each color channel's data panel.
     * Each boolean[] argument is the ignore list for the matching channel,
     * forwarded directly to that channel's DataPanelContainer.
     */
    public void loadPunctaIgnoreList(boolean[] red, boolean[] green, boolean[] blue)
    {
    	Red.loadPunctaIgnoreList(red);
    	Green.loadPunctaIgnoreList(green);
    	Blue.loadPunctaIgnoreList(blue);
    }
    
    /**
     * Loads a dendrite-ignore list. Dendrites are shared across channels so
     * only the Red container (the canonical owner) receives it. The list
     * parameter is the boolean mask of ignored dendrite indices.
     */
    public void loadDendriteIgnoreList(boolean[] list)
    {
    	Red.loadDendriteIgnoreList(list);
    }
    
    /**
     * Updates the underlying Group value to a new integer group id. This is
     * used when tabs are reordered or deleted so remaining tabs stay in
     * sequential order. The group parameter is the new group index.
     */
    public void resetGroup(int group)
    {
    	myGroup.setGroup(group);
    }
    
    /**
     * Forces each child DataPanelContainer to recompute and update its
     * preferred size. Typically called after the table data changes and the
     * row count may have changed.
     */
    public void checkSize()
    {
    	Red.checkSize();
    	Green.checkSize();
    	Blue.checkSize();
    }
    
    /**
     * Synchronizes the horizontal scroll position of the middle panel
     * (dendrite data) across all three color tabs so users see the same
     * columns when switching channels. The value parameter is the scroll
     * bar position to apply.
     */
    public void setMiddlePanelScrollBar(int value)
    {
    	Red.setMiddlePanelScrollBar(value);
    	Green.setMiddlePanelScrollBar(value);
    	Blue.setMiddlePanelScrollBar(value);
    }
    
    /**
     * Synchronizes the horizontal scroll position of the bottom panel
     * (individual puncta/spine/cell data) across all three color tabs. The
     * value parameter is the scroll bar position to apply.
     */
    public void setBottomPanelScrollBar(int value)
    {
    	Red.setBottomPanelScrollBar(value);
    	Green.setBottomPanelScrollBar(value);
    	Blue.setBottomPanelScrollBar(value);
    }
    
    
    
    
}
    