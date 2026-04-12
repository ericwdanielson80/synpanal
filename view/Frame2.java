package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.WritableRaster;

/**
 * Frame2.java
 *
 * Main application frame for the neuron_analyzer image analysis tool.
 * This file contains the central JPanel that coordinates the overall
 * neuron analysis user interface, together with a few small adapter
 * classes used to route low-level AWT mouse events back to the frame.
 *
 * Classes in this file:
 * <ul>
 *   <li>{@code Frame2} - the main application panel. It owns the
 *       {@link RegionOptionsPanel} (top toolbar), {@link ImagePropertiesPanel}
 *       (west), {@link TiffPanel}/{@link TiffScrollPane} (center image display),
 *       {@link RegionGroupTab} (east color/region tabs) and
 *       {@link ThresholdOptionsPanel} (south). It coordinates loading and
 *       saving of neuron profile data, drives batch processing and data
 *       logging, handles global key bindings and exposes a large surface of
 *       methods used by helper classes such as {@link PunctaCounter} and
 *       {@link functionListener} implementors.</li>
 *   <li>{@code Frame2_this_mouseWheelAdapter} - adapter that forwards
 *       mouse wheel events from the frame to {@link Frame2#this_mouseWheelMoved}.</li>
 *   <li>{@code Frame2_this_mouseAdapter} - adapter that forwards mouse
 *       press events from the frame to {@link Frame2#this_mouseClicked}.</li>
 *   <li>{@code Frame2_this_mouseMotionAdapter} - adapter that forwards
 *       mouse motion events from the frame to {@link Frame2#this_mouseMoved}.</li>
 * </ul>
 *
 * Main panel of the neuron_analyzer application window. Frame2 lays out the
 * various sub-panels (region options, image properties, TIFF display,
 * threshold controls, region group tabs) using a BorderLayout, keeps a
 * reference to the parent {@link JFrame}, and exposes the large set of
 * actions and queries the rest of the application uses to interact with the
 * currently-loaded image. It also implements {@link functionListener} to
 * serve as the central command target, {@link Runnable} for batch
 * processing, {@link ItemListener} for the file combo box and
 * {@link KeyEventDispatcher} for global keyboard shortcuts.
 */
public class Frame2 extends JPanel implements functionListener, Runnable, ItemListener, KeyEventDispatcher {
    RegionOptionsPanel ROP;
    ImagePropertiesPanel IP;
    ThresholdOptionsPanel TOP;
    TiffPanel TP;

    TiffScrollPane TSP;
    JFrame parent; 
    


    RegionGroupTab RGT;

    int HIndent = 35;
    int VIndent = 35;
    int ROPWidth = 600;
    int IPHeight = 600;
    int TOPHeight = 35;
    File[] fileName;
    int imgCount = 0;
    double calibration = 0.12726;
    PrintWriter pW;
    LogInfo lI;

    JComboBox fileBox;
    String names;
    int numbers;
    
    boolean ctrl = false;
    boolean demoMode = true;
    boolean isListening = true;
    
    Vector countEventListenerList = new Vector();
    Vector spineEventListenerList = new Vector();
    Vector cellEventListenerList = new Vector();
    Vector thresholdEventListenerList = new Vector();
    
    ImagePunctaDataPrinter iP;
    ImageSpineDataPrinter sP;
    ImageCellDataPrinter cP;
    
    DendritePunctaDataPrinter dpP;
    DendriteSpineDataPrinter dsP;
    
    IndividualPunctaDataPrinter ipP;
    IndividualSpineDataPrinter isP;
    IndividualCellDataPrinter icP;
    
    String logDirectory;
    
    int currentSaveVersion = 3;

        
    /**
     * Builds the main frame for a given set of image files. Stores the
     * parent window reference, the list of files being analyzed, the
     * {@link LogInfo} with logging/tab configuration and the demo-mode
     * flag. A {@link JComboBox} listing the file names is created and
     * registered as an item listener so the frame can respond to file
     * switches. After laying out the UI through {@link #jbInit()} the
     * constructor configures the log output, prepares the save directory,
     * checks for an existing profile to load, reloads the color channels,
     * resets the threshold options panel and triggers a repaint.
     */
    public Frame2(JFrame Parent, File[] filename,LogInfo li, String logFile,boolean mode) {
    	parent = Parent;
        fileName = filename;      
        demoMode = mode;
        String[] names = new String[filename.length];
        int[] numbers = new int[filename.length];
        for(int k = 0; k < names.length; k++)
        {
        	names[k]=filename[k].getName();
        	numbers[k] = k +1;
        }
        fileBox = new JComboBox(names);
        fileBox.setSelectedIndex(0);
        fileBox.addItemListener(this);
        lI = li;
        logDirectory = logFile;        
        
        
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        setLog(logFile);
        TP.makeDir(); 
        checkLoadFile();        
        TP.loadColors();        
        TOP.resetThresholdOptionsPanel();        
        TP.repaint();
    }
    
    /**
     * Reinitializes the frame with a new set of image files without
     * rebuilding the UI. Replaces the file list and {@link LogInfo},
     * repopulates the file combo box, refreshes the log file, prepares
     * the save directory, attempts to load any existing profile, reloads
     * color channels, resets the threshold options and repaints.
     */
    public void ReMake(File[] filename,LogInfo li,String logFile)
    {
    	fileName = filename;
    	lI = li;
    	logDirectory = logFile;
    	
    	String[] names = new String[filename.length];
        int[] numbers = new int[filename.length];
        fileBox.removeAllItems();
        for(int k = 0; k < names.length; k++)
        {
        	names[k]=filename[k].getName();
        	fileBox.addItem(names[k]);
        	numbers[k] = k +1;
        }
        setLog(logFile);        
        TP.makeDir(); 
        checkLoadFile();        
        TP.loadColors();        
        TOP.resetThresholdOptionsPanel();        
        TP.repaint();
        
    }
    
       
    /**
     * Returns the parent {@link JFrame} that hosts this panel.
     */
    public JFrame getFrame()
    {
    	return parent;
    }
     
    /**
     * Initializes and lays out all of the major child components of the
     * main frame. Constructs the region options panel (toolbar), the
     * image properties panel, the threshold options panel, the region
     * group tab (color channels) and the {@link TiffPanel} wrapped in a
     * {@link TiffScrollPane}. Registers mouse, mouse-motion and
     * mouse-wheel listeners on this panel and installs this frame as a
     * global {@link KeyEventDispatcher}. Places each sub-panel into the
     * BorderLayout (NORTH/WEST/CENTER/EAST/SOUTH) and sizes them
     * relative to the screen. Finally enables the TIFF panel's own mouse
     * and motion listeners.
     */
    private void jbInit() throws Exception {    	
        setLayout(null);
        ROP = new RegionOptionsPanel(this);
        ROP.add(fileBox);
        IP = new ImagePropertiesPanel(this);
        TOP = new ThresholdOptionsPanel(this);
        
        RGT = new RegionGroupTab(lI.transfectionConditions,this);
        TP = new TiffPanel(this,fileName);
        TSP = new TiffScrollPane(TP);
        TP.setScrollPane(TSP);
        
        //lI = new LogInfo(new String[]{"Ctrl","S-SCAM"},new int[] {0,1,2},false,false);
        //setLog("DataFile.txt");
        

        
        //this.setClosable(true);

        //this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);        
        this.addMouseMotionListener(new Frame2_this_mouseMotionAdapter(this));
        this.addMouseListener(new Frame2_this_mouseAdapter(this));
        this.addMouseWheelListener(new Frame2_this_mouseWheelAdapter(this));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        
        this.setLayout(new BorderLayout());        
        add(ROP,BorderLayout.NORTH);
        add(IP,BorderLayout.WEST);        
        add(TSP,BorderLayout.CENTER);        
        add(RGT,BorderLayout.EAST);
        add(TOP,BorderLayout.SOUTH);

        /*ROP.setBounds(HIndent,0,ROPWidth,VIndent);
        IP.setBounds(0,HIndent,VIndent,IPHeight);
        TOP.setBounds(HIndent,VIndent+IPHeight,ROPWidth,TOPHeight);
        TSP.setBounds(HIndent,VIndent,ROPWidth,IPHeight);
        TP.setPreferredSize(new Dimension(1024,1024));

        RGT.setBounds(635,0,200,670);*/
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        d.setSize(d.width - 15, d.height - 100);
        ROP.setPreferredSize(new Dimension(d.width,VIndent));  //North        
        IP.setPreferredSize(new Dimension(HIndent,d.height - VIndent - VIndent - VIndent)); //west        
        //TP.setPreferredSize(new Dimension(d.width - HIndent - 200,d.height - VIndent - VIndent)); //center
        RGT.setPreferredSize(new Dimension(200,d.height - VIndent - VIndent - VIndent )); //east
        TOP.setPreferredSize(new Dimension(d.width,VIndent * 2 + 50)); //south
        
        TP.addMouseMotion();
        TP.addMouse();
        
        

    }
    
