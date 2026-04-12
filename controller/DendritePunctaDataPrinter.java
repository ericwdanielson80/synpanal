package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that emits per-dendrite puncta measurements into
 * "Puncta_Data_Per_Dendrite.txt". Each row summarizes one dendrite's puncta
 * statistics for a single group/color/threshold combination using tableData
 * supplied by the DataPrinterManager.
 */
public class DendritePunctaDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens "Puncta_Data_Per_Dendrite.txt" in the supplied directory and
	 * writes the header row. The l, FL and d parameters are the standard
	 * DataPrinter dependencies (LogInfo, controller and data manager).
	 */
	public DendritePunctaDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Puncta_Data_Per_Dendrite.txt",l,FL,d);
		printPunctaDataHeader();
	}
	
	/**
	 * Appends one block of per-dendrite puncta rows for the image named
	 * filename by calling the private helper.
	 */
	public void printData(String filename)
	{
		printPunctaDataPerDendrite(filename);
	}

	/**
	 * Writes the header row. Starts with "Filename", "Group", "Color",
	 * "Threshold" then appends column titles returned by
	 * dataManager.getDendritePunctaTableData(0, 0) via the base class
	 * printTitles helper.
	 */
	public void printPunctaDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getDendritePunctaTableData(0,0));		
	}
	
	/**
	 * Iterates each group name and every enabled color channel, populates
	 * the leader array (filename, group, color, threshold - the threshold
	 * is formatted via dI.toString) and calls printDendritePunctaInfo to
	 * emit the corresponding dendrite-puncta rows.
	 */
	private void printPunctaDataPerDendrite(String filename)
    {
//    	filename, group, color, threshold
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
    			this.printDendritePunctaInfo(k,0,leader);	    			
    		}
    		if(lI.printGreen())
    		{
    			leader[2]="Green";
    			leader[3] = dI.toString(fL.getGreenThreshold());
    			this.printDendritePunctaInfo(k,1,leader);	    			
    		}
    		if(lI.printBlue())
    		{
    			leader[2]="Blue";
    			leader[3] = dI.toString(fL.getBlueThreshold());
    			this.printDendritePunctaInfo(k,2,leader);	    			
    		}
    		
    	}
    }
    
	
	/**
	 * Fetches the dendrite-puncta tableData for the given group and color
	 * indices and writes its rows with DataPrinter.printTableData using
	 * leader as the shared prefix.
	 */
	private void printDendritePunctaInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getDendritePunctaTableData(myGroup, myColor);
	printTableData(d, leader);
	}

			
		
}
