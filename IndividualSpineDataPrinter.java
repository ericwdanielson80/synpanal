package neuron_analyzer;

import java.io.*;

public class IndividualSpineDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	public IndividualSpineDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{	
		super(baseDirectory + '/' + "Individual_Spine_Data.txt",l,FL,d);
		printSpineDataHeader();
	}
	
	public void printData(String filename)
	{ 			
		printIndividualSpineData(filename);
	}
	
	
	public void printSpineDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getIndividualSpineData(0,0));		
	}
	
    
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
	

	private void printIndividualSpineInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getIndividualSpineData(myGroup, myColor);
	printTableData(d, leader);
	}
		
		
}
