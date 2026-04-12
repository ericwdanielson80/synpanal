package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Font;
import java.text.DecimalFormat;
		
/**
 * Table data backing the per-dendrite spine summary table. Extends
 * DendritePunctaTableData so it reuses its group/color bookkeeping but
 * overrides the column set and per-cell value lookup to report
 * spine-density, spine morphology (head/neck widths, length), spine-type
 * breakdown (mushroom, thin, stubby, filopodia) and puncta-per-spine
 * counts for the dendrites in the current group and color channel.
 */
public class DendriteSpineTableData extends DendritePunctaTableData {
		
	/**
	 * Constructs the spine table data, forwarding the functionListener FL,
	 * the Group (analysis condition) and the Color channel to the base
	 * DendritePunctaTableData constructor for storage.
	 */
	public DendriteSpineTableData(functionListener FL, Group Group, int Color)
	{
		super(FL,Group,Color);
	}
		
	/**
	 * Returns the displayed text for a given cell in the spine summary
	 * table. Column 0 is the dendrite index, column 1 the dendrite length,
	 * column 2 the spine density, columns 3-5 mean spine head width, neck
	 * width and length, columns 6-9 the counts of the four spine types
	 * (mushroom, thin, stubby, filopodia), and columns 10-13 the
	 * puncta-per-spine buckets (0, 1, 2, 3+). All values are looked up
	 * through fL.getDendrite for the current group and row. The row
	 * parameter is the dendrite index and column is the table column.
	 */
	public String getData(int row, int column) {
		// TODO Auto-generated method stub
		/*
		 * Dendrite Number | Dendrite Length | Spine Density |Ave Head Width| Ave Neck Width| Ave Length| Mushroom Density| Thin Density|Stubby Density|Filopodia Density|0 Puncta|1 Puncta|2 Puncta|3>=Puncta
		 * 
		 * 
		 */
		switch(column)
	       {
	       case 0: return dN.toString(row);
	       case 1: return dF.format(fL.getDendrite(myGroup.getValue(),row).getLength(fL.getCalibration()));	       
	       case 2: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineNum(fL.getCalibration()));
	       case 3: return dF.format(fL.getDendrite(myGroup.getValue(),row).getAveSpineWidth(fL.getCalibration()));     
	       case 4: return dF.format(fL.getDendrite(myGroup.getValue(),row).getAveSpineNeckWidth(fL.getCalibration())); 
	       case 5: return dF.format(fL.getDendrite(myGroup.getValue(),row).getAveSpineLength(fL.getCalibration()));
	       case 6: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(0,fL.getCalibration()));
	       case 7: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(1,fL.getCalibration()));
	       case 8: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(2,fL.getCalibration()));
	       case 9: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(3,fL.getCalibration()));
	       case 10: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(0,myColor,fL.getCalibration()));
	       case 11: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(1,myColor,fL.getCalibration()));
	       case 12: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(2,myColor,fL.getCalibration()));
	       case 13: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(3,myColor,fL.getCalibration()));
	       }
	       return "Error";
		
	}
	
	
	/**
	 * Returns the human-readable column titles for the spine summary
	 * table, split from a single comma-separated source string.
	 */
	public String[] getTitles()
	{
		String out = "Dendrite,Length,Spine Density,Ave Head Width,Ave Neck Width,Ave Length,Mushroom Density, Thin Density, Stubby Density, Filopodia Density, 0 Puncta,1 Puncta,2 Puncta,3>=Puncta";
		return out.split(",");
	}
	
	
	/**
	 * Returns the pixel width layout used to size each column of the
	 * table, with the trailing small value acting as a margin/sentinel.
	 */
	public int[] getLayout()
	  {		  
		  return new int[]{60,50,95,84,60,60,60,60,60,60,60,60,60,60,60,60,15};
	  }
	
	
	
}
	