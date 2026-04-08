package neuron_analyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.image.BufferedImage;


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
	
	public Dimension getPreferredSize()
	{
		return new Dimension(512,512);
	}
	
	public void stopAnimating()
	{
		if(myThread != null)
			myThread.terminate();
		
		myThread = null;
	}
	
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
