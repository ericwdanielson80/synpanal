package neuron_analyzer;
import java.awt.Font;
import java.text.DecimalFormat;
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


	public SpineTableData(functionListener FL, Group Group, int Color)
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
	
	
	public boolean[] getIgnoredArray() {
		// TODO Auto-generated method stub
		return null;
	}

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
		String out = "Spine Name,Head Width,Neck Width,Length,Spine Type,Puncta per Spine";
		return out.split(",");
	}
	
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	public int[] getLayout()
	  {
		  return new int[]{60,60,60,95,84,60,15};
	  }
	
	public void setFont(Font f)
	{
		myFont = f;
	}
	
	public void fireSpineUpdateEvent()
	{
		makeSpineList();
	}
	
	public void countEventFired()
	{
		makeSpineList();
	}
	
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
	
	public boolean[] getPrintList()
	{		
		return columnDisplay;
	}
	
	public int getGroup()
	{
		return myGroup.getValue();
	}

}
