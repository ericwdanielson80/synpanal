package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.awt.Font;

import javax.swing.JPanel;
/**
 * Aggregates per-group puncta measurements for a single image and exposes
 * them through the tableData interface used by the data panes. Each puncta
 * has a display name (encoding which dendrite it belongs to), an integrated
 * intensity, an area in pixels, and a BooleanContainer that tracks whether
 * the user has selected or ignored it. Implements Savable so the ignore
 * list can be persisted across sessions while the other per-puncta numbers
 * are recomputed when images are reprocessed.
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
    

    /**
     * Constructs the group data by stashing the parallel arrays of names,
     * intensities, and areas along with the matched BooleanContainer array
     * for selection/ignore state and the repaint-target JPanel listeners.
     * The parameters are PN (puncta names, typically "<dendrite>.<index>"),
     * PI (integrated intensities), PA (areas), bc (per-puncta state
     * containers), and l (JPanels to repaint after state changes). A local
     * counter is declared but unused.
     */
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

    /**
     * Returns the text for the cell at (row, column): the puncta name for
     * column 0, the integrated intensity for column 1, and the area for
     * column 2; returns "Error" for any other column index.
     */
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

    /**
     * Toggles the selection state of the puncta at the given row and
     * repaints all registered listener panels so the change is visible.
     */
    public void pushSelected(int row)
    {
       bC[row].pushSelected();
       if(Listeners != null)
       {
           for(int k = 0; k < Listeners.length; k++)
           Listeners[k].repaint();
       }
    }

    /**
     * Toggles the ignore state of the puncta at the given row and repaints
     * the listener panels so downstream views refresh.
     */
    public void pushIgnored(int row)
    {
        bC[row].pushIgnored();
        
        if(Listeners != null)
            {
                for(int k = 0; k < Listeners.length; k++)
                Listeners[k].repaint();
            }
    }

    /** Returns whether the puncta at the given row is currently selected. */
    public boolean isSelected(int row)
    {
        return bC[row].isSelected();
    }
    /** Returns whether the puncta at the given row is currently ignored. */
    public boolean isIgnored(int row)
   {
       return bC[row].isIgnored();
   }
   /** Clears the selection flag on every puncta in the group. */
   public void resetSelected()
   {
       for(int k = 0; k < bC.length; k++)
       {
           bC[k].isSelected = false;
       }
   }

   /** Clears the ignore flag on every puncta and repaints listener panels so the restored data is shown. */
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


   /**
    * Computes the mean puncta area for the puncta belonging to the given
    * dendrite. A puncta belongs to dendrite d when its name parses to a
    * double in [d, d+1). Puncta flagged as ignored are skipped. Locals num
    * and sum accumulate the count and total area; zero is returned if no
    * puncta match.
    */
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

   /**
    * Computes the mean integrated intensity of the puncta on the given
    * dendrite, skipping ignored puncta; returns zero if none qualify. The
    * locals num and sum accumulate the count and intensity total for the
    * mean calculation.
    */
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

   /**
    * Counts the non-ignored puncta whose name places them on the given
    * dendrite, using the same "d <= name < d+1" convention as the averaging
    * helpers.
    */
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

   /** Returns the total number of puncta rows, matching the length of the name array. */
   public int getRows()
  {
      return punctaNames.length;
  }

  /**
   * Provided for tableData compatibility; the parameter j is ignored, and
   * instead the existing Listeners array is simply repainted.
   */
  public void setListener(JPanel j)
  {

      if(Listeners != null)
            {
                for(int k = 0; k < Listeners.length; k++)
                	Listeners[k].repaint();
            }

  }
  
  /**
   * Persists only the per-puncta ignore list; everything else is derived
   * from fresh image measurements when a session is reopened. The
   * parameters ds and i are the output stream and the serialization helper
   * used to tag the saved boolean array.
   */
  public void Save(DataOutputStream ds,IoContainer i)
  {
	  /*
	   * saves only which puncta are ignored everything else 
	   * is recalculated on the fly
	   */
	  boolean[] isIgnored = getIgnoredArray();
	  i.writeBooleanArray(ds,"PunctaData ignore list", isIgnored);
  }

  /**
   * Stub loader kept for the Savable contract; the actual ignore-list
   * restoration is handled elsewhere (see loadOldData). Always returns
   * null.
   */
  public Object Load(DataInputStream di, IoContainer i, int version, Group[] groupList)
  {
	 // isIgnored = i.readBooleanArray(di, "puncta ignore list");
	  return null;
  }
  
  /**
   * Merges the ignore flags from an older PunctaGroupData into this one by
   * matching puncta names in sorted numeric order. The locals d1 and d2 are
   * the parsed numeric names of the current and old rows, and the indices k
   * and j walk the two arrays in parallel: when names match the ignore flag
   * is copied; otherwise whichever index points at the smaller name is
   * advanced. If the old data is null the routine returns immediately.
   */
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
  
  /** Copies the per-puncta isIgnored flags into a freshly allocated boolean array and returns it. */
  public boolean[] getIgnoredArray()
  {
	  boolean[] r = new boolean[bC.length];
	  for(int k = 0; k < bC.length; k++)
		  {
		  r[k] = bC[k].isIgnored;
		  }
	  return r;	 
  }
  
  /**
   * Returns a shallow snapshot of this group's puncta names and
   * BooleanContainers, wrapped in a new PunctaGroupData, so later code can
   * replay the ignore state onto freshly measured puncta. The locals
   * oldIgnore and oldNames hold the copied references.
   */
  public PunctaGroupData getOldData()
  {
	  BooleanContainer[] oldIgnore = new BooleanContainer[bC.length];
	  String[] oldNames = new String[punctaNames.length];
	  System.arraycopy(bC,0,oldIgnore,0,oldIgnore.length);
	  System.arraycopy(punctaNames,0,oldNames,0,punctaNames.length);
	  return new PunctaGroupData(oldNames,null,null,oldIgnore,null);
  }
  
  /** Returns null because this table does not use named column headers. */
  public String[] getTitles()
  {
	  return null;
  }
  
  /** No-op: this table does not honor per-column display flags. */
  public void setColumnDisplay(boolean[] b)
  {
	  
  }
  
  /** Returns null because this table does not define its own column layout. */
  public int[] getLayout()
  {
	  return null;
  }
  
  /** Stores the supplied font for later drawing operations. */
  public void setFont(Font f)
	{
		myFont = f;
	}
  
  /** Returns null because this table does not expose a per-column print mask. */
  public boolean[] getPrintList()
  {
	  return null;
  }
  


}
