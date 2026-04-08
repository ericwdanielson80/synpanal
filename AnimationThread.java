package neuron_analyzer;
import javax.swing.*;
public class AnimationThread extends Thread {
JFrame myPanel;
boolean run = true;
long time;
	public AnimationThread(JFrame p)
	{
		myPanel = p;
		time = System.currentTimeMillis();
	}
	
	public void run()
	{
		
		while(run && myPanel.isVisible())
		{					    
			    if(System.currentTimeMillis() - time > 16) //repaints at maximum rate of 60Hz
				{			    	
			    	myPanel.repaint();
			    	time = System.currentTimeMillis();			    	
				}
				
		}
		myPanel.repaint(); //one last repaint to make sure everything is finished.
	}
	
	public void terminate()
	{
		run = false;
	}
}
