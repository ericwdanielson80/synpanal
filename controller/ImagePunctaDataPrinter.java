package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that writes one row per analyzed image of aggregated puncta
 * measurements into "Puncta_Data_Per_Image.txt". The row includes puncta
 * density, puncta intensity, puncta area and puncta intensity per dendrite
 * length, grouped by the current group/color/threshold leader columns.
 */
public class ImagePunctaDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Creates the printer targeting "Puncta_Data_Per_Image.txt" in the given
	 * directory. The baseDirectory parameter is the output folder; l is the
	 * LogInfo controlling which channels are logged; FL is the controller
	 * used to read current thresholds; d is the DataPrinterManager supplying
	 * table data. The header row is emitted immediately via
	 * printImagePunctaData.
	 */
	public ImagePunctaDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Puncta_Data_Per_Image.txt",l,FL,d);
		printImagePunctaData();
	}
	
	/**
	 * Appends a data block for the current image identified by filename by
	 * invoking the private helper.
	 */
	public void printData(String filename)
	{
		printImagePunctaData(filename);
	}


	/**
	 * Emits the column header, starting with "Filename", "Group", "Color",
	 * "Threshold" and followed by the measurement titles split from the
	 * comma-separated string "Puncta Density, Puncta Intensity, Puncta Area,
	 * Puncta Intensity / Length". Each title is prefixed with a tab and the
	 * line is terminated by println.
	 */
	public void printImagePunctaData()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		String s = "Puncta Density, Puncta Intensity, Puncta Area, Puncta Intensity / Length";
		String[] titles = s.split(",");
		for(int k = 0; k < titles.length; k++)
		{
			pW.print('\t' + titles[k]);
		}
		pW.println();
	}
	
    
	/**
	 * Delegates to DataPrinter.printPunctaDataPerNeuron, which walks the
	 * group/color matrix and emits one row per enabled combination using
	 * filename as the leading column.
	 */
	private void printImagePunctaData(String filename)
    {
    	//filename, group, color, threshold
 		super.printPunctaDataPerNeuron(filename);
    }
		
		
}
