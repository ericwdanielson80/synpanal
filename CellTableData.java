package neuron_analyzer;
import java.awt.Font;
import java.text.DecimalFormat;

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
	


		public CellTableData(functionListener FL, Group Group, int Color)
		{
			fL=FL;
			myGroup = Group;
			myColor = Color;
			columnDisplay = new boolean[this.getTitles().length];
			for(int k = 0; k < columnDisplay.length; k++)
				columnDisplay[k] = true;
		}
		
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

	public boolean[] getIgnoredArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRows() {
		// TODO Auto-generated method stub
		//CellBody[] c = fL.get		
		return fL.getCellNumber(myGroup.getValue());		
	}

	public boolean isIgnored(int row) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSelected(int row) {
		// TODO Auto-generated method stub
		return false;
	}

	public void pushIgnored(int row) {
		// TODO Auto-generated method stub

	}

	public void pushSelected(int row) {
		// TODO Auto-generated method stub

	}

	public void resetIgnored() {
		// TODO Auto-generated method stub

	}

	public void resetSelected() {
		// TODO Auto-generated method stub

	}
	
	public String[] getTitles()
	{
		String out = "Region Number,Integrated Intensity,Ave Intensity,Thresholded Area";
		return out.split(",");
	}
	
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	public int[] getLayout()
	{
		return new int[] {60,60,60,60,15};
	}
	
	public void setFont(Font f)
	{
		myFont = f;
	}
	
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
	
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	public int getGroup()
	{
		return myGroup.getValue();
	}

}
