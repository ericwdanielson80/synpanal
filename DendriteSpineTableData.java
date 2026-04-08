package neuron_analyzer;
import java.awt.Font;
import java.text.DecimalFormat;

public class DendriteSpineTableData extends DendritePunctaTableData {
		
	public DendriteSpineTableData(functionListener FL, Group Group, int Color)
	{
		super(FL,Group,Color);
	}

	public String getData(int row, int column) {
		// TODO Auto-generated method stub
		/*
		 * Dendrite Number | Dendrite Length | Spine Density |Ave Head Width| Ave Neck Width| Ave Length| Mushroom Density| Thin Density|Stubby Density|Filopodia Density|0 Puncta|1 Puncta|2 Puncta|3>=Puncta
		 * 
		 * 
		 */
		switch(column)
	       {
	       case 0: return dN.toString(row);
	       case 1: return dF.format(fL.getDendrite(myGroup.getValue(),row).getLength(fL.getCalibration()));	       
	       case 2: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineNum(fL.getCalibration()));
	       case 3: return dF.format(fL.getDendrite(myGroup.getValue(),row).getAveSpineWidth(fL.getCalibration()));     
	       case 4: return dF.format(fL.getDendrite(myGroup.getValue(),row).getAveSpineNeckWidth(fL.getCalibration())); 
	       case 5: return dF.format(fL.getDendrite(myGroup.getValue(),row).getAveSpineLength(fL.getCalibration()));
	       case 6: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(0,fL.getCalibration()));
	       case 7: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(1,fL.getCalibration()));
	       case 8: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(2,fL.getCalibration()));
	       case 9: return dF.format(fL.getDendrite(myGroup.getValue(),row).getSpineTypeNum(3,fL.getCalibration()));
	       case 10: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(0,myColor,fL.getCalibration()));
	       case 11: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(1,myColor,fL.getCalibration()));
	       case 12: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(2,myColor,fL.getCalibration()));
	       case 13: return dF.format(fL.getDendrite(myGroup.getValue(),row).getPunctaPerSpineData(3,myColor,fL.getCalibration()));
	       }
	       return "Error";
		
	}

	
	public String[] getTitles()
	{
		String out = "Dendrite,Length,Spine Density,Ave Head Width,Ave Neck Width,Ave Length,Mushroom Density, Thin Density, Stubby Density, Filopodia Density, 0 Puncta,1 Puncta,2 Puncta,3>=Puncta";
		return out.split(",");
	}
	
	
	public int[] getLayout()
	  {		  
		  return new int[]{60,50,95,84,60,60,60,60,60,60,60,60,60,60,60,60,15};
	  }
	
	

}