    /**
     * Returns the number of color/region tabs currently present in the
     * {@link RegionGroupTab}.
     */
    public int getTabCount()
    {
    	return RGT.getTabCount();
    }

    /**
     * Enables zoom-by-mousewheel on the image panel by installing its
     * wheel listener.
     */
    public void setZoomMode()
    {
        TP.addMouseWheel();
    }
    
    /**
     * Increases the image zoom level by 10% of the current value and
     * repaints the display.
     */
    public void zoomIn()
    {
    	TP.setZoom(TP.getZoom() + ((TP.getZoom() * 10) / 100),0,0);
    	updateZoom();
        TP.repaint();
    }
    
    /**
     * Decreases the image zoom level by 10% of the current value and
     * repaints the display.
     */
    public void zoomOut()
    {
    	TP.setZoom(TP.getZoom() - ((TP.getZoom() * 10) / 100),0,0);
    	updateZoom();
        TP.repaint();
    }

    /**
     * Disables zoom-by-mousewheel on the image panel.
     */
    public void removeZoomMode()
    {
        TP.removeMouseWheel();
    }


    /**
     * Toggles the red channel on (called when the red channel becomes
     * active in the UI).
     */
    public void redColorActive()
    {
       TP.switchRed();
    }
    /**
     * Toggles the red channel off (called when the red channel becomes
     * inactive in the UI).
     */
    public void redColorInActive()
    {
        TP.switchRed();
    }
    /**
     * Toggles the green channel on (called when the green channel
     * becomes active in the UI).
     */
    public void greenColorActive()
    {
        TP.switchGreen();
    }
    /**
     * Toggles the green channel off (called when the green channel
     * becomes inactive in the UI).
     */
    public void greenColorInActive()
    {
        TP.switchGreen();
    }
    /**
     * Toggles the blue channel on (called when the blue channel becomes
     * active in the UI).
     */
    public void blueColorActive()
    {
        TP.switchBlue();
    }
    /**
     * Toggles the blue channel off (called when the blue channel becomes
     * inactive in the UI).
     */
    public void blueColorInActive()
    {
        TP.switchBlue();
    }
    
    /**
     * Runs puncta counting against the cached intensity backup, used
     * when recounting after a file has just been loaded.
     */
    public void loadFilecountPuncta()
    {    	
    	TP.countPuncta(TP.backupInt);
    }
    
    /**
     * Recounts puncta on the current image using the cached intensity
     * backup and repaints the display.
     */
    public void countPuncta()
    {  
    	TP.countPuncta(TP.backupInt);   
        TP.repaint();
    }
        
    
    /**
     * Recounts puncta and then waits for any dendrite/cell worker
     * threads to finish; intended for use during batch processing.
     */
    public void recountPuncta()
    {    
    	//to be used for batch stuff
    	
        TP.countPuncta(TP.backupInt); 
        checkThreads();	   
    }
    
    /**
     * Spins until each worker thread in the dendrite and cell thread
     * arrays held by the {@link TiffPanel} reports that it is no longer
     * alive. Used as a simple join barrier during batch operations.
     */
    public void checkThreads()
    {
    	if(TP.dendriteThread != null){
    	for(int k = 0; k < TP.dendriteThread.length; k++)
    	{
    		if(TP.dendriteThread[k].isAlive())
    		{
    			k--;
    		}
    	}
    	}
    	
    	if(TP.cellThread != null)
    	{
    		for(int k = 0; k < TP.cellThread.length; k++)
        	{
        		if(TP.cellThread[k].isAlive())
        		{
        			k--;
        		}
        	}
    	}
    }
    
        
    /**
     * Serializes the current analysis state (save version, calibration,
     * tab info and names, threshold settings, dendrite width and
     * geometry, cell regions, per-tab puncta ignore lists and dendrite
     * ignore lists) to {@code TP.saveFile} using an
     * {@link IoContainer}. Opens the file, writes all data in the order
     * commented inline, and flushes the stream. Aborts quietly if the
     * target file cannot be opened.
     */
    private void saveToFile()
    {
    DataOutputStream ds;
   	IoContainer i = new IoContainer();
   	 try
   	 {
   	 ds = new DataOutputStream(new FileOutputStream(TP.saveFile));
   	 }
   	 catch (FileNotFoundException ex)
   	 {
   		 System.out.println("Cannot Save File. File not Found");
   		 return;
   	 }
   	 //Save SaveVersion :1
   	 //Save Calibration :2
   	 //Save Tab Number  :0
   	 //Save Tab Names	:0
   	 //Save Thresholds	:0
   	 //Save Dendrite Width	:0
   	 //Save Dendrite Number	:0
   	 //Save Dendrite Info	:0
   	 //Save cell region number	:0
   	 //Save cell region Info	:0
   	 //Save punctaGroupData		:0
   	 //Save dendrite Ignore Info	:0
   	 i.writeInt(ds, "SaveVersion", currentSaveVersion);
   	 i.writeDouble(ds, "Save Calibration", calibration);
   	 TP.saveTabInfo(ds, i, RGT.getTabCount());
   	 for(int k = 0; k < RGT.getTabCount(); k++)
   	 {
   		 i.writeString(ds,"tab name", RGT.getTitleAt(k));
   	 }
   	 Dendrite[] mD = TP.getDendrites();
   	 TP.saveThresholdIndo(ds, i,mD);
   	 TP.saveDendriteWidth(ds, i);
   	 TP.saveDendriteInfo(ds, i,mD);
   	 TP.saveCellInfo(ds, i);
   	 
   	 boolean[] bl;
   	 for(int k = 0; k < RGT.getTabCount(); k++)
   		 {
   		 //TP.savePunctaInfo(ds, i,k,mD);
   		 bl = ((ColorTabs)RGT.getComponentAt(k)).Red.getPunctaIgnoreList();
   		 i.writeBooleanArray(ds, "Puncta ignore list", bl);
   		bl = ((ColorTabs)RGT.getComponentAt(k)).Green.getPunctaIgnoreList();
  		 i.writeBooleanArray(ds, "Puncta ignore list", bl);
  		bl = ((ColorTabs)RGT.getComponentAt(k)).Blue.getPunctaIgnoreList();
  		 i.writeBooleanArray(ds, "Puncta ignore list", bl);
   		 }
   	 
   	 TP.saveDendriteIgnoreInfo(ds, i, RGT.getTabCount(),RGT);
   	 try
   	 {
   	 ds.flush();
   	 }
   	 catch(IOException ex)
   	 {
   		 System.out.println("Cannot flush");
   	 } 
    }
    
    /**
     * Creates the save file on disk (if needed) and writes the current
     * region/threshold/puncta state to it.
     */
    public void saveRegionInformation()
    {
    	makeSaveFile();
    	saveToFile();    	
    }
    
    /**
     * Prompts the user for a new tab (region group) name via an
     * {@link OptionsFrame}, and if supplied, appends an empty
     * {@link DendriteContainer} to the dendrite groups, adds a new
     * {@link ColorTabs} to the region group tab, records the name in
     * the {@link LogInfo}, selects the new tab and repaints.
     */
    public void addTab()
    {   
    	String name = new OptionsFrame(this).newCategories(parent);
    	if(name == null)
    		return;
    	
    	DendriteContainer[] dC = new DendriteContainer[TP.myDendritesGroups.length + 1];
    	System.arraycopy(TP.myDendritesGroups, 0, dC, 0, TP.myDendritesGroups.length);
    	dC[TP.myDendritesGroups.length] = new DendriteContainer(new Dendrite[10]);
    	TP.myDendritesGroups = dC;
    	RGT.addTab(name, new ColorTabs(this,RGT.getTabCount()));
    	lI.addTab(name);
    	RGT.setSelectedIndex(RGT.getTabCount()-1);
    	repaint();
    }
    
    /**
     * Adds a Sholl-analysis ring set to the image. The currently used
     * path calls {@link TiffPanel#normalizedSholl} with a fixed radius
     * step of 10 units; the commented-out block shows an earlier manual
     * construction of {@link ShollDendrite} objects at increasing radii.
     */
    public void addSholl(Point p)
    {       	
    	/*Point circleCenter = p;
    	int radiusWidth = 10;
    	int startRadius = 50;
    	for(int k = startRadius; k < TP.imageWidth / 2; k += radiusWidth * 2)
    	{	
    		TP.addDendrite(new ShollDendrite(new dendriteWidth(1),TP.dendriteWatch,TP.fL.getCurrentDendriteGroup(),circleCenter,k,radiusWidth));
    	}*/
    	TP.normalizedSholl(this.getCalibration(),10,0);
    	TP.repaint();
    	
    }
    
