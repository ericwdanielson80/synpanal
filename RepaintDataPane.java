package neuron_analyzer;

public class RepaintDataPane implements NeuronCommand{
functionListener FL;
	public RepaintDataPane(functionListener fL)
	{
		FL = fL;
	}
	
	public void executeCommand()
	{
		FL.repaintDataPane();
	}
}
