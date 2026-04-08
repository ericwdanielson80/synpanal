package neuron_analyzer;


public class LogInfo{

	String[] transfectionConditions;
	//int[] colors;
	boolean red = false;
	boolean green = false;
	boolean blue = false;
	boolean dI = false; //print out dendrite Info
	boolean pI = false; //print Ave puncta info
	boolean sI = false; //print Ave Spine INfo
	boolean cI = false; //print Ave cell body Info
	boolean ssI = false; //print individual spinedata
	boolean ppI = false; //print individual punctaData
	boolean ccI = false; //print individual cellData
	boolean dsI = false; //print dendrite spine info
	boolean dpI = false; //print dendrite puncta info
	boolean ppS = false; //print Puncta per Spine Data
	boolean autolog = true;
	String[] dataLabels;
	boolean[] outputData;
	int punctaLimitRed = 4;
	int punctaLimitGreen = 4;
	int punctaLimitBlue = 4;
	
	
	public LogInfo()
	{
		transfectionConditions = new String[] {"Ctrl"};
	}	
		
	public LogInfo(String[] tC, int[] c,boolean d, boolean p,boolean s, boolean cel,boolean ss, boolean pp)
	{
		transfectionConditions = tC;
		if(c[0] != -1)
		{
			red = true;
		}
		if(c[1] != -1)
		{
			green = true;
		}
		if(c[2] != -1)
		{
			blue = true;
		}
		dI = d;
		pI = p;		
		sI = s;
		cI = cel;
		ppI = pp;
		ssI = ss;
	}
	
	public void setTabs(String[] tabInfo)
	{
		transfectionConditions = tabInfo;
	}
	
	public void addTab(String in)
	{
		String[] s = new String[transfectionConditions.length + 1];
		System.arraycopy(transfectionConditions, 0, s, 0, transfectionConditions.length);
		s[transfectionConditions.length] = in;
		transfectionConditions = s;
	}
	
	public void deleteTab(int j)
	{
		String[] s = new String[transfectionConditions.length - 1];
		int counter = 0;
		for(int k = 0; k < transfectionConditions.length; k++)
		{
			if(k != j)
			{
				s[counter] = transfectionConditions[k];
				counter++;
			}
		}
		transfectionConditions = s;
	}
	
	public boolean printRed()
	{		
		return red;		
	}
	
	public boolean printGreen()
	{		
		return green;		
	}
	
	public boolean printBlue()
	{		
		return blue;		
	}
	
	public void toggleDendriteSpineInfo()
	{
		if(dsI)
			dsI = false;
		else
			dsI = true;
	}
	public void toggleDendritePunctaInfo()
	{
		if(dpI)
			dpI = false;
		else
			dpI = true;
	}
	
	public void toggleImagePunctaInfo()
	{
		if(pI)
			pI = false;
		else
			pI = true;
	}
	
	public void toggleImageSpineInfo()
	{
		if(sI)
			sI = false;
		else
			sI = true;
	}
	
	public void toggleImageCellInfo()
	{
		if(cI)
			cI = false;
		else
			cI = true;
	}
	
	public void toggleIndividualPunctaInfo()
	{
		if(ppI)
			ppI = false;
		else
			ppI = true;
	}
	
	public void toggleIndividualSpineInfo()
	{
		
		if(ssI)
			ssI = false;
		else
			ssI = true;
	}
	
	public void toggleIndividualCellInfo()
	{
		if(ccI)
			ccI = false;
		else
			ccI = true;
	}
	
	public void togglePunctaPerSpine()
	{		
		if(ppS)
			ppS = false;
		else
			ppS = true;
	}
	/*
	boolean dI = true; //print out dendrite Info
	
	 */
	
	public void toggleAutoLog()
	{	
		if(autolog)
			autolog = false;
		else
			autolog = true;
	}
	
	public String getCategories()
	{
		String s = transfectionConditions[0];
		for(int k = 1; k < this.transfectionConditions.length; k++)
		{
			s = s+":"+transfectionConditions[k];
		}
		return s;
	}
	
	
		
}
