package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.io.PrintWriter;
import java.awt.Font;
/**
 * Table-data adapter that presents the dendrites belonging to a single
 * group and a chosen color channel through the tableData interface. In
 * addition to the table-column accessors required by tableData, it exposes
 * a catalogue of per-group aggregate getters (average puncta number,
 * average intensity, intensity-per-length, spine counts by type, etc.)
 * used by the data-printer classes and the on-screen summaries. Each
 * dendrite also gets a BooleanContainer so selection and ignore state can
 * be toggled individually.
 */
public class DendriteGroupData implements tableData {
    int[] dendriteNames;
    float[] dendriteLengths;
    Float dL = new Float(0.0);
    Integer dN = new Integer(0);
    PunctaGroupData punctaData;    
    public BooleanContainer[] bC;
    JPanel[] Listeners;
    DecimalFormat dF = new DecimalFormat("#######.##");
    functionListener fL;

    Dendrite[] myDendrites;
    int myColor;
    Font myFont;
        
    /*public DendriteGroupData(int[] DendriteNames, float[] DendriteLengths, PunctaGroupData PunctaData, boolean[] IS, boolean[] II,JPanel[] l,functionListener fl) {
        dendriteNames = DendriteNames;
        dendriteLengths = DendriteLengths;
        punctaData = PunctaData;
        isSelected = IS;
        isIgnored = II;
        Listeners = l;
        fL = fl;        
    }*/
    
    /**
     * Wires up the group data with the dendrite array, repaint-target
     * panels, shared functionListener, color channel index, backing
     * PunctaGroupData, and parallel IS/II arrays that seed the per-dendrite
     * selected/ignored flags. It builds a BooleanContainer for every
     * non-null dendrite and links it back into the dendrite via
     * linkBoolean so downstream toggles propagate correctly.
     */
    public DendriteGroupData(Dendrite[] d,JPanel[] l, functionListener fl,int color,PunctaGroupData PunctaData,boolean[] IS,boolean[] II)
    {
    	myDendrites = d;
    	Listeners = l;
    	fL = fl;
    	myColor = color;
    	punctaData = PunctaData;    
    	bC = new BooleanContainer[IS.length];
    	for(int k = 0;k < myDendrites.length; k++)
    	{
    		if(myDendrites[k] != null)
    			{
    			bC[k] = new BooleanContainer(IS[k],II[k]);
    			myDendrites[k].linkBoolean(bC[k]);
    			}
    	}
    }
    
    /** Returns the display value for a single cell: the row index as a dendrite id, the dendrite length, the puncta number, the average puncta intensity, or the average puncta area depending on the column. */
    public String getData(int row, int column)
   {
       
       switch(column)
       {
       case 0: return dN.toString(row);
       case 1: return dF.format(myDendrites[row].getLength(fL.getCalibration()));
       case 2: return dF.format(((float)(myDendrites[row].getPunctaNumber(myColor,fL.getCalibration()))));
       case 3: return dN.toString(myDendrites[row].getAvePunctaIntensity(myColor));
       case 4: return dF.format((float)myDendrites[row].getAvePunctaArea(myColor,fL.getCalibration()));       
       }
       return "Error";
   }

   /** Returns the mean puncta count per non-ignored dendrite; sum and count accumulate the totals and zero is returned if no dendrites qualify. */
   public float getAvePunctaNum()
   {
       float sum = 0;
       float count = 0;
       
              
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += ((float)myDendrites[k].getPunctaNumber(myColor, fL.getCalibration()));
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;
   }

   /** Returns the mean average-puncta-intensity across non-ignored dendrites; returns zero when none qualify. */
   public int getAveIntensity()
   {
       int sum = 0;
       int count = 0;
       
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += myDendrites[k].getAvePunctaIntensity(myColor);
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;
   }
   
   /** Returns the mean total-integrated-puncta-intensity-per-length across non-ignored dendrites; returns zero when none qualify. Locals sum and count accumulate the total and the denominator. */
   public float getAveTotalPunctaIntegratedIntensityPerLength()
   {
	   int sum = 0;
       int count = 0;
       
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += myDendrites[k].getTotalPunctaIntegratedIntensityPerLength(myColor,fL.getCalibration());
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;
   }

   /** Returns the mean puncta area across non-ignored dendrites; returns zero when none qualify. */
   public float getAveArea()
  {
      float sum = 0;
      float count = 0;
     
      for(int k = 0; k < myDendrites.length; k++)
      {
          if(myDendrites[k] != null && !myDendrites[k].isIgnored())
          {
              sum += myDendrites[k].getAvePunctaArea(myColor,fL.getCalibration());
              count++;
          }
      }
      if(count == 0)
          return count;
      return sum / count;

  }
   
