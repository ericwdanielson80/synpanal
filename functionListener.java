package neuron_analyzer;
import java.awt.Color;
import java.awt.Point;
import javax.swing.JFrame;
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
public interface functionListener {

    public void setZoomMode();
    public void removeZoomMode();
    public void redColorActive();
    public void redColorInActive();
    public void greenColorActive();
    public void greenColorInActive();
    public void blueColorActive();
    public void blueColorInActive();
    public void countPuncta();
    public void addDendrite();
    public void addComplexDendrite();
    public void setThreshold(int r,int g,int b);
    public void setstThreshold(int r,int g,int b);
    public int getCurrentColorGroup();
    public Group getCurrentDendriteGroup();
    public void repaintPane();
    public void setDendriteWidth(int w);
    public void setCalibration(double d);
    public void nextImage();
    public void printData();
    public void focusDendrite(int row);
    public void addSpine();
    public void addSpine2();
    public double getCalibration();
    public double getAveSpineNum(int group);
    public double getAveSpineWidth(int group);
    public double getAveSpineLength(int group);
    public double getSpineTypeNum(int group,int type);
    public double getAveCellIntensity(int color, int group);
    public double getAveCellAveIntensity(int color, int group);
    public double getCellAveIntensity(int k, int color);
    public int getCellNumber(int group);
    public void addCell();
    public boolean showCell(int k, int color,int group);
    public double getCellIntensity(int k,int color);
    public int getCellNum();
    public Dendrite getDendrite(int group,int k);
    public void repaintScrollPane();
    public void toggleSpeed();
    public void toggleBlind();
    public void batchProcess();
    public int getTabCount();
    public void repaintDataPane();
    public void previousImage();
    public void removeSpine();
    public void removeCell();
    public void resetColors();
    public void setRedThresholdColor(Color c);
    public void setBlueThresholdColor(Color c);
    public void setGreenThresholdColor(Color c);
    public void setIgnoreCriteria(IgnoreCriteria[] ignoreCriteria);
    public int getRedThreshold();
    public int getGreenThreshold();
    public int getBlueThreshold();
    public int getstRedThreshold();
    public int getstGreenThreshold();
    public int getstBlueThreshold();
    public Color getRedThresholdColor();
    public Color getGreenThresholdColor();
    public Color getBlueThresholdColor();
    public Dendrite[] getDendrites(int groupMember);
    //public DendriteGroupData getDendriteGroupData(int groupMember, int color);
    public int getDataMode();    
    public void setDataMode(int d);
    public void addCountEventListener(CountEventListener l);
    public void addSpineEventListener(SpineEventListener l);
    public void addCellEventListener(CellEventListener l);
    public void addThresholdEventListener(ThresholdEventListener l);
    public void notifyCountEventListeners();
    public void notifySpineEventListeners();
    public void notifyCellEventListeners();
    public CellBody[] getCells();
    public int getsaveVersion();
    public void toggleRadiusView();
    public void saveRegionInformation();
    public void toggleAutoLog();
    public boolean isAutoLog();
    public void addTab();
    public void deleteTab(int group);
    public Group[] getGroupList();
    public void setCategories(String[] s);
    public int getDendriteWidth();
    public JFrame getFrame();
    public void pauseKeyListener(boolean pause);
    public int getZoom();
    public void updateZoom();
    public void setZoom(int z);
    public LogInfo getLogInfo();
    public void measureColocalization();
    public ColocalizationInfo[] getColocalizationInfo();
    public void setMiddlePanelScrollBar(int value);
    public void setBottomPanelScrollBar(int value);
    public void addSholl(Point p);
    public void setLookUp(int r, int g, int b);



}