    /**
     * Deletes a region-group tab. After user confirmation via an
     * {@link OptionsFrame}, removes every count/spine/cell event
     * listener belonging to the specified group, shrinks the
     * {@link TiffPanel#myDendritesGroups} array by one, removes cells
     * assigned to that group, deletes the tab from the
     * {@link RegionGroupTab} and the {@link LogInfo}, and repaints.
     */
    public void deleteTab(int group)
    {
    	if(new OptionsFrame(this).confirmDelete(parent))
    	{
        int k = 0;
    	while(k < countEventListenerList.size())
    	{    		
    		if(((CountEventListener)countEventListenerList.elementAt(k)).getGroup() == group)
    			{
    			countEventListenerList.removeElementAt(k);    			
    			}
    		else
    			k++;
    	}
    	k = 0;
    	while(k < spineEventListenerList.size())
    	{    	
    		if(((SpineEventListener)spineEventListenerList.elementAt(k)).getGroup() == group)
    			{
    			spineEventListenerList.removeElementAt(k);    			
    			}
    		else
    			k++;
    	}
    	k = 0;
    	while(k < cellEventListenerList.size())
    	{    	
    		if(((CellEventListener)cellEventListenerList.elementAt(k)).getGroup() == group)
    			{
    			cellEventListenerList.removeElementAt(k);    			
    			}
    		else
    			k++;
    	}
    	
    	TP.myDendritesGroups[group] = null;    	
    	DendriteContainer[] dC = new DendriteContainer[TP.myDendritesGroups.length - 1];    	
    	int counter = 0;
    	
    	for(k = 0; k < TP.myDendritesGroups.length; k++) //change Dendrite groups
    	{
    		if(k != group)
    		{
    			dC[counter] = TP.myDendritesGroups[k];    			    			
    			counter++;
    		}
    	}    	
    	TP.removeCells(group);
    	TP.myDendritesGroups = dC;    	
    	RGT.deleteTab(group);
    	lI.deleteTab(group);
    	TP.repaint();
    	}
    }
    
    /**
     * Legacy profile loader. Clears all event listener vectors, opens
     * {@code TP.saveFile} for reading and (unless the file has the
     * older {@code .nrn} extension) reads the save-version header. It
     * then reads calibration, tab count, tab names, threshold info,
     * dendrite widths, dendrite info, cell info, per-tab puncta ignore
     * lists and per-tab dendrite ignore lists, rebuilds the UI tabs to
     * match, recomputes the threshold image, fires listener
     * notifications and forces garbage collection. Used as a fallback
     * path for older saves.
     */
    public void loadFromFile()
    {
    	 //Load Save Version	:1
    	 //Load Calibration :2
    	 //Load Tab Number 	:0
      	 //Load Thresholds	:0
      	 //Load Dendrite Width	:0
      	 //Load Dendrite Number	:0
      	 //Load Dendrite Info	:0
      	 //Load cell region number	:0
      	 //Load cell region Info	:0
      	 //Load punctaGroupData	:0
      	 //Load dendrite Ignore Info	:0
    	//int tabCount = 
       	
    	countEventListenerList = new Vector();
        spineEventListenerList = new Vector();
        cellEventListenerList = new Vector();
        thresholdEventListenerList = new Vector();
        
    	
       	 DataInputStream di;
       	 IoContainer i = new IoContainer();
       	 try
       	 {
       		 di = new DataInputStream(new FileInputStream(TP.saveFile));
       	 }
       	 catch(FileNotFoundException ex)
       	 {
       		 System.out.println("Cannot Load File. File not Found");
       		 return;
       	 }
       	 
       	 String[] extL = TP.saveFile.getName().split("\\.");
       	 String ext = extL[extL.length-1];
       	 int version = 0;       	 
       	 if(ext.compareTo("nrn") != 0)
       	 {       		 
       		 version = i.readInt(di, "LoadSaveVersion");
       	 }
       	
       	 double cal;
       	 if(version >= 2)
       		 cal = i.readDouble(di, "Load Calibration");
       	 
       	 //TOP.setCalibration(calibration);
       	 
       	 int tabCount = TP.loadTabCount(di, i,version);
       	 
       	 if(tabCount != TP.myDendritesGroups.length)
       		 TP.resetDendrites(tabCount);
       	 
       	 String[] s = new String[tabCount];
       	
       	RGT.removeAll();
       	 
       	 for(int k = 0; k < tabCount; k++)
       	 {
       		 RGT.addTab("temp",new ColorTabs(this,k));
       	 }
       	 for(int k = 0; k < tabCount; k++)
       	 {
       		 s[k] = i.readString(di,"tab name");
       		 RGT.setTitleAt(k, s[k]);       		 
       	 }       	 
       	 lI.transfectionConditions = s;       	 
       	 TP.myDendritesGroups = new DendriteContainer[RGT.getTabCount()];
       	 for(int k = 0; k < RGT.getTabCount(); k++)
       	 {
       		 TP.myDendritesGroups[k] = new DendriteContainer(new Dendrite[1]);
       	 }
       	 
       	 TP.loadThresholdInfo(di, i,version);
       	 TOP.resetThresholdOptionsPanel();
       	 TP.loadDendriteWidth(di, i,version);
       	 TP.loadDendriteInfo(di, i,version,RGT.getGroupList());
       	 TP.loadCellInfo(di, i,version,RGT.getGroupList());     
       	 
       	 loadFilecountPuncta();   	
       	        	 
       	 boolean[] red;
       	 boolean[] green;
       	 boolean[] blue;       	       	
       	
     	for(int k = 0; k < tabCount; k++)
 		 {  
 		 red = i.readBooleanArray(di,"Puncta Ignore Info");
 		 green = i.readBooleanArray(di,"Puncta Ignore Info");
 		 blue = i.readBooleanArray(di,"Puncta Ignore Info");
 		 ((ColorTabs)RGT.getComponentAt(k)).loadPunctaIgnoreList(red, green, blue);
 		 ((ColorTabs)RGT.getComponentAt(k)).checkSize();
 		 }
       	 
       	 
       	boolean[] redB;
        boolean[] greenB;
       	boolean[] blueB;
       	
       	 for(int k = 0; k < tabCount; k++)
       	 {       		 
       		 redB = i.readBooleanArray(di,"Dendrite Ignore Info");
           	 greenB = i.readBooleanArray(di,"Dendrite Ignore Info");
           	 blueB = i.readBooleanArray(di,"Dendrite Ignore Info");
           	 ((ColorTabs)RGT.getComponentAt(k)).loadDendriteIgnoreList(redB);       		
       	 }	        	 
       	 
       	TP.createThresholdImage();  
       	
       	notifyCountEventListeners();
      	notifyCellEventListeners();
      	notifySpineEventListeners();
      	System.gc();
       	 
    }
    
    /**
     * Removes every registered count, spine and cell event listener
     * whose group index is greater than or equal to the supplied group.
     * Used when tabs are removed or the tab count is reduced.
     */
    public void trimNeuronEventListeners(int group)
    {
    	int k = 0;
    	while(k < countEventListenerList.size())
    	{    		
    		if(((CountEventListener)countEventListenerList.elementAt(k)).getGroup() >= group)
    			{
    			countEventListenerList.removeElementAt(k);    			
    			}
    		else
    			k++;
    	}
    	k = 0;
    	while(k < spineEventListenerList.size())
    	{    	
    		if(((SpineEventListener)spineEventListenerList.elementAt(k)).getGroup() >= group)
    			{
    			spineEventListenerList.removeElementAt(k);    			
    			}
    		else
    			k++;
    	}
    	k = 0;
    	while(k < cellEventListenerList.size())
    	{    	
    		if(((CellEventListener)cellEventListenerList.elementAt(k)).getGroup() >= group)
    			{
    			cellEventListenerList.removeElementAt(k);    			
    			}
    		else
    			k++;
    	}
    }
    
