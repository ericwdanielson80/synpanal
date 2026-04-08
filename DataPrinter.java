package neuron_analyzer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

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
	
	public void finishPrinting()
	{
		pW.flush();
		pW.close();
		pW = null;
		lI = null;
		dataManager = null;
		dI = null;
	}
	
	public void flush()
	{
		pW.flush();
	}
		

	public void printData(String filename)
	{ 			
		
	}
	
	public void printDataHeader()
	{
		//prints the column names.
	}
	
	public void printTitles(tableData d)
	{
		String[] s = d.getTitles();
		for(int k = 0; k < s.length; k++)
		{
			pW.print('\t'+ s[k]);
		}
		pW.println();		
	}
	
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
		
				
		private void printIndividualPunctaInfo(int myGroup, int myColor, String[] leader)
		{
			tableData d = dataManager.getIndividualPunctaData(myGroup, myColor);
			this.printTableData(d, leader);
		}
		
		private void printIndividualSpineInfo(int myGroup, int myColor, String[] leader)
		{
			tableData d = dataManager.getIndividualSpineData(myGroup, myColor);
			this.printTableData(d, leader);
		}
		
		private void printIndividualCellInfo(int myGroup, int myColor, String[] leader)
		{
			tableData d = dataManager.getIndividualSpineData(myGroup, myColor);
			this.printTableData(d, leader);
		}
		
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
