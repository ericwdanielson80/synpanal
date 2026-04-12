package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that writes one row per individual cell/region into
 * "Individual_Generic_Data.txt". For each image it iterates over every group
 * tab and each enabled color channel and asks the DataPrinterManager for the
 * corresponding tableData of generic per-cell measurements, then appends the
 * rows after a filename/group/color/threshold leader.
 */
public class IndividualCellDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens "Individual_Generic_Data.txt" under baseDirectory and writes the
	 * header row. The l, FL and d parameters are the LogInfo, controller and
	 * DataPrinterManager used by the DataPrinter base class to read
	 * thresholds and table data. The header is emitted by
	 * printCellDataHeader.
	 */
	public IndividualCellDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Individual_Generic_Data.txt",l,FL,d);
		printCellDataHeader();
	}
	
	/**
	 * Appends the per-cell data block for the image named filename by
	 * delegating to the private helper.
	 */
	public void printData(String filename)
	{
		printIndividualCellData(filename);
	}


	/**
	 * Writes the header row, starting with the fixed "Filename", "Group",
	 * "Color", "Threshold" columns and then appending the column titles
	 * returned by dataManager.getIndividualCellData(0, 0) via
	 * DataPrinter.printTitles.
	 */
	public void printCellDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getIndividualCellData(0,0));		
	}
	
    
	/**
	 * Iterates over every group and each enabled color channel and emits the
	 * per-cell rows. A four-element leader array carries filename, group,
	 * color and threshold; for each channel that lI reports as enabled the
	 * leader is populated with the channel name and the threshold is pulled
	 * from fL.getRedThreshold, fL.getGreenThreshold or fL.getBlueThreshold
	 * (formatted via dI.toString) before printIndividualCellInfo is called
	 * for that (group, color) combination. The filename parameter is the
	 * image whose rows are being recorded.
	 */
	private void printIndividualCellData(String filename)
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
    			printIndividualCellInfo(k,0,leader);	    			
    		}
    		if(lI.printGreen())
    		{
    			leader[2]="Green";
    			leader[3] = dI.toString(fL.getGreenThreshold());
    			printIndividualCellInfo(k,1,leader);	    			
    		}
    		if(lI.printBlue())
    		{
    			leader[2]="Blue";
    			leader[3] = dI.toString(fL.getBlueThreshold());
    			printIndividualCellInfo(k,2,leader);	    			
    		}
    		
    	}
    }
	

	/**
	 * Fetches the individual-cell tableData for the given group index
	 * myGroup and color index myColor (0 red, 1 green, 2 blue) and prints it
	 * row by row through DataPrinter.printTableData, prefixing each row
	 * with the leader array already populated by the caller.
	 */
	private void printIndividualCellInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getIndividualCellData(myGroup, myColor);
	printTableData(d, leader);
	}
		
		
}
