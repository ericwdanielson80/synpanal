package neuron_analyzer;

import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.io.PrintWriter;
import java.awt.Font;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DendriteGroupData implements tableData {
    int[] dendriteNames;
    float[] dendriteLengths;
    Float dL = new Float(0.0);
    Integer dN = new Integer(0);
    PunctaGroupData punctaData;    
    BooleanContainer[] bC;
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

    public boolean isSelected(int row)
    {       	    	
        return myDendrites[row].isSelected();
    }
    public boolean isIgnored(int row)
   {    	
       return myDendrites[row].isIgnored();
   }
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

   public void setListener(JPanel[] j, int k)
  {
      Listeners = j;
  }
   
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
  
  public boolean[] getIgnoredArray()
  {	  
	  boolean[] r = new boolean[bC.length];
	  for(int k = 0; k < bC.length; k++)
		  {
		  r[k] = bC[k].isIgnored;
		  }
	  return r;	  
  }
  
  public void loadIgnoredArray(boolean[] b)
  {
	  for(int k = 0; k < bC.length; k++)
	  {
		  bC[k].isIgnored = b[k];
	  }
  }
  
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
  
  public String[] getTitles()
  {
	  return null;
  }
  
  public void setColumnDisplay(boolean[] b)
  {
	  
  }
  
  public int[] getLayout()
  {
	  return new int[]{60,50,95,84,60,15};
  }
  
  public void setFont(Font f)
	{
		myFont = f;
	}
  
  public boolean[] getPrintList()
	{
		return null;
	}
  
  
  

}
