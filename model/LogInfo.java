package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
	
	
/**
 * Configuration object carrying the user's logging and batch-printing
 * preferences: which transfection/condition labels exist, which color
 * channels to include, whether each kind of summary (dendrite, puncta,
 * spine, cell body) should be written, whether individual per-object
 * rows should be exported, the per-color puncta limits, and the
 * autolog flag. Toggle methods flip each of these flags and are called
 * by UI actions that change logging settings.
 */
public class LogInfo{
		
	public String[] transfectionConditions;
	//int[] colors;
	public boolean red = false;
	public boolean green = false;
	public boolean blue = false;
	public boolean dI = false; //print out dendrite Info
	public boolean pI = false; //print Ave puncta info
	public boolean sI = false; //print Ave Spine INfo
	public boolean cI = false; //print Ave cell body Info
	public boolean ssI = false; //print individual spinedata
	public boolean ppI = false; //print individual punctaData
	public boolean ccI = false; //print individual cellData
	public boolean dsI = false; //print dendrite spine info
	public boolean dpI = false; //print dendrite puncta info
	public boolean ppS = false; //print Puncta per Spine Data
	public boolean autolog = true;
	String[] dataLabels;
	boolean[] outputData;
	public int punctaLimitRed = 4;
	public int punctaLimitGreen = 4;
	public int punctaLimitBlue = 4;
	
	
	/**
	 * Default constructor that seeds the transfectionConditions array
	 * with a single "Ctrl" label so the first analysis group always has
	 * a default name.
	 */
	public LogInfo()
	{		
		transfectionConditions = new String[] {"Ctrl"};
	}	
		
	/**
	 * Full constructor used when building a log configuration from a
	 * dialog. tC is the list of transfection/condition labels. Each
	 * entry of c marks a color as active when not -1, setting red,
	 * green, and blue respectively. The remaining flags map directly
	 * to the corresponding summary toggles: d enables dendrite info, p
	 * enables image puncta info, s enables image spine info, cel
	 * enables image cell info, ss enables individual spine info and
	 * pp enables individual puncta info.
	 */
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
	
	/** Replaces the current list of condition labels with tabInfo. */
	public void setTabs(String[] tabInfo)
	{		
		transfectionConditions = tabInfo;
	}
	
	/**
	 * Appends a new condition label in to the end of
	 * transfectionConditions by allocating a new array s one element
	 * longer, copying the existing labels, and writing in at the last
	 * position.
	 */
	public void addTab(String in)
	{		
		String[] s = new String[transfectionConditions.length + 1];
		System.arraycopy(transfectionConditions, 0, s, 0, transfectionConditions.length);
		s[transfectionConditions.length] = in;
		transfectionConditions = s;
	}
	
	/**
	 * Removes the condition label at index j by building a new array s
	 * one element shorter and copying every non-j entry into it while
	 * counter tracks the write position.
	 */
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
	
	/** Reports whether the red channel should be included in output. */
	public boolean printRed()
	{
		return red;		
	}
	
	/** Reports whether the green channel should be included in output. */
	public boolean printGreen()
	{
		return green;		
	}
	
	/** Reports whether the blue channel should be included in output. */
	public boolean printBlue()
	{
		return blue;		
	}
	
	/** Flips the per-dendrite spine-info output flag. */
	public void toggleDendriteSpineInfo()
	{
		if(dsI)
			dsI = false;
		else
			dsI = true;
	}
	/** Flips the per-dendrite puncta-info output flag. */
	public void toggleDendritePunctaInfo()
	{
		if(dpI)
			dpI = false;
		else
			dpI = true;
	}
	
	/** Flips the per-image puncta-info output flag. */
	public void toggleImagePunctaInfo()
	{
		if(pI)
			pI = false;
		else
			pI = true;
	}
	
	/** Flips the per-image spine-info output flag. */
	public void toggleImageSpineInfo()
	{
		if(sI)
			sI = false;
		else
			sI = true;
	}
	
	/** Flips the per-image cell-info output flag. */
	public void toggleImageCellInfo()
	{
		if(cI)
			cI = false;
		else
			cI = true;
	}
	
	/** Flips the individual puncta output flag. */
	public void toggleIndividualPunctaInfo()
	{
		if(ppI)
			ppI = false;
		else
			ppI = true;
	}
	
	/** Flips the individual spine output flag. */
	public void toggleIndividualSpineInfo()
	{
	
		if(ssI)
			ssI = false;
		else
			ssI = true;
	}
	
	/** Flips the individual cell-body output flag. */
	public void toggleIndividualCellInfo()
	{
		if(ccI)
			ccI = false;
		else
			ccI = true;
	}
	
	/** Flips the puncta-per-spine table output flag. */
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
	
	/** Flips the automatic logging flag. */
	public void toggleAutoLog()
	{
		if(autolog)
			autolog = false;
		else
			autolog = true;
	}
	
	/**
	 * Returns a colon-delimited concatenation of all
	 * transfectionConditions labels. The local s starts with the first
	 * entry and each subsequent label is appended with a leading
	 * ':' separator.
	 */
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
	