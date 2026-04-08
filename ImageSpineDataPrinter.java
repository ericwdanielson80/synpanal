package neuron_analyzer;

import java.io.*;

public class ImageSpineDataPrinter extends DataPrinter{
//Prints Data for each dendrite in each image
//Can print Integrated Intensity, Ave Intensity, Total Puncta Intensity, Ave Puncta Intensity, Ave Puncta Area, Puncta Density,
//Spine Density, Ave Spine Head Width, Ave Spine Neck Width, Ave Spine Length, Number or Spines with 0,1,2,3++ Puncta.
//Also Dendrite Lengths : In the Future Dendrite Types

	
/*Will control the data output to three files: Dendrite puncta data & Dendrite spine data
 * will print the spine data as it appears in the data panel, Red first, Green next Blue last
 */

	public ImageSpineDataPrinter(String baseDirectory,LogInfo l, functionListener FL, DataPrinterManager d)
	{	
		super(baseDirectory + '/' + "Spine_Data_Per_Image.txt",l,FL,d);
		printImageSpineData();
	}
	
	public void printData(String filename)
	{ 			
		printImageSpineData(filename);
	}
	
	
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
	
    
	private void printImageSpineData(String filename)
    {
    	//filename, group, color, threshold
 		super.printSpineDataPerNeuron(filename);
    }
		
		
}
