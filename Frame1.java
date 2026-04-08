package neuron_analyzer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.net.*;
import java.lang.*;

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
public class Frame1 extends JFrame implements MouseListener {
	JustOneServer myServer;
	boolean runServer = true;
	JMenuBar menuBar;
	JMenu Files;
	ProfileMenu Profile;	
	JMenu LogInfo;
	JMenuItem Open;
	JMenuItem Close;
	JMenuItem dataDestination;
	functionListener fL;
	double calibration = -1;
	int dendriteWidth = -1;
	int[] red = new int[2];
	int[] green = new int[2];
	int[] blue = new int[2];
	AnimationPanel AP;
	Frame2 analysisPane;
	String openPath;
	String dataPath;
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//InternalMFPanel mfp;
    //BorderLayout borderLayout1 = new BorderLayout();
    
    //JButton jButton1 = new JButton("JButton1");
    
    
    
    File[] fileNames;    
    String logFile;
    String home;
    
    
    int[] colors = {-1,-1,-1};
    LogInfo lI;
    
    
    public Frame1(String path) {   
    	
    	home = path;    
    	dataPath = home;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {  
    	lI = new LogInfo();    	    	
    	this.makeMenuBar();
    	this.setJMenuBar(menuBar);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);       
        
        
    }
    public void loadBlankFrame()
    {
        //ImageIcon image = new ImageIcon(ImagePropertiesPanel.class.getResource("BeginImage.gif"));
        //this.getContentPane().add(new JLabel(image));
    	AP = new AnimationPanel(this);
    	this.getContentPane().add(AP);
   	 	this.setJMenuBar(menuBar);
   	 	this.pack();
   	 	this.validate();
   	 	runServer = false;   
    }
    
    public void setAnalysisFrame(File[] filenames,boolean mode)
    {
     if(AP != null)
     {
    	 AP.stopAnimating();
    	 AP.freeMemory();
     }
     if(analysisPane != null)
     {
    	 analysisPane.freeMemory();
    	 analysisPane = null;  
    	 
    	 //analysisPane.ReMake(filenames, lI, dataPath);
    	 System.gc();
    	 //return;
     }
     setLocation(0,0);
     Toolkit toolkit =  Toolkit.getDefaultToolkit ();
   	 Dimension dim = toolkit.getScreenSize();
     setSize(dim);
     analysisPane = new Frame2(this,filenames,lI,dataPath,mode);     
   	 analysisPane.setVisible(true);
   	 //setPreferredSize(analysisPane.getPreferredSize());
   	 getContentPane().removeAll();
   	 getContentPane().setLayout(new BorderLayout());
   	 getContentPane().add(analysisPane,BorderLayout.CENTER);
   	 if(calibration != -1)
   		 analysisPane.setCalibration(calibration);
   	if(dendriteWidth != -1)
  		 analysisPane.setDendriteWidth(dendriteWidth);
   	Profile.setFunctionListener(analysisPane);
   	 //this.setJMenuBar(menuBar);   	 
   	 //this.pack();
   	 this.validate();
   	 runServer = false;
    }
    
    
    
    public void makeMenuBar()
    {
    	menuBar = new JMenuBar();
    	Files = new FileMenu(this);
    	
    	LogInfo = new JMenu("LogInfo");
    	LogInfo.add(new ColorMenu(lI));
    	LogInfo.add(new LogFileDataMenu(lI));
    	Profile = new ProfileMenu(this,home);
    	
    	
    	
    	menuBar.add(Files);
    	menuBar.add(LogInfo);
    	menuBar.add(Profile);
    	
    }
    
    

