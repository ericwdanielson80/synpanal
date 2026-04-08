package neuron_analyzer;

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
    
       
    public JFrame getFrame()
    {
    	return parent;
    }
     
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
    
    public int getTabCount()
    {
    	return RGT.getTabCount();
    }

    public void setZoomMode()
    {
        TP.addMouseWheel();
    }
    
    public void zoomIn()
    {
    	TP.setZoom(TP.getZoom() + ((TP.getZoom() * 10) / 100),0,0);
    	updateZoom();
        TP.repaint();
    }
    
    public void zoomOut()
    {
    	TP.setZoom(TP.getZoom() - ((TP.getZoom() * 10) / 100),0,0);
    	updateZoom();
        TP.repaint();
    }

    public void removeZoomMode()
    {
        TP.removeMouseWheel();
    }


    public void redColorActive()
    {
       TP.switchRed();
    }
    public void redColorInActive()
    {
        TP.switchRed();
    }
    public void greenColorActive()
    {
        TP.switchGreen();
    }
    public void greenColorInActive()
    {
        TP.switchGreen();
    }
    public void blueColorActive()
    {
        TP.switchBlue();
    }
    public void blueColorInActive()
    {
        TP.switchBlue();
    }
    
    public void loadFilecountPuncta()
    {    	
    	TP.countPuncta(TP.backupInt);
    }
    
    public void countPuncta()
    {  
    	TP.countPuncta(TP.backupInt);   
        TP.repaint();
    }
        
    
    public void recountPuncta()
    {    
    	//to be used for batch stuff
    	
        TP.countPuncta(TP.backupInt); 
        checkThreads();	   
    }
    
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
    
    public void saveRegionInformation()
    {
    	makeSaveFile();
    	saveToFile();    	
    }
    
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
    
    
    public void addDendrite()
    {       
        TP.setMouseMode(1);
    }
    
    public void addComplexDendrite()
    {
    	TP.setMouseMode(2);
    }
    

    public void setThreshold(int r,int g,int b)
    {
    	//changes all the thresholds
    	//does not reset old threshold to 256;
        TP.setredThreshold(r);
        TP.setgreenThreshold(g);
        TP.setblueThreshold(b);
        TP.thresholdImage();        
    }
    
    public void setLookUp(int r, int g, int b)
    {
    	TP.setLookUp(r,g,b);
    	TP.createMainImage();
    	//TP.invalidate();
    	TP.repaint();
    }
    
    public void setstThreshold(int r,int g,int b)
    {
    	//changes all the thresholds
    	//does not reset old threshold to 256;
        TP.setstredThreshold(r);
        TP.setstgreenThreshold(g);
        TP.setstblueThreshold(b);        
        //TP.thresholdImage();

    }
    
    public void resetColors()
    {
    	TP.loadColors();
    }

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

    public int getCurrentColorGroup()
    {
        return RGT.getCurrentColor();
    }

    public Group getCurrentDendriteGroup()
    {
        return RGT.getCurrentDendriteGroup();
    }

    public void repaintPane()
    {
    	if(TP!=null)
        TP.repaint();
    }
    
    public void setCategories(String[] s)
    {
    	lI.setTabs(s);
    	
    }

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
    
    public void toggleSpeed()
    {
    	if(TP.speed)
    	TP.speed = false;
    	else
    	TP.speed = true;
    	//TP.paintImage();
    	TP.repaint();
    }
    
    public int getRedThreshold()
    {
    	return TP.redth;
    }
    
    public int getGreenThreshold()
    {
    	return TP.greenth;
    }
    
    public int getBlueThreshold()
    {
    	return TP.blueth;
    }
    
    public int getstRedThreshold()
    {
    	return TP.stredth;
    }
    
    public int getstGreenThreshold()
    {
    	return TP.stgreenth;
    }
    
    public int getstBlueThreshold()
    {
    	return TP.stblueth;
    }
    
    public Color getRedThresholdColor()
    {
    	if(TP == null || TP.redTC == null)
    		return new Color(255,255,255,255);
    	return TP.redTC.c;
    }
    public Color getGreenThresholdColor()
    {
    	if(TP == null || TP.greenTC == null)
    		return new Color(255,255,255,255);
    	return TP.greenTC.c;
    }
    public Color getBlueThresholdColor()
    {
    	if(TP == null || TP.blueTC == null)
    		return new Color(255,255,255,255);
    	return TP.blueTC.c;
    }

    public void this_mouseWheelMoved(MouseWheelEvent e) {


    }
    
    public void updateZoom()
    {
    	IP.zLabel.updateZoom();
    }

    public void this_mouseClicked(MouseEvent e) {

    }

    public void this_mouseMoved(MouseEvent e) {

    }
    
    public void batchProcess()
    {
    	//Thread runner;
    	//runner = new Thread(this,"batch Process");
    	//runner.start();
    	//run();
    }
    	
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
        
    public void test()
    {
    	
    }
    
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
    
    public boolean isAutoLog()
    {
    	return lI.autolog;
    }
    
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
    
    public void addSpine()
    {
    	TP.setMouseMode(5);
    }
    
    public Group[] getGroupList()
    {
    	return RGT.getGroupList();
    }
    
    public void removeSpine()
    {
    	TP.setMouseMode(9);
    }
    
    public void addSpine2()
    {
    	TP.setMouseMode(8);
    }
    
    public double getCalibration()
    {
    	return this.calibration;
    }
    
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

	public boolean showCell(int k, int color, int group)
    {
    	if(TP.myCells[k].groupMember.getValue() == group && TP.myCells[k].watchColor[color])
    		return true;
    	return false;
    }
    
    public double getCellIntensity(int k, int color)
    {    	
    	return TP.myCells[k].getIntegratedCellIntensity(color);
    }
    
    public double getCellAveIntensity(int k, int color)
    {
    	return TP.myCells[k].getAveCellIntensity(color, getCalibration());
    }
    
    public Dendrite[] getDendrites(int groupMember)
    {    	
    	if(TP == null)
    		return null;    	
    	return TP.myDendritesGroups[groupMember].myDendrites;
    }
    
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
    
    public double getAveCellIntensity(int color, int group)
    {
    	//Average Cell Average Intensity
    	return TP.getAverageCellIntensity(color, group);
    }
    
    public double getAveCellAveIntensity(int color, int group)
    {
    	//Average Cell Average Intensity
    	return TP.getAverageCellAveIntensity(color, group);
    }
    
    public int getCellNumber(int group)
    {
    	return TP.getCellNumber(group);
    }
    
    public CellBody[] getCells()
    {
    	return TP.myCells;
    }
    
    public void addCell()
    {
    	TP.setMouseMode(4);
    }
    public Dendrite getDendrite(int group,int k)
    {
    	return TP.myDendritesGroups[group].myDendrites[k];
    }

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
    
    public void repaintScrollPane()
    {
    	TSP.repaint();
    }
    
    public void repaintDataPane()
    {
    	for(int k = 0; k < RGT.getComponentCount(); k++){
    	((ColorTabs)RGT.getComponentAt(k)).Red.repaintDataPane();    	
    	((ColorTabs)RGT.getComponentAt(k)).Green.repaintDataPane();
    	((ColorTabs)RGT.getComponentAt(k)).Blue.repaintDataPane();
    	}
    }
    
    public void toggleAutoLog()
    {    	
    	lI.toggleAutoLog();    	
    }
    
    public void removeCell()
    {
    	TP.setMouseMode(10);
    }
    
    public void itemStateChanged(ItemEvent e)
    {
    	if(e.getStateChange() == ItemEvent.SELECTED)
    		{
    			this.setNextImage(fileBox.getSelectedIndex());
    		}
    }
    
    public void setRedThresholdColor(Color c)
    {
    	TP.redTC.setColor(c);
    	TP.redTC.oldTh = 256;
    	TP.thresholdImage();
    	TP.repaint();
    }
    public void setBlueThresholdColor(Color c)
    {
    	TP.blueTC.setColor(c);
    	TP.blueTC.oldTh = 256;
    	TP.thresholdImage();
    	TP.repaint();    	
    }
    public void setGreenThresholdColor(Color c)
    {
    	TP.greenTC.setColor(c);
    	TP.greenTC.oldTh = 256;
    	TP.thresholdImage();
    	TP.repaint();
    }
    
    public void setIgnoreCriteria(IgnoreCriteria[] ignoreCriteria)
    {
    	TP.setIgnoreCriteria(ignoreCriteria);
    	TP.autoIgnore();
    	TP.repaint();
    }
    
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
    
    public void increaseSpineRadius()
    {
    	TP.spineAreaRadius += 1;
    	TP.repaint();
    }
    
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
    
    public int getDataMode()
    {
    	return TP.dataMode;
    }
    
    public void setDataMode(int d)
    {
    	TP.dataMode = d;
    }
    
    public void measureColocalization()
    {
    	if(getDataMode() == 3)
    		{
    		TP.measureColocalization();
    		notifyThrehsoldEventListeners();
    		repaintDataPane();
    		}
    }
    
    public ColocalizationInfo[] getColocalizationInfo()
    {
    	return TP.getColocalizationInfo();
    }
    
    public void testFeature()
    {
    	TP.tpmf.setMode(14);
    	
    }
    
    public void activateSpineRadiusMode()
    {
    	toggleRadiusView();
    }
    
    public void selectAll()
    {    	
    	Dendrite selectAll = new Dendrite(new dendriteWidth(1014),TP.dendriteWatch,TP.fL.getCurrentDendriteGroup());
    	selectAll.add(5, 512);
    	selectAll.add(1019, 512);    	
    	TP.addDendrite(selectAll);
    	TP.countPuncta(selectAll);
    	TP.repaint();
    }
    
    
        
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
    
    public void addCountEventListener(CountEventListener l)
    {
    	countEventListenerList.add(l);
    }
    
    public void notifyCountEventListeners()
    {  int g;
    	for(int k = 0; k < countEventListenerList.size(); k++)
    	{    		
    		((CountEventListener)countEventListenerList.elementAt(k)).countEventFired();
    	}
    }
    
    public void addSpineEventListener(SpineEventListener l)
    {
    	spineEventListenerList.add(l);
    }
    
    public void notifySpineEventListeners()
    {  
    	for(int k = 0; k < spineEventListenerList.size(); k++)
    	{
    		((SpineEventListener)spineEventListenerList.elementAt(k)).fireSpineUpdateEvent();
    	}
    }
    
    public void addCellEventListener(CellEventListener l)
    {
    	cellEventListenerList.add(l);
    }
    
    public void addThresholdEventListener(ThresholdEventListener l)
    {
    	thresholdEventListenerList.add(l);
    }
    
    public void notifyCellEventListeners()
    {  
    	for(int k = 0; k < cellEventListenerList.size(); k++)
    	{
    		((CellEventListener)cellEventListenerList.elementAt(k)).fireCellUpdateEvent();
    	}
    }
    
    public void notifyThrehsoldEventListeners()
    {  
    	for(int k = 0; k < thresholdEventListenerList.size(); k++)
    	{
    		((ThresholdEventListener)thresholdEventListenerList.elementAt(k)).fireTresholdUpdateEvent();
    	}
    }
    
        
    public int getsaveVersion()
    {
    	return currentSaveVersion;
    }
    
    public int getDendriteWidth()
    {
    	return TP.dendriteWidth.width;
    }
    
    public void pauseKeyListener(boolean pause)
    {
    	isListening = pause;
    }
    
    public int getZoom()
    {
    	if(TP != null)
    		return TP.zoom;
    	return 100;
    }
    
    public void setZoom(int z)
    {
    	if(TP != null)
    		{
    		TP.zoom = z;
    		TP.repaint();
    		}
    }
    
    public LogInfo getLogInfo()
    {
    	return lI;
    }
    
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
    
    public void setMiddlePanelScrollBar(int value)
    {
    	RGT.setMiddlePanelScrollBar(value);
    }
    
    public void setBottomPanelScrollBar(int value)
    {
    	RGT.setBottomPanelScrollBar(value);
    }
    

}


class Frame2_this_mouseWheelAdapter implements MouseWheelListener {
    private Frame2 adaptee;
    Frame2_this_mouseWheelAdapter(Frame2 adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        adaptee.this_mouseWheelMoved(e);
    }
}


class Frame2_this_mouseAdapter extends MouseAdapter {
    private Frame2 adaptee;
    Frame2_this_mouseAdapter(Frame2 adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}


class Frame2_this_mouseMotionAdapter extends MouseMotionAdapter {
    private Frame2 adaptee;
    Frame2_this_mouseMotionAdapter(Frame2 adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseMoved(MouseEvent e) {
        adaptee.this_mouseMoved(e);
    }
}



