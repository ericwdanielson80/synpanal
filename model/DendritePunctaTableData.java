package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
	
import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
		
/**
 * Table model backing the per-dendrite puncta summary view and the
 * base class for DendriteSpineTableData. Each row represents one
 * non-null Dendrite in the current group, reporting its length,
 * puncta density, intensity and area statistics, and puncta count.
 * The class also forwards dendrite selection and ignore toggles
 * into the underlying Dendrite objects and supports
 * loadDendriteIgnoreList for round-tripping ignored state.
 */
public class DendritePunctaTableData implements tableData {
Group myGroup;
int myColor;
boolean[] columnDisplay;
functionListener fL;
Font myFont;;
		
DecimalFormat dF = new DecimalFormat("#######.##");
Float dL = new Float(0.0);
Integer dN = new Integer(0);
	
	/**
	 * Stores the functionListener FL, the Group and Color, and
	 * initialises columnDisplay to an all-true mask sized to the
	 * number of titles so every column is visible by default.
	 */
	public DendritePunctaTableData(functionListener FL, Group Group, int Color)
	{
		fL = FL;
		myGroup = Group;
		myColor = Color;
		columnDisplay = new boolean[this.getTitles().length];
		for(int k = 0; k < columnDisplay.length; k++)
			columnDisplay[k] = true;
	}
		
	/**
	 * Returns the formatted cell text for one row/column of the
	 * dendrite puncta summary. Column 0 is the dendrite index;
	 * column 1 the length; column 2 the puncta density;
	 * column 3 the average puncta intensity; column 4 the average
	 * puncta area; column 5 the total puncta integrated intensity
	 * per length; column 6 the dendrite integrated intensity;
	 * column 7 the dendrite mean intensity; column 8 the number of
	 * visible (non-ignored) puncta. Each value is pulled through
	 * the functionListener and formatted by dN or dF. Returns
	 * "Error" for any other column.
	 */
	public String getData(int row, int column) {
		// TODO Auto-generated method stub		
		/*
		 * Dendrite Number | Dendrite Length | Puncta Density | Puncta Intenisty | Puncta Area | Puncta / Length | Total Intensity | Ave Intensity 
		 * 
		 * 
		 * 
		 */		
		switch(column)
	       {
	       case 0: return dN.toString(row);
	       case 1: return dF.format(fL.getDendrite(myGroup.getValue(),row).getLength(fL.getCalibration()));
	       case 2: return dF.format(((float)(fL.getDendrite(myGroup.getValue(),row).getPunctaNumber(myColor,fL.getCalibration()))));
	       case 3: return dN.toString(fL.getDendrite(myGroup.getValue(),row).getAvePunctaIntensity(myColor));
	       case 4: return dF.format((float)fL.getDendrite(myGroup.getValue(),row).getAvePunctaArea(myColor,fL.getCalibration()));     
	       case 5: return dF.format((float)fL.getDendrite(myGroup.getValue(),row).getTotalPunctaIntegratedIntensityPerLength(myColor,fL.getCalibration()));
	       case 6: return dN.toString(fL.getDendrite(myGroup.getValue(),row).getDendriteIntensity(myColor));
	       case 7: return dF.format((float)fL.getDendrite(myGroup.getValue(),row).getDendriteAveIntensity(myColor,fL.getCalibration()));
	       case 8: return dN.toString(fL.getDendrite(myGroup.getValue(),row).getVisiblePunctaNumber(myColor));
	       }
	       return "Error";
		
	}
		   
	/**
	 * Returns the ignored-flag state for each non-null dendrite in
	 * group-row order. out is sized to getRows() and counter tracks
	 * the next write index as null dendrites are skipped.
	 */
	public boolean[] getIgnoredArray() {
		int num = getRows();
		boolean[] out = new boolean[num];
		Dendrite[] myDendrites = fL.getDendrites(myGroup.getValue());
		int counter = 0;		
		for(int k = 0; k < myDendrites.length; k++)
		{
			if(myDendrites[k]!= null)
			{
				out[counter] = myDendrites[k].isIgnored();
				counter++;
			}
		}
		return out;
	}
			
