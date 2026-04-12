package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Color;

/**
 * Pairs a human-readable text label with an associated {@link Color}. The
 * neuron analyzer uses {@code ColorLabel} as a lightweight combo-box/list
 * item where the user sees the string while the UI internally reads the
 * color value for drawing overlays, legends, or selection indicators.
 */
public class ColorLabel {
Color c;
String s;

	/**
	 * Creates a labeled color entry. The {@code str} parameter is saved in
	 * field {@code s} and becomes the label text returned by
	 * {@link #toString()}; {@code col} is saved in field {@code c} and is
	 * the color this label represents.
	 */
	public ColorLabel(String str, Color col)
	{
		s = str;
		c = col;
	}

	/**
	 * Returns the stored label text so that Swing components (lists, combo
	 * boxes) render this entry using the human-readable string.
	 */
	public String toString()
	{
		return s;
	}

	/**
	 * Returns the {@link Color} associated with this label, as set at
	 * construction or most recently modified via {@link #setAlpha(int)}.
	 */
	public Color getColor()
	{
		return c;
	}

	/**
	 * Replaces the stored color with a new one that keeps the existing red,
	 * green, and blue components but adopts the given alpha. The method
	 * reads {@code r}, {@code g}, and {@code b} from the current color and
	 * constructs a fresh {@link Color} with the alpha parameter {@code a}.
	 */
	public void setAlpha(int a)
	{
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		c = new Color(r,g,b,a);
	}
}
