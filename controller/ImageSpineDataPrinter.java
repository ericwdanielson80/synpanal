package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that produces one row per image of aggregated spine
 * measurements into "Spine_Data_Per_Image.txt", including density,
 * head/neck widths, length, per-morphology densities and histogram buckets
 * for spines carrying zero, one, two or three or more puncta.
 */
public class ImageSpineDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens "Spine_Data_Per_Image.txt" under baseDirectory and writes the
	 * header row immediately. The l, FL and d parameters are the LogInfo,
	 * functionListener controller and DataPrinterManager used by the parent
	 * DataPrinter class to query thresholds and table data.
	 */
	public ImageSpineDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Spine_Data_Per_Image.txt",l,FL,d);
		printImageSpineData();
	}
	
	/**
	 * Appends a block of spine measurements for the image named filename,
	 * delegating to the private helper.
	 */
	public void printData(String filename)
	{
		printImageSpineData(filename);
	}


	/**
	 * Writes the header row containing "Filename", "Group", "Color",
	 * "Threshold" followed by the measurement titles parsed from a long
	 * comma-separated literal (Spine Density, Spine Head Width, Spine Neck
	 * Width, Spine Length, the four per-type densities and the four spine-
	 * per-puncta buckets). Each parsed title is written with a tab prefix.
	 */
	public void printImageSpineData()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		String s = "Spine Density, Spine Head Width,Spine Neck Width, Spine Length,Mushroom Density,Thin Density,Stubby Density,Filopodia Density,Spines with 0, Spines with 1, Spines with 2, Spines with >3";
		String[] titles = s.split(",");
		for(int k = 0; k < titles.length; k++)
		{
			pW.print('\t' + titles[k]);
		}
		pW.println();
	}
	
    
	/**
	 * Delegates to DataPrinter.printSpineDataPerNeuron to emit rows for
	 * every group and enabled color, using filename as the leading column.
	 */
	private void printImageSpineData(String filename)
    {
    	//filename, group, color, threshold
 		super.printSpineDataPerNeuron(filename);
    }
		
		
}