    public static void main(String[] args ) {    	
    	String ss = null;
    	String out;
    	Frame1 frame1;
		try {
			out = URLDecoder.decode(Frame1.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
			out = out.split("!")[0];
			if(out.charAt(0) == 'f')
				out = out.substring(5,out.length() - 11);
			else
				out = out.substring(0,out.length() - 11);
	    } catch (UnsupportedEncodingException e) {
	        out = "";
	    }
		
    	if(args.length > 0)
    	{
    		ss = args[0];
    	}
    	//ss = "filename";
    	
    	JustOneServer sds = new JustOneServer(ss);
  	    java.net.URL imgURL = Frame1.class.getResource("Frame1.class");
  	    String s = imgURL.getPath();
  	    s = s.substring(0,s.length() - 13);
  	    	
  	    
  	    	
  	    Thread server = new Thread(sds);
  	    if(sds.isChecking)
  	    	{
  	    	server.start();
  	    	System.out.print("Setting up server"); 
  	    	while(sds.isChecking)
  	  	    {
  	  	    	System.out.print(".");
  	  	    }
  	    	}
  	   
  	    
  	  
  	   if(!sds.isClient)
  	   {
  	   	
  	    	frame1 = new Frame1(out);
  	    	Toolkit toolkit =  Toolkit.getDefaultToolkit ();
  	    	Dimension dim = toolkit.getScreenSize();  	    	
  	    	frame1.setLocation(dim.width / 2 - 250, dim.height / 2 - 250);
  	    	frame1.myServer = sds;
  	    	frame1.setTitle("SynPAnal");
  	    	System.out.print("opening generic frame waiting for new files");
  	    	frame1.loadBlankFrame();
  	    	frame1.pack();        
  	  	    frame1.setVisible(true);
  	  	    
  	            
  	    
        
        if(sds.isServer)
        {
        	long time = System.currentTimeMillis();
            while(System.currentTimeMillis() - time < 1500) {
            	System.out.print(".");
      	      try { /*System.out.print(".");*/ Thread.sleep(5 * 60); }
      	      catch(Exception e) { e.printStackTrace(); }
      	      }
            System.out.println("");
            
        		System.out.println("loading files");
        		frame1.setLocation(0,0);
        		frame1.setAnalysisFrame(sds.getFiles(), false);
        		frame1.pack();        
  	  	    	frame1.setVisible(true);
        }
                
        try{
        	if(sds.serverSocket != null)
        		{
        		sds.serverSocket.close();
        		System.out.println("closing socket");
        		}
        }
        catch(IOException ioe)
        {
        	
        }
  	    
  	   }
        /*int[] a = new int[] {0,0,3,3};ja
    	int[] b = new int[] {0,4,3,0};
    	lineTools lT = new lineTools();
    	int[] xy = lT.findIntersect(a,b);
    	*/
        
       	 //will convert 4 integers (0-255) into a single int
       	 /*
       	  * Bit shift 24: unclaimed
       	  * Bit shift 16: unclaimed
       	  * Bit shift  8: start threshold
       	  * Bit shift  0: threshold
       	  */
        
        /*int one = 257;
        int two = 257;
        
        
        
       	 int t = 0;
       	 t = one + (two << 16);    	 
       	 
       	 
        
        
       	 int[] out = new int[4];
       	 out[0] = 0x0000FFFF << 0 & t;
       	 out[1] = (0xFFFF0000 & t) >> 16;
       	 
       	 
       	
        */
       
        
        
        //long = 64bits
      /* int j = 1023; //int == 32 bit 1024 needs 10bits F =4 bits
       int k = 1023;//x&y = 10bits each, 8 bits for threshold value 28 bits needed
       //0x3FF= 10 bits, 0x7FF = 11 bits x&y can be 11 bits,
       //int l = 0x3FF;
       k = ((k<<10)&(0x3FF<<10))+(j&0x3FF);
       //j = j + (k&0x3FF);
       j = k&0x3FF;
       k = k >>10;
       */



        /*int[] k = {(int)-0.8,0,(int)0.8,1,(int)1.5};
       */
       
    }

    
    public void mousePressed(MouseEvent e)
    {
    	
    }
    
    public void mouseReleased(MouseEvent e)
    {
    	
    }
    
    public boolean openFiles(){
    
    JFileChooser chooser = new JFileChooser(openPath);
    chooser.setMultiSelectionEnabled(true);
    int returnVal = chooser.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
    
        fileNames = chooser.getSelectedFiles(); 
        openPath = fileNames[0].getPath();
        return true;
    }
    return false;
    }
    
    public boolean setDataFileDirectory()
    {
    	JFileChooser chooser = new JFileChooser(openPath);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        
            dataPath = chooser.getSelectedFile().getPath();   
            if(analysisPane != null)
            {
            	analysisPane.setLog(dataPath);
            }
            return true;
        }
        return false;
    }
    
    public void mouseClicked(MouseEvent e)
    {	
    	if(e.getSource() == Open)
    	{
    		if(openFiles())
    			setAnalysisFrame(fileNames,true);
    		
    	}
    	else
    		if(e.getSource() == Close)
    		{
    			this.dispose();    			
    		}
    }
    
    public void mouseEntered(MouseEvent e)
    {
    	
    }
    
    public void mouseExited(MouseEvent e)
    {
    	
    }
    
    public void makeLogInfo()
    {
    	lI = new LogInfo();
    }
    
}



