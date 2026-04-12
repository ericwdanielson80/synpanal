package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;

/**
 * DataPrinter that emits one row per analyzed image into
 * "Generic_Data_Per_Image.txt" in the chosen output directory. The row
 * contains image-wide "generic" measurements (integrated intensity and
 * average intensity) grouped by cell-type tab, color channel and threshold.
 * A header line is written once when the printer is constructed and
 * subsequent printData calls append a data row for each image.
 */
public class ImageCellDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	/**
	 * Opens the output file and writes the header row. The baseDirectory
	 * parameter is the folder into which the log file will be created; l is
	 * the LogInfo describing which color channels are currently enabled for
	 * printing; FL is the controller used to read thresholds; d is the
	 * DataPrinterManager that supplies table data on demand. The superclass
	 * constructor concatenates the fixed filename "Generic_Data_Per_Image.txt"
	 * onto baseDirectory and opens the PrintWriter pW, after which
	 * printImageCellData is invoked to emit the header.
	 */
	public ImageCellDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{
		super(baseDirectory + '/' + "Generic_Data_Per_Image.txt",l,FL,d);
		printImageCellData();
	}
	
	/**
	 * Appends a data block for the image named filename by delegating to the
	 * private printImageCellData helper. The filename parameter is the image
	 * being recorded.
	 */
	public void printData(String filename)
	{
		printImageCellData(filename);
	}


	/**
	 * Writes the header row once, containing "Filename", "Group", "Color" and
	 * "Threshold" followed by the splittable title string "Integrated
	 * Intensity, Average Intensity", split on commas into the titles array
	 * and emitted tab-separated, then terminated by a newline.
	 */
	public void printImageCellData()
	{
		//prints the column names once at the beginning of the data output
		//Filename	Group	Color	Threshold 
		pW.print("Filename"+'\t'+"Group"+'\t'+"Color"+'\t'+"Threshold");
		String s = "Integrated Intensity, Average Intensity";
		String[] titles = s.split(",");
		for(int k = 0; k < titles.length; k++)
		{
			pW.print('\t' + titles[k]);
		}
		pW.println();
	}
	
    
	/**
	 * Prints one data row per group/color by delegating to
	 * DataPrinter.printCellDataPerNeuron, which handles iteration across
	 * groups and enabled channels and writes the filename, group, color and
	 * threshold leader columns in addition to the measurement values. The
	 * filename parameter is embedded into the leader.
	 */
	private void printImageCellData(String filename)
    {
    	//filename, group, color, threshold
 		super.printCellDataPerNeuron(filename);
    }
		
		
}
