package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Simple immutable-after-construction value object pairing a color channel
 * identifier with a corresponding intensity threshold. Used throughout the
 * intensity/colocalization computations as a compact way to describe "look
 * at channel X with cutoff Y" when passing parameters to measurement code.
 */
public class Marker {
int colorWatch;
int thresh;

//this class is to be used to contain a color and threshold value for computation of intensities

	/**
	 * Creates a new marker binding the given color channel to a threshold
	 * value. The {@code color} parameter is stored in {@code colorWatch} and
	 * identifies which of the R/G/B channels to examine; {@code threshold}
	 * is stored in {@code thresh} and represents the minimum intensity a
	 * pixel must reach on that channel to count during analysis.
	 */
	public Marker(int color, int threshold)
	{
	colorWatch = color;
	thresh = threshold;
	}
}
