package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

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
 * TiffPanel is the main image viewport of the neuron_analyzer application. It renders
 * the currently loaded 8-bit RGB TIFF image along with all per-color threshold overlays
 * and the annotation layers (dendrite shafts, spines, puncta of each color channel and
 * cell bodies) that are computed by PunctaCounter and the Dendrite / CellBody objects.
 *
 * The panel owns the backing BufferedImages for the TIFF itself, a transparent overlay
 * image used for annotation rendering, and three per-channel threshold images
 * (ThresholdR, ThresholdG, ThresholdB) that color pixels above a user-selected intensity.
 * It keeps the original image bytes in backupInt / backupRaster so that the display image
 * can be rebuilt any time the red, green or blue lookup, threshold, or channel visibility
 * changes. DendriteContainer[] myDendritesGroups holds the tab-grouped dendrites, and
 * CellBody[] myCells holds cell body annotations; drawing switches between them based on
 * the currently selected region group from the functionListener.
 *
 * TiffPanel manages the pixel coordinate system through a zoom percentage (25-800) that is
 * applied by a Graphics2D.scale call in paint, so that overlays drawn in the original image
 * coordinate system are automatically scaled to match the zoomed image. Mouse input (click,
 * drag, move, wheel) is delegated to TiffPanelMouseFunctions, which interprets the active
 * editing mode. The class also hosts file-IO helpers used when a .nro project file is
 * loaded or saved, colocalization measurements between color channels, and automatic
 * ignore logic that filters puncta based on size, overlap and intensity criteria.
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
    
    public dendriteWidth dendriteWidth = new dendriteWidth(20);
    
    public Dendrite newDendrite;
    public CellBody newCell;
    
    
    public int[][] newLines;
    Color[] lineColors;
    public int newSpineWidth = 6;
    

    public functionListener fL;
    PunctaCounter punctaCounter;
    neuronToolKit ntk = new neuronToolKit();


    float[] f = {10.0f};
    BasicStroke stroke = new BasicStroke(0.5f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);//,10.0f, f, 0.0f);


    //ThresholdInfo threshInfo;
        
    public int zoom = 100;
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
    public boolean[] dendriteWatch = {true,true,true};
    boolean speed = false;
    boolean blind = false;
    
    boolean drawArea = false;
    boolean spineRadiusIgnore = false;
    int spineAreaRadius = 4;

    JScrollPane scrollPane;


    //Dendrite[] myDendrites;
    public DendriteContainer[] myDendritesGroups;
    
    public CellBody[] myCells;
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

    /**
     * Constructs a TiffPanel bound to a functionListener that supplies global application
     * state (region groups, threshold colors, tab count) and a list of TIFF files to step
     * through. It stores the listener in fL and the file list in fileName, then delegates
     * image loading and initial state creation to jbInit, catching and printing any
     * Exception. Finally it instantiates the TiffPanelMouseFunctions helper (tpmf) that
     * will receive all mouse events for this panel. The parameter fl is the application's
     * functionListener, and filename is the array of TIFF files the user has opened.
     */
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

    /**
     * Delegates to the TiffPanelMouseFunctions helper to set the current mouse interaction
     * mode (e.g. dendrite drawing, spine editing, cell outlining). The integer t is the
     * mode identifier interpreted by tpmf.
     */
    public void setMouseMode(int t)
    {
    	tpmf.setMode(t);
    }

    /**
     * Stores a reference to the JScrollPane that wraps this panel so it can be queried
     * later for viewport geometry. The parameter sP is the scroll pane created by the
     * parent container.
     */
    public void setScrollPane(JScrollPane sP)
    {
        scrollPane = sP;
    }

    /**
     * Removes every CellBody from myCells whose groupMember value equals the specified
     * group. The method first counts the cells that are kept (not null and not in the
     * target group) into the local counter; if none are kept, myCells is reset to a fresh
     * empty array of length 10. Otherwise a new CellBody[] temp sized to counter is
     * allocated, the surviving cells are copied into temp in order, and myCells is
     * replaced by temp. The parameter group is the group identifier whose cells should
     * be discarded.
     */
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

    /**
     * Performs the one-time Swing and image initialization for the panel. It installs the
     * pre-built BorderLayout, enables double buffering to reduce flicker, loads the first
     * TIFF file through ImageIO.read and hands it to createInitialImage to build the
     * backing buffers, resets all dendrite containers to match the current tab count,
     * allocates a default-size CellBody[] of length 10, constructs the PunctaCounter
     * using the TIFF's width and height, records the first file's name in currentFile,
     * and sets imgCount to 1. Any Exception encountered is propagated to the caller.
     */
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


    /**
     * Registers this panel as its own MouseListener so that mousePressed, mouseReleased
     * and related callbacks will be delivered to the overrides below.
     */
    public void addMouse() {
        this.addMouseListener(this);
    }
    
    /**
     * Creates a fresh DendriteContainer for each region-group tab currently reported by
     * the functionListener. The local array myDendritesGroups is allocated with length
     * fL.getTabCount(), and every slot receives a new DendriteContainer wrapping an
     * empty Dendrite[10]. This effectively discards all existing dendrites and matches
     * the number of containers to the UI tab count.
     */
    public void resetDendrites()
    {
    	myDendritesGroups = new DendriteContainer[fL.getTabCount()];    	
    	for(int k = 0; k < myDendritesGroups.length; k++)
    	{
    		myDendritesGroups[k] = new DendriteContainer(new Dendrite[10]);
    	}
    }
    
    /**
     * Resets the dendrite-group storage to a caller-specified size, discarding all
     * prior dendrite data. myDendritesGroups is reallocated to the given newSize and
     * each slot receives a new DendriteContainer holding an empty Dendrite[10]. The
     * parameter newSize is the desired number of region-group containers. A
     * commented-out block above shows an earlier version that preserved existing
     * dendrites up to the common length.
     */
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
    
    /**
     * Discards all existing cell bodies by replacing myCells with a fresh
     * CellBody[10].
     */
    public void resetCells()
    {
    	myCells = new CellBody[10];
    }

    /**
     * Unregisters this panel as a MouseListener so click events are no longer
     * delivered.
     */
    public void removeMouse() {
        this.removeMouseListener(this);
    }

    /**
     * Registers this panel as a MouseWheelListener so wheel rotation triggers the
     * zoom handler below.
     */
    public void addMouseWheel() {
        this.addMouseWheelListener(this);
    }

    /**
     * Unregisters this panel as a MouseWheelListener to stop receiving wheel
     * events.
     */
    public void removeMouseWheel() {
        this.removeMouseWheelListener(this);
    }

    /**
     * Registers this panel as a MouseMotionListener so mouseMoved and mouseDragged
     * are delivered.
     */
    public void addMouseMotion()
    {
        this.addMouseMotionListener(this);
    }

    /**
     * Unregisters this panel as a MouseMotionListener.
     */
    public void removeMouseMotion()
    {
        this.removeMouseMotionListener(this);
    }




    /**
     * Renders the entire viewport: the TIFF image, the overlay layer, the three
     * per-channel threshold overlays, and all annotation elements (dendrites, puncta,
     * cells, spines and any temporary in-progress drawings). The supplied Graphics is
     * cast to Graphics2D (g2) so that it can accept stroke and transform state.
     * super.paint(g2) lets the Swing hierarchy paint first, then the clearRect wipes
     * the area covered by the original image, and a small Helvetica 4pt font is set
     * for any text fragments. The local width and height are recomputed from the TIFF
     * dimensions scaled by the zoom percentage divided by 100, and several rendering
     * hints are configured to prioritize speed (nearest-neighbor interpolation,
     * antialiasing off, color and render set to SPEED). drawImage then blits the
     * tiff, the overlayImage (at 1:1), and each per-color threshold image scaled to
     * the same width and height. g2.scale is applied so the subsequent dendrite,
     * puncta, cell and spine drawings use the original image coordinate system and
     * are automatically scaled to the zoomed viewport. Stroke is set, and when speed
     * mode is off the non-temporary overlays are drawn; the three drawTemp* calls
     * always render any in-progress edits. Finally the font is switched to 14pt
     * Helvetica, drawing color becomes white, and the current file name (unless blind
     * mode is on) and the "Image X of N" counter are stamped at the top. The
     * parameter g is the Swing-provided Graphics context for repainting.
     */
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
    
    /**
     * Draws the dendrite currently being edited (newDendrite) as a semi-transparent
     * white shape, then restores the drawing color to black. If newDendrite is null
     * the method does nothing. g2 is the Graphics2D context that has already been
     * scaled by paint to match the current zoom.
     */
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

    /**
     * Draws every existing Dendrite in the region group currently selected through
     * fL.getCurrentDendriteGroup(). The local Dendrite[] mD is obtained from
     * myDendritesGroups, and if it is null the method returns early. Each non-null
     * dendrite is rendered with paintDendriteArea using the dendriteViewMode flag to
     * choose the drawing style, preceded by a semi-transparent white color and
     * followed by a reset to black. g2 is the zoom-scaled Graphics2D.
     */
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

    /**
     * Draws puncta markers for every dendrite in the currently selected region group,
     * limited to the given color channel. The local Dendrite[] mD is retrieved from
     * myDendritesGroups for the active tab, the drawing color is set to yellow, and
     * each non-null dendrite is asked to drawPuncta for the specified color with its
     * index k used as an identifier label. g is the scaled Graphics2D, and color is
     * the integer channel index (0 red, 1 green, 2 blue).
     */
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
    
    /**
     * Draws the cell body currently being edited (newCell) as a semi-transparent
     * white outline; if newCell is null nothing is drawn. The color is then restored
     * to black. g is the zoom-scaled Graphics2D.
     */
    public void drawTempCells(Graphics2D g)
    {
    	g.setColor(new Color(255, 255, 255, 128));
    	if(newCell != null)
    		newCell.paintOutline(g);
    	g.setColor(Color.black);
    }
    
    /**
     * Draws every existing CellBody belonging to the current region group.
     * Iteration visits each slot of myCells; any non-null cell whose groupMember
     * matches the current dendrite group is rendered via paintCell using a
     * semi-transparent white color. The drawing color is restored to black at the
     * end. g is the zoom-scaled Graphics2D.
     */
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
    
    /**
     * Draws the temporary spine measurement line set stored in newLines. The method
     * returns immediately if newLines is null. Otherwise each non-null row of
     * newLines (a 4-element int array containing x1,y1,x2,y2) is drawn as a line
     * using the corresponding entry of lineColors. The commented-out block at the
     * top shows an older fixed three-line implementation. g is the scaled
     * Graphics2D.
     */
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
    
    /**
     * Draws all spines attached to dendrites in the currently selected region group.
     * The base color is set to pink. The local Dendrite[] mD is the current group's
     * dendrite array; iteration breaks out of the inner loop with a return when a
     * null slot is reached (since spines are stored contiguously). When drawArea is
     * false each spine is rendered via drawSpine, otherwise drawSpineRadius is used
     * with spineAreaRadius to visualize the spine's area disc. g is the
     * zoom-scaled Graphics2D.
     */
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



    /**
     * Applies a new zoom level centered on an approximate screen position and
     * triggers a revalidate so the scroll pane can adjust. The requested zoom is
     * stored in the zoom field and clamped to the range 25-800 percent. The overlay
     * image is rebuilt (createOverlayImage), the local oldX and oldY capture the
     * post-zoom image width and mouse y (unused after assignment) and the preferred
     * size is updated to the scaled image dimensions before revalidate is called.
     * The parameter z is the target zoom percentage and x, y are the pointer
     * coordinates that the caller intended to keep near-stationary.
     */
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

    /**
     * Updates the working dendrite width used when drawing new dendrite shafts and
     * rebuilds the geometric area of every existing dendrite so they render at the
     * new thickness. dendriteWidth.setValue(v) stores the new value, then a nested
     * loop over myDendritesGroups and each container's myDendrites calls makeArea on
     * every non-null dendrite. The parameter v is the new dendrite width in pixels.
     */
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

    /**
     * Returns the current zoom percentage.
     */
    public int getZoom()
    {
        return zoom;
    }

    /**
     * Builds all BufferedImage buffers used by the panel from a freshly loaded
     * TIFF. imageHeight and imageWidth are taken from the input raster, and a new
     * BufferedImage tiff of type TYPE_3BYTE_BGR is created and filled with the input
     * pixels. The raster is then read back into backupInt (the authoritative copy
     * of the original pixels), bufferInt is sized identically and filled by
     * System.arraycopy so it can be used as a scratch buffer, and tiff is then
     * reassigned to a new TYPE_INT_ARGB image of the same dimensions. The three
     * threshold images (ThresholdR/G/B) are allocated the same way, and the
     * original pixels are written into tiff through setRGB. backupRaster is
     * snapshotted via tiff.copyData so later channel toggling can start from the
     * unmodified image. Finally createRGB builds the per-channel integer buffers,
     * createMainImage composes the display image based on the current channel
     * toggles, createOverlayImage prepares the overlay, and createThresholdImage
     * fills the threshold overlays. The parameter inputImage is the newly decoded
     * TIFF BufferedImage. A commented-out block describes the previous logic that
     * chose image type by component count.
     */
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
    
    /**
     * Decomposes the current backupInt pixel array into three per-channel ARGB
     * buffers and wraps each in a ThresholdContainer. bufferIntR, bufferIntG and
     * bufferIntB are allocated the same length as bufferInt. The loop reads each
     * packed pixel tempInt, computes alpha a as 255 shifted to the high byte, then
     * isolates the red, green and blue channels with mask-and-shift constants,
     * storing a|r, a|g and a|b into the corresponding buffer so each channel can be
     * rendered individually with full alpha. After the loop, redTC, greenTC and
     * blueTC are instantiated with each channel's buffer, its bit mask, its bit
     * shift (16/8/0), the matching BufferedImage target, a numeric id (1-3), and
     * the threshold color supplied by the functionListener.
     */
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

    /**
     * Computes colocalization statistics between two channels of imageArray and
     * returns the result as a ColocalizationInfo. tA, tB and AB count how many
     * pixels exceed the channel A threshold, exceed the channel B threshold, or
     * exceed both, while sA, sB, sAB and sBA accumulate the intensity sums for A
     * pixels, B pixels, A within overlap, and B within overlap respectively. Each
     * iteration extracts channel A with bitOpA mask and bitShiftA right shift, and
     * channel B likewise, comparing them against Ath and Bth. imageArray is the
     * packed RGB pixel array (usually backupInt), bitOpA/bitOpB are channel masks,
     * bitShiftA/bitShiftB convert the masked value to 0-255 intensity, and
     * Ath/Bth are the threshold cutoffs for each channel.
     */
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
    
    /**
     * Runs colocalization analysis across every pair of channels whose thresholds
     * are actually active (less than 256) and caches the results in the instance
     * array colocalizationInfo. A local ColocalizationInfo[] out of length 3 is
     * populated: slot 0 for red-green, slot 1 for red-blue, slot 2 for green-blue,
     * each only if both participating thresholds are below 256. When done the
     * finished array is assigned to colocalizationInfo.
     */
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
    
    /**
     * Returns the cached colocalizationInfo array produced by the last
     * measureColocalization call.
     */
    public ColocalizationInfo[] getColocalizationInfo()
    {
    	return colocalizationInfo;
    }
    
    /**
     * Runs colocalization between the red and green channels of backupInt using the
     * current redth and greenth thresholds and returns the resulting
     * ColocalizationInfo. The bit masks (0x00FF0000 and 0x0000FF00) and shifts (16
     * and 8) select the red and green bytes from packed ARGB pixels.
     */
    public ColocalizationInfo RedandGreen()
    {
    	return measureColocalization(backupInt,0x00FF0000,0x0000FF00,16,8,redth,greenth);
    }
    
    /**
     * Runs colocalization between red and blue channels using redth and blueth as
     * cutoffs, bit mask 0x00FF0000 with shift 16 for red and 0x000000FF with shift 0
     * for blue.
     */
    public ColocalizationInfo RedandBlue()
    {
    	return measureColocalization(backupInt,0x00FF0000,0x000000FF,16,0,redth,blueth);
    }
    
    /**
     * Runs colocalization between the green and blue channels using greenth and
     * blueth, with bit mask 0x0000FF00 (shift 8) for green and 0x000000FF (shift 0)
     * for blue.
     */
    public ColocalizationInfo GreenandBlue()
    {
    	return measureColocalization(backupInt,0x0000FF00,0x000000FF,8,0,greenth,blueth);
    }
    
    /**
     * Overwrites the current tiff image with the pristine backupInt pixel data,
     * discarding any runtime modifications to the display buffer.
     */
    public void restoreImagetoDefault()
    {
        tiff.setRGB(0,0,imageWidth,imageHeight,backupInt,0,imageWidth);
    }
    
    /**
     * Pre-renders annotation overlays directly onto the tiff BufferedImage so they
     * are baked in and do not need to be recalculated on the fly at each paint. A
     * Graphics2D g2 is obtained from the tiff, the font is set to a small Helvetica,
     * and the local width and height are taken straight from the TIFF dimensions.
     * Four rendering hints are configured for speed, then the tiff is drawn onto
     * itself (a no-op that positions the stroke state), the stroke is applied, and
     * if myDendritesGroups is non-null and the speed flag is on the dendrites,
     * puncta, cells and spines of the current group are drawn into the image.
     * Comments note that the off-screen image was originally 3x normal size.
     */
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
    
    /**
     * Refreshes the overlay image. The entire body is commented out in the current
     * version, so the method has no runtime effect; the preserved comment block
     * describes an earlier implementation that cleared the overlay, applied zoom
     * scaling and redrew the dendrite, puncta, cell and spine layers onto the
     * transparent overlayImage.
     */
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
    
    /**
     * Rebuilds the visible tiff by reading the three per-channel sample arrays
     * (redInt, greenInt, blueInt) from backupRaster, applying per-channel lookup
     * scaling, and writing the result (or a zero-filled blackInt) back into the
     * tiff's WritableRaster depending on the red/green/blue toggles. rs, gs and bs
     * are 256-divided-by-lookup scale factors used so that a lookup value below 256
     * stretches darker pixels toward the full 0-255 range; each scaled sample is
     * clamped to 255. Channels whose visibility boolean is false receive the
     * zero-filled blackInt instead of their scaled samples.
     */
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
    
    /**
     * Asks each per-channel ThresholdContainer (redTC, greenTC, blueTC) to refresh
     * its threshold overlay image using the current redth, greenth and blueth
     * cutoffs.
     */
    public void createThresholdImage()
    {
    	redTC.thresholdArray(redth);
    	greenTC.thresholdArray(greenth);
    	blueTC.thresholdArray(blueth);
    }
        
    
    /**
     * Runs the full image-rebuild sequence used between batch operations. If tiff
     * is null the method returns; otherwise it rebuilds the main image, the overlay
     * and the threshold overlays in order, without triggering a repaint.
     */
    public void batchcreateImage()
    {

        if(tiff == null)
            return;

        createMainImage();
        createOverlayImage();
        createThresholdImage();
    }
    
    

    /**
     * Toggles whether the red channel is included in the composed image. The red
     * boolean is flipped, then loadColors resets the ThresholdContainer state and
     * rebuilds the main image, createThresholdImage refreshes the red overlay, and
     * the panel is repainted.
     */
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

    /**
     * Toggles the blue channel flag and refreshes the composed image and thresholds
     * in the same way as switchRed.
     */
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

    /**
     * Toggles the green channel flag and refreshes the composed image and
     * thresholds.
     */
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
    
    /**
     * Forces a full recomputation of the main composed image: each of
     * redTC/blueTC/greenTC has its oldTh reset to 256 so the next threshold pass
     * recalculates from scratch, and createMainImage is then called.
     */
    public void loadColors()
    {    	
    	redTC.oldTh = 256;
    	blueTC.oldTh = 256;
    	greenTC.oldTh = 256;    	
    	createMainImage();        	    	
    }
        
    /**
     * Records a new red threshold. Saves the previous redth value into
     * redTC.oldTh so the threshold container can decide what work to redo, then
     * replaces redth with t.
     */
    public void setredThreshold(int t)
    {
    	redTC.oldTh = redth;
    	redth = t;    	
    }

    /**
     * Records a new blue threshold, preserving the previous value in blueTC.oldTh
     * before overwriting blueth with t.
     */
    public void setblueThreshold(int t)
    {   
    	blueTC.oldTh = blueth;
        blueth = t;        
    }

    /**
     * Records a new green threshold, preserving the previous value in
     * greenTC.oldTh before overwriting greenth with t.
     */
    public void setgreenThreshold(int t)
    {   
    	greenTC.oldTh = greenth;
        greenth = t;        
    }
    
    /**
     * Sets the red start threshold (stredth) used by the double-threshold counter.
     */
    public void setstredThreshold(int t)
    {
    	stredth = t;    	
    }

    /**
     * Sets the blue start threshold (stblueth).
     */
    public void setstblueThreshold(int t)
    {    	
        stblueth = t;        
    }

    /**
     * Sets the green start threshold (stgreenth).
     */
    public void setstgreenThreshold(int t)
    {    	
        stgreenth = t;        
    }

    /**
     * Rebuilds the three threshold overlays, invalidates the panel and requests a
     * repaint so the new threshold colors appear.
     */
    public void thresholdImage()
    {       	
    	createThresholdImage();   
    	this.invalidate();
        repaint();       
    }
    
    /**
     * Rebuilds the main composed image, invalidates the panel and requests a
     * repaint. Used after channel lookup adjustments.
     */
    public void editMainImage()
    {       	
    	createMainImage();   
    	this.invalidate();
        repaint();       
    }
    
    /**
     * Sets the per-channel lookup ceiling values used by createMainImage. The
     * parameters r, g and b are the new red, green and blue lookup values (typically
     * in the 1-256 range).
     */
    public void setLookUp(int r, int g, int b)
    {
    	redLu = r;
    	greenLu = g;
    	blueLu = b;
    }
    
    /**
     * Loads a different TIFF from the fileName array and makes it the new active
     * image. A try/catch wraps the ImageIO.read call so an IOException prints a
     * "Failed to open next image" message without propagating. On success
     * createInitialImage rebuilds every backing buffer, currentFile is updated to
     * the new filename, and imgCount is set to imageNum+1 for the status display.
     * The parameter imageNum is the zero-based index into fileName.
     */
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
    
    /**
     * Returns the threshold for a color channel: case 0 returns redth, case 1
     * greenth, case 2 blueth; any other value returns 0. The parameter color is the
     * channel index.
     */
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
    
    /**
     * Returns the file name of the currently displayed TIFF.
     */
    public String getCurrentFile()
    {
    	return currentFile;
    }
    
    /**
     * Convenience overload that delegates to the three-argument getData. The extra
     * boolean[] b parameter is accepted but ignored (the commented-out line shows it
     * was once used to seed the isIgnored array). Returns whatever dgd the core
     * method produces. The parameters group, calibration and color have the same
     * meaning as in the main overload.
     */
    public DendriteGroupData getData(int group,double calibration,int color,boolean[] b)
    {
    	DendriteGroupData dgd = this.getData(group, calibration, color);
    	//dgd.isIgnored = b;
    	return dgd;
    }

    /**
     * Builds the DendriteGroupData structure that DataPanelContainer uses to display
     * and export dendrite and puncta measurements for a particular region group and
     * color channel. It first walks the group's Dendrite[] mD counting dendriteNumber
     * (non-null slots); if there are none it returns null. Boolean arrays isS and
     * isI of that length are allocated for per-dendrite selected/ignored state, an
     * int[] dendriteNames is filled with each dendrite's 1-based index, and the loop
     * is broken at the first null slot since dendrites are contiguous. A second
     * pass counts punctaNumber by summing getPunctaNumber(color) for each dendrite
     * whose watchColor flag is true for this channel, and parallel arrays
     * punctaNames, intensity, area and bc (a BooleanContainer array sharing the
     * per-puncta ignore flags) are sized and filled in the third loop by iterating
     * each dendrite's myPuncta[color].myPuncta to copy the bC references. A
     * PunctaGroupData pgd bundles those arrays. Then a fourth pass walks the
     * dendrites and for each one whose watchColor is on, stores its length in
     * dendriteLengths at counter2, calls loadData on the dendrite to populate the
     * per-puncta fields of pgd, and advances counter. Finally pgd.loadOldData is
     * called with a null oldpgd (the commented-out line once read prior data from
     * the first dendrite) and the DendriteGroupData wrapper is constructed and
     * returned. The parameter group selects which region tab's dendrites to pull,
     * calibration is the pixel-to-micron factor used by getLength and loadData, and
     * color is the channel index for puncta.
     */
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
        

    /**
     * Launches a parallel puncta-counting pass across every dendrite in every
     * region group, using the pixel intensity array r as the working image. The
     * first pair of nested loops counts threadCounter (the number of non-null
     * dendrites) so dendriteThread can be sized. A second pair of loops then, for
     * each non-null dendrite, calls countPunctaT with the current threshold triplet
     * (redth/greenth/blueth) and start threshold triplet (stredth/stgreenth/stblueth)
     * along with r, the image height, the shared punctaCounter, the neuronToolKit
     * ntk, and the tiff width and height. Each dendrite is started in its own Thread
     * (named "dendrites") stored in dendriteThread. The subsequent while loop busy
     * waits by walking k forward only while the current thread has completed,
     * effectively joining all threads sequentially. After all dendrites finish, cell
     * intensities are computed with calcCellIntensity(r), autoIgnore is invoked if
     * ignoreCriteria is set and any of the AutoIgnorePercentages/AutoIgnoreOverlap
     * flags are on, autoIgnoreSpineRadius runs when spineRadiusIgnore is enabled,
     * and autoIgnorePunctaSize applies size filtering (its own guard keeps it inert
     * when all limits are at or below 4). Finally listeners are notified, the
     * overlay is rebuilt, and the data pane is repainted. The parameter r is the
     * raw pixel intensity array used for measurement. The local tF is declared but
     * left unused.
     */
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
    
    /**
     * Runs the puncta counter on a single dendrite rather than the entire group.
     * The panel's own backupInt is used as the image array r. The dendrite's
     * countPunctaT is configured with the current threshold and start-threshold
     * triplets, image dimensions and helpers, and then its run method is called
     * synchronously (no thread). The same autoIgnore and spineRadiusIgnore guards
     * apply as in the bulk version, and autoIgnorePunctaSize is called individually
     * per channel only if the LogInfo's punctaLimitRed/Green/Blue is greater than 4.
     * Finally the listeners are notified, the overlay is rebuilt, and the data pane
     * is repainted. The parameter d is the dendrite whose puncta are being
     * (re)counted. A trailing comment describes a Sholl-related auto-ignore plan.
     */
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
    
    /**
     * Draws dendrites and puncta for the current region group into two off-screen
     * BufferedImages (i1 for dendrites, i2 for puncta) to support Sholl analysis.
     * For every non-null dendrite d[k], ShollFilter is called with the two
     * Graphics2D contexts, the dendrite's 1-based index and color channel 1 (green).
     * A large commented-out block at the bottom shows the previously planned
     * pixel-scan that would populate a per-dendrite puncta presence list and feed it
     * back to d[k].ShollIgnore.
     */
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
    
    /**
     * Opens the OptionsFrame dialog to let the user set the puncta size limit for
     * the currently selected color group. The local k is the active color group;
     * currentSize starts at 4 and is overridden with the channel's existing
     * LogInfo limit (punctaLimitRed/Green/Blue). The color string holds the human
     * label passed to newPunctaLimit, which displays the dialog and returns the new
     * limit that is stored back into the matching punctaLimit field.
     */
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
    
        
    /**
     * Convenience wrapper that recomputes cell intensity on the panel's backupInt
     * and then fires the cell event listeners through the functionListener.
     */
    public void CalcCellIntesity()
    {
    	calcCellIntensity(backupInt);
    	fL.notifyCellEventListeners();
    }
    
    /**
     * Spawns one thread per cell body to compute intensity measurements over the
     * pixel array r, then schedules a ThreadChecker to repaint the data pane when
     * all cell threads complete. cellThread is sized to myCells.length, cellCounter
     * tracks how many threads were actually started, and tF is an unused flag. Each
     * non-null cell has setupThread called with the current threshold triplet, r,
     * the image height and the shared punctaCounter before its Thread (named "batch
     * Process") is started. The ThreadChecker is only started if at least one cell
     * thread launched. The parameter r is the pixel intensity array.
     */
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
    
    /**
     * Returns the average integrated cell intensity across every cell in the given
     * group for the specified color. The local totalIntensity sums
     * getIntegratedCellIntensity values and counter counts participating cells;
     * if counter is 0 the method returns -1, otherwise the arithmetic mean is
     * returned.
     */
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
    
    /**
     * Returns the average of per-cell mean intensities for the given color and
     * group, where each cell's mean is obtained from getAveCellIntensity using the
     * current calibration. Returns -1 if no cells in that group exist.
     */
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
    
    /**
     * Counts how many cells currently belong to the specified group. Walks myCells
     * and increments counter for any non-null cell whose groupMember equals group.
     */
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
    
    /**
     * Asks the functionListener to repaint the surrounding scroll pane.
     */
    public void repaintScrollPane()
    {
    	fL.repaintScrollPane();
    }
    

    /**
     * MouseListener.mouseClicked stub. The commented-out tpmf delegation shows
     * the intended forwarding target, but currently the method is a no-op.
     */
    public void mouseClicked(MouseEvent e) {        
    	//tpmf.mouseClicked(e);
    }
    
    /**
     * Forwards a mouse press event to TiffPanelMouseFunctions so the current mode
     * can respond. The parameter e is the Swing MouseEvent.
     */
    public void mousePressed(MouseEvent e)
    {
    	tpmf.mousePressed(e);
    }
    
    /**
     * Forwards a mouse release event to TiffPanelMouseFunctions.
     */
    public void mouseReleased(MouseEvent e)
    {
    	tpmf.mouseReleased(e);
    }

    /**
     * Zooms in or out in response to a scroll wheel rotation centered on the
     * pointer. The new zoom is the current zoom plus a tenth of the current zoom
     * multiplied by the wheel rotation direction, and is passed to setZoom along
     * with the event's x/y coordinates. fL.updateZoom lets the UI reflect the new
     * level, then a repaint is requested. The parameter e is the MouseWheelEvent.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        setZoom(getZoom() + ((getZoom() * e.getWheelRotation()*10)/100),e.getX(),e.getY());
        fL.updateZoom();
        repaint();
    }

    /**
     * Forwards a mouse-moved event to TiffPanelMouseFunctions.
     */
     public void mouseMoved(MouseEvent e) {
        tpmf.mouseMoved(e);
     }
     
    /**
     * Forwards a mouse-dragged event to TiffPanelMouseFunctions.
     */
     public void mouseDragged(MouseEvent e){
    	 tpmf.mouseDragged(e);
     }
     
    /**
     * Empty MouseListener.mouseEntered handler.
     */
     public void mouseEntered(MouseEvent e){
    	 
     }
     
    /**
     * Empty MouseListener.mouseExited handler.
     */
     public void mouseExited(MouseEvent e){
    	 
     }
     
    /**
     * Creates a "Regions" directory next to the currently displayed image file if
     * it does not already exist. saveName is computed as the current file's base
     * name with its extension replaced by "nrn" (unused in this method but preserved
     * from legacy code), dirName is the literal "Regions", and saveDir is a File
     * built from the parent folder of the current image. If the directory does not
     * yet exist, mkdir is attempted and a failure message is printed to stdout.
     */
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
          
     
     
    /**
     * Loads red, green and blue threshold pairs (main and start thresholds) from
     * the given DataInputStream. The IoContainer i reads an int[] rgbTHs of three
     * packed entries, each decoded by unpackInt into a 2-element array. redInts,
     * greenInts and blueInts supply the main threshold and start threshold which
     * are assigned to redth/stredth, greenth/stgreenth and blueth/stblueth. The
     * parameter version carries the file-format version for compatibility (not
     * branched on here).
     */
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
     
    /**
     * Reads a single dendrite width value from the stream and applies it to the
     * shared dendriteWidth helper. The parameters di, i and version identify the
     * source stream, IoContainer helper and file version.
     */
     public void loadDendriteWidth(DataInputStream di, IoContainer i,int version)
     {
    	 int dW = i.readInt(di,"read dendrite width");
    	 dendriteWidth.setValue(dW);
     }
     
    /**
     * Loads every saved dendrite from the input stream and reconstructs parent
     * pointers between them. First dendriteNum is read, then for k in 0..dendriteNum
     * a Dendrite is reconstructed via Dendrite.loadDendrite, its area polygon is
     * recomputed with makeArea, and it is appended through addDendrite. After
     * loading, the flattened dendrite list d is retrieved with getDendrites and a
     * second loop wires each dendrite's myParent to d[myIndex] when the stored
     * myIndex is not -1, then overwrites myIndex with its current position. A
     * trailing comment notes that the new index is informational because additions
     * or deletions could shift it. The parameter groupList is passed through to
     * loadDendrite so tab membership can be restored.
     */
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
    /**
     * Reads the number of cell bodies from the stream, reallocates myCells, and
     * reconstructs each CellBody via its DataInputStream constructor. If the saved
     * count was 0, myCells is replaced with a default-size CellBody[10].
     */
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
     
    /**
     * Locates the saved dendrite whose groupMember corresponds to the region tab
     * wg and dispatches its LoadPunctaInfo routine to read per-dendrite puncta.
     * Only the first matching dendrite is used; the break exits immediately. The
     * parameter wg is the target tab index and d is the flat dendrite array.
     */
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
     
     
    /**
     * Reads and returns the stored tab count from the stream.
     */
     public int loadTabCount(DataInputStream di, IoContainer i,int version)
     {
    	 return i.readInt(di,"reading tab number");
     }
     
    /**
     * Flattens myDendritesGroups into a single Dendrite[] containing every non-null
     * dendrite across all region groups. A first pair of nested loops counts the
     * non-null slots into counter; the second pair allocates myDendrites at that
     * size and copies the same slots in group-then-slot order.
     */
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
     
    /**
     * Writes the red, green and blue thresholds (main plus start) as three packed
     * ints. packInt stores main in low 16 bits and start in high 16 bits, third arg
     * unused. A commented-out block at the top shows an earlier conditional that
     * wrote 256 sentinels when no dendrites were present.
     */
     public void saveThresholdIndo(DataOutputStream ds, IoContainer i,Dendrite[] myDendrites)
     {    	     	 
    	 /*if(myDendrites.length == 0)
    		 i.writeIntArray(ds,"TP thresholds", new int[] {256,256,256});
    	 else if(myDendrites[0] == null)
    		 i.writeIntArray(ds,"TP thresholds", new int[] {256,256,256});
    	 else*/
    		 i.writeIntArray(ds,"TP thresholds", new int[] {packInt(redth,stredth,0),packInt(greenth,stgreenth,0),packInt(blueth,stblueth,0)}); 
     }
     
    /**
     * Writes the current tab count to the stream so it can be restored on load.
     */
     public void saveTabInfo(DataOutputStream ds, IoContainer i,int tabCount)
     {
    	 i.writeInt(ds,"tab number", tabCount);
     }
     
    /**
     * Writes the current dendrite width value to the stream.
     */
     public void saveDendriteWidth(DataOutputStream ds, IoContainer i)
     {
    	 i.writeInt(ds,"TP dendrite width", dendriteWidth.width);
     }
     
    /**
     * Writes every dendrite in myDendrites to the stream. A first loop re-numbers
     * each non-null dendrite's myIndex to its saved position and increments
     * dendriteCount; the count is then written, and a second loop invokes Save on
     * each non-null dendrite so it serializes itself.
     */
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
     
    /**
     * Writes all non-null cell bodies to the stream. The first loop counts cellCount
     * and it is written out, then the second loop calls Save on every non-null
     * CellBody.
     */
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
     
    /**
     * Writes puncta data for every dendrite that belongs to region group wg by
     * calling savePunctaInfo on each matching dendrite. A commented-out break shows
     * an earlier version that stopped after the first match.
     */
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
     
    /**
     * Writes the dendrite "ignored" flag arrays for every tab, covering each of the
     * red, green and blue ColorTabs. tabCount limits the iteration and rT provides
     * access to each ColorTabs component via getComponentAt so the three
     * getDendriteIgnoredList boolean arrays can be serialized.
     */
     public void saveDendriteIgnoreInfo(DataOutputStream ds, IoContainer i,int tabCount,RegionGroupTab rT)
     {
    	 for(int k = 0; k < tabCount; k++)
    	 {
    	  i.writeBooleanArray(ds,"dendrite tab ignore", ((ColorTabs)rT.getComponentAt(k)).Red.getDendriteIgnoredList());
    	  i.writeBooleanArray(ds,"dendrite tab ignore", ((ColorTabs)rT.getComponentAt(k)).Green.getDendriteIgnoredList());
    	  i.writeBooleanArray(ds,"dendrite tab ignore", ((ColorTabs)rT.getComponentAt(k)).Blue.getDendriteIgnoredList());
    	 }
     }
          
     
    /**
     * Returns the default save file name for this image: the current file's base
     * name with the extension replaced by "nro".
     */
     public String getSaveName()
     {
    	 return currentFile.substring(0,currentFile.length() - 3) + "nro";
     }
     
    /**
     * Returns the Dendrite[] for the region group currently selected in the UI.
     */
     public Dendrite[] getCurrentDendriteGroup()
     {
    	 return myDendritesGroups[fL.getCurrentDendriteGroup().getValue()].myDendrites;
     }
     
    /**
     * Appends a dendrite to its target group's Dendrite[] container, growing the
     * backing array when it is full. The local mD points at the dendrites of
     * d.groupMember. If slot 0 is null the new dendrite is stored there. Otherwise
     * k starts at mD.length and if the last slot is already occupied a new array
     * tmp is allocated at (length+1)*2, the existing contents are copied in, the
     * container is swapped to tmp, and k is updated. A while loop then decrements
     * k while the slot ahead is null to find the first empty position, and the
     * dendrite is placed at mD[k]. The parameter d is the dendrite being added.
     */
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
     
    /**
     * Dispatches automatic-ignore logic based on the current ignoreCriteria. If
     * ignoreCriteria is null the method returns. If the first criterion's activeP
     * flag is set, a threshold triplet (redth/greenth/blueth) is built and every
     * non-null dendrite across all groups has its autoIgnore called with input,
     * ignoreCriteria and backupRaster. If any of the three criteria's activeO flag
     * is set, a set of 3-element boolean arrays (rgb, restoreRed, restoreGreen,
     * restoreBlue, ifRed, ifGreen, ifBlue) is built from the overlap fields to
     * describe which channels participate in overlap-based ignores, and the
     * multi-argument autoIgnore overload is called. Commented-out sample boolean
     * arrays record an earlier hardcoded configuration.
     */
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
     
    /**
     * Per-dendrite overlap-based autoIgnore pass. Walks every non-null dendrite in
     * every region group and forwards the seven boolean[] arrays unchanged to the
     * dendrite's own autoIgnore method. The parameter names mirror the fields used
     * by IgnoreCriteria: rgb flags which channels trigger the pass, restoreRed /
     * restoreGreen / restoreBlue indicate which channels are un-ignored when they
     * overlap in the respective color, and ifRed / ifGreen / ifBlue encode the
     * overlap combinations that activate each color's test.
     */
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
     
    /**
     * Bundles three per-color IgnoreCriteria into the ignoreCriteria instance field
     * as a new length-3 array.
     */
     public void setIgnoreCriteria(IgnoreCriteria red, IgnoreCriteria green, IgnoreCriteria blue)
     {
    	 ignoreCriteria = new IgnoreCriteria[] {red,green,blue};
     }
     
    /**
     * Replaces the ignoreCriteria field with the given array r wholesale. The
     * commented-out line above shows a fixed default used historically.
     */
     public void setIgnoreCriteria(IgnoreCriteria[] r)
     {
    	 //ignoreCriteria = new IgnoreCriteria[] {new IgnoreCriteria(100,20,100),new IgnoreCriteria(100,80,100),new IgnoreCriteria(100,80,100)};
    	 ignoreCriteria = r;
     }
     
    /**
     * Packs up to three integers 0-256 into a single int; one occupies the low
     * 16 bits, two occupies bits 16-31 (shifted left 16), and three is reserved but
     * currently unused. A leading comment block documents the unclaimed
     * bit-shift-18 slot and describes the layout used for the paired main/start
     * threshold values.
     */
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
     
    /**
     * Reverses packInt, returning an int[2] where out[0] holds the low 16 bits and
     * out[1] holds the high 16 bits (shifted back down).
     */
     public int[] unpackInt(int t)
     {
    	 int[] out = new int[2];
       	 out[0] = (0x0000FFFF & t);
       	 out[1] = (0xFFFF0000 & t) >> 16;       	 
       	 
    	 return out;
     }
     
    /**
     * Inverts the ignored flag on every puncta of every dendrite in the current
     * region group by calling invertIgnored on each non-null dendrite, then
     * repaints and asks the data pane to refresh.
     */
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
     
    /**
     * Restores all previously ignored puncta in the current region group to their
     * saved state by calling restoreIgnored on each non-null dendrite, then
     * repaints and updates the data pane.
     */
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
     
    /**
     * Applies the spine-radius ignore filter for all three colors to every non-null
     * dendrite in every region group, using spineAreaRadius as the radius.
     */
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
     
    /**
     * Filters out puncta whose size is below the LogInfo per-color limit when the
     * limit is larger than 4 (the sentinel for "no filtering"). The three
     * limits (redLim, greenLim, blueLim) are fetched from fL.getLogInfo; if all are
     * four or fewer the method returns immediately. Otherwise each non-null
     * dendrite in every group receives up to three autoIgnorePunctaSize calls, one
     * per color that has an active limit.
     */
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
     
     
    /**
     * Prepares the temporary-line drawing buffer used by drawTempSpines. newLines
     * is allocated with rows empty int[] slots (to be filled in by addNewTempLine)
     * and lineColors is set to c so each row can be drawn in its own color. The
     * parameter rows is the number of line slots and c is the parallel array of
     * colors. A leading comment explains the int[row][4] shape.
     */
     public void resetNewLines(int rows, Color[] c)
     {
    	 /*
    	  * for drawing of temparary lines will set int[row][4]
    	  */
    	 newLines = new int[rows][];
    	 lineColors = c;
     }
     
    /**
     * Clears the temporary-line buffer so drawTempSpines renders nothing.
     */
     public void delNewLines()
     {
    	 newLines = null;
     }
     
    /**
     * Stores the spine-length line (row 0) into newLines. a, b are the first
     * endpoint and c, d are the second.
     */
     public void addTempSpineLength(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,0);
     }
     
    /**
     * Stores the spine-neck line (row 2) into newLines.
     */
     public void addTempSpineNeck(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,2);
     }
     
    /**
     * Stores the spine-width line (row 1) into newLines.
     */
     public void addTempSpineWidth(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,1);
     }
     
    /**
     * Stores the spine-base line (row 3) into newLines.
     */
     public void addTempSpineBase(int a, int b, int c, int d)
     {
    	 addNewTempLine(a,b,c,d,3);
     }
     
    /**
     * Internal helper that writes the 4-coordinate endpoint array into newLines at
     * the specified row.
     */
     private void addNewTempLine(int a, int b, int c, int d, int row)
     {
    	 newLines[row] = new int[] {a,b,c,d};    	 
     }
     
    /**
     * Adjusts the current temporary spine width by k (only while the mouse mode is
     * 13 for spine-width editing), recomputes the spine lines through
     * tpmf.recalcSpineLines, and repaints. The parameter k is the signed delta to
     * apply to newSpineWidth.
     */
     public void newSpineWidth(int k)
     {
    	 if(tpmf.mode == 13)
    	 {
    		 newSpineWidth +=k;
    		 tpmf.recalcSpineLines();
    		 repaint();
    	 }
    	 
     }
     
    /**
     * Calculates the spine-to-shaft intensity ratio for every non-null dendrite in
     * every region group by calling calculateSpineShaftIntensity with the current
     * threshold triplet, pixel intensity array r, image height and shared
     * punctaCounter. Breaks out of the inner loop at the first null slot.
     */
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
     
    /**
     * Swaps the group membership of every cell between the two tabs (assumes
     * exactly two tabs). Cells in group 0 are moved into groupList[1] and cells in
     * group 1 are moved into groupList[0]. Cell intensities are then recomputed and
     * the panel is repainted. The parameter groupList supplies the Group references
     * to assign.
     */
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
     
    /**
     * Cycles dendriteViewMode through 0..dendriteModeMax and triggers a repaint so
     * the new dendrite display style takes effect.
     */
     public void swicthDendriteViewMode()
     {
    	 dendriteViewMode++;
    	 if(dendriteViewMode > dendriteModeMax)
    	 {
    		 dendriteViewMode = 0;
    	 }
    	 repaint();
     }
    
    /**
     * Compacts the dendrite array in the specified region group by delegating to
     * DendriteContainer.compactDendrites.
     */
     public void CompactDendriteGroup(int group)
     {
    	 myDendritesGroups[group].compactDendrites();
     }
     
    /**
     * Nulls out every reference held by the panel to let the garbage collector
     * reclaim potentially large buffers (images, rasters, threshold containers,
     * dendrite and cell arrays, file handles, Swing helpers and thread arrays).
     * Intended to be called when the panel is being disposed so memory usage drops
     * promptly.
     */
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
     
    /**
     * Builds a normalized Sholl profile for the dendrites of the specified region
     * group using the given per-pixel calibration and the sampling increment. For
     * every non-null dendrite d[k] the method records the starting distance from
     * the root (start[j]) and the ending distance (end[j] = start + length), tracks
     * the maximum distance in max, and counts size non-null dendrites. It then
     * samples at positions 0, increment, 2*increment, ... up to max, storing the
     * current sample position in xList and the count of dendrites whose
     * [start,end] interval contains pos in sholldata. If myShollData is null it is
     * allocated to match the current group list, then slot [group] receives a new
     * PolyLine combining xList and sholldata. The Sholl data is printed to stdout
     * for diagnostics. The parameters calibration, increment and group describe
     * the distance scale, sampling step and which tab's dendrites are analyzed.
     */
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

 







