package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Stub/placeholder printer intended to emit per-image averaged measurements to the
 * application's log output. The class is currently an empty shell: the entire
 * printing implementation (header row, per-metric helper methods for region,
 * dendrite, puncta, and spine averages) is preserved only as a commented-out
 * block that documents the intended output format. No methods are active, so
 * the class produces no output when instantiated; it is retained as a scaffold
 * for a future image-level summary printer that would sit alongside the other
 * DataPrinter subclasses. The two imports (PrintWriter and DecimalFormat) are
 * leftovers from the commented-out implementation and are not used.
 */
public class ImageDataPrinter {//extends DataPrinter{
//prints the data averaged for the image
/*
 * Prints:
 * Region Integrated Intensity
 * Region Ave Intensity
 * 
 * Ave Dendrite Integrated Intensity
 * Ave Dendrite Ave Intensity
 * Ave Dendrite Integrated Puncta Intensity
 * Ave Dendrite Length
 * 
 * Ave Puncta Density
 * Ave Puncta Intensity
 * Ave Puncta Area
 * 
 * Ave Spine Density
 * Ave Spine Head Width
 * Ave Spine Neck Width
 * Ave Spine Length
 * Mushroom Spine Density, Thin Spine Density, Stubby Spine Density, Filopodia Density
 * Number of Spines with 0,1,2,3++ Puncta (Density) 
 */
	
	
	/*public void printHeader()
	{
		String Header = "Filename" + '\t' + "Group" + '\t' + "Color" + '\t' + "Threshold";
		String Data = "";
		for(int k = 0; k < lI.outputData.length; k++)
		{
			if(lI.outputData[k])
				Data = Data + '\t' + lI.dataLabels[k];
		}
		pW.println(Header + Data);
	}
	
	private void printPunctaDensity(int groupMember,int color)
	{	
		//pW.print('\t' + dF.format(neuronData.getAvePunctaNum()));
	}
	
	private void printPunctaIntensity(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveIntensity()));
	}
	
	private void printPunctaArea(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveArea()));
	}
	
	private void printSpineDensity(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveSpineNum()));
	}
	
	private void printSpineHeadWidth(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveSpineWidth()));
	}
	
	private void printSpineNeckWidth(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveSpineNeckWidth()));
	}
	
	private void printSpineLength(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveSpineLength()));
	}
	
	private void printSpineType(int groupMember,int color)
	{
		
	}
	
	private void printPunctaPerSpine(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAvePunctaNum() / neuronData.getAveSpineNum()));
	}
	
	private void printSpineTypeDensity(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getSpineTypeNum(0)));
		//pW.print("\t" + dF.format(neuronData.getSpineTypeNum(1)));
		//pW.print("\t" + dF.format(neuronData.getSpineTypeNum(2)));
		//pW.print("\t" + dF.format(neuronData.getSpineTypeNum(3)));		
	}
	
	private void printSpinesWithPuncta(int groupMember,int color)
	{
		
	}
	
	private void printRegionIntegrated(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(fL.getAveCellIntensity(color, groupMember)));
	}
	
	private void printRegionAveIntensity(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(fL.getAveCellAveIntensity(color, groupMember)));
	}
	
		
	private void printDendriteLength(int groupMember,int color)
	{
		
	}
	
	private void printDendriteIntegrated(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveDendriteIntensity()));
	}
	
	private void printDendriteAveIntensity(int groupMember,int color)
	{
		//pW.print("\t" + dF.format(neuronData.getAveDendriteAveIntensity()));
	}
	
	private void printDendritePunctaIntegrated(int groupMember,int color)
	{
		
	}
	
	*/
}
