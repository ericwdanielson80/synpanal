package neuron_analyzer;

public class OverlapObject {
Puncta[] rgb = new Puncta[3];
OwnershipObject oo = new OwnershipObject();
int number = 0;
	
    public OverlapObject()
	{
		
	}
		
	public int numberOverlap()
	{
		return number;
	}
	
	public void setRed(Puncta p)
	{
		rgb[0] = p;
		number++;
	}
	
	public void setGreen(Puncta p)
	{
		rgb[1] = p;
		number++;
	}
	
	public void setBlue(Puncta p)
	{
		rgb[2] = p;
		number++;
	}
	
	public boolean hasRed()
	{
		if(rgb[0] != null)
			return true;
		return false;
	}
	
	public boolean hasGreen()
	{
		if(rgb[1] != null)
			return true;
		return false;
	}
	
	public boolean hasBlue()
	{
		if(rgb[2] != null)
			return true;
		return false;
	}
	
	public void restore(boolean restoreRed, boolean restoreGreen, boolean restoreBlue, boolean ifRed, boolean ifGreen, boolean ifBlue)
	{
		if(ifRed == hasRed() && ifGreen == hasGreen() && ifBlue == hasBlue())
		{
			if(restoreRed)				
				rgb[0].bC.isIgnored = false;				
			if(restoreGreen)
				rgb[1].bC.isIgnored = false;
			if(restoreBlue)
				rgb[2].bC.isIgnored = false;
		}
	}
	
		
}
