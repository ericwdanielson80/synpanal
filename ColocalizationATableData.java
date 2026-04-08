package neuron_analyzer;

import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

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

	public boolean[] getIgnoredArray() {
		
		return null;
	}

	public int getRows() {
		// TODO Auto-generated method stub
	       return 3;
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
		String out = "Colocalization Colors ,Integrated,Integrated %,Area     ,Area %     ,Average     ,Average %     ";
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
		return new int[]{150,100,100,100,100,100,100,15};
	  }	
	
	public boolean[] getPrintList()
	{
		return columnDisplay;
	}
	
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
	private String getIntegratedPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].integratedA / myInfo[myColor].integratedA) * 100 );
		case 1: return dF.format((myInfo[myColor].integratedAwithB/ myInfo[myColor].integratedA) * 100 );
		case 2: return dF.format(((myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB)/ myInfo[myColor].integratedA) * 100 );	
		}
		return "error";
	}
	private String getArea(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].totalPixelsA);
		case 1: return dF.format(myInfo[myColor].totalPixelsAB);
		case 2: return dF.format(myInfo[myColor].totalPixelsA - myInfo[myColor].totalPixelsAB);		
		}
		return "error";
	}
	private String getAreaPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].totalPixelsA / myInfo[myColor].totalPixelsA) * 100 );
		case 1: return dF.format((myInfo[myColor].totalPixelsAB / myInfo[myColor].totalPixelsA) * 100 );
		case 2: return dF.format(((myInfo[myColor].totalPixelsA - myInfo[myColor].totalPixelsAB) / myInfo[myColor].totalPixelsA ) * 100 );	
		}
		return "error";
	}
	private String getAverage(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA);
		case 1: return dF.format(myInfo[myColor].integratedAwithB / myInfo[myColor].totalPixelsAB);
		case 2: return dF.format((myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB)/ (myInfo[myColor].totalPixelsA -myInfo[myColor].totalPixelsAB));		
		}
		return "error";
		}
	
	private String getAveragePercent(int row){
		switch(row)
		{
		case 0: return dF.format(((myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA) / (myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA)) * 100 );
		case 1: return dF.format(((myInfo[myColor].integratedAwithB / myInfo[myColor].totalPixelsAB) / (myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA)) * 100 );
		case 2: return dF.format((((myInfo[myColor].integratedA - myInfo[myColor].integratedAwithB)/ (myInfo[myColor].totalPixelsA -myInfo[myColor].totalPixelsAB)) / ( myInfo[myColor].integratedA / myInfo[myColor].totalPixelsA)) * 100);		
		}
		return "error";
		}
	
	public void fireTresholdUpdateEvent()
	{
		myInfo = fL.getColocalizationInfo();
	}
	
	
	
	

}
