package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Font;
import java.text.DecimalFormat;
	
/**
 * Table data model backing the cell-body (soma) data view. Each row
 * represents one CellBody assigned to the current analysis group, and the
 * columns expose the integrated and average intensities for the current
 * color channel along with the thresholded area. The class listens for
 * cell-update events so it can refresh its internal index of which
 * CellBody objects currently belong to the owning group.
 */
public class CellTableData implements tableData, CellEventListener {
	Group myGroup;
	int myColor;
	boolean[] columnDisplay;
	Font myFont;
		
	Double pA = new Double(0.0);
	Integer pI = new Integer(0);
	
	functionListener fL;
	DecimalFormat dF = new DecimalFormat("#######.##");
	int[] cellList;
	
	
	
		/**
		 * Stores the functionListener FL, the Group that identifies which
		 * analysis group this table shows, and the Color channel index.
		 * Initializes columnDisplay to an all-true array sized to the
		 * number of titles so every column is visible by default.
		 */
		public CellTableData(functionListener FL, Group Group, int Color)
		{
			fL=FL;
			myGroup = Group;
			myColor = Color;
			columnDisplay = new boolean[this.getTitles().length];
			for(int k = 0; k < columnDisplay.length; k++)
				columnDisplay[k] = true;
		}
		
	/**
	 * Returns the formatted string for the requested cell in the table.
	 * If the cellList index has not been built yet, fireCellUpdateEvent
	 * rebuilds it from the current CellBody[] filtered by group. The local
	 * CellBody c is looked up via cellList[row]. Column 0 returns the row
	 * number, column 1 the integer integrated intensity for myColor,
	 * column 2 the formatted average intensity, and column 3 the
	 * thresholded area scaled by calibration squared. The parameters row
	 * and column specify the requested cell.
	 */
	public String getData(int row, int column) {
		// TODO Auto-generated method stub
		/*
		 * Integrated Intensity| Ave Intensity
		 */
		if(cellList == null)
			fireCellUpdateEvent();
		CellBody c = fL.getCells()[cellList[row]]; 
		switch(column)
		{
		case 0: return pI.toString(row);
		case 1: return pI.toString(c.cellIntensity[myColor]);
		case 2: return dF.format(c.getAveCellIntensity(myColor,fL.getCalibration()));
		case 3: return dF.format(c.cellIntensity[3] * fL.getCalibration() * fL.getCalibration());
		}
		return "error";
	}
	
	/** Cell-body rows do not support ignoring; always returns null. */
	public boolean[] getIgnoredArray() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns the number of cell bodies assigned to this group, queried
	 * from the functionListener.
	 */
	public int getRows() {
		// TODO Auto-generated method stub
		//CellBody[] c = fL.get		
		return fL.getCellNumber(myGroup.getValue());		
	}
	
	/** Cell rows are never marked ignored; always returns false. */
	public boolean isIgnored(int row) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/** Cell rows are never marked selected; always returns false. */
	public boolean isSelected(int row) {
		// TODO Auto-generated method stub
		return false;
	}

	/** No-op: ignoring is not supported for cell-body rows. */
	public void pushIgnored(int row) {
		// TODO Auto-generated method stub

	}

	/** No-op: selection is not supported for cell-body rows. */
	public void pushSelected(int row) {
		// TODO Auto-generated method stub

	}

	/** No-op: there is no ignore state to reset. */
	public void resetIgnored() {
		// TODO Auto-generated method stub

	}

	/** No-op: there is no selection state to reset. */
	public void resetSelected() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Returns the column header strings, parsed from a single
	 * comma-separated list.
	 */
	public String[] getTitles()
	{
		String out = "Region Number,Integrated Intensity,Ave Intensity,Thresholded Area";
		return out.split(",");
	}
	
	/**
	 * Replaces the per-column visibility mask with the provided boolean
	 * array b.
	 */
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	/** Returns the per-column pixel widths used by the table renderer. */
	public int[] getLayout()
	{
		return new int[] {60,60,60,60,15};
	}
	
	/** Stores the font to be used when rendering table cells. */
	public void setFont(Font f)
	{
		myFont = f;
	}
	
	/**
	 * Rebuilds the internal cellList index by scanning the full CellBody
	 * array returned from the functionListener and collecting those whose
	 * groupMember matches myGroup. cellNum is the expected count queried
	 * from the functionListener, counter is the next write position in
	 * cellList, and cellList itself maps table rows back to indices in
	 * the underlying CellBody array.
	 */
	public void fireCellUpdateEvent()
	{
		CellBody[] c = fL.getCells();
		if(c == null)
			{
			cellList = null;
			return;
			}
		int cellNum = fL.getCellNumber(myGroup.getValue());
		cellList = new int[cellNum];
		int counter = 0;
		for(int k = 0; k < c.length; k++)
		{
			if(c[k] != null && c[k].groupMember == myGroup)
			{
				cellList[counter] = k;
				counter++;
			}
		}
	}
	
	/**
	 * Returns the per-column visibility mask indicating which columns
	 * should be included when printing.
	 */
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	/** Returns the numeric value of the group this table belongs to. */
	public int getGroup()
	{
		return myGroup.getValue();
	}

}
