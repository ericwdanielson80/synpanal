package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
	
import java.io.*;
	
/**
 * Concrete DataPrinter that writes per-dendrite puncta and spine
 * measurements to a text file, row per dendrite per group per color.
 * The file comment reminds which statistics are emitted (intensity,
 * puncta/spine densities, morphology breakdowns, and so on) in the
 * red-then-green-then-blue order that matches the data panel. The
 * class cooperates with a DataPrinterManager which supplies the
 * group list and the tableData views whose rows are serialised.
 */
public class DendriteDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types
	
	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */
	
	/**
	 * Builds the printer. The output path is constructed from
	 * baseDirectory plus a fixed "Puncta_Data_Per_Dendrite.txt"
	 * filename. l is the LogInfo carrying which channels/summaries
	 * should be printed, FL is the functionListener used to fetch
	 * runtime values like thresholds, and d is the DataPrinterManager
	 * facade. After the base constructor sets up the writer,
	 * printPunctaDataHeader emits the header line.
	 */
	public DendriteDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{	
		super(baseDirectory + '/' + "Puncta_Data_Per_Dendrite.txt",l,FL,d);
		printPunctaDataHeader();
	}
	
	/**
	 * Entry point invoked once per image. Delegates to
	 * printPunctaDataPerDendrite, passing the image filename so each
	 * emitted row can be tagged with its source file.
	 */
	public void printData(String filename)
	{ 			
		printPunctaDataPerDendrite(filename);
	}
	
	/**
	 * Writes the header row for per-dendrite puncta output: the
	 * fixed leading columns Filename/Group/Color/Threshold followed
	 * by the column titles from a sample DendritePunctaTableData
	 * taken from group 0, color 0.
	 */
	public void printPunctaDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		super.printTitles(dataManager.getDendritePunctaTableData(0,0));		
	}
	
	/**
	 * Writes the header row for per-dendrite spine output following
	 * the same pattern as printPunctaDataHeader but sourcing titles
	 * from a DendriteSpineTableData instead.
	 */
	public void printSpineDataHeader()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold"+'\t');
		super.printTitles(dataManager.getDendriteSpineTableData(0,0));		
	}
	
	/**
	 * Emits one block of rows per group and per enabled color for
	 * the dendrite puncta table. leader is a 4-element array holding
	 * Filename/Group/Color/Threshold that is filled in before each
	 * call to printDendritePunctaInfo. groups is the list of group
	 * names from the DataPrinterManager, and LogInfo.printRed/Green/
	 * Blue gate which color channels actually run. The threshold for
	 * each color is fetched from the functionListener via
	 * getRedThreshold/getGreenThreshold/getBlueThreshold.
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
	 * Mirror of printPunctaDataPerDendrite but for the per-dendrite
	 * spine table. Builds the same 4-element leader and iterates
	 * over groups and enabled colors, delegating to
	 * printDendriteSpineInfo for each combination.
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
	 * Retrieves the puncta tableData for (myGroup, myColor) through
	 * the DataPrinterManager and asks the shared printTableData to
	 * emit all of its rows with the supplied leader prefix.
	 */
	private void printDendritePunctaInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getDendritePunctaTableData(myGroup, myColor);
	printTableData(d, leader);
	}
    	
	/**
	 * Retrieves the spine tableData for (myGroup, myColor) and
	 * emits each row with the leader prefix via the shared
	 * printTableData helper.
	 */
	private void printDendriteSpineInfo(int myGroup, int myColor, String[] leader)
	{
	tableData d = dataManager.getDendriteSpineTableData(myGroup, myColor);
	printTableData(d, leader);
	}
		
		
}
    		