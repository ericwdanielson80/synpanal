package neuron_analyzer;

import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DendritePunctaTableData implements tableData {
Group myGroup;
int myColor;
boolean[] columnDisplay;
functionListener fL;
Font myFont;;

DecimalFormat dF = new DecimalFormat("#######.##");
Float dL = new Float(0.0);
Integer dN = new Integer(0);
	
	public DendritePunctaTableData(functionListener FL, Group Group, int Color)
	{
		fL = FL;
		myGroup = Group;
		myColor = Color;
		columnDisplay = new boolean[this.getTitles().length];
		for(int k = 0; k < columnDisplay.length; k++)
			columnDisplay[k] = true;
	}

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

	public boolean isIgnored(int row) {
		// TODO Auto-generated method stub		
		return fL.getDendrite(myGroup.getValue(), row).isIgnored();
	}

	public boolean isSelected(int row) {
		// TODO Auto-generated method stub
		return fL.getDendrite(myGroup.getValue(), row).isSelected();
	}

	public void pushIgnored(int row) {
		// TODO Auto-generated method stub
		fL.getDendrite(myGroup.getValue(), row).bC.pushIgnored();

	}

	public void pushSelected(int row) {
		// TODO Auto-generated method stub
		fL.getDendrite(myGroup.getValue(), row).bC.pushSelected();
			
		if(fL.getDendrite(myGroup.getValue(), row).bC.isSelected)
        	fL.focusDendrite(row);
	}

	public void resetIgnored() {
		// TODO Auto-generated method stub
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		for(int k = 0; k < d.length; k++)
		{
			if(d != null)
				d[k].bC.isIgnored = false;
		}
	}

	public void resetSelected() {
		// TODO Auto-generated method stub
		Dendrite[] d = fL.getDendrites(myGroup.getValue());
		for(int k = 0; k < d.length; k++)
		{
			if(d != null)
				d[k].bC.isSelected = false;
		}

	}
	
	public String[] getTitles()
	{
		String out = "Dendrite,Length,Puncta Density,Ave Puncta Intensity,Ave Puncta Area,Total Puncta Intensity Per Length,Dendrite Intensity,Dendrite Ave Intensity,Puncta Number";
		return out.split(",");
	}
	
	public void setColumnDisplay(boolean[] b)
	{
		columnDisplay = b;
	}
	
	public void setFont(Font f)
	{
		myFont = f;
	}
	
	public int[] getLayout()
	  {	
		return new int[]{60,60,100,100,100,170,130,130,60,15};
	  }
	
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
	
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
	
	

}
