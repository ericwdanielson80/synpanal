package neuron_analyzer;

import java.io.*;

public class ImageCellDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	public ImageCellDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{	
		super(baseDirectory + '/' + "Generic_Data_Per_Image.txt",l,FL,d);
		printImageCellData();
	}
	
	public void printData(String filename)
	{ 			
		printImageCellData(filename);
	}
	
	
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
	
    
	private void printImageCellData(String filename)
    {
    	//filename, group, color, threshold
 		super.printCellDataPerNeuron(filename);
    }
		
		
}
