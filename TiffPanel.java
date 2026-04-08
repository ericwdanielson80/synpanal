package neuron_analyzer;

import java.awt.*;
import java.io.*;


import javax.swing.*;
//import com.sun.imageio.plugins.*;

import java.awt.Color;
import java.awt.Frame;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;









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
public class TiffPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener{
    BorderLayout borderLayout1 = new BorderLayout();
    JViewport myView;
    
    WritableRaster backupRaster;
    
    int[] backupInt;    
    int[] bufferInt;
    int redLu=256;
    int greenLu=256;
    int blueLu=256;
   
    ThresholdContainer redTC;
    ThresholdContainer blueTC;
    ThresholdContainer greenTC;
    
    IgnoreCriteria[] ignoreCriteria;
    

    int a,r,g,b,tempInt,rshift,gshift,bshift,bitOp,thresholdInt,tshift;

    BufferedImage tiff; //for the actual image
    BufferedImage overlayImage; //for drawing dendrites
    //BufferedImage offScreenTiff; 
    
    BufferedImage ThresholdR; //for red threshold color
    BufferedImage ThresholdB; //for blue threshold color
    BufferedImage ThresholdG; //for green threshold color
    
    dendriteWidth dendriteWidth = new dendriteWidth(20);
    
    Dendrite newDendrite;
    CellBody newCell;
    
    
    int[][] newLines;
    Color[] lineColors;
    int newSpineWidth = 6;
    

    functionListener fL;
    PunctaCounter punctaCounter;
    neuronToolKit ntk = new neuronToolKit();


    float[] f = {10.0f};
    BasicStroke stroke = new BasicStroke(0.5f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);//,10.0f, f, 0.0f);


    //ThresholdInfo threshInfo;
        
    int zoom = 100;
    int width;
    int height;
    boolean red = true;
    int redth = 256;
    int stredth = 256;
    boolean blue = true;
    int blueth = 256;
    int stblueth = 256;
    boolean green = true;
    int greenth = 256;
    int stgreenth = 256;
    int[] colors = new int[3];
    boolean[] dendriteWatch = {true,true,true};
    boolean speed = false;
    boolean blind = false;
    
    boolean drawArea = false;
    boolean spineRadiusIgnore = false;
    int spineAreaRadius = 4;

    JScrollPane scrollPane;


    //Dendrite[] myDendrites;
    DendriteContainer[] myDendritesGroups;
    
    CellBody[] myCells;
    PolyLine[] myShollData;

    int pX,pY;
    File[] fileName;
    String currentFile;
    File saveDir;
    File saveFile;
    int imgCount;
    
    TiffPanelMouseFunctions tpmf;

    boolean finishCalc = false;
    
    int imageWidth;
    int imageHeight;
    
    Thread[] dendriteThread;
    Thread[] cellThread;
    
    boolean countPuncta;
    boolean calcCellIntensity;
    boolean calcDendriteIntensity;
    boolean calcSpineShaft;
    int dendriteViewMode = 0;
    int dendriteModeMax = 1;
    int dataMode = 0;
    
    ColocalizationInfo[] colocalizationInfo;

    public TiffPanel(functionListener fl, File[] filename) {
        fL = fl;
        fileName = filename;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        tpmf = new TiffPanelMouseFunctions(this);
       
        
    }
    
    public void setMouseMode(int t)
    {
    	tpmf.setMode(t);
    }

    public void setScrollPane(JScrollPane sP)
    {
        scrollPane = sP;
    }
    
    public void removeCells(int group)
    {
    	
    	int counter = 0;
    	for(int k = 0; k < myCells.length; k++)
    	{
    		if(myCells[k] != null && myCells[k].groupMember.getValue() != group)
    			counter++;
    	}
    	if(counter == 0)
    	{
    		myCells = new CellBody[10];
    		return;
    	}
    	CellBody[] temp = new CellBody[counter];
    	counter = 0;
    	for(int k = 0; k < myCells.length; k++)
    	{
    		if(myCells[k] != null && myCells[k].groupMember.getValue() != group)
    			{
    			temp[counter] = myCells[k];
    			counter++;
    			}
    	}
    	myCells = temp;
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        this.setDoubleBuffered(true);      
        //System.out.println(ImageIO.read(fileName[0]).getType());
        //System.out.println(BufferedImage.TYPE_CUSTOM);
        createInitialImage(ImageIO.read(fileName[0]));       
        resetDendrites();
        myCells = new CellBody[10];
        punctaCounter = new PunctaCounter(tiff.getWidth(),tiff.getHeight());
        currentFile = fileName[0].getName();
        imgCount = 1;
               
    }


    public void addMouse() {
        this.addMouseListener(this);
    }
    
    public void resetDendrites()
    {
    	myDendritesGroups = new DendriteContainer[fL.getTabCount()];    	
    	for(int k = 0; k < myDendritesGroups.length; k++)
    	{
    		myDendritesGroups[k] = new DendriteContainer(new Dendrite[10]);
    	}
    }
    
    public void resetDendrites(int newSize)
    {
    	/*DendriteContainer[] temp = new DendriteContainer[newSize]; 
    	for(int k = 0; k < temp.length; k++)
    	{
    		if(k < myDendritesGroups.length)
    			temp[k] = myDendritesGroups[k];
    		else
    			temp[k] = new DendriteContainer(new Dendrite[10]);
    	}
    	myDendritesGroups = temp;
    	*/
    	
    	myDendritesGroups = new DendriteContainer[newSize]; 
    	for(int k = 0; k < myDendritesGroups.length; k++)
    	{
    		myDendritesGroups[k] = new DendriteContainer(new Dendrite[10]);
    	}
    	
    }
    
    public void resetCells()
    {
    	myCells = new CellBody[10];
    }

    public void removeMouse() {
        this.removeMouseListener(this);
    }

    public void addMouseWheel() {
        this.addMouseWheelListener(this);
    }

    public void removeMouseWheel() {
        this.removeMouseWheelListener(this);
    }

    public void addMouseMotion()
    {
        this.addMouseMotionListener(this);
    }

    public void removeMouseMotion()
    {
        this.removeMouseMotionListener(this);
    }




    public void paint(Graphics g)
    {
    	Graphics2D g2 = (Graphics2D)g;    	
        super.paint(g2);
    	g2.clearRect(0, 0, imageWidth, imageHeight);
        //g2.clearRect(myView.getX(),myView.getY(),myView.getWidth(),myView.getHeight());
        g.setFont(new Font("Helvetica",Font.PLAIN,4));
        
        //g2.setClip(myView.getX(),myView.getY(),myView.getWidth(),myView.getHeight());
        width = (tiff.getWidth() * zoom)/100;
        height =(tiff.getHeight() * zoom)/100;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                            RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_SPEED);
        //g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

       /* Rectangle viewRect = myView.getViewRect();
        viewRect.x = (viewRect.x * zoom) / 100;
        viewRect.y = (viewRect.y * zoom) / 100;
        viewRect.width = (viewRect.width * zoom) / 100;
        viewRect.height = (viewRect.height * zoom) / 100;*/
        
