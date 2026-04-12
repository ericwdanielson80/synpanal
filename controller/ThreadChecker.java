package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.io.*;
/**
 * A supervisory thread that waits for a batch of worker threads to complete
 * and then fires a follow-up NeuronCommand on the same thread. The analyzer
 * uses this helper to spawn parallel per-channel or per-region analysis jobs
 * and schedule a "finish" callback (for example, to update the UI or write
 * results) that should only run once every worker has finished.
 */
public class ThreadChecker extends Thread{
Thread[] threadList;
NeuronCommand finishExecute;
	/**
	 * Stores the array of worker threads to monitor and the command to run
	 * after all of them have finished. The parameter ThreadList is the set of
	 * Threads whose completion this instance will await, and FinishCommand is
	 * the NeuronCommand to invoke once the wait is over (may be null if no
	 * follow-up action is needed).
	 */
	public ThreadChecker(Thread[] ThreadList,NeuronCommand FinishCommand)
	{
		threadList = ThreadList;
		finishExecute = FinishCommand;
	}

	/**
	 * Walks the thread list in order and advances the index k only once the
	 * current slot is either null or references a thread that is no longer
	 * alive. The busy loop therefore spins on each slot until it settles,
	 * then moves on. Once every element has been cleared this way the loop
	 * exits and, if a finishExecute command was provided, its executeCommand
	 * method is invoked to perform whatever work should follow completion of
	 * the monitored threads.
	 */
	public void run()
	{
		int k = 0;
		while(k < threadList.length)
		{   //cannot exit loop until threads are finished
			if(threadList[k] == null || !threadList[k].isAlive())
			{
				k++;
			}
		}

		if(finishExecute != null)
			finishExecute.executeCommand();
	}

}
