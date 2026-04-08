package neuron_analyzer;

public class IgnoreCriteria {
int[] percentages;
boolean[] overlap;
boolean AutoIgnorePercentages;
boolean AutoIgnoreOverlap;

boolean activeP = false;
boolean activeO = false;

	public IgnoreCriteria(int[] Percentages)
	{
		percentages = Percentages;
	}
	
	public IgnoreCriteria(boolean[] Overlap)
	{
		overlap = Overlap;
	}
	
	public IgnoreCriteria(int r, int g, int b)
	{
		percentages = new int[] {r,g,b};
	}
	
	public IgnoreCriteria(boolean r, boolean g, boolean b)
	{
		overlap = new boolean[] {r,g,b};
	}
	
	public void load(int[] t)
	{		
		percentages = t;
	}
	
	public void load(boolean[] t)
	{		
		overlap = t;
	}
		
			
	private boolean testRed(int r)
	{
		//if r (percent) is less than cutoff the ignore this puncta		
		return (r < percentages[0]);
	}
	
	private boolean testGreen(int r)
	{		
		return (r < percentages[1]);
	}
	
	private boolean testBlue(int r)
	{
		return (r < percentages[2]);
	}
	
	private boolean testRed(boolean r)
	{
		//if r (percent) is less than cutoff the ignore this puncta		
		return (r == overlap[0]);
	}
	
	private boolean testGreen(boolean r)
	{		
		return (r == overlap[1]);
	}
	
	private boolean testBlue(boolean r)
	{
		return (r == overlap[2]);
	}
	
	public boolean testCriteria(int[] input)
	{
		return testRed(input[0]) || testGreen(input[1]) || testBlue(input[2]);
	}
	
	public boolean testCriteria(boolean[] input)
	{
		return testRed(input[0]) || testGreen(input[1]) || testBlue(input[2]);
	}
	
	public String getText(int i)
	{
		return new Integer(percentages[i]).toString();
	}
	
	public String getText2(int i)
	{
		return new Boolean(overlap[i]).toString();
	}
	
	public void autoIgnorePercentages(boolean b)
	{
		AutoIgnorePercentages = b;
	}
	
	public void autoIgnoreOverlap(boolean b)
	{
		AutoIgnoreOverlap = b;
	}
	
	public void activateP()
	{
		activeP = true;
	}
	
	public void deactivateO()
	{
		activeO = false;
	}
	
	public void deactivateP()
	{
		activeP = false;
	}
	
	public void activateO()
	{
		activeO = true;
	}
}
