package neuron_analyzer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.awt.Font;

import javax.swing.JPanel;
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
public class PunctaGroupData implements tableData, Savable{
    String[] punctaNames;
    int[] punctaIntensity;
    double[] punctaArea;
    //boolean[] isSelected;
    //boolean[] isIgnored;
    BooleanContainer[] bC;
    Double pA = new Double(0.0);
    Integer pI = new Integer(0);
    JPanel[] Listeners;    
    Font myFont;
    

    public PunctaGroupData(String[] PN, int[] PI, double[] PA,BooleanContainer[] bc, JPanel[] l) {
        punctaNames = PN;
        punctaIntensity = PI;
        punctaArea = PA;
        
        int counter = 0;
       
        bC = bc;
        
        //isSelected = IS;
        //isIgnored = II;
        Listeners = l;        
    }   

    public String getData(int row, int column)
    {
        switch(column)
        {
        case 0: return punctaNames[row];
        case 1: return pI.toString(punctaIntensity[row]);
        case 2: return pA.toString(punctaArea[row]);
        }
        return "Error";
    }

    public void pushSelected(int row)
    {
       bC[row].pushSelected();
       if(Listeners != null)
       {
           for(int k = 0; k < Listeners.length; k++)
           Listeners[k].repaint();
       }
    }

    public void pushIgnored(int row)
    {
        bC[row].pushIgnored();
        
        if(Listeners != null)
            {
                for(int k = 0; k < Listeners.length; k++)
                Listeners[k].repaint();
            }
    }

    public boolean isSelected(int row)
    {
        return bC[row].isSelected();
    }
    public boolean isIgnored(int row)
   {
       return bC[row].isIgnored();
   }
   public void resetSelected()
   {
       for(int k = 0; k < bC.length; k++)
       {
           bC[k].isSelected = false;
       }
   }

   public void resetIgnored()
   {
       for(int k = 0; k < bC.length; k++)
       {
           bC[k].isIgnored = false;
       }
       if(Listeners != null)
           {
               for(int k = 0; k < Listeners.length; k++)
               Listeners[k].repaint();
           }



   }


   public double getAveArea(int dendrite)
   {
       double num = 0;
       double sum = 0;
       for(int k = 0; k < punctaNames.length; k++)
       {
           if(dendrite <= Double.parseDouble(punctaNames[k]) && Double.parseDouble(punctaNames[k]) < dendrite + 1 && !bC[k].isIgnored)
           {
               sum+= punctaArea[k];
               num++;
           }
       }
       if(num == 0)
           return num;

       return sum / num;
   }

   public int getAveIntensity(int dendrite)
   {
       int num = 0;
       int sum = 0;
       for(int k = 0; k < punctaNames.length; k++)
       {
           if(dendrite <= Double.parseDouble(punctaNames[k]) && Double.parseDouble(punctaNames[k]) < dendrite + 1 && !bC[k].isIgnored)
           {
               sum+= punctaIntensity[k];
               num++;
           }
       }

       if(num == 0)
           return num;

       return sum / num;
   }

   public int getPunctaNum(int dendrite)
   {
       int num = 0;
       for(int k = 0; k < punctaNames.length; k++)
       {
           if(dendrite <= Double.parseDouble(punctaNames[k]) && Double.parseDouble(punctaNames[k]) < dendrite + 1 && !bC[k].isIgnored())
           {
               num++;
           }
       }
       return num;
   }

   public int getRows()
  {
      return punctaNames.length;
  }

  public void setListener(JPanel j)
  {

      if(Listeners != null)
            {
                for(int k = 0; k < Listeners.length; k++)
                	Listeners[k].repaint();
            }

  }
  
  public void Save(DataOutputStream ds,IoContainer i)
  {
	  /*
	   * saves only which puncta are ignored everything else 
	   * is recalculated on the fly
	   */
	  boolean[] isIgnored = getIgnoredArray();
	  i.writeBooleanArray(ds,"PunctaData ignore list", isIgnored);
  }

  public Object Load(DataInputStream di, IoContainer i, int version, Group[] groupList)
  {
	 // isIgnored = i.readBooleanArray(di, "puncta ignore list");
	  return null;
  }
  
  public void loadOldData(PunctaGroupData old)
  {
	  double d1;
	  double d2;
	  int k = 0;
	  int j = 0;
	  if(old == null)
		  return;
	  
	  
	  while(k < punctaNames.length && j < old.punctaNames.length)
	  {
		  
		  d1 = Double.valueOf(punctaNames[k]);
		  d2 = Double.valueOf(old.punctaNames[j]);
		 
		  if(d1 == d2)
		  {			  
			  bC[k].isIgnored = old.bC[j].isIgnored;
			  k++;
			  j++;
		  }
		  else if(d1 > d2)
		  {
			  j++;
		  }
		  else if(d1 < d2)
		  {
			  k++;
		  }
	  }
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
  
  public PunctaGroupData getOldData()
  {
	  BooleanContainer[] oldIgnore = new BooleanContainer[bC.length];
	  String[] oldNames = new String[punctaNames.length];
	  System.arraycopy(bC,0,oldIgnore,0,oldIgnore.length);
	  System.arraycopy(punctaNames,0,oldNames,0,punctaNames.length);
	  return new PunctaGroupData(oldNames,null,null,oldIgnore,null);
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
	  return null;
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
