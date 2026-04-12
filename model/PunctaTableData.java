package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
	
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.text.DecimalFormat;
		
import javax.swing.JPanel;
		
/**
 * Table model backing the individual-puncta view for one analysis
 * group and one color channel. Rows are the flattened list of puncta
 * across every Dendrite in the group, labelled with a
 * "dendrite.puncta" name and showing the puncta's intensity and
 * calibrated area. The class listens for count events and rebuilds
 * its internal arrays then, and supports selection/ignore toggles
 * that propagate into the underlying Puncta state.
 */
public class PunctaTableData implements tableData, CountEventListener {
functionListener fL;
Group myGroup;
int myColor;
Double pA = new Double(0.0);
Integer pI = new Integer(0);
JPanel[] Listeners;    
Font myFont;
		
String[] punctaNames;
String[] punctaIntensities;
int[] punctaAreas;
Puncta[] punctaList;
DecimalFormat dF = new DecimalFormat("#######.##");
	
boolean[] columnDisplay;
		
		
	/**
	 * Stores the functionListener FL, the Group, and the Color
	 * channel index, and initialises columnDisplay to an all-true
	 * mask sized to the number of titles.
	 */
	public PunctaTableData(functionListener FL, Group Group, int Color)
	{		
		fL=FL;
		myGroup = Group;
		myColor = Color;
		columnDisplay = new boolean[this.getTitles().length];
		for(int k = 0; k < columnDisplay.length; k++)
			columnDisplay[k] = true;
	}
	
	/**
	 * Returns the display string for a given cell. Column 0 is the
	 * cached puncta name, column 1 the cached intensity string, and
	 * column 2 the calibrated area (punctaAreas[row] * calibration
	 * squared) formatted via dF. Returns "Error" for other columns.
	 */
	public String getData(int row, int column) {
		// TODO Auto-generated method stub
	
		switch(column)
		{						
		case 0: return punctaNames[row];
        case 1: return punctaIntensities[row];
        case 2: return dF.format(punctaAreas[row] * fL.getCalibration() * fL.getCalibration());
        }
        return "Error";
		
		
	}
	
	/**
	 * Returns the current ignored-flag state for each puncta as a
	 * boolean array. When punctaList has not been built yet an
	 * empty array is returned; otherwise each entry is read from
	 * punctaList[k].isIgnored().
	 */
	public boolean[] getIgnoredArray() {
		if(punctaList == null)
		{
			return new boolean[0];
		}		
		int num = punctaList.length;
		boolean[] out = new boolean[num];
		for(int k = 0; k < punctaList.length; k++)
		{
			out[k] = punctaList[k].isIgnored();
		}		
		return out;
	}
	
	/** Returns the per-column pixel widths used when rendering the table. */
	public int[] getLayout() {
		// TODO Auto-generated method stub
		return new int[] {600,60,60,15};
	}
	
	/**
	 * Computes the total number of puncta across every non-null
	 * dendrite in this group and channel. counter accumulates each
	 * dendrite's getPunctaNumber(myColor). Returns 0 when there are
	 * no dendrites.
	 */
	public int getRows() 
	{
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		int counter = 0;
		if(d == null)
			return 0;
		for(int k = 0; k < d.length; k++)
		{
			if(d[k] != null)
			{				
				counter += d[k].getPunctaNumber(myColor);
			}
		}	
		return counter;
	}
	
	/** Returns the three column titles for the puncta table. */
	public String[] getTitles() {
		String s = "Puncta Name,Puncta Intensity,Puncta Area";
		return s.split(",");
	}
	
	/** Reports whether the puncta in the given row is currently ignored. */
	public boolean isIgnored(int row) {
		return punctaList[row].isIgnored();
	}
	
	/** Reports whether the puncta in the given row is currently selected. */
	public boolean isSelected(int row) {
		return punctaList[row].isSelected();
	}

	/** Toggles the ignored flag on the indicated puncta and asks the scroll pane to repaint. */
	public void pushIgnored(int row) {
		punctaList[row].pushIgnored();
		fL.repaintScrollPane();
	}

	/** Toggles the selected flag on the indicated puncta and requests a scroll-pane repaint. */
	public void pushSelected(int row) {
		punctaList[row].pushSelected();
		fL.repaintScrollPane();
	}

	/** Clears the ignored flag on every tracked puncta. */
	public void resetIgnored() {
		// TODO Auto-generated method stub
		for(int k = 0; k < punctaList.length; k++)
		{
			punctaList[k].bC.isIgnored = false;
		}

	}

	/** Clears the selected flag on every tracked puncta. */
	public void resetSelected() {
		// TODO Auto-generated method stub
		for(int k = 0; k < punctaList.length; k++)
		{
			punctaList[k].bC.isSelected = false;
		}

	}

	/** Intended to set per-column display mask; currently a no-op. */
	public void setColumnDisplay(boolean[] b) {
		// TODO Auto-generated method stub

	}

	/** Intended to set the rendering font; currently a no-op. */
	public void setFont(Font f) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * CountEventListener callback that rebuilds the per-row caches.
	 * First pass counts the total number of puncta across non-null
	 * dendrites into counter so punctaNames, punctaIntensities,
	 * punctaAreas and punctaList can be sized exactly; second pass
	 * walks every dendrite and each of its Puncta in the target
	 * channel, storing "dendrite.j" labels, intensity strings,
	 * raw areas, and the Puncta references. dendrite is the
	 * display index for dendrites (skipping null slots) and
	 * calibration is precomputed as calibration squared for later
	 * area formatting.
	 */
	public void countEventFired()
	{
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		int counter = 0;
		if(d == null)
			return;
		for(int k = 0; k < d.length; k++)
		{
			if(d[k] != null)
			{
				counter += d[k].getPunctaNumber(myColor);
			}
		}		
		punctaNames = new String[counter];
		punctaIntensities = new String[counter];
		punctaAreas = new int[counter];
		punctaList = new Puncta[counter];
		
		counter = 0;	
		int dendrite = 0;	
		Puncta[] p;
		double calibration = fL.getCalibration();
		calibration = calibration * calibration;
		for(int k = 0; k < d.length; k++)
		{						
			if(d[k] != null)
			{				
				p = d[k].myPuncta[myColor].myPuncta;
				for(int j = 0; j < p.length; j++)
				{					
					punctaNames[counter]=pI.toString(dendrite)+"."+pI.toString(j);
					punctaIntensities[counter]=pI.toString(p[j].intensity);
					punctaAreas[counter]=p[j].area;
					punctaList[counter] = p[j];					
					counter++;					
				}
				dendrite++;
			}
		}	
		
	}
	
	/**
	 * Restores ignored flags from a previously saved boolean array.
	 * When the list size does not match punctaList a diagnostic is
	 * printed and nothing is applied; otherwise each flag is copied
	 * into punctaList[k].bC.isIgnored.
	 */
	public void loadPunctaIgnoreList(boolean[] list)
	{
		if(punctaList == null)
			return;
		if(punctaList.length != list.length)
			{
			System.out.println("Puncta Ignore List size incorrect");
			System.out.println("punctalist: " + punctaList.length);
			System.out.println("list: " + list.length);
			}
		else
		{
			for(int k = 0; k < list.length; k++)
			{
				punctaList[k].bC.isIgnored = list[k];
			}
		}
	}
	
	/** Returns the per-column print-inclusion mask. */
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	/** Returns the numeric group id this table belongs to. */
	public int getGroup()
	{
		return myGroup.getValue();
	}
	
	
	
	

}
