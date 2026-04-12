package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Abstract-style base class (used directly and subclassed) that writes
 * formatted log data to a single text file through a PrintWriter. It holds
 * the LogInfo, functionListener, and DataPrinterManager references needed
 * to pull the currently selected groups, colors, and thresholds, plus a
 * shared DecimalFormat for numeric output. Subclasses extend the per-neuron
 * and per-row print routines to produce the various export formats used by
 * the neuron analyzer.
 */
public class DataPrinter {
	PrintWriter pW;	
	/*
	 * PrintWriter punctaWriter
	 * PrintWriter spineWriter
	 * PrintWriter cellWriter
	 */
	LogInfo lI;
	DecimalFormat dF = new DecimalFormat("#######.##");
	functionListener fL;
	DataPrinterManager dataManager;
	Integer dI = new Integer(0);
	
	/**
	 * Opens a PrintWriter on the given output file and stashes references
	 * to the LogInfo, functionListener, and DataPrinterManager so later
	 * print routines can ask them for the information to export. If the
	 * file cannot be opened the exception is logged to stdout and pW is
	 * left null.
	 */
	public DataPrinter(String fileName,LogInfo l, functionListener FL, DataPrinterManager d)
	{		
		try{
	    	pW = new PrintWriter(new File(fileName));	    	
	    	}
	    	catch(FileNotFoundException e)
	    	{
	    		System.out.println("Cannot make File: " + fileName);
	    	}
		
		lI=l;
		fL=FL;
		dataManager=d;		
	}
	
	/** Flushes and closes the underlying PrintWriter and nulls out the held references so the object can be garbage collected. */
	public void finishPrinting()
	{
		pW.flush();
		pW.close();
		pW = null;
		lI = null;
		dataManager = null;
		dI = null;
	}
	
	/** Flushes any buffered output to disk without closing the writer. */
	public void flush()
	{
		pW.flush();
	}
		

	/** Subclass hook for emitting the data body; the base implementation is intentionally empty. */
	public void printData(String filename)
	{ 			
		
	}
	
	/** Subclass hook for emitting the column-name header row; the base implementation is empty. */
	public void printDataHeader()
	{
		//prints the column names.
	}
	
	/** Writes the tab-separated column titles of a tableData followed by a newline, used by subclasses emitting table exports. */
	public void printTitles(tableData d)
	{
		String[] s = d.getTitles();
		for(int k = 0; k < s.length; k++)
		{
			pW.print('\t'+ s[k]);
		}
		pW.println();		
	}
	
	/**
	 * Writes every non-ignored row of a tableData to the PrintWriter,
	 * prefixing each row with the supplied leader columns (filename, group,
	 * color, and an extra identifier). Only columns whose printList entry
	 * is true are emitted; the locals num and printList hold the row count
	 * and per-column visibility mask.
	 */
	public void printTableData(tableData d,String[] leader)
	{		
		//Header should contain Filename, Group, Color, for Per Data
		//Header should contain Filename, Group, Color, Dendrite for Puncta & Spines
//		Header should contain Filename, Group, Color, Cell Number for Cell Body
		int num = d.getRows();
		boolean[] printList = d.getPrintList();		
		for(int k = 0; k < num; k++)
		{
			
			if(!d.isIgnored(k))
			{
				printLeader(leader); //prints all the crap in front of the data;
				for(int j = 0; j < printList.length; j++)				
				{
					if(printList[j])
						pW.print('\t' + d.getData(k,j));
				}
				pW.println();
			}
		}			
	}
	
