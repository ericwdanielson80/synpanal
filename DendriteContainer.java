package neuron_analyzer;

public class DendriteContainer {
Dendrite[] myDendrites;

public DendriteContainer(Dendrite[] d)
{
	myDendrites = d;
}

public void compactDendrites()
{
	int counter = 0;
	for(int k = 0; k < myDendrites.length; k++)
	{
		if(myDendrites[k] != null)
			counter++;
	}
	Dendrite[] out = new Dendrite[counter + 1];
	counter = 0;
	for(int k = 0; k < myDendrites.length; k++)
	{
		if(myDendrites[k] != null)
			{
			out[counter] = myDendrites[k];
			counter++;
			}
	}
	myDendrites = out;
	
}

/*public void removeGroup(int group)
{
	for(int k = 0; k < myDendrites.length; k++)
	{
		if(myDendrites[k] != null)
		{
			if(myDendrites[k].groupMember == group)
				myDendrites[k] = null;
		}
	}
}*/
}
