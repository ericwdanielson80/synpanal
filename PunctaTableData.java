package neuron_analyzer;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.text.DecimalFormat;

import javax.swing.JPanel;

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


	public PunctaTableData(functionListener FL, Group Group, int Color)
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
		
		switch(column)
		{
		case 0: return punctaNames[row];
        case 1: return punctaIntensities[row];
        case 2: return dF.format(punctaAreas[row] * fL.getCalibration() * fL.getCalibration());
        }
        return "Error";
		
		
	}

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

	public int[] getLayout() {
		// TODO Auto-generated method stub
		return new int[] {600,60,60,15};
	}

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

	public String[] getTitles() {
		String s = "Puncta Name,Puncta Intensity,Puncta Area";
		return s.split(",");
	}

	public boolean isIgnored(int row) {
		return punctaList[row].isIgnored();
	}

	public boolean isSelected(int row) {
		return punctaList[row].isSelected();
	}

	public void pushIgnored(int row) {
		punctaList[row].pushIgnored();
		fL.repaintScrollPane();
	}

	public void pushSelected(int row) {
		punctaList[row].pushSelected();
		fL.repaintScrollPane();
	}

	public void resetIgnored() {
		// TODO Auto-generated method stub
		for(int k = 0; k < punctaList.length; k++)
		{
			punctaList[k].bC.isIgnored = false;
		}

	}

	public void resetSelected() {
		// TODO Auto-generated method stub
		for(int k = 0; k < punctaList.length; k++)
		{
			punctaList[k].bC.isSelected = false;
		}

	}

	public void setColumnDisplay(boolean[] b) {
		// TODO Auto-generated method stub

	}

	public void setFont(Font f) {
		// TODO Auto-generated method stub

	}
	
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
	
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	public int getGroup()
	{
		return myGroup.getValue();
	}
	
	
	
	

}