	/**
	 * For each group and for each enabled color channel, prints the
	 * per-neuron puncta aggregate row with a leader of (filename, group,
	 * color, threshold). Red/green/blue inclusion is controlled by the
	 * LogInfo print flags.
	 */
	public void printPunctaDataPerNeuron(String filename)
	    {
	 		//filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printNeuronPunctaInfo(k,0,leader);	   
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printNeuronPunctaInfo(k,1,leader);	  
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printNeuronPunctaInfo(k,2,leader);	  
	    			pW.println();
	    		}
	    		
	    	}
	    }
	 	
	/** Per-neuron spine summary analogue of printPunctaDataPerNeuron. Emits group/color gated rows driven by LogInfo. */
	public void printSpineDataPerNeuron(String filename)
	    {
	 		//filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{	    			
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printNeuronSpineInfo(k,0,leader);	 
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printNeuronSpineInfo(k,1,leader);	    
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printNeuronSpineInfo(k,2,leader);	  
	    			pW.println();
	    		}
	    		
	    	}
	    }
	 	
	/** Per-neuron cell-body summary analogue of printPunctaDataPerNeuron. Emits group/color gated rows driven by LogInfo. */
	public void printCellDataPerNeuron(String filename)
	    {
	 		//filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printNeuronCellInfo(k,0,leader);	
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printNeuronCellInfo(k,1,leader);	   
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printNeuronCellInfo(k,2,leader);	  
	    			pW.println();
	    		}
	    		
	    	}
	    }
	 	
	/** Per-neuron dendrite summary analogue of printPunctaDataPerNeuron. Emits group/color gated rows driven by LogInfo. */
	private void printDendriteDataPerNeuron(String filename)
	    {
	 		//filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printNeuronDendriteInfo(k,0,leader);	 
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printNeuronDendriteInfo(k,1,leader);	  
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printNeuronDendriteInfo(k,2,leader);	  
	    			pW.println();
	    		}
	    		
	    	}
	    }
	    
	
	    
	/** Per-puncta row-level export: iterates groups and enabled channels and delegates to printIndividualPunctaInfo for the body. */
	private void printIndividualPunctaData(String filename)
	    {
//	    	filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printIndividualPunctaInfo(k,0,leader);	    	
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printIndividualPunctaInfo(k,1,leader);	    
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printIndividualPunctaInfo(k,2,leader);	    	
	    			pW.println();
	    		}
	    		
	    	}
	    }
	    
	/** Per-spine row-level export; mirror of printIndividualPunctaData targeting spine data. */
	private void printIndividualSpineData(String filename)
	    {
//	    	filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printIndividualSpineInfo(k,0,leader);	    	
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printIndividualSpineInfo(k,1,leader);	    
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printIndividualSpineInfo(k,2,leader);	    
	    			pW.println();
	    		}
	    		
	    	}
	    }
	    
	/** Per-cell-body row-level export; mirror of printIndividualPunctaData targeting cell data. */
	private void printIndividualCellData(String filename)
	    {
//	    	filename, group, color, threshold
	 		String[] leader = new String[4];
	 		leader[0] = filename;
	    	String[] groups = dataManager.getGroupNames();
	    	for(int k = 0; k < groups.length; k++)
	    	{
	    		leader[1]=groups[k];
	    		if(lI.printRed())
	    		{
	    			leader[2]="Red";
	    			leader[3] = dI.toString(fL.getRedThreshold());
	    			this.printIndividualCellInfo(k,0,leader);	    	
	    			pW.println();
	    		}
	    		if(lI.printGreen())
	    		{
	    			leader[2]="Green";
	    			leader[3] = dI.toString(fL.getGreenThreshold());
	    			this.printIndividualCellInfo(k,1,leader);	    
	    			pW.println();
	    		}
	    		if(lI.printBlue())
	    		{
	    			leader[2]="Blue";
	    			leader[3] = dI.toString(fL.getBlueThreshold());
	    			this.printIndividualCellInfo(k,2,leader);	    
	    			pW.println();
	    		}
	    		
	    	}
	    }
				
				
	/** Writes a single per-neuron puncta row by asking dataManager for the formatted values and prefixing them with the leader columns. */
	private void printNeuronPunctaInfo(int myGroup, int myColor,String[] leader)
		{
			String[] s = dataManager.getNeuronPunctaData(myGroup, myColor);
			this.printLeader(leader);
			for(int k = 0; k < s.length; k++)
			{
				pW.print('\t' + s[k]);
			}
			//pW.println();
		}
		
		/** Writes a single per-neuron spine row by asking dataManager for the formatted values and prefixing them with the leader columns. */
		private void printNeuronSpineInfo(int myGroup, int myColor, String[] leader)
		{
			String[] s = dataManager.getNeuronSpineData(myGroup, myColor);
			this.printLeader(leader);
			for(int k = 0; k < s.length; k++)
			{
				pW.print('\t' + s[k]);
			}
			//pW.println();
		}
		
		/** Writes a single per-neuron cell-body row by asking dataManager for the formatted values and prefixing them with the leader columns. */
		private void printNeuronCellInfo(int myGroup, int myColor,String[] leader)
		{
			String[] s = dataManager.getNeuronCellData(myGroup, myColor);
			this.printLeader(leader);
			for(int k = 0; k < s.length; k++)
			{
				pW.print('\t' + s[k]);
			}
			//pW.println();
		}
		
		/** Writes a single per-neuron dendrite row by asking dataManager for the formatted values and prefixing them with the leader columns. */
		private void printNeuronDendriteInfo(int myGroup, int myColor, String[] leader)
		{
			String[] s = dataManager.getNeuronDendriteData(myGroup, myColor);
			this.printLeader(leader);
			for(int k = 0; k < s.length; k++)
			{
				pW.print('\t' + s[k]);
			}
			//pW.println();
		}
		
				
		/** Fetches the individual-puncta tableData for the group and color and delegates to printTableData for row-by-row output. */
		private void printIndividualPunctaInfo(int myGroup, int myColor, String[] leader)
		{
			tableData d = dataManager.getIndividualPunctaData(myGroup, myColor);
			this.printTableData(d, leader);
		}
		
		/** Fetches the individual-spine tableData for the group and color and delegates to printTableData for row-by-row output. */
		private void printIndividualSpineInfo(int myGroup, int myColor, String[] leader)
		{
			tableData d = dataManager.getIndividualSpineData(myGroup, myColor);
			this.printTableData(d, leader);
		}
		
		/** Fetches the individual-cell tableData (currently reusing the spine data source) and delegates to printTableData for row-by-row output. */
		private void printIndividualCellInfo(int myGroup, int myColor, String[] leader)
		{
			tableData d = dataManager.getIndividualSpineData(myGroup, myColor);
			this.printTableData(d, leader);
		}
		
		/** Prints the leader columns for a row: leader[0] (filename) without a leading tab, and the rest of the entries prefixed with tabs. */
		private void printLeader(String[] leader)
		{
			/*
			 * get filename from fL
			 * get group name from fL or dataManager
			 * get Color somehow
			 * get threshold from fL or dataManager
			 * modify puncta and spine stuff so that it describes what dendrite it came from...or just print the name e.g. 2.0
			 */
			pW.print(leader[0]);
			for(int k = 1; k < leader.length; k++)
			{				
				pW.print('\t'+ leader[k]);
			}
		}
		
		
	
	
	
}
