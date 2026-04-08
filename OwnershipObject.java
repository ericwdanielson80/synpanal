package neuron_analyzer;

public class OwnershipObject {
int mySpine = -1; 
int ownership = -1;

	public OwnershipObject()
	{
		
	}
	
	public void addSpine(int spineIndex, int ownershipWeight)
	{
		if(mySpine == -1)
		{
			ownership = ownershipWeight;
			mySpine  = spineIndex;
			return;
		}
		
		if(ownershipWeight < ownership)
		{
			ownership = ownershipWeight;
			mySpine  = spineIndex;
		}
	}
}