	/**
	 * Returns the number of non-null Dendrite entries in the
	 * current group. counter counts non-null myDendrites slots;
	 * returns 0 when no dendrite array exists.
	 */
	public int getRows() {
		// TODO Auto-generated method stub
		int counter = 0;
		Dendrite[] myDendrites = fL.getDendrites(myGroup.getValue());
		
		if(myDendrites == null)
			return 0;
		
		   for(int k = 0; k < myDendrites.length; k++)
		   {			  
			   if(myDendrites[k] != null)				  
				   counter++;
		   }
		   
	       return counter;
	}
	
	/** Returns whether the dendrite in the given row is currently ignored. */
	public boolean isIgnored(int row) {
		// TODO Auto-generated method stub
		return fL.getDendrite(myGroup.getValue(), row).isIgnored();
	}
	
	/** Returns whether the dendrite in the given row is currently selected. */
	public boolean isSelected(int row) {
		// TODO Auto-generated method stub
		return fL.getDendrite(myGroup.getValue(), row).isSelected();
	}
	
	/** Toggles the ignored flag on the dendrite in row. */
	public void pushIgnored(int row) {
		// TODO Auto-generated method stub
		fL.getDendrite(myGroup.getValue(), row).bC.pushIgnored();
	
	}
	
	/**
	 * Toggles the selected flag on the dendrite in row. When the
	 * toggle leaves the dendrite selected, focusDendrite is also
	 * invoked so the UI scrolls/pans to highlight it.
	 */
	public void pushSelected(int row) {
		// TODO Auto-generated method stub
		fL.getDendrite(myGroup.getValue(), row).bC.pushSelected();
	
		if(fL.getDendrite(myGroup.getValue(), row).bC.isSelected)
        	fL.focusDendrite(row);
	}
	
	/** Clears the ignored flag on every dendrite in the current group. */
	public void resetIgnored() {
		// TODO Auto-generated method stub
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		for(int k = 0; k < d.length; k++)
		{
			if(d != null)
				d[k].bC.isIgnored = false;
		}
	}
	
	/** Clears the selected flag on every dendrite in the current group. */
	public void resetSelected() {
		// TODO Auto-generated method stub
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		for(int k = 0; k < d.length; k++)
		{
			if(d != null)
				d[k].bC.isSelected = false;
		}
	
	}
	
	/** Returns the column header titles, parsed from a single comma-separated string. */
	public String[] getTitles()
	{
		String out = "Dendrite,Length,Puncta Density,Ave Puncta Intensity,Ave Puncta Area,Total Puncta Intensity Per Length,Dendrite Intensity,Dendrite Ave Intensity,Puncta Number";
		return out.split(",");
	}
	
	/** Replaces the per-column display mask with b. */
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	/** Stores the rendering font f for table cells. */
	public void setFont(Font f)
	{
		myFont = f;
	}
	
	/** Returns the pixel widths used when laying out each column. */
	public int[] getLayout()
	  {	
		return new int[]{60,60,100,100,100,170,130,130,60,15};
	  }
	
	/**
	 * Applies a previously saved dendrite ignore mask to the current
	 * dendrites. counter tracks the position within list as null
	 * dendrite slots are skipped so the mask aligns with the visible
	 * rows.
	 */
	public void loadDendriteIgnoreList(boolean[] list)
	{
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		if(d == null)
			return;
		int counter = 0;
		for(int k = 0; k < d.length; k++)
		{
			if(d[k]!= null)
			{
				d[k].bC.isIgnored = list[counter];
				counter++;
			}
		}
	}
	
	/** Returns the per-column print-inclusion mask. */
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	
	

}
