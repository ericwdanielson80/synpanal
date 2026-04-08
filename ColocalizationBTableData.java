package neuron_analyzer;

import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class ColocalizationBTableData extends ColocalizationATableData implements tableData, ThresholdEventListener {

	
	public ColocalizationBTableData(functionListener FL, Group Group, int Color)
	{
		super(FL,Group,Color);
		
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
	
	
	private String getColumnLabel(int row)
	{
		switch(row)
		{
		case 0: return "Total " + B;
		case 1: return "{" + B + "}" + "\u2229" + "{" + A + "}";
		case 2: return "{" + B + "}" + "\\" + "{" + A + "}";		
		}
		return "error";
	}
	private String getIntegrated(int row)
	{
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedB);
		case 1: return dF.format(myInfo[myColor].integratedBwithA);
		case 2: return dF.format(myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA);		
		}
		return "error";
	}
	private String getIntegratedPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].integratedB / myInfo[myColor].integratedB) * 100 );
		case 1: return dF.format((myInfo[myColor].integratedBwithA/ myInfo[myColor].integratedB) * 100 );
		case 2: return dF.format(((myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA)/ myInfo[myColor].integratedB) * 100 );	
		}
		return "error";
	}
	private String getArea(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].totalPixelsB);
		case 1: return dF.format(myInfo[myColor].totalPixelsAB);
		case 2: return dF.format(myInfo[myColor].totalPixelsB - myInfo[myColor].totalPixelsAB);		
		}
		return "error";
	}
	private String getAreaPercent(int row){
		switch(row)
		{
		case 0: return dF.format((myInfo[myColor].totalPixelsB / myInfo[myColor].totalPixelsB) * 100 );
		case 1: return dF.format((myInfo[myColor].totalPixelsAB/ myInfo[myColor].totalPixelsB) * 100 );
		case 2: return dF.format(((myInfo[myColor].totalPixelsB - myInfo[myColor].totalPixelsAB)/ myInfo[myColor].totalPixelsB) * 100 );	
		}
		return "error";
	}
	private String getAverage(int row){
		switch(row)
		{
		case 0: return dF.format(myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB);
		case 1: return dF.format(myInfo[myColor].integratedBwithA / myInfo[myColor].totalPixelsAB);
		case 2: return dF.format((myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA)/ (myInfo[myColor].totalPixelsB -myInfo[myColor].totalPixelsAB));		
		}
		return "error";
		}
	
	private String getAveragePercent(int row){
		switch(row)
		{
		case 0: return dF.format(((myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB) / (myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB)) * 100 );
		case 1: return dF.format(((myInfo[myColor].integratedBwithA / myInfo[myColor].totalPixelsAB) / (myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB)) * 100 );
		case 2: return dF.format((((myInfo[myColor].integratedB - myInfo[myColor].integratedBwithA)/ (myInfo[myColor].totalPixelsB -myInfo[myColor].totalPixelsAB)) / ( myInfo[myColor].integratedB / myInfo[myColor].totalPixelsB)) * 100);		
		}
		return "error";
		}
	
		

}
