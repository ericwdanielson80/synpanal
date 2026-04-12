package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
		
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.image.BufferedImage;
	
	
/**
 * Splash-screen style panel that paints an animated reveal of the
 * application's title graphic. The main image and the
 * title-with-credit overlay are drawn into offscreen BufferedImages;
 * the AnimationThread drives repeated paints while a growing
 * rectangular clip shape exposes more of the overlay each frame.
 * When the animation finishes the panel paints a static title frame
 * and can free its image buffers.
 */
public class AnimationPanel extends JPanel {
BufferedImage image;
BufferedImage drawImage;
AnimationThread myThread;
float k = 1;
int j = 1;
Random r;
Random repaintImage;
BufferedImage bImage;
int size = 4;
int x,y,w,h;
boolean first = true;
long time;
Shape circle;
Graphics2D bII;
Graphics2D g22;
Graphics2D bI;
Graphics2D g2;
int width = 512;
int height = 512;
double hs;
double ws;
		
	/**
	 * Builds the panel. Creates the 512x512 RGB buffered images for
	 * the source image and its draw copy, loads the Main.gif splash
	 * into image through its Graphics, adds a placeholder JLabel J,
	 * constructs an AnimationThread bound to the parent JFrame f,
	 * turns on double buffering, and seeds the r and repaintImage
	 * Random generators. The parameter f is the host frame used by
	 * the AnimationThread for timing callbacks.
	 */
	public AnimationPanel(JFrame f)
	{	
		super();
		image = new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB);
		image.getGraphics().drawImage(new ImageIcon(ImagePropertiesPanel.class.getResource("Main.gif")).getImage(),0,0,null);
		drawImage = new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB);
		JLabel J = new JLabel();		
		add(J);
		myThread = new AnimationThread(f);
		this.setDoubleBuffered(true);
		r = new Random();
		repaintImage = new Random();
		bImage = new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)drawImage.getGraphics();
		g.drawImage(image,0,0,null); //puts the main image into the bufferedImage that will be used for drawing		
		
	}
	
	/** Reports a fixed 512x512 preferred size matching the image buffers. */
	public Dimension getPreferredSize()
	{
		return new Dimension(512,512);
	}
	
	/**
	 * Stops the running AnimationThread (if any) and clears the field
	 * so subsequent paints render the static final frame.
	 */
	public void stopAnimating()
	{
		if(myThread != null)
			myThread.terminate();
	
		myThread = null;
	}
	
	/**
	 * Custom paint routine. When the animation thread has ended, the
	 * static image is drawn along with the "SynPAnal" title and the
	 * author credit and the method returns. Otherwise the thread is
	 * started on first paint (recording the start time), and the
	 * growing clip rectangle is computed: size increases with elapsed
	 * milliseconds, producing width/height scale factors ws and hs;
	 * x/y/w/h locate a centred Rectangle.Float stored in the circle
	 * clip. If size exceeds 2000 the overlay is finalised via
	 * newImage and the size counter is reset; otherwise the clip is
	 * installed on bII (the offscreen graphics) and drawImage is
	 * painted through it, and the offscreen buffer bImage is blitted
	 * onto the component graphics g22. Locals bII and g22 are
	 * Graphics2D casts for offscreen and onscreen painting.
	 */
	public void paint(Graphics g)
	{
		if(myThread == null)
		{			
			g.drawImage(image,0,0,null);
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Lucida Sans",Font.BOLD,40));
			g.drawString("SynPAnal",10,40);
			g.setFont(new Font("Lucida Sans",Font.PLAIN,12));
			g.drawString("written by Eric Danielson",50,60);
			return;			
		}
		
		g.setColor(Color.black);		
		bII = (Graphics2D)bImage.getGraphics();
		
		g22 = (Graphics2D)g;
		
		if(myThread != null && !myThread.isAlive())
			{
			myThread.start();
			time = System.currentTimeMillis();
			}
	
		size = (int)(System.currentTimeMillis() - time) + 4;
		
		hs = (double)height * (double)size/2000.00;
		ws = (double)width * (double)size/2000.00;
		
		/*
		x = r.nextInt(1 + (int)hs);
		y = r.nextInt(1 + (int)hs);
		w = r.nextInt(1 + (int)hs);
		h = r.nextInt(1 + (int)hs);
		*/
		
		x = width/2 - (int)ws / 2;
		y = height/2 - (int)hs / 2;
		w = (int)ws;
		h = (int)hs;
		
		circle = new Rectangle.Float(x,y,w,h);
		
				
		if(size > 2000)
		{			
			newImage();
			size = 4;
			time = System.currentTimeMillis();
			g22.drawImage(image,0,0,null); //draw the full image before beginning to draw the writing
			return;			
		}
		bII.clip(circle);
		bII.drawImage(drawImage,0,0,null);
		g22.drawImage(bImage,0,0,null);
		
	}
	
	/**
	 * Advances the animation to its next stage. On the first call it
	 * overlays the "SynPAnal" text and credit on drawImage using the
	 * bI Graphics2D with text antialiasing enabled, then shrinks the
	 * animated region by resetting height and width to 80 and 200 so
	 * the next iteration zooms into just the title area. On the
	 * second call it stops the animation entirely.
	 */
	public void newImage()
	{
		if(first)
		{
			first = false;
			drawImage.getGraphics().drawImage(image,0,0,null); //puts the full image into drawImage
			bI = (Graphics2D)drawImage.getGraphics();
			bI.setColor(Color.white);		
			bI.setRenderingHint(
			        RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			bI.setFont(new Font("Lucida Sans",Font.BOLD,40));
			bI.drawString("SynPAnal",10,40);
			bI.setFont(new Font("Lucida Sans",Font.PLAIN,12));
			bI.drawString("written by Eric Danielson",50,60);
			height = 80;
			width = 200;
		}
		else
		{
			this.stopAnimating();
		}
	}
	
	/**
	 * Releases all references held by the panel (image buffers,
	 * thread, Random instances, Graphics2D helpers) so they can be
	 * garbage collected once the splash animation is no longer
	 * needed.
	 */
	public void freeMemory()
	{
		image = null;
		drawImage = null;
		myThread = null;
		r = null;
		repaintImage = null;
		bImage = null;
		circle = null;
		bI = null;
		bII = null;
		g2 = null;
		g22 = null;
	}
	
}
		