  /** Returns the total dendrite-shaft intensity for dendrite k in the configured color channel, pulling totalRed/Green/BlueIntensity[0] as appropriate. */
  public float getDendriteIntensity(int k)
  {
	  float sum = 0;
	  
	  switch(myColor)
	  {
	  case 0: 	sum += (float)(myDendrites[k].totalRedIntensity[0]); break;
	  case 1: 	sum += (float)(myDendrites[k].totalGreenIntensity[0]); break;
	  case 2: 	sum += (float)(myDendrites[k].totalBlueIntensity[0]); break;	  
	  }
	  return sum;
  }
  
  /** Returns the average shaft intensity for dendrite k (sum over pixel count) scaled by the calibration squared to convert to per-area units. */
  public float getDendriteAveIntensity(int k)
  {
	  float sum = 0;
	  
	  switch(myColor)
	  {
	  case 0: 	sum += (float)(myDendrites[k].totalRedIntensity[0]/myDendrites[k].totalRedIntensity[1]); break;
	  case 1: 	sum += (float)(myDendrites[k].totalGreenIntensity[0]/myDendrites[k].totalGreenIntensity[1]); break;
	  case 2: 	sum += (float)(myDendrites[k].totalBlueIntensity[0]/myDendrites[k].totalBlueIntensity[1]); break;	  
	  }
	  return sum / ((float)fL.getCalibration() * (float)fL.getCalibration());
  }
  
  /** Returns the mean of getDendriteIntensity across non-ignored dendrites; returns zero when none qualify. */
  public float getAveDendriteIntensity()
  {
      float sum = 0;
      float count = 0;
      
      for(int k = 0; k < myDendrites.length; k++)
      {
          if(myDendrites[k] != null && !myDendrites[k].isIgnored())
          {
              sum += getDendriteIntensity(k);
              count++;
          }
      }
      if(count == 0)
          return count;
      return sum / count;

  }
  
  /** Returns the mean of getDendriteAveIntensity across non-ignored dendrites; returns zero when none qualify. */
  public float getAveDendriteAveIntensity()
  {
      float sum = 0;
      float count = 0;     
      for(int k = 0; k < myDendrites.length; k++)
      {
          if(myDendrites[k] != null && !myDendrites[k].isIgnored())
          {
              sum += getDendriteAveIntensity(k);
              count++;
          }
      }
      if(count == 0)
          return count;
      return sum / count;

  }

  /**
   * Toggles the selection state of the dendrite at row and focuses it
   * through the functionListener if it is now selected. Returns early if
   * the dendrite list is null or the chosen row is null.
   */
  public void pushSelected(int row)
    {
	  
	  if(myDendrites == null)
		  return;
	  
	  if(myDendrites[row] == null)
		  return;
	  
	  
        myDendrites[row].bC.pushSelected();   
        
        if(myDendrites[row].bC.isSelected)
        	fL.focusDendrite(row);
        	
            
    }

    /** Toggles the ignore state of the dendrite at row and repaints every registered listener panel; returns early if the list or target is null. */
    public void pushIgnored(int row)
    {
    	
    	
    	if(myDendrites == null)
  		  return;
  	  
  	  if(myDendrites[row] == null)
  		  return;
  	  
          myDendrites[row].bC.pushIgnored();
          	
              
        if(Listeners != null)
           {
               for(int k = 0; k < Listeners.length; k++)
               Listeners[k].repaint();
           }
    }

    /** Reports whether the dendrite at row is currently selected. */
    public boolean isSelected(int row)
    {       	    	
        return myDendrites[row].isSelected();
    }
    /** Reports whether the dendrite at row is currently ignored. */
    public boolean isIgnored(int row)
   {    	
       return myDendrites[row].isIgnored();
   }
   /** Clears the selection flag on every non-null dendrite. */
   public void resetSelected()
   {
	   
	   if(myDendrites == null)
		   return;
       for(int k = 0; k < myDendrites.length; k++)
       {
    	   if(myDendrites[k] != null)
    		   myDendrites[k].bC.isSelected = false;
       }
   }

   /** Clears the ignore flag on every non-null dendrite and repaints any registered listener panels. */
   public void resetIgnored()
   {
	  
	   if(myDendrites == null)
		   return;
       for(int k = 0; k < myDendrites.length; k++)
       {
    	   if(myDendrites[k] != null)
    		   myDendrites[k].bC.isIgnored = false;
       }
       if(Listeners != null)
            {
                for(int k = 0; k < Listeners.length; k++)
                Listeners[k].repaint();
            }

   }