    /**
     * Current profile loader. Behaves like {@link #loadFromFile()} but
     * lazily counts puncta the first time it encounters a non-empty
     * ignore list. Clears all event listener vectors, reads the save
     * version (if the file extension is not {@code .nrn}), calibration,
     * tab names, thresholds, dendrite and cell info, and the per-tab
     * puncta/dendrite ignore lists. When any of the ignore lists has
     * entries it triggers {@link #loadFilecountPuncta()} and notifies
     * count listeners. After loading, recreates the threshold image,
     * fires count/cell/spine notifications and forces garbage collection.
     */
    public void loadFromFile2()
    {
    	 //Load Save Version	:1
    	 //Load Calibration :2
    	 //Load Tab Number 	:0
      	 //Load Thresholds	:0
      	 //Load Dendrite Width	:0
      	 //Load Dendrite Number	:0
      	 //Load Dendrite Info	:0
      	 //Load cell region number	:0
      	 //Load cell region Info	:0
      	 //Load punctaGroupData	:0
      	 //Load dendrite Ignore Info	:0
    	//int tabCount = 
    	countEventListenerList = new Vector();
        spineEventListenerList = new Vector();
        cellEventListenerList = new Vector();
        thresholdEventListenerList = new Vector();
        
       	 DataInputStream di;
       	 IoContainer i = new IoContainer();
       	 try
       	 {
       		 di = new DataInputStream(new FileInputStream(TP.saveFile));
       	 }
       	 catch(FileNotFoundException ex)
       	 {
       		 System.out.println("Cannot Load File. File not Found");
       		 return;
       	 }
       	 
       	 String[] extL = TP.saveFile.getName().split("\\.");
       	 String ext = extL[extL.length-1];
       	 int version = 0;       	 
       	 if(ext.compareTo("nrn") != 0)
       	 {       		 
       		 version = i.readInt(di, "LoadSaveVersion");
       	 }
       	
       	 double cal;
       	 if(version >= 2)
       		 cal = i.readDouble(di, "Load Calibration");
       	 
       	 int tabCount = TP.loadTabCount(di, i,version);
       	 
       	 if(tabCount != TP.myDendritesGroups.length)
       		 TP.resetDendrites(tabCount);
       	 
       	 String[] s = new String[tabCount];
       	
       	 RGT.removeAll();
       	 for(int k = 0; k < tabCount; k++)
       	 {
       		 RGT.addTab("temp",new ColorTabs(this,k));
       	 }
       	 
       	 for(int k = 0; k < tabCount; k++)
       	 {
       		 s[k] = i.readString(di,"tab name");
       		 RGT.setTitleAt(k, s[k]);       		 
       	 }       	 
       	 lI.transfectionConditions = s; 
       	 
       	 TP.loadThresholdInfo(di, i,version);
       	 TOP.resetThresholdOptionsPanel();
       	 TP.loadDendriteWidth(di, i,version);
       	 TP.loadDendriteInfo(di, i,version,RGT.getGroupList());
       	 TP.loadCellInfo(di, i,version,RGT.getGroupList());     
       	 
       	 boolean hasCounted = false;
       	 
       	 
       	 boolean[] red;
       	 boolean[] green;
       	 boolean[] blue;
       	 
       	 
       	 
     	for(int k = 0; k < tabCount; k++)
 		 {  
 		 red = i.readBooleanArray(di,"Puncta Ignore Info");
 		 green = i.readBooleanArray(di,"Puncta Ignore Info");
 		 blue = i.readBooleanArray(di,"Puncta Ignore Info"); 		 
 		 if(!hasCounted && (red.length > 0 || green.length > 0 || blue.length > 0))
 			{
 			 loadFilecountPuncta();   	
 	       	 notifyCountEventListeners();
 	       	 hasCounted = true;
 			}
 		 ((ColorTabs)RGT.getComponentAt(k)).loadPunctaIgnoreList(red, green, blue);
 		 ((ColorTabs)RGT.getComponentAt(k)).checkSize();
 		 }
       	 
       	 
       	boolean[] redB;
        boolean[] greenB;
       	boolean[] blueB;
       	
       	 for(int k = 0; k < tabCount; k++)
       	 {       		 
       		 redB = i.readBooleanArray(di,"Dendrite Ignore Info");
           	 greenB = i.readBooleanArray(di,"Dendrite Ignore Info");
           	 blueB = i.readBooleanArray(di,"Dendrite Ignore Info");
           	 ((ColorTabs)RGT.getComponentAt(k)).loadDendriteIgnoreList(redB);       		
       	 }	        	 
       	 
       	TP.createThresholdImage();  
       	
       	notifyCountEventListeners();
      	notifyCellEventListeners();
      	notifySpineEventListeners();
      	System.gc();
       	 
    }
    
    /**
     * Ensures that a save file exists for the currently displayed image
     * by resolving the save name, creating a {@link File} in the save
     * directory and attempting to create the file on disk. Silently
     * aborts and nulls {@code TP.saveFile} on I/O failure.
     */
    private void makeSaveFile()
    { 
   	 if(!TP.saveDir.isDirectory())
   		 return;
   	 
   	 String saveName = TP.getSaveName();    	     	 	 
   	 TP.saveFile = new File(TP.saveDir,saveName);
   	 
   	 try
   	{
   		if(TP.saveFile.createNewFile())
   		{
   			
   		}
   		else
   		{
   			   			
   		}
   	}
   	 catch(IOException ex)
   	 {
   		 System.out.println("could not create" + saveName);
   		 TP.saveFile = null;
   	 }
   	 
   	 
    }
    
    /**
     * Attempts to load a legacy {@code .nrn} profile that matches the
     * base name of the current save file. If such a file exists,
     * delegates to {@link #loadFromFile()} and then restores
     * {@code TP.saveFile} back to the new-style name.
     */
    public void loadPreviousVersion(String saveName)
    {
    	String[] list = saveName.split("\\.");
    	System.out.println(list[0]);
    	String base ="";
    	for(int k = 0; k < list.length-1; k++)
    	{
    		base = base + list[k];    		
    	}
    	
    	
    	
    	
    		TP.saveFile	 = new File(TP.saveDir,base + ".nrn");
    		
    		if(TP.saveFile.isFile())
    		{
    			loadFromFile();
    			TP.saveFile = new File(TP.saveDir,saveName);
    			return;
    		}
    	
    	
    }
    
        
    /**
     * Checks the save directory for an existing profile for the current
     * image. If the canonical save file exists, loads it via
     * {@link #loadFromFile2()}; otherwise attempts to load a legacy
     * {@code .nrn} profile through {@link #loadPreviousVersion}. Any
     * {@link SecurityException} is caught and the save file reference
     * is nulled.
     */
    public void checkLoadFile()
    {
    	if(!TP.saveDir.isDirectory())
      		 return;
    	String saveName = TP.getSaveName();    
    	
      	 TP.saveFile = new File(TP.saveDir,saveName);
      	 
      	 try
      	{
      		if(TP.saveFile.isFile())
      		{
      			loadFromFile2();      			
      		}
      		else
      		{
      			loadPreviousVersion(saveName);
      		}
      	}
      	 catch(SecurityException ex)
      	 {
      		 System.out.println("could not load" + saveName);
      		 TP.saveFile = null;
      	 }
    }
    
    
    /**
     * Switches the image panel into "add simple dendrite" mouse mode.
     */
    public void addDendrite()
    {       
        TP.setMouseMode(1);
    }
    
    /**
     * Switches the image panel into "add complex (multi-segment)
     * dendrite" mouse mode.
     */
    public void addComplexDendrite()
    {
    	TP.setMouseMode(2);
    }
    

    /**
     * Applies new red, green and blue threshold values to the image
     * panel and recomputes the threshold image. Does not touch the
     * "old threshold" tracking used to invalidate caches.
     */
    public void setThreshold(int r,int g,int b)
    {
    	//changes all the thresholds
    	//does not reset old threshold to 256;
        TP.setredThreshold(r);
        TP.setgreenThreshold(g);
        TP.setblueThreshold(b);
        TP.thresholdImage();        
    }
    
    /**
     * Applies new lookup-table values to the image panel, rebuilds the
     * main image and repaints.
     */
    public void setLookUp(int r, int g, int b)
    {
    	TP.setLookUp(r,g,b);
    	TP.createMainImage();
    	//TP.invalidate();
    	TP.repaint();
    }
    
    /**
     * Applies new "spine threshold" values (stredth/stgreenth/stblueth)
     * for the red, green and blue channels without recomputing the
     * threshold image.
     */
    public void setstThreshold(int r,int g,int b)
    {
    	//changes all the thresholds
    	//does not reset old threshold to 256;
        TP.setstredThreshold(r);
        TP.setstgreenThreshold(g);
        TP.setstblueThreshold(b);        
        //TP.thresholdImage();

    }
    
    /**
     * Reloads the default color channel settings from the image panel.
     */
    public void resetColors()
    {
    	TP.loadColors();
    }

    /**
     * Sets the dendrite width used when drawing new dendrites. Also
     * updates the text field in the {@link ThresholdOptionsPanel} if
     * it currently shows a different value, then repaints the image.
     */
    public void setDendriteWidth(int w)
    {
        TP.setDendriteWidth(w);
        if(new Integer(TOP.dendriteWidth.getText()).intValue() != w)
        {
        	TOP.dendriteWidth.setText(new Integer(w).toString());
        	TOP.repaint();
        }
        //TP.paintImage();
        TP.repaint();
    }

    /**
     * Returns the color channel index (R/G/B) that is currently
     * selected in the {@link RegionGroupTab}.
     */
    public int getCurrentColorGroup()
    {
        return RGT.getCurrentColor();
    }

    /**
     * Returns the dendrite {@link Group} that corresponds to the
     * currently selected tab.
     */
    public Group getCurrentDendriteGroup()
    {
        return RGT.getCurrentDendriteGroup();
    }

    /**
     * Repaints the image panel if it has been initialized.
     */
    public void repaintPane()
    {
    	if(TP!=null)
        TP.repaint();
    }
    
    /**
     * Updates the tab/category names stored in the {@link LogInfo}.
     */
    public void setCategories(String[] s)
    {
    	lI.setTabs(s);
    	
    }

    /**
     * Sets the microns-per-pixel calibration value used throughout the
     * analysis and mirrors it into the {@link ThresholdOptionsPanel}
     * text field if it does not already match. Triggers a data-pane
     * repaint so that displayed measurements update immediately.
     */
    public void setCalibration(double d)
    {
        calibration = d;
        if(new Double(TOP.calibrationField.getText()).doubleValue() != d)
        	{
        	TOP.setCalibration(d);
        	TOP.repaint();
        	}
        this.repaintDataPane();
    }
    
