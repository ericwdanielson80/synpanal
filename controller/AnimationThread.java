package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import javax.swing.*;
/**
 * Background animation driver that repeatedly requests repaints of a Swing
 * JFrame so its contents appear to update smoothly during long-running
 * analyses. The thread paces itself so it will not redraw faster than ~60Hz
 * and terminates once either the frame is hidden or terminate() has been
 * called explicitly.
 */
public class AnimationThread extends Thread {
JFrame myPanel;
boolean run = true;
long time;
	/**
	 * Captures the frame to animate and records the current system time as
	 * the initial reference for the repaint-throttling logic. The parameter
	 * p is stored in myPanel and will be the repaint target in run(); time is
	 * seeded with System.currentTimeMillis() so the first repaint can fire
	 * once at least 16 ms have passed.
	 */
	public AnimationThread(JFrame p)
	{
		myPanel = p;
		time = System.currentTimeMillis();
	}

	/**
	 * Busy-loops until either the run flag is cleared by terminate() or the
	 * target frame becomes invisible. Inside the loop, the method checks
	 * how many milliseconds have elapsed since the previous repaint, stored
	 * in the field time; once that delta exceeds 16 ms (roughly a 60 Hz
	 * cap) it calls myPanel.repaint() and resets time to the current system
	 * time. After the loop exits, a final repaint is issued so any pending
	 * visual updates are flushed to the screen.
	 */
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

	/**
	 * Signals the animation loop to stop at its next iteration by setting
	 * the run flag to false. The thread will then fall out of its while
	 * loop and perform the final catch-up repaint before exiting.
	 */
	public void terminate()
	{
		run = false;
	}
}