   /** Returns the number of non-null dendrite entries, matching the visible row count in the table. */
   public int getRows()
   {
	   
	   int counter = 0;
	   for(int k = 0; k < myDendrites.length; k++)
	   {
		   if(myDendrites[k] != null)
			   counter++;
	   }
       return counter;
   }

   /** Replaces the array of JPanels that should be repainted when the data changes; the k parameter is unused. */
   public void setListener(JPanel[] j, int k)
  {
      Listeners = j;
  }
   
  /** Returns the mean number of spines per non-ignored dendrite, using the current calibration to map pixel lengths to real units. */
  public double getAveSpineNum()
  {
	  
	  double total = 0;
	  int counter = 0;
	    int k = 0;
	    for(k = 0; k < myDendrites.length; k++)
	    {
	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
	    	{
	    		counter++;
	    		total += myDendrites[k].getSpineNum(fL.getCalibration());	    		
	    	}
	    	
	    }    
	    if(k == 0)
	    	return 0;    
	    
	    return total / (double)(counter) ;	    
  }
  
  /** Returns the mean count of spines of a particular type (0 mushroom, 1 thin, 2 stubby, 3 filopodia) per non-ignored dendrite. */
  public double getSpineTypeNum(int type)
  {
	  double total = 0;
	  int counter = 0;
	  
	    int k = 0;
	    for(k = 0; k < myDendrites.length; k++)
	    {
	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
	    	{
	    		counter++;
	    	total += myDendrites[k].getSpineTypeNum(type,fL.getCalibration());
	    	}
	    }    
	    if(k == 0)
	    	return 0;
	    return total / (double)(counter) ;	  
  }
  
  /** Returns the mean spine head width across non-ignored dendrites. */
  public double getAveSpineWidth()
  {
	  double total = 0;
	  int counter = 0;
	  
	    int k = 0;
	    for(k = 0; k < myDendrites.length; k++)
	    {
	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
	    	{
	    		counter++;
	    	total += myDendrites[k].getAveSpineWidth(fL.getCalibration());
	    	}
	    }    
	    if(k == 0)
	    	return 0;
	    return total / (double)(counter) ;	   
  }
  
  /** Returns the mean spine length across non-ignored dendrites. */
  public double getAveSpineLength()
  {
	  int counter = 0;
	  double total = 0;
	  
	    int k = 0;
	    for(k = 0; k < myDendrites.length; k++)
	    {
	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
	    	{
	    		counter++;
	    	total += myDendrites[k].getAveSpineLength(fL.getCalibration());
	    	}
	    }    
	    if(k == 0)
	    	return 0;
	    return total / (double)(counter) ;	 	  
  }
  
  /** Copies the per-dendrite isIgnored flags out of bC into a fresh boolean array. */
  public boolean[] getIgnoredArray()
  {	  
	  boolean[] r = new boolean[bC.length];
	  for(int k = 0; k < bC.length; k++)
		  {
		  r[k] = bC[k].isIgnored;
		  }
	  return r;	  
  }
  
  /** Applies the given boolean array onto the per-dendrite isIgnored flags, restoring a previously saved ignore mask. */
  public void loadIgnoredArray(boolean[] b)
  {
	  for(int k = 0; k < bC.length; k++)
	  {
		  bC[k].isIgnored = b[k];
	  }
  }
  
  /** Returns the mean (dendrite intensity / dendrite length) across non-ignored dendrites; returns zero when none qualify. */
  public float getAveDendriteIntensityPerLength()
  {
      float sum = 0;
      float count = 0;
      
      for(int k = 0; k < myDendrites.length; k++)
      {
          if(myDendrites[k] != null && !myDendrites[k].isIgnored())
          {
              sum += (getDendriteIntensity(k) / myDendrites[k].getLength(fL.getCalibration()));
              count++;
          }
      }
      if(count == 0)
          return count;
      return sum / count;

  } 
  
  /** Returns null because this table keeps titles externally rather than deriving them here. */
  public String[] getTitles()
  {
	  return null;
  }
  
  /** No-op: per-column visibility is not honored by this table. */
  public void setColumnDisplay(boolean[] b)
  {
	  
  }
  
  /** Returns the fixed pixel column widths (plus the trailing 15-pixel row height) used by the data table. */
  public int[] getLayout()
  {
	  return new int[]{60,50,95,84,60,15};
  }
  
  /** Stores the supplied font for later drawing operations. */
  public void setFont(Font f)
	{
		myFont = f;
	}
  
  /** Returns null because no per-column print mask is defined for this table. */
  public boolean[] getPrintList()
	{
		return null;
	}
  
  
  

}
