package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that writes one row per individual spine into
 * "Individual_Spine_Data.txt". Iterates over every group and every enabled
 * color and appends rows using the tableData returned by
 * DataPrinterManager.getIndividualSpineData for that combination.
 */
public class IndividualSpineDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens "Individual_Spine_Data.txt" under baseDirectory and emits the
	 * header row via printSpineDataHeader. The l, FL and d parameters are
	 * the LogInfo, controller and DataPrinterManager consulted by the base
	 * class.
	 */
	public IndividualSpineDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Individual_Spine_Data.txt",l,FL,d);
		printSpineDataHeader();
	}
	
	/**
	 * Appends per-individual-spine rows for the image named filename.
	 */
	public void printData(String filename)
	{
		printIndividualSpineData(filename);
	}


	/**
	 * Writes the header row with fixed "Filename", "Group", "Color",
	 * "Threshold" columns then the title columns returned by
	 * dataManager.getIndividualSpineData(0, 0).
	 */
	public void printSpineDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getIndividualSpineData(0,0));		
	}
	
    
	/**
	 * Iterates every group and every enabled channel, populates the leader
	 * array with filename, group name, channel name and channel threshold
	 * (looked up from fL.getRedThreshold etc. and formatted via dI.toString)
	 * and then calls printIndividualSpineInfo for that combination.
	 */
	private void printIndividualSpineData(String filename)
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
    			printIndividualSpineInfo(k,0,leader);	    			
    		}
    		if(lI.printGreen())
    		{
    			leader[2]="Green";
    			leader[3] = dI.toString(fL.getGreenThreshold());
    			printIndividualSpineInfo(k,1,leader);	    			
    		}
    		if(lI.printBlue())
    		{
    			leader[2]="Blue";
    			leader[3] = dI.toString(fL.getBlueThreshold());
    			printIndividualSpineInfo(k,2,leader);	    			
    		}
    		
    	}
    }
	

	/**
	 * Fetches the individual-spine tableData for the given group and color
	 * indices and writes its rows through DataPrinter.printTableData, using
	 * leader as the shared prefix.
	 */
	private void printIndividualSpineInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getIndividualSpineData(myGroup, myColor);
	printTableData(d, leader);
	}
		
		
}
