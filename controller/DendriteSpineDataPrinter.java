package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that emits per-dendrite spine measurements into
 * "Spine_Data_Per_Dendrite.txt". Each row represents a single dendrite's
 * spine statistics for a given group/color/threshold combination, with
 * columns taken from the dendrite-level spine tableData supplied by the
 * DataPrinterManager.
 */
public class DendriteSpineDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens "Spine_Data_Per_Dendrite.txt" and writes the header row. The
	 * baseDirectory, l, FL and d parameters mirror the base DataPrinter
	 * constructor's role: output folder, LogInfo, controller and data
	 * manager used to query tableData.
	 */
	public DendriteSpineDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Spine_Data_Per_Dendrite.txt",l,FL,d);
		printSpineDataHeader();
	}
	
	/**
	 * Appends per-dendrite spine rows for the image named filename.
	 */
	public void printData(String filename)
	{
		printSpineDataPerDendrite(filename);
	}


	/**
	 * Emits the header row with fixed "Filename", "Group", "Color",
	 * "Threshold" columns followed by the spine-per-dendrite column titles
	 * obtained from dataManager.getDendriteSpineTableData(0, 0) via
	 * DataPrinter.printTitles.
	 */
	public void printSpineDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getDendriteSpineTableData(0,0));		
	}
	
    
	/**
	 * Walks each group returned by dataManager.getGroupNames and, for every
	 * color channel that LogInfo.printRed/Green/Blue reports enabled, fills
	 * a four-element leader with filename/group/color/threshold (the
	 * threshold coming from the controller via fL.getRedThreshold etc.) and
	 * invokes printDendriteSpineInfo to write the rows.
	 */
	private void printSpineDataPerDendrite(String filename)
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
    			printDendriteSpineInfo(k,0,leader);	    			
    		}
    		if(lI.printGreen())
    		{
    			leader[2]="Green";
    			leader[3] = dI.toString(fL.getGreenThreshold());
    			printDendriteSpineInfo(k,1,leader);	    			
    		}
    		if(lI.printBlue())
    		{
    			leader[2]="Blue";
    			leader[3] = dI.toString(fL.getBlueThreshold());
    			printDendriteSpineInfo(k,2,leader);	    			
    		}
    		
    	}
    }
	

	/**
	 * Retrieves the dendrite-spine tableData for the given group index and
	 * color index and emits it row by row through
	 * DataPrinter.printTableData, using the supplied leader to prefix each
	 * row.
	 */
	private void printDendriteSpineInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getDendriteSpineTableData(myGroup, myColor);
	printTableData(d, leader);
	}
		
		
}