        g2.drawImage(tiff,0,0,width,height,this);//draw image
        g2.drawImage(overlayImage,0,0,this);
        g2.drawImage(ThresholdR,0,0,width,height,this);
        g2.drawImage(ThresholdG,0,0,width,height,this);
        g2.drawImage(ThresholdB,0,0,width,height,this);
        
        
        g2.scale(((double) zoom) / 100.0, ((double) zoom) / 100.0); //scales following dendrites & puncta
        
        g2.setStroke(stroke);
        
        if(!speed)
        {
        drawDendrites(g2);
        drawPuncta(g2,fL.getCurrentColorGroup());
        drawCells(g2);
        drawSpines(g2);
        }
        drawTempDendrite(g2);
        drawTempCells(g2);
        drawTempSpines(g2);
        
        g.setFont(new Font("Helvetica",Font.PLAIN,14));
        g.setColor(Color.white);
        if(!blind)
        g.drawString(currentFile, 0, 15);
        g.drawString("Image " + imgCount + " of " + fileName.length, 0, 30);
        
        
    }
    
    public void drawTempDendrite(Graphics2D g2)
    {
    	if(newDendrite != null)
        {//draw dendrite

            g2.setColor(new Color(255, 255, 255, 128));
            //g2.drawPolyline(newDendrite.xList,newDendrite.yList,newDendrite.xList.length);
            newDendrite.paintDendriteShaft(g2);
            g2.setColor(Color.black);

        }
    	
    }

    public void drawDendrites(Graphics2D g2)
    {
    	if(myDendritesGroups[fL.getCurrentDendriteGroup().getValue()] == null)
    		return;
    	
    	Dendrite[] mD = myDendritesGroups[fL.getCurrentDendriteGroup().getValue()].myDendrites;
        for(int k = 0; k < mD.length; k++)
        {
            if(mD[k] != null)
            {//draw dendrites

                g2.setColor(new Color(255, 255, 255, 128));
                //g2.fill(myDendrites[k].dendriteArea);
                mD[k].paintDendriteArea(g2,dendriteViewMode);
                g2.setColor(Color.black);

            }
        }
    }

    public void drawPuncta(Graphics2D g,int color)
    { 
    	Dendrite[] mD = myDendritesGroups[fL.getCurrentDendriteGroup().getValue()].myDendrites;
    	g.setColor(Color.yellow);
    	
        for(int k = 0; k < mD.length; k++) 
        {
        	if(mD[k] != null)
        		{        		
        		mD[k].drawPuncta(g,color,k);
        		}
        }
            
    }
    
    public void drawTempCells(Graphics2D g)
    {
    	g.setColor(new Color(255, 255, 255, 128));
    	if(newCell != null)
    		newCell.paintOutline(g);
    	g.setColor(Color.black);
    }
    
    public void drawCells(Graphics2D g)
    {	
    	g.setColor(new Color(255, 255, 255, 128));
    	for(int k = 0; k < myCells.length; k++)
    	{
    		if(myCells[k] != null && myCells[k].groupMember.getValue() == fL.getCurrentDendriteGroup().getValue())
    			myCells[k].paintCell(g);
    	}
    	g.setColor(Color.black);
    }
    
    public void drawTempSpines(Graphics2D g)
    {
    	
    	/*g.setColor(Color.BLUE);
    	if(newSpineLength != null)
    		g.drawLine(newSpineLength[0],newSpineLength[1], newSpineLength[2], newSpineLength[3]);
    	g.setColor(Color.CYAN);
    	if(newSpineWidth != null)
    		g.drawLine(newSpineWidth[0],newSpineWidth[1], newSpineWidth[2], newSpineWidth[3]);
    	g.setColor(Color.MAGENTA);
    	if(newSpineNeck != null)
    		g.drawLine(newSpineNeck[0],newSpineNeck[1], newSpineNeck[2], newSpineNeck[3]);*/
    	if(newLines == null)
    		return;
    	
    	for(int k = 0; k < newLines.length; k++)
    	{
    		if(newLines[k]!= null)
    		{    		
    		g.setColor(lineColors[k]);
    		g.drawLine(newLines[k][0],newLines[k][1],newLines[k][2],newLines[k][3]);
    		}
    	}
    }
    
    public void drawSpines(Graphics2D g)
    {
    	g.setColor(Color.PINK);
    	Dendrite[] mD = myDendritesGroups[fL.getCurrentDendriteGroup().getValue()].myDendrites;
    	for(int k = 0; k < mD.length; k++)
    	{
    		if(mD[k] == null)
    			return;
    		
    		if(!drawArea)
    		{
    			for(int j = 0; j < mD[k].spineNumber; j++)
    		
    				{
    				mD[k].spineData[j].drawSpine(g);    				
    				}
    		}
    		else
    		{
    			for(int j = 0; j < mD[k].spineNumber; j++)
    	    		
				{
				mD[k].spineData[j].drawSpineRadius(g,spineAreaRadius);    				
				}
    		}
    	}
    	g.setColor(Color.black);
    }



    public void setZoom(int z,int x, int y)
    {
        zoom = z;
        if(zoom < 25)
            zoom = 25;
        if(zoom > 800)
            zoom = 800;
        
        createOverlayImage();



        long oldX = (tiff.getWidth() * zoom)/100;
        long oldY = y;

        this.setPreferredSize(new Dimension((tiff.getWidth() * zoom)/100,(tiff.getHeight() * zoom)/100));
        //paintImage();
        this.revalidate();




    }

    public void setDendriteWidth(int v)
    {
        dendriteWidth.setValue(v);
        for(int k = 0;k < myDendritesGroups.length; k++)
        {
        	for(int j = 0; j < myDendritesGroups[k].myDendrites.length; j++)
        	{
        		if(myDendritesGroups[k].myDendrites[j] != null)
        			myDendritesGroups[k].myDendrites[j].makeArea();
        	}
        }
    }

    public int getZoom()
    {
        return zoom;
    }

    public void createInitialImage(BufferedImage inputImage)
    {
    	Raster backup = inputImage.getData();
    	    	
    	imageHeight = backup.getHeight();
        imageWidth = backup.getWidth();
        int imageType = 0;
        /*if(inputImage.getColorModel().getNumComponents() == 3)
        	imageType = BufferedImage.TYPE_INT_BGR;
        else
        	imageType = BufferedImage.TYPE_INT_ARGB;
        tiff = new BufferedImage(imageWidth, imageHeight,imageType);// BufferedImage.TYPE_3BYTE_BGR);
        this was the old way of doing it
    	*/
        tiff = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        tiff.getGraphics().drawImage(inputImage, 0, 0, null);
        backup = tiff.getData();
        //these three lines converth the inputImage into the necessary format.
        
        
        WritableRaster wr = tiff.getRaster();        
                
        
        backupInt = backup.getPixels(0,0,imageWidth,imageHeight,(int[])null);
        
        wr.setPixels(0,0,imageWidth,imageHeight,backupInt);       

        
        backupInt = tiff.getRGB(0, 0, imageWidth, imageHeight, (int[])null, 0, imageWidth);        
        bufferInt = new int[backupInt.length];
        
        System.arraycopy(backupInt,0,bufferInt,0,backupInt.length);
        
        
        tiff = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_ARGB); //switch to RGB
          
        ThresholdR = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_ARGB);  
        ThresholdG = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_ARGB);
        ThresholdB = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_ARGB);
        
        tiff.setRGB(0,0,imageWidth,imageHeight,bufferInt,0,imageWidth);
        backupRaster = tiff.copyData(backupRaster);
        
        createRGB();        
        createMainImage();
        createOverlayImage();
        createThresholdImage();
        
        
    }
    
    public void createRGB()
    {
    	int[] bufferIntR = new int[bufferInt.length];
    	int[] bufferIntB = new int[bufferInt.length];
    	int[] bufferIntG = new int[bufferInt.length]; 
    	
        //now we make the colored
        
            for (int k = 0; k < backupInt.length; k++) {
                
            	tempInt = backupInt[k];
                
                a = 255 << 24; //gives alpha value of 255
                r = 0x00FF0000 <<0 & tempInt;// move FF to alpha part
                g = 0x0000FF00 <<0 & tempInt;
                b = 0x000000FF <<0 & tempInt;

                bufferIntR[k] = a + r;
                bufferIntG[k] = a + g;
                bufferIntB[k] = a + b;
                
            }
            
            redTC = new ThresholdContainer(bufferIntR,0x00FF0000,16,ThresholdR,1,fL.getRedThresholdColor());
            greenTC = new ThresholdContainer(bufferIntG,0x0000FF00,8,ThresholdG,2,fL.getGreenThresholdColor());
            blueTC = new ThresholdContainer(bufferIntB,0x000000FF,0,ThresholdB,3,fL.getBlueThresholdColor());
        
            }

    public ColocalizationInfo measureColocalization(int[] imageArray, int bitOpA, int bitOpB, int bitShiftA, int bitShiftB, int Ath, int Bth)
    {
    	int tA = 0;
    	int tB = 0;
    	int AB = 0;
    	
    	long sA = 0;
    	long sB = 0;
    	long sAB = 0;
    	long sBA = 0;
    	
    	int A = 0;
    	int B = 0;
    	
    	int tempInt;
    	
    	for(int k = 0; k < imageArray.length; k++)
    	{    		
    			tempInt = imageArray[k];
    			A = (bitOpA & tempInt) >> bitShiftA;
    			B = (bitOpB & tempInt) >> bitShiftB;
                
                
                if(A >= Ath)
                {                	
                	tA++;
                	sA += A;
                }
                
                if(B >= Bth)
                {
                	tB++;
                	sB += B;
                }
                
                if(A >= Ath && B >= Bth)
                {
                	AB++;
                	sAB+= A;
                	sBA+= B;
                }
    		
    	}
    	return new ColocalizationInfo(tA,tB,AB,sA,sB,sAB,sBA);    	
    }
    
    public void measureColocalization()
    {
    	ColocalizationInfo[] out = new ColocalizationInfo[3];
    	if(redth < 256 & greenth < 256)
    	{
    		out[0] = RedandGreen();
    	}
    	
    	if(redth < 256 & blueth < 256)
    	{
    		out[1] = RedandBlue();
    	}
    	
    	if(greenth < 256 & blueth < 256)
    	{
    		out[2] = GreenandBlue();
    	}
    	
    	colocalizationInfo = out;
    }
    
    public ColocalizationInfo[] getColocalizationInfo()
    {
    	return colocalizationInfo;
    }
    
    public ColocalizationInfo RedandGreen()
    {
    	return measureColocalization(backupInt,0x00FF0000,0x0000FF00,16,8,redth,greenth);
    }
    
    public ColocalizationInfo RedandBlue()
    {
    	return measureColocalization(backupInt,0x00FF0000,0x000000FF,16,0,redth,blueth);
    }
    
    public ColocalizationInfo GreenandBlue()
    {
    	return measureColocalization(backupInt,0x0000FF00,0x000000FF,8,0,greenth,blueth);
    }
    
    public void restoreImagetoDefault()
    {
        tiff.setRGB(0,0,imageWidth,imageHeight,backupInt,0,imageWidth);
    }
    
    public void paintImage()
    {
    	//draws overlay stuff onto image so it doesn't need 
    	// to be recalculated on the fly
    	// off screen image is 3X Normal Image
    	// This is so lines can be thin or thick depending on the zoom
    	
    	Graphics2D g2 = tiff.createGraphics();
        
        g2.setFont(new Font("Helvetica",Font.PLAIN,4));
        
        width = tiff.getWidth();
        height =tiff.getHeight();
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                            RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_SPEED);
        

        g2.drawImage(tiff,0,0,width,height,this); //puts tiff into offscreen image
        g2.setStroke(stroke);
        //puts overlay on image
        if(myDendritesGroups == null)
        	return;
        
        if(speed)
        {
        drawDendrites(g2); 
        drawPuncta(g2,fL.getCurrentColorGroup());
        drawCells(g2);
        drawSpines(g2);
        }
        
    }
    
    public void createOverlayImage()
    {
    	/*Graphics2D g2 = overlayImage.createGraphics();
    	
        
        g2.setFont(new Font("Helvetica",Font.PLAIN,4));
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                            RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_SPEED);
        g2.setBackground(new Color(0,0,0,0));
        g2.clearRect(0, 0, imageWidth * 8, imageHeight * 8);
        
        

        g2.scale(((double) zoom) / 100.0, ((double) zoom) / 100.0); //scales following dendrites & puncta
        g2.setStroke(stroke);
        //puts overlay on image
        if(myDendritesGroups == null)
        	return;
        
        
        drawDendrites(g2); 
        drawPuncta(g2,fL.getCurrentColorGroup());
        drawCells(g2);
        drawSpines(g2);*/
        
    }
    
    public void createMainImage()
    {
    	WritableRaster r = tiff.getRaster();
    	int[] redInt = backupRaster.getSamples(0, 0,tiff.getWidth(),tiff.getHeight(),0,(int[]) null);
    	int[] greenInt = backupRaster.getSamples(0, 0,tiff.getWidth(),tiff.getHeight(),1,(int[]) null);
    	int[] blueInt = backupRaster.getSamples(0, 0,tiff.getWidth(),tiff.getHeight(),2,(int[]) null);    	
    	int[] blackInt = new int[redInt.length];
    	double rs = 256.0/(double)redLu;
    	double gs= 256.0/(double)greenLu;
    	double bs = 256.0/(double)blueLu;
    	
    	//if(redLu < 256 || greenLu < 256 || blueLu < 256)
    	{
    		for(int k = 0; k < redInt.length; k++)
    		{
    			redInt[k] *= rs;
    			if(redInt[k] > 255)
    				redInt[k] = 255;
    			greenInt[k] *= gs;
    			if(greenInt[k] > 255)
    				greenInt[k] = 255;
    			blueInt[k] *= bs;
    			if(blueInt[k] > 255)
    				blueInt[k] = 255;
    			
    		}
    		
    	}
    	
    	
    	if(red)
    		r.setSamples(0, 0,tiff.getWidth(),tiff.getHeight(),0,redInt);
    	else
    		r.setSamples(0, 0,tiff.getWidth(),tiff.getHeight(),0,blackInt);
    	if(green)
    		r.setSamples(0, 0,tiff.getWidth(),tiff.getHeight(),1,greenInt);
    	else
    		r.setSamples(0, 0,tiff.getWidth(),tiff.getHeight(),1,blackInt);
    	if(blue)
    		r.setSamples(0, 0,tiff.getWidth(),tiff.getHeight(),2,blueInt);
    	else
    		r.setSamples(0, 0,tiff.getWidth(),tiff.getHeight(),2,blackInt);
    	
    }
    
    public void createThresholdImage()
    {
    	redTC.thresholdArray(redth);
    	greenTC.thresholdArray(greenth);
    	blueTC.thresholdArray(blueth);
    }
        
    
    public void batchcreateImage()
    {

        if(tiff == null)
            return;

        createMainImage();
        createOverlayImage();
        createThresholdImage();
    }
    
    

    public void switchRed()
    {
        if(red)
            red = false;
        else
           {
               red = true;
           }
        
        loadColors();
        createThresholdImage();        
        repaint();
    }

    public void switchBlue()
    {
        if(blue)
            blue = false;
        else
            blue = true;

        
        loadColors();
        createThresholdImage();        
        repaint();
    }

    public void switchGreen()
    {
        if(green)
            green = false;
        else
            green = true;
        
        
        loadColors();
        createThresholdImage();        
        repaint();
    }
    
    public void loadColors()
    {    	
    	redTC.oldTh = 256;
    	blueTC.oldTh = 256;
    	greenTC.oldTh = 256;    	
    	createMainImage();        	    	
    }
        
    public void setredThreshold(int t)
    {
    	redTC.oldTh = redth;
    	redth = t;    	
    }

    public void setblueThreshold(int t)
    {   
    	blueTC.oldTh = blueth;
        blueth = t;        
    }

    public void setgreenThreshold(int t)
    {   
    	greenTC.oldTh = greenth;
        greenth = t;        
    }
    
    public void setstredThreshold(int t)
    {
    	stredth = t;    	
    }

    public void setstblueThreshold(int t)
    {    	
        stblueth = t;        
    }

    public void setstgreenThreshold(int t)
    {    	
        stgreenth = t;        
    }

    public void thresholdImage()
    {       	
    	createThresholdImage();   
    	this.invalidate();
        repaint();       
    }
    
    public void editMainImage()
    {       	
    	createMainImage();   
    	this.invalidate();
        repaint();       
    }
    
    public void setLookUp(int r, int g, int b)
    {
    	redLu = r;
    	greenLu = g;
    	blueLu = b;
    }
    
    public void setImage(int imageNum)
    {   
    	try{
    		//tiff = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR); //three color image
    	createInitialImage(ImageIO.read(fileName[imageNum]));
    	}
    	catch(IOException e)
    	{
    		System.out.println("Failed to open next image");
    	}
    	currentFile = fileName[imageNum].getName();
    	imgCount = imageNum + 1;
    	
    }
    
    public int getThreshold(int color)
    {
    	switch (color)
    	{
    	case 0: return redth;
    	case 1: return greenth;
    	case 2: return blueth;
    	default: return 0;
    	}
    }
    
    public String getCurrentFile()
    {
    	return currentFile;
    }
    
    public DendriteGroupData getData(int group,double calibration,int color,boolean[] b)
    {
    	DendriteGroupData dgd = this.getData(group, calibration, color);
    	//dgd.isIgnored = b;
    	return dgd;
    }

    public DendriteGroupData getData(int group,double calibration,int color)
    {
        //generates dataFile for DataPanelContainer

        int dendriteNumber = 0;
       
        Dendrite[] mD = myDendritesGroups[group].myDendrites;
        

        for(int k = 0; k < mD.length; k++)
        {
            if(mD[k] != null)
                dendriteNumber++;                        
        }
        
        if(dendriteNumber == 0)
            return null;
        
        boolean[] isS = new boolean[dendriteNumber];
        boolean[] isI = new boolean[dendriteNumber];
        
        PunctaGroupData oldpgd = null;
        //oldpgd = mD[0].punctaData[color];
        
        int[] dendriteNames = new int[dendriteNumber];
        dendriteNumber = 0;
        for(int k = 0; k < mD.length; k++)
        {//need to fix so is a string...maybe 
        	if(mD[k] == null)
                break;
        	
        	dendriteNames[dendriteNumber] = k + 1;
        	isS[k] = false;
        	isI[k] = false;
        	dendriteNumber++;     
        }


        float[] dendriteLengths = new float[dendriteNumber];
        //figure out dendrite lengths
        
        int punctaNumber = 0;
        for(int k = 0; k < mD.length; k++)
        {
            if(mD[k] == null)
                break;
            if(mD[k].watchColor[color])
            punctaNumber += mD[k].getPunctaNumber(color);
        }

        String[] punctaNames = new String[punctaNumber];
        int[] intensity = new int[punctaNumber];
        double[] area = new double[punctaNumber];      
        BooleanContainer[] bc = new BooleanContainer[punctaNumber];
                
        int counter = 0;
        for(int k = 0; k < mD.length; k++)
        {
        	if(mD[k] != null && mD[k].myPuncta[color] != null)
        	for(int j = 0; j < mD[k].myPuncta[color].getTotalPunctaNumber(); j++)
        	{
        		bc[counter] = mD[k].myPuncta[color].myPuncta[j].bC;
        		counter++;
        	}
        }
        
        PunctaGroupData pgd = new PunctaGroupData(punctaNames,intensity,area,bc,null);
        
        counter = 0;
        int counter2 = 0;
        for(int k = 0; k < mD.length; k++)
        {
            if(mD[k] == null)
                break;
            if(mD[k].watchColor[color]){
            dendriteLengths[counter2] =mD[k].getLength(calibration);
            counter = mD[k].loadData(pgd,counter,dendriteNames[counter2],calibration,color,k);
            counter2++;
        }

        }        
        pgd.loadOldData(oldpgd);
    	DendriteGroupData dgd = new DendriteGroupData(myDendritesGroups[group].myDendrites,null,fL,color,pgd,isS,isI);
        return dgd;
    }
        

    public void countPuncta(int[] r)
    {
    	//r is a int array with all the pixel intensities
    	int threadCounter = 0;
    	for(int k = 0; k < myDendritesGroups.length;k++)
        {  
        for(int j = 0; j < myDendritesGroups[k].myDendrites.length; j++)
        {
        	if(myDendritesGroups[k].myDendrites[j] != null)
        		threadCounter++;
        }
        }
    	dendriteThread = new Thread[threadCounter];
    	boolean tF = false;    
    	threadCounter = 0;
        for(int k = 0; k < myDendritesGroups.length;k++)
        {
        Dendrite[] mD = myDendritesGroups[k].myDendrites;
            
        for(int j = 0; j < mD.length; j++)
        {
        	if(mD[j] != null)
        	{    
            
        //mD[j].countPuncta(new int[]{redth,greenth,blueth},r,tiff.getHeight(),punctaCounter,ntk,tiff.getWidth(),tiff.getHeight());//need to change
        mD[j].countPunctaT(new int[]{redth,greenth,blueth}, new int[]{stredth,stgreenth,stblueth},r,tiff.getHeight(),punctaCounter,ntk,tiff.getWidth(),tiff.getHeight());//need to change
        dendriteThread[threadCounter] = new Thread(mD[j],"dendrites");
        dendriteThread[threadCounter].start();  
        threadCounter++;
        	}
        }
        }
       
        int k = 0;
        while(k < dendriteThread.length)
        {
        	if(!dendriteThread[k].isAlive())
        		k++;
        }
                
        calcCellIntensity(r);
        if(ignoreCriteria != null && (ignoreCriteria[0].AutoIgnorePercentages || ignoreCriteria[0].AutoIgnoreOverlap))
        	autoIgnore();
        if(spineRadiusIgnore)
        	autoIgnoreSpineRadius();
        
        autoIgnorePunctaSize(); //if the limits are below 4 nothing happens
        
        fL.notifyCountEventListeners();
        createOverlayImage();
        fL.repaintDataPane();
    }
    
    public void countPuncta(Dendrite d)
    {
    	//r is a int array with all the pixel intensities
    	
    	int[] r = this.backupInt;
        d.countPunctaT(new int[]{redth,greenth,blueth}, new int[]{stredth,stgreenth,stblueth},r,tiff.getHeight(),punctaCounter,ntk,tiff.getWidth(),tiff.getHeight());//need to change
        d.run(); 
        
        if(ignoreCriteria != null && (ignoreCriteria[0].AutoIgnorePercentages || ignoreCriteria[0].AutoIgnoreOverlap))
        	autoIgnore();
        if(spineRadiusIgnore)
        	autoIgnoreSpineRadius();
        
        if(fL.getLogInfo().punctaLimitRed > 4)
        {
        	d.autoIgnorePunctaSize(0,fL.getLogInfo().punctaLimitRed);
        }
        if(fL.getLogInfo().punctaLimitGreen > 4)
        {
        	d.autoIgnorePunctaSize(1,fL.getLogInfo().punctaLimitGreen);
        }
        if(fL.getLogInfo().punctaLimitBlue > 4)
        {
        	d.autoIgnorePunctaSize(2,fL.getLogInfo().punctaLimitBlue);
        }
        
                    
        /*
         *  if it is a sholl dendrite then auto-ignore puncta that are very small and that do not span entire area. 
         */
        
        fL.notifyCountEventListeners();        
        createOverlayImage();
        fL.repaintDataPane();
        
    }
    
    public void doShollAnalysis()
    {
    	System.out.println("doShollAnalysis()");
    	//countPuncta(this.backupInt);
    	Dendrite[] d = this.getCurrentDendriteGroup();
    	BufferedImage i1 = new BufferedImage(imageWidth, imageHeight,BufferedImage.TYPE_INT_RGB);
    	BufferedImage i2 = new BufferedImage(imageWidth, imageHeight,BufferedImage.TYPE_INT_RGB);
    	//int[][] punctaList = new int[d.length][];
    	for(int k = 0; k < d.length; k++)
    	{
    		if(d[k] != null)
    		{
    		d[k].ShollFilter((Graphics2D)i1.getGraphics(),(Graphics2D)i2.getGraphics(),k + 1,1);//for now only green
    		//draws rings and puncta into i1 and i2
    		//punctaList[k] = new int[d[k].myPuncta[1].myPuncta.length * 2];
    		}
    	}
    	
    	/*Raster r1 = i1.getData();
    	Raster r2 = i2.getData();
    	int[] dN;
    	int[] pN;
    	for(int col = 0; col < imageHeight; col++)
    	{
    		for(int row = 0; row < imageWidth; row++)
    		{
    			dN = r1.getPixel(row,col,(int[])null);
    			pN = r2.getPixel(row,col,(int[])null);
    			if(dN[0] > 0)    				
    				if(dN[0] > 0)
    			{
    				if(pN[0] > 0)
    				{
    					punctaList[dN[0] - 1][pN[0] - 1] = 1; 
    				}
    			}
    			else
    				if(dN[1] > 0)
    				{
        				if(pN[0] > 0)
        				{
        					punctaList[dN[0] - 1][(pN[0] * 2) - 1] = 1; 
        				}
        			}
    				
    				
    				
    		}
    	}
    	for(int k = 0; k < d.length; k++)
    	{
    		if(d[k] != null)
    		d[k].ShollIgnore(punctaList[k],1);
    	}*/
    }
    
    public void setPunctaLimit()
    {
    	int k = fL.getCurrentColorGroup();
    	int currentSize = 4;
    	String color;
    	if(k ==0)
    		{
    		color = "red";
    		currentSize = fL.getLogInfo().punctaLimitRed;
    		fL.getLogInfo().punctaLimitRed = new OptionsFrame(fL).newPunctaLimit(fL.getFrame(), color, currentSize);
    		}
    	if(k ==1)
    		{
    		color = "green";
    		currentSize = fL.getLogInfo().punctaLimitGreen;
    		fL.getLogInfo().punctaLimitGreen = new OptionsFrame(fL).newPunctaLimit(fL.getFrame(), color, currentSize);
    		}
    	if(k ==2)
    		{
    		color = "blue";
    		currentSize = fL.getLogInfo().punctaLimitBlue;
    		fL.getLogInfo().punctaLimitBlue = new OptionsFrame(fL).newPunctaLimit(fL.getFrame(), color, currentSize);
    		}
    	
    }
    
        
    public void CalcCellIntesity()
    {
    	calcCellIntensity(backupInt);
    	fL.notifyCellEventListeners();
    }
    
    public void calcCellIntensity(int[] r)
    {
    	Thread[] cellThread = new Thread[myCells.length];   	
    	boolean tF = false;    	
    	int cellCounter = 0;    	
    	for(int k = 0; k < myCells.length; k++)
    	{
    		if(myCells[k] != null)
    		{
    		myCells[k].setupThread(new int[]{redth,greenth,blueth}, r, tiff.getHeight(), punctaCounter);
    		cellThread[k] = new Thread(myCells[k],"batch Process");
    		cellThread[k].start();
    		cellCounter++;
    		}
        	//myCells[k].generateCellIntensity(new int[]{redth,greenth,blueth}, r, tiff.getHeight(), punctaCounter);
    		
    	}    	
    	ThreadChecker threadCheck = new ThreadChecker(cellThread,new RepaintDataPane(fL));
    	if(cellCounter > 0)
    		threadCheck.start();
    	 
    	
    }
    
    public double getAverageCellIntensity(int color, int group)
    {
    	double totalIntensity = 0;
    	int counter = 0;
    	for(int k = 0;k < myCells.length; k++)
    	{
    		
    		if(myCells[k] != null && myCells[k].groupMember.getValue() == group)
    			{
    			totalIntensity += myCells[k].getIntegratedCellIntensity(color);
    			counter++;
    			}
    	}
    	if(counter == 0)
    		return -1;
    	return totalIntensity / (double)counter;
    }
    
    public double getAverageCellAveIntensity(int color, int group)
    {
    	double totalIntensity = 0;
    	int counter = 0;
    	for(int k = 0;k < myCells.length; k++)
    	{
    		
    		if(myCells[k] != null && myCells[k].groupMember.getValue() == group)
    			{
    			totalIntensity += myCells[k].getAveCellIntensity(color,fL.getCalibration());
    			counter++;
    			}
    	}
    	if(counter == 0)
    		return -1;
    	return totalIntensity / (double)counter;
    }
    
    public int getCellNumber(int group)
    {    	
    	int counter = 0;
    	for(int k = 0;k < myCells.length; k++)
    	{
    		
    		if(myCells[k] != null && myCells[k].groupMember.getValue() == group)
    			{    			
    			counter++;
    			}
    	}
    	
    	return counter;
    }
    
    public void repaintScrollPane()
    {
    	fL.repaintScrollPane();
    }
    

    public void mouseClicked(MouseEvent e) {        
    	//tpmf.mouseClicked(e);
    }
    
    public void mousePressed(MouseEvent e)
    {
    	tpmf.mousePressed(e);
    }
    
    public void mouseReleased(MouseEvent e)
    {
    	tpmf.mouseReleased(e);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        setZoom(getZoom() + ((getZoom() * e.getWheelRotation()*10)/100),e.getX(),e.getY());
        fL.updateZoom();
        repaint();
    }

     public void mouseMoved(MouseEvent e) {
        tpmf.mouseMoved(e);
     }
     
     public void mouseDragged(MouseEvent e){
    	 tpmf.mouseDragged(e);
     }
     
     public void mouseEntered(MouseEvent e){
    	 
     }
     
     public void mouseExited(MouseEvent e){
    	 
     }
     
     public void makeDir()
     {
    	 String saveName = currentFile.substring(0,currentFile.length() - 3) + "nrn";
    	 String dirName =new String("Regions");    	     	 
    	 saveDir = new File(fileName[imgCount - 1].getParentFile(),dirName);
    	 if(!saveDir.exists())    		 
    		 if(saveDir.mkdir())
    			{
    			     			 
    			}
    		else
    			System.out.println("Cannot created Save Directory"); 
     }
          
     
     
     public void loadThresholdInfo(DataInputStream di, IoContainer i,int version)
     {
    	 int[] rgbTHs = i.readIntArray(di,"load thresholds");
    	 int[] redInts = unpackInt(rgbTHs[0]);
    	 int[] greenInts = unpackInt(rgbTHs[1]);
    	 int[] blueInts = unpackInt(rgbTHs[2]);
    	 redth = redInts[0];
    	 stredth = redInts[1];
    	 greenth = greenInts[0];
    	 stgreenth = greenInts[1];
    	 blueth = blueInts[0];
    	 stblueth = blueInts[1];
    	
    	 
    	 
     }
     
     public void loadDendriteWidth(DataInputStream di, IoContainer i,int version)
     {
    	 int dW = i.readInt(di,"read dendrite width");
    	 dendriteWidth.setValue(dW);
     }
     
     public void loadDendriteInfo(DataInputStream di, IoContainer i,int version,Group[] groupList)
     {
    	 int dendriteNum = i.readInt(di,"read dendrite number");
    	 
    	 Dendrite mD;
    	 for(int k = 0; k < dendriteNum; k++)
    	 {    		 
    		 mD = Dendrite.loadDendrite(di, i, dendriteWidth,version,groupList);
    		 mD.makeArea();
    		 addDendrite(mD);
    	 }    
    	 Dendrite[] d = getDendrites();
    	 for(int k = 0; k < d.length; k++)
    	 {
    		 if(d[k] != null)
    		 {
    			 if(d[k].myIndex != -1)
    				 d[k].myParent = d[d[k].myIndex];
    			 d[k].myIndex = k; //this doesn't really matter it will change if dendrites added or del.
    		 }
    	 }
     }
     public void loadCellInfo(DataInputStream di, IoContainer i,int version,Group[] groupList)
     {
    	 int cellNum = i.readInt(di,"read cell number");
    	 myCells = new CellBody[cellNum];
    	 for(int k = 0; k < myCells.length; k++)
    	 {
    		 myCells[k] = new CellBody(di,i,version,groupList);    		 
    	 }
    	 if(myCells.length == 0)
    	 {
    		 myCells = new CellBody[10];
    	 }
     }
     
     public void loadPunctaInfo(DataInputStream di, IoContainer i,int wg,Dendrite[] d,int version,Group[] groupList)
     {
    	 for(int k = 0; k < d.length;k++)
    	 {
    		 if(d[k].groupMember == groupList[wg])
    		 {
    			 d[k].LoadPunctaInfo(di, i,version);   
    			 break;
    		 }
    	 }
     }
     
     
     public int loadTabCount(DataInputStream di, IoContainer i,int version)
     {
    	 return i.readInt(di,"reading tab number");
     }
     
     public Dendrite[] getDendrites()
     {
    	 int counter = 0;
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 for(int j = 0; j < myDendritesGroups[k].myDendrites.length; j++)
    			 if(myDendritesGroups[k].myDendrites[j] != null)
    				 counter++;
    	 }
    	 Dendrite[] myDendrites = new Dendrite[counter];
    	 counter = 0;
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 for(int j = 0; j < myDendritesGroups[k].myDendrites.length; j++)
    			 if(myDendritesGroups[k].myDendrites[j] != null)
    				 {
    				 myDendrites[counter] = myDendritesGroups[k].myDendrites[j];
    				 counter++;
    				 }
    	 }
    	 return myDendrites;
     }
     
     public void saveThresholdIndo(DataOutputStream ds, IoContainer i,Dendrite[] myDendrites)
     {    	     	 
    	 /*if(myDendrites.length == 0)
    		 i.writeIntArray(ds,"TP thresholds", new int[] {256,256,256});
    	 else if(myDendrites[0] == null)
    		 i.writeIntArray(ds,"TP thresholds", new int[] {256,256,256});
    	 else*/
    		 i.writeIntArray(ds,"TP thresholds", new int[] {packInt(redth,stredth,0),packInt(greenth,stgreenth,0),packInt(blueth,stblueth,0)}); 
     }
     
     public void saveTabInfo(DataOutputStream ds, IoContainer i,int tabCount)
     {
    	 i.writeInt(ds,"tab number", tabCount);
     }
     
     public void saveDendriteWidth(DataOutputStream ds, IoContainer i)
     {
    	 i.writeInt(ds,"TP dendrite width", dendriteWidth.width);
     }
     
     public void saveDendriteInfo(DataOutputStream ds, IoContainer i, Dendrite[] myDendrites)
     {
    	 int dendriteCount = 0;
    	 for(int k = 0; k < myDendrites.length; k++)
    	 {
    		 if(myDendrites[k] != null)
    			 {
    			 myDendrites[k].myIndex = dendriteCount;
    			 dendriteCount++;
    			 }
    	 }
    	 i.writeInt(ds,"TP dendrite number", dendriteCount);
    	 //System.out.print("Save Dendrite " + dendriteCount);
    	 for(int k = 0; k < myDendrites.length; k++)
    	 {
    		 if(myDendrites[k] != null)
    			 myDendrites[k].Save(ds, i);
    	 }
    	     	 
    	 
     }
     
     public void saveCellInfo(DataOutputStream ds, IoContainer i)
     {
    	 int cellCount = 0;
    	 for(int k = 0; k < myCells.length; k++)
    	 {
    		 if(myCells[k] != null)
    			 cellCount++;
    	 }
    	 i.writeInt(ds, "TP cell number", cellCount);
    	 for(int k = 0; k < myCells.length; k++)
    	 {
    		 if(myCells[k] != null)
    			 myCells[k].Save(ds,i);
    	 }
    	 
     }
     
     public void savePunctaInfo(DataOutputStream ds, IoContainer i, int wg,Dendrite[] myDendrites)
     {    	 
    	 for(int k = 0; k < myDendrites.length; k++)
    	 {
    		 if(myDendrites[k]!= null && myDendrites[k].groupMember.getValue() == wg)
    			 {
    			 myDendrites[k].savePunctaInfo(ds, i);
    			 //break;
    			 }
    	 }
     }
     
     public void saveDendriteIgnoreInfo(DataOutputStream ds, IoContainer i,int tabCount,RegionGroupTab rT)
     {
    	 for(int k = 0; k < tabCount; k++)
    	 {
    	  i.writeBooleanArray(ds,"dendrite tab ignore", ((ColorTabs)rT.getComponentAt(k)).Red.getDendriteIgnoredList());
    	  i.writeBooleanArray(ds,"dendrite tab ignore", ((ColorTabs)rT.getComponentAt(k)).Green.getDendriteIgnoredList());
    	  i.writeBooleanArray(ds,"dendrite tab ignore", ((ColorTabs)rT.getComponentAt(k)).Blue.getDendriteIgnoredList());
    	 }
     }
          
     
     public String getSaveName()
     {
    	 return currentFile.substring(0,currentFile.length() - 3) + "nro";
     }
     
     public Dendrite[] getCurrentDendriteGroup()
     {
    	 return myDendritesGroups[fL.getCurrentDendriteGroup().getValue()].myDendrites;
     }
     
     public void addDendrite(Dendrite d)
     {       	 
    	 
    	     	 
    	 Dendrite mD[] = myDendritesGroups[d.groupMember.getValue()].myDendrites;
    	 
    	
    	 if(mD[0] == null)
    	 {
    		 mD[0] = d;
    		 return;
    	 }
    	 int k = mD.length;
    	 if(mD[k - 1] != null)
    	 {
    		 Dendrite[] tmp = new Dendrite[(mD.length + 1) *2];
             System.arraycopy(mD,0,tmp,0,mD.length);
             myDendritesGroups[d.groupMember.getValue()].myDendrites = tmp;
             mD = myDendritesGroups[d.groupMember.getValue()].myDendrites;
             k = mD.length;
    	 }
    	 while(mD[k - 1] == null) //finds the empty and puts the dendrite there
    	 {
    		 k--;
    	 }
    	 mD[k] = d;
    	 
     }
     
     public void autoIgnore()
     { 
    	 if(ignoreCriteria == null)
    		 return;
    	 
    	 if(ignoreCriteria[0].activeP){    	 
    	 int[] input = new int[] {redth,greenth,blueth};    	 
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 Dendrite mD[] = myDendritesGroups[k].myDendrites;
    		 for(int j = 0; j < mD.length; j++)
    		 {
    			 if(mD[j] != null)
    				 mD[j].autoIgnore(input, ignoreCriteria, backupRaster);
    		 }
    	 }
    	 }
    	 if(ignoreCriteria[0].activeO || ignoreCriteria[1].activeO || ignoreCriteria[2].activeO)
    	 {
    		 boolean[] rgb = new boolean[3];
    		 boolean[] restoreRed = new boolean[3];
    		 boolean[] restoreGreen = new boolean[3];
    		 boolean[] restoreBlue = new boolean[3];
    		 boolean[] ifRed = new boolean[3];
    		 boolean[] ifGreen = new boolean[3];
    		 boolean[] ifBlue = new boolean[3];
    		 
    		 rgb[0] = ignoreCriteria[0].activeO;
    		 rgb[1] = ignoreCriteria[1].activeO;
    		 rgb[2] = ignoreCriteria[2].activeO;
    		 
    		 restoreRed = new boolean[] {rgb[0],false,false};
    		 restoreGreen = new boolean[] {false,rgb[1],false};
    		 restoreBlue = new boolean[] {false,false,rgb[2]};
    		/* boolean[] rgb = new boolean[] {false,true,false};
    	    	boolean[] restoreRed = new boolean[] {false,false,false};
    	    	boolean[] restoreGreen = new boolean[] {false,true,false};
    	    	boolean[] restoreBlue = new boolean[] {false,false,false};
    	    	boolean[] ifRed = new boolean[] {false,true,false};
    	    	boolean[] ifGreen = new boolean[] {false,true,false};
    	    	boolean[] ifBlue = new boolean[] {false,false,false};*/
    		 
    		 ifRed = ignoreCriteria[0].overlap;
    		 ifGreen = ignoreCriteria[1].overlap;
    		 ifBlue = ignoreCriteria[2].overlap;
    		 
    		 
    		 autoIgnore(rgb, restoreRed, restoreGreen, restoreBlue, ifRed, ifGreen, ifBlue);
    		 
    	 }
     }
     
     public void autoIgnore(boolean rgb[], boolean[] restoreRed, boolean[] restoreGreen, boolean[] restoreBlue, boolean[] ifRed, boolean[] ifGreen, boolean[] ifBlue)
     {    	     	 
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 Dendrite mD[] = myDendritesGroups[k].myDendrites;
    		 for(int j = 0; j < mD.length; j++)
    		 {
    			 if(mD[j] != null)
    				 mD[j].autoIgnore(rgb, restoreRed, restoreGreen, restoreBlue, ifRed, ifGreen, ifBlue);
    		 }
    	 }
     }
     
     public void setIgnoreCriteria(IgnoreCriteria red, IgnoreCriteria green, IgnoreCriteria blue)
     {
    	 ignoreCriteria = new IgnoreCriteria[] {red,green,blue};
     }
     
     public void setIgnoreCriteria(IgnoreCriteria[] r)
     {
    	 //ignoreCriteria = new IgnoreCriteria[] {new IgnoreCriteria(100,20,100),new IgnoreCriteria(100,80,100),new IgnoreCriteria(100,80,100)};
    	 ignoreCriteria = r;
     }
     
     public int packInt(int one, int two, int three)
     {
    	 //will convert 3 integers (0-256) into a single int
    	 /*    	  * 
    	  * Bit shift 18: unclaimed
    	  * Bit shift  9: start threshold
    	  * Bit shift  0: threshold
    	  */
    	 int out = 0;
    	 out = one + (two << 16);
    	 
    	 
    	 return out;
     }
     
     public int[] unpackInt(int t)
     {
    	 int[] out = new int[2];
       	 out[0] = (0x0000FFFF & t);
       	 out[1] = (0xFFFF0000 & t) >> 16;       	 
       	 
    	 return out;
     }
     
     public void invertIgnored()
     {
    	 Dendrite[] mD = this.getCurrentDendriteGroup();
    	 for(int k = 0; k < mD.length; k++)
    	 {
    		 if(mD[k] != null)
    			 mD[k].invertIgnored();
    	 }
    	 
    	 repaint();
    	 fL.repaintDataPane();
     }
     
     public void restoreIgnored()
     {
    	 Dendrite[] mD = this.getCurrentDendriteGroup();
    	 for(int k = 0; k < mD.length; k++)
    	 {
    		 if(mD[k] != null)
    			 mD[k].restoreIgnored();
    	 }
    	 
    	 repaint();
    	 fL.repaintDataPane();
     }
     
     public void autoIgnoreSpineRadius()
     {
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 Dendrite mD[] = myDendritesGroups[k].myDendrites;
    		 for(int j = 0; j < mD.length; j++)
    		 {
    			 if(mD[j] != null)
    				 {
    				 	mD[j].autoIgnoreSpineRadius(0, spineAreaRadius);
    				 	mD[j].autoIgnoreSpineRadius(1, spineAreaRadius);
    				 	mD[j].autoIgnoreSpineRadius(2, spineAreaRadius);
    				 }
    		 }
    	 }
     }
     
     public void autoIgnorePunctaSize()
     {
    	 int redLim = fL.getLogInfo().punctaLimitRed;
    	 int greenLim = fL.getLogInfo().punctaLimitGreen;
    	 int blueLim = fL.getLogInfo().punctaLimitBlue;
    	 
    	 if(redLim <= 4 && greenLim <= 4 && blueLim <= 4)
    		 return;
    	 
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 Dendrite mD[] = myDendritesGroups[k].myDendrites;
    		 for(int j = 0; j < mD.length; j++)
    		 {
    			 if(mD[j] != null)
    				 {
    				 	if(redLim > 4)
    				 	mD[j].autoIgnorePunctaSize(0,redLim);
    				 	if(greenLim > 4)
        				 	mD[j].autoIgnorePunctaSize(1,greenLim);
    				 	if(blueLim > 4)
        				 	mD[j].autoIgnorePunctaSize(2,blueLim);
    				 	
    				 }
    		 }
    	 }
     }
     
     
     public void resetNewLines(int rows, Color[] c)
     {
    	 /*
    	  * for drawing of temparary lines will set int[row][4]
    	  */
    	 newLines = new int[rows][];
    	 lineColors = c;
     }
     
     public void delNewLines()
     {
    	 newLines = null;
     }
     
     public void addTempSpineLength(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,0);
     }
     
     public void addTempSpineNeck(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,2);
     }
     
     public void addTempSpineWidth(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,1);
     }
     
     public void addTempSpineBase(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,3);
     }
     
     private void addNewTempLine(int a, int b, int c, int d, int row)
     {
    	 newLines[row] = new int[] {a,b,c,d};    	 
     }
     
     public void newSpineWidth(int k)
     {
    	 if(tpmf.mode == 13)
    	 {
    		 newSpineWidth +=k;
    		 tpmf.recalcSpineLines();
    		 repaint();
    	 }
    	 
     }
     
     public void calculateSpineShaftRatio(int[] r)
     {
    	 for(int k = 0; k < myDendritesGroups.length; k++)
    	 {
    		 Dendrite[] d = myDendritesGroups[k].myDendrites;
    		 for(int j = 0; j < d.length; j++)
    		 {
    			 if(d[j]==null)
    				 break;
    			 d[j].calculateSpineShaftIntensity(new int[]{redth,greenth,blueth}, r, tiff.getHeight(), punctaCounter);
    		 }
    	 }
     }
     
     public void SwapCellRegions(Group[] groupList)
     {
    	 //for now assumes there are only two tabs
    	 for(int k = 0; k < myCells.length; k++)
    	 {
    		 if(myCells[k] != null)
    		 {
    			 if(myCells[k].groupMember.getValue() == 0)
    				 myCells[k].groupMember = groupList[1];
    			 else
    			 {
    				 if(myCells[k].groupMember.getValue() == 1)
    				 {
    					 myCells[k].groupMember = groupList[0];
    				 }
    			 }
    		 }
    	 }
    	 CalcCellIntesity();
    	 repaint();
     }
     
     public void swicthDendriteViewMode()
     {
    	 dendriteViewMode++;
    	 if(dendriteViewMode > dendriteModeMax)
    	 {
    		 dendriteViewMode = 0;
    	 }
    	 repaint();
     }
    
     public void CompactDendriteGroup(int group)
     {
    	 myDendritesGroups[group].compactDendrites();
     }
     
     public void freeMemory()
     {
    	 borderLayout1 = null;
    	 myView = null;
    	    
    	 backupRaster = null;
    	 backupInt = null;    
    	 bufferInt  = null;
    	   
    	 redTC = null;
    	 blueTC = null;
    	 greenTC = null;
    	    
    	 ignoreCriteria = null;
    	 tiff = null; //for the actual image
    	 overlayImage = null; //for drawing dendrites
    	    //BufferedImage offScreenTiff; 
    	    
    	  ThresholdR = null; //for red threshold color
    	    ThresholdB = null; //for blue threshold color
    	    ThresholdG = null; //for green threshold color
    	    
    	    dendriteWidth = null;
    	    
    	    newDendrite = null;
    	    newCell = null;
    	    
    	    
    	    newLines = null;
    	    lineColors = null;
    	    
    	    

    	    fL = null;
    	    punctaCounter = null;
    	    ntk = null;


    	   f = null;
    	    stroke = null;


    	    //ThresholdInfo threshInfo;
    	        
    	    
    	    colors =  null;
    	    dendriteWatch =  null;
    	    
    	    scrollPane = null;


    	    //Dendrite[] myDendrites;
    	    myDendritesGroups = null;
    	    
    	    myCells = null;

    	    
    	    fileName = null;
    	    currentFile = null;
    	    saveDir = null;
    	    saveFile = null;
    	    
    	    tpmf = null;

    	    
    	    dendriteThread = null;
    	    cellThread = null;
    	    
    	    
     }
     
     public void normalizedSholl(double calibration, double increment, int group)
     {
    	 Dendrite[] d = myDendritesGroups[group].myDendrites;
    	 
    	 int size = 0;
    	 for(int k = 0; k < d.length; k++)
    	 {
    		 if(d[k] != null)
    			 size++;
    	 }
    	 
    	 float[] start = new float[size];
    	 float[] end = new float[size];
    	 float max = 0;
    	 int j = 0;
    	 //System.out.println("normalized sholl");
    	 for(int k = 0; k < d.length; k++)
    	 {
    		 if(d[k] != null)
    		 {
    			 start[j] = d[k].getLengthFromRoot(calibration);
    			 end[j] = start[j] + d[k].getLength(calibration);
    			// System.out.println("start " + start[j]);
    			 //System.out.println("end " + end[j]);
    			 if(end[j] > max)
    				 max = end[j];
    			 j++;
    		 }
    	 }
    	 
    	 //System.out.println("max " + max);
    	 
    	 int l = (int)(max/increment);
    	 int[] sholldata = new int[l];
    	 int[] xList = new int[l];
    	 float pos = 0;
    	 
    	 for(int k = 0; k < sholldata.length; k++)
    	 {
    		 xList[k] = (int)pos;
    		 for(int a = 0; a < start.length; a++)    		 {
    			 
    			 if(pos >= start[a] && pos <= end[a])
    			 {
    				 sholldata[k]++;    				 
    			 }    			     			 
    		 }
    		 pos += increment;
    	 }
    	 if(myShollData == null)
    		 myShollData = new PolyLine[fL.getGroupList().length];
    	 myShollData[group] = new PolyLine(xList,sholldata);
    	 System.out.println("Sholl Data");
    	 myShollData[group].printData();
    	 
     }
     
          
    	 
     
}

 







