package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Table data adapter that reports the colocalization statistics for one
 * color channel in a given Group; used by the data panes to render the
 * "Total", "intersection with other color", and "set-minus" rows for
 * integrated intensity, area, and average intensity. It also implements
 * ThresholdEventListener so that whenever the user changes a channel
 * threshold the underlying ColocalizationInfo is refetched from the
 * functionListener and the display updates accordingly.
 */
public class ColocalizationATableData implements tableData, ThresholdEventListener {
Group myGroup;
int myColor;
boolean[] columnDisplay;
functionListener fL;
Font myFont;;

DecimalFormat dF = new DecimalFormat("#######.##");
Float dL = new Float(0.0);
Integer dN = new Integer(0);

ColocalizationInfo[] myInfo;
String A;
String B;
	
	/**
	 * Constructs the table data, registering as a threshold listener on the
	 * supplied functionListener so threshold changes trigger a refresh. The
	 * parameter FL is the shared functionListener, Group is the neuron group
	 * the data belongs to, and Color is the reference color channel index
	 * (0=red, 1=green, 2=blue); the strings A and B are initialized to the
	 * names of this channel and the paired channel used in the set-operation
	 * labels. The columnDisplay flags are all initialized to true so every
	 * column is printable by default.
	 */
	public ColocalizationATableData(functionListener FL, Group Group, int Color)
	{
		fL = FL;
		fL.addThresholdEventListener(this);
		myGroup = Group;
		myColor = Color;
		columnDisplay = new boolean[this.getTitles().length];
		for(int k = 0; k < columnDisplay.length; k++)
			columnDisplay[k] = true;
		if(Color == 0)
			{
			A = "Red";
			B = "Green";
			}
		
		if(Color == 1)
			{
			A = "Green";
			B = "Blue";
			}
		
		if(Color == 2)
			{
			A = "Blue";
			B = "Red";
			}
	}

	/**
	 * Looks up the cell contents for the given row (0=total, 1=intersection,
	 * 2=set difference) and column (label plus three pairs of value/percent
	 * columns). If no ColocalizationInfo has been loaded yet it returns "NA"
	 * for value columns, and "Error" if the column index is out of range. The
	 * column switch dispatches to the various private getXxx helpers to format
	 * the numbers.
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

	/** Returns null because colocalization rows cannot be individually ignored. */
	public boolean[] getIgnoredArray() {
		
		return null;
	}

	/** Returns the fixed row count of three: total, intersection, and set minus. */
	public int getRows() {
		// TODO Auto-generated method stub
	       return 3;
	}

	/** Always returns false; ignore-flagging is not supported on this table. */
	public boolean isIgnored(int row) {
		// TODO Auto-generated method stub		
		return false;
	}

	/** Always returns false; selection is not supported on this table. */
	public boolean isSelected(int row) {
		// TODO Auto-generated method stub
		return false;
	}

	/** No-op; individual rows of a colocalization table cannot be ignored. */
	public void pushIgnored(int row) {
		// TODO Auto-generated method stub
		

	}

	/** No-op; individual rows of a colocalization table cannot be selected. */
	public void pushSelected(int row) {
		// TODO Auto-generated method stub
		
	}

	/** No-op; there is nothing to reset because rows cannot be ignored. */
	public void resetIgnored() {
		// TODO Auto-generated method stub
		
	}

	/** No-op; there is nothing to reset because rows cannot be selected. */
	public void resetSelected() {
		// TODO Auto-generated method stub
		

	}
	
	/**
	 * Builds the column titles for the colocalization table by splitting a
	 * comma-separated template. The returned array includes the leading
	 * "Colocalization Colors" label plus the six value/percent columns.
	 */
	public String[] getTitles()
	{
		String out = "Colocalization Colors ,Integrated,Integrated %,Area     ,Area %     ,Average     ,Average %     ";
		return out.split(",");
	}
	
	/** Stores the caller's per-column visibility mask so later printing honors it. */
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	/** Saves the supplied font for subsequent drawing operations. */
	public void setFont(Font f)
	{
		myFont = f;
	}
	
	/**
	 * Returns the fixed pixel widths of each column followed by the vertical
	 * row increment as the final element, matching the tableData contract.
	 */
	public int[] getLayout()
	  {	
		return new int[]{150,100,100,100,100,100,100,15};
	  }	
	
	/** Returns the per-column visibility mask used when exporting rows. */
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	/**
	 * Returns the label for the given row: total of color A, the A-B
	 * intersection built as "{A}\u2229{B}", or the set difference "{A}\\{B}".
	 */
	private String getColumnLabel(int row)
	{
		switch(row)
		{
		case 0: return "Total " + A;
		case 1: return "{" + A + "}" + "\u2229" + "{" + B + "}";
		case 2: return "{" + A + "}" + "\\" + "{" + B + "}";		
		}
		return "error";
	}
	/** Formats the integrated-intensity value for the requested row using the shared DecimalFormat. */
	private String getIntegrated(int row)
	{
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedA);
		case 1: return dF.format(myInfo[myColor].integratedAwithB);
		case 2: return dF.format(myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB);		
		}
		return "error";
	}
	/** Formats the integrated intensity as a percentage of the total for this color. */
	private String getIntegratedPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].integratedA / myInfo[myColor].integratedA) * 100 );
		case 1: return dF.format((myInfo[myColor].integratedAwithB/ myInfo[myColor].integratedA) * 100 );
		case 2: return dF.format(((myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB)/ myInfo[myColor].integratedA) * 100 );	
		}
		return "error";
	}
	/** Formats the raw pixel area count for the requested row. */
	private String getArea(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].totalPixelsA);
		case 1: return dF.format(myInfo[myColor].totalPixelsAB);
		case 2: return dF.format(myInfo[myColor].totalPixelsA - myInfo[myColor].totalPixelsAB);		
		}
		return "error";
	}
	/** Formats the area as a percentage of the total area for this color. */
	private String getAreaPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].totalPixelsA / myInfo[myColor].totalPixelsA) * 100 );
		case 1: return dF.format((myInfo[myColor].totalPixelsAB / myInfo[myColor].totalPixelsA) * 100 );
		case 2: return dF.format(((myInfo[myColor].totalPixelsA - myInfo[myColor].totalPixelsAB) / myInfo[myColor].totalPixelsA ) * 100 );	
		}
		return "error";
	}
	/** Formats the average intensity (integrated / area) for the requested row. */
	private String getAverage(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA);
		case 1: return dF.format(myInfo[myColor].integratedAwithB / myInfo[myColor].totalPixelsAB);
		case 2: return dF.format((myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB)/ (myInfo[myColor].totalPixelsA -myInfo[myColor].totalPixelsAB));		
		}
		return "error";
		}
	
	/** Formats the average intensity of the row as a percentage of the overall average. */
	private String getAveragePercent(int row){
		switch(row)
		{
		case 0: return dF.format(((myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA) / (myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA)) * 100 );
		case 1: return dF.format(((myInfo[myColor].integratedAwithB / myInfo[myColor].totalPixelsAB) / (myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA)) * 100 );
		case 2: return dF.format((((myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB)/ (myInfo[myColor].totalPixelsA -myInfo[myColor].totalPixelsAB)) / ( myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA)) * 100);		
		}
		return "error";
		}
	
	/**
	 * ThresholdEventListener callback invoked whenever the user changes a
	 * channel threshold; it refreshes myInfo with the latest colocalization
	 * data from the functionListener so subsequent getData calls reflect the
	 * new threshold values.
	 */
	public void fireTresholdUpdateEvent()
	{
		myInfo = fL.getColocalizationInfo();
	}
	
	
	
	

}
