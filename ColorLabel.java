package neuron_analyzer;
import java.awt.Color;

public class ColorLabel {
Color c;
String s;
	public ColorLabel(String str, Color col)
	{
		s = str;
		c = col;
	}
	
	public String toString()
	{
		return s;
	}
	
	public Color getColor()
	{
		return c;
	}
	
	public void setAlpha(int a)
	{
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		c = new Color(r,g,b,a);		
	}
}
