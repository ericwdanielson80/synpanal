package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.awt.Color;
import java.awt.Point;
import javax.swing.JFrame;
/**
 * Central action-dispatch interface that the main application
 * implements and passes to every UI and data subsystem. The methods
 * here cover zoom control, per-channel activation, counting/
 * thresholding, dendrite/spine/cell creation and editing, navigation
 * between images, print/save invocations, event-listener
 * registration and notification, group-tab management,
 * colocalization computation, scroll-bar synchronisation, and
 * miscellaneous accessors for calibration, threshold values, UI
 * state, and log settings. Implementations own the actual image,
 * dendrite, puncta and cell state; interface consumers only know
 * how to ask this listener to perform an action or hand back data.
 */
public interface functionListener {

    /** Enters zoom-select mode for the image view. */
    public void setZoomMode();
    /** Exits zoom-select mode for the image view. */
    public void removeZoomMode();
    /** Marks the red channel as active for analysis overlays. */
    public void redColorActive();
    /** Marks the red channel as inactive. */
    public void redColorInActive();
    /** Marks the green channel as active for analysis overlays. */
    public void greenColorActive();
    /** Marks the green channel as inactive. */
    public void greenColorInActive();
    /** Marks the blue channel as active for analysis overlays. */
    public void blueColorActive();
    /** Marks the blue channel as inactive. */
    public void blueColorInActive();
    /** Runs the puncta-counting pass on the current image. */
    public void countPuncta();
    /** Starts a new straight-line dendrite trace. */
    public void addDendrite();
    /** Starts a new polygon (complex) dendrite region. */
    public void addComplexDendrite();
    /** Sets the upper thresholds for red, green, and blue channels. */
    public void setThreshold(int r,int g,int b);
    /** Sets the start thresholds for red, green, and blue channels. */
    public void setstThreshold(int r,int g,int b);
    /** Returns the currently selected color-group index. */
    public int getCurrentColorGroup();
    /** Returns the Group object of the currently selected tab. */
    public Group getCurrentDendriteGroup();
    /** Repaints the primary image pane. */
    public void repaintPane();
    /** Sets the default dendrite rendering width to w. */
    public void setDendriteWidth(int w);
    /** Sets the microns-per-pixel calibration value d. */
    public void setCalibration(double d);
    /** Advances to the next image in the current batch. */
    public void nextImage();
    /** Invokes the full data-print/export routine. */
    public void printData();
    /** Scrolls to and highlights the dendrite at table row. */
    public void focusDendrite(int row);
    /** Starts adding a spine via the primary workflow. */
    public void addSpine();
    /** Starts adding a spine via the secondary workflow (e.g. drag). */
    public void addSpine2();
    /** Returns the current microns-per-pixel calibration. */
    public double getCalibration();
    /** Returns the average spine count for the given group. */
    public double getAveSpineNum(int group);
    /** Returns the average spine head width for the given group. */
    public double getAveSpineWidth(int group);
    /** Returns the average spine length for the given group. */
    public double getAveSpineLength(int group);
    /** Returns the count of spines of a given type for the group. */
    public double getSpineTypeNum(int group,int type);
    /** Returns the average cell integrated intensity for color and group. */
    public double getAveCellIntensity(int color, int group);
    /** Returns the average of the per-cell average intensities for color and group. */
    public double getAveCellAveIntensity(int color, int group);
    /** Returns the mean intensity of cell k in the given color. */
    public double getCellAveIntensity(int k, int color);
    /** Returns the number of cells assigned to the given group. */
    public int getCellNumber(int group);
    /** Starts a new cell-body annotation workflow. */
    public void addCell();
    /** Reports whether cell k of group should be visible in color. */
    public boolean showCell(int k, int color,int group);
    /** Returns the integrated intensity of cell k in the given color. */
    public double getCellIntensity(int k,int color);
    /** Returns the total number of cells in the current image. */
    public int getCellNum();
    /** Returns the k-th dendrite of the specified group. */
    public Dendrite getDendrite(int group,int k);
    /** Asks the data scroll pane to repaint. */
    public void repaintScrollPane();
    /** Toggles the "speed" rendering mode (hide dendrite overlays). */
    public void toggleSpeed();
    /** Toggles the "blind" mode that hides condition labels. */
    public void toggleBlind();
    /** Runs the batch-processing pipeline across all queued images. */
    public void batchProcess();
    /** Returns the number of group tabs currently open. */
    public int getTabCount();
    /** Repaints the data-table pane. */
    public void repaintDataPane();
    /** Steps back to the previous image in the batch. */
    public void previousImage();
    /** Removes the currently selected spine. */
    public void removeSpine();
    /** Removes the currently selected cell. */
    public void removeCell();
    /** Resets threshold/overlay colors to defaults. */
    public void resetColors();
    /** Sets the red threshold overlay color. */
    public void setRedThresholdColor(Color c);
    /** Sets the blue threshold overlay color. */
    public void setBlueThresholdColor(Color c);
    /** Sets the green threshold overlay color. */
    public void setGreenThresholdColor(Color c);
    /** Installs the array of auto-ignore criteria rules. */
    public void setIgnoreCriteria(IgnoreCriteria[] ignoreCriteria);
    /** Returns the red channel upper threshold. */
    public int getRedThreshold();
    /** Returns the green channel upper threshold. */
    public int getGreenThreshold();
    /** Returns the blue channel upper threshold. */
    public int getBlueThreshold();
    /** Returns the red channel start threshold. */
    public int getstRedThreshold();
    /** Returns the green channel start threshold. */
    public int getstGreenThreshold();
    /** Returns the blue channel start threshold. */
    public int getstBlueThreshold();
    /** Returns the red threshold overlay color. */
    public Color getRedThresholdColor();
    /** Returns the green threshold overlay color. */
    public Color getGreenThresholdColor();
    /** Returns the blue threshold overlay color. */
    public Color getBlueThresholdColor();
    /** Returns the Dendrite array for the given group. */
    public Dendrite[] getDendrites(int groupMember);
    //public DendriteGroupData getDendriteGroupData(int groupMember, int color);
    /** Returns the current data-view mode identifier. */
    public int getDataMode();    
    /** Sets the data-view mode to d. */
    public void setDataMode(int d);
    /** Registers a CountEventListener to be notified on puncta counts. */
    public void addCountEventListener(CountEventListener l);
    /** Registers a SpineEventListener. */
    public void addSpineEventListener(SpineEventListener l);
    /** Registers a CellEventListener. */
    public void addCellEventListener(CellEventListener l);
    /** Registers a ThresholdEventListener. */
    public void addThresholdEventListener(ThresholdEventListener l);
    /** Fires count events to all registered CountEventListeners. */
    public void notifyCountEventListeners();
    /** Fires spine events to all registered SpineEventListeners. */
    public void notifySpineEventListeners();
    /** Fires cell events to all registered CellEventListeners. */
    public void notifyCellEventListeners();
    /** Returns all CellBody objects in the current image. */
    public CellBody[] getCells();
    /** Returns the current save-file format version. */
    public int getsaveVersion();
    /** Toggles the radius overlay view. */
    public void toggleRadiusView();
    /** Persists the current region annotations to disk. */
    public void saveRegionInformation();
    /** Toggles automatic logging on image change. */
    public void toggleAutoLog();
    /** Reports whether autolog is enabled. */
    public boolean isAutoLog();
    /** Adds a new group tab. */
    public void addTab();
    /** Removes the group tab at the given index. */
    public void deleteTab(int group);
    /** Returns the Group[] list covering every open tab. */
    public Group[] getGroupList();
    /** Replaces the list of category/condition labels with s. */
    public void setCategories(String[] s);
    /** Returns the current default dendrite width. */
    public int getDendriteWidth();
    /** Returns the top-level application JFrame. */
    public JFrame getFrame();
    /** Temporarily pauses (true) or resumes (false) the key listener. */
    public void pauseKeyListener(boolean pause);
    /** Returns the current zoom level. */
    public int getZoom();
    /** Updates the UI to reflect the current zoom value. */
    public void updateZoom();
    /** Sets the zoom level to z and refreshes the view. */
    public void setZoom(int z);
    /** Returns the shared LogInfo configuration object. */
    public LogInfo getLogInfo();
    /** Triggers colocalization measurement across channels. */
    public void measureColocalization();
    /** Returns the most recently computed ColocalizationInfo array. */
    public ColocalizationInfo[] getColocalizationInfo();
    /** Broadcasts a horizontal scroll position to every middle panel. */
    public void setMiddlePanelScrollBar(int value);
    /** Broadcasts a horizontal scroll position to every bottom panel. */
    public void setBottomPanelScrollBar(int value);
    /** Records a Sholl-analysis center point. */
    public void addSholl(Point p);
    /** Sets the look-up-table scaling values for the three channels. */
    public void setLookUp(int r, int g, int b);



}