    /**
     * Toggles the image panel's "speed" flag, which is used to skip
     * expensive rendering during batch processing, and repaints.
     */
    public void toggleSpeed()
    {
    	if(TP.speed)
    	TP.speed = false;
    	else
    	TP.speed = true;
    	//TP.paintImage();
    	TP.repaint();
    }
    
    /**
     * Returns the current red channel threshold.
     */
    public int getRedThreshold()
    {
    	return TP.redth;
    }
    
    /**
     * Returns the current green channel threshold.
     */
    public int getGreenThreshold()
    {
    	return TP.greenth;
    }
    
    /**
     * Returns the current blue channel threshold.
     */
    public int getBlueThreshold()
    {
    	return TP.blueth;
    }
    
    /**
     * Returns the current red spine threshold.
     */
    public int getstRedThreshold()
    {
    	return TP.stredth;
    }
    
    /**
     * Returns the current green spine threshold.
     */
    public int getstGreenThreshold()
    {
    	return TP.stgreenth;
    }
    
    /**
     * Returns the current blue spine threshold.
     */
    public int getstBlueThreshold()
    {
    	return TP.stblueth;
    }
    
    /**
     * Returns the {@link Color} used to render the red threshold
     * overlay, falling back to opaque white if the image panel or its
     * red threshold color holder is not yet initialized.
     */
    public Color getRedThresholdColor()
    {
    	if(TP == null || TP.redTC == null)
    		return new Color(255,255,255,255);
    	return TP.redTC.c;
    }
    /**
     * Returns the {@link Color} used to render the green threshold
     * overlay, falling back to opaque white if no color has been set.
     */
    public Color getGreenThresholdColor()
    {
    	if(TP == null || TP.greenTC == null)
    		return new Color(255,255,255,255);
    	return TP.greenTC.c;
    }
    /**
     * Returns the {@link Color} used to render the blue threshold
     * overlay, falling back to opaque white if no color has been set.
     */
    public Color getBlueThresholdColor()
    {
    	if(TP == null || TP.blueTC == null)
    		return new Color(255,255,255,255);
    	return TP.blueTC.c;
    }

    /**
     * Handler invoked by {@link Frame2_this_mouseWheelAdapter} when the
     * mouse wheel moves over this panel. Currently a no-op.
     */
    public void this_mouseWheelMoved(MouseWheelEvent e) {


    }
    
    /**
     * Refreshes the zoom indicator on the {@link ImagePropertiesPanel}.
     */
    public void updateZoom()
    {
    	IP.zLabel.updateZoom();
    }

    /**
     * Handler invoked by {@link Frame2_this_mouseAdapter} for mouse
     * press events. Currently a no-op.
     */
    public void this_mouseClicked(MouseEvent e) {

    }

    /**
     * Handler invoked by {@link Frame2_this_mouseMotionAdapter} for
     * mouse move events. Currently a no-op.
     */
    public void this_mouseMoved(MouseEvent e) {

    }
    
    /**
     * Entry point for batch-processing. The current implementation is
     * disabled; it previously launched {@link #run()} on a worker
     * {@link Thread}.
     */
    public void batchProcess()
    {
    	//Thread runner;
    	//runner = new Thread(this,"batch Process");
    	//runner.start();
    	//run();
    }
    	
    /**
     * {@link Runnable} implementation used for batch processing.
     * Snapshots the current threshold settings, enables high-speed
     * rendering, then iterates through every file in {@code fileName}.
     * For each image it reapplies the saved thresholds, recomputes the
     * threshold image, resets the threshold options panel, counts
     * puncta, waits for worker threads to finish and advances to the
     * next image. Restores normal rendering speed and repaints at
     * the end.
     */
    public void run()
    {
    	int r = TP.redth;
    	int g = TP.greenth;
    	int b = TP.blueth;
    	int str = TP.stredth;
    	int stg = TP.stgreenth;
    	int stb = TP.stblueth;
    	
    	TP.speed = true;
    	
    	while(imgCount < fileName.length)
    	{
    		TP.setredThreshold(r);
            TP.setgreenThreshold(g);
            TP.setblueThreshold(b);
            TP.stredth = str;
        	TP.stgreenth =stg;
        	TP.stblueth = stb;
            
    		TP.thresholdImage();
    		TOP.resetThresholdOptionsPanel();
            
    		countPuncta();    
    		checkThreads();
    		if(imgCount +1 < fileName.length)
    			nextImage();
    	}    	
    	TP.speed = false;
    	TP.repaint();
    	
    }
        
    /**
     * Placeholder hook for ad-hoc test code; currently empty.
     */
    public void test()
    {
    	
    }
    
    /**
     * Moves to the previous image in the file list. If the selection
     * would wrap around, beeps and jumps to the last file instead.
     */
    public void previousImage()
    {
    	if(imgCount -1 < 0)
    	{
    		Toolkit.getDefaultToolkit().beep();
    		fileBox.setSelectedIndex(fileName.length - 1);
    	}
    	else
    		fileBox.setSelectedIndex(imgCount -1);
    }
    
    /**
     * Moves to the next image in the file list. If the selection would
     * overflow, beeps and wraps back to the first file.
     */
    public void nextImage()
    {
    	if(imgCount +1 >= fileName.length)
    	{
    		Toolkit.getDefaultToolkit().beep();
    		fileBox.setSelectedIndex(0);
    	}
    	else
    		fileBox.setSelectedIndex(imgCount + 1);
    	
    }
    
    /**
     * Switches the active image to the one at {@code imageIndex}.
     * First saves the current region state. If auto-logging is enabled
     * the current data is printed before the switch. Clears dendrite
     * and cell state on the {@link TiffPanel}, loads the new image,
     * runs {@link #checkLoadFile()} to pick up any saved profile,
     * resets the threshold options panel and repaints.
     */
    public void setNextImage(int imageIndex)
    {
    	saveRegionInformation();
    	
    	imgCount = imageIndex;
    	
    	if(lI.autolog)
    		printData();
    	
    	TP.resetDendrites();
    	TP.setImage(imgCount);
    	TP.resetCells();
    	for(int k = 0; k <RGT.getTabCount();k++)
        {            
            //((ColorTabs)RGT.getComponentAt(k)).Red.NDP.repaint();         
           //((ColorTabs)RGT.getComponentAt(k)).Green.NDP.repaint();            
            //((ColorTabs)RGT.getComponentAt(k)).Blue.NDP.repaint();
        }    	
    	checkLoadFile();
    	TOP.resetThresholdOptionsPanel();    
    	
    	TP.repaint();
    	repaintDataPane();
    }
    
    /**
     * Returns whether auto-logging (automatic data printing on image
     * change) is enabled in the {@link LogInfo}.
     */
    public boolean isAutoLog()
    {
    	return lI.autolog;
    }
    
    /**
     * Advances to the next image while running a batch. Saves the
     * region information, prints data, resets dendrites and cells,
     * loads the new image, rebuilds the threshold image without
     * interactive repaints, resets the threshold options panel and
     * repaints. Returns immediately if there is no next image.
     */
    public void batchnextImage()
    {
    	
    	saveRegionInformation();
    	
    	imgCount++;
    	if(imgCount >= fileName.length)
    		return;
    	
    	printData();
    	TP.resetDendrites();
    	TP.setImage(imgCount);
    	TP.resetCells();
    	TP.batchcreateImage();
    	TOP.resetThresholdOptionsPanel();   
    	TP.repaint();
    }
    
    /**
     * Writes all enabled data tables to the log directory for the
     * currently displayed image. Each data printer (image/dendrite/
     * individual variants for puncta, spines and cells) is created
     * lazily on first use with the configured {@link LogInfo} and a
     * fresh {@link DataPrinterManager}, then asked to print data for
     * the current file and flushed. Printing of each category is
     * gated on the matching boolean flag in {@link LogInfo}.
     */
    public void printData()
    {    	
    	if(lI.pI)
    	{	
    	if(iP == null)
    		iP = new ImagePunctaDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    		
    	iP.printData(TP.getCurrentFile());	
    	iP.flush();
    	}
    	if(lI.cI)
    	{
    		if(cP == null)
    			cP  = new ImageCellDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	cP.printData(TP.getCurrentFile());	
    	cP.flush();
    	}
    	if(lI.sI)
    	{
    		if(sP == null)
    			sP = new ImageSpineDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	sP.printData(TP.getCurrentFile());	
    	sP.flush();
    	}
    	if(lI.ppI)
    	{
    		if(ipP == null)
    			ipP = new IndividualPunctaDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	ipP.printData(TP.getCurrentFile());	
    	ipP.flush();
    	}
    	if(lI.ssI)
    	{
    		if(isP == null)
    			isP = new IndividualSpineDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	isP.printData(TP.getCurrentFile());	
    	isP.flush();
    	}
    	if(lI.ccI)
    	{
    		if(icP == null)
    			icP = new IndividualCellDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	icP.printData(TP.getCurrentFile());	
    	icP.flush();
    	}
    	
    	if(lI.dsI)
    	{
    		if(dsP == null)
    			dsP = new DendriteSpineDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	dsP.printData(TP.getCurrentFile());	
    	dsP.flush();
    	}
    	if(lI.dpI)
    	{
    		if(dpP == null)
    			dpP = new DendritePunctaDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    		
    	dpP.printData(TP.getCurrentFile());	
    	dpP.flush();
    	}
    	
    }
           
