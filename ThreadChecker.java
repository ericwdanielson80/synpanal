package neuron_analyzer;
import java.io.*;
public class ThreadChecker extends Thread{
Thread[] threadList;
NeuronCommand finishExecute;
	public ThreadChecker(Thread[] ThreadList,NeuronCommand FinishCommand)
	{
		threadList = ThreadList;
		finishExecute = FinishCommand;
	}
	
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
