package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Table model for the "B" side of a colocalization analysis. Rows
 * summarise the total signal in channel B, the B signal intersecting
 * with A, and the B-minus-A difference; each row is shown in four
 * forms (integrated value, percent of total, area, area percent) and
 * two averages (per-pixel average and average as percent of the
 * overall B average). The numeric values are sourced from a
 * per-channel ColocalizationInfo array supplied by the superclass.
 */
public class ColocalizationBTableData extends ColocalizationATableData implements tableData, ThresholdEventListener {

	
	/**
	 * Constructor that defers to ColocalizationATableData for its
	 * functionListener FL, Group and Color storage. No additional
	 * state is initialised here.
	 */
	public ColocalizationBTableData(functionListener FL, Group Group, int Color)
	{
		super(FL,Group,Color);

	}
	
	/**
	 * Returns the formatted cell string for the colocalization B
	 * table. When myInfo is unavailable for the current channel the
	 * cell is reported as "NA". Column 0 returns the row label,
	 * columns 1-6 return the integrated, integrated percent, area,
	 * area percent, average, and average-percent values respectively
	 * by delegating to small private helpers that read the
	 * appropriate ColocalizationInfo fields for row (0 total B,
	 * 1 B intersect A, 2 B minus A).
	 */
	public String getData(int row, int column) {
		// TODO Auto-generated method stub
		/*
		 * Dendrite Number | Dendrite Length | Puncta Density | Puncta Intenisty | Puncta Area | Puncta / Length | Total Intensity | Ave Intensity 
		 * 
		 * 
		 * 
		 */		
		if(column > 0 && myInfo == null)
			return "NA";
		if(column > 0 && myInfo[myColor] == null)
			return "NA";
		switch(column)
	       {
	       case 0: return getColumnLabel(row);	
	       case 1: return getIntegrated(row);//"Integrated"; 
	       case 2: return getIntegratedPercent(row);//"Integrated %";
	       case 3: return getArea(row);//"Area";
	       case 4: return getAreaPercent(row);//"Area %";
	       case 5: return getAverage(row);// "Average";     
	       case 6: return getAveragePercent(row);//"Average %";	       
	       }
	       return "Error";
		
	}
	
	
	/**
	 * Returns the text label for one of the three rows: "Total B"
	 * for row 0, the set-builder intersection of B and A for row 1,
	 * and the set-builder difference (B minus A) for row 2.
	 */
	private String getColumnLabel(int row)
	{
		switch(row)
		{
		case 0: return "Total " + B;
		case 1: return "{" + B + "}" + "\u2229" + "{" + A + "}";
		case 2: return "{" + B + "}" + "\\" + "{" + A + "}";		
		}
		return "error";
	}
	/**
	 * Returns the integrated intensity per row: total integratedB
	 * (0), integratedBwithA (1), or the difference (2) of those.
	 */
	private String getIntegrated(int row)
	{
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedB);
		case 1: return dF.format(myInfo[myColor].integratedBwithA);
		case 2: return dF.format(myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA);		
		}
		return "error";
	}
	/**
	 * Returns each row's integrated intensity expressed as a percent
	 * of the total integratedB value. Row 0 always reports 100,
	 * row 1 integratedBwithA/integratedB, row 2 the complementary
	 * fraction.
	 */
	private String getIntegratedPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].integratedB / myInfo[myColor].integratedB) * 100 );
		case 1: return dF.format((myInfo[myColor].integratedBwithA/ myInfo[myColor].integratedB) * 100 );
		case 2: return dF.format(((myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA)/ myInfo[myColor].integratedB) * 100 );	
		}
		return "error";
	}
	/**
	 * Returns the pixel area per row: totalPixelsB (0),
	 * totalPixelsAB (1), or their difference (2).
	 */
	private String getArea(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].totalPixelsB);
		case 1: return dF.format(myInfo[myColor].totalPixelsAB);
		case 2: return dF.format(myInfo[myColor].totalPixelsB - myInfo[myColor].totalPixelsAB);		
		}
		return "error";
	}
	/**
	 * Returns each area value as a percent of totalPixelsB, with
	 * row 0 always 100, row 1 the fraction of B pixels that also
	 * intersect A, and row 2 the remaining B-only fraction.
	 */
	private String getAreaPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].totalPixelsB / myInfo[myColor].totalPixelsB) * 100 );
		case 1: return dF.format((myInfo[myColor].totalPixelsAB/ myInfo[myColor].totalPixelsB) * 100 );
		case 2: return dF.format(((myInfo[myColor].totalPixelsB - myInfo[myColor].totalPixelsAB)/ myInfo[myColor].totalPixelsB) * 100 );	
		}
		return "error";
	}
	/**
	 * Returns the per-pixel average intensity for each row by
	 * dividing the corresponding integrated intensity by the
	 * matching area.
	 */
	private String getAverage(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB);
		case 1: return dF.format(myInfo[myColor].integratedBwithA / myInfo[myColor].totalPixelsAB);
		case 2: return dF.format((myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA)/ (myInfo[myColor].totalPixelsB -myInfo[myColor].totalPixelsAB));		
		}
		return "error";
		}
	
	/**
	 * Returns each row's per-pixel average expressed as a percent
	 * of the overall B average (integratedB/totalPixelsB). Row 0
	 * always yields 100; rows 1 and 2 scale the intersection and
	 * complementary averages accordingly.
	 */
	private String getAveragePercent(int row){
		switch(row)
		{
		case 0: return dF.format(((myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB) / (myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB)) * 100 );
		case 1: return dF.format(((myInfo[myColor].integratedBwithA / myInfo[myColor].totalPixelsAB) / (myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB)) * 100 );
		case 2: return dF.format((((myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA)/ (myInfo[myColor].totalPixelsB -myInfo[myColor].totalPixelsAB)) / ( myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB)) * 100);		
		}
		return "error";
		}
	
		

}