    /**
     * Reconfigures the log directory and rebuilds every enabled data
     * printer so that subsequent output is written to {@code fName}.
     * For each printer flagged in the {@link LogInfo}, any existing
     * instance is finished (flushed/closed) and replaced with a new
     * one bound to the new directory and a fresh
     * {@link DataPrinterManager}.
     */
    public void setLog(String fName)
    {
    	logDirectory = fName; 
    	
    	if(lI.pI)
    	{
    	if(iP != null)
    		iP.finishPrinting();
    	
    	iP = new ImagePunctaDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	if(lI.cI)
    	{
    	
    		if(cP != null)
    			cP.finishPrinting();
    		cP  = new ImageCellDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));
    	
    	}
    	if(lI.sI)
    	{
    		if(sP != null)
    			sP.finishPrinting();
    	
    		sP = new ImageSpineDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	if(lI.ppI)
    	{
    		if(ipP != null)
    			ipP.finishPrinting();
    		ipP  = new IndividualPunctaDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	if(lI.ssI)
    	{
    	
    		if(isP != null)
    			isP.finishPrinting();
    		isP  = new IndividualSpineDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	if(lI.ccI)
    	{
    		if(icP != null)
    			icP.finishPrinting();
    		icP  = new IndividualCellDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	
    	if(lI.dsI)
    	{
    		if(dsP != null)
    			dsP.finishPrinting();
    		dsP  = new DendriteSpineDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	if(lI.dpI)
    	{
    		if(dpP != null)
    			dpP.finishPrinting();
    		dpP  = new DendritePunctaDataPrinter(logDirectory,lI,this,new DataPrinterManager(RGT));	
    	
    	}
    	    	
    	
        
        
        
        
        
        
        
        
        
    	
    }
    
    /**
     * Scrolls the image view so that the bounding box of the specified
     * dendrite (by row index, within the current dendrite group) is
     * visible. The computed view position is adjusted for the current
     * zoom and clamped so it never exceeds the scaled image
     * dimensions.
     */
    public void focusDendrite(int row)
    {    	
    	
    	
    	
    	Point p = TP.myDendritesGroups[getCurrentDendriteGroup().getValue()].myDendrites[row].dendriteArea.getBounds().getLocation();   	
    	p.setLocation((p.x * TP.zoom) / 100, (p.y * TP.zoom) / 100);    	    	
    	
    	if(p.x + TSP.getViewport().getWidth() > (TP.imageWidth* TP.zoom) / 100)
    	{
    		p.x = (TP.imageWidth* TP.zoom) / 100 - TSP.getViewport().getWidth();
    	}
    	
    	if((TP.imageWidth* TP.zoom) / 100 < TSP.getViewport().getWidth())
    		p.x = 0;
    	
    	
    	if(p.y + TSP.getViewport().getHeight() > (TP.imageHeight* TP.zoom) / 100)
    	{
    		p.y = (TP.imageHeight* TP.zoom) / 100 - TSP.getViewport().getHeight();
    	}    	
    	
    	if((TP.imageHeight* TP.zoom) / 100 < TSP.getViewport().getHeight())
    		p.y = 0;
    	
    	TSP.getViewport().setViewPosition(p);
    	//TSP.getViewport().revalidate();
    }
    
    /**
     * Switches the image panel into "add spine (measure)" mouse mode.
     */
    public void addSpine()
    {
    	TP.setMouseMode(5);
    }
    
    /**
     * Returns the list of color/region {@link Group}s known to the
     * {@link RegionGroupTab}.
     */
    public Group[] getGroupList()
    {
    	return RGT.getGroupList();
    }
    
    /**
     * Switches the image panel into "remove spine" mouse mode.
     */
    public void removeSpine()
    {
    	TP.setMouseMode(9);
    }
    
    /**
     * Switches the image panel into "add spine (count)" mouse mode,
     * used when only a spine count is wanted.
     */
    public void addSpine2()
    {
    	TP.setMouseMode(8);
    }
    
    /**
     * Returns the current microns-per-pixel calibration value.
     */
    public double getCalibration()
    {
    	return this.calibration;
    }
    
    /**
     * Returns the average number of spines per dendrite for the given
     * group, computed using the current calibration. Returns 0 when
     * the group contains no dendrites.
     */
    public double getAveSpineNum(int group){    	
    double total = 0;
    int k = 0;
    for(k = 0; k < TP.myDendritesGroups[group].myDendrites.length; k++)
    {
    	if(TP.myDendritesGroups[group].myDendrites != null)
    	total += TP.myDendritesGroups[group].myDendrites[k].getSpineNum(getCalibration());
    }    
    if(k == 0)
    	return 0;
    
    return total / ((double)k) ;
    }
    
    /**
     * Returns the average spine width across all dendrites in the
     * given group. Returns -1 if the group contains no dendrites.
     */
    public double getAveSpineWidth(int group){
    	double total = 0;
    	double temp = 0;
        int k = 0;
        for(k = 0; k < TP.myDendritesGroups[group].myDendrites.length; k++)
        {
        	if(TP.myDendritesGroups[group].myDendrites != null)
        	total += TP.myDendritesGroups[group].myDendrites[k].getAveSpineWidth(getCalibration());        	
        	if(temp != 0)
        		total += temp;
        }    
        if(k == 0)
        	return -1;
        return total / ((double)k);	
    }
    /**
     * Returns the average spine length across all dendrites in the
     * given group. Returns -1 if the group contains no dendrites.
     */
    public double getAveSpineLength(int group){
    	double total = 0;
    	double temp = 0;
        int k = 0;
        for(k = 0; k < TP.myDendritesGroups[group].myDendrites.length; k++)
        {
        	if(TP.myDendritesGroups[group].myDendrites != null)
        	total += TP.myDendritesGroups[group].myDendrites[k].getAveSpineLength(getCalibration());
        	if(temp != 0)
        		total += temp;
        }
        if(k == 0)
        	return -1;
        
        return total / ((double)k);
    }
    /**
     * Returns the average number of spines of the specified
     * morphological type across all dendrites in the group. Returns 0
     * when the group is empty.
     */
    public double getSpineTypeNum(int group,int type){
    	double total = 0;
        int k = 0;
        for(k = 0; k < TP.myDendritesGroups[group].myDendrites.length; k++)
        {
        	if(TP.myDendritesGroups[group].myDendrites != null)
        	total += TP.myDendritesGroups[group].myDendrites[k].getSpineTypeNum(type, getCalibration());
        } 
        if(k == 0)
        	return 0;
        return total / ((double)k);
    }
    
    /**
     * Toggles blind analysis mode. When blind is turned on the file
     * combo box is hidden from the region options panel so the user
     * cannot identify which image is currently displayed; when turned
     * off it is restored. Also notifies the region options panel and
     * repaints.
     */
    public void toggleBlind()
	{
		if(TP.blind)
		{
			TP.blind = false;
			ROP.add(fileBox);
		}
		else
		{
			TP.blind = true;
			ROP.remove(fileBox);
		}
		ROP.toggleBlind();
		TP.repaint();
	}

    /**
     * Returns true if the cell at index {@code k} belongs to the given
     * group and is currently watching the specified color channel.
     */
	public boolean showCell(int k, int color, int group)
    {
    	if(TP.myCells[k].groupMember.getValue() == group && TP.myCells[k].watchColor[color])
    		return true;
    	return false;
    }
    
    /**
     * Returns the integrated intensity of the cell at index {@code k}
     * for the given color channel.
     */
    public double getCellIntensity(int k, int color)
    {    	
    	return TP.myCells[k].getIntegratedCellIntensity(color);
    }
    
    /**
     * Returns the average intensity of the cell at index {@code k} in
     * the given color channel, normalized by the current calibration.
     */
    public double getCellAveIntensity(int k, int color)
    {
    	return TP.myCells[k].getAveCellIntensity(color, getCalibration());
    }
    
    /**
     * Returns the dendrite array for the given group, or {@code null}
     * if the image panel has not been initialized.
     */
    public Dendrite[] getDendrites(int groupMember)
    {    	
    	if(TP == null)
    		return null;    	
    	return TP.myDendritesGroups[groupMember].myDendrites;
    }
    
    /**
     * Returns the number of cells currently stored in the image panel
     * by scanning {@code TP.myCells} for the first null entry.
     */
    public int getCellNum()
    {
    	int k = 0;
    	for(k = 0; k < TP.myCells.length; k++ )
    	{
    		if(TP.myCells[k] == null)
    			{    			
    			break;
    			}
    	}
    	return k;
    }
    
    /**
     * Returns the mean integrated cell intensity for the given color
     * channel across all cells in the given group.
     */
    public double getAveCellIntensity(int color, int group)
    {
    	//Average Cell Average Intensity
    	return TP.getAverageCellIntensity(color, group);
    }
    
    /**
     * Returns the mean of the per-cell average intensities for the
     * given color channel and group.
     */
    public double getAveCellAveIntensity(int color, int group)
    {
    	//Average Cell Average Intensity
    	return TP.getAverageCellAveIntensity(color, group);
    }
    
    /**
     * Returns the number of cells registered for the given group.
     */
    public int getCellNumber(int group)
    {
    	return TP.getCellNumber(group);
    }
    
    /**
     * Returns the entire backing array of {@link CellBody}s from the
     * image panel.
     */
    public CellBody[] getCells()
    {
    	return TP.myCells;
    }
    
    /**
     * Switches the image panel into "add cell body" mouse mode.
     */
    public void addCell()
    {
    	TP.setMouseMode(4);
    }
    /**
     * Returns the {@code k}-th dendrite from the given group.
     */
    public Dendrite getDendrite(int group,int k)
    {
    	return TP.myDendritesGroups[group].myDendrites[k];
    }

    /**
     * Releases references to every sub-panel, closes the log writer
     * and runs garbage collection. Used when the frame is being torn
     * down so its large image buffers can be reclaimed.
     */
    public void dispose()
    {
        ROP = null;
        IP = null;
        TP = null;
        TOP = null;
        TSP = null;
        RGT = null;
        //super.dispose();
        pW.close();
        System.gc();
    }
    
    /**
     * Forces the TIFF scroll pane to repaint.
     */
    public void repaintScrollPane()
    {
    	TSP.repaint();
    }
    
    /**
     * Repaints every per-channel data pane (red/green/blue) across all
     * {@link ColorTabs} currently held by the region group tab. Used
     * after any change that invalidates the displayed measurements.
     */
    public void repaintDataPane()
    {
    	for(int k = 0; k < RGT.getComponentCount(); k++){
    	((ColorTabs)RGT.getComponentAt(k)).Red.repaintDataPane();    	
    	((ColorTabs)RGT.getComponentAt(k)).Green.repaintDataPane();
    	((ColorTabs)RGT.getComponentAt(k)).Blue.repaintDataPane();
    	}
    }
    
    /**
     * Toggles the auto-log flag in the {@link LogInfo}.
     */
    public void toggleAutoLog()
    {    	
    	lI.toggleAutoLog();    	
    }
    
    /**
     * Switches the image panel into "remove cell body" mouse mode.
     */
    public void removeCell()
    {
    	TP.setMouseMode(10);
    }
    
    /**
     * Reacts to changes in the file combo box: when a new file is
     * selected, loads that image through {@link #setNextImage(int)}.
     */
    public void itemStateChanged(ItemEvent e)
    {
    	if(e.getStateChange() == ItemEvent.SELECTED)
    		{
    			this.setNextImage(fileBox.getSelectedIndex());
    		}
    }
    
    /**
     * Sets the red threshold overlay color, invalidates the cached old
     * threshold so the image is recomputed, rebuilds the threshold
     * image and repaints.
     */
    public void setRedThresholdColor(Color c)
    {
    	TP.redTC.setColor(c);
    	TP.redTC.oldTh = 256;
    	TP.thresholdImage();
    	TP.repaint();
    }
    /**
     * Sets the blue threshold overlay color, invalidates the cached
     * old threshold, rebuilds the threshold image and repaints.
     */
    public void setBlueThresholdColor(Color c)
    {
    	TP.blueTC.setColor(c);
    	TP.blueTC.oldTh = 256;
    	TP.thresholdImage();
    	TP.repaint();    	
    }
    /**
     * Sets the green threshold overlay color, invalidates the cached
     * old threshold, rebuilds the threshold image and repaints.
     */
    public void setGreenThresholdColor(Color c)
    {
    	TP.greenTC.setColor(c);
    	TP.greenTC.oldTh = 256;
    	TP.thresholdImage();
    	TP.repaint();
    }
    
    /**
     * Installs a new array of {@link IgnoreCriteria} on the image
     * panel, triggers the automatic ignore pass and repaints.
     */
    public void setIgnoreCriteria(IgnoreCriteria[] ignoreCriteria)
    {
    	TP.setIgnoreCriteria(ignoreCriteria);
    	TP.autoIgnore();
    	TP.repaint();
    }
    
    /**
     * Toggles the on-image rendering of the spine-area radius. When
     * enabled, the image panel both draws the area guide and applies
     * radius-based spine ignore logic; when disabled both are turned
     * off. Repaints afterwards.
     */
    public void toggleRadiusView()
    {
    	if(TP.drawArea)
    		{
    		TP.drawArea = false;
    		TP.spineRadiusIgnore = false;
    		}
    	else
    		{
    		TP.drawArea = true;
    		TP.spineRadiusIgnore = true;
    		}
    
    	TP.repaint();
    }
    
    /**
     * Increases the spine-area radius by one pixel and repaints.
     */
    public void increaseSpineRadius()
    {
    	TP.spineAreaRadius += 1;
    	TP.repaint();
    }
    
    /**
     * Decreases the spine-area radius by one pixel (minimum zero) and
     * repaints.
     */
    public void decreaseSpineRadius()
    {
    	if(TP.spineAreaRadius > 0)
    	TP.spineAreaRadius -= 1;
    	TP.repaint();
    }
    
    /*public DendriteGroupData getDendriteGroupData(int groupMember, int color)
    {
    	switch(color)
    	{
    	case 0: return ((ColorTabs)RGT.getComponentAt(groupMember)).Red.NDP.neuronData; 
    	case 1: return ((ColorTabs)RGT.getComponentAt(groupMember)).Green.NDP.neuronData; 
    	case 2: return ((ColorTabs)RGT.getComponentAt(groupMember)).Blue.NDP.neuronData;
    	}
    	return null;
    }*/
    
    /**
     * Returns the image panel's current data mode (which governs what
     * measurements are collected/shown).
     */
    public int getDataMode()
    {
    	return TP.dataMode;
    }
    
    /**
     * Sets the image panel's current data mode.
     */
    public void setDataMode(int d)
    {
    	TP.dataMode = d;
    }
    
    /**
     * Runs colocalization measurement, but only if the current data
     * mode is 3 (colocalization). Fires threshold event listeners and
     * repaints the data panes so the new values are shown.
     */
    public void measureColocalization()
    {
    	if(getDataMode() == 3)
    		{
    		TP.measureColocalization();
    		notifyThrehsoldEventListeners();
    		repaintDataPane();
    		}
    }
    
    /**
     * Returns the array of {@link ColocalizationInfo} results for each
     * channel pair from the image panel.
     */
    public ColocalizationInfo[] getColocalizationInfo()
    {
    	return TP.getColocalizationInfo();
    }
    
    /**
     * Switches the image panel's mouse feature handler into mode 14
     * (reserved for experimental features).
     */
    public void testFeature()
    {
    	TP.tpmf.setMode(14);
    	
    }
    
    /**
     * Enables the spine-radius mode (currently just calls
     * {@link #toggleRadiusView()}).
     */
    public void activateSpineRadiusMode()
    {
    	toggleRadiusView();
    }
    
    /**
     * Creates a single horizontal dendrite that spans the full image
     * width, adds it to the image panel and immediately counts puncta
     * along it. Used as a quick "select all" command.
     */
    public void selectAll()
    {    	
    	Dendrite selectAll = new Dendrite(new dendriteWidth(1014),TP.dendriteWatch,TP.fL.getCurrentDendriteGroup());
    	selectAll.add(5, 512);
    	selectAll.add(1019, 512);    	
    	TP.addDendrite(selectAll);
    	TP.countPuncta(selectAll);
    	TP.repaint();
    }
    
    
        
    /**
     * Global keyboard dispatcher for the application. Reacts only to
     * key-pressed events while listening is enabled. Without Control
     * held down the function keys F1-F12 map to mouse modes (add
     * dendrite, complex dendrite, cell, remove cell, spines, etc.),
     * toggle radius/blind modes, print data, save, and set batch mode;
     * arrow keys step through images; letter keys trigger speed
     * toggle, Sholl analysis, zoom, radius view, spine width changes
     * and dendrite view mode; +/- zoom in/out; page up/down adjust the
     * spine radius. With Control held down a reduced set of shortcuts
     * handles removals and nextImage. Always returns {@code false} so
     * the event continues to propagate.
     */
    public boolean dispatchKeyEvent(KeyEvent e)
    {
    	
    	if(e.getID() == KeyEvent.KEY_PRESSED)    		
    	{	   
    		if(!e.isControlDown() & isListening)
    		{	
    			switch(e.getKeyCode())    		
    			{
    		case KeyEvent.VK_ESCAPE: countPuncta(); break;
    		case KeyEvent.VK_F1: TP.setMouseMode(1); break; //add dendrite
    		case KeyEvent.VK_F2: TP.setMouseMode(2); break; //add complex dendrite
    		case KeyEvent.VK_F3: TP.setMouseMode(4); break; //add cell
    		case KeyEvent.VK_F4: TP.setMouseMode(10); break; //remove cell
    		case KeyEvent.VK_F5: TP.setMouseMode(8); break; //add count spine
    		case KeyEvent.VK_F6: TP.setMouseMode(5); break; //add spine measure
    		case KeyEvent.VK_F7: TP.setMouseMode(9); break; //remove spine
    		case KeyEvent.VK_F8: toggleRadiusView(); break; // future 
    		case KeyEvent.VK_F9: toggleBlind(); break; // tiggle blind mode
    		case KeyEvent.VK_F10: printData(); break; // print data to logfile
    		case KeyEvent.VK_F11: saveRegionInformation(); break; // save info
    		case KeyEvent.VK_F12: TP.setMouseMode(0); break; // Batch recalculate
    		case KeyEvent.VK_RIGHT: nextImage(); break;
    		case KeyEvent.VK_LEFT: previousImage(); break;
    		case KeyEvent.VK_A: toggleSpeed(); break;
    		case KeyEvent.VK_T: testFeature(); break;
    		case KeyEvent.VK_S: TP.normalizedSholl(getCalibration(),10,0); break;
    		case KeyEvent.VK_EQUALS: zoomIn(); break;
    		case KeyEvent.VK_MINUS:  zoomOut(); break;
    		case KeyEvent.VK_R: toggleRadiusView(); break;
    		case KeyEvent.VK_PAGE_UP: increaseSpineRadius(); break;
    		case KeyEvent.VK_PAGE_DOWN: decreaseSpineRadius() ;break;
    		case KeyEvent.VK_PERIOD: TP.newSpineWidth(2);break;
    		case KeyEvent.VK_COMMA: TP.newSpineWidth(-2);break;
    		case KeyEvent.VK_V: TP.swicthDendriteViewMode();break;
    		default: break;
    			}
    		}
    	}
    		else 
    		{
    			if(e.isControlDown() & isListening)
    			{
    				
    			switch(e.getKeyCode())    		
        			{
        		case KeyEvent.VK_ESCAPE: countPuncta(); break;
        		case KeyEvent.VK_A: selectAll(); break;
        		case KeyEvent.VK_F1: TP.setMouseMode(0); break; //remove dendrite
        		case KeyEvent.VK_F2: TP.setMouseMode(0); break; //remove dendrite
        		case KeyEvent.VK_F3: TP.setMouseMode(10); break; //remove cell
        		case KeyEvent.VK_F4: TP.setMouseMode(0); break; //future
        		case KeyEvent.VK_F5: TP.setMouseMode(9); break; //remove spine 
        		case KeyEvent.VK_F6: TP.setMouseMode(9); break; //remove spine 
        		case KeyEvent.VK_F7: TP.setMouseMode(9); break; //future
        		case KeyEvent.VK_F8: TP.setMouseMode(0); break; // future 
        		case KeyEvent.VK_F9: TP.setMouseMode(0); break; // future
        		case KeyEvent.VK_F10: TP.setMouseMode(0); break; // future
        		case KeyEvent.VK_F11: TP.setMouseMode(0); break; // future
        		case KeyEvent.VK_F12: nextImage(); break; // future
        		default: break;
        			}
    			}
    		}
    	
    	return false;
    }
    
    /**
     * Registers a {@link CountEventListener} that will be notified
     * whenever puncta counts change.
     */
    public void addCountEventListener(CountEventListener l)
    {
    	countEventListenerList.add(l);
    }
    
    /**
     * Fires a count event on every registered
     * {@link CountEventListener}.
     */
    public void notifyCountEventListeners()
    {  int g;
    	for(int k = 0; k < countEventListenerList.size(); k++)
    	{    		
    		((CountEventListener)countEventListenerList.elementAt(k)).countEventFired();
    	}
    }
    
    /**
     * Registers a {@link SpineEventListener} that will be notified
     * whenever spine measurements change.
     */
    public void addSpineEventListener(SpineEventListener l)
    {
    	spineEventListenerList.add(l);
    }
    
    /**
     * Fires a spine update event on every registered
     * {@link SpineEventListener}.
     */
    public void notifySpineEventListeners()
    {  
    	for(int k = 0; k < spineEventListenerList.size(); k++)
    	{
    		((SpineEventListener)spineEventListenerList.elementAt(k)).fireSpineUpdateEvent();
    	}
    }
    
    /**
     * Registers a {@link CellEventListener} that will be notified
     * whenever cell measurements change.
     */
    public void addCellEventListener(CellEventListener l)
    {
    	cellEventListenerList.add(l);
    }
    
    /**
     * Registers a {@link ThresholdEventListener} that will be notified
     * whenever threshold-related measurements change.
     */
    public void addThresholdEventListener(ThresholdEventListener l)
    {
    	thresholdEventListenerList.add(l);
    }
    
    /**
     * Fires a cell update event on every registered
     * {@link CellEventListener}.
     */
    public void notifyCellEventListeners()
    {  
    	for(int k = 0; k < cellEventListenerList.size(); k++)
    	{
    		((CellEventListener)cellEventListenerList.elementAt(k)).fireCellUpdateEvent();
    	}
    }
    
    /**
     * Fires a threshold update event on every registered
     * {@link ThresholdEventListener}. (Method name misspelling is
     * preserved intentionally.)
     */
    public void notifyThrehsoldEventListeners()
    {  
    	for(int k = 0; k < thresholdEventListenerList.size(); k++)
    	{
    		((ThresholdEventListener)thresholdEventListenerList.elementAt(k)).fireTresholdUpdateEvent();
    	}
    }
    
        
    /**
     * Returns the current save-file version number emitted by this
     * build of the application.
     */
    public int getsaveVersion()
    {
    	return currentSaveVersion;
    }
    
    /**
     * Returns the current dendrite drawing width from the image panel.
     */
    public int getDendriteWidth()
    {
    	return TP.dendriteWidth.width;
    }
    
    /**
     * Enables or disables the global key dispatcher. When {@code pause}
     * is false, key events will be ignored until re-enabled.
     */
    public void pauseKeyListener(boolean pause)
    {
    	isListening = pause;
    }
    
    /**
     * Returns the current zoom percentage from the image panel, or
     * 100 if the image panel has not been created yet.
     */
    public int getZoom()
    {
    	if(TP != null)
    		return TP.zoom;
    	return 100;
    }
    
    /**
     * Sets the image panel zoom percentage (if the panel exists) and
     * repaints.
     */
    public void setZoom(int z)
    {
    	if(TP != null)
    		{
    		TP.zoom = z;
    		TP.repaint();
    		}
    }
    
    /**
     * Returns the {@link LogInfo} configuration driving the data
     * printers.
     */
    public LogInfo getLogInfo()
    {
    	return lI;
    }
    
    /**
     * Nulls out references to every heavy sub-component and collection
     * held by this frame. Calls {@link TiffPanel#freeMemory()} first to
     * release large image buffers, then removes this frame as a
     * {@link KeyEventDispatcher} from the global focus manager. Used
     * before reloading a different project to let the garbage
     * collector reclaim memory.
     */
    public void freeMemory()
    {
    	/*
    	 ROP.freeMemory();
    	 IP.freeMemory();
    	 TOP.freeMemory();
    	 TP.freeMemory();
    	 TSP.freeMemory();
    	 parent.freeMemory();
    	 RGT.freeMemory();
    	 fileName = null;
    	 pW = null;
    	 lI = null;
    	 fileBox = null;
    	 String names = null;
    	   
    	    
    	 countEventListenerList = null;
    	 spineEventListenerList = null;
    	 cellEventListenerList = null;
    	    
    	 iP.freeMemory();
    	 sP.freeMemory();
    	 cP.freeMemory();
    	    
    	 dpP.freeMemory();
    	 dsP.freeMemory();
    	    
    	 ipP.freeMemory();
    	 isP.freeMemory();
    	 icP.freeMemory();
    	    
    	 logDirectory.freeMemory();
    	 */   
     	
     ROP = null;
   	 IP = null;
   	 TOP = null;
   	 TP.freeMemory();
   	 TP = null;
   	 TSP = null;
   	 parent = null;
   	 RGT = null;
   	 fileName = null;
   	 pW = null;
   	 lI = null;
   	 fileBox = null;
   	 String names = null;
   	   
   	    
   	 countEventListenerList = null;
   	 spineEventListenerList = null;
   	 cellEventListenerList = null;
   	    
   	 iP = null;
   	 sP = null;
   	 cP = null;
   	    
   	 dpP = null;
   	 dsP = null;
   	    
   	 ipP = null;
   	 isP = null;
   	 icP = null;
   	    
   	 logDirectory = null;
   	KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    	    
    }
    
    /**
     * Sets the scroll position of the middle (dendrite) data panels
     * via the region group tab.
     */
    public void setMiddlePanelScrollBar(int value)
    {
    	RGT.setMiddlePanelScrollBar(value);
    }
    
    /**
     * Sets the scroll position of the bottom (cell/spine) data panels
     * via the region group tab.
     */
    public void setBottomPanelScrollBar(int value)
    {
    	RGT.setBottomPanelScrollBar(value);
    }
    

}
