package neuron_analyzer;

public class BooleanContainer {

boolean isSelected;
boolean isIgnored;

	BooleanContainer(boolean iS,boolean iI)
	{
		isSelected = iS;
		isIgnored = iI;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public boolean isIgnored()
	{
		return isIgnored;
	}
	
	public void pushSelected()
	{
		if(isSelected)
			isSelected = false;
		else
			isSelected = true;
	}
	
	public void pushIgnored()
	{
		if(isIgnored)
			isIgnored = false;
		else
			isIgnored = true;
	}
		
}
