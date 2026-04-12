package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.awt.image.Raster;
/**
 * Wraps a Puncta array (typically one per color channel on a single
 * dendrite) and provides aggregate queries, drawing, ignored-flag
 * management, auto-ignore logic, and save/load support. Most methods
 * skip puncta whose bC.isIgnored flag is true so statistics reflect
 * only active puncta.
 */
public class PunctaContainer {
public Puncta[] myPuncta;
	
/** Stores the backing Puncta array p for all subsequent operations. */
public PunctaContainer(Puncta[] p)
{	
	myPuncta = p;	
}
	
/** Returns the total number of puncta stored, including ignored ones; 0 when unset. */
public int getTotalPunctaNumber()
{	
	if(myPuncta == null)
		return 0;
	return myPuncta.length;
}
	
/**
 * Returns the count of puncta that are not currently ignored. counter
 * increments once per non-ignored entry in myPuncta.
 */
public int getPunctaNumber()
{
	if(myPuncta == null)
		return 0;
	
	int counter = 0;
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(!myPuncta[k].bC.isIgnored)
			counter++;
	}
	return counter;
}
	
/**
 * Forwards a draw request to every stored Puncta. The Graphics2D g is
 * the target canvas and dendrite/puncta are index hints passed through
 * to each Puncta.drawPuncta call for labelling purposes.
 */
public void drawPuncta(Graphics2D g,int dendrite,int puncta)
{
	for(int k = 0 ;k < myPuncta.length; k++)
		myPuncta[k].drawPuncta(g, dendrite, puncta);
}
	
/**
 * Returns the mean intensity across non-ignored puncta. counter
 * tracks the number of contributing puncta and intensity accumulates
 * their individual intensities; returns -1 if myPuncta is null and 0
 * if counter is zero.
 */
public int getAveIntensity()
{
	if(myPuncta == null)
		return -1;
	
	int counter = 0;
	int intensity = 0;	
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(!myPuncta[k].bC.isIgnored)
			{			
			counter++;
			intensity += myPuncta[k].intensity;
			}
	}
	if(counter == 0)
		return 0;
	return intensity / counter;
}
	
/**
 * Returns the mean area of non-ignored puncta. counter and area
 * accumulate the count and summed areas; returns -1 when unset and 0
 * when no puncta are active.
 */
public int getAveArea()
{

	if(myPuncta == null)
		return -1;
	
	int counter = 0;
	int area = 0;
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(!myPuncta[k].bC.isIgnored)
			{
			counter++;
			area += myPuncta[k].area;
			}
	}
	
	if(counter == 0)
		return 0;
	return area / counter;
}

/**
 * Returns the total intensity summed across non-ignored puncta; the
 * local intensity accumulates each puncta's intensity value.
 */
public int getIntegratedIntensity()
{
	int intensity = 0;
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(!myPuncta[k].bC.isIgnored)
			{			
			intensity += myPuncta[k].intensity;
			}
	}
	return intensity;
}

/** Returns the raw intensity for the k-th puncta. */
public int getIntensity(int k)
{
	return myPuncta[k].intensity;
}

/** Returns the pixel area of the k-th puncta. */
public int getArea(int k)
{
	return myPuncta[k].area;
}

/** Reports whether the k-th puncta is currently selected in the UI. */
public boolean isSelected(int k)
{
	return myPuncta[k].bC.isSelected;
}

/** Reports whether the k-th puncta has been marked ignored. */
public boolean isIgnored(int k)
{
	return myPuncta[k].bC.isIgnored;
}

/**
 * Locates the first puncta whose border polygon contains the Point
 * p and returns it (or null if none match). Used for mouse hit
 * testing.
 */
public Puncta findPuncta(Point p)
{
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(myPuncta[k].border.contains(p))
			return myPuncta[k];
	}
	return null;
}

/**
 * Replaces the stored puncta with a new array, preserving the
 * ignored flag where indices overlap. If p is null the call is a
 * no-op; if no previous puncta were set, p is stored directly.
 * Otherwise max is the lesser of the two lengths and the loop copies
 * isIgnored from the old entries onto the new ones before myPuncta
 * is rebound to p. The parameter p is the replacement Puncta array.
 */
public void updatePuncta(Puncta[] p)
{
	if(p == null)
		return;	
	if(myPuncta == null)
	{
		myPuncta = p;
		return;
	}
	int max = p.length;
	if(myPuncta.length < max)
		max = myPuncta.length;
	
	if(myPuncta != null)
	{
		for(int k = 0; k < max; k++)
		{						
			if(myPuncta[k].bC.isIgnored)
				p[k].bC.isIgnored = true;
		}
	}
	myPuncta = p;
}

/**
 * Restores the per-puncta ignored flags from the stream. Reads a
 * boolean[] isIgnored via the IoContainer and, if non-null, copies
 * each flag onto the matching Puncta's bC.isIgnored field. The
 * version parameter is accepted for format changes.
 */
public void Load(DataInputStream di, IoContainer i,int version)
{
	boolean[] isIgnored = i.readBooleanArray(di, "puncta ignore list");
	if(isIgnored == null)
		return;	
	for(int k = 0; k < myPuncta.length; k++)
	{
		myPuncta[k].bC.isIgnored = isIgnored[k];
	}
}

/**
 * Returns a fresh boolean array b, one entry per stored puncta,
 * reflecting whether each is currently ignored.
 */
public boolean[] getIgnoredList()
{
	boolean[] b = new boolean[myPuncta.length];
	for(int k = 0; k < myPuncta.length; k++)
	{
		b[k] = myPuncta[k].isIgnored();
	}
	return b;
}

/**
 * Writes the current ignored-flag array to the stream via
 * writeBooleanArray so Load can later restore it.
 */
public void Save(DataOutputStream ds, IoContainer i)
{
	boolean[] isIgnored = getIgnoredList();
	i.writeBooleanArray(ds,"PunctaData ignore list", isIgnored);
}

/**
 * Applies auto-ignore rules to each puncta in turn, skipping null
 * entries. The thresholds array carries the per-channel threshold
 * values, ignoreCriteria encapsulates the rule parameters and r is
 * the source raster used to evaluate the rules.
 */
public void autoIgnore(int[] thresholds,IgnoreCriteria ignoreCriteria,Raster r)
{
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(myPuncta[k] != null)
		{						
			myPuncta[k].autoIgnore(thresholds, ignoreCriteria, r);
		}
	}
}

/** Invokes invertIgnored on every stored puncta, flipping its ignored flag. */
public void invertIgnored()
{
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(myPuncta[k] != null)
		{
			myPuncta[k].invertIgnored();
		}
	}
}

/** Invokes restoreIgnored on every stored puncta, clearing the ignored flag. */
public void restoreIgnored()
{
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(myPuncta[k] != null)
		{
			myPuncta[k].restoreIgnored();
		}
	}
}

}
