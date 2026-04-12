package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Abstract interface supplying a single scalar measurement from an underlying
 * neuron-analysis object (dendrites, spines, puncta, cells, etc.). Concrete
 * implementations know how to look up a particular feature (length, area,
 * intensity, count, etc.) for a given element index and color channel,
 * optionally applying a unit calibration. Plots and tables use this interface
 * to remain agnostic of the concrete data-bearing class.
 */
public interface DataSource {

	/**
	 * Returns the value of the measurement represented by this data source for
	 * the element at position {@code i}, scaled by {@code calibration} (for
	 * example pixels-per-micron) and evaluated on the color channel specified
	 * by {@code color}. The {@code i} parameter indexes into the underlying
	 * collection of objects, {@code calibration} is a multiplier applied to
	 * raw pixel measurements, and {@code color} selects one of the red,
	 * green, or blue channels.
	 */
	public float getData(int i,double calibration,int color);
}
