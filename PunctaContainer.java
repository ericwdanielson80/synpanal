package neuron_analyzer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.awt.image.Raster;
public class PunctaContainer {
Puncta[] myPuncta;

public PunctaContainer(Puncta[] p)
{
	myPuncta = p;	
}

public int getTotalPunctaNumber()
{
	if(myPuncta == null)
		return 0;
	return myPuncta.length;
}

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

public void drawPuncta(Graphics2D g,int dendrite,int puncta)
{
	for(int k = 0 ;k < myPuncta.length; k++)
		myPuncta[k].drawPuncta(g, dendrite, puncta);
}

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

public int getIntensity(int k)
{
	return myPuncta[k].intensity;
}

public int getArea(int k)
{
	return myPuncta[k].area;
}

public boolean isSelected(int k)
{
	return myPuncta[k].bC.isSelected;
}

public boolean isIgnored(int k)
{
	return myPuncta[k].bC.isIgnored;
}

public Puncta findPuncta(Point p)
{
	for(int k = 0; k < myPuncta.length; k++)
	{
		if(myPuncta[k].border.contains(p))
			return myPuncta[k];
	}
	return null;
}

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

public boolean[] getIgnoredList()
{
	boolean[] b = new boolean[myPuncta.length];
	for(int k = 0; k < myPuncta.length; k++)
	{
		b[k] = myPuncta[k].isIgnored();
	}
	return b;
}

public void Save(DataOutputStream ds, IoContainer i)
{	
	boolean[] isIgnored = getIgnoredList();
	i.writeBooleanArray(ds,"PunctaData ignore list", isIgnored);
}

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
