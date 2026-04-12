package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Font;
import java.text.DecimalFormat;
/**
 * Table model backing the individual-spine view: every row corresponds
 * to one SpineInfo on one Dendrite inside the current Group, reporting
 * its head width, neck width, length, morphological type, and the
 * number of puncta associated with the spine in the chosen channel.
 * The class listens for spine-update and count events and rebuilds
 * its internal spineList/spineNames index on demand so row lookups
 * stay cheap.
 */
public class SpineTableData implements tableData, SpineEventListener {
Group myGroup;
int myColor;
boolean[] columnDisplay;
Font myFont;
	
Double pA = new Double(0.0);
Integer pI = new Integer(0);
	
SpineInfo[] spineList;
String[] spineNames;
	
functionListener fL;
DecimalFormat dF = new DecimalFormat("#######.##");
	
	
	/**
	 * Stores the shared functionListener FL, the Group of the owning
	 * tab, and the target Color channel, then initialises
	 * columnDisplay to an all-true array sized to the title count.
	 */
	public SpineTableData(functionListener FL, Group Group, int Color)
	{		
		fL=FL;
		myGroup = Group;
		myColor = Color;
		columnDisplay = new boolean[this.getTitles().length];
		for(int k = 0; k < columnDisplay.length; k++)
			columnDisplay[k] = true;
	}
	
	/**
	 * Returns the formatted string for a cell in the spine table. If
	 * the internal spine list has not been built it is rebuilt via
	 * makeSpineList. Column 0 returns the spine name (dendrite.spine),
	 * columns 1-3 return the spine's head width, neck width and
	 * length via the calibration-aware accessors, column 4 the spine
	 * type string, and column 5 the puncta-per-spine count for
	 * myColor. The parameters are the table row and column.
	 */
	public String getData(int row, int column) {
		// TODO Auto-generated method stub
		/*
		 * SpineName | Spine Head Width | Spine Neck Width | Spine Length | Spine Type | Puncta per Spine
		 */
		if(spineNames == null)
			makeSpineList();
		switch(column)
		{
		case 0: return spineNames[row];
		case 1: return dF.format(spineList[row].getWidth(fL.getCalibration()));
		case 2: return dF.format(spineList[row].getNeckWidth(fL.getCalibration()));
		case 3: return dF.format(spineList[row].getLength(fL.getCalibration()));
		case 4: return spineList[row].getSpineType(fL.getCalibration());
		case 5: return pI.toString(spineList[row].getPunctaNum(myColor));
		}
		return "Error";
	}
	
	
	/** Spine rows do not participate in the ignore mechanism; always null. */
	public boolean[] getIgnoredArray() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns the total number of spines across every non-null
	 * Dendrite in the current group. counter accumulates
	 * d[k].spineNumber from each dendrite.
	 */
	public int getRows() {
		// TODO Auto-generated method stub
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		if(d == null)
			return 0;
		int counter = 0;
		for(int k = 0; k < d.length; k++)
		{
			if(d[k] != null)
			{
				counter+= d[k].spineNumber;
			}
		}
		return counter;
	}
	
	/** Spines are never marked ignored; returns false. */
	public boolean isIgnored(int row) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/** Spines are never marked selected in this model; returns false. */
	public boolean isSelected(int row) {
		// TODO Auto-generated method stub
		return false;
	}
		
	/** No-op: spine rows cannot be ignored. */
	public void pushIgnored(int row) {
		// TODO Auto-generated method stub
	
	}
	
	/** No-op: spine rows cannot be selected through this model. */
	public void pushSelected(int row) {
		// TODO Auto-generated method stub

	}

	/** No-op: no ignored state to reset. */
	public void resetIgnored() {
		// TODO Auto-generated method stub

	}

	/** No-op: no selection state to reset. */
	public void resetSelected() {
		// TODO Auto-generated method stub

	}
	
	/** Returns the column header names for the spine table. */
	public String[] getTitles()
	{
		String out = "Spine Name,Head Width,Neck Width,Length,Spine Type,Puncta per Spine";
		return out.split(",");
	}
	
	/** Replaces the per-column visibility mask with b. */
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	/** Returns the per-column pixel widths used by the renderer. */
	public int[] getLayout()
	  {
		  return new int[]{60,60,60,95,84,60,15};
	  }
	
	/** Stores the font to be used by the table renderer. */
	public void setFont(Font f)
	{
		myFont = f;
	}
	
	/** SpineEventListener callback triggering a rebuild of the spine list. */
	public void fireSpineUpdateEvent()
	{
		makeSpineList();
	}
	
	/** Count-event callback triggering a rebuild of the spine list. */
	public void countEventFired()
	{
		makeSpineList();
	}
	
	/**
	 * Rebuilds the per-row spineNames and spineList arrays by scanning
	 * the Dendrite[] for this group. First pass counts the total
	 * number of spines into counter so the arrays can be allocated;
	 * second pass walks each dendrite (using local dendrite as the
	 * display index that skips nulls) and each SpineInfo entry j,
	 * storing a "dendrite.j" string in spineNames[counter] and the
	 * SpineInfo itself in spineList[counter].
	 */
	private void makeSpineList()
	{
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		if(d == null)
			return;
		int counter = 0;
		for(int k = 0; k < d.length; k++)
		{
			if(d[k] != null)
			{
				counter+= d[k].spineNumber;
			}
		}
		
		spineNames = new String[counter];
		spineList = new SpineInfo[counter];
		counter = 0;
		int dendrite = 0;
		for(int k = 0; k < d.length; k++)
		{
			if(d[k] != null)
			{
				SpineInfo[] s = d[k].spineData;
				if(d[k].spineNumber > 0)
				for(int j = 0; j < d[k].spineNumber; j++)
				{
					spineNames[counter] = pI.toString(dendrite) + "." + pI.toString(j);
					spineList[counter] = s[j];
					counter++;
				}
				dendrite++;
			}
		}
	}
	
	/** Returns the per-column print-inclusion mask. */
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	/** Returns the numeric index of the group this table belongs to. */
	public int getGroup()
	{
		return myGroup.getValue();
	}

}
