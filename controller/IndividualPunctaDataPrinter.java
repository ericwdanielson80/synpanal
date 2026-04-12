package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that writes one row per individual puncta into
 * "Individual_Puncta_Data.txt". For each image it walks every group and
 * every enabled color and appends rows built from
 * DataPrinterManager.getIndividualPunctaData.
 */
public class IndividualPunctaDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens "Individual_Puncta_Data.txt" under baseDirectory and emits the
	 * header via printPunctaDataHeader. The l, FL and d parameters carry
	 * the LogInfo, controller and data manager consulted by the base class.
	 */
	public IndividualPunctaDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Individual_Puncta_Data.txt",l,FL,d);
		printPunctaDataHeader();
	}
	
	/**
	 * Appends per-individual-puncta rows for the image named filename.
	 */
	public void printData(String filename)
	{
		printIndividualPunctaData(filename);
	}


	/**
	 * Writes the header row with fixed "Filename", "Group", "Color",
	 * "Threshold" columns and then the titles from
	 * dataManager.getIndividualPunctaData(0, 0).
	 */
	public void printPunctaDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getIndividualPunctaData(0,0));		
	}
	
    
	/**
	 * Iterates each group returned by dataManager.getGroupNames and every
	 * enabled color channel, filling leader with filename, group name,
	 * channel name and the channel threshold read from the controller, and
	 * then calls printIndividualPunctaInfo to emit the rows.
	 */
	private void printIndividualPunctaData(String filename)
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
    			printIndividualPunctaInfo(k,0,leader);	    			
    		}
    		if(lI.printGreen())
    		{
    			leader[2]="Green";
    			leader[3] = dI.toString(fL.getGreenThreshold());
    			printIndividualPunctaInfo(k,1,leader);	    			
    		}
    		if(lI.printBlue())
    		{
    			leader[2]="Blue";
    			leader[3] = dI.toString(fL.getBlueThreshold());
    			printIndividualPunctaInfo(k,2,leader);	    			
    		}
    		
    	}
    }
	

	/**
	 * Looks up the individual-puncta tableData for the given group and
	 * color indices and writes it via DataPrinter.printTableData using the
	 * supplied leader as the per-row prefix.
	 */
	private void printIndividualPunctaInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getIndividualPunctaData(myGroup, myColor);
	printTableData(d, leader);
	}
		
		
}